package interfaces;

import model.ReservationDetails;
import model.DateRange;

public interface IReservationSystem {
    boolean checkReservationAvailability(String carId, DateRange dateRange);
    boolean validateReservationData(ReservationDetails reservation);
    String createReservationRequest(ReservationDetails reservation);
    String generateReservationId();
    double calculateReservationCost(String carId, DateRange dateRange);
}