public class MessageModel {
    private final String type;
    private final String content;
    private final String user;
    //private final String room;

    public MessageModel(String type, String content, String user) {
        this.type = type;
        this.content = content;
        this.user = user;
    }

    public String getType() {
        return type;
    }

    public String getContent() {
        return content;
    }

    public String getUser() {
        return user;
    }
}