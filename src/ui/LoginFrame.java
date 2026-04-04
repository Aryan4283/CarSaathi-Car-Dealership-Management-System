package ui;

import service.UserService;
import model.User;
import ui.admin.AdminDashboard;
import ui.dealer.DealerDashboard;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;

public class LoginFrame extends JFrame {

    // ── Colour palette ──────────────────────────────────────────────────────
    private static final Color BG_DARK          = new Color(8,  10, 18);
    private static final Color CARD_BG          = new Color(16, 19, 32);
    private static final Color CARD_BORDER      = new Color(35, 40, 65);
    private static final Color ACCENT           = new Color(0,  180, 220);
    private static final Color ACCENT_HOVER     = new Color(30, 210, 255);
    private static final Color ACCENT_GLOW      = new Color(0,  180, 220, 40);
    private static final Color TEXT_PRIMARY     = new Color(225, 228, 245);
    private static final Color TEXT_SECONDARY   = new Color(110, 118, 150);
    private static final Color INPUT_BG         = new Color(22, 26, 44);
    private static final Color INPUT_BORDER     = new Color(45, 52, 80);
    private static final Color INPUT_FOCUS      = new Color(0,  180, 220);

    private JTextField     emailField;
    private JPasswordField passwordField;
    private JButton        loginButton;
    private UserService    userService;

    public LoginFrame() {

        userService = new UserService();  // ← logic untouched

        setTitle("Car Dealership Management System");
        setSize(460, 540);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Dark background on the frame itself
        getContentPane().setBackground(BG_DARK);
        setLayout(new GridBagLayout());

        add(buildCard());

        // ── Listeners (logic untouched) ──────────────────────────────────
        loginButton.addActionListener(e -> handleLogin());
        emailField.addActionListener(e -> passwordField.requestFocus());
        passwordField.addActionListener(e -> handleLogin());
    }

    // ────────────────────────────────────────────────────────────────────────
    //  Card panel
    // ────────────────────────────────────────────────────────────────────────
    private JPanel buildCard() {

        JPanel card = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                    RenderingHints.VALUE_ANTIALIAS_ON);
                // subtle outer glow
                g2.setColor(ACCENT_GLOW);
                g2.fill(new RoundRectangle2D.Float(-4, -4,
                        getWidth() + 8, getHeight() + 8, 28, 28));
                // card body
                g2.setColor(CARD_BG);
                g2.fill(new RoundRectangle2D.Float(0, 0,
                        getWidth(), getHeight(), 20, 20));
                // card border
                g2.setColor(CARD_BORDER);
                g2.setStroke(new BasicStroke(1f));
                g2.draw(new RoundRectangle2D.Float(0.5f, 0.5f,
                        getWidth() - 1, getHeight() - 1, 20, 20));
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setPreferredSize(new Dimension(380, 470));
        card.setLayout(new GridBagLayout());

        GridBagConstraints g = new GridBagConstraints();
        g.gridx   = 0;
        g.fill    = GridBagConstraints.HORIZONTAL;
        g.weightx = 1.0;

        // Logo
        g.gridy  = 0;
        g.insets = new Insets(40, 40, 0, 40);
        card.add(buildLogo(), g);

        // Title
        g.gridy  = 1;
        g.insets = new Insets(18, 40, 2, 40);
        JLabel title = new JLabel("Welcome Back", JLabel.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(TEXT_PRIMARY);
        card.add(title, g);

        // Subtitle
        g.gridy  = 2;
        g.insets = new Insets(0, 40, 28, 40);
        JLabel sub = new JLabel("Sign in to continue", JLabel.CENTER);
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        sub.setForeground(TEXT_SECONDARY);
        card.add(sub, g);

        // Email label + field
        g.gridy  = 3;
        g.insets = new Insets(0, 40, 5, 40);
        card.add(fieldLabel("Email Address"), g);

        g.gridy  = 4;
        g.insets = new Insets(0, 40, 16, 40);
        emailField = styledTextField();
        card.add(emailField, g);

        // Password label + field
        g.gridy  = 5;
        g.insets = new Insets(0, 40, 5, 40);
        card.add(fieldLabel("Password"), g);

        g.gridy  = 6;
        g.insets = new Insets(0, 40, 30, 40);
        passwordField = styledPasswordField();
        card.add(passwordField, g);

        // Login button
        g.gridy  = 7;
        g.insets = new Insets(0, 40, 40, 40);
        loginButton = styledButton("Sign In");
        card.add(loginButton, g);

        return card;
    }

    // ────────────────────────────────────────────────────────────────────────
    //  Logo panel  (simple car icon drawn with Graphics2D)
    // ────────────────────────────────────────────────────────────────────────
    private JPanel buildLogo() {
        JPanel p = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                    RenderingHints.VALUE_ANTIALIAS_ON);

                int s = 54, x = (getWidth() - s) / 2, y = 2;

                // Glow ring
                g2.setColor(ACCENT_GLOW);
                g2.fillOval(x - 6, y - 6, s + 12, s + 12);
                // Circle border
                g2.setColor(ACCENT);
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawOval(x, y, s, s);

                // Car silhouette
                int cx = x + s / 2, cy = y + s / 2;
                g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND,
                                             BasicStroke.JOIN_ROUND));
                // body
                int[] bx = {cx-16, cx-18, cx-18, cx+18, cx+18, cx+16};
                int[] by = {cy-3,  cy-3,  cy+6,  cy+6,  cy-3,  cy-3};
                g2.drawPolyline(bx, by, 6);
                // roof
                int[] rx = {cx-10, cx-6, cx+6, cx+10};
                int[] ry = {cy-3,  cy-11, cy-11, cy-3};
                g2.drawPolyline(rx, ry, 4);
                // wheels
                g2.setColor(ACCENT);
                g2.fillOval(cx - 15, cy + 3, 10, 10);
                g2.fillOval(cx +  5, cy + 3, 10, 10);

                g2.dispose();
            }
        };
        p.setOpaque(false);
        p.setPreferredSize(new Dimension(300, 62));
        return p;
    }

    // ────────────────────────────────────────────────────────────────────────
    //  Helpers
    // ────────────────────────────────────────────────────────────────────────
    private JLabel fieldLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        l.setForeground(TEXT_SECONDARY);
        return l;
    }

    private JTextField styledTextField() {
        JTextField f = new JTextField() {
            @Override protected void paintComponent(Graphics g) {
                paintRoundedBg(g, this);
                super.paintComponent(g);
            }
            @Override protected void paintBorder(Graphics g) {
                paintRoundedBorder(g, this);
            }
        };
        applyInputStyle(f);
        return f;
    }

    private JPasswordField styledPasswordField() {
        JPasswordField f = new JPasswordField() {
            @Override protected void paintComponent(Graphics g) {
                paintRoundedBg(g, this);
                super.paintComponent(g);
            }
            @Override protected void paintBorder(Graphics g) {
                paintRoundedBorder(g, this);
            }
        };
        applyInputStyle(f);
        return f;
    }

    private void applyInputStyle(JTextField f) {
        f.setOpaque(false);
        f.setForeground(TEXT_PRIMARY);
        f.setCaretColor(ACCENT);
        f.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        f.setBorder(new EmptyBorder(10, 14, 10, 14));
        f.setPreferredSize(new Dimension(300, 44));
        // Repaint on focus change so border colour updates
        f.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) { f.repaint(); }
            @Override public void focusLost(FocusEvent e)   { f.repaint(); }
        });
    }

    private void paintRoundedBg(Graphics g, Component c) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(INPUT_BG);
        g2.fill(new RoundRectangle2D.Float(0, 0, c.getWidth(), c.getHeight(), 10, 10));
        g2.dispose();
    }

    private void paintRoundedBorder(Graphics g, Component c) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);
        boolean focused = c.hasFocus();
        g2.setColor(focused ? INPUT_FOCUS : INPUT_BORDER);
        g2.setStroke(new BasicStroke(focused ? 1.5f : 1f));
        g2.draw(new RoundRectangle2D.Float(0, 0,
                c.getWidth() - 1, c.getHeight() - 1, 10, 10));
        g2.dispose();
    }

    private JButton styledButton(String text) {
        JButton btn = new JButton(text) {
            private boolean hovered = false;
            private boolean pressed = false;
            {
                addMouseListener(new MouseAdapter() {
                    @Override public void mouseEntered(MouseEvent e) { hovered = true;  repaint(); }
                    @Override public void mouseExited (MouseEvent e) { hovered = false; repaint(); }
                    @Override public void mousePressed(MouseEvent e) { pressed = true;  repaint(); }
                    @Override public void mouseReleased(MouseEvent e){ pressed = false; repaint(); }
                });
            }
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                    RenderingHints.VALUE_ANTIALIAS_ON);
                Color fill = pressed ? ACCENT : (hovered ? ACCENT_HOVER : ACCENT);
                // Glow under button
                if (hovered) {
                    g2.setColor(new Color(0, 180, 220, 60));
                    g2.fill(new RoundRectangle2D.Float(-3, -3,
                            getWidth() + 6, getHeight() + 6, 16, 16));
                }
                g2.setColor(fill);
                g2.fill(new RoundRectangle2D.Float(0, 0,
                        getWidth(), getHeight(), 10, 10));
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 14));
                FontMetrics fm = g2.getFontMetrics();
                int tx = (getWidth()  - fm.stringWidth(getText())) / 2;
                int ty = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(getText(), tx, ty);
                g2.dispose();
            }
            @Override protected void paintBorder(Graphics g) { /* none */ }
        };
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(300, 46));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    // ────────────────────────────────────────────────────────────────────────
    //  Login logic — completely unchanged
    // ────────────────────────────────────────────────────────────────────────
    private void handleLogin() {

        String email    = emailField.getText();
        String password = new String(passwordField.getPassword());

        User user = userService.loginUser(email, password);

        if (user == null) {
            JOptionPane.showMessageDialog(this,
                    "Invalid Credentials",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        String role = userService.getUserRole(user.getUserId());

        this.dispose();

        switch (role) {
            case "ADMIN":
                String scope = userService.getAdminAccessScope(user.getUserId());
                new AdminDashboard(user.getName(), scope).setVisible(true);
                break;
            case "DEALER":
                new DealerDashboard(user.getUserId(), user.getName()).setVisible(true);
                break;
            default:
                JOptionPane.showMessageDialog(null,
                        "No valid role assigned",
                        "Access Denied",
                        JOptionPane.ERROR_MESSAGE);
        }
    }
}