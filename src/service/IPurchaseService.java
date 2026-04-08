package service;

import java.util.List;
import exception.InvalidPurchaseException;
public interface IPurchaseService {

    boolean processPurchase(int customerId, int carId, double finalPrice) throws InvalidPurchaseException;

    List<String> getDealerSalesHistory(int dealerId);
}