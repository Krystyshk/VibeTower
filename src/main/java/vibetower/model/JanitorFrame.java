package vibetower.model;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.swing.Timer;

/**
 * JanitorFrame — Робота двірником.
 * При прибиранні мусору анімовано випадає ресурс (монетки, зірочки).
 * tutorialMode=true → 6 об'єктів у квартирі сусіда (навчання).
 * tutorialMode=false → 10 об'єктів (звичайна робота).
 */
public class JanitorFrame extends JFrame {

    private final GameState gameState;
    private final boolean tutorialMode;
    private final Random rnd = new Random();

    private JLabel statsLabel;
    private JLabel characterLabel;
    private Timer movementTimer;

    private int cleaned = 0;
    private final List<JLabel> trashList = new ArrayList<>();
    private final int TOTAL;

    // Відображення ресурсів, що випадають (анімація)
    private final List<DropLabel> activeDrops = new ArrayList<>();
    private Timer dropAnimTimer;

    private static final String[] TRASH_ICONS = {"📄","🍬","🍂","🥤","🛍","🍫","🧻","🗑","🍃","🎃"};

    private static final int[][] POS_REGULAR  = {
            {150,190},{280,270},{405,240},{525,305},{200,375},
            {350,415},{480,375},{145,450},{600,255},{295,180}
    };
    private static final int[][] POS_TUTORIAL = {
            {165,205},{295,285},{415,255},{535,315},{215,385},{360,425}
    };

    // Кольори
    private static final Color TOP_BG   = new Color(30, 50, 20, 220);
    private static final Color HINT_BG  = new Color(20, 40, 10, 200);

    public JanitorFrame(GameState gameState, boolean tutorialMode) {
        this.gameState    = gameState;
        this.tutorialMode = tutorialMode;
        this.TOTAL        = tutorialMode ? 6 : 10;

        String bgPath = tutorialMode ? "src/main/resources/komnata.jpg"
                : "src/main/resources/garden.png";

        setTitle(tutorialMode ? "VibeTower — Прибирання у сусідки 🧹"
                : "VibeTower — Двірник 🧹");
        setSize(800, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        BackgroundPanel bg = new BackgroundPanel(bgPath);
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
        statsLabel.setBounds(10, 5, 560, 20);
        top.add(statsLabel);

        // Шкала прогресу прибирання
        JLabel progLabel = new JLabel("", SwingConstants.LEFT);
        progLabel.setForeground(new Color(160, 230, 120));
        progLabel.setFont(new Font("Arial", Font.BOLD, 12));
        progLabel.setBounds(10, 28, 450, 18);
        top.add(progLabel);
        // Зберігаємо посилання для оновлення
        this.progressBarLabel = progLabel;

        JButton backBtn = makeTopBtn("↩ Карта", new Color(70,90,120));
        backBtn.setBounds(672, 10, 110, 34);
        backBtn.addActionListener(e -> goToMap());
        top.add(backBtn);

        updateStats();

        // ── NPC підказка ────────────────────────────────────────────────────
        String npcText = tutorialMode
                ? "👩 Сусідка: «Клікай на кожен предмет сміття — персонаж підійде і прибере!»"
                : "🧹 Клікай на сміття щоб прибрати. За кожен предмет отримаєш ресурси!";
        JLabel hint = new JLabel(npcText, SwingConstants.CENTER);
        hint.setBounds(10, 548, 780, 26);
        hint.setForeground(new Color(200, 255, 180));
        hint.setFont(new Font("Arial", Font.ITALIC, 12));
        hint.setOpaque(true);
        hint.setBackground(HINT_BG);
        bg.add(hint);

        // ── Персонаж ────────────────────────────────────────────────────────
        characterLabel = new JLabel("🧑", SwingConstants.CENTER);
        characterLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 36));
        characterLabel.setBounds(365, 420, 50, 58);
        bg.add(characterLabel);

        // ── Сміття ──────────────────────────────────────────────────────────
        int[][] positions = tutorialMode ? POS_TUTORIAL : POS_REGULAR;
        for (int i = 0; i < TOTAL; i++) {
            final int idx = i;
            String icon = TRASH_ICONS[rnd.nextInt(TRASH_ICONS.length)];
            JLabel trash = new JLabel(icon, SwingConstants.CENTER);
            trash.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));
            trash.setBounds(positions[i][0], positions[i][1], 44, 44);
            trash.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            trash.setToolTipText("Клікни — прибрати (-4⚡, +монети, +XP)");

            // Підсвітка при наведенні
            trash.addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) {
                    if (trash.isVisible()) trash.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 34));
                }
                @Override public void mouseExited(MouseEvent e) {
                    if (trash.isVisible()) trash.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));
                }
                @Override public void mousePressed(MouseEvent e) {
                    if (trash.isVisible()) handleClean(trash, positions[idx]);
                }
            });

            bg.add(trash);
            trashList.add(trash);
        }

        // ── Клік по фону ────────────────────────────────────────────────────
        bg.addMouseListener(new MouseAdapter() {
            @Override public void mousePressed(MouseEvent e) {
                if (e.getY() > 55) moveCharacter(e.getX()-25, e.getY()-30, null);
            }
        });

        // ── Таймер анімації ресурсів ────────────────────────────────────────
        dropAnimTimer = new Timer(30, e -> animateDrops(bg));
        dropAnimTimer.start();
    }

    private JLabel progressBarLabel;

    // ════════════════════════════════════════════════════════════════════════
    //  ПРИБИРАННЯ
    // ════════════════════════════════════════════════════════════════════════
    private void handleClean(JLabel trash, int[] pos) {
        if (!trash.isVisible()) return;
        if (gameState.getEnergy() < 4) {
            JOptionPane.showMessageDialog(this,
                    "⚡ Недостатньо енергії!\nПотрібно 4 ⚡. Купи напій або відпочинь.",
                    "Втома", JOptionPane.WARNING_MESSAGE);
            return;
        }
        // Рухаємось і прибираємо
        moveCharacter(pos[0]-10, pos[1]+8, () -> {
            if (!trash.isVisible()) return;
            trash.setVisible(false);
            cleaned++;

            gameState.spendEnergy(4);
            int silver = 6 + rnd.nextInt(5);
            gameState.addSilver(silver);
            gameState.addXp(8);

            boolean rare = rnd.nextInt(100) < 5;
            if (rare) { gameState.addGold(1); gameState.addXp(15); }

            SaveManager.saveGame(gameState);
            updateStats();

            // ── Анімація ресурсів що випадають ─────────────────────────────
            spawnDrop("+" + silver + " 🪙", pos[0], pos[1], new Color(255, 210, 50));
            spawnDrop("+8 XP",              pos[0]+30, pos[1]-10, new Color(120, 200, 255));
            if (rare) {
                spawnDrop("+1 🌟 РІДКІСТЬ!", pos[0]-20, pos[1]-20, new Color(255, 180, 255));
            }

            // Повідомлення тільки при рідкісному дропі
            if (rare) {
                JOptionPane.showMessageDialog(this,
                        "🎉 Знайдено рідкісний ресурс!\n+" + silver + " 🪙  +8 XP  +1 🌟  +15 XP",
                        "Рідкісна знахідка!", JOptionPane.INFORMATION_MESSAGE);
            }

            checkAllCleaned();
        });
    }

    // ── Спавн текстового дропу ──────────────────────────────────────────────
    private void spawnDrop(String text, int x, int y, Color color) {
        JPanel bg = (JPanel) getContentPane();
        DropLabel drop = new DropLabel(text, x, y, color);
        drop.setBounds(x, y, 150, 28);
        bg.add(drop);
        bg.setComponentZOrder(drop, 0);
        activeDrops.add(drop);
        bg.revalidate();
        bg.repaint();
    }

    private void animateDrops(JPanel bg) {
        List<DropLabel> toRemove = new ArrayList<>();
        for (DropLabel d : activeDrops) {
            d.tick();
            if (d.isDone()) { bg.remove(d); toRemove.add(d); }
        }
        activeDrops.removeAll(toRemove);
        if (!toRemove.isEmpty()) { bg.revalidate(); bg.repaint(); }
    }

    // ════════════════════════════════════════════════════════════════════════
    //  ЗАВЕРШЕННЯ
    // ════════════════════════════════════════════════════════════════════════
    private void checkAllCleaned() {
        if (cleaned < TOTAL) return;
        if (dropAnimTimer != null) dropAnimTimer.stop();

        if (tutorialMode) {
            gameState.addSilver(50); gameState.addXp(60);
            SaveManager.saveGame(gameState);
            JOptionPane.showMessageDialog(this,
                    "🎉 Чудово! Прибрано " + TOTAL + " предметів.\n+50 🪙  +60 XP\nНавчальне завдання виконано!",
                    "Навчання завершено!", JOptionPane.INFORMATION_MESSAGE);
            new MapFrame(gameState).setVisible(true);
            dispose();
        } else {
            int bonus = 80 + rnd.nextInt(41);
            gameState.addSilver(bonus); gameState.addXp(80);
            SaveManager.saveGame(gameState);
            int opt = JOptionPane.showConfirmDialog(this,
                    "✅ Роботу завершено!\nБонус: +" + bonus + " 🪙  +80 XP\n\nПрацювати ще одну зміну?",
                    "Зміна завершена", JOptionPane.YES_NO_OPTION);
            if (opt == JOptionPane.YES_OPTION) {
                dispose();
                new JanitorFrame(gameState, false).setVisible(true);
            } else goToMap();
        }
    }

    // ════════════════════════════════════════════════════════════════════════
    //  ПЕРЕМІЩЕННЯ
    // ════════════════════════════════════════════════════════════════════════
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

    // ════════════════════════════════════════════════════════════════════════
    //  СТАТИСТИКА
    // ════════════════════════════════════════════════════════════════════════
    private void updateStats() {
        statsLabel.setText(String.format("⚡%d/100  🪙%d  🌟%d  XP:%d",
                gameState.getEnergy(), gameState.getSilver(), gameState.getGold(), gameState.getXp()));
        // Шкала прогресу
        int bars = TOTAL > 0 ? cleaned * 20 / TOTAL : 0;
        StringBuilder bar = new StringBuilder("🧹 [");
        for (int i = 0; i < 20; i++) bar.append(i < bars ? "█" : "░");
        bar.append("]  ").append(cleaned).append("/").append(TOTAL);
        progressBarLabel.setText(bar.toString());
    }

    private JButton makeTopBtn(String text, Color color) {
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
        btn.setFont(new Font("Arial", Font.BOLD, 12));
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void goToMap() {
        if (movementTimer != null) movementTimer.stop();
        if (dropAnimTimer != null) dropAnimTimer.stop();
        SaveManager.saveGame(gameState);
        new MapFrame(gameState).setVisible(true);
        dispose();
    }

    // ════════════════════════════════════════════════════════════════════════
    //  КЛАС: Анімований ресурс що падає вгору
    // ════════════════════════════════════════════════════════════════════════
    static class DropLabel extends JLabel {
        private int alpha = 255;
        private float floatY;
        private Color baseColor;

        DropLabel(String text, int x, int y, Color color) {
            super(text, SwingConstants.LEFT);
            this.floatY = y;
            this.baseColor = color;
            setFont(new Font("Arial", Font.BOLD, 14));
            setOpaque(false);
        }

        void tick() {
            floatY -= 1.5f;
            alpha  -= 5;
            if (alpha < 0) alpha = 0;
            setLocation(getX(), (int) floatY);
            repaint();
        }

        boolean isDone() { return alpha <= 0; }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            // Тінь
            g2.setColor(new Color(0, 0, 0, Math.min(alpha, 180)));
            g2.setFont(getFont());
            g2.drawString(getText(), 2, getFont().getSize() + 1);
            // Основний текст
            g2.setColor(new Color(baseColor.getRed(), baseColor.getGreen(), baseColor.getBlue(), Math.max(0, Math.min(255, alpha))));
            g2.drawString(getText(), 0, getFont().getSize());
            g2.dispose();
        }
    }

    // ── Фон ─────────────────────────────────────────────────────────────────
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
