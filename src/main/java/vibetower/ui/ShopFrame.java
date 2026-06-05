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
        setSize(950, 680);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setLayout(new BorderLayout());

        topPanel = new JPanel();
        topPanel.setOpaque(false);
        topPanel.setLayout(new BorderLayout());
        topPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 10, 30));

        JLabel titleLabel = new JLabel("Магазин інтер'єру", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 36));
        titleLabel.setForeground(new Color(72, 37, 120));

        topPanel.add(titleLabel, BorderLayout.CENTER);
        topPanel.add(new CurrencyPanel(gameState), BorderLayout.EAST);

        mainPanel.add(topPanel, BorderLayout.NORTH);

        JPanel shopPanel = new JPanel();
        shopPanel.setOpaque(false);
        shopPanel.setLayout(new GridLayout(2, 3, 20, 20));
        shopPanel.setBorder(BorderFactory.createEmptyBorder(30, 70, 70, 70));

        shopPanel.add(createShopCard("Диван", 250));
        shopPanel.add(createShopCard("Ліжко", 300));
        shopPanel.add(createShopCard("Стіл", 150));
        shopPanel.add(createShopCard("Килим", 120));
        shopPanel.add(createShopCard("Лампа", 90));
        shopPanel.add(createShopCard("Картина", 180));

        mainPanel.add(shopPanel, BorderLayout.CENTER);

        add(mainPanel, BorderLayout.CENTER);
    }

    public ShopFrame() {
        this(new GameState());
    }

    private JPanel createShopCard(String itemName, int price) {
        JPanel card = new JPanel();
        card.setBackground(new Color(250, 250, 250));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(120, 82, 160), 3),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        card.setLayout(new BorderLayout());

        JLabel itemIcon = new JLabel("▣", SwingConstants.CENTER);
        itemIcon.setFont(new Font("Arial", Font.BOLD, 48));
        itemIcon.setForeground(new Color(120, 82, 160));

        JLabel nameLabel = new JLabel(itemName, SwingConstants.CENTER);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 21));
        nameLabel.setForeground(new Color(72, 37, 120));

        JLabel priceLabel = new JLabel(price + " срібла", SwingConstants.CENTER);
        priceLabel.setFont(new Font("Arial", Font.BOLD, 17));
        priceLabel.setForeground(new Color(150, 90, 20));

        JButton buyButton = new JButton("Купити");
        buyButton.setFont(new Font("Arial", Font.BOLD, 17));
        buyButton.setBackground(new Color(255, 218, 130));
        buyButton.setForeground(new Color(72, 37, 120));
        buyButton.setFocusPainted(false);

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
                        "Недостатньо срібла для покупки!",
                        "Помилка",
                        JOptionPane.WARNING_MESSAGE
                );
            }
        });

        JPanel textPanel = new JPanel();
        textPanel.setOpaque(false);
        textPanel.setLayout(new GridLayout(2, 1));
        textPanel.add(nameLabel);
        textPanel.add(priceLabel);

        card.add(textPanel, BorderLayout.NORTH);
        card.add(itemIcon, BorderLayout.CENTER);
        card.add(buyButton, BorderLayout.SOUTH);

        return card;
    }

    private void refreshCurrency() {
        topPanel.remove(1);
        topPanel.add(new CurrencyPanel(gameState), BorderLayout.EAST);
        topPanel.revalidate();
        topPanel.repaint();
    }
}