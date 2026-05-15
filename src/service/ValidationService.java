package service;

import domain.Timetable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class ValidationService {

    private static final DateTimeFormatter TIME_FORMATTER_24H = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter TIME_FORMATTER_12H = DateTimeFormatter.ofPattern("hh:mm a");

    /**
     * Prevents setting a start date/time after an end date/time[cite: 6].
     */
    public void validateDateAndTime(LocalDate startDate, LocalDate endDate, LocalTime startTime, LocalTime endTime) {
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Invalid format: Start date cannot be after the end date.");
        }
        if (startDate.isEqual(endDate) && startTime.isAfter(endTime)) {
            throw new IllegalArgumentException("Invalid format: Start time cannot be after the end time.");
        }
    }

    /**
     * Automatically assigns a unique name for unnamed timetables (e.g., "timetable 1", "timetable 2")[cite: 8].
     */
    public String generateUniqueTimetableName(List<Timetable> existingTimetables) {
        int maxNumber = 0;
        for (Timetable t : existingTimetables) {
            String name = t.getTimetableName();
            if (name != null && name.toLowerCase().startsWith("timetable ")) {
                try {
                    int currentNumber = Integer.parseInt(name.substring(10).trim());
                    if (currentNumber > maxNumber) {
                        maxNumber = currentNumber;
                    }
                } catch (NumberFormatException ignored) {
                    // Ignore names that don't match the strict integer suffix pattern
                }
            }
        }
        return "timetable " + (maxNumber + 1);
    }

    /**
     * Enforces a 24-hour format. Attempts to automatically fix 12-hour inputs or throws an error[cite: 12].
     */
    public LocalTime enforce24HourFormat(String timeInput) {
        try {
            return LocalTime.parse(timeInput.trim(), TIME_FORMATTER_24H);
        } catch (DateTimeParseException e) {
            try {
                // Attempt to fix a 12-hour format string by parsing and returning as 24-hour
                return LocalTime.parse(timeInput.trim().toUpperCase(), TIME_FORMATTER_12H);
            } catch (DateTimeParseException ex) {
                throw new IllegalArgumentException("Invalid time format. Time must be stored in a 24-hour format (e.g., 14:30).");
            }
        }
    }
}