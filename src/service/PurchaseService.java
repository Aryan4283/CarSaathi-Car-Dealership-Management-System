package service;

import dao.CarInstanceDao;
import dao.PurchaseDao;
import db.DatabaseConnection;
import model.CarInstance;
import model.Purchase;

import java.sql.Connection;
import java.time.LocalDate;
import java.util.List;
import exception.InvalidPurchaseException;

public class PurchaseService implements IPurchaseService {

    private PurchaseDao purchaseDao = new PurchaseDao();
    private CarInstanceDao carInstanceDao = new CarInstanceDao();

    public boolean processPurchase(int customerId, int carId, double finalPrice) throws InvalidPurchaseException {

        Connection conn = null;

        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            CarInstance car = carInstanceDao.findById(carId);

            if (car == null) {
                throw new InvalidPurchaseException("Car not found");
            }

            if (!car.getStatus().equalsIgnoreCase("Available")) {
                throw new InvalidPurchaseException("Car already sold");
            }

            Purchase purchase = new Purchase(
                    0,
                    customerId,
                    carId,
                    LocalDate.now(),
                    finalPrice);

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
                if (conn != null)
                    conn.rollback();
            } catch (Exception ignored) {
            }
            e.printStackTrace();
        }

        return false;
    }

    public List<String> getDealerSalesHistory(int dealerId) {
        return purchaseDao.getSalesHistoryByDealer(dealerId);
    }
}