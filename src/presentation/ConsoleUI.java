package presentation;

import controller.ClassController;
import controller.MenusController;
import controller.TimetableController;

/**
 * ConsoleUI is the primary user interface driver.
 */
public class ConsoleUI {

    private final MenusController menusController;
    private final ClassController classController;
    private final TimetableController timetableController;

    private final OutputFormatter formatter;
    private final InputHandler inputHandler;

    public final AsciiBanner asciiBanner = new AsciiBanner();
    public final MainMenu mainMenu = new MainMenu();
    public final SubMenu subMenu = new SubMenu();

    public ConsoleUI(MenusController menusController, ClassController classController, TimetableController timetableController) {
        this.menusController = menusController;
        this.classController = classController;
        this.timetableController = timetableController;

        this.formatter = new OutputFormatter();
        this.inputHandler = new InputHandler(this.menusController, this.formatter);
    }

    public void start() {
        asciiBanner.printTitle();
        mainMenu.display();
    }

    // ==========================================
    // Internal Components
    // ==========================================

    public class AsciiBanner {
        public void printTitle() {
            String banner = formatter.styler.applyCyanTitle(
                    "  _____ _                _        _     _        \n" +
                            " |_   _(_)_ __ ___   ___| |_ __ _| |__ | | ___   \n" +
                            "   | | | | '_ ` _ \\ / _ \\ __/ _` | '_ \\| |/ _ \\  \n" +
                            "   | | | | | | | | |  __/ || (_| | |_) | |  __/  \n" +
                            "   |_| |_|_| |_| |_|\\___|\\__\\__,_|_.__/|_|\\___|  \n" +
                            "   ___        _   _           _                  \n" +
                            "  / _ \\ _ __ | |_(_)_ __ ___ (_)_______ _ __     \n" +
                            " | | | | '_ \\| __| | '_ ` _ \\| |_  / _ \\ '__|    \n" +
                            " | |_| | |_) | |_| | | | | | | |/ /  __/ |       \n" +
                            "  \\___/| .__/ \\__|_|_| |_| |_|_/___\\___|_|       \n" +
                            "       |_|                                       "
            );
            System.out.println(banner);
            System.out.println(formatter.styler.applyBold((" Type 'EXIT' at any prompt to close the application.\n")));
        }
    }

    public class MainMenu {
        public void display() {
            while (true) {
                System.out.println(formatter.styler.applyBold("\n--- Main Menu ---"));
                System.out.println("1. Import Class Data (CSV)");
                System.out.println("2. Browse / View Classes");
                System.out.println("3. Manage Classes (Search/Edit/Delete)");
                System.out.println("4. Timetable Management (Generate/View/Export)");
                System.out.println("5. Exit");

                int choice = inputHandler.menuValidator.readValidIntRange("Select an option: ", 1, 5);

                switch (choice) {
                    case 1:
                        // Call controller to import
                        break;
                    case 2:
                        // Call controller to browse
                        break;
                    case 3:
                        subMenu.displayClassManagementMenu();
                        break;
                    case 4:
                        subMenu.displayTimetableMenu();
                        break;
                    case 5:
                        inputHandler.exitWatcher.checkExitCommand("EXIT");
                        break;
                }
            }
        }
    }

    public class SubMenu {
        public void displayClassManagementMenu() {
            System.out.println(formatter.styler.applyBold("\n--- Class Management ---"));
            System.out.println("1. Search Classes");
            System.out.println("2. Edit Class");
            System.out.println("3. Delete Class");
            System.out.println("4. Return to Main Menu");

            int choice = inputHandler.menuValidator.readValidIntRange("Select an option: ", 1, 4);

            if (choice == 2) {
                // Example of enforcing confirmation via UI before passing to controller
                boolean confirmed = inputHandler.menuValidator.requestConfirmation("WARNING: Are you sure you want to edit this class?");
                // classController.getEditHandler().processEdit(..., ..., confirmed);
            }
            // Logic handled by routing to ClassController...
        }

        public void displayTimetableMenu() {
            System.out.println(formatter.styler.applyBold("\n--- Timetable Management ---"));
            System.out.println("1. Generate New Timetable");
            System.out.println("2. View Existing Timetables");
            System.out.println("3. Edit Timetable (Swap Classes)");
            System.out.println("4. Export Timetable");
            System.out.println("5. Return to Main Menu");

            int choice = inputHandler.menuValidator.readValidIntRange("Select an option: ", 1, 5);
            // Logic handled by routing to TimetableController...

            if (choice == 1) {
                menusController.getSessionState().setHasUnsavedChanges(true); // Flag session state
            }
        }
    }
}