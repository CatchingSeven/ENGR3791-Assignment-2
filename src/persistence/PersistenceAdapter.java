package persistence;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import domain.*;

/**
 * PersistenceAdapter handles all file I/O operations for the application.
 * It encapsulates CSV reading/writing, timetable repository functions,
 * and settings storage.
 */
public class PersistenceAdapter {

    // Enforces strict 24-hour time format for reading and writing data
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd MMM yyyy"); // Adjusted for full dates

    private static final String SETTINGS_FILE = "settings.csv";
    private static final String DELIMITER = ",";

    // ==========================================
    // CSV Reader & Writer (Class Data)
    // ==========================================

    /**
     * Writes a list of ClassInstances to a CSV file.
     * Overwrites the original file if it already exists.
     */
    public void writeClassesToCSV(List<ClassInstance> classInstances, String filePath) throws IOException {
        // Using try-with-resources and default FileWriter behavior to strictly overwrite existing files
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, false))) {
            writer.write("ClassInstanceNo,StartDate,EndDate,StartTime,EndTime,Day,Building,Room\n");

            for (ClassInstance instance : classInstances) {
                String line = String.format("%d%s%s%s%s%s%s%s%s%s%s%s%s%s%s",
                        instance.getClassInstanceNo(), DELIMITER,
                        instance.getStartDate().format(DATE_FORMATTER), DELIMITER,
                        instance.getEndDate().format(DATE_FORMATTER), DELIMITER,
                        instance.getStartTime().format(TIME_FORMATTER), DELIMITER,
                        instance.getEndTime().format(TIME_FORMATTER), DELIMITER,
                        instance.getDay(), DELIMITER,
                        instance.getBuilding(), DELIMITER,
                        instance.getRoom());
                writer.write(line);
                writer.newLine();
            }
        }
    }

    /**
     * Reads ClassInstances from a CSV file.
     */
    public List<ClassInstance> readClassesFromCSV(String filePath) throws IOException {
        List<ClassInstance> instances = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line = reader.readLine(); // Skip header

            while ((line = reader.readLine()) != null) {
                String[] values = line.split(DELIMITER);
                if (values.length == 8) {
                    try {
                        int instanceNo = Integer.parseInt(values[0].trim());
                        LocalDate startDate = LocalDate.parse(values[1].trim(), DATE_FORMATTER);
                        LocalDate endDate = LocalDate.parse(values[2].trim(), DATE_FORMATTER);
                        LocalTime startTime = LocalTime.parse(values[3].trim(), TIME_FORMATTER);
                        LocalTime endTime = LocalTime.parse(values[4].trim(), TIME_FORMATTER);
                        String day = values[5].trim();
                        String building = values[6].trim();
                        String room = values[7].trim();

                        instances.add(new ClassInstance(instanceNo, startDate, endDate, startTime, endTime, day, building, room));
                    } catch (DateTimeParseException | NumberFormatException e) {
                        System.err.println("Error parsing row due to invalid format: " + line);
                    }
                }
            }
        }
        return instances;
    }

    // ==========================================
    // Timetable Repository (Exporting)
    // ==========================================

    /**
     * Exports a generated timetable to a CSV file.
     * Overwrites the file if it exists.
     */
    public void exportTimetable(Timetable timetable, List<ClassInstance> scheduledClasses, String filePath) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, false))) {
            writer.write("Timetable Code: " + timetable.getTimetableCode() + ", Name: " + timetable.getTimetableName() + "\n");
            writer.write("Semester: " + timetable.getSemester() + ", Allowed Overlap: " + timetable.isAllowOverlap() + "\n");
            writer.write("--------------------------------------------------\n");
            writer.write("ClassInstanceNo,StartDate,EndDate,StartTime,EndTime,Day,Building,Room\n");

            for (ClassInstance instance : scheduledClasses) {
                String line = String.format("%d%s%s%s%s%s%s%s%s%s%s%s%s%s%s",
                        instance.getClassInstanceNo(), DELIMITER,
                        instance.getStartDate().format(DATE_FORMATTER), DELIMITER,
                        instance.getEndDate().format(DATE_FORMATTER), DELIMITER,
                        instance.getStartTime().format(TIME_FORMATTER), DELIMITER,
                        instance.getEndTime().format(TIME_FORMATTER), DELIMITER,
                        instance.getDay(), DELIMITER,
                        instance.getBuilding(), DELIMITER,
                        instance.getRoom());
                writer.write(line);
                writer.newLine();
            }
        }
    }

    // ==========================================
    // Settings Store
    // ==========================================

    /**
     * Saves user timetable preferences so the last used settings are remembered.
     */
    public void saveSettings(TimetablePreferences preferences) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(SETTINGS_FILE, false))) {
            writer.write(preferences.getPreferredCampus() + DELIMITER +
                    preferences.getPreferredTime() + DELIMITER +
                    preferences.getPreferredDay());
        }
    }

    /**
     * Loads the last used timetable preferences.
     */
    public TimetablePreferences loadSettings() throws IOException {
        File file = new File(SETTINGS_FILE);
        if (!file.exists()) {
            return null; // No previous settings found
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line = reader.readLine();
            if (line != null) {
                String[] values = line.split(DELIMITER);
                if (values.length == 3) {
                    return new TimetablePreferences(values[0].trim(), values[1].trim(), values[2].trim());
                }
            }
        }
        return null;
    }
}
