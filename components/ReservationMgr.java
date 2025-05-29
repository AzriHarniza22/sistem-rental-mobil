package components;

import interfaces.IReservationMgt;
import model.ReservationDetails;
import model.RentalDetails;
import java.util.*;

public class ReservationMgr implements IReservationMgt {
    private Map<String, ReservationDetails> reservations = new HashMap<>();
    private Map<String, List<String>> customerReservations = new HashMap<>();
    private CarMgr carMgr;

    public ReservationMgr(CarMgr carMgr) {
        this.carMgr = carMgr;
    }

    @Override
    public String createReservation(ReservationDetails reservation) {
        String resId = "RES" + new Random().nextInt(10000);
        reservation.reservationId = resId;
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

    // Methods for backward compatibility
    public String makeReservation(RentalDetails res, String custId) {
        ReservationDetails reservation = new ReservationDetails(custId, res.carId, res.dateRange);
        return createReservation(reservation);
    }

    public String startCarRental(String resRef) {
        ReservationDetails res = reservations.get(resRef);
        if (res != null && !"ACTIVE".equals(res.status)) {
            res.status = "ACTIVE";
            return "Plate-" + res.carId;
        }
        return null;
    }

    public boolean endCarRental(String resRef) {
        ReservationDetails res = reservations.get(resRef);
        if (res != null && "ACTIVE".equals(res.status)) {
            res.status = "COMPLETED";
            reservations.remove(resRef);
            customerReservations.values().forEach(list -> list.remove(resRef));
            carMgr.updateCarAvailability(res.carId, true);
            return true;
        }
        return false;
    }

    public boolean extendRental(String resRef, String newEndDate) {
        ReservationDetails res = reservations.get(resRef);
        if (res != null && "ACTIVE".equals(res.status)) {
            res.dateRange.endDate = newEndDate;
            return true;
        }
        return false;
    }

    public String getCustomerForReservation(String resRef) {
        ReservationDetails res = reservations.get(resRef);
        return res != null ? res.customerId : null;
    }
}