package vibetower.ui;

import vibetower.model.GameState;
import vibetower.model.HomeFrame;
import vibetower.model.Item;
import vibetower.model.Task;

import javax.swing.*;
import java.awt.*;

public class TasksFrame extends JFrame {

    private final GameState gameState;
    private JPanel tasksPanel;
    private JProgressBar xpBar;

    public TasksFrame(GameState gameState) {
        this.gameState = gameState;
        this.gameState.fixAfterLoad();

        setTitle("VibeTower — Завдання");
        setSize(900, 620);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(new Color(252, 248, 240));
        main.setBorder(BorderFactory.createEmptyBorder(15, 25, 15, 25));

        JButton homeButton = new JButton("🏠 Додому");
        homeButton.setFont(new Font("Arial", Font.BOLD, 16));
        homeButton.addActionListener(e -> {
            new HomeFrame(gameState).setVisible(true);
            dispose();
        });

        JLabel title = new JLabel(
                "Рівень " + gameState.getLevel() + " — " + gameState.getLevelUnlockText(),
                SwingConstants.CENTER
        );
        title.setFont(new Font("Arial", Font.BOLD, 20));
        title.setForeground(new Color(72, 37, 120));

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(homeButton, BorderLayout.WEST);
        top.add(title, BorderLayout.CENTER);

        xpBar = new JProgressBar(0, Math.max(1, gameState.getExperienceToNextLevel()));
        xpBar.setValue(gameState.getExperience());
        xpBar.setString(gameState.getExperience() + " / " + gameState.getExperienceToNextLevel() + " XP");
        xpBar.setStringPainted(true);

        JPanel header = new JPanel(new BorderLayout(0, 12));
        header.setOpaque(false);
        header.add(top, BorderLayout.NORTH);
        header.add(xpBar, BorderLayout.SOUTH);

        main.add(header, BorderLayout.NORTH);

        tasksPanel = new JPanel();
        tasksPanel.setLayout(new BoxLayout(tasksPanel, BoxLayout.Y_AXIS));
        tasksPanel.setBackground(new Color(252, 248, 240));

        JScrollPane scroll = new JScrollPane(tasksPanel);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(new Color(252, 248, 240));

        main.add(scroll, BorderLayout.CENTER);
        add(main, BorderLayout.CENTER);

        renderTasks();
    }

    private void renderTasks() {
        tasksPanel.removeAll();

        for (Task task : getTasks()) {
            tasksPanel.add(createTaskCard(task));
            tasksPanel.add(Box.createVerticalStrut(12));
        }

        tasksPanel.revalidate();
        tasksPanel.repaint();
    }

    private Task[] getTasks() {
        return new Task[]{
                new Task(
                        "clean_apartment",
                        "Прибрати квартиру",
                        "Повернись у квартиру та натисни «Відпочинок».",
                        1,
                        0,
                        20,
                        50,
                        0
                ),
                new Task(
                        "choose_apartment",
                        "Обрати квартиру",
                        "Обери стартову квартиру.",
                        2,
                        0,
                        100,
                        100,
                        0
                ),
                new Task(
                        "buy_bathroom",
                        "Купити санвузол або другу кімнату",
                        "Купи кімнату в квартирі.",
                        3,
                        10,
                        150,
                        120,
                        1
                ),
                new Task(
                        "start_repair",
                        "Увійти в режим ремонту",
                        "Повернись у квартиру та натисни «Режим ремонту».",
                        3,
                        5,
                        80,
                        80,
                        0
                ),
                new Task(
                        "buy_item",
                        "Купити предмет інтер'єру",
                        "Відкрий магазин та купи будь-який предмет.",
                        4,
                        5,
                        100,
                        100,
                        0
                ),
                new Task(
                        "place_item",
                        "Поставити предмет у кімнату",
                        "Відкрий інвентар у режимі ремонту та постав предмет.",
                        4,
                        5,
                        120,
                        120,
                        0,
                        new Item("Ваза-нагорода", "Декор", "🏺", 0, "silver", 1)
                ),
                new Task(
                        "rest_home",
                        "Відновити енергію вдома",
                        "Повернись у квартиру та відпочинь.",
                        1,
                        0,
                        30,
                        30,
                        0
                )
        };
    }

    private JPanel createTaskCard(Task task) {
        JPanel card = new JPanel(new BorderLayout());
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createLineBorder(new Color(120, 82, 160), 2));

        boolean locked = gameState.getLevel() < task.getRequiredLevel();
        boolean completed = gameState.isTaskCompleted(task.getId());
        boolean active = gameState.hasActiveTask(task.getId());

        JLabel text = new JLabel();
        text.setBorder(BorderFactory.createEmptyBorder(10, 18, 10, 10));
        text.setFont(new Font("Arial", Font.BOLD, 16));

        String statusIcon;
        String statusText;

        if (completed) {
            statusIcon = "✅";
            statusText = "Виконано";
            text.setForeground(new Color(110, 110, 110));
        } else if (locked) {
            statusIcon = "🔒";
            statusText = "Доступно з " + task.getRequiredLevel() + " рівня";
            text.setForeground(new Color(130, 130, 130));
        } else if (active) {
            statusIcon = "⏳";
            statusText = "Виконується";
            text.setForeground(new Color(72, 37, 120));
        } else {
            statusIcon = "⭐";
            statusText = "Доступно";
            text.setForeground(new Color(72, 37, 120));
        }

        text.setText(
                "<html>"
                        + statusIcon + " <b>" + task.getTitle() + "</b><br>"
                        + "<span style='font-size:11px;'>"
                        + task.getDescription()
                        + "<br>Нагорода: +" + task.getRewardSilver()
                        + " срібла, +" + task.getRewardGold()
                        + " золота, +" + task.getRewardXp() + " XP"
                        + "<br>Статус: " + statusText
                        + "</span></html>"
        );

        JButton button = new JButton();

        if (locked) {
            button.setText("Закрито");
            button.setEnabled(false);
        } else if (completed) {
            button.setText("Отримано");
            button.setEnabled(false);
        } else if (active) {
            button.setText("Перейти");
            button.addActionListener(e -> goToTask(task));
        } else {
            button.setText("Почати");
            button.addActionListener(e -> {
                gameState.startTask(task.getId());
                SaveManagerProxy.save(gameState);

                JOptionPane.showMessageDialog(
                        this,
                        "Завдання розпочато:\n\n"
                                + task.getTitle()
                                + "\n\n" + task.getDescription(),
                        "Завдання",
                        JOptionPane.INFORMATION_MESSAGE
                );

                goToTask(task);
            });
        }

        button.setPreferredSize(new Dimension(140, 45));
        button.setFont(new Font("Arial", Font.BOLD, 14));

        JPanel right = new JPanel(new GridBagLayout());
        right.setOpaque(false);
        right.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 18));
        right.add(button);

        card.add(text, BorderLayout.CENTER);
        card.add(right, BorderLayout.EAST);

        return card;
    }

    private void goToTask(Task task) {
        new HomeFrame(gameState).setVisible(true);
        dispose();
    }

    private static class SaveManagerProxy {
        static void save(GameState state) {
            vibetower.model.SaveManager.saveGame(state);
        }
    }
}