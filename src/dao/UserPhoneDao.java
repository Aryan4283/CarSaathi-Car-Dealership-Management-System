package dao;

import db.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserPhoneDao {

    public boolean addPhone(int userId, String phone) {

        String sql = "INSERT INTO USER_PHONE (UserID, PhoneNumber) VALUES (?, ?)";

        try(Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)){

            stmt.setInt(1, userId);
            stmt.setString(2, phone);

            return stmt.executeUpdate() > 0;

        }catch(Exception e){
            e.printStackTrace();
        }

        return false;
    }

    public int getUserByPhone(String phone){

        String sql = "SELECT UserID FROM USER_PHONE WHERE PhoneNumber = ?";

        try(Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)){

            stmt.setString(1, phone);
            ResultSet rs = stmt.executeQuery();

            if(rs.next()){
                return rs.getInt("UserID");
            }

        }catch(Exception e){
            e.printStackTrace();
        }

        return -1;
    }
    public List<String> getPhonesByUserId(int userId) {
    List<String> phones = new ArrayList<>();
    String sql = "SELECT PhoneNumber FROM USER_PHONE WHERE UserID = ?";
    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        stmt.setInt(1, userId);
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) phones.add(rs.getString("PhoneNumber"));
    } catch (Exception e) { e.printStackTrace(); }
    return phones;
}

public boolean updatePhone(int userId, String oldPhone, String newPhone) {
    String sql = "UPDATE USER_PHONE SET PhoneNumber = ? WHERE UserID = ? AND PhoneNumber = ?";
    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        stmt.setString(1, newPhone);
        stmt.setInt(2, userId);
        stmt.setString(3, oldPhone);
        return stmt.executeUpdate() > 0;
    } catch (Exception e) { e.printStackTrace(); }
    return false;
}
}