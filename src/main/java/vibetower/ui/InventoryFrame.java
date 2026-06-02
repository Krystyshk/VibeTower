package vibetower.ui;

import vibetower.model.GameState;
import vibetower.model.SaveManager;
import javax.swing.*;
import java.awt.*;

public class InventoryFrame extends JFrame {
    private GameState gameState;

    public InventoryFrame(GameState gameState) {
        this.gameState = gameState;

        setTitle("VibeTower — Інвентар");
        setSize(750, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        add(createTopPanel(), BorderLayout.NORTH);
        add(createInventoryPanel(), BorderLayout.CENTER);
        add(createBottomPanel(), BorderLayout.SOUTH);
    }

    private JPanel createTopPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(new Color(45, 62, 90));
        panel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JLabel title = new JLabel("Інвентар куплених предметів");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Arial", Font.BOLD, 22));

        panel.add(title);
        return panel;
    }

    private JScrollPane createInventoryPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(0, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        panel.setBackground(new Color(238, 241, 245));

        if (gameState.getInventory().isEmpty()) {
            JLabel emptyLabel = new JLabel("Інвентар порожній. Купіть предмети в магазині.", SwingConstants.CENTER);
            emptyLabel.setFont(new Font("Arial", Font.BOLD, 18));
            panel.add(emptyLabel);
        } else {
            for (String item : gameState.getInventory()) {
                JPanel itemCard = new JPanel(new BorderLayout());
                itemCard.setBackground(Color.WHITE);
                itemCard.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(180, 180, 180)),
                        BorderFactory.createEmptyBorder(10, 10, 10, 10)
                ));

                JLabel itemLabel = new JLabel(item);
                itemLabel.setFont(new Font("Arial", Font.PLAIN, 17));

                JButton placeButton = new JButton("Поставити в квартиру");

                placeButton.addActionListener(e -> {
                    gameState.placeFurniture(item);
                    SaveManager.saveGame(gameState);

                    JOptionPane.showMessageDialog(
                            this,
                            "Предмет додано в квартиру: " + item
                    );

                    new ApartmentFrame(gameState).setVisible(true);
                    dispose();
                });

                itemCard.add(itemLabel, BorderLayout.CENTER);
                itemCard.add(placeButton, BorderLayout.EAST);

                panel.add(itemCard);
            }
        }

        return new JScrollPane(panel);
    }

    private JPanel createBottomPanel() {
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton shopButton = new JButton("До магазину");
        JButton repairButton = new JButton("Режим ремонту");
        JButton backButton = new JButton("До меню");

        panel.add(shopButton);
        panel.add(repairButton);
        panel.add(backButton);

        shopButton.addActionListener(e -> {
            new ShopFrame(gameState).setVisible(true);
            dispose();
        });

        repairButton.addActionListener(e -> {
            new RepairFrame(gameState).setVisible(true);
            dispose();
        });

        backButton.addActionListener(e -> {
            new HomeFrame(gameState).setVisible(true);
            dispose();
        });

        return panel;
    }
}