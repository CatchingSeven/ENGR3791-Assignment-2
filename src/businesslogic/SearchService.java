package businesslogic;

import domain.Schedule;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.function.Predicate;

/** Layer 3 component: SearchService with CriteriaMatcher, ResultAggregator and QueryBuilder responsibilities. */
public class SearchService {
    public static class SearchCriteria {
        public String topicCode = "";
        public String topicName = "";
        public String attendanceMode = "";
        public String campus = "";
        public String semester = "";
        public String availabilityNumber = "";
        public String classFormat = "";
        public String classInstance = "";
        public String firstDate = "";
        public String lastDate = "";
        public String day = "";
        public String startTime = "";
        public String endTime = "";
        public String building = "";
        public String room = "";
    }

    public static class BrowseClassSummary {
        private final String key;
        private final String topicCode;
        private final String topicName;
        private final String attendanceMode;
        private final String campus;
        private final String semester;
        private final int availabilityNo;
        private final String classFormat;
        private final int classInstanceNo;
        private final int occurrenceCount;
        private final int firstRecordId;

        public BrowseClassSummary(String key, Schedule sample, int occurrenceCount, int firstRecordId) {
            this.key = key;
            this.topicCode = sample.getTopic().getTopicCode();
            this.topicName = sample.getTopic().getTopicName();
            this.attendanceMode = sample.getAvailability().getAttendanceMode();
            this.campus = sample.getAvailability().getCampus();
            this.semester = sample.getAvailability().getSemester();
            this.availabilityNo = sample.getAvailability().getAvailabilityNo();
            this.classFormat = sample.getTopicClass().getClassFormat();
            this.classInstanceNo = sample.getClassInstance().getClassInstanceNo();
            this.occurrenceCount = occurrenceCount;
            this.firstRecordId = firstRecordId;
        }

        public String getKey() { return key; }
        public String getTopicCode() { return topicCode; }
        public String getTopicName() { return topicName; }
        public String getAttendanceMode() { return attendanceMode; }
        public String getCampus() { return campus; }
        public String getSemester() { return semester; }
        public int getAvailabilityNo() { return availabilityNo; }
        public String getClassFormat() { return classFormat; }
        public int getClassInstanceNo() { return classInstanceNo; }
        public int getOccurrenceCount() { return occurrenceCount; }
        public int getFirstRecordId() { return firstRecordId; }
    }

    public static class QueryBuilder {
        private final SearchCriteria criteria = new SearchCriteria();

        public QueryBuilder withTopicCode(String value) { criteria.topicCode = value; return this; }
        public QueryBuilder withTopicName(String value) { criteria.topicName = value; return this; }
        public QueryBuilder withAttendanceMode(String value) { criteria.attendanceMode = value; return this; }
        public QueryBuilder withCampus(String value) { criteria.campus = value; return this; }
        public QueryBuilder withSemester(String value) { criteria.semester = value; return this; }
        public QueryBuilder withAvailabilityNumber(String value) { criteria.availabilityNumber = value; return this; }
        public QueryBuilder withClassFormat(String value) { criteria.classFormat = value; return this; }
        public QueryBuilder withClassInstance(String value) { criteria.classInstance = value; return this; }
        public QueryBuilder withFirstDate(String value) { criteria.firstDate = value; return this; }
        public QueryBuilder withLastDate(String value) { criteria.lastDate = value; return this; }
        public QueryBuilder withDay(String value) { criteria.day = value; return this; }
        public QueryBuilder withStartTime(String value) { criteria.startTime = value; return this; }
        public QueryBuilder withEndTime(String value) { criteria.endTime = value; return this; }
        public QueryBuilder withBuilding(String value) { criteria.building = value; return this; }
        public QueryBuilder withRoom(String value) { criteria.room = value; return this; }
        public SearchCriteria build() { return criteria; }
    }

    public List<Schedule> searchClasses(List<Schedule> dataset, SearchCriteria criteria) {
        Predicate<Schedule> matcher = buildMatcher(criteria);
        List<Schedule> results = new ArrayList<>();
        for (Schedule schedule : dataset) {
            if (matcher.test(schedule)) results.add(schedule);
        }
        results.sort(scheduleComparator());
        return results;
    }

    public List<BrowseClassSummary> browseGroups(List<Schedule> schedules) {
        Map<String, List<Schedule>> grouped = groupByOffering(schedules);
        List<BrowseClassSummary> summaries = new ArrayList<>();
        for (Map.Entry<String, List<Schedule>> entry : grouped.entrySet()) {
            List<Schedule> group = entry.getValue();
            group.sort(scheduleComparator());
            summaries.add(new BrowseClassSummary(entry.getKey(), group.get(0), group.size(), group.get(0).getRecordId()));
        }
        summaries.sort(Comparator
                .comparing(BrowseClassSummary::getTopicCode, String.CASE_INSENSITIVE_ORDER)
                .thenComparing(BrowseClassSummary::getClassFormat, String.CASE_INSENSITIVE_ORDER)
                .thenComparingInt(BrowseClassSummary::getClassInstanceNo));
        return summaries;
    }

    public Map<String, List<Schedule>> groupByOffering(List<Schedule> schedules) {
        Map<String, List<Schedule>> grouped = new LinkedHashMap<>();
        List<Schedule> sorted = new ArrayList<>(schedules);
        sorted.sort(scheduleComparator());
        for (Schedule s : sorted) {
            grouped.computeIfAbsent(s.classOfferingKey(), k -> new ArrayList<>()).add(s);
        }
        return grouped;
    }

    public Map<String, List<List<Schedule>>> groupCandidatesByRequiredClass(List<Schedule> schedules) {
        Map<String, List<Schedule>> offerings = groupByOffering(schedules);
        Map<String, List<List<Schedule>>> candidates = new LinkedHashMap<>();
        for (List<Schedule> offeringRows : offerings.values()) {
            if (offeringRows.isEmpty()) continue;
            String requirementKey = offeringRows.get(0).timetableChoiceGroupKey();
            candidates.computeIfAbsent(requirementKey, k -> new ArrayList<>()).add(offeringRows);
        }
        return candidates;
    }

    public Schedule findById(List<Schedule> schedules, int recordId) {
        for (Schedule schedule : schedules) if (schedule.getRecordId() == recordId) return schedule;
        return null;
    }

    public List<String> distinctTopicLabels(List<Schedule> schedules) {
        List<String> labels = new ArrayList<>();
        for (Schedule s : schedules) {
            String label = s.getTopic().getTopicCode() + " - " + s.getTopic().getTopicName();
            if (!containsIgnoreCase(labels, label)) labels.add(label);
        }
        labels.sort(String.CASE_INSENSITIVE_ORDER);
        return labels;
    }

    public List<String> distinctCampuses(List<Schedule> schedules) {
        List<String> campuses = new ArrayList<>();
        for (Schedule s : schedules) {
            String campus = s.getAvailability().getCampus();
            if (!containsIgnoreCase(campuses, campus)) campuses.add(campus);
        }
        campuses.sort(String.CASE_INSENSITIVE_ORDER);
        return campuses;
    }

    public Comparator<Schedule> scheduleComparator() {
        return Comparator
                .comparing((Schedule s) -> s.getTopic().getTopicCode(), String.CASE_INSENSITIVE_ORDER)
                .thenComparing(s -> s.getTopicClass().getClassFormat(), String.CASE_INSENSITIVE_ORDER)
                .thenComparingInt(s -> s.getClassInstance().getClassInstanceNo())
                .thenComparing(Schedule::normalDay)
                .thenComparing(Schedule::startTime)
                .thenComparing(Schedule::startDate);
    }

    private Predicate<Schedule> buildMatcher(SearchCriteria c) {
        return s -> contains(s.getTopic().getTopicCode(), c.topicCode)
                && contains(s.getTopic().getTopicName(), c.topicName)
                && contains(s.getAvailability().getAttendanceMode(), c.attendanceMode)
                && contains(s.getAvailability().getCampus(), c.campus)
                && contains(s.getAvailability().getSemester(), c.semester)
                && numberMatches(s.getAvailability().getAvailabilityNo(), c.availabilityNumber)
                && contains(s.getTopicClass().getClassFormat(), c.classFormat)
                && numberMatches(s.getClassInstance().getClassInstanceNo(), c.classInstance)
                && dateMatches(s.getClassInstance().getStartDate(), c.firstDate)
                && dateMatches(s.getClassInstance().getEndDate(), c.lastDate)
                && contains(s.getClassInstance().getDay(), c.day)
                && timeMatches(s.getClassInstance().getStartTime(), c.startTime)
                && timeMatches(s.getClassInstance().getEndTime(), c.endTime)
                && contains(s.getClassInstance().getBuilding(), c.building)
                && contains(s.getClassInstance().getRoom(), c.room);
    }

    private boolean contains(String actual, String expected) {
        if (expected == null || expected.isBlank()) return true;
        if (actual == null) return false;
        return actual.toLowerCase(Locale.ROOT).contains(expected.trim().toLowerCase(Locale.ROOT));
    }

    private boolean numberMatches(int actual, String expected) {
        if (expected == null || expected.isBlank()) return true;
        return String.valueOf(actual).equals(expected.trim());
    }

    private boolean dateMatches(LocalDate actual, String expected) {
        if (expected == null || expected.isBlank()) return true;
        return actual.toString().equals(expected.trim());
    }

    private boolean timeMatches(LocalTime actual, String expected) {
        if (expected == null || expected.isBlank()) return true;
        return actual.toString().equals(expected.trim());
    }

    private boolean containsIgnoreCase(List<String> list, String value) {
        for (String item : list) if (item.equalsIgnoreCase(value)) return true;
        return false;
    }
}
