package businesslogic;

import java.util.regex.Pattern;

/**
 * Handles all business rules pertaining to data validation and formatting.
 */
public class ValidationService {

    private DateTimeValidator dateTimeValidator;
    private NameGenerator nameGenerator;
    private TimeFormatFixer timeFormatFixer;

    public ValidationService() {
        this.dateTimeValidator = new DateTimeValidator();
        this.nameGenerator = new NameGenerator();
        this.timeFormatFixer = new TimeFormatFixer();
    }

    public boolean validateDateRange(String startDate, String endDate) {
        return dateTimeValidator.isStartBeforeEnd(startDate, endDate);
    }

    public boolean validateTimeRange(String startTime, String endTime) {
        return dateTimeValidator.isStartBeforeEnd(startTime, endTime);
    }

    public String getUniqueTimetableName(String userInput, int existingCount) {
        return nameGenerator.generateName(userInput, existingCount);
    }

    public String enforce24HourFormat(String timeInput) throws IllegalArgumentException {
        return timeFormatFixer.fixFormat(timeInput);
    }

    // ===================================================================================
    // INTERNAL COMPONENTS
    // ===================================================================================

    private class DateTimeValidator {
        public boolean isStartBeforeEnd(String start, String end) {
            // Because strings can be complex ("27 Jul" vs "14:00"),
            // robust parsing logic goes here. For time:
            try {
                int sTime = parseToMins(start);
                int eTime = parseToMins(end);
                return sTime < eTime;
            } catch (Exception e) {
                return false;
            }
        }

        private int parseToMins(String time24) {
            String[] parts = time24.split(":");
            return (Integer.parseInt(parts[0].trim()) * 60) + Integer.parseInt(parts[1].trim());
        }
    }

    private class NameGenerator {
        public String generateName(String input, int count) {
            if (input != null && !input.trim().isEmpty()) {
                return input.trim();
            }
            // Auto-generates sequentially named timetables (e.g., "timetable 1")
            return "timetable " + (count + 1);
        }
    }

    private class TimeFormatFixer {
        public String fixFormat(String time) throws IllegalArgumentException {
            time = time.toLowerCase().trim();
            if (!time.matches(".*[ap]m.*")) {
                // If it doesn't contain am/pm, ensure it's valid 24hr HH:mm
                if (!Pattern.matches("^([01]\\d|2[0-3]):?([0-5]\\d)$", time)) {
                    throw new IllegalArgumentException("Invalid 24-hour format.");
                }
                return time;
            }

            try {
                String[] parts = time.split("[: ]");
                int hours = Integer.parseInt(parts[0]);
                int mins = Integer.parseInt(parts[1].substring(0, 2));
                boolean isPM = time.contains("pm");

                if (isPM && hours < 12) hours += 12;
                if (!isPM && hours == 12) hours = 0;

                return String.format("%02d:%02d", hours, mins);
            } catch (Exception e) {
                throw new IllegalArgumentException("Invalid time format provided.");
            }
        }
    }
}