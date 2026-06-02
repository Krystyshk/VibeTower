package vibetower.model;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import javax.imageio.ImageIO;

public class CafeFrame extends JFrame {
    private GameState gameState;
    private JLabel character;
    private Timer movementTimer;

    private Rectangle[] obstacles = {

    };

    public CafeFrame(GameState gameState) {
        this.gameState = gameState;

        setTitle("VibeTower — Кафе");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        BackgroundPanel bgPanel = new BackgroundPanel("src/main/java/vibetower/cafe.png");
        bgPanel.setLayout(null);
        setContentPane(bgPanel);

        character = new JLabel("👤");
        character.setOpaque(true);
        character.setBackground(Color.GRAY);
        character.setBounds(100, 400, 40, 60);
        bgPanel.add(character);

        bgPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                moveTo(e.getX(), e.getY());
            }
        });

        bgPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                System.out.println("X: " + e.getX() + ", Y: " + e.getY());
            }
        });
    }

    private void moveTo(int targetX, int targetY) {
        if (movementTimer != null && movementTimer.isRunning()) movementTimer.stop();

        movementTimer = new Timer(20, e -> {
            int nextX = character.getX();
            int nextY = character.getY();

            if (nextX < targetX) nextX += 3;
            else if (nextX > targetX) nextX -= 3;
            if (nextY < targetY) nextY += 3;
            else if (nextY > targetY) nextY -= 3;

            Rectangle nextRect = new Rectangle(nextX, nextY, character.getWidth(), character.getHeight());
            boolean collision = false;
            for (Rectangle obs : obstacles) {
                if (nextRect.intersects(obs)) {
                    collision = true;
                    break;
                }
            }

            if (!collision) {
                character.setLocation(nextX, nextY);
            } else {
                movementTimer.stop();
            }

            if (Math.abs(nextX - targetX) < 5 && Math.abs(nextY - targetY) < 5) movementTimer.stop();
        });
        movementTimer.start();
    }

    class BackgroundPanel extends JPanel {
        private Image img;
        public BackgroundPanel(String path) {
            try { img = ImageIO.read(new File(path)); }
            catch (Exception e) { System.out.println("Фон не знайдено!"); }
        }
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (img != null) g.drawImage(img, 0, 0, getWidth(), getHeight(), this);
        }
    }
}