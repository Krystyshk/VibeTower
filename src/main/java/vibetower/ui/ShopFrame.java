package vibetower.ui;

import vibetower.model.GameState;
import vibetower.model.Item;

import javax.swing.*;
import java.awt.*;

public class ShopFrame extends JFrame {

    private final GameState gameState;
    private JPanel topPanel;
    private JPanel goodsPanel;

    public ShopFrame(GameState gameState) {
        this.gameState = gameState;
        this.gameState.fixAfterLoad();

        setTitle("VibeTower — Магазин інтер'єру");
        setSize(980, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(252, 248, 240));

        topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.setBorder(BorderFactory.createEmptyBorder(18, 25, 10, 25));

        JLabel titleLabel = new JLabel("Магазин інтер'єру", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 34));
        titleLabel.setForeground(new Color(72, 37, 120));

        topPanel.add(titleLabel, BorderLayout.CENTER);
        topPanel.add(new CurrencyPanel(gameState), BorderLayout.EAST);
        mainPanel.add(topPanel, BorderLayout.NORTH);

        JPanel tabs = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        tabs.setOpaque(false);

        JButton allButton = createTabButton("Усе");
        JButton bathroomButton = createTabButton("Санвузол");
        JButton bedroomButton = createTabButton("Спальня");
        JButton kitchenButton = createTabButton("Кухня");
        JButton techButton = createTabButton("Техніка");
        JButton decorButton = createTabButton("Декор");
        JButton premiumButton = createTabButton("Преміум");

        tabs.add(allButton);
        tabs.add(bathroomButton);
        tabs.add(bedroomButton);
        tabs.add(kitchenButton);
        tabs.add(techButton);
        tabs.add(decorButton);
        tabs.add(premiumButton);

        mainPanel.add(tabs, BorderLayout.BEFORE_FIRST_LINE);

        goodsPanel = new JPanel(new GridLayout(0, 3, 18, 18));
        goodsPanel.setBackground(new Color(252, 248, 240));
        goodsPanel.setBorder(BorderFactory.createEmptyBorder(20, 45, 30, 45));

        JScrollPane scrollPane = new JScrollPane(goodsPanel);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(new Color(252, 248, 240));
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        allButton.addActionListener(e -> showGoods("Усе"));
        bathroomButton.addActionListener(e -> showGoods("Санвузол"));
        bedroomButton.addActionListener(e -> showGoods("Спальня"));
        kitchenButton.addActionListener(e -> showGoods("Кухня"));
        techButton.addActionListener(e -> showGoods("Техніка"));
        decorButton.addActionListener(e -> showGoods("Декор"));
        premiumButton.addActionListener(e -> showGoods("Преміум"));

        add(mainPanel, BorderLayout.CENTER);
        showGoods("Усе");
    }

    public ShopFrame() {
        this(new GameState());
    }

    private void showGoods(String category) {
        goodsPanel.removeAll();

        Item[] items = {
                new Item("Унітаз", "Санвузол", "🚽", 220, "silver", 3),
                new Item("Ванна", "Санвузол", "🛁", 450, "silver", 3),
                new Item("Раковина", "Санвузол", "🚰", 260, "silver", 3),
                new Item("Дзеркало", "Санвузол", "🪞", 180, "silver", 3),

                new Item("Ліжко", "Спальня", "🛏", 600, "silver", 5),
                new Item("Шафа", "Спальня", "🗄", 480, "silver", 5),
                new Item("Тумба", "Спальня", "▣", 180, "silver", 5),
                new Item("Лампа", "Спальня", "💡", 130, "silver", 2),

                new Item("Холодильник", "Кухня", "🧊", 800, "silver", 7),
                new Item("Плита", "Кухня", "🍳", 650, "silver", 7),
                new Item("Стіл", "Кухня", "▭", 280, "silver", 2),
                new Item("Стільці", "Кухня", "🪑", 240, "silver", 2),

                new Item("Телевізор", "Техніка", "📺", 900, "silver", 4),
                new Item("Комп'ютер", "Техніка", "💻", 1200, "silver", 6),
                new Item("Колонки", "Техніка", "🔊", 350, "silver", 4),

                new Item("Картина", "Декор", "🖼", 200, "silver", 1),
                new Item("Рослина", "Декор", "🪴", 160, "silver", 1),
                new Item("Килим", "Декор", "▤", 260, "silver", 1),

                new Item("Золота люстра", "Преміум", "💎", 8, "gold", 8),
                new Item("Преміум-диван", "Преміум", "🛋", 12, "gold", 9),
                new Item("Рідкісна статуетка", "Преміум", "🏆", 15, "gold", 10)
        };

        for (Item item : items) {
            if ("Усе".equals(category) || item.getCategory().equals(category)) {
                goodsPanel.add(createShopCard(item));
            }
        }

        goodsPanel.revalidate();
        goodsPanel.repaint();
    }

    private JPanel createShopCard(Item item) {
        JPanel card = new JPanel(new BorderLayout(8, 8));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(120, 82, 160), 3),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        JLabel iconLabel = new JLabel(item.getIcon(), SwingConstants.CENTER);
        iconLabel.setFont(new Font("Arial", Font.BOLD, 44));
        iconLabel.setForeground(new Color(120, 82, 160));

        JLabel nameLabel = new JLabel(item.getName(), SwingConstants.CENTER);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 20));
        nameLabel.setForeground(new Color(72, 37, 120));

        String currencyText = item.isGoldItem() ? " золота" : " срібла";
        JLabel infoLabel = new JLabel("<html><center>"
                + item.getCategory()
                + "<br>Ціна: " + item.getPrice() + currencyText
                + "<br>Рівень: " + item.getRequiredLevel()
                + "</center></html>", SwingConstants.CENTER);
        infoLabel.setFont(new Font("Arial", Font.BOLD, 14));
        infoLabel.setForeground(new Color(120, 82, 160));

        JButton previewButton = createSmallButton("Переглянути");
        previewButton.addActionListener(e -> JOptionPane.showMessageDialog(
                this,
                item.getIcon() + " " + item.getName()
                        + "\nКатегорія: " + item.getCategory()
                        + "\nДоступно з рівня: " + item.getRequiredLevel(),
                "Перегляд товару",
                JOptionPane.INFORMATION_MESSAGE
        ));

        JButton buyButton = createSmallButton("Купити");
        buyButton.addActionListener(e -> {
            String result = gameState.tryBuyItem(item);

            JOptionPane.showMessageDialog(
                    this,
                    result,
                    "Покупка",
                    result.contains("додано") ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.WARNING_MESSAGE
            );

            refreshCurrency();
        });

        JPanel buttons = new JPanel(new GridLayout(1, 2, 8, 0));
        buttons.setOpaque(false);
        buttons.add(previewButton);
        buttons.add(buyButton);

        JPanel top = new JPanel(new GridLayout(2, 1));
        top.setOpaque(false);
        top.add(nameLabel);
        top.add(infoLabel);

        card.add(top, BorderLayout.NORTH);
        card.add(iconLabel, BorderLayout.CENTER);
        card.add(buttons, BorderLayout.SOUTH);

        return card;
    }

    private JButton createTabButton(String text) {
        JButton button = createSmallButton(text);
        button.setPreferredSize(new Dimension(120, 36));
        return button;
    }

    private JButton createSmallButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(new Color(255, 218, 130));
        button.setForeground(new Color(72, 37, 120));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(new Color(120, 82, 160), 2));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private void refreshCurrency() {
        topPanel.remove(1);
        topPanel.add(new CurrencyPanel(gameState), BorderLayout.EAST);
        topPanel.revalidate();
        topPanel.repaint();
    }
}
