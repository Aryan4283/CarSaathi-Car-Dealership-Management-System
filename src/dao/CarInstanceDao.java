package dao;

import db.DatabaseConnection;
import model.CarInstance;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CarInstanceDao {

    public CarInstance findById(int carId) {

        String sql = "SELECT * FROM CARINSTANCE WHERE CarID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, carId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new CarInstance(
                        rs.getInt("CarID"),
                        rs.getString("VIN"),
                        rs.getString("Status"),
                        rs.getInt("ModelID"),
                        rs.getInt("DealerID")
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public List<Object[]> getAvailableCarsByDealer(int dealerId) {
    List<Object[]> cars = new ArrayList<>();
    String sql = "SELECT ci.CarID, ci.VIN, ci.ModelID, cm.ModelName " +
                 "FROM CARINSTANCE ci " +
                 "JOIN CARMODEL cm ON ci.ModelID = cm.ModelID " +
                 "WHERE ci.Status = 'Available' AND ci.DealerID = ?";
    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        stmt.setInt(1, dealerId);
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            cars.add(new Object[]{
                rs.getInt("CarID"),
                rs.getString("VIN"),
                rs.getInt("ModelID"),
                rs.getString("ModelName")
            });
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return cars;
    }

    public boolean updateStatus(int carId, String status, Connection conn) throws SQLException {

        String sql = "UPDATE CARINSTANCE SET Status = ? WHERE CarID = ?";

        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, status);
        stmt.setInt(2, carId);

        return stmt.executeUpdate() > 0;
    }
    public List<CarInstance> getAllCarsByDealer(int dealerId) {

    List<CarInstance> cars = new ArrayList<>();

    String sql = "SELECT * FROM CARINSTANCE WHERE DealerID = ? AND Status = 'Available";

    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setInt(1, dealerId);
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            cars.add(new CarInstance(
                    rs.getInt("CarID"),
                    rs.getString("VIN"),
                    rs.getString("Status"),
                    rs.getInt("ModelID"),
                    rs.getInt("DealerID")
            ));
        }

    } catch (SQLException e) {
        e.printStackTrace();
    }

    return cars;
}
public int countTotalCars(int modelId, int dealerId) {
    String sql = "SELECT COUNT(*) FROM CARINSTANCE WHERE ModelID = ? AND DealerID = ?";
    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        stmt.setInt(1, modelId);
        stmt.setInt(2, dealerId);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) return rs.getInt(1);
    } catch (Exception e) { e.printStackTrace(); }
    return 0;
}

public int countAvailableCars(int modelId, int dealerId) {
    String sql = "SELECT COUNT(*) FROM CARINSTANCE WHERE ModelID = ? AND DealerID = ? AND Status = 'Available'";
    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        stmt.setInt(1, modelId);
        stmt.setInt(2, dealerId);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) return rs.getInt(1);
    } catch (Exception e) { e.printStackTrace(); }
    return 0;
}

    public boolean insertCarInstance(String vin, int modelId, int dealerId) {

    String sql = "INSERT INTO CARINSTANCE (VIN, Status, ModelID, DealerID) VALUES (?, 'Available', ?, ?)";

    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setString(1, vin);
        stmt.setInt(2, modelId);
        stmt.setInt(3, dealerId);

        return stmt.executeUpdate() > 0;

    } catch (SQLException e) {
        e.printStackTrace();
    }

    return false;
}
    public int getAvailableCarId(int modelId, int dealerId) {
    String sql = "SELECT CarID FROM CARINSTANCE WHERE ModelID = ? AND DealerID = ? AND Status = 'Available' LIMIT 1";

    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setInt(1, modelId);
        stmt.setInt(2, dealerId);

        ResultSet rs = stmt.executeQuery();

        if (rs.next()) {
            return rs.getInt("CarID");
        }

    } catch (Exception e) {
        e.printStackTrace();
    }

    return -1;
}
}