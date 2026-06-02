package vibetower.ui;

import vibetower.model.GameState;
import vibetower.model.SaveManager;

import javax.swing.*;
import java.awt.*;

public class ApartmentFrame extends JFrame {
    private GameState gameState;
    private JLabel apartmentLabel;

    public ApartmentFrame(GameState gameState) {
        this.gameState = gameState;

        setTitle("VibeTower — Квартира");
        setSize(800, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel topPanel = createTopPanel();
        add(topPanel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BorderLayout());
        centerPanel.setBackground(new Color(245, 235, 220));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        apartmentLabel = new JLabel("", SwingConstants.CENTER);
        apartmentLabel.setFont(new Font("Arial", Font.BOLD, 24));

        updateApartmentText();

        centerPanel.add(apartmentLabel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(2, 3, 10, 10));

        JButton chooseApartmentButton = new JButton("Обрати квартиру");
        JButton restButton = new JButton("Відпочити");
        JButton shopButton = new JButton("Магазин");
        JButton repairButton = new JButton("Ремонт");
        JButton inventoryButton = new JButton("Інвентар");
        JButton backButton = new JButton("До меню");

        buttonPanel.add(chooseApartmentButton);
        buttonPanel.add(restButton);
        buttonPanel.add(shopButton);
        buttonPanel.add(repairButton);
        buttonPanel.add(inventoryButton);
        buttonPanel.add(backButton);

        add(centerPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        chooseApartmentButton.addActionListener(e -> chooseApartment());

        restButton.addActionListener(e -> {
            gameState.restoreEnergy();
            JOptionPane.showMessageDialog(this, "Персонаж відпочив. Енергія відновлена!");
            refreshWindow();
        });

        shopButton.addActionListener(e -> {
            new ShopFrame(gameState).setVisible(true);
            dispose();
        });

        repairButton.addActionListener(e -> {
            new RepairFrame(gameState).setVisible(true);
            dispose();
        });

        inventoryButton.addActionListener(e -> {
            new InventoryFrame(gameState).setVisible(true);
            dispose();
        });

        backButton.addActionListener(e -> {
            new HomeFrame(gameState).setVisible(true);
            dispose();
        });
    }

    private JPanel createTopPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(new Color(45, 62, 90));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel label = new JLabel(
                "Рівень: " + gameState.getLevel()
                        + " | XP: " + gameState.getXp()
                        + " | Срібло: " + gameState.getSilver()
                        + " | Золото: " + gameState.getGold()
                        + " | Енергія: " + gameState.getEnergy()
        );

        label.setForeground(Color.WHITE);
        label.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(label);

        return panel;
    }

    private void chooseApartment() {
        String[] apartments = {
                "Світла квартира",
                "Затишна бежева квартира",
                "Сучасна темна квартира"
        };

        String selected = (String) JOptionPane.showInputDialog(
                this,
                "Оберіть стартову квартиру:",
                "Вибір квартири",
                JOptionPane.QUESTION_MESSAGE,
                null,
                apartments,
                apartments[0]
        );

        if (selected != null) {
            gameState.setApartment(selected);
            SaveManager.saveGame(gameState);
            JOptionPane.showMessageDialog(this, "Ви обрали: " + selected);
            refreshWindow();
        }
    }

    private void updateApartmentText() {
        if (gameState.getApartment() == null) {
            apartmentLabel.setText("<html><center>У вас ще немає квартири.<br>Оберіть стартову квартиру.</center></html>");
        } else {
            StringBuilder text = new StringBuilder();
            text.append("<html><center>");
            text.append("Ваша квартира: ").append(gameState.getApartment()).append("<br><br>");
            text.append("Розміщені меблі:<br>");

            if (gameState.getPlacedFurniture().isEmpty()) {
                text.append("Поки що меблів немає.");
            } else {
                for (String item : gameState.getPlacedFurniture()) {
                    text.append("• ").append(item).append("<br>");
                }
            }

            text.append("</center></html>");
            apartmentLabel.setText(text.toString());
        }
    }

    private void refreshWindow() {
        new ApartmentFrame(gameState).setVisible(true);
        dispose();
    }
}