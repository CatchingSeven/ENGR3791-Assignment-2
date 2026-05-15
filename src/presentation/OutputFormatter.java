package presentation;

import domain.ClassInstance;
import java.util.List;

/**
 * OutputFormatter manages the visual presentation of data in the console.
 */
public class OutputFormatter {

    public final AnsiStyler styler = new AnsiStyler();
    public final TablePrinter tablePrinter = new TablePrinter();
    public final MessageWriter messageWriter = new MessageWriter();

    // ==========================================
    // Internal Components
    // ==========================================

    public class AnsiStyler {
        public static final String RESET = "\u001B[0m";
        public static final String BOLD = "\u001B[1m";
        public static final String ITALIC = "\u001B[3m";
        public static final String UNDERLINE = "\u001B[4m";
        public static final String RED = "\u001B[31m";
        public static final String GREEN = "\u001B[32m";
        public static final String YELLOW = "\u001B[33m";
        public static final String CYAN = "\u001B[36m";

        public String applyBold(String text) { return BOLD + text + RESET; }
        public String applyRed(String text) { return RED + text + RESET; }
        public String applyGreen(String text) { return GREEN + text + RESET; }
        public String applyCyanTitle(String text) { return CYAN + BOLD + UNDERLINE + text + RESET; }
    }

    public class MessageWriter {
        public void printError(String message) {
            System.out.println(styler.applyRed("[ERROR] " + message));
        }

        public void printSuccess(String message) {
            System.out.println(styler.applyGreen("[SUCCESS] " + message));
        }

        public void printWarning(String message) {
            System.out.println(styler.applyBold(AnsiStyler.YELLOW + "[WARNING] " + message + AnsiStyler.RESET));
        }

        public void printImportResults(int newRecords, int updatedRecords) {
            System.out.println(styler.applyBold("\n--- Import Complete ---"));
            printSuccess("Total new records imported: " + newRecords);
            printSuccess("Total existing records updated: " + updatedRecords);
            System.out.println("-----------------------\n");
        }
    }

    public class TablePrinter {
        /**
         * Formats class instance data cleanly into a console table.
         */
        public void printClassTable(List<ClassInstance> classes) {
            if (classes == null || classes.isEmpty()) {
                messageWriter.printWarning("No classes found to display.");
                return;
            }

            System.out.println(styler.applyCyanTitle("\nClass Schedule Data"));
            System.out.printf(AnsiStyler.BOLD + "%-5s | %-12s | %-12s | %-6s | %-6s | %-10s | %-20s | %-10s%n" + AnsiStyler.RESET,
                    "ID", "Start Date", "End Date", "Start", "End", "Day", "Building", "Room");
            System.out.println("------------------------------------------------------------------------------------------------------");

            for (ClassInstance c : classes) {
                System.out.printf("%-5d | %-12s | %-12s | %-6s | %-6s | %-10s | %-20s | %-10s%n",
                        c.getClassInstanceNo(), c.getStartDate(), c.getEndDate(),
                        c.getStartTime(), c.getEndTime(), c.getDay(), c.getBuilding(), c.getRoom());
            }
            System.out.println("------------------------------------------------------------------------------------------------------\n");
        }
    }
}