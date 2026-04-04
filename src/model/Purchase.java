package model;

import java.time.LocalDate;

public class Purchase {
    private int id;
    private int customerId;
    private int carInstanceId;
    private LocalDate purchaseDate;
    private double totalPrice;

    public Purchase() {}

    public Purchase(int id, int customerId, int carInstanceId, LocalDate purchaseDate, double totalPrice) {
        this.id = id;
        this.customerId = customerId;
        this.carInstanceId = carInstanceId;
        this.purchaseDate = purchaseDate;
        this.totalPrice = totalPrice;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public int getCarInstanceId() {
        return carInstanceId;
    }

    public void setCarInstanceId(int carInstanceId) {
        this.carInstanceId = carInstanceId;
    }

    public LocalDate getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(LocalDate purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }
}
