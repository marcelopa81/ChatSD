public class Application {
    public static void main(String[] args) {
        // Instancia a interface gráfica do chat
        ChatClient chatInterface = new ChatClient();
        chatInterface.main(args); // Chama o método principal da classe ChatUI
    }
}
