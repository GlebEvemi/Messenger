import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.Buffer;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Scanner;

public class Client {

    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String username;
    private int port;

    public Client(int port, String username){
        this.port = port;
        this.username = username;

        try{
            connectToServer();
        }catch (IOException e){
            System.out.println("Failed to connect to port: " + port +". Maybe port is available. Creating a new chat.");
            try{
                startServerOnPort(port);
                connectToServer();
            }catch (IOException y){
                System.out.println("Failed to create a server");
                y.printStackTrace();
            }

        }
    }

    private void connectToServer() throws IOException {
        this.socket = new Socket("localhost", port);
        this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        bufferedWriter.write(username);
        bufferedWriter.newLine();
        bufferedWriter.flush();
    }

    private void startServerOnPort(int port){
        try{
            ServerSocket serverSocket = new ServerSocket(port);
            Server server = new Server(serverSocket);
            System.out.println("New chat created on port " + port);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    server.startServer();
                }
            }).start();
        }catch (IOException e){
            System.out.println("Failed to create server on port " + port);
            e.printStackTrace();
        }
    }


    public void sendMessage(){
        try{

            bufferedWriter.newLine();
            bufferedWriter.flush();

            Scanner scanner = new Scanner(System.in);
            SimpleDateFormat sdf3 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            while (socket.isConnected()){
                String messageToSend = scanner.nextLine();
                bufferedWriter.write(sdf3.format(timestamp)+ "\t" +username + ": " + messageToSend);
                bufferedWriter.newLine();
                bufferedWriter.flush();
            }
        }catch(IOException e){
            closeEverything(socket, bufferedReader, bufferedWriter);
            System.out.println("Connection lost");
        }
    }

    public void listenForMessage(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String msgFromGroupChat;

                while(socket.isConnected()){
                    try{
                        msgFromGroupChat = bufferedReader.readLine();
                        System.out.println(msgFromGroupChat);
                    }catch (IOException e){
                        closeEverything(socket, bufferedReader, bufferedWriter);
                    }

                }
            }
        }).start();
    }

    public void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter){
        try{
            if (bufferedReader != null){
                bufferedReader.close();
            }
            if (bufferedWriter != null){
                bufferedWriter.close();
            }
            if (socket != null){
                socket.close();
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException{

        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter your username for the group chat: ");
        String username = scanner.nextLine();
        System.out.println("Enter the port");
        int port = scanner.nextInt();
        Client client = new Client(port, username);
        client.listenForMessage();
        client.sendMessage();
    }
}
