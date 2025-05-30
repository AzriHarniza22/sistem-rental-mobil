package components;

import interfaces.IRental;
import model.CarDetails;
import model.DateRange;
import model.ReservationDetails;
import java.util.*;

public class RentalSystem implements IRental {
    private ReservationMgr reservationMgr;
    private CarMgr carMgr;
    private Map<String, String> activeRentals = new HashMap<>(); // reservationId -> plateNumber

    public RentalSystem(ReservationMgr reservationMgr, CarMgr carMgr) {
        this.reservationMgr = reservationMgr;
        this.carMgr = carMgr;
    }

    @Override
    public String startRental(String reservationId) {
        ReservationDetails reservation = reservationMgr.getReservation(reservationId);
        if (reservation != null && "CONFIRMED".equals(reservation.status)) {
            reservation.status = "ACTIVE";
            carMgr.updateCarAvailability(reservation.carId, false);
            String plateNumber = "Plate-" + reservation.carId;
            activeRentals.put(reservationId, plateNumber);
            return plateNumber;
        }
        return null;
    }

    @Override
    public boolean endRental(String reservationId) {
        ReservationDetails reservation = reservationMgr.getReservation(reservationId);
        if (reservation != null && "ACTIVE".equals(reservation.status)) {
            reservation.status = "COMPLETED";
            carMgr.updateCarAvailability(reservation.carId, true);
            activeRentals.remove(reservationId);
            return true;
        }
        return false;
    }

    @Override
    public boolean extendRental(String reservationId, String newEndDate) {
        ReservationDetails reservation = reservationMgr.getReservation(reservationId);
        if (reservation != null && "ACTIVE".equals(reservation.status)) {
            reservation.dateRange.endDate = newEndDate;
            return true;
        }
        return false;
    }

    @Override
    public boolean checkRentalStatus(String reservationId) {
        ReservationDetails reservation = reservationMgr.getReservation(reservationId);
        return reservation != null && "ACTIVE".equals(reservation.status);
    }

    @Override
    public double calculateRentalCost(String reservationId, DateRange extension) {
        ReservationDetails reservation = reservationMgr.getReservation(reservationId);
        if (reservation != null) {
            CarDetails car = carMgr.getCarInfo(reservation.carId);
            if (car != null) {
                return car.price * (extension.getDays() - 1);
            }
        }
        return 0.0;
    }

    @Override
    public boolean updateRentalStatus(String reservationId, String status) {
        ReservationDetails reservation = reservationMgr.getReservation(reservationId);
        if (reservation != null) {
            reservation.status = status;
            return true;
        }
        return false;
    }

    @Override
    public boolean processRentalRequest(String reservationId, String action) {
        switch (action.toLowerCase()) {
            case "start":
                return startRental(reservationId) != null;
            case "end":
                return endRental(reservationId);
            default:
                return false;
        }
    }

    // Legacy methods for backward compatibility
    public String startCarRental(String resRef) {
        return startRental(resRef);
    }

    public boolean endCarRental(String resRef) {
        return endRental(resRef);
    }
}