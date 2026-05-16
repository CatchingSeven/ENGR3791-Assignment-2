package businesslogic;

import domain.Schedule;
import domain.Timetable;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Locale;

/** Layer 3 component: ValidationService with DateTimeValidator, NameGenerator and TimeFormatFixer responsibilities. */
public class ValidationService {
    private static final DateTimeFormatter TIME_24 = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter TIME_12 = DateTimeFormatter.ofPattern("h:mm a", Locale.ENGLISH);
    private static final DateTimeFormatter DATE_ISO = DateTimeFormatter.ISO_LOCAL_DATE;

    public void validateDateAndTime(LocalDate startDate, LocalDate endDate, LocalTime startTime, LocalTime endTime) {
        if (startDate == null || endDate == null || startTime == null || endTime == null) {
            throw new IllegalArgumentException("Invalid format: date and time fields cannot be empty.");
        }
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Invalid format: start date cannot be after the end date.");
        }
        if (startDate.isEqual(endDate) && !startTime.isBefore(endTime)) {
            throw new IllegalArgumentException("Invalid format: start time must be before the end time.");
        }
    }

    public void validateSchedule(Schedule schedule) {
        validateDateAndTime(
                schedule.getClassInstance().getStartDate(),
                schedule.getClassInstance().getEndDate(),
                schedule.getClassInstance().getStartTime(),
                schedule.getClassInstance().getEndTime()
        );
    }

    public LocalTime enforce24HourFormat(String timeInput) {
        if (timeInput == null || timeInput.isBlank()) {
            throw new IllegalArgumentException("Invalid time format. Use HH:mm, for example 14:30.");
        }
        String input = timeInput.trim();
        try {
            return LocalTime.parse(input, TIME_24);
        } catch (DateTimeParseException ignored) {
            try {
                return LocalTime.parse(input.toUpperCase(Locale.ENGLISH), TIME_12);
            } catch (DateTimeParseException e) {
                throw new IllegalArgumentException("Invalid time format. Use 24-hour time like 14:30, or 12-hour time like 2:30 PM so it can be fixed.");
            }
        }
    }

    public String formatTime(LocalTime time) {
        return time.format(TIME_24);
    }

    public LocalDate parseDate(String dateInput) {
        try {
            return LocalDate.parse(dateInput.trim(), DATE_ISO);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid date format. Use YYYY-MM-DD, for example 2026-03-11.");
        }
    }

    public String generateUniqueTimetableName(List<Timetable> existingTimetables) {
        int highest = 0;
        for (Timetable timetable : existingTimetables) {
            String name = timetable.getTimetableName();
            if (name != null && name.toLowerCase(Locale.ROOT).startsWith("timetable ")) {
                try {
                    int number = Integer.parseInt(name.substring("timetable ".length()).trim());
                    if (number > highest) highest = number;
                } catch (NumberFormatException ignored) {
                    // Not an auto-generated name.
                }
            }
        }
        return "timetable " + (highest + 1);
    }

    public boolean timetableNameExists(List<Timetable> existingTimetables, String name) {
        for (Timetable timetable : existingTimetables) {
            if (timetable.getTimetableName().equalsIgnoreCase(name)) return true;
        }
        return false;
    }

    public String ensureUniqueTimetableName(String requestedName, List<Timetable> existingTimetables) {
        String name = requestedName == null ? "" : requestedName.trim();
        if (name.isEmpty()) return generateUniqueTimetableName(existingTimetables);
        if (!timetableNameExists(existingTimetables, name)) return name;
        int suffix = 2;
        String candidate;
        do {
            candidate = name + " " + suffix;
            suffix++;
        } while (timetableNameExists(existingTimetables, candidate));
        return candidate;
    }
}
