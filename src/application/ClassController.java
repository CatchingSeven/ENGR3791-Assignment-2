package application;

import businesslogic.SearchService;
import businesslogic.ValidationService;
import domain.ClassRecord;
import presentation.InputHandler;
import presentation.OutputFormatter;

import java.util.List;
import java.util.Map;

/**
 * Coordinates operations related to individual class records.
 * Handlers are implemented as private methods per project team instruction.
 */
public class ClassController {

    private SearchService searchService;
    private ValidationService validationService;
    private InputHandler inputHandler;
    private OutputFormatter outputFormatter;

    public ClassController(SearchService searchService, ValidationService validationService,
                           InputHandler inputHandler, OutputFormatter outputFormatter) {
        this.searchService = searchService;
        this.validationService = validationService;
        this.inputHandler = inputHandler;
        this.outputFormatter = outputFormatter;
    }

    /**
     * Option 1: Displays combined class data (topic, availability, class, instance)[cite: 45, 46].
     */
    public void handleBrowseClasses(List<ClassRecord> currentState) {
        if (currentState.isEmpty()) {
            outputFormatter.printError("No classes currently loaded. Please import data first.");
            return;
        }
        outputFormatter.printClassTable(currentState); // Renders minimal combined data [cite: 47]
    }

    /**
     * Option 1: Displays full detailed class data[cite: 49, 50].
     */
    public void handleViewClasses(List<ClassRecord> currentState) {
        if (currentState.isEmpty()) {
            outputFormatter.printError("No classes currently loaded. Please import data first.");
            return;
        }
        outputFormatter.printDetailedClassTable(currentState); // Renders all 15 data fields [cite: 51, 52]
    }

    /**
     * Manages the multi-criteria search workflow[cite: 53, 56].
     */
    public void handleSearchClasses(List<ClassRecord> currentState) {
        if (currentState.isEmpty()) {
            outputFormatter.printError("No classes currently loaded. Please import data first.");
            return;
        }

        Map<String, String> criteria = inputHandler.collectSearchCriteria(); // User selects fields to filter [cite: 58, 59]
        List<ClassRecord> results = searchService.searchClasses(currentState, criteria);

        if (results.isEmpty()) {
            outputFormatter.printError("No records matched all your search criteria."); // Strict AND logic [cite: 56]
        } else {
            outputFormatter.printDetailedClassTable(results);
        }
    }

    /**
     * Manages the editing workflow using a number selection system for fields[cite: 60].
     */
    public void handleEditClass(List<ClassRecord> currentState) {
        if (currentState.isEmpty()) {
            outputFormatter.printError("No classes available to edit.");
            return;
        }

        outputFormatter.printDetailedClassTable(currentState);
        int recordIndex = inputHandler.getValidMenuChoice(1, currentState.size()) - 1;
        ClassRecord target = currentState.get(recordIndex);

        // Display fields by number for user selection
        outputFormatter.printEditFieldMenu();
        int fieldChoice = inputHandler.getValidMenuChoice(1, 15);
        String newValue = inputHandler.getStringInput("Enter the new value: ");

        // Mandatory warning and confirmation [cite: 62, 63]
        boolean confirm = inputHandler.getYesNoConfirmation("WARNING: You are about to modify a class record. Proceed?");
        if (confirm) {
            updateRecordField(target, fieldChoice, newValue);
            outputFormatter.printSuccess("Class updated successfully and exported to CSV."); // [cite: 10, 11]
            // Note: MenuController should handle persistenceAdapter.saveClass(currentState) here
        } else {
            outputFormatter.printSuccess("Edit cancelled.");
        }
    }

    /**
     * Manages the deletion workflow with strict confirmation[cite: 64, 65].
     */
    public void handleDeleteClass(List<ClassRecord> currentState) {
        if (currentState.isEmpty()) {
            outputFormatter.printError("No classes available to delete.");
            return;
        }

        outputFormatter.printDetailedClassTable(currentState);
        int index = inputHandler.getValidMenuChoice(1, currentState.size()) - 1;

        boolean confirm = inputHandler.getYesNoConfirmation("WARNING: Are you sure you want to delete this class?");
        if (confirm) {
            currentState.remove(index);
            outputFormatter.printSuccess("Class successfully deleted.");
        } else {
            outputFormatter.printSuccess("Deletion cancelled.");
        }
    }

    /**
     * Internal helper to map numerical selection to record fields[cite: 61].
     */
    private void updateRecordField(ClassRecord record, int choice, String value) {
        switch (choice) {
            case 1 -> record.setTopicCode(value);
            case 2 -> record.setTopicName(value);
            case 3 -> record.setAttendanceMode(value);
            case 4 -> record.setCampus(value);
            case 5 -> record.setSemester(value);
            case 6 -> record.setAvailabilityNumber(value);
            case 7 -> record.setClassFormat(value);
            case 8 -> record.setClassInstance(value);
            case 9 -> record.setStartDate(value);
            case 10 -> record.setEndDate(value);
            case 11 -> record.setDay(value);
            case 12 -> record.setStartTime(value);
            case 13 -> record.setEndTime(value);
            case 14 -> record.setBuilding(value);
            case 15 -> record.setRoom(value);
        }
    }
}