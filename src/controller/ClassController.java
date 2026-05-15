package controller;

import domain.ClassInstance;
import service.SearchService;

import java.util.List;
import java.util.function.Predicate;

/**
 * ClassController coordinates use cases relating to class management.
 */
public class ClassController {

    private final EditHandler editHandler = new EditHandler();
    private final BrowseHandler browseHandler = new BrowseHandler();
    private final DeleteHandler deleteHandler = new DeleteHandler();
    private final SearchHandler searchHandler = new SearchHandler();

    private final SearchService searchService;

    public ClassController(SearchService searchService) {
        this.searchService = searchService;
    }

    public EditHandler getEditHandler() { return editHandler; }
    public BrowseHandler getBrowseHandler() { return browseHandler; }
    public DeleteHandler getDeleteHandler() { return deleteHandler; }
    public SearchHandler getSearchHandler() { return searchHandler; }

    // ==========================================
    // Internal Components
    // ==========================================

    public class EditHandler {
        /**
         * Coordinates editing a class. Enforces confirmation.
         */
        public boolean processEdit(ClassInstance original, ClassInstance updated, boolean isConfirmed) {
            if (!isConfirmed) {
                // Presentation layer must request user confirmation first
                return false;
            }
            // Update logic executed here (typically passed down to PersistenceAdapter via a repository)
            original.setStartTime(updated.getStartTime());
            original.setEndTime(updated.getEndTime());
            original.setRoom(updated.getRoom());
            return true;
        }
    }

    public class BrowseHandler {
        /**
         * Coordinates retrieval of all classes for browsing.
         */
        public List<ClassInstance> processBrowse(List<ClassInstance> allDatabaseClasses) {
            // Further sorting or grouping logic can be applied here before sending to Presentation
            return allDatabaseClasses;
        }
    }

    public class DeleteHandler {
        /**
         * Coordinates deleting a class. Enforces confirmation.
         */
        public boolean processDelete(List<ClassInstance> database, ClassInstance target, boolean isConfirmed) {
            if (!isConfirmed) {
                // Presentation layer must request user confirmation first
                return false;
            }
            return database.remove(target);
        }
    }

    public class SearchHandler {
        /**
         * Coordinates searching classes utilizing the SearchService.
         */
        public List<SearchService.FullClassDetails> processSearch(
                List<SearchService.FullClassDetails> dataset,
                Predicate<SearchService.FullClassDetails> criteria) {

            return searchService.searchClasses(dataset, criteria);
        }
    }
}