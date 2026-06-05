package vibetower.ui;

import vibetower.model.GameState;
import vibetower.model.HomeFrame;

import javax.swing.*;
import java.awt.*;

public class CharacterCreatorFrame extends JFrame {

    private final GameState gameState;

    private JTextField emailField;
    private JPasswordField passwordField;
    private JPasswordField repeatPasswordField;
    private JTextField characterNameField;

    private String selectedGender = "Жіночий персонаж";

    public CharacterCreatorFrame(GameState gameState) {
        this.gameState = gameState;

        setTitle("VibeTower — Реєстрація");
        setSize(1280, 720);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        BackgroundPanel mainPanel = new BackgroundPanel("/registration_screen.png");
        mainPanel.setLayout(null);

        JButton femaleButton = createHotspotButton();
        femaleButton.setBounds(455, 243, 195, 45);
        femaleButton.addActionListener(e -> selectedGender = "Жіночий персонаж");
        mainPanel.add(femaleButton);

        JButton maleButton = createHotspotButton();
        maleButton.setBounds(665, 243, 215, 45);
        maleButton.addActionListener(e -> selectedGender = "Чоловічий персонаж");
        mainPanel.add(maleButton);

        /*
         * ПОЛЯ ВВОДУ.
         * Тут НЕМАЄ placeholder-тексту.
         * Поля порожні, а курсор стоїть чітко всередині кожного поля.
         */

        emailField = createTextField();
        emailField.setBounds(520, 342, 310, 24);
        mainPanel.add(emailField);

        passwordField = createPasswordField();
        passwordField.setBounds(520, 402, 310, 24);
        mainPanel.add(passwordField);

        repeatPasswordField = createPasswordField();
        repeatPasswordField.setBounds(520, 462, 310, 24);
        mainPanel.add(repeatPasswordField);

        characterNameField = createTextField();
        characterNameField.setBounds(520, 522, 310, 24);
        mainPanel.add(characterNameField);

        JButton registerButton = createHotspotButton();
        registerButton.setBounds(500, 555, 350, 65);
        registerButton.addActionListener(e -> registerUser());
        mainPanel.add(registerButton);

        JButton backButton = createHotspotButton();
        backButton.setBounds(555, 625, 190, 45);
        backButton.addActionListener(e -> {
            StartFrame startFrame = new StartFrame(gameState);
            startFrame.setVisible(true);
            dispose();
        });
        mainPanel.add(backButton);

        JButton loginButton = createHotspotButton();
        loginButton.setBounds(650, 675, 200, 35);
        loginButton.addActionListener(e -> {
            StartFrame startFrame = new StartFrame(gameState);
            startFrame.setVisible(true);
            dispose();
        });
        mainPanel.add(loginButton);

        setContentPane(mainPanel);
    }

    public CharacterCreatorFrame() {
        this(new GameState());
    }

    private JTextField createTextField() {
        JTextField field = new JTextField();

        field.setFont(new Font("Avenir Next", Font.PLAIN, 16));
        field.setForeground(Color.WHITE);
        field.setCaretColor(Color.WHITE);

        field.setOpaque(false);
        field.setBorder(null);
        field.setBackground(new Color(0, 0, 0, 0));

        field.setHorizontalAlignment(JTextField.LEFT);

        return field;
    }

    private JPasswordField createPasswordField() {
        JPasswordField field = new JPasswordField();

        field.setFont(new Font("Avenir Next", Font.PLAIN, 16));
        field.setForeground(Color.WHITE);
        field.setCaretColor(Color.WHITE);

        field.setOpaque(false);
        field.setBorder(null);
        field.setBackground(new Color(0, 0, 0, 0));

        field.setHorizontalAlignment(JTextField.LEFT);
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

    private void registerUser() {
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();
        String repeatPassword = new String(repeatPasswordField.getPassword()).trim();
        String characterName = characterNameField.getText().trim();

        if (email.isEmpty() || password.isEmpty() || repeatPassword.isEmpty() || characterName.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Заповніть усі поля!",
                    "Помилка",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        if (!email.contains("@") || !email.contains(".")) {
            JOptionPane.showMessageDialog(
                    this,
                    "Введіть коректний email!",
                    "Помилка",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        if (!password.equals(repeatPassword)) {
            JOptionPane.showMessageDialog(
                    this,
                    "Паролі не співпадають!",
                    "Помилка",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        if (characterName.length() < 3 || characterName.length() > 16) {
            JOptionPane.showMessageDialog(
                    this,
                    "Ім’я персонажа має містити від 3 до 16 символів!",
                    "Помилка",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        JOptionPane.showMessageDialog(
                this,
                "Акаунт створено!\n" +
                        "Email: " + email + "\n" +
                        "Ім’я персонажа: " + characterName + "\n" +
                        "Тип персонажа: " + selectedGender,
                "Успішно",
                JOptionPane.INFORMATION_MESSAGE
        );

        HomeFrame homeFrame = new HomeFrame(gameState);
        homeFrame.setVisible(true);
        dispose();
    }

    private static class BackgroundPanel extends JPanel {

        private final Image backgroundImage;

        public BackgroundPanel(String imagePath) {
            ImageIcon icon = new ImageIcon(BackgroundPanel.class.getResource(imagePath));

            if (icon.getIconWidth() == -1) {
                JOptionPane.showMessageDialog(
                        null,
                        "Не знайдено картинку: " + imagePath,
                        "Помилка",
                        JOptionPane.ERROR_MESSAGE
                );
            }

            backgroundImage = icon.getImage();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            g.drawImage(
                    backgroundImage,
                    0,
                    0,
                    getWidth(),
                    getHeight(),
                    this
            );
        }
    }
}