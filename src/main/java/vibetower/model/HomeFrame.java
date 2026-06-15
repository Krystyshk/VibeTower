package vibetower.model;

import vibetower.ui.AnimatedCharacterPanel;
import vibetower.ui.ApartmentFrame;
import vibetower.ui.AppearanceInventoryFrame;
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
    private AnimatedCharacterPanel charPanel;

    public HomeFrame(GameState gameState) {
        this.gameState = gameState;

        setTitle("VibeTower — Головне меню");
        setSize(1150, 820);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel backgroundPanel = new JPanel(new BorderLayout());
        backgroundPanel.setBackground(Color.WHITE);

        // ── заголовок ──────────────────────────────────────────────────────────
        JLabel titleLabel = new JLabel("Меню гри", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 42));
        titleLabel.setForeground(new Color(72, 37, 120));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(30, 0, 10, 0));
        backgroundPanel.add(titleLabel, BorderLayout.NORTH);

        // ── персонаж зліва ─────────────────────────────────────────────────────
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setOpaque(false);
        leftPanel.setPreferredSize(new Dimension(320, 0));
        leftPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 20, 5));

        charPanel = new AnimatedCharacterPanel(
                mapGender(gameState.getGender()),
                mapHair(gameState.getHairStyle()),
                mapEyes(gameState.getEyeColor())
        );
        charPanel.setWalking(true);

        String name = gameState.getCharacterName() != null ? gameState.getCharacterName() : "Персонаж";
        JLabel charName = new JLabel(name, SwingConstants.CENTER);
        charName.setFont(new Font("Arial", Font.BOLD, 16));
        charName.setForeground(new Color(72, 37, 120));
        charName.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));

        leftPanel.add(charPanel, BorderLayout.CENTER);
        leftPanel.add(charName, BorderLayout.SOUTH);
        backgroundPanel.add(leftPanel, BorderLayout.WEST);

        // ── кнопки по центру ───────────────────────────────────────────────────
        JPanel centerPanel = new JPanel(new GridLayout(4, 3, 28, 22));
        centerPanel.setOpaque(false);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(35, 20, 45, 55));

        ImageButton apartmentButton = new ImageButton("/квартира.png");
        ImageButton tasksButton     = new ImageButton("/завдання.png");
        ImageButton inventoryButton = new ImageButton("/інвентар.png");
        ImageButton mapButton       = new ImageButton("/карта.png");
        ImageButton repairButton    = new ImageButton("/режим ремонту.png");
        ImageButton shopButton      = new ImageButton("/магазин інтер.png");
        ImageButton saveButton      = new ImageButton("/зберегти прогрес.png");

        JButton passportButton     = createMenuButton("Паспорт");
        JButton beautySalonButton  = createMenuButton("Салон краси");
        JButton clothingShopButton = createMenuButton("Магазин одягу");
        JButton avatarButton       = createMenuButton("Гардероб");

        apartmentButton.addActionListener(e -> new ApartmentFrame(gameState).setVisible(true));
        tasksButton.addActionListener(e -> new TasksFrame(gameState).setVisible(true));
        inventoryButton.addActionListener(e -> new InventoryFrame(gameState).setVisible(true));
        mapButton.addActionListener(e -> new MapFrame(gameState).setVisible(true));
        repairButton.addActionListener(e -> new RepairFrame(gameState).setVisible(true));
        shopButton.addActionListener(e -> new ShopFrame(gameState).setVisible(true));
        passportButton.addActionListener(e -> new PassportFrame(gameState).setVisible(true));
        beautySalonButton.addActionListener(e -> new BeautySalonFrame(gameState).setVisible(true));
        clothingShopButton.addActionListener(e -> new ClothingShopFrame(gameState).setVisible(true));
        avatarButton.addActionListener(e -> new AppearanceInventoryFrame(gameState).setVisible(true));
        saveButton.addActionListener(e -> {
            SaveManager.saveGame(gameState);
            JOptionPane.showMessageDialog(this, "Прогрес збережено!", "Збереження", JOptionPane.INFORMATION_MESSAGE);
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
        centerPanel.add(createButtonCell(avatarButton));
        centerPanel.add(createButtonCell(saveButton));
        centerPanel.add(createEmptyCell());

        backgroundPanel.add(centerPanel, BorderLayout.CENTER);
        add(backgroundPanel, BorderLayout.CENTER);

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                charPanel.stopAnimation();
            }
        });
    }

    public HomeFrame() {
        this(new GameState());
    }

    // ── маппінг назв на імена файлів ──────────────────────────────────────────

    private String mapGender(String gender) {
        if (gender == null) return "female";
        return gender.toLowerCase().contains("чол") ? "male" : "female";
    }

    private String mapHair(String hairStyle) {
        if (hairStyle == null) return "hair_wavy";
        switch (hairStyle) {
            case "Довге":
            case "Довге пряме": return "hair_wavy";
            case "Кучеряве":    return "hair_curly";
            case "Хвіст":       return "hair_ponytail";
            case "Пучок":       return "hair_bun";
            case "Каре":        return "hair_bob";
            case "Коса":        return "hair_braids";
            default:            return "hair_wavy";
        }
    }

    private String mapEyes(String eyeColor) {
        if (eyeColor == null) return "eyes_brown";
        switch (eyeColor) {
            case "Карі":     return "eyes_brown";
            case "Сині":     return "eyes_blue";
            case "Зелені":   return "eyes_green";
            case "Сірі":     return "eyes_gray";
            case "Горіхові": return "eyes_hazel";
            default:         return "eyes_brown";
        }
    }

    // ── helpers ───────────────────────────────────────────────────────────────

    private JPanel createButtonCell(JButton button) {
        JPanel cell = new JPanel(new GridBagLayout());
        cell.setOpaque(false);
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