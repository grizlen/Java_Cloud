import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    public static void main(String[] args) {
        new Server();
    }

    public Server() {
        try (ServerSocket serverSocket = new ServerSocket(8189)){
            System.out.println("Server started...");
            while (true){
                Socket socket = serverSocket.accept();
                System.out.println("Client connected...");
                ClientHandler clientHandler = new ClientHandler(socket);
                new Thread(clientHandler).start();
            }
        } catch (Exception e) {
            System.err.println("Server was broken.");
        }
    }
}
