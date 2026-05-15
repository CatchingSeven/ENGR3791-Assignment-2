package service;
import java.util.List;

/**
 * Applies user preferences to rank and sort generated timetables.
 */
public class PreferenceEngine {

    /**
     * PreferenceRanker: Sorts a list of valid timetables based on aggregated user preferences.
     *
     * @param validTimetables The list of timetables that passed scheduling rules.
     * @param preferences The ordered list of user preferences.
     * @return A sorted list of timetables from most to least preferred.
     */
    public List<Object> rankPreferences(List<Object> validTimetables, List<Object> preferences) {
        // TODO: Iterate over validTimetables.
        // TODO: Calculate a total score for each timetable using applyScoringMethod().
        // TODO: Sort the list descending based on total score.
        return validTimetables;
    }

    /**
     * ScoringMethod: Calculates a numerical score for a timetable against a specific preference.
     *
     * @param timetable The timetable being evaluated.
     * @param preference The specific preference criteria.
     * @param weight The ranking weight assigned to this preference.
     * @return The calculated score.
     */
    private int applyScoringMethod(Object timetable, Object preference, int weight) {
        // TODO: Evaluate timetable alignment with the preference.
        // TODO: Multiply result by weight and return.
        return 0;
    }
}