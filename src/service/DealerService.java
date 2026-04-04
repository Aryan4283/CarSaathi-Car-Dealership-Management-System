package service;

import dao.DealerDao;
import dao.UserDao;
import model.User;

public class DealerService {

    private DealerDao dealerDao;
    private UserDao userDao;

    public DealerService() {
        dealerDao = new DealerDao();
        userDao = new UserDao();
    }

    public boolean registerDealer(String name, String email,String password, String showroom, String location) {

        try {

            User user = new User();
            user.setName(name);
            user.setEmail(email);
            user.setPassword(password);

            int userId = userDao.insertUser(user);

            if (userId == -1) {
                return false;
            }

            return dealerDao.insertDealer(userId, showroom, location);
            
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
}