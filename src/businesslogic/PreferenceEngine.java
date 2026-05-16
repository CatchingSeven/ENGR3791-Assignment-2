package businesslogic;

import domain.Schedule;
import domain.Timetable;
import domain.TimetablePreferences;

import java.util.*;

/** Layer 3 component: PreferenceEngine with PreferenceRanker and ScoringMethod responsibilities. */
public class PreferenceEngine {
    public List<Timetable> rankPreferences(List<Timetable> validTimetables, TimetablePreferences preferences) {
        List<Timetable> ranked = new ArrayList<>(validTimetables);
        for (Timetable timetable : ranked) {
            timetable.setScore(scoreTimetable(timetable, preferences));
        }
        ranked.sort(Comparator.comparingInt(Timetable::getScore).reversed()
                .thenComparing(Timetable::getTimetableName, String.CASE_INSENSITIVE_ORDER));
        return ranked;
    }

    private int scoreTimetable(Timetable timetable, TimetablePreferences preferences) {
        int total = 0;
        List<String> ordered = preferences.getOrderedPreferences();
        for (int i = 0; i < ordered.size(); i++) {
            int weight = ordered.size() - i;
            total += applyScoringMethod(timetable, ordered.get(i), weight);
        }
        return total;
    }

    private int applyScoringMethod(Timetable timetable, String preference, int weight) {
        String p = preference == null ? "" : preference.toLowerCase(Locale.ROOT).trim();
        if (p.isBlank()) return 0;
        int rawScore = 0;

        if (p.contains("same campus")) {
            Set<String> campuses = new HashSet<>();
            for (Schedule s : timetable.getSchedules()) campuses.add(s.getAvailability().getCampus().toLowerCase(Locale.ROOT));
            rawScore = campuses.size() == 1 ? 10 : Math.max(0, 4 - campuses.size());
        } else if (p.contains("morning")) {
            for (Schedule s : timetable.getSchedules()) if (s.startTime().getHour() < 12) rawScore++;
        } else if (p.contains("afternoon")) {
            for (Schedule s : timetable.getSchedules()) if (s.startTime().getHour() >= 12) rawScore++;
        } else if (p.contains("spread")) {
            rawScore = countDistinctDays(timetable);
        } else if (p.contains("compact") || p.contains("few days")) {
            rawScore = Math.max(0, 7 - countDistinctDays(timetable));
        } else if (isWeekday(p)) {
            for (Schedule s : timetable.getSchedules()) if (s.normalDay().equals(dayKey(p))) rawScore++;
        } else {
            for (Schedule s : timetable.getSchedules()) {
                if (s.getAvailability().getCampus().toLowerCase(Locale.ROOT).contains(p)) rawScore++;
            }
        }
        return rawScore * weight;
    }

    private int countDistinctDays(Timetable timetable) {
        Set<String> days = new HashSet<>();
        for (Schedule s : timetable.getSchedules()) days.add(s.normalDay());
        return days.size();
    }

    private boolean isWeekday(String p) {
        return p.contains("monday") || p.contains("tuesday") || p.contains("wednesday")
                || p.contains("thursday") || p.contains("friday");
    }

    private String dayKey(String p) {
        if (p.contains("monday")) return "monday";
        if (p.contains("tuesday")) return "tuesday";
        if (p.contains("wednesday")) return "wednesday";
        if (p.contains("thursday")) return "thursday";
        if (p.contains("friday")) return "friday";
        return p;
    }
}
