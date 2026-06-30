package ui;

import dao.SubjectDAO;
import model.Subject;
import model.User;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class UserDashboard extends JFrame {
    private final User user;

    public UserDashboard(User user) {
        this.user = user;
        setTitle("Quiz Forge — User");
        setSize(640, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        getContentPane().setBackground(new Color(0x1E1E2E));
        setLayout(new BorderLayout());

        JLabel header = new JLabel("  Welcome, " + user.username);
        header.setFont(new Font("SansSerif", Font.BOLD, 22));
        header.setForeground(new Color(0xF5C518));
        header.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 0));
        add(header, BorderLayout.NORTH);

        JPanel center = new JPanel(new GridBagLayout());
        center.setOpaque(false);
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(10, 10, 10, 10);
        g.fill = GridBagConstraints.HORIZONTAL;

        try {
            List<Subject> subjects = new SubjectDAO().listAll();
            JComboBox<Subject> subBox = new JComboBox<>(subjects.toArray(new Subject[0]));
            JComboBox<String> diffBox = new JComboBox<>(new String[]{"EASY", "MEDIUM", "HARD"});

            JLabel l1 = new JLabel("Subject");    l1.setForeground(Color.WHITE);
            JLabel l2 = new JLabel("Difficulty"); l2.setForeground(Color.WHITE);

            g.gridx = 0; g.gridy = 0; center.add(l1, g);
            g.gridx = 1;             center.add(subBox, g);
            g.gridx = 0; g.gridy = 1; center.add(l2, g);
            g.gridx = 1;             center.add(diffBox, g);

            JButton start = LoginFrame.primaryButton("Start Quiz");
            JButton logout = LoginFrame.secondaryButton("Logout");

            g.gridx = 0; g.gridy = 2; g.gridwidth = 2;
            JPanel btns = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
            btns.setOpaque(false);
            btns.add(start); btns.add(logout);
            center.add(btns, g);

            add(center, BorderLayout.CENTER);

            start.addActionListener(e -> {
                Subject s = (Subject) subBox.getSelectedItem();
                String d = (String) diffBox.getSelectedItem();
                dispose();
                new QuizFrame(user, s, d).setVisible(true);
            });
            logout.addActionListener(e -> { dispose(); new LoginFrame().setVisible(true); });

        } catch (Exception ex) {
            add(new JLabel("DB error: " + ex.getMessage()), BorderLayout.CENTER);
        }
    }
}
