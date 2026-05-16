package application;

import businesslogic.PreferenceEngine;
import businesslogic.ScheduleEngine;
import businesslogic.SearchService;
import businesslogic.ValidationService;
import domain.Schedule;
import domain.Timetable;
import domain.TimetablePreferences;
import persistence.PersistenceAdapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Layer 2 component: TimetableController with ExportHandler, GenerateHandler and EditHandler responsibilities. */
public class TimetableController {
    private final ScheduleEngine scheduleEngine;
    private final PreferenceEngine preferenceEngine;
    private final ValidationService validationService;
    private final PersistenceAdapter persistenceAdapter;
    private final SearchService searchService;
    private final List<Timetable> timetables;

    private final ExportHandler exportHandler = new ExportHandler();
    private final GenerateHandler generateHandler = new GenerateHandler();
    private final EditHandler editHandler = new EditHandler();

    public TimetableController(ScheduleEngine scheduleEngine, PreferenceEngine preferenceEngine,
                               ValidationService validationService, PersistenceAdapter persistenceAdapter,
                               SearchService searchService) {
        this.scheduleEngine = scheduleEngine;
        this.preferenceEngine = preferenceEngine;
        this.validationService = validationService;
        this.persistenceAdapter = persistenceAdapter;
        this.searchService = searchService;
        this.timetables = new ArrayList<>();
    }

    public List<Timetable> getTimetables() { return timetables; }

    public Timetable generateTimetable(TimetablePreferences preferences, List<Schedule> classDatabase) {
        return generateHandler.processGenerate(preferences, classDatabase);
    }

    public Timetable findTimetable(String codeOrName) {
        for (Timetable timetable : timetables) {
            if (timetable.getTimetableCode().equalsIgnoreCase(codeOrName)
                    || timetable.getTimetableName().equalsIgnoreCase(codeOrName)) {
                return timetable;
            }
        }
        return null;
    }

    public boolean deleteTimetable(String codeOrName, boolean confirmed) {
        if (!confirmed) return false;
        Timetable timetable = findTimetable(codeOrName);
        return timetable != null && timetables.remove(timetable);
    }

    public List<Schedule> getSwapCandidates(Timetable timetable, int currentRecordId, List<Schedule> classDatabase) {
        Schedule current = searchService.findById(timetable.getSchedules(), currentRecordId);
        if (current == null) return new ArrayList<>();
        List<Schedule> candidates = new ArrayList<>();
        for (Schedule schedule : classDatabase) {
            if (schedule.sameTopicAndClass(current) && !schedule.classOfferingKey().equalsIgnoreCase(current.classOfferingKey())) {
                candidates.add(schedule);
            }
        }
        Map<String, Schedule> onePerOffering = new LinkedHashMap<>();
        for (Schedule candidate : candidates) {
            onePerOffering.putIfAbsent(candidate.classOfferingKey(), candidate);
        }
        List<Schedule> result = new ArrayList<>(onePerOffering.values());
        result.sort(searchService.scheduleComparator());
        return result;
    }

    public SwapResult swapClass(String timetableCodeOrName, int currentRecordId, int replacementRecordId,
                                List<Schedule> classDatabase, boolean confirmedIfWarning) {
        return editHandler.processSwap(timetableCodeOrName, currentRecordId, replacementRecordId, classDatabase, confirmedIfWarning);
    }

    public boolean exportTimetable(String codeOrName, String filePath) throws IOException {
        Timetable timetable = findTimetable(codeOrName);
        return exportHandler.processExport(timetable, filePath);
    }

    public ExportHandler getExportHandler() { return exportHandler; }
    public GenerateHandler getGenerateHandler() { return generateHandler; }
    public EditHandler getEditHandler() { return editHandler; }

    public class GenerateHandler {
        public Timetable processGenerate(TimetablePreferences preferences, List<Schedule> classDatabase) {
            if (preferences.getSelectedTopicCodes().isEmpty()) {
                throw new IllegalArgumentException("At least one topic must be selected.");
            }
            String uniqueName = validationService.ensureUniqueTimetableName(preferences.getTimetableName(), timetables);
            preferences.setTimetableName(uniqueName);
            List<Timetable> generated = scheduleEngine.buildTimetables(classDatabase, preferences, 20);
            if (generated.isEmpty()) return null;
            List<Timetable> ranked = preferenceEngine.rankPreferences(generated, preferences);
            Timetable best = ranked.get(0);
            best.setTimetableCode("TT-" + (timetables.size() + 1));
            best.setTimetableName(uniqueName);
            timetables.add(best);
            return best;
        }
    }

    public class EditHandler {
        public SwapResult processSwap(String timetableCodeOrName, int currentRecordId, int replacementRecordId,
                                      List<Schedule> classDatabase, boolean confirmedIfWarning) {
            Timetable timetable = findTimetable(timetableCodeOrName);
            if (timetable == null) return SwapResult.FAILURE;
            Schedule current = searchService.findById(timetable.getSchedules(), currentRecordId);
            Schedule replacement = searchService.findById(classDatabase, replacementRecordId);
            if (current == null || replacement == null || !current.sameTopicAndClass(replacement)) {
                return SwapResult.FAILURE;
            }

            List<Schedule> replacementOffering = getOfferingRows(classDatabase, replacement.classOfferingKey());
            List<Schedule> proposed = new ArrayList<>();
            for (Schedule s : timetable.getSchedules()) {
                if (!s.classOfferingKey().equalsIgnoreCase(current.classOfferingKey())) proposed.add(s.copy());
            }
            for (Schedule s : replacementOffering) proposed.add(s.copy());
            proposed.sort(searchService.scheduleComparator());

            List<ScheduleEngine.ConflictIssue> issues = scheduleEngine.findIssues(proposed, timetable.isAllowOverlap());
            if (!issues.isEmpty() && !confirmedIfWarning) {
                timetable.getWarnings().clear();
                for (ScheduleEngine.ConflictIssue issue : issues) timetable.addWarning(issue.getMessage());
                return SwapResult.WARNING_REQUIRES_CONFIRMATION;
            }

            timetable.setSchedules(proposed);
            timetable.getWarnings().clear();
            for (ScheduleEngine.ConflictIssue issue : issues) timetable.addWarning(issue.getMessage());
            return SwapResult.SUCCESS;
        }

        private List<Schedule> getOfferingRows(List<Schedule> classDatabase, String offeringKey) {
            List<Schedule> rows = new ArrayList<>();
            for (Schedule schedule : classDatabase) {
                if (schedule.classOfferingKey().equalsIgnoreCase(offeringKey)) rows.add(schedule);
            }
            return rows;
        }
    }

    public class ExportHandler {
        public boolean processExport(Timetable timetable, String filePath) throws IOException {
            if (timetable == null || timetable.getSchedules().isEmpty()) return false;
            persistenceAdapter.exportTimetable(timetable, filePath);
            return true;
        }
    }

    public enum SwapResult {
        SUCCESS,
        WARNING_REQUIRES_CONFIRMATION,
        FAILURE
    }
}
