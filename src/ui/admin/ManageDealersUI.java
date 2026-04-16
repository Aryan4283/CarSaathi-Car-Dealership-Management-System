package ui.admin;

import service.DealerService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;

public class ManageDealersUI extends JFrame {

    private static final Color BG_DARK        = new Color(8,   10,  18);
    private static final Color PANEL_BG       = new Color(13,  16,  28);
    private static final Color CARD_BG        = new Color(18,  22,  38);
    private static final Color CARD_BORDER    = new Color(35,  42,  68);
    private static final Color ACCENT         = new Color(0,   180, 220);
    private static final Color TEXT_PRIMARY   = new Color(225, 228, 245);
    private static final Color TEXT_SECONDARY = new Color(110, 118, 150);
    private static final Color INPUT_BG       = new Color(22,  27,  46);
    private static final Color INPUT_BORDER   = new Color(45,  52,  80);
    private static final Color DIVIDER        = new Color(30,  36,  58);

    private JTextField     nameField, emailField, showroomField, locationField;
    private JPasswordField passwordField;
    private DealerService  dealerService;
    private JTextField phoneField;
    public ManageDealersUI() {
        dealerService = new DealerService();

        setTitle("Register Dealer");
        setSize(460, 590);
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
        card.setPreferredSize(new Dimension(400, 530));
        card.setBorder(new EmptyBorder(28, 32, 28, 32));

        GridBagConstraints g = new GridBagConstraints();
        g.gridx = 0; g.fill = GridBagConstraints.HORIZONTAL; g.weightx = 1.0;

        // Title
        g.gridy = 0; g.insets = new Insets(0, 0, 6, 0);
        JLabel title = new JLabel("Register Dealer", JLabel.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(TEXT_PRIMARY);
        card.add(title, g);

        g.gridy = 1; g.insets = new Insets(0, 0, 24, 0);
        JLabel sub = new JLabel("Fill in the details below", JLabel.CENTER);
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        sub.setForeground(TEXT_SECONDARY);
        card.add(sub, g);

        // Fields
        String[] labels = {"Dealer Name", "Dealer Email", "Password", "Showroom Name", "Location","Phone Number"};
        g.insets = new Insets(0, 0, 4, 0);

        for (int i = 0; i < labels.length; i++) {
            g.gridy = 2 + i * 2;
            card.add(fieldLabel(labels[i]), g);
            g.gridy = 3 + i * 2;
            g.insets = new Insets(0, 0, i == labels.length - 1 ? 24 : 10, 0);

            if (labels[i].equals("Password")) {
                passwordField = styledPasswordField();
                card.add(passwordField, g);
            } else {
                JTextField f = miniField();
                switch (labels[i]) {
                    case "Dealer Name":     nameField     = f; break;
                    case "Dealer Email":    emailField    = f; break;
                    case "Showroom Name":   showroomField = f; break;
                    case "Location":        locationField = f; break;
                    case "Phone Number": phoneField = f; break;
                }
                card.add(f, g);
            }
            g.insets = new Insets(0, 0, 4, 0);
        }

        g.gridy = 14; g.insets = new Insets(0, 0, 0, 0);
        JButton btn = styledButton("Register Dealer");
        btn.addActionListener(e -> registerDealer());
        card.add(btn, g);

        return card;
    }

    // ── Logic (unchanged) ─────────────────────────────────────────────────────
    private void registerDealer() {
    String name = nameField.getText().trim();
    String email = emailField.getText().trim();
    String password = new String(passwordField.getPassword());
    String showroom = showroomField.getText().trim();
    String location = locationField.getText().trim();
    String phone = phoneField.getText().trim();

    if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
        JOptionPane.showMessageDialog(this, "All fields are required.");
        return;
    }
    if (!email.contains("@") || !email.contains(".")) {
        JOptionPane.showMessageDialog(this, "Enter a valid email address.");
        return;
    }
    if (!phone.matches("\\d{10}")) {
        JOptionPane.showMessageDialog(this, "Phone number must be exactly 10 digits.");
        return;
    }

    boolean success = dealerService.registerDealer(name, email, password, showroom, location);
    JOptionPane.showMessageDialog(this,
        success ? "Dealer Registered Successfully" : "Registration Failed");
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
        f.setPreferredSize(new Dimension(300, 36));
        f.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) { f.repaint(); }
            @Override public void focusLost(FocusEvent e)   { f.repaint(); }
        });
        return f;
    }

    private JPasswordField styledPasswordField() {
        JPasswordField f = new JPasswordField() {
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
        f.setPreferredSize(new Dimension(300, 36));
        f.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) { f.repaint(); }
            @Override public void focusLost(FocusEvent e)   { f.repaint(); }
        });
        return f;
    }

    private JButton styledButton(String text) {
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
                    g2.setColor(new Color(0, 180, 220, 50));
                    g2.fill(new RoundRectangle2D.Float(-3,-3, getWidth()+6, getHeight()+6, 14, 14));
                }
                g2.setColor(pressed ? ACCENT.darker() : hovered ? ACCENT.brighter() : ACCENT);
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
        btn.setPreferredSize(new Dimension(300, 42));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }
}