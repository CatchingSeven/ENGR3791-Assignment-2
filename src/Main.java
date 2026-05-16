import application.ClassController;
import application.MenusController;
import application.TimetableController;
import businesslogic.*;
import persistence.PersistenceAdapter;
import presentation.ConsoleUI;

/**
 * Composition root. Instantiates layers from bottom to top:
 * Layer 4 Persistence/Domain -> Layer 3 Business Logic -> Layer 2 Application -> Layer 1 Presentation.
 */
public class Main {
    public static void main(String[] args) {
        try {
            PersistenceAdapter persistenceAdapter = new PersistenceAdapter();

            ImportService importService = new ImportService();
            SearchService searchService = new SearchService();
            ScheduleEngine scheduleEngine = new ScheduleEngine();
            PreferenceEngine preferenceEngine = new PreferenceEngine();
            ValidationService validationService = new ValidationService();

            MenusController menusController = new MenusController();
            ClassController classController = new ClassController(importService, searchService, validationService, persistenceAdapter);
            TimetableController timetableController = new TimetableController(
                    scheduleEngine, preferenceEngine, validationService, persistenceAdapter, searchService
            );

            ConsoleUI consoleUI = new ConsoleUI(
                    menusController, classController, timetableController, validationService, persistenceAdapter
            );
            consoleUI.start();
        } catch (Exception e) {
            System.err.println("[FATAL ERROR] " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
