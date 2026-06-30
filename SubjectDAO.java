package dao;

import db.DBConnection;
import model.Subject;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SubjectDAO {
    public List<Subject> listAll() throws SQLException {
        List<Subject> list = new ArrayList<>();
        try (Statement st = DBConnection.get().createStatement();
             ResultSet rs = st.executeQuery("SELECT id,name FROM subjects ORDER BY name")) {
            while (rs.next()) list.add(new Subject(rs.getInt(1), rs.getString(2)));
        }
        return list;
    }
}
