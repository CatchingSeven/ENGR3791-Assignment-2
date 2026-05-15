package application;

import businesslogic.ScheduleEngine;
import businesslogic.PreferenceEngine;
import domain.ClassInstance;
import domain.Timetable;
import persistence.PerseveranceAdapter;
import presentation.InputHandler;
import presentation.OutputFormatter;

import java.util.List;

/**
 * Coordinates operations related to generating, managing, and exporting timetables.
 */
public class TimetableController {

    private ScheduleEngine scheduleEngine;
    private PreferenceEngine preferenceEngine;
    private PerseveranceAdapter persistenceAdapter;
    private InputHandler inputHandler;
    private OutputFormatter outputFormatter;

    public TimetableController(ScheduleEngine scheduleEngine, PreferenceEngine preferenceEngine,
                               PerseveranceAdapter persistenceAdapter, InputHandler inputHandler,
                               OutputFormatter outputFormatter) {
        this.scheduleEngine = scheduleEngine;
        this.preferenceEngine = preferenceEngine;
        this.persistenceAdapter = persistenceAdapter;
        this.inputHandler = inputHandler;
        this.outputFormatter = outputFormatter;
    }

    /**
     * Displays the timetable sub-menu and routes commands.
     * @param availableClasses The current session state of available class instances.
     */
    public void displayTimetableMenu(List<ClassInstance> availableClasses) {
        boolean inTimetableMenu = true;
        while (inTimetableMenu) {
            outputFormatter.printTimetableMenu();
            int choice = inputHandler.getValidMenuChoice(1, 4);

            switch (choice) {
                case 1 -> handleGenerateTimetable(availableClasses);
                case 2 -> handleBrowseTimetables();
                case 3 -> handleExportTimetable();
                case 4 -> inTimetableMenu = false; // Returns to Main Menu
            }
        }
    }

    private void handleGenerateTimetable(List<ClassInstance> availableClasses) {
        if (availableClasses == null || availableClasses.isEmpty()) {
            outputFormatter.printError("Cannot generate a timetable. No classes have been imported.");
            return;
        }

        // Ensures previously used settings are retrieved
        try {
            String lastSettings = persistenceAdapter.loadSettings();
            if (!lastSettings.isEmpty()) {
                outputFormatter.printSuccess("Loaded last used settings: " + lastSettings);
            }
        } catch (Exception e) {
            outputFormatter.printError("Could not load previous settings.");
        }

        // Delegates complex generation steps to the engine
        outputFormatter.printSuccess("Initiating timetable generation sequence...");

        // TODO: Implement preference gathering and ScheduleEngine invocation
    }

    private void handleBrowseTimetables() {
        // Implementation for browsing generated timetables
    }

    private void handleExportTimetable() {
        // Implementation for exporting timetables
    }
}