package ui.admin;

import dao.PurchaseDao;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.util.List;

public class AdminDashboard extends JFrame {
    private JButton manageCarsBtn;
private JButton manageDealersBtn;
private String accessScope;
private static final Color WARNING = new Color(230, 160, 0);


    private static final Color BG_DARK        = new Color(8,   10,  18);
    private static final Color PANEL_BG       = new Color(13,  16,  28);
    private static final Color CARD_BG        = new Color(18,  22,  38);
    private static final Color CARD_BORDER    = new Color(35,  42,  68);
    private static final Color ACCENT         = new Color(0,   180, 220);
    private static final Color ACCENT_GLOW    = new Color(0,   180, 220, 35);
    private static final Color SUCCESS        = new Color(0,   200, 130);
    private static final Color TEXT_PRIMARY   = new Color(225, 228, 245);
    private static final Color TEXT_SECONDARY = new Color(110, 118, 150);
    private static final Color INPUT_BG       = new Color(22,  27,  46);
    private static final Color INPUT_BORDER   = new Color(45,  52,  80);
    private static final Color TABLE_HEADER   = new Color(22,  27,  46);
    private static final Color TABLE_ROW      = new Color(18,  22,  38);
    private static final Color TABLE_ROW_ALT  = new Color(21,  26,  44);
    private static final Color TABLE_SEL      = new Color(0,   180, 220, 60);
    private static final Color DIVIDER        = new Color(30,  36,  58);

    private String adminName;

    public AdminDashboard(String adminName,String accessScope) {
        this.adminName = adminName;
        this.accessScope = accessScope;
        setTitle("Admin Dashboard");
        setSize(520, 420);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(BG_DARK);
        getContentPane().setLayout(new BorderLayout());

        getContentPane().add(buildHeader(), BorderLayout.NORTH);
        getContentPane().add(buildCenter(), BorderLayout.CENTER);
    }

    // ── Header ────────────────────────────────────────────────────────────────
    private JPanel buildHeader() {
        JPanel h = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(PANEL_BG);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(DIVIDER);
                g2.fillRect(0, getHeight() - 1, getWidth(), 1);
                g2.dispose();
            }
        };
        h.setOpaque(false);
        h.setBorder(new EmptyBorder(14, 24, 14, 24));

        JLabel brand = new JLabel("CarSaathi");
        brand.setFont(new Font("Segoe UI", Font.BOLD, 16));
        brand.setForeground(ACCENT);

        JLabel title = new JLabel("Admin Dashboard", JLabel.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(TEXT_PRIMARY);

        JLabel chip = new JLabel("  " + adminName + "  ") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(ACCENT_GLOW);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 20, 20));
                g2.setColor(new Color(0, 180, 220, 80));
                g2.setStroke(new BasicStroke(1f));
                g2.draw(new RoundRectangle2D.Float(0, 0, getWidth()-1, getHeight()-1, 20, 20));
                super.paintComponent(g);
                g2.dispose();
            }
        };
        chip.setOpaque(false);
        chip.setForeground(ACCENT);
        chip.setFont(new Font("Segoe UI", Font.BOLD, 12));
        chip.setBorder(new EmptyBorder(5, 12, 5, 12));

        h.add(brand, BorderLayout.WEST);
        h.add(title, BorderLayout.CENTER);
        h.add(chip,  BorderLayout.EAST);
        return h;
    }

    // ── Center card with buttons ──────────────────────────────────────────────
    private JPanel buildCenter() {
        JPanel wrap = new JPanel(new GridBagLayout());
        wrap.setOpaque(false);
        wrap.setBorder(new EmptyBorder(30, 40, 30, 40));

        JPanel card = new JPanel(new GridBagLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(CARD_BG);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 16, 16));
                g2.setColor(CARD_BORDER);
                g2.setStroke(new BasicStroke(1f));
                g2.draw(new RoundRectangle2D.Float(0.5f, 0.5f, getWidth()-1, getHeight()-1, 16, 16));
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(32, 36, 32, 36));
        card.setPreferredSize(new Dimension(420, 300));

        GridBagConstraints g = new GridBagConstraints();
        g.gridx = 0; g.fill = GridBagConstraints.HORIZONTAL; g.weightx = 1.0;

        // Subtitle
        g.gridy = 0; g.insets = new Insets(0, 0, 28, 0);
        JLabel sub = new JLabel("What would you like to manage?", JLabel.CENTER);
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        sub.setForeground(TEXT_SECONDARY);
        card.add(sub, g);

        // Buttons
        manageCarsBtn    = styledButton("Manage Car Models", ACCENT);
        manageDealersBtn = styledButton("Register Dealers",    ACCENT);
        JButton viewPurchasesBtn = styledButton("View All Purchases", SUCCESS);
        JButton analyticsBtn = styledButton("View Analytics", WARNING);
analyticsBtn.addActionListener(e -> new AdminAnalyticsUI().setVisible(true));

g.gridy = 4; g.insets = new Insets(12, 0, 0, 0);
card.add(analyticsBtn, g);
        manageCarsBtn.addActionListener(e ->
    new ManageCarsUI(accessScope.equals("LIMITED_ACCESS")).setVisible(true));
manageDealersBtn.addActionListener(e ->
    new ManageDealersUI().setVisible(true));
viewPurchasesBtn.addActionListener(e -> showPurchases());

        g.gridy = 1; g.insets = new Insets(0, 0, 12, 0);
        card.add(manageCarsBtn, g);
        g.gridy = 2;
        card.add(manageDealersBtn, g);
        g.gridy = 3; g.insets = new Insets(0, 0, 0, 0);
        card.add(viewPurchasesBtn, g);

        wrap.add(card);
        return wrap;
    }

    // ── Logic (unchanged) ─────────────────────────────────────────────────────
    private void showPurchases() {
    PurchaseDao purchaseDao = new PurchaseDao();
    List<String> purchases = purchaseDao.getAllPurchases();

    String[] cols = {"Purchase ID", "Car ID", "Customer ID", "Price", "Date"};
    DefaultTableModel model = new DefaultTableModel(cols, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };

    if (purchases.isEmpty()) {
        model.addRow(new Object[]{"—", "—", "—", "No purchases found.", "—"});
    } else {
        for (String p : purchases) {
            // expects format: "PurchaseID: 1 | CarID: 2 | CustomerID: 3 | Price: 500.0 | Date: 2024-01-01"
            try {
                String[] parts = p.split("\\|");
                model.addRow(new Object[]{
                    parts[0].replace("PurchaseID:", "").trim(),
                    parts[1].replace("CarID:", "").trim(),
                    parts[2].replace("CustomerID:", "").trim(),
                    parts[3].replace("Price:", "").trim(),
                    parts[4].replace("Date:", "").trim()
                });
            } catch (Exception ex) {
                model.addRow(new Object[]{"?", "?", "?", p, "?"});
            }
        }
    }

    JTable table = new JTable(model);
    table.setBackground(TABLE_ROW);
    table.setForeground(TEXT_PRIMARY);
    table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
    table.setRowHeight(30);
    table.setShowGrid(false);
    table.setIntercellSpacing(new Dimension(0, 0));
    table.setSelectionBackground(TABLE_SEL);
    table.setFocusable(false);

    JTableHeader header = table.getTableHeader();
    header.setBackground(TABLE_HEADER);
    header.setForeground(TEXT_SECONDARY);
    header.setFont(new Font("Segoe UI", Font.BOLD, 12));
    header.setReorderingAllowed(false);

    DefaultTableCellRenderer r = new DefaultTableCellRenderer() {
        @Override public Component getTableCellRendererComponent(
                JTable t, Object val, boolean sel, boolean foc, int row, int col) {
            super.getTableCellRendererComponent(t, val, sel, foc, row, col);
            setOpaque(true);
            setForeground(TEXT_PRIMARY);
            setBackground(row % 2 == 0 ? TABLE_ROW : TABLE_ROW_ALT);
            setBorder(new EmptyBorder(0, 8, 0, 8));
            return this;
        }
    };
    for (int i = 0; i < table.getColumnCount(); i++)
        table.getColumnModel().getColumn(i).setCellRenderer(r);

    JScrollPane scroll = new JScrollPane(table);
    scroll.setPreferredSize(new Dimension(620, 320));
    scroll.setBorder(BorderFactory.createLineBorder(INPUT_BORDER, 1));
    scroll.setBackground(INPUT_BG);
    scroll.getViewport().setBackground(INPUT_BG);

    JOptionPane.showMessageDialog(this, scroll,
            "All Purchases", JOptionPane.PLAIN_MESSAGE);
}

    // ── UI helpers ────────────────────────────────────────────────────────────
    private JButton styledButton(String text, Color color) {
        JButton btn = new JButton(text) {
            boolean hovered = false, pressed = false;
            {
                addMouseListener(new MouseAdapter() {
                    @Override public void mouseEntered(MouseEvent e)  { hovered = true;  repaint(); }
                    @Override public void mouseExited(MouseEvent e)   { hovered = false; repaint(); }
                    @Override public void mousePressed(MouseEvent e)  { pressed = true;  repaint(); }
                    @Override public void mouseReleased(MouseEvent e) { pressed = false; repaint(); }
                });
            }
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (hovered) {
                    g2.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 50));
                    g2.fill(new RoundRectangle2D.Float(-3,-3, getWidth()+6, getHeight()+6, 14, 14));
                }
                g2.setColor(pressed ? color.darker() : hovered ? color.brighter() : color);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8));
                g2.setFont(new Font("Segoe UI", Font.BOLD, 13));
                g2.setColor(Color.WHITE);
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(getText(),
                    (getWidth()  - fm.stringWidth(getText())) / 2,
                    (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
                g2.dispose();
            }
            @Override protected void paintBorder(Graphics g) {}
        };
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(340, 44));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }
}