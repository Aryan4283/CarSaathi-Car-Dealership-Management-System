package service;

import dao.CarInstanceDao;
import dao.CarModelDao;
import model.CarInstance;
import model.CarModel;

import java.util.ArrayList;
import java.util.List;

public class InventoryService implements IInventoryService {

    private CarInstanceDao carInstanceDao;
    private CarModelDao    carModelDao;
    public InventoryService() {
        this.carInstanceDao = new CarInstanceDao();
        this.carModelDao = new CarModelDao();
    }

    public List<Object[]> getAvailableCarsByDealer(int dealerId) {
    return carInstanceDao.getAvailableCarsByDealer(dealerId);
}

    public CarInstance getCarById(int carId) {
        return carInstanceDao.findById(carId);
    }
    public boolean addCarToInventory(String vin, int modelId, int dealerId) {
    return carInstanceDao.insertCarInstance(vin, modelId, dealerId);
}
    public List<CarInstance> getAllCarsByDealer(int dealerId) {
    return carInstanceDao.getAllCarsByDealer(dealerId);
}
    public List<Object[]> getModelInventory(int dealerId) {
    List<Object[]> list = new ArrayList<>();
    List<CarModel> models = carModelDao.getAllCarModels();
    for (CarModel model : models) {
        int total     = carInstanceDao.countTotalCars(model.getModelId(), dealerId);
        int available = carInstanceDao.countAvailableCars(model.getModelId(), dealerId);
        String status = (available > 0) ? "Available" : "Unavailable";
        list.add(new Object[]{
            model.getModelId(),
            model.getModelName(),
            total,
            available,
            status
        });
    }
    return list;
}
    public int getAvailableCarId(int modelId, int dealerId) {
    return carInstanceDao.getAvailableCarId(modelId, dealerId);
}
}