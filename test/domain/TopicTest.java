package domain;
import org.junit.jupiter.api.Tag;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

class TopicCodeTest {
    Topic topic = new Topic("COMP303", "Professional Skills");
    @Test
    @DisplayName("Test get and set topic code")
    @Tag("Thomas")
    @Tag("Critical")
    void testTopicCode() {
        Topic topicTest = new Topic("COMP1702", "Fundamentals of Software Engineering");

        assertEquals("COMP1702", topicTest.getTopicCode());
        topicTest.setTopicCode("COMP1256");

        assertEquals("COMP1256", topicTest.getTopicCode());

    }

    @Test
    @DisplayName("Test get and set topic name")
    @Tag("Thomas")
    @Tag("Critical")
    void testTopicName() {
        Topic topicTest = new Topic("COMP1702", "Fundamentals of Software Engineering");

        assertEquals("Fundamentals of Software Engineering", topicTest.getTopicName());
        topicTest.setTopicName("Computer Programming 2");

        assertEquals("Computer Programming 2", topicTest.getTopicName());

    }

    @Test
    @DisplayName("Test display name if topic name is blank")
    @Tag("Thomas")
    @Tag("Critical")
    void displayNameBlankName() {
        Topic topicTest = new Topic("COMP1702", "");

        assertEquals("COMP1702", topicTest.displayName());
    }

    @Test
    @DisplayName("Test display name if topic code is blank")
    @Tag("Thomas")
    @Tag("Critical")
    void displayBlankBlank() {
        Topic topicTest = new Topic("", "Fundamentals of Software Engineering");

        assertEquals("Fundamentals of Software Engineering", topicTest.displayName());
    }

    @Test
    @DisplayName("Test display name if name and code are filed")
    @Tag("Thomas")
    @Tag("Critical")
    void displayTestComplete() {
        Topic topicTest = new Topic("COMP1702", "Fundamentals of Software Engineering");

        assertEquals("COMP1702 Fundamentals of Software Engineering", topicTest.displayName());
    }

    @Test
    @DisplayName("Test that topic copies correctly")
    @Tag("Thomas")
    @Tag("Critical")
    void copyTest() {
        Topic original = new Topic("COMP1702", "Fundamentals of Software Engineering");
        Topic copy = original.copy();

        assertEquals(original, copy);
    }

    @Test
    @DisplayName("Test toString")
    @Tag("Thomas")
    @Tag("Critical")
    void toStringTest() {
        Topic topic = new Topic("COMP1702", "Fundamentals of Software Engineering");

        assertEquals("COMP1702 Fundamentals of Software Engineering", topic.toString());
    }
    @Test
    @DisplayName("hashCode: 2 topics same with different case")
    @Tag("Thomas")
    @Tag("Critical")
    void testHasCodeSame() {
        Topic topic1 = new Topic("COMP1702", "Fundamentals");
        Topic topic2 = new Topic("comp1702", "fundamentals");

        assertEquals(topic1, topic2);
        assertEquals(topic1.hashCode(), topic2.hashCode());
    }

    @Test
    @DisplayName("hashCode: equals both same string test")
    @Tag("Thomas")
    @Tag("Core")
    void testEqualsSame1() {
        Topic topic1 = new Topic("COMP1702", "Fundamentals");
        Topic topic2 = new Topic("COMP1702", "Fundamentals");

        assertTrue(topic1.equals(topic2));
    }

    @Test
    @DisplayName("hashCode: Equals both same string different case test")
    @Tag("Thomas")
    @Tag("Critical")
    void testEqualsCase() {
        Topic topic1 = new Topic("COMP1702", "Fundamentals");
        Topic topic2 = new Topic("comp1702", "fundamentals");

        assertTrue(topic1.equals(topic2));
    }

    @Test
    @DisplayName("hashCode: Equals both different")
    @Tag("Thomas")
    @Tag("Critical")
    void testEqualsFalse() {
        Topic topic1 = new Topic("COMP1802", "Programming");
        Topic topic2 = new Topic("comp1702", "fundamentals");

        assertFalse(topic1.equals(topic2));
    }

}