package vibetower.ui;

import vibetower.model.GameState;
import vibetower.model.Item;

import javax.swing.*;
import java.awt.*;

public class ShopFrame extends JFrame {

    private GameState gameState;
    private JPanel topPanel;

    public ShopFrame(GameState gameState) {
        this.gameState = gameState;

        setTitle("VibeTower — Магазин інтер'єру");
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(GameUiStyle.BACKGROUND);

        topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 10, 30));

        JLabel titleLabel = GameUiStyle.title("Магазин інтер'єру");

        topPanel.add(titleLabel, BorderLayout.CENTER);
        topPanel.add(new CurrencyPanel(gameState), BorderLayout.EAST);

        mainPanel.add(topPanel, BorderLayout.NORTH);

        JPanel shopPanel = new JPanel(new GridLayout(2, 3, 25, 25));
        shopPanel.setOpaque(false);
        shopPanel.setBorder(BorderFactory.createEmptyBorder(35, 70, 60, 70));

        shopPanel.add(createShopCard("Диван", 250, "🛋"));
        shopPanel.add(createShopCard("Ліжко", 300, "🛏"));
        shopPanel.add(createShopCard("Стіл", 150, "▤"));
        shopPanel.add(createShopCard("Килим", 120, "▭"));
        shopPanel.add(createShopCard("Лампа", 90, "💡"));
        shopPanel.add(createShopCard("Картина", 180, "▧"));

        mainPanel.add(shopPanel, BorderLayout.CENTER);

        add(mainPanel, BorderLayout.CENTER);
    }

    public ShopFrame() {
        this(new GameState());
    }

    private JPanel createShopCard(String itemName, int price, String icon) {
        JPanel card = GameUiStyle.createCard();
        card.setLayout(new BorderLayout());

        JLabel nameLabel = GameUiStyle.cardTitle(itemName);

        JLabel iconLabel = new JLabel(icon, SwingConstants.CENTER);
        iconLabel.setFont(new Font("Arial", Font.BOLD, 48));
        iconLabel.setForeground(GameUiStyle.PURPLE);

        JLabel priceLabel = GameUiStyle.cardText(price + " срібла");

        ImageButton buyButton = new ImageButton("/купити.png", 170, 55);

        buyButton.addActionListener(e -> {
            Item item = new Item(itemName, price);
            boolean bought = gameState.buyItem(item);

            if (bought) {
                JOptionPane.showMessageDialog(
                        this,
                        itemName + " додано в інвентар!",
                        "Покупка",
                        JOptionPane.INFORMATION_MESSAGE
                );

                refreshCurrency();
            } else {
                JOptionPane.showMessageDialog(
                        this,
                        "Недостатньо срібла!",
                        "Помилка",
                        JOptionPane.WARNING_MESSAGE
                );
            }
        });

        JPanel topTextPanel = new JPanel(new GridLayout(2, 1));
        topTextPanel.setOpaque(false);
        topTextPanel.add(nameLabel);
        topTextPanel.add(priceLabel);

        JPanel buttonPanel = new JPanel(new GridBagLayout());
        buttonPanel.setOpaque(false);
        buttonPanel.add(buyButton);

        card.add(topTextPanel, BorderLayout.NORTH);
        card.add(iconLabel, BorderLayout.CENTER);
        card.add(buttonPanel, BorderLayout.SOUTH);

        return card;
    }

    private void refreshCurrency() {
        topPanel.remove(1);
        topPanel.add(new CurrencyPanel(gameState), BorderLayout.EAST);
        topPanel.revalidate();
        topPanel.repaint();
    }
}