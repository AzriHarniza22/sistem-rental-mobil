package components;

import interfaces.*;
import model.*;
import java.util.Random;

public class ReservationSystem implements IReservationSystem {
    private CarMgr carMgr;
    private ReservationMgr reservationMgr;

    public ReservationSystem(CarMgr carMgr, ReservationMgr reservationMgr) {
        this.carMgr = carMgr;
        this.reservationMgr = reservationMgr;
    }

    @Override
    public boolean checkReservationAvailability(String carId, DateRange dateRange) {
        CarDetails car = carMgr.getCarInfo(carId);
        return car != null && car.available;
    }

    @Override
    public boolean validateReservationData(ReservationDetails reservation) {
        return reservation != null && reservation.customerId != null 
               && reservation.carId != null && reservation.dateRange != null;
    }

    @Override
    public String createReservationRequest(ReservationDetails reservation) {
        if (validateReservationData(reservation)) {
            reservation.totalCost = calculateReservationCost(reservation.carId, reservation.dateRange);
            return reservationMgr.createReservation(reservation);
        }
        return null;
    }

    @Override
    public String generateReservationId() {
        return "RES" + String.format("%04d", new Random().nextInt(10000));
    }

    @Override
    public double calculateReservationCost(String carId, DateRange dateRange) {
        CarDetails car = carMgr.getCarInfo(carId);
        if (car != null && dateRange != null) {
            return car.price * dateRange.getDays();
        }
        return 0.0;
    }
}