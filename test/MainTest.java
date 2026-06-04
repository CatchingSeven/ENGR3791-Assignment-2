import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class MainTest {

    private final InputStream originalIn = System.in;

    @AfterEach
    void restoreInput() {
        System.setIn(originalIn);
    }

    @Test
    @Tag("Additional")
    @Tag("Lachlan")
    @DisplayName("Main creates the services/controllers and starts the console UI")
    void mainCreatesServicesControllersAndStartsConsoleUi() {
        System.setIn(new ByteArrayInputStream("13\n".getBytes()));

        assertDoesNotThrow(() -> Main.main(new String[]{}));
    }
}