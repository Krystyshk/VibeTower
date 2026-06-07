package vibetower.model;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.io.File;

/**
 * CafeFrame — Кафе в стилі Аватарії.
 * Два режими:
 *   1) ВІДВІДУВАЧ — меню як в Аватарії (овальний попап з картинками страв).
 *      Після замовлення NPC-офіціант "підходить" і приносить страву на столик.
 *   2) РОБОТА ОФІЦІАНТОМ — замовлення на столиках, персонаж виконує їх.
 */
public class CafeFrame extends JFrame {

    private final GameState gameState;
    private Timer movementTimer;
    private Timer cooldownTimer;

    // Персонаж гравця
    private JLabel playerLabel;
    // NPC офіціант
    private JLabel waiterLabel;
    private Timer waiterTimer;

    private JLabel statsLabel;
    private JLabel energyBar;
    private JButton finishShiftBtn;

    // Столики (для режиму роботи)
    private TableWidget[] tables;
    private TableWidget targetTable = null;
    private int totalOrders = 0;
    private int ordersLeft;

    private boolean isCooldown = false;
    private final int COOLDOWN_SECS = 20 * 60;
    private int cooldownLeft = 0;

    // Режим: false = відвідувач, true = офіціант
    private boolean workMode = false;

    // Обрана страва для анімації офіціанта
    private String orderedDish = "";

    // Кольори UI
    private static final Color BG_OVERLAY = new Color(0, 0, 0, 170);
    private static final Color MENU_BG    = new Color(250, 245, 235);
    private static final Color BTN_GREEN  = new Color(80, 170, 50);
    private static final Color BTN_RED    = new Color(190, 50, 50);
    private static final Color STATS_BG   = new Color(30, 30, 30, 210);

    public CafeFrame(GameState gameState) {
        this.gameState = gameState;

        setTitle("VibeTower — Кафе ☕");
        setSize(800, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        long remaining = gameState.getCafeCooldownEndTime() - System.currentTimeMillis();
        if (remaining > 0) { isCooldown = true; cooldownLeft = (int)(remaining/1000); }
        else { gameState.setCafeCooldownEndTime(0); }

        BackgroundPanel bg = new BackgroundPanel("src/main/java/vibetower/cafe.png");
        bg.setLayout(null);
        setContentPane(bg);

        // ── Топ-панель ───────────────────────────────────────────────────────
        JPanel top = new JPanel(null);
        top.setBounds(0, 0, 800, 56);
        top.setBackground(STATS_BG);
        bg.add(top);

        statsLabel = new JLabel();
        statsLabel.setForeground(Color.WHITE);
        statsLabel.setFont(new Font("Arial", Font.BOLD, 13));
        statsLabel.setBounds(10, 4, 480, 22);
        top.add(statsLabel);

        // Шкала енергії
        energyBar = new JLabel();
        energyBar.setBounds(10, 28, 320, 20);
        energyBar.setFont(new Font("Arial", Font.BOLD, 11));
        top.add(energyBar);

        // Кнопки режимів
        JButton visitBtn = roundBtn("🍽  Меню кафе", new Color(60,140,200));
        visitBtn.setBounds(340, 8, 150, 38);
        visitBtn.addActionListener(e -> openMenuPopup(bg));
        top.add(visitBtn);

        JButton workBtn = roundBtn("👔  Працювати", new Color(180,130,20));
        workBtn.setBounds(500, 8, 150, 38);
        workBtn.addActionListener(e -> {
            workMode = true;
            activateWorkMode(bg);
            visitBtn.setEnabled(false);
            workBtn.setEnabled(false);
        });
        top.add(workBtn);

        finishShiftBtn = roundBtn("✅  Завершити зміну", BTN_GREEN);
        finishShiftBtn.setBounds(500, 8, 180, 38);
        finishShiftBtn.setVisible(false);
        finishShiftBtn.addActionListener(e -> completeShift());
        top.add(finishShiftBtn);

        JButton backBtn = roundBtn("↩  Карта", new Color(80,80,100));
        backBtn.setBounds(688, 8, 100, 38);
        backBtn.addActionListener(e -> { stopTimers(); new MapFrame(gameState).setVisible(true); dispose(); });
        top.add(backBtn);

        updateStats();

        // ── Персонаж гравця ──────────────────────────────────────────────────
        playerLabel = new JLabel("🧑", SwingConstants.CENTER);
        playerLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 36));
        playerLabel.setBounds(350, 430, 50, 58);
        bg.add(playerLabel);

        // ── NPC офіціант (стоїть біля стійки) ──────────────────────────────
        waiterLabel = new JLabel("<html><center>👩<br><small style='color:#fff;background:#333;padding:1px 3px;border-radius:3px'>Офіціантка</small></center></html>", SwingConstants.CENTER);
        waiterLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 32));
        waiterLabel.setBounds(560, 310, 70, 70);
        bg.add(waiterLabel);

        // ── Клік по фону = рух ───────────────────────────────────────────────
        bg.addMouseListener(new MouseAdapter() {
            @Override public void mousePressed(MouseEvent e) {
                if (e.getY() > 56) movePlayer(e.getX()-25, e.getY()-30, null);
            }
        });

        // ── Таймер кулдауну ──────────────────────────────────────────────────
        cooldownTimer = new Timer(1000, e -> {
            if (!isCooldown) return;
            long diff = gameState.getCafeCooldownEndTime() - System.currentTimeMillis();
            if (diff > 0) { cooldownLeft = (int)(diff/1000); updateStats(); }
            else {
                isCooldown = false;
                gameState.setCafeCooldownEndTime(0);
                if (tables != null) resetTables();
                updateStats();
                JOptionPane.showMessageDialog(this, "☕ Нові клієнти вже чекають!", "Нова зміна", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        cooldownTimer.start();
    }

    // ════════════════════════════════════════════════════════════════════════
    //  МЕНЮ — попап як в Аватарії
    // ════════════════════════════════════════════════════════════════════════
    private void openMenuPopup(JPanel bg) {
        // Напівпрозорий шар поверх усього
        JPanel overlay = new JPanel(null) {
            @Override protected void paintComponent(Graphics g) {
                g.setColor(BG_OVERLAY);
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        overlay.setOpaque(false);
        overlay.setBounds(0, 0, 800, 650);
        bg.add(overlay, Integer.valueOf(10));
        bg.revalidate();
        bg.repaint();

        // Біла овальна панель меню
        MenuPanel menu = new MenuPanel();
        menu.setBounds(80, 130, 640, 340);
        overlay.add(menu);

        // Заголовок
        JLabel title = new JLabel("Меню", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 22));
        title.setForeground(new Color(80, 50, 20));
        title.setBounds(0, 20, 640, 30);
        menu.add(title);

        // Три страви
        String[][] dishes = {
                {"🍰", "Торт",    "+15⚡", "150"},
                {"☕", "Кава",    "+30⚡", "30" },
                {"🍕", "Піца",    "+100⚡","10" }
        };
        int startX = 60;
        for (int i = 0; i < 3; i++) {
            final String[] d = dishes[i];
            JPanel dishCard = buildDishCard(d[0], d[1], d[2], d[3], overlay, bg);
            dishCard.setBounds(startX + i * 185, 60, 160, 210);
            menu.add(dishCard);
        }

        // Кнопка закрити
        JButton closeBtn = roundBtn("Закрити", new Color(140, 50, 50));
        closeBtn.setBounds(240, 285, 160, 36);
        closeBtn.addActionListener(e -> {
            bg.remove(overlay);
            bg.revalidate();
            bg.repaint();
        });
        menu.add(closeBtn);
        bg.revalidate();
        bg.repaint();
    }

    private JPanel buildDishCard(String emoji, String name, String energy, String price,
                                 JPanel overlay, JPanel bg) {
        JPanel card = new JPanel(null);
        card.setOpaque(false);

        // Велике зображення страви
        JLabel imgLbl = new JLabel(emoji, SwingConstants.CENTER);
        imgLbl.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 52));
        imgLbl.setBounds(30, 0, 100, 80);
        card.add(imgLbl);

        JLabel nameLbl = new JLabel(name, SwingConstants.CENTER);
        nameLbl.setFont(new Font("Arial", Font.BOLD, 14));
        nameLbl.setForeground(new Color(60, 40, 10));
        nameLbl.setBounds(0, 82, 160, 22);
        card.add(nameLbl);

        JLabel energyLbl = new JLabel("Енергія " + energy, SwingConstants.CENTER);
        energyLbl.setFont(new Font("Arial", Font.PLAIN, 12));
        energyLbl.setForeground(new Color(50, 120, 50));
        energyLbl.setBounds(0, 106, 160, 18);
        card.add(energyLbl);

        // Ціна (монетка)
        JLabel priceLbl = new JLabel(price + " 🪙", SwingConstants.CENTER);
        priceLbl.setFont(new Font("Arial", Font.BOLD, 13));
        priceLbl.setForeground(new Color(160, 100, 0));
        priceLbl.setBounds(0, 126, 160, 20);
        card.add(priceLbl);

        // Кнопка «Замовити»
        JButton orderBtn = roundBtn("Замовити", BTN_GREEN);
        orderBtn.setBounds(15, 158, 130, 34);
        orderBtn.addActionListener(e -> {
            int cost = Integer.parseInt(price);
            if (!gameState.spendSilver(cost)) {
                JOptionPane.showMessageDialog(this, "🪙 Недостатньо срібла! Потрібно " + cost + ".", "Нестача", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int energyGain = Integer.parseInt(energy.replace("+","").replace("⚡",""));
            orderedDish = emoji + " " + name;
            // Закриваємо меню
            bg.remove(overlay);
            bg.revalidate();
            bg.repaint();
            // Анімація — офіціант підходить до гравця
            animateWaiter(energyGain, name);
        });
        card.add(orderBtn);
        return card;
    }

    // ════════════════════════════════════════════════════════════════════════
    //  АНІМАЦІЯ ОФІЦІАНТА
    // ════════════════════════════════════════════════════════════════════════
    private void animateWaiter(int energyGain, String dishName) {
        // Офіціант рухається до гравця
        int targetX = playerLabel.getX() - 60;
        int targetY = playerLabel.getY();

        if (waiterTimer != null && waiterTimer.isRunning()) waiterTimer.stop();
        waiterTimer = new Timer(16, null);
        waiterTimer.addActionListener(e -> {
            int wx = waiterLabel.getX(), wy = waiterLabel.getY();
            int nx = approach(wx, targetX, 4);
            int ny = approach(wy, targetY, 4);
            waiterLabel.setLocation(nx, ny);
            if (nx == targetX && ny == targetY) {
                waiterTimer.stop();
                // Офіціант дійшов — показує страву гравцеві
                waiterLabel.setText("<html><center>👩<br>🍽</center></html>");
                gameState.addEnergy(energyGain);
                gameState.addXp(5);
                SaveManager.saveGame(gameState);
                updateStats();
                JOptionPane.showMessageDialog(CafeFrame.this,
                        "👩‍🍳 Офіціантка принесла: " + orderedDish
                                + "\n+" + energyGain + " ⚡  +5 XP",
                        "Замовлення доставлено!", JOptionPane.INFORMATION_MESSAGE);
                // Офіціант повертається
                moveWaiterBack();
            }
        });
        waiterTimer.start();
    }

    private void moveWaiterBack() {
        int homeX = 560, homeY = 310;
        Timer back = new Timer(16, null);
        back.addActionListener(e -> {
            int wx = waiterLabel.getX(), wy = waiterLabel.getY();
            int nx = approach(wx, homeX, 4);
            int ny = approach(wy, homeY, 4);
            waiterLabel.setLocation(nx, ny);
            if (nx == homeX && ny == homeY) {
                back.stop();
                waiterLabel.setText("<html><center>👩<br><small style='color:#fff;background:#333;padding:1px 3px;border-radius:3px'>Офіціантка</small></center></html>");
            }
        });
        back.start();
    }

    // ════════════════════════════════════════════════════════════════════════
    //  РЕЖИМ РОБОТИ
    // ════════════════════════════════════════════════════════════════════════
    private void activateWorkMode(JPanel bg) {
        tables = new TableWidget[]{
                new TableWidget(90,  145, bg),
                new TableWidget(245, 280, bg),
                new TableWidget(470, 200, bg),
                new TableWidget(560, 350, bg)
        };
        ordersLeft = tables.length;

        if (isCooldown) {
            for (TableWidget t : tables) t.clearOrder();
        }

        // Підказка
        JLabel hint = new JLabel("💬 Натискай на 📄 замовлення, підходь до столика і виконуй!", SwingConstants.CENTER);
        hint.setBounds(50, 600, 700, 26);
        hint.setForeground(new Color(255, 240, 180));
        hint.setFont(new Font("Arial", Font.ITALIC, 12));
        hint.setOpaque(true);
        hint.setBackground(new Color(30,30,30,180));
        bg.add(hint);
        bg.revalidate();
        bg.repaint();
    }

    private void handleOrderClick(TableWidget table) {
        if (isCooldown) return;
        if (gameState.getEnergy() < 5) {
            JOptionPane.showMessageDialog(this, "❌ Замало енергії! Потрібно 5 ⚡.\nКупи їжу або напій.", "Втома", JOptionPane.WARNING_MESSAGE);
            return;
        }
        targetTable = table;
        movePlayer(table.x - 10, table.y + 20, () -> {
            if (targetTable == null || !targetTable.hasOrder) return;
            int dist = (int) Math.hypot(playerLabel.getX()-targetTable.x, playerLabel.getY()-targetTable.y);
            if (dist > 160) {
                JOptionPane.showMessageDialog(this, "🚶 Занадто далеко! Підійди ближче.", "Увага", JOptionPane.WARNING_MESSAGE);
                targetTable = null; return;
            }
            targetTable.serveFood();
            int silver = 10 + (int)(Math.random() * 6);
            gameState.spendEnergy(5);
            gameState.addSilver(silver);
            gameState.addXp(10);
            totalOrders++;
            ordersLeft--;
            boolean gotGold = totalOrders % 15 == 0;
            if (gotGold) gameState.addGold(1);
            SaveManager.saveGame(gameState);
            updateStats();
            String msg = "✅ Замовлення виконано!\n+" + silver + " 🪙  +10 XP  -5 ⚡";
            if (gotGold) msg += "\n🎉 +1 🌟 за 15-те замовлення!";
            JOptionPane.showMessageDialog(this, msg);
            if (ordersLeft == 0) { finishShiftBtn.setVisible(true); JOptionPane.showMessageDialog(this, "🎉 Всі столики обслужені! Натисни «Завершити зміну»."); }
            targetTable = null;
        });
    }

    private void completeShift() {
        gameState.addSilver(50); gameState.addXp(50);
        SaveManager.saveGame(gameState);
        long end = System.currentTimeMillis() + COOLDOWN_SECS * 1000L;
        gameState.setCafeCooldownEndTime(end);
        isCooldown = true; cooldownLeft = COOLDOWN_SECS;
        if (tables != null) for (TableWidget t : tables) t.clearOrder();
        finishShiftBtn.setVisible(false);
        updateStats();
        JOptionPane.showMessageDialog(this, "💼 Зміну завершено!\n+50 🪙  +50 XP\nНаступна зміна через 20 хвилин.");
    }

    private void resetTables() {
        ordersLeft = tables.length;
        for (TableWidget t : tables) t.restoreOrder();
        finishShiftBtn.setVisible(false);
    }

    // ════════════════════════════════════════════════════════════════════════
    //  ПЕРЕМІЩЕННЯ
    // ════════════════════════════════════════════════════════════════════════
    private void movePlayer(int tx, int ty, Runnable onArrival) {
        if (movementTimer != null && movementTimer.isRunning()) movementTimer.stop();
        movementTimer = new Timer(15, null);
        movementTimer.addActionListener(e -> {
            int cx = playerLabel.getX(), cy = playerLabel.getY();
            int nx = approach(cx, tx, 4), ny = approach(cy, ty, 4);
            if (ny < 56) ny = 56;
            playerLabel.setLocation(nx, ny);
            if (nx == tx && ny == ty) {
                movementTimer.stop();
                if (onArrival != null) onArrival.run();
            }
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
        String timer = isCooldown
                ? String.format("  ⏳ %02d:%02d", cooldownLeft/60, cooldownLeft%60)
                : "";
        statsLabel.setText(String.format("⚡%d  🪙%d  🌟%d  XP:%d%s",
                gameState.getEnergy(), gameState.getSilver(), gameState.getGold(), gameState.getXp(), timer));
        int e = gameState.getEnergy();
        StringBuilder bar = new StringBuilder("⚡ ");
        for (int i = 0; i < 10; i++) bar.append(i < e/10 ? "█" : "░");
        bar.append(" ").append(e).append("%");
        energyBar.setText(bar.toString());
        energyBar.setForeground(e > 50 ? new Color(80,220,80) : e > 20 ? new Color(220,180,0) : new Color(220,60,60));
    }

    private void stopTimers() {
        if (movementTimer != null) movementTimer.stop();
        if (cooldownTimer != null) cooldownTimer.stop();
        if (waiterTimer   != null) waiterTimer.stop();
    }

    // ════════════════════════════════════════════════════════════════════════
    //  ДОПОМІЖНІ
    // ════════════════════════════════════════════════════════════════════════
    private JButton roundBtn(String text, Color color) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? color.brighter() : color);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
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

    // ── Овальна панель меню ──────────────────────────────────────────────────
    static class MenuPanel extends JPanel {
        MenuPanel() { setOpaque(false); setLayout(null); }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            // Тінь
            g2.setColor(new Color(0,0,0,80));
            g2.fillRoundRect(6, 6, getWidth()-6, getHeight()-6, 50, 50);
            // Фон (слонова кістка, як в Аватарії)
            g2.setColor(new Color(252, 248, 238));
            g2.fillRoundRect(0, 0, getWidth()-4, getHeight()-4, 50, 50);
            // Обводка зелена
            g2.setStroke(new BasicStroke(3));
            g2.setColor(new Color(100, 170, 60));
            g2.drawRoundRect(2, 2, getWidth()-8, getHeight()-8, 46, 46);
            // Зелений заголовок-прямокутник зверху
            g2.setColor(new Color(100, 170, 60));
            g2.fillRoundRect(getWidth()/2-60, -1, 120, 38, 20, 20);
            g2.dispose();
        }
    }

    // ── Столик ───────────────────────────────────────────────────────────────
    class TableWidget {
        int x, y;
        JLabel orderLbl;
        boolean hasOrder = true;

        TableWidget(int x, int y, JPanel parent) {
            this.x = x; this.y = y;
            orderLbl = new JLabel("", SwingConstants.CENTER);
            setOrderStyle();
            orderLbl.setBounds(x, y, 50, 52);
            orderLbl.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            orderLbl.setToolTipText("Замовлення — клікни, щоб виконати");
            orderLbl.addMouseListener(new MouseAdapter() {
                @Override public void mousePressed(MouseEvent e) {
                    if (hasOrder && workMode) handleOrderClick(TableWidget.this);
                }
            });
            parent.add(orderLbl);
        }
        void setOrderStyle() {
            orderLbl.setText("<html><div style='color:#00DDFF;font-size:22px;background:rgba(0,0,0,0.45);padding:2px 5px;border-radius:7px;'>📄</div></html>");
        }
        void serveFood() {
            hasOrder = false;
            orderLbl.setText("<html><div style='font-size:22px'>🍲</div></html>");
        }
        void clearOrder() { hasOrder = false; orderLbl.setText(""); }
        void restoreOrder() { hasOrder = true; setOrderStyle(); }
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
            else { g.setColor(new Color(45, 30, 15)); g.fillRect(0,0,getWidth(),getHeight()); }
        }
    }
}
