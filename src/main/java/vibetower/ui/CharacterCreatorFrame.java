package vibetower.ui;

import vibetower.model.PlayerProfile;

import javax.swing.*;
import java.awt.*;

public class CharacterCreatorFrame extends JFrame {

    private JTextField nameField;

    private JComboBox<String> genderBox;
    private JComboBox<String> skinBox;
    private JComboBox<String> hairStyleBox;
    private JComboBox<String> hairColorBox;
    private JComboBox<String> eyeColorBox;
    private JComboBox<String> outfitBox;

    private CharacterAvatarPanel avatarPanel;

    public CharacterCreatorFrame() {
        setTitle("VibeTower — Створення персонажа");
        setSize(1100, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        add(createHeaderPanel(), BorderLayout.NORTH);
        add(createSettingsPanel(), BorderLayout.WEST);

        avatarPanel = new CharacterAvatarPanel();
        add(avatarPanel, BorderLayout.CENTER);

        add(createBottomPanel(), BorderLayout.SOUTH);

        updatePreview();

        setVisible(true);
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(255, 248, 240));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        JLabel title = new JLabel("Створення персонажа", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 34));
        title.setForeground(new Color(65, 45, 35));

        JLabel subtitle = new JLabel(
                "Обери аватара у стилі VibeTower: голова 25%, тулуб 30%, ноги 45%",
                SwingConstants.CENTER
        );
        subtitle.setFont(new Font("Arial", Font.PLAIN, 16));
        subtitle.setForeground(new Color(105, 75, 55));

        panel.add(title, BorderLayout.CENTER);
        panel.add(subtitle, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createSettingsPanel() {
        JPanel panel = new JPanel();
        panel.setPreferredSize(new Dimension(360, 0));
        panel.setLayout(new GridLayout(8, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
        panel.setBackground(new Color(255, 248, 240));

        nameField = new JTextField();
        nameField.setBorder(BorderFactory.createTitledBorder("Ім’я персонажа"));

        genderBox = new JComboBox<>(new String[]{
                "Жіночий",
                "Чоловічий"
        });

        skinBox = new JComboBox<>(new String[]{
                "Світла",
                "Рожева",
                "Смаглява",
                "Темна"
        });

        hairStyleBox = new JComboBox<>(new String[]{
                "Пучок",
                "Довге",
                "Коротке",
                "Об’ємне"
        });

        hairColorBox = new JComboBox<>(new String[]{
                "Каштановий",
                "Блонд",
                "Чорний",
                "Рудий",
                "Рожевий"
        });

        eyeColorBox = new JComboBox<>(new String[]{
                "Карі",
                "Блакитні",
                "Зелені",
                "Сірі"
        });

        outfitBox = new JComboBox<>(new String[]{
                "Повсякденний",
                "Світлий верх",
                "Чорний костюм",
                "Худі"
        });

        genderBox.setBorder(BorderFactory.createTitledBorder("Стать"));
        skinBox.setBorder(BorderFactory.createTitledBorder("Колір шкіри"));
        hairStyleBox.setBorder(BorderFactory.createTitledBorder("Зачіска"));
        hairColorBox.setBorder(BorderFactory.createTitledBorder("Колір волосся"));
        eyeColorBox.setBorder(BorderFactory.createTitledBorder("Колір очей"));
        outfitBox.setBorder(BorderFactory.createTitledBorder("Одяг"));

        genderBox.addActionListener(e -> updatePreview());
        skinBox.addActionListener(e -> updatePreview());
        hairStyleBox.addActionListener(e -> updatePreview());
        hairColorBox.addActionListener(e -> updatePreview());
        eyeColorBox.addActionListener(e -> updatePreview());
        outfitBox.addActionListener(e -> updatePreview());

        panel.add(nameField);
        panel.add(genderBox);
        panel.add(skinBox);
        panel.add(hairStyleBox);
        panel.add(hairColorBox);
        panel.add(eyeColorBox);
        panel.add(outfitBox);

        JLabel note = new JLabel(
                "<html><center>Це базовий макет персонажа.<br>Пізніше можна замінити його на PNG-аватар.</center></html>",
                SwingConstants.CENTER
        );
        note.setFont(new Font("Arial", Font.PLAIN, 13));
        note.setForeground(new Color(120, 90, 65));
        panel.add(note);

        return panel;
    }

    private JPanel createBottomPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(new Color(255, 248, 240));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 20, 20, 20));

        JButton backButton = new JButton("Назад");
        JButton createButton = new JButton("Створити персонажа");

        backButton.setFont(new Font("Arial", Font.BOLD, 16));
        createButton.setFont(new Font("Arial", Font.BOLD, 16));

        panel.add(backButton);
        panel.add(createButton);

        backButton.addActionListener(e -> {
            new StartFrame();
            dispose();
        });

        createButton.addActionListener(e -> createCharacter());

        return panel;
    }

    private void updatePreview() {
        if (avatarPanel == null) {
            return;
        }

        avatarPanel.updateAvatar(
                getSelected(genderBox),
                getSelected(skinBox),
                getSelected(hairStyleBox),
                getSelected(hairColorBox),
                getSelected(eyeColorBox),
                getSelected(outfitBox)
        );
    }

    private void createCharacter() {
        String name = nameField.getText().trim();

        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Введіть ім’я персонажа",
                    "Помилка",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        PlayerProfile profile = new PlayerProfile(
                name,
                getSelected(genderBox),
                getSelected(skinBox),
                getSelected(hairStyleBox),
                getSelected(hairColorBox),
                getSelected(eyeColorBox),
                getSelected(outfitBox)
        );

        JOptionPane.showMessageDialog(
                this,
                "Персонажа створено!\nID: " + profile.getId() + "\nІм’я: " + profile.getName(),
                "Успішно",
                JOptionPane.INFORMATION_MESSAGE
        );

        // Паспорт персонажа підключимо наступним комітом.
        // Поки що просто залишаємо користувача на цьому екрані.
    }

    private String getSelected(JComboBox<String> box) {
        Object selected = box.getSelectedItem();
        return selected == null ? "" : selected.toString();
    }
}
