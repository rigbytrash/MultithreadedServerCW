import java.net.*;
import java.io.*;

public class Server {
    private ServerSocket serverSocket = null;
    private ServerProtocol server = null;

    public Server() {
        try {
            serverSocket = new ServerSocket(2323);
        } catch (IOException e) {
            System.err.println("Could not listen on port: 2323.");
            System.exit(1);
        }
        server = new ServerProtocol();
    }

    public void runServer() {
        Socket clientSocket = null;

        while (true) {
            try {
                clientSocket = serverSocket.accept();
            } catch (IOException e) {
                System.err.println("Accept failed.");
                System.exit(1);
            }

            System.out.println("Client connected: " + clientSocket.getRemoteSocketAddress());
            try {
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
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
                    
                    if (outputLine.equals("Bye."))
                        break;
                }

                out.close();
                in.close();
                clientSocket.close();
            } catch (IOException e) {
                System.out.println(e);
            }
        }
    }

    public static void main(String[] args) {
        Server ss = new Server();
        ss.runServer();
    }
}
