package vibetower.model;

import vibetower.ui.InventoryFrame;
import vibetower.ui.RepairFrame;
import vibetower.ui.ShopFrame;
import vibetower.ui.TasksFrame;
import vibetower.ui.UiStyle;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class HomeFrame extends JFrame {

    private final GameState gameState;

    private JLayeredPane layeredPane;
    private JLabel backgroundLabel;

    private double zoomScale = 1.0;

    private JLabel xpValueLabel;
    private JLabel energyValueLabel;
    private JLabel silverValueLabel;
    private JLabel goldValueLabel;
    private JLabel comfortLabel;
    private JLabel titleLabel;
    private JLabel levelBadgeLabel;

    private JPanel inventoryPanel;

    private final ArrayList<JComponent> buttonsToHideInRepair = new ArrayList<>();
    private final ArrayList<JComponent> renderedRoomItems = new ArrayList<>();

    private static final int WINDOW_WIDTH = 1200;
    private static final int WINDOW_HEIGHT = 720;

    public HomeFrame(GameState gameState) {
        this.gameState = gameState;

        setTitle("VibeTower — Квартира");
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
        setContentPane(layeredPane);

        backgroundLabel = createApartmentBackground();
        backgroundLabel.setBounds(0, 0, WINDOW_WIDTH, WINDOW_HEIGHT);
        layeredPane.add(backgroundLabel, Integer.valueOf(0));

        addTopInfo();
        renderSavedRoomItems();
        addLeftButtons();
        addRightButtons();
        addBottomButtons();

        updateTopInfo();
    }

    private JLabel createApartmentBackground() {
        JLabel label = new JLabel();

        String backgroundPath = gameState.getCurrentRoomBackgroundImage();

        ImageIcon icon = loadBackgroundIcon(backgroundPath, WINDOW_WIDTH, WINDOW_HEIGHT);

        if (icon != null) {
            label.setIcon(icon);
        } else {
            icon = loadBackgroundIcon("src/main/resources/apartment_blue.jpg", WINDOW_WIDTH, WINDOW_HEIGHT);

            if (icon != null) {
                label.setIcon(icon);
            } else {
                label.setOpaque(true);
                label.setBackground(new Color(230, 220, 210));
            }
        }

        return label;
    }

    private ImageIcon loadBackgroundIcon(String path, int width, int height) {
        try {
            if (path == null || path.trim().isEmpty()) return null;

            ImageIcon original = null;
            java.io.File file = new java.io.File(path);

            if (file.exists()) {
                original = new ImageIcon(file.getAbsolutePath());
            } else {
                String cleanPath = path.replace("src/main/resources/", "");
                java.net.URL url = getClass().getClassLoader().getResource(cleanPath);

                if (url != null) {
                    original = new ImageIcon(url);
                }
            }

            if (original == null || original.getIconWidth() <= 0 || original.getIconHeight() <= 0) {
                return null;
            }

            Image image = original.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
            return new ImageIcon(image);

        } catch (Exception e) {
            return null;
        }
    }

    private void addTopInfo() {
        JPanel topPanel = new JPanel(null);
        topPanel.setOpaque(false);
        topPanel.setBounds(0, 0, WINDOW_WIDTH, 95);

        JLabel crown = new JLabel();
        ImageIcon crownIcon = loadButtonIcon("src/main/resources/crown.png", 60, 60);

        if (crownIcon != null) {
            crown.setIcon(crownIcon);
        } else {
            crown.setText("👑");
            crown.setFont(new Font("Arial", Font.BOLD, 38));
        }

        crown.setBounds(28, 20, 70, 60);
        topPanel.add(crown);

        titleLabel = new JLabel("Твоя квартира");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(UiStyle.titleFont(25));
        titleLabel.setBounds(95, 18, 330, 35);
        topPanel.add(titleLabel);

        comfortLabel = new JLabel("Комфорт: 0 ⭐");
        comfortLabel.setForeground(Color.WHITE);
        comfortLabel.setFont(UiStyle.titleFont(20));
        comfortLabel.setBounds(95, 52, 320, 32);
        topPanel.add(comfortLabel);

        levelBadgeLabel = new DiamondLevelLabel("1");
        levelBadgeLabel.setBounds(585, 16, 58, 58);
        topPanel.add(levelBadgeLabel);

        JLabel xpIcon = new JLabel("⭐");
        xpIcon.setFont(new Font("Arial", Font.BOLD, 26));
        xpIcon.setBounds(660, 28, 35, 35);
        topPanel.add(xpIcon);

        xpValueLabel = new JLabel("XP 0/100");
        xpValueLabel.setForeground(UiStyle.BLUE);
        xpValueLabel.setFont(UiStyle.titleFont(22));
        xpValueLabel.setBounds(695, 28, 170, 35);
        topPanel.add(xpValueLabel);

        JLabel energyIcon = new JLabel("⚡");
        energyIcon.setFont(new Font("Arial", Font.BOLD, 24));
        energyIcon.setBounds(860, 28, 35, 35);
        topPanel.add(energyIcon);

        energyValueLabel = new JLabel("100");
        energyValueLabel.setForeground(UiStyle.BLUE);
        energyValueLabel.setFont(UiStyle.titleFont(22));
        energyValueLabel.setBounds(895, 28, 80, 35);
        topPanel.add(energyValueLabel);

        JLabel silverIcon = new JLabel();
        ImageIcon silver = loadButtonIcon("src/main/resources/serebro.png", 30, 30);

        if (silver != null) {
            silverIcon.setIcon(silver);
        } else {
            silverIcon.setText("⚪");
            silverIcon.setFont(new Font("Arial", Font.BOLD, 21));
        }

        silverIcon.setBounds(980, 28, 35, 35);
        topPanel.add(silverIcon);

        silverValueLabel = new JLabel("500");
        silverValueLabel.setForeground(UiStyle.BLUE);
        silverValueLabel.setFont(UiStyle.titleFont(22));
        silverValueLabel.setBounds(1015, 28, 85, 35);
        topPanel.add(silverValueLabel);

        JLabel goldIcon = new JLabel();
        ImageIcon gold = loadButtonIcon("src/main/resources/zoloto.png", 30, 30);

        if (gold != null) {
            goldIcon.setIcon(gold);
        } else {
            goldIcon.setText("🟡");
            goldIcon.setFont(new Font("Arial", Font.BOLD, 21));
        }

        goldIcon.setBounds(1100, 28, 35, 35);
        topPanel.add(goldIcon);

        goldValueLabel = new JLabel("20");
        goldValueLabel.setForeground(UiStyle.BLUE);
        goldValueLabel.setFont(UiStyle.titleFont(22));
        goldValueLabel.setBounds(1135, 28, 60, 35);
        topPanel.add(goldValueLabel);

        layeredPane.add(topPanel, Integer.valueOf(50));
        buttonsToHideInRepair.add(topPanel);
    }

    private void addLeftButtons() {
        int x = 28;

        JButton restButton = createImageButton(
                "src/main/resources/відпочинок.png",
                "Відпочинок",
                x,
                260,
                185,
                74
        );

        restButton.addActionListener(e -> restPlayer());

        JButton apartmentButton = createImageButton(
                "src/main/resources/кнопка квартира.png",
                "Квартира",
                x,
                350,
                165,
                66
        );

        apartmentButton.addActionListener(e -> openRoomChooser());

        JButton mapButton = createImageButton(
                "src/main/resources/карта.png",
                "Карта",
                x,
                430,
                165,
                66
        );

        mapButton.addActionListener(e ->
                JOptionPane.showMessageDialog(this, "Карта буде додана пізніше.")
        );

        JButton tasksButton = createImageButton(
                "src/main/resources/завдання.png",
                "Завдання",
                x,
                510,
                165,
                66
        );

        tasksButton.addActionListener(e -> new TasksFrame(gameState).setVisible(true));

        layeredPane.add(restButton, Integer.valueOf(50));
        layeredPane.add(apartmentButton, Integer.valueOf(50));
        layeredPane.add(mapButton, Integer.valueOf(50));
        layeredPane.add(tasksButton, Integer.valueOf(50));

        buttonsToHideInRepair.add(restButton);
        buttonsToHideInRepair.add(apartmentButton);
        buttonsToHideInRepair.add(mapButton);
        buttonsToHideInRepair.add(tasksButton);
    }

    private void addRightButtons() {
        JButton zoomPlusButton = createImageButton(
                "src/main/resources/zoom_plus.png",
                "Збільшити",
                1130,
                330,
                50,
                50
        );

        zoomPlusButton.addActionListener(e -> zoomApartment(0.1));

        JButton zoomMinusButton = createImageButton(
                "src/main/resources/zoom_minus.png",
                "Зменшити",
                1130,
                405,
                50,
                50
        );

        zoomMinusButton.addActionListener(e -> zoomApartment(-0.1));

        layeredPane.add(zoomPlusButton, Integer.valueOf(50));
        layeredPane.add(zoomMinusButton, Integer.valueOf(50));

        buttonsToHideInRepair.add(zoomPlusButton);
        buttonsToHideInRepair.add(zoomMinusButton);
    }

    private void addBottomButtons() {
        JPanel bottomPanel = new JPanel(new GridLayout(1, 3, 28, 0));
        bottomPanel.setOpaque(false);
        bottomPanel.setBounds(260, 600, 680, 95);

        JButton shopButton = createImageButton(
                "src/main/resources/магазин інтер.png",
                "Магазин",
                0,
                0,
                205,
                85
        );

        shopButton.addActionListener(e -> {
            ShopFrame shopFrame = new ShopFrame(gameState);
            shopFrame.setVisible(true);
            updateTopInfo();
        });

        JButton inventoryButton = createImageButton(
                "src/main/resources/інвентар.png",
                "Інвентар",
                0,
                0,
                205,
                85
        );

        inventoryButton.addActionListener(e -> openInventoryPanel());

        JButton repairButton = createImageButton(
                "src/main/resources/режим ремонту.png",
                "Режим ремонту",
                0,
                0,
                205,
                85
        );

        repairButton.addActionListener(e -> openRepairFrameAndCheckTask());

        bottomPanel.add(shopButton);
        bottomPanel.add(inventoryButton);
        bottomPanel.add(repairButton);

        layeredPane.add(bottomPanel, Integer.valueOf(50));
        buttonsToHideInRepair.add(bottomPanel);
    }

    private JButton createImageButton(String imagePath, String text, int x, int y, int width, int height) {
        JButton button = new JButton();

        button.setBounds(x, y, width, height);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setOpaque(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setToolTipText(text);

        ImageIcon icon = loadButtonIcon(imagePath, width, height);

        if (icon != null) {
            button.setIcon(icon);
        } else {
            button.setText(text);
            button.setForeground(UiStyle.BLUE);
            button.setFont(UiStyle.titleFont(14));
            button.setBackground(UiStyle.CREAM);
            button.setOpaque(true);
            button.setBorder(BorderFactory.createLineBorder(UiStyle.PURPLE, 2));
        }

        return button;
    }

    private ImageIcon loadButtonIcon(String path, int maxWidth, int maxHeight) {
        try {
            if (path == null || path.trim().isEmpty()) return null;

            ImageIcon original = null;

            String cleanPath = path.trim();

            java.io.File file = new java.io.File(cleanPath);
            if (file.exists()) {
                original = new ImageIcon(file.getAbsolutePath());
            }

            if (original == null) {
                file = new java.io.File("png/" + cleanPath);
                if (file.exists()) {
                    original = new ImageIcon(file.getAbsolutePath());
                }
            }

            if (original == null) {
                file = new java.io.File("src/main/resources/" + cleanPath);
                if (file.exists()) {
                    original = new ImageIcon(file.getAbsolutePath());
                }
            }

            if (original == null) {
                file = new java.io.File("src/main/resources/png/" + cleanPath);
                if (file.exists()) {
                    original = new ImageIcon(file.getAbsolutePath());
                }
            }

            if (original == null) {
                String onlyName = cleanPath.replace("\\", "/");

                if (onlyName.contains("/")) {
                    onlyName = onlyName.substring(onlyName.lastIndexOf("/") + 1);
                }

                file = new java.io.File("png/" + onlyName);
                if (file.exists()) {
                    original = new ImageIcon(file.getAbsolutePath());
                }
            }

            if (original == null) {
                String onlyName = cleanPath.replace("\\", "/");

                if (onlyName.contains("/")) {
                    onlyName = onlyName.substring(onlyName.lastIndexOf("/") + 1);
                }

                file = new java.io.File("src/main/resources/" + onlyName);
                if (file.exists()) {
                    original = new ImageIcon(file.getAbsolutePath());
                }
            }

            if (original == null) {
                String resourcePath = cleanPath.replace("src/main/resources/", "");

                java.net.URL url = getClass().getClassLoader().getResource(resourcePath);

                if (url == null) {
                    url = getClass().getClassLoader().getResource("png/" + resourcePath);
                }

                if (url == null) {
                    String onlyName = resourcePath.replace("\\", "/");

                    if (onlyName.contains("/")) {
                        onlyName = onlyName.substring(onlyName.lastIndexOf("/") + 1);
                    }

                    url = getClass().getClassLoader().getResource(onlyName);

                    if (url == null) {
                        url = getClass().getClassLoader().getResource("png/" + onlyName);
                    }
                }

                if (url != null) {
                    original = new ImageIcon(url);
                }
            }

            if (original == null || original.getIconWidth() <= 0 || original.getIconHeight() <= 0) {
                return null;
            }

            int originalWidth = original.getIconWidth();
            int originalHeight = original.getIconHeight();

            double scale = Math.min(
                    (double) maxWidth / originalWidth,
                    (double) maxHeight / originalHeight
            );

            int newWidth = Math.max(1, (int) Math.round(originalWidth * scale));
            int newHeight = Math.max(1, (int) Math.round(originalHeight * scale));

            Image image = original.getImage().getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);

            return new ImageIcon(image);

        } catch (Exception e) {
            return null;
        }
    }

    private void renderSavedRoomItems() {
        clearRenderedRoomItems();

        ArrayList<PlacedRoomItem> items = gameState.getCurrentRoomPlacedRoomItems();

        for (PlacedRoomItem placed : items) {
            if (placed == null) continue;

            placed.fixAfterLoad();

            JButton itemButton = createPlacedItemButton(placed);
            itemButton.setBounds(
                    placed.getX(),
                    placed.getY(),
                    Math.max(30, placed.getWidth()),
                    Math.max(30, placed.getHeight())
            );

            renderedRoomItems.add(itemButton);
            layeredPane.add(itemButton, Integer.valueOf(20));
        }

        layeredPane.repaint();
    }

    private JButton createPlacedItemButton(PlacedRoomItem placed) {
        placed.fixAfterLoad();

        JButton button = new JButton();
        button.setName("SAVED_ROOM_ITEM");
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setOpaque(false);
        button.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));

        int imageWidth = Math.max(30, placed.getWidth() - 80);
        int imageHeight = Math.max(30, placed.getHeight() - 50);

        ImageIcon icon = loadObjectIcon(placed, imageWidth, imageHeight);

        if (icon != null) {
            if (placed.isMirrored()) {
                icon = mirrorIcon(icon);
            }

            button.setIcon(icon);
            button.setText("");
        } else {
            button.setText(placed.getItemIcon());
            button.setFont(new Font("Arial", Font.BOLD, 36));
        }

        return button;
    }

    private ImageIcon loadObjectIcon(PlacedRoomItem placed, int maxWidth, int maxHeight) {
        if (placed == null) return null;

        placed.fixAfterLoad();

        ImageIcon icon = loadButtonIcon(placed.getImageFile(), maxWidth, maxHeight);

        if (icon != null) return icon;

        Item item = placed.getItem();

        if (item != null) {
            icon = loadButtonIcon(item.getImageFile(), maxWidth, maxHeight);

            if (icon != null) {
                placed.setImageFile(item.getImageFile());
                return icon;
            }
        }

        String path = placed.getImageFile();

        if (path != null) {
            String fileName = path.replace("\\", "/");

            if (fileName.contains("/")) {
                fileName = fileName.substring(fileName.lastIndexOf("/") + 1);
            }

            icon = loadButtonIcon("png/" + fileName, maxWidth, maxHeight);
            if (icon != null) return icon;

            icon = loadButtonIcon("src/main/resources/" + fileName, maxWidth, maxHeight);
            if (icon != null) return icon;

            icon = loadButtonIcon(fileName, maxWidth, maxHeight);
            if (icon != null) return icon;
        }

        return null;
    }

    private void clearRenderedRoomItems() {
        for (JComponent component : renderedRoomItems) {
            layeredPane.remove(component);
        }

        renderedRoomItems.clear();
        layeredPane.repaint();
    }

    public void prepareRoomItemsForRepair() {
        clearRenderedRoomItems();
    }

    public void reloadRoomItemsAfterRepair() {
        renderSavedRoomItems();
        updateTopInfo();
    }

    private void openRoomChooser() {
        ArrayList<Room> rooms = getAvailableRoomsForChooser();

        if (rooms == null || rooms.isEmpty()) {
            JOptionPane.showMessageDialog(this, "У тебе поки немає кімнат.");
            return;
        }

        JDialog dialog = new JDialog(this, "Квартира", true);
        dialog.setSize(430, 430);
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);

        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBackground(UiStyle.CREAM);
        root.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));

        JLabel title = new JLabel("Обери кімнату", SwingConstants.CENTER);
        title.setForeground(UiStyle.BLUE);
        title.setFont(UiStyle.titleFont(26));

        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBackground(UiStyle.CREAM);

        for (Room room : rooms) {
            if (room == null) continue;

            JButton roomButton = UiStyle.button(getRoomButtonText(room));
            roomButton.setFont(UiStyle.titleFont(18));
            roomButton.setMaximumSize(new Dimension(360, 48));
            roomButton.setPreferredSize(new Dimension(360, 48));
            roomButton.setAlignmentX(Component.CENTER_ALIGNMENT);

            roomButton.addActionListener(e -> {
                gameState.setCurrentRoomId(room.getId());
                SaveManager.saveGame(gameState);

                dialog.dispose();
                dispose();

                new HomeFrame(gameState).setVisible(true);
            });

            listPanel.add(roomButton);
            listPanel.add(Box.createVerticalStrut(10));
        }

        JScrollPane scroll = new JScrollPane(listPanel);
        scroll.setBorder(BorderFactory.createLineBorder(UiStyle.PURPLE, 2));
        scroll.getViewport().setBackground(UiStyle.CREAM);

        JButton closeButton = UiStyle.button("Закрити");
        closeButton.setFont(UiStyle.textFont(16));
        closeButton.addActionListener(e -> dialog.dispose());

        root.add(title, BorderLayout.NORTH);
        root.add(scroll, BorderLayout.CENTER);
        root.add(closeButton, BorderLayout.SOUTH);

        dialog.setContentPane(root);
        dialog.setVisible(true);
    }

    private ArrayList<Room> getAvailableRoomsForChooser() {
        ArrayList<Room> result = new ArrayList<>();
        ArrayList<String> usedRoomIds = new ArrayList<>();

        ArrayList<Room> allRooms = gameState.getRooms();

        if (allRooms == null) {
            return result;
        }

        Room mainRoom = null;

        for (Room room : allRooms) {
            if (room != null && "main".equals(room.getId())) {
                mainRoom = room;
                break;
            }
        }

        if (mainRoom != null) {
            result.add(mainRoom);
            usedRoomIds.add(mainRoom.getId());
        }

        for (Room room : allRooms) {
            if (room == null) continue;

            ArrayList<PlacedRoomItem> placedItems = room.getPlacedRoomItems();

            for (PlacedRoomItem placed : placedItems) {
                if (placed == null) continue;

                placed.fixAfterLoad();

                if (!placed.isDoor()) continue;

                String targetId = placed.getTargetRoomId();

                if (targetId == null || targetId.trim().isEmpty()) continue;
                if (usedRoomIds.contains(targetId)) continue;

                Room targetRoom = findRoomById(targetId);

                if (targetRoom != null) {
                    result.add(targetRoom);
                    usedRoomIds.add(targetRoom.getId());
                }
            }
        }

        return result;
    }

    private Room findRoomById(String roomId) {
        if (roomId == null) return null;

        ArrayList<Room> rooms = gameState.getRooms();

        if (rooms == null) return null;

        for (Room room : rooms) {
            if (room == null) continue;

            if (roomId.equals(room.getId())) {
                return room;
            }
        }

        return null;
    }

    private String getRoomButtonText(Room room) {
        if ("main".equals(room.getId())) {
            return "🏠 Твоя квартира";
        }

        String name = room.getName();

        if (name == null || name.trim().isEmpty()) {
            name = "Кімната";
        }

        return "🚪 " + name;
    }

    private void openInventoryPanel() {
        if (inventoryPanel != null) {
            layeredPane.remove(inventoryPanel);
            inventoryPanel = null;
            layeredPane.repaint();
        }

        InventoryFrame inventoryFrame = new InventoryFrame(gameState);
        inventoryFrame.setVisible(true);
    }

    private void openRepairFrameAndCheckTask() {
        setGameButtonsVisible(false);
        prepareRoomItemsForRepair();

        RepairFrame repairFrame = new RepairFrame(this, gameState);

        repairFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                setGameButtonsVisible(true);
                reloadRoomItemsAfterRepair();
                updateTopInfo();
            }

            @Override
            public void windowClosing(WindowEvent e) {
                setGameButtonsVisible(true);
                reloadRoomItemsAfterRepair();
                updateTopInfo();
            }
        });

        repairFrame.setVisible(true);
        updateTopInfo();
    }

    private void setGameButtonsVisible(boolean visible) {
        for (JComponent component : buttonsToHideInRepair) {
            component.setVisible(visible);
        }

        if (inventoryPanel != null) {
            layeredPane.remove(inventoryPanel);
            inventoryPanel = null;
        }

        layeredPane.repaint();
    }

    private void restPlayer() {
        gameState.setEnergy(100);

        SaveManager.saveGame(gameState);
        updateTopInfo();

        JOptionPane.showMessageDialog(
                this,
                "Ти відпочила. Енергія відновлена!",
                "Відпочинок",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    private void zoomApartment(double delta) {
        zoomScale += delta;

        if (zoomScale < 0.8) {
            zoomScale = 0.8;
        }

        if (zoomScale > 1.3) {
            zoomScale = 1.3;
        }

        int newWidth = (int) (WINDOW_WIDTH * zoomScale);
        int newHeight = (int) (WINDOW_HEIGHT * zoomScale);

        int x = (WINDOW_WIDTH - newWidth) / 2;
        int y = (WINDOW_HEIGHT - newHeight) / 2;

        backgroundLabel.setBounds(x, y, newWidth, newHeight);

        String backgroundPath = gameState.getCurrentRoomBackgroundImage();
        ImageIcon icon = loadBackgroundIcon(backgroundPath, newWidth, newHeight);

        if (icon != null) {
            backgroundLabel.setIcon(icon);
        }

        layeredPane.repaint();
    }

    private void updateTopInfo() {
        if (titleLabel != null) {
            titleLabel.setText(gameState.getCurrentRoomName());
        }

        if (comfortLabel != null) {
            comfortLabel.setText("Комфорт: " + gameState.getComfort() + " ⭐");
        }

        if (xpValueLabel != null) {
            xpValueLabel.setText("XP " + gameState.getXp() + "/100");
        }

        if (energyValueLabel != null) {
            energyValueLabel.setText(String.valueOf(gameState.getEnergy()));
        }

        if (silverValueLabel != null) {
            silverValueLabel.setText(String.valueOf(gameState.getSilver()));
        }

        if (goldValueLabel != null) {
            goldValueLabel.setText(String.valueOf(gameState.getGold()));
        }

        if (levelBadgeLabel != null) {
            levelBadgeLabel.setText(String.valueOf(gameState.getLevel()));
        }

        layeredPane.repaint();
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

    private static class DiamondLevelLabel extends JLabel {

        public DiamondLevelLabel(String text) {
            super(text, SwingConstants.CENTER);
            setForeground(Color.WHITE);
            setFont(new Font("Arial", Font.BOLD, 20));
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();

            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();

            Path2D diamond = new Path2D.Double();
            diamond.moveTo(w / 2.0, 3);
            diamond.lineTo(w - 3, h / 2.0);
            diamond.lineTo(w / 2.0, h - 3);
            diamond.lineTo(3, h / 2.0);
            diamond.closePath();

            g2.setColor(new Color(205, 35, 145));
            g2.fill(diamond);

            g2.setStroke(new BasicStroke(3));
            g2.setColor(new Color(120, 30, 120));
            g2.draw(diamond);

            g2.dispose();

            super.paintComponent(g);
        }
    }
}