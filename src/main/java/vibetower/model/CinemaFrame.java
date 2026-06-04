package vibetower.model;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.Random;

/**
 * CinemaFrame — Кінотеатр (з 5 рівня).
 * Два режими: перегляд фільму та робота працівником кінотеатру.
 * Фон: касса (lobby) + зал (hall) — вкладки.
 */
public class CinemaFrame extends JFrame {

    private final GameState gameState;
    private final Random rnd = new Random();

    private JLabel statsLabel;
    private JLabel characterLabel;
    private Timer movementTimer;

    // Стан роботи кінотеатру
    private int ticketsSold    = 0;
    private int popcornSold    = 0;
    private int hallCleaned    = 0;

    // Фільми та вікторина
    private static final String[][] MOVIES = {
            {"🎭 Пригоди у місті",  "Весела комедія про міське життя та дружбу."},
            {"🚀 Зорі та мрії",     "Фантастика про космічні подорожі та відкриття."},
            {"🌸 Весна назавжди",   "Романтична драма про першу любов і сміливість."},
            {"🐉 Легенда дракона",  "Фентезі-екшн із магією та давніми таємницями."}
    };
    private static final String[][] QUIZ = {
            {"Де відбуваються пригоди?",     "У місті","В лісі","На морі",         "0"},
            {"Що знаходить герой у космосі?","Зорю","Планету","Ракету",             "0"},
            {"Яка пора року у фільмі?",      "Весна","Зима","Осінь",               "0"},
            {"Яка головна істота легенди?",  "Дракон","Фенікс","Єдиноріг",         "0"}
    };

    public CinemaFrame(GameState gameState) {
        this.gameState = gameState;

        setTitle("VibeTower — Кінотеатр 🎬");
        setSize(800, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Використовуємо лобі (касса) як основний фон
        BackgroundPanel bg = new BackgroundPanel("src/main/resources/cinema_lobby.png");
        bg.setLayout(null);
        setContentPane(bg);

        // ── Топ-панель ─────────────────────────────────────────────────────
        JPanel top = new JPanel(null);
        top.setBounds(0, 0, 800, 50);
        top.setBackground(new Color(20, 10, 50, 220));
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

        // ── Персонаж ─────────────────────────────────────────────────────────
        characterLabel = new JLabel("👤", SwingConstants.CENTER);
        characterLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 35));
        characterLabel.setBounds(380, 460, 50, 60);
        bg.add(characterLabel);

        // ── NPC-підказка ─────────────────────────────────────────────────────
        JLabel npc = new JLabel(
                "<html><i>Касир: «Купи квиток і насолоджуйся фільмом, або попрацюй у кінотеатрі!»</i></html>",
                SwingConstants.CENTER);
        npc.setBounds(80, 645, 640, 22);
        npc.setForeground(new Color(200, 180, 255));
        npc.setFont(new Font("Arial", Font.ITALIC, 12));
        bg.add(npc);

        // ──────── СЕКЦІЯ: ПЕРЕГЛЯД ФІЛЬМУ ─────────────────────────────────
        JLabel movieTitle = new JLabel("─── Обери фільм та купи квиток ───", SwingConstants.CENTER);
        movieTitle.setBounds(50, 55, 700, 24);
        movieTitle.setForeground(new Color(180, 160, 255));
        movieTitle.setFont(new Font("Arial", Font.BOLD, 14));
        bg.add(movieTitle);

        // Кнопки фільмів (2 + 2 в ряд)
        for (int i = 0; i < MOVIES.length; i++) {
            final int idx = i;
            int row = i / 2, col = i % 2;
            JButton movieBtn = makeBtn(MOVIES[i][0] + "  🎫 -50🪙", 80 + col * 330, 85 + row * 44, 310, 36);
            movieBtn.addActionListener(e -> watchMovie(idx));
            bg.add(movieBtn);
        }

        // Розділювач
        JSeparator sep = new JSeparator();
        sep.setBounds(50, 180, 700, 10);
        bg.add(sep);

        // ──────── СЕКЦІЯ: РОБОТА У КІНОТЕАТРІ ─────────────────────────────
        JLabel workTitle = new JLabel("─── Робота у кінотеатрі ───", SwingConstants.CENTER);
        workTitle.setBounds(50, 192, 700, 24);
        workTitle.setForeground(new Color(120, 210, 255));
        workTitle.setFont(new Font("Arial", Font.BOLD, 14));
        bg.add(workTitle);

        JButton ticketBtn = makeBtn("🎫 Продати квиток  (+20🪙 +15XP -5⚡)",  80, 225, 310, 36);
        JButton popcornBtn= makeBtn("🍿 Видати попкорн  (+15🪙 +10XP -4⚡)",  420, 225, 310, 36);
        JButton cleanBtn  = makeBtn("🧹 Прибрати зал    (+25🪙 +20XP -8⚡)",  80, 270, 310, 36);
        JButton seatBtn   = makeBtn("🗺 Допомогти знайти місце  (+10XP ±🌟)", 420, 270, 310, 36);

        // Переміщення до точки каси, потім дія
        ticketBtn.addActionListener(e -> moveCharacter(140, 370, () -> sellTicket(ticketBtn)));
        popcornBtn.addActionListener(e -> moveCharacter(560, 320, () -> givePopcorn(popcornBtn)));
        cleanBtn.addActionListener(e  -> moveToCinemaHall(() -> cleanHall(cleanBtn)));
        seatBtn.addActionListener(e   -> moveCharacter(450, 400, () -> helpFindSeat()));

        bg.add(ticketBtn);
        bg.add(popcornBtn);
        bg.add(cleanBtn);
        bg.add(seatBtn);

        // ── Інфо про прогрес роботи ──────────────────────────────────────────
        JLabel workProgress = new JLabel("", SwingConstants.CENTER);
        workProgress.setBounds(50, 315, 700, 22);
        workProgress.setForeground(new Color(180, 220, 255));
        workProgress.setFont(new Font("Arial", Font.PLAIN, 12));
        bg.add(workProgress);

        // ── Клік по фону ─────────────────────────────────────────────────────
        bg.addMouseListener(new MouseAdapter() {
            @Override public void mousePressed(MouseEvent e) {
                if (e.getY() > 55) moveCharacter(e.getX() - 25, e.getY() - 30, null);
            }
        });
    }

    // ── Перегляд фільму ───────────────────────────────────────────────────
    private void watchMovie(int movieIdx) {
        if (!gameState.spendSilver(50)) {
            JOptionPane.showMessageDialog(this, "🪙 Недостатньо срібла! Квиток коштує 50.", "Нестача коштів", JOptionPane.WARNING_MESSAGE);
            return;
        }
        // Попкорн за бажанням
        int opt = JOptionPane.showConfirmDialog(this,
                "Взяти попкорн 🍿 за 20 срібла?", "Попкорн", JOptionPane.YES_NO_OPTION);
        if (opt == JOptionPane.YES_OPTION) {
            if (!gameState.spendSilver(20))
                JOptionPane.showMessageDialog(this, "Не вистачає на попкорн, дивимось без нього.");
        }

        // Відкрити вікно залу
        showCinemaHall(movieIdx);
    }

    private void showCinemaHall(int movieIdx) {
        // Окремий екран залу
        JDialog hallDialog = new JDialog(this, "🎬 " + MOVIES[movieIdx][0], true);
        hallDialog.setSize(800, 580);
        hallDialog.setLocationRelativeTo(this);

        CinemaHallPanel hallBg = new CinemaHallPanel("src/main/resources/cinema_hall.png");
        hallBg.setLayout(null);
        hallDialog.setContentPane(hallBg);

        JLabel filmInfo = new JLabel("<html><center><b>" + MOVIES[movieIdx][0] + "</b><br>"
                + MOVIES[movieIdx][1] + "</center></html>", SwingConstants.CENTER);
        filmInfo.setBounds(100, 30, 600, 60);
        filmInfo.setForeground(Color.WHITE);
        filmInfo.setFont(new Font("Arial", Font.PLAIN, 14));
        hallBg.add(filmInfo);

        JButton watchBtn = new JButton("▶ Переглянути фільм");
        watchBtn.setBounds(280, 490, 240, 40);
        watchBtn.setBackground(new Color(160, 60, 20));
        watchBtn.setForeground(Color.WHITE);
        watchBtn.setFont(new Font("Arial", Font.BOLD, 14));
        watchBtn.addActionListener(e -> {
            hallDialog.dispose();
            finishWatchingMovie(movieIdx);
        });
        hallBg.add(watchBtn);

        hallDialog.setVisible(true);
    }

    private void finishWatchingMovie(int movieIdx) {
        gameState.addEnergy(10);
        gameState.addXp(30);
        SaveManager.saveGame(gameState);
        updateStats();
        JOptionPane.showMessageDialog(this,
                "🎬 Фільм переглянуто!\n+10 ⚡  +30 XP");

        // Вікторина
        String[] q = QUIZ[movieIdx];
        String[] answers = {q[1], q[2], q[3]};
        int correct = Integer.parseInt(q[4]);
        int choice = JOptionPane.showOptionDialog(this,
                "❓ Питання по фільму:\n" + q[0],
                "Вікторина", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
                null, answers, answers[0]);
        if (choice == correct) {
            gameState.addSilver(30);
            gameState.addXp(20);
            SaveManager.saveGame(gameState);
            updateStats();
            JOptionPane.showMessageDialog(this, "✅ Правильно!\n+30 🪙  +20 XP");
        } else if (choice >= 0) {
            JOptionPane.showMessageDialog(this, "❌ Неправильно. Правильна відповідь: " + answers[correct]);
        }
    }

    // ── Робота у кінотеатрі ───────────────────────────────────────────────
    private void sellTicket(JButton btn) {
        if (gameState.getEnergy() < 5) { showWarn("⚡ Замало енергії!"); return; }
        gameState.spendEnergy(5);
        gameState.addSilver(20);
        gameState.addXp(15);
        ticketsSold++;
        SaveManager.saveGame(gameState);
        updateStats();
        JOptionPane.showMessageDialog(this, "🎫 Квиток продано!\n+20 🪙  +15 XP  -5 ⚡\nПродано квитків: " + ticketsSold);
    }

    private void givePopcorn(JButton btn) {
        if (gameState.getEnergy() < 4) { showWarn("⚡ Замало енергії!"); return; }
        gameState.spendEnergy(4);
        gameState.addSilver(15);
        gameState.addXp(10);
        popcornSold++;
        SaveManager.saveGame(gameState);
        updateStats();
        JOptionPane.showMessageDialog(this, "🍿 Попкорн видано!\n+15 🪙  +10 XP  -4 ⚡");
    }

    private void moveToCinemaHall(Runnable action) {
        moveCharacter(380, 300, action);
    }

    private void cleanHall(JButton btn) {
        if (gameState.getEnergy() < 8) { showWarn("⚡ Замало енергії! Потрібно 8."); return; }
        gameState.spendEnergy(8);
        gameState.addSilver(25);
        gameState.addXp(20);
        hallCleaned++;
        SaveManager.saveGame(gameState);
        updateStats();
        JOptionPane.showMessageDialog(this, "🧹 Зал прибрано!\n+25 🪙  +20 XP  -8 ⚡\nПрибрань виконано: " + hallCleaned);
    }

    private void helpFindSeat() {
        gameState.addXp(10);
        boolean gotGold = rnd.nextInt(100) < 30;
        if (gotGold) gameState.addGold(1);
        SaveManager.saveGame(gameState);
        updateStats();
        String msg = "🗺 Глядач знайшов місце!\n+10 XP";
        if (gotGold) msg += "\n🎉 Щедрий глядач подарував: +1 🌟";
        JOptionPane.showMessageDialog(this, msg);
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
        b.setBackground(new Color(60, 40, 100));
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

    // ── Фони ────────────────────────────────────────────────────────────
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

    static class CinemaHallPanel extends JPanel {
        private Image img;
        CinemaHallPanel(String path) {
            try { img = ImageIO.read(new File(path)); }
            catch (Exception e) { System.out.println("Фон залу не знайдено: " + path); }
        }
        @Override protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (img != null) g.drawImage(img, 0, 0, getWidth(), getHeight(), this);
            else { g.setColor(new Color(20,10,40)); g.fillRect(0,0,getWidth(),getHeight()); }
        }
    }
}

