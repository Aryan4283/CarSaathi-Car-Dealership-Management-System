package ui.dealer;

import service.AnalyticsService;
import db.DatabaseConnection;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.sql.*;
import java.util.Calendar;

public class DealerAnalyticsUI extends JFrame {

    private static final Color BG_DARK        = new Color(8,   10,  18);
    private static final Color PANEL_BG       = new Color(13,  16,  28);
    private static final Color CARD_BG        = new Color(18,  22,  38);
    private static final Color CARD_BORDER    = new Color(35,  42,  68);
    private static final Color ACCENT         = new Color(0,   180, 220);
    private static final Color SUCCESS        = new Color(0,   200, 130);
    private static final Color WARNING        = new Color(230, 160, 0);
    private static final Color DANGER         = new Color(220, 60,  80);
    private static final Color PURPLE         = new Color(150, 100, 255);
    private static final Color TEXT_PRIMARY   = new Color(225, 228, 245);
    private static final Color TEXT_SECONDARY = new Color(110, 118, 150);
    private static final Color INPUT_BG       = new Color(22,  27,  46);
    private static final Color INPUT_BORDER   = new Color(45,  52,  80);
    private static final Color TABLE_HEADER   = new Color(22,  27,  46);
    private static final Color TABLE_ROW      = new Color(18,  22,  38);
    private static final Color TABLE_ROW_ALT  = new Color(21,  26,  44);
    private static final Color TABLE_SEL      = new Color(0,   180, 220, 60);
    private static final Color DIVIDER        = new Color(30,  36,  58);

    private AnalyticsService   analyticsService = new AnalyticsService();
    private int                dealerId;
    private int                currentYear = Calendar.getInstance().get(Calendar.YEAR);

    private JComboBox<Integer> yearBox;
    private JComboBox<String>  monthBox;
    private JLabel             yearlyRevenueLabel;
    private JLabel             monthlyRevenueLabel;
    private JLabel             carsSoldLabel;
    private JLabel             topModelLabel;
    private DefaultTableModel  monthlyModel;
    private DefaultTableModel  stockModel;

    public DealerAnalyticsUI(int dealerId) {
        this.dealerId = dealerId;

        setTitle("Dealer Analytics");
        setSize(820, 620);
        setMinimumSize(new Dimension(720, 520));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        getContentPane().setBackground(BG_DARK);
        getContentPane().setLayout(new BorderLayout());

        getContentPane().add(buildHeader(),  BorderLayout.NORTH);
        getContentPane().add(buildContent(), BorderLayout.CENTER);

        refreshData(currentYear, 0);
    }

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

        JLabel title = new JLabel("My Analytics", JLabel.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(TEXT_PRIMARY);

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        filterPanel.setOpaque(false);

        JLabel yearLbl = new JLabel("Year:");
        yearLbl.setForeground(TEXT_SECONDARY);
        yearLbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        yearBox = new JComboBox<>();
        for (int y = currentYear; y >= currentYear - 4; y--)
            yearBox.addItem(y);
        styleComboBox(yearBox);
        yearBox.addActionListener(e -> refreshData(
            (Integer) yearBox.getSelectedItem(),
            monthBox.getSelectedIndex()));

        JLabel monthLbl = new JLabel("Month:");
        monthLbl.setForeground(TEXT_SECONDARY);
        monthLbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        monthBox = new JComboBox<>(new String[]{
            "All","Jan","Feb","Mar","Apr","May","Jun",
            "Jul","Aug","Sep","Oct","Nov","Dec"
        });
        styleComboBox(monthBox);
        monthBox.addActionListener(e -> refreshData(
            (Integer) yearBox.getSelectedItem(),
            monthBox.getSelectedIndex()));

        filterPanel.add(yearLbl);
        filterPanel.add(yearBox);
        filterPanel.add(monthLbl);
        filterPanel.add(monthBox);

        h.add(brand,       BorderLayout.WEST);
        h.add(title,       BorderLayout.CENTER);
        h.add(filterPanel, BorderLayout.EAST);
        return h;
    }

    private JPanel buildContent() {
        JPanel p = new JPanel(new BorderLayout(0, 14));
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(18, 22, 18, 22));

        JPanel topRow = new JPanel(new GridLayout(1, 4, 14, 0));
        topRow.setOpaque(false);

        yearlyRevenueLabel  = new JLabel("₹0", JLabel.CENTER);
        monthlyRevenueLabel = new JLabel("—",  JLabel.CENTER);
        carsSoldLabel       = new JLabel("0",  JLabel.CENTER);
        topModelLabel       = new JLabel("—",  JLabel.CENTER);

        topRow.add(dynamicStatCard("Revenue",    yearlyRevenueLabel,  ACCENT));
        topRow.add(dynamicStatCard("This Month", monthlyRevenueLabel, SUCCESS));
        topRow.add(dynamicStatCard("Cars Sold",  carsSoldLabel,       WARNING));
        topRow.add(dynamicStatCard("Top Model",  topModelLabel,       PURPLE));

        p.add(topRow, BorderLayout.NORTH);

        JPanel bottom = new JPanel(new GridLayout(1, 2, 14, 0));
        bottom.setOpaque(false);
        bottom.add(buildMonthlyTrendCard());
        bottom.add(buildStockCard());

        p.add(bottom, BorderLayout.CENTER);
        return p;
    }

    private void refreshData(int year, int monthIndex) {
        boolean allMonths = monthIndex == 0;
        int month = allMonths
                ? Calendar.getInstance().get(Calendar.MONTH) + 1
                : monthIndex;

        // Revenue card
        if (allMonths) {
            double yearly = analyticsService.getYearlyTotal(dealerId, year);
            yearlyRevenueLabel.setText(yearly == 0
                    ? "No Sales" : "₹" + String.format("%,.0f", yearly));
        } else {
            double mon = analyticsService.getMonthlyRevenue(dealerId, month, year);
            yearlyRevenueLabel.setText(mon == 0
                    ? "No Sales" : "₹" + String.format("%,.0f", mon));
        }

        // This Month card — always current month of selected year
        double mon = analyticsService.getMonthlyRevenue(dealerId,
                Calendar.getInstance().get(Calendar.MONTH) + 1, year);
        monthlyRevenueLabel.setText(mon == 0
                ? "No Sales" : "₹" + String.format("%,.0f", mon));

        // Cars sold
        String soldSql = "SELECT COUNT(*) FROM PURCHASE p " +
            "JOIN CARINSTANCE ci ON p.CarID = ci.CarID " +
            "WHERE ci.DealerID = ? AND YEAR(p.PurchaseDate) = ?" +
            (allMonths ? "" : " AND MONTH(p.PurchaseDate) = ?");
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(soldSql)) {
            stmt.setInt(1, dealerId);
            stmt.setInt(2, year);
            if (!allMonths) stmt.setInt(3, month);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) carsSoldLabel.setText(String.valueOf(rs.getInt(1)));
        } catch (Exception e) { e.printStackTrace(); }

        // Top model
        String topSql = "SELECT cm.ModelName, COUNT(*) AS cnt " +
            "FROM PURCHASE p " +
            "JOIN CARINSTANCE ci ON p.CarID = ci.CarID " +
            "JOIN CARMODEL cm ON ci.ModelID = cm.ModelID " +
            "WHERE ci.DealerID = ? AND YEAR(p.PurchaseDate) = ?" +
            (allMonths ? "" : " AND MONTH(p.PurchaseDate) = ?") +
            " GROUP BY cm.ModelName ORDER BY cnt DESC LIMIT 1";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(topSql)) {
            stmt.setInt(1, dealerId);
            stmt.setInt(2, year);
            if (!allMonths) stmt.setInt(3, month);
            ResultSet rs = stmt.executeQuery();
            if (rs.next())
                topModelLabel.setText(rs.getString("ModelName") +
                        " (" + rs.getInt("cnt") + " sold)");
            else
                topModelLabel.setText("No Sales");
        } catch (Exception e) { e.printStackTrace(); }

        // Monthly trend — always full year
        if (monthlyModel != null) {
            monthlyModel.setRowCount(0);
            Object[][] data = analyticsService.getMonthlySalesTable(dealerId, year);
            for (Object[] row : data) monthlyModel.addRow(row);
        }
    }

    private JPanel buildMonthlyTrendCard() {
        JPanel card = card();
        card.setLayout(new BorderLayout(0, 10));
        card.setBorder(new EmptyBorder(16, 16, 16, 16));

        JLabel lbl = new JLabel("Monthly Trend");
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lbl.setForeground(TEXT_PRIMARY);
        card.add(lbl, BorderLayout.NORTH);

        monthlyModel = new DefaultTableModel(
                new String[]{"Month", "Revenue"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        JTable table = new JTable(monthlyModel);
        styleTable(table, -1);
        card.add(darkScroll(table), BorderLayout.CENTER);
        return card;
    }

    private JPanel buildStockCard() {
        JPanel card = card();
        card.setLayout(new BorderLayout(0, 10));
        card.setBorder(new EmptyBorder(16, 16, 16, 16));

        JLabel lbl = new JLabel("Stock Overview");
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lbl.setForeground(TEXT_PRIMARY);
        card.add(lbl, BorderLayout.NORTH);

        stockModel = new DefaultTableModel(
                new String[]{"Model Name", "Available"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT ModelID, ModelName FROM CARMODEL")) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int    modelId   = rs.getInt("ModelID");
                String modelName = rs.getString("ModelName");
                int    stock     = analyticsService.getAvailableStock(modelId, dealerId);
                stockModel.addRow(new Object[]{modelName, stock});
            }
        } catch (Exception e) { e.printStackTrace(); }

        JTable table = new JTable(stockModel);
        styleTable(table, -1);

        table.getColumnModel().getColumn(1).setCellRenderer(new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(
                    JTable t, Object val, boolean sel, boolean foc, int r, int c) {
                super.getTableCellRendererComponent(t, val, sel, foc, r, c);
                setHorizontalAlignment(CENTER);
                setOpaque(true);
                int stock = val == null ? 0 : (int) val;
                if      (stock == 0) { setForeground(DANGER);  setBackground(new Color(220, 60, 80, 25)); }
                else if (stock == 1) { setForeground(WARNING); setBackground(new Color(230, 160, 0, 25)); }
                else                 { setForeground(SUCCESS); setBackground(new Color(0, 200, 130, 25)); }
                setBorder(new EmptyBorder(0, 8, 0, 8));
                return this;
            }
        });

        card.add(darkScroll(table), BorderLayout.CENTER);
        return card;
    }

    private JPanel dynamicStatCard(String label, JLabel valueLabel, Color accent) {
        JPanel card = new JPanel(new GridBagLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                    RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(CARD_BG);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 14, 14));
                g2.setColor(accent);
                g2.setStroke(new BasicStroke(2f));
                g2.drawLine(20, 1, getWidth() - 20, 1);
                g2.setColor(CARD_BORDER);
                g2.setStroke(new BasicStroke(1f));
                g2.draw(new RoundRectangle2D.Float(0.5f, 0.5f,
                        getWidth()-1, getHeight()-1, 14, 14));
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setPreferredSize(new Dimension(0, 90));

        GridBagConstraints g = new GridBagConstraints();
        g.gridx = 0; g.gridy = 0; g.insets = new Insets(0, 8, 6, 8);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        valueLabel.setForeground(accent);
        valueLabel.setHorizontalAlignment(JLabel.CENTER);
        card.add(valueLabel, g);

        g.gridy = 1; g.insets = new Insets(0, 8, 0, 8);
        JLabel lbl = new JLabel(label, JLabel.CENTER);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lbl.setForeground(TEXT_SECONDARY);
        card.add(lbl, g);

        return card;
    }

    private JPanel card() {
        return new JPanel() {
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
    }

    private void styleTable(JTable table, int colorCol) {
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
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, DIVIDER));
        header.setReorderingAllowed(false);

        DefaultTableCellRenderer def = new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(
                    JTable t, Object val, boolean sel, boolean foc, int r, int c) {
                super.getTableCellRendererComponent(t, val, sel, foc, r, c);
                setOpaque(true);
                setForeground(TEXT_PRIMARY);
                setBackground(sel ? TABLE_SEL : (r % 2 == 0 ? TABLE_ROW : TABLE_ROW_ALT));
                setBorder(new EmptyBorder(0, 8, 0, 8));
                return this;
            }
        };
        for (int i = 0; i < table.getColumnCount(); i++)
            table.getColumnModel().getColumn(i).setCellRenderer(def);
    }

    private void styleComboBox(JComboBox<?> box) {
        box.setBackground(INPUT_BG);
        box.setForeground(TEXT_PRIMARY);
        box.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        box.setBorder(BorderFactory.createLineBorder(INPUT_BORDER, 1));
        box.setFocusable(false);
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