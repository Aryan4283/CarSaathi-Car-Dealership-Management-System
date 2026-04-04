package service;
import db.DatabaseConnection;
import java.sql.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class AnalyticsService {

    // ── FUNCTIONS ─────────────────────────────────────────────────────────────
    public double getTotalSales(int dealerId) {
        String sql = "SELECT getTotalSales(?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, dealerId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getDouble(1);
        } catch (Exception e) { e.printStackTrace(); }
        return 0;
    }

    public int getAvailableStock(int modelId, int dealerId) {
        String sql = "SELECT getAvailableStock(?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, modelId);
            stmt.setInt(2, dealerId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (Exception e) { e.printStackTrace(); }
        return 0;
    }

    // ── PROCEDURES ────────────────────────────────────────────────────────────
    public String getTopSellingModel(int dealerId) {
    String sql = "{CALL getTopSellingModel(?)}";
    try (Connection conn = DatabaseConnection.getConnection();
         CallableStatement stmt = conn.prepareCall(sql)) {
        stmt.setInt(1, dealerId);
        ResultSet rs = stmt.executeQuery();
        if (rs.next())
            return rs.getString("ModelName") + " (" + rs.getInt("SalesCount") + " sold)";
    } catch (Exception e) { e.printStackTrace(); }
    return "No sales yet";
}

public double getMonthlyRevenue(int dealerId, int month, int year) {
    String sql = "{CALL getMonthlyRevenue(?, ?, ?)}";
    try (Connection conn = DatabaseConnection.getConnection();
         CallableStatement stmt = conn.prepareCall(sql)) {
        stmt.setInt(1, dealerId);
        stmt.setInt(2, month);
        stmt.setInt(3, year);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) return rs.getDouble("Revenue");
    } catch (Exception e) { e.printStackTrace(); }
    return 0;
}

    // ── ADMIN ANALYTICS ───────────────────────────────────────────────────────
    public double getTotalSystemRevenue() {
        String sql = "SELECT COALESCE(SUM(FinalPrice), 0) FROM PURCHASE";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getDouble(1);
        } catch (Exception e) { e.printStackTrace(); }
        return 0;
    }

    public String getTopPerformingDealer() {
        String sql =
            "SELECT u.Name, SUM(p.FinalPrice) AS Revenue " +
            "FROM PURCHASE p " +
            "JOIN CARINSTANCE ci ON p.CarID = ci.CarID " +
            "JOIN USER u ON ci.DealerID = u.UserID " +
            "GROUP BY u.Name ORDER BY Revenue DESC LIMIT 1";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            if (rs.next())
                return rs.getString("Name") + " — ₹" + String.format("%.0f", rs.getDouble("Revenue"));
        } catch (Exception e) { e.printStackTrace(); }
        return "No data";
    }

    public String getMostPopularModel() {
        String sql =
            "SELECT cm.ModelName, COUNT(*) AS cnt " +
            "FROM PURCHASE p " +
            "JOIN CARINSTANCE ci ON p.CarID = ci.CarID " +
            "JOIN CARMODEL cm ON ci.ModelID = cm.ModelID " +
            "GROUP BY cm.ModelName ORDER BY cnt DESC LIMIT 1";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            if (rs.next())
                return rs.getString("ModelName") + " (" + rs.getInt("cnt") + " sold)";
        } catch (Exception e) { e.printStackTrace(); }
        return "No data";
    }

    public int getTotalPurchasesCount() {
        String sql = "SELECT COUNT(*) FROM PURCHASE";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (Exception e) { e.printStackTrace(); }
        return 0;
    }

    public int getTotalDealersCount() {
        String sql = "SELECT COUNT(*) FROM DEALER";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (Exception e) { e.printStackTrace(); }
        return 0;
    }
    private static final String[] MONTH_NAMES = {
    "Jan","Feb","Mar","Apr","May","Jun",
    "Jul","Aug","Sep","Oct","Nov","Dec"
};

public Object[][] getMonthlySalesTable(int dealerId, int year) {
    Map<Integer, Double> map = new LinkedHashMap<>();
    for (int i = 1; i <= 12; i++) map.put(i, 0.0);

    String sql = "{CALL getMonthlySales(?, ?)}";
    try (Connection conn = DatabaseConnection.getConnection();
         CallableStatement stmt = conn.prepareCall(sql)) {
        stmt.setInt(1, dealerId);
        stmt.setInt(2, year);
        ResultSet rs = stmt.executeQuery();
        while (rs.next())
            map.put(rs.getInt("Month"), rs.getDouble("Revenue"));
    } catch (Exception e) { e.printStackTrace(); }

    Object[][] data = new Object[12][2];
    for (int i = 1; i <= 12; i++) {
        data[i-1][0] = MONTH_NAMES[i-1];
        data[i-1][1] = map.get(i) == 0 ? "—" : "₹" + String.format("%,.0f", map.get(i));
    }
    return data;
}

public double getYearlyTotal(int dealerId, int year) {
    String sql = "{CALL getYearlySales(?)}";
    try (Connection conn = DatabaseConnection.getConnection();
         CallableStatement stmt = conn.prepareCall(sql)) {
        stmt.setInt(1, dealerId);
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            if (rs.getInt("Year") == year)
                return rs.getDouble("Revenue");
        }
    } catch (Exception e) { e.printStackTrace(); }
    return 0;
}

public Object[][] getSystemMonthlySalesTable(int year) {
    Map<Integer, Double> map = new LinkedHashMap<>();
    for (int i = 1; i <= 12; i++) map.put(i, 0.0);

    String sql = "SELECT MONTH(PurchaseDate) AS Month, SUM(FinalPrice) AS Revenue " +
                 "FROM PURCHASE WHERE YEAR(PurchaseDate) = ? " +
                 "GROUP BY MONTH(PurchaseDate) ORDER BY Month";
    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        stmt.setInt(1, year);
        ResultSet rs = stmt.executeQuery();
        while (rs.next())
            map.put(rs.getInt("Month"), rs.getDouble("Revenue"));
    } catch (Exception e) { e.printStackTrace(); }

    Object[][] data = new Object[12][2];
    for (int i = 1; i <= 12; i++) {
        data[i-1][0] = MONTH_NAMES[i-1];
        data[i-1][1] = map.get(i) == 0 ? "—" : "₹" + String.format("%,.0f", map.get(i));
    }
    return data;
}

}