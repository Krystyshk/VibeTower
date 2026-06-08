package vibetower.model;

import vibetower.ui.InventoryFrame;
import vibetower.ui.RepairFrame;
import vibetower.ui.ShopFrame;
import vibetower.ui.TasksFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Path2D;
import java.io.File;

public class HomeFrame extends JFrame {

    private final GameState gameState;

    private JLabel backgroundLabel;
    private String currentBackgroundPath;
    private double zoomScale = 1.0;

    private JLabel xpValueLabel;
    private JLabel energyValueLabel;
    private JLabel silverValueLabel;
    private JLabel goldValueLabel;

    private static final int WINDOW_WIDTH = 1200;
    private static final int WINDOW_HEIGHT = 720;

    public HomeFrame(GameState gameState) {
        this.gameState = gameState;
        this.gameState.fixAfterLoad();

        setTitle("VibeTower — Квартира");
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
        setContentPane(layeredPane);

        backgroundLabel = createApartmentBackground();
        backgroundLabel.setBounds(0, 0, WINDOW_WIDTH, WINDOW_HEIGHT);
        layeredPane.add(backgroundLabel, Integer.valueOf(0));

        addTopInfo(layeredPane);
        addLeftButtons(layeredPane);
        addRightButtons(layeredPane);
        addBottomButtons(layeredPane);

        updateTopInfo();
    }

    private JLabel createApartmentBackground() {
        String apartmentType = gameState.getApartmentType();

        if (apartmentType.equalsIgnoreCase("pink")) {
            currentBackgroundPath = "src/main/resources/apartment_pink.jpg";
        } else if (apartmentType.equalsIgnoreCase("peach")) {
            currentBackgroundPath = "src/main/resources/komnata.jpg";
        } else {
            currentBackgroundPath = "src/main/resources/apartment_blue.jpg";
        }

        ImageIcon icon = new ImageIcon(currentBackgroundPath);

        if (icon.getIconWidth() > 0) {
            Image scaledImage = icon.getImage().getScaledInstance(
                    WINDOW_WIDTH,
                    WINDOW_HEIGHT,
                    Image.SCALE_SMOOTH
            );
            return new JLabel(new ImageIcon(scaledImage));
        }

        JLabel fallback = new JLabel("Квартира", SwingConstants.CENTER);
        fallback.setOpaque(true);
        fallback.setBackground(new Color(235, 225, 215));
        fallback.setFont(new Font("Arial", Font.BOLD, 54));
        fallback.setForeground(Color.WHITE);
        return fallback;
    }

    private void updateBackgroundZoom() {
        ImageIcon icon = new ImageIcon(currentBackgroundPath);

        if (icon.getIconWidth() <= 0) {
            return;
        }

        int newWidth = (int) (WINDOW_WIDTH * zoomScale);
        int newHeight = (int) (WINDOW_HEIGHT * zoomScale);

        Image scaledImage = icon.getImage().getScaledInstance(
                newWidth,
                newHeight,
                Image.SCALE_SMOOTH
        );

        backgroundLabel.setIcon(new ImageIcon(scaledImage));

        int x = (WINDOW_WIDTH - newWidth) / 2;
        int y = (WINDOW_HEIGHT - newHeight) / 2;

        backgroundLabel.setBounds(x, y, newWidth, newHeight);
        backgroundLabel.revalidate();
        backgroundLabel.repaint();
    }

    private void zoomInRoom() {
        if (zoomScale < 1.25) {
            zoomScale += 0.05;
            updateBackgroundZoom();
        }
    }

    private void zoomOutRoom() {
        if (zoomScale > 0.90) {
            zoomScale -= 0.05;
            updateBackgroundZoom();
        }
    }

    private void addTopInfo(JLayeredPane layeredPane) {
        JPanel topPanel = new JPanel(null);
        topPanel.setBounds(0, 0, WINDOW_WIDTH, 80);
        topPanel.setOpaque(false);
        layeredPane.add(topPanel, Integer.valueOf(5));

        // Велика корона зліва
        JLabel crownIcon = createIconLabel(
                70,
                58,
                "src/main/resources/crown.png"
        );
        crownIcon.setBounds(20, 10, 70, 58);
        topPanel.add(crownIcon);

        JLabel titleLabel = new JLabel("Твоя квартира");
        titleLabel.setBounds(95, 13, 240, 24);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        topPanel.add(titleLabel);

        JLabel comfortLabel = new JLabel("Комфорт: 0 ⭐");
        comfortLabel.setBounds(95, 38, 220, 24);
        comfortLabel.setFont(new Font("Arial", Font.BOLD, 18));
        comfortLabel.setForeground(Color.WHITE);
        topPanel.add(comfortLabel);

        // Верхні показники посунуті вправо
        DiamondBadge levelBadge = new DiamondBadge(String.valueOf(gameState.getLevel()));
        levelBadge.setBounds(640, 9, 48, 48);
        topPanel.add(levelBadge);

        JLabel starIcon = createTextIcon("⭐");
        starIcon.setBounds(700, 17, 26, 26);
        topPanel.add(starIcon);

        JLabel xpText = new JLabel("XP");
        xpText.setBounds(730, 15, 35, 28);
        xpText.setFont(new Font("Arial", Font.BOLD, 19));
        xpText.setForeground(new Color(45, 35, 120));
        topPanel.add(xpText);

        xpValueLabel = createTopValueLabel();
        xpValueLabel.setFont(new Font("Arial", Font.BOLD, 19));
        xpValueLabel.setBounds(768, 15, 100, 28);
        topPanel.add(xpValueLabel);

        JLabel energyIcon = createTextIcon("⚡");
        energyIcon.setBounds(875, 17, 26, 26);
        topPanel.add(energyIcon);

        energyValueLabel = createTopValueLabel();
        energyValueLabel.setFont(new Font("Arial", Font.BOLD, 19));
        energyValueLabel.setBounds(905, 15, 55, 28);
        topPanel.add(energyValueLabel);

        JLabel silverIcon = createIconLabel(
                28,
                28,
                "src/main/resources/serebro.png"
        );
        silverIcon.setBounds(975, 15, 28, 28);
        topPanel.add(silverIcon);

        silverValueLabel = createTopValueLabel();
        silverValueLabel.setFont(new Font("Arial", Font.BOLD, 19));
        silverValueLabel.setBounds(1010, 15, 70, 28);
        topPanel.add(silverValueLabel);

        JLabel goldIcon = createIconLabel(
                28,
                28,
                "src/main/resources/zoloto.png"
        );
        goldIcon.setBounds(1090, 15, 28, 28);
        topPanel.add(goldIcon);

        goldValueLabel = createTopValueLabel();
        goldValueLabel.setFont(new Font("Arial", Font.BOLD, 19));
        goldValueLabel.setBounds(1125, 15, 55, 28);
        topPanel.add(goldValueLabel);
    }

    private void addLeftButtons(JLayeredPane layeredPane) {
        int x = 18;
        int startY = 255;
        int gap = 16;

        int buttonWidth = 160;
        int buttonHeight = 88;

        JButton restButton = createImageButton(
                buttonWidth,
                buttonHeight,
                "Відпочинок",
                "src/main/resources/відпочинок.png"
        );
        restButton.setBounds(x, startY, buttonWidth, buttonHeight);
        layeredPane.add(restButton, Integer.valueOf(6));

        JButton mapButton = createImageButton(
                buttonWidth,
                buttonHeight,
                "Карта",
                "src/main/resources/карта.png"
        );
        mapButton.setBounds(x, startY + buttonHeight + gap, buttonWidth, buttonHeight);
        layeredPane.add(mapButton, Integer.valueOf(6));

        JButton tasksButton = createImageButton(
                buttonWidth,
                buttonHeight,
                "Завдання",
                "src/main/resources/завдання.png"
        );
        tasksButton.setBounds(x, startY + (buttonHeight + gap) * 2, buttonWidth, buttonHeight);
        layeredPane.add(tasksButton, Integer.valueOf(6));

        restButton.addActionListener(e -> restPlayer());

        mapButton.addActionListener(e -> {
            new MapFrame(gameState).setVisible(true);
            dispose();
        });

        tasksButton.addActionListener(e -> {
            new TasksFrame(gameState).setVisible(true);
            dispose();
        });
    }

    private void addRightButtons(JLayeredPane layeredPane) {
        int buttonSize = 62;
        int x = WINDOW_WIDTH - buttonSize - 14;
        int startY = 240;
        int gap = 10;

        JButton zoomPlusButton = createImageButton(
                buttonSize,
                buttonSize,
                "Збільшити",
                "src/main/resources/zoom_plus.png"
        );
        zoomPlusButton.setBounds(x, startY, buttonSize, buttonSize);
        layeredPane.add(zoomPlusButton, Integer.valueOf(6));

        JButton zoomMinusButton = createImageButton(
                buttonSize,
                buttonSize,
                "Зменшити",
                "src/main/resources/zoom_minus.png"
        );
        zoomMinusButton.setBounds(x, startY + buttonSize + gap, buttonSize, buttonSize);
        layeredPane.add(zoomMinusButton, Integer.valueOf(6));

        JButton editButton = createImageButton(
                buttonSize,
                buttonSize,
                "Редагувати",
                "src/main/resources/edit_space.png"
        );
        editButton.setBounds(x, startY + (buttonSize + gap) * 2, buttonSize, buttonSize);
        layeredPane.add(editButton, Integer.valueOf(6));

        zoomPlusButton.addActionListener(e -> zoomInRoom());

        zoomMinusButton.addActionListener(e -> zoomOutRoom());

        editButton.addActionListener(e -> {
            new RepairFrame(gameState).setVisible(true);
            dispose();
        });
    }

    private void addBottomButtons(JLayeredPane layeredPane) {
        RoundedPanel bottomPanel = new RoundedPanel(22, new Color(125, 95, 65, 150));
        bottomPanel.setLayout(null);

        // Коричневый блок стал меньше и аккуратнее
        bottomPanel.setBounds(270, 595, 660, 80);
        bottomPanel.setOpaque(false);
        layeredPane.add(bottomPanel, Integer.valueOf(5));

        int buttonWidth = 200;
        int buttonHeight = 66;
        int gap = 18;

        // Все кнопки на одной высоте
        int y = 7;

        JButton shopButton = createImageButton(
                buttonWidth,
                buttonHeight,
                "Магазин",
                "src/main/resources/магазин інтер.png"
        );
        shopButton.setBounds(15, y, buttonWidth, buttonHeight);
        bottomPanel.add(shopButton);

        JButton inventoryButton = createImageButton(
                buttonWidth,
                buttonHeight,
                "Інвентар",
                "src/main/resources/інвентар.png"
        );
        inventoryButton.setBounds(15 + buttonWidth + gap, y, buttonWidth, buttonHeight);
        bottomPanel.add(inventoryButton);

        JButton repairButton = createImageButton(
                buttonWidth,
                buttonHeight,
                "Режим ремонту",
                "src/main/resources/режим ремонту.png"
        );
        repairButton.setBounds(15 + (buttonWidth + gap) * 2, y, buttonWidth, buttonHeight);
        bottomPanel.add(repairButton);

        shopButton.addActionListener(e -> {
            new ShopFrame(gameState).setVisible(true);
            dispose();
        });

        inventoryButton.addActionListener(e -> {
            new InventoryFrame(gameState).setVisible(true);
            dispose();
        });

        repairButton.addActionListener(e -> {
            new RepairFrame(gameState).setVisible(true);
            dispose();
        });
    }

    private JButton createImageButton(int width, int height, String fallbackText, String... imagePaths) {
        ImageIcon icon = findIcon(imagePaths);
        JButton button;

        if (icon != null && icon.getIconWidth() > 0) {
            Image scaledImage = scaleImageKeepProportion(icon.getImage(), width, height);
            button = new JButton(new ImageIcon(scaledImage));
        } else {
            button = createFallbackButton(fallbackText);
        }

        button.setPreferredSize(new Dimension(width, height));
        button.setMinimumSize(new Dimension(width, height));
        button.setMaximumSize(new Dimension(width, height));

        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setOpaque(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        return button;
    }

    private Image scaleImageKeepProportion(Image image, int maxWidth, int maxHeight) {
        int originalWidth = image.getWidth(null);
        int originalHeight = image.getHeight(null);

        if (originalWidth <= 0 || originalHeight <= 0) {
            return image.getScaledInstance(maxWidth, maxHeight, Image.SCALE_SMOOTH);
        }

        double widthRatio = (double) maxWidth / originalWidth;
        double heightRatio = (double) maxHeight / originalHeight;
        double ratio = Math.min(widthRatio, heightRatio);

        int newWidth = (int) (originalWidth * ratio);
        int newHeight = (int) (originalHeight * ratio);

        return image.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
    }

    private ImageIcon findIcon(String... imagePaths) {
        for (String path : imagePaths) {
            File file = new File(path);

            if (file.exists()) {
                ImageIcon icon = new ImageIcon(path);

                if (icon.getIconWidth() > 0) {
                    return icon;
                }
            }
        }

        return null;
    }

    private JButton createFallbackButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 15));
        button.setForeground(new Color(70, 40, 125));
        button.setBackground(new Color(255, 230, 180));
        button.setBorder(BorderFactory.createLineBorder(new Color(95, 65, 150), 3));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private JLabel createIconLabel(int width, int height, String... imagePaths) {
        ImageIcon icon = findIcon(imagePaths);

        if (icon != null && icon.getIconWidth() > 0) {
            Image scaledImage = scaleImageKeepProportion(icon.getImage(), width, height);
            return new JLabel(new ImageIcon(scaledImage));
        }

        return new JLabel("");
    }

    private JLabel createTextIcon(String text) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 20));
        label.setForeground(new Color(75, 40, 125));
        return label;
    }

    private JLabel createTopValueLabel() {
        JLabel label = new JLabel();
        label.setFont(new Font("Arial", Font.BOLD, 17));
        label.setForeground(new Color(45, 35, 120));
        return label;
    }

    private void updateTopInfo() {
        xpValueLabel.setText(gameState.getExperience() + "/" + gameState.getExperienceToNextLevel());
        energyValueLabel.setText(String.valueOf(gameState.getEnergy()));
        silverValueLabel.setText(String.valueOf(gameState.getSilver()));
        goldValueLabel.setText(String.valueOf(gameState.getGold()));
    }

    private void restPlayer() {
        if (gameState.getEnergy() >= 100) {
            JOptionPane.showMessageDialog(this, "Енергія вже повна!");
            return;
        }

        gameState.restoreEnergy();
        updateTopInfo();

        JOptionPane.showMessageDialog(this, "Ти відпочила. Енергія відновлена!");
    }

    private static class DiamondBadge extends JPanel {

        private final String text;

        DiamondBadge(String text) {
            this.text = text;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();

            Path2D diamond = new Path2D.Float();
            diamond.moveTo(w / 2.0, 0);
            diamond.lineTo(w, h * 0.42);
            diamond.lineTo(w / 2.0, h);
            diamond.lineTo(0, h * 0.42);
            diamond.closePath();

            GradientPaint gradient = new GradientPaint(
                    w / 2f, 0, new Color(240, 80, 160),
                    w / 2f, h, new Color(190, 30, 110)
            );

            g2.setPaint(gradient);
            g2.fill(diamond);

            g2.setColor(new Color(155, 20, 90));
            g2.setStroke(new BasicStroke(2f));
            g2.draw(diamond);

            g2.setFont(new Font("Arial", Font.BOLD, 18));
            g2.setColor(Color.WHITE);

            FontMetrics fm = g2.getFontMetrics();
            int tx = (w - fm.stringWidth(text)) / 2;
            int ty = (int) (h * 0.42) + fm.getAscent() / 2;

            g2.drawString(text, tx, ty);
            g2.dispose();
        }
    }

    private static class RoundedPanel extends JPanel {

        private final int radius;
        private final Color backgroundColor;

        public RoundedPanel(int radius, Color backgroundColor) {
            this.radius = radius;
            this.backgroundColor = backgroundColor;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(backgroundColor);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);

            g2.setColor(new Color(150, 115, 70, 150));
            g2.setStroke(new BasicStroke(2));
            g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, radius, radius);

            g2.dispose();
        }
    }
}