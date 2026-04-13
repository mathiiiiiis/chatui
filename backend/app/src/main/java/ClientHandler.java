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
    public Gson gson;
    public CopyOnWriteArrayList<ClientHandler> clients;
    public BufferedReader reader;
    public BufferedWriter sender;
    public String username;

    public ClientHandler (Socket client,CopyOnWriteArrayList<ClientHandler> clients) throws IOException {
        this.socket = client;
        this.clients = clients;
        reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
        sender = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
        gson = new Gson();
    }

    public void register() throws IOException {
        String line = reader.readLine();
        Message msg = gson.fromJson(line, Message.class);
        if (msg.getType().equals("register")) {
            username = msg.getContent();
            if (username != null) {
                System.out.println("New user: " + username);
                sender.write(gson.toJson(new Message("register" ,"Welcome, " + username + " you may now send messages", null)));
                sender.newLine();
                sender.flush();
            } else {
                throw new IOException("Error: " + username + " is not a valid username");
            }
        } else {
            throw new IOException("Expected register, got: " + msg.getType());
        }
    }

    public void streamReadMessages() throws IOException {
        String message;
        while ((message = reader.readLine()) != null) {
            Message msg = gson.fromJson(message, Message.class);
            Message outgoing = new Message(msg.getType(), msg.getContent(), username);
            for (ClientHandler c : clients) {
                if (c == this) {
                    continue;
                }
                c.sender.write(gson.toJson(outgoing));
                System.out.println("New message: " + gson.toJson(outgoing));
                c.sender.newLine();
                c.sender.flush();
            }
        }
    }

    @Override
    public void run() {
        try {
            register();
            streamReadMessages();
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
