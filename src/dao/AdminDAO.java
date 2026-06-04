package dao;

import model.Admin;
import util.DatabaseConnection;

import java.sql.*;

public class AdminDAO {

    private Connection conn() {
        return DatabaseConnection.getConnection();
    }

    public Admin findByUsernamePassword(String username, String password) {
        String sql = "SELECT * FROM admin WHERE username=? AND password=?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Admin(
                    rs.getInt("id_admin"),
                    rs.getString("nama"),
                    rs.getString("no_telp"),
                    rs.getString("username"),
                    rs.getString("password")
                );
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }
}