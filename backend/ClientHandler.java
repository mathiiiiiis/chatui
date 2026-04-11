import java.io.*;
import java.net.Socket;
import java.util.concurrent.CopyOnWriteArrayList;

public class ClientHandler implements Runnable {
    public Socket socket;
    public CopyOnWriteArrayList<ClientHandler> clients;
    public BufferedReader reader;
    public BufferedWriter sender;

    public ClientHandler (Socket client, CopyOnWriteArrayList<ClientHandler> clients) throws IOException {
        this.socket = client;
        this.clients = clients;
        reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
        sender = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
    }

    @Override
    public void run() {
        String message;
        try {
            while ((message = reader.readLine()) != null) {
                for (ClientHandler c : clients) {
                    if (c == this) {
                        continue;
                    }
                    c.sender.write(message);
                    c.sender.newLine();
                    c.sender.flush();
                }
            }
        } catch (IOException e) {
            System.out.println("Error: " + e);
        } finally {
            clients.remove(this);
            try {
                socket.close();
            } catch (IOException e) {
                System.out.println("Error: " + e);
            }
            System.out.println("⛔️ | Client disconnected");
        }
    }
}
