import java.util.ArrayList;

public class RoomModel {
    private final String name;
    private final ArrayList<MessageModel> messages;
    private final ArrayList<ClientHandler> clients;

    public RoomModel(String name) {
        this.name = name;
        this.messages = new ArrayList<>();
        this.clients = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public ArrayList<MessageModel> getMessages() {
        return messages;
    }

    public ArrayList<ClientHandler> getClients() {
        return clients;
    }
}