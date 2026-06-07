package vibetower.model;

import vibetower.ui.ApartmentFrame;
import vibetower.ui.BeautySalonFrame;
import vibetower.ui.ClothingShopFrame;
import vibetower.ui.ImageButton;
import vibetower.ui.InventoryFrame;
import vibetower.ui.PassportFrame;
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
        setSize(1150, 820);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel backgroundPanel = new JPanel();
        backgroundPanel.setBackground(Color.WHITE);
        backgroundPanel.setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("Меню квартири", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 42));
        titleLabel.setForeground(new Color(72, 37, 120));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(30, 0, 10, 0));
        backgroundPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel();
        centerPanel.setOpaque(false);
        centerPanel.setLayout(new GridLayout(4, 3, 28, 22));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(35, 70, 45, 70));

        ImageButton apartmentButton = new ImageButton("/квартира.png");
        ImageButton tasksButton = new ImageButton("/завдання.png");
        ImageButton inventoryButton = new ImageButton("/інвентар.png");

        ImageButton mapButton = new ImageButton("/карта.png");
        ImageButton repairButton = new ImageButton("/режим ремонту.png");
        ImageButton shopButton = new ImageButton("/магазин інтер.png");

        ImageButton saveButton = new ImageButton("/зберегти прогрес.png");

        JButton passportButton = createMenuButton("Паспорт");
        JButton beautySalonButton = createMenuButton("Салон краси");
        JButton clothingShopButton = createMenuButton("Магазин одягу");

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

        mapButton.addActionListener(e -> {
            MapFrame mapFrame = new MapFrame(gameState);
            mapFrame.setVisible(true);
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

        passportButton.addActionListener(e -> {
            PassportFrame passportFrame = new PassportFrame(gameState);
            passportFrame.setVisible(true);
        });

        beautySalonButton.addActionListener(e -> {
            BeautySalonFrame beautySalonFrame = new BeautySalonFrame(gameState);
            beautySalonFrame.setVisible(true);
        });

        clothingShopButton.addActionListener(e -> {
            ClothingShopFrame clothingShopFrame = new ClothingShopFrame(gameState);
            clothingShopFrame.setVisible(true);
        });

        centerPanel.add(createButtonCell(apartmentButton));
        centerPanel.add(createButtonCell(tasksButton));
        centerPanel.add(createButtonCell(inventoryButton));

        centerPanel.add(createButtonCell(mapButton));
        centerPanel.add(createButtonCell(repairButton));
        centerPanel.add(createButtonCell(shopButton));

        centerPanel.add(createButtonCell(passportButton));
        centerPanel.add(createButtonCell(beautySalonButton));
        centerPanel.add(createButtonCell(clothingShopButton));

        centerPanel.add(createEmptyCell());
        centerPanel.add(createButtonCell(saveButton));
        centerPanel.add(createEmptyCell());

        backgroundPanel.add(centerPanel, BorderLayout.CENTER);
        add(backgroundPanel, BorderLayout.CENTER);
    }

    public HomeFrame() {
        this(new GameState());
    }

    private JPanel createButtonCell(JButton button) {
        JPanel cell = new JPanel();
        cell.setOpaque(false);
        cell.setLayout(new GridBagLayout());
        cell.add(button);
        return cell;
    }

    private JPanel createEmptyCell() {
        JPanel cell = new JPanel();
        cell.setOpaque(false);
        return cell;
    }

    private JButton createMenuButton(String text) {
        JButton button = new JButton(text);

        button.setPreferredSize(new Dimension(300, 75));
        button.setMinimumSize(new Dimension(300, 75));
        button.setMaximumSize(new Dimension(300, 75));

        button.setFont(new Font("Arial", Font.BOLD, 22));
        button.setBackground(new Color(255, 218, 130));
        button.setForeground(new Color(72, 37, 120));

        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createLineBorder(new Color(72, 37, 120), 3));

        return button;
    }
}