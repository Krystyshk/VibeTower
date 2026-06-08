package vibetower.ui;

import vibetower.model.GameState;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

public class CurrencyPanel extends JPanel {

    public CurrencyPanel(GameState gameState) {
        setOpaque(false);
        setLayout(new FlowLayout(FlowLayout.RIGHT, 14, 0));

        add(createTextBlock("⭐", String.valueOf(gameState.getLevel())));
        add(createTextBlock("XP", gameState.getLevel() >= 10 ? "MAX" : gameState.getExperience() + "/" + gameState.getXpToNextLevel()));
        add(createTextBlock("⚡", String.valueOf(gameState.getEnergy())));
        add(createMoneyBlock("/serebro.png", "🪙", String.valueOf(gameState.getSilver())));
        add(createMoneyBlock("/zoloto.png", "🟡", String.valueOf(gameState.getGold())));
    }

    private JPanel createTextBlock(String label, String amount) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        panel.setOpaque(false);

        JLabel iconLabel = new JLabel(label);
        iconLabel.setFont(new Font("Arial", Font.BOLD, 20));
        iconLabel.setForeground(new Color(72, 37, 120));

        JLabel amountLabel = new JLabel(amount);
        amountLabel.setFont(new Font("Arial", Font.BOLD, 18));
        amountLabel.setForeground(new Color(72, 37, 120));

        panel.add(iconLabel);
        panel.add(amountLabel);
        return panel;
    }

    private JPanel createMoneyBlock(String imagePath, String fallbackIcon, String amount) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        panel.setOpaque(false);

        JLabel iconLabel = new JLabel();
        URL imageUrl = getClass().getResource(imagePath);

        if (imageUrl != null) {
            ImageIcon icon = new ImageIcon(imageUrl);
            Image scaled = icon.getImage().getScaledInstance(34, 34, Image.SCALE_SMOOTH);
            iconLabel.setIcon(new ImageIcon(scaled));
        } else {
            iconLabel.setText(fallbackIcon);
            iconLabel.setFont(new Font("Arial", Font.BOLD, 20));
        }

        JLabel amountLabel = new JLabel(amount);
        amountLabel.setFont(new Font("Arial", Font.BOLD, 20));
        amountLabel.setForeground(new Color(72, 37, 120));

        panel.add(iconLabel);
        panel.add(amountLabel);

        return panel;
    }
}
