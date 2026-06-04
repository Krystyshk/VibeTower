package vibetower.ui;

import vibetower.model.GameState;
import vibetower.model.HomeFrame;

import javax.swing.*;
import java.awt.*;

public class StartFrame extends JFrame {

    private GameState gameState;

    public StartFrame(GameState gameState) {
        this.gameState = gameState;

        setTitle("VibeTower — Вхід");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setLayout(null);

        JLabel titleLabel = new JLabel("VibeTower");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 54));
        titleLabel.setForeground(new Color(72, 37, 120));
        titleLabel.setBounds(290, 80, 400, 70);
        mainPanel.add(titleLabel);

        JLabel subtitleLabel = new JLabel("Ласкаво просимо у гру!");
        subtitleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        subtitleLabel.setForeground(new Color(120, 82, 160));
        subtitleLabel.setBounds(285, 155, 400, 40);
        mainPanel.add(subtitleLabel);

        JTextField emailField = new JTextField();
        emailField.setBounds(300, 240, 300, 45);
        emailField.setFont(new Font("Arial", Font.PLAIN, 18));
        emailField.setBorder(BorderFactory.createTitledBorder("Email"));
        mainPanel.add(emailField);

        JPasswordField passwordField = new JPasswordField();
        passwordField.setBounds(300, 305, 300, 45);
        passwordField.setFont(new Font("Arial", Font.PLAIN, 18));
        passwordField.setBorder(BorderFactory.createTitledBorder("Пароль"));
        mainPanel.add(passwordField);

        JButton startButton = new JButton("Увійти в гру");
        startButton.setBounds(335, 385, 230, 55);
        startButton.setFont(new Font("Arial", Font.BOLD, 22));
        startButton.setBackground(new Color(255, 210, 120));
        startButton.setForeground(new Color(72, 37, 120));
        startButton.setFocusPainted(false);
        mainPanel.add(startButton);

        startButton.addActionListener(e -> {
            HomeFrame homeFrame = new HomeFrame(gameState);
            homeFrame.setVisible(true);
            dispose();
        });

        add(mainPanel, BorderLayout.CENTER);
    }

    public StartFrame() {
        this(new GameState());
    }
}