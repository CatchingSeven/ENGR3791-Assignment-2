import java.util.ArrayList;

public class Topic {
    private String topicCode;
    private String topicName;
    private ArrayList<TopicClass> topicClasses;

    public Topic(String topicCode, String topicName) {
        this.topicCode = topicCode;
        this.topicName = topicName;
        this.topicClasses = new ArrayList<>();
    }

    public String getTopicCode() { return topicCode; }
    public String getTopicName() { return topicName; }
    public ArrayList<TopicClass> getTopicClasses() { return topicClasses; }
}