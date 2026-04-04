package ui.customer;

import service.CustomerService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;

public class CustomerDashboard extends JFrame {

    private static final Color BG_DARK        = new Color(8,   10,  18);
    private static final Color CARD_BG        = new Color(18,  22,  38);
    private static final Color CARD_BORDER    = new Color(35,  42,  68);
    private static final Color ACCENT         = new Color(0,   180, 220);
    private static final Color SUCCESS        = new Color(0,   200, 130);
    private static final Color TEXT_PRIMARY   = new Color(225, 228, 245);
    private static final Color TEXT_SECONDARY = new Color(110, 118, 150);
    private static final Color INPUT_BG       = new Color(22,  27,  46);
    private static final Color INPUT_BORDER   = new Color(45,  52,  80);

    private JTextField     nameField, emailField, streetField;
    private JTextField     cityField, stateField, pincodeField, phoneField;
    private CustomerService customerService;

    public CustomerDashboard() {
        customerService = new CustomerService();

        setTitle("Register Customer");
        setSize(500, 620);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        getContentPane().setBackground(BG_DARK);
        getContentPane().setLayout(new GridBagLayout());

        getContentPane().add(buildCard());
    }

    private JPanel buildCard() {
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
        card.setPreferredSize(new Dimension(420, 570));
        card.setBorder(new EmptyBorder(28, 32, 28, 32));

        GridBagConstraints g = new GridBagConstraints();
        g.gridx = 0; g.fill = GridBagConstraints.HORIZONTAL; g.weightx = 1.0;

        g.gridy = 0; g.insets = new Insets(0, 0, 4, 0);
        JLabel title = new JLabel("Register Customer", JLabel.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(TEXT_PRIMARY);
        card.add(title, g);

        g.gridy = 1; g.insets = new Insets(0, 0, 22, 0);
        JLabel sub = new JLabel("Fill in the customer details", JLabel.CENTER);
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        sub.setForeground(TEXT_SECONDARY);
        card.add(sub, g);

        String[] labels = {"Name", "Email", "Street", "City", "State", "Pincode", "Phone Number"};
        JTextField[] fields = new JTextField[7];

        for (int i = 0; i < labels.length; i++) {
            g.gridy = 2 + i * 2;
            g.insets = new Insets(0, 0, 4, 0);
            card.add(fieldLabel(labels[i]), g);
            g.gridy = 3 + i * 2;
            g.insets = new Insets(0, 0, i == labels.length - 1 ? 22 : 8, 0);
            fields[i] = miniField();
            card.add(fields[i], g);
        }

        nameField    = fields[0]; emailField  = fields[1];
        streetField  = fields[2]; cityField   = fields[3];
        stateField   = fields[4]; pincodeField = fields[5];
        phoneField   = fields[6];

        g.gridy = 16; g.insets = new Insets(0, 0, 0, 0);
        JButton btn = styledButton("Register Customer", SUCCESS);
        btn.addActionListener(e -> registerCustomer());
        card.add(btn, g);

        return card;
    }

    // ── Logic (unchanged) ─────────────────────────────────────────────────────
    private void registerCustomer() {
        String name    = nameField.getText();
        String email   = emailField.getText();
        String phone   = phoneField.getText();

        if (name.isEmpty() || email.isEmpty() || phone.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Name, Email, Phone required");
            return;
        }

        boolean success = customerService.registerCustomer(
            name, email,
            streetField.getText(), cityField.getText(),
            stateField.getText(), pincodeField.getText(),
            phone
        );

        if (success) {
            JOptionPane.showMessageDialog(this, "Customer Registered Successfully");
            clearFields();
        } else {
            JOptionPane.showMessageDialog(this, "Customer Registration Failed");
        }
    }

    private void clearFields() {
        nameField.setText(""); emailField.setText(""); streetField.setText("");
        cityField.setText(""); stateField.setText(""); pincodeField.setText("");
        phoneField.setText("");
    }

    // ── UI helpers ────────────────────────────────────────────────────────────
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
        f.setBorder(new EmptyBorder(8, 10, 8, 10));
        f.setPreferredSize(new Dimension(340, 36));
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
        btn.setPreferredSize(new Dimension(340, 42));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }
}