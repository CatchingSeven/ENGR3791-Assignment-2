package application;

import businesslogic.ImportService;
import businesslogic.SearchService;
import businesslogic.ValidationService;
import domain.Schedule;
import persistence.PersistenceAdapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/** Layer 2 component: ClassController with EditHandler, BrowseHandler, DeleteHandler and SearchHandler responsibilities. */
public class ClassController {
    private final ImportService importService;
    private final SearchService searchService;
    private final ValidationService validationService;
    private final PersistenceAdapter persistenceAdapter;
    private final List<Schedule> classDatabase;

    private final EditHandler editHandler = new EditHandler();
    private final BrowseHandler browseHandler = new BrowseHandler();
    private final DeleteHandler deleteHandler = new DeleteHandler();
    private final SearchHandler searchHandler = new SearchHandler();

    public ClassController(ImportService importService, SearchService searchService,
                           ValidationService validationService, PersistenceAdapter persistenceAdapter) {
        this.importService = importService;
        this.searchService = searchService;
        this.validationService = validationService;
        this.persistenceAdapter = persistenceAdapter;
        this.classDatabase = new ArrayList<>();
    }

    public void loadSavedClasses() throws IOException {
        classDatabase.clear();
        classDatabase.addAll(persistenceAdapter.loadClasses());
    }

    public ImportService.ImportResult importCsv(String filePath) throws IOException {
        List<Schedule> incoming = persistenceAdapter.readHandbookCsv(filePath);
        ImportService.ImportResult result = importService.mergeImportedSchedules(incoming, classDatabase);
        persistenceAdapter.saveClasses(classDatabase);
        return result;
    }

    public List<Schedule> getAllClasses() { return classDatabase; }

    public Schedule findClassById(int recordId) { return searchService.findById(classDatabase, recordId); }

    public List<SearchService.BrowseClassSummary> browseClasses() {
        return browseHandler.processBrowse(classDatabase);
    }

    public List<Schedule> viewClasses() {
        return searchHandler.processSearch(new SearchService.SearchCriteria());
    }

    public List<Schedule> searchClasses(SearchService.SearchCriteria criteria) {
        return searchHandler.processSearch(criteria);
    }

    public boolean editClass(int recordId, Schedule updated, boolean confirmed) throws IOException {
        return editHandler.processEdit(recordId, updated, confirmed);
    }

    public boolean deleteClass(int recordId, boolean confirmed) throws IOException {
        return deleteHandler.processDelete(recordId, confirmed);
    }

    public List<String> distinctTopicLabels() { return searchService.distinctTopicLabels(classDatabase); }
    public List<String> distinctCampuses() { return searchService.distinctCampuses(classDatabase); }

    public EditHandler getEditHandler() { return editHandler; }
    public BrowseHandler getBrowseHandler() { return browseHandler; }
    public DeleteHandler getDeleteHandler() { return deleteHandler; }
    public SearchHandler getSearchHandler() { return searchHandler; }

    public class EditHandler {
        public boolean processEdit(int recordId, Schedule updated, boolean confirmed) throws IOException {
            if (!confirmed) return false;
            validationService.validateSchedule(updated);
            Schedule original = findClassById(recordId);
            if (original == null) return false;
            int index = classDatabase.indexOf(original);
            updated.setRecordId(recordId);
            classDatabase.set(index, updated);
            persistenceAdapter.saveClasses(classDatabase);
            return true;
        }
    }

    public class BrowseHandler {
        public List<SearchService.BrowseClassSummary> processBrowse(List<Schedule> allDatabaseClasses) {
            return searchService.browseGroups(allDatabaseClasses);
        }
    }

    public class DeleteHandler {
        public boolean processDelete(int recordId, boolean confirmed) throws IOException {
            if (!confirmed) return false;
            Schedule target = findClassById(recordId);
            if (target == null) return false;
            boolean removed = classDatabase.remove(target);
            if (removed) persistenceAdapter.saveClasses(classDatabase);
            return removed;
        }
    }

    public class SearchHandler {
        public List<Schedule> processSearch(SearchService.SearchCriteria criteria) {
            return searchService.searchClasses(classDatabase, criteria);
        }
    }
}
