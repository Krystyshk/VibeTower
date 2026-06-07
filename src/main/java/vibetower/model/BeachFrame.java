package vibetower.model;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * BeachFrame — Пляж (з 4 рівня).
 * Кнопки в стилі CafeFrame. Мушлі оновлюються раз на годину з таймером відліку.
 */
public class BeachFrame extends JFrame {

    private final GameState gameState;
    private final Random rnd = new Random();

    private JLabel statsLabel;
    private JLabel shellTimerLabel;
    private JLabel characterLabel;
    private Timer movementTimer;
    private Timer shellCooldownTimer;

    private int shellsCollected = 0;
    private static final int TOTAL_SHELLS = 5;
    private final List<JLabel> shellLabels = new ArrayList<>();

    // Кулдаун мушель (1 година = 3600 сек)
    private static final long SHELL_COOLDOWN_MS = 60 * 60 * 1000L; // 1 година
    private int shellCooldownLeft = 0; // секунди

    private static final int BAR_X = 700, BAR_Y = 170;
    private static final int BALL_X = 350, BALL_Y = 370;

    private static final int[][] SHELL_POS = {
            {100, 370}, {210, 400}, {340, 415}, {460, 385}, {580, 375}
    };

    private static final Color TOP_BG   = new Color(20, 70, 130, 220);
    private static final Color BTN_BLUE = new Color(30, 100, 190);
    private static final Color BTN_TEAL = new Color(20, 130, 120);

    public BeachFrame(GameState gameState) {
        this.gameState = gameState;

        setTitle("VibeTower — Пляж 🏖");
        setSize(800, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        // Перевіряємо кулдаун мушель
        long shellEndTime = gameState.getShellCooldownEndTime();
        long diff = shellEndTime - System.currentTimeMillis();
        boolean shellsOnCooldown = diff > 0;
        if (shellsOnCooldown) shellCooldownLeft = (int)(diff / 1000);

        BackgroundPanel bg = new BackgroundPanel("src/main/resources/beach.png");
        bg.setLayout(null);
        setContentPane(bg);

        // ── Топ-панель ─────────────────────────────────────────────────────
        JPanel top = new JPanel(null);
        top.setBounds(0, 0, 800, 55);
        top.setBackground(TOP_BG);
        bg.add(top);

        statsLabel = new JLabel();
        statsLabel.setForeground(Color.WHITE);
        statsLabel.setFont(new Font("Arial", Font.BOLD, 13));
        statsLabel.setBounds(10, 5, 680, 22);
        top.add(statsLabel);

        // Таймер оновлення мушель
        shellTimerLabel = new JLabel();
        shellTimerLabel.setForeground(shellsOnCooldown ? new Color(255, 180, 80) : new Color(120, 255, 120));
        shellTimerLabel.setFont(new Font("Arial", Font.BOLD, 12));
        shellTimerLabel.setBounds(10, 28, 500, 18);
        top.add(shellTimerLabel);

        JButton backBtn = makeBtn("↩ На карту", new Color(60, 80, 110));
        backBtn.setBounds(672, 10, 116, 34);
        backBtn.addActionListener(e -> goToMap());
        top.add(backBtn);

        updateStats(shellsOnCooldown);

        // ── Персонаж ────────────────────────────────────────────────────────
        characterLabel = new JLabel("🧑", SwingConstants.CENTER);
        characterLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 36));
        characterLabel.setBounds(360, 290, 50, 58);
        bg.add(characterLabel);

        // ── Кнопки внизу (стиль як кафе) ────────────────────────────────────
        JPanel btnPanel = buildButtonPanel();
        btnPanel.setBounds(0, 542, 800, 42);
        bg.add(btnPanel);

        // ── NPC підказка ────────────────────────────────────────────────────
        JLabel hint = new JLabel("🌊 Відпочивальник: «Збирай мушлі на піску або купи напій у барі!»",
                SwingConstants.CENTER);
        hint.setBounds(0, 586, 800, 22);
        hint.setForeground(new Color(255, 245, 200));
        hint.setFont(new Font("Arial", Font.ITALIC, 12));
        hint.setOpaque(true);
        hint.setBackground(new Color(0, 40, 80, 180));
        bg.add(hint);

        // ── Мушлі ────────────────────────────────────────────────────────────
        for (int i = 0; i < TOTAL_SHELLS; i++) {
            final int idx = i;
            JLabel shell = new JLabel("🐚", SwingConstants.CENTER);
            shell.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));
            shell.setBounds(SHELL_POS[i][0], SHELL_POS[i][1], 44, 44);
            shell.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            shell.setVisible(!shellsOnCooldown);

            shell.addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) {
                    if (shell.isVisible()) shell.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 34));
                }
                @Override public void mouseExited(MouseEvent e) {
                    if (shell.isVisible()) shell.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));
                }
                @Override public void mousePressed(MouseEvent e) {
                    if (shell.isVisible() && !shellsOnCooldown(shell))
                        moveCharacter(SHELL_POS[idx][0], SHELL_POS[idx][1]+5, () -> collectShell(shell));
                }
            });
            bg.add(shell);
            shellLabels.add(shell);
        }

        // ── Клік по фону ────────────────────────────────────────────────────
        bg.addMouseListener(new MouseAdapter() {
            @Override public void mousePressed(MouseEvent e) {
                if (e.getY() > 55 && e.getY() < 580) moveCharacter(e.getX()-25, e.getY()-30, null);
            }
        });

        // ── Таймер кулдауну мушель ─────────────────────────────────────────
        // Запускаємо таймер тільки якщо кулдаун реально активний
        shellCooldownTimer = new Timer(1000, e -> {
            long end = gameState.getShellCooldownEndTime();
            if (end == 0) return; // кулдаун не встановлений — нічого не робимо
            long d = end - System.currentTimeMillis();
            if (d > 0) {
                shellCooldownLeft = (int)(d / 1000);
                updateStats(true);
            } else {
                // Кулдаун закінчився — скидаємо ОДИН РАЗ
                gameState.setShellCooldownEndTime(0);
                shellCooldownLeft = 0;
                shellsCollected = 0;
                for (JLabel s : shellLabels) s.setVisible(true);
                updateStats(false);
                shellCooldownTimer.stop(); // зупиняємо щоб не спрацьовувало знову
                JOptionPane.showMessageDialog(BeachFrame.this,
                        "🐚 Мушлі знову з'явились на пляжі!",
                        "Оновлення", JOptionPane.INFORMATION_MESSAGE);
                shellCooldownTimer.start(); // відновлюємо для наступного разу
            }
        });
        shellCooldownTimer.start();
    }

    private boolean shellsOnCooldown(JLabel shell) {
        return gameState.getShellCooldownEndTime() > System.currentTimeMillis();
    }

    // ── Панель кнопок (стиль CafeFrame) ─────────────────────────────────────
    private JPanel buildButtonPanel() {
        JPanel p = new JPanel(null);
        p.setBackground(new Color(0, 30, 70, 200));

        JButton lemonBtn = makeBtn("🍋  Лимонад  (+15⚡, -20🪙)", BTN_BLUE);
        lemonBtn.setBounds(10, 10, 240, 38);
        lemonBtn.addActionListener(e -> moveCharacter(BAR_X, BAR_Y, () -> buyItem("lemonade")));
        p.add(lemonBtn);

        JButton iceBtn = makeBtn("🍦  Морозиво  (+10⚡, -25🪙)", BTN_BLUE);
        iceBtn.setBounds(260, 10, 240, 38);
        iceBtn.addActionListener(e -> moveCharacter(BAR_X, BAR_Y, () -> buyItem("icecream")));
        p.add(iceBtn);

        JButton fruitBtn = makeBtn("🍉  Фрукти  (+20⚡, -30🪙)", BTN_BLUE);
        fruitBtn.setBounds(510, 10, 230, 38);
        fruitBtn.addActionListener(e -> moveCharacter(BAR_X, BAR_Y, () -> buyItem("fruits")));
        p.add(fruitBtn);

        return p;
    }

    // ── Купівля ──────────────────────────────────────────────────────────────
    private void buyItem(String type) {
        int price, energy; String icon, name;
        switch (type) {
            case "lemonade": price=20; energy=15; icon="🍋"; name="Лимонад"; break;
            case "icecream":  price=25; energy=10; icon="🍦"; name="Морозиво"; break;
            default:          price=30; energy=20; icon="🍉"; name="Фрукти"; break;
        }
        if (!gameState.spendSilver(price)) {
            JOptionPane.showMessageDialog(this, "🪙 Недостатньо срібла! Потрібно " + price + ".",
                    "Нестача", JOptionPane.WARNING_MESSAGE); return;
        }
        gameState.addEnergy(energy);
        SaveManager.saveGame(gameState);
        updateStats(gameState.getShellCooldownEndTime() > System.currentTimeMillis());
        JOptionPane.showMessageDialog(this, icon + " " + name + "!\n-" + price + " 🪙  +" + energy + " ⚡");
    }

    private void collectShell(JLabel shell) {
        if (!shell.isVisible()) return;
        shell.setVisible(false);
        shellsCollected++;
        int silver = 8 + rnd.nextInt(8);
        gameState.addSilver(silver);
        gameState.addXp(10);
        boolean rare = rnd.nextInt(100) < 15;
        if (rare) gameState.addGold(1);
        SaveManager.saveGame(gameState);

        boolean onCooldown = gameState.getShellCooldownEndTime() > System.currentTimeMillis();
        updateStats(onCooldown);

        String msg = "🐚 Мушля знайдена!\n+" + silver + " 🪙  +10 XP";
        if (rare) msg += "\n🎉 Рідкісна мушля! +1 🌟";
        JOptionPane.showMessageDialog(this, msg, "Знахідка!", JOptionPane.INFORMATION_MESSAGE);

        if (shellsCollected >= TOTAL_SHELLS) {
            // Починаємо кулдаун 1 година
            long end = System.currentTimeMillis() + SHELL_COOLDOWN_MS;
            gameState.setShellCooldownEndTime(end);
            shellCooldownLeft = (int)(SHELL_COOLDOWN_MS / 1000);
            SaveManager.saveGame(gameState);
            JOptionPane.showMessageDialog(this, "🐚 Зібрано всі мушлі!\nНові з'являться через 1 годину.", "Мушлі зібрано", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    // ── Переміщення ──────────────────────────────────────────────────────────
    private void moveCharacter(int tx, int ty, Runnable onArrival) {
        if (movementTimer != null && movementTimer.isRunning()) movementTimer.stop();
        movementTimer = new Timer(15, null);
        movementTimer.addActionListener(e -> {
            int cx = characterLabel.getX(), cy = characterLabel.getY();
            int nx = approach(cx, tx, 4), ny = approach(cy, ty, 4);
            if (ny < 56) ny = 56;
            characterLabel.setLocation(nx, ny);
            if (nx == tx && ny == ty) { movementTimer.stop(); if (onArrival != null) onArrival.run(); }
        });
        movementTimer.start();
    }

    private int approach(int c, int t, int s) {
        if (Math.abs(c-t) <= s) return t;
        return c + (c < t ? s : -s);
    }

    private void updateStats(boolean onCooldown) {
        statsLabel.setText(String.format("⚡ %d/100  |  🪙 %d  |  🌟 %d  |  XP: %d  |  Мушель: %d/%d",
                gameState.getEnergy(), gameState.getSilver(), gameState.getGold(),
                gameState.getXp(), shellsCollected, TOTAL_SHELLS));

        if (onCooldown && shellCooldownLeft > 0) {
            int h = shellCooldownLeft / 3600;
            int m = (shellCooldownLeft % 3600) / 60;
            int s = shellCooldownLeft % 60;
            shellTimerLabel.setText(String.format("🕐 Мушлі оновляться через: %02d:%02d:%02d", h, m, s));
            shellTimerLabel.setForeground(new Color(255, 180, 80));
        } else {
            shellTimerLabel.setText("🐚 Мушлі доступні для збору!");
            shellTimerLabel.setForeground(new Color(120, 255, 120));
        }
    }

    private JButton makeBtn(String text, Color color) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? color.brighter() : color);
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

    private void goToMap() {
        if (movementTimer != null) movementTimer.stop();
        if (shellCooldownTimer != null) shellCooldownTimer.stop();
        SaveManager.saveGame(gameState);
        new MapFrame(gameState).setVisible(true);
        dispose();
    }

    static class BackgroundPanel extends JPanel {
        private Image img;
        BackgroundPanel(String path) {
            try { img = ImageIO.read(new File(path)); }
            catch (Exception e) { System.out.println("Фон не знайдено: " + path); }
        }
        @Override protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (img != null) g.drawImage(img, 0, 0, getWidth(), getHeight(), this);
            else { g.setColor(new Color(200,210,180)); g.fillRect(0,0,getWidth(),getHeight()); }
        }
    }
}
