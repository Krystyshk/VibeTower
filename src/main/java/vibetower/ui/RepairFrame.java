package vibetower.ui;

import vibetower.model.GameState;

import javax.swing.*;
import java.awt.*;

public class RepairFrame extends JFrame {

    private GameState gameState;

    public RepairFrame(GameState gameState) {
        this.gameState = gameState;

        setTitle("VibeTower — Режим ремонту");
        setSize(900, 620);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setLayout(new BorderLayout());

        JPanel topPanel = new JPanel();
        topPanel.setOpaque(false);
        topPanel.setLayout(new BorderLayout());
        topPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 10, 30));

        JLabel titleLabel = new JLabel("Режим ремонту", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 38));
        titleLabel.setForeground(new Color(72, 37, 120));

        topPanel.add(titleLabel, BorderLayout.CENTER);
        topPanel.add(new CurrencyPanel(gameState), BorderLayout.EAST);

        mainPanel.add(topPanel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel();
        centerPanel.setBackground(Color.WHITE);
        centerPanel.setLayout(new GridLayout(2, 2, 30, 30));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(90, 120, 120, 120));

        ImageButton wallpaperButton = new ImageButton("/змінити шпалери.png", 300, 75);
        ImageButton floorButton = new ImageButton("/змінити підлогу.png", 300, 75);
        ImageButton saveButton = new ImageButton("/зберегти ремонт.png", 300, 75);
        ImageButton cancelButton = new ImageButton("/скасувати.png", 300, 75);

        wallpaperButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Шпалери змінено!");
        });

        floorButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Підлогу змінено!");
        });

        saveButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Ремонт збережено!");
        });

        cancelButton.addActionListener(e -> {
            dispose();
        });

        centerPanel.add(createButtonCell(wallpaperButton));
        centerPanel.add(createButtonCell(floorButton));
        centerPanel.add(createButtonCell(saveButton));
        centerPanel.add(createButtonCell(cancelButton));

        mainPanel.add(centerPanel, BorderLayout.CENTER);

        add(mainPanel, BorderLayout.CENTER);
    }

    public RepairFrame() {
        this(new GameState());
    }

    private JPanel createButtonCell(ImageButton button) {
        JPanel cell = new JPanel();
        cell.setOpaque(false);
        cell.setLayout(new GridBagLayout());
        cell.add(button);
        return cell;
    }
}