import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class ShopFrame extends JFrame {
    private GameState gameState;
    private JPanel itemsPanel;
    private JLabel infoLabel;
    private ArrayList<Item> shopItems;

    public ShopFrame(GameState gameState) {
        this.gameState = gameState;
        this.shopItems = createShopItems();

        setTitle("VibeTower — Магазин інтер'єру");
        setSize(850, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        add(createTopPanel(), BorderLayout.NORTH);

        itemsPanel = new JPanel();
        itemsPanel.setLayout(new GridLayout(0, 2, 15, 15));
        itemsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        itemsPanel.setBackground(new Color(238, 241, 245));

        JScrollPane scrollPane = new JScrollPane(itemsPanel);
        add(scrollPane, BorderLayout.CENTER);

        add(createBottomPanel(), BorderLayout.SOUTH);

        showItems("Усі");
    }

    private JPanel createTopPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(45, 62, 90));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        infoLabel = new JLabel(
                "Рівень: " + gameState.getLevel()
                        + " | Срібло: " + gameState.getSilver()
                        + " | Золото: " + gameState.getGold()
        );

        infoLabel.setForeground(Color.WHITE);
        infoLabel.setFont(new Font("Arial", Font.BOLD, 14));

        JLabel title = new JLabel("Магазин інтер'єру");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Arial", Font.BOLD, 22));

        panel.add(title, BorderLayout.WEST);
        panel.add(infoLabel, BorderLayout.EAST);

        return panel;
    }

    private JPanel createBottomPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(2, 4, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton allButton = new JButton("Усі");
        JButton wallButton = new JButton("Шпалери");
        JButton floorButton = new JButton("Підлога");
        JButton furnitureButton = new JButton("Меблі");
        JButton decorButton = new JButton("Декор");
        JButton techButton = new JButton("Техніка");
        JButton premiumButton = new JButton("Преміум");
        JButton backButton = new JButton("До меню");

        panel.add(allButton);
        panel.add(wallButton);
        panel.add(floorButton);
        panel.add(furnitureButton);
        panel.add(decorButton);
        panel.add(techButton);
        panel.add(premiumButton);
        panel.add(backButton);

        allButton.addActionListener(e -> showItems("Усі"));
        wallButton.addActionListener(e -> showItems("Шпалери"));
        floorButton.addActionListener(e -> showItems("Підлога"));
        furnitureButton.addActionListener(e -> showItems("Меблі"));
        decorButton.addActionListener(e -> showItems("Декор"));
        techButton.addActionListener(e -> showItems("Техніка"));
        premiumButton.addActionListener(e -> showItems("Преміум"));

        backButton.addActionListener(e -> {
            new HomeFrame(gameState).setVisible(true);
            dispose();
        });

        return panel;
    }

    private void showItems(String category) {
        itemsPanel.removeAll();

        for (Item item : shopItems) {
            if (category.equals("Усі") || item.getCategory().equals(category)) {
                itemsPanel.add(createItemCard(item));
            }
        }

        itemsPanel.revalidate();
        itemsPanel.repaint();
    }

    private JPanel createItemCard(Item item) {
        JPanel card = new JPanel();
        card.setLayout(new BorderLayout());
        card.setBorder(BorderFactory.createLineBorder(new Color(180, 180, 180), 1));
        card.setBackground(Color.WHITE);

        JLabel nameLabel = new JLabel(
                "<html><center>"
                        + "<span style='font-size:28px;'>" + item.getIcon() + "</span><br>"
                        + "<b>" + item.getName() + "</b><br>"
                        + "Категорія: " + item.getCategory() + "<br>"
                        + "Ціна: " + item.getPrice() + " " + translateCurrency(item.getCurrency()) + "<br>"
                        + "Доступно з рівня: " + item.getMinLevel()
                        + "</center></html>",
                SwingConstants.CENTER
        );

        JButton buyButton = new JButton("Купити");

        buyButton.addActionListener(e -> buyItem(item));

        card.add(nameLabel, BorderLayout.CENTER);
        card.add(buyButton, BorderLayout.SOUTH);

        return card;
    }

    private void buyItem(Item item) {
        if (gameState.getLevel() < item.getMinLevel()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Цей предмет доступний з " + item.getMinLevel() + " рівня."
            );
            return;
        }

        boolean success;

        if (item.getCurrency().equals("silver")) {
            success = gameState.spendSilver(item.getPrice());
        } else {
            success = gameState.spendGold(item.getPrice());
        }

        if (success) {
            gameState.addItemToInventory(item.getFullName());
            SaveManager.saveGame(gameState);

            JOptionPane.showMessageDialog(
                    this,
                    "Покупка успішна! Предмет додано в інвентар."
            );

            refreshWindow();
        } else {
            JOptionPane.showMessageDialog(
                    this,
                    "Недостатньо валюти для покупки."
            );
        }
    }

    private String translateCurrency(String currency) {
        if (currency.equals("silver")) {
            return "срібла";
        }
        return "золота";
    }

    private void refreshWindow() {
        new ShopFrame(gameState).setVisible(true);
        dispose();
    }

    private ArrayList<Item> createShopItems() {
        ArrayList<Item> items = new ArrayList<>();

        items.add(new Item("Бежеві шпалери", "Шпалери", 80, "silver", 1, "🧱"));
        items.add(new Item("Рожеві шпалери", "Шпалери", 120, "silver", 2, "🌸"));
        items.add(new Item("Преміальні шпалери", "Преміум", 8, "gold", 5, "✨"));

        items.add(new Item("Дерев'яна підлога", "Підлога", 100, "silver", 1, "🟫"));
        items.add(new Item("Світла підлога", "Підлога", 140, "silver", 3, "⬜"));
        items.add(new Item("Преміальна підлога", "Преміум", 10, "gold", 6, "💎"));

        items.add(new Item("Ліжко", "Меблі", 180, "silver", 2, "🛏️"));
        items.add(new Item("Шафа", "Меблі", 160, "silver", 2, "🚪"));
        items.add(new Item("Стіл", "Меблі", 100, "silver", 1, "🪑"));
        items.add(new Item("Диван", "Меблі", 250, "silver", 4, "🛋️"));
        items.add(new Item("Кухонний стіл", "Меблі", 200, "silver", 7, "🍽️"));

        items.add(new Item("Картина", "Декор", 70, "silver", 1, "🖼️"));
        items.add(new Item("Рослина", "Декор", 90, "silver", 2, "🪴"));
        items.add(new Item("Килим", "Декор", 130, "silver", 3, "🧶"));

        items.add(new Item("Телевізор", "Техніка", 300, "silver", 4, "📺"));
        items.add(new Item("Комп'ютер", "Техніка", 12, "gold", 6, "💻"));
        items.add(new Item("Холодильник", "Техніка", 350, "silver", 7, "🧊"));

        items.add(new Item("Преміальна статуетка", "Преміум", 15, "gold", 8, "🏆"));
        items.add(new Item("Рідкісний декор", "Преміум", 20, "gold", 9, "👑"));

        return items;
    }
}