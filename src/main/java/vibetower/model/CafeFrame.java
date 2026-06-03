package vibetower.model;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import javax.imageio.ImageIO;

public class CafeFrame extends JFrame {
    private GameState gameState;
    private JLabel character;
    private Timer movementTimer;
    private Timer cooldownTimer;

    private JLabel statsLabel;
    private JButton finishShiftButton;
    private JButton backButton;

    private TableData[] tables;
    private TableData targetedTable = null;
    private int shiftOrdersCompleted = 0;
    private int totalOrdersCount = 0;
    private int activeOrdersRemaining = 2;

    // --- ЛОГІКА ПЕРЕРВИ (ГЛОБАЛЬНА) ---
    private boolean isCooldown = false;
    private final int COOLDOWN_TIME_SECS = 20 * 60; // 20 хвилин (1200 секунд)

    // ПІДКАЗКА: Якщо для тестів треба швидко (10 секунд), закоментуй рядок вище
    // і розкоментуй рядок нижче:
    // private final int COOLDOWN_TIME_SECS = 10;

    private int cooldownSecondsRemaining = 0;

    private Rectangle[] obstacles = {
            new Rectangle(50, 110, 150, 100),
            new Rectangle(230, 240, 130, 110),
            new Rectangle(180, 430, 260, 120),
            new Rectangle(450, 410, 330, 210),
            new Rectangle(0, 0, 800, 120)
    };

    public CafeFrame(GameState gameState) {
        this.gameState = gameState;

        setTitle("VibeTower — Кафе");
        setSize(800, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // --- ПЕРЕВІРКА ЧАСУ ПРИ ВХОДІ В КАФЕ ---
        // Вираховуємо, чи закінчився вже час у реальному світі
        long remainingMillis = gameState.getCafeCooldownEndTime() - System.currentTimeMillis();
        if (remainingMillis > 0) {
            isCooldown = true;
            cooldownSecondsRemaining = (int) (remainingMillis / 1000);
        } else {
            isCooldown = false;
            gameState.setCafeCooldownEndTime(0); // Очищаємо, якщо час вийшов
        }

        BackgroundPanel bgPanel = new BackgroundPanel("src/main/java/vibetower/cafe.png");
        bgPanel.setLayout(null);
        setContentPane(bgPanel);

        // --- ПАНЕЛЬ СТАТИСТИКИ ---
        JPanel topPanel = new JPanel();
        topPanel.setBounds(0, 0, 800, 50);
        topPanel.setBackground(new Color(40, 40, 40, 220));
        topPanel.setLayout(null);
        bgPanel.add(topPanel);

        statsLabel = new JLabel();
        statsLabel.setForeground(Color.WHITE);
        statsLabel.setFont(new Font("Arial", Font.BOLD, 14));
        statsLabel.setBounds(10, 10, 470, 30);
        topPanel.add(statsLabel);
        updateStats();

        // КНОПКА ВИХОДУ НА КАРТУ
        backButton = new JButton("↩ На карту");
        backButton.setBounds(670, 10, 100, 30);
        backButton.setFocusable(false);
        backButton.addActionListener(e -> {
            if (cooldownTimer != null) cooldownTimer.stop();
            if (movementTimer != null) movementTimer.stop();
            // ВАЖЛИВО: Повертаємось на карту!
            new MapFrame(gameState).setVisible(true);
            this.dispose();
        });
        topPanel.add(backButton);

        finishShiftButton = new JButton("💼 Завершити зміну");
        finishShiftButton.setBounds(480, 10, 180, 30);
        finishShiftButton.setFocusable(false);
        finishShiftButton.setBackground(new Color(46, 204, 113));
        finishShiftButton.setForeground(Color.WHITE);
        finishShiftButton.setFont(new Font("Arial", Font.BOLD, 12));
        finishShiftButton.setVisible(false);
        finishShiftButton.addActionListener(e -> completeShift());
        topPanel.add(finishShiftButton);

        // --- СТОЛИКИ ---
        tables = new TableData[] {
                new TableData(90, 140, bgPanel),
                new TableData(240, 275, bgPanel)
        };

        // Якщо зараз перерва (гравець зайшов, а час ще не вийшов), ховаємо замовлення
        if (isCooldown) {
            for (TableData table : tables) {
                table.hasOrder = false;
                table.hasFood = false;
                table.orderLabel.setText("");
            }
        }

        // --- ПЕРСОНАЖ ---
        character = new JLabel("👤", SwingConstants.CENTER);
        character.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 35));
        character.setBounds(100, 340, 50, 60);
        bgPanel.add(character);

        bgPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getY() > 50) {
                    targetedTable = null;
                    moveTo(e.getX() - 25, e.getY() - 30);
                }
            }
        });

        // --- РОЗУМНИЙ ТАЙМЕР ВІДЛІКУ ---
        cooldownTimer = new Timer(1000, e -> {
            if (isCooldown) {
                long timeDiff = gameState.getCafeCooldownEndTime() - System.currentTimeMillis();

                if (timeDiff > 0) {
                    cooldownSecondsRemaining = (int) (timeDiff / 1000);
                    updateStats();
                } else {
                    isCooldown = false;
                    gameState.setCafeCooldownEndTime(0);
                    resetCafeLocation();
                    updateStats();
                    JOptionPane.showMessageDialog(this, "☕ Відпочинок завершено! Нові клієнти вже чекають за столиками.", "Робота", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });
        cooldownTimer.start();
    }

    private void updateStats() {
        String timerText = "";
        if (isCooldown) {
            int mins = cooldownSecondsRemaining / 60;
            int secs = cooldownSecondsRemaining % 60;
            timerText = String.format(" | ⏳ До нової зміни: %02d:%02d", mins, secs);
        }

        statsLabel.setText(String.format(
                "⚡ Енергія: %d/100 | 🪙 %d | 🌟 %d | 📈 XP: %d%s",
                gameState.getEnergy(), gameState.getSilver(), gameState.getGold(), gameState.getXp(), timerText
        ));
    }

    private void moveTo(int targetX, int targetY) {
        if (movementTimer != null && movementTimer.isRunning()) movementTimer.stop();

        movementTimer = new Timer(15, e -> {
            int currentX = character.getX();
            int currentY = character.getY();
            int nextX = currentX;
            int nextY = currentY;
            int step = 4;

            if (Math.abs(currentX - targetX) > step) {
                nextX += (currentX < targetX) ? step : -step;
            } else {
                nextX = targetX;
            }

            if (Math.abs(currentY - targetY) > step) {
                nextY += (currentY < targetY) ? step : -step;
            } else {
                nextY = targetY;
            }

            Rectangle characterBounds = new Rectangle(nextX + 10, nextY + 35, 30, 25);
            boolean collided = false;
            for (Rectangle obs : obstacles) {
                if (characterBounds.intersects(obs)) {
                    collided = true;
                    break;
                }
            }

            if (!collided) {
                character.setLocation(nextX, nextY);
            } else {
                movementTimer.stop();
                checkProximityInteraction();
                return;
            }

            if (currentX == targetX && currentY == targetY) {
                movementTimer.stop();
                checkProximityInteraction();
            }
        });
        movementTimer.start();
    }

    private void checkProximityInteraction() {
        if (targetedTable != null && targetedTable.hasOrder && !isCooldown) {
            int charCenterX = character.getX() + 25;
            int charCenterY = character.getY() + 30;
            int tableCenterX = targetedTable.x + 20;
            int tableCenterY = targetedTable.y + 20;

            double distance = Math.sqrt(Math.pow(charCenterX - tableCenterX, 2) + Math.pow(charCenterY - tableCenterY, 2));

            if (distance <= 155) {
                targetedTable.hasOrder = false;
                targetedTable.hasFood = true;
                targetedTable.orderLabel.setText("<html><div style='color: #FF5722; font-size: 24px;'>🍲</div></html>");

                int silverReward = 10 + (int)(Math.random() * 6);
                gameState.spendEnergy(5);
                gameState.addSilver(silverReward);
                gameState.addXp(10);

                shiftOrdersCompleted++;
                totalOrdersCount++;
                activeOrdersRemaining--;

                if (totalOrdersCount % 15 == 0) {
                    gameState.addGold(1);
                    JOptionPane.showMessageDialog(this, "🎉 Бонус за 15-те замовлення: +1 Золото!", "Успіх", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, String.format("Замовлення виконано!\n-5 Енергії\n+%d Срібла\n+10 XP", silverReward));
                }

                updateStats();

                if (activeOrdersRemaining == 0) {
                    finishShiftButton.setVisible(true);
                    JOptionPane.showMessageDialog(this, "🎉 Всі столики обслужені! Натисніть кнопку 'Завершити зміну' зверху.");
                }
            } else {
                JOptionPane.showMessageDialog(this, "🚶 Занадто далеко! Підійдіть ближче до столика.", "Увага", JOptionPane.WARNING_MESSAGE);
            }
            targetedTable = null;
        }
    }

    // --- НАЖАТТЯ КНОПКИ ЗАВЕРШЕННЯ ЗМІНИ ---
    private void completeShift() {
        gameState.addSilver(50);
        gameState.addXp(50);

        long endTime = System.currentTimeMillis() + (COOLDOWN_TIME_SECS * 1000L);
        gameState.setCafeCooldownEndTime(endTime);

        isCooldown = true;
        cooldownSecondsRemaining = COOLDOWN_TIME_SECS;

        for (TableData table : tables) {
            table.hasOrder = false;
            table.hasFood = false;
            table.orderLabel.setText("");
        }

        finishShiftButton.setVisible(false);
        updateStats();

        JOptionPane.showMessageDialog(this, "💼 Зміну завершено! Бонус: +50 Срібла, +50 XP.\n\nВи зможете взяти нові замовлення через 20 хвилин.", "Перерва", JOptionPane.INFORMATION_MESSAGE);
    }

    private void resetCafeLocation() {
        shiftOrdersCompleted = 0;
        activeOrdersRemaining = tables.length;
        for (TableData table : tables) {
            table.hasOrder = true;
            table.hasFood = false;
            table.setDefaultOrderStyle();
        }
        finishShiftButton.setVisible(false);
    }

    class TableData {
        int x, y;
        JLabel orderLabel;
        boolean hasOrder = true;
        boolean hasFood = false;

        public TableData(int x, int y, JComponent parent) {
            this.x = x;
            this.y = y;

            orderLabel = new JLabel("", SwingConstants.CENTER);
            setDefaultOrderStyle();
            orderLabel.setBounds(x, y, 50, 50);
            orderLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            orderLabel.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    if (!hasOrder || isCooldown) return;

                    if (gameState.getEnergy() < 5) {
                        JOptionPane.showMessageDialog(CafeFrame.this, "❌ Недостатньо енергії! Потрібно хоча б 5.", "Втома", JOptionPane.WARNING_MESSAGE);
                        return;
                    }

                    targetedTable = TableData.this;
                    moveTo(x, y);
                }
            });
            parent.add(orderLabel);
        }

        public void setDefaultOrderStyle() {
            orderLabel.setText("<html><div style='" +
                    "color: #00F0FF;" +
                    "font-size: 24px;" +
                    "font-weight: bold;" +
                    "text-shadow: 0px 0px 5px #0055FF;" +
                    "background: rgba(0, 0, 0, 0.4);" +
                    "padding: 2px 6px;" +
                    "border-radius: 8px;" +
                    "'>📄</div></html>");
        }
    }

    class BackgroundPanel extends JPanel {
        private Image img;
        public BackgroundPanel(String path) {
            try { img = ImageIO.read(new File(path)); }
            catch (Exception e) { System.out.println("Фон не знайдено!"); }
        }
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (img != null) g.drawImage(img, 0, 0, getWidth(), getHeight(), this);
        }
    }
}