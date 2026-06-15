package vibetower.ui;

import vibetower.model.GameState;
import vibetower.model.Item;
import vibetower.model.SaveManager;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;

public class InventoryFrame extends JDialog {

    private final GameState gameState;

    private JLabel countLabel;
    private final JPanel itemsPanel = new ScrollableItemsPanel();

    private String selectedCategory = "Усе";

    private static final String[] CATEGORIES = {
            "Усе",
            "Дивани",
            "Стільці",
            "Ліжка",
            "Шафи",
            "Столи",
            "Техніка",
            "Освітлення",
            "Декор",
            "Рослини",
            "Санвузол",
            "Кухня"
    };

    public InventoryFrame(JFrame parent, GameState gameState) {
        super(parent, "Інвентар", true);

        this.gameState = gameState;

        setUndecorated(true);
        setSize(930, 580);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        setBackground(new Color(0, 0, 0, 0));

        JPanel content = createScreen();
        content.setOpaque(false);
        setContentPane(content);

        showItems();
    }

    public InventoryFrame(GameState gameState) {
        this(null, gameState);
    }

    private JPanel createScreen() {
        JPanel root = new JPanel(new GridBagLayout());
        root.setOpaque(false);
        root.setBackground(new Color(0, 0, 0, 0));

        UiStyle.RoundPanel modal = new UiStyle.RoundPanel(
                new BorderLayout(18, 12),
                34,
                UiStyle.CREAM,
                UiStyle.PURPLE,
                3
        );

        modal.setPreferredSize(new Dimension(850, 520));
        modal.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));
        modal.setOpaque(false);

        root.add(modal);

        modal.add(createHeader(), BorderLayout.NORTH);
        modal.add(createCategories(), BorderLayout.WEST);
        modal.add(createCenter(), BorderLayout.CENTER);

        return root;
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout(10, 0));
        header.setOpaque(true);
        header.setBackground(UiStyle.CREAM);

        countLabel = new JLabel();
        countLabel.setForeground(UiStyle.BLUE);
        countLabel.setFont(UiStyle.textFont(16));
        countLabel.setPreferredSize(new Dimension(185, 50));

        JLabel title = new JLabel("Інвентар", SwingConstants.CENTER);
        title.setForeground(UiStyle.BLUE);
        title.setFont(UiStyle.titleFont(30));

        JButton home = UiStyle.button("Додому");
        home.setPreferredSize(new Dimension(115, 45));
        home.setFont(UiStyle.textFont(16));

        home.addActionListener(e -> {
            InventoryFrame.this.setVisible(false);
            InventoryFrame.this.dispose();
        });

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        rightPanel.setOpaque(true);
        rightPanel.setBackground(UiStyle.CREAM);
        rightPanel.setPreferredSize(new Dimension(130, 50));
        rightPanel.add(home);

        header.add(countLabel, BorderLayout.WEST);
        header.add(title, BorderLayout.CENTER);
        header.add(rightPanel, BorderLayout.EAST);

        return header;
    }

    private JPanel createCategories() {
        JPanel panel = new JPanel(new GridLayout(CATEGORIES.length, 1, 0, 8));
        panel.setOpaque(true);
        panel.setBackground(UiStyle.CREAM);
        panel.setPreferredSize(new Dimension(160, 0));

        for (String category : CATEGORIES) {
            JButton button = UiStyle.button(category);
            button.setFont(UiStyle.textFont(14));

            button.addActionListener(e -> {
                selectedCategory = category;
                showItems();
            });

            panel.add(button);
        }

        return panel;
    }

    private JPanel createCenter() {
        JPanel center = new JPanel(new BorderLayout());
        center.setOpaque(true);
        center.setBackground(UiStyle.CREAM);

        itemsPanel.setOpaque(true);
        itemsPanel.setBackground(UiStyle.CREAM);

        JScrollPane scroll = new JScrollPane(itemsPanel);
        scroll.setBorder(null);
        scroll.setOpaque(true);
        scroll.setBackground(UiStyle.CREAM);
        scroll.getViewport().setOpaque(true);
        scroll.getViewport().setBackground(UiStyle.CREAM);

        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scroll.getVerticalScrollBar().setUnitIncrement(18);

        center.add(scroll, BorderLayout.CENTER);

        return center;
    }

    private void showItems() {
        itemsPanel.removeAll();

        LinkedHashMap<String, InventoryGroup> groupedItems = groupInventoryItems();

        int totalCount = 0;

        for (InventoryGroup group : groupedItems.values()) {
            totalCount += group.count;
        }

        countLabel.setText("Предметів: " + totalCount);

        if (groupedItems.isEmpty()) {
            showEmptyInventory();
        } else {
            itemsPanel.setLayout(new GridLayout(0, 3, 14, 14));

            for (InventoryGroup group : groupedItems.values()) {
                itemsPanel.add(createCard(group.item, group.count));
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
            if (isDoor(item)) continue;
            if (!matchesSelectedCategory(item)) continue;

            String key = item.getName() + "|" + item.getImageFile();

            if (groupedItems.containsKey(key)) {
                groupedItems.get(key).count++;
            } else {
                groupedItems.put(key, new InventoryGroup(item, 1));
            }
        }

        return groupedItems;
    }

    private boolean matchesSelectedCategory(Item item) {
        if (selectedCategory == null || selectedCategory.equals("Усе")) return true;
        if (item.getCategory() == null) return false;

        return item.getCategory().equals(selectedCategory);
    }

    private void showEmptyInventory() {
        itemsPanel.setLayout(new BorderLayout());

        JLabel empty = new JLabel(
                "<html><center>У цьому розділі нічого немає<br>Купи предмети в магазині</center></html>",
                SwingConstants.CENTER
        );

        empty.setForeground(UiStyle.BLUE);
        empty.setFont(UiStyle.textFont(22));

        JPanel emptyPanel = new JPanel(new GridBagLayout());
        emptyPanel.setOpaque(true);
        emptyPanel.setBackground(UiStyle.CREAM);
        emptyPanel.add(empty);

        itemsPanel.add(emptyPanel, BorderLayout.CENTER);
    }

    private JPanel createCard(Item item, int count) {
        UiStyle.RoundPanel card = new UiStyle.RoundPanel(
                new BorderLayout(6, 6),
                20,
                new Color(255, 252, 247),
                UiStyle.PURPLE,
                2
        );

        card.setPreferredSize(new Dimension(185, 210));
        card.setMinimumSize(new Dimension(185, 210));
        card.setMaximumSize(new Dimension(185, 210));
        card.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        card.setOpaque(false);

        JLabel name = new JLabel(item.getName(), SwingConstants.CENTER);
        name.setForeground(UiStyle.BLUE);
        name.setFont(UiStyle.textFont(14));

        JPanel imageWrap = new JPanel(new GridBagLayout());
        imageWrap.setOpaque(true);
        imageWrap.setBackground(new Color(255, 252, 247));
        imageWrap.setPreferredSize(new Dimension(150, 95));

        JLabel image = new JLabel("", SwingConstants.CENTER);
        image.setHorizontalAlignment(SwingConstants.CENTER);
        image.setVerticalAlignment(SwingConstants.CENTER);

        ImageIcon icon = UiStyle.iconContain(item.getImageFile(), 130, 90);

        if (icon != null) {
            image.setIcon(icon);
        } else {
            image.setText("<html><center><span style='font-size:34px'>" + item.getIcon() + "</span></center></html>");
        }

        imageWrap.add(image);

        JLabel category = new JLabel(item.getCategory(), SwingConstants.CENTER);
        category.setForeground(new Color(100, 80, 130));
        category.setFont(UiStyle.textFont(12));

        JLabel amount = new JLabel("Кількість: ×" + count, SwingConstants.CENTER);
        amount.setForeground(UiStyle.BLUE);
        amount.setFont(UiStyle.textFont(13));

        JButton place = UiStyle.button("Поставити");
        place.setFont(UiStyle.textFont(13));
        place.setPreferredSize(new Dimension(130, 30));

        place.addActionListener(e -> placeItem(item));

        JPanel bottom = new JPanel(new GridLayout(3, 1, 0, 4));
        bottom.setOpaque(true);
        bottom.setBackground(new Color(255, 252, 247));
        bottom.add(category);
        bottom.add(amount);
        bottom.add(place);

        card.add(name, BorderLayout.NORTH);
        card.add(imageWrap, BorderLayout.CENTER);
        card.add(bottom, BorderLayout.SOUTH);

        return card;
    }

    private void placeItem(Item item) {
        if (isDoor(item)) {
            JOptionPane.showMessageDialog(
                    this,
                    "Двері не можна ставити як предмет. Вони відкривають нову кімнату."
            );
            return;
        }

        gameState.placeItem(item);

        SaveManager.saveGame(gameState);

        JOptionPane.showMessageDialog(
                this,
                "Предмет додано в кімнату: " + item.getName()
        );

        showItems();
    }

    private boolean isDoor(Item item) {
        if (item == null || item.getName() == null) return false;

        String name = item.getName().toLowerCase();

        return name.contains("двері");
    }

    private static class InventoryGroup {
        private final Item item;
        private int count;

        public InventoryGroup(Item item, int count) {
            this.item = item;
            this.count = count;
        }
    }

    private static class ScrollableItemsPanel extends JPanel implements Scrollable {

        public ScrollableItemsPanel() {
            super(new GridLayout(0, 3, 14, 14));
            setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 8));
            setOpaque(true);
            setBackground(UiStyle.CREAM);
        }

        @Override
        public Dimension getPreferredScrollableViewportSize() {
            return getPreferredSize();
        }

        @Override
        public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
            return 18;
        }

        @Override
        public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
            return 180;
        }

        @Override
        public boolean getScrollableTracksViewportWidth() {
            return true;
        }

        @Override
        public boolean getScrollableTracksViewportHeight() {
            return false;
        }
    }
}