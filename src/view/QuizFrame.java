package ui;

import dao.AttemptDAO;
import dao.QuestionDAO;
import model.Question;
import model.Subject;
import model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QuizFrame extends JFrame {
    private static final int NUM_QUESTIONS = 10;
    private static final int TIME_SECONDS  = 5 * 60; // 5 minutes

    private final User user;
    private final Subject subject;
    private final String difficulty;
    private final List<Question> questions;
    private final Map<Integer, Integer> answers = new HashMap<>(); // qIndex -> optionIndex
    private int currentIndex = 0;
    private int remaining = TIME_SECONDS;
    private final long startMillis = System.currentTimeMillis();

    private JLabel timerLabel;
    private JLabel qNumberLabel;
    private JTextArea qTextArea;
    private final JRadioButton[] optionBtns = new JRadioButton[4];
    private final ButtonGroup group = new ButtonGroup();
    private JButton prevBtn, nextBtn, submitBtn;
    private Timer swingTimer;

    public QuizFrame(User user, Subject subject, String difficulty) {
        this.user = user; this.subject = subject; this.difficulty = difficulty;
        try {
            this.questions = new QuestionDAO().randomQuestions(subject.id, difficulty, NUM_QUESTIONS);
        } catch (Exception e) { throw new RuntimeException(e); }
        if (questions.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No questions available.");
            dispose(); new UserDashboard(user).setVisible(true);
            throw new RuntimeException("no questions");
        }

        setTitle("Quiz Forge — " + subject + " (" + difficulty + ")");
        setSize(780, 560);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        getContentPane().setBackground(new Color(0x1E1E2E));
        setLayout(new BorderLayout());

        // top bar
        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(new Color(0x14141F));
        top.setBorder(new EmptyBorder(12, 16, 12, 16));
        JLabel title = new JLabel(subject + "  •  " + difficulty);
        title.setForeground(new Color(0xF5C518));
        title.setFont(new Font("SansSerif", Font.BOLD, 16));
        timerLabel = new JLabel(formatTime(remaining));
        timerLabel.setForeground(Color.WHITE);
        timerLabel.setFont(new Font("Monospaced", Font.BOLD, 18));
        top.add(title, BorderLayout.WEST);
        top.add(timerLabel, BorderLayout.EAST);
        add(top, BorderLayout.NORTH);

        // center
        JPanel center = new JPanel();
        center.setOpaque(false);
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setBorder(new EmptyBorder(20, 30, 10, 30));

        qNumberLabel = new JLabel();
        qNumberLabel.setForeground(new Color(0x9aa0a6));
        qNumberLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));

        qTextArea = new JTextArea();
        qTextArea.setLineWrap(true);
        qTextArea.setWrapStyleWord(true);
        qTextArea.setEditable(false);
        qTextArea.setBackground(new Color(0x1E1E2E));
        qTextArea.setForeground(Color.WHITE);
        qTextArea.setFont(new Font("SansSerif", Font.BOLD, 18));
        qTextArea.setBorder(new EmptyBorder(8, 0, 12, 0));

        center.add(qNumberLabel);
        center.add(qTextArea);

        for (int i = 0; i < 4; i++) {
            optionBtns[i] = new JRadioButton();
            optionBtns[i].setBackground(new Color(0x1E1E2E));
            optionBtns[i].setForeground(Color.WHITE);
            optionBtns[i].setFont(new Font("SansSerif", Font.PLAIN, 15));
            optionBtns[i].setFocusPainted(false);
            group.add(optionBtns[i]);
            int idx = i;
            optionBtns[i].addActionListener(e -> answers.put(currentIndex, idx));
            center.add(Box.createVerticalStrut(6));
            center.add(optionBtns[i]);
        }
        add(center, BorderLayout.CENTER);

        // bottom
        JPanel bot = new JPanel(new FlowLayout(FlowLayout.CENTER, 14, 14));
        bot.setOpaque(false);
        prevBtn   = LoginFrame.secondaryButton("Previous");
        nextBtn   = LoginFrame.secondaryButton("Next");
        submitBtn = LoginFrame.primaryButton("Submit");
        bot.add(prevBtn); bot.add(nextBtn); bot.add(submitBtn);
        add(bot, BorderLayout.SOUTH);

        prevBtn.addActionListener(e -> { if (currentIndex > 0) { currentIndex--; render(); } });
        nextBtn.addActionListener(e -> { if (currentIndex < questions.size() - 1) { currentIndex++; render(); } });
        submitBtn.addActionListener(e -> finishQuiz(false));

        render();
        startTimer();
    }

    private void render() {
        Question q = questions.get(currentIndex);
        qNumberLabel.setText("Question " + (currentIndex + 1) + " of " + questions.size());
        qTextArea.setText(q.text);
        group.clearSelection();
        for (int i = 0; i < 4; i++) {
            if (i < q.options.size()) {
                optionBtns[i].setText(q.options.get(i).text);
                optionBtns[i].setVisible(true);
            } else optionBtns[i].setVisible(false);
        }
        Integer chosen = answers.get(currentIndex);
        if (chosen != null && chosen < 4) optionBtns[chosen].setSelected(true);
        prevBtn.setEnabled(currentIndex > 0);
        nextBtn.setEnabled(currentIndex < questions.size() - 1);
    }

    private void startTimer() {
        swingTimer = new Timer(1000, e -> {
            remaining--;
            timerLabel.setText(formatTime(remaining));
            if (remaining <= 30) timerLabel.setForeground(new Color(0xFF6B6B));
            if (remaining <= 0) finishQuiz(true);
        });
        swingTimer.start();
    }

    private String formatTime(int s) {
        if (s < 0) s = 0;
        return String.format("Time left: %02d:%02d", s / 60, s % 60);
    }

    private void finishQuiz(boolean timedOut) {
        if (swingTimer != null) swingTimer.stop();
        int score = 0;
        for (int i = 0; i < questions.size(); i++) {
            Integer ans = answers.get(i);
            if (ans == null) continue;
            Question q = questions.get(i);
            if (ans < q.options.size() && q.options.get(ans).correct) score++;
        }
        int taken = (int) ((System.currentTimeMillis() - startMillis) / 1000);
        try {
            new AttemptDAO().save(user.id, subject.id, difficulty, score, questions.size(), taken);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Save error: " + ex.getMessage());
        }
        String msg = (timedOut ? "Time's up!\n\n" : "Quiz complete!\n\n") +
                     "Score: " + score + " / " + questions.size() + "\n" +
                     "Time taken: " + taken + "s";
        JOptionPane.showMessageDialog(this, msg);
        dispose();
        new UserDashboard(user).setVisible(true);
    }
}
