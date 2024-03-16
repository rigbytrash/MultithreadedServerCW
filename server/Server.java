import java.net.*;
import java.io.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private ExecutorService executorService;
    private ServerProtocol server;

    public Server() {
        executorService = Executors.newCachedThreadPool();
        server = new ServerProtocol();
    }

    private void handleClient(Socket clientSocket) throws IOException {
        try (PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {

            String inputLine;
            if ((inputLine = in.readLine()) != null) {
                String[] inputArray = inputLine.split(" ");
                String outputLine = server.processInput(inputArray);

                if (outputLine.equals("Ready for file transfer")) {
                    server.handleFileTransfer(clientSocket, inputArray[1]);
                } else {
                    System.out.println(outputLine);
                    out.println(outputLine);
                }

                if (outputLine.equals("Bye.")) {
                    return;
                }
            }
        } finally {
            clientSocket.close();
        }
    }

    private void startServerOnPort(int port) {
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            //System.out.println("Listening on port: " + port);
            
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected on port " + port + ": " + clientSocket.getRemoteSocketAddress());

                executorService.submit(() -> {
                    try {
                        handleClient(clientSocket);
                    } catch (IOException e) {
                        System.err.println("Error handling client on port " + port + ": " + e.getMessage());
                    }
                });
            }

        } catch (IOException e) {
            System.err.println("Could not listen on port: " + port);
        }
    }

    public void runServer() {
        for (int port = 9100; port <= 9999; port++) {
            int finalPort = port;
            executorService.submit(() -> startServerOnPort(finalPort));
        }
    }

    public static void main(String[] args) {
        Server ss = new Server();
        ss.runServer();
    }
}
