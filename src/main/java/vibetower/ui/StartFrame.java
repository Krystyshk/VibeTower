package vibetower.ui;

import javax.swing.*;
import java.awt.*;

public class StartFrame extends JFrame {

    public StartFrame() {
        setTitle("VibeTower");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4, 1, 10, 20));
        panel.setBorder(BorderFactory.createEmptyBorder(100, 250, 100, 250));

        JLabel title = new JLabel("VibeTower", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 42));

        JLabel subtitle = new JLabel("Створи свого персонажа та почни гру", SwingConstants.CENTER);
        subtitle.setFont(new Font("Arial", Font.PLAIN, 18));

        JButton registerButton = new JButton("Зареєструватися");
        JButton loginButton = new JButton("Увійти");

        registerButton.setFont(new Font("Arial", Font.BOLD, 18));
        loginButton.setFont(new Font("Arial", Font.BOLD, 18));

        panel.add(title);
        panel.add(subtitle);
        panel.add(registerButton);
        panel.add(loginButton);

        add(panel);

        setVisible(true);
    }
}
