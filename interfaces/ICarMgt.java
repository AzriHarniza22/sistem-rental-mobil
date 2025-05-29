package interfaces;

import model.CarDetails;
import java.util.List;

public interface ICarMgt {
    CarDetails[] getAvailableCars(String filter);
    CarDetails getCarInfo(String carId);
    boolean addCar(CarDetails car);
    boolean editCar(String carId, String model, String type, Double price);
    boolean deleteCar(String carId);
    List<CarDetails> listCars();
    boolean updateCarAvailability(String carId, boolean available); // Sudah ada, tetap dipertahankan
}