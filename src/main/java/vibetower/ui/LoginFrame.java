package vibetower.ui;

import vibetower.model.GameState;
import vibetower.model.HomeFrame;
import vibetower.model.SaveManager;

import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {

    private final GameState gameState;
    private JTextField emailField;
    private JPasswordField passwordField;

    public LoginFrame(GameState gameState) {
        this.gameState = gameState;

        setTitle("VibeTower — Вхід");
        setSize(1280, 720);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        BackgroundPanel mainPanel = new BackgroundPanel("/login_screen.png");
        mainPanel.setLayout(null);

        emailField = createTextField();
        emailField.setBounds(510, 270, 390, 28);
        mainPanel.add(emailField);

        passwordField = createPasswordField();
        passwordField.setBounds(510, 340, 350, 28);
        mainPanel.add(passwordField);

        // Кнопка "Увійти"
        JButton loginButton = createHotspotButton();
        loginButton.setBounds(440, 420, 400, 75);
        loginButton.addActionListener(e -> login());
        mainPanel.add(loginButton);

        // Кнопка "Зареєструватися"
        JButton registerButton = createHotspotButton();
        registerButton.setBounds(460, 565, 370, 52);
        registerButton.addActionListener(e -> {
            CharacterCreatorFrame creator = new CharacterCreatorFrame(gameState);
            creator.setVisible(true);
            dispose();
        });
        mainPanel.add(registerButton);

        setContentPane(mainPanel);
    }

    public LoginFrame() {
        this(SaveManager.loadGame());
    }

    private void login() {
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        if (email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Заповніть усі поля!", "Помилка", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!gameState.hasAccount()) {
            JOptionPane.showMessageDialog(this,
                    "Акаунт не знайдено.\nСпочатку зареєструйтесь!", "Помилка", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!gameState.checkLogin(email, password)) {
            JOptionPane.showMessageDialog(this,
                    "Невірний email або пароль!", "Помилка", JOptionPane.ERROR_MESSAGE);
            return;
        }

        HomeFrame homeFrame = new HomeFrame(gameState);
        homeFrame.setVisible(true);
        dispose();
    }

    private JTextField createTextField() {
        JTextField field = new JTextField();
        field.setFont(new Font("Arial", Font.PLAIN, 16));
        field.setForeground(Color.WHITE);
        field.setCaretColor(Color.WHITE);
        field.setOpaque(false);
        field.setBorder(null);
        field.setBackground(new Color(0, 0, 0, 0));
        return field;
    }

    private JPasswordField createPasswordField() {
        JPasswordField field = new JPasswordField();
        field.setFont(new Font("Arial", Font.PLAIN, 16));
        field.setForeground(Color.WHITE);
        field.setCaretColor(Color.WHITE);
        field.setOpaque(false);
        field.setBorder(null);
        field.setBackground(new Color(0, 0, 0, 0));
        field.setEchoChar('●');
        return field;
    }

    private JButton createHotspotButton() {
        JButton button = new JButton();
        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    static class BackgroundPanel extends JPanel {
        private final Image backgroundImage;

        public BackgroundPanel(String imagePath) {
            ImageIcon icon = new ImageIcon(getClass().getResource(imagePath));
            backgroundImage = icon.getImage();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }
}