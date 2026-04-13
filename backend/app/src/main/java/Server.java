import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class Server {
    private final int port;
    public final CopyOnWriteArrayList<ClientHandler> clients;
    public final HashMap<String, Room> rooms;
    private final ServerSocket serverSocket;

    public Server(int port, int backlog) throws IOException {
        this.port = port;
        this.clients = new CopyOnWriteArrayList<>();
        this.rooms = new HashMap<>();
        serverSocket = new ServerSocket(port, backlog);
    }

    public void start() throws IOException {
        System.out.println("📢 | Server started: " + port);
        
        while (true) { 
            Socket client = serverSocket.accept();
            ClientHandler clientHandler = new ClientHandler(client, clients, rooms);
            System.out.println("✅ | Client connected");
            clients.add(clientHandler);
            new Thread(
                clientHandler
            ).start();
        }
    }

    public static void main(String[] args) throws IOException {
        Server server = new Server(5000, 50);
        server.start();
    }
}
