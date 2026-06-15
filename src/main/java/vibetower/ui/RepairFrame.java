package vibetower.ui;

import vibetower.model.GameState;
import vibetower.model.Item;
import vibetower.model.PlacedRoomItem;
import vibetower.model.Room;
import vibetower.model.SaveManager;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.LinkedHashMap;

public class RepairFrame extends JDialog {

    private final JFrame parent;
    private final GameState gameState;

    private final JPanel itemsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));
    private final ArrayList<EditableRoomItem> placedItems = new ArrayList<>();

    private EditableRoomItem selectedItem;
    private EditableRoomItem draggedItem;

    private Item draggedCatalogItem;
    private JLabel draggedPreview;

    private JButton saveButton;

    public RepairFrame(JFrame parent, GameState gameState) {
        super(parent, "Режим ремонту", false);

        this.parent = parent;
        this.gameState = gameState;

        setUndecorated(true);

        int width = parent.getWidth() - 60;
        int height = 165;

        setSize(width, height);
        setLocation(parent.getX() + 30, parent.getY() + parent.getHeight() - height - 28);

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setBackground(new Color(0, 0, 0, 0));

        JPanel content = createScreen();
        content.setOpaque(false);
        setContentPane(content);

        loadExistingRoomItems();
        showItems();
    }

    public RepairFrame(GameState gameState) {
        this(null, gameState);
    }

    private JPanel createScreen() {
        JPanel root = new JPanel(new BorderLayout());
        root.setOpaque(false);

        UiStyle.RoundPanel panel = new UiStyle.RoundPanel(
                new BorderLayout(8, 5),
                26,
                UiStyle.CREAM,
                UiStyle.PURPLE,
                3
        );

        panel.setBorder(BorderFactory.createEmptyBorder(7, 12, 7, 12));
        panel.setOpaque(false);

        panel.add(createTitle(), BorderLayout.NORTH);
        panel.add(createItemsScroll(), BorderLayout.CENTER);
        panel.add(createButtons(), BorderLayout.SOUTH);

        root.add(panel, BorderLayout.CENTER);

        return root;
    }

    private JPanel createTitle() {
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(true);
        titlePanel.setBackground(UiStyle.CREAM);

        JLabel title = new JLabel("🛠️  Режим ремонту", SwingConstants.CENTER);
        title.setForeground(UiStyle.BLUE);
        title.setFont(UiStyle.titleFont(22));

        JButton home = UiStyle.button("Додому");
        home.setForeground(UiStyle.BLUE);
        home.setFont(UiStyle.textFont(13));
        home.setPreferredSize(new Dimension(95, 30));

        home.addActionListener(e -> closeRepairMode());

        titlePanel.add(new JLabel(""), BorderLayout.WEST);
        titlePanel.add(title, BorderLayout.CENTER);
        titlePanel.add(home, BorderLayout.EAST);

        return titlePanel;
    }

    private JScrollPane createItemsScroll() {
        itemsPanel.setOpaque(true);
        itemsPanel.setBackground(UiStyle.CREAM);

        JScrollPane scroll = new JScrollPane(itemsPanel);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(215, 180, 130), 1));
        scroll.setOpaque(true);
        scroll.setBackground(UiStyle.CREAM);
        scroll.getViewport().setOpaque(true);
        scroll.getViewport().setBackground(UiStyle.CREAM);

        scroll.setPreferredSize(new Dimension(0, 78));
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        scroll.getHorizontalScrollBar().setUnitIncrement(20);

        return scroll;
    }

    private JPanel createButtons() {
        JPanel buttons = new JPanel(new GridLayout(1, 3, 10, 0));
        buttons.setOpaque(true);
        buttons.setBackground(UiStyle.CREAM);

        saveButton = UiStyle.button("💾 Зберегти");
        JButton cancel = UiStyle.button("↩ Скасувати");
        JButton clear = UiStyle.button("Прибрати все");

        saveButton.setFont(UiStyle.textFont(13));
        cancel.setFont(UiStyle.textFont(13));
        clear.setFont(UiStyle.textFont(13));

        saveButton.addActionListener(e -> saveRepair());
        cancel.addActionListener(e -> undoLastAction());
        clear.addActionListener(e -> clearPlacedItems());

        buttons.add(saveButton);
        buttons.add(cancel);
        buttons.add(clear);

        return buttons;
    }

    private void loadExistingRoomItems() {
        ArrayList<PlacedRoomItem> savedItems = gameState.getCurrentRoomPlacedRoomItems();

        for (PlacedRoomItem placed : savedItems) {
            if (placed == null || placed.getItem() == null) continue;

            EditableRoomItem editable = new EditableRoomItem(placed.getItem(), placed);
            editable.setBounds(
                    placed.getX(),
                    placed.getY(),
                    Math.max(30, placed.getWidth()),
                    Math.max(30, placed.getHeight())
            );

            editable.setMirroredFromSave(placed.isMirrored());
            editable.turnOffSelectionOnly();

            placedItems.add(editable);
            parent.getLayeredPane().add(editable, Integer.valueOf(20));
        }

        parent.getLayeredPane().revalidate();
        parent.getLayeredPane().repaint();
    }

    private void showItems() {
        itemsPanel.removeAll();

        LinkedHashMap<String, InventoryGroup> groupedItems = groupInventoryItems();

        if (groupedItems.isEmpty()) {
            JLabel empty = new JLabel("Інвентар порожній");
            empty.setForeground(UiStyle.BLUE);
            empty.setFont(UiStyle.textFont(16));
            itemsPanel.add(empty);
        } else {
            for (InventoryGroup group : groupedItems.values()) {
                itemsPanel.add(createItemCard(group.item, group.count));
            }
        }

        itemsPanel.revalidate();
        itemsPanel.repaint();
    }

    private LinkedHashMap<String, InventoryGroup> groupInventoryItems() {
        LinkedHashMap<String, InventoryGroup> groupedItems = new LinkedHashMap<>();

        ArrayList<Item> inventory = gameState.getInventory();

        for (Item item : inventory) {
            if (item == null) continue;

            String key = item.getName() + "|" + item.getImageFile();

            if (groupedItems.containsKey(key)) {
                groupedItems.get(key).count++;
            } else {
                groupedItems.put(key, new InventoryGroup(item, 1));
            }
        }

        ArrayList<PlacedRoomItem> roomItems = gameState.getCurrentRoomPlacedRoomItems();

        for (PlacedRoomItem placed : roomItems) {
            if (placed == null || placed.getItem() == null) continue;

            Item item = placed.getItem();
            String key = item.getName() + "|" + item.getImageFile();

            if (groupedItems.containsKey(key)) {
                InventoryGroup group = groupedItems.get(key);
                group.count--;

                if (group.count <= 0) {
                    groupedItems.remove(key);
                }
            }
        }

        return groupedItems;
    }

    private JPanel createItemCard(Item item, int count) {
        UiStyle.RoundPanel card = new UiStyle.RoundPanel(
                new BorderLayout(2, 1),
                14,
                new Color(255, 252, 247),
                UiStyle.PURPLE,
                2
        );

        card.setPreferredSize(new Dimension(92, 72));
        card.setBorder(BorderFactory.createEmptyBorder(3, 4, 3, 4));
        card.setOpaque(false);
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel image = new JLabel("", SwingConstants.CENTER);
        image.setHorizontalAlignment(SwingConstants.CENTER);
        image.setVerticalAlignment(SwingConstants.CENTER);

        ImageIcon icon = UiStyle.iconContain(item.getImageFile(), 58, 36);

        if (icon != null) {
            image.setIcon(icon);
        } else {
            image.setText(item.getIcon());
            image.setFont(UiStyle.titleFont(21));
        }

        JLabel name = new JLabel(shortName(item.getName()), SwingConstants.CENTER);
        name.setForeground(UiStyle.BLUE);
        name.setFont(UiStyle.textFont(10));

        JLabel amount = new JLabel("×" + count, SwingConstants.CENTER);
        amount.setForeground(UiStyle.BLUE);
        amount.setFont(UiStyle.textFont(10));

        JPanel bottom = new JPanel(new GridLayout(2, 1, 0, 0));
        bottom.setOpaque(true);
        bottom.setBackground(new Color(255, 252, 247));
        bottom.add(name);
        bottom.add(amount);

        card.add(image, BorderLayout.CENTER);
        card.add(bottom, BorderLayout.SOUTH);

        MouseAdapter adapter = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                startPreviewDrag(item, e);
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                dragPreview(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                finishPreviewDrag(e);
            }
        };

        card.addMouseListener(adapter);
        card.addMouseMotionListener(adapter);
        image.addMouseListener(adapter);
        image.addMouseMotionListener(adapter);

        return card;
    }

    private void startPreviewDrag(Item item, MouseEvent e) {
        draggedCatalogItem = item;

        draggedPreview = new JLabel();
        draggedPreview.setName("ROOM_ITEM_PREVIEW");
        draggedPreview.setHorizontalAlignment(SwingConstants.CENTER);
        draggedPreview.setVerticalAlignment(SwingConstants.CENTER);

        ImageIcon icon = UiStyle.iconContain(
                item.getImageFile(),
                getRoomItemWidth(item),
                getRoomItemHeight(item)
        );

        if (icon != null) {
            draggedPreview.setIcon(icon);
            draggedPreview.setSize(icon.getIconWidth(), icon.getIconHeight());
        } else {
            draggedPreview.setText(item.getIcon());
            draggedPreview.setFont(UiStyle.titleFont(38));
            draggedPreview.setSize(110, 80);
        }

        movePreviewToMouse(e);

        parent.getLayeredPane().add(draggedPreview, JLayeredPane.DRAG_LAYER);
        parent.getLayeredPane().repaint();
    }

    private void dragPreview(MouseEvent e) {
        if (draggedPreview == null) return;

        movePreviewToMouse(e);
        parent.getLayeredPane().repaint();
    }

    private void finishPreviewDrag(MouseEvent e) {
        if (draggedPreview == null || draggedCatalogItem == null) return;

        Point screenPoint = e.getLocationOnScreen();
        Point dialogPoint = new Point(screenPoint);
        SwingUtilities.convertPointFromScreen(dialogPoint, this);

        boolean releasedInsidePanel =
                dialogPoint.x >= 0
                        && dialogPoint.y >= 0
                        && dialogPoint.x <= getWidth()
                        && dialogPoint.y <= getHeight();

        if (releasedInsidePanel) {
            parent.getLayeredPane().remove(draggedPreview);
            parent.getLayeredPane().repaint();
            draggedPreview = null;
            draggedCatalogItem = null;
            return;
        }

        Point location = draggedPreview.getLocation();

        parent.getLayeredPane().remove(draggedPreview);

        int itemWidth = Math.max(draggedPreview.getWidth() + 80, 150);
        int itemHeight = draggedPreview.getHeight() + 50;

        PlacedRoomItem savedItem = gameState.addPlacedRoomItem(
                draggedCatalogItem,
                location.x,
                location.y,
                itemWidth,
                itemHeight
        );

        EditableRoomItem item = new EditableRoomItem(draggedCatalogItem, savedItem);
        item.setBounds(location.x, location.y, itemWidth, itemHeight);

        placedItems.add(item);

        parent.getLayeredPane().add(item, Integer.valueOf(20));
        selectItem(item);
        item.updateValidation();

        draggedPreview = null;
        draggedCatalogItem = null;

        updateSaveButtonState();
        showItems();

        parent.getLayeredPane().revalidate();
        parent.getLayeredPane().repaint();
    }

    private Room createRoomForDoor() {
        JTextField roomNameField = new JTextField();
        roomNameField.setFont(UiStyle.textFont(16));

        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.add(new JLabel("Введи назву кімнати:"), BorderLayout.NORTH);
        panel.add(roomNameField, BorderLayout.CENTER);

        int result = JOptionPane.showConfirmDialog(
                parent,
                panel,
                "Назви кімнату",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result != JOptionPane.OK_OPTION) {
            return null;
        }

        String roomName = roomNameField.getText();

        if (roomName == null || roomName.trim().isEmpty()) {
            roomName = "Нова кімната";
        }

        String[] options = {"Синя", "Персикова", "Рожева"};

        int choice = JOptionPane.showOptionDialog(
                parent,
                "Обери вигляд кімнати",
                "Нова кімната",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.PLAIN_MESSAGE,
                null,
                options,
                options[0]
        );

        if (choice == JOptionPane.CLOSED_OPTION) {
            return null;
        }

        String type = "blue";

        if (choice == 1) {
            type = "peach";
        } else if (choice == 2) {
            type = "pink";
        }

        Room room = gameState.addCustomRoom(roomName, type);

        JOptionPane.showMessageDialog(
                parent,
                "Кімнату створено: " + room.getName(),
                "Двері",
                JOptionPane.INFORMATION_MESSAGE
        );

        return room;
    }

    private void movePreviewToMouse(MouseEvent e) {
        Point screenPoint = e.getLocationOnScreen();
        Point framePoint = new Point(screenPoint);
        SwingUtilities.convertPointFromScreen(framePoint, parent.getLayeredPane());

        draggedPreview.setLocation(
                framePoint.x - draggedPreview.getWidth() / 2,
                framePoint.y - draggedPreview.getHeight() / 2
        );
    }

    private void selectItem(EditableRoomItem item) {
        if (selectedItem != null && selectedItem != item) {
            selectedItem.setSelected(false);
        }

        selectedItem = item;
        selectedItem.setSelected(true);
        selectedItem.updateValidation();

        parent.getLayeredPane().moveToFront(item);
        parent.getLayeredPane().repaint();

        updateSaveButtonState();
    }

    private void closeRepairMode() {
        if (draggedPreview != null) {
            parent.getLayeredPane().remove(draggedPreview);
            draggedPreview = null;
            draggedCatalogItem = null;
        }

        for (EditableRoomItem item : new ArrayList<>(placedItems)) {
            item.syncToSavedItem();
            parent.getLayeredPane().remove(item);
        }

        placedItems.clear();
        selectedItem = null;
        draggedItem = null;

        SaveManager.saveGame(gameState);

        parent.getLayeredPane().revalidate();
        parent.getLayeredPane().repaint();

        RepairFrame.this.setVisible(false);
        RepairFrame.this.dispose();
    }

    private boolean isValidPlacement(JComponent component, Item item) {
        String type = detectItemType(item);
        String surface = detectSurface(component);

        if ("CEILING".equals(surface)) return false;
        if ("OUTSIDE_ROOM".equals(surface)) return false;

        if ("RUG".equals(type)) {
            return "FLOOR".equals(surface);
        }

        if ("DOOR".equals(type)) {
            return "WALL".equals(surface) || "FLOOR".equals(surface);
        }

        if ("WALL_TV".equals(type) || "WALL_CABINET".equals(type)) {
            return "WALL".equals(surface);
        }

        if ("COMPUTER".equals(type) || "TABLE_LAMP".equals(type)) {
            return "TABLE_SURFACE".equals(surface) || "FLOOR".equals(surface);
        }

        if ("SMALL_SPEAKER".equals(type)) {
            return "TABLE_SURFACE".equals(surface) || "FLOOR".equals(surface);
        }

        if ("BIG_SPEAKER".equals(type)
                || "FLOOR_LAMP".equals(type)
                || "PLANT".equals(type)) {
            return "FLOOR".equals(surface);
        }

        if ("STANDING_TV".equals(type)) {
            return "FLOOR".equals(surface) || "TABLE_SURFACE".equals(surface);
        }

        if ("FRAME_DECOR".equals(type)) {
            return "WALL".equals(surface) || "FLOOR".equals(surface) || "TABLE_SURFACE".equals(surface);
        }

        return "FLOOR".equals(surface);
    }

    private String detectSurface(JComponent component) {
        Rectangle bounds = component.getBounds();

        int centerX = bounds.x + bounds.width / 2;
        int centerY = bounds.y + bounds.height / 2;

        int frameW = parent.getWidth();
        int frameH = parent.getHeight();

        int topLimit = (int) (frameH * 0.10);
        int wallStart = (int) (frameH * 0.16);
        int floorStart = (int) (frameH * 0.40);
        int repairPanelTop = getY() - parent.getY();

        int leftRoom = (int) (frameW * 0.02);
        int rightRoom = (int) (frameW * 0.98);

        if (centerX < leftRoom || centerX > rightRoom) {
            return "OUTSIDE_ROOM";
        }

        if (centerY < topLimit) {
            return "CEILING";
        }

        if (centerY >= repairPanelTop - 20) {
            return "OUTSIDE_ROOM";
        }

        if (intersectsTableSurface(component)) {
            return "TABLE_SURFACE";
        }

        if (centerY >= floorStart) {
            return "FLOOR";
        }

        if (centerY >= wallStart && centerY < floorStart) {
            return "WALL";
        }

        return "CEILING";
    }

    private boolean intersectsTableSurface(JComponent component) {
        Rectangle current = component.getBounds();

        for (EditableRoomItem placed : placedItems) {
            if (placed == component) continue;

            if (isTableSurfaceType(placed.itemType) && current.intersects(placed.getBounds())) {
                return true;
            }
        }

        return false;
    }

    private boolean isTableSurfaceType(String type) {
        return "TABLE".equals(type)
                || "CABINET".equals(type)
                || "WARDROBE".equals(type)
                || "KITCHEN_CABINET".equals(type);
    }

    private String detectItemType(Item item) {
        if (item == null || item.getName() == null) return "FURNITURE";

        String n = item.getName().toLowerCase();

        if (n.contains("коврик") || n.contains("килим")) return "RUG";
        if (n.contains("двері") || n.contains("дверь")) return "DOOR";

        if (n.contains("панель")
                || n.contains("навісний телевізор")
                || n.contains("телевізор навісний")) {
            return "WALL_TV";
        }

        if (n.contains("телевізор")) return "STANDING_TV";

        if (n.contains("комп'ютер") || n.contains("компютер") || n.contains("ноутбук")) {
            return "COMPUTER";
        }

        if (n.contains("колонка велика")) return "BIG_SPEAKER";
        if (n.contains("колонка")) return "SMALL_SPEAKER";

        if (n.contains("настільна лампа") || n.contains("міні лампа")) return "TABLE_LAMP";
        if (n.contains("торшер") || n.contains("коршер")) return "FLOOR_LAMP";

        if (n.contains("квіти")
                || n.contains("цветы")
                || n.contains("рослина")
                || n.contains("растение")
                || n.contains("пальма")) {
            return "PLANT";
        }

        if (n.contains("картина")
                || n.contains("рамка")
                || n.contains("фоторамка")
                || n.contains("дзеркало")
                || n.contains("ваза")
                || n.contains("декор")) {
            return "FRAME_DECOR";
        }

        if (n.contains("кухонні навісні шафи")
                || n.contains("кухонні нависні шафи")
                || n.contains("навісні шафи")
                || n.contains("полички")) {
            return "WALL_CABINET";
        }

        if (n.contains("стіл") || n.contains("столик") || n.contains("тумба")) return "TABLE";
        if (n.contains("комод") || n.contains("шафчик")) return "CABINET";
        if (n.contains("шафа")) return "WARDROBE";
        if (n.contains("кухонні шафи")) return "KITCHEN_CABINET";

        if (n.contains("диван")) return "SOFA";
        if (n.contains("крісл")) return "ARMCHAIR";
        if (n.contains("стілець")) return "CHAIR";
        if (n.contains("ліжко")) return "BED";

        if (n.contains("холодильник")
                || n.contains("пральна")
                || n.contains("плита")
                || n.contains("духовка")
                || n.contains("піч")) {
            return "TECH_FLOOR";
        }

        return "FURNITURE";
    }

    private int getRoomItemWidth(Item item) {
        switch (detectItemType(item)) {
            case "RUG": return 105;
            case "DOOR": return 95;
            case "WALL_TV": return 135;
            case "STANDING_TV": return 120;
            case "COMPUTER": return 95;
            case "BIG_SPEAKER": return 80;
            case "SMALL_SPEAKER": return 65;
            case "FRAME_DECOR": return 80;
            case "TABLE_LAMP": return 60;
            case "FLOOR_LAMP": return 80;
            case "PLANT": return 80;
            case "SOFA": return 145;
            case "BED": return 140;
            case "WARDROBE": return 115;
            case "TECH_FLOOR": return 110;
            default: return 110;
        }
    }

    private int getRoomItemHeight(Item item) {
        switch (detectItemType(item)) {
            case "RUG": return 70;
            case "DOOR": return 145;
            case "WALL_TV": return 80;
            case "STANDING_TV": return 95;
            case "COMPUTER": return 80;
            case "BIG_SPEAKER": return 115;
            case "SMALL_SPEAKER": return 60;
            case "FRAME_DECOR": return 75;
            case "TABLE_LAMP": return 75;
            case "FLOOR_LAMP": return 120;
            case "PLANT": return 95;
            case "SOFA": return 100;
            case "BED": return 105;
            case "WARDROBE": return 135;
            case "TECH_FLOOR": return 120;
            default: return 95;
        }
    }

    private void undoLastAction() {
        if (placedItems.isEmpty()) {
            JOptionPane.showMessageDialog(parent, "Немає дій для скасування.");
            return;
        }

        EditableRoomItem last = placedItems.remove(placedItems.size() - 1);
        parent.getLayeredPane().remove(last);
        gameState.removePlacedRoomItem(last.savedItem);

        if (selectedItem == last) {
            selectedItem = null;
        }

        showItems();

        parent.getLayeredPane().revalidate();
        parent.getLayeredPane().repaint();
        updateSaveButtonState();
    }

    private void clearPlacedItems() {
        for (EditableRoomItem item : placedItems) {
            parent.getLayeredPane().remove(item);
        }

        placedItems.clear();
        selectedItem = null;
        gameState.clearCurrentRoomPlacedItems();

        showItems();

        parent.getLayeredPane().revalidate();
        parent.getLayeredPane().repaint();
        updateSaveButtonState();

        JOptionPane.showMessageDialog(parent, "Усі предмети прибрано з кімнати.");
    }

    private void saveRepair() {
        if (hasInvalidItems()) {
            JOptionPane.showMessageDialog(
                    parent,
                    "Є предмети, які стоять неправильно.\nПерестав їх із червоної зони.",
                    "Ремонт",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        for (EditableRoomItem item : placedItems) {
            item.syncToSavedItem();
            item.confirmWithoutDoorWindow();
        }

        selectedItem = null;

        SaveManager.saveGame(gameState);

        closeRepairMode();
    }

    private boolean hasInvalidItems() {
        for (EditableRoomItem item : placedItems) {
            if (!item.validPlacement) return true;
        }

        return false;
    }

    private void updateSaveButtonState() {
        if (saveButton == null) return;

        boolean hasInvalid = hasInvalidItems();

        saveButton.setEnabled(true);
        saveButton.setText("💾 Зберегти");

        if (hasInvalid) {
            saveButton.setForeground(Color.RED);
        } else {
            saveButton.setForeground(UiStyle.BLUE);
        }
    }

    private String shortName(String name) {
        if (name == null) return "Предмет";
        if (name.length() <= 12) return name;
        return name.substring(0, 11) + "...";
    }

    private class EditableRoomItem extends JPanel {

        private final Item item;
        private final String itemType;
        private final PlacedRoomItem savedItem;

        private final JLabel imageLabel = new JLabel("", SwingConstants.CENTER);
        private final JPanel toolsPanel = new JPanel(new GridLayout(1, 5, 2, 0));

        private boolean selected = false;
        private boolean confirmed = false;
        private boolean validPlacement = true;
        private boolean mirrored = false;
        private boolean doorRoomCreated = false;

        private ImageIcon originalIcon;

        private int imageWidth;
        private int imageHeight;

        private int dragOffsetX;
        private int dragOffsetY;

        public EditableRoomItem(Item item, PlacedRoomItem savedItem) {
            super(new BorderLayout());

            this.item = item;
            this.savedItem = savedItem;
            this.itemType = detectItemType(item);

            this.imageWidth = getRoomItemWidth(item);
            this.imageHeight = getRoomItemHeight(item);

            if (savedItem != null) {
                this.imageWidth = Math.max(30, savedItem.getWidth() - 80);
                this.imageHeight = Math.max(30, savedItem.getHeight() - 50);
                this.mirrored = savedItem.isMirrored();
                this.doorRoomCreated = savedItem.isDoor()
                        && savedItem.getTargetRoomId() != null
                        && !savedItem.getTargetRoomId().isEmpty();
            }

            setName("ROOM_ITEM");
            setOpaque(false);
            setCursor(new Cursor(Cursor.MOVE_CURSOR));

            buildImage();
            buildTools();

            add(imageLabel, BorderLayout.CENTER);
            add(toolsPanel, BorderLayout.NORTH);

            setToolsVisible(true);

            MouseAdapter moveAdapter = new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    confirmed = false;

                    setToolsVisible(true);
                    setSelected(true);
                    selectItem(EditableRoomItem.this);

                    Point p = SwingUtilities.convertPoint(
                            e.getComponent(),
                            e.getPoint(),
                            EditableRoomItem.this
                    );

                    dragOffsetX = p.x;
                    dragOffsetY = p.y;
                    draggedItem = EditableRoomItem.this;

                    parent.getLayeredPane().moveToFront(EditableRoomItem.this);
                    parent.getLayeredPane().repaint();
                }

                @Override
                public void mouseDragged(MouseEvent e) {
                    if (draggedItem == null) return;

                    Point screenPoint = e.getLocationOnScreen();
                    Point framePoint = new Point(screenPoint);
                    SwingUtilities.convertPointFromScreen(framePoint, parent.getLayeredPane());

                    setLocation(framePoint.x - dragOffsetX, framePoint.y - dragOffsetY);
                    updateValidation();
                    syncToSavedItem();
                    parent.getLayeredPane().repaint();
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    draggedItem = null;
                    updateValidation();
                    syncToSavedItem();
                }
            };

            addMouseListener(moveAdapter);
            addMouseMotionListener(moveAdapter);
            imageLabel.addMouseListener(moveAdapter);
            imageLabel.addMouseMotionListener(moveAdapter);
        }

        private void buildImage() {
            originalIcon = UiStyle.iconContain(item.getImageFile(), imageWidth, imageHeight);

            if (originalIcon != null) {
                if (mirrored) {
                    imageLabel.setIcon(mirrorIcon(originalIcon));
                } else {
                    imageLabel.setIcon(originalIcon);
                }
            } else {
                imageLabel.setText(item.getIcon());
                imageLabel.setFont(UiStyle.titleFont(38));
            }

            imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
            imageLabel.setVerticalAlignment(SwingConstants.CENTER);
        }

        private void buildTools() {
            toolsPanel.setOpaque(true);
            toolsPanel.setBackground(new Color(255, 248, 235, 230));
            toolsPanel.setBorder(new LineBorder(UiStyle.PURPLE, 1, true));
            toolsPanel.setPreferredSize(new Dimension(150, 24));

            JButton plus = smallToolButton("+");
            JButton minus = smallToolButton("−");
            JButton flip = smallToolButton("↔");
            JButton ok = smallToolButton("✓");
            JButton delete = smallToolButton("✕");

            delete.setForeground(Color.RED);
            delete.setFont(UiStyle.titleFont(14));

            plus.addActionListener(e -> resizeItem(1.12));
            minus.addActionListener(e -> resizeItem(0.88));
            flip.addActionListener(e -> mirrorItem());

            ok.addActionListener(e -> confirmWithDoorWindow());
            delete.addActionListener(e -> removeThisItem());

            toolsPanel.add(plus);
            toolsPanel.add(minus);
            toolsPanel.add(flip);
            toolsPanel.add(ok);
            toolsPanel.add(delete);
        }

        private JButton smallToolButton(String text) {
            JButton button = new JButton(text);

            button.setFocusPainted(false);
            button.setFont(UiStyle.textFont(11));
            button.setForeground(UiStyle.BLUE);
            button.setBackground(new Color(255, 248, 235));
            button.setBorder(new LineBorder(UiStyle.PURPLE, 1, true));
            button.setCursor(new Cursor(Cursor.HAND_CURSOR));

            return button;
        }

        private void setToolsVisible(boolean visible) {
            toolsPanel.setVisible(true);

            for (Component component : toolsPanel.getComponents()) {
                component.setVisible(visible);
            }

            if (visible) {
                toolsPanel.setOpaque(true);
                toolsPanel.setBorder(new LineBorder(UiStyle.PURPLE, 1, true));
            } else {
                toolsPanel.setOpaque(false);
                toolsPanel.setBorder(BorderFactory.createEmptyBorder());
            }

            toolsPanel.repaint();
        }

        private void resizeItem(double scale) {
            imageWidth = Math.max(35, Math.min(260, (int) (imageWidth * scale)));
            imageHeight = Math.max(30, Math.min(260, (int) (imageHeight * scale)));

            ImageIcon icon = UiStyle.iconContain(item.getImageFile(), imageWidth, imageHeight);

            if (icon != null) {
                originalIcon = icon;

                if (mirrored) {
                    imageLabel.setIcon(mirrorIcon(originalIcon));
                } else {
                    imageLabel.setIcon(originalIcon);
                }

                int newWidth = Math.max(icon.getIconWidth() + 80, 150);
                int newHeight = icon.getIconHeight() + 50;
                setSize(newWidth, newHeight);
            } else {
                int newWidth = Math.max(imageWidth + 80, 150);
                int newHeight = imageHeight + 50;
                setSize(newWidth, newHeight);
            }

            syncToSavedItem();
            revalidate();
            updateValidation();
            parent.getLayeredPane().repaint();
        }

        private void mirrorItem() {
            mirrored = !mirrored;

            if (originalIcon != null) {
                if (mirrored) {
                    imageLabel.setIcon(mirrorIcon(originalIcon));
                } else {
                    imageLabel.setIcon(originalIcon);
                }
            }

            syncToSavedItem();
            updateValidation();
            parent.getLayeredPane().repaint();
        }

        private ImageIcon mirrorIcon(ImageIcon icon) {
            int w = icon.getIconWidth();
            int h = icon.getIconHeight();

            BufferedImage mirroredImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = mirroredImage.createGraphics();

            g2.drawImage(icon.getImage(), w, 0, -w, h, null);
            g2.dispose();

            return new ImageIcon(mirroredImage);
        }

        public void setSelected(boolean selected) {
            this.selected = selected;

            setToolsVisible(selected);

            if (selected) {
                setBorder(new LineBorder(UiStyle.PURPLE, 3, true));
            } else {
                setBorder(null);
            }

            updateValidation();
            repaint();
        }

        public void updateValidation() {
            validPlacement = isValidPlacement(this, item);

            imageLabel.setBorder(null);

            if (!validPlacement) {
                setBorder(new LineBorder(Color.RED, 3, true));
            } else if (selected) {
                setBorder(new LineBorder(UiStyle.PURPLE, 3, true));
            } else {
                setBorder(null);
            }

            updateSaveButtonState();
        }

        private void confirmWithDoorWindow() {
            updateValidation();

            if (!validPlacement) {
                JOptionPane.showMessageDialog(
                        parent,
                        "Предмет стоїть неправильно.\nПерестав його з червоної зони."
                );
                return;
            }

            if ("DOOR".equals(itemType) && !doorRoomCreated) {
                Room room = createRoomForDoor();

                if (room == null) {
                    return;
                }

                if (savedItem != null) {
                    savedItem.setDoor(true);
                    savedItem.setTargetRoomId(room.getId());
                }

                doorRoomCreated = true;
            }

            syncToSavedItem();
            confirmWithoutDoorWindow();
        }

        public void confirmWithoutDoorWindow() {
            updateValidation();

            if (!validPlacement) return;

            confirmed = true;
            selected = false;

            setToolsVisible(false);
            setBorder(null);
            imageLabel.setBorder(null);

            if (selectedItem == this) {
                selectedItem = null;
            }

            syncToSavedItem();
            repaint();
            updateSaveButtonState();
        }

        public void turnOffSelectionOnly() {
            selected = false;
            setToolsVisible(false);
            setBorder(null);
            imageLabel.setBorder(null);
            repaint();
        }

        public void setMirroredFromSave(boolean mirrored) {
            this.mirrored = mirrored;

            if (originalIcon != null) {
                if (mirrored) {
                    imageLabel.setIcon(mirrorIcon(originalIcon));
                } else {
                    imageLabel.setIcon(originalIcon);
                }
            }
        }

        private void syncToSavedItem() {
            if (savedItem == null) return;

            savedItem.setX(getX());
            savedItem.setY(getY());
            savedItem.setWidth(getWidth());
            savedItem.setHeight(getHeight());
            savedItem.setMirrored(mirrored);

            if (item != null) {
                savedItem.setItem(item);
                savedItem.setItemName(item.getName());
                savedItem.setItemIcon(item.getIcon());
                savedItem.setItemCategory(item.getCategory());
                savedItem.setImageFile(item.getImageFile());
            }

            if ("DOOR".equals(itemType)) {
                savedItem.setDoor(true);
            }

            SaveManager.saveGame(gameState);
        }

        private void removeThisItem() {
            parent.getLayeredPane().remove(this);

            placedItems.remove(this);
            gameState.removePlacedRoomItem(savedItem);

            if (selectedItem == this) {
                selectedItem = null;
            }

            showItems();

            parent.getLayeredPane().revalidate();
            parent.getLayeredPane().repaint();
            updateSaveButtonState();
        }
    }

    private static class InventoryGroup {
        private final Item item;
        private int count;

        public InventoryGroup(Item item, int count) {
            this.item = item;
            this.count = count;
        }
    }
}