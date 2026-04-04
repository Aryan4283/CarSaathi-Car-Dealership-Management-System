package dao;

import db.DatabaseConnection;
import model.CarModel;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CarModelDao {

    public boolean insertCarModel(CarModel carModel) {

        String sql = "INSERT INTO CARMODEL (Brand, ModelName, Category, FuelType, BasePrice) VALUES (?, ?, ?, ?, ?)";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, carModel.getBrand());
            ps.setString(2, carModel.getModelName());
            ps.setString(3, carModel.getCategory());
            ps.setString(4, carModel.getFuelType());
            ps.setDouble(5, carModel.getBasePrice());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public List<CarModel> getAllCarModels() {

        List<CarModel> list = new ArrayList<>();
        String sql = "SELECT * FROM CARMODEL";

        try (Connection con = DatabaseConnection.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {

                CarModel model = new CarModel(
                        rs.getInt("ModelID"),
                        rs.getString("Brand"),
                        rs.getString("ModelName"),
                        rs.getString("Category"),
                        rs.getString("FuelType"),
                        rs.getDouble("BasePrice")
                );

                list.add(model);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public CarModel getCarModelById(int modelId) {

        String sql = "SELECT * FROM CARMODEL WHERE ModelID = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, modelId);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                return new CarModel(
                        rs.getInt("ModelID"),
                        rs.getString("Brand"),
                        rs.getString("ModelName"),
                        rs.getString("Category"),
                        rs.getString("FuelType"),
                        rs.getDouble("BasePrice")
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
    
    public boolean deleteCarModel(int modelId) {

        String sql = "DELETE FROM CARMODEL WHERE ModelID = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, modelId);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }
}