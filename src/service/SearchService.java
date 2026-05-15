package service;

import domain.ClassInstance;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class SearchService {

    /**
     * DTO to aggregate domain attributes for comprehensive searching.
     */
    public static class FullClassDetails {
        public ClassInstance instance;
        public String topicCode;
        public String topicName;
        public String campus;
        public String semester;

        public FullClassDetails(ClassInstance instance, String topicCode, String topicName, String campus, String semester) {
            this.instance = instance;
            this.topicCode = topicCode;
            this.topicName = topicName;
            this.campus = campus;
            this.semester = semester;
        }
    }

    /**
     * QueryBuilder class to chain multiple search criteria[cite: 56].
     */
    public static class QueryBuilder {
        private Predicate<FullClassDetails> criteria = c -> true; // Defaults to match all [cite: 57]

        public QueryBuilder withTopicCode(String topicCode) {
            if (topicCode != null && !topicCode.isEmpty()) {
                criteria = criteria.and(c -> c.topicCode.equalsIgnoreCase(topicCode));
            }
            return this;
        }

        public QueryBuilder withCampus(String campus) {
            if (campus != null && !campus.isEmpty()) {
                criteria = criteria.and(c -> c.campus.equalsIgnoreCase(campus));
            }
            return this;
        }

        public QueryBuilder withSemester(String semester) {
            if (semester != null && !semester.isEmpty()) {
                criteria = criteria.and(c -> c.semester.equalsIgnoreCase(semester));
            }
            return this;
        }

        public Predicate<FullClassDetails> build() {
            return criteria;
        }
    }

    /**
     * ResultAggregator: Executes the match and aggregates results.
     */
    public List<FullClassDetails> searchClasses(List<FullClassDetails> dataset, Predicate<FullClassDetails> criteria) {
        return dataset.stream()
                .filter(criteria)
                .collect(Collectors.toList());
    }
}