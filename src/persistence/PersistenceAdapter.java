package persistence;

import domain.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoField;
import java.util.*;

/** Layer 4 component: PersistenceAdapter. Handles CSVReader, CSVWriter, SettingsStore and TimetableRepository. */
public class PersistenceAdapter {
    private static final Path DATA_DIR = Paths.get("data");
    private static final Path CLASS_REPOSITORY = DATA_DIR.resolve("classes.csv");
    private static final Path SETTINGS_FILE = DATA_DIR.resolve("settings.csv");

    private static final DateTimeFormatter ISO_DATE = DateTimeFormatter.ISO_LOCAL_DATE;
    private static final DateTimeFormatter TIME_24 = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter DISPLAY_DATE = DateTimeFormatter.ofPattern("dd MMM uuuu", Locale.ENGLISH);

    private static final List<String> HANDBOOK_HEADERS = Arrays.asList(
            "Topic", "Availability", "Class", "Class instance", "Date", "Day", "Time", "Location"
    );

    public PersistenceAdapter() {
        try {
            Files.createDirectories(DATA_DIR);
            Files.createDirectories(Paths.get("exports"));
        } catch (IOException e) {
            throw new IllegalStateException("Could not create data directories: " + e.getMessage(), e);
        }
    }

    public List<Schedule> loadClasses() throws IOException {
        if (!Files.exists(CLASS_REPOSITORY)) return new ArrayList<>();
        return readInternalClassCsv(CLASS_REPOSITORY.toString());
    }

    public void saveClasses(List<Schedule> schedules) throws IOException {
        Files.createDirectories(DATA_DIR);
        writeInternalClassCsv(schedules, CLASS_REPOSITORY.toString());
    }

    public List<Schedule> readHandbookCsv(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        if (!Files.exists(path)) {
            throw new IOException("File does not exist: " + filePath);
        }

        List<Schedule> results = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String headerLine = reader.readLine();
            if (headerLine == null) throw new IOException("CSV file is empty.");
            List<String> headers = parseCsvLine(stripBom(headerLine));
            validateHandbookHeaders(headers);

            String line;
            int rowNo = 1;
            int nextRecordId = 1;
            while ((line = reader.readLine()) != null) {
                rowNo++;
                if (line.trim().isEmpty()) continue;
                List<String> cells = parseCsvLine(line);
                if (cells.size() != HANDBOOK_HEADERS.size()) {
                    throw new IOException("CSV row " + rowNo + " has " + cells.size() + " columns; expected 8.");
                }
                results.add(mapHandbookRowToSchedule(nextRecordId++, cells, rowNo));
            }
        }
        return results;
    }

    public void writeInternalClassCsv(List<Schedule> schedules, String filePath) throws IOException {
        Path path = Paths.get(filePath);
        if (path.getParent() != null) Files.createDirectories(path.getParent());
        assignMissingRecordIds(schedules);
        try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
            writer.write(String.join(",",
                    "RecordId", "TopicCode", "TopicName", "AttendanceMode", "Campus", "Semester", "AvailabilityNo",
                    "ClassFormat", "ClassCode", "ClassInstanceNo", "StartDate", "EndDate", "Day",
                    "StartTime", "EndTime", "Building", "Room"));
            writer.newLine();
            for (Schedule s : schedules) {
                List<String> cells = new ArrayList<>();
                cells.add(String.valueOf(s.getRecordId()));
                cells.add(s.getTopic().getTopicCode());
                cells.add(s.getTopic().getTopicName());
                cells.add(s.getAvailability().getAttendanceMode());
                cells.add(s.getAvailability().getCampus());
                cells.add(s.getAvailability().getSemester());
                cells.add(String.valueOf(s.getAvailability().getAvailabilityNo()));
                cells.add(s.getTopicClass().getClassFormat());
                cells.add(s.getTopicClass().getClassCode());
                cells.add(String.valueOf(s.getClassInstance().getClassInstanceNo()));
                cells.add(s.getClassInstance().getStartDate().format(ISO_DATE));
                cells.add(s.getClassInstance().getEndDate().format(ISO_DATE));
                cells.add(s.getClassInstance().getDay());
                cells.add(s.getClassInstance().getStartTime().format(TIME_24));
                cells.add(s.getClassInstance().getEndTime().format(TIME_24));
                cells.add(s.getClassInstance().getBuilding());
                cells.add(s.getClassInstance().getRoom());
                writer.write(toCsvLine(cells));
                writer.newLine();
            }
        }
    }

    public List<Schedule> readInternalClassCsv(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        if (!Files.exists(path)) return new ArrayList<>();
        List<Schedule> schedules = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String header = reader.readLine();
            if (header == null) return schedules;
            String line;
            int rowNo = 1;
            while ((line = reader.readLine()) != null) {
                rowNo++;
                if (line.trim().isEmpty()) continue;
                List<String> c = parseCsvLine(line);
                if (c.size() != 17) {
                    throw new IOException("Saved class CSV row " + rowNo + " has invalid format.");
                }
                int i = 0;
                int recordId = parseInt(c.get(i++), "RecordId", rowNo);
                Topic topic = new Topic(c.get(i++), c.get(i++));
                ClassAvailability availability = new ClassAvailability(c.get(i++), c.get(i++), c.get(i++), parseInt(c.get(i++), "AvailabilityNo", rowNo));
                TopicClass topicClass = new TopicClass(c.get(i++), c.get(i++));
                int instanceNo = parseInt(c.get(i++), "ClassInstanceNo", rowNo);
                LocalDate startDate = LocalDate.parse(c.get(i++), ISO_DATE);
                LocalDate endDate = LocalDate.parse(c.get(i++), ISO_DATE);
                String day = c.get(i++);
                LocalTime startTime = LocalTime.parse(c.get(i++), TIME_24);
                LocalTime endTime = LocalTime.parse(c.get(i++), TIME_24);
                String building = c.get(i++);
                String room = c.get(i++);
                ClassInstance instance = new ClassInstance(instanceNo, topicClass.getClassCode(), startDate, endDate, startTime, endTime, day, building, room);
                schedules.add(new Schedule(recordId, topic, availability, topicClass, instance));
            }
        }
        return schedules;
    }

    public void exportTimetable(Timetable timetable, String filePath) throws IOException {
        Path path = Paths.get(filePath);
        if (path.getParent() != null) Files.createDirectories(path.getParent());
        try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
            writer.write("Timetable Code," + escape(timetable.getTimetableCode()));
            writer.newLine();
            writer.write("Timetable Name," + escape(timetable.getTimetableName()));
            writer.newLine();
            writer.write("Semester," + escape(timetable.getSemester()));
            writer.newLine();
            writer.write("Allow lecture overlap," + timetable.isAllowOverlap());
            writer.newLine();
            writer.newLine();
            writer.write(String.join(",",
                    "Topic Code", "Topic Name", "Attendance Mode", "Campus", "Semester", "Availability Number",
                    "Class", "Class Instance", "Date of First Class", "Date of Last Class", "Day",
                    "Start Time", "End Time", "Building", "Room"));
            writer.newLine();
            for (Schedule s : timetable.getSchedules()) {
                writer.write(toCsvLine(fullScheduleCells(s, true)));
                writer.newLine();
            }
            if (!timetable.getWarnings().isEmpty()) {
                writer.newLine();
                writer.write("Warnings");
                writer.newLine();
                for (String warning : timetable.getWarnings()) {
                    writer.write(escape(warning));
                    writer.newLine();
                }
            }
        }
    }

    public List<String> fullScheduleCells(Schedule s, boolean displayDates) {
        List<String> cells = new ArrayList<>();
        cells.add(s.getTopic().getTopicCode());
        cells.add(s.getTopic().getTopicName());
        cells.add(s.getAvailability().getAttendanceMode());
        cells.add(s.getAvailability().getCampus());
        cells.add(s.getAvailability().getSemester());
        cells.add(String.valueOf(s.getAvailability().getAvailabilityNo()));
        cells.add(s.getTopicClass().getClassFormat());
        cells.add(String.valueOf(s.getClassInstance().getClassInstanceNo()));
        cells.add(displayDates ? s.getClassInstance().getStartDate().format(DISPLAY_DATE) : s.getClassInstance().getStartDate().format(ISO_DATE));
        cells.add(displayDates ? s.getClassInstance().getEndDate().format(DISPLAY_DATE) : s.getClassInstance().getEndDate().format(ISO_DATE));
        cells.add(s.getClassInstance().getDay());
        cells.add(s.getClassInstance().getStartTime().format(TIME_24));
        cells.add(s.getClassInstance().getEndTime().format(TIME_24));
        cells.add(s.getClassInstance().getBuilding());
        cells.add(s.getClassInstance().getRoom());
        return cells;
    }

    public void saveSettings(TimetablePreferences preferences) throws IOException {
        Files.createDirectories(DATA_DIR);
        try (BufferedWriter writer = Files.newBufferedWriter(SETTINGS_FILE, StandardCharsets.UTF_8)) {
            writer.write(toCsvLine(Arrays.asList(
                    preferences.getTimetableName(),
                    preferences.getSemester(),
                    String.valueOf(preferences.isAllowLectureOverlap()),
                    String.join(";", preferences.getSelectedTopicCodes()),
                    String.join(";", preferences.getSelectedCampuses()),
                    String.join(";", preferences.getOrderedPreferences()),
                    preferences.getPreferredCampus(),
                    preferences.getPreferredTime(),
                    preferences.getPreferredDay()
            )));
            writer.newLine();
        }
    }

    public TimetablePreferences loadSettings() throws IOException {
        if (!Files.exists(SETTINGS_FILE)) return null;
        try (BufferedReader reader = Files.newBufferedReader(SETTINGS_FILE, StandardCharsets.UTF_8)) {
            String line = reader.readLine();
            if (line == null || line.isBlank()) return null;
            List<String> c = parseCsvLine(line);
            if (c.size() < 9) return null;
            TimetablePreferences p = new TimetablePreferences(c.get(6), c.get(7), c.get(8));
            p.setTimetableName(c.get(0));
            p.setSemester(c.get(1));
            p.setAllowLectureOverlap(Boolean.parseBoolean(c.get(2)));
            p.setSelectedTopicCodes(splitSemi(c.get(3)));
            p.setSelectedCampuses(splitSemi(c.get(4)));
            p.setOrderedPreferences(splitSemi(c.get(5)));
            return p;
        }
    }

    public String safeExportPath(String timetableName) {
        String base = timetableName == null || timetableName.isBlank() ? "timetable" : timetableName;
        base = base.replaceAll("[^A-Za-z0-9._-]+", "_");
        return Paths.get("exports", base + ".csv").toString();
    }

    public void assignMissingRecordIds(List<Schedule> schedules) {
        Set<Integer> used = new LinkedHashSet<>();
        int max = 0;
        for (Schedule s : schedules) {
            if (s.getRecordId() > 0) {
                used.add(s.getRecordId());
                if (s.getRecordId() > max) max = s.getRecordId();
            }
        }
        int next = max + 1;
        for (Schedule s : schedules) {
            if (s.getRecordId() <= 0 || used.contains(s.getRecordId()) && countId(schedules, s.getRecordId()) > 1) {
                while (used.contains(next)) next++;
                s.setRecordId(next);
                used.add(next);
                next++;
            }
        }
    }

    private int countId(List<Schedule> schedules, int id) {
        int count = 0;
        for (Schedule s : schedules) if (s.getRecordId() == id) count++;
        return count;
    }

    private Schedule mapHandbookRowToSchedule(int recordId, List<String> cells, int rowNo) throws IOException {
        String topicRaw = cells.get(0).trim();
        String availabilityRaw = cells.get(1).trim();
        String classRaw = cells.get(2).trim();
        int classInstanceNo = parseInt(cells.get(3), "Class instance", rowNo);
        LocalDate[] dates = parseDateRange(cells.get(4), rowNo);
        String day = cells.get(5).trim();
        LocalTime[] times = parseTimeRange(cells.get(6), rowNo);
        String[] location = parseLocation(cells.get(7));

        Topic topic = parseTopic(topicRaw);
        ClassAvailability availability = parseAvailability(availabilityRaw, rowNo);
        TopicClass topicClass = new TopicClass(classRaw, classRaw);
        ClassInstance instance = new ClassInstance(classInstanceNo, topicClass.getClassCode(),
                dates[0], dates[1], times[0], times[1], day, location[0], location[1]);
        return new Schedule(recordId, topic, availability, topicClass, instance);
    }

    private Topic parseTopic(String topicRaw) throws IOException {
        if (topicRaw.isBlank()) throw new IOException("Topic column is empty.");
        String[] parts = topicRaw.trim().split("\\s+", 2);
        String code = parts[0].trim();
        String name = parts.length > 1 ? parts[1].trim() : "";
        return new Topic(code, name);
    }

    private ClassAvailability parseAvailability(String availabilityRaw, int rowNo) throws IOException {
        String[] raw = availabilityRaw.split("\\s+-\\s+");
        if (raw.length < 4) {
            throw new IOException("CSV row " + rowNo + " has invalid Availability format. Expected 'Attendance - Campus - Semester - Number'.");
        }
        String attendanceMode = raw[0].trim();
        String semester = raw[raw.length - 2].trim();
        int availabilityNo = parseInt(raw[raw.length - 1], "Availability number", rowNo);
        String campus = String.join(" - ", Arrays.copyOfRange(raw, 1, raw.length - 2)).trim();
        return new ClassAvailability(attendanceMode, campus, semester, availabilityNo);
    }

    private LocalDate[] parseDateRange(String text, int rowNo) throws IOException {
        String[] parts = text.split("\\s+-\\s+");
        if (parts.length == 0 || parts.length > 2) throw new IOException("CSV row " + rowNo + " has invalid Date range.");
        LocalDate start = parseDayMonth(parts[0].trim(), rowNo);
        LocalDate end = parts.length == 2 ? parseDayMonth(parts[1].trim(), rowNo) : start;
        if (end.isBefore(start)) end = end.plusYears(1);
        return new LocalDate[]{start, end};
    }

    private LocalDate parseDayMonth(String text, int rowNo) throws IOException {
        DateTimeFormatter f = new DateTimeFormatterBuilder()
                .parseCaseInsensitive()
                .appendPattern("d MMM")
                .parseDefaulting(ChronoField.YEAR, Year.now().getValue())
                .toFormatter(Locale.ENGLISH);
        try {
            return LocalDate.parse(text, f);
        } catch (DateTimeParseException e) {
            throw new IOException("CSV row " + rowNo + " has invalid date: " + text);
        }
    }

    private LocalTime[] parseTimeRange(String text, int rowNo) throws IOException {
        String[] parts = text.split("\\s+-\\s+");
        if (parts.length != 2) throw new IOException("CSV row " + rowNo + " has invalid Time range.");
        return new LocalTime[]{parseTime(parts[0].trim(), rowNo), parseTime(parts[1].trim(), rowNo)};
    }

    private LocalTime parseTime(String text, int rowNo) throws IOException {
        try {
            return LocalTime.parse(text, TIME_24);
        } catch (DateTimeParseException e) {
            throw new IOException("CSV row " + rowNo + " has invalid 24-hour time: " + text);
        }
    }

    private String[] parseLocation(String locationRaw) {
        String location = locationRaw == null ? "" : locationRaw.trim();
        int comma = location.indexOf(',');
        if (comma >= 0) {
            return new String[]{location.substring(0, comma).trim(), location.substring(comma + 1).trim()};
        }
        return new String[]{location, ""};
    }

    private void validateHandbookHeaders(List<String> headers) throws IOException {
        if (headers.size() != HANDBOOK_HEADERS.size()) {
            throw new IOException("CSV header is invalid. Expected 8 columns: " + HANDBOOK_HEADERS);
        }
        for (int i = 0; i < HANDBOOK_HEADERS.size(); i++) {
            if (!HANDBOOK_HEADERS.get(i).equalsIgnoreCase(headers.get(i).trim())) {
                throw new IOException("CSV header column " + (i + 1) + " should be '" + HANDBOOK_HEADERS.get(i) + "' but was '" + headers.get(i) + "'.");
            }
        }
    }

    private int parseInt(String text, String fieldName, int rowNo) throws IOException {
        try {
            return Integer.parseInt(text.trim());
        } catch (NumberFormatException e) {
            throw new IOException("CSV row " + rowNo + " has invalid integer for " + fieldName + ": " + text);
        }
    }

    private List<String> splitSemi(String text) {
        List<String> out = new ArrayList<>();
        if (text == null || text.isBlank()) return out;
        for (String part : text.split(";")) {
            String trimmed = part.trim();
            if (!trimmed.isEmpty()) out.add(trimmed);
        }
        return out;
    }

    private String stripBom(String s) {
        return s != null && !s.isEmpty() && s.charAt(0) == '\uFEFF' ? s.substring(1) : s;
    }

    public List<String> parseCsvLine(String line) throws IOException {
        List<String> values = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;
        for (int i = 0; i < line.length(); i++) {
            char ch = line.charAt(i);
            if (ch == '"') {
                if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    current.append('"');
                    i++;
                } else {
                    inQuotes = !inQuotes;
                }
            } else if (ch == ',' && !inQuotes) {
                values.add(current.toString().trim());
                current.setLength(0);
            } else {
                current.append(ch);
            }
        }
        if (inQuotes) throw new IOException("CSV line has an unclosed quote: " + line);
        values.add(current.toString().trim());
        return values;
    }

    private String toCsvLine(List<String> cells) {
        List<String> escaped = new ArrayList<>();
        for (String cell : cells) escaped.add(escape(cell));
        return String.join(",", escaped);
    }

    private String escape(String value) {
        if (value == null) return "";
        boolean mustQuote = value.contains(",") || value.contains("\"") || value.contains("\n") || value.contains("\r");
        String escaped = value.replace("\"", "\"\"");
        return mustQuote ? "\"" + escaped + "\"" : escaped;
    }
}
