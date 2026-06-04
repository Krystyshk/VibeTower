package vibetower.ui;

import vibetower.model.GameState;
import vibetower.model.Item;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.util.ArrayList;

public class ApartmentFrame extends JFrame {

    private GameState gameState;

    public ApartmentFrame(GameState gameState) {
        this.gameState = gameState;

        setTitle("VibeTower — Квартира");
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setLayout(new BorderLayout());

        JPanel topPanel = new JPanel();
        topPanel.setOpaque(false);
        topPanel.setLayout(new BorderLayout());
        topPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 10, 30));

        JLabel titleLabel = new JLabel("Квартира персонажа", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 36));
        titleLabel.setForeground(new Color(72, 37, 120));

        topPanel.add(titleLabel, BorderLayout.CENTER);
        topPanel.add(new CurrencyPanel(gameState), BorderLayout.EAST);

        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(new RoomPanel(gameState), BorderLayout.CENTER);

        add(mainPanel, BorderLayout.CENTER);
    }

    public ApartmentFrame() {
        this(new GameState());
    }

    private static class RoomPanel extends JPanel {

        private GameState gameState;
        private Image roomImage;

        public RoomPanel(GameState gameState) {
            this.gameState = gameState;
            setBackground(Color.WHITE);

            URL roomUrl = getClass().getResource("/komnata.jpg");
            if (roomUrl != null) {
                ImageIcon roomIcon = new ImageIcon(roomUrl);
                roomImage = roomIcon.getImage();
            } else {
                System.out.println("Картинку кімнати не знайдено");
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            if (roomImage != null) {
                g.drawImage(roomImage, 70, 20, 850, 500, this);
            } else {
                g.setFont(new Font("Arial", Font.BOLD, 24));
                g.setColor(Color.RED);
                g.drawString("Картинку кімнати не знайдено", 300, 250);
            }

            drawPlacedItems(g);
        }

        private void drawPlacedItems(Graphics g) {
            ArrayList<Item> placedItems = gameState.getPlacedItems();
            g.setFont(new Font("Arial", Font.BOLD, 42));
            g.setColor(new Color(72, 37, 120));

            int x = 180;
            int y = 420;

            for (Item item : placedItems) {
                g.drawString(getItemIcon(item.getName()), x, y);
                x += 90;
                if (x > 760) {
                    x = 180;
                    y -= 80;
                }
            }
        }

        private String getItemIcon(String name) {
            if (name.equals("Диван")) return "🛋";
            if (name.equals("Ліжко")) return "🛏";
            if (name.equals("Стіл")) return "▤";
            if (name.equals("Килим")) return "▭";
            if (name.equals("Лампа")) return "💡";
            if (name.equals("Картина")) return "▧";
            return "▣";
        }
    }
}
