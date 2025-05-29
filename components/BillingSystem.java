package components;

import interfaces.IBillingSystem;
import model.*;
import java.util.Random;

public class BillingSystem implements IBillingSystem {
    private CarMgr carMgr;
    private ReservationMgr reservationMgr;

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
            System.out.println("Tagihan " + billId + " untuk " + customerId + 
                             ": Rp" + amount + " untuk reservasi " + reservationId);
            return billId;
        }
        return null;
    }

    @Override
    public boolean processBillPayment(String billId) {
        System.out.println("Pembayaran untuk tagihan " + billId + " berhasil diproses.");
        return true;
    }

    @Override
    public double calculateAdditionalCharges(String reservationId, DateRange extension) {
        if (reservationMgr != null && carMgr != null) {
            ReservationDetails reservation = reservationMgr.getReservation(reservationId);
            if (reservation != null) {
                CarDetails car = carMgr.getCarInfo(reservation.carId);
                if (car != null) {
                    return car.price * (extension.getDays() - 1); // Kurangi 1 untuk menghitung hari tambahan yang benar
                }
            }
        }
        return 0.0;
    }

    // Method untuk backward compatibility
    public boolean generateBill(String customerId, String resRef, double amount) {
        return createBillRequest(customerId, resRef, amount) != null;
    }
}