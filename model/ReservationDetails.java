package model;

public class ReservationDetails {
    public String reservationId;
    public String customerId;
    public String carId;
    public DateRange dateRange;
    public double totalCost;
    public String status; // "PENDING", "CONFIRMED", "ACTIVE", "COMPLETED", "CANCELLED"

    public ReservationDetails(String customerId, String carId, DateRange dateRange) {
        this.customerId = customerId;
        this.carId = carId;
        this.dateRange = dateRange;
        this.status = "PENDING";
    }

    public ReservationDetails(String reservationId, String customerId, String carId, DateRange dateRange, double totalCost) {
        this.reservationId = reservationId;
        this.customerId = customerId;
        this.carId = carId;
        this.dateRange = dateRange;
        this.totalCost = totalCost;
        this.status = "CONFIRMED";
    }
}