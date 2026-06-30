package ui;

import dao.UserDAO;

import javax.swing.*;
import java.awt.*;

public class SignupFrame extends JFrame {

    public SignupFrame() {
        setTitle("Quiz Forge — Sign up");
        setSize(520, 460);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        getContentPane().setBackground(new Color(0x1E1E2E));
        setLayout(new BorderLayout());

        JLabel header = new JLabel("Create your account", SwingConstants.CENTER);
        header.setFont(new Font("SansSerif", Font.BOLD, 26));
        header.setForeground(new Color(0xF5C518));
        header.setBorder(BorderFactory.createEmptyBorder(30, 0, 10, 0));
        add(header, BorderLayout.NORTH);

        JTextField userF = new JTextField(18);
        JTextField emailF = new JTextField(18);
        JPasswordField passF = new JPasswordField(18);
        LoginFrame.styleField(userF);
        LoginFrame.styleField(emailF);
        LoginFrame.styleField(passF);

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(8, 8, 8, 8);
        g.fill = GridBagConstraints.HORIZONTAL;

        String[] labels = {"Username", "Email", "Password"};
        JComponent[] fields = {userF, emailF, passF};
        for (int i = 0; i < labels.length; i++) {
            JLabel l = new JLabel(labels[i]); l.setForeground(Color.WHITE);
            g.gridx = 0; g.gridy = i; form.add(l, g);
            g.gridx = 1;             form.add(fields[i], g);
        }

        JButton create = LoginFrame.primaryButton("Create account");
        JButton back   = LoginFrame.secondaryButton("Back to login");

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        btns.setOpaque(false);
        btns.add(create); btns.add(back);

        JPanel center = new JPanel(new BorderLayout(0, 10));
        center.setOpaque(false);
        center.setBorder(BorderFactory.createEmptyBorder(10, 40, 30, 40));
        center.add(form, BorderLayout.CENTER);
        center.add(btns, BorderLayout.SOUTH);
        add(center, BorderLayout.CENTER);

        UserDAO dao = new UserDAO();
        create.addActionListener(e -> {
            String u = userF.getText().trim();
            String em = emailF.getText().trim();
            String p = new String(passF.getPassword());
            if (u.isEmpty() || em.isEmpty() || p.length() < 4) {
                JOptionPane.showMessageDialog(this, "Fill all fields. Password >= 4 chars."); return;
            }
            try {
                dao.signup(u, em, p);
                JOptionPane.showMessageDialog(this, "Account created. Please log in.");
                dispose();
                new LoginFrame().setVisible(true);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        });
        back.addActionListener(e -> { dispose(); new LoginFrame().setVisible(true); });
    }
}
