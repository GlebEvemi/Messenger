import java.io.*;
import java.net.Socket;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

public class Client {

    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String username;
    private int port;
    private MessageListener messageListener;

    public Client(int port, String username){
        this.port = port;
        this.username = username;

        try{
            connectToServer();
        }catch (IOException e){
    JOptionPane.showMessageDialog(null,"Failed to connect to port");

    new Timer(1, ex -> System.exit(1)).start();
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


    public void setMessageListener(MessageListener listener) {
    this.messageListener = listener;
}   



    public void sendMessage(String message) {
    try {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        bufferedWriter.write(
            sdf.format(timestamp) + "\t" + username + ": " + message
        );
        bufferedWriter.newLine();
        bufferedWriter.flush();
    } catch (IOException e) {
        closeEverything(socket, bufferedReader, bufferedWriter);
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
                            if (messageListener != null) {
                                messageListener.onMessage(msgFromGroupChat);
                            }                           
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

    
    

    public static void main(String[] args) {
    String username = JOptionPane.showInputDialog("Enter username:");
    String portStr = JOptionPane.showInputDialog("Enter port:");

    int port = Integer.parseInt(portStr);

    Client client = new Client(port, username);

    SwingUtilities.invokeLater(() -> {
        new ChatFrame(client);
    });
}

}
