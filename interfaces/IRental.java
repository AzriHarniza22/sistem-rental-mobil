package interfaces;

import model.DateRange;

public interface IRental {
    boolean checkRentalStatus(String reservationId);
    double calculateRentalCost(String reservationId, DateRange extension);
    boolean updateRentalStatus(String reservationId, String status);
    boolean processRentalRequest(String reservationId, String action);
    String startRental(String reservationId);
    boolean endRental(String reservationId);
    boolean extendRental(String reservationId, String newEndDate);
}