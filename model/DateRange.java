package model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class DateRange {
    public String startDate;
    public String endDate;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public DateRange(String startDate, String endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
    }
    
    public int getDays() {
        LocalDate start = LocalDate.parse(startDate, formatter);
        LocalDate end = LocalDate.parse(endDate, formatter);
        
        // ChronoUnit.DAYS.between returns the number of days between two dates
        long daysBetween = ChronoUnit.DAYS.between(start, end);
        
        // Add 1 to include both start and end dates
        return (int) daysBetween + 1;
    }
}