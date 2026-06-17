package domain;
import org.junit.jupiter.api.Tag;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

class TopicTest {
    Topic topic = new Topic("COMP303", "Professional Skills");
    @ParameterizedTest
    @CsvSource({"COMP1702, COMP1256", "ENGR1267, COMP1267", "COMP0986, ENGR1289"})
    @DisplayName("Test get and set topic code")
    @Tag("1.7.1")
    @Tag("Thomas")
    @Tag("Critical")
    void testTopicCode(String topicCode1, String topicCode2) {
        Topic topicTest = new Topic(topicCode1, "Fundamentals of Software Engineering");

        assertAll(
                () -> assertEquals(topicCode1, topicTest.getTopicCode()),
                () -> topicTest.setTopicCode(topicCode2),
                () -> assertEquals(topicCode2, topicTest.getTopicCode())
        );


    }

    @ParameterizedTest
    @CsvSource({"Fundamentals of Software Engineering, Computer Programming 2",
            "UX Fundamentals, Computer Programming",
            "Software Systems, Software Testing"})
    @DisplayName("Test get and set topic name")
    @Tag("1.7.1")
    @Tag("Thomas")
    @Tag("Critical")
    void testTopicName(String topicName1, String topicName2) {
        Topic topicTest = new Topic("COMP1702", topicName1);

        assertEquals(topicName1, topicTest.getTopicName());
        topicTest.setTopicName(topicName2);

        assertEquals(topicName2, topicTest.getTopicName());

    }

    @ParameterizedTest
    @ValueSource(strings = {"COMP1702", "ENGR1287", "COMP9846"})
    @DisplayName("Test display name if topic name is blank")
    @Tag("1.7.1")
    @Tag("Thomas")
    @Tag("Critical")
    void displayNameBlankName(String topicCode) {
        Topic topicTest = new Topic(topicCode, "");

        assertEquals(topicCode, topicTest.displayName());
    }

    @ParameterizedTest
    @ValueSource(strings = {"Fundamentals of Software Engineering", "Computer Programming", "Software Testing"})
    @DisplayName("Test display name if topic code is blank")
    @Tag("1.7.1")
    @Tag("Thomas")
    @Tag("Critical")
    void displayBlankBlank(String topicName) {
        Topic topicTest = new Topic("", topicName);

        assertEquals(topicName, topicTest.displayName());
    }

    @ParameterizedTest
    @CsvSource({"COMP2623, Computer Programming",
            "COMP1702, Fundamentals of Software Engineering",
            "ENGR1278, Professional Standards"})
    @DisplayName("Test display name if name and code are filled")
    @Tag("1.7.1")
    @Tag("Thomas")
    @Tag("Critical")
    void displayTestComplete(String topicCode, String topicName) {
        Topic topicTest = new Topic(topicCode, topicName);

        assertEquals(topicCode + " " + topicName, topicTest.displayName());
    }

    @ParameterizedTest
    @CsvSource({"COMP2623, Computer Programming",
            "COMP1702, Fundamentals of Software Engineering",
            "ENGR1278, Professional Standards"})
    @DisplayName("Test that topic copies correctly")
    @Tag("1.7.1")
    @Tag("Thomas")
    @Tag("Critical")
    void copyTest(String topicCode, String topicName) {
        Topic original = new Topic(topicCode, topicName);
        Topic copy = original.copy();

        assertEquals(original, copy);
    }

    @ParameterizedTest
    @CsvSource({"COMP0880, Computer Programming 2",
            "COMP1702, Software Testing",
            "ENGR0972, Professional Standards"})
    @DisplayName("Test the the topic is converted to a string correctly")
    @Tag("1.7.1")
    @Tag("Thomas")
    @Tag("Critical")
    void toStringTest(String topicCode, String topicName) {
        Topic topic = new Topic(topicCode, topicName);

        assertEquals(topicCode + " " + topicName, topic.toString());
    }
    @ParameterizedTest
    @CsvSource({"COMP1702, comp1702, Computer Programming, computer programming",
            "ENGR7684, Engr7684, Fundamentals of Software Engineering, fundamentals of software engineering",
            "comp1256, COmp1256, Software testing, Software Testing"})
    @DisplayName("Test 2 topics same with different case in hascode function")
    @Tag("1.7.1")
    @Tag("Thomas")
    @Tag("Critical")
    void testHasCodeSame(String topicCode1, String topicCode2, String topicName1, String topicName2) {
        Topic topic1 = new Topic(topicCode1, topicName1);
        Topic topic2 = new Topic(topicCode2, topicName2);

        assertEquals(topic1, topic2);
        assertEquals(topic1.hashCode(), topic2.hashCode());
    }

    @ParameterizedTest
    @CsvSource({"COMP1702, Computer Programming",
            "ENGR1823,  Fundamentals of Software Engineering",
            "comp1098, Software Testing"})
    @DisplayName("Test hashcode when both strings are exactly equal")
    @Tag("1.7.1")
    @Tag("Thomas")
    @Tag("Core")
    void testEqualsSame1(String topicCode, String topicName) {
        Topic topic1 = new Topic(topicCode, topicName);
        Topic topic2 = new Topic(topicCode, topicName);

        assertTrue(topic1.equals(topic2));
    }

    @ParameterizedTest
    @CsvSource({"COMP1702, comp1702, Computer Programming, software Testing",
            "ENGR7684, Engr7684, Fundamentals of Software Engineering, fundamentals of prgramming",
            "comp1256, COmp1256, Software testing, Software systems"})
    @DisplayName("hashCode: Equals both different")
    @Tag("1.7.1")
    @Tag("Thomas")
    @Tag("Critical")
    void testEqualsFalse() {
        Topic topic1 = new Topic("COMP1802", "Programming");
        Topic topic2 = new Topic("comp1702", "fundamentals");

        assertFalse(topic1.equals(topic2));
    }

}