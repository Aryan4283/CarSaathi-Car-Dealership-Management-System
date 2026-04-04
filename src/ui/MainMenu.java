package ui;

import javax.swing.*;
import java.awt.*;

public class MainMenu extends JFrame {

    public MainMenu(String name) {

        setTitle("Dashboard");
        setSize(500, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JLabel label = new JLabel(
                "Welcome " + name,
                JLabel.CENTER
        );

        label.setFont(new Font("Segoe UI", Font.BOLD, 20));

        add(label);
    }
}
