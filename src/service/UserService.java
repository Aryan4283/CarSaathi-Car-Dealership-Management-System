package service;

import dao.UserDao;
import db.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import dao.AdminDao;
import dao.DealerDao;
import dao.CustomerDao;
import model.User;

public class UserService {

    private UserDao userDao;
    private AdminDao adminDao;
    private DealerDao dealerDao;
    private CustomerDao customerDao;

    public UserService() {
        this.userDao = new UserDao();
        this.adminDao = new AdminDao();
        this.dealerDao = new DealerDao();
        this.customerDao = new CustomerDao();
    }

    public boolean registerUser(String name, String email, String password) {

        User existingUser = userDao.getUserByEmail(email);
        if (existingUser != null) {
            return false;
        }

        User newUser = new User(name, email, password);
        int userId=userDao.insertUser(newUser);
        return userId != -1;
    }

    public User loginUser(String email, String password) {

        User user = userDao.getUserByEmail(email);

        if (user == null) {
            return null;
        }

        if (!user.getPassword().equals(password)) {
            return null;
        }

        return user;
    }

    public String getUserRole(int userId) {

        if (adminDao.isAdmin(userId)) {
            return "ADMIN";
        }
        else if (dealerDao.isDealer(userId)) {
            return "DEALER";
        }
        else if (customerDao.isCustomer(userId)) {
            return "CUSTOMER";
        }
        else {
            return "UNKNOWN";
        }
    }

    public String getAdminAccessScope(int userId) {
    String sql = "SELECT AccessScope FROM ADMIN WHERE UserID = ?";
    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        stmt.setInt(1, userId);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) return rs.getString("AccessScope");
    } catch (Exception e) { e.printStackTrace(); }
    return "LIMITED_ACCESS"; 
}
}