package presentation;

import java.util.Scanner;

/**
 * Manages user input, enforcing validation loops and global commands (like exit).
 */
public class InputHandler {

    private Scanner scanner;

    public InputHandler() {
        this.scanner = new Scanner(System.in);
    }

    /**
     * Prompts the user for a yes/no confirmation, trapping them until 'y' or 'n' is entered.
     * * @param prompt The warning message to display.
     * @return true if 'y', false if 'n'.
     */
    public boolean getYesNoConfirmation(String prompt) {
        while (true) {
            System.out.print(prompt + " (y/n): ");
            String input = scanner.nextLine().trim().toLowerCase();

            // Exit watcher check can be integrated here
            if (input.equals("exit")) {
                // TODO: Route to global exit command
            }

            if (input.equals("y")) {
                return true;
            } else if (input.equals("n")) {
                return false;
            } else {
                System.out.println("Invalid input. Please enter 'y' or 'n'.");
            }
        }
    }

    /**
     * Gets an integer choice within a specific range for menu selection.
     */
    public int getValidMenuChoice(int min, int max) {
        // TODO: Implement try-catch for NumberFormatException inside a while loop
        return -1;
    }
}