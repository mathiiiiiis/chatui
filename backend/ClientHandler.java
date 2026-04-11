import java.io.*;
import java.net.Socket;

public class ClientHandler implements Runnable {
    public Socket socket;
    public BufferedReader reader;

    public ClientHandler (Socket client) throws IOException {
        this.socket = client;
        reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
    }

    @Override
    public void run() {
        String message;
        try {
            while ((message = reader.readLine()) != null) {
                System.out.println(message);
            }
        } catch (IOException e) {
            System.out.println("Error: " + e);
        }
    }
}
