import database.Network;
import lombok.Data;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

@Data
public class Server {
    // Define the port on which the server is listening
    public static final int PORT = 8100;
    public int connCount = 0;
    private ServerSocket serverSocket = null;
    private static boolean running = true;

    public void closeServer() throws IOException {
        running = false;
        System.out.println("Server is no longer creating new threads.");
       /* IOException IOException = null;
        if (this.getConnCount()==0) throw IOException;*/
    }

    public static boolean isRunning() {
        return running;
    }

    public static void setRunning(boolean running) {
        Server.running = running;
    }

    public Server() throws IOException {

        try {
            this.serverSocket = new ServerSocket(PORT);
            while (running) {
                System.out.println("Waiting for a client ...");

                Socket socket = this.serverSocket.accept();
                connCount++;
                // Execute the client's request in a new thread
                if (running) new ClientThread(socket, this).start();
                //System.out.println(connCount);
            }
            if (connCount == 0) {
                System.out.println("Server stopped");
                return;
            }

        } catch (IOException e) {
            //System.out.println(connCount);
            System.out.println("Server stopped");
        } finally {
            assert serverSocket != null;
            serverSocket.close();

        }
        Network network = new Network();
        network.save();
    }

    public static void main(String[] args) throws IOException {
        Network network = new Network();
        network.load();
        Server server = new Server();
    }

}