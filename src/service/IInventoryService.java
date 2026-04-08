package service;

import model.CarInstance;
import java.util.List;

public interface IInventoryService {

    List<Object[]> getAvailableCarsByDealer(int dealerId);

    CarInstance getCarById(int carId);

    boolean addCarToInventory(String vin, int modelId, int dealerId);

    List<CarInstance> getAllCarsByDealer(int dealerId);

    List<Object[]> getModelInventory(int dealerId);

    int getAvailableCarId(int modelId, int dealerId);
}