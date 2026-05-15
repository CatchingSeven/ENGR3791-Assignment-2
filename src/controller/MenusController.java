package controller;

import domain.TimetablePreferences;

/**
 * MenusController acts as the primary coordinator for application state and navigation.
 */
public class MenusController {

    private final SessionState sessionState;
    private final RootRouter rootRouter;

    public MenusController() {
        this.sessionState = new SessionState();
        this.rootRouter = new RootRouter(this.sessionState);
    }

    public SessionState getSessionState() { return sessionState; }
    public RootRouter getRootRouter() { return rootRouter; }

    // ==========================================
    // Internal Components
    // ==========================================

    public class SessionState {
        private String currentMenuLocation = "MAIN_MENU";
        private TimetablePreferences lastUsedSettings;
        private boolean hasUnsavedChanges = false;

        public String getCurrentMenuLocation() { return currentMenuLocation; }
        public void setCurrentMenuLocation(String location) { this.currentMenuLocation = location; }

        public TimetablePreferences getLastUsedSettings() { return lastUsedSettings; }
        public void setLastUsedSettings(TimetablePreferences settings) { this.lastUsedSettings = settings; }

        public boolean hasUnsavedChanges() { return hasUnsavedChanges; }
        public void setHasUnsavedChanges(boolean hasUnsavedChanges) { this.hasUnsavedChanges = hasUnsavedChanges; }
    }

    public class RootRouter {
        private final SessionState state;

        public RootRouter(SessionState state) {
            this.state = state;
        }

        /**
         * Routes the user to a new menu, checking for unsaved work if exiting.
         */
        public boolean requestRoute(String targetMenu, boolean confirmExitWithoutSaving) {
            if (targetMenu.equalsIgnoreCase("EXIT") && state.hasUnsavedChanges() && !confirmExitWithoutSaving) {
                // Returns false to notify the Presentation layer to prompt for a save confirmation
                return false;
            }
            state.setCurrentMenuLocation(targetMenu.toUpperCase());
            return true;
        }
    }
}