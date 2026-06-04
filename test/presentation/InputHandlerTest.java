package presentation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;

class InputHandlerTest {

    @Test
    @Tag("Core")
    @Tag("Lachlan")
    @DisplayName("InputHandler reads normal input")
    void readsNormalInput() {
        setInput("hello\n");

        InputHandler inputHandler = new InputHandler(new OutputFormatter());

        String result = inputHandler.inputReader.readString("Enter input: ");

        assertEquals("hello", result);
    }

    @Test
    @Tag("Core")
    @Tag("Lachlan")
    @DisplayName("InputHandler validates menu choices")
    void validatesMenuChoices() {
        setInput("abc\n9\n2\n");

        InputHandler inputHandler = new InputHandler(new OutputFormatter());

        int result = inputHandler.menuValidator.readValidIntRange("Choose option: ", 1, 5);

        assertEquals(2, result);
    }

    @Test
    @Tag("Core")
    @Tag("Lachlan")
    @DisplayName("InputHandler recognises exit commands")
    void recognisesExitCommands() {
        setInput("EXIT\n");

        InputHandler inputHandler = new InputHandler(new OutputFormatter());

        assertThrows(
                InputHandler.ExitRequestedException.class,
                () -> inputHandler.inputReader.readString("Enter input: ")
        );
    }

    private void setInput(String input) {
        InputStream testInput = new ByteArrayInputStream(input.getBytes());
        System.setIn(testInput);
    }
}