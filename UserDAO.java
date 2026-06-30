package dao;

import db.DBConnection;
import model.User;
import util.PasswordUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    public User authenticate(String username, String password) throws SQLException {
        String sql = "SELECT id,username,email,password_hash,salt,role FROM users WHERE username=?";
        try (PreparedStatement ps = DBConnection.get().prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                String salt = rs.getString("salt");
                String storedHash = rs.getString("password_hash");
                if (!PasswordUtil.hash(salt, password).equalsIgnoreCase(storedHash)) return null;
                return new User(rs.getInt("id"), rs.getString("username"),
                                rs.getString("email"), rs.getString("role"));
            }
        }
    }

    public User signup(String username, String email, String password) throws SQLException {
        String salt = PasswordUtil.generateSalt();
        String hash = PasswordUtil.hash(salt, password);
        String sql = "INSERT INTO users(username,email,password_hash,salt,role) VALUES (?,?,?,?,'USER')";
        try (PreparedStatement ps = DBConnection.get().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, username);
            ps.setString(2, email);
            ps.setString(3, hash);
            ps.setString(4, salt);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                keys.next();
                return new User(keys.getInt(1), username, email, "USER");
            }
        }
    }

    public List<User> listAll() throws SQLException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT id,username,email,role FROM users ORDER BY id";
        try (Statement st = DBConnection.get().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                users.add(new User(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4)));
            }
        }
        return users;
    }
}
