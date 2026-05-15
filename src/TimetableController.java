import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

public class TimetableController {
    private ArrayList<Student> students;
    private ArrayList<Topic> topics;
    private ArrayList<Timetable> timetables;
    private Scanner scanner;
    private String currentFilePath;

    public TimetableController() {
        this.students = new ArrayList<>();
        this.topics = new ArrayList<>();
        this.timetables = new ArrayList<>();
        this.scanner = new Scanner(System.in);
        this.currentFilePath = "subjects.csv";
    }

    public void start() {
        boolean running = true;
        while (running) {
            System.out.println("\n--- Timetable Management System ---");
            System.out.println("1. Import Subject Data");
            System.out.println("2. Manage Classes (Browse/Search/Edit/Delete)");
            System.out.println("3. Generate Custom Timetable");
            System.out.println("4. Export Subject Data");
            System.out.println("5. Exit");
            System.out.print("Select an option: ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    System.out.print("Enter file path (e.g., subjects.csv): ");
                    currentFilePath = scanner.nextLine();
                    importSubjectData(currentFilePath);
                    break;
                case "2":
                    manageClassesMenu();
                    break;
                case "3":
                    generateTimetable();
                    break;
                case "4":
                    exportSubjectData(currentFilePath);
                    break;
                case "5":
                    handleExit();
                    running = false;
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }

    // --- Timetable Generation & Constraints ---

    private void generateTimetable() {
        if (topics.isEmpty()) {
            System.out.println("Error: Please import subject data before generating a timetable.");
            return;
        }

        System.out.println("\n--- Generate Custom Timetable ---");
        System.out.print("Enter Timetable Name (Leave blank to auto-generate): ");
        String name = scanner.nextLine().trim();

        // Sequential Auto-Naming Requirement
        if (name.isEmpty()) {
            name = "Timetable " + (timetables.size() + 1);
        }

        System.out.print("Enter Semester: ");
        String semester = scanner.nextLine();

        System.out.print("Allow class overlapping? (Y/N): ");
        boolean allowOverlap = scanner.nextLine().trim().equalsIgnoreCase("Y");

        String code = "TT" + (timetables.size() + 1);
        Timetable newTimetable = new Timetable(code, name, semester, allowOverlap);

        boolean adding = true;
        while (adding) {
            System.out.print("\nEnter Topic Code to add a class (or type 'DONE' to finish): ");
            String input = scanner.nextLine().toUpperCase();
            if (input.equals("DONE")) {
                break;
            }

            Topic foundTopic = getTopic(input);
            if (foundTopic == null) {
                System.out.println("Topic not found. Try again.");
                continue;
            }

            System.out.println("Available Classes for " + foundTopic.getTopicCode() + ":");
            for (TopicClass tc : foundTopic.getTopicClasses()) {
                for (ClassInstance ci : tc.getClassInstances()) {
                    System.out.println("  [Instance: " + ci.getClassInstanceNo() + "] Type: " + tc.getClassCode() +
                            " | Day: " + ci.getDay() + " | Time: " + ci.getStartTime() + "-" + ci.getEndTime() +
                            " | Loc: " + ci.getBuilding());
                }
            }

            System.out.print("Enter the Instance No to add to timetable: ");
            String instanceNo = scanner.nextLine();
            ClassInstance selectedInstance = getInstanceByNo(foundTopic, instanceNo);

            if (selectedInstance != null) {
                // Strict Validation Check
                if (validateAddition(newTimetable, selectedInstance, foundTopic.getTopicCode(), allowOverlap)) {
                    newTimetable.getScheduledClasses().add(selectedInstance);
                    System.out.println("Successfully added to " + newTimetable.getTimetableName() + "!");
                }
            } else {
                System.out.println("Instance No not found for this topic.");
            }
        }

        timetables.add(newTimetable);
        System.out.println("\nTimetable '" + newTimetable.getTimetableName() + "' finalized with " +
                newTimetable.getScheduledClasses().size() + " classes.");
    }

    private boolean validateAddition(Timetable tt, ClassInstance newCi, String currentTopicCode, boolean allowOverlap) {
        String newCampus = extractCampus(newCi.getBuilding());
        int newStart = timeToMinutes(newCi.getStartTime());
        int newEnd = timeToMinutes(newCi.getEndTime());

        for (ClassInstance existingCi : tt.getScheduledClasses()) {
            String existingTopicCode = getTopicCodeForInstance(existingCi);
            String existingCampus = extractCampus(existingCi.getBuilding());
            int existStart = timeToMinutes(existingCi.getStartTime());
            int existEnd = timeToMinutes(existingCi.getEndTime());

            // Constraint 1: Campus Mix for the SAME topic
            if (existingTopicCode.equals(currentTopicCode)) {
                boolean newIsCity = newCampus.equals("City");
                boolean existIsCity = existingCampus.equals("City");

                if (newIsCity != existIsCity && (!newCampus.equals("Unknown") && !existingCampus.equals("Unknown"))) {
                    System.out.println(">> CONSTRAINT ERROR: Cannot mix City campus with Bedford/Tonsley for the same topic.");
                    return false;
                }
            }

            // Constraints 2 & 3: Only apply if classes are on the same day
            if (existingCi.getDay().equalsIgnoreCase(newCi.getDay())) {

                // Overlap Check
                boolean overlaps = (newStart < existEnd && existStart < newEnd);
                if (overlaps && !allowOverlap) {
                    System.out.println(">> CONSTRAINT ERROR: Class times overlap and overlap is set to false.");
                    return false;
                }

                // Commuting Buffer Check (30 mins if campuses differ)
                if (!newCampus.equals(existingCampus) && !newCampus.equals("Unknown") && !existingCampus.equals("Unknown")) {
                    int buffer = (newStart >= existEnd) ? (newStart - existEnd) : (existStart - newEnd);

                    if (buffer > 0 && buffer < 30) {
                        System.out.println(">> CONSTRAINT ERROR: Insufficient commute time. Need minimum 30 mins between " +
                                existingCampus + " and " + newCampus + ". Currently only have " + buffer + " mins.");
                        return false;
                    }
                }
            }
        }
        return true;
    }

    // --- Helper Methods ---

    private String extractCampus(String location) {
        if (location == null) return "Unknown";
        String locLower = location.toLowerCase();
        if (locLower.contains("city") || locLower.contains("victoria sq")) return "City";
        if (locLower.contains("tonsley")) return "Tonsley";
        if (locLower.contains("bedford")) return "Bedford Park";
        return "Unknown";
    }

    private int timeToMinutes(String time) {
        if (time == null || !time.contains(":")) return 0;
        try {
            String cleanTime = time.split("-")[0].trim(); // Failsafe for un-split CSV strings
            String[] parts = cleanTime.split(":");
            return Integer.parseInt(parts[0]) * 60 + Integer.parseInt(parts[1]);
        } catch (Exception e) {
            return 0;
        }
    }

    private Topic getTopic(String topicCode) {
        for (Topic t : topics) {
            if (t.getTopicCode().equalsIgnoreCase(topicCode)) return t;
        }
        return null;
    }

    private ClassInstance getInstanceByNo(Topic t, String no) {
        for (TopicClass tc : t.getTopicClasses()) {
            for (ClassInstance ci : tc.getClassInstances()) {
                if (ci.getClassInstanceNo().equals(no)) return ci;
            }
        }
        return null;
    }

    private String getTopicCodeForInstance(ClassInstance target) {
        for (Topic t : topics) {
            for (TopicClass tc : t.getTopicClasses()) {
                if (tc.getClassInstances().contains(target)) return t.getTopicCode();
            }
        }
        return "Unknown";
    }

    // --- Class Management Sub-Menu ---

    private void manageClassesMenu() {
        boolean back = false;
        while (!back) {
            System.out.println("\n--- Manage Classes ---");
            System.out.println("1. Browse All Topics");
            System.out.println("2. Search Classes by Topic Code");
            System.out.println("3. Edit a Class");
            System.out.println("4. Delete a Class");
            System.out.println("5. Return to Main Menu");
            System.out.print("Select an option: ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    browseTopics();
                    break;
                case "2":
                    searchClasses();
                    break;
                case "3":
                    editClass();
                    break;
                case "4":
                    deleteClass();
                    break;
                case "5":
                    back = true;
                    break;
                default:
                    System.out.println("Invalid option. Trapped in loop until valid entry.");
            }
        }
    }

    private void browseTopics() {
        if (topics.isEmpty()) {
            System.out.println("No topics loaded. Please import data first.");
            return;
        }
        System.out.println("\n--- All Topics ---");
        for (Topic t : topics) {
            System.out.println("- " + t.getTopicCode() + " (" + t.getTopicClasses().size() + " class formats)");
        }
    }

    private void searchClasses() {
        System.out.print("Enter Topic Code to search: ");
        String searchCode = scanner.nextLine().toUpperCase();

        Topic foundTopic = null;
        for (Topic t : topics) {
            if (t.getTopicCode().equalsIgnoreCase(searchCode)) {
                foundTopic = t;
                break;
            }
        }

        if (foundTopic == null) {
            System.out.println("Topic not found.");
            return;
        }

        System.out.println("\n--- Classes for " + foundTopic.getTopicCode() + " ---");
        for (TopicClass tc : foundTopic.getTopicClasses()) {
            for (ClassInstance ci : tc.getClassInstances()) {
                System.out.println("Class: " + tc.getClassCode() + " | Instance: " + ci.getClassInstanceNo() +
                        " | Day: " + ci.getDay() + " | Time: " + ci.getStartTime() + "-" + ci.getEndTime());
            }
        }
    }

    private void editClass() {
        System.out.print("Enter Topic Code of the class to edit: ");
        String searchCode = scanner.nextLine().toUpperCase();
        System.out.println("WARNING: Are you sure you want to edit a class for " + searchCode + "? (Y/N)");
        String confirm = scanner.nextLine();

        if (confirm.equalsIgnoreCase("Y")) {
            System.out.print("Enter new Start Time (24HR format, e.g., 13:00): ");
            String newTime = scanner.nextLine();
            if (validateTime(newTime)) {
                System.out.println("Time updated successfully. (Placeholder logic applied)");
            } else {
                System.out.println("Invalid 24-hour time format. Update aborted.");
            }
        } else {
            System.out.println("Edit cancelled.");
        }
    }

    private void deleteClass() {
        System.out.print("Enter Topic Code of the class to delete: ");
        String searchCode = scanner.nextLine().toUpperCase();
        System.out.println("WARNING: This will permanently delete the class record. Proceed? (Y/N)");
        String confirm = scanner.nextLine();

        if (confirm.equalsIgnoreCase("Y")) {
            System.out.println("Class deleted successfully. (Placeholder logic applied)");
        } else {
            System.out.println("Deletion cancelled.");
        }
    }

    private boolean validateTime(String time) {
        return time.matches("^([01]\\d|2[0-3]):([0-5]\\d)$");
    }

    // --- Data Export & Import Logic ---

    public void exportSubjectData(String filePath) {
        if (topics.isEmpty()) {
            System.out.println("No data to export.");
            return;
        }

        try (PrintWriter pw = new PrintWriter(new FileWriter(filePath))) {
            pw.println("Topic,Availability,Class,Class instance,Date,Day,Time,Location");

            for (Topic t : topics) {
                for (TopicClass tc : t.getTopicClasses()) {
                    for (ClassInstance ci : tc.getClassInstances()) {
                        String timeStr = ci.getStartTime() + " - " + ci.getEndTime();
                        String locationStr = ci.getBuilding() + (ci.getRoom().equals("Unknown") ? "" : " " + ci.getRoom());

                        pw.printf("%s,%s,%s,%s,%s,%s,%s,%s\n",
                                t.getTopicCode(),
                                ci.getAvailability().getAttendanceMode(),
                                tc.getClassCode(),
                                ci.getClassInstanceNo(),
                                ci.getStartDate(),
                                ci.getDay(),
                                timeStr,
                                locationStr
                        );
                    }
                }
            }
            System.out.println("Data successfully exported to " + filePath);
        } catch (IOException e) {
            System.out.println("Error writing to file: " + e.getMessage());
        }
    }

    private void handleExit() {
        System.out.println("Do you want to save your current data before exiting? (Y/N)");
        String save = scanner.nextLine();
        if (save.equalsIgnoreCase("Y")) {
            exportSubjectData(currentFilePath);
        }
        System.out.println("System shutting down...");
    }

    public void importSubjectData(String filePath) {
        String line = "";
        String cvsSplitBy = ",";

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            br.readLine();

            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;

                String[] data = line.split(cvsSplitBy);
                if (data.length < 8) continue;

                String topicCode = data[0].trim();
                String availabilityData = data[1].trim();
                String classCode = data[2].trim();
                String instanceNo = data[3].trim();
                String dateData = data[4].trim();
                String dayStr = data[5].trim();
                String timeData = data[6].trim();
                String locationData = data[7].trim();

                Topic currentTopic = getOrCreateTopic(topicCode);
                TopicClass currentClass = getOrCreateTopicClass(currentTopic, classCode);
                ClassInstance existingInstance = findClassInstance(currentClass, availabilityData, instanceNo, dateData, dayStr);

                if (existingInstance != null) {
                    existingInstance.setStartTime(timeData);
                    existingInstance.setEndTime(timeData);
                    existingInstance.setBuilding(locationData);
                } else {
                    ClassAvailability availability = new ClassAvailability(availabilityData, "Unknown", "Unknown", "Unknown");
                    ClassInstance newInstance = new ClassInstance(
                            instanceNo, classCode, dateData, dateData, timeData, timeData,
                            dayStr, locationData, "Unknown", availability
                    );
                    currentClass.getClassInstances().add(newInstance);
                }
            }
            System.out.println("Data import completed successfully. Topics loaded: " + topics.size());

        } catch (IOException e) {
            System.out.println("Error reading the CSV file: " + e.getMessage());
        }
    }

    private Topic getOrCreateTopic(String topicCode) {
        for (Topic t : topics) {
            if (t.getTopicCode().equals(topicCode)) {
                return t;
            }
        }
        Topic newTopic = new Topic(topicCode, "Unknown");
        topics.add(newTopic);
        return newTopic;
    }

    private TopicClass getOrCreateTopicClass(Topic topic, String classCode) {
        for (TopicClass tc : topic.getTopicClasses()) {
            if (tc.getClassCode().equals(classCode)) {
                return tc;
            }
        }
        TopicClass newClass = new TopicClass("Unknown", classCode);
        topic.getTopicClasses().add(newClass);
        return newClass;
    }

    private ClassInstance findClassInstance(TopicClass topicClass, String availability, String instanceNo, String date, String day) {
        for (ClassInstance ci : topicClass.getClassInstances()) {
            if (ci.getClassInstanceNo().equals(instanceNo) &&
                    ci.getStartDate().equals(date) &&
                    ci.getDay().equals(day) &&
                    ci.getAvailability().getAttendanceMode().equals(availability)) {
                return ci;
            }
        }
        return null;
    }

    public static void main(String[] args) {
        TimetableController controller = new TimetableController();
        controller.start();
    }
}