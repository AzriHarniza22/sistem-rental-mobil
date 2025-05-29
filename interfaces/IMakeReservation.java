package interfaces;

import model.DateRange;

public interface IMakeReservation {
    String reserveCar(String customerId, String carId, DateRange dateRange);
}