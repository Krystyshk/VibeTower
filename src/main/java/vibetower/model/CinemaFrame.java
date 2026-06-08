package vibetower.model;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.Random;

/**
 * CinemaFrame — Кінотеатр (з 5 рівня).
 * workMode=false → тільки перегляд фільмів (з MapFrame "Місця").
 * workMode=true  → тільки робота (з MapFrame "Робота"), 2 дії кожного виду, потім завершити зміну.
 */
public class CinemaFrame extends JFrame {

    private final GameState gameState;
    private final boolean workMode;
    private final Random rnd = new Random();

    private JLabel statsLabel;
    private JLabel characterLabel;
    private Timer movementTimer;

    // Лічильники роботи (макс 2 кожного)
    private int ticketsSold = 0;

    // Кулдаун після зміни (30 хвилин)
    private static final int CINEMA_COOLDOWN_SECS = 30 * 60;
    private Timer cinemaCooldownTimer;
    private int   cinemaCooldownLeft = 0;
    private boolean cinemaCooldownActive = false;
    private int popcornGiven = 0;
    private int hallCleaned = 0;
    private int seatsHelped = 0;
    private static final int MAX_PER_ACTION = 2;

    private JButton ticketBtn, popcornBtn, cleanBtn, seatBtn, finishBtn;
    private JLabel workProgressLabel;

    private static final String[][] MOVIES = {
            {"🎭 Пригоди у місті",  "Весела комедія про міське життя та дружбу."},
            {"🚀 Зорі та мрії",     "Фантастика про космічні подорожі."},
            {"🌸 Весна назавжди",   "Романтична драма про першу любов."},
            {"🐉 Легенда дракона",  "Фентезі з магією та пригодами."}
    };
    private static final String[][] QUIZ = {
            {"Де відбуваються пригоди?",     "У місті",  "В лісі",  "На морі",  "0"},
            {"Що знаходить герой у космосі?","Зорю",     "Планету", "Ракету",   "0"},
            {"Яка пора року у фільмі?",      "Весна",    "Зима",    "Осінь",    "0"},
            {"Яка головна істота легенди?",  "Дракон",   "Фенікс",  "Єдиноріг", "0"}
    };

    private static final Color TOP_BG      = new Color(18, 10, 42, 220);
    private static final Color BTN_PURPLE  = new Color(80, 40, 140);
    private static final Color BTN_GOLD    = new Color(160, 100, 20);
    private static final Color BTN_TEAL    = new Color(20, 110, 130);
    private static final Color BTN_RED     = new Color(160, 30, 30);
    private static final Color BTN_GREEN   = new Color(30, 130, 60);
    private static final Color BTN_GRAY    = new Color(60, 60, 80);

    /** Конструктор для режиму "Місця" (тільки фільми) */
    public CinemaFrame(GameState gameState) {
        this(gameState, false);
    }

    /** Конструктор з явним вибором режиму */
    public CinemaFrame(GameState gameState, boolean workMode) {
        this.gameState = gameState;
        this.workMode  = workMode;

        // Перевіряємо кулдаун при вході (тільки для режиму роботи)
        if (workMode) {
            long diff = gameState.getCinemaWorkCooldownEndTime() - System.currentTimeMillis();
            if (diff > 0) {
                cinemaCooldownActive = true;
                cinemaCooldownLeft = (int)(diff / 1000);
            }
        }

        setTitle("VibeTower — Кінотеатр 🎬");
        setSize(800, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        BackgroundPanel bg = new BackgroundPanel("src/main/resources/cinema_lobby.png");
        bg.setLayout(null);
        setContentPane(bg);

        // ── Топ-панель ────────────────────────────────────────────────────
        JPanel top = new JPanel(null);
        top.setBounds(0, 0, 800, 55);
        top.setBackground(TOP_BG);
        bg.add(top);

        statsLabel = new JLabel();
        statsLabel.setForeground(Color.WHITE);
        statsLabel.setFont(new Font("Arial", Font.BOLD, 13));
        statsLabel.setBounds(10, 14, 680, 26);
        top.add(statsLabel);
        updateStats();

        JButton backBtn = makeBtn("↩ На карту", BTN_GRAY);
        backBtn.setBounds(672, 10, 116, 34);
        backBtn.addActionListener(e -> goToMap());
        top.add(backBtn);

        // ── Персонаж ──────────────────────────────────────────────────────
        characterLabel = new JLabel("🧑", SwingConstants.CENTER);
        characterLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 36));
        characterLabel.setBounds(350, 460, 50, 58);
        bg.add(characterLabel);

        // ── NPC підказка ──────────────────────────────────────────────────
        String hintText = workMode
                ? "👔 Касир: «Виконуй кожну дію по 2 рази, потім завершуй зміну!»"
                : "🎬 Касир: «Купи квиток і насолоджуйся фільмом!»";
        JLabel hint = new JLabel(hintText, SwingConstants.CENTER);
        hint.setBounds(0, 580, 800, 22);
        hint.setForeground(new Color(200, 180, 255));
        hint.setFont(new Font("Arial", Font.ITALIC, 12));
        hint.setOpaque(true);
        hint.setBackground(new Color(10, 5, 30, 190));
        bg.add(hint);

        // ── Вміст залежно від режиму ──────────────────────────────────────
        if (workMode) {
            buildWorkUI(bg);
            // Якщо кулдаун вже активний при вході — запускаємо таймер
            if (cinemaCooldownActive) {
                // Блокуємо кнопки
                if (ticketBtn  != null) { ticketBtn.setEnabled(false);  }
                if (popcornBtn != null) { popcornBtn.setEnabled(false);  }
                if (cleanBtn   != null) { cleanBtn.setEnabled(false);   }
                if (seatBtn    != null) { seatBtn.setEnabled(false);    }
                if (finishBtn  != null) { finishBtn.setVisible(false);  }
                startCinemaCooldownTimer();
            }
        } else {
            buildVisitUI(bg);
        }

        // ── Клік по фону ──────────────────────────────────────────────────
        bg.addMouseListener(new MouseAdapter() {
            @Override public void mousePressed(MouseEvent e) {
                if (e.getY() > 55 && e.getY() < 600) moveCharacter(e.getX()-25, e.getY()-30, null);
            }
        });
    }

    // ════════════════════════════════════════════════════════════════════════
    //  РЕЖИМ ВІДВІДУВАЧА — тільки фільми
    // ════════════════════════════════════════════════════════════════════════
    private void buildVisitUI(JPanel bg) {
        JLabel title = new JLabel("── Оберіть фільм та купіть квиток ──", SwingConstants.CENTER);
        title.setBounds(50, 56, 700, 22);
        title.setForeground(new Color(200, 170, 255));
        title.setFont(new Font("Arial", Font.BOLD, 15));
        bg.add(title);

        // 4 фільми по 2 в ряд
        for (int i = 0; i < MOVIES.length; i++) {
            final int idx = i;
            int row = i / 2, col = i % 2;
            JButton btn = makeBtn(MOVIES[i][0] + "   🎫 -50🪙", BTN_PURPLE);
            btn.setBounds(60 + col * 390, 70 + row * 50, 370, 40);
            btn.addActionListener(e -> watchMovie(idx));
            bg.add(btn);
        }
    }

    // ════════════════════════════════════════════════════════════════════════
    //  РЕЖИМ РОБОТИ — 4 дії по 2 рази кожна
    // ════════════════════════════════════════════════════════════════════════
    private void buildWorkUI(JPanel bg) {
        JLabel title = new JLabel("── Робота у кінотеатрі ──", SwingConstants.CENTER);
        title.setBounds(50, 56, 700, 22);
        title.setForeground(new Color(120, 210, 255));
        title.setFont(new Font("Arial", Font.BOLD, 15));
        bg.add(title);

        // 4 кнопки дій 2×2
        ticketBtn  = makeBtn("🎫  Продати квиток    (+20🪙 +15XP -5⚡)  [0/2]", BTN_TEAL);
        popcornBtn = makeBtn("🍿  Видати попкорн    (+15🪙 +10XP -4⚡)  [0/2]", BTN_TEAL);
        cleanBtn   = makeBtn("🧹  Прибрати зал      (+25🪙 +20XP -8⚡)  [0/2]", BTN_TEAL);
        seatBtn    = makeBtn("🗺  Допомогти з місцем (+10XP ±🌟)         [0/2]", BTN_TEAL);

        ticketBtn.setBounds(30, 70, 360, 40);
        popcornBtn.setBounds(410, 70, 360, 40);
        cleanBtn.setBounds(30, 118, 360, 40);
        seatBtn.setBounds(410, 118, 360, 40);

        ticketBtn.addActionListener(e ->
                moveCharacter(140, 370, () -> doWorkAction("ticket")));
        popcornBtn.addActionListener(e ->
                moveCharacter(560, 330, () -> doWorkAction("popcorn")));
        cleanBtn.addActionListener(e ->
                moveCharacter(380, 310, () -> doWorkAction("clean")));
        seatBtn.addActionListener(e ->
                moveCharacter(450, 410, () -> doWorkAction("seat")));

        bg.add(ticketBtn);
        bg.add(popcornBtn);
        bg.add(cleanBtn);
        bg.add(seatBtn);

        // Прогрес роботи
        workProgressLabel = new JLabel("", SwingConstants.CENTER);
        workProgressLabel.setBounds(30, 164, 740, 20);
        workProgressLabel.setForeground(new Color(180, 220, 255));
        workProgressLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        bg.add(workProgressLabel);
        updateWorkProgress();

        // Кнопка завершення зміни
        finishBtn = makeBtn("✅  Завершити зміну  (+80🪙 +60XP)", BTN_GREEN);
        finishBtn.setBounds(220, 190, 360, 40);
        finishBtn.setEnabled(false);
        finishBtn.addActionListener(e -> finishWorkShift());
        bg.add(finishBtn);
    }

    // ── Дії роботи ────────────────────────────────────────────────────────
    private void doWorkAction(String type) {
        int energyCost; int silverGain; int xpGain; String msg;
        int currentCount;

        switch (type) {
            case "ticket":
                if (ticketsSold >= MAX_PER_ACTION) { showInfo("Вже продано максимум квитків за цю зміну!"); return; }
                if (gameState.getEnergy() < 5) { showWarn("⚡ Замало енергії! Потрібно 5."); return; }
                gameState.spendEnergy(5); gameState.addSilver(20); gameState.addXp(15);
                ticketsSold++; msg = "🎫 Квиток продано!\n+20 🪙  +15 XP  -5 ⚡";
                ticketBtn.setText("🎫  Продати квиток    (+20🪙 +15XP -5⚡)  [" + ticketsSold + "/2]");
                if (ticketsSold >= MAX_PER_ACTION) ticketBtn.setEnabled(false);
                break;
            case "popcorn":
                if (popcornGiven >= MAX_PER_ACTION) { showInfo("Вже видано максимум попкорну!"); return; }
                if (gameState.getEnergy() < 4) { showWarn("⚡ Замало енергії! Потрібно 4."); return; }
                gameState.spendEnergy(4); gameState.addSilver(15); gameState.addXp(10);
                popcornGiven++; msg = "🍿 Попкорн видано!\n+15 🪙  +10 XP  -4 ⚡";
                popcornBtn.setText("🍿  Видати попкорн    (+15🪙 +10XP -4⚡)  [" + popcornGiven + "/2]");
                if (popcornGiven >= MAX_PER_ACTION) popcornBtn.setEnabled(false);
                break;
            case "clean":
                if (hallCleaned >= MAX_PER_ACTION) { showInfo("Зал вже прибрано максимум разів!"); return; }
                if (gameState.getEnergy() < 8) { showWarn("⚡ Замало енергії! Потрібно 8."); return; }
                gameState.spendEnergy(8); gameState.addSilver(25); gameState.addXp(20);
                hallCleaned++; msg = "🧹 Зал прибрано!\n+25 🪙  +20 XP  -8 ⚡";
                cleanBtn.setText("🧹  Прибрати зал      (+25🪙 +20XP -8⚡)  [" + hallCleaned + "/2]");
                if (hallCleaned >= MAX_PER_ACTION) cleanBtn.setEnabled(false);
                break;
            default: // seat
                if (seatsHelped >= MAX_PER_ACTION) { showInfo("Вже допомогли максимуму глядачів!"); return; }
                gameState.addXp(10);
                boolean gotGold = rnd.nextInt(100) < 30;
                if (gotGold) gameState.addGold(1);
                seatsHelped++;
                msg = "🗺 Глядач знайшов місце!\n+10 XP" + (gotGold ? "\n🎉 +1 🌟" : "");
                seatBtn.setText("🗺  Допомогти з місцем (+10XP ±🌟)         [" + seatsHelped + "/2]");
                if (seatsHelped >= MAX_PER_ACTION) seatBtn.setEnabled(false);
                break;
        }

        SaveManager.saveGame(gameState);
        updateStats();
        updateWorkProgress();
        showInfo(msg);

        // Якщо всі дії виконані — активуємо кнопку завершення
        if (ticketsSold >= MAX_PER_ACTION && popcornGiven >= MAX_PER_ACTION &&
                hallCleaned >= MAX_PER_ACTION && seatsHelped >= MAX_PER_ACTION) {
            finishBtn.setEnabled(true);
            finishBtn.setBackground(BTN_GREEN);
            JOptionPane.showMessageDialog(this, "🎉 Всі завдання виконано! Можеш завершити зміну.");
        }
    }

    private void finishWorkShift() {
        gameState.addSilver(80); gameState.addXp(60);

        // Встановлюємо кулдаун 30 хвилин
        long end = System.currentTimeMillis() + CINEMA_COOLDOWN_SECS * 1000L;
        gameState.setCinemaWorkCooldownEndTime(end);
        cinemaCooldownActive = true;
        cinemaCooldownLeft = CINEMA_COOLDOWN_SECS;

        SaveManager.saveGame(gameState);
        updateStats();
        JOptionPane.showMessageDialog(this,
                "💼 Зміну завершено!\n+80 🪙  +60 XP\nНаступна зміна через 30 хвилин.");

        // Блокуємо всі кнопки роботи
        if (ticketBtn  != null) { ticketBtn.setEnabled(false);  ticketBtn.setText("🎫  Продати квиток  [заблоковано]"); }
        if (popcornBtn != null) { popcornBtn.setEnabled(false); popcornBtn.setText("🍿  Видати попкорн  [заблоковано]"); }
        if (cleanBtn   != null) { cleanBtn.setEnabled(false);   cleanBtn.setText("🧹  Прибрати зал  [заблоковано]"); }
        if (seatBtn    != null) { seatBtn.setEnabled(false);    seatBtn.setText("🗺  Допомогти  [заблоковано]"); }
        if (finishBtn  != null)   finishBtn.setVisible(false);

        startCinemaCooldownTimer();
    }

    private void startCinemaCooldownTimer() {
        if (cinemaCooldownTimer != null) cinemaCooldownTimer.stop();
        cinemaCooldownTimer = new Timer(1000, e -> {
            long end = gameState.getCinemaWorkCooldownEndTime();
            if (end == 0) { cinemaCooldownTimer.stop(); return; }
            long d = end - System.currentTimeMillis();
            if (d > 0) {
                cinemaCooldownLeft = (int)(d / 1000);
                updateStats();
            } else {
                // Кулдаун завершено — розблоковуємо
                gameState.setCinemaWorkCooldownEndTime(0);
                cinemaCooldownActive = false;
                cinemaCooldownLeft = 0;
                cinemaCooldownTimer.stop();
                resetWorkShift();
                updateStats();
                JOptionPane.showMessageDialog(CinemaFrame.this,
                        "🎬 Можна брати нову зміну у кінотеатрі!",
                        "Нова зміна", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        cinemaCooldownTimer.start();
    }

    private void resetWorkShift() {
        ticketsSold = 0; popcornGiven = 0; hallCleaned = 0; seatsHelped = 0;
        if (ticketBtn  != null) { ticketBtn.setEnabled(true);  ticketBtn.setText("🎫  Продати квиток    (+20🪙 +15XP -5⚡)  [0/2]"); }
        if (popcornBtn != null) { popcornBtn.setEnabled(true); popcornBtn.setText("🍿  Видати попкорн    (+15🪙 +10XP -4⚡)  [0/2]"); }
        if (cleanBtn   != null) { cleanBtn.setEnabled(true);   cleanBtn.setText("🧹  Прибрати зал      (+25🪙 +20XP -8⚡)  [0/2]"); }
        if (seatBtn    != null) { seatBtn.setEnabled(true);    seatBtn.setText("🗺  Допомогти з місцем (+10XP ±🌟)         [0/2]"); }
        if (finishBtn  != null) { finishBtn.setEnabled(false); finishBtn.setVisible(true); }
        if (workProgressLabel != null) updateWorkProgress();
    }

    private void updateWorkProgress() {
        if (workProgressLabel == null) return;
        workProgressLabel.setText(String.format(
                "Квитків: %d/2  |  Попкорн: %d/2  |  Прибрань: %d/2  |  Місця: %d/2",
                ticketsSold, popcornGiven, hallCleaned, seatsHelped));
    }

    // ════════════════════════════════════════════════════════════════════════
    //  ПЕРЕГЛЯД ФІЛЬМУ
    // ════════════════════════════════════════════════════════════════════════
    private void watchMovie(int idx) {
        if (!gameState.spendSilver(50)) {
            showWarn("🪙 Недостатньо срібла! Квиток коштує 50."); return;
        }
        int opt = JOptionPane.showConfirmDialog(this,
                "Взяти попкорн 🍿 за 20 срібла?", "Попкорн", JOptionPane.YES_NO_OPTION);
        if (opt == JOptionPane.YES_OPTION && !gameState.spendSilver(20)) {
            JOptionPane.showMessageDialog(this, "Не вистачає на попкорн, дивимось без нього.");
        }
        showCinemaHall(idx);
    }

    private void showCinemaHall(int idx) {
        JDialog hall = new JDialog(this, "🎬 " + MOVIES[idx][0], true);
        hall.setSize(800, 580);
        hall.setLocationRelativeTo(this);

        CinemaHallPanel hallBg = new CinemaHallPanel("src/main/resources/cinema_hall.png");
        hallBg.setLayout(null);
        hall.setContentPane(hallBg);

        JLabel info = new JLabel("<html><center><b style='color:white;font-size:16px'>"
                + MOVIES[idx][0] + "</b><br><span style='color:#ccc'>" + MOVIES[idx][1] + "</span></center></html>",
                SwingConstants.CENTER);
        info.setBounds(100, 30, 600, 70);
        hallBg.add(info);

        JButton watchBtn = makeBtn("▶  Переглянути фільм", new Color(160, 60, 20));
        watchBtn.setBounds(280, 490, 240, 44);
        watchBtn.addActionListener(e -> { hall.dispose(); finishMovie(idx); });
        hallBg.add(watchBtn);

        hall.setVisible(true);
    }

    private void finishMovie(int idx) {
        gameState.addEnergy(10); gameState.addXp(30);
        SaveManager.saveGame(gameState);
        updateStats();
        JOptionPane.showMessageDialog(this, "🎬 Фільм переглянуто!\n+10 ⚡  +30 XP");

        String[] q = QUIZ[idx];
        String[] ans = {q[1], q[2], q[3]};
        int correct = Integer.parseInt(q[4]);
        int choice = JOptionPane.showOptionDialog(this,
                "❓ Питання по фільму:\n" + q[0],
                "Вікторина", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
                null, ans, ans[0]);
        if (choice == correct) {
            gameState.addSilver(30); gameState.addXp(20);
            SaveManager.saveGame(gameState); updateStats();
            showInfo("✅ Правильно!\n+30 🪙  +20 XP");
        } else if (choice >= 0) {
            showInfo("❌ Неправильно. Правильна відповідь: " + ans[correct]);
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
    //  ДОПОМІЖНІ
    // ════════════════════════════════════════════════════════════════════════
    private void updateStats() {
        String timer = (workMode && cinemaCooldownActive && cinemaCooldownLeft > 0)
                ? String.format("  ⏳ До нової зміни: %02d:%02d",
                cinemaCooldownLeft / 60, cinemaCooldownLeft % 60)
                : "";
        statsLabel.setText(String.format("⚡ %d/100  |  🪙 %d  |  🌟 %d  |  XP: %d%s",
                gameState.getEnergy(), gameState.getSilver(), gameState.getGold(),
                gameState.getXp(), timer));
    }

    private JButton makeBtn(String text, Color color) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color c = isEnabled() ? (getModel().isRollover() ? color.brighter() : color)
                        : new Color(50, 50, 60);
                g2.setColor(c);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
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

    private void showInfo(String msg) { JOptionPane.showMessageDialog(this, msg); }
    private void showWarn(String msg) { JOptionPane.showMessageDialog(this, msg, "Увага", JOptionPane.WARNING_MESSAGE); }

    private void goToMap() {
        if (movementTimer != null) movementTimer.stop();
        if (cinemaCooldownTimer != null) cinemaCooldownTimer.stop();
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
            else { g.setColor(new Color(18,10,40)); g.fillRect(0,0,getWidth(),getHeight()); }
        }
    }

    static class CinemaHallPanel extends JPanel {
        private Image img;
        CinemaHallPanel(String path) {
            try { img = ImageIO.read(new File(path)); }
            catch (Exception e) { System.out.println("Фон залу не знайдено: " + path); }
        }
        @Override protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (img != null) g.drawImage(img, 0, 0, getWidth(), getHeight(), this);
            else { g.setColor(new Color(18,10,40)); g.fillRect(0,0,getWidth(),getHeight()); }
        }
    }
}
