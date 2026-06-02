package vibetower.ui;

import javax.swing.*;
import java.awt.*;

public class StartFrame extends JFrame {

    public StartFrame() {
        setTitle("VibeTower");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayout(5, 1, 10, 22));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(105, 250, 105, 250));

        JLabel titleLabel = new JLabel("VibeTower", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 42));

        JLabel subtitleLabel = new JLabel("Створи свого персонажа та почни гру", SwingConstants.CENTER);
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 18));

        JLabel managerHintLabel = new JLabel("Вхід доступний для менеджер-акаунту", SwingConstants.CENTER);
        managerHintLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        managerHintLabel.setForeground(new Color(90, 90, 90));

        JButton registerButton = new JButton("Зареєструватися");
        JButton loginButton = new JButton("Увійти як менеджер");

        registerButton.setFont(new Font("Arial", Font.BOLD, 18));
        loginButton.setFont(new Font("Arial", Font.BOLD, 18));

        mainPanel.add(titleLabel);
        mainPanel.add(subtitleLabel);
        mainPanel.add(managerHintLabel);
        mainPanel.add(registerButton);
        mainPanel.add(loginButton);

        add(mainPanel, BorderLayout.CENTER);

        registerButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(
                    this,
                    "Реєстрація персонажа буде додана у наступному етапі. Зараз доступний вхід через менеджер-акаунт."
            );
        });

        loginButton.addActionListener(e -> {
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
            dispose();
        });
    }
}
