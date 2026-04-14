public class ErrorModel {
    private final String type;
    private final String title;
    private final String description;
    private final int priority; // 5 - highest, 1 - lowest. for UI displaying

    public ErrorModel(String type, String title, String description, int priority) {
        this.type = type;
        this.title = title;
        this.description = description;
        this.priority = priority;
    }

    public String getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public int getPriority() {
        return priority;
    }
}