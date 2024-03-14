import java.io.*;
import java.net.*;

public class Client {

    private Socket clientSocket = null;
    private PrintWriter socketOutput = null;
    private BufferedReader socketInput = null;

    public void playClient(String command) {

        try {
            clientSocket = new Socket("localhost", 2323);
            socketOutput = new PrintWriter(clientSocket.getOutputStream(), true);
            socketInput = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host.\n");
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to host.\n");
            System.exit(1);
        }

        String fromServer;

        try {
            // Send the command to the server
            if (command != null) {
                socketOutput.println(command);
            }

            // Read response from server
            while ((fromServer = socketInput.readLine()) != null) {
                System.out.println(fromServer);
            }

            // Close resources
            socketOutput.close();
            socketInput.close();
            clientSocket.close();
        } catch (IOException e) {
            System.err.println("I/O exception during execution\n");
            System.exit(1);
        }
    }

    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Usage: java Client <command>");
            System.exit(1);
        }

        Client client = new Client();
        client.playClient(args[0]);
    }
}
