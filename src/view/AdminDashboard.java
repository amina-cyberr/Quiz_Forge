package ui;

import dao.AttemptDAO;
import dao.QuestionDAO;
import dao.SubjectDAO;
import dao.UserDAO;
import model.Subject;
import model.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class AdminDashboard extends JFrame {
    private final User admin;

    public AdminDashboard(User admin) {
        this.admin = admin;
        setTitle("Quiz Forge — Admin");
        setSize(900, 620);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        getContentPane().setBackground(new Color(0x1E1E2E));
        setLayout(new BorderLayout());

        JLabel head = new JLabel("  Admin Console — " + admin.username);
        head.setForeground(new Color(0xF5C518));
        head.setFont(new Font("SansSerif", Font.BOLD, 22));
        head.setBorder(BorderFactory.createEmptyBorder(18, 16, 18, 0));
        add(head, BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Users",       buildUsersTab());
        tabs.addTab("Attempts",    buildAttemptsTab());
        tabs.addTab("Add Question", buildAddQuestionTab());
        add(tabs, BorderLayout.CENTER);

        JButton logout = LoginFrame.secondaryButton("Logout");
        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        south.setOpaque(false);
        south.add(logout);
        add(south, BorderLayout.SOUTH);
        logout.addActionListener(e -> { dispose(); new LoginFrame().setVisible(true); });
    }

    private JComponent buildUsersTab() {
        DefaultTableModel m = new DefaultTableModel(
                new Object[]{"ID", "Username", "Email", "Role"}, 0);
        try {
            for (User u : new UserDAO().listAll())
                m.addRow(new Object[]{u.id, u.username, u.email, u.role});
        } catch (Exception ex) { m.addRow(new Object[]{"-", "error", ex.getMessage(), ""}); }
        return new JScrollPane(new JTable(m));
    }

    private JComponent buildAttemptsTab() {
        DefaultTableModel m = new DefaultTableModel(
                new Object[]{"User", "Subject", "Difficulty", "Score", "Time", "When"}, 0);
        try {
            for (String[] r : new AttemptDAO().listAll()) m.addRow(r);
        } catch (Exception ex) { m.addRow(new Object[]{"error", ex.getMessage(), "", "", "", ""}); }
        return new JScrollPane(new JTable(m));
    }

    private JComponent buildAddQuestionTab() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(new Color(0x1E1E2E));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(6, 8, 6, 8);
        g.fill = GridBagConstraints.HORIZONTAL;

        JComboBox<Subject> subBox;
        try {
            List<Subject> subs = new SubjectDAO().listAll();
            subBox = new JComboBox<>(subs.toArray(new Subject[0]));
        } catch (Exception ex) {
            p.add(new JLabel("DB error: " + ex.getMessage()));
            return p;
        }
        JComboBox<String> diffBox = new JComboBox<>(new String[]{"EASY", "MEDIUM", "HARD"});
        JTextField qField = new JTextField(30);
        JTextField[] opts = new JTextField[]{ new JTextField(28), new JTextField(28),
                                              new JTextField(28), new JTextField(28) };
        JComboBox<String> correctBox = new JComboBox<>(new String[]{"A", "B", "C", "D"});

        for (JTextField f : opts) LoginFrame.styleField(f);
        LoginFrame.styleField(qField);

        int y = 0;
        addRow(p, g, y++, "Subject",    subBox);
        addRow(p, g, y++, "Difficulty", diffBox);
        addRow(p, g, y++, "Question",   qField);
        for (int i = 0; i < 4; i++) addRow(p, g, y++, "Option " + (char)('A' + i), opts[i]);
        addRow(p, g, y++, "Correct",    correctBox);

        JButton save = LoginFrame.primaryButton("Add question");
        g.gridx = 1; g.gridy = y;
        p.add(save, g);

        save.addActionListener(e -> {
            try {
                String qtxt = qField.getText().trim();
                if (qtxt.isEmpty()) { JOptionPane.showMessageDialog(this, "Enter question"); return; }
                String[] optTexts = new String[4];
                for (int i = 0; i < 4; i++) {
                    optTexts[i] = opts[i].getText().trim();
                    if (optTexts[i].isEmpty()) { JOptionPane.showMessageDialog(this, "Fill all options"); return; }
                }
                int correctIdx = correctBox.getSelectedIndex();
                Subject s = (Subject) subBox.getSelectedItem();
                String d = (String) diffBox.getSelectedItem();
                new QuestionDAO().addQuestion(s.id, d, qtxt, optTexts, correctIdx);
                JOptionPane.showMessageDialog(this, "Question added.");
                qField.setText(""); for (JTextField f : opts) f.setText("");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        });
        return p;
    }

    private void addRow(JPanel p, GridBagConstraints g, int y, String label, JComponent field) {
        JLabel l = new JLabel(label);
        l.setForeground(Color.WHITE);
        g.gridx = 0; g.gridy = y; p.add(l, g);
        g.gridx = 1;             p.add(field, g);
    }
}
