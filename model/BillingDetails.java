package model;

public class BillingDetails {
    public String billId;
    public String customerId;
    public String reservationId;
    public double amount;
    public String status; // "PENDING", "PAID", "CANCELLED"
    public String createdDate;
    public String paidDate;

    public BillingDetails(String billId, String customerId, String reservationId, double amount) {
        this.billId = billId;
        this.customerId = customerId;
        this.reservationId = reservationId;
        this.amount = amount;
        this.status = "PENDING";
        this.createdDate = java.time.LocalDate.now().toString();
    }
}