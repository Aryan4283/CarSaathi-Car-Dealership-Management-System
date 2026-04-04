package ui.dealer;

import service.PurchaseService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.util.List;

public class ViewSalesUI extends JFrame {

    private static final Color BG_DARK        = new Color(8,   10,  18);
    private static final Color PANEL_BG       = new Color(13,  16,  28);
    private static final Color CARD_BG        = new Color(18,  22,  38);
    private static final Color CARD_BORDER    = new Color(35,  42,  68);
    private static final Color ACCENT         = new Color(0,   180, 220);
    private static final Color TEXT_PRIMARY   = new Color(225, 228, 245);
    private static final Color TEXT_SECONDARY = new Color(110, 118, 150);
    private static final Color INPUT_BG       = new Color(22,  27,  46);
    private static final Color INPUT_BORDER   = new Color(45,  52,  80);
    private static final Color TABLE_HEADER   = new Color(22,  27,  46);
    private static final Color TABLE_ROW      = new Color(18,  22,  38);
    private static final Color TABLE_ROW_ALT  = new Color(21,  26,  44);
    private static final Color TABLE_SEL      = new Color(0,   180, 220, 60);
    private static final Color DIVIDER        = new Color(30,  36,  58);

    private DefaultTableModel tableModel;
    private PurchaseService   purchaseService;
    private int               dealerId;

    public ViewSalesUI(int dealerId) {
        this.dealerId        = dealerId;
        this.purchaseService = new PurchaseService();

        setTitle("Sales History");
        setSize(720, 500);
        setMinimumSize(new Dimension(600, 400));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        getContentPane().setBackground(BG_DARK);
        getContentPane().setLayout(new BorderLayout());

        getContentPane().add(buildHeader(), BorderLayout.NORTH);
        getContentPane().add(buildCard(),   BorderLayout.CENTER);

        loadSales();
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

        JLabel title = new JLabel("Sales History", JLabel.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(TEXT_PRIMARY);

        h.add(brand, BorderLayout.WEST);
        h.add(title, BorderLayout.CENTER);
        return h;
    }

    // ── Main card ─────────────────────────────────────────────────────────────
    private JPanel buildCard() {
        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setOpaque(false);
        wrap.setBorder(new EmptyBorder(18, 22, 18, 22));

        JPanel card = new JPanel(new BorderLayout(0, 12)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                    RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(CARD_BG);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 14, 14));
                g2.setColor(CARD_BORDER);
                g2.setStroke(new BasicStroke(1f));
                g2.draw(new RoundRectangle2D.Float(0.5f, 0.5f,
                        getWidth()-1, getHeight()-1, 14, 14));
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(18, 18, 18, 18));

        // Header row
        JPanel head = new JPanel(new BorderLayout());
        head.setOpaque(false);
        JLabel lbl = new JLabel("All Sales");
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lbl.setForeground(TEXT_PRIMARY);
        head.add(lbl, BorderLayout.WEST);

        JButton refreshBtn = ghostButton("Refresh");
        refreshBtn.addActionListener(e -> loadSales());
        head.add(refreshBtn, BorderLayout.EAST);
        card.add(head, BorderLayout.NORTH);

        // Table
        tableModel = new DefaultTableModel(
        new String[]{"Purchase ID", "VIN", "Customer", "Price", "Date"}, 0) {
    @Override public boolean isCellEditable(int r, int c) { return false; }
};
        JTable table = new JTable(tableModel);
        styleTable(table);
        card.add(darkScroll(table), BorderLayout.CENTER);

        wrap.add(card);
        return wrap;
    }

    // ── Logic ─────────────────────────────────────────────────────────────────
    private void loadSales() {
    tableModel.setRowCount(0);
    List<String> sales = purchaseService.getDealerSalesHistory(dealerId);
    if (sales.isEmpty()) {
        tableModel.addRow(new Object[]{"—", "—", "—", "—", "—"});
        return;
    }
    for (String s : sales) {
        try {
            String[] parts = s.split("\\|");
            tableModel.addRow(new Object[]{
                parts[0].replace("PurchaseID:", "").trim(),
                parts[2].replace("VIN:", "").trim(),
                parts[1].replace("Customer:", "").trim(),
                parts[3].replace("Price:", "").trim(),
                parts[4].replace("Date:", "").trim()
            });
        } catch (Exception ex) {
            tableModel.addRow(new Object[]{"?", "?", "?", "?", s});
        }
    }
}

    // ── UI helpers ────────────────────────────────────────────────────────────
    private void styleTable(JTable table) {
        table.setBackground(TABLE_ROW);
        table.setForeground(TEXT_PRIMARY);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(32);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(TABLE_SEL);
        table.setSelectionForeground(TEXT_PRIMARY);
        table.setFocusable(false);

        JTableHeader header = table.getTableHeader();
        header.setBackground(TABLE_HEADER);
        header.setForeground(TEXT_SECONDARY);
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, DIVIDER));
        header.setReorderingAllowed(false);

        DefaultTableCellRenderer r = new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(
                    JTable t, Object val, boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, val, sel, foc, row, col);
                setOpaque(true);
                setForeground(TEXT_PRIMARY);
                setBackground(sel ? TABLE_SEL : (row % 2 == 0 ? TABLE_ROW : TABLE_ROW_ALT));
                setBorder(new EmptyBorder(0, 8, 0, 8));
                return this;
            }
        };
        for (int i = 0; i < table.getColumnCount(); i++)
            table.getColumnModel().getColumn(i).setCellRenderer(r);
    }

    private JButton ghostButton(String text) {
        JButton btn = new JButton(text) {
            boolean hovered = false;
            {
                addMouseListener(new MouseAdapter() {
                    @Override public void mouseEntered(MouseEvent e) { hovered = true;  repaint(); }
                    @Override public void mouseExited(MouseEvent e)  { hovered = false; repaint(); }
                });
            }
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                    RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(hovered ? CARD_BG : new Color(0,0,0,0));
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8));
                g2.setColor(hovered ? CARD_BORDER : INPUT_BORDER);
                g2.setStroke(new BasicStroke(1f));
                g2.draw(new RoundRectangle2D.Float(0, 0, getWidth()-1, getHeight()-1, 8, 8));
                g2.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                g2.setColor(hovered ? TEXT_PRIMARY : TEXT_SECONDARY);
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
        btn.setPreferredSize(new Dimension(100, 32));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JScrollPane darkScroll(Component c) {
        JScrollPane sp = new JScrollPane(c);
        sp.setBorder(BorderFactory.createLineBorder(INPUT_BORDER, 1));
        sp.setBackground(INPUT_BG);
        sp.getViewport().setBackground(INPUT_BG);
        styleScrollBar(sp.getVerticalScrollBar());
        return sp;
    }

    private void styleScrollBar(JScrollBar sb) {
        sb.setBackground(INPUT_BG);
        sb.setPreferredSize(new Dimension(6, 6));
        sb.setUI(new javax.swing.plaf.basic.BasicScrollBarUI() {
            @Override protected void configureScrollBarColors() {
                thumbColor = new Color(50, 60, 90);
                trackColor = INPUT_BG;
            }
            @Override protected JButton createDecreaseButton(int o) { return zero(); }
            @Override protected JButton createIncreaseButton(int o) { return zero(); }
            private JButton zero() {
                JButton b = new JButton();
                b.setPreferredSize(new Dimension(0, 0));
                return b;
            }
        });
    }
}