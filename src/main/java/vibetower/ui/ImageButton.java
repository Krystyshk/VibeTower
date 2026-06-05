package vibetower.ui;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

public class ImageButton extends JButton {

    private static final int BUTTON_WIDTH = 300;
    private static final int BUTTON_HEIGHT = 75;

    public ImageButton(String imagePath) {
        setPreferredSize(new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT));
        setMinimumSize(new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT));
        setMaximumSize(new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT));

        setBorderPainted(false);
        setContentAreaFilled(false);
        setFocusPainted(false);
        setOpaque(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));

        URL imageUrl = getClass().getResource(imagePath);

        if (imageUrl != null) {
            ImageIcon originalIcon = new ImageIcon(imageUrl);

            Image scaledImage = originalIcon.getImage().getScaledInstance(
                    BUTTON_WIDTH,
                    BUTTON_HEIGHT,
                    Image.SCALE_SMOOTH
            );

            setIcon(new ImageIcon(scaledImage));
        } else {
            setText("Картинку не знайдено");
            setFont(new Font("Arial", Font.BOLD, 16));
            System.out.println("Не знайдено картинку: " + imagePath);
        }
    }
}