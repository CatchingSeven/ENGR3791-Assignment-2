package presentation;

/**
 * Handles the display of menus, banners, and primary user interfaces.
 */
public class ConsoleUI {

    private OutputFormatter formatter;

    public ConsoleUI(OutputFormatter formatter) {
        this.formatter = formatter;
    }

    /**
     * Renders the mandatory ASCII art title banner upon application startup.
     */
    public void displayAsciiBanner() {
        // TODO: Insert project team's chosen ASCII art string
        System.out.println("=========================================");
        System.out.println("         TIMETABLE OPTIMISER             ");
        System.out.println("=========================================");
    }

    /**
     * Displays the primary navigation menu.
     */
    public void displayMainMenu() {
        System.out.println("\n--- Main Menu ---");
        System.out.println("1. Import Classes");
        System.out.println("2. Browse Classes");
        System.out.println("3. Search Classes");
        System.out.println("4. Manage Timetables");
        System.out.println("5. Exit");
        System.out.print("Please select an option: ");
    }

    // Additional methods for rendering sub-menus
}