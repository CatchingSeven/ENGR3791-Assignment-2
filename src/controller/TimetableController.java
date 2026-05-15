package controller;

import domain.ClassInstance;
import domain.Timetable;
import domain.TimetablePreferences;
import service.ScheduleEngine;

import java.util.List;

/**
 * TimetableController coordinates the generation, editing, and exporting of student timetables.
 */
public class TimetableController {

    private final ExportHandler exportHandler = new ExportHandler();
    private final GenerateHandler generateHandler = new GenerateHandler();
    private final EditHandler editHandler = new EditHandler();

    private final ScheduleEngine scheduleEngine;

    public TimetableController(ScheduleEngine scheduleEngine) {
        this.scheduleEngine = scheduleEngine;
    }

    public ExportHandler getExportHandler() { return exportHandler; }
    public GenerateHandler getGenerateHandler() { return generateHandler; }
    public EditHandler getEditHandler() { return editHandler; }

    // ==========================================
    // Internal Components
    // ==========================================

    public class ExportHandler {
        /**
         * Coordinates the export process.
         * File writing is delegated to the PersistenceAdapter by the caller.
         */
        public boolean processExport(Timetable timetable, List<ClassInstance> currentSchedule) {
            if (timetable == null || currentSchedule == null || currentSchedule.isEmpty()) {
                return false;
            }
            return true; // Signals Presentation layer that export is ready to be written
        }
    }

    public class GenerateHandler {
        /**
         * Coordinates generating a timetable based on selected parameters and preferences.
         */
        public Timetable processGenerate(String name, String semester, TimetablePreferences preferences,
                                         boolean allowOverlap, List<String> selectedTopics) {
            // Constructs a new Timetable object based on the parameters
            return new Timetable("GEN-T", name, semester, allowOverlap);
            // Actual population of the timetable with ClassInstances would be coordinated here
            // via the PreferenceEngine and ScheduleEngine.
        }
    }

    public class EditHandler {
        /**
         * Handles swapping one class instance for another.
         * Interacts with ScheduleEngine to detect clashes or commute violations.
         */
        public SwapResult processSwap(List<ClassInstance> currentSchedule, List<String> currentCampuses,
                                      ClassInstance classToRemove, ClassInstance classToAdd, String newCampus,
                                      boolean isConfirmed) {

            // 1. Temporarily remove the target class to check the proposed schedule
            currentSchedule.remove(classToRemove);

            // 2. Validate the new class against the ScheduleEngine rules
            boolean hasClashOrCommuteIssue = scheduleEngine.hasTimeClashOrCommuteIssue(
                    currentSchedule, currentCampuses, classToAdd, newCampus);

            // 3. Handle Business Rules & Confirmations
            if (hasClashOrCommuteIssue && !isConfirmed) {
                // Revert removal and notify Presentation layer that a warning must be shown
                currentSchedule.add(classToRemove);
                return SwapResult.WARNING_REQUIRES_CONFIRMATION;
            }

            // 4. Finalize the swap
            currentSchedule.add(classToAdd);
            return SwapResult.SUCCESS;
        }
    }

    /**
     * Enum for strict control flow communication with the Presentation layer.
     */
    public enum SwapResult {
        SUCCESS,
        WARNING_REQUIRES_CONFIRMATION,
        FAILURE
    }
}