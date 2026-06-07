package vibetower.ui;

import vibetower.model.GameState;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;

public class PassportFrame extends JFrame {

    public PassportFrame(GameState gameState) {
        setTitle("VibeTower — Паспорт персонажа");
        setSize(620, 560);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(255, 242, 235));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(25, 35, 25, 35));

        JLabel titleLabel = new JLabel("Паспорт персонажа", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 34));
        titleLabel.setForeground(new Color(72, 37, 120));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        JPanel cardPanel = new JPanel();
        cardPanel.setBackground(Color.WHITE);
        cardPanel.setLayout(new BorderLayout());
        cardPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(120, 82, 160), 3),
                BorderFactory.createEmptyBorder(25, 30, 25, 30)
        ));

        JPanel avatarPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(new Color(255, 218, 130));
                g2.fillOval(25, 15, 100, 100);

                g2.setColor(new Color(255, 205, 170));
                g2.fillOval(45, 35, 60, 60);

                g2.setColor(new Color(95, 55, 35));
                g2.fillArc(35, 20, 80, 65, 0, 180);

                g2.setColor(new Color(72, 37, 120));
                g2.fillOval(62, 60, 6, 6);
                g2.fillOval(82, 60, 6, 6);

                g2.setColor(new Color(255, 160, 170));
                g2.fillArc(66, 72, 22, 12, 180, 180);

                g2.setColor(new Color(120, 82, 160));
                g2.fillRoundRect(45, 105, 60, 85, 25, 25);
            }
        };

        avatarPanel.setPreferredSize(new Dimension(150, 210));
        avatarPanel.setBackground(Color.WHITE);

        JPanel infoPanel = new JPanel(new GridLayout(10, 1, 6, 6));
        infoPanel.setBackground(Color.WHITE);

        String id = createCharacterId(gameState);

        infoPanel.add(createInfoLabel("ID: " + id));
        infoPanel.add(createInfoLabel("Ім’я: " + gameState.getCharacterName()));
        infoPanel.add(createInfoLabel("Стать: " + gameState.getGender()));
        infoPanel.add(createInfoLabel("Рівень: " + gameState.getLevel()));
        infoPanel.add(createInfoLabel("Досвід: " + gameState.getExperience() + " XP"));
        infoPanel.add(createInfoLabel("Срібло: " + gameState.getSilver()));
        infoPanel.add(createInfoLabel("Золото: " + gameState.getGold()));
        infoPanel.add(createInfoLabel("Енергія: " + gameState.getEnergy()));
        infoPanel.add(createInfoLabel("Зачіска: " + gameState.getHairStyle()));
        infoPanel.add(createInfoLabel("Дата створення: " + LocalDate.now()));

        cardPanel.add(avatarPanel, BorderLayout.WEST);
        cardPanel.add(infoPanel, BorderLayout.CENTER);

        JButton closeButton = new JButton("Закрити");
        closeButton.setFont(new Font("Arial", Font.BOLD, 18));
        closeButton.setBackground(new Color(255, 218, 130));
        closeButton.setForeground(new Color(72, 37, 120));
        closeButton.setFocusPainted(false);
        closeButton.addActionListener(e -> dispose());

        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        buttonPanel.add(closeButton);

        mainPanel.add(titleLabel, BorderLayout.NORTH);
        mainPanel.add(cardPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private JLabel createInfoLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 17));
        label.setForeground(new Color(60, 45, 80));
        return label;
    }

    private String createCharacterId(GameState gameState) {
        int number = Math.abs(gameState.hashCode() % 10000);
        return String.format("VT-%04d", number);
    }
}