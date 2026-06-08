package vibetower.ui;

import vibetower.model.GameState;
import vibetower.model.SaveManager;

import javax.swing.*;
import java.awt.*;

public class TasksFrame extends JFrame {

    private final GameState gameState;
    private JPanel topPanel;
    private JPanel tasksPanel;

    public TasksFrame(GameState gameState) {
        this.gameState = gameState;
        this.gameState.fixAfterLoad();

        setTitle("VibeTower — Завдання");
        setSize(930, 680);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(252, 248, 240));

        topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.setBorder(BorderFactory.createEmptyBorder(18, 25, 10, 25));

        JLabel titleLabel = new JLabel("Завдання та прогрес", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 36));
        titleLabel.setForeground(new Color(72, 37, 120));

        topPanel.add(titleLabel, BorderLayout.CENTER);
        topPanel.add(new CurrencyPanel(gameState), BorderLayout.EAST);
        mainPanel.add(topPanel, BorderLayout.NORTH);

        mainPanel.add(buildProgressPanel(), BorderLayout.BEFORE_FIRST_LINE);

        tasksPanel = new JPanel(new GridLayout(0, 1, 10, 10));
        tasksPanel.setOpaque(false);
        tasksPanel.setBorder(BorderFactory.createEmptyBorder(20, 80, 35, 80));

        addTask("level1_clean", 1, "Прибрати квартиру", "Нагорода: +50 срібла, +20 XP", 50, 0, 20);
        addTask("level2_apartment", 2, "Обрати квартиру", "Нагорода: +80 срібла, +30 XP", 80, 0, 30);
        addTask("level3_bathroom", 3, "Купити санвузол або другу кімнату", "Нагорода: +1 золото, +60 XP", 0, 1, 60);
        addTask("level4_location", 4, "Відвідати парк або пляж", "Нагорода: +120 срібла, +70 XP", 120, 0, 70);
        addTask("level5_cinema", 5, "Відкрити кінотеатр", "Нагорода: +150 срібла, +90 XP", 150, 0, 90);
        addTask("level6_style", 6, "Купити предмет для краси або декору", "Нагорода: +1 золото, +110 XP", 0, 1, 110);
        addTask("level7_bedroom", 7, "Облаштувати спальню", "Нагорода: +200 срібла, +130 XP", 200, 0, 130);
        addTask("level8_fashion", 8, "Виконати модне завдання", "Нагорода: +2 золота, +150 XP", 0, 2, 150);
        addTask("level9_kitchen", 9, "Облаштувати кухню", "Нагорода: +250 срібла, +180 XP", 250, 0, 180);
        addTask("level10_finish", 10, "Завершити основний прогрес", "Нагорода: рідкісний предмет, +300 XP", 0, 3, 300);

        JScrollPane scrollPane = new JScrollPane(tasksPanel);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(new Color(252, 248, 240));
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        add(mainPanel, BorderLayout.CENTER);
    }

    public TasksFrame() {
        this(new GameState());
    }

    private JPanel buildProgressPanel() {
        JPanel panel = new JPanel(new BorderLayout(12, 6));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(0, 80, 12, 80));

        JLabel levelLabel = new JLabel("Рівень " + gameState.getLevel() + " — " + gameState.getLevelUnlockText());
        levelLabel.setFont(new Font("Arial", Font.BOLD, 16));
        levelLabel.setForeground(new Color(72, 37, 120));

        JProgressBar xpBar = new JProgressBar();
        if (gameState.getLevel() >= 10) {
            xpBar.setMaximum(1);
            xpBar.setValue(1);
            xpBar.setString("MAX");
        } else {
            xpBar.setMaximum(gameState.getXpToNextLevel());
            xpBar.setValue(gameState.getExperience());
            xpBar.setString(gameState.getExperience() + " / " + gameState.getXpToNextLevel() + " XP");
        }
        xpBar.setStringPainted(true);
        xpBar.setForeground(new Color(120, 190, 80));

        panel.add(levelLabel, BorderLayout.NORTH);
        panel.add(xpBar, BorderLayout.CENTER);

        return panel;
    }

    private void addTask(String id, int requiredLevel, String title, String reward, int silverReward, int goldReward, int xpReward) {
        tasksPanel.add(createTaskCard(id, requiredLevel, title, reward, silverReward, goldReward, xpReward));
    }

    private JPanel createTaskCard(String id, int requiredLevel, String title, String reward, int silverReward, int goldReward, int xpReward) {
        JPanel card = new JPanel(new BorderLayout(12, 0));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(120, 82, 160), 2),
                BorderFactory.createEmptyBorder(12, 18, 12, 18)
        ));

        JPanel textPanel = new JPanel(new GridLayout(2, 1));
        textPanel.setOpaque(false);

        boolean locked = gameState.getLevel() < requiredLevel;
        boolean done = gameState.isTaskCompleted(id);

        JLabel titleLabel = new JLabel((locked ? "🔒 " : done ? "✅ " : "⭐ ") + title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 19));
        titleLabel.setForeground(locked ? Color.GRAY : new Color(72, 37, 120));

        JLabel rewardLabel = new JLabel(locked ? "Доступно з " + requiredLevel + " рівня" : reward);
        rewardLabel.setFont(new Font("Arial", Font.PLAIN, 15));
        rewardLabel.setForeground(locked ? Color.GRAY : new Color(120, 82, 160));

        textPanel.add(titleLabel);
        textPanel.add(rewardLabel);

        JButton completeButton = new JButton(done ? "Отримано" : locked ? "Закрито" : "Виконати");
        completeButton.setFont(new Font("Arial", Font.BOLD, 15));
        completeButton.setBackground(new Color(255, 218, 130));
        completeButton.setForeground(new Color(72, 37, 120));
        completeButton.setFocusPainted(false);
        completeButton.setEnabled(!done && !locked);

        completeButton.addActionListener(e -> {
            gameState.addSilver(silverReward);
            gameState.addGold(goldReward);
            gameState.addExperience(xpReward);
            gameState.completeTask(id);
            SaveManager.saveGame(gameState);

            completeButton.setEnabled(false);
            completeButton.setText("Отримано");

            JOptionPane.showMessageDialog(
                    this,
                    "Завдання виконано!\n+ " + silverReward + " срібла\n+ " + goldReward + " золота\n+ " + xpReward + " XP",
                    "Нагорода",
                    JOptionPane.INFORMATION_MESSAGE
            );

            refreshFrame();
        });

        card.add(textPanel, BorderLayout.CENTER);
        card.add(completeButton, BorderLayout.EAST);

        return card;
    }

    private void refreshFrame() {
        getContentPane().removeAll();

        TasksFrame refreshed = new TasksFrame(gameState);
        setContentPane(refreshed.getContentPane());

        revalidate();
        repaint();
    }
}
