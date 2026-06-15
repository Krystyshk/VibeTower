package vibetower.ui;

import vibetower.model.Catalog;
import vibetower.model.GameState;
import vibetower.model.Item;
import vibetower.model.SaveManager;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class ShopFrame extends JDialog {

    private final GameState gameState;
    private final ArrayList<Item> shopItems = Catalog.items();

    private JLabel silverLabel;
    private JLabel goldLabel;
    private JLabel levelLabel;

    private final JPanel itemsPanel = new ScrollableItemsPanel();

    private String category = "Усе";
    private String currencyFilter = "Усе";

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

    private static final String SILVER_ICON = "serebro.png";
    private static final String GOLD_ICON = "zoloto.png";

    public ShopFrame(JFrame parent, GameState gameState) {
        super(parent, "Магазин інтер’єру", true);

        this.gameState = gameState;

        setUndecorated(true);
        setSize(980, 640);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        setBackground(new Color(0, 0, 0, 0));

        JPanel content = createScreen();
        content.setOpaque(false);
        setContentPane(content);

        updateMoneyLabels();
        showItems();
    }

    public ShopFrame(GameState gameState) {
        this(null, gameState);
    }

    private JPanel createScreen() {
        JPanel root = new JPanel(new GridBagLayout());
        root.setOpaque(false);
        root.setBackground(new Color(0, 0, 0, 0));
        root.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        UiStyle.RoundPanel modal = new UiStyle.RoundPanel(
                new BorderLayout(18, 12),
                34,
                UiStyle.CREAM,
                UiStyle.PURPLE,
                3
        );

        modal.setPreferredSize(new Dimension(900, 560));
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

        JPanel leftInfo = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        leftInfo.setOpaque(true);
        leftInfo.setBackground(UiStyle.CREAM);
        leftInfo.setPreferredSize(new Dimension(230, 55));

        levelLabel = new JLabel();
        silverLabel = new JLabel();
        goldLabel = new JLabel();

        levelLabel.setForeground(UiStyle.BLUE);
        silverLabel.setForeground(UiStyle.BLUE);
        goldLabel.setForeground(UiStyle.BLUE);

        levelLabel.setFont(UiStyle.textFont(15));
        silverLabel.setFont(UiStyle.textFont(15));
        goldLabel.setFont(UiStyle.textFont(15));

        leftInfo.add(levelLabel);
        leftInfo.add(silverLabel);
        leftInfo.add(goldLabel);

        JLabel title = new JLabel("Магазин інтер’єру", SwingConstants.CENTER);
        title.setForeground(UiStyle.BLUE);
        title.setFont(UiStyle.titleFont(30));

        JButton home = UiStyle.button("Додому");
        home.setPreferredSize(new Dimension(115, 48));
        home.setFont(UiStyle.textFont(16));

        home.addActionListener(e -> {
            ShopFrame.this.setVisible(false);
            ShopFrame.this.dispose();
        });

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        rightPanel.setOpaque(true);
        rightPanel.setBackground(UiStyle.CREAM);
        rightPanel.setPreferredSize(new Dimension(130, 55));
        rightPanel.add(home);

        header.add(leftInfo, BorderLayout.WEST);
        header.add(title, BorderLayout.CENTER);
        header.add(rightPanel, BorderLayout.EAST);

        return header;
    }

    private JPanel createCategories() {
        JPanel panel = new JPanel(new GridLayout(CATEGORIES.length, 1, 0, 8));
        panel.setOpaque(true);
        panel.setBackground(UiStyle.CREAM);
        panel.setPreferredSize(new Dimension(165, 0));

        for (String c : CATEGORIES) {
            JButton button = UiStyle.button(c);
            button.setFont(UiStyle.textFont(15));

            button.addActionListener(e -> {
                category = c;
                showItems();
            });

            panel.add(button);
        }

        return panel;
    }

    private JPanel createCenter() {
        JPanel center = new JPanel(new BorderLayout(0, 12));
        center.setOpaque(true);
        center.setBackground(UiStyle.CREAM);

        JPanel filters = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        filters.setOpaque(true);
        filters.setBackground(UiStyle.CREAM);

        JButton all = UiStyle.button("Усе");
        JButton silver = UiStyle.button("За срібло");
        JButton gold = UiStyle.button("За золото");

        all.setFont(UiStyle.textFont(15));
        silver.setFont(UiStyle.textFont(15));
        gold.setFont(UiStyle.textFont(15));

        all.addActionListener(e -> {
            currencyFilter = "Усе";
            showItems();
        });

        silver.addActionListener(e -> {
            currencyFilter = "silver";
            showItems();
        });

        gold.addActionListener(e -> {
            currencyFilter = "gold";
            showItems();
        });

        filters.add(all);
        filters.add(silver);
        filters.add(gold);

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

        center.add(filters, BorderLayout.NORTH);
        center.add(scroll, BorderLayout.CENTER);

        return center;
    }

    private void showItems() {
        itemsPanel.removeAll();

        for (Item item : shopItems) {
            if (item == null) continue;

            boolean okCategory = Catalog.matchesCategory(item, category);
            boolean okCurrency = currencyFilter.equals("Усе") || item.getCurrency().equals(currencyFilter);

            if (okCategory && okCurrency) {
                itemsPanel.add(createCard(item));
            }
        }

        itemsPanel.revalidate();
        itemsPanel.repaint();
    }

    private JPanel createCard(Item item) {
        UiStyle.RoundPanel card = new UiStyle.RoundPanel(
                new BorderLayout(6, 6),
                20,
                new Color(255, 252, 247),
                UiStyle.PURPLE,
                2
        );

        card.setPreferredSize(new Dimension(185, 225));
        card.setMinimumSize(new Dimension(185, 225));
        card.setMaximumSize(new Dimension(185, 225));
        card.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        card.setOpaque(false);

        JLabel name = new JLabel(item.getName(), SwingConstants.CENTER);
        name.setForeground(UiStyle.BLUE);
        name.setFont(UiStyle.textFont(14));

        JPanel imageWrap = new JPanel(new GridBagLayout());
        imageWrap.setOpaque(true);
        imageWrap.setBackground(new Color(255, 252, 247));
        imageWrap.setPreferredSize(new Dimension(150, 80));

        JLabel image = new JLabel("", SwingConstants.CENTER);
        image.setHorizontalAlignment(SwingConstants.CENTER);
        image.setVerticalAlignment(SwingConstants.CENTER);

        ImageIcon icon = UiStyle.iconContain(item.getImageFile(), 125, 78);

        if (icon != null) {
            image.setIcon(icon);
        } else {
            image.setText("<html><center><span style='font-size:34px'>" + item.getIcon() + "</span></center></html>");
        }

        imageWrap.add(image);

        JLabel levelInfo = new JLabel(
                "Відкривається з " + item.getRequiredLevel() + " рівня",
                SwingConstants.CENTER
        );

        levelInfo.setFont(UiStyle.textFont(10));

        if (gameState.getLevel() >= item.getRequiredLevel()) {
            levelInfo.setForeground(new Color(70, 120, 70));
        } else {
            levelInfo.setForeground(new Color(170, 70, 70));
        }

        JLabel price = createMoneyLabel(item.getCurrency(), item.getPrice(), 14, 18);
        price.setHorizontalAlignment(SwingConstants.CENTER);

        JButton buy = UiStyle.button("Купити");
        buy.setFont(UiStyle.textFont(13));
        buy.setPreferredSize(new Dimension(130, 30));

        if (gameState.getLevel() < item.getRequiredLevel()) {
            buy.setText("Закрито");
            buy.setEnabled(false);
        } else {
            buy.addActionListener(e -> buyItem(item));
        }

        JPanel bottom = new JPanel(new GridLayout(3, 1, 0, 4));
        bottom.setOpaque(true);
        bottom.setBackground(new Color(255, 252, 247));
        bottom.add(levelInfo);
        bottom.add(price);
        bottom.add(buy);

        card.add(name, BorderLayout.NORTH);
        card.add(imageWrap, BorderLayout.CENTER);
        card.add(bottom, BorderLayout.SOUTH);

        return card;
    }

    private JLabel createMoneyLabel(String currency, int amount, int fontSize, int iconSize) {
        JLabel label = new JLabel(String.valueOf(amount));

        label.setForeground(UiStyle.BLUE);
        label.setFont(UiStyle.textFont(fontSize));
        label.setHorizontalTextPosition(SwingConstants.RIGHT);
        label.setIconTextGap(5);

        String iconPath = "gold".equals(currency) ? GOLD_ICON : SILVER_ICON;
        ImageIcon icon = UiStyle.iconContain(iconPath, iconSize, iconSize);

        if (icon != null) {
            label.setIcon(icon);
        } else {
            label.setText(("gold".equals(currency) ? "🟡 " : "⚪ ") + amount);
        }

        return label;
    }

    private void buyItem(Item item) {
        String message = gameState.tryBuyItem(item);

        JOptionPane.showMessageDialog(this, message);

        SaveManager.saveGame(gameState);

        updateMoneyLabels();
        showItems();
    }

    private void updateMoneyLabels() {
        levelLabel.setText("💎 " + gameState.getLevel() + " рів.");

        silverLabel.setText(String.valueOf(gameState.getSilver()));
        goldLabel.setText(String.valueOf(gameState.getGold()));

        ImageIcon silverIcon = UiStyle.iconContain(SILVER_ICON, 18, 18);
        ImageIcon goldIcon = UiStyle.iconContain(GOLD_ICON, 18, 18);

        if (silverIcon != null) {
            silverLabel.setIcon(silverIcon);
            silverLabel.setIconTextGap(5);
        } else {
            silverLabel.setText("⚪ " + gameState.getSilver());
        }

        if (goldIcon != null) {
            goldLabel.setIcon(goldIcon);
            goldLabel.setIconTextGap(5);
        } else {
            goldLabel.setText("🟡 " + gameState.getGold());
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