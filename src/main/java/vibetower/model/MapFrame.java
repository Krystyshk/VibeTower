package vibetower.model;

import javax.swing.*;
import java.awt.*;

/**
 * MapFrame — Карта міста.
 * Показує всі локації. Заблоковані мають позначку рівня.
 */
public class MapFrame extends JFrame {

    private final GameState gameState;

    public MapFrame(GameState gameState) {
        this.gameState = gameState;

        setTitle("VibeTower — Карта міста 🗺");
        setSize(800, 580);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // ── Топ-панель ─────────────────────────────────────────────────────
        JPanel topPanel = new JPanel();
        topPanel.setBackground(new Color(30, 32, 36));
        topPanel.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));

        JLabel statsLabel = new JLabel(
                "Рівень: " + gameState.getLevel() +
                        "  |  ⚡ " + gameState.getEnergy() +
                        "  |  🪙 " + gameState.getSilver() +
                        "  |  🌟 " + gameState.getGold() +
                        "  |  XP: " + gameState.getXp()
        );
        statsLabel.setForeground(new Color(220, 220, 220));
        statsLabel.setFont(new Font("Arial", Font.BOLD, 14));
        topPanel.add(statsLabel);
        add(topPanel, BorderLayout.NORTH);

        // ── Сітка локацій ──────────────────────────────────────────────────
        JPanel centerPanel = new JPanel(new GridLayout(3, 2, 15, 15));
        centerPanel.setBackground(new Color(45, 48, 54));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(30, 80, 20, 80));

        int lvl = gameState.getLevel();

        // Нічний клуб — з 1 рівня
        JButton clubBtn = makeLocBtn("🎵 Нічний клуб",    1, lvl);
        clubBtn.addActionListener(e -> { new NightClubFrame(gameState).setVisible(true); dispose(); });

        // Кафе — з 2 рівня
        JButton cafeBtn = makeLocBtn("☕ Кафе (Офіціант)", 2, lvl);
        cafeBtn.addActionListener(e -> { new CafeFrame(gameState).setVisible(true); dispose(); });

        // Робота двірником — з 1 рівня
        JButton janitorBtn = makeLocBtn("🧹 Двірник",       1, lvl);
        janitorBtn.addActionListener(e -> { new JanitorFrame(gameState, false).setVisible(true); dispose(); });

        // Парк — з 4 рівня
        JButton parkBtn = makeLocBtn("🌳 Парк",            4, lvl);
        parkBtn.addActionListener(e -> { new ParkFrame(gameState).setVisible(true); dispose(); });

        // Пляж — з 4 рівня
        JButton beachBtn = makeLocBtn("🏖 Пляж",           4, lvl);
        beachBtn.addActionListener(e -> { new BeachFrame(gameState).setVisible(true); dispose(); });

        // Кінотеатр — з 5 рівня
        JButton cinemaBtn = makeLocBtn("🎬 Кінотеатр",     5, lvl);
        cinemaBtn.addActionListener(e -> { new CinemaFrame(gameState).setVisible(true); dispose(); });

        centerPanel.add(clubBtn);
        centerPanel.add(cafeBtn);
        centerPanel.add(janitorBtn);
        centerPanel.add(parkBtn);
        centerPanel.add(beachBtn);
        centerPanel.add(cinemaBtn);

        add(centerPanel, BorderLayout.CENTER);

        // ── Кнопка «Додому» ────────────────────────────────────────────────
        JPanel botPanel = new JPanel();
        botPanel.setBackground(new Color(30, 32, 36));
        botPanel.setBorder(BorderFactory.createEmptyBorder(8, 8, 10, 8));

        JButton homeBtn = new JButton("🏠 Повернутися додому");
        homeBtn.setBackground(new Color(70, 75, 85));
        homeBtn.setForeground(Color.WHITE);
        homeBtn.setFocusable(false);
        homeBtn.setFont(new Font("Arial", Font.BOLD, 13));
        homeBtn.addActionListener(e -> { new HomeFrame(gameState).setVisible(true); dispose(); });
        botPanel.add(homeBtn);

        add(botPanel, BorderLayout.SOUTH);
    }

    private JButton makeLocBtn(String name, int required, int current) {
        boolean unlocked = current >= required;
        JButton btn = new JButton();
        btn.setFont(new Font("Arial", Font.BOLD, 14));
        btn.setFocusable(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        if (unlocked) {
            btn.setText(name);
            btn.setBackground(new Color(60, 65, 75));
            btn.setForeground(Color.WHITE);
        } else {
            btn.setText("🔒 " + name + "  (з " + required + " рівня)");
            btn.setBackground(new Color(35, 38, 42));
            btn.setForeground(new Color(120, 120, 120));
            btn.setEnabled(false);
        }
        return btn;
    }
}
