package domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TopicClassTest {
    Topic topic = new Topic("COMP303", "Professional Skills");
    @Test
    @DisplayName("Test get and set class format")
    @Tag("Thomas")
    @Tag("Critical")
    void testClassFormat1() {
        TopicClass topicClassTest = new TopicClass("Lecture", "COMP1702");

        assertEquals("Lecture", topicClassTest.getClassFormat());

        topicClassTest.setClassFormat("Workshop");

        assertEquals("Workshop", topicClassTest.getClassFormat());

    }

    @Test
    @DisplayName("Test get and set class code")
    @Tag("Thomas")
    @Tag("Critical")
    void testClassCode1() {
        TopicClass topicClassTest = new TopicClass("Lecture", "COMP1702");

        assertEquals("COMP1702", topicClassTest.getClassCode());

        topicClassTest.setClassCode("ENGR0342");

        assertEquals("ENGR0342", topicClassTest.getClassCode());

    }

    @Test
    @DisplayName("Test class copy")
    @Tag("Thomas")
    @Tag("Critical")
    void testCopy1() {
        TopicClass original = new TopicClass("Lecture", "COMP1702");
        TopicClass copy = original.copy();

        assertAll(
                () -> assertEquals(original.getClassFormat(), copy.getClassFormat()),
                () -> assertEquals(original.getClassCode(), copy.getClassCode())
        );

    }
}