package vibetower.ui;

import javax.swing.*;
import java.awt.*;

public class GameUiStyle {

    public static final Color BACKGROUND = Color.WHITE;
    public static final Color PURPLE = new Color(72, 37, 120);
    public static final Color LIGHT_PURPLE = new Color(160, 125, 210);
    public static final Color YELLOW = new Color(255, 232, 160);
    public static final Color CARD_BACKGROUND = new Color(255, 250, 235);

    public static JLabel title(String text) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 36));
        label.setForeground(PURPLE);
        return label;
    }

    public static JPanel createCard() {
        JPanel panel = new JPanel();
        panel.setBackground(CARD_BACKGROUND);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(PURPLE, 3),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        return panel;
    }

    public static JLabel cardTitle(String text) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 19));
        label.setForeground(PURPLE);
        return label;
    }

    public static JLabel cardText(String text) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 15));
        label.setForeground(new Color(100, 70, 140));
        return label;
    }
}