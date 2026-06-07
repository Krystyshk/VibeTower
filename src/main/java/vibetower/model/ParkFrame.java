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
 * ParkFrame — Локація «Парк» (з 4 рівня).
 * Інтерактиви: сік, солодка вата (випадковий колір), допомога NPC, фото.
 */
public class ParkFrame extends JFrame {

    private final GameState gameState;
    private final Random rnd = new Random();

    private JLabel statsLabel;
    private JLabel characterLabel;
    private Timer movementTimer;

    // Координати точок інтерактивів
    private static final int JUICE_X   = 70,  JUICE_Y   = 320;
    private static final int CANDY_X   = 630, CANDY_Y   = 270;
    private static final int NPC_X     = 340, NPC_Y     = 170;
    private static final int PHOTO_X   = 400, PHOTO_Y   = 300;

    public ParkFrame(GameState gameState) {
        this.gameState = gameState;

        setTitle("VibeTower — Парк 🌳");
        setSize(800, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        BackgroundPanel bg = new BackgroundPanel("src/main/resources/park.png");
        bg.setLayout(null);
        setContentPane(bg);

        // ── Топ-панель ─────────────────────────────────────────────────────
        JPanel top = new JPanel(null);
        top.setBounds(0, 0, 800, 50);
        top.setBackground(new Color(30, 80, 30, 220));
        bg.add(top);

        statsLabel = new JLabel();
        statsLabel.setForeground(Color.WHITE);
        statsLabel.setFont(new Font("Arial", Font.BOLD, 14));
        statsLabel.setBounds(10, 12, 540, 26);
        top.add(statsLabel);
        updateStats();

        JButton backBtn = new JButton("↩ На карту");
        backBtn.setBounds(670, 10, 110, 30);
        backBtn.setFocusable(false);
        backBtn.addActionListener(e -> goToMap());
        top.add(backBtn);

        // ── Персонаж ────────────────────────────────────────────────────────
        characterLabel = new JLabel("👤", SwingConstants.CENTER);
        characterLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 35));
        characterLabel.setBounds(370, 420, 50, 60);
        bg.add(characterLabel);

        // ── Кнопки-інтерактиви ─────────────────────────────────────────────
        // 1. Сік
        JButton juiceBtn = makeBtn("🧃 Сік  (+10⚡, -20🪙)", JUICE_X - 10, 490, 220, 34);
        juiceBtn.addActionListener(e -> moveCharacter(JUICE_X, JUICE_Y, () -> buyJuice()));
        bg.add(juiceBtn);

        // 2. Солодка вата
        JButton candyBtn = makeBtn("🍭 Солодка вата  (-30🪙, сюрприз!)", 350, 490, 280, 34);
        candyBtn.addActionListener(e -> moveCharacter(CANDY_X, CANDY_Y, () -> buyCottonCandy(candyBtn)));
        bg.add(candyBtn);

        // 3. Допомогти NPC (одноразово)
        JButton npcBtn = makeBtn("🧑 Допомогти NPC  (+30-50🪙, +30XP)", 100, 530, 280, 34);
        npcBtn.addActionListener(e -> moveCharacter(NPC_X, NPC_Y, () -> helpNpc(npcBtn)));
        bg.add(npcBtn);

        // 4. Фото біля фонтану
        JButton photoBtn = makeBtn("📸 Фото  (+5XP)", 430, 530, 180, 34);
        photoBtn.addActionListener(e -> moveCharacter(PHOTO_X, PHOTO_Y, () -> takePhoto()));
        bg.add(photoBtn);

        // ── NPC-підказка ─────────────────────────────────────────────────────
        JLabel hint = new JLabel(
                "<html><i>Перехожий: «Спробуй купити сік або солодку вату — кожен раз сюрприз!»</i></html>",
                SwingConstants.CENTER);
        hint.setBounds(80, 570, 640, 22);
        hint.setForeground(new Color(200, 240, 200));
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
    private void buyJuice() {
        if (!gameState.spendSilver(20)) { showWarn("🪙 Недостатньо срібла! Потрібно 20."); return; }
        gameState.addEnergy(10);
        SaveManager.saveGame(gameState);
        updateStats();
        JOptionPane.showMessageDialog(this, "🧃 Смачний сік!\n-20 🪙  +10 ⚡");
    }

    private void buyCottonCandy(JButton btn) {
        if (gameState.isCottonCandyDone()) {
            JOptionPane.showMessageDialog(this, "Солодка вата вже куплена сьогодні!"); return;
        }
        if (!gameState.spendSilver(30)) { showWarn("🪙 Недостатньо срібла! Потрібно 30."); return; }

        int roll = rnd.nextInt(100);
        String result;
        if (roll < 70) {
            gameState.addEnergy(8);
            result = "🍭 Рожева вата!\n-30 🪙  +8 ⚡";
        } else if (roll < 90) {
            gameState.addEnergy(12);
            gameState.addXp(10);
            result = "🍭 Блакитна незвичайна вата!\n-30 🪙  +12 ⚡  +10 XP";
        } else {
            gameState.addGold(1);
            result = "🌈 РІДКІСНА РАЙДУЖНА ВАТА!\n-30 🪙  +1 🌟\n🎉 Вітаємо! Вам пощастило!";
        }

        gameState.setCottonCandyDone();
        btn.setText("✅ Вата куплена");
        btn.setEnabled(false);
        SaveManager.saveGame(gameState);
        updateStats();
        JOptionPane.showMessageDialog(this, result, "Солодка вата!", JOptionPane.INFORMATION_MESSAGE);
    }

    private void helpNpc(JButton btn) {
        if (gameState.isNpcQuestDone()) {
            JOptionPane.showMessageDialog(this, "NPC: «Дякую! Ти вже допоміг(ла) мені.»"); return;
        }
        String[] opts = {"🌿 Шукати у кущах", "🪑 Шукати біля лавки", "⛲ Шукати біля фонтану"};
        int choice = JOptionPane.showOptionDialog(this,
                "NPC: «Я загубив(ла) ключі в парку! Де шукати?»",
                "Завдання NPC", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
                null, opts, opts[0]);
        if (choice == 1) {  // правильна відповідь
            int reward = 30 + rnd.nextInt(21);
            gameState.addSilver(reward);
            gameState.addXp(30);
            gameState.setNpcQuestDone();
            btn.setText("✅ Завдання виконано");
            btn.setEnabled(false);
            SaveManager.saveGame(gameState);
            updateStats();
            JOptionPane.showMessageDialog(this, "NPC: «Знайшов(ла)! Дякую!»\n+" + reward + " 🪙  +30 XP");
        } else if (choice >= 0) {
            JOptionPane.showMessageDialog(this, "NPC: «Тут немає... Спробуй деінде.»");
        }
    }

    private void takePhoto() {
        gameState.addXp(5);
        SaveManager.saveGame(gameState);
        updateStats();
        JOptionPane.showMessageDialog(this, "📸 Гарне фото на пам'ять!\n+5 XP");
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

    // ── Допоміжні ─────────────────────────────────────────────────────────
    private JButton makeBtn(String text, int x, int y, int w, int h) {
        JButton b = new JButton(text);
        b.setBounds(x, y, w, h);
        b.setBackground(new Color(34, 120, 50));
        b.setForeground(Color.WHITE);
        b.setFont(new Font("Arial", Font.BOLD, 12));
        b.setFocusable(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    private void updateStats() {
        statsLabel.setText(String.format(
                "⚡ %d/100  |  🪙 %d  |  🌟 %d  |  XP: %d",
                gameState.getEnergy(), gameState.getSilver(), gameState.getGold(), gameState.getXp()
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
            else { g.setColor(new Color(30,80,30)); g.fillRect(0,0,getWidth(),getHeight()); }
        }
    }
}

