package service;

import dao.CarInstanceDao;
import dao.PurchaseDao;
import db.DatabaseConnection;
import model.CarInstance;
import model.Purchase;

import java.sql.Connection;
import java.time.LocalDate;
import java.util.List;

public class PurchaseService {

    private PurchaseDao purchaseDao = new PurchaseDao();
    private CarInstanceDao carInstanceDao = new CarInstanceDao();

    public boolean processPurchase(int customerId, int carId, double finalPrice) {

        Connection conn = null;

        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            CarInstance car = carInstanceDao.findById(carId);

            if (car == null) {
                System.out.println("Car not found.");
                return false;
            }

            if (!car.getStatus().equalsIgnoreCase("Available")) {
                System.out.println("Car already sold.");
                return false;
            }

            Purchase purchase = new Purchase(
                    0,
                    customerId,
                    carId,
                    LocalDate.now(),
                    finalPrice
            );

            boolean purchaseInserted = purchaseDao.insertPurchase(purchase, conn);

            if (!purchaseInserted) {
                conn.rollback();
                return false;
            }

            boolean statusUpdated = carInstanceDao.updateStatus(carId, "Sold", conn);

            if (!statusUpdated) {
                conn.rollback();
                return false;
            }

            conn.commit();
            conn.close();
            return true;

        } catch (Exception e) {
            try {
                if (conn != null) conn.rollback();
            } catch (Exception ignored) {}
            e.printStackTrace();
        }

        return false;
    }
    public List<String> getDealerSalesHistory(int dealerId) {
    return purchaseDao.getSalesHistoryByDealer(dealerId);
}
}