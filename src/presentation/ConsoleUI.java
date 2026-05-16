package presentation;

import application.ClassController;
import application.MenusController;
import application.TimetableController;
import businesslogic.ImportService;
import businesslogic.SearchService;
import businesslogic.ValidationService;
import domain.Schedule;
import domain.Timetable;
import domain.TimetablePreferences;
import persistence.PersistenceAdapter;

import java.io.IOException;
import java.util.*;

/** Layer 1 component: ConsoleUI with MainMenu, SubMenu and AsciiBanner responsibilities. */
public class ConsoleUI {
    private final MenusController menusController;
    private final ClassController classController;
    private final TimetableController timetableController;
    private final ValidationService validationService;
    private final PersistenceAdapter persistenceAdapter;

    private final OutputFormatter formatter;
    private final InputHandler inputHandler;

    public final AsciiBanner asciiBanner = new AsciiBanner();
    public final MainMenu mainMenu = new MainMenu();
    public final SubMenu subMenu = new SubMenu();

    public ConsoleUI(MenusController menusController, ClassController classController,
                     TimetableController timetableController, ValidationService validationService,
                     PersistenceAdapter persistenceAdapter) {
        this.menusController = menusController;
        this.classController = classController;
        this.timetableController = timetableController;
        this.validationService = validationService;
        this.persistenceAdapter = persistenceAdapter;
        this.formatter = new OutputFormatter();
        this.inputHandler = new InputHandler(formatter);
    }

    public void start() {
        try {
            classController.loadSavedClasses();
            TimetablePreferences saved = persistenceAdapter.loadSettings();
            if (saved != null) menusController.getSessionState().setLastUsedSettings(saved);
            asciiBanner.printTitle();
            mainMenu.display();
        } catch (InputHandler.ExitRequestedException e) {
            handleExit();
        } catch (Exception e) {
            formatter.messageWriter.printError("Unexpected error: " + e.getMessage());
        }
    }

    private void handleExit() {
        try {
            if (menusController.getSessionState().hasUnsavedChanges()) {
                formatter.messageWriter.printWarning("You have unsaved timetable settings or generated timetables that may not be exported.");
                boolean save = inputHandler.menuValidator.requestConfirmation("Save last-used timetable settings before exiting?");
                if (save && menusController.getSessionState().getLastUsedSettings() != null) {
                    persistenceAdapter.saveSettings(menusController.getSessionState().getLastUsedSettings());
                    formatter.messageWriter.printSuccess("Settings saved.");
                }
            }
        } catch (InputHandler.ExitRequestedException ignored) {
            // A second EXIT during the save prompt exits immediately.
        } catch (IOException e) {
            formatter.messageWriter.printError("Could not save settings: " + e.getMessage());
        }
        System.out.println("Goodbye.");
    }

    public class AsciiBanner {
        public void printTitle() {
            String banner =
                    "  _______ _                _        _     _        ____        _   _           _              \n" +
                    " |__   __(_)              | |      | |   | |      / __ \\      | | (_)         (_)             \n" +
                    "    | |   _ _ __ ___   ___| |_ __ _| |__ | | ___ | |  | |_ __ | |_ _ _ __ ___  _ ___  ___ _ __ \n" +
                    "    | |  | | '_ ` _ \\ / _ \\ __/ _` | '_ \\| |/ _ \\| |  | | '_ \\| __| | '_ ` _ \\| / __|/ _ \\ '__|\n" +
                    "    | |  | | | | | | |  __/ || (_| | |_) | |  __/| |__| | |_) | |_| | | | | | | \\__ \\  __/ |   \n" +
                    "    |_|  |_|_| |_| |_|\\___|\\__\\__,_|_.__/|_|\\___| \\____/| .__/ \\__|_|_| |_| |_|_|___/\\___|_|   \n" +
                    "                                                             | |                                  \n" +
                    "                                                             |_|                                  ";
            System.out.println(formatter.styler.cyanTitle(banner));
            System.out.println(formatter.styler.italic("Console only. Type EXIT at any prompt to close the application."));
        }
    }

    public class MainMenu {
        public void display() {
            while (true) {
                formatter.messageWriter.heading("Main Menu");
                System.out.println("1. Import class data from CSV");
                System.out.println("2. Browse classes");
                System.out.println("3. View classes");
                System.out.println("4. Search classes");
                System.out.println("5. Edit class");
                System.out.println("6. Delete class");
                System.out.println("7. Generate timetable");
                System.out.println("8. Browse timetables");
                System.out.println("9. View timetable");
                System.out.println("10. Edit timetable (swap class instance)");
                System.out.println("11. Delete timetable");
                System.out.println("12. Export timetable");
                System.out.println("13. Exit");

                int choice = inputHandler.menuValidator.readValidIntRange("Select an option: ", 1, 13);
                try {
                    switch (choice) {
                        case 1: subMenu.importClasses(); break;
                        case 2: formatter.tablePrinter.printBrowseTable(classController.browseClasses()); break;
                        case 3: formatter.tablePrinter.printClassTable(classController.viewClasses()); break;
                        case 4: subMenu.searchClasses(); break;
                        case 5: subMenu.editClass(); break;
                        case 6: subMenu.deleteClass(); break;
                        case 7: subMenu.generateTimetable(); break;
                        case 8: formatter.tablePrinter.printTimetableSummary(timetableController.getTimetables()); break;
                        case 9: subMenu.viewTimetable(); break;
                        case 10: subMenu.editTimetable(); break;
                        case 11: subMenu.deleteTimetable(); break;
                        case 12: subMenu.exportTimetable(); break;
                        case 13: throw new InputHandler.ExitRequestedException();
                        default: formatter.messageWriter.printError("Invalid option.");
                    }
                } catch (IllegalArgumentException | IOException e) {
                    formatter.messageWriter.printError(e.getMessage());
                }
            }
        }
    }

    public class SubMenu {
        public void importClasses() throws IOException {
            formatter.messageWriter.heading("Import Classes");
            formatter.messageWriter.printInfo("Expected CSV header: Topic, Availability, Class, Class instance, Date, Day, Time, Location");
            String path = inputHandler.inputReader.readString("CSV path (blank for sample COMP1002 file): ");
            if (path.isBlank()) path = "COMP1002 Fundamentals of Artificial Intelligence.csv";
            ImportService.ImportResult result = classController.importCsv(path);
            formatter.messageWriter.printSuccess("Import complete. New records: " + result.getNewRecords() + ", Updated records: " + result.getUpdatedRecords());
        }

        public void searchClasses() {
            formatter.messageWriter.heading("Search Classes");
            formatter.messageWriter.printInfo("Leave a field blank to ignore it. Multiple fields are combined with AND.");
            SearchService.SearchCriteria c = new SearchService.SearchCriteria();
            c.topicCode = inputHandler.inputReader.readOptional("Topic code: ");
            c.topicName = inputHandler.inputReader.readOptional("Topic name: ");
            c.attendanceMode = inputHandler.inputReader.readOptional("Attendance mode: ");
            c.campus = inputHandler.inputReader.readOptional("Campus: ");
            c.semester = inputHandler.inputReader.readOptional("Semester (S1/S2): ");
            c.availabilityNumber = inputHandler.inputReader.readOptional("Availability number: ");
            c.classFormat = inputHandler.inputReader.readOptional("Class: ");
            c.classInstance = inputHandler.inputReader.readOptional("Class instance: ");
            c.firstDate = inputHandler.inputReader.readOptional("Date of first class (YYYY-MM-DD): ");
            c.lastDate = inputHandler.inputReader.readOptional("Date of last class (YYYY-MM-DD): ");
            c.day = inputHandler.inputReader.readOptional("Day: ");
            c.startTime = inputHandler.inputReader.readOptional("Start time (HH:mm): ");
            c.endTime = inputHandler.inputReader.readOptional("End time (HH:mm): ");
            c.building = inputHandler.inputReader.readOptional("Building: ");
            c.room = inputHandler.inputReader.readOptional("Room: ");
            formatter.tablePrinter.printClassTable(classController.searchClasses(c));
        }

        public void editClass() throws IOException {
            formatter.messageWriter.heading("Edit Class");
            formatter.tablePrinter.printClassTable(classController.viewClasses());
            int id = inputHandler.menuValidator.readAnyInt("Record ID to edit: ");
            Schedule original = classController.findClassById(id);
            if (original == null) {
                formatter.messageWriter.printError("No class found with record ID " + id + ".");
                return;
            }
            Schedule updated = original.copy();
            System.out.println(formatter.styler.italic("Leave a field blank to keep the current value."));
            applyTextEdit("Topic code", updated.getTopic().getTopicCode(), updated.getTopic()::setTopicCode);
            applyTextEdit("Topic name", updated.getTopic().getTopicName(), updated.getTopic()::setTopicName);
            applyTextEdit("Attendance mode", updated.getAvailability().getAttendanceMode(), updated.getAvailability()::setAttendanceMode);
            applyTextEdit("Campus", updated.getAvailability().getCampus(), updated.getAvailability()::setCampus);
            applyTextEdit("Semester", updated.getAvailability().getSemester(), updated.getAvailability()::setSemester);
            applyIntEdit("Availability number", updated.getAvailability().getAvailabilityNo(), updated.getAvailability()::setAvailabilityNo);
            applyTextEdit("Class", updated.getTopicClass().getClassFormat(), updated.getTopicClass()::setClassFormat);
            applyIntEdit("Class instance", updated.getClassInstance().getClassInstanceNo(), updated.getClassInstance()::setClassInstanceNo);
            applyDateEdit("Date of first class", updated.getClassInstance().getStartDate().toString(), updated);
            applyDateEndEdit("Date of last class", updated.getClassInstance().getEndDate().toString(), updated);
            applyTextEdit("Day", updated.getClassInstance().getDay(), updated.getClassInstance()::setDay);
            applyTimeEdit("Start time", updated.getClassInstance().getStartTime().toString(), true, updated);
            applyTimeEdit("End time", updated.getClassInstance().getEndTime().toString(), false, updated);
            applyTextEdit("Building", updated.getClassInstance().getBuilding(), updated.getClassInstance()::setBuilding);
            applyTextEdit("Room", updated.getClassInstance().getRoom(), updated.getClassInstance()::setRoom);

            validationService.validateSchedule(updated);
            boolean confirmed = inputHandler.menuValidator.requestConfirmation("WARNING: Save these class changes?");
            if (classController.editClass(id, updated, confirmed)) {
                formatter.messageWriter.printSuccess("Class updated and saved to data/classes.csv.");
            } else {
                formatter.messageWriter.printWarning("Class edit cancelled.");
            }
        }

        public void deleteClass() throws IOException {
            formatter.messageWriter.heading("Delete Class");
            formatter.tablePrinter.printClassTable(classController.viewClasses());
            int id = inputHandler.menuValidator.readAnyInt("Record ID to delete: ");
            Schedule target = classController.findClassById(id);
            if (target == null) {
                formatter.messageWriter.printError("No class found with record ID " + id + ".");
                return;
            }
            System.out.println("Target: " + target.compactLabel());
            boolean confirmed = inputHandler.menuValidator.requestConfirmation("WARNING: Permanently delete this class record?");
            if (classController.deleteClass(id, confirmed)) {
                formatter.messageWriter.printSuccess("Class deleted and data/classes.csv updated.");
            } else {
                formatter.messageWriter.printWarning("Class deletion cancelled.");
            }
        }

        public void generateTimetable() throws IOException {
            formatter.messageWriter.heading("Generate Timetable");
            if (classController.getAllClasses().isEmpty()) {
                formatter.messageWriter.printWarning("Import class data before generating a timetable.");
                return;
            }
            TimetablePreferences prefs = collectTimetablePreferences();
            menusController.getSessionState().setLastUsedSettings(prefs.copy());
            persistenceAdapter.saveSettings(prefs);
            Timetable timetable = timetableController.generateTimetable(prefs, classController.getAllClasses());
            if (timetable == null) {
                formatter.messageWriter.printWarning("No valid timetable could be generated with those settings.");
                return;
            }
            menusController.getSessionState().setHasUnsavedChanges(true);
            formatter.messageWriter.printSuccess("Generated " + timetable.getTimetableCode() + " - " + timetable.getTimetableName());
            formatter.tablePrinter.printTimetable(timetable);
        }

        public void viewTimetable() {
            formatter.tablePrinter.printTimetableSummary(timetableController.getTimetables());
            if (timetableController.getTimetables().isEmpty()) return;
            String code = inputHandler.inputReader.readString("Timetable code or name to view: ");
            formatter.tablePrinter.printTimetable(timetableController.findTimetable(code));
        }

        public void editTimetable() {
            formatter.messageWriter.heading("Edit Timetable - Swap Class Instance");
            formatter.tablePrinter.printTimetableSummary(timetableController.getTimetables());
            if (timetableController.getTimetables().isEmpty()) return;
            String code = inputHandler.inputReader.readString("Timetable code or name: ");
            Timetable timetable = timetableController.findTimetable(code);
            if (timetable == null) {
                formatter.messageWriter.printError("Timetable not found.");
                return;
            }
            formatter.tablePrinter.printTimetable(timetable);
            int currentId = inputHandler.menuValidator.readAnyInt("Record ID in timetable to replace: ");
            List<Schedule> candidates = timetableController.getSwapCandidates(timetable, currentId, classController.getAllClasses());
            if (candidates.isEmpty()) {
                formatter.messageWriter.printWarning("No replacement class instances found for the same topic and class.");
                return;
            }
            formatter.messageWriter.heading("Replacement candidates");
            formatter.tablePrinter.printClassTable(candidates);
            int replacementId = inputHandler.menuValidator.readAnyInt("Replacement record ID: ");
            TimetableController.SwapResult result = timetableController.swapClass(code, currentId, replacementId, classController.getAllClasses(), false);
            if (result == TimetableController.SwapResult.WARNING_REQUIRES_CONFIRMATION) {
                for (String warning : timetable.getWarnings()) formatter.messageWriter.printWarning(warning);
                boolean confirm = inputHandler.menuValidator.requestConfirmation("WARNING: Swap causes a clash or commute issue. Complete anyway?");
                result = timetableController.swapClass(code, currentId, replacementId, classController.getAllClasses(), confirm);
            }
            if (result == TimetableController.SwapResult.SUCCESS) {
                menusController.getSessionState().setHasUnsavedChanges(true);
                formatter.messageWriter.printSuccess("Timetable updated.");
                formatter.tablePrinter.printTimetable(timetable);
            } else {
                formatter.messageWriter.printError("Swap failed or was cancelled.");
            }
        }

        public void deleteTimetable() {
            formatter.messageWriter.heading("Delete Timetable");
            formatter.tablePrinter.printTimetableSummary(timetableController.getTimetables());
            if (timetableController.getTimetables().isEmpty()) return;
            String code = inputHandler.inputReader.readString("Timetable code or name to delete: ");
            boolean confirmed = inputHandler.menuValidator.requestConfirmation("WARNING: Delete this timetable?");
            if (timetableController.deleteTimetable(code, confirmed)) {
                formatter.messageWriter.printSuccess("Timetable deleted.");
            } else {
                formatter.messageWriter.printWarning("Timetable deletion cancelled or timetable not found.");
            }
        }

        public void exportTimetable() throws IOException {
            formatter.messageWriter.heading("Export Timetable");
            formatter.tablePrinter.printTimetableSummary(timetableController.getTimetables());
            if (timetableController.getTimetables().isEmpty()) return;
            String code = inputHandler.inputReader.readString("Timetable code or name to export: ");
            Timetable timetable = timetableController.findTimetable(code);
            if (timetable == null) {
                formatter.messageWriter.printError("Timetable not found.");
                return;
            }
            String defaultPath = persistenceAdapter.safeExportPath(timetable.getTimetableName());
            String path = inputHandler.inputReader.readString("Export path (blank for " + defaultPath + "): ");
            if (path.isBlank()) path = defaultPath;
            if (timetableController.exportTimetable(code, path)) {
                menusController.getSessionState().setHasUnsavedChanges(false);
                formatter.messageWriter.printSuccess("Timetable exported to " + path);
            } else {
                formatter.messageWriter.printError("Export failed.");
            }
        }

        private TimetablePreferences collectTimetablePreferences() {
            TimetablePreferences last = menusController.getSessionState().getLastUsedSettings();
            if (last != null) {
                formatter.messageWriter.printInfo("Last used settings loaded. Press Enter on a prompt to keep the displayed default.");
            }
            TimetablePreferences prefs = new TimetablePreferences();
            String defaultName = last == null ? "" : last.getTimetableName();
            String name = promptWithDefault("Timetable name", defaultName);
            prefs.setTimetableName(name);

            String defaultSemester = last == null ? "both" : last.getSemester();
            String semester;
            while (true) {
                semester = promptWithDefault("Semester (1, 2, or both)", defaultSemester);
                if (semester.equalsIgnoreCase("1") || semester.equalsIgnoreCase("2") || semester.equalsIgnoreCase("both")) break;
                formatter.messageWriter.printError("Invalid semester. Enter 1, 2, or both.");
            }
            prefs.setSemester(semester.equals("1") ? "S1" : semester.equals("2") ? "S2" : "both");

            prefs.setSelectedTopicCodes(selectTopics(last));
            prefs.setSelectedCampuses(selectCampuses(last));

            boolean defaultAllow = last != null && last.isAllowLectureOverlap();
            String overlap = promptWithDefault("Allow lecture overlap? (Y/N)", defaultAllow ? "Y" : "N");
            while (!overlap.equalsIgnoreCase("Y") && !overlap.equalsIgnoreCase("YES") && !overlap.equalsIgnoreCase("N") && !overlap.equalsIgnoreCase("NO")) {
                formatter.messageWriter.printError("Enter Y or N.");
                overlap = promptWithDefault("Allow lecture overlap? (Y/N)", defaultAllow ? "Y" : "N");
            }
            prefs.setAllowLectureOverlap(overlap.equalsIgnoreCase("Y") || overlap.equalsIgnoreCase("YES"));

            prefs.setOrderedPreferences(selectOrderedPreferences(last));
            return prefs;
        }

        private String promptWithDefault(String label, String defaultValue) {
            String prompt = defaultValue == null || defaultValue.isBlank()
                    ? label + ": "
                    : label + " [" + defaultValue + "]: ";
            String input = inputHandler.inputReader.readString(prompt);
            return input.isBlank() ? (defaultValue == null ? "" : defaultValue) : input;
        }

        private List<String> selectTopics(TimetablePreferences last) {
            List<String> labels = classController.distinctTopicLabels();
            for (int i = 0; i < labels.size(); i++) System.out.println((i + 1) + ". " + labels.get(i));
            String defaultText = last == null || last.getSelectedTopicCodes().isEmpty() ? "" : String.join(",", last.getSelectedTopicCodes());
            while (true) {
                String input = promptWithDefault("Topics to include (comma numbers/codes, or all)", defaultText);
                List<String> selected = parseTopicSelection(input, labels);
                if (!selected.isEmpty()) return selected;
                formatter.messageWriter.printError("Selecting no topics is invalid.");
            }
        }

        private List<String> parseTopicSelection(String input, List<String> labels) {
            List<String> out = new ArrayList<>();
            if (input.equalsIgnoreCase("all")) {
                for (String label : labels) out.add(label.split(" - ", 2)[0]);
                return out;
            }
            for (String part : input.split(",")) {
                String value = part.trim();
                if (value.isEmpty()) continue;
                try {
                    int index = Integer.parseInt(value);
                    if (index >= 1 && index <= labels.size()) out.add(labels.get(index - 1).split(" - ", 2)[0]);
                } catch (NumberFormatException ignored) {
                    out.add(value.toUpperCase());
                }
            }
            return unique(out);
        }

        private List<String> selectCampuses(TimetablePreferences last) {
            List<String> campuses = classController.distinctCampuses();
            if (campuses.isEmpty()) campuses = Arrays.asList("Bedford Park", "Tonsley", "Flinders City Campus");
            for (int i = 0; i < campuses.size(); i++) System.out.println((i + 1) + ". " + campuses.get(i));
            String defaultText = last == null || last.getSelectedCampuses().isEmpty() ? "all" : String.join(",", last.getSelectedCampuses());
            while (true) {
                String input = promptWithDefault("Campuses (comma numbers/names, or all)", defaultText);
                List<String> selected = parseCampusSelection(input, campuses);
                if (!selected.isEmpty()) return selected;
                formatter.messageWriter.printError("Select at least one campus.");
            }
        }

        private List<String> parseCampusSelection(String input, List<String> campuses) {
            List<String> out = new ArrayList<>();
            if (input.equalsIgnoreCase("all")) return new ArrayList<>(campuses);
            for (String part : input.split(",")) {
                String value = part.trim();
                if (value.isEmpty()) continue;
                try {
                    int index = Integer.parseInt(value);
                    if (index >= 1 && index <= campuses.size()) out.add(campuses.get(index - 1));
                } catch (NumberFormatException ignored) {
                    out.add(value);
                }
            }
            return unique(out);
        }

        private List<String> selectOrderedPreferences(TimetablePreferences last) {
            Map<Integer, String> options = new LinkedHashMap<>();
            options.put(1, "Bedford Park");
            options.put(2, "Tonsley");
            options.put(3, "Flinders City Campus");
            options.put(4, "all at same campus");
            options.put(5, "mornings");
            options.put(6, "afternoons");
            options.put(7, "Monday");
            options.put(8, "Tuesday");
            options.put(9, "Wednesday");
            options.put(10, "Thursday");
            options.put(11, "Friday");
            options.put(12, "evenly spread classes across days");
            options.put(13, "compact classes to as few days as possible");
            System.out.println("Preferences are optional. Lower number entered first = higher priority.");
            for (Map.Entry<Integer, String> e : options.entrySet()) System.out.println(e.getKey() + ". " + e.getValue());
            String defaultText = last == null || last.getOrderedPreferences().isEmpty() ? "" : String.join(",", last.getOrderedPreferences());
            String input = promptWithDefault("Preference order (comma numbers/names, blank for none)", defaultText);
            List<String> out = new ArrayList<>();
            if (input.isBlank()) return out;
            for (String part : input.split(",")) {
                String value = part.trim();
                if (value.isEmpty()) continue;
                try {
                    int option = Integer.parseInt(value);
                    if (options.containsKey(option)) out.add(options.get(option));
                } catch (NumberFormatException ignored) {
                    out.add(value);
                }
            }
            return unique(out);
        }

        private List<String> unique(List<String> list) {
            List<String> out = new ArrayList<>();
            for (String item : list) {
                boolean exists = false;
                for (String existing : out) if (existing.equalsIgnoreCase(item)) exists = true;
                if (!exists) out.add(item);
            }
            return out;
        }

        private void applyTextEdit(String label, String current, TextSetter setter) {
            String value = inputHandler.inputReader.readString(label + " [" + current + "]: ");
            if (!value.isBlank()) setter.set(value);
        }

        private void applyIntEdit(String label, int current, IntSetter setter) {
            while (true) {
                String value = inputHandler.inputReader.readString(label + " [" + current + "]: ");
                if (value.isBlank()) return;
                try {
                    setter.set(Integer.parseInt(value));
                    return;
                } catch (NumberFormatException e) {
                    formatter.messageWriter.printError("Enter a whole number.");
                }
            }
        }

        private void applyDateEdit(String label, String current, Schedule updated) {
            String value = inputHandler.inputReader.readString(label + " [" + current + "]: ");
            if (!value.isBlank()) updated.getClassInstance().setStartDate(validationService.parseDate(value));
        }

        private void applyDateEndEdit(String label, String current, Schedule updated) {
            String value = inputHandler.inputReader.readString(label + " [" + current + "]: ");
            if (!value.isBlank()) updated.getClassInstance().setEndDate(validationService.parseDate(value));
        }

        private void applyTimeEdit(String label, String current, boolean isStart, Schedule updated) {
            String value = inputHandler.inputReader.readString(label + " [" + current + "]: ");
            if (!value.isBlank()) {
                if (isStart) updated.getClassInstance().setStartTime(validationService.enforce24HourFormat(value));
                else updated.getClassInstance().setEndTime(validationService.enforce24HourFormat(value));
            }
        }
    }

    private interface TextSetter { void set(String value); }
    private interface IntSetter { void set(int value); }
}
