package application;

import domain.TimetablePreferences;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class MenusControllerTest {

    private MenusController controller;

    @BeforeEach
    void setUp() {
        controller = new MenusController();
    }

    @Test
    @Order(1)
    @DisplayName("17.01 - SessionState has sensible defaults")
    @Tag("Critical")
    @Tag("Samuel")
    void sessionStateHasSensibleDefaults() {
        MenusController.SessionState state = controller.getSessionState();
        assertAll(
                () -> assertNotNull(state),
                () -> assertEquals("MAIN_MENU", state.getCurrentMenuLocation()),
                () -> assertFalse(state.hasUnsavedChanges()),
                () -> assertNull(state.getLastUsedSettings())
        );
    }

    @Test
    @Order(2)
    @DisplayName("17.02 - RootRouter navigates and updates location")
    @Tag("Core")
    @Tag("Samuel")
    void rootRouterNavigatesAndUpdatesLocation() {
        boolean ok = controller.getRootRouter().requestRoute("TIMETABLES", false);
        assertAll(
                () -> assertTrue(ok),
                () -> assertEquals("TIMETABLES", controller.getSessionState().getCurrentMenuLocation())
        );
    }

    @Test
    @Order(3)
    @DisplayName("17.03 - RootRouter blocks EXIT when unsaved changes and not confirmed")
    @Tag("Critical")
    @Tag("Samuel")
    void rootRouterBlocksExitWhenUnsavedAndNotConfirmed() {
        controller.getSessionState().setHasUnsavedChanges(true);
        assertFalse(controller.getRootRouter().requestRoute("EXIT", false));
    }

    @Test
    @Order(4)
    @DisplayName("17.04 - setLastUsedSettings stores preferences correctly")
    @Tag("Core")
    @Tag("Samuel")
    void setLastUsedSettingsStoresPreferences() {
        TimetablePreferences prefs = new TimetablePreferences();
        prefs.setTimetableName("Test");
        controller.getSessionState().setLastUsedSettings(prefs);
        assertEquals("Test", controller.getSessionState().getLastUsedSettings().getTimetableName());
    }

    @Test
    @Order(5)
    @DisplayName("17.05 - Null target stored as UNKNOWN in RootRouter")
    @Tag("Additional")
    @Tag("Samuel")
    void nullTargetStoredAsUnknown() {
        controller.getRootRouter().requestRoute(null, false);
        assertEquals("UNKNOWN", controller.getSessionState().getCurrentMenuLocation());
    }
}