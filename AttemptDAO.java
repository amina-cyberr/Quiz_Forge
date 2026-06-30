package dao;

import db.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AttemptDAO {
    public void save(int userId, int subjectId, String difficulty,
                     int score, int total, int timeSec) throws SQLException {
        String sql = "INSERT INTO quiz_attempts(user_id,subject_id,difficulty,score,total_questions,time_taken_sec) " +
                     "VALUES(?,?,?,?,?,?)";
        try (PreparedStatement ps = DBConnection.get().prepareStatement(sql)) {
            ps.setInt(1, userId); ps.setInt(2, subjectId);
            ps.setString(3, difficulty);
            ps.setInt(4, score); ps.setInt(5, total); ps.setInt(6, timeSec);
            ps.executeUpdate();
        }
    }

    public List<String[]> listAll() throws SQLException {
        List<String[]> rows = new ArrayList<>();
        String sql = "SELECT u.username, s.name, a.difficulty, a.score, a.total_questions, " +
                     "a.time_taken_sec, a.attempted_at " +
                     "FROM quiz_attempts a JOIN users u ON a.user_id=u.id " +
                     "JOIN subjects s ON a.subject_id=s.id ORDER BY a.attempted_at DESC";
        try (Statement st = DBConnection.get().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                rows.add(new String[]{
                    rs.getString(1), rs.getString(2), rs.getString(3),
                    rs.getInt(4) + "/" + rs.getInt(5),
                    rs.getInt(6) + "s", rs.getTimestamp(7).toString()
                });
            }
        }
        return rows;
    }
}
