import components.*;
import model.*;

import java.util.Scanner;

public class App {
    public static void main(String[] args) {
        CarMgr carMgr = new CarMgr();
        CustomerMgr custMgr = new CustomerMgr();
        ReservationMgr reservationMgr = new ReservationMgr(carMgr);
        BillingSystem billing = new BillingSystem(carMgr, reservationMgr);
        
        ReservationSystem reservationSystem = new ReservationSystem(carMgr, reservationMgr);
        Scanner sc = new Scanner(System.in);

        try {
            boolean running = true;
            while (running) {
                System.out.println("\n===== SISTEM RESERVASI KENDARAAN =====");
                System.out.println("Login sebagai (admin/pelanggan): ");
                String role = sc.nextLine();

                if (role.equalsIgnoreCase("exit")) {
                    running = false;
                    System.out.println("Terima kasih telah menggunakan sistem reservasi.");
                } else if (role.equalsIgnoreCase("admin")) {
                    adminMenu(carMgr, custMgr, sc);
                } else if (role.equalsIgnoreCase("pelanggan")) {
                    customerMenu(reservationMgr, reservationSystem, carMgr, custMgr, billing, sc);
                } else {
                    System.out.println("Pilihan tidak valid. Silakan coba lagi.");
                }
            }
        } finally {
            sc.close();
        }
    }

    private static void adminMenu(CarMgr carMgr, CustomerMgr custMgr, Scanner sc) {
        boolean adminLoggedIn = true;
        while (adminLoggedIn) {
            System.out.println("\n===== MENU ADMIN =====");
            System.out.println("1. Tambah Mobil\n2. Edit Mobil\n3. Hapus Mobil\n4. Lihat Mobil\n5. Lihat Pelanggan\n6. Logout");
            System.out.println("Pilihan: ");
            String choice = sc.nextLine();
            switch (choice) {
                case "1":
                    System.out.print("ID: "); String id = sc.nextLine();
                    System.out.print("Model: "); String model = sc.nextLine();
                    System.out.print("Tipe: "); String type = sc.nextLine();
                    System.out.print("Harga per hari: "); double price = Double.parseDouble(sc.nextLine());
                    carMgr.addCar(new CarDetails(id, model, type, true, price));
                    System.out.println("Mobil berhasil ditambahkan!");
                    break;
                case "2":
                    System.out.print("ID: "); String eid = sc.nextLine();
                    System.out.print("Model baru: "); String emodel = sc.nextLine();
                    System.out.print("Tipe baru: "); String etype = sc.nextLine();
                    System.out.print("Harga per hari baru: "); double eprice = Double.parseDouble(sc.nextLine());
                    carMgr.editCar(eid, emodel, etype, eprice);
                    System.out.println("Mobil berhasil diupdate!");
                    break;
                case "3":
                    System.out.print("ID: "); String did = sc.nextLine();
                    carMgr.deleteCar(did);
                    System.out.println("Mobil berhasil dihapus!");
                    break;
                case "4":
                    System.out.println("\nDaftar Mobil:");
                    carMgr.listCars().forEach(c -> System.out.println(c.carId + " - " + c.model + " (" + c.type + ") " + " - Rp" + c.price + "/hari"));
                    break;
                case "5":
                    System.out.println("\nDaftar Pelanggan:");
                    custMgr.listCustomers().forEach(c -> System.out.println(c.customerId + " - " + c.name + " - " + c.email));
                    break;
                case "6":
                    adminLoggedIn = false;
                    System.out.println("Admin berhasil logout!");
                    break;
                default:
                    System.out.println("Pilihan tidak valid. Silakan coba lagi.");
            }
        }
    }

    private static void customerMenu(ReservationMgr reservationMgr, ReservationSystem reservationSystem, CarMgr carMgr, CustomerMgr custMgr, BillingSystem billing, Scanner sc) {
        System.out.print("Nama: "); String name = sc.nextLine();
        System.out.print("Email: "); String email = sc.nextLine();
        
        // Cek apakah customer sudah ada berdasarkan email
        String custId = null;
        CustomerDetails existingCustomer = null;
        for (CustomerDetails customer : custMgr.listCustomers()) {
            if (customer.email.equals(email)) {
                custId = customer.customerId;
                existingCustomer = customer;
                break;
            }
        }
        
        // Jika belum ada, register sebagai customer baru
        if (custId == null) {
            custId = custMgr.registerCustomer(name, email);
            existingCustomer = custMgr.getCustomer(custId);
            System.out.println("Berhasil mendaftar sebagai pelanggan baru. ID: " + custId);
        } else {
            System.out.println("Selamat datang kembali! ID: " + custId);
        }
        
        CustomerDetails customer = existingCustomer;

        boolean customerLoggedIn = true;
        String currentResRef = null;
        
        while (customerLoggedIn) {
            System.out.println("\n===== MENU PELANGGAN =====");
            System.out.println("Selamat datang, " + customer.name);
            System.out.println("1. Buat Reservasi\n2. Mulai Sewa\n3. Selesai Sewa\n4. Perpanjang Sewa\n5. Logout");
            System.out.println("Pilihan: ");
            String choice = sc.nextLine();
            
            switch (choice) {
                case "1":
                    currentResRef = makeReservation(reservationSystem, carMgr, custId, billing, sc);
                    break;
                case "2":
                    if (currentResRef == null) {
                        System.out.println("Anda belum membuat reservasi!");
                    } else {
                        String plate = reservationMgr.startCarRental(currentResRef);
                        if (plate != null) {
                            System.out.println("Sewa Mobil dimulai. Plat: " + plate);
                            // Update car availability
                            ReservationDetails reservation = reservationMgr.getReservation(currentResRef);
                            if (reservation != null) {
                                carMgr.updateCarAvailability(reservation.carId, false);
                            }
                        } else {
                            System.out.println("Gagal memulai sewa.");
                        }
                    }
                    break;
                case "3":
                    if (currentResRef == null) {
                        System.out.println("Anda belum membuat reservasi!");
                    } else {
                        ReservationDetails reservation = reservationMgr.getReservation(currentResRef);
                        if (reservation == null) {
                            System.out.println("Reservasi tidak ditemukan.");
                        } else if (!"ACTIVE".equals(reservation.status)) {
                            System.out.println("Sewa belum dimulai. Mulai sewa terlebih dahulu.");
                        } else {
                            boolean success = reservationMgr.endCarRental(currentResRef);
                            if (success) {
                                System.out.println("Sewa mobil berhasil diselesaikan.");
                                currentResRef = null;
                            } else {
                                System.out.println("Gagal menyelesaikan sewa.");
                            }
                        }
                    }
                    break;
                case "4":
                    if (currentResRef == null) {
                        System.out.println("Anda belum membuat reservasi!");
                    } else {
                        System.out.print("Tanggal akhir baru (YYYY-MM-DD): ");
                        String newEndDate = sc.nextLine();
                        
                        // Dapatkan informasi perpanjangan terlebih dahulu
                        ReservationDetails reservation = reservationMgr.getReservation(currentResRef);
                        if (reservation == null) {
                            System.out.println("Reservasi tidak ditemukan.");
                            break;
                        }
                        
                        DateRange extensionRange = new DateRange(reservation.dateRange.endDate, newEndDate);
                        int additionalDays = extensionRange.getDays() - 1;
                        
                        if (additionalDays <= 0) {
                            System.out.println("Tanggal perpanjangan harus setelah tanggal akhir sewa saat ini.");
                            break;
                        }
                        
                        double additionalCost = billing.calculateAdditionalCharges(currentResRef, extensionRange);
                        
                        System.out.println("Biaya perpanjangan " + additionalDays + " hari: Rp" + additionalCost);
                        System.out.print("Setuju dengan biaya perpanjangan? (ya/tidak): ");
                        String confirm = sc.nextLine();
                        
                        if (confirm.equalsIgnoreCase("ya")) {
                            boolean success = reservationMgr.extendRental(currentResRef, newEndDate);
                            if (success) {
                                System.out.println("Sewa berhasil diperpanjang hingga " + newEndDate);
                                
                                // Generate bill for extension
                                String billId = billing.createBillRequest(custId, currentResRef, additionalCost);
                                if (billId != null) {
                                    billing.processBillPayment(billId);
                                }
                            } else {
                                System.out.println("Gagal memperpanjang sewa.");
                            }
                        } else {
                            System.out.println("Perpanjangan dibatalkan.");
                        }
                    }
                    break;
                case "5":
                    customerLoggedIn = false;
                    System.out.println("Pelanggan berhasil logout!");
                    break;
                default:
                    System.out.println("Pilihan tidak valid. Silakan coba lagi.");
            }
        }
    }
    
    private static String makeReservation(ReservationSystem reservationSystem, CarMgr carMgr, String custId, BillingSystem billing, Scanner sc) {
        System.out.println("\nMobil Tersedia:");
        CarDetails[] list = carMgr.getAvailableCars("");
        if (list.length == 0) {
            System.out.println("Tidak ada mobil tersedia saat ini.");
            return null;
        }
        
        for (int i = 0; i < list.length; i++) {
            System.out.println((i + 1) + ". " + list[i].model + " (" + list[i].type + ") - Rp" + list[i].price + "/hari");
        }

        
        System.out.print("Pilih (nomor): ");
        int pilih = Integer.parseInt(sc.nextLine()) - 1;
        if (pilih < 0 || pilih >= list.length) {
            System.out.println("Pilihan tidak valid.");
            return null;
        }
        
        CarDetails chosen = list[pilih];

        System.out.print("Mulai (YYYY-MM-DD): "); String start = sc.nextLine();
        System.out.print("Selesai (YYYY-MM-DD): "); String end = sc.nextLine();
        DateRange dr = new DateRange(start, end);
        
        // Hitung dan tampilkan total biaya sebelum konfirmasi
        double totalCost = reservationSystem.calculateReservationCost(chosen.carId, dr);
        int totalDays = dr.getDays();
        
        System.out.println("\n===== DETAIL RESERVASI =====");
        System.out.println("Mobil: " + chosen.model + " (" + chosen.type + ")");
        System.out.println("Tanggal: " + start + " sampai " + end + " (" + totalDays + " hari)");
        System.out.println("Harga per hari: Rp" + chosen.price);
        System.out.println("Total biaya: Rp" + totalCost);
        
        System.out.print("\nLanjutkan dengan reservasi? (ya/tidak): ");
        String confirm = sc.nextLine();
        
        if (confirm.equalsIgnoreCase("ya")) {
            try {
                // Create reservation using new structure
                ReservationDetails reservation = new ReservationDetails(custId, chosen.carId, dr);
                
                // Validate and create reservation
                if (reservationSystem.checkReservationAvailability(chosen.carId, dr)) {
                    String resRef = reservationSystem.createReservationRequest(reservation);
                    if (resRef != null) {
                        // Generate bill
                        String billId = billing.createBillRequest(custId, resRef, totalCost);
                        if (billId != null) {
                            billing.processBillPayment(billId);
                        }
                        
                        System.out.println("Reservasi Berhasil. Kode: " + resRef);
                        return resRef;
                    }
                }
                System.out.println("Gagal membuat reservasi. Mobil mungkin tidak tersedia.");
                return null;
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
                return null;
            }
        } else {
            System.out.println("Reservasi dibatalkan.");
            return null;
        }
    }
}