import javax.swing.*;
import java.awt.*;

public class RepairFrame extends JFrame {
    private GameState gameState;
    private JPanel roomPanel;

    public RepairFrame(GameState gameState) {
        this.gameState = gameState;

        setTitle("VibeTower — Режим ремонту");
        setSize(850, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        add(createTopPanel(), BorderLayout.NORTH);
        add(createRoomPanel(), BorderLayout.CENTER);
        add(createBottomPanel(), BorderLayout.SOUTH);
    }

    private JPanel createTopPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(45, 62, 90));
        panel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JLabel title = new JLabel("Режим ремонту квартири");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Arial", Font.BOLD, 22));

        JLabel info = new JLabel(
                "Квартира: " + (gameState.getApartment() == null ? "не обрана" : gameState.getApartment())
        );
        info.setForeground(Color.WHITE);
        info.setFont(new Font("Arial", Font.BOLD, 14));

        panel.add(title, BorderLayout.WEST);
        panel.add(info, BorderLayout.EAST);

        return panel;
    }

    private JPanel createRoomPanel() {
        roomPanel = new JPanel();
        roomPanel.setLayout(new GridLayout(0, 3, 15, 15));
        roomPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        roomPanel.setBackground(new Color(245, 235, 220));

        if (gameState.getApartment() == null) {
            JLabel label = new JLabel("Спочатку потрібно обрати квартиру.", SwingConstants.CENTER);
            label.setFont(new Font("Arial", Font.BOLD, 20));
            roomPanel.setLayout(new BorderLayout());
            roomPanel.add(label, BorderLayout.CENTER);
            return roomPanel;
        }

        if (gameState.getPlacedFurniture().isEmpty()) {
            JLabel label = new JLabel("У квартирі поки що немає меблів.", SwingConstants.CENTER);
            label.setFont(new Font("Arial", Font.BOLD, 20));
            roomPanel.setLayout(new BorderLayout());
            roomPanel.add(label, BorderLayout.CENTER);
        } else {
            for (String item : gameState.getPlacedFurniture()) {
                JPanel furnitureCard = new JPanel(new BorderLayout());
                furnitureCard.setBackground(Color.WHITE);
                furnitureCard.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(150, 150, 150)),
                        BorderFactory.createEmptyBorder(20, 10, 20, 10)
                ));

                JLabel itemLabel = new JLabel("<html><center>" + item + "</center></html>", SwingConstants.CENTER);
                itemLabel.setFont(new Font("Arial", Font.BOLD, 16));

                furnitureCard.add(itemLabel, BorderLayout.CENTER);
                roomPanel.add(furnitureCard);
            }
        }

        return roomPanel;
    }

    private JPanel createBottomPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(2, 3, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton inventoryButton = new JButton("Взяти з інвентаря");
        JButton wallpaperButton = new JButton("Змінити шпалери");
        JButton floorButton = new JButton("Змінити підлогу");
        JButton saveButton = new JButton("Зберегти ремонт");
        JButton cancelButton = new JButton("Скасувати зміни");
        JButton backButton = new JButton("До квартири");

        panel.add(inventoryButton);
        panel.add(wallpaperButton);
        panel.add(floorButton);
        panel.add(saveButton);
        panel.add(cancelButton);
        panel.add(backButton);

        inventoryButton.addActionListener(e -> {
            new InventoryFrame(gameState).setVisible(true);
            dispose();
        });

        wallpaperButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(
                    this,
                    "Шпалери змінено. Для демо-версії зміна показана як дія ремонту."
            );
        });

        floorButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(
                    this,
                    "Підлогу змінено. Для демо-версії зміна показана як дія ремонту."
            );
        });

        saveButton.addActionListener(e -> {
            SaveManager.saveGame(gameState);
            JOptionPane.showMessageDialog(this, "Ремонт збережено!");
        });

        cancelButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(
                    this,
                    "Зміни скасовано. У демо-версії повертаємося до квартири."
            );
            new ApartmentFrame(gameState).setVisible(true);
            dispose();
        });

        backButton.addActionListener(e -> {
            new ApartmentFrame(gameState).setVisible(true);
            dispose();
        });

        return panel;
    }
}