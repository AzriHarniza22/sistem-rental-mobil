package components;

import interfaces.*;
import model.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ReservationSystem implements IReservationSystem {
    private CarMgr carMgr;
    private ReservationMgr reservationMgr;

    public ReservationSystem(CarMgr carMgr, ReservationMgr reservationMgr) {
        this.carMgr = carMgr;
        this.reservationMgr = reservationMgr;
    }

    @Override
    public boolean checkReservationAvailability(String carId, DateRange dateRange) {
        CarDetails car = carMgr.getCarInfo(carId);
        if (car == null) {
            return false;
        }
        
        // Cek apakah ada reservasi yang bentrok dengan tanggal yang diminta
        for (ReservationDetails reservation : reservationMgr.reservations.values()) {
            if (reservation.carId.equals(carId) && 
                !reservation.status.equals("CANCELLED") && 
                !reservation.status.equals("COMPLETED")) {
                
                // Cek apakah tanggal bentrok
                if (isDateRangeOverlap(dateRange, reservation.dateRange)) {
                    return false; // Ada konflik tanggal
                }
            }
        }
        
        return true; // Tidak ada konflik
    }

    // Method untuk mendapatkan tanggal yang sudah direservasi
    public List<DateRange> getReservedDates(String carId) {
        List<DateRange> reservedDates = new ArrayList<>();
        
        for (ReservationDetails reservation : reservationMgr.reservations.values()) {
            if (reservation.carId.equals(carId) && 
                !reservation.status.equals("CANCELLED") && 
                !reservation.status.equals("COMPLETED")) {
                reservedDates.add(reservation.dateRange);
            }
        }
        
        return reservedDates;
    }

    // Helper method untuk mengecek overlap tanggal
    private boolean isDateRangeOverlap(DateRange range1, DateRange range2) {
        try {
            LocalDate start1 = LocalDate.parse(range1.startDate);
            LocalDate end1 = LocalDate.parse(range1.endDate);
            LocalDate start2 = LocalDate.parse(range2.startDate);
            LocalDate end2 = LocalDate.parse(range2.endDate);
            
            // Tidak overlap jika salah satu range berakhir sebelum yang lain dimulai
            return !(end1.isBefore(start2) || end2.isBefore(start1));
        } catch (Exception e) {
            return true; // Jika error parsing tanggal, anggap ada konflik untuk safety
        }
    }

    @Override
    public boolean validateReservationData(ReservationDetails reservation) {
        return reservation != null && reservation.customerId != null 
               && reservation.carId != null && reservation.dateRange != null;
    }

    @Override
    public String createReservationRequest(ReservationDetails reservation) {
        if (validateReservationData(reservation)) {
            reservation.totalCost = calculateReservationCost(reservation.carId, reservation.dateRange);
            return reservationMgr.createReservation(reservation);
        }
        return null;
    }

    @Override
    public String generateReservationId() {
        return "RES" + String.format("%04d", new Random().nextInt(10000));
    }

    @Override
    public double calculateReservationCost(String carId, DateRange dateRange) {
        CarDetails car = carMgr.getCarInfo(carId);
        if (car != null && dateRange != null) {
            return car.price * dateRange.getDays();
        }
        return 0.0;
    }
}