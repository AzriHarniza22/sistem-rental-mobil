package components;

import interfaces.ICarMgt;
import model.*;
import java.util.*;

public class CarMgr implements ICarMgt {
    private List<CarDetails> cars = new ArrayList<>();

    public CarMgr() {
        cars.add(new CarDetails("CAR001", "Toyota Avanza", "MPV", true, 350000));
        cars.add(new CarDetails("CAR002", "Honda Brio", "Hatchback", true, 250000));
    }

    public CarDetails[] getAvailableCars(String match) {
        return cars.stream()
            .filter(car -> car.available && car.model.toLowerCase().contains(match.toLowerCase()))
            .toArray(CarDetails[]::new);
    }

    public CarDetails getCarInfo(String carId) {
        return cars.stream().filter(car -> car.carId.equals(carId)).findFirst().orElse(null);
    }

    @Override
    public boolean updateCarAvailability(String carId, boolean available) {
        for (CarDetails car : cars) {
            if (car.carId.equals(carId)) {
                car.available = available;
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean addCar(CarDetails car) {
        return cars.add(car);
    }

    @Override
    public boolean editCar(String carId, String model, String type, Double price) {
        for (CarDetails car : cars) {
            if (car.carId.equals(carId)) {
                if (model != null) car.model = model;
                if (type != null) car.type = type;
                if (price != null) car.price = price;
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean deleteCar(String carId) {
        return cars.removeIf(car -> car.carId.equals(carId));
    }

    @Override
    public List<CarDetails> listCars() {
        return cars;
    }
}