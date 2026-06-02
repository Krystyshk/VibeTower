import javax.swing.*;
import java.awt.*;

public class HomeFrame extends JFrame {
    private GameState gameState;
    private JLabel infoLabel;

    public HomeFrame(GameState gameState) {
        this.gameState = gameState;

        setTitle("VibeTower — Головне меню");
        setSize(700, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel();
        topPanel.setBackground(new Color(45, 62, 90));
        topPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel titleLabel = new JLabel("VibeTower");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));

        topPanel.add(titleLabel);
        add(topPanel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new GridLayout(7, 1, 10, 10));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(30, 150, 30, 150));
        centerPanel.setBackground(new Color(238, 241, 245));

        infoLabel = new JLabel("", SwingConstants.CENTER);
        infoLabel.setFont(new Font("Arial", Font.BOLD, 15));
        updateInfoLabel();

        JButton apartmentButton = new JButton("Квартира");
        JButton shopButton = new JButton("Магазин інтер'єру");
        JButton inventoryButton = new JButton("Інвентар");
        JButton repairButton = new JButton("Режим ремонту");
        JButton tasksButton = new JButton("Завдання");
        JButton saveButton = new JButton("Зберегти прогрес");

        centerPanel.add(infoLabel);
        centerPanel.add(apartmentButton);
        centerPanel.add(shopButton);
        centerPanel.add(inventoryButton);
        centerPanel.add(repairButton);
        centerPanel.add(tasksButton);
        centerPanel.add(saveButton);

        add(centerPanel, BorderLayout.CENTER);

        apartmentButton.addActionListener(e -> {
            ApartmentFrame apartmentFrame = new ApartmentFrame(gameState);
            apartmentFrame.setVisible(true);
            dispose();
        });

        shopButton.addActionListener(e -> {
            ShopFrame shopFrame = new ShopFrame(gameState);
            shopFrame.setVisible(true);
            dispose();
        });

        inventoryButton.addActionListener(e -> {
            InventoryFrame inventoryFrame = new InventoryFrame(gameState);
            inventoryFrame.setVisible(true);
            dispose();
        });

        repairButton.addActionListener(e -> {
            RepairFrame repairFrame = new RepairFrame(gameState);
            repairFrame.setVisible(true);
            dispose();
        });

        tasksButton.addActionListener(e -> {
            TasksFrame tasksFrame = new TasksFrame(gameState);
            tasksFrame.setVisible(true);
            dispose();
        });

        saveButton.addActionListener(e -> {
            SaveManager.saveGame(gameState);
            JOptionPane.showMessageDialog(this, "Прогрес гри збережено!");
            updateInfoLabel();
        });
    }

    private void updateInfoLabel() {
        infoLabel.setText(
                "Рівень: " + gameState.getLevel()
                        + " | XP: " + gameState.getXp()
                        + " | Срібло: " + gameState.getSilver()
                        + " | Золото: " + gameState.getGold()
                        + " | Енергія: " + gameState.getEnergy()
        );
    }
}