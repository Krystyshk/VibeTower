package vibetower.model;

import javax.swing.*;
import java.awt.*;

public class MapFrame extends JFrame {
    private GameState gameState;

    public MapFrame(GameState gameState) {
        this.gameState = gameState;

        setTitle("VibeTower — Карта міста");
        setSize(800, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel();
        topPanel.setBackground(new Color(30, 32, 36));
        topPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel statsLabel = new JLabel(
                "Рівень: " + gameState.getLevel() +
                        " | Енергія: " + gameState.getEnergy() +
                        " | Срібло: " + gameState.getSilver()
        );
        statsLabel.setForeground(new Color(220, 220, 220));
        statsLabel.setFont(new Font("Monospaced", Font.BOLD, 16));
        topPanel.add(statsLabel);
        add(topPanel, BorderLayout.NORTH);

        // Центральная панель с локациями
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new GridLayout(3, 2, 15, 15));
        centerPanel.setBackground(new Color(45, 48, 54));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(40, 100, 40, 100));

        // Створення кнопок локацій (без масивів)
        JButton clubBtn = createLocationButton("Нічний клуб", 1, gameState.getLevel());
        JButton cafeBtn = createLocationButton("Кафе (Робота)", 2, gameState.getLevel());
        JButton parkBtn = createLocationButton("Парк", 4, gameState.getLevel());
        JButton beachBtn = createLocationButton("Пляж", 4, gameState.getLevel());
        JButton cinemaBtn = createLocationButton("Кінотеатр", 5, gameState.getLevel());

        JButton backBtn = new JButton("Повернутися додому");
        backBtn.setBackground(new Color(70, 75, 85));
        backBtn.setForeground(Color.WHITE);

        centerPanel.add(clubBtn);
        centerPanel.add(cafeBtn);
        centerPanel.add(parkBtn);
        centerPanel.add(beachBtn);
        centerPanel.add(cinemaBtn);
        centerPanel.add(backBtn);

        add(centerPanel, BorderLayout.CENTER);

        backBtn.addActionListener(e -> {
            new HomeFrame(gameState).setVisible(true);
            dispose();
        });

        cafeBtn.addActionListener(e -> {
            new CafeFrame(gameState).setVisible(true);
            dispose();
        });

    }

    private JButton createLocationButton(String name, int requiredLevel, int currentLevel) {
        JButton btn = new JButton(name + " (з " + requiredLevel + " рівня)");
        btn.setFont(new Font("SansSerif", Font.BOLD, 14));

        if (currentLevel >= requiredLevel) {
            btn.setBackground(new Color(60, 65, 75));
            btn.setForeground(Color.WHITE);
            btn.setEnabled(true);
        } else {
            btn.setBackground(new Color(35, 38, 42));
            btn.setForeground(Color.GRAY);
            btn.setEnabled(false);
            btn.setText("🔒 " + name + " (рівень " + requiredLevel + ")");
        }
        return btn;
    }
}
