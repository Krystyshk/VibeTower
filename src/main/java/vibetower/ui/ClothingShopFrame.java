package vibetower.ui;

import vibetower.model.GameState;
import vibetower.model.Item;

import javax.swing.*;
import java.awt.*;

public class ClothingShopFrame extends JFrame {

    private final GameState gameState;

    private JLabel silverLabel;
    private JLabel goldLabel;

    private final Item[] clothingItems = {
            new Item("Біла футболка", "Верх", 80, "silver", 1),
            new Item("Рожевий топ", "Верх", 120, "silver", 1),
            new Item("Сорочка", "Верх", 160, "silver", 2),
            new Item("Чорна кофта", "Верх", 190, "silver", 2),
            new Item("Стильна куртка", "Верх", 5, "gold", 3),

            new Item("Джинси", "Низ", 150, "silver", 1),
            new Item("Спідниця", "Низ", 130, "silver", 1),
            new Item("Шорти", "Низ", 110, "silver", 1),
            new Item("Класичні штани", "Низ", 200, "silver", 2),
            new Item("Шкіряні штани", "Низ", 6, "gold", 4),

            new Item("Літня сукня", "Сукня", 220, "silver", 2),
            new Item("Червона сукня", "Сукня", 280, "silver", 3),
            new Item("Коротка сукня", "Сукня", 230, "silver", 2),
            new Item("Вечірня сукня", "Сукня", 10, "gold", 4),
            new Item("Золота сукня", "Сукня", 14, "gold", 5),

            new Item("Кросівки", "Взуття", 140, "silver", 1),
            new Item("Туфлі", "Взуття", 180, "silver", 2),
            new Item("Черевики", "Взуття", 210, "silver", 2),
            new Item("Босоніжки", "Взуття", 160, "silver", 1),
            new Item("Золоті туфлі", "Взуття", 8, "gold", 4),

            new Item("Окуляри", "Аксесуари", 90, "silver", 1),
            new Item("Сумка", "Аксесуари", 160, "silver", 2),
            new Item("Сережки", "Аксесуари", 120, "silver", 1),
            new Item("Браслет", "Аксесуари", 100, "silver", 1),
            new Item("Ланцюжок", "Аксесуари", 5, "gold", 3),

            new Item("Кепка", "Головний убір", 100, "silver", 1),
            new Item("Шапка", "Головний убір", 90, "silver", 1),
            new Item("Капелюх", "Головний убір", 170, "silver", 2),
            new Item("Берет", "Головний убір", 150, "silver", 2),
            new Item("Корона", "Головний убір", 15, "gold", 5)
    };

    public ClothingShopFrame(GameState gameState) {
        this.gameState = gameState;

        setTitle("VibeTower — Магазин одягу");
        setSize(1050, 720);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(255, 242, 235));

        JPanel topPanel = createTopPanel();

        JPanel shopPanel = new JPanel(new GridLayout(0, 3, 18, 18));
        shopPanel.setBackground(new Color(255, 242, 235));
        shopPanel.setBorder(BorderFactory.createEmptyBorder(25, 40, 40, 40));

        for (Item item : clothingItems) {
            shopPanel.add(createItemCard(item));
        }

        JScrollPane scrollPane = new JScrollPane(shopPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        add(mainPanel);
    }

    private JPanel createTopPanel() {
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 15, 30));

        JLabel titleLabel = new JLabel("Магазин одягу", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 36));
        titleLabel.setForeground(new Color(72, 37, 120));

        JPanel moneyPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        moneyPanel.setOpaque(false);

        silverLabel = new JLabel();
        goldLabel = new JLabel();

        silverLabel.setFont(new Font("Arial", Font.BOLD, 17));
        goldLabel.setFont(new Font("Arial", Font.BOLD, 17));

        silverLabel.setForeground(new Color(72, 37, 120));
        goldLabel.setForeground(new Color(72, 37, 120));

        moneyPanel.add(silverLabel);
        moneyPanel.add(goldLabel);

        updateCurrencyLabels();

        topPanel.add(titleLabel, BorderLayout.CENTER);
        topPanel.add(moneyPanel, BorderLayout.EAST);

        return topPanel;
    }

    private JPanel createItemCard(Item item) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(120, 82, 160), 3),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)
        ));

        JLabel iconLabel = new JLabel(getIconForCategory(item.getCategory()), SwingConstants.CENTER);
        iconLabel.setFont(new Font("Arial", Font.PLAIN, 42));

        JLabel nameLabel = new JLabel(item.getName(), SwingConstants.CENTER);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 18));
        nameLabel.setForeground(new Color(72, 37, 120));

        JLabel infoLabel = new JLabel(
                item.getCategory() + " | " + item.getPriceText() + " | рівень " + item.getMinLevel(),
                SwingConstants.CENTER
        );
        infoLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        infoLabel.setForeground(new Color(90, 70, 110));

        JPanel textPanel = new JPanel(new GridLayout(2, 1, 4, 4));
        textPanel.setOpaque(false);
        textPanel.add(nameLabel);
        textPanel.add(infoLabel);

        JButton tryButton = createButton("Приміряти");
        JButton buyButton = createButton("Купити");
        JButton wearButton = createButton("Одягнути");

        tryButton.addActionListener(e -> tryItem(item));
        buyButton.addActionListener(e -> buyItem(item));
        wearButton.addActionListener(e -> wearItem(item));

        JPanel buttonsPanel = new JPanel(new GridLayout(1, 3, 6, 6));
        buttonsPanel.setOpaque(false);
        buttonsPanel.add(tryButton);
        buttonsPanel.add(buyButton);
        buttonsPanel.add(wearButton);

        card.add(textPanel, BorderLayout.NORTH);
        card.add(iconLabel, BorderLayout.CENTER);
        card.add(buttonsPanel, BorderLayout.SOUTH);

        return card;
    }

    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setBackground(new Color(255, 218, 130));
        button.setForeground(new Color(72, 37, 120));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private String getIconForCategory(String category) {
        if (category.equals("Верх")) {
            return "👕";
        }

        if (category.equals("Низ")) {
            return "👖";
        }

        if (category.equals("Сукня")) {
            return "👗";
        }

        if (category.equals("Взуття")) {
            return "👟";
        }

        if (category.equals("Аксесуари")) {
            return "👜";
        }

        if (category.equals("Головний убір")) {
            return "👒";
        }

        return "⭐";
    }

    private void tryItem(Item item) {
        JOptionPane.showMessageDialog(
                this,
                "Примірка: " + item.getName() + "\nКатегорія: " + item.getCategory(),
                "Примірка",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    private void buyItem(Item item) {
        if (gameState.getInventory().contains(item)) {
            JOptionPane.showMessageDialog(
                    this,
                    "Цей предмет уже куплено.",
                    "Магазин одягу",
                    JOptionPane.INFORMATION_MESSAGE
            );
            return;
        }

        if (gameState.getLevel() < item.getMinLevel()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Цей предмет відкривається з рівня " + item.getMinLevel() + ".",
                    "Недоступно",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        boolean bought = gameState.buyClothingItem(item);

        if (bought) {
            JOptionPane.showMessageDialog(
                    this,
                    "Покупка успішна!\nПредмет додано в інвентар.",
                    "Магазин одягу",
                    JOptionPane.INFORMATION_MESSAGE
            );
            updateCurrencyLabels();
        } else {
            JOptionPane.showMessageDialog(
                    this,
                    "Недостатньо валюти для покупки.",
                    "Помилка",
                    JOptionPane.WARNING_MESSAGE
            );
        }
    }

    private void wearItem(Item item) {
        if (!gameState.getInventory().contains(item)) {
            JOptionPane.showMessageDialog(
                    this,
                    "Спочатку потрібно купити цей предмет.",
                    "Помилка",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        gameState.equipItem(item);

        JOptionPane.showMessageDialog(
                this,
                item.getName() + " одягнуто!\nКатегорія: " + item.getCategory(),
                "Образ оновлено",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    private void updateCurrencyLabels() {
        silverLabel.setText("Срібло: " + gameState.getSilver());
        goldLabel.setText("Золото: " + gameState.getGold());
    }
}
