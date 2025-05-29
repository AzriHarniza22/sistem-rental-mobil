package model;

public class RentalDetails {
    public String carId;
    public DateRange dateRange;
    public boolean claimed = false;    // Mobil sudah mulai disewa
    public boolean completed = false;  // Sewa sudah selesai

    public RentalDetails(String carId, DateRange dateRange) {
        this.carId = carId;
        this.dateRange = dateRange;
    }
}