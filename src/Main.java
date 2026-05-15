

import persistence.PersistenceAdapter;
import service.ImportService;
import service.PreferenceEngine;
import service.ScheduleEngine;
import service.SearchService;
import service.ValidationService;
import controller.ClassController;
import controller.MenusController;
import controller.TimetableController;
import presentation.ConsoleUI;

/**
 * Main serves as the entry point and Composition Root for the application.
 * It strictly handles the instantiation and dependency injection (DI) of all layers
 * from the bottom up (Layer 4 -> Layer 3 -> Layer 2 -> Layer 1).
 */
public class Main {

    public static void main(String[] args) {
        try {
            // ==========================================
            // 1. Layer 4: Persistence
            // ==========================================
            // Initializes the primary data access component.
            PersistenceAdapter persistenceAdapter = new PersistenceAdapter();

            // ==========================================
            // 2. Layer 3: Business Logic (Services Domain)
            // ==========================================
            // Initializes services and injects the PersistenceAdapter so they can read/write data.
            ImportService importService = new ImportService();
            ScheduleEngine scheduleEngine = new ScheduleEngine();
            PreferenceEngine preferenceEngine = new PreferenceEngine();
            SearchService searchService = new SearchService();
            ValidationService validationService = new ValidationService();

            // ==========================================
            // 3. Layer 2: Application (Controllers)
            // ==========================================
            // Initializes controllers and injects the required Layer 3 services.
            MenusController menusController = new MenusController();

            // Note: Controller constructors are assumed to take the services they coordinate.
            ClassController classController = new ClassController(
                    searchService
            );

            TimetableController timetableController = new TimetableController(
                    scheduleEngine

            );

            // ==========================================
            // 4. Layer 1: Presentation
            // ==========================================
            // Initializes the UI and injects the Application controllers to route user commands.
            // (InputHandler and OutputFormatter are instantiated within ConsoleUI's constructor
            // or passed along depending on your specific implementation setup).
            ConsoleUI consoleUI = new ConsoleUI(
                    menusController,
                    classController,
                    timetableController
            );

            // ==========================================
            // 5. Start the Application
            // ==========================================
            // Traps the user in the main application loop.
            consoleUI.start();

        } catch (Exception e) {
            // Top-level error handling to catch any fatal initialization or runtime crashes.
            System.err.println("\n[FATAL ERROR] An unexpected system failure occurred.");
            System.err.println("Details: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}