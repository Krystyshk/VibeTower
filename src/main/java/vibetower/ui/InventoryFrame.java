package vibetower.ui;

import vibetower.model.GameState;
import vibetower.model.Item;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class InventoryFrame extends JFrame {

    private final GameState gameState;
    private JPanel itemsPanel;

    public InventoryFrame(GameState gameState) {
        this.gameState = gameState;
        this.gameState.fixAfterLoad();

        setTitle("VibeTower — Інвентар");
        setSize(900, 640);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(252, 248, 240));

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.setBorder(BorderFactory.createEmptyBorder(18, 25, 10, 25));

        JLabel titleLabel = new JLabel("Інвентар", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 36));
        titleLabel.setForeground(new Color(72, 37, 120));

        topPanel.add(titleLabel, BorderLayout.CENTER);
        topPanel.add(new CurrencyPanel(gameState), BorderLayout.EAST);
        mainPanel.add(topPanel, BorderLayout.NORTH);

        JPanel tabs = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        tabs.setOpaque(false);
        String[] categories = {"Усе", "Меблі", "Санвузол", "Спальня", "Кухня", "Техніка", "Декор", "Преміум", "Їжа"};
        for (String category : categories) {
            JButton button = createSmallButton(category);
            button.addActionListener(e -> showItems(category));
            tabs.add(button);
        }
        mainPanel.add(tabs, BorderLayout.BEFORE_FIRST_LINE);

        itemsPanel = new JPanel(new GridLayout(0, 3, 18, 18));
        itemsPanel.setBackground(new Color(252, 248, 240));
        itemsPanel.setBorder(BorderFactory.createEmptyBorder(25, 55, 35, 55));

        JScrollPane scrollPane = new JScrollPane(itemsPanel);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(new Color(252, 248, 240));
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        add(mainPanel, BorderLayout.CENTER);
        showItems("Усе");
    }

    public InventoryFrame() {
        this(new GameState());
    }

    private void showItems(String category) {
        itemsPanel.removeAll();

        ArrayList<Item> items = gameState.getInventory();
        int shown = 0;

        for (Item item : items) {
            if ("Усе".equals(category) || item.getCategory().equals(category)) {
                itemsPanel.add(createItemCard(item));
                shown++;
            }
        }

        if (shown == 0) {
            JLabel emptyLabel = new JLabel("У цій категорії поки немає предметів.", SwingConstants.CENTER);
            emptyLabel.setFont(new Font("Arial", Font.BOLD, 24));
            emptyLabel.setForeground(new Color(120, 82, 160));
            itemsPanel.add(emptyLabel);
        }

        itemsPanel.revalidate();
        itemsPanel.repaint();
    }

    private JPanel createItemCard(Item item) {
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

        JLabel infoLabel = new JLabel("<html><center>"
                + item.getCategory()
                + "<br>Ціна: " + item.getPrice() + (item.isGoldItem() ? " золота" : " срібла")
                + "<br>Рівень: " + item.getRequiredLevel()
                + "</center></html>", SwingConstants.CENTER);
        infoLabel.setFont(new Font("Arial", Font.BOLD, 14));
        infoLabel.setForeground(new Color(120, 82, 160));

        JButton placeButton = createSmallButton("Поставити");
        placeButton.addActionListener(e -> {
            if ("Їжа".equals(item.getCategory())) {
                gameState.addEnergy(20);
                JOptionPane.showMessageDialog(this, "Енергію відновлено на 20.");
            } else {
                gameState.placeItem(item);
                JOptionPane.showMessageDialog(this, item.getName() + " додано у кімнату. Відкрий режим ремонту, щоб перемістити предмет.");
            }
        });

        JPanel top = new JPanel(new GridLayout(2, 1));
        top.setOpaque(false);
        top.add(nameLabel);
        top.add(infoLabel);

        card.add(top, BorderLayout.NORTH);
        card.add(iconLabel, BorderLayout.CENTER);
        card.add(placeButton, BorderLayout.SOUTH);

        return card;
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
}
