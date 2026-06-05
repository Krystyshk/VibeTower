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
 * JanitorFrame — Робота двірником / прибирання у квартирі сусіда.
 * tutorialMode=true → режим навчання (6 об'єктів у квартирі сусіда).
 * tutorialMode=false → звичайна робота (10 об'єктів).
 */
public class JanitorFrame extends JFrame {

    private final GameState gameState;
    private final boolean tutorialMode;
    private final Random rnd = new Random();

    private JLabel statsLabel;
    private JLabel characterLabel;
    private Timer movementTimer;

    private int cleaned = 0;
    private final List<JLabel> trashLabels = new ArrayList<>();
    private final int TOTAL;

    private static final String[] TRASH_ICONS = {"📄","🍬","🍂","🥤","🛍","🍫","🧻","🗑"};

    private static final int[][] POSITIONS_REGULAR = {
            {150,200},{280,280},{400,250},{520,310},{200,380},{350,420},
            {480,380},{150,450},{600,260},{300,180}
    };
    private static final int[][] POSITIONS_TUTORIAL = {
            {160,210},{290,290},{410,260},{530,320},{210,390},{360,430}
    };

    public JanitorFrame(GameState gameState, boolean tutorialMode) {
        this.gameState   = gameState;
        this.tutorialMode = tutorialMode;
        this.TOTAL = tutorialMode ? 6 : 10;

        String bgPath = tutorialMode
                ? "src/main/resources/komnata.jpg"
                : "src/main/resources/garden.png";   // двір/парк як фон роботи двірника
        String title = tutorialMode
                ? "VibeTower — Прибирання у сусідки 🧹"
                : "VibeTower — Робота двірником 🧹";

        setTitle(title);
        setSize(800, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        BackgroundPanel bg = new BackgroundPanel(bgPath);
        bg.setLayout(null);
        setContentPane(bg);

        // ── Топ-панель ─────────────────────────────────────────────────────
        JPanel topPanel = new JPanel();
        topPanel.setBounds(0, 0, 800, 50);
        topPanel.setBackground(new Color(40, 60, 30, 220));
        topPanel.setLayout(null);
        bg.add(topPanel);

        statsLabel = new JLabel();
        statsLabel.setForeground(Color.WHITE);
        statsLabel.setFont(new Font("Arial", Font.BOLD, 14));
        statsLabel.setBounds(10, 12, 540, 26);
        topPanel.add(statsLabel);
        updateStats();

        JButton backBtn = new JButton("↩ На карту");
        backBtn.setBounds(670, 10, 110, 30);
        backBtn.setFocusable(false);
        backBtn.addActionListener(e -> goToMap());
        topPanel.add(backBtn);

        // ── NPC-підказка ───────────────────────────────────────────────────
        String npcText = tutorialMode
                ? "<html><i>Сусідка: «Клікай на кожен предмет сміття, щоб прибрати!»</i></html>"
                : "<html><i>«Прибирай сміття — клікай на кожен об'єкт. Стежи за енергією!»</i></html>";
        JLabel hint = new JLabel(npcText, SwingConstants.CENTER);
        hint.setBounds(80, 605, 640, 28);
        hint.setForeground(new Color(180, 255, 160));
        hint.setFont(new Font("Arial", Font.ITALIC, 12));
        bg.add(hint);

        // ── Персонаж ────────────────────────────────────────────────────────
        characterLabel = new JLabel("👤", SwingConstants.CENTER);
        characterLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 35));
        characterLabel.setBounds(370, 490, 50, 60);
        bg.add(characterLabel);

        // ── Розкидати сміття ────────────────────────────────────────────────
        int[][] positions = tutorialMode ? POSITIONS_TUTORIAL : POSITIONS_REGULAR;
        for (int i = 0; i < TOTAL; i++) {
            final int idx = i;
            String icon = TRASH_ICONS[rnd.nextInt(TRASH_ICONS.length)];
            JLabel trash = new JLabel(icon, SwingConstants.CENTER);
            trash.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 30));
            trash.setBounds(positions[i][0], positions[i][1], 44, 44);
            trash.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            trash.setToolTipText("Клікни, щоб прибрати (-4⚡  +8XP  +6-10🪙)");
            trash.addMouseListener(new MouseAdapter() {
                @Override public void mousePressed(MouseEvent e) {
                    handleClean(trash);
                }
            });
            bg.add(trash);
            trashLabels.add(trash);
        }

        // ── Клік по фону = рух ───────────────────────────────────────────────
        bg.addMouseListener(new MouseAdapter() {
            @Override public void mousePressed(MouseEvent e) {
                if (e.getY() > 55) moveCharacter(e.getX() - 25, e.getY() - 30, null);
            }
        });
    }

    // ── Логіка прибирання ─────────────────────────────────────────────────
    private void handleClean(JLabel trash) {
        if (!trash.isVisible()) return;

        if (gameState.getEnergy() < 4) {
            JOptionPane.showMessageDialog(this,
                    "⚡ Недостатньо енергії!\nКупи напій або відпочинь.", "Втома", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Рухаємось до сміття, потім прибираємо
        int targetX = trash.getX();
        int targetY = trash.getY();
        moveCharacter(targetX - 10, targetY + 10, () -> {
            if (!trash.isVisible()) return;
            trash.setVisible(false);
            cleaned++;

            gameState.spendEnergy(4);
            int silver = 6 + rnd.nextInt(5);
            gameState.addSilver(silver);
            gameState.addXp(8);

            // 5% шанс знайти рідкісний предмет
            boolean rare = rnd.nextInt(100) < 5;
            if (rare) {
                gameState.addGold(1);
                gameState.addXp(15);
            }

            updateStats();
            SaveManager.saveGame(gameState); // Bug fix: зберігаємо після кожного прибирання

            String msg = "Прибрано! +" + silver + " 🪙  +8 XP  -4 ⚡";
            if (rare) msg += "\n🎉 Рідкісна знахідка! +1 золото  +15 XP";
            JOptionPane.showMessageDialog(this, msg, "Прибрано!", JOptionPane.INFORMATION_MESSAGE);

            updateStats();
            checkAllCleaned();
        });
    }

    private void checkAllCleaned() {
        if (cleaned >= TOTAL) {
            if (tutorialMode) {
                gameState.addSilver(50);
                gameState.addXp(60);
                SaveManager.saveGame(gameState);
                JOptionPane.showMessageDialog(this,
                        "🎉 Чудово! Прибрано " + TOTAL + " предметів.\n+50 🪙  +60 XP\nНавчальне завдання виконано!",
                        "Навчання завершено!", JOptionPane.INFORMATION_MESSAGE);
                new MapFrame(gameState).setVisible(true);
                dispose();
            } else {
                int bonus = 80 + rnd.nextInt(41);
                gameState.addSilver(bonus);
                gameState.addXp(80);
                SaveManager.saveGame(gameState);
                int opt = JOptionPane.showConfirmDialog(this,
                        "Роботу завершено! Бонус: +" + bonus + " 🪙  +80 XP\n\nПрацювати ще?",
                        "Зміна завершена", JOptionPane.YES_NO_OPTION);
                if (opt == JOptionPane.YES_OPTION) {
                    dispose();
                    new JanitorFrame(gameState, false).setVisible(true);
                } else {
                    goToMap();
                }
            }
        }
    }

    // ── Переміщення персонажа ─────────────────────────────────────────────
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
    private void updateStats() {
        statsLabel.setText(String.format(
                "⚡ %d/100  |  🪙 %d  |  🌟 %d  |  XP: %d  |  Прибрано: %d/%d",
                gameState.getEnergy(), gameState.getSilver(), gameState.getGold(),
                gameState.getXp(), cleaned, TOTAL
        ));
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
            else { g.setColor(new Color(40,60,30)); g.fillRect(0,0,getWidth(),getHeight()); }
        }
    }
}
