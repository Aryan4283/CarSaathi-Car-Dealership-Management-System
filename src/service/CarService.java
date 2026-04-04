package service;

import dao.CarModelDao;
import model.CarModel;

import java.util.List;

public class CarService {

    private CarModelDao carModelDao;

    public CarService() {
        this.carModelDao = new CarModelDao();
    }

    public boolean addCarModel(String brand,
                               String modelName,
                               String category,
                               String fuelType,
                               double basePrice) {

        CarModel carModel = new CarModel(
                brand,
                modelName,
                category,
                fuelType,
                basePrice
        );

        return carModelDao.insertCarModel(carModel);
    }

    public List<CarModel> getAllCarModels() {
        return carModelDao.getAllCarModels();
    }

    public CarModel getCarModelById(int modelId) {
        return carModelDao.getCarModelById(modelId);
    }

    public boolean deleteCarModel(int modelId) {
        return carModelDao.deleteCarModel(modelId);
    }
}