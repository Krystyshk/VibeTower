package vibetower.ui;

import vibetower.model.GameState;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

public class CurrencyPanel extends JPanel {

    public CurrencyPanel(GameState gameState) {
        setOpaque(false);
        setLayout(new FlowLayout(FlowLayout.RIGHT, 18, 0));

        add(createMoneyBlock("/serebro.png", String.valueOf(gameState.getSilver())));
        add(createMoneyBlock("/zoloto.png", String.valueOf(gameState.getGold())));
    }

    private JPanel createMoneyBlock(String imagePath, String amount) {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 0));

        JLabel iconLabel = new JLabel();
        URL imageUrl = getClass().getResource(imagePath);

        if (imageUrl != null) {
            ImageIcon icon = new ImageIcon(imageUrl);
            Image scaled = icon.getImage().getScaledInstance(42, 42, Image.SCALE_SMOOTH);
            iconLabel.setIcon(new ImageIcon(scaled));
        } else {
            iconLabel.setText("?");
            System.out.println("Не знайдено картинку: " + imagePath);
        }

        JLabel amountLabel = new JLabel(amount);
        amountLabel.setFont(new Font("Arial", Font.BOLD, 24));
        amountLabel.setForeground(new Color(72, 37, 120));

        panel.add(iconLabel);
        panel.add(amountLabel);
        return panel;
    }
}
