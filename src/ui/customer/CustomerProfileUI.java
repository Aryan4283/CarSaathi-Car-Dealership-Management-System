package ui.customer;

import dao.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.util.List;

public class CustomerProfileUI extends JFrame {

    // ── Palette (matches DealerDashboard) ────────────────────────────────────
    private static final Color BG_DARK        = new Color(8,   10,  18);
    private static final Color PANEL_BG       = new Color(13,  16,  28);
    private static final Color CARD_BG        = new Color(18,  22,  38);
    private static final Color CARD_BORDER    = new Color(35,  42,  68);
    private static final Color ACCENT         = new Color(0,   180, 220);
    private static final Color ACCENT_GLOW    = new Color(0,   180, 220, 35);
    private static final Color SUCCESS        = new Color(0,   200, 130);
    private static final Color DANGER         = new Color(220, 60,  80);
    private static final Color TEXT_PRIMARY   = new Color(225, 228, 245);
    private static final Color TEXT_SECONDARY = new Color(110, 118, 150);
    private static final Color INPUT_BG       = new Color(22,  27,  46);
    private static final Color INPUT_BORDER   = new Color(45,  52,  80);
    private static final Color TABLE_HEADER   = new Color(22,  27,  46);
    private static final Color TABLE_ROW      = new Color(18,  22,  38);
    private static final Color TABLE_ROW_ALT  = new Color(21,  26,  44);
    private static final Color TABLE_SEL      = new Color(0,   180, 220, 60);
    private static final Color DIVIDER        = new Color(30,  36,  58);

    // ── DAOs ──────────────────────────────────────────────────────────────────
    private UserPhoneDao phoneDao    = new UserPhoneDao();
    private CustomerDao  customerDao = new CustomerDao();
    private PurchaseDao  purchaseDao = new PurchaseDao();
    private BookingDao   bookingDao  = new BookingDao();

    private int currentUserId = -1;

    // ── UI components ─────────────────────────────────────────────────────────
    private JTextField        phoneField;
    private JLabel            nameLabel;
    private JLabel            emailLabel;
    private JLabel            addressLabel;
    private DefaultTableModel purchaseModel;
    private DefaultTableModel bookingModel;

    public CustomerProfileUI() {
        setTitle("Customer Profile");
        setSize(780, 620);
        setMinimumSize(new Dimension(680, 520));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        getContentPane().setBackground(BG_DARK);
        getContentPane().setLayout(new BorderLayout());

        getContentPane().add(buildHeader(),  BorderLayout.NORTH);
        getContentPane().add(buildContent(), BorderLayout.CENTER);
    }

    // ────────────────────────────────────────────────────────────────────────
    //  Header
    // ────────────────────────────────────────────────────────────────────────
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

        JLabel title = new JLabel("Customer Profile", JLabel.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(TEXT_PRIMARY);

        // Search bar
        JPanel searchBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        searchBar.setOpaque(false);
        
        JButton editPhoneBtn = styledButton("Edit Phone");
editPhoneBtn.addActionListener(e -> editPhone());
searchBar.add(editPhoneBtn);
        phoneField = new JTextField(14) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                    RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(INPUT_BG);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8));
                g2.dispose();
                super.paintComponent(g);
            }
            @Override protected void paintBorder(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                    RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(hasFocus() ? ACCENT : INPUT_BORDER);
                g2.setStroke(new BasicStroke(hasFocus() ? 1.5f : 1f));
                g2.draw(new RoundRectangle2D.Float(0, 0,
                        getWidth() - 1, getHeight() - 1, 8, 8));
                g2.dispose();
            }
        };
        phoneField.setOpaque(false);
        phoneField.setForeground(TEXT_PRIMARY);
        phoneField.setCaretColor(ACCENT);
        phoneField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        phoneField.setBorder(new EmptyBorder(6, 10, 6, 10));
        phoneField.setPreferredSize(new Dimension(180, 34));
        phoneField.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) { phoneField.repaint(); }
            @Override public void focusLost(FocusEvent e)   { phoneField.repaint(); }
        });
        phoneField.addActionListener(e -> loadCustomer());

        JButton searchBtn = styledButton("Search");
        searchBtn.addActionListener(e -> loadCustomer());

        JLabel hint = new JLabel("Phone:");
        hint.setForeground(TEXT_SECONDARY);
        hint.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        searchBar.add(hint);
        searchBar.add(phoneField);
        searchBar.add(searchBtn);

        h.add(brand,     BorderLayout.WEST);
        h.add(title,     BorderLayout.CENTER);
        h.add(searchBar, BorderLayout.EAST);
        return h;
    }

    // ────────────────────────────────────────────────────────────────────────
    //  Main content
    // ────────────────────────────────────────────────────────────────────────
    private JPanel buildContent() {
        JPanel p = new JPanel(new BorderLayout(0, 14));
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(18, 22, 18, 22));

        p.add(buildProfileCard(),  BorderLayout.NORTH);
        p.add(buildTablesPanel(),  BorderLayout.CENTER);

        return p;
    }

    // ── Profile info card ─────────────────────────────────────────────────────
    private JPanel buildProfileCard() {
        JPanel card = card();
        card.setLayout(new GridLayout(1, 3, 20, 0));
        card.setBorder(new EmptyBorder(18, 22, 18, 22));
        card.setPreferredSize(new Dimension(0, 90));

        nameLabel    = infoBlock("Name",    "—");
        emailLabel   = infoBlock("Email",   "—");
        addressLabel = infoBlock("Address", "—");

        card.add(nameLabel);
        card.add(emailLabel);
        card.add(addressLabel);

        return card;
    }

    private JLabel infoBlock(String labelText, String value) {
        JLabel l = new JLabel("<html><span style='color:#6e7696;font-size:10px;'>"
                + labelText + "</span><br/><b>" + value + "</b></html>");
        l.setForeground(TEXT_PRIMARY);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        l.setOpaque(false);
        return l;
    }

    // ── Purchases + Bookings tables ───────────────────────────────────────────
    private JPanel buildTablesPanel() {
        JPanel p = new JPanel(new GridLayout(1, 2, 14, 0));
        p.setOpaque(false);

        // Purchases
        JPanel purchaseCard = card();
        purchaseCard.setLayout(new BorderLayout(0, 10));
        purchaseCard.setBorder(new EmptyBorder(16, 16, 16, 16));
        purchaseCard.add(sectionLabel("Purchases"), BorderLayout.NORTH);

        purchaseModel = new DefaultTableModel(
                new String[]{"Purchase ID", "Price", "Date"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable purchaseTable = new JTable(purchaseModel);
        styleTable(purchaseTable, -1);
        purchaseCard.add(darkScroll(purchaseTable), BorderLayout.CENTER);

        // Bookings
        JPanel bookingCard = card();
        bookingCard.setLayout(new BorderLayout(0, 10));
        bookingCard.setBorder(new EmptyBorder(16, 16, 16, 16));
        bookingCard.add(sectionLabel("Bookings"), BorderLayout.NORTH);

        bookingModel = new DefaultTableModel(
                new String[]{"Booking ID", "Model", "Status"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable bookingTable = new JTable(bookingModel);
        styleTable(bookingTable, 2);
        bookingCard.add(darkScroll(bookingTable), BorderLayout.CENTER);

        p.add(purchaseCard);
        p.add(bookingCard);
        return p;
    }

    // ────────────────────────────────────────────────────────────────────────
    //  Logic
    // ────────────────────────────────────────────────────────────────────────
    private void loadCustomer() {
        String phone = phoneField.getText().trim();
        if (phone.isEmpty()) return;

        int userId = phoneDao.getUserByPhone(phone);
        if (userId == -1) {
            nameLabel.setText("<html><span style='color:#6e7696;font-size:10px;'>Name</span>"
                    + "<br/><b>Not found</b></html>");
            emailLabel.setText("<html><span style='color:#6e7696;font-size:10px;'>Email</span>"
                    + "<br/><b>—</b></html>");
            addressLabel.setText("<html><span style='color:#6e7696;font-size:10px;'>Address</span>"
                    + "<br/><b>—</b></html>");
            purchaseModel.setRowCount(0);
            bookingModel.setRowCount(0);
            return;
        }

        currentUserId = userId;

        // Profile info
        String details = customerDao.getCustomerDetails(userId);
        parseAndDisplay(details);

        // Purchases
        purchaseModel.setRowCount(0);
        List<String> purchases = purchaseDao.getPurchasesByCustomer(userId);
        for (String p : purchases) {
            String[] parts = p.split("\\|");
            purchaseModel.addRow(new Object[]{
                parts[0].replace("ID: ", "").trim(),
                parts[1].replace("Price: ", "").trim(),
                parts[2].replace("Date: ", "").trim()
            });
        }

        // Bookings
        bookingModel.setRowCount(0);
        List<String> bookings = bookingDao.getBookingsByCustomer(userId);
        for (String b : bookings) {
            String[] parts = b.split("\\|");
            bookingModel.addRow(new Object[]{
                parts[0].replace("BookingID: ", "").trim(),
                parts[1].replace("Model: ", "").trim(),
                parts[2].replace("Status: ", "").trim()
            });
        }
    }

    private void parseAndDisplay(String details) {
        if (details.equals("Customer not found")) {
            nameLabel.setText("<html><span style='color:#6e7696;font-size:10px;'>Name</span>"
                    + "<br/><b>Not found</b></html>");
            return;
        }
        String[] lines = details.split("\n");
        String name    = lines.length > 0 ? lines[0].replace("Name: ", "")    : "—";
        String email   = lines.length > 1 ? lines[1].replace("Email: ", "")   : "—";
        String address = lines.length > 2 ? lines[2].replace("Address: ", "") : "—";

        nameLabel.setText("<html><span style='color:#6e7696;font-size:10px;'>Name</span>"
                + "<br/><b>" + name + "</b></html>");
        emailLabel.setText("<html><span style='color:#6e7696;font-size:10px;'>Email</span>"
                + "<br/><b>" + email + "</b></html>");
        addressLabel.setText("<html><span style='color:#6e7696;font-size:10px;'>Address</span>"
                + "<br/><b>" + address + "</b></html>");
    }

    // ────────────────────────────────────────────────────────────────────────
    //  UI helpers
    // ────────────────────────────────────────────────────────────────────────
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

    private JLabel sectionLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.BOLD, 13));
        l.setForeground(TEXT_PRIMARY);
        l.setOpaque(false);
        return l;
    }

    private JButton styledButton(String text) {
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
                g2.setColor(hovered ? ACCENT.brighter() : ACCENT);
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
        btn.setPreferredSize(new Dimension(90, 34));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void styleTable(JTable table, int statusCol) {
        table.setBackground(TABLE_ROW);
        table.setForeground(TEXT_PRIMARY);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(30);
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

        DefaultTableCellRenderer rowRenderer = new DefaultTableCellRenderer() {
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
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(rowRenderer);
        }

        if (statusCol >= 0 && statusCol < table.getColumnCount()) {
            table.getColumnModel().getColumn(statusCol).setCellRenderer(
                    new DefaultTableCellRenderer() {
                @Override public Component getTableCellRendererComponent(
                        JTable t, Object val, boolean sel, boolean foc, int r, int c) {
                    super.getTableCellRendererComponent(t, val, sel, foc, r, c);
                    setHorizontalAlignment(CENTER);
                    setOpaque(true);
                    String s = val == null ? "" : val.toString();
                    if (s.equals("Available")) {
                        setForeground(SUCCESS);
                        setBackground(new Color(0, 200, 130, 25));
                    } else {
                        setForeground(DANGER);
                        setBackground(new Color(220, 60, 80, 25));
                    }
                    setBorder(new EmptyBorder(0, 8, 0, 8));
                    return this;
                }
            });
        }
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

    private void editPhone() {
    if (currentUserId == -1) {
        JOptionPane.showMessageDialog(this,
            "Search a customer first.", "No Customer",
            JOptionPane.WARNING_MESSAGE);
        return;
    }

    List<String> phones = phoneDao.getPhonesByUserId(currentUserId);
    if (phones.isEmpty()) {
        JOptionPane.showMessageDialog(this,
            "No phone numbers found for this customer.");
        return;
    }

    // Let dealer pick which phone to edit
    String[] phoneArr = phones.toArray(new String[0]);
    String selected = (String) JOptionPane.showInputDialog(
        this,
        "Select phone number to edit:",
        "Edit Phone",
        JOptionPane.PLAIN_MESSAGE,
        null, phoneArr, phoneArr[0]
    );
    if (selected == null) return;

    String newPhone = JOptionPane.showInputDialog(
        this, "Enter new phone number:", selected);
    if (newPhone == null || newPhone.trim().isEmpty()) return;

    boolean ok = phoneDao.updatePhone(currentUserId, selected, newPhone.trim());
    JOptionPane.showMessageDialog(this,
        ok ? "Phone updated successfully!" : "Update failed.",
        ok ? "Success" : "Error",
        ok ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);
}
}