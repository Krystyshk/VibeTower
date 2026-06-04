package vibetower.ui;

import vibetower.model.GameState;
import vibetower.model.Item;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class InventoryFrame extends JFrame {

    private GameState gameState;

    public InventoryFrame(GameState gameState) {
        this.gameState = gameState;

        setTitle("VibeTower — Інвентар");
        setSize(850, 620);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setLayout(new BorderLayout());

        JPanel topPanel = new JPanel();
        topPanel.setOpaque(false);
        topPanel.setLayout(new BorderLayout());
        topPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 10, 30));

        JLabel titleLabel = new JLabel("Інвентар", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 38));
        titleLabel.setForeground(new Color(72, 37, 120));

        topPanel.add(titleLabel, BorderLayout.CENTER);
        topPanel.add(new CurrencyPanel(gameState), BorderLayout.EAST);

        mainPanel.add(topPanel, BorderLayout.NORTH);

        ArrayList<Item> items = gameState.getInventory();

        if (items.isEmpty()) {
            JLabel emptyLabel = new JLabel("Інвентар поки порожній. Купи предмет у магазині.", SwingConstants.CENTER);
            emptyLabel.setFont(new Font("Arial", Font.BOLD, 24));
            emptyLabel.setForeground(new Color(120, 82, 160));
            mainPanel.add(emptyLabel, BorderLayout.CENTER);
        } else {
            JPanel itemsPanel = new JPanel();
            itemsPanel.setOpaque(false);
            itemsPanel.setLayout(new GridLayout(2, 3, 20, 20));
            itemsPanel.setBorder(BorderFactory.createEmptyBorder(40, 80, 80, 80));

            for (Item item : items) {
                itemsPanel.add(createItemCard(item));
            }

            mainPanel.add(itemsPanel, BorderLayout.CENTER);
        }

        add(mainPanel, BorderLayout.CENTER);
    }

    public InventoryFrame() {
        this(new GameState());
    }

    private JPanel createItemCard(Item item) {
        JPanel card = new JPanel();
        card.setBackground(new Color(250, 250, 250));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(120, 82, 160), 3),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        card.setLayout(new BorderLayout());

        JLabel iconLabel = new JLabel("▣", SwingConstants.CENTER);
        iconLabel.setFont(new Font("Arial", Font.BOLD, 48));
        iconLabel.setForeground(new Color(120, 82, 160));

        JLabel nameLabel = new JLabel(item.getName(), SwingConstants.CENTER);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 21));
        nameLabel.setForeground(new Color(72, 37, 120));

        JLabel priceLabel = new JLabel("Ціна: " + item.getPrice() + " срібла", SwingConstants.CENTER);
        priceLabel.setFont(new Font("Arial", Font.BOLD, 16));
        priceLabel.setForeground(new Color(150, 90, 20));

        JPanel textPanel = new JPanel();
        textPanel.setOpaque(false);
        textPanel.setLayout(new GridLayout(2, 1));
        textPanel.add(nameLabel);
        textPanel.add(priceLabel);

        card.add(textPanel, BorderLayout.NORTH);
        card.add(iconLabel, BorderLayout.CENTER);

        return card;
    }
}