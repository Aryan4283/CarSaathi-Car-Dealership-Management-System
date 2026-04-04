package ui.admin;

import model.CarModel;
import service.CarService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.util.List;

public class ManageCarsUI extends JFrame {

    private static final Color BG_DARK        = new Color(8,   10,  18);
    private static final Color PANEL_BG       = new Color(13,  16,  28);
    private static final Color CARD_BG        = new Color(18,  22,  38);
    private static final Color CARD_BORDER    = new Color(35,  42,  68);
    private static final Color ACCENT         = new Color(0,   180, 220);
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

    private JTextField brandField, modelField, categoryField, fuelField, priceField;
    private DefaultTableModel tableModel;
    private CarService carService;
    private boolean readOnly;

    public ManageCarsUI(boolean readOnly) {
        this.readOnly   = readOnly;
        this.carService = new CarService();

        setTitle(readOnly ? "View Car Models" : "Manage Car Models");
        setSize(readOnly ? 700 : 900, 580);
        setMinimumSize(new Dimension(readOnly ? 600 : 800, 500));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        getContentPane().setBackground(BG_DARK);
        getContentPane().setLayout(new BorderLayout());

        getContentPane().add(buildHeader(),  BorderLayout.NORTH);
        getContentPane().add(buildCenter(),  BorderLayout.CENTER);
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

        JLabel title = new JLabel(readOnly ? "View Car Models" : "Car Model Management",
                JLabel.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(TEXT_PRIMARY);

        h.add(brand, BorderLayout.WEST);
        h.add(title, BorderLayout.CENTER);
        return h;
    }

    private JPanel buildCenter() {
        JPanel p = new JPanel(new BorderLayout(16, 0));
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(18, 22, 18, 22));

        if (!readOnly) {
            p.add(buildFormCard(), BorderLayout.WEST);
        }
        p.add(buildTableCard(), BorderLayout.CENTER);
        return p;
    }

    private JPanel buildFormCard() {
        JPanel card = card();
        card.setLayout(new GridBagLayout());
        card.setBorder(new EmptyBorder(20, 20, 20, 20));
        card.setPreferredSize(new Dimension(280, 0));

        GridBagConstraints g = new GridBagConstraints();
        g.gridx = 0; g.fill = GridBagConstraints.HORIZONTAL; g.weightx = 1.0;

        g.gridy = 0; g.insets = new Insets(0, 0, 16, 0);
        JLabel sec = new JLabel("Add Car Model");
        sec.setFont(new Font("Segoe UI", Font.BOLD, 14));
        sec.setForeground(TEXT_PRIMARY);
        card.add(sec, g);

        g.insets = new Insets(0, 0, 4, 0);
        String[] labels = {"Brand", "Model Name", "Category", "Fuel Type", "Base Price"};
        JTextField[] fields = new JTextField[5];

        for (int i = 0; i < labels.length; i++) {
            g.gridy = 1 + i * 2;
            card.add(fieldLabel(labels[i]), g);
            g.gridy = 2 + i * 2;
            g.insets = new Insets(0, 0, i == labels.length - 1 ? 16 : 8, 0);
            fields[i] = miniField();
            card.add(fields[i], g);
        }

        brandField    = fields[0];
        modelField    = fields[1];
        categoryField = fields[2];
        fuelField     = fields[3];
        priceField    = fields[4];

        g.gridy = 11; g.insets = new Insets(0, 0, 0, 0);
        JButton addBtn = styledButton("Add Car Model", ACCENT);
        addBtn.addActionListener(e -> addCarModel());
        card.add(addBtn, g);

        return card;
    }

    private JPanel buildTableCard() {
        JPanel card = card();
        card.setLayout(new BorderLayout(0, 12));
        card.setBorder(new EmptyBorder(18, 18, 18, 18));

        JPanel head = new JPanel(new BorderLayout());
        head.setOpaque(false);
        JLabel lbl = new JLabel("All Car Models");
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lbl.setForeground(TEXT_PRIMARY);
        head.add(lbl, BorderLayout.WEST);

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btnRow.setOpaque(false);
        JButton viewBtn = ghostButton("Refresh");
        viewBtn.addActionListener(e -> displayAllCars());
        btnRow.add(viewBtn);

        if (!readOnly) {
            JButton deleteBtn = ghostButton("Delete by ID");
            deleteBtn.addActionListener(e -> deleteCarModel());
            btnRow.add(deleteBtn);
        }

        head.add(btnRow, BorderLayout.EAST);
        card.add(head, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(
                new String[]{"ID", "Brand", "Model Name", "Category", "Fuel Type", "Base Price"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(tableModel);
        styleTable(table);
        card.add(darkScroll(table), BorderLayout.CENTER);

        displayAllCars();
        return card;
    }

    // ── Logic ─────────────────────────────────────────────────────────────────
    private void addCarModel() {
        try {
            boolean success = carService.addCarModel(
                brandField.getText(), modelField.getText(),
                categoryField.getText(), fuelField.getText(),
                Double.parseDouble(priceField.getText())
            );
            if (success) {
                JOptionPane.showMessageDialog(this, "Car Model Added Successfully!");
                clearFields();
                displayAllCars();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Invalid Input");
        }
    }

    private void displayAllCars() {
        tableModel.setRowCount(0);
        List<CarModel> cars = carService.getAllCarModels();
        for (CarModel car : cars) {
            tableModel.addRow(new Object[]{
                car.getModelId(), car.getBrand(), car.getModelName(),
                car.getCategory(), car.getFuelType(), car.getBasePrice()
            });
        }
    }

    private void deleteCarModel() {
        String idStr = JOptionPane.showInputDialog(this, "Enter Model ID to Delete:");
        if (idStr != null) {
            try {
                boolean success = carService.deleteCarModel(Integer.parseInt(idStr));
                if (success) {
                    JOptionPane.showMessageDialog(this, "Deleted Successfully");
                    displayAllCars();
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Invalid ID");
            }
        }
    }

    private void clearFields() {
        brandField.setText(""); modelField.setText("");
        categoryField.setText(""); fuelField.setText(""); priceField.setText("");
    }

    // ── UI helpers ────────────────────────────────────────────────────────────
    private JPanel card() {
        return new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(CARD_BG);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 14, 14));
                g2.setColor(CARD_BORDER);
                g2.setStroke(new BasicStroke(1f));
                g2.draw(new RoundRectangle2D.Float(0.5f, 0.5f, getWidth()-1, getHeight()-1, 14, 14));
                g2.dispose();
            }
        };
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
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(INPUT_BG);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8));
                g2.dispose();
                super.paintComponent(g);
            }
            @Override protected void paintBorder(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(hasFocus() ? ACCENT : INPUT_BORDER);
                g2.setStroke(new BasicStroke(hasFocus() ? 1.5f : 1f));
                g2.draw(new RoundRectangle2D.Float(0, 0, getWidth()-1, getHeight()-1, 8, 8));
                g2.dispose();
            }
        };
        f.setOpaque(false);
        f.setForeground(TEXT_PRIMARY);
        f.setCaretColor(ACCENT);
        f.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        f.setBorder(new EmptyBorder(7, 10, 7, 10));
        f.setPreferredSize(new Dimension(200, 34));
        f.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) { f.repaint(); }
            @Override public void focusLost(FocusEvent e)   { f.repaint(); }
        });
        return f;
    }

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
                    (getWidth() - fm.stringWidth(getText())) / 2,
                    (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
                g2.dispose();
            }
            @Override protected void paintBorder(Graphics g) {}
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
                    @Override public void mouseEntered(MouseEvent e) { hovered = true;  repaint(); }
                    @Override public void mouseExited(MouseEvent e)  { hovered = false; repaint(); }
                });
            }
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(hovered ? CARD_BG : new Color(0,0,0,0));
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8));
                g2.setColor(hovered ? CARD_BORDER : INPUT_BORDER);
                g2.setStroke(new BasicStroke(1f));
                g2.draw(new RoundRectangle2D.Float(0, 0, getWidth()-1, getHeight()-1, 8, 8));
                g2.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                g2.setColor(hovered ? TEXT_PRIMARY : TEXT_SECONDARY);
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(getText(),
                    (getWidth() - fm.stringWidth(getText())) / 2,
                    (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
                g2.dispose();
            }
            @Override protected void paintBorder(Graphics g) {}
        };
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(130, 32));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

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