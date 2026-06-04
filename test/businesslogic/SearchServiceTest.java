package businesslogic;

import domain.ClassAvailability;
import domain.ClassInstance;
import domain.Schedule;
import domain.Topic;
import domain.TopicClass;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SearchServiceTest {
    private Schedule createSchedule(int id, String topicCode, String campus, String day) {
        Topic topic = new Topic(topicCode, "Game Design");
        ClassAvailability availability = new ClassAvailability("In person", campus, "S2", 1);
        TopicClass topicClass = new TopicClass("Workshop", "Workshop");

        ClassInstance classInstance = new ClassInstance(
                1,
                "Workshop",
                LocalDate.of(2026, 7, 27),
                LocalDate.of(2026, 9, 14),
                LocalTime.of(9, 0),
                LocalTime.of(10, 0),
                day,
                campus + " Building",
                "1.08"
        );

        return new Schedule(id, topic, availability, topicClass, classInstance);
    }
    @DisplayName("3.5.1 - SearchService finds matching class records")
    @Tag("Core")
    @Tag("Junaid")
    @Test
    void searchServiceFindsMatchingClassRecords() {
        SearchService service = new SearchService();

        List<Schedule> dataset = List.of(
                createSchedule(1, "COMP1701", "Tonsley", "Monday"),
                createSchedule(2, "COMP1701", "Bedford Park", "Monday"),
                createSchedule(3, "COMP1701", "Flinders City Campus", "Monday")
        );

        SearchService.SearchCriteria criteria = new SearchService.QueryBuilder()
                .withCampus("Tonsley")
                .build();

        List<Schedule> results = service.searchClasses(dataset, criteria);

        assertFalse(results.isEmpty());
        assertEquals(1, results.size());
        assertEquals("Tonsley", results.get(0).getAvailability().getCampus());
    }
    @DisplayName("3.5.2 - SearchService groups related class rows")
    @Tag("Additional")
    @Tag("Junaid")
    @Test
    void searchServiceGroupsRelatedClassRows() {
        SearchService service = new SearchService();

        Schedule relatedRowOne = createSchedule(1, "COMP1701", "Tonsley", "Monday");
        Schedule relatedRowTwo = createSchedule(2, "COMP1701", "Tonsley", "Monday");
        Schedule unrelatedRow = createSchedule(3, "COMP1702", "Bedford Park", "Tuesday");

        List<Schedule> dataset = List.of(relatedRowOne, relatedRowTwo, unrelatedRow);

        List<SearchService.BrowseClassSummary> groups = service.browseGroups(dataset);

        assertEquals(2, groups.size());
        assertTrue(groups.stream().anyMatch(group -> group.getOccurrenceCount() == 2));
        assertTrue(groups.stream().anyMatch(group -> group.getTopicCode().equals("COMP1702")));
    }
}