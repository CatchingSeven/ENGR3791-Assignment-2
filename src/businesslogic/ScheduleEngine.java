package businesslogic;

import domain.ClassInstance;
import domain.Timetable;
import java.util.ArrayList;
import java.util.List;

/**
 * Core engine for generating timetables and enforcing scheduling constraints.
 */
public class ScheduleEngine {

    private ClashDetector clashDetector;
    private CommuteRule commuteRule;
    private TimetableBuilder timetableBuilder;

    public ScheduleEngine() {
        this.clashDetector = new ClashDetector();
        this.commuteRule = new CommuteRule();
        this.timetableBuilder = new TimetableBuilder();
    }

    /**
     * Generates a list of all valid, clash-free timetables from the selected pool of classes.
     */
    public List<Timetable> generateValidTimetables(String timetableName, String semester,
                                                   boolean allowLectureOverlap, List<ClassInstance> selectedClasses) {
        // Build all possible combinations of the selected classes
        List<List<ClassInstance>> allCombinations = timetableBuilder.buildCombinations(selectedClasses);
        List<Timetable> validTimetables = new ArrayList<>();

        for (List<ClassInstance> combination : allCombinations) {
            if (isValidSchedule(combination, allowLectureOverlap)) {
                Timetable validTimetable = new Timetable(
                        "TC-" + System.currentTimeMillis(), // Auto-generate internal code
                        timetableName,
                        semester,
                        allowLectureOverlap
                );
                // Note: requires setting scheduled classes in the Timetable domain object
                // validTimetable.setScheduledClasses(combination);
                validTimetables.add(validTimetable);
            }
        }
        return validTimetables;
    }

    private boolean isValidSchedule(List<ClassInstance> schedule, boolean allowLectureOverlap) {
        for (int i = 0; i < schedule.size(); i++) {
            for (int j = i + 1; j < schedule.size(); j++) {
                ClassInstance c1 = schedule.get(i);
                ClassInstance c2 = schedule.get(j);

                if (clashDetector.hasTimeClash(c1, c2, allowLectureOverlap)) return false;
                if (!commuteRule.hasValidCommuteTime(c1, c2)) return false;
            }
        }
        return true;
    }

    // ===================================================================================
    // INTERNAL COMPONENTS
    // ===================================================================================

    private class ClashDetector {
        public boolean hasTimeClash(ClassInstance c1, ClassInstance c2, boolean allowLectureOverlap) {
            if (!c1.getDay().equalsIgnoreCase(c2.getDay())) return false;

            // Extract format assuming the ClassCode contains it (e.g., "COMP1701-Lecture")
            boolean bothLectures = c1.getClassCode().toLowerCase().contains("lecture") &&
                    c2.getClassCode().toLowerCase().contains("lecture");
            if (bothLectures && allowLectureOverlap) return false;

            int start1 = parseTime(c1.getStartTime());
            int end1 = parseTime(c1.getEndTime());
            int start2 = parseTime(c2.getStartTime());
            int end2 = parseTime(c2.getEndTime());

            return (start1 < end2) && (end1 > start2);
        }

        private int parseTime(String time) {
            String[] parts = time.split(":");
            return (Integer.parseInt(parts[0].trim()) * 60) + Integer.parseInt(parts[1].trim());
        }
    }

    private class CommuteRule {
        public boolean hasValidCommuteTime(ClassInstance c1, ClassInstance c2) {
            if (!c1.getDay().equalsIgnoreCase(c2.getDay())) return true;

            String campus1 = getCampus(c1.getBuilding());
            String campus2 = getCampus(c2.getBuilding());

            if (campus1.equalsIgnoreCase(campus2)) return true; // 0 mins required for same campus

            int end1 = parseTime(c1.getEndTime());
            int start2 = parseTime(c2.getStartTime());
            int end2 = parseTime(c2.getEndTime());
            int start1 = parseTime(c1.getStartTime());

            // Check gap regardless of chronological order in the list
            if (start2 >= end1) return (start2 - end1) >= 30;
            if (start1 >= end2) return (start1 - end2) >= 30;

            return false;
        }

        private String getCampus(String building) {
            if (building.toLowerCase().contains("tonsley")) return "tonsley";
            if (building.toLowerCase().contains("bedford")) return "bedford";
            return "city";
        }

        private int parseTime(String time) {
            String[] parts = time.split(":");
            return (Integer.parseInt(parts[0].trim()) * 60) + Integer.parseInt(parts[1].trim());
        }
    }

    private class TimetableBuilder {
        public List<List<ClassInstance>> buildCombinations(List<ClassInstance> pool) {
            // Implementation of a Cartesian product or backtracking algorithm to generate
            // all permutations of required topics -> classes -> instances.
            return new ArrayList<>(); // Placeholder for combinatorial logic
        }
    }
}