package ui.dealer;

import model.CarInstance;
import model.CarModel;
import service.InventoryService;
import service.PurchaseService;
import ui.customer.CustomerProfileUI;
import service.BookingService;
import service.CarService;
import dao.UserPhoneDao;
import exception.InvalidPurchaseException;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.util.List;

public class DealerDashboard extends JFrame {

    // ── Palette ───────────────────────────────────────────────────────────────
    private static final Color BG_DARK = new Color(8, 10, 18);
    private static final Color PANEL_BG = new Color(13, 16, 28);
    private static final Color CARD_BG = new Color(18, 22, 38);
    private static final Color CARD_BORDER = new Color(35, 42, 68);
    private static final Color ACCENT = new Color(0, 180, 220);
    private static final Color ACCENT_GLOW = new Color(0, 180, 220, 35);
    private static final Color SUCCESS = new Color(0, 200, 130);
    private static final Color DANGER = new Color(220, 60, 80);
    private static final Color TEXT_PRIMARY = new Color(225, 228, 245);
    private static final Color TEXT_SECONDARY = new Color(110, 118, 150);
    private static final Color INPUT_BG = new Color(22, 27, 46);
    private static final Color INPUT_BORDER = new Color(45, 52, 80);
    private static final Color TABLE_HEADER = new Color(22, 27, 46);
    private static final Color TABLE_ROW = new Color(18, 22, 38);
    private static final Color TABLE_ROW_ALT = new Color(21, 26, 44);
    private static final Color TABLE_SEL = new Color(0, 180, 220, 60);
    private static final Color DIVIDER = new Color(30, 36, 58);

    // ── State ─────────────────────────────────────────────────────────────────
    private int dealerId;
    private String dealerName;

    // ── Services ──────────────────────────────────────────────────────────────
    private InventoryService inventoryService = new InventoryService();
    private PurchaseService purchaseService = new PurchaseService();
    private BookingService bookingService = new BookingService();
    private UserPhoneDao phoneDao = new UserPhoneDao();

    // ── Booking table ──────────────────────────────────────────────────────────
    private DefaultTableModel tableModel;
    private JTable bookingTable;
    private JComboBox<String> filterBox;

    // ── Inventory table ────────────────────────────────────────────────────────
    private DefaultTableModel inventoryModel;
    private JTable inventoryTable;
    private JComboBox<String> inventoryFilter;

    // ── Models table ───────────────────────────────────────────────────────────
    private DefaultTableModel modelsTableModel;
    private JTable modelsTable;

    // ── Right column fields ────────────────────────────────────────────────────
    private JTextField finalPriceField;
    private JTextField vinField;
    private JTextField modelIdField;

    // ─────────────────────────────────────────────────────────────────────────
    public DealerDashboard(int dealerId, String dealerName) {
        this.dealerId = dealerId;
        this.dealerName = dealerName;

        setTitle("Dealer Dashboard — " + dealerName);
        setSize(1100, 720);
        setMinimumSize(new Dimension(950, 620));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(BG_DARK);
        getContentPane().setLayout(new BorderLayout());

        getContentPane().add(buildHeader(), BorderLayout.NORTH);
        getContentPane().add(buildCenter(), BorderLayout.CENTER);
        getContentPane().add(buildBottomBar(), BorderLayout.SOUTH);

        loadBookings();
        loadInventory();

        // Auto-fill price when booking row selected
        bookingTable.getSelectionModel().addListSelectionListener(e -> {
            int row = bookingTable.getSelectedRow();
            if (row != -1) {
                try {
                    int modelId = (int) tableModel.getValueAt(row, 3);
                    CarService carService = new CarService();
                    CarModel model = carService.getCarModelById(modelId);
                    finalPriceField.setText(String.valueOf(model.getBasePrice()));
                } catch (Exception ex) {
                    // ignore
                }
            }
        });
    }

    // ────────────────────────────────────────────────────────────────────────
    // Header
    // ────────────────────────────────────────────────────────────────────────
    private JPanel buildHeader() {
        JPanel h = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
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
        h.setBorder(new EmptyBorder(14, 28, 14, 28));

        JLabel brand = new JLabel("CarSaathi");
        brand.setFont(new Font("Segoe UI", Font.BOLD, 18));
        brand.setForeground(ACCENT);

        JLabel title = new JLabel("Dealer Dashboard", JLabel.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(TEXT_PRIMARY);

        JLabel chip = new JLabel("  " + dealerName + "  ") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(ACCENT_GLOW);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 20, 20));
                g2.setColor(new Color(0, 180, 220, 80));
                g2.setStroke(new BasicStroke(1f));
                g2.draw(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, 20, 20));
                super.paintComponent(g);
                g2.dispose();
            }
        };
        chip.setOpaque(false);
        chip.setForeground(ACCENT);
        chip.setFont(new Font("Segoe UI", Font.BOLD, 13));
        chip.setBorder(new EmptyBorder(5, 12, 5, 12));

        h.add(brand, BorderLayout.WEST);
        h.add(title, BorderLayout.CENTER);
        h.add(chip, BorderLayout.EAST);
        return h;
    }

    // ────────────────────────────────────────────────────────────────────────
    // Center
    // ────────────────────────────────────────────────────────────────────────
    private JPanel buildCenter() {
        JPanel center = new JPanel(new BorderLayout(14, 0));
        center.setOpaque(false);
        center.setBorder(new EmptyBorder(18, 22, 10, 22));
        center.add(buildMainArea(), BorderLayout.CENTER);
        center.add(buildRightColumn(), BorderLayout.EAST);
        return center;
    }

    private JPanel buildMainArea() {
        JPanel p = new JPanel(new GridLayout(2, 1, 0, 14));
        p.setOpaque(false);
        p.add(buildBookingCard());
        p.add(buildInventoryCard());
        return p;
    }

    // ── Booking card ──────────────────────────────────────────────────────────
    private JPanel buildBookingCard() {
        JPanel card = card();
        card.setLayout(new BorderLayout(0, 10));
        card.setBorder(new EmptyBorder(16, 18, 16, 18));

        JPanel head = new JPanel(new BorderLayout(10, 0));
        head.setOpaque(false);
        head.add(sectionLabel("Bookings"), BorderLayout.WEST);

        filterBox = new JComboBox<>(new String[] { "All", "Available", "Unavailable" });
        styleComboBox(filterBox);
        filterBox.addActionListener(e -> applyFilter());

        JPanel filterWrap = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        filterWrap.setOpaque(false);
        JLabel filterLbl = new JLabel("Filter: ");
        filterLbl.setForeground(TEXT_SECONDARY);
        filterLbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        filterWrap.add(filterLbl);
        filterWrap.add(filterBox);
        head.add(filterWrap, BorderLayout.EAST);
        card.add(head, BorderLayout.NORTH);

        String[] cols = { "Booking ID", "Name", "Cust ID", "Model ID", "Model Name", "Status", "Phone" };
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        bookingTable = new JTable(tableModel);
        styleTable(bookingTable, 5);

        card.add(darkScroll(bookingTable), BorderLayout.CENTER);
        return card;
    }

    // ── Inventory card (Cars / Car Models toggle) ──────────────────────────────
    private JPanel buildInventoryCard() {
        JPanel card = card();
        card.setLayout(new BorderLayout(0, 10));
        card.setBorder(new EmptyBorder(16, 18, 16, 18));

        JPanel head = new JPanel(new BorderLayout(10, 0));
        head.setOpaque(false);
        head.add(sectionLabel("Inventory"), BorderLayout.WEST);

        JPanel rightHead = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        rightHead.setOpaque(false);

        JButton carsBtn = ghostButton("Cars");
        JButton modelsBtn = ghostButton("Car Models");
        carsBtn.setPreferredSize(new Dimension(90, 28));
        modelsBtn.setPreferredSize(new Dimension(110, 28));

        inventoryFilter = new JComboBox<>(new String[] { "All", "Available", "Unavailable" });
        styleComboBox(inventoryFilter);
        inventoryFilter.setVisible(false);

        JLabel filterLbl = new JLabel("  Filter: ");
        filterLbl.setForeground(TEXT_SECONDARY);
        filterLbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        filterLbl.setVisible(false);

        rightHead.add(carsBtn);
        rightHead.add(modelsBtn);
        rightHead.add(filterLbl);
        rightHead.add(inventoryFilter);
        head.add(rightHead, BorderLayout.EAST);
        card.add(head, BorderLayout.NORTH);

        // CardLayout switcher
        JPanel switcher = new JPanel(new CardLayout());
        switcher.setOpaque(false);

        // Cars view
        inventoryModel = new DefaultTableModel(
                new String[] { "Car ID", "VIN", "Model ID", "Model Name" }, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        inventoryTable = new JTable(inventoryModel);
        styleTable(inventoryTable, -1);
        switcher.add(darkScroll(inventoryTable), "CARS");

        // Models view
        modelsTableModel = new DefaultTableModel(
                new String[] { "Model ID", "Model Name", "Total", "Available", "Status" }, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        modelsTable = new JTable(modelsTableModel);
        styleTable(modelsTable, 4);
        switcher.add(darkScroll(modelsTable), "MODELS");

        card.add(switcher, BorderLayout.CENTER);

        carsBtn.addActionListener(e -> {
            ((CardLayout) switcher.getLayout()).show(switcher, "CARS");
            inventoryFilter.setVisible(false);
            filterLbl.setVisible(false);
            loadInventory();
        });

        modelsBtn.addActionListener(e -> {
            ((CardLayout) switcher.getLayout()).show(switcher, "MODELS");
            inventoryFilter.setVisible(true);
            filterLbl.setVisible(true);
            loadModels((String) inventoryFilter.getSelectedItem());
        });

        inventoryFilter.addActionListener(e -> loadModels((String) inventoryFilter.getSelectedItem()));

        return card;
    }

    // ── Right column ──────────────────────────────────────────────────────────
    private JScrollPane buildRightColumn() {
        JPanel col = new JPanel();
        col.setOpaque(false);
        col.setLayout(new BoxLayout(col, BoxLayout.Y_AXIS));
        col.setPreferredSize(new Dimension(280, 0));

        col.add(buildPurchaseForm());
        col.add(Box.createVerticalStrut(12));
        col.add(buildAddCarForm());

        JScrollPane sp = new JScrollPane(col);
        sp.setPreferredSize(new Dimension(296, 0));
        sp.setBorder(null);
        sp.setOpaque(false);
        sp.getViewport().setOpaque(false);
        styleScrollBar(sp.getVerticalScrollBar());
        return sp;
    }

    private JPanel buildPurchaseForm() {
        JPanel card = card();
        card.setLayout(new GridBagLayout());
        card.setBorder(new EmptyBorder(14, 16, 14, 16));

        GridBagConstraints g = new GridBagConstraints();
        g.gridx = 0;
        g.fill = GridBagConstraints.HORIZONTAL;
        g.weightx = 1.0;

        g.gridy = 0;
        g.insets = new Insets(0, 0, 10, 0);
        card.add(sectionLabel("Process Sale"), g);

        g.gridy = 1;
        g.insets = new Insets(0, 0, 3, 0);
        card.add(fieldLabel("Final Price"), g);
        g.gridy = 2;
        g.insets = new Insets(0, 0, 8, 0);
        finalPriceField = miniField();
        card.add(finalPriceField, g);

        g.gridy = 3;
        g.insets = new Insets(0, 0, 10, 0);
        JLabel hint = new JLabel("Select a row in the table first");
        hint.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        hint.setForeground(TEXT_SECONDARY);
        card.add(hint, g);

        g.gridy = 4;
        g.insets = new Insets(0, 0, 0, 0);
        JButton btn = styledButton("Process Sale", SUCCESS);
        btn.addActionListener(e -> processFromTable());
        card.add(btn, g);

        return card;
    }

    private JPanel buildAddCarForm() {
        JPanel card = card();
        card.setLayout(new GridBagLayout());
        card.setBorder(new EmptyBorder(14, 16, 14, 16));

        GridBagConstraints g = new GridBagConstraints();
        g.gridx = 0;
        g.fill = GridBagConstraints.HORIZONTAL;
        g.weightx = 1.0;

        g.gridy = 0;
        g.insets = new Insets(0, 0, 10, 0);
        card.add(sectionLabel("Add Car to Inventory"), g);

        g.gridy = 1;
        g.insets = new Insets(0, 0, 3, 0);
        card.add(fieldLabel("VIN"), g);
        g.gridy = 2;
        g.insets = new Insets(0, 0, 8, 0);
        vinField = miniField();
        card.add(vinField, g);

        g.gridy = 3;
        g.insets = new Insets(0, 0, 3, 0);
        card.add(fieldLabel("Model ID"), g);
        g.gridy = 4;
        g.insets = new Insets(0, 0, 12, 0);
        modelIdField = miniField();
        card.add(modelIdField, g);

        g.gridy = 5;
        g.insets = new Insets(0, 0, 0, 0);
        JButton btn = styledButton("Add Car", ACCENT);
        btn.addActionListener(e -> addCar());
        card.add(btn, g);

        return card;
    }

    // ── Bottom bar ────────────────────────────────────────────────────────────
    private JPanel buildBottomBar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(PANEL_BG);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(DIVIDER);
                g2.fillRect(0, 0, getWidth(), 1);
                g2.dispose();
            }
        };
        bar.setOpaque(false);

        JButton bookBtn = ghostButton("Book Car");
        JButton historyBtn = ghostButton("View Sales");
        JButton registerBtn = ghostButton("Register Customer");
        JButton profileBtn = ghostButton("View Customer");
        profileBtn.addActionListener(e -> new CustomerProfileUI().setVisible(true));
        bar.add(profileBtn);
        bookBtn.addActionListener(e -> bookCar());
        historyBtn.addActionListener(e -> new ViewSalesUI(dealerId).setVisible(true));
        registerBtn.addActionListener(e -> new ui.customer.CustomerDashboard().setVisible(true));
        JButton analyticsBtn = ghostButton("Analytics");
        analyticsBtn.addActionListener(e -> new DealerAnalyticsUI(dealerId).setVisible(true));
        bar.add(analyticsBtn);

        bar.add(bookBtn);
        bar.add(historyBtn);
        bar.add(registerBtn);
        return bar;
    }

    // ────────────────────────────────────────────────────────────────────────
    // Logic
    // ────────────────────────────────────────────────────────────────────────
    private void loadInventory() {
        inventoryModel.setRowCount(0);
        List<Object[]> cars = inventoryService.getAvailableCarsByDealer(dealerId);
        for (Object[] car : cars) {
            inventoryModel.addRow(new Object[] {
                    car[0], car[1], car[2], car[3]
            });
        }
    }

    private void loadModels(String filter) {
        modelsTableModel.setRowCount(0);
        List<Object[]> data = inventoryService.getModelInventory(dealerId);
        for (Object[] row : data) {
            String status = (String) row[4];
            if (filter.equals("Available") && !status.equals("Available"))
                continue;
            if (filter.equals("Unavailable") && !status.equals("Unavailable"))
                continue;
            modelsTableModel.addRow(row);
        }
    }

    private void loadBookings() {
        tableModel.setRowCount(0);
        for (Object[] row : bookingService.getBookings(dealerId)) {
            tableModel.addRow(row);
        }
    }

    private void applyFilter() {
        String selected = (String) filterBox.getSelectedItem();
        tableModel.setRowCount(0);
        for (Object[] row : bookingService.getBookings(dealerId)) {
            if (selected.equals("All") || row[5].equals(selected)) {
                tableModel.addRow(row);
            }
        }
    }

    private void bookCar() {
        try {
            String phone = JOptionPane.showInputDialog(this, "Enter Customer Phone:");
            if (phone == null || phone.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Phone required");
                return;
            }

            int customerId = phoneDao.getUserByPhone(phone);
            if (customerId == -1) {
                JOptionPane.showMessageDialog(this, "Customer not found");
                return;
            }

            String modelStr = JOptionPane.showInputDialog(this, "Enter Model ID:");
            int modelId = Integer.parseInt(modelStr);

            boolean available = inventoryService.getAvailableCarsByDealer(dealerId)
                    .stream().anyMatch(car -> (int) car[2] == modelId);

            boolean success = bookingService.createBooking(customerId, modelId, dealerId, available);

            if (success) {
                JOptionPane.showMessageDialog(this, "Booking Added");
                loadBookings();
            } else {
                JOptionPane.showMessageDialog(this, "Booking Failed");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Invalid Input");
        }
    }

    private void processFromTable() {
        int row = bookingTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a booking row first.", "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        String status = (String) tableModel.getValueAt(row, 5);
        if (!status.equalsIgnoreCase("Available")) {
            JOptionPane.showMessageDialog(this,
                    "Cannot process sale. Booking is Unavailable.",
                    "Blocked", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            int bookingId = (int) tableModel.getValueAt(row, 0);
            int customerId = (int) tableModel.getValueAt(row, 2);
            int modelId = (int) tableModel.getValueAt(row, 3);
            double price = Double.parseDouble(finalPriceField.getText());

            int carId = inventoryService.getAvailableCarId(modelId, dealerId);

            if (carId == -1) {
                JOptionPane.showMessageDialog(this, "No available car for this model.");
                return;
            }

            boolean ok = purchaseService.processPurchase(customerId, carId, price);
            if (ok) {
                bookingService.removeBooking(bookingId);
                loadBookings();
                loadInventory();
                loadModels("All");
                JOptionPane.showMessageDialog(this, "Purchase successful!");
            } else {
                JOptionPane.showMessageDialog(this, "Purchase failed.", "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        } catch (InvalidPurchaseException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Something went wrong", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void addCar() {
        try {
            String vin = vinField.getText();
            int modelId = Integer.parseInt(modelIdField.getText());
            boolean success = inventoryService.addCarToInventory(vin, modelId, dealerId);
            if (success) {
                bookingService.updateBookingStatus(modelId, dealerId);
                JOptionPane.showMessageDialog(this, "Car Added");
                loadInventory();
                loadBookings();
            } else {
                JOptionPane.showMessageDialog(this, "Failed");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Invalid Input");
        }
    }

    // ────────────────────────────────────────────────────────────────────────
    // Table styling
    // ────────────────────────────────────────────────────────────────────────
    private void styleTable(JTable table, int statusCol) {
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

        DefaultTableCellRenderer rowRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
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
                        @Override
                        public Component getTableCellRendererComponent(
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

    // ────────────────────────────────────────────────────────────────────────
    // UI helpers
    // ────────────────────────────────────────────────────────────────────────
    private JPanel card() {
        return new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(CARD_BG);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 14, 14));
                g2.setColor(CARD_BORDER);
                g2.setStroke(new BasicStroke(1f));
                g2.draw(new RoundRectangle2D.Float(0.5f, 0.5f, getWidth() - 1, getHeight() - 1, 14, 14));
                g2.dispose();
            }
        };
    }

    private JLabel sectionLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.BOLD, 14));
        l.setForeground(TEXT_PRIMARY);
        l.setOpaque(false);
        return l;
    }

    private JLabel fieldLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        l.setForeground(TEXT_SECONDARY);
        l.setOpaque(false);
        return l;
    }

    private JTextField miniField() {
        JTextField f = new JTextField() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(INPUT_BG);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8));
                g2.dispose();
                super.paintComponent(g);
            }

            @Override
            protected void paintBorder(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(hasFocus() ? ACCENT : INPUT_BORDER);
                g2.setStroke(new BasicStroke(hasFocus() ? 1.5f : 1f));
                g2.draw(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, 8, 8));
                g2.dispose();
            }
        };
        f.setOpaque(false);
        f.setForeground(TEXT_PRIMARY);
        f.setCaretColor(ACCENT);
        f.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        f.setBorder(new EmptyBorder(7, 10, 7, 10));
        f.setPreferredSize(new Dimension(240, 34));
        f.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                f.repaint();
            }

            @Override
            public void focusLost(FocusEvent e) {
                f.repaint();
            }
        });
        return f;
    }

    private JButton styledButton(String text, Color color) {
        JButton btn = new JButton(text) {
            boolean hovered = false, pressed = false;
            {
                addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        hovered = true;
                        repaint();
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                        hovered = false;
                        repaint();
                    }

                    @Override
                    public void mousePressed(MouseEvent e) {
                        pressed = true;
                        repaint();
                    }

                    @Override
                    public void mouseReleased(MouseEvent e) {
                        pressed = false;
                        repaint();
                    }
                });
            }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (hovered) {
                    g2.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 50));
                    g2.fill(new RoundRectangle2D.Float(-3, -3, getWidth() + 6, getHeight() + 6, 14, 14));
                }
                g2.setColor(pressed ? color.darker() : hovered ? color.brighter() : color);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8));
                g2.setFont(new Font("Segoe UI", Font.BOLD, 13));
                g2.setColor(Color.WHITE);
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(getText(),
                        (getWidth() - fm.stringWidth(getText())) / 2,
                        (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
                g2.dispose();
            }

            @Override
            protected void paintBorder(Graphics g) {
            }
        };
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(240, 36));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JButton ghostButton(String text) {
        JButton btn = new JButton(text) {
            boolean hovered = false;
            {
                addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        hovered = true;
                        repaint();
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                        hovered = false;
                        repaint();
                    }
                });
            }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(hovered ? CARD_BG : new Color(0, 0, 0, 0));
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8));
                g2.setColor(hovered ? CARD_BORDER : INPUT_BORDER);
                g2.setStroke(new BasicStroke(1f));
                g2.draw(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, 8, 8));
                g2.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                g2.setColor(hovered ? TEXT_PRIMARY : TEXT_SECONDARY);
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(getText(),
                        (getWidth() - fm.stringWidth(getText())) / 2,
                        (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
                g2.dispose();
            }

            @Override
            protected void paintBorder(Graphics g) {
            }
        };
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(180, 34));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void styleComboBox(JComboBox<String> box) {
        box.setBackground(INPUT_BG);
        box.setForeground(TEXT_PRIMARY);
        box.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        box.setBorder(BorderFactory.createLineBorder(INPUT_BORDER, 1));
        box.setFocusable(false);
        ((JLabel) box.getRenderer()).setHorizontalAlignment(JLabel.CENTER);
    }

    private JScrollPane darkScroll(Component c) {
        JScrollPane sp = new JScrollPane(c);
        sp.setBorder(BorderFactory.createLineBorder(INPUT_BORDER, 1));
        sp.setBackground(INPUT_BG);
        sp.getViewport().setBackground(INPUT_BG);
        styleScrollBar(sp.getVerticalScrollBar());
        styleScrollBar(sp.getHorizontalScrollBar());
        return sp;
    }

    private void styleScrollBar(JScrollBar sb) {
        sb.setBackground(INPUT_BG);
        sb.setPreferredSize(new Dimension(6, 6));
        sb.setUI(new javax.swing.plaf.basic.BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                thumbColor = new Color(50, 60, 90);
                trackColor = INPUT_BG;
            }

            @Override
            protected JButton createDecreaseButton(int o) {
                return zero();
            }

            @Override
            protected JButton createIncreaseButton(int o) {
                return zero();
            }

            private JButton zero() {
                JButton b = new JButton();
                b.setPreferredSize(new Dimension(0, 0));
                return b;
            }
        });
    }
}