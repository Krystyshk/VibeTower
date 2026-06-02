package vibetower.ui;

import vibetower.model.GameState;
import vibetower.model.SaveManager;

import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {

    private static final String MANAGER_EMAIL = "manager@vibetower.com";
    private static final String MANAGER_PASSWORD = "manager123";

    public LoginFrame() {
        setTitle("Вхід менеджера — VibeTower");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel panel = new JPanel(new GridLayout(6, 1, 10, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(95, 250, 95, 250));

        JLabel titleLabel = new JLabel("Вхід менеджера", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 32));

        JLabel hintLabel = new JLabel("Введіть пошту та пароль менеджер-акаунту", SwingConstants.CENTER);
        hintLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        hintLabel.setForeground(new Color(90, 90, 90));

        JTextField emailField = new JTextField();
        JPasswordField passwordField = new JPasswordField();

        emailField.setBorder(BorderFactory.createTitledBorder("Пошта"));
        passwordField.setBorder(BorderFactory.createTitledBorder("Пароль"));

        JButton loginButton = new JButton("Увійти");
        JButton backButton = new JButton("Назад");

        loginButton.setFont(new Font("Arial", Font.BOLD, 18));
        backButton.setFont(new Font("Arial", Font.BOLD, 18));

        panel.add(titleLabel);
        panel.add(hintLabel);
        panel.add(emailField);
        panel.add(passwordField);
        panel.add(loginButton);
        panel.add(backButton);

        add(panel, BorderLayout.CENTER);

        loginButton.addActionListener(e -> {
            String email = emailField.getText().trim();
            String password = new String(passwordField.getPassword());

            if (email.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Заповніть пошту та пароль");
                return;
            }

            if (MANAGER_EMAIL.equals(email) && MANAGER_PASSWORD.equals(password)) {
                JOptionPane.showMessageDialog(this, "Вхід виконано успішно");
                openMainGame();
            } else {
                JOptionPane.showMessageDialog(this, "Невірна пошта або пароль менеджера");
            }
        });

        backButton.addActionListener(e -> {
            StartFrame startFrame = new StartFrame();
            startFrame.setVisible(true);
            dispose();
        });
    }

    private void openMainGame() {
        GameState gameState = SaveManager.loadGame();
        HomeFrame homeFrame = new HomeFrame(gameState);
        homeFrame.setVisible(true);
        dispose();
    }
}
