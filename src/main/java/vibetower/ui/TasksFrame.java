package vibetower.ui;

import vibetower.model.GameState;
import vibetower.model.Task;
import vibetower.model.SaveManager;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class TasksFrame extends JFrame {
    private GameState gameState;
    private ArrayList<Task> tasks;

    public TasksFrame(GameState gameState) {
        this.gameState = gameState;
        this.tasks = createTasks();

        setTitle("VibeTower — Завдання");
        setSize(850, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        add(createTopPanel(), BorderLayout.NORTH);
        add(createTasksPanel(), BorderLayout.CENTER);
        add(createBottomPanel(), BorderLayout.SOUTH);
    }

    private JPanel createTopPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(45, 62, 90));
        panel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JLabel title = new JLabel("Завдання та прогрес");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Arial", Font.BOLD, 22));

        JLabel info = new JLabel(
                "Рівень: " + gameState.getLevel()
                        + " | XP: " + gameState.getXp()
                        + " | Срібло: " + gameState.getSilver()
                        + " | Золото: " + gameState.getGold()
        );

        info.setForeground(Color.WHITE);
        info.setFont(new Font("Arial", Font.BOLD, 14));

        panel.add(title, BorderLayout.WEST);
        panel.add(info, BorderLayout.EAST);

        return panel;
    }

    private JScrollPane createTasksPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(0, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        panel.setBackground(new Color(238, 241, 245));

        for (Task task : tasks) {
            panel.add(createTaskCard(task));
        }

        return new JScrollPane(panel);
    }

    private JPanel createTaskCard(Task task) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(180, 180, 180)),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)
        ));

        boolean completed = gameState.isTaskCompleted(task.getName());
        boolean locked = gameState.getLevel() < task.getMinLevel();

        JLabel text = new JLabel(
                "<html>"
                        + "<b>" + task.getName() + "</b><br>"
                        + "Опис: " + task.getDescription() + "<br>"
                        + "Доступно з рівня: " + task.getMinLevel() + "<br>"
                        + "Нагорода: " + task.getXpReward() + " XP, "
                        + task.getSilverReward() + " срібла, "
                        + task.getGoldReward() + " золота<br>"
                        + "Статус: " + getStatusText(completed, locked)
                        + "</html>"
        );

        JButton completeButton = new JButton("Виконати");

        if (completed) {
            completeButton.setText("Виконано");
            completeButton.setEnabled(false);
        }

        if (locked) {
            completeButton.setText("Заблоковано");
            completeButton.setEnabled(false);
        }

        completeButton.addActionListener(e -> completeTask(task));

        card.add(text, BorderLayout.CENTER);
        card.add(completeButton, BorderLayout.EAST);

        return card;
    }

    private String getStatusText(boolean completed, boolean locked) {
        if (completed) {
            return "нагороду отримано";
        }

        if (locked) {
            return "не відкрито";
        }

        return "доступне";
    }

    private void completeTask(Task task) {
        if (gameState.getLevel() < task.getMinLevel()) {
            JOptionPane.showMessageDialog(this, "Це завдання ще недоступне.");
            return;
        }

        if (gameState.isTaskCompleted(task.getName())) {
            JOptionPane.showMessageDialog(this, "Це завдання вже виконано.");
            return;
        }

        gameState.addXp(task.getXpReward());
        gameState.addSilver(task.getSilverReward());
        gameState.addGold(task.getGoldReward());
        gameState.completeTask(task.getName());

        SaveManager.saveGame(gameState);

        JOptionPane.showMessageDialog(
                this,
                "Завдання виконано!\n"
                        + "Отримано: "
                        + task.getXpReward() + " XP, "
                        + task.getSilverReward() + " срібла, "
                        + task.getGoldReward() + " золота."
        );

        new TasksFrame(gameState).setVisible(true);
        dispose();
    }

    private JPanel createBottomPanel() {
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton levelsButton = new JButton("Таблиця рівнів");
        JButton backButton = new JButton("До меню");

        panel.add(levelsButton);
        panel.add(backButton);

        levelsButton.addActionListener(e -> showLevelsTable());

        backButton.addActionListener(e -> {
            new HomeFrame(gameState).setVisible(true);
            dispose();
        });

        return panel;
    }

    private void showLevelsTable() {
        String text =
                "1 → 2: 100 XP — вибір власної квартири\n"
                        + "2 → 3: 250 XP — робота офіціантом і базовий ремонт\n"
                        + "3 → 4: 450 XP — парк і пляж\n"
                        + "4 → 5: 700 XP — кінотеатр\n"
                        + "5 → 6: 1000 XP — салон краси\n"
                        + "6 → 7: 1350 XP — нові меблі та спальня\n"
                        + "7 → 8: 1750 XP — модні завдання та новий декор\n"
                        + "8 → 9: 2200 XP — кухня та велика квартира\n"
                        + "9 → 10: 2700 XP — преміальні предмети та фінальні завдання\n"
                        + "10 рівень — вільний режим гри";

        JOptionPane.showMessageDialog(this, text, "Таблиця 10 рівнів", JOptionPane.INFORMATION_MESSAGE);
    }

    private ArrayList<Task> createTasks() {
        ArrayList<Task> list = new ArrayList<>();

        list.add(new Task(
                "Завершити навчання",
                "Пройти стартове навчання та прибрати сміття у сусіда.",
                1,
                100,
                100,
                0
        ));

        list.add(new Task(
                "Обрати квартиру",
                "Обрати першу власну квартиру персонажа.",
                2,
                250,
                150,
                0
        ));

        list.add(new Task(
                "Купити другу кімнату",
                "Відкрити другу кімнату та підготувати її до ремонту.",
                3,
                450,
                200,
                2
        ));

        list.add(new Task(
                "Відвідати парк і пляж",
                "Виконати інтерактиви у відкритих локаціях.",
                4,
                700,
                250,
                0
        ));

        list.add(new Task(
                "Сходити в кінотеатр",
                "Пройти кіно-завдання та отримати нагороду.",
                5,
                1000,
                300,
                0
        ));

        list.add(new Task(
                "Скористатися салоном краси",
                "Змінити зовнішність персонажа.",
                6,
                1350,
                350,
                1
        ));

        list.add(new Task(
                "Облаштувати спальню",
                "Купити та розставити меблі для спальні.",
                7,
                1750,
                400,
                0
        ));

        list.add(new Task(
                "Створити модний образ",
                "Виконати модне завдання та отримати золото.",
                8,
                2200,
                450,
                3
        ));

        list.add(new Task(
                "Облаштувати кухню",
                "Купити кухонні меблі та техніку.",
                9,
                2700,
                500,
                5
        ));

        list.add(new Task(
                "Завершити основний прогрес",
                "Досягти максимального рівня першої версії гри.",
                10,
                0,
                1000,
                10
        ));

        return list;
    }
}