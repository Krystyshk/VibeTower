package vibetower.ui;

import vibetower.model.GameState;
import vibetower.model.Item;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.net.URL;

public class ClothingShopFrame extends JFrame {

    private final GameState gameState;

    private JLabel silverLabel;
    private JLabel goldLabel;
    private JPanel shopPanel;
    private String activeCategory = "Усе";

    // ── неонова палітра у стилі стартового екрану ──────────────────────────────
    private static final Color BG_TOP      = new Color(26, 18, 48);
    private static final Color BG_BOTTOM   = new Color(46, 24, 64);
    private static final Color NEON_PINK   = new Color(255, 95, 168);
    private static final Color NEON_CYAN   = new Color(90, 209, 255);
    private static final Color NEON_GOLD   = new Color(255, 211, 92);
    private static final Color CARD_BG     = new Color(42, 31, 71, 235);
    private static final Color TEXT_LIGHT  = new Color(235, 230, 250);
    private static final Color TEXT_DIM    = new Color(190, 175, 220);

    private static final String[] CATEGORIES = {
            "Усе", "Верх", "Низ", "Сукня", "Взуття", "Аксесуари", "Головний убір"
    };

    private final Item[] clothingItems = {
            new Item("Рожева футболка", "Верх", 80, "silver", 1, "/top_tshirt_pink.png"),
            new Item("Чорна футболка", "Верх", 90, "silver", 1, "/top_tshirt_black.png"),
            new Item("Біла кроп-топ", "Верх", 120, "silver", 1, "/top_crop_white.png"),
            new Item("Бежевий джемпер", "Верх", 170, "silver", 2, "/top_knit_beige.png"),
            new Item("Рожеве худі", "Верх", 150, "silver", 2, "/top_hoodie_pink.png"),
            new Item("Червоний пеплум", "Верх", 190, "silver", 2, "/top_peplum_red.png"),
            new Item("Стильна куртка", "Верх", 5, "gold", 3),

            new Item("Сині джинси", "Низ", 150, "silver", 1, "/bottom_jeans_blue.png"),
            new Item("Чорні легінси", "Низ", 130, "silver", 1, "/bottom_leggings_black.png"),
            new Item("Бежеві шорти", "Низ", 110, "silver", 1, "/bottom_shorts_beige.png"),
            new Item("Класичні штани", "Низ", 200, "silver", 2),
            new Item("Шкіряні штани", "Низ", 6, "gold", 4),

            new Item("Літня сукня", "Сукня", 220, "silver", 2),
            new Item("Червона сукня", "Сукня", 280, "silver", 3),
            new Item("Коротка сукня", "Сукня", 230, "silver", 2),
            new Item("Вечірня сукня", "Сукня", 10, "gold", 4),
            new Item("Золота сукня", "Сукня", 14, "gold", 5),

            new Item("Кросівки", "Взуття", 140, "silver", 1),
            new Item("Туфлі", "Взуття", 180, "silver", 2),
            new Item("Черевики", "Взуття", 210, "silver", 2),
            new Item("Босоніжки", "Взуття", 160, "silver", 1),
            new Item("Золоті туфлі", "Взуття", 8, "gold", 4),

            new Item("Окуляри", "Аксесуари", 90, "silver", 1),
            new Item("Сумка", "Аксесуари", 160, "silver", 2),
            new Item("Сережки", "Аксесуари", 120, "silver", 1),
            new Item("Браслет", "Аксесуари", 100, "silver", 1),
            new Item("Ланцюжок", "Аксесуари", 5, "gold", 3),

            new Item("Кепка", "Головний убір", 100, "silver", 1),
            new Item("Шапка", "Головний убір", 90, "silver", 1),
            new Item("Капелюх", "Головний убір", 170, "silver", 2),
            new Item("Берет", "Головний убір", 150, "silver", 2),
            new Item("Корона", "Головний убір", 15, "gold", 5)
    };

    public ClothingShopFrame(GameState gameState) {
        this.gameState = gameState;

        setTitle("VibeTower — Магазин одягу");
        setSize(1100, 760);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        NeonBackgroundPanel mainPanel = new NeonBackgroundPanel();
        mainPanel.setLayout(new BorderLayout());

        mainPanel.add(createTopPanel(), BorderLayout.NORTH);

        shopPanel = new JPanel(new GridLayout(0, 3, 18, 18));
        shopPanel.setOpaque(false);
        shopPanel.setBorder(BorderFactory.createEmptyBorder(10, 30, 30, 30));

        JScrollPane scrollPane = new JScrollPane(shopPanel);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        mainPanel.add(scrollPane, BorderLayout.CENTER);

        refreshItems();

        add(mainPanel);
    }

    private JPanel createTopPanel() {
        JPanel topPanel = new JPanel();
        topPanel.setOpaque(false);
        topPanel.setLayout(new BorderLayout());
        topPanel.setBorder(BorderFactory.createEmptyBorder(22, 30, 12, 30));

        JLabel titleLabel = new JLabel("Магазин одягу");
        titleLabel.setFont(new Font("Avenir Next", Font.BOLD, 34));
        titleLabel.setForeground(NEON_PINK);

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.add(titleLabel, BorderLayout.WEST);
        header.add(buildCurrencyPanel(), BorderLayout.EAST);

        topPanel.add(header, BorderLayout.NORTH);
        topPanel.add(buildCategoryBar(), BorderLayout.SOUTH);

        return topPanel;
    }

    private JPanel buildCurrencyPanel() {
        JPanel moneyPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 14, 0));
        moneyPanel.setOpaque(false);

        silverLabel = new JLabel();
        goldLabel = new JLabel();

        silverLabel.setFont(new Font("Avenir Next", Font.BOLD, 17));
        goldLabel.setFont(new Font("Avenir Next", Font.BOLD, 17));

        silverLabel.setForeground(TEXT_LIGHT);
        goldLabel.setForeground(NEON_GOLD);

        moneyPanel.add(buildCurrencyPill("/serebro.png", silverLabel, NEON_CYAN));
        moneyPanel.add(buildCurrencyPill("/zoloto.png", goldLabel, NEON_GOLD));

        updateCurrencyLabels();

        return moneyPanel;
    }

    private JPanel buildCurrencyPill(String iconPath, JLabel label, Color glow) {
        JPanel pill = new PillPanel(glow);
        pill.setLayout(new FlowLayout(FlowLayout.CENTER, 8, 0));
        pill.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));

        JLabel iconLabel = new JLabel();
        URL url = getClass().getResource(iconPath);
        if (url != null) {
            Image scaled = new ImageIcon(url).getImage().getScaledInstance(28, 28, Image.SCALE_SMOOTH);
            iconLabel.setIcon(new ImageIcon(scaled));
        }

        pill.add(iconLabel);
        pill.add(label);
        return pill;
    }

    private JPanel buildCategoryBar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        bar.setOpaque(false);

        for (String cat : CATEGORIES) {
            JButton btn = new CategoryButton(cat, cat.equals(activeCategory));
            btn.addActionListener(e -> {
                activeCategory = cat;
                for (Component c : bar.getComponents()) {
                    if (c instanceof CategoryButton cb) {
                        cb.setActive(cb.getText().equals(cat));
                    }
                }
                refreshItems();
            });
            bar.add(btn);
        }

        return bar;
    }

    private void refreshItems() {
        shopPanel.removeAll();

        for (Item item : clothingItems) {
            if (activeCategory.equals("Усе") || item.getCategory().equals(activeCategory)) {
                shopPanel.add(createItemCard(item));
            }
        }

        shopPanel.revalidate();
        shopPanel.repaint();
    }

    private JPanel createItemCard(Item item) {
        ItemCardPanel card = new ItemCardPanel();
        card.setLayout(new BorderLayout(0, 8));
        card.setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));

        card.add(buildItemVisual(item), BorderLayout.CENTER);

        JLabel nameLabel = new JLabel(item.getName(), SwingConstants.CENTER);
        nameLabel.setFont(new Font("Avenir Next", Font.BOLD, 17));
        nameLabel.setForeground(TEXT_LIGHT);

        Color priceColor = item.getCurrency().equals("gold") ? NEON_GOLD : NEON_CYAN;
        JLabel infoLabel = new JLabel(
                item.getCategory() + " · " + item.getPriceText() + " · рівень " + item.getMinLevel(),
                SwingConstants.CENTER
        );
        infoLabel.setFont(new Font("Avenir Next", Font.PLAIN, 12));
        infoLabel.setForeground(priceColor);

        JPanel textPanel = new JPanel(new GridLayout(2, 1, 2, 4));
        textPanel.setOpaque(false);
        textPanel.add(nameLabel);
        textPanel.add(infoLabel);

        JButton tryButton = createNeonButton("Приміряти", NEON_CYAN, false);
        JButton buyButton = createNeonButton("Купити", NEON_PINK, true);
        JButton wearButton = createNeonButton("Одягнути", NEON_GOLD, false);

        tryButton.addActionListener(e -> tryItem(item));
        buyButton.addActionListener(e -> buyItem(item));
        wearButton.addActionListener(e -> wearItem(item));

        JPanel buttonsPanel = new JPanel(new GridLayout(1, 3, 6, 6));
        buttonsPanel.setOpaque(false);
        buttonsPanel.add(tryButton);
        buttonsPanel.add(buyButton);
        buttonsPanel.add(wearButton);

        JPanel southPanel = new JPanel(new BorderLayout(0, 8));
        southPanel.setOpaque(false);
        southPanel.add(textPanel, BorderLayout.NORTH);
        southPanel.add(buttonsPanel, BorderLayout.SOUTH);

        card.add(southPanel, BorderLayout.SOUTH);

        return card;
    }

    /** Створює зображення товару: реальне фото одягу або стильну неонову іконку-значок. */
    private JComponent buildItemVisual(Item item) {
        if (item.getImagePath() != null) {
            URL url = getClass().getResource(item.getImagePath());
            if (url != null) {
                Image img = new ImageIcon(url).getImage();
                ProductImagePanel panel = new ProductImagePanel(img);
                panel.setPreferredSize(new Dimension(150, 150));
                return panel;
            }
        }

        IconBadgePanel badge = new IconBadgePanel(getIconForCategory(item.getCategory()), item.getCurrency().equals("gold"));
        badge.setPreferredSize(new Dimension(150, 150));
        return badge;
    }

    private JButton createNeonButton(String text, Color accent, boolean filled) {
        return new NeonButton(text, accent, filled);
    }

    private String getIconForCategory(String category) {
        return switch (category) {
            case "Верх" -> "👕";
            case "Низ" -> "👖";
            case "Сукня" -> "👗";
            case "Взуття" -> "👟";
            case "Аксесуари" -> "👜";
            case "Головний убір" -> "👒";
            default -> "⭐";
        };
    }

    private void tryItem(Item item) {
        JOptionPane.showMessageDialog(
                this,
                "Примірка: " + item.getName() + "\nКатегорія: " + item.getCategory(),
                "Примірка",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    private void buyItem(Item item) {
        if (gameState.getInventory().contains(item)) {
            JOptionPane.showMessageDialog(
                    this,
                    "Цей предмет уже куплено.",
                    "Магазин одягу",
                    JOptionPane.INFORMATION_MESSAGE
            );
            return;
        }

        if (gameState.getLevel() < item.getMinLevel()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Цей предмет відкривається з рівня " + item.getMinLevel() + ".",
                    "Недоступно",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        boolean bought = gameState.buyClothingItem(item);

        if (bought) {
            JOptionPane.showMessageDialog(
                    this,
                    "Покупка успішна!\nПредмет додано в інвентар.",
                    "Магазин одягу",
                    JOptionPane.INFORMATION_MESSAGE
            );
            updateCurrencyLabels();
        } else {
            JOptionPane.showMessageDialog(
                    this,
                    "Недостатньо валюти для покупки.",
                    "Помилка",
                    JOptionPane.WARNING_MESSAGE
            );
        }
    }

    private void wearItem(Item item) {
        if (!gameState.getInventory().contains(item)) {
            JOptionPane.showMessageDialog(
                    this,
                    "Спочатку потрібно купити цей предмет.",
                    "Помилка",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        gameState.equipItem(item);

        JOptionPane.showMessageDialog(
                this,
                item.getName() + " одягнуто!\nКатегорія: " + item.getCategory(),
                "Образ оновлено",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    private void updateCurrencyLabels() {
        silverLabel.setText(gameState.getSilver() + "");
        goldLabel.setText(gameState.getGold() + "");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Кастомні компоненти у неоновому стилі стартового екрану
    // ─────────────────────────────────────────────────────────────────────────

    /** Фон у стилі стартового екрану: темний фіолетово-синій градієнт із неоновими відблисками. */
    private static class NeonBackgroundPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth(), h = getHeight();

            GradientPaint gradient = new GradientPaint(0, 0, BG_TOP, 0, h, BG_BOTTOM);
            g2.setPaint(gradient);
            g2.fillRect(0, 0, w, h);

            // м'які неонові відблиски
            g2.setPaint(new RadialGradientPaint(
                    new Point(w / 5, h / 6), w * 0.55f,
                    new float[]{0f, 1f},
                    new Color[]{new Color(255, 95, 168, 60), new Color(255, 95, 168, 0)}
            ));
            g2.fillRect(0, 0, w, h);

            g2.setPaint(new RadialGradientPaint(
                    new Point(w - w / 6, h - h / 6), w * 0.5f,
                    new float[]{0f, 1f},
                    new Color[]{new Color(90, 209, 255, 50), new Color(90, 209, 255, 0)}
            ));
            g2.fillRect(0, 0, w, h);

            g2.dispose();
        }
    }

    /** Капсулоподібна панель з тонким неоновим контуром (для валютних індикаторів). */
    private static class PillPanel extends JPanel {
        private final Color glow;

        PillPanel(Color glow) {
            this.glow = glow;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(new Color(255, 255, 255, 18));
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), getHeight(), getHeight());

            g2.setColor(glow);
            g2.setStroke(new BasicStroke(1.6f));
            g2.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, getHeight(), getHeight());

            g2.dispose();
            super.paintComponent(g);
        }
    }

    /** Кнопка вкладки категорії у неоновому стилі. */
    private static class CategoryButton extends JButton {
        private boolean active;

        CategoryButton(String text, boolean active) {
            super(text);
            this.active = active;
            setFont(new Font("Avenir Next", Font.BOLD, 13));
            setFocusPainted(false);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setForeground(active ? Color.WHITE : TEXT_DIM);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));
        }

        void setActive(boolean active) {
            this.active = active;
            setForeground(active ? Color.WHITE : TEXT_DIM);
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            if (active) {
                GradientPaint gp = new GradientPaint(0, 0, NEON_PINK, getWidth(), 0, NEON_CYAN.darker());
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), getHeight(), getHeight());
            } else {
                g2.setColor(new Color(255, 255, 255, 16));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), getHeight(), getHeight());
                g2.setColor(new Color(255, 255, 255, 40));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, getHeight(), getHeight());
            }

            g2.dispose();
            super.paintComponent(g);
        }
    }

    /** Картка товару: темне напівпрозоре скло з неоновим контуром, що світиться. */
    private static class ItemCardPanel extends JPanel {
        ItemCardPanel() {
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth(), h = getHeight();
            RoundRectangle2D shape = new RoundRectangle2D.Float(2, 2, w - 4, h - 4, 22, 22);

            g2.setColor(CARD_BG);
            g2.fill(shape);

            g2.setPaint(new GradientPaint(0, 0, NEON_PINK, w, h, NEON_CYAN));
            g2.setStroke(new BasicStroke(1.6f));
            g2.draw(shape);

            g2.dispose();
            super.paintComponent(g);
        }
    }

    /** Панель для фото товару: біла "вітринна" плитка з м'якою рамкою. */
    private static class ProductImagePanel extends JPanel {
        private final Image image;

        ProductImagePanel(Image image) {
            this.image = image;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

            int w = getWidth(), h = getHeight();
            int pad = 6;

            g2.setColor(new Color(255, 255, 255, 235));
            g2.fillRoundRect(pad, pad, w - pad * 2, h - pad * 2, 16, 16);

            int imgSize = Math.min(w, h) - pad * 4;
            int x = (w - imgSize) / 2;
            int y = (h - imgSize) / 2;
            g2.drawImage(image, x, y, imgSize, imgSize, this);

            g2.dispose();
            super.paintComponent(g);
        }
    }

    /** Значок-іконка для товарів без фото: неоновий градієнтний медальйон з емодзі. */
    private static class IconBadgePanel extends JPanel {
        private final String emoji;
        private final boolean premium;

        IconBadgePanel(String emoji, boolean premium) {
            this.emoji = emoji;
            this.premium = premium;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth(), h = getHeight();
            int d = Math.min(w, h) - 20;
            int x = (w - d) / 2;
            int y = (h - d) / 2;

            Color c1 = premium ? NEON_GOLD : NEON_PINK;
            Color c2 = premium ? new Color(255, 150, 70) : NEON_CYAN;

            g2.setPaint(new RadialGradientPaint(
                    new Point(w / 2, h / 2), d / 2f,
                    new float[]{0f, 1f},
                    new Color[]{new Color(c1.getRed(), c1.getGreen(), c1.getBlue(), 70), new Color(c2.getRed(), c2.getGreen(), c2.getBlue(), 10)}
            ));
            g2.fillOval(x, y, d, d);

            g2.setPaint(new GradientPaint(x, y, c1, x + d, y + d, c2));
            g2.setStroke(new BasicStroke(2.5f));
            g2.drawOval(x, y, d, d);

            g2.setFont(new Font("Arial", Font.PLAIN, 56));
            FontMetrics fm = g2.getFontMetrics();
            int textX = (w - fm.stringWidth(emoji)) / 2;
            int textY = (h - fm.getHeight()) / 2 + fm.getAscent();
            g2.drawString(emoji, textX, textY);

            g2.dispose();
            super.paintComponent(g);
        }
    }

    /** Кнопка дії в неоновому стилі: заповнена для основної дії, з контуром для другорядних. */
    private static class NeonButton extends JButton {
        private final Color accent;
        private final boolean filled;

        NeonButton(String text, Color accent, boolean filled) {
            super(text);
            this.accent = accent;
            this.filled = filled;
            setFont(new Font("Avenir Next", Font.BOLD, 12));
            setFocusPainted(false);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setForeground(filled ? Color.WHITE : accent);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setBorder(BorderFactory.createEmptyBorder(8, 4, 8, 4));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth(), h = getHeight();

            if (filled) {
                g2.setPaint(new GradientPaint(0, 0, accent, w, h, accent.darker()));
                g2.fillRoundRect(0, 0, w, h, h, h);
            } else {
                g2.setColor(new Color(255, 255, 255, 14));
                g2.fillRoundRect(0, 0, w, h, h, h);
                g2.setColor(accent);
                g2.setStroke(new BasicStroke(1.4f));
                g2.drawRoundRect(0, 0, w - 1, h - 1, h, h);
            }

            g2.dispose();
            super.paintComponent(g);
        }
    }
}
