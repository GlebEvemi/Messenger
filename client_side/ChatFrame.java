import javax.swing.*;
import java.awt.*;

public class ChatFrame extends JFrame {

    private JTextArea chatArea;
    private JTextField inputField;
    private JButton sendButton;
    private Client client;

    public ChatFrame(Client client) {
        this.client = client;

        setTitle("Chat Client");
        setSize(700, 1000);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setLineWrap(true);

        JScrollPane scrollPane = new JScrollPane(chatArea);

        inputField = new JTextField();
        sendButton = new JButton("Send");

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(inputField, BorderLayout.CENTER);
        bottomPanel.add(sendButton, BorderLayout.EAST);

        add(scrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        
        sendButton.addActionListener(e -> send());

        
        inputField.addActionListener(e -> send());

        setVisible(true);

        client.setMessageListener(this::appendMessage);
        client.listenForMessage();
    }

    private void send() {
        String text = inputField.getText().trim();
        if (!text.isEmpty()) {
            client.sendMessage(text);
            inputField.setText("");
        }
    }

    private void appendMessage(String message) {
        SwingUtilities.invokeLater(() -> {
            chatArea.append(message + "\n");
        });
    }
}
