package vibetower.model;

import vibetower.ui.ApartmentFrame;
import vibetower.ui.ImageButton;
import vibetower.ui.InventoryFrame;
import vibetower.ui.RepairFrame;
import vibetower.ui.ShopFrame;
import vibetower.ui.TasksFrame;

import javax.swing.*;
import java.awt.*;

public class HomeFrame extends JFrame {

    private GameState gameState;

    public HomeFrame(GameState gameState) {
        this.gameState = gameState;

        setTitle("VibeTower — Головне меню");
        setSize(1100, 750);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel backgroundPanel = new JPanel();
        backgroundPanel.setBackground(Color.WHITE);
        backgroundPanel.setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("Меню квартири", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 42));
        titleLabel.setForeground(new Color(72, 37, 120));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(45, 0, 20, 0));
        backgroundPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel();
        centerPanel.setOpaque(false);
        centerPanel.setLayout(new GridLayout(2, 3, 45, 45));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(110, 80, 130, 80));

        ImageButton apartmentButton = new ImageButton("/квартира.png");
        ImageButton tasksButton = new ImageButton("/завдання.png");
        ImageButton inventoryButton = new ImageButton("/інвентар.png");
        ImageButton repairButton = new ImageButton("/режим ремонту.png");
        ImageButton shopButton = new ImageButton("/магазин інтер.png");
        ImageButton saveButton = new ImageButton("/зберегти прогрес.png");

        apartmentButton.addActionListener(e -> {
            ApartmentFrame apartmentFrame = new ApartmentFrame(gameState);
            apartmentFrame.setVisible(true);
        });

        tasksButton.addActionListener(e -> {
            TasksFrame tasksFrame = new TasksFrame(gameState);
            tasksFrame.setVisible(true);
        });

        inventoryButton.addActionListener(e -> {
            InventoryFrame inventoryFrame = new InventoryFrame(gameState);
            inventoryFrame.setVisible(true);
        });

        repairButton.addActionListener(e -> {
            RepairFrame repairFrame = new RepairFrame(gameState);
            repairFrame.setVisible(true);
        });

        shopButton.addActionListener(e -> {
            ShopFrame shopFrame = new ShopFrame(gameState);
            shopFrame.setVisible(true);
        });

        saveButton.addActionListener(e -> {
            SaveManager.saveGame(gameState);

            JOptionPane.showMessageDialog(
                    this,
                    "Прогрес збережено!",
                    "Збереження",
                    JOptionPane.INFORMATION_MESSAGE
            );
        });

        centerPanel.add(createButtonCell(apartmentButton));
        centerPanel.add(createButtonCell(tasksButton));
        centerPanel.add(createButtonCell(inventoryButton));

        centerPanel.add(createButtonCell(repairButton));
        centerPanel.add(createButtonCell(shopButton));
        centerPanel.add(createButtonCell(saveButton));

        backgroundPanel.add(centerPanel, BorderLayout.CENTER);
        add(backgroundPanel, BorderLayout.CENTER);
    }

    public HomeFrame() {
        this(new GameState());
    }

    private JPanel createButtonCell(ImageButton button) {
        JPanel cell = new JPanel();
        cell.setOpaque(false);
        cell.setLayout(new GridBagLayout());
        cell.add(button);
        return cell;
    }
}