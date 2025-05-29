package model;

public class CarDetails {
    public String carId;
    public String model;
    public String type;
    public boolean available;
    public double price;

    public CarDetails(String carId, String model, String type, boolean available, double price) {
        this.carId = carId;
        this.model = model;
        this.type = type;
        this.available = available;
        this.price = price;
    }
}
