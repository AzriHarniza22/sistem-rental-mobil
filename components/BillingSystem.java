package components;

import interfaces.IBillingSystem;
import model.*;
import java.util.Random;
import java.util.HashMap;
import java.util.Map;

public class BillingSystem implements IBillingSystem {
    private CarMgr carMgr;
    private ReservationMgr reservationMgr;
    private Map<String, BillingDetails> bills = new HashMap<>();

    public BillingSystem() {
        // Default constructor untuk backward compatibility
    }

    public BillingSystem(CarMgr carMgr, ReservationMgr reservationMgr) {
        this.carMgr = carMgr;
        this.reservationMgr = reservationMgr;
    }

    @Override
    public boolean validateBillingData(String customerId, double amount) {
        return customerId != null && !customerId.trim().isEmpty() && amount > 0;
    }

    @Override
    public String createBillRequest(String customerId, String reservationId, double amount) {
        if (validateBillingData(customerId, amount)) {
            String billId = "BILL" + new Random().nextInt(10000);
            BillingDetails bill = new BillingDetails(billId, customerId, reservationId, amount);
            bills.put(billId, bill);
            System.out.println("Tagihan " + billId + " untuk " + customerId + 
                             ": Rp" + amount + " untuk reservasi " + reservationId);
            return billId;
        }
        return null;
    }

    @Override
    public boolean processBillPayment(String billId) {
        BillingDetails bill = bills.get(billId);
        if (bill != null && "PENDING".equals(bill.status)) {
            bill.status = "PAID";
            bill.paidDate = java.time.LocalDate.now().toString();
            System.out.println("Pembayaran untuk tagihan " + billId + " berhasil diproses.");
            return true;
        }
        return false;
    }

    @Override
    public double calculateAdditionalCharges(String reservationId, DateRange extension) {
        if (reservationMgr != null && carMgr != null) {
            ReservationDetails reservation = reservationMgr.getReservation(reservationId);
            if (reservation != null) {
                CarDetails car = carMgr.getCarInfo(reservation.carId);
                if (car != null) {
                    return car.price * (extension.getDays() - 1);
                }
            }
        }
        return 0.0;
    }

    public BillingDetails getBill(String billId) {
        return bills.get(billId);
    }

    // Method untuk backward compatibility
    public boolean generateBill(String customerId, String resRef, double amount) {
        return createBillRequest(customerId, resRef, amount) != null;
    }
}