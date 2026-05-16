package businesslogic;

import domain.Schedule;
import domain.Timetable;
import domain.TimetablePreferences;

import java.time.Duration;
import java.util.*;

/** Layer 3 component: ScheduleEngine with ClashDetector, CommuteRule and TimetableBuilder responsibilities. */
public class ScheduleEngine {
    private final SearchService searchService = new SearchService();

    public static class ConflictIssue {
        private final Schedule first;
        private final Schedule second;
        private final String message;

        public ConflictIssue(Schedule first, Schedule second, String message) {
            this.first = first;
            this.second = second;
            this.message = message;
        }

        public Schedule getFirst() { return first; }
        public Schedule getSecond() { return second; }
        public String getMessage() { return message; }
    }

    public List<Timetable> buildTimetables(List<Schedule> allClasses, TimetablePreferences preferences, int maxResults) {
        List<Schedule> filtered = filterClassesForPreferences(allClasses, preferences);
        Map<String, List<Schedule>> rowsByTopic = groupRowsByTopic(filtered);
        List<List<List<Schedule>>> plansByTopic = new ArrayList<>();

        for (List<Schedule> topicRows : rowsByTopic.values()) {
            List<List<Schedule>> topicPlans = new ArrayList<>();
            for (List<Schedule> pathRows : splitTopicRowsIntoCampusCompatiblePaths(topicRows)) {
                topicPlans.addAll(buildPlansForSingleTopicPath(pathRows, preferences));
            }
            if (topicPlans.isEmpty()) {
                return new ArrayList<>();
            }
            plansByTopic.add(topicPlans);
        }

        List<Timetable> results = new ArrayList<>();
        backtrackTopicPlans(0, plansByTopic, new ArrayList<>(), preferences, results, maxResults);
        return results;
    }

    public List<Schedule> filterClassesForPreferences(List<Schedule> allClasses, TimetablePreferences preferences) {
        List<Schedule> filtered = new ArrayList<>();
        for (Schedule schedule : allClasses) {
            if (!semesterMatches(schedule, preferences.getSemester())) continue;
            if (!topicSelected(schedule, preferences.getSelectedTopicCodes())) continue;
            if (!campusSelected(schedule, preferences.getSelectedCampuses())) continue;
            filtered.add(schedule);
        }
        return filtered;
    }

    public List<ConflictIssue> findIssues(List<Schedule> schedule, boolean allowLectureOverlap) {
        List<ConflictIssue> issues = new ArrayList<>();
        for (int i = 0; i < schedule.size(); i++) {
            for (int j = i + 1; j < schedule.size(); j++) {
                ConflictIssue issue = findIssue(schedule.get(i), schedule.get(j), allowLectureOverlap);
                if (issue != null) issues.add(issue);
            }
        }
        return issues;
    }

    public ConflictIssue findIssue(Schedule a, Schedule b, boolean allowLectureOverlap) {
        if (!sameDayAndDateWindow(a, b)) return null;
        boolean lectureCanOverlap = allowLectureOverlap && (a.isLecture() || b.isLecture());
        boolean overlaps = a.startTime().isBefore(b.endTime()) && a.endTime().isAfter(b.startTime());
        if (overlaps && !lectureCanOverlap) {
            return new ConflictIssue(a, b, "Time clash between " + a.compactLabel() + " and " + b.compactLabel());
        }
        if (overlaps) return null;

        if (!sameCampus(a, b)) {
            long gap = gapMinutes(a, b);
            if (gap >= 0 && gap < 30) {
                return new ConflictIssue(a, b, "Not enough commute time between campuses (" + gap + " minutes) for " + a.compactLabel() + " and " + b.compactLabel());
            }
        }
        return null;
    }

    public boolean hasTimeClashOrCommuteIssue(List<Schedule> existingSchedule, Schedule newClass, boolean allowLectureOverlap) {
        for (Schedule existing : existingSchedule) {
            if (findIssue(existing, newClass, allowLectureOverlap) != null) return true;
        }
        return false;
    }

    public boolean violatesCityMixingRule(List<Schedule> existingSchedule, List<Schedule> candidateOffering) {
        if (candidateOffering.isEmpty()) return false;
        String topicCode = candidateOffering.get(0).getTopic().getTopicCode();
        boolean candidateHasCity = containsCityCampus(candidateOffering);
        boolean candidateHasNonCity = containsNonCityCampus(candidateOffering);
        for (Schedule existing : existingSchedule) {
            if (!existing.getTopic().getTopicCode().equalsIgnoreCase(topicCode)) continue;
            boolean existingCity = isCityCampus(existing.getAvailability().getCampus());
            if ((candidateHasCity && !existingCity) || (candidateHasNonCity && existingCity)) return true;
        }
        return false;
    }

    private List<List<Schedule>> buildPlansForSingleTopicPath(List<Schedule> pathRows, TimetablePreferences preferences) {
        Map<String, List<List<Schedule>>> candidatesByRequirement = searchService.groupCandidatesByRequiredClass(pathRows);
        List<String> requirementKeys = new ArrayList<>(candidatesByRequirement.keySet());
        List<List<Schedule>> plans = new ArrayList<>();
        backtrackRequirementOfferings(0, requirementKeys, candidatesByRequirement, new ArrayList<>(), preferences, plans, 200);
        return plans;
    }

    private void backtrackRequirementOfferings(int index, List<String> requirementKeys,
                                               Map<String, List<List<Schedule>>> candidatesByRequirement,
                                               List<Schedule> chosen, TimetablePreferences preferences,
                                               List<List<Schedule>> plans, int maxPlans) {
        if (plans.size() >= maxPlans) return;
        if (index >= requirementKeys.size()) {
            plans.add(copySchedules(chosen));
            return;
        }

        List<List<Schedule>> candidates = candidatesByRequirement.get(requirementKeys.get(index));
        if (candidates == null || candidates.isEmpty()) return;
        for (List<Schedule> offering : candidates) {
            if (canAddOffering(chosen, offering, preferences.isAllowLectureOverlap())) {
                chosen.addAll(offering);
                backtrackRequirementOfferings(index + 1, requirementKeys, candidatesByRequirement, chosen, preferences, plans, maxPlans);
                for (int i = 0; i < offering.size(); i++) chosen.remove(chosen.size() - 1);
            }
            if (plans.size() >= maxPlans) return;
        }
    }

    private void backtrackTopicPlans(int index, List<List<List<Schedule>>> plansByTopic, List<Schedule> chosen,
                                     TimetablePreferences preferences, List<Timetable> results, int maxResults) {
        if (results.size() >= maxResults) return;
        if (index >= plansByTopic.size()) {
            Timetable timetable = new Timetable("TT-" + (results.size() + 1), preferences.getTimetableName(), preferences.getSemester(), preferences.isAllowLectureOverlap());
            timetable.setSchedules(copySchedules(chosen));
            for (ConflictIssue issue : findIssues(timetable.getSchedules(), preferences.isAllowLectureOverlap())) {
                timetable.addWarning(issue.getMessage());
            }
            results.add(timetable);
            return;
        }

        for (List<Schedule> topicPlan : plansByTopic.get(index)) {
            if (canAddPlan(chosen, topicPlan, preferences.isAllowLectureOverlap())) {
                chosen.addAll(topicPlan);
                backtrackTopicPlans(index + 1, plansByTopic, chosen, preferences, results, maxResults);
                for (int i = 0; i < topicPlan.size(); i++) chosen.remove(chosen.size() - 1);
            }
            if (results.size() >= maxResults) return;
        }
    }

    private boolean canAddPlan(List<Schedule> chosen, List<Schedule> plan, boolean allowLectureOverlap) {
        List<Schedule> proposed = new ArrayList<>(chosen);
        for (Schedule schedule : plan) {
            if (hasTimeClashOrCommuteIssue(proposed, schedule, allowLectureOverlap)) return false;
            proposed.add(schedule);
        }
        return true;
    }

    private Map<String, List<Schedule>> groupRowsByTopic(List<Schedule> rows) {
        Map<String, List<Schedule>> grouped = new java.util.LinkedHashMap<>();
        for (Schedule row : rows) {
            grouped.computeIfAbsent(row.getTopic().getTopicCode().toLowerCase(Locale.ROOT), k -> new ArrayList<>()).add(row);
        }
        return grouped;
    }

    private List<List<Schedule>> splitTopicRowsIntoCampusCompatiblePaths(List<Schedule> topicRows) {
        List<Schedule> cityRows = new ArrayList<>();
        List<Schedule> nonCityRows = new ArrayList<>();
        for (Schedule row : topicRows) {
            if (isCityCampus(row.getAvailability().getCampus())) cityRows.add(row);
            else nonCityRows.add(row);
        }
        List<List<Schedule>> paths = new ArrayList<>();
        if (!nonCityRows.isEmpty()) paths.add(nonCityRows);
        if (!cityRows.isEmpty()) paths.add(cityRows);
        return paths;
    }

    private boolean canAddOffering(List<Schedule> chosen, List<Schedule> offering, boolean allowLectureOverlap) {
        List<Schedule> proposed = new ArrayList<>(chosen);
        for (Schedule schedule : offering) {
            if (hasTimeClashOrCommuteIssue(proposed, schedule, allowLectureOverlap)) return false;
            proposed.add(schedule);
        }
        return findIssues(offering, allowLectureOverlap).isEmpty();
    }

    private List<Schedule> copySchedules(List<Schedule> schedules) {
        List<Schedule> copies = new ArrayList<>();
        for (Schedule schedule : schedules) copies.add(schedule.copy());
        copies.sort(searchService.scheduleComparator());
        return copies;
    }

    private boolean semesterMatches(Schedule schedule, String requestedSemester) {
        if (requestedSemester == null || requestedSemester.isBlank() || requestedSemester.equalsIgnoreCase("both")) return true;
        String normalized = requestedSemester.trim().toUpperCase(Locale.ROOT);
        if (normalized.equals("1")) normalized = "S1";
        if (normalized.equals("2")) normalized = "S2";
        return schedule.getAvailability().getSemester().equalsIgnoreCase(normalized);
    }

    private boolean topicSelected(Schedule schedule, List<String> selectedTopicCodes) {
        if (selectedTopicCodes == null || selectedTopicCodes.isEmpty()) return true;
        for (String topic : selectedTopicCodes) {
            if (schedule.getTopic().getTopicCode().equalsIgnoreCase(topic.trim())) return true;
        }
        return false;
    }

    private boolean campusSelected(Schedule schedule, List<String> selectedCampuses) {
        if (selectedCampuses == null || selectedCampuses.isEmpty()) return true;
        for (String campus : selectedCampuses) {
            if (schedule.getAvailability().getCampus().equalsIgnoreCase(campus.trim())) return true;
        }
        return false;
    }

    private boolean sameDayAndDateWindow(Schedule a, Schedule b) {
        if (!a.normalDay().equalsIgnoreCase(b.normalDay())) return false;
        return !a.endDate().isBefore(b.startDate()) && !b.endDate().isBefore(a.startDate());
    }

    private boolean sameCampus(Schedule a, Schedule b) {
        return a.getAvailability().getCampus().equalsIgnoreCase(b.getAvailability().getCampus());
    }

    private long gapMinutes(Schedule a, Schedule b) {
        if (a.endTime().isBefore(b.startTime()) || a.endTime().equals(b.startTime())) {
            return Duration.between(a.endTime(), b.startTime()).toMinutes();
        }
        if (b.endTime().isBefore(a.startTime()) || b.endTime().equals(a.startTime())) {
            return Duration.between(b.endTime(), a.startTime()).toMinutes();
        }
        return -1;
    }

    private boolean containsCityCampus(List<Schedule> schedules) {
        for (Schedule s : schedules) if (isCityCampus(s.getAvailability().getCampus())) return true;
        return false;
    }

    private boolean containsNonCityCampus(List<Schedule> schedules) {
        for (Schedule s : schedules) if (!isCityCampus(s.getAvailability().getCampus())) return true;
        return false;
    }

    private boolean isCityCampus(String campus) {
        return campus != null && campus.toLowerCase(Locale.ROOT).contains("city");
    }

    public Set<String> campusesUsed(List<Schedule> schedules) {
        Set<String> campuses = new HashSet<>();
        for (Schedule s : schedules) campuses.add(s.getAvailability().getCampus().toLowerCase(Locale.ROOT));
        return campuses;
    }
}
