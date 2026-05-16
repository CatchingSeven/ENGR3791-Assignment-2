package presentation;

import java.util.Scanner;

/** Layer 1 component: InputHandler with InputReader, MenuValidator and ExitWatcher responsibilities. */
public class InputHandler {
    private final Scanner scanner;
    private final OutputFormatter.MessageWriter messageWriter;

    public final InputReader inputReader = new InputReader();
    public final MenuValidator menuValidator = new MenuValidator();
    public final ExitWatcher exitWatcher = new ExitWatcher();

    public InputHandler(OutputFormatter formatter) {
        this.scanner = new Scanner(System.in);
        this.messageWriter = formatter.messageWriter;
    }

    public static class ExitRequestedException extends RuntimeException {
        public ExitRequestedException() { super("Exit requested"); }
    }

    public class InputReader {
        public String readString(String prompt) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            exitWatcher.checkExitCommand(input);
            return input;
        }

        public String readOptional(String prompt) {
            return readString(prompt);
        }
    }

    public class MenuValidator {
        public int readValidIntRange(String prompt, int min, int max) {
            while (true) {
                String input = inputReader.readString(prompt);
                try {
                    int choice = Integer.parseInt(input);
                    if (choice >= min && choice <= max) return choice;
                    messageWriter.printError("Invalid option. Enter a number between " + min + " and " + max + ".");
                } catch (NumberFormatException e) {
                    messageWriter.printError("Invalid format. Enter a whole number.");
                }
            }
        }

        public int readAnyInt(String prompt) {
            while (true) {
                String input = inputReader.readString(prompt);
                try {
                    return Integer.parseInt(input);
                } catch (NumberFormatException e) {
                    messageWriter.printError("Invalid format. Enter a whole number.");
                }
            }
        }

        public boolean requestConfirmation(String prompt) {
            while (true) {
                String input = inputReader.readString(prompt + " (Y/N): ").trim().toUpperCase();
                if (input.equals("Y") || input.equals("YES")) return true;
                if (input.equals("N") || input.equals("NO")) return false;
                messageWriter.printError("Invalid input. Enter Y or N.");
            }
        }
    }

    public class ExitWatcher {
        public void checkExitCommand(String input) {
            if (input != null && input.equalsIgnoreCase("EXIT")) {
                throw new ExitRequestedException();
            }
        }
    }
}
