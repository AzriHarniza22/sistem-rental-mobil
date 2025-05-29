package interfaces;

import model.ReservationDetails;
import java.util.List;

public interface IReservationMgt {
    String createReservation(ReservationDetails reservation);
    ReservationDetails getReservation(String reservationId);
    boolean updateReservation(String reservationId, ReservationDetails details);
    boolean cancelReservation(String reservationId);
    List<ReservationDetails> getCustomerReservations(String customerId);
}