public class Message {
    private final String type;
    private final String content;
    //private String user, room; for later use

    public Message(String type, String content) {
        this.type = type;
        this.content = content;
    }

    public String getType() {
        return type;
    }

    public String getContent() {
        return content;
    }
}