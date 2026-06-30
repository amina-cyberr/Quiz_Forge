package ui;

import dao.UserDAO;
import model.User;

import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {

    private final UserDAO userDAO = new UserDAO();

    public LoginFrame() {
        setTitle("Quiz Forge — Login");
        setSize(520, 460);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        getContentPane().setBackground(new Color(0x1E1E2E));
        setLayout(new BorderLayout());

        // Header
        JLabel header = new JLabel("QUIZ FORGE", SwingConstants.CENTER);
        header.setFont(new Font("SansSerif", Font.BOLD, 36));
        header.setForeground(new Color(0xF5C518));
        header.setBorder(BorderFactory.createEmptyBorder(30, 0, 5, 0));
        add(header, BorderLayout.NORTH);

        JLabel sub = new JLabel("Sign in to start the challenge", SwingConstants.CENTER);
        sub.setForeground(Color.LIGHT_GRAY);
        sub.setFont(new Font("SansSerif", Font.PLAIN, 14));

        JPanel form = new JPanel();
        form.setOpaque(false);
        form.setLayout(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(8, 8, 8, 8);
        g.fill = GridBagConstraints.HORIZONTAL;

        JTextField userF = new JTextField(18);
        JPasswordField passF = new JPasswordField(18);
        styleField(userF); styleField(passF);

        JLabel lu = new JLabel("Username");
        JLabel lp = new JLabel("Password");
        lu.setForeground(Color.WHITE); lp.setForeground(Color.WHITE);

        g.gridx = 0; g.gridy = 0; form.add(lu, g);
        g.gridx = 1;             form.add(userF, g);
        g.gridx = 0; g.gridy = 1; form.add(lp, g);
        g.gridx = 1;             form.add(passF, g);

        JButton loginBtn  = primaryButton("Login");
        JButton signupBtn = secondaryButton("Sign up");

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        btns.setOpaque(false);
        btns.add(loginBtn);
        btns.add(signupBtn);

        JPanel center = new JPanel(new BorderLayout(0, 10));
        center.setOpaque(false);
        center.setBorder(BorderFactory.createEmptyBorder(10, 40, 20, 40));
        center.add(sub, BorderLayout.NORTH);
        center.add(form, BorderLayout.CENTER);
        center.add(btns, BorderLayout.SOUTH);
        add(center, BorderLayout.CENTER);

        JLabel foot = new JLabel("Admin? Use admin / admin123", SwingConstants.CENTER);
        foot.setForeground(new Color(0x9aa0a6));
        foot.setBorder(BorderFactory.createEmptyBorder(0, 0, 18, 0));
        add(foot, BorderLayout.SOUTH);

        loginBtn.addActionListener(e -> {
            try {
                User u = userDAO.authenticate(userF.getText().trim(),
                                              new String(passF.getPassword()));
                if (u == null) {
                    JOptionPane.showMessageDialog(this, "Invalid credentials");
                    return;
                }
                dispose();
                if ("ADMIN".equals(u.role)) new AdminDashboard(u).setVisible(true);
                else                        new UserDashboard(u).setVisible(true);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        });

        signupBtn.addActionListener(e -> {
            dispose();
            new SignupFrame().setVisible(true);
        });
    }

    static void styleField(JTextField f) {
        f.setBackground(new Color(0x2A2A3D));
        f.setForeground(Color.WHITE);
        f.setCaretColor(Color.WHITE);
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0x3D3D55)),
                BorderFactory.createEmptyBorder(6, 8, 6, 8)));
    }

    static JButton primaryButton(String t) {
        JButton b = new JButton(t);
        b.setBackground(new Color(0xF5C518));
        b.setForeground(Color.BLACK);
        b.setFocusPainted(false);
        b.setFont(new Font("SansSerif", Font.BOLD, 14));
        b.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));
        return b;
    }

    static JButton secondaryButton(String t) {
        JButton b = new JButton(t);
        b.setBackground(new Color(0x2A2A3D));
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createLineBorder(new Color(0xF5C518)));
        b.setFont(new Font("SansSerif", Font.BOLD, 14));
        return b;
    }
}
