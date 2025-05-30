package components;

import interfaces.ICarMgt;
import model.*;
import java.util.*;

public class CarMgr implements ICarMgt {
    private List<CarDetails> cars = new ArrayList<>();

    public CarMgr() {
        // Updated with valid car types: sedan, suv, pickup
        cars.add(new CarDetails("CAR001", "Toyota Avanza", "suv", true, 350000));
        cars.add(new CarDetails("CAR002", "Honda Brio", "sedan", true, 250000));
        cars.add(new CarDetails("CAR003", "Ford Ranger", "pickup", true, 450000));
        cars.add(new CarDetails("CAR004", "Honda CR-V", "suv", true, 400000));
        cars.add(new CarDetails("CAR005", "Toyota Camry", "sedan", true, 380000));
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
        // Check if car ID already exists
        if (getCarInfo(car.carId) != null) {
            return false;
        }
        return cars.add(car);
    }

    @Override
    public boolean editCar(String carId, String model, String type, Double price) {
        for (CarDetails car : cars) {
            if (car.carId.equals(carId)) {
                if (model != null && !model.trim().isEmpty()) car.model = model;
                if (type != null && !type.trim().isEmpty()) car.type = type;
                if (price != null && price > 0) car.price = price;
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