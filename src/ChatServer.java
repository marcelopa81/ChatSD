
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

                // Se o nome for nulo ou vazio, encerrar a conexão
                if (nome == null || nome.trim().isEmpty()) {
                    out.println("Nome inválido. Conexão encerrada.");
                    return;
                }

                // Notifica que o cliente entrou
                ChatServer.broadcast("[" + new Date() + "] " + nome + " entrou na sala.", this);

                String mensagem;
                while ((mensagem = input.readLine()) != null) {
                	// Verifica se a mensagem é um ping
                    if (mensagem.equals("PING")) {
                        out.println("PONG"); // Responde ao ping
                		System.out.println("PONG");
                	} else {
                		 ChatServer.broadcast("[" + new Date() + "]: " + nome + ": " + mensagem, this);
                	}
                   
                }

            } catch (IOException e) {
                System.out.println("Erro na comunicação: " + e.getMessage());
            } finally {
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                // Remove o cliente e garante que a mensagem de saída seja apropriada
                ChatServer.removerCliente(this);
                ChatServer.broadcast(nome + " saiu do chat!", this);
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
