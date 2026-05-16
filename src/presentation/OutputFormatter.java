package presentation;

import businesslogic.SearchService;
import domain.Schedule;
import domain.Timetable;

import java.time.format.DateTimeFormatter;
import java.util.List;

/** Layer 1 component: OutputFormatter with TablePrinter, AnsiStyler and MessageWriter responsibilities. */
public class OutputFormatter {
    public final AnsiStyler styler = new AnsiStyler();
    public final TablePrinter tablePrinter = new TablePrinter();
    public final MessageWriter messageWriter = new MessageWriter();

    public class AnsiStyler {
        public final String RESET = "\u001B[0m";
        public final String BOLD = "\u001B[1m";
        public final String ITALIC = "\u001B[3m";
        public final String UNDERLINE = "\u001B[4m";
        public final String RED = "\u001B[31m";
        public final String GREEN = "\u001B[32m";
        public final String YELLOW = "\u001B[33m";
        public final String CYAN = "\u001B[36m";
        public final String DIM = "\u001B[2m";

        public String bold(String text) { return BOLD + text + RESET; }
        public String italic(String text) { return ITALIC + text + RESET; }
        public String underline(String text) { return UNDERLINE + text + RESET; }
        public String red(String text) { return RED + text + RESET; }
        public String green(String text) { return GREEN + text + RESET; }
        public String yellow(String text) { return YELLOW + text + RESET; }
        public String cyanTitle(String text) { return CYAN + BOLD + UNDERLINE + text + RESET; }
        public String dim(String text) { return DIM + text + RESET; }
    }

    public class MessageWriter {
        public void printError(String message) { System.out.println(styler.red("[ERROR] " + message)); }
        public void printSuccess(String message) { System.out.println(styler.green("[SUCCESS] " + message)); }
        public void printWarning(String message) { System.out.println(styler.yellow("[WARNING] " + message)); }
        public void printInfo(String message) { System.out.println(styler.dim(message)); }
        public void heading(String text) { System.out.println(styler.cyanTitle("\n" + text)); }
    }

    public class TablePrinter {
        private final DateTimeFormatter dateFmt = DateTimeFormatter.ofPattern("dd MMM yyyy");

        public void printClassTable(List<Schedule> classes) {
            if (classes == null || classes.isEmpty()) {
                messageWriter.printWarning("No classes found.");
                return;
            }
            System.out.println(styler.cyanTitle("\nClass Data"));
            System.out.printf(styler.bold("%-4s | %-9s | %-34s | %-13s | %-20s | %-3s | %-5s | %-11s | %-9s | %-11s | %-5s | %-5s | %-18s | %-22s%n"),
                    "ID", "Topic", "Name", "Class", "Campus", "Sem", "Avail", "Inst", "Day", "First", "Start", "End", "Building", "Room");
            System.out.println("---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
            for (Schedule s : classes) {
                System.out.printf("%-4d | %-9s | %-34s | %-13s | %-20s | %-3s | %-5d | %-11d | %-9s | %-11s | %-5s | %-5s | %-18s | %-22s%n",
                        s.getRecordId(),
                        truncate(s.getTopic().getTopicCode(), 9),
                        truncate(s.getTopic().getTopicName(), 34),
                        truncate(s.getTopicClass().getClassFormat(), 13),
                        truncate(s.getAvailability().getCampus(), 20),
                        s.getAvailability().getSemester(),
                        s.getAvailability().getAvailabilityNo(),
                        s.getClassInstance().getClassInstanceNo(),
                        truncate(s.getClassInstance().getDay(), 9),
                        s.getClassInstance().getStartDate().format(dateFmt),
                        s.getClassInstance().getStartTime(),
                        s.getClassInstance().getEndTime(),
                        truncate(s.getClassInstance().getBuilding(), 18),
                        truncate(s.getClassInstance().getRoom(), 22));
            }
        }

        public void printBrowseTable(List<SearchService.BrowseClassSummary> classes) {
            if (classes == null || classes.isEmpty()) {
                messageWriter.printWarning("No class groups found.");
                return;
            }
            System.out.println(styler.cyanTitle("\nBrowse Classes (combined by topic, availability, class and instance)"));
            System.out.printf(styler.bold("%-4s | %-9s | %-34s | %-15s | %-20s | %-3s | %-5s | %-13s | %-5s | %-10s%n"),
                    "ID", "Topic", "Name", "Attendance", "Campus", "Sem", "Avail", "Class", "Inst", "Rows");
            System.out.println("-------------------------------------------------------------------------------------------------------------------------------------");
            for (SearchService.BrowseClassSummary s : classes) {
                System.out.printf("%-4d | %-9s | %-34s | %-15s | %-20s | %-3s | %-5d | %-13s | %-5d | %-10d%n",
                        s.getFirstRecordId(), truncate(s.getTopicCode(), 9), truncate(s.getTopicName(), 34),
                        truncate(s.getAttendanceMode(), 15), truncate(s.getCampus(), 20), s.getSemester(),
                        s.getAvailabilityNo(), truncate(s.getClassFormat(), 13), s.getClassInstanceNo(), s.getOccurrenceCount());
            }
        }

        public void printTimetableSummary(List<Timetable> timetables) {
            if (timetables == null || timetables.isEmpty()) {
                messageWriter.printWarning("No timetables have been generated yet.");
                return;
            }
            System.out.println(styler.cyanTitle("\nTimetables"));
            System.out.printf(styler.bold("%-8s | %-25s | %-8s | %-7s | %-6s | %-8s%n"), "Code", "Name", "Semester", "Classes", "Score", "Warnings");
            System.out.println("--------------------------------------------------------------------------------");
            for (Timetable t : timetables) {
                System.out.printf("%-8s | %-25s | %-8s | %-7d | %-6d | %-8d%n",
                        t.getTimetableCode(), truncate(t.getTimetableName(), 25), t.getSemester(), t.getSchedules().size(), t.getScore(), t.getWarnings().size());
            }
        }

        public void printTimetable(Timetable timetable) {
            if (timetable == null) {
                messageWriter.printWarning("Timetable not found.");
                return;
            }
            System.out.println(styler.cyanTitle("\n" + timetable.getTimetableCode() + " - " + timetable.getTimetableName()));
            System.out.println(styler.bold("Semester: ") + timetable.getSemester() + "   " + styler.bold("Allow lecture overlap: ") + timetable.isAllowOverlap());
            printClassTable(timetable.getSchedules());
            if (!timetable.getWarnings().isEmpty()) {
                messageWriter.printWarning("Issues detected:");
                for (String warning : timetable.getWarnings()) System.out.println(" - " + warning);
            }
        }

        private String truncate(String value, int width) {
            if (value == null) return "";
            if (value.length() <= width) return value;
            if (width <= 3) return value.substring(0, width);
            return value.substring(0, width - 3) + "...";
        }
    }
}
