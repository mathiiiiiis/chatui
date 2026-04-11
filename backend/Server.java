import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class Server {
    private final int port;
    public final ArrayList<ClientHandler> clients;
    private final ServerSocket serverSocket;

    public Server(int port, int backlog) throws IOException {
        this.port = port;
        this.clients = new ArrayList<>();
        serverSocket = new ServerSocket(port, backlog);
    }

    public void start() throws IOException {
        System.out.println("📢 | Server started: " + port);

        while (true) { 
            Socket client = serverSocket.accept();
            ClientHandler clientHandler = new ClientHandler(client, clients);
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
