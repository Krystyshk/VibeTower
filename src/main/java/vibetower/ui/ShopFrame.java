package vibetower.ui;

import vibetower.model.GameState;
import vibetower.model.HomeFrame;
import vibetower.model.Item;

import javax.swing.*;
import java.awt.*;

public class ShopFrame extends JFrame {

    private final GameState gameState;
    private JPanel topPanel;
    private JPanel goodsPanel;
    private String currentCurrencyFilter = "all";

    public ShopFrame(GameState gameState) {
        this.gameState = gameState;
        this.gameState.fixAfterLoad();

        setTitle("VibeTower — Магазин інтер'єру");
        setSize(1100, 720);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(252, 248, 240));

        topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.setBorder(BorderFactory.createEmptyBorder(18, 25, 10, 25));

        JButton homeButton = createSmallButton("🏠 Додому");
        homeButton.setPreferredSize(new Dimension(150, 42));
        homeButton.addActionListener(e -> {
            new HomeFrame(gameState).setVisible(true);
            dispose();
        });

        JLabel titleLabel = new JLabel("Магазин меблів", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 34));
        titleLabel.setForeground(new Color(72, 37, 120));

        topPanel.add(homeButton, BorderLayout.WEST);
        topPanel.add(titleLabel, BorderLayout.CENTER);
        topPanel.add(new CurrencyPanel(gameState), BorderLayout.EAST);

        mainPanel.add(topPanel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setOpaque(false);

        centerPanel.add(createLeftMenu(), BorderLayout.WEST);

        JPanel shopArea = new JPanel(new BorderLayout());
        shopArea.setOpaque(false);

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 8));
        filterPanel.setOpaque(false);

        JButton allFilter = createSmallButton("Усе");
        JButton silverFilter = createSmallButton("За срібло");
        JButton goldFilter = createSmallButton("За золото");

        allFilter.addActionListener(e -> {
            currentCurrencyFilter = "all";
            showGoods("Усе");
        });

        silverFilter.addActionListener(e -> {
            currentCurrencyFilter = "silver";
            showGoods("Усе");
        });

        goldFilter.addActionListener(e -> {
            currentCurrencyFilter = "gold";
            showGoods("Усе");
        });

        filterPanel.add(allFilter);
        filterPanel.add(silverFilter);
        filterPanel.add(goldFilter);

        shopArea.add(filterPanel, BorderLayout.NORTH);

        goodsPanel = new JPanel(new GridLayout(0, 3, 18, 18));
        goodsPanel.setBackground(new Color(252, 248, 240));
        goodsPanel.setBorder(BorderFactory.createEmptyBorder(20, 35, 30, 35));

        JScrollPane scrollPane = new JScrollPane(goodsPanel);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(new Color(252, 248, 240));
        shopArea.add(scrollPane, BorderLayout.CENTER);

        centerPanel.add(shopArea, BorderLayout.CENTER);
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        add(mainPanel, BorderLayout.CENTER);

        showGoods("Усе");
    }

    public ShopFrame() {
        this(new GameState());
    }

    private JPanel createLeftMenu() {
        JPanel leftMenu = new JPanel();
        leftMenu.setLayout(new BoxLayout(leftMenu, BoxLayout.Y_AXIS));
        leftMenu.setPreferredSize(new Dimension(220, 0));
        leftMenu.setBackground(new Color(235, 245, 235));
        leftMenu.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 0, 3, new Color(120, 170, 80)),
                BorderFactory.createEmptyBorder(18, 18, 18, 18)
        ));

        String[] categories = {
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

        for (String category : categories) {
            JButton button = createCategoryButton(category);
            button.addActionListener(e -> showGoods(category));

            leftMenu.add(button);
            leftMenu.add(Box.createVerticalStrut(8));
        }

        return leftMenu;
    }

    private void showGoods(String category) {
        goodsPanel.removeAll();

        Item[] items = {
                new Item("Чорний диван", "Дивани", "🛋", 1200, "silver", 2),
                new Item("Білий диван", "Дивани", "🛋", 1400, "silver", 2),
                new Item("Кутовий диван", "Дивани", "🛋", 35, "gold", 3),

                new Item("Стілець", "Стільці", "🪑", 250, "silver", 1),
                new Item("Крісло", "Стільці", "🪑", 500, "silver", 2),

                new Item("Односпальне ліжко", "Ліжка", "🛏", 800, "silver", 4),
                new Item("Двоспальне ліжко", "Ліжка", "🛏", 1300, "silver", 5),

                new Item("Шафа", "Шафи", "🗄", 700, "silver", 3),
                new Item("Комод", "Шафи", "🗄", 500, "silver", 3),

                new Item("Журнальний стіл", "Столи", "▭", 450, "silver", 2),
                new Item("Обідній стіл", "Столи", "▭", 650, "silver", 3),

                new Item("Телевізор", "Техніка", "📺", 900, "silver", 4),
                new Item("Комп'ютер", "Техніка", "💻", 1200, "silver", 6),
                new Item("Колонки", "Техніка", "🔊", 350, "silver", 4),

                new Item("Лампа", "Освітлення", "💡", 200, "silver", 1),
                new Item("Торшер", "Освітлення", "💡", 350, "silver", 2),

                new Item("Картина", "Декор", "🖼", 200, "silver", 1),
                new Item("Килим", "Декор", "▤", 300, "silver", 1),
                new Item("Ваза", "Декор", "🏺", 12, "gold", 1),

                new Item("Рослина", "Рослини", "🪴", 180, "silver", 1),
                new Item("Пальма", "Рослини", "🌴", 350, "silver", 2),

                new Item("Унітаз", "Санвузол", "🚽", 250, "silver", 3),
                new Item("Ванна", "Санвузол", "🛁", 450, "silver", 3),
                new Item("Раковина", "Санвузол", "🚰", 260, "silver", 3),
                new Item("Дзеркало", "Санвузол", "🪞", 180, "silver", 3),

                new Item("Холодильник", "Кухня", "🧊", 900, "silver", 6),
                new Item("Плита", "Кухня", "🍳", 700, "silver", 6),
                new Item("Кухонний стіл", "Кухня", "▭", 600, "silver", 6)
        };

        for (Item item : items) {
            boolean categoryOk = "Усе".equals(category) || item.getCategory().equals(category);
            boolean currencyOk = "all".equals(currentCurrencyFilter)
                    || item.getCurrency().equalsIgnoreCase(currentCurrencyFilter);

            if (categoryOk && currencyOk) {
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
        iconLabel.setFont(new Font("Arial", Font.BOLD, 48));

        JLabel nameLabel = new JLabel(item.getName(), SwingConstants.CENTER);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 19));
        nameLabel.setForeground(new Color(72, 37, 120));

        String currencyText = item.isGoldItem() ? " золота" : " срібла";

        JLabel infoLabel = new JLabel(
                "<html><center>"
                        + item.getCategory()
                        + "<br>Ціна: " + item.getPrice() + currencyText
                        + "<br>Рівень: " + item.getRequiredLevel()
                        + "</center></html>",
                SwingConstants.CENTER
        );

        infoLabel.setFont(new Font("Arial", Font.BOLD, 14));
        infoLabel.setForeground(new Color(120, 82, 160));

        JButton previewButton = createSmallButton("Переглянути");
        previewButton.addActionListener(e -> JOptionPane.showMessageDialog(
                this,
                item.getIcon() + " " + item.getName()
                        + "\nКатегорія: " + item.getCategory()
                        + "\nЦіна: " + item.getPrice() + currencyText
                        + "\nДоступно з рівня: " + item.getRequiredLevel(),
                "Перегляд товару",
                JOptionPane.INFORMATION_MESSAGE
        ));

        JButton buyButton = createSmallButton("Купити");
        buyButton.addActionListener(e -> confirmPurchase(item));

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

    private void confirmPurchase(Item item) {
        String currencyText = item.isGoldItem() ? " золота" : " срібла";

        int answer = JOptionPane.showConfirmDialog(
                this,
                "Підтвердити покупку?\n\n"
                        + item.getName()
                        + "\nЦіна: " + item.getPrice() + currencyText
                        + "\n\nПісля покупки предмет буде додано до інвентарю.",
                "Підтвердження покупки",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (answer != JOptionPane.YES_OPTION) {
            JOptionPane.showMessageDialog(
                    this,
                    "Покупку скасовано.",
                    "Скасовано",
                    JOptionPane.INFORMATION_MESSAGE
            );
            return;
        }

        String result = gameState.tryBuyItem(item);

        if (result.contains("додано")) {
            JOptionPane.showMessageDialog(
                    this,
                    "✅ Покупку успішно завершено!\n\n"
                            + item.getName()
                            + "\nдодано до інвентарю.",
                    "Покупка",
                    JOptionPane.INFORMATION_MESSAGE
            );
        } else {
            JOptionPane.showMessageDialog(
                    this,
                    result,
                    "Помилка покупки",
                    JOptionPane.WARNING_MESSAGE
            );
        }

        refreshCurrency();
    }

    private JButton createCategoryButton(String text) {
        JButton button = new JButton(text);
        button.setMaximumSize(new Dimension(180, 40));
        button.setPreferredSize(new Dimension(180, 40));
        button.setFont(new Font("Arial", Font.BOLD, 15));
        button.setBackground(new Color(255, 236, 170));
        button.setForeground(new Color(72, 37, 120));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(new Color(120, 82, 160), 2));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
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
        topPanel.removeAll();

        JButton homeButton = createSmallButton("🏠 Додому");
        homeButton.setPreferredSize(new Dimension(150, 42));
        homeButton.addActionListener(e -> {
            new HomeFrame(gameState).setVisible(true);
            dispose();
        });

        JLabel titleLabel = new JLabel("Магазин меблів", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 34));
        titleLabel.setForeground(new Color(72, 37, 120));

        topPanel.add(homeButton, BorderLayout.WEST);
        topPanel.add(titleLabel, BorderLayout.CENTER);
        topPanel.add(new CurrencyPanel(gameState), BorderLayout.EAST);

        topPanel.revalidate();
        topPanel.repaint();
    }
}