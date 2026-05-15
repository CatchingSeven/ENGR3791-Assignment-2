package presentation;

import java.util.List;

/**
 * Handles text styling, colouration, and tabular data formatting.
 */
public class OutputFormatter {

    // ANSI escape codes for basic console colours
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";

    /**
     * Prints an error message in red text.
     */
    public void printError(String message) {
        System.out.println(ANSI_RED + "Error: " + message + ANSI_RESET);
    }

    /**
     * Prints a success message in green text.
     */
    public void printSuccess(String message) {
        System.out.println(ANSI_GREEN + "Success: " + message + ANSI_RESET);
    }

    /**
     * Formats a list of string arrays into a readable, aligned console table
     * using dynamically calculated column widths.
     * * @param headers The column titles.
     * @param data The row data to display.
     */
    public void printTable(String[] headers, List<String[]> data) {
        if (headers == null || headers.length == 0) return;

        int[] columnWidths = new int[headers.length];

        // Initialize widths with the length of the headers
        for (int i = 0; i < headers.length; i++) {
            columnWidths[i] = headers[i].length();
        }

        // Iterate through data to find the maximum width required for each column
        if (data != null) {
            for (String[] row : data) {
                for (int i = 0; i < row.length && i < headers.length; i++) {
                    if (row[i] != null && row[i].length() > columnWidths[i]) {
                        columnWidths[i] = row[i].length();
                    }
                }
            }
        }

        // Build the dynamic format string (e.g., "%-20s %-15s %-30s%n")
        StringBuilder formatBuilder = new StringBuilder();
        for (int width : columnWidths) {
            // Add 3 spaces of padding between columns for readability
            formatBuilder.append("%-").append(width + 3).append("s");
        }
        formatBuilder.append("%n");
        String formatString = formatBuilder.toString();

        // Print Headers
        System.out.printf(formatString, (Object[]) headers);

        // Print Separator Line
        StringBuilder separator = new StringBuilder();
        for (int width : columnWidths) {
            separator.append("-".repeat(width)).append("   ");
        }
        System.out.println(separator.toString());

        // Print Data Rows
        if (data != null) {
            for (String[] row : data) {
                // Ensure the row array matches the header length to prevent formatting errors
                Object[] formattedRow = new Object[headers.length];
                for(int i = 0; i < headers.length; i++) {
                    formattedRow[i] = (i < row.length && row[i] != null) ? row[i] : "";
                }
                System.out.printf(formatString, formattedRow);
            }
        }
    }
}