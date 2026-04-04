package model;

public class CarInstance {

    private int carId;
    private String vin;
    private String status;
    private int modelId;
    private int dealerId;

    public CarInstance() {}

    public CarInstance(int carId,
                       String vin,
                       String status,
                       int modelId,
                       int dealerId) {

        this.carId = carId;
        this.vin = vin;
        this.status = status;
        this.modelId = modelId;
        this.dealerId = dealerId;
    }

    public int getCarId() {
        return carId;
    }

    public void setCarId(int carId) {
        this.carId = carId;
    }

    public String getVin() {
        return vin;
    }

    public void setVin(String vin) {
        this.vin = vin;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getModelId() {
        return modelId;
    }

    public void setModelId(int modelId) {
        this.modelId = modelId;
    }

    public int getDealerId() {
        return dealerId;
    }

    public void setDealerId(int dealerId) {
        this.dealerId = dealerId;
    }
}