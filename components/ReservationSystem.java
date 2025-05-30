package components;

import interfaces.IReservationMgt;
import model.ReservationDetails;
import model.RentalDetails;
import model.CarDetails;
import model.DateRange;
import java.util.*;
import java.time.LocalDate;

public class ReservationSystem implements IReservationMgt {
    Map<String, ReservationDetails> reservations = new HashMap<>();
    private Map<String, List<String>> customerReservations = new HashMap<>();
    private CarMgr carMgr;

    public ReservationSystem(CarMgr carMgr) {
        this.carMgr = carMgr;
    }

    @Override
    public String createReservation(ReservationDetails reservation) {
        String resId = "RES" + new Random().nextInt(10000);
        reservation.reservationId = resId;
        reservation.status = "CONFIRMED";
        reservations.put(resId, reservation);
        
        // Track customer reservations
        customerReservations.computeIfAbsent(reservation.customerId, k -> new ArrayList<>()).add(resId);
        
        return resId;
    }

    @Override
    public ReservationDetails getReservation(String reservationId) {
        return reservations.get(reservationId);
    }

    @Override
    public boolean updateReservation(String reservationId, ReservationDetails details) {
        if (reservations.containsKey(reservationId)) {
            details.reservationId = reservationId;
            reservations.put(reservationId, details);
            return true;
        }
        return false;
    }

    @Override
    public boolean cancelReservation(String reservationId) {
        ReservationDetails reservation = reservations.get(reservationId);
        if (reservation != null) {
            reservation.status = "CANCELLED";
            return true;
        }
        return false;
    }

    @Override
    public List<ReservationDetails> getCustomerReservations(String customerId) {
        List<String> resIds = customerReservations.get(customerId);
        if (resIds == null) return new ArrayList<>();
        
        List<ReservationDetails> results = new ArrayList<>();
        for (String resId : resIds) {
            ReservationDetails res = reservations.get(resId);
            if (res != null) {
                results.add(res);
            }
        }
        return results;
    }

    // Reservation availability check
    public boolean checkReservationAvailability(String carId, DateRange dateRange) {
        for (ReservationDetails reservation : reservations.values()) {
            if (reservation.carId.equals(carId) && 
                !reservation.status.equals("CANCELLED") && 
                !reservation.status.equals("COMPLETED")) {
                
                if (isDateRangeOverlap(dateRange, reservation.dateRange)) {
                    return false;
                }
            }
        }
        return true;
    }

    // Get reserved dates for a car
    public List<DateRange> getReservedDates(String carId) {
        List<DateRange> reservedDates = new ArrayList<>();
        
        for (ReservationDetails reservation : reservations.values()) {
            if (reservation.carId.equals(carId) && 
                !reservation.status.equals("CANCELLED") && 
                !reservation.status.equals("COMPLETED")) {
                reservedDates.add(reservation.dateRange);
            }
        }
        
        return reservedDates;
    }

    // Calculate reservation cost
    public double calculateReservationCost(String carId, DateRange dateRange) {
        CarDetails car = carMgr.getCarInfo(carId);
        if (car != null && dateRange != null) {
            return car.price * dateRange.getDays();
        }
        return 0.0;
    }

    // Helper method for date overlap check
    private boolean isDateRangeOverlap(DateRange range1, DateRange range2) {
        try {
            LocalDate start1 = LocalDate.parse(range1.startDate);
            LocalDate end1 = LocalDate.parse(range1.endDate);
            LocalDate start2 = LocalDate.parse(range2.startDate);
            LocalDate end2 = LocalDate.parse(range2.endDate);
            
            return !(end1.isBefore(start2) || end2.isBefore(start1));
        } catch (Exception e) {
            return true;
        }
    }

    // Methods for backward compatibility
    public String makeReservation(RentalDetails res, String custId) {
        ReservationDetails reservation = new ReservationDetails(custId, res.carId, res.dateRange);
        reservation.totalCost = calculateReservationCost(res.carId, res.dateRange);
        return createReservation(reservation);
    }

    public String getCustomerForReservation(String resRef) {
        ReservationDetails res = reservations.get(resRef);
        return res != null ? res.customerId : null;
    }
}