package vibetower.model;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;

/**
 * MapFrame — Карта міста в стилі Аватарії.
 * Дві вкладки: «Місця» і «Робота».
 */
public class MapFrame extends JFrame {

    private final GameState gameState;
    private JPanel tabMesta, tabRobota;
    private JButton btnMesta, btnRobota;
    private JPanel contentPanel;

    // Кольори в стилі Аватарії
    private static final Color BG_DARK      = new Color(34, 42, 58);
    private static final Color BG_PANEL     = new Color(42, 52, 72);
    private static final Color TAB_ACTIVE   = new Color(120, 190, 80);
    private static final Color TAB_INACTIVE = new Color(60, 75, 100);
    private static final Color CARD_BG      = new Color(50, 62, 86);
    private static final Color CARD_HOVER   = new Color(65, 80, 108);
    private static final Color CARD_BORDER  = new Color(80, 110, 160);
    private static final Color LOCKED_BG    = new Color(35, 42, 58);
    private static final Color LOCKED_BORD  = new Color(55, 65, 85);
    private static final Color TEXT_WHITE   = new Color(240, 240, 245);
    private static final Color TEXT_GRAY    = new Color(130, 140, 160);
    private static final Color GOLD_COLOR   = new Color(255, 200, 50);
    private static final Color HEADER_TOP   = new Color(28, 35, 50);

    public MapFrame(GameState gameState) {
        this.gameState = gameState;

        setTitle("VibeTower — Карта міста");
        setSize(820, 620);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        // Головна панель
        JPanel main = new JPanel(new BorderLayout(0, 0));
        main.setBackground(BG_DARK);
        setContentPane(main);

        main.add(buildHeader(), BorderLayout.NORTH);
        main.add(buildBody(),   BorderLayout.CENTER);
        main.add(buildFooter(), BorderLayout.SOUTH);
    }

    // ── Заголовок з вкладками ───────────────────────────────────────────────
    private JPanel buildHeader() {
        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setBackground(HEADER_TOP);
        wrap.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        // Рядок зі статистикою
        JPanel statsRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 6));
        statsRow.setBackground(HEADER_TOP);
        statsRow.add(statChip("⚡", String.valueOf(gameState.getEnergy()), new Color(80,200,80)));
        statsRow.add(statChip("🪙", String.valueOf(gameState.getSilver()), GOLD_COLOR));
        statsRow.add(statChip("🌟", String.valueOf(gameState.getGold()), new Color(255,220,80)));
        statsRow.add(statChip("📈", "XP " + gameState.getXp(), new Color(120,180,255)));
        statsRow.add(statChip("🏅", "Рів. " + gameState.getLevel(), new Color(200,130,255)));

        // Вкладки
        JPanel tabsRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        tabsRow.setBackground(HEADER_TOP);
        tabsRow.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 0));

        btnMesta   = makeTabBtn("🏘  Місця",  true);
        btnRobota  = makeTabBtn("💼  Робота", false);

        btnMesta.addActionListener(e  -> switchTab(true));
        btnRobota.addActionListener(e -> switchTab(false));

        tabsRow.add(btnMesta);
        tabsRow.add(Box.createHorizontalStrut(4));
        tabsRow.add(btnRobota);

        wrap.add(statsRow, BorderLayout.NORTH);
        wrap.add(tabsRow,  BorderLayout.SOUTH);
        return wrap;
    }

    private JLabel statChip(String icon, String value, Color color) {
        JLabel lbl = new JLabel(icon + " " + value);
        lbl.setForeground(color);
        lbl.setFont(new Font("Segoe UI Emoji", Font.BOLD, 12));
        lbl.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color.darker(), 1, true),
                BorderFactory.createEmptyBorder(2, 7, 2, 7)
        ));
        lbl.setOpaque(true);
        lbl.setBackground(new Color(color.getRed()/6, color.getGreen()/6, color.getBlue()/6));
        return lbl;
    }

    private JButton makeTabBtn(String text, boolean active) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color bg = getModel().isPressed() ? TAB_ACTIVE.darker()
                        : isSelected() ? TAB_ACTIVE : TAB_INACTIVE;
                g2.setColor(bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setSelected(active);
        btn.setForeground(active ? Color.WHITE : TEXT_GRAY);
        btn.setFont(new Font("Arial", Font.BOLD, 13));
        btn.setPreferredSize(new Dimension(150, 34));
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    // ── Тіло з вмістом ─────────────────────────────────────────────────────
    private JPanel buildBody() {
        contentPanel = new JPanel(new CardLayout());
        contentPanel.setBackground(BG_PANEL);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));

        tabMesta  = buildMestaTab();
        tabRobota = buildRobotaTab();

        contentPanel.add(tabMesta,  "mesta");
        contentPanel.add(tabRobota, "robota");
        return contentPanel;
    }

    // ── Вкладка МІСЦЯ ───────────────────────────────────────────────────────
    private JPanel buildMestaTab() {
        JPanel p = new JPanel(new GridLayout(2, 3, 12, 12));
        p.setOpaque(false);

        int lvl = gameState.getLevel();
        p.add(locationCard("🎵", "Нічний клуб", "Танці та коктейлі", 1, lvl,
                () -> { new NightClubFrame(gameState).setVisible(true); dispose(); }));
        p.add(locationCard("☕", "Кафе",         "Відпочинок і їжа",  2, lvl,
                () -> { new CafeFrame(gameState).setVisible(true); dispose(); }));
        p.add(locationCard("🌳", "Парк",         "Прогулянки, сік, вата", 4, lvl,
                () -> { new ParkFrame(gameState).setVisible(true); dispose(); }));
        p.add(locationCard("🏖", "Пляж",         "Мушлі та напої",    4, lvl,
                () -> { new BeachFrame(gameState).setVisible(true); dispose(); }));
        p.add(locationCard("🎬", "Кінотеатр",    "Фільми та попкорн", 5, lvl,
                () -> { new CinemaFrame(gameState, false).setVisible(true); dispose(); }));
        p.add(new JPanel() {{ setOpaque(false); }}); // порожня клітинка
        return p;
    }

    // ── Вкладка РОБОТА ──────────────────────────────────────────────────────
    private JPanel buildRobotaTab() {
        JPanel p = new JPanel(new GridLayout(2, 3, 12, 12));
        p.setOpaque(false);

        int lvl = gameState.getLevel();
        p.add(jobCard("👔", "Офіціант",    "Кафе · +10-15🪙 · -5⚡", 2, lvl,
                () -> { new CafeFrame(gameState).setVisible(true); dispose(); }));
        p.add(jobCard("🧹", "Двірник",     "Прибирання · +6-10🪙 · -4⚡", 1, lvl,
                () -> { new JanitorFrame(gameState, false).setVisible(true); dispose(); }));
        p.add(jobCard("🎬", "Працівник\nкінотеатру", "Каса · +15-25🪙 · -4⚡", 5, lvl,
                () -> { new CinemaFrame(gameState, true).setVisible(true); dispose(); }));
        // Порожні
        for (int i = 0; i < 3; i++) p.add(new JPanel() {{ setOpaque(false); }});
        return p;
    }

    // ── Картка локації ──────────────────────────────────────────────────────
    private JPanel locationCard(String emoji, String name, String desc,
                                int req, int current, Runnable action) {
        boolean unlocked = current >= req;
        JPanel card = new JPanel(null) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(unlocked ? CARD_BG : LOCKED_BG);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.setColor(unlocked ? CARD_BORDER : LOCKED_BORD);
                g2.setStroke(new BasicStroke(2));
                g2.drawRoundRect(1, 1, getWidth()-2, getHeight()-2, 16, 16);
                g2.dispose();
            }
        };
        card.setPreferredSize(new Dimension(220, 110));
        card.setOpaque(false);

        // Емодзі (велике, зліва)
        JLabel emojiLbl = new JLabel(emoji, SwingConstants.CENTER);
        emojiLbl.setFont(new Font("Segoe UI Emoji", Font.PLAIN, unlocked ? 38 : 28));
        emojiLbl.setBounds(10, 12, 60, 55);
        card.add(emojiLbl);

        // Назва
        JLabel nameLbl = new JLabel(unlocked ? name : "🔒 " + name);
        nameLbl.setForeground(unlocked ? TEXT_WHITE : TEXT_GRAY);
        nameLbl.setFont(new Font("Arial", Font.BOLD, 14));
        nameLbl.setBounds(78, 12, 150, 22);
        card.add(nameLbl);

        // Опис
        JLabel descLbl = new JLabel(unlocked ? desc : "з " + req + " рівня");
        descLbl.setForeground(unlocked ? TEXT_GRAY : new Color(80,90,110));
        descLbl.setFont(new Font("Arial", Font.PLAIN, 11));
        descLbl.setBounds(78, 36, 150, 18);
        card.add(descLbl);

        if (unlocked) {
            JButton btn = new JButton("Увійти →") {
                @Override protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(getModel().isRollover() ? new Color(90,160,60) : new Color(70,140,40));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                    g2.dispose();
                    super.paintComponent(g);
                }
            };
            btn.setForeground(Color.WHITE);
            btn.setFont(new Font("Arial", Font.BOLD, 12));
            btn.setBounds(78, 62, 100, 28);
            btn.setContentAreaFilled(false);
            btn.setBorderPainted(false);
            btn.setFocusPainted(false);
            btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btn.addActionListener(e -> action.run());
            card.add(btn);
        }

        // Hover ефект
        if (unlocked) {
            card.addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) { card.repaint(); }
                @Override public void mouseExited(MouseEvent e)  { card.repaint(); }
            });
        }
        return card;
    }

    // ── Картка роботи ───────────────────────────────────────────────────────
    private JPanel jobCard(String emoji, String name, String desc,
                           int req, int current, Runnable action) {
        boolean unlocked = current >= req;
        JPanel card = new JPanel(null) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color top = unlocked ? new Color(55, 100, 160) : new Color(40, 50, 68);
                Color bot = unlocked ? new Color(38, 58, 92)   : LOCKED_BG;
                GradientPaint gp = new GradientPaint(0, 0, top, 0, getHeight(), bot);
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.setColor(unlocked ? new Color(100, 160, 220) : LOCKED_BORD);
                g2.setStroke(new BasicStroke(2));
                g2.drawRoundRect(1, 1, getWidth()-2, getHeight()-2, 16, 16);
                g2.dispose();
            }
        };
        card.setOpaque(false);

        JLabel emojiLbl = new JLabel(emoji, SwingConstants.CENTER);
        emojiLbl.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 34));
        emojiLbl.setBounds(10, 12, 56, 50);
        card.add(emojiLbl);

        JLabel nameLbl = new JLabel("<html><b>" + name.replace("\n","<br>") + "</b></html>");
        nameLbl.setForeground(unlocked ? TEXT_WHITE : TEXT_GRAY);
        nameLbl.setFont(new Font("Arial", Font.BOLD, 13));
        nameLbl.setBounds(74, 10, 160, 34);
        card.add(nameLbl);

        JLabel descLbl = new JLabel("<html><small>" + (unlocked ? desc : "з " + req + " рівня") + "</small></html>");
        descLbl.setForeground(unlocked ? new Color(160, 190, 220) : new Color(80,90,110));
        descLbl.setFont(new Font("Arial", Font.PLAIN, 11));
        descLbl.setBounds(74, 46, 160, 22);
        card.add(descLbl);

        if (unlocked) {
            JButton btn = new JButton("Працювати →") {
                @Override protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(getModel().isRollover() ? new Color(220,160,30) : new Color(200,140,20));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                    g2.dispose();
                    super.paintComponent(g);
                }
            };
            btn.setForeground(Color.WHITE);
            btn.setFont(new Font("Arial", Font.BOLD, 12));
            btn.setBounds(74, 72, 130, 26);
            btn.setContentAreaFilled(false);
            btn.setBorderPainted(false);
            btn.setFocusPainted(false);
            btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btn.addActionListener(e -> action.run());
            card.add(btn);
        }
        return card;
    }

    // ── Перемикання вкладок ──────────────────────────────────────────────────
    private void switchTab(boolean mesta) {
        btnMesta.setSelected(mesta);
        btnRobota.setSelected(!mesta);
        btnMesta.setForeground(mesta ? Color.WHITE : TEXT_GRAY);
        btnRobota.setForeground(!mesta ? Color.WHITE : TEXT_GRAY);
        btnMesta.repaint();
        btnRobota.repaint();
        ((CardLayout) contentPanel.getLayout()).show(contentPanel, mesta ? "mesta" : "robota");
    }

    // ── Футер ────────────────────────────────────────────────────────────────
    private JPanel buildFooter() {
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 8));
        footer.setBackground(HEADER_TOP);
        footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(60, 75, 100)));

        JButton homeBtn = makeFooterBtn("🏠  Додому", new Color(70, 90, 130));
        homeBtn.addActionListener(e -> { new HomeFrame(gameState).setVisible(true); dispose(); });
        footer.add(homeBtn);
        return footer;
    }

    private JButton makeFooterBtn(String text, Color color) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? color.brighter() : color);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Arial", Font.BOLD, 13));
        btn.setPreferredSize(new Dimension(160, 36));
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }
}
