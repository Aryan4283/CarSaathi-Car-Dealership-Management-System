package dao;

import db.DatabaseConnection;
import java.sql.*;
import java.util.*;

public class BookingDao {

    public boolean addBooking(int customerId, int modelId, int dealerId, String status){

        String sql = "INSERT INTO BOOKING (CustomerID, ModelID, DealerID, Status) VALUES (?, ?, ?, ?)";

        try(Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)){

            stmt.setInt(1, customerId);
            stmt.setInt(2, modelId);
            stmt.setInt(3, dealerId);
            stmt.setString(4, status);

            return stmt.executeUpdate() > 0;

        }catch(Exception e){
            e.printStackTrace();
        }

        return false;
    }

    public List<Object[]> getBookings(int dealerId){

        List<Object[]> list = new ArrayList<>();

        String sql =
    "SELECT b.BookingID, u.Name, b.CustomerID, b.ModelID AS ModelID, cm.ModelName, b.Status, up.PhoneNumber " +
    "FROM BOOKING b " +
    "JOIN USER u ON b.CustomerID = u.UserID " +
    "JOIN CARMODEL cm ON b.ModelID = cm.ModelID " +
    "LEFT JOIN USER_PHONE up ON u.UserID = up.UserID " +
    "WHERE b.DealerID = ?";

        try(Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)){

            stmt.setInt(1, dealerId);
            ResultSet rs = stmt.executeQuery();

            while(rs.next()){
                list.add(new Object[]{
                    rs.getInt("BookingID"),
                    rs.getString("Name"),
                    rs.getInt("CustomerID"),
                    rs.getInt("ModelID"),
                    rs.getString("ModelName"),
                    rs.getString("Status"),
                    rs.getString("PhoneNumber")
                });
            }

        }catch(Exception e){
            e.printStackTrace();
        }

        return list;
    }

    public boolean deleteBooking(int bookingId){

        String sql = "DELETE FROM BOOKING WHERE BookingID = ?";

        try(Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)){

            stmt.setInt(1, bookingId);
            return stmt.executeUpdate() > 0;

        }catch(Exception e){
            e.printStackTrace();
        }

        return false;
    }
    public boolean updateBookingStatus(int modelId, int dealerId) {

    String sql = "UPDATE BOOKING SET Status = 'Available' " +
                 "WHERE ModelID = ? AND DealerID = ?";
    
    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setInt(1, modelId);
        stmt.setInt(2, dealerId);

        return stmt.executeUpdate() > 0;

    } catch (SQLException e) {
        e.printStackTrace();
    }

    return false;
}
public List<String> getBookingsByCustomer(int customerId){

    List<String> list = new ArrayList<>();

    String sql =
        "SELECT BookingID, ModelID, Status " +
        "FROM BOOKING WHERE CustomerID = ?";

    try(Connection conn = DatabaseConnection.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)){

        stmt.setInt(1, customerId);
        ResultSet rs = stmt.executeQuery();

        while(rs.next()){
            list.add(
                "BookingID: " + rs.getInt("BookingID") +
                " | Model: " + rs.getInt("ModelID") +
                " | Status: " + rs.getString("Status")
            );
        }

    }catch(Exception e){
        e.printStackTrace();
    }

    return list;
}
}