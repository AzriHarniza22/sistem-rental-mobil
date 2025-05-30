package interfaces;

import model.DateRange;

public interface IBilling {
    boolean validateBillingData(String customerId, double amount);
    String createBillRequest(String customerId, String reservationId, double amount);
    boolean processBillPayment(String billId);
    double calculateAdditionalCharges(String reservationId, DateRange extension);
}