import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.concurrent.CopyOnWriteArrayList;

import com.google.gson.Gson;

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
                Gson gson = new Gson();
                Message msg = gson.fromJson(message, Message.class);
                for (ClientHandler c : clients) {
                    if (c == this) {
                        continue;
                    }
                    c.sender.write(gson.toJson(msg));
                    System.out.println("New message: " + msg.getContent());
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
