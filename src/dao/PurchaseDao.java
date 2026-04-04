package dao;

import model.Purchase;
import db.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PurchaseDao {

    public boolean insertPurchase(Purchase purchase, Connection conn) throws SQLException {

        String sql = "INSERT INTO PURCHASE (PurchaseDate, FinalPrice, CarID, CustomerID) VALUES (?, ?, ?, ?)";

        PreparedStatement stmt = conn.prepareStatement(sql);

        stmt.setDate(1, java.sql.Date.valueOf(purchase.getPurchaseDate()));
        stmt.setDouble(2, purchase.getTotalPrice());
        stmt.setInt(3, purchase.getCarInstanceId());
        stmt.setInt(4, purchase.getCustomerId());

        return stmt.executeUpdate() > 0;
    }

    public List<String> getSalesHistoryByDealer(int dealerId) {

        List<String> sales = new ArrayList<>();

        String sql =
                "SELECT p.PurchaseID, u.Name AS CustomerName, c.VIN, p.FinalPrice, p.PurchaseDate " +
                "FROM PURCHASE p " +
                "JOIN USER u ON p.CustomerID = u.UserID " +
                "JOIN CARINSTANCE c ON p.CarID = c.CarID " +
                "WHERE c.DealerID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, dealerId);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {

                String row =
                        "PurchaseID: " + rs.getInt("PurchaseID") +
                        " | Customer: " + rs.getString("CustomerName") +
                        " | VIN: " + rs.getString("VIN") +
                        " | Price: " + rs.getDouble("FinalPrice") +
                        " | Date: " + rs.getDate("PurchaseDate");

                sales.add(row);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return sales;
    }

    public List<String> getAllPurchases() {

        List<String> list = new ArrayList<>();

        String sql =
                "SELECT p.PurchaseID, p.PurchaseDate, p.FinalPrice, p.CarID, p.CustomerID " +
                "FROM PURCHASE p";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {

                String row =
                        "PurchaseID: " + rs.getInt("PurchaseID") +
                        " | CarID: " + rs.getInt("CarID") +
                        " | CustomerID: " + rs.getInt("CustomerID") +
                        " | Price: " + rs.getDouble("FinalPrice") +
                        " | Date: " + rs.getDate("PurchaseDate");

                list.add(row);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }
    public List<String> getPurchasesByCustomer(int customerId){

    List<String> list = new ArrayList<>();

    String sql =
        "SELECT PurchaseID, FinalPrice, PurchaseDate " +
        "FROM PURCHASE WHERE CustomerID = ?";

    try(Connection conn = DatabaseConnection.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)){

        stmt.setInt(1, customerId);
        ResultSet rs = stmt.executeQuery();

        while(rs.next()){
            list.add(
                "ID: " + rs.getInt("PurchaseID") +
                " | Price: " + rs.getDouble("FinalPrice") +
                " | Date: " + rs.getDate("PurchaseDate")
            );
        }

    }catch(Exception e){
        e.printStackTrace();
    }

    return list;
}
}