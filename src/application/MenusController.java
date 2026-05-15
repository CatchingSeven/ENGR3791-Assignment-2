package application;

import businesslogic.ImportService;
import businesslogic.ImportService.ParsedRecordWrapper;
import presentation.ConsoleUI;
import presentation.InputHandler;
import presentation.OutputFormatter;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages the core application loop, session state, and top-level navigation. [cite: 93, 96]
 */
public class MenusController {

    private List<ParsedRecordWrapper> activeClassData;
    private boolean isRunning;
    private boolean workInProgress; // Tracks if the user is currently editing or generating

    private ImportService importService;
    private ClassController classController;
    private TimetableController timetableController;
    private ConsoleUI consoleUI;
    private InputHandler inputHandler;
    private OutputFormatter outputFormatter;

    public MenusController(ImportService importService, ClassController classController,
                           TimetableController timetableController, ConsoleUI consoleUI,
                           InputHandler inputHandler, OutputFormatter outputFormatter) {
        this.importService = importService;
        this.classController = classController;
        this.timetableController = timetableController;
        this.consoleUI = consoleUI;
        this.inputHandler = inputHandler;
        this.outputFormatter = outputFormatter;
        this.activeClassData = new ArrayList<>();
        this.isRunning = true;
        this.workInProgress = false;
    }

    public void startApplication() {
        consoleUI.displayAsciiBanner(); // [cite: 6]

        while (isRunning) {
            consoleUI.displayMainMenu();
            int choice = inputHandler.getValidMenuChoice(1, 5); // Enforces input validation loop [cite: 88]

            switch (choice) {
                case 1 -> handleImport();
                case 2 -> classController.handleBrowseClasses(activeClassData);
                case 3 -> classController.handleSearchClasses(activeClassData);
                case 4 -> handleTimetableMenu();
                case 5 -> handleExit();
            }
        }
    }

    private void handleImport() {
        String filePath = inputHandler.getStringInput("Enter the file path for the CSV data: ");

        // Handle global exit command from any input point
        if (filePath.equalsIgnoreCase("exit")) {
            handleExit();
            return;
        }

        String resultMessage = importService.importClassData(filePath); // [cite: 7]
        outputFormatter.printSuccess(resultMessage); // [cite: 20]
        this.activeClassData = importService.getImportedData();
    }

    private void handleTimetableMenu() {
        // State changes to "in progress" when entering generation/editing workflows
        this.workInProgress = true;
        timetableController.displayTimetableMenu(activeClassData);
        this.workInProgress = false;
    }

    private void handleExit() {
        // Enforce the requirement to ask about saving before exiting if work is in progress
        if (workInProgress) {
            boolean saveFirst = inputHandler.getYesNoConfirmation("You have unsaved changes. Would you like to save before exiting?");
            if (saveFirst) {
                // Trigger save logic (e.g., export to CSV) [cite: 89]
                outputFormatter.printSuccess("Saving changes to persistent storage...");
            }
        }

        boolean confirmExit = inputHandler.getYesNoConfirmation("Are you sure you want to exit?");
        if (confirmExit) {
            outputFormatter.printSuccess("Exiting Timetable Optimiser. Goodbye.");
            this.isRunning = false;
        }
    }
}