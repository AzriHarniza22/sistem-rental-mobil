package interfaces;

import model.CarDetails;
import model.DateRange;

public interface ICarSystem {
    boolean checkCarAvailability(String carId, DateRange dateRange);
    boolean validateCarData(CarDetails car);
    String createCarRequest(CarDetails car);
    String generateCarId();
    boolean updateCarAvailability(String carId, boolean available);
}
