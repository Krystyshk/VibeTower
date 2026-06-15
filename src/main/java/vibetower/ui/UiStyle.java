package vibetower.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class UiStyle {

    private static final Map<String, ImageIcon> ICON_CACHE = new HashMap<>();

    public static final Color CREAM = new Color(255, 248, 235);
    public static final Color CARD = new Color(255, 252, 247);
    public static final Color PANEL = new Color(255, 248, 235);

    public static final Color PURPLE = new Color(126, 90, 170);
    public static final Color BLUE = new Color(35, 35, 135);
    public static final Color LIGHT_PURPLE = new Color(230, 220, 250);

    public static final Color GREEN = new Color(120, 185, 80);
    public static final Color RED = new Color(210, 85, 85);
    public static final Color YELLOW = new Color(255, 220, 90);
    public static final Color DARK = new Color(75, 55, 100);

    public static Font titleFont(int size) {
        return new Font("Arial", Font.BOLD, size);
    }

    public static Font textFont(int size) {
        return new Font("Arial", Font.BOLD, size);
    }

    public static JButton button(String text) {
        JButton button = new JButton(text);

        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setOpaque(false);

        button.setForeground(BLUE);
        button.setFont(textFont(17));

        button.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(PURPLE, 2, 14),
                BorderFactory.createEmptyBorder(6, 12, 6, 12)
        ));

        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        return button;
    }

    public static ImageIcon icon(String path, int width, int height) {
        if (path == null || path.isEmpty()) return null;

        String cacheKey = path + "_" + width + "x" + height + "_stretch";

        if (ICON_CACHE.containsKey(cacheKey)) {
            return ICON_CACHE.get(cacheKey);
        }

        ImageIcon original = loadOriginalIcon(path);

        if (original == null) return null;

        Image scaled = original.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
        ImageIcon result = new ImageIcon(scaled);

        ICON_CACHE.put(cacheKey, result);

        return result;
    }

    public static ImageIcon iconContain(String path, int maxW, int maxH) {
        if (path == null || path.isEmpty()) return null;

        String cacheKey = path + "_" + maxW + "x" + maxH + "_contain";

        if (ICON_CACHE.containsKey(cacheKey)) {
            return ICON_CACHE.get(cacheKey);
        }

        ImageIcon original = loadOriginalIcon(path);

        if (original == null) return null;

        int w = original.getIconWidth();
        int h = original.getIconHeight();

        if (w <= 0 || h <= 0) return null;

        double scale = Math.min((double) maxW / w, (double) maxH / h);

        int newW = Math.max(1, (int) Math.round(w * scale));
        int newH = Math.max(1, (int) Math.round(h * scale));

        Image scaled = original.getImage().getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
        ImageIcon result = new ImageIcon(scaled);

        ICON_CACHE.put(cacheKey, result);

        return result;
    }

    private static ImageIcon loadOriginalIcon(String path) {
        if (path == null || path.isEmpty()) return null;

        File file = new File(path);
        if (file.exists()) return new ImageIcon(file.getAbsolutePath());

        file = new File("png/" + path);
        if (file.exists()) return new ImageIcon(file.getAbsolutePath());

        file = new File("src/main/resources/" + path);
        if (file.exists()) return new ImageIcon(file.getAbsolutePath());

        file = new File("src/main/resources/png/" + path);
        if (file.exists()) return new ImageIcon(file.getAbsolutePath());

        URL url = UiStyle.class.getClassLoader().getResource(path);
        if (url != null) return new ImageIcon(url);

        url = UiStyle.class.getClassLoader().getResource("png/" + path);
        if (url != null) return new ImageIcon(url);

        return null;
    }

    public static class RoundPanel extends JPanel {

        private final int radius;
        private final Color backgroundColor;
        private final Color borderColor;
        private final int borderWidth;

        public RoundPanel(LayoutManager layout, int radius, Color backgroundColor, Color borderColor, int borderWidth) {
            super(layout);

            this.radius = radius;
            this.backgroundColor = backgroundColor;
            this.borderColor = borderColor;
            this.borderWidth = borderWidth;

            setOpaque(false);
        }

        public RoundPanel(int radius, Color backgroundColor, Color borderColor, int borderWidth) {
            this(new BorderLayout(), radius, backgroundColor, borderColor, borderWidth);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();

            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();

            g2.setColor(backgroundColor);
            g2.fillRoundRect(
                    borderWidth,
                    borderWidth,
                    w - borderWidth * 2,
                    h - borderWidth * 2,
                    radius,
                    radius
            );

            g2.dispose();

            super.paintComponent(g);
        }

        @Override
        protected void paintBorder(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();

            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();

            g2.setStroke(new BasicStroke(borderWidth));
            g2.setColor(borderColor);
            g2.drawRoundRect(
                    borderWidth,
                    borderWidth,
                    w - borderWidth * 2,
                    h - borderWidth * 2,
                    radius,
                    radius
            );

            g2.dispose();
        }
    }

    private static class RoundedBorder implements javax.swing.border.Border {

        private final Color color;
        private final int thickness;
        private final int radius;

        public RoundedBorder(Color color, int thickness, int radius) {
            this.color = color;
            this.thickness = thickness;
            this.radius = radius;
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(6, 10, 6, 10);
        }

        @Override
        public boolean isBorderOpaque() {
            return false;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();

            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.setStroke(new BasicStroke(thickness));

            g2.drawRoundRect(
                    x + thickness,
                    y + thickness,
                    width - thickness * 2 - 1,
                    height - thickness * 2 - 1,
                    radius,
                    radius
            );

            g2.dispose();
        }
    }
    public static ImageIcon iconCover(String path, int width, int height) {
        try {
            if (path == null || path.isEmpty()) return null;

            java.io.File file = new java.io.File(path);

            ImageIcon original;

            if (file.exists()) {
                original = new ImageIcon(path);
            } else {
                java.net.URL url = UiStyle.class.getClassLoader().getResource(path);
                if (url == null) {
                    url = UiStyle.class.getClassLoader().getResource(path.replace("src/main/resources/", ""));
                }

                if (url == null) return null;

                original = new ImageIcon(url);
            }

            if (original.getIconWidth() <= 0 || original.getIconHeight() <= 0) {
                return null;
            }

            Image image = original.getImage();

            int originalWidth = original.getIconWidth();
            int originalHeight = original.getIconHeight();

            double scale = Math.max(
                    (double) width / originalWidth,
                    (double) height / originalHeight
            );

            int newWidth = (int) Math.round(originalWidth * scale);
            int newHeight = (int) Math.round(originalHeight * scale);

            Image scaled = image.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);

            BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = result.createGraphics();

            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int x = (width - newWidth) / 2;
            int y = (height - newHeight) / 2;

            g2.drawImage(scaled, x, y, null);
            g2.dispose();

            return new ImageIcon(result);

        } catch (Exception e) {
            return null;
        }
    }
}