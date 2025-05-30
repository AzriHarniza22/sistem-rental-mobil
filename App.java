import components.*;
import model.*;

import java.util.List;
import java.util.Scanner;

public class App {
    // Valid car types
    private static final String[] VALID_CAR_TYPES = {"sedan", "suv", "pickup"};
    
    public static void main(String[] args) {
        CarMgr carMgr = new CarMgr();
        CustomerMgr custMgr = new CustomerMgr();
        ReservationMgr reservationMgr = new ReservationMgr(carMgr);
        RentalSystem rentalMgr = new RentalSystem(reservationMgr, carMgr); // NEW
        BillingSystem billing = new BillingSystem(carMgr, reservationMgr);
        
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
                    customerMenu(reservationMgr, rentalMgr, carMgr, custMgr, billing, sc); // UPDATED
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
                    addCarMenu(carMgr, sc);
                    break;
                case "2":
                    editCarMenu(carMgr, sc);
                    break;
                case "3":
                    deleteCarMenu(carMgr, sc);
                    break;
                case "4":
                    displayCarList(carMgr);
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
    
    private static void addCarMenu(CarMgr carMgr, Scanner sc) {
        System.out.print("ID: "); 
        String id = sc.nextLine();
        
        // Check if car ID already exists
        if (carMgr.getCarInfo(id) != null) {
            System.out.println("Error: ID mobil sudah ada! Gunakan ID yang berbeda.");
            return;
        }
        
        System.out.print("Model: "); 
        String model = sc.nextLine();
        
        String type = selectCarType(sc);
        if (type == null) {
            System.out.println("Gagal menambah mobil: Tipe tidak valid.");
            return;
        }
        
        System.out.print("Harga per hari: "); 
        double price;
        try {
            price = Double.parseDouble(sc.nextLine());
            if (price <= 0) {
                System.out.println("Error: Harga harus lebih dari 0.");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Error: Format harga tidak valid.");
            return;
        }
        
        carMgr.addCar(new CarDetails(id, model, type, true, price));
        System.out.println("Mobil berhasil ditambahkan!");
    }
    
    private static void editCarMenu(CarMgr carMgr, Scanner sc) {
        System.out.println("\n===== DAFTAR MOBIL =====");
        displayCarList(carMgr);
        
        System.out.print("\nMasukkan ID mobil yang akan diedit: "); 
        String eid = sc.nextLine();
        
        // Check if car exists
        CarDetails existingCar = carMgr.getCarInfo(eid);
        if (existingCar == null) {
            System.out.println("Error: Mobil dengan ID '" + eid + "' tidak ditemukan!");
            return;
        }
        
        System.out.println("\nData mobil saat ini:");
        System.out.println("ID: " + existingCar.carId);
        System.out.println("Model: " + existingCar.model);
        System.out.println("Tipe: " + existingCar.type.toUpperCase());
        System.out.println("Harga: Rp" + existingCar.price + "/hari");
        System.out.println("Status: " + (existingCar.available ? "Tersedia" : "Disewa"));
        
        System.out.print("\nModel baru (kosong = tidak berubah): "); 
        String emodel = sc.nextLine();
        if (emodel.trim().isEmpty()) emodel = null;
        
        System.out.println("\nPilih tipe baru (kosong = tidak berubah):");
        String etype = selectCarType(sc, true);
        
        System.out.print("Harga per hari baru (kosong = tidak berubah): "); 
        String priceInput = sc.nextLine();
        Double eprice = null;
        if (!priceInput.trim().isEmpty()) {
            try {
                eprice = Double.parseDouble(priceInput);
                if (eprice <= 0) {
                    System.out.println("Error: Harga harus lebih dari 0.");
                    return;
                }
            } catch (NumberFormatException e) {
                System.out.println("Error: Format harga tidak valid.");
                return;
            }
        }
        
        boolean success = carMgr.editCar(eid, emodel, etype, eprice);
        if (success) {
            System.out.println("Mobil berhasil diupdate!");
        } else {
            System.out.println("Gagal mengupdate mobil.");
        }
    }
    
    private static void deleteCarMenu(CarMgr carMgr, Scanner sc) {
        System.out.println("\n===== DAFTAR MOBIL =====");
        displayCarList(carMgr);
        
        System.out.print("\nMasukkan ID mobil yang akan dihapus: "); 
        String did = sc.nextLine();
        
        // Check if car exists
        CarDetails existingCar = carMgr.getCarInfo(did);
        if (existingCar == null) {
            System.out.println("Error: Mobil dengan ID '" + did + "' tidak ditemukan!");
            return;
        }
        
        System.out.println("Mobil yang akan dihapus: " + existingCar.model + " (" + existingCar.type.toUpperCase() + ")");
        System.out.print("Yakin ingin menghapus? (ya/tidak): ");
        String confirm = sc.nextLine();
        
        if (confirm.equalsIgnoreCase("ya")) {
            boolean success = carMgr.deleteCar(did);
            if (success) {
                System.out.println("Mobil berhasil dihapus!");
            } else {
                System.out.println("Gagal menghapus mobil.");
            }
        } else {
            System.out.println("Penghapusan dibatalkan.");
        }
    }
    
    private static void displayCarList(CarMgr carMgr) {
        System.out.println("\nDaftar Mobil:");
        System.out.println("=".repeat(70));
        System.out.printf("%-10s %-20s %-10s %-15s %-10s%n", "ID", "Model", "Tipe", "Harga/hari", "Status");
        System.out.println("=".repeat(70));
        
        carMgr.listCars().forEach(c -> 
            System.out.printf("%-10s %-20s %-10s Rp%-12.0f %-10s%n", 
                c.carId, 
                c.model, 
                c.type.toUpperCase(), 
                c.price, 
                c.available ? "Tersedia" : "Disewa"
            )
        );
        System.out.println("=".repeat(70));
    }
    
    private static String selectCarType(Scanner sc) {
        return selectCarType(sc, false);
    }
    
    private static String selectCarType(Scanner sc, boolean allowEmpty) {
        System.out.println("\nTipe mobil yang tersedia:");
        for (int i = 0; i < VALID_CAR_TYPES.length; i++) {
            System.out.println((i + 1) + ". " + VALID_CAR_TYPES[i].toUpperCase());
        }
        if (allowEmpty) {
            System.out.println("0. Tidak berubah");
        }
        
        System.out.print("Pilih tipe (nomor): ");
        String input = sc.nextLine();
        
        if (allowEmpty && input.trim().isEmpty()) {
            return null;
        }
        
        try {
            int choice = Integer.parseInt(input);
            if (allowEmpty && choice == 0) {
                return null;
            }
            if (choice >= 1 && choice <= VALID_CAR_TYPES.length) {
                return VALID_CAR_TYPES[choice - 1];
            }
        } catch (NumberFormatException e) {
            // Handle invalid number format
        }
        
        System.out.println("Pilihan tidak valid!");
        return selectCarType(sc, allowEmpty);
    }

    private static void customerMenu(ReservationMgr reservationMgr, RentalSystem rentalMgr, CarMgr carMgr, CustomerMgr custMgr, BillingSystem billing, Scanner sc) {
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
                    currentResRef = makeReservation(reservationMgr, carMgr, custId, billing, sc); // UPDATED
                    break;
                case "2":
                    if (currentResRef == null) {
                        System.out.println("Anda belum membuat reservasi!");
                    } else {
                        String plate = rentalMgr.startCarRental(currentResRef); // UPDATED
                        if (plate != null) {
                            System.out.println("Sewa Mobil dimulai. Plat: " + plate);
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
                            boolean success = rentalMgr.endCarRental(currentResRef); // UPDATED
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
                            boolean success = rentalMgr.extendRental(currentResRef, newEndDate); // UPDATED
                            if (success) {
                                System.out.println("Sewa berhasil diperpanjang hingga " + newEndDate);
                                
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
    
    private static String makeReservation(ReservationMgr reservationMgr, CarMgr carMgr, String custId, BillingSystem billing, Scanner sc) {
        System.out.println("\nMobil Tersedia:");
        CarDetails[] list = carMgr.getAvailableCars("");
        if (list.length == 0) {
            System.out.println("Tidak ada mobil tersedia saat ini.");
            return null;
        }
        
        System.out.println("=".repeat(60));
        System.out.printf("%-5s %-20s %-10s %-15s%n", "No.", "Model", "Tipe", "Harga/hari");
        System.out.println("=".repeat(60));
        
        for (int i = 0; i < list.length; i++) {
            System.out.printf("%-5d %-20s %-10s Rp%-12.0f%n", 
                (i + 1), 
                list[i].model, 
                list[i].type.toUpperCase(), 
                list[i].price
            );
        }
        System.out.println("=".repeat(60));

        
        System.out.print("Pilih (nomor): ");
        int pilih = Integer.parseInt(sc.nextLine()) - 1;
        if (pilih < 0 || pilih >= list.length) {
            System.out.println("Pilihan tidak valid.");
            return null;
        }
        
        CarDetails chosen = list[pilih];

        // Tampilkan tanggal yang sudah direservasi untuk mobil ini
        showReservedDates(reservationMgr, chosen.carId); // UPDATED

        System.out.print("Mulai (YYYY-MM-DD): "); String start = sc.nextLine();
        System.out.print("Selesai (YYYY-MM-DD): "); String end = sc.nextLine();
        DateRange dr = new DateRange(start, end);
        
        // Hitung dan tampilkan total biaya sebelum konfirmasi
        double totalCost = reservationMgr.calculateReservationCost(chosen.carId, dr); // UPDATED
        int totalDays = dr.getDays();
        
        System.out.println("\n===== DETAIL RESERVASI =====");
        System.out.println("Mobil: " + chosen.model + " (" + chosen.type.toUpperCase() + ")");
        System.out.println("Tanggal: " + start + " sampai " + end + " (" + totalDays + " hari)");
        System.out.println("Harga per hari: Rp" + chosen.price);
        System.out.println("Total biaya: Rp" + totalCost);
        
        System.out.print("\nLanjutkan dengan reservasi? (ya/tidak): ");
        String confirm = sc.nextLine();
        
        if (confirm.equalsIgnoreCase("ya")) {
            try {
                ReservationDetails reservation = new ReservationDetails(custId, chosen.carId, dr);
                reservation.totalCost = totalCost;
                
                if (reservationMgr.checkReservationAvailability(chosen.carId, dr)) { // UPDATED
                    String resRef = reservationMgr.createReservation(reservation); // UPDATED
                    if (resRef != null) {
                        String billId = billing.createBillRequest(custId, resRef, totalCost);
                        if (billId != null) {
                            billing.processBillPayment(billId);
                        }
                        
                        System.out.println("Reservasi Berhasil. Kode: " + resRef);
                        return resRef;
                    }
                } else {
                    System.out.println("Gagal membuat reservasi. Mobil tidak tersedia pada tanggal tersebut.");
                    return null;
                }
                System.out.println("Gagal membuat reservasi. Terjadi kesalahan sistem.");
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
    private static void showReservedDates(ReservationMgr reservationMgr, String carId) {
        System.out.println("\n===== STATUS RESERVASI MOBIL =====");
        
        List<DateRange> reservedRanges = reservationMgr.getReservedDates(carId); // UPDATED
        
        System.out.println("Tanggal yang sudah direservasi:");
        
        if (reservedRanges.isEmpty()) {
            System.out.println("- Belum ada reservasi untuk mobil ini");
        } else {
            for (int i = 0; i < reservedRanges.size(); i++) {
                DateRange range = reservedRanges.get(i);
                System.out.println("- " + range.startDate + " sampai " + range.endDate + 
                                " (" + range.getDays() + " hari)");
            }
        }
        
        System.out.println("=".repeat(40));
    }
}