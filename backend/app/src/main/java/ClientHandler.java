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
    public final HashMap<String, RoomModel> rooms;
    public BufferedReader reader;
    public BufferedWriter sender;
    public String username;
    public String currentRoom;

    public ClientHandler (Socket client, CopyOnWriteArrayList<ClientHandler> clients, HashMap<String, RoomModel> rooms) throws IOException {
        this.socket = client;
        this.clients = clients;
        this.rooms = rooms;
        reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
        sender = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
        gson = new Gson();
    }

    public void register() throws IOException {
        String line = reader.readLine();
        MessageModel msg = gson.fromJson(line, MessageModel.class);
        if (msg.getType().equals("register")) {
            username = msg.getContent();
            if (username != null) {
                System.out.println("New user: " + username);
                sender.write(gson.toJson(new MessageModel("register", "Welcome, " + username, null)));
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
            rooms.put(roomName, new RoomModel(roomName));
            System.out.println(username + " created " + roomName);
            responseText = "You created and joined ";
        }

        rooms.get(roomName).getClients().add(this);
        currentRoom = roomName;
        System.out.println(username + " joined " + roomName);
        sender.write(gson.toJson(new MessageModel("join", responseText + roomName, null)));
        sender.newLine();
        sender.flush();
    }

    public void leaveOrDeleteRoom() throws IOException {
        String responseText = null;
        String oldRoom = currentRoom;

        if (currentRoom != null) {
            rooms.get(currentRoom).getClients().remove(this);
            responseText = "You left ";
            if (rooms.get(currentRoom).getClients().isEmpty()) {
                rooms.remove(currentRoom);
                System.out.println("Deleted " + oldRoom + ", because empty");
            }
            currentRoom = null;
        }

        System.out.println(username + " left " + oldRoom);
        sender.write(gson.toJson(new MessageModel("leave", responseText + oldRoom, null)));
        sender.newLine();
        sender.flush();
    }

    public void streamReadMessages() throws IOException {
        String message;
        while ((message = reader.readLine()) != null) {
            MessageModel msg = gson.fromJson(message, MessageModel.class);
            MessageModel outgoing = new MessageModel(msg.getType(), msg.getContent(), username);
            switch (msg.getType()) {
                case "room.join" -> joinOrCreateRoom(msg.getContent());
                case "room.leave" -> leaveOrDeleteRoom();
                case "room.message" -> {
                        if (currentRoom == null) {
                            sender.write(gson.toJson(new ErrorModel("error.room", "No Room joined", "Please join a room before sending a message.", 2)));
                            sender.newLine();
                            sender.flush();
                            break;
                        } 
                        for (ClientHandler c : rooms.get(currentRoom).getClients()) {
                            if (c == this) {
                                continue;
                            }
                            c.sender.write(gson.toJson(outgoing));
                            System.out.println("New message: " + gson.toJson(outgoing));
                            c.sender.newLine();
                            c.sender.flush();
                        }
                }
                default -> { 
                    sender.write(gson.toJson(new ErrorModel("error.message", "Error while sending message", "Failed to send message to room: " + currentRoom + ", does it still exist?", 2)));
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
