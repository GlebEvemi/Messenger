import java.io.*;
import java.net.Socket;
import java.util.concurrent.CopyOnWriteArrayList;

public class ClientHandler implements Runnable {

    public static CopyOnWriteArrayList<ClientHandler> clientHandlers = new CopyOnWriteArrayList<>();

    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String clientUsername;

    public ClientHandler(Socket socket) {
        try {
            this.socket = socket;
            this.bufferedWriter = new BufferedWriter(
                    new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));

            this.clientUsername = bufferedReader.readLine();
            clientHandlers.add(this);

            broadcast("SERVER: " + clientUsername + " joined the chat");

        } catch (IOException e) {
            close();
        }
    }

    @Override
    public void run() {
        String message;

        while (socket.isConnected()) {
            try {
                message = bufferedReader.readLine();
                if (message == null) break;
                broadcast(message);
            } catch (IOException e) {
                break;
            }
        }
        close();
    }

    private void broadcast(String message) {
        for (ClientHandler client : clientHandlers) {
            if (client != this) {
                try {
                    client.bufferedWriter.write(message);
                    client.bufferedWriter.newLine();
                    client.bufferedWriter.flush();
                } catch (IOException e) {
                    client.close();
                }
            }
        }
    }

    private void close() {
        clientHandlers.remove(this);
        broadcast("SERVER: " + clientUsername + " left the chat");

        try {
            socket.close();
            bufferedReader.close();
            bufferedWriter.close();
        } catch (IOException ignored) {}
    }
}
