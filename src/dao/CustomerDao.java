package dao;

import db.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CustomerDao {

    public boolean isCustomer(int userId) {

        String sql = "SELECT * FROM CUSTOMER WHERE UserID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);

            ResultSet rs = stmt.executeQuery();

            return rs.next();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean insertCustomer(
            int userId,
            String street,
            String city,
            String state,
            String pincode
    ) {

        String sql =
                "INSERT INTO CUSTOMER (UserID, Street, City, State, Pincode) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.setString(2, street);
            stmt.setString(3, city);
            stmt.setString(4, state);
            stmt.setString(5, pincode);

            int rows = stmt.executeUpdate();

            return rows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }
    public String getCustomerDetails(int userId){

    String sql =
        "SELECT u.Name, u.Email, c.Street, c.City, c.State, c.Pincode " +
        "FROM USER u JOIN CUSTOMER c ON u.UserID = c.UserID " +
        "WHERE u.UserID = ?";

    try(Connection conn = DatabaseConnection.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)){

        stmt.setInt(1, userId);
        ResultSet rs = stmt.executeQuery();

        if(rs.next()){
            return "Name: " + rs.getString("Name") +
                   "\nEmail: " + rs.getString("Email") +
                   "\nAddress: " +
                   rs.getString("Street") + ", " +
                   rs.getString("City") + ", " +
                   rs.getString("State") + " - " +
                   rs.getString("Pincode");
        }

    }catch(Exception e){
        e.printStackTrace();
    }

    return "Customer not found";
}
}