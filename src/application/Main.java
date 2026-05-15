package application;

import businesslogic.ImportService;
import businesslogic.PreferenceEngine;
import businesslogic.ScheduleEngine;
import businesslogic.SearchService;
import businesslogic.ValidationService;
import persistence.PerseveranceAdapter;
import presentation.ConsoleUI;
import presentation.InputHandler;
import presentation.OutputFormatter;

/**
 * The main entry point for the Timetable Optimiser application.
 * Responsible for dependency injection and initialising the application layers.
 */
public class Main {

    public static void main(String[] args) {

        // --- Layer 4: Persistence ---
        PerseveranceAdapter persistenceAdapter = new PerseveranceAdapter();

        // --- Layer 3: Business Logic ---
        ValidationService validationService = new ValidationService();
        ImportService importService = new ImportService(persistenceAdapter);
        ScheduleEngine scheduleEngine = new ScheduleEngine();
        PreferenceEngine preferenceEngine = new PreferenceEngine();
        SearchService searchService = new SearchService();

        // --- Layer 2: Application (Controllers) ---
        TimetableController timetableController = new TimetableController(scheduleEngine, preferenceEngine, persistenceAdapter);
        ClassController classController = new ClassController(searchService);
        MenusController menusController = new MenusController();

        // --- Layer 1: Presentation ---
        OutputFormatter outputFormatter = new OutputFormatter();
        ConsoleUI consoleUI = new ConsoleUI(outputFormatter);
        InputHandler inputHandler = new InputHandler();

        // --- Application Execution ---
        // 1. Display the mandatory ASCII banner on startup
        consoleUI.displayAsciiBanner();

        // 2. Hand over control to the MenusController to start the application loop
        // Note: In a full implementation, menusController would need references to
        // the other controllers and UI elements to route commands effectively.
        menusController.startApplication();
    }
}
