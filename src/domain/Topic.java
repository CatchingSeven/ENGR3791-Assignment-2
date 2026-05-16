package domain;

import java.util.Objects;

/** DMCD entity: topic. */
public class Topic {
    private String topicCode;
    private String topicName;

    public Topic(String topicCode, String topicName) {
        this.topicCode = clean(topicCode);
        this.topicName = clean(topicName);
    }

    public String getTopicCode() { return topicCode; }
    public void setTopicCode(String topicCode) { this.topicCode = clean(topicCode); }

    public String getTopicName() { return topicName; }
    public void setTopicName(String topicName) { this.topicName = clean(topicName); }

    public String displayName() {
        if (topicName == null || topicName.isBlank()) return topicCode;
        if (topicCode == null || topicCode.isBlank()) return topicName;
        return topicCode + " " + topicName;
    }

    public Topic copy() { return new Topic(topicCode, topicName); }

    private static String clean(String value) { return value == null ? "" : value.trim(); }

    @Override
    public String toString() { return displayName(); }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Topic)) return false;
        Topic topic = (Topic) o;
        return topicCode.equalsIgnoreCase(topic.topicCode) && topicName.equalsIgnoreCase(topic.topicName);
    }

    @Override
    public int hashCode() { return Objects.hash(topicCode.toLowerCase(), topicName.toLowerCase()); }
}
