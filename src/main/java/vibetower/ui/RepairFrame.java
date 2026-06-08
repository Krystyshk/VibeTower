package vibetower.ui;

import vibetower.model.GameState;
import vibetower.model.HomeFrame;
import vibetower.model.SaveManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;

public class RepairFrame extends JFrame {

    private final GameState gameState;

    private JLayeredPane layeredPane;
    private JLabel backgroundLabel;
    private JPanel bottomCatalogPanel;
    private JPanel categoryPanel;

    private String currentBackgroundPath;
    private double zoomScale = 1.0;

    private static final int WINDOW_WIDTH = 1200;
    private static final int WINDOW_HEIGHT = 720;

    public RepairFrame(GameState gameState) {
        this.gameState = gameState;
        this.gameState.fixAfterLoad();

        setTitle("VibeTower — Режим ремонту");
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
        setContentPane(layeredPane);

        backgroundLabel = createApartmentBackground();
        backgroundLabel.setBounds(0, 0, WINDOW_WIDTH, WINDOW_HEIGHT);
        layeredPane.add(backgroundLabel, Integer.valueOf(0));

        addTopButtons();
        addLeftShopButton();
        addRightButtons();
        addBottomCatalogPanel();

        showMainRepairCategories();
    }

    private JLabel createApartmentBackground() {
        currentBackgroundPath = getBackgroundPathByWallpaper();

        ImageIcon icon = new ImageIcon(currentBackgroundPath);

        if (icon.getIconWidth() > 0) {
            Image scaledImage = icon.getImage().getScaledInstance(
                    WINDOW_WIDTH,
                    WINDOW_HEIGHT,
                    Image.SCALE_SMOOTH
            );
            return new JLabel(new ImageIcon(scaledImage));
        }

        JLabel fallback = new JLabel("Режим ремонту", SwingConstants.CENTER);
        fallback.setOpaque(true);
        fallback.setBackground(new Color(235, 225, 215));
        fallback.setForeground(new Color(75, 40, 125));
        fallback.setFont(new Font("Arial", Font.BOLD, 42));
        return fallback;
    }

    private String getBackgroundPathByWallpaper() {
        String wallpaper = gameState.getWallpaper();

        if (wallpaper == null) {
            return "src/main/resources/apartment_blue.jpg";
        }

        if (wallpaper.equalsIgnoreCase("pink")
                || wallpaper.contains("Рожев")
                || wallpaper.contains("рожев")) {
            return "src/main/resources/apartment_pink.jpg";
        }

        if (wallpaper.equalsIgnoreCase("peach")
                || wallpaper.contains("Персиков")
                || wallpaper.contains("персиков")) {
            return "src/main/resources/komnata.jpg";
        }

        return "src/main/resources/apartment_blue.jpg";
    }

    private void addTopButtons() {
        JButton cancelButton = createImageButton(
                150,
                58,
                "Скасувати",
                "src/main/resources/скасувати.png"
        );
        cancelButton.setBounds(18, 18, 150, 58);
        layeredPane.add(cancelButton, Integer.valueOf(6));

        JButton saveRepairButton = createImageButton(
                185,
                58,
                "Зберегти ремонт",
                "src/main/resources/зберегти ремонт.png"
        );
        saveRepairButton.setBounds(175, 18, 185, 58);
        layeredPane.add(saveRepairButton, Integer.valueOf(6));

        JButton saveProgressButton = createImageButton(
                185,
                58,
                "Зберегти прогрес",
                "src/main/resources/зберегти прогрес.png"
        );
        saveProgressButton.setBounds(367, 18, 185, 58);
        layeredPane.add(saveProgressButton, Integer.valueOf(6));

        cancelButton.addActionListener(e -> {
            new HomeFrame(gameState).setVisible(true);
            dispose();
        });

        saveRepairButton.addActionListener(e -> saveRepairAndReturn());

        saveProgressButton.addActionListener(e -> {
            SaveManager.saveGame(gameState);
            JOptionPane.showMessageDialog(this, "Прогрес збережено!");
        });
    }

    private void saveRepairAndReturn() {
        SaveManager.saveGame(gameState);
        JOptionPane.showMessageDialog(this, "Ремонт збережено!");
        new HomeFrame(gameState).setVisible(true);
        dispose();
    }

    private void addLeftShopButton() {
        JButton shopButton = createImageButton(
                165,
                82,
                "Магазин",
                "src/main/resources/магазин.png",
                "src/main/resources/магазин інтер.png",
                "src/main/resources/магазин інтер'єру.png"
        );

        // Кнопка магазину зліва нижче
        shopButton.setBounds(18, 585, 165, 82);
        layeredPane.add(shopButton, Integer.valueOf(7));

        shopButton.addActionListener(e -> {
            new ShopFrame(gameState).setVisible(true);
            dispose();
        });
    }

    private void addRightButtons() {
        int buttonSize = 62;
        int x = WINDOW_WIDTH - buttonSize - 14;
        int startY = 250;
        int gap = 12;

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

        zoomPlusButton.addActionListener(e -> zoomInRoom());
        zoomMinusButton.addActionListener(e -> zoomOutRoom());
    }

    private void addBottomCatalogPanel() {
        bottomCatalogPanel = new JPanel(null);

        // Панель предметів правіше, бо зліва окремо стоїть магазин
        bottomCatalogPanel.setBounds(195, 585, 875, 90);
        bottomCatalogPanel.setOpaque(false);
        layeredPane.add(bottomCatalogPanel, Integer.valueOf(5));

        RoundedPanel background = new RoundedPanel(24, new Color(125, 95, 65, 165));
        background.setBounds(0, 0, 875, 90);
        background.setLayout(null);
        bottomCatalogPanel.add(background);

        categoryPanel = new JPanel(null);
        categoryPanel.setBounds(15, 9, 845, 72);
        categoryPanel.setOpaque(false);
        background.add(categoryPanel);
    }

    private void showMainRepairCategories() {
        categoryPanel.removeAll();

        int x = 5;
        int y = 3;
        int buttonWidth = 160;
        int buttonHeight = 66;
        int gap = 8;

        JButton wallpaperButton = createImageButton(
                buttonWidth,
                buttonHeight,
                "Змінити шпалери",
                "src/main/resources/змінити шпалери.png"
        );
        wallpaperButton.setBounds(x, y, buttonWidth, buttonHeight);
        categoryPanel.add(wallpaperButton);

        x += buttonWidth + gap;

        JButton floorButton = createImageButton(
                buttonWidth,
                buttonHeight,
                "Змінити підлогу",
                "src/main/resources/змінити підлогу.png"
        );
        floorButton.setBounds(x, y, buttonWidth, buttonHeight);
        categoryPanel.add(floorButton);

        x += buttonWidth + gap;

        JButton sofasButton = createCategoryButton("Дивани");
        sofasButton.setBounds(x, y, 120, buttonHeight);
        categoryPanel.add(sofasButton);

        x += 120 + gap;

        JButton bedsButton = createCategoryButton("Ліжка");
        bedsButton.setBounds(x, y, 120, buttonHeight);
        categoryPanel.add(bedsButton);

        x += 120 + gap;

        JButton decorButton = createCategoryButton("Декор");
        decorButton.setBounds(x, y, 120, buttonHeight);
        categoryPanel.add(decorButton);

        wallpaperButton.addActionListener(e -> showWallpaperCategory());
        floorButton.addActionListener(e -> showFloorCategory());
        sofasButton.addActionListener(e -> showFurnitureCategory("Дивани"));
        bedsButton.addActionListener(e -> showFurnitureCategory("Ліжка"));
        decorButton.addActionListener(e -> showFurnitureCategory("Декор"));

        categoryPanel.revalidate();
        categoryPanel.repaint();
    }

    private void showWallpaperCategory() {
        categoryPanel.removeAll();

        int x = 5;
        int y = 3;
        int buttonWidth = 130;
        int buttonHeight = 66;
        int gap = 10;

        JButton blueButton = createCategoryButton("Блакитні");
        blueButton.setBounds(x, y, buttonWidth, buttonHeight);
        categoryPanel.add(blueButton);

        x += buttonWidth + gap;

        JButton pinkButton = createCategoryButton("Рожеві");
        pinkButton.setBounds(x, y, buttonWidth, buttonHeight);
        categoryPanel.add(pinkButton);

        x += buttonWidth + gap;

        JButton peachButton = createCategoryButton("Персикові");
        peachButton.setBounds(x, y, buttonWidth, buttonHeight);
        categoryPanel.add(peachButton);

        x += buttonWidth + gap;

        JButton backButton = createCategoryButton("Назад");
        backButton.setBounds(x, y, buttonWidth, buttonHeight);
        categoryPanel.add(backButton);

        blueButton.addActionListener(e -> changeWallpaper("blue"));
        pinkButton.addActionListener(e -> changeWallpaper("pink"));
        peachButton.addActionListener(e -> changeWallpaper("peach"));
        backButton.addActionListener(e -> showMainRepairCategories());

        categoryPanel.revalidate();
        categoryPanel.repaint();
    }

    private void showFloorCategory() {
        categoryPanel.removeAll();

        int x = 5;
        int y = 3;
        int buttonWidth = 145;
        int buttonHeight = 66;
        int gap = 10;

        JButton brownButton = createCategoryButton("Паркет");
        brownButton.setBounds(x, y, buttonWidth, buttonHeight);
        categoryPanel.add(brownButton);

        x += buttonWidth + gap;

        JButton lightButton = createCategoryButton("Світлий");
        lightButton.setBounds(x, y, buttonWidth, buttonHeight);
        categoryPanel.add(lightButton);

        x += buttonWidth + gap;

        JButton darkButton = createCategoryButton("Темний");
        darkButton.setBounds(x, y, buttonWidth, buttonHeight);
        categoryPanel.add(darkButton);

        x += buttonWidth + gap;

        JButton backButton = createCategoryButton("Назад");
        backButton.setBounds(x, y, buttonWidth, buttonHeight);
        categoryPanel.add(backButton);

        brownButton.addActionListener(e -> {
            gameState.setFloor("Коричневий паркет");
            JOptionPane.showMessageDialog(this, "Підлогу змінено на коричневий паркет.");
        });

        lightButton.addActionListener(e -> {
            gameState.setFloor("Світлий паркет");
            JOptionPane.showMessageDialog(this, "Підлогу змінено на світлий паркет.");
        });

        darkButton.addActionListener(e -> {
            gameState.setFloor("Темна підлога");
            JOptionPane.showMessageDialog(this, "Підлогу змінено на темну підлогу.");
        });

        backButton.addActionListener(e -> showMainRepairCategories());

        categoryPanel.revalidate();
        categoryPanel.repaint();
    }

    private void showFurnitureCategory(String categoryName) {
        categoryPanel.removeAll();

        ArrayList<CatalogItem> items = getItemsByCategory(categoryName);

        int x = 5;
        int y = 4;
        int buttonWidth = 74;
        int buttonHeight = 62;
        int gap = 10;

        for (CatalogItem item : items) {
            JButton itemButton = createCatalogItemButton(
                    buttonWidth,
                    buttonHeight,
                    item.name,
                    item.imagePath
            );
            itemButton.setBounds(x, y, buttonWidth, buttonHeight);
            categoryPanel.add(itemButton);

            itemButton.addActionListener(e -> addItemToRoom(item));

            x += buttonWidth + gap;
        }

        JButton backButton = createCategoryButton("Назад");
        backButton.setBounds(x + 5, y, 100, buttonHeight);
        categoryPanel.add(backButton);
        backButton.addActionListener(e -> showMainRepairCategories());

        categoryPanel.revalidate();
        categoryPanel.repaint();
    }

    private ArrayList<CatalogItem> getItemsByCategory(String categoryName) {
        ArrayList<CatalogItem> items = new ArrayList<>();

        if (categoryName.equals("Дивани")) {
            items.add(new CatalogItem("Диван 1", "src/main/resources/sofa.png"));
            items.add(new CatalogItem("Диван 2", "src/main/resources/sofa_2.png"));
            items.add(new CatalogItem("Крісло", "src/main/resources/chair.png"));
            items.add(new CatalogItem("Пуф", "src/main/resources/pouf.png"));
        } else if (categoryName.equals("Ліжка")) {
            items.add(new CatalogItem("Ліжко 1", "src/main/resources/bed.png"));
            items.add(new CatalogItem("Ліжко 2", "src/main/resources/bed_2.png"));
            items.add(new CatalogItem("Тумба", "src/main/resources/cabinet.png"));
        } else if (categoryName.equals("Декор")) {
            items.add(new CatalogItem("Картина", "src/main/resources/picture.png"));
            items.add(new CatalogItem("Квіти", "src/main/resources/flower.png"));
            items.add(new CatalogItem("Ваза", "src/main/resources/vase.png"));
            items.add(new CatalogItem("Лампа", "src/main/resources/lamp.png"));
            items.add(new CatalogItem("Декор", "src/main/resources/decor.png"));
        }

        return items;
    }

    private void changeWallpaper(String wallpaperType) {
        if (wallpaperType.equals("pink")) {
            gameState.setWallpaper("Рожеві шпалери");
            currentBackgroundPath = "src/main/resources/apartment_pink.jpg";
        } else if (wallpaperType.equals("peach")) {
            gameState.setWallpaper("Персикові шпалери");
            currentBackgroundPath = "src/main/resources/komnata.jpg";
        } else {
            gameState.setWallpaper("Блакитні шпалери");
            currentBackgroundPath = "src/main/resources/apartment_blue.jpg";
        }

        // Якщо в GameState є метод setApartmentType, він викличеться автоматично.
        // Якщо його немає, код все одно працюватиме.
        setApartmentTypeIfExists(wallpaperType);

        zoomScale = 1.0;
        updateBackgroundZoom();
    }

    private void setApartmentTypeIfExists(String apartmentType) {
        try {
            Method method = gameState.getClass().getMethod("setApartmentType", String.class);
            method.invoke(gameState, apartmentType);
        } catch (Exception ignored) {
            // Метод може бути відсутній у старій версії GameState, це не помилка.
        }
    }

    private void addItemToRoom(CatalogItem item) {
        JLabel itemLabel = createMovableItem(item.imagePath, item.name);
        itemLabel.setBounds(520, 350, 100, 100);
        layeredPane.add(itemLabel, Integer.valueOf(4));
        layeredPane.repaint();
    }

    private JLabel createMovableItem(String imagePath, String fallbackName) {
        ImageIcon icon = findIcon(imagePath);
        JLabel itemLabel;

        if (icon != null && icon.getIconWidth() > 0) {
            Image scaledImage = scaleImageKeepProportion(icon.getImage(), 100, 100);
            itemLabel = new JLabel(new ImageIcon(scaledImage));
        } else {
            itemLabel = new JLabel(fallbackName, SwingConstants.CENTER);
            itemLabel.setOpaque(true);
            itemLabel.setBackground(new Color(245, 228, 185));
            itemLabel.setForeground(new Color(75, 40, 125));
            itemLabel.setFont(new Font("Arial", Font.BOLD, 12));
            itemLabel.setBorder(BorderFactory.createLineBorder(new Color(95, 65, 150), 2));
        }

        itemLabel.setCursor(new Cursor(Cursor.MOVE_CURSOR));

        final Point[] startPoint = new Point[1];

        itemLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                startPoint[0] = e.getPoint();
            }
        });

        itemLabel.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                int newX = itemLabel.getX() + e.getX() - startPoint[0].x;
                int newY = itemLabel.getY() + e.getY() - startPoint[0].y;

                if (newX < 120) newX = 120;
                if (newX > 980) newX = 980;
                if (newY < 110) newY = 110;
                if (newY > 560) newY = 560;

                itemLabel.setLocation(newX, newY);
            }
        });

        return itemLabel;
    }

    private void zoomInRoom() {
        if (zoomScale < 1.20) {
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

    private JButton createCategoryButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 15));
        button.setForeground(new Color(60, 35, 120));
        button.setBackground(new Color(245, 228, 185));
        button.setBorder(BorderFactory.createLineBorder(new Color(95, 65, 150), 2));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private JButton createCatalogItemButton(int width, int height, String fallbackText, String imagePath) {
        JButton button = new JButton();
        button.setLayout(null);
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setOpaque(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        ImageIcon icon = findIcon(imagePath);

        if (icon != null && icon.getIconWidth() > 0) {
            Image scaledImage = scaleImageKeepProportion(icon.getImage(), width - 8, height - 8);
            JLabel iconLabel = new JLabel(new ImageIcon(scaledImage));
            iconLabel.setBounds(4, 4, width - 8, height - 8);
            button.add(iconLabel);
        } else {
            JLabel label = new JLabel(fallbackText, SwingConstants.CENTER);
            label.setBounds(0, 0, width, height);
            label.setFont(new Font("Arial", Font.BOLD, 11));
            label.setForeground(new Color(75, 40, 125));
            label.setOpaque(true);
            label.setBackground(new Color(245, 228, 185));
            label.setBorder(BorderFactory.createLineBorder(new Color(95, 65, 150), 2));
            button.add(label);
        }

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
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setForeground(new Color(75, 40, 125));
        button.setBackground(new Color(245, 228, 185));
        button.setBorder(BorderFactory.createLineBorder(new Color(95, 65, 150), 2));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private static class CatalogItem {

        private final String name;
        private final String imagePath;

        public CatalogItem(String name, String imagePath) {
            this.name = name;
            this.imagePath = imagePath;
        }
    }

    private static class RoundedPanel extends JPanel {

        private final int radius;
        private final Color backgroundColor;

        public RoundedPanel(int radius, Color backgroundColor) {
            this.radius = radius;
            this.backgroundColor = backgroundColor;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(backgroundColor);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);

            g2.setColor(new Color(150, 115, 70, 130));
            g2.setStroke(new BasicStroke(2));
            g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, radius, radius);

            g2.dispose();
        }
    }
}