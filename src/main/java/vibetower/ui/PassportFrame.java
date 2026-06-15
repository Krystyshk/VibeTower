package vibetower.ui;

import vibetower.model.GameState;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.time.LocalDate;

public class PassportFrame extends JFrame {

    private AnimatedCharacterPanel charPanel;

    public PassportFrame(GameState gameState) {
        setTitle("VibeTower — Паспорт персонажа");
        setSize(900, 560);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);

        // ── фонова панель з зображенням паспорту ──────────────────────────────
        JPanel bgPanel = new JPanel(null) {
            private BufferedImage bg;
            {
                try {
                    var url = getClass().getResource("/passport_bg.png");
                    if (url != null) bg = ImageIO.read(url);
                } catch (IOException ignored) {}
            }
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (bg != null) {
                    g.drawImage(bg, 0, 0, getWidth(), getHeight(), null);
                } else {
                    g.setColor(new Color(240, 230, 215));
                    g.fillRect(0, 0, getWidth(), getHeight());
                }
            }
        };
        bgPanel.setPreferredSize(new Dimension(900, 560));

        // ── персонаж в овалі зліва ────────────────────────────────────────────
        charPanel = new AnimatedCharacterPanel(
                mapGender(gameState.getGender()),
                mapHair(gameState.getHairStyle()),
                mapEyes(gameState.getEyeColor())
        );
        charPanel.setWalking(false);
        charPanel.setBounds(30, 60, 280, 420);
        charPanel.setOpaque(false);
        bgPanel.add(charPanel);

        // ── дані персонажа поверх полів праворуч ──────────────────────────────
        String id = String.format("VT-%04d", Math.abs(gameState.hashCode() % 10000));

        // Ім'я — верхнє широке поле
        JLabel nameLabel = makeLabel(gameState.getCharacterName(), 18, true);
        nameLabel.setBounds(360, 55, 490, 70);
        bgPanel.add(nameLabel);

        // Рядок 1: ID | Стать
        JLabel idLabel = makeLabel("ID: " + id, 14, false);
        idLabel.setBounds(360, 148, 230, 45);
        bgPanel.add(idLabel);

        JLabel genderLabel = makeLabel("Стать: " + gameState.getGender(), 14, false);
        genderLabel.setBounds(605, 148, 245, 45);
        bgPanel.add(genderLabel);

        // Рядок 2: Рівень | Досвід
        JLabel levelLabel = makeLabel("Рівень: " + gameState.getLevel(), 14, false);
        levelLabel.setBounds(360, 208, 230, 45);
        bgPanel.add(levelLabel);

        JLabel xpLabel = makeLabel("Досвід: " + gameState.getExperience() + " XP", 14, false);
        xpLabel.setBounds(605, 208, 245, 45);
        bgPanel.add(xpLabel);

        // Рядок 3: Срібло | Золото
        JLabel silverLabel = makeLabel("Срібло: " + gameState.getSilver(), 14, false);
        silverLabel.setBounds(360, 268, 230, 45);
        bgPanel.add(silverLabel);

        JLabel goldLabel = makeLabel("Золото: " + gameState.getGold(), 14, false);
        goldLabel.setBounds(605, 268, 245, 45);
        bgPanel.add(goldLabel);

        // Нижнє широке поле — рівномірно по висоті
        JLabel energyLabel = makeLabel("Енергія: " + gameState.getEnergy(), 13, false);
        energyLabel.setBounds(360, 362, 490, 40);
        bgPanel.add(energyLabel);

        JLabel hairLabel = makeLabel("Зачіска: " + gameState.getHairStyle(), 13, false);
        hairLabel.setBounds(360, 407, 490, 40);
        bgPanel.add(hairLabel);

        JLabel dateLabel = makeLabel("Дата: " + LocalDate.now(), 13, false);
        dateLabel.setBounds(360, 452, 490, 40);
        bgPanel.add(dateLabel);

        // ── кнопка закрити ────────────────────────────────────────────────────
        JButton closeBtn = new JButton("Закрити");
        closeBtn.setFont(new Font("Arial", Font.BOLD, 14));
        closeBtn.setBackground(new Color(255, 218, 130));
        closeBtn.setForeground(new Color(72, 37, 120));
        closeBtn.setFocusPainted(false);
        closeBtn.setBorder(BorderFactory.createLineBorder(new Color(72, 37, 120), 2));
        closeBtn.setBounds(390, 505, 120, 36);
        closeBtn.addActionListener(e -> { charPanel.stopAnimation(); dispose(); });
        bgPanel.add(closeBtn);

        setContentPane(bgPanel);

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override public void windowClosing(java.awt.event.WindowEvent e) {
                charPanel.stopAnimation();
            }
        });
    }

    private JLabel makeLabel(String text, int size, boolean bold) {
        JLabel lbl = new JLabel(text, SwingConstants.CENTER);
        lbl.setFont(new Font("Arial", bold ? Font.BOLD : Font.PLAIN, size));
        lbl.setForeground(new Color(80, 55, 30));
        lbl.setOpaque(false);
        lbl.setVerticalAlignment(SwingConstants.CENTER);
        return lbl;
    }

    private String mapGender(String g) {
        if (g == null) return "female";
        return g.toLowerCase().contains("чол") ? "male" : "female";
    }

    private String mapHair(String s) {
        if (s == null) return "hair_wavy";
        switch (s) {
            case "Довге": case "Довге пряме": return "hair_wavy";
            case "Кучеряве":  return "hair_curly";
            case "Хвіст":     return "hair_ponytail";
            case "Пучок":     return "hair_bun";
            case "Каре":      return "hair_bob";
            case "Коса":      return "hair_braids";
            default:          return "hair_wavy";
        }
    }

    private String mapEyes(String e) {
        if (e == null) return "eyes_brown";
        switch (e) {
            case "Карі":     return "eyes_brown";
            case "Сині":     return "eyes_blue";
            case "Зелені":   return "eyes_green";
            case "Сірі":     return "eyes_gray";
            case "Горіхові": return "eyes_hazel";
            default:         return "eyes_brown";
        }
    }
}
