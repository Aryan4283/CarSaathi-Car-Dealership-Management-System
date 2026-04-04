package service;

import dao.CustomerDao;
import dao.UserDao;
import dao.UserPhoneDao;
import model.User;

public class CustomerService {

    private UserDao userDao;
    private CustomerDao customerDao;
    private UserPhoneDao phoneDao;
    public CustomerService() {
        userDao = new UserDao();
        customerDao = new CustomerDao();
        phoneDao = new UserPhoneDao();
    }

    public boolean registerCustomer(
            String name,
            String email,
            String street,
            String city,
            String state,
            String pincode,
            String phone
    ) {

        User user = new User();
    user.setName(name);
    user.setEmail(email);
    user.setPassword("cust123");

    int userId = userDao.insertUser(user);

        if (userId ==-1) {
            return false;
        }
        boolean customerInserted = customerDao.insertCustomer(
                userId,
                street,
                city,
                state,
                pincode
        );

        boolean phoneInserted = phoneDao.addPhone(userId, phone);

        return customerInserted && phoneInserted;
    }
}