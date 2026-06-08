package vibetower.model;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.Random;
import javax.swing.Timer;

/**
 * NightClubFrame — Нічний клуб.
 * Навчальні дії: коктейль + танці (3 варіанти, випадково).
 * Доступний з 1 рівня.
 */
public class NightClubFrame extends JFrame {

    private final GameState gameState;
    private final Random rnd = new Random();

    private JLabel statsLabel;
    private JLabel characterLabel;
    private Timer movementTimer;

    private boolean cocktailDone = false;
    private boolean danceDone    = false;

    // Координати бармена (ціль для коктейлю) та танцпол (ціль для танців)
    private static final int BAR_X    = 560, BAR_Y    = 220;
    private static final int DANCE_X  = 280, DANCE_Y  = 310;

    private static final String[] DANCE_NAMES = {
            "💃 Сальса", "🕺 Брейк-данс", "✨ Вільний стиль"
    };
    private static final String[] DANCE_DESCS = {
            "Публіка захоплена! Ти танцюєш сальсу!",
            "Всі дивляться — неймовірний брейк-данс!",
            "Твій вільний стиль підкорив танцпол!"
    };

    public NightClubFrame(GameState gameState) {
        this.gameState = gameState;

        setTitle("VibeTower — Нічний клуб 🎵");
        setSize(800, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        BackgroundPanel bg = new BackgroundPanel("src/main/resources/club.png");
        bg.setLayout(null);
        setContentPane(bg);

        // ── Верхня панель статистики ──────────────────────────────────────
        JPanel topPanel = new JPanel();
        topPanel.setBounds(0, 0, 800, 50);
        topPanel.setBackground(new Color(20, 10, 40, 220));
        topPanel.setLayout(null);
        bg.add(topPanel);

        statsLabel = new JLabel();
        statsLabel.setForeground(Color.WHITE);
        statsLabel.setFont(new Font("Arial", Font.BOLD, 14));
        statsLabel.setBounds(10, 12, 500, 26);
        topPanel.add(statsLabel);
        updateStats();

        JButton backBtn = new JButton("↩ На карту");
        backBtn.setBounds(670, 10, 110, 30);
        backBtn.setFocusable(false);
        backBtn.setBackground(new Color(80, 30, 120));
        backBtn.setForeground(Color.WHITE);
        backBtn.addActionListener(e -> goToMap());
        topPanel.add(backBtn);

        // ── Персонаж ──────────────────────────────────────────────────────
        characterLabel = new JLabel("👤", SwingConstants.CENTER);
        characterLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 35));
        characterLabel.setBounds(380, 360, 50, 60);
        bg.add(characterLabel);

        // ── Кнопка «Коктейль» (біля бару) ───────────────────────────────
        // ── Нижня панель кнопок ──────────────────────────────────────────
        JPanel btnPanel = new JPanel(null);
        btnPanel.setBounds(0, 542, 800, 42);
        btnPanel.setBackground(new Color(20, 10, 50, 210));
        bg.add(btnPanel);

        JButton cocktailBtn = makeActionButton("🍹  Коктейль  (-15🪙  +5⚡  +10XP)");
        cocktailBtn.setBounds(80, 4, 300, 38);
        cocktailBtn.addActionListener(e -> moveCharacter(BAR_X, BAR_Y, () -> drinkCocktail(cocktailBtn)));
        btnPanel.add(cocktailBtn);

        JButton danceBtn = makeActionButton("🕺  Танцювати  (-8⚡  +15XP)");
        danceBtn.setBounds(420, 4, 300, 38);
        danceBtn.addActionListener(e -> moveCharacter(DANCE_X, DANCE_Y, () -> dance(danceBtn)));
        btnPanel.add(danceBtn);

        // ── NPC-підказка ─────────────────────────────────────────────────
        JLabel npcHint = makeHintLabel(
                "<html><i>Сусідка: «Замов коктейль біля бару, а потім танцюй на танцполі!»</i></html>",
                0, 584, 800, 24
        );
        bg.add(npcHint);

        // ── Переміщення мишкою ───────────────────────────────────────────
        bg.addMouseListener(new MouseAdapter() {
            @Override public void mousePressed(MouseEvent e) {
                if (e.getY() > 55) moveCharacter(e.getX() - 25, e.getY() - 30, null);
            }
        });
    }

    // ── Логіка ───────────────────────────────────────────────────────────────

    private void drinkCocktail(JButton btn) {
        if (cocktailDone) { showInfo("Ти вже випив(ла) коктейль!"); return; }
        if (!gameState.spendSilver(15)) { showWarn("🪙 Недостатньо срібла! Потрібно 15."); return; }
        gameState.addEnergy(5);
        gameState.addXp(10);
        cocktailDone = true;
        btn.setEnabled(false);
        btn.setText("✅ Коктейль випито");
        SaveManager.saveGame(gameState);
        updateStats();
        showInfo("🍹 Смачний коктейль!\n-15 срібла  +5 ⚡  +10 XP");
        checkTutorial();
    }

    private void dance(JButton btn) {
        if (danceDone) { showInfo("Ти вже потанцював(ла)!"); return; }
        if (gameState.getEnergy() < 8) { showWarn("⚡ Замало енергії! Потрібно 8 ⚡."); return; }
        int idx = rnd.nextInt(3);
        gameState.spendEnergy(8);
        gameState.addXp(15);
        danceDone = true;
        btn.setEnabled(false);
        btn.setText("✅ Потанцювали!");
        SaveManager.saveGame(gameState);
        updateStats();
        showInfo(DANCE_NAMES[idx] + "\n" + DANCE_DESCS[idx] + "\n\n-8 ⚡  +15 XP");
        checkTutorial();
    }

    private void checkTutorial() {
        if (cocktailDone && danceDone) {
            JOptionPane.showMessageDialog(this,
                    "🎉 Навчальне завдання виконано!\nТепер ти знаєш, як витрачається енергія.",
                    "Навчання завершено!", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    // ── Переміщення персонажа ─────────────────────────────────────────────
    private void moveCharacter(int targetX, int targetY, Runnable onArrival) {
        if (movementTimer != null && movementTimer.isRunning()) movementTimer.stop();
        movementTimer = new Timer(15, null);
        movementTimer.addActionListener(e -> {
            int cx = characterLabel.getX(), cy = characterLabel.getY();
            int step = 4;
            int nx = approach(cx, targetX, step);
            int ny = approach(cy, targetY, step);
            characterLabel.setLocation(nx, ny);
            if (nx == targetX && ny == targetY) {
                movementTimer.stop();
                if (onArrival != null) onArrival.run();
            }
        });
        movementTimer.start();
    }

    private int approach(int current, int target, int step) {
        if (Math.abs(current - target) <= step) return target;
        return current + (current < target ? step : -step);
    }

    // ── Допоміжні методи ──────────────────────────────────────────────────
    private JButton makeActionButton(String text, int x, int y, int w, int h) {
        JButton btn = makeActionButton(text);
        btn.setBounds(x, y, w, h);
        return btn;
    }

    private JButton makeActionButton(String text) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? new Color(130, 50, 200) : new Color(100, 30, 160));
                g2.fillRoundRect(0,0,getWidth(),getHeight(),12,12);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Arial", Font.BOLD, 13));
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JLabel makeHintLabel(String text, int x, int y, int w, int h) {
        JLabel lbl = new JLabel(text, SwingConstants.CENTER);
        lbl.setBounds(x, y, w, h);
        lbl.setForeground(new Color(220, 190, 255));
        lbl.setFont(new Font("Arial", Font.ITALIC, 12));
        return lbl;
    }

    private void updateStats() {
        statsLabel.setText(String.format(
                "⚡ Енергія: %d/100  |  🪙 %d  |  🌟 %d  |  XP: %d",
                gameState.getEnergy(), gameState.getSilver(), gameState.getGold(), gameState.getXp()
        ));
    }

    private void showInfo(String msg)  { JOptionPane.showMessageDialog(this, msg); }
    private void showWarn(String msg)  { JOptionPane.showMessageDialog(this, msg, "Увага", JOptionPane.WARNING_MESSAGE); }

    private void goToMap() {
        if (movementTimer != null) movementTimer.stop();
        new MapFrame(gameState).setVisible(true);
        dispose();
    }

    // ── Фоновий JPanel ────────────────────────────────────────────────────
    static class BackgroundPanel extends JPanel {
        private Image img;
        BackgroundPanel(String path) {
            try { img = ImageIO.read(new File(path)); }
            catch (Exception e) { System.out.println("Фон не знайдено: " + path); }
        }
        @Override protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (img != null) g.drawImage(img, 0, 0, getWidth(), getHeight(), this);
            else { g.setColor(new Color(20,10,40)); g.fillRect(0,0,getWidth(),getHeight()); }
        }
    }

}

