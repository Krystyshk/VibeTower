package vibetower.ui;

import vibetower.model.GameState;

import javax.swing.*;
import java.awt.*;

public class TasksFrame extends JFrame {

    private GameState gameState;
    private JPanel topPanel;
    private JPanel tasksPanel;

    public TasksFrame(GameState gameState) {
        this.gameState = gameState;

        setTitle("VibeTower — Завдання");
        setSize(850, 620);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setLayout(new BorderLayout());

        topPanel = new JPanel();
        topPanel.setOpaque(false);
        topPanel.setLayout(new BorderLayout());
        topPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 10, 30));

        JLabel titleLabel = new JLabel("Завдання", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 38));
        titleLabel.setForeground(new Color(72, 37, 120));

        topPanel.add(titleLabel, BorderLayout.CENTER);
        topPanel.add(new CurrencyPanel(gameState), BorderLayout.EAST);

        mainPanel.add(topPanel, BorderLayout.NORTH);

        tasksPanel = new JPanel();
        tasksPanel.setOpaque(false);
        tasksPanel.setLayout(new GridLayout(5, 1, 10, 10));
        tasksPanel.setBorder(BorderFactory.createEmptyBorder(30, 100, 60, 100));

        tasksPanel.add(createTaskCard(
                "1. Прибрати квартиру",
                "Нагорода: +50 срібла, +20 XP",
                50,
                20
        ));

        tasksPanel.add(createTaskCard(
                "2. Купити предмет інтер'єру",
                "Нагорода: +30 XP",
                0,
                30
        ));

        tasksPanel.add(createTaskCard(
                "3. Відкрити інвентар",
                "Нагорода: +10 XP",
                0,
                10
        ));

        tasksPanel.add(createTaskCard(
                "4. Зайти в режим ремонту",
                "Нагорода: +15 XP",
                0,
                15
        ));

        tasksPanel.add(createTaskCard(
                "5. Зберегти прогрес",
                "Нагорода: +5 XP",
                0,
                5
        ));

        mainPanel.add(tasksPanel, BorderLayout.CENTER);

        add(mainPanel, BorderLayout.CENTER);
    }

    public TasksFrame() {
        this(new GameState());
    }

    private JPanel createTaskCard(String title, String reward, int silverReward, int xpReward) {
        JPanel card = new JPanel();
        card.setBackground(new Color(250, 250, 250));
        card.setLayout(new BorderLayout());
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(120, 82, 160), 2),
                BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));

        JPanel textPanel = new JPanel();
        textPanel.setOpaque(false);
        textPanel.setLayout(new GridLayout(2, 1));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(new Color(72, 37, 120));

        JLabel rewardLabel = new JLabel(reward);
        rewardLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        rewardLabel.setForeground(new Color(120, 82, 160));

        textPanel.add(titleLabel);
        textPanel.add(rewardLabel);

        JButton completeButton = new JButton("Виконати");
        completeButton.setFont(new Font("Arial", Font.BOLD, 16));
        completeButton.setBackground(new Color(255, 218, 130));
        completeButton.setForeground(new Color(72, 37, 120));
        completeButton.setFocusPainted(false);

        completeButton.addActionListener(e -> {
            gameState.addSilver(silverReward);
            gameState.addExperience(xpReward);

            completeButton.setEnabled(false);
            completeButton.setText("Виконано");

            JOptionPane.showMessageDialog(
                    this,
                    "Завдання виконано!\nОтримано: +" + silverReward + " срібла, +" + xpReward + " XP",
                    "Нагорода",
                    JOptionPane.INFORMATION_MESSAGE
            );

            refreshCurrency();
        });

        card.add(textPanel, BorderLayout.CENTER);
        card.add(completeButton, BorderLayout.EAST);

        return card;
    }

    private void refreshCurrency() {
        topPanel.remove(1);
        topPanel.add(new CurrencyPanel(gameState), BorderLayout.EAST);
        topPanel.revalidate();
        topPanel.repaint();
    }
}