package vibetower.ui;

import vibetower.model.GameState;
import vibetower.model.HomeFrame;

import javax.swing.*;
import java.awt.*;

public class StartFrame extends JFrame {

    private final GameState gameState;

    public StartFrame(GameState gameState) {
        this.gameState = gameState;

        setTitle("VibeTower — Головне меню");
        setSize(1280, 720);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        BackgroundPanel mainPanel = new BackgroundPanel("/main_menu.png");
        mainPanel.setLayout(null);

        // Прозора кнопка "Увійти"
        JButton loginButton = createHotspotButton();
        loginButton.setBounds(500, 300, 390, 95);

        loginButton.addActionListener(e -> {
            HomeFrame homeFrame = new HomeFrame(gameState);
            homeFrame.setVisible(true);
            dispose();
        });

        // Прозора кнопка "Зареєструватися"
        JButton registerButton = createHotspotButton();
        registerButton.setBounds(500, 420, 390, 95);

        registerButton.addActionListener(e -> {
            CharacterCreatorFrame creatorFrame = new CharacterCreatorFrame();
            creatorFrame.setVisible(true);
            dispose();
        });

        mainPanel.add(loginButton);
        mainPanel.add(registerButton);

        setContentPane(mainPanel);
    }

    public StartFrame() {
        this(new GameState());
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