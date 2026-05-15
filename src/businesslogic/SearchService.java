package businesslogic;

import businesslogic.ImportService.ParsedRecordWrapper;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Handles complex multi-criteria filtering of class records.
 */
public class SearchService {

    private QueryBuilder queryBuilder;
    private CriteriaMatcher criteriaMatcher;
    private ResultAggregator resultAggregator;

    public SearchService() {
        this.queryBuilder = new QueryBuilder();
        this.criteriaMatcher = new CriteriaMatcher();
        this.resultAggregator = new ResultAggregator();
    }

    /**
     * Executes a strict AND search across the provided dataset.
     */
    public List<ParsedRecordWrapper> searchClasses(List<ParsedRecordWrapper> dataset, Map<String, String> rawCriteria) {
        if (rawCriteria == null || rawCriteria.isEmpty()) {
            return dataset; // Return all if no criteria defined[cite: 35].
        }

        Map<String, String> sanitizedQuery = queryBuilder.buildQuery(rawCriteria);
        List<ParsedRecordWrapper> matchedResults = new ArrayList<>();

        for (ParsedRecordWrapper record : dataset) {
            if (criteriaMatcher.matchesAll(record, sanitizedQuery)) {
                matchedResults.add(record);
            }
        }

        return resultAggregator.aggregate(matchedResults);
    }

    // ===================================================================================
    // INTERNAL COMPONENTS
    // ===================================================================================

    private class QueryBuilder {
        public Map<String, String> buildQuery(Map<String, String> rawInput) {
            // Sanitizes input keys and values (e.g., lowercase, trim) for accurate comparison
            rawInput.replaceAll((k, v) -> v.toLowerCase().trim());
            return rawInput;
        }
    }

    private class CriteriaMatcher {
        public boolean matchesAll(ParsedRecordWrapper record, Map<String, String> criteria) {
            for (Map.Entry<String, String> entry : criteria.entrySet()) {
                String key = entry.getKey().toLowerCase();
                String value = entry.getValue();

                boolean match = switch (key) {
                    case "semester" -> record.getAvailability().getSemester().toLowerCase().contains(value);
                    case "campus" -> record.getAvailability().getCampus().toLowerCase().contains(value);
                    case "topic code" -> record.getTopic().getTopicCode().toLowerCase().contains(value);
                    case "day" -> record.getInstance().getDay().toLowerCase().contains(value);
                    case "class format" -> record.getCourseClass().getClassFormat().toLowerCase().contains(value);
                    // Add other criteria cases as required by spec[cite: 32].
                    default -> false;
                };

                // Strict AND logic: if any single criterion fails, the record is rejected
                if (!match) return false;
            }
            return true;
        }
    }

    private class ResultAggregator {
        public List<ParsedRecordWrapper> aggregate(List<ParsedRecordWrapper> rawResults) {
            // Sorts or groups the final results before returning to the Controller
            return rawResults;
        }
    }
}