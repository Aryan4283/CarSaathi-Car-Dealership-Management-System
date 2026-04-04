package dao;

import db.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AdminDao {

    public boolean isAdmin(int userId) {
        String sql = "SELECT * FROM ADMIN WHERE UserID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            return rs.next();  // true if record exists

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }
}
