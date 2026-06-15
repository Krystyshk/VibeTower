package vibetower.ui;

import vibetower.model.GameState;
import vibetower.model.Item;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;
import java.util.Map;

public class AppearanceInventoryFrame extends JFrame {

    private final GameState gameState;
    private String activeCategory = "Усе";
    private JPanel itemsPanel;
    private AnimatedCharacterPanel charPanel;
    private JPanel wornPanel;

    private static final Color BG       = new Color(255, 242, 235);
    private static final Color PURPLE   = new Color(72, 37, 120);
    private static final Color GOLD     = new Color(255, 218, 130);
    private static final Color GREEN_BG = new Color(220, 255, 220);
    private static final Color GREEN_BR = new Color(60, 160, 60);

    private static final String[] CATEGORIES = {"Усе", "Верх", "Низ", "Сукні", "Взуття", "Аксесуари", "Головний убір"};

    public AppearanceInventoryFrame(GameState gameState) {
        this.gameState = gameState;
        setTitle("VibeTower — Гардероб");
        setSize(1100, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG);

        // ── заголовок ──────────────────────────────────────────────────────────
        JLabel title = new JLabel("👗 Гардероб", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 30));
        title.setForeground(PURPLE);
        title.setBorder(new EmptyBorder(18, 0, 10, 0));
        root.add(title, BorderLayout.NORTH);

        // ── ліво: категорії + список предметів ────────────────────────────────
        JPanel left = new JPanel(new BorderLayout());
        left.setOpaque(false);
        left.setPreferredSize(new Dimension(640, 0));

        left.add(buildCategoryBar(), BorderLayout.NORTH);

        itemsPanel = new JPanel();
        itemsPanel.setLayout(new WrapLayout(FlowLayout.LEFT, 10, 10));
        itemsPanel.setBackground(Color.WHITE);
        JScrollPane scroll = new JScrollPane(itemsPanel);
        scroll.setBorder(BorderFactory.createLineBorder(PURPLE, 2));
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        left.add(scroll, BorderLayout.CENTER);
        root.add(left, BorderLayout.WEST);

        // ── право: персонаж + одягнуто ────────────────────────────────────────
        JPanel right = new JPanel(new BorderLayout());
        right.setOpaque(false);
        right.setBorder(new EmptyBorder(0, 12, 0, 12));

        charPanel = new AnimatedCharacterPanel(
                mapGender(gameState.getGender()),
                mapHair(gameState.getHairStyle()),
                mapEyes(gameState.getEyeColor())
        );
        charPanel.setWalking(true);
        right.add(charPanel, BorderLayout.CENTER);

        wornPanel = new JPanel();
        wornPanel.setLayout(new BoxLayout(wornPanel, BoxLayout.Y_AXIS));
        wornPanel.setBackground(Color.WHITE);
        wornPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(PURPLE, 2),
                new EmptyBorder(8, 12, 8, 12)
        ));
        wornPanel.setPreferredSize(new Dimension(0, 160));
        right.add(wornPanel, BorderLayout.SOUTH);
        root.add(right, BorderLayout.CENTER);

        setContentPane(root);
        refreshItems();
        refreshWornPanel();

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                charPanel.stopAnimation();
            }
        });
    }

    // ── панель вкладок категорій ───────────────────────────────────────────────
    private JPanel buildCategoryBar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 6));
        bar.setBackground(BG);
        for (String cat : CATEGORIES) {
            JButton btn = new JButton(cat);
            btn.setFont(new Font("Arial", Font.BOLD, 13));
            btn.setFocusPainted(false);
            btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            styleTab(btn, cat.equals(activeCategory));
            btn.addActionListener(e -> {
                activeCategory = cat;
                for (Component c : bar.getComponents()) {
                    if (c instanceof JButton) styleTab((JButton) c, ((JButton) c).getText().equals(cat));
                }
                refreshItems();
            });
            bar.add(btn);
        }
        return bar;
    }

    private void styleTab(JButton btn, boolean active) {
        btn.setBackground(active ? PURPLE : new Color(230, 215, 255));
        btn.setForeground(active ? Color.WHITE : PURPLE);
        btn.setBorder(BorderFactory.createLineBorder(PURPLE, 2));
    }

    // ── оновити список предметів ──────────────────────────────────────────────
    private void refreshItems() {
        itemsPanel.removeAll();
        List<Item> inv = gameState.getInventory();
        Map<String, Item> equipped = gameState.getEquippedItems();

        boolean female = "Жіночий".equals(gameState.getGender()) || "Жіночий персонаж".equals(gameState.getGender());

        for (Item item : inv) {
            if (item == null || item.getCategory() == null) continue;
            if (!activeCategory.equals("Усе") && !item.getCategory().equals(activeCategory)) continue;
            if ("Сукні".equals(item.getCategory()) && !female) continue;
            if ("Сукня".equals(item.getCategory()) && !female) continue;

            boolean isEquipped = equipped.containsKey(item.getCategory())
                    && equipped.get(item.getCategory()).getName().equals(item.getName());

            itemsPanel.add(buildCard(item, isEquipped));
        }

        if (itemsPanel.getComponentCount() == 0) {
            JLabel empty = new JLabel("Тут поки нічого немає 🛍");
            empty.setFont(new Font("Arial", Font.PLAIN, 16));
            empty.setForeground(Color.GRAY);
            empty.setBorder(new EmptyBorder(20, 20, 20, 20));
            itemsPanel.add(empty);
        }

        itemsPanel.revalidate();
        itemsPanel.repaint();
    }

    // ── картка одного предмету ────────────────────────────────────────────────
    private JPanel buildCard(Item item, boolean equipped) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setPreferredSize(new Dimension(155, 120));
        card.setBackground(equipped ? GREEN_BG : Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(equipped ? GREEN_BR : PURPLE, 2),
                new EmptyBorder(6, 8, 6, 8)
        ));

        JLabel name = new JLabel(item.getName());
        name.setFont(new Font("Arial", Font.BOLD, 13));
        name.setForeground(PURPLE);
        name.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel cat = new JLabel(item.getCategory());
        cat.setFont(new Font("Arial", Font.PLAIN, 11));
        cat.setForeground(Color.GRAY);
        cat.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel status = new JLabel(equipped ? "✅ Одягнуто" : "");
        status.setFont(new Font("Arial", Font.BOLD, 11));
        status.setForeground(new Color(40, 140, 40));
        status.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton actionBtn = new JButton(equipped ? "❌ Зняти" : "✅ Одягнути");
        actionBtn.setFont(new Font("Arial", Font.BOLD, 12));
        actionBtn.setBackground(equipped ? new Color(255, 200, 200) : GOLD);
        actionBtn.setForeground(PURPLE);
        actionBtn.setFocusPainted(false);
        actionBtn.setBorder(BorderFactory.createLineBorder(PURPLE, 1));
        actionBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        actionBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        actionBtn.addActionListener(e -> {
            if (equipped) {
                gameState.unequipItem(item.getCategory());
            } else {
                gameState.equipItem(item);
            }
            refreshItems();
            refreshWornPanel();
        });

        card.add(name);
        card.add(Box.createVerticalStrut(3));
        card.add(cat);
        card.add(Box.createVerticalStrut(3));
        card.add(status);
        card.add(Box.createVerticalStrut(6));
        card.add(actionBtn);
        return card;
    }

    // ── панель "зараз одягнуто" ───────────────────────────────────────────────
    private void refreshWornPanel() {
        wornPanel.removeAll();

        JLabel header = new JLabel("🧍 Зараз одягнуто:");
        header.setFont(new Font("Arial", Font.BOLD, 14));
        header.setForeground(PURPLE);
        wornPanel.add(header);
        wornPanel.add(Box.createVerticalStrut(4));

        Map<String, Item> equipped = gameState.getEquippedItems();
        if (equipped.isEmpty()) {
            JLabel none = new JLabel("  Нічого не одягнуто");
            none.setFont(new Font("Arial", Font.PLAIN, 13));
            none.setForeground(Color.GRAY);
            wornPanel.add(none);
        } else {
            for (Map.Entry<String, Item> entry : equipped.entrySet()) {
                JLabel lbl = new JLabel("  • " + entry.getKey() + ": " + entry.getValue().getName());
                lbl.setFont(new Font("Arial", Font.PLAIN, 13));
                lbl.setForeground(new Color(40, 100, 40));
                wornPanel.add(lbl);
            }
        }

        wornPanel.revalidate();
        wornPanel.repaint();
    }

    // ── маппінг ───────────────────────────────────────────────────────────────

    private String mapGender(String gender) {
        if (gender == null) return "female";
        return gender.toLowerCase().contains("чол") ? "male" : "female";
    }

    private String mapHair(String hairStyle) {
        if (hairStyle == null) return "hair_wavy";
        switch (hairStyle) {
            case "Довге":
            case "Довге пряме": return "hair_wavy";
            case "Кучеряве":    return "hair_curly";
            case "Хвіст":       return "hair_ponytail";
            case "Пучок":       return "hair_bun";
            case "Каре":        return "hair_bob";
            case "Коса":        return "hair_braids";
            default:            return "hair_wavy";
        }
    }

    private String mapEyes(String eyeColor) {
        if (eyeColor == null) return "eyes_brown";
        switch (eyeColor) {
            case "Карі":     return "eyes_brown";
            case "Сині":     return "eyes_blue";
            case "Зелені":   return "eyes_green";
            case "Сірі":     return "eyes_gray";
            case "Горіхові": return "eyes_hazel";
            default:         return "eyes_brown";
        }
    }

    // ── WrapLayout ────────────────────────────────────────────────────────────
    static class WrapLayout extends FlowLayout {
        WrapLayout(int align, int hgap, int vgap) { super(align, hgap, vgap); }

        @Override
        public Dimension preferredLayoutSize(Container target) { return layoutSize(target, true); }
        @Override
        public Dimension minimumLayoutSize(Container target) { return layoutSize(target, false); }

        private Dimension layoutSize(Container target, boolean preferred) {
            synchronized (target.getTreeLock()) {
                int targetWidth = target.getSize().width;
                if (targetWidth == 0) targetWidth = Integer.MAX_VALUE;
                int hgap = getHgap(), vgap = getVgap();
                Insets insets = target.getInsets();
                int maxw = targetWidth - (insets.left + insets.right + hgap * 2);
                int totalH = insets.top + insets.bottom + vgap * 2;
                int rowW = 0, rowH = 0;
                for (int i = 0; i < target.getComponentCount(); i++) {
                    Component c = target.getComponent(i);
                    if (!c.isVisible()) continue;
                    Dimension d = preferred ? c.getPreferredSize() : c.getMinimumSize();
                    if (rowW + d.width > maxw) {
                        totalH += rowH + vgap;
                        rowW = 0; rowH = 0;
                    }
                    rowW += d.width + hgap;
                    rowH = Math.max(rowH, d.height);
                }
                totalH += rowH;
                return new Dimension(targetWidth, totalH);
            }
        }
    }
}