package vibetower.ui;

import vibetower.model.GameState;

import javax.swing.*;
import java.awt.*;

public class RepairFrame extends JFrame {

    private GameState gameState;

    public RepairFrame(GameState gameState) {
        this.gameState = gameState;

        setTitle("VibeTower — Режим ремонту");
        setSize(900, 600);
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

        JLabel titleLabel = new JLabel("Режим ремонту", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 38));
        titleLabel.setForeground(new Color(72, 37, 120));

        topPanel.add(titleLabel, BorderLayout.CENTER);
        topPanel.add(new CurrencyPanel(gameState), BorderLayout.EAST);
        mainPanel.add(topPanel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel();
        centerPanel.setBackground(new Color(250, 250, 250));
        centerPanel.setBorder(BorderFactory.createLineBorder(new Color(72, 37, 120), 4));
        centerPanel.setLayout(null);

        JLabel roomLabel = new JLabel("Редагування кімнати", SwingConstants.CENTER);
        roomLabel.setFont(new Font("Arial", Font.BOLD, 26));
        roomLabel.setForeground(new Color(100, 70, 140));
        roomLabel.setBounds(250, 80, 400, 50);
        centerPanel.add(roomLabel);

        JButton wallpaperButton = createMenuButton("Змінити шпалери");
        wallpaperButton.setBounds(310, 170, 280, 45);
        centerPanel.add(wallpaperButton);

        JButton floorButton = createMenuButton("Змінити підлогу");
        floorButton.setBounds(310, 230, 280, 45);
        centerPanel.add(floorButton);

        JButton saveButton = createMenuButton("Зберегти ремонт");
        saveButton.setBounds(310, 290, 280, 45);
        centerPanel.add(saveButton);

        JButton cancelButton = createMenuButton("Скасувати");
        cancelButton.setBounds(310, 350, 280, 45);
        centerPanel.add(cancelButton);

        wallpaperButton.addActionListener(e -> JOptionPane.showMessageDialog(this, "Шпалери змінено!"));
        floorButton.addActionListener(e -> JOptionPane.showMessageDialog(this, "Підлогу змінено!"));
        saveButton.addActionListener(e -> JOptionPane.showMessageDialog(this, "Ремонт збережено!"));
        cancelButton.addActionListener(e -> dispose());

        mainPanel.add(centerPanel, BorderLayout.CENTER);
        add(mainPanel, BorderLayout.CENTER);
    }

    public RepairFrame() {
        this(new GameState());
    }

    private JButton createMenuButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 18));
        button.setBackground(new Color(255, 218, 130));
        button.setForeground(new Color(72, 37, 120));
        button.setFocusPainted(false);
        return button;
    }
}
