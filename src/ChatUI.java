import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ChatUI {
	private static PrintWriter output;
	private static BufferedReader input;
    private static JTextArea chatArea;

    public void main(String[] args) {
        JFrame frame = new JFrame("Chat");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 400);

        // Área de texto onde as mensagens aparecem
        chatArea = new JTextArea();
        chatArea.setEditable(false); // Não permite edição direta
        JScrollPane scrollPane = new JScrollPane(chatArea);

        // Campo de texto onde o usuário digita as mensagens
        JTextField messageField = new JTextField();
        messageField.setPreferredSize(new Dimension(300, 30));

        // Botão de envio
        JButton sendButton = new JButton("Enviar");

        // Painel inferior para campo de mensagem e botão de envio
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add(messageField, BorderLayout.CENTER);
        panel.add(sendButton, BorderLayout.EAST);

        // Adiciona os componentes à janela principal
        frame.getContentPane().add(scrollPane, BorderLayout.CENTER);
        frame.getContentPane().add(panel, BorderLayout.SOUTH);

        // Evento de clique no botão de enviar
        sendButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String message = messageField.getText();
                if (!message.isEmpty()) {
                    enviarMensagem(message);
                    messageField.setText(""); // Limpa o campo de texto
                }
            }
        });

        // Mostra a janela
        frame.setVisible(true);

        // Conecta ao servidor
        conectarServidor();
        
        // Inicia uma thread para receber mensagens do servidor
        new Thread(() -> receberMensagens()).start();
    }

    // Método para conectar ao servidor
    private void conectarServidor() {
        try {
            Socket socket = new Socket("localhost", 12345); // Endereço do servidor
            output = new PrintWriter(socket.getOutputStream(), true);
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            chatArea.append("Conectado ao servidor.\n");
        } catch (IOException e) {
            chatArea.append("Erro ao conectar ao servidor.\n");
        }
    }

    // Método para enviar uma mensagem ao servidor
    private static void enviarMensagem(String message) {
    	String currentTime = new SimpleDateFormat("HH:mm:ss").format(new Date());
        output.println(message);
        chatArea.append(String.format("[%s]: Você: %s \n",currentTime, message));
    }

    // Método para receber mensagens do servidor
    private void receberMensagens() {
        String mensagem;
        try {
            while ((mensagem = input.readLine()) != null) {
                chatArea.append(mensagem + "\n");
            }
        } catch (IOException e) {
            chatArea.append("Erro ao receber mensagens do servidor.\n");
        }
    }
}
