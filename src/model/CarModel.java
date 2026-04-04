package model;

public class CarModel {

    private int modelId;
    private String brand;
    private String modelName;
    private String category;
    private String fuelType;
    private double basePrice;

    public CarModel() {
    }

    public CarModel(String brand, String modelName, String category, String fuelType, double basePrice) {
        this.brand = brand;
        this.modelName = modelName;
        this.category = category;
        this.fuelType = fuelType;
        this.basePrice = basePrice;
    }

    public CarModel(int modelId, String brand, String modelName, String category, String fuelType, double basePrice) {
        this.modelId = modelId;
        this.brand = brand;
        this.modelName = modelName;
        this.category = category;
        this.fuelType = fuelType;
        this.basePrice = basePrice;
    }

    public int getModelId() {
        return modelId;
    }

    public String getBrand() {
        return brand;
    }

    public String getModelName() {
        return modelName;
    }

    public String getCategory() {
        return category;
    }

    public String getFuelType() {
        return fuelType;
    }

    public double getBasePrice() {
        return basePrice;
    }

    public void setModelId(int modelId) {
        this.modelId = modelId;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setFuelType(String fuelType) {
        this.fuelType = fuelType;
    }

    public void setBasePrice(double basePrice) {
        this.basePrice = basePrice;
    }
}