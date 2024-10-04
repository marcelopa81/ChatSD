import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class ChatServer {
    private static List<ClienteHandler> clientes = new ArrayList<>();
    private static int porta = 12345; // Porta padrão do servidor

    public static void main(String[] args) throws IOException {
        ServerSocket servidorSocket = new ServerSocket(porta);
        System.out.println("Servidor iniciado na porta " + porta);

        while (true) {
            Socket socket = servidorSocket.accept();
            ClienteHandler clienteHandler = new ClienteHandler(socket);
            clientes.add(clienteHandler);
            clienteHandler.start();
        }
    }

    public static void broadcast(String mensagem, ClienteHandler remetente) {
        for (ClienteHandler cliente : clientes) {
            if (cliente != remetente) {
                cliente.enviarMensagem(mensagem);
            }
        }
    }

    public static void removerCliente(ClienteHandler cliente) {
        clientes.remove(cliente);
        System.out.println("Cliente " + cliente.getNome() + " saiu.");
    }

    static class ClienteHandler extends Thread {
    	private Socket clientSocket;
        private PrintWriter out;
        private BufferedReader input;
        private String nome;

        public ClienteHandler(Socket socket) {
            this.clientSocket = socket;
        }

        public void run() {
            try {
            	input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                out = new PrintWriter(clientSocket.getOutputStream(), true);

                out.println("Digite seu nome:");
                nome = input.readLine();

                ChatServer.broadcast("[" + new Date() + "] " + nome + " entrou na sala.", this);

                String mensagem;
                while ((mensagem = input.readLine()) != null) {
                   ChatServer.broadcast("[" + new Date() + "]: " + nome + ": " + mensagem, this);
                }

            } catch (IOException e) {
                System.out.println("Erro na comunicação: " + e.getMessage());
            } finally {
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                removerCliente(this);
                ChatServer.broadcast("[" + new Date() + "]: " + nome + " saiu da sala.", this);
            }
        }

        public String getNome() {
            return nome;
        }

        public void enviarMensagem(String mensagem) {
            out.println(mensagem);
        }
    }
}
