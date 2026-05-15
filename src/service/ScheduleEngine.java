package service;

import domain.ClassInstance;
import java.time.Duration;
import java.util.List;

public class ScheduleEngine {

    /**
     * Evaluates a new class against an existing schedule to identify overlaps or commute violations.
     */
    public boolean hasTimeClashOrCommuteIssue(List<ClassInstance> existingSchedule, List<String> existingCampuses,
                                              ClassInstance newClass, String newCampus) {
        for (int i = 0; i < existingSchedule.size(); i++) {
            ClassInstance existing = existingSchedule.get(i);
            String existingCampus = existingCampuses.get(i);

            if (existing.getDay().equalsIgnoreCase(newClass.getDay())) {

                // 1. Clash Detector: Check for overlapping times
                boolean overlaps = newClass.getStartTime().isBefore(existing.getEndTime()) &&
                        newClass.getEndTime().isAfter(existing.getStartTime());
                if (overlaps) {
                    return true;
                }

                // 2. Commute Rule: Classes at different campuses require a minimum 30-minute gap[cite: 80].
                // Classes at the same campus can be scheduled directly after one another[cite: 79].
                if (!existingCampus.equalsIgnoreCase(newCampus)) {
                    Duration gapBefore = Duration.between(existing.getEndTime(), newClass.getStartTime());
                    Duration gapAfter = Duration.between(newClass.getEndTime(), existing.getStartTime());

                    if ((!gapBefore.isNegative() && gapBefore.toMinutes() < 30) ||
                            (!gapAfter.isNegative() && gapAfter.toMinutes() < 30)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}