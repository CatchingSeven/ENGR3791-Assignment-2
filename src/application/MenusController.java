package application;

import domain.TimetablePreferences;

/** Layer 2 component: MenusController with SessionState and RootRouter responsibilities. */
public class MenusController {
    private final SessionState sessionState;
    private final RootRouter rootRouter;

    public MenusController() {
        this.sessionState = new SessionState();
        this.rootRouter = new RootRouter(sessionState);
    }

    public SessionState getSessionState() { return sessionState; }
    public RootRouter getRootRouter() { return rootRouter; }

    public static class SessionState {
        private String currentMenuLocation = "MAIN_MENU";
        private TimetablePreferences lastUsedSettings;
        private boolean hasUnsavedChanges = false;

        public String getCurrentMenuLocation() { return currentMenuLocation; }
        public void setCurrentMenuLocation(String currentMenuLocation) { this.currentMenuLocation = currentMenuLocation; }

        public TimetablePreferences getLastUsedSettings() { return lastUsedSettings; }
        public void setLastUsedSettings(TimetablePreferences lastUsedSettings) { this.lastUsedSettings = lastUsedSettings; }

        public boolean hasUnsavedChanges() { return hasUnsavedChanges; }
        public void setHasUnsavedChanges(boolean hasUnsavedChanges) { this.hasUnsavedChanges = hasUnsavedChanges; }
    }

    public static class RootRouter {
        private final SessionState state;

        public RootRouter(SessionState state) {
            this.state = state;
        }

        public boolean requestRoute(String targetMenu, boolean confirmExitWithoutSaving) {
            if ("EXIT".equalsIgnoreCase(targetMenu) && state.hasUnsavedChanges() && !confirmExitWithoutSaving) {
                return false;
            }
            state.setCurrentMenuLocation(targetMenu == null ? "UNKNOWN" : targetMenu.toUpperCase());
            return true;
        }
    }
}
