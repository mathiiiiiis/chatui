import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import com.google.gson.Gson;

public class ClientHandler implements Runnable {
    public Socket socket;
    public Gson gson;
    public CopyOnWriteArrayList<ClientHandler> clients;
    public final HashMap<String, Room> rooms;
    public BufferedReader reader;
    public BufferedWriter sender;
    public String username;
    public String currentRoom;

    public ClientHandler (Socket client, CopyOnWriteArrayList<ClientHandler> clients, HashMap<String, Room> rooms) throws IOException {
        this.socket = client;
        this.clients = clients;
        this.rooms = rooms;
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
                sender.write(gson.toJson(new Message("register", "Welcome, " + username, null)));
                sender.newLine();
                sender.flush();
            } else {
                throw new IOException("Error: " + username + " is not a valid username");
            }
        } else {
            throw new IOException("Expected register, got: " + msg.getType());
        }
    }

    public void joinOrCreateRoom(String roomName) throws IOException {
        String responseText;

        if (rooms.containsKey(roomName)) {
            responseText = "You joined ";
        } else {
            rooms.put(roomName, new Room(roomName));
            System.out.println(username + " created " + roomName);
            responseText = "You created and joined ";
        }

        rooms.get(roomName).getClients().add(this);
        currentRoom = roomName;
        System.out.println(username + " joined " + roomName);
        sender.write(gson.toJson(new Message("join", responseText + roomName, null)));
        sender.newLine();
        sender.flush();
    }

    public void streamReadMessages() throws IOException {
        String message;
        while ((message = reader.readLine()) != null) {
            Message msg = gson.fromJson(message, Message.class);
            Message outgoing = new Message(msg.getType(), msg.getContent(), username);
            if (msg.getType().equals("room.join")) {
                joinOrCreateRoom(msg.getContent());
            } else {                
                if (currentRoom == null) {
                    sender.write("Failed to send message: Please join a room before sending a message." );
                    sender.newLine();
                    sender.flush();
                    continue;
                }
                if (msg.getType().equals("room.message")) {
                    for (ClientHandler c : rooms.get(currentRoom).getClients()) {
                        if (c == this) {
                            continue;
                        }
                        c.sender.write(gson.toJson(outgoing));
                        System.out.println("New message: " + gson.toJson(outgoing));
                        c.sender.newLine();
                        c.sender.flush();
                    }
                } else {
                    sender.write("Failed to send message to room: " + currentRoom + ", does it still exist?");
                    System.out.println(username + "'s message failed to send to room: " + currentRoom);
                    sender.newLine();
                    sender.flush();
                }
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
