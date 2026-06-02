package vibetower.ui;

import javax.swing.*;
import java.awt.*;

public class CharacterAvatarPanel extends JPanel {

    private String gender = "Жіночий";
    private String skinColor = "Світла";
    private String hairStyle = "Пучок";
    private String hairColor = "Каштановий";
    private String eyeColor = "Карі";
    private String outfit = "Повсякденний";

    public CharacterAvatarPanel() {
        setPreferredSize(new Dimension(360, 560));
        setBackground(new Color(255, 248, 240));
    }

    public void updateAvatar(String gender, String skinColor, String hairStyle,
                             String hairColor, String eyeColor, String outfit) {
        this.gender = gender;
        this.skinColor = skinColor;
        this.hairStyle = hairStyle;
        this.hairColor = hairColor;
        this.eyeColor = eyeColor;
        this.outfit = outfit;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);

        Graphics2D g = (Graphics2D) graphics;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        drawCardBackground(g);
        drawAvatar(g);
        drawProportionLabels(g);
    }

    private void drawCardBackground(Graphics2D g) {
        g.setColor(new Color(255, 248, 240));
        g.fillRoundRect(12, 12, getWidth() - 24, getHeight() - 24, 28, 28);

        g.setColor(new Color(220, 185, 135));
        g.setStroke(new BasicStroke(2));
        g.drawRoundRect(12, 12, getWidth() - 24, getHeight() - 24, 28, 28);

        g.setColor(new Color(205, 135, 45));
        g.fillRoundRect(28, 25, 95, 30, 14, 14);

        g.setFont(new Font("Arial", Font.BOLD, 14));
        g.setColor(Color.WHITE);
        g.drawString("ВАРІАНТ 2", 38, 45);

        g.setColor(new Color(80, 55, 35));
        g.setFont(new Font("Arial", Font.BOLD, 15));
        g.drawString("★ ОПТИМАЛЬНИЙ БАЛАНС", 135, 45);
    }

    private void drawAvatar(Graphics2D g) {
        int centerX = getWidth() / 2;

        Color skin = getSkinColor();
        Color hair = getHairColor();
        Color eyes = getEyeColor();

        int headY = 85;
        int headW = 84;
        int headH = 95;

        int bodyY = headY + headH - 6;
        int bodyW = 95;
        int bodyH = 128;

        int legsY = bodyY + bodyH - 5;
        int legsH = 185;

        g.setColor(new Color(0, 0, 0, 35));
        g.fillOval(centerX - 85, legsY + legsH + 5, 170, 26);

        drawLegs(g, centerX, legsY, legsH);
        drawBody(g, centerX, bodyY, bodyW, bodyH);
        drawArms(g, centerX, bodyY, skin);
        drawHead(g, centerX, headY, headW, headH, skin, eyes);
        drawHair(g, centerX, headY, hair);
        drawShoes(g, centerX, legsY, legsH);
    }

    private void drawHead(Graphics2D g, int centerX, int y, int w, int h, Color skin, Color eyes) {
        g.setColor(skin);
        g.fillOval(centerX - w / 2, y, w, h);

        g.setColor(new Color(165, 115, 90));
        g.setStroke(new BasicStroke(1));
        g.drawOval(centerX - w / 2, y, w, h);

        g.setColor(Color.WHITE);
        g.fillOval(centerX - 26, y + 43, 20, 13);
        g.fillOval(centerX + 6, y + 43, 20, 13);

        g.setColor(eyes);
        g.fillOval(centerX - 20, y + 46, 9, 9);
        g.fillOval(centerX + 12, y + 46, 9, 9);

        g.setColor(Color.BLACK);
        g.fillOval(centerX - 17, y + 48, 4, 4);
        g.fillOval(centerX + 15, y + 48, 4, 4);

        g.setColor(new Color(70, 45, 30));
        g.setStroke(new BasicStroke(2));
        g.drawLine(centerX - 30, y + 37, centerX - 10, y + 34);
        g.drawLine(centerX + 10, y + 34, centerX + 30, y + 37);

        g.setColor(new Color(175, 115, 95));
        g.drawLine(centerX, y + 53, centerX - 3, y + 64);

        if (gender.equals("Жіночий")) {
            g.setColor(new Color(180, 75, 80));
        } else {
            g.setColor(new Color(120, 70, 60));
        }
        g.drawArc(centerX - 12, y + 69, 24, 10, 180, 180);

        if (gender.equals("Жіночий")) {
            g.setColor(new Color(255, 150, 150, 80));
            g.fillOval(centerX - 39, y + 58, 18, 10);
            g.fillOval(centerX + 21, y + 58, 18, 10);
        }
    }

    private void drawHair(Graphics2D g, int centerX, int y, Color hair) {
        g.setColor(hair);

        if (gender.equals("Жіночий")) {
            if (hairStyle.equals("Пучок")) {
                g.fillOval(centerX - 34, y - 36, 68, 55);
                g.fillOval(centerX - 48, y - 5, 96, 45);
                g.fillOval(centerX - 55, y + 5, 25, 65);
                g.fillOval(centerX + 30, y + 5, 25, 65);
            } else if (hairStyle.equals("Довге")) {
                g.fillOval(centerX - 55, y - 8, 110, 135);
                g.setColor(getSkinColor());
                g.fillOval(centerX - 39, y + 14, 78, 92);
                g.setColor(hair);
                g.fillArc(centerX - 46, y - 5, 92, 58, 0, 180);
            } else {
                g.fillOval(centerX - 46, y - 10, 92, 55);
                g.fillOval(centerX - 46, y + 10, 30, 60);
                g.fillOval(centerX + 16, y + 10, 30, 60);
            }
        } else {
            g.fillOval(centerX - 46, y - 13, 92, 45);
            g.fillOval(centerX - 39, y - 21, 36, 36);
            g.fillOval(centerX - 10, y - 26, 36, 36);
            g.fillOval(centerX + 16, y - 19, 36, 36);
        }
    }

    private void drawBody(Graphics2D g, int centerX, int y, int w, int h) {
        if (gender.equals("Жіночий")) {
            g.setColor(new Color(248, 248, 248));
            g.fillRoundRect(centerX - w / 2, y, w, h, 30, 30);

            g.setColor(new Color(35, 35, 40));
            g.fillRoundRect(centerX - 48, y + 95, 96, 80, 22, 22);

            g.setColor(new Color(45, 40, 40));
            g.setStroke(new BasicStroke(4));
            g.drawLine(centerX - 35, y + 5, centerX + 35, y + 112);
        } else {
            g.setColor(new Color(25, 25, 30));
            g.fillRoundRect(centerX - w / 2, y, w, h, 32, 32);

            g.setColor(new Color(15, 15, 20));
            g.drawOval(centerX - 46, y - 10, 92, 50);

            g.setColor(new Color(45, 45, 50));
            g.setStroke(new BasicStroke(4));
            g.drawLine(centerX - 35, y + 10, centerX + 35, y + 112);
        }
    }

    private void drawArms(Graphics2D g, int centerX, int bodyY, Color skin) {
        if (gender.equals("Жіночий")) {
            g.setColor(new Color(248, 248, 248));
        } else {
            g.setColor(new Color(25, 25, 30));
        }

        g.fillRoundRect(centerX - 78, bodyY + 22, 29, 115, 22, 22);
        g.fillRoundRect(centerX + 49, bodyY + 22, 29, 115, 22, 22);

        g.setColor(skin);
        g.fillOval(centerX - 76, bodyY + 128, 24, 24);
        g.fillOval(centerX + 51, bodyY + 128, 24, 24);
    }

    private void drawLegs(Graphics2D g, int centerX, int y, int h) {
        g.setColor(new Color(35, 35, 40));

        g.fillRoundRect(centerX - 50, y, 43, h, 18, 18);
        g.fillRoundRect(centerX + 7, y, 43, h, 18, 18);

        g.setColor(new Color(60, 60, 65));
        g.drawRect(centerX - 44, y + 48, 24, 28);
        g.drawRect(centerX + 20, y + 48, 24, 28);
    }

    private void drawShoes(Graphics2D g, int centerX, int legsY, int legsH) {
        int shoeY = legsY + legsH - 3;

        g.setColor(Color.WHITE);
        g.fillRoundRect(centerX - 62, shoeY, 58, 24, 15, 15);
        g.fillRoundRect(centerX + 4, shoeY, 58, 24, 15, 15);

        g.setColor(new Color(180, 180, 180));
        g.drawRoundRect(centerX - 62, shoeY, 58, 24, 15, 15);
        g.drawRoundRect(centerX + 4, shoeY, 58, 24, 15, 15);
    }

    private void drawProportionLabels(Graphics2D g) {
        g.setFont(new Font("Arial", Font.BOLD, 14));
        g.setColor(new Color(60, 45, 35));

        g.drawString("ГОЛОВА", 255, 155);
        g.drawString("25%", 270, 175);

        g.drawString("ТУЛУБ", 260, 275);
        g.drawString("30%", 270, 295);

        g.drawString("НОГИ", 270, 415);
        g.drawString("45%", 270, 435);
    }

    private Color getSkinColor() {
        return switch (skinColor) {
            case "Смаглява" -> new Color(190, 130, 95);
            case "Темна" -> new Color(120, 75, 55);
            case "Рожева" -> new Color(245, 190, 175);
            default -> new Color(235, 190, 160);
        };
    }

    private Color getHairColor() {
        return switch (hairColor) {
            case "Блонд" -> new Color(220, 175, 80);
            case "Чорний" -> new Color(35, 25, 20);
            case "Рудий" -> new Color(175, 80, 35);
            case "Рожевий" -> new Color(220, 120, 160);
            default -> new Color(90, 45, 25);
        };
    }

    private Color getEyeColor() {
        return switch (eyeColor) {
            case "Блакитні" -> new Color(60, 130, 200);
            case "Зелені" -> new Color(60, 140, 90);
            case "Сірі" -> new Color(120, 120, 130);
            default -> new Color(80, 45, 25);
        };
    }
}
