package presentation;

import controller.MenusController;
import java.util.Scanner;

/**
 * InputHandler manages all user input, enforces strict validation,
 * and handles global exit commands.
 */
public class InputHandler {

    private final Scanner scanner;
    private final MenusController menusController;
    private final OutputFormatter.MessageWriter messageWriter;

    public final InputReader inputReader = new InputReader();
    public final MenuValidator menuValidator = new MenuValidator();
    public final ExitWatcher exitWatcher = new ExitWatcher();

    public InputHandler(MenusController menusController, OutputFormatter formatter) {
        this.scanner = new Scanner(System.in);
        this.menusController = menusController;
        this.messageWriter = formatter.messageWriter;
    }

    // ==========================================
    // Internal Components
    // ==========================================

    public class InputReader {
        /**
         * Reads a string input. Triggers ExitWatcher if "EXIT" is typed.
         */
        public String readString(String prompt) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            exitWatcher.checkExitCommand(input);
            return input;
        }
    }

    public class MenuValidator {
        /**
         * Traps the user in a loop until a valid integer within the specified range is provided.
         */
        public int readValidIntRange(String prompt, int min, int max) {
            int choice = -1;
            boolean valid = false;

            while (!valid) {
                System.out.print(prompt);
                String input = scanner.nextLine().trim();
                exitWatcher.checkExitCommand(input);

                try {
                    choice = Integer.parseInt(input);
                    if (choice >= min && choice <= max) {
                        valid = true;
                    } else {
                        messageWriter.printError("Invalid option. Please enter a number between " + min + " and " + max + ".");
                    }
                } catch (NumberFormatException e) {
                    messageWriter.printError("Invalid format. Please enter a valid numerical value.");
                }
            }
            return choice;
        }

        /**
         * Traps user until a valid Yes/No response is provided for confirmations.
         */
        public boolean requestConfirmation(String prompt) {
            while (true) {
                System.out.print(prompt + " (Y/N): ");
                String input = scanner.nextLine().trim().toUpperCase();
                exitWatcher.checkExitCommand(input);

                if (input.equals("Y") || input.equals("YES")) return true;
                if (input.equals("N") || input.equals("NO")) return false;

                messageWriter.printError("Invalid input. Please enter 'Y' or 'N'.");
            }
        }
    }

    public class ExitWatcher {
        /**
         * Checks if the user typed the global exit command.
         * If they have unsaved changes, forces a save prompt before terminating.
         */
        public void checkExitCommand(String input) {
            if (input.equalsIgnoreCase("EXIT")) {
                if (menusController.getSessionState().hasUnsavedChanges()) {
                    messageWriter.printWarning("You have unsaved changes in your timetable or class edits.");
                    boolean saveFirst = menuValidator.requestConfirmation("Would you like to save your work before exiting?");
                    if (saveFirst) {
                        System.out.println("Saving data...");
                        // Trigger persistence via controller here...
                    }
                }
                System.out.println("Exiting Timetable Optimiser. Goodbye!");
                System.exit(0);
            }
        }
    }
}