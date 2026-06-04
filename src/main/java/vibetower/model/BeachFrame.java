package vibetower.model;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * BeachFrame — Локація «Пляж» (з 4 рівня).
 * Інтерактиви: лимонад, морозиво, фрукти, збір мушель, пляжний м'яч.
 */
public class BeachFrame extends JFrame {

    private final GameState gameState;
    private final Random rnd = new Random();

    private JLabel statsLabel;
    private JLabel characterLabel;
    private Timer movementTimer;

    private int shellsCollected = 0;
    private static final int TOTAL_SHELLS = 5;
    private final List<JLabel> shellLabels = new ArrayList<>();

    // Координати кіосків
    private static final int BAR_X  = 680, BAR_Y  = 160;
    private static final int BALL_X = 330, BALL_Y = 400;

    // Позиції мушель
    private static final int[][] SHELL_POS = {
            {120,440},{220,480},{340,500},{460,460},{580,490}
    };

    public BeachFrame(GameState gameState) {
        this.gameState = gameState;

        setTitle("VibeTower — Пляж 🏖");
        setSize(800, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        BackgroundPanel bg = new BackgroundPanel("src/main/resources/beach.png");
        bg.setLayout(null);
        setContentPane(bg);

        // ── Топ-панель ─────────────────────────────────────────────────────
        JPanel top = new JPanel(null);
        top.setBounds(0, 0, 800, 50);
        top.setBackground(new Color(20, 80, 140, 220));
        bg.add(top);

        statsLabel = new JLabel();
        statsLabel.setForeground(Color.WHITE);
        statsLabel.setFont(new Font("Arial", Font.BOLD, 14));
        statsLabel.setBounds(10, 12, 550, 26);
        top.add(statsLabel);
        updateStats();

        JButton backBtn = new JButton("↩ На карту");
        backBtn.setBounds(670, 10, 110, 30);
        backBtn.setFocusable(false);
        backBtn.addActionListener(e -> goToMap());
        top.add(backBtn);

        // ── Персонаж ─────────────────────────────────────────────────────────
        characterLabel = new JLabel("👤", SwingConstants.CENTER);
        characterLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 35));
        characterLabel.setBounds(340, 350, 50, 60);
        bg.add(characterLabel);

        // ── Кнопки кіосків ─────────────────────────────────────────────────
        JButton lemonBtn = makeBtn("🍋 Лимонад (+15⚡, -20🪙)", 30, 565, 230, 34);
        lemonBtn.addActionListener(e -> moveCharacter(BAR_X, BAR_Y, () -> buyItem("lemonade")));
        bg.add(lemonBtn);

        JButton iceBtn = makeBtn("🍦 Морозиво (+10⚡, -25🪙)", 275, 565, 230, 34);
        iceBtn.addActionListener(e -> moveCharacter(BAR_X, BAR_Y, () -> buyItem("icecream")));
        bg.add(iceBtn);

        JButton fruitBtn = makeBtn("🍉 Фрукти (+20⚡, -30🪙)", 520, 565, 250, 34);
        fruitBtn.addActionListener(e -> moveCharacter(BAR_X, BAR_Y, () -> buyItem("fruits")));
        bg.add(fruitBtn);

        JButton ballBtn = makeBtn("🏐 Пляжний м'яч (-5⚡, +15XP)", 270, 610, 260, 34);
        ballBtn.addActionListener(e -> moveCharacter(BALL_X, BALL_Y, () -> playBall()));
        bg.add(ballBtn);

        // ── Мушлі ───────────────────────────────────────────────────────────
        for (int i = 0; i < TOTAL_SHELLS; i++) {
            final int idx = i;
            JLabel shell = new JLabel("🐚", SwingConstants.CENTER);
            shell.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));
            shell.setBounds(SHELL_POS[i][0], SHELL_POS[i][1], 40, 40);
            shell.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            shell.setToolTipText("Зібрати мушлю (+XP, +🪙, шанс 🌟)");
            shell.addMouseListener(new MouseAdapter() {
                @Override public void mousePressed(MouseEvent e) {
                    if (!shell.isVisible()) return;
                    moveCharacter(SHELL_POS[idx][0], SHELL_POS[idx][1] + 5, () -> collectShell(shell));
                }
            });
            bg.add(shell);
            shellLabels.add(shell);
        }

        // ── NPC-підказка ─────────────────────────────────────────────────────
        JLabel hint = new JLabel(
                "<html><i>Відпочивальник: «Збирай мушлі на піску або купи напій у барі!»</i></html>",
                SwingConstants.CENTER);
        hint.setBounds(80, 645, 640, 22);
        hint.setForeground(new Color(255, 245, 200));
        hint.setFont(new Font("Arial", Font.ITALIC, 12));
        bg.add(hint);

        // ── Клік по фону ─────────────────────────────────────────────────────
        bg.addMouseListener(new MouseAdapter() {
            @Override public void mousePressed(MouseEvent e) {
                if (e.getY() > 55) moveCharacter(e.getX() - 25, e.getY() - 30, null);
            }
        });
    }

    // ── Інтерактиви ───────────────────────────────────────────────────────
    private void buyItem(String type) {
        int price, energy;
        String icon, name;
        switch (type) {
            case "lemonade": price=20; energy=15; icon="🍋"; name="Лимонад"; break;
            case "icecream":  price=25; energy=10; icon="🍦"; name="Морозиво"; break;
            default:          price=30; energy=20; icon="🍉"; name="Фрукти"; break;
        }
        if (!gameState.spendSilver(price)) { showWarn("🪙 Недостатньо срібла! Потрібно " + price + "."); return; }
        gameState.addEnergy(energy);
        SaveManager.saveGame(gameState);
        updateStats();
        JOptionPane.showMessageDialog(this, icon + " " + name + "!\n-" + price + " 🪙  +" + energy + " ⚡");
    }

    private void playBall() {
        if (gameState.getEnergy() < 5) { showWarn("⚡ Замало енергії! Потрібно 5."); return; }
        gameState.spendEnergy(5);
        gameState.addXp(15);
        SaveManager.saveGame(gameState);
        updateStats();
        JOptionPane.showMessageDialog(this, "🏐 Весело пограли!\n-5 ⚡  +15 XP");
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
        updateStats();
        String msg = "🐚 Мушля знайдена!\n+" + silver + " 🪙  +10 XP";
        if (rare) msg += "\n🎉 Рідкісна мушля! +1 🌟";
        JOptionPane.showMessageDialog(this, msg, "Знахідка!", JOptionPane.INFORMATION_MESSAGE);
        if (shellsCollected >= TOTAL_SHELLS)
            JOptionPane.showMessageDialog(this, "🎉 Зібрано всі мушлі на пляжі! Молодець!");
    }

    // ── Переміщення ──────────────────────────────────────────────────────
    private void moveCharacter(int tx, int ty, Runnable onArrival) {
        if (movementTimer != null && movementTimer.isRunning()) movementTimer.stop();
        movementTimer = new Timer(15, null);
        movementTimer.addActionListener(e -> {
            int cx = characterLabel.getX(), cy = characterLabel.getY();
            int nx = approach(cx, tx, 4);
            int ny = approach(cy, ty, 4);
            characterLabel.setLocation(nx, ny);
            if (nx == tx && ny == ty) {
                movementTimer.stop();
                if (onArrival != null) onArrival.run();
            }
        });
        movementTimer.start();
    }

    private int approach(int c, int t, int s) {
        if (Math.abs(c - t) <= s) return t;
        return c + (c < t ? s : -s);
    }

    private JButton makeBtn(String text, int x, int y, int w, int h) {
        JButton b = new JButton(text);
        b.setBounds(x, y, w, h);
        b.setBackground(new Color(20, 100, 180));
        b.setForeground(Color.WHITE);
        b.setFont(new Font("Arial", Font.BOLD, 12));
        b.setFocusable(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    private void updateStats() {
        statsLabel.setText(String.format(
                "⚡ %d/100  |  🪙 %d  |  🌟 %d  |  XP: %d  |  Мушель: %d/%d",
                gameState.getEnergy(), gameState.getSilver(), gameState.getGold(),
                gameState.getXp(), shellsCollected, TOTAL_SHELLS
        ));
    }

    private void showWarn(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Увага", JOptionPane.WARNING_MESSAGE);
    }

    private void goToMap() {
        if (movementTimer != null) movementTimer.stop();
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
            else { g.setColor(new Color(255,210,120)); g.fillRect(0,0,getWidth(),getHeight()); }
        }
    }
}
