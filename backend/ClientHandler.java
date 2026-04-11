import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandler implements Runnable {
    public Socket socket;
    public ArrayList<ClientHandler> clients;
    public BufferedReader reader;
    public BufferedWriter sender;

    public ClientHandler (Socket client, ArrayList<ClientHandler> clients) throws IOException {
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
        }
    }
}
