package vibetower.ui;

import vibetower.model.GameState;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

public class ApartmentFrame extends JFrame {

    private GameState gameState;

    public ApartmentFrame(GameState gameState) {
        this.gameState = gameState;

        setTitle("VibeTower — Квартира");
        setSize(1000, 700);
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

        JLabel titleLabel = new JLabel("Квартира персонажа", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 36));
        titleLabel.setForeground(new Color(72, 37, 120));

        topPanel.add(titleLabel, BorderLayout.CENTER);
        topPanel.add(new CurrencyPanel(gameState), BorderLayout.EAST);

        mainPanel.add(topPanel, BorderLayout.NORTH);

        JLabel roomLabel = new JLabel();
        roomLabel.setHorizontalAlignment(SwingConstants.CENTER);
        roomLabel.setVerticalAlignment(SwingConstants.CENTER);

        URL roomUrl = getClass().getResource("/komnata.jpg");

        if (roomUrl != null) {
            ImageIcon roomIcon = new ImageIcon(roomUrl);

            Image scaledRoom = roomIcon.getImage().getScaledInstance(
                    860,
                    500,
                    Image.SCALE_SMOOTH
            );

            roomLabel.setIcon(new ImageIcon(scaledRoom));
        } else {
            roomLabel.setText("Картинку кімнати не знайдено");
            roomLabel.setFont(new Font("Arial", Font.BOLD, 24));
            roomLabel.setForeground(Color.RED);
        }

        mainPanel.add(roomLabel, BorderLayout.CENTER);

        add(mainPanel, BorderLayout.CENTER);
    }

    public ApartmentFrame() {
        this(new GameState());
    }
}
