package vibetower.ui;

import vibetower.model.GameState;

import javax.swing.*;
import java.awt.*;

public class BeautySalonFrame extends JFrame {

    private final GameState gameState;

    private JComboBox<String> skinBox;
    private JComboBox<String> hairStyleBox;
    private JComboBox<String> hairColorBox;
    private JComboBox<String> eyeColorBox;

    private AvatarPreviewPanel avatarPreviewPanel;

    public BeautySalonFrame(GameState gameState) {
        this.gameState = gameState;

        setTitle("VibeTower — Салон краси");
        setSize(900, 640);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(255, 242, 235));

        JLabel titleLabel = new JLabel("Салон краси", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 36));
        titleLabel.setForeground(new Color(72, 37, 120));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(25, 0, 20, 0));

        avatarPreviewPanel = new AvatarPreviewPanel();

        JPanel controlsPanel = new JPanel();
        controlsPanel.setBackground(Color.WHITE);
        controlsPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(120, 82, 160), 3),
                BorderFactory.createEmptyBorder(25, 25, 25, 25)
        ));
        controlsPanel.setLayout(new GridLayout(6, 1, 10, 10));

        skinBox = new JComboBox<>(new String[]{
                "Світла", "Персикова", "Смаглява", "Темна"
        });

        hairStyleBox = new JComboBox<>(new String[]{
                "Пучок", "Довге волосся", "Каре", "Хвилясте волосся", "Коротке волосся", "Кучері"
        });

        hairColorBox = new JComboBox<>(new String[]{
                "Каштановий", "Блонд", "Чорний", "Рудий", "Рожевий", "Фіолетовий"
        });

        eyeColorBox = new JComboBox<>(new String[]{
                "Карі", "Блакитні", "Зелені", "Сірі", "Фіолетові"
        });

        setCurrentValues();

        controlsPanel.add(createLabeledBox("Колір шкіри:", skinBox));
        controlsPanel.add(createLabeledBox("Зачіска:", hairStyleBox));
        controlsPanel.add(createLabeledBox("Колір волосся:", hairColorBox));
        controlsPanel.add(createLabeledBox("Колір очей:", eyeColorBox));

        JButton previewButton = createButton("Приміряти");
        previewButton.addActionListener(e -> updatePreview());

        JButton saveButton = createButton("Зберегти зміни");
        saveButton.addActionListener(e -> saveAppearance());

        controlsPanel.add(previewButton);
        controlsPanel.add(saveButton);

        JPanel rightWrapper = new JPanel(new BorderLayout());
        rightWrapper.setOpaque(false);
        rightWrapper.setBorder(BorderFactory.createEmptyBorder(20, 20, 40, 35));
        rightWrapper.add(controlsPanel, BorderLayout.CENTER);

        mainPanel.add(titleLabel, BorderLayout.NORTH);
        mainPanel.add(avatarPreviewPanel, BorderLayout.CENTER);
        mainPanel.add(rightWrapper, BorderLayout.EAST);

        add(mainPanel);

        updatePreview();
    }

    private JPanel createLabeledBox(String text, JComboBox<String> comboBox) {
        JPanel panel = new JPanel(new BorderLayout(0, 6));
        panel.setOpaque(false);

        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 16));
        label.setForeground(new Color(72, 37, 120));

        comboBox.setFont(new Font("Arial", Font.PLAIN, 15));
        comboBox.setBackground(new Color(255, 248, 240));
        comboBox.setForeground(new Color(60, 45, 80));

        panel.add(label, BorderLayout.NORTH);
        panel.add(comboBox, BorderLayout.CENTER);

        return panel;
    }

    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 17));
        button.setBackground(new Color(255, 218, 130));
        button.setForeground(new Color(72, 37, 120));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private void setCurrentValues() {
        skinBox.setSelectedItem(gameState.getSkinColor());
        hairStyleBox.setSelectedItem(gameState.getHairStyle());
        hairColorBox.setSelectedItem(gameState.getHairColor());
        eyeColorBox.setSelectedItem(gameState.getEyeColor());
    }

    private void updatePreview() {
        avatarPreviewPanel.setAvatarData(
                skinBox.getSelectedItem().toString(),
                hairStyleBox.getSelectedItem().toString(),
                hairColorBox.getSelectedItem().toString(),
                eyeColorBox.getSelectedItem().toString()
        );
    }

    private void saveAppearance() {
        gameState.setAppearance(
                skinBox.getSelectedItem().toString(),
                hairStyleBox.getSelectedItem().toString(),
                hairColorBox.getSelectedItem().toString(),
                eyeColorBox.getSelectedItem().toString()
        );

        updatePreview();

        JOptionPane.showMessageDialog(
                this,
                "Зовнішність персонажа оновлено!",
                "Салон краси",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    private static class AvatarPreviewPanel extends JPanel {

        private String skinColor = "Світла";
        private String hairStyle = "Пучок";
        private String hairColor = "Каштановий";
        private String eyeColor = "Карі";

        public AvatarPreviewPanel() {
            setBackground(new Color(255, 242, 235));
        }

        public void setAvatarData(String skinColor, String hairStyle, String hairColor, String eyeColor) {
            this.skinColor = skinColor;
            this.hairStyle = hairStyle;
            this.hairColor = hairColor;
            this.eyeColor = eyeColor;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int centerX = getWidth() / 2;

            drawCard(g2, centerX);
            drawBody(g2, centerX);
            drawFace(g2, centerX);
            drawHair(g2, centerX);
            drawEyes(g2, centerX);
            drawMouth(g2, centerX);
            drawDescription(g2, centerX);
        }

        private void drawCard(Graphics2D g2, int centerX) {
            g2.setColor(Color.WHITE);
            g2.fillRoundRect(centerX - 170, 40, 340, 500, 35, 35);

            g2.setColor(new Color(120, 82, 160));
            g2.setStroke(new BasicStroke(3));
            g2.drawRoundRect(centerX - 170, 40, 340, 500, 35, 35);

            g2.setColor(new Color(255, 218, 130));
            g2.fillOval(centerX - 95, 70, 190, 190);
        }

        private void drawBody(Graphics2D g2, int centerX) {
            g2.setColor(new Color(120, 82, 160));
            g2.fillRoundRect(centerX - 70, 320, 140, 150, 45, 45);

            g2.setColor(new Color(255, 218, 130));
            g2.fillOval(centerX - 45, 340, 90, 35);
        }

        private void drawFace(Graphics2D g2, int centerX) {
            g2.setColor(getSkinPaint());
            g2.fillOval(centerX - 65, 145, 130, 145);

            g2.setColor(getSkinPaint());
            g2.fillRoundRect(centerX - 22, 275, 44, 55, 20, 20);
        }

        private void drawHair(Graphics2D g2, int centerX) {
            g2.setColor(getHairPaint());

            if (hairStyle.equals("Пучок")) {
                g2.fillOval(centerX - 45, 105, 90, 70);
                g2.fillArc(centerX - 75, 125, 150, 110, 0, 180);
            } else if (hairStyle.equals("Довге волосся")) {
                g2.fillRoundRect(centerX - 85, 120, 170, 230, 60, 60);
                g2.setColor(getSkinPaint());
                g2.fillOval(centerX - 65, 145, 130, 145);
            } else if (hairStyle.equals("Каре")) {
                g2.fillRoundRect(centerX - 80, 120, 160, 165, 45, 45);
                g2.setColor(getSkinPaint());
                g2.fillOval(centerX - 65, 145, 130, 145);
            } else if (hairStyle.equals("Хвилясте волосся")) {
                for (int i = 0; i < 8; i++) {
                    g2.fillOval(centerX - 90 + i * 25, 120 + (i % 2) * 15, 45, 170);
                }
                g2.setColor(getSkinPaint());
                g2.fillOval(centerX - 65, 145, 130, 145);
            } else if (hairStyle.equals("Коротке волосся")) {
                g2.fillArc(centerX - 75, 115, 150, 115, 0, 180);
            } else {
                for (int i = 0; i < 10; i++) {
                    g2.fillOval(centerX - 85 + i * 18, 115 + (i % 3) * 12, 38, 55);
                }
            }
        }

        private void drawEyes(Graphics2D g2, int centerX) {
            g2.setColor(Color.WHITE);
            g2.fillOval(centerX - 38, 205, 24, 15);
            g2.fillOval(centerX + 14, 205, 24, 15);

            g2.setColor(getEyePaint());
            g2.fillOval(centerX - 31, 207, 11, 11);
            g2.fillOval(centerX + 21, 207, 11, 11);

            g2.setColor(new Color(72, 37, 120));
            g2.drawArc(centerX - 43, 190, 33, 12, 0, 180);
            g2.drawArc(centerX + 10, 190, 33, 12, 0, 180);
        }

        private void drawMouth(Graphics2D g2, int centerX) {
            g2.setColor(new Color(230, 90, 120));
            g2.fillArc(centerX - 22, 240, 44, 22, 180, 180);
        }

        private void drawDescription(Graphics2D g2, int centerX) {
            g2.setFont(new Font("Arial", Font.BOLD, 18));
            g2.setColor(new Color(72, 37, 120));
            g2.drawString("Попередній перегляд", centerX - 100, 505);
        }

        private Color getSkinPaint() {
            if (skinColor.equals("Персикова")) {
                return new Color(255, 205, 170);
            }

            if (skinColor.equals("Смаглява")) {
                return new Color(205, 140, 95);
            }

            if (skinColor.equals("Темна")) {
                return new Color(130, 80, 55);
            }

            return new Color(255, 225, 200);
        }

        private Color getHairPaint() {
            if (hairColor.equals("Блонд")) {
                return new Color(235, 195, 90);
            }

            if (hairColor.equals("Чорний")) {
                return new Color(45, 35, 35);
            }

            if (hairColor.equals("Рудий")) {
                return new Color(190, 85, 35);
            }

            if (hairColor.equals("Рожевий")) {
                return new Color(235, 120, 170);
            }

            if (hairColor.equals("Фіолетовий")) {
                return new Color(120, 70, 170);
            }

            return new Color(95, 55, 35);
        }

        private Color getEyePaint() {
            if (eyeColor.equals("Блакитні")) {
                return new Color(80, 150, 220);
            }

            if (eyeColor.equals("Зелені")) {
                return new Color(70, 150, 100);
            }

            if (eyeColor.equals("Сірі")) {
                return new Color(120, 130, 140);
            }

            if (eyeColor.equals("Фіолетові")) {
                return new Color(130, 80, 180);
            }

            return new Color(95, 55, 35);
        }
    }
}