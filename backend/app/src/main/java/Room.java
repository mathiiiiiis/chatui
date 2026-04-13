import java.util.ArrayList;

public class Room {
    private final String name;
    private final ArrayList<Message> messages;
    private final ArrayList<ClientHandler> clients;

    public Room(String name) {
        this.name = name;
        this.messages = new ArrayList<>();
        this.clients = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public ArrayList<Message> getMessages() {
        return messages;
    }

    public ArrayList<ClientHandler> getClients() {
        return clients;
    }
}