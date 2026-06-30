package dao;

import db.DBConnection;
import model.Question;

import java.sql.*;
import java.util.*;

public class QuestionDAO {

    /** Pull N RANDOM questions for a (subject, difficulty). Different set each call. */
    public List<Question> randomQuestions(int subjectId, String difficulty, int n) throws SQLException {
        List<Question> result = new ArrayList<>();
        String sql = "SELECT id, text FROM questions WHERE subject_id=? AND difficulty=? " +
                     "ORDER BY RAND() LIMIT ?";
        try (PreparedStatement ps = DBConnection.get().prepareStatement(sql)) {
            ps.setInt(1, subjectId);
            ps.setString(2, difficulty);
            ps.setInt(3, n);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int qid = rs.getInt(1);
                    String qtext = rs.getString(2);
                    result.add(new Question(qid, qtext, loadOptions(qid)));
                }
            }
        }
        return result;
    }

    private List<Question.Option> loadOptions(int qid) throws SQLException {
        List<Question.Option> opts = new ArrayList<>();
        String sql = "SELECT id, text, is_correct FROM options WHERE question_id=?";
        try (PreparedStatement ps = DBConnection.get().prepareStatement(sql)) {
            ps.setInt(1, qid);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    opts.add(new Question.Option(rs.getInt(1), rs.getString(2), rs.getInt(3) == 1));
                }
            }
        }
        Collections.shuffle(opts); // also randomize option order
        return opts;
    }

    public void addQuestion(int subjectId, String difficulty, String text,
                            String[] options, int correctIndex) throws SQLException {
        Connection c = DBConnection.get();
        c.setAutoCommit(false);
        try {
            int qid;
            try (PreparedStatement ps = c.prepareStatement(
                    "INSERT INTO questions(subject_id,difficulty,text) VALUES(?,?,?)",
                    Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, subjectId);
                ps.setString(2, difficulty);
                ps.setString(3, text);
                ps.executeUpdate();
                try (ResultSet k = ps.getGeneratedKeys()) { k.next(); qid = k.getInt(1); }
            }
            try (PreparedStatement ps = c.prepareStatement(
                    "INSERT INTO options(question_id,text,is_correct) VALUES(?,?,?)")) {
                for (int i = 0; i < options.length; i++) {
                    ps.setInt(1, qid);
                    ps.setString(2, options[i]);
                    ps.setInt(3, i == correctIndex ? 1 : 0);
                    ps.addBatch();
                }
                ps.executeBatch();
            }
            c.commit();
        } catch (SQLException ex) {
            c.rollback();
            throw ex;
        } finally {
            c.setAutoCommit(true);
        }
    }
}
