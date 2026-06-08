package vibetower.ui;

import vibetower.model.GameState;
import vibetower.model.Item;
import vibetower.model.SaveManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class RepairFrame extends JFrame {

    private final GameState gameState;
    private JPanel roomPanel;
    private JComboBox<String> wallpaperBox;
    private JComboBox<String> floorBox;

    public RepairFrame(GameState gameState) {
        this.gameState = gameState;
        this.gameState.fixAfterLoad();

        setTitle("VibeTower — Режим ремонту");
        setSize(1050, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(252, 248, 240));

        mainPanel.add(buildHeader(), BorderLayout.NORTH);
        mainPanel.add(buildSidePanel(), BorderLayout.WEST);
        mainPanel.add(buildRoomPanel(), BorderLayout.CENTER);

        add(mainPanel, BorderLayout.CENTER);
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(BorderFactory.createEmptyBorder(18, 25, 12, 25));

        JLabel title = new JLabel("Режим ремонту", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 34));
        title.setForeground(new Color(72, 37, 120));

        header.add(title, BorderLayout.CENTER);
        header.add(new CurrencyPanel(gameState), BorderLayout.EAST);
        return header;
    }

    private JPanel buildSidePanel() {
        JPanel side = new JPanel(null);
        side.setPreferredSize(new Dimension(285, 0));
        side.setBackground(new Color(245, 238, 255));
        side.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel wallpaperLabel = new JLabel("Шпалери");
        wallpaperLabel.setBounds(20, 25, 230, 25);
        wallpaperLabel.setFont(new Font("Arial", Font.BOLD, 17));
        wallpaperLabel.setForeground(new Color(72, 37, 120));
        side.add(wallpaperLabel);

        wallpaperBox = new JComboBox<>(new String[]{"Білі шпалери", "Персикові шпалери", "Блакитні шпалери", "Квіткові шпалери"});
        wallpaperBox.setBounds(20, 55, 235, 34);
        wallpaperBox.setSelectedItem(gameState.getWallpaper());
        side.add(wallpaperBox);

        JLabel floorLabel = new JLabel("Підлога");
        floorLabel.setBounds(20, 105, 230, 25);
        floorLabel.setFont(new Font("Arial", Font.BOLD, 17));
        floorLabel.setForeground(new Color(72, 37, 120));
        side.add(floorLabel);

        floorBox = new JComboBox<>(new String[]{"Коричневий паркет", "Світлий паркет", "Темна підлога", "Плитка"});
        floorBox.setBounds(20, 135, 235, 34);
        floorBox.setSelectedItem(gameState.getFloor());
        side.add(floorBox);

        JButton applyStyleButton = createMenuButton("Застосувати стиль");
        applyStyleButton.setBounds(20, 185, 235, 40);
        applyStyleButton.addActionListener(e -> {
            gameState.setWallpaper((String) wallpaperBox.getSelectedItem());
            gameState.setFloor((String) floorBox.getSelectedItem());
            redrawRoomItems();
        });
        side.add(applyStyleButton);

        JButton roomsButton = createMenuButton("Купити кімнату");
        roomsButton.setBounds(20, 240, 235, 40);
        roomsButton.addActionListener(e -> showRoomShop());
        side.add(roomsButton);

        JButton saveButton = createMenuButton("Зберегти ремонт");
        saveButton.setBounds(20, 455, 235, 40);
        saveButton.addActionListener(e -> {
            gameState.setWallpaper((String) wallpaperBox.getSelectedItem());
            gameState.setFloor((String) floorBox.getSelectedItem());
            SaveManager.saveGame(gameState);
            JOptionPane.showMessageDialog(this, "Ремонт збережено!");
        });
        side.add(saveButton);

        JButton cancelButton = createMenuButton("Скасувати");
        cancelButton.setBounds(20, 510, 235, 40);
        cancelButton.addActionListener(e -> dispose());
        side.add(cancelButton);

        JTextArea hint = new JTextArea("Перетягуй меблі мишкою по кімнаті. Предмети не можна винести за межі кімнати.");
        hint.setBounds(20, 300, 235, 120);
        hint.setLineWrap(true);
        hint.setWrapStyleWord(true);
        hint.setEditable(false);
        hint.setOpaque(false);
        hint.setFont(new Font("Arial", Font.BOLD, 14));
        hint.setForeground(new Color(100, 70, 140));
        side.add(hint);

        return side;
    }

    private JPanel buildRoomPanel() {
        roomPanel = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                paintRoom((Graphics2D) g);
            }
        };
        roomPanel.setBackground(new Color(235, 245, 255));
        roomPanel.setBorder(BorderFactory.createLineBorder(new Color(72, 37, 120), 4));

        redrawRoomItems();
        return roomPanel;
    }

    private void paintRoom(Graphics2D g2) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Color wall = new Color(248, 248, 240);
        if ("Персикові шпалери".equals(gameState.getWallpaper())) wall = new Color(255, 230, 215);
        if ("Блакитні шпалери".equals(gameState.getWallpaper())) wall = new Color(220, 235, 250);
        if ("Квіткові шпалери".equals(gameState.getWallpaper())) wall = new Color(250, 235, 245);

        Color floor = new Color(150, 100, 60);
        if ("Світлий паркет".equals(gameState.getFloor())) floor = new Color(205, 165, 110);
        if ("Темна підлога".equals(gameState.getFloor())) floor = new Color(85, 55, 40);
        if ("Плитка".equals(gameState.getFloor())) floor = new Color(205, 205, 195);

        int w = roomPanel.getWidth();
        int h = roomPanel.getHeight();

        g2.setColor(wall);
        g2.fillRect(0, 0, w, h - 170);

        g2.setColor(floor);
        g2.fillRect(0, h - 170, w, 170);

        g2.setColor(new Color(150, 150, 160));
        g2.setStroke(new BasicStroke(3));
        g2.drawLine(0, h - 170, w, h - 170);

        g2.setColor(new Color(150, 210, 245));
        g2.fillRoundRect(w - 190, 70, 100, 120, 10, 10);
        g2.setColor(Color.WHITE);
        g2.setStroke(new BasicStroke(4));
        g2.drawLine(w - 140, 70, w - 140, 190);
        g2.drawLine(w - 190, 130, w - 90, 130);
    }

    private void redrawRoomItems() {
        if (roomPanel == null) return;

        roomPanel.removeAll();

        for (Item item : gameState.getPlacedItems()) {
            JLabel itemLabel = createDraggableItem(item);
            roomPanel.add(itemLabel);
        }

        roomPanel.revalidate();
        roomPanel.repaint();
    }

    private JLabel createDraggableItem(Item item) {
        JLabel label = new JLabel(item.getIcon() + " " + item.getName(), SwingConstants.CENTER);
        label.setBounds(item.getX(), item.getY(), 155, 52);
        label.setOpaque(true);
        label.setBackground(new Color(255, 225, 165));
        label.setForeground(new Color(72, 37, 120));
        label.setFont(new Font("Arial", Font.BOLD, 15));
        label.setBorder(BorderFactory.createLineBorder(new Color(120, 82, 160), 2));
        label.setCursor(new Cursor(Cursor.MOVE_CURSOR));

        MouseAdapter adapter = new MouseAdapter() {
            private int offsetX;
            private int offsetY;

            @Override
            public void mousePressed(MouseEvent e) {
                offsetX = e.getX();
                offsetY = e.getY();
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                int newX = label.getX() + e.getX() - offsetX;
                int newY = label.getY() + e.getY() - offsetY;

                newX = Math.max(0, Math.min(roomPanel.getWidth() - label.getWidth(), newX));
                newY = Math.max(0, Math.min(roomPanel.getHeight() - label.getHeight(), newY));

                label.setLocation(newX, newY);
                item.setPosition(newX, newY);
            }
        };

        label.addMouseListener(adapter);
        label.addMouseMotionListener(adapter);
        return label;
    }

    private void showRoomShop() {
        String[] rooms = {"Друга кімната", "Санвузол", "Спальня", "Кухня", "Велика кімната"};
        String room = (String) JOptionPane.showInputDialog(
                this,
                "Обери кімнату для покупки:",
                "Розширення квартири",
                JOptionPane.PLAIN_MESSAGE,
                null,
                rooms,
                rooms[0]
        );

        if (room != null) {
            String result = gameState.buyRoom(room);
            JOptionPane.showMessageDialog(this, result);
        }
    }

    private JButton createMenuButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 15));
        button.setBackground(new Color(255, 218, 130));
        button.setForeground(new Color(72, 37, 120));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(new Color(120, 82, 160), 2));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }
}
