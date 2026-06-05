package vibetower.ui;

import vibetower.model.GameState;

import javax.swing.*;
import java.awt.*;

public class RepairFrame extends JFrame {

    public RepairFrame(GameState gameState) {
        setTitle("VibeTower — Режим ремонту");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(new Color(255, 235, 205));
        mainPanel.setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("Режим ремонту", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 38));
        titleLabel.setForeground(new Color(72, 37, 120));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(25, 0, 20, 0));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel();
        centerPanel.setBackground(new Color(255, 248, 235));
        centerPanel.setBorder(BorderFactory.createLineBorder(new Color(72, 37, 120), 4));
        centerPanel.setLayout(null);

        JLabel roomLabel = new JLabel("Редагування кімнати", SwingConstants.CENTER);
        roomLabel.setFont(new Font("Arial", Font.BOLD, 26));
        roomLabel.setForeground(new Color(100, 70, 140));
        roomLabel.setBounds(250, 110, 400, 50);
        centerPanel.add(roomLabel);

        JButton wallpaperButton = createMenuButton("Змінити шпалери");
        wallpaperButton.setBounds(310, 190, 280, 45);
        centerPanel.add(wallpaperButton);

        JButton floorButton = createMenuButton("Змінити підлогу");
        floorButton.setBounds(310, 250, 280, 45);
        centerPanel.add(floorButton);

        JButton saveButton = createMenuButton("Зберегти ремонт");
        saveButton.setBounds(310, 310, 280, 45);
        centerPanel.add(saveButton);

        JButton cancelButton = createMenuButton("Скасувати");
        cancelButton.setBounds(310, 370, 280, 45);
        centerPanel.add(cancelButton);

        wallpaperButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Шпалери змінено!");
        });

        floorButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Підлогу змінено!");
        });

        saveButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Ремонт збережено!");
        });

        cancelButton.addActionListener(e -> {
            dispose();
        });

        mainPanel.add(centerPanel, BorderLayout.CENTER);

        add(mainPanel, BorderLayout.CENTER);
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