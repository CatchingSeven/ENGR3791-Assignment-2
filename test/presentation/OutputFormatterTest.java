package presentation;

import domain.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OutputFormatterTest {

    private final PrintStream originalOut = System.out;

    @AfterEach
    void restoreOutput() {
        System.setOut(originalOut);
    }

    @Test
    @Tag("Critical")
    @Tag("Lachlan")
    @DisplayName("OutputFormatter formats class lists for console output")
    void formatsClassListsForConsoleOutput() {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        System.setOut(new PrintStream(output));

        OutputFormatter formatter = new OutputFormatter();
        formatter.tablePrinter.printClassTable(List.of(createSchedule()));

        String result = output.toString();

        assertTrue(result.contains("Class Data"));
        assertTrue(result.contains("ENGR3791"));
        assertTrue(result.contains("Software Testing"));
        assertTrue(result.contains("Workshop"));
        assertTrue(result.contains("Tonsley"));
        assertTrue(result.contains("Monday"));
    }

    @Test
    @Tag("Critical")
    @Tag("Lachlan")
    @DisplayName("OutputFormatter formats timetable details for console output")
    void formatsTimetableDetailsForConsoleOutput() {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        System.setOut(new PrintStream(output));

        OutputFormatter formatter = new OutputFormatter();

        Timetable timetable = new Timetable(
                "TT1",
                "Main Timetable",
                "S1",
                false
        );

        timetable.getSchedules().add(createSchedule());

        formatter.tablePrinter.printTimetable(timetable);

        String result = output.toString();

        assertTrue(result.contains("TT1"));
        assertTrue(result.contains("Main Timetable"));
        assertTrue(result.contains("Semester"));
        assertTrue(result.contains("S1"));
        assertTrue(result.contains("ENGR3791"));
    }

    @Test
    @Tag("Critical")
    @Tag("Lachlan")
    @DisplayName("OutputFormatter formats messages for console output")
    void formatsMessagesForConsoleOutput() {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        System.setOut(new PrintStream(output));

        OutputFormatter formatter = new OutputFormatter();

        formatter.messageWriter.printSuccess("Saved successfully");
        formatter.messageWriter.printError("Invalid input");
        formatter.messageWriter.printWarning("Check timetable");
        formatter.messageWriter.printInfo("Information message");

        String result = output.toString();

        assertTrue(result.contains("[SUCCESS] Saved successfully"));
        assertTrue(result.contains("[ERROR] Invalid input"));
        assertTrue(result.contains("[WARNING] Check timetable"));
        assertTrue(result.contains("Information message"));
    }

    private Schedule createSchedule() {
        return new Schedule(
                1,
                new Topic("ENGR3791", "Software Testing"),
                new ClassAvailability("Internal", "Tonsley", "S1", 1),
                new TopicClass("Workshop", "W01"),
                new ClassInstance(
                        1,
                        "W01",
                        LocalDate.of(2026, 3, 1),
                        LocalDate.of(2026, 6, 1),
                        LocalTime.of(9, 0),
                        LocalTime.of(10, 0),
                        "Monday",
                        "Tonsley Building",
                        "Room 101"
                )
        );
    }
}