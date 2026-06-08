package vibetower.model;

import javax.swing.*;
import java.awt.*;

public class ApartmentChoiceFrame extends JFrame {

    private final GameState gameState;

    public ApartmentChoiceFrame(GameState gameState) {
        this.gameState = gameState;

        setTitle("VibeTower — Вибір квартири");
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        JPanel mainPanel = new JPanel(null);
        mainPanel.setBackground(new Color(245, 238, 230));
        setContentPane(mainPanel);

        JLabel titleLabel = new JLabel("Обери свою першу квартиру", SwingConstants.CENTER);
        titleLabel.setBounds(0, 35, 1200, 60);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 38));
        titleLabel.setForeground(new Color(75, 40, 125));
        mainPanel.add(titleLabel);

        JLabel subtitleLabel = new JLabel("Після вибору квартира буде закріплена за персонажем", SwingConstants.CENTER);
        subtitleLabel.setBounds(0, 95, 1200, 35);
        subtitleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        subtitleLabel.setForeground(new Color(100, 70, 150));
        mainPanel.add(subtitleLabel);

        addApartmentCard(
                mainPanel,
                "Синя квартира",
                "src/main/resources/apartment_blue.jpg",
                70,
                170,
                "blue"
        );

        addApartmentCard(
                mainPanel,
                "Рожева квартира",
                "src/main/resources/apartment_pink.jpg",
                430,
                170,
                "pink"
        );

        addApartmentCard(
                mainPanel,
                "Персикова квартира",
                "src/main/resources/apartment_peach.jpg",
                790,
                170,
                "peach"
        );
    }

    private void addApartmentCard(JPanel mainPanel, String title, String imagePath, int x, int y, String apartmentType) {
        JPanel card = new JPanel(null);
        card.setBounds(x, y, 330, 500);
        card.setBackground(new Color(255, 248, 240));
        card.setBorder(BorderFactory.createLineBorder(new Color(95, 65, 150), 4));
        mainPanel.add(card);

        JLabel imageLabel = createImageLabel(imagePath);
        imageLabel.setBounds(15, 20, 300, 230);
        card.add(imageLabel);

        JLabel nameLabel = new JLabel(title, SwingConstants.CENTER);
        nameLabel.setBounds(0, 270, 330, 35);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 24));
        nameLabel.setForeground(new Color(75, 40, 125));
        card.add(nameLabel);

        JTextArea descriptionArea = new JTextArea();
        descriptionArea.setBounds(30, 315, 270, 70);
        descriptionArea.setEditable(false);
        descriptionArea.setOpaque(false);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setFont(new Font("Arial", Font.PLAIN, 15));
        descriptionArea.setForeground(new Color(90, 70, 120));
        descriptionArea.setText("Стартова однокімнатна квартира. Її можна ремонтувати, обставляти меблями та розширювати.");
        card.add(descriptionArea);

        JButton chooseButton = createGameButton("Обрати");
        chooseButton.setBounds(65, 410, 200, 55);
        card.add(chooseButton);

        chooseButton.addActionListener(e -> {
            gameState.setApartmentType(apartmentType);
            SaveManager.saveGame(gameState);

            HomeFrame homeFrame = new HomeFrame(gameState);
            homeFrame.setVisible(true);

            dispose();
        });
    }

    private JLabel createImageLabel(String imagePath) {
        ImageIcon icon = new ImageIcon(imagePath);

        if (icon.getIconWidth() > 0) {
            Image scaledImage = icon.getImage().getScaledInstance(300, 230, Image.SCALE_SMOOTH);
            return new JLabel(new ImageIcon(scaledImage));
        }

        JLabel fallback = new JLabel("Немає зображення", SwingConstants.CENTER);
        fallback.setOpaque(true);
        fallback.setBackground(new Color(230, 220, 240));
        fallback.setForeground(new Color(75, 40, 125));
        fallback.setFont(new Font("Arial", Font.BOLD, 18));
        return fallback;
    }

    private JButton createGameButton(String text) {
        JButton button = new JButton(text);

        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setOpaque(false);

        button.setFont(new Font("Arial", Font.BOLD, 22));
        button.setForeground(new Color(70, 40, 125));

        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(95, 65, 150), 4),
                BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));

        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        return button;
    }
}