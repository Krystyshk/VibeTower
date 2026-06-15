package vibetower.ui;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * Панель з анімованим персонажем зібраним з частин тіла.
 * Підтримує анімацію ходьби та зміну зачіски/очей/статі.
 *
 * Використання:
 *   AnimatedCharacterPanel panel = new AnimatedCharacterPanel("female", "hair_wavy", "eyes_brown");
 *   panel.setWalking(true);   // запустити анімацію
 *   panel.setHair("hair_bun"); // змінити зачіску
 */
public class AnimatedCharacterPanel extends JPanel {

    // ── зображення частин тіла ────────────────────────────────────────────────
    private BufferedImage imgHead;
    private BufferedImage imgTorso;
    private BufferedImage imgArmLeft;
    private BufferedImage imgArmRight;
    private BufferedImage imgLegLeft;
    private BufferedImage imgLegRight;
    private BufferedImage imgHair;
    private BufferedImage imgEyes;

    // ── одяг ─────────────────────────────────────────────────────────────────
    private BufferedImage imgClothingTop;    // верх (футболка, худі тощо)
    private String clothingTopFile = "top_tshirt_pink"; // за замовчуванням
    private BufferedImage imgClothingBottom; // низ (джинси, легінси тощо)
    private String clothingBottomFile = "bottom_jeans_blue"; // за замовчуванням

    // ── стан анімації ─────────────────────────────────────────────────────────
    private double animPhase = 0.0;
    private Timer  animTimer;
    private boolean walking = false;

    // ── налаштування персонажа ────────────────────────────────────────────────
    private String gender    = "female";
    private String hairStyle = "hair_wavy";
    private String eyeStyle  = "eyes_brown";

    // ── константи анімації ────────────────────────────────────────────────────
    private static final double MAX_LEG_SWING = Math.toRadians(28);
    private static final double MAX_ARM_SWING = Math.toRadians(20);

    // Корекція кута: PNG зображення рук мають руку по діагоналі,
    // ці кути повертають руки у вертикальне "висяче" положення
    private static final double ARM_FIX_LEFT  = Math.toRadians(0);
    private static final double ARM_FIX_RIGHT = Math.toRadians(0);

    // ─────────────────────────────────────────────────────────────────────────
    // Конструктори
    // ─────────────────────────────────────────────────────────────────────────

    public AnimatedCharacterPanel(String gender, String hairStyle, String eyeStyle) {
        this.gender    = gender;
        this.hairStyle = hairStyle;
        this.eyeStyle  = eyeStyle;
        setPreferredSize(new Dimension(310, 560));
        setOpaque(false);
        loadImages();
        setupTimer();
    }

    public AnimatedCharacterPanel() {
        this("female", "hair_wavy", "eyes_brown");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Публічний API
    // ─────────────────────────────────────────────────────────────────────────

    /** Увімкнути / вимкнути анімацію ходьби */
    public void setWalking(boolean walking) {
        this.walking = walking;
        if (!walking) animPhase = 0;
        repaint();
    }

    /** Змінити зачіску. Назва: "hair_wavy", "hair_bun", "hair_curly" тощо */
    public void setHair(String style) {
        hairStyle = style;
        imgHair = load("/" + style + ".png");
        repaint();
    }

    /** Змінити очі. Назва: "eyes_brown", "eyes_blue", "eyes_green" тощо */
    public void setEyes(String style) {
        eyeStyle = style;
        imgEyes = load("/" + style + ".png");
        repaint();
    }

    /** Змінити верхній одяг. Назва файлу без .png, напр. "top_tshirt_pink" */
    public void setClothingTop(String filename) {
        clothingTopFile = filename;
        imgClothingTop = load("/" + filename + ".png");
        repaint();
    }

    /** Змінити нижній одяг. Назва файлу без .png, напр. "bottom_jeans_blue" */
    public void setClothingBottom(String filename) {
        clothingBottomFile = filename;
        imgClothingBottom = load("/" + filename + ".png");
        repaint();
    }

    /** Змінити стать: "female" або "male" */
    public void setGender(String g) {
        gender   = g;
        imgHead  = load("/head_"  + g + ".png");
        imgTorso = load("/torso_" + g + ".png");
        repaint();
    }

    /** Зупинити таймер анімації (викликати при закритті вікна) */
    public void stopAnimation() {
        if (animTimer != null) animTimer.stop();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Завантаження зображень
    // ─────────────────────────────────────────────────────────────────────────

    private void loadImages() {
        imgHead        = load("/head_"   + gender    + ".png");
        imgTorso       = load("/torso_"  + gender    + ".png");
        imgArmLeft     = load("/arm_left.png");
        imgArmRight    = load("/arm_right.png");
        imgLegLeft     = load("/leg_left.png");
        imgLegRight    = load("/leg_right.png");
        imgHair        = load("/" + hairStyle + ".png");
        imgEyes        = load("/" + eyeStyle  + ".png");
        imgClothingTop    = load("/" + clothingTopFile    + ".png");
        imgClothingBottom = load("/" + clothingBottomFile + ".png");
    }

    private BufferedImage load(String path) {
        try {
            var url = getClass().getResource(path);
            if (url != null) return removeWhiteBg(ImageIO.read(url));
        } catch (IOException e) {
            System.err.println("AnimatedCharacterPanel: не вдалося завантажити " + path);
        }
        return null;
    }

    /**
     * Видаляє білий/світлий фон з PNG зображення.
     * Пікселі де R, G, B > 230 стають прозорими.
     * Кольори шкіри і одягу мають хоча б один канал < 230, тому не зачіпаються.
     */
    private BufferedImage removeWhiteBg(BufferedImage src) {
        if (src == null) return null;
        int w = src.getWidth(), h = src.getHeight();
        BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        int[] pixels = src.getRGB(0, 0, w, h, null, 0, w);
        for (int i = 0; i < pixels.length; i++) {
            int r = (pixels[i] >> 16) & 0xFF;
            int g = (pixels[i] >>  8) & 0xFF;
            int b =  pixels[i]        & 0xFF;
            if (r > 230 && g > 230 && b > 230) {
                pixels[i] = 0; // прозорий
            }
        }
        out.setRGB(0, 0, w, h, pixels, 0, w);
        return out;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Таймер анімації
    // ─────────────────────────────────────────────────────────────────────────

    private void setupTimer() {
        animTimer = new Timer(50, e -> {
            if (walking) {
                animPhase += 0.18;
                if (animPhase >= Math.PI * 2) animPhase -= Math.PI * 2;
                repaint();
            }
        });
        animTimer.start();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Малювання персонажа
    // ─────────────────────────────────────────────────────────────────────────

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,  RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING,     RenderingHints.VALUE_RENDER_QUALITY);

        int W  = getWidth();
        int cx = W / 2;

        // ── розміри частин ────────────────────────────────────────────────────
        int headW  = 120, headH  = 120;
        int torsoW = 108, torsoH = 140;
        // Руки: вузькі та високі — рука у PNG іде по діагоналі, вертикальне розтягування
        // робить її більш природньо висячою
        int armW   = 95,  armH   = 145;
        // Ноги: збільшені
        int legW   = 115, legH   = 210;

        // ── координати ────────────────────────────────────────────────────────
        int headY     = 17;
        int torsoY    = headY + headH - 40;
        int hipY      = torsoY + torsoH - 18;
        int shoulderY = torsoY + 24;

        int leftShoulderX  = cx - torsoW / 2;
        int rightShoulderX = cx + torsoW / 2;

        // Стегна розсунуті ширше щоб ноги не схрещувались
        int leftHipX  = cx - 20;
        int rightHipX = cx + 20;

        // ── кути анімації (тимчасово вимкнено для налаштування позиції) ──────
        double legSwing = 0;
        double armSwing = 0;

        // ── порядок малювання (задні шари → передні) ─────────────────────────

        // Кольори одягу
        Color jeansColor  = new Color(40, 60, 120, 220);   // темно-сині джинси
        Color shirtColor  = new Color(255, 255, 255, 200);  // біла футболка
        Color shirtShadow = new Color(220, 220, 235, 180);  // тінь на футболці

        // 1. Задня нога (права)
        drawRotated(g2, imgLegRight,
                rightHipX - legW / 2, hipY, legW, legH,
                rightHipX, hipY, -legSwing);

        // 2. Обидві руки — ЗА тулубом
        drawRotated(g2, imgArmLeft,
                leftShoulderX - armW + 61, shoulderY, armW, armH,
                leftShoulderX + 61, shoulderY, armSwing + ARM_FIX_LEFT);
        drawRotated(g2, imgArmRight,
                rightShoulderX - 61, shoulderY, armW, armH,
                rightShoulderX - 61, shoulderY, -armSwing + ARM_FIX_RIGHT);

        // 3. Тулуб + одяг (поверх рук)
        drawImg(g2, imgTorso, cx - torsoW / 2, torsoY, torsoW, torsoH);
        if (imgClothingTop != null) {
            drawImg(g2, imgClothingTop, cx - torsoW / 2, torsoY, torsoW, torsoH);
        } else {
            g2.setColor(shirtColor);
            g2.fillRoundRect(cx - torsoW / 2 - 2, torsoY + 5, torsoW + 4, torsoH - 5, 10, 10);
        }

        // 4. Передня нога (ліва)
        drawRotated(g2, imgLegLeft,
                leftHipX - legW / 2, hipY, legW, legH,
                leftHipX, hipY, legSwing);

        // 5. Нижній одяг поверх обох ніг (повністю покриває ноги)
        if (imgClothingBottom != null) {
            int bw = legW * 2 + 10;
            int bh = legH + 30; // з запасом щоб нижня нога не виглядала
            drawImg(g2, imgClothingBottom, cx - bw / 2, hipY - 15, bw, bh);
        } else {
            g2.setColor(jeansColor);
            g2.fillRoundRect(leftHipX - legW / 2, hipY, legW, legH - 30, 14, 14);
            g2.fillRoundRect(rightHipX - legW / 2, hipY, legW, legH - 30, 14, 14);
        }

        // 6. Голова
        drawImg(g2, imgHead, cx - headW / 2, headY, headW, headH);

        // 7. Зачіска
        if (imgHair != null) {
            int hairW = (int) (headW * 1.22) + 5;
            int hairH = (int) (headH * 1.22) + 5;
            drawImg(g2, imgHair,
                    cx - hairW / 2,
                    headY - (int) (headH * 0.12) - 25,
                    hairW, hairH);
        }

        g2.dispose();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Допоміжні методи малювання
    // ─────────────────────────────────────────────────────────────────────────

    private void drawImg(Graphics2D g2, BufferedImage img, int x, int y, int w, int h) {
        if (img != null) {
            g2.drawImage(img, x, y, w, h, null);
        }
        // якщо зображення null — нічого не малюємо (без сірої заглушки)
    }

    private void drawRotated(Graphics2D g2, BufferedImage img,
                             int x, int y, int w, int h,
                             int pivotX, int pivotY, double angle) {
        AffineTransform saved = g2.getTransform();
        g2.rotate(angle, pivotX, pivotY);
        drawImg(g2, img, x, y, w, h);
        g2.setTransform(saved);
    }
}