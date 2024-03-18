import java.io.*;
import java.net.*;

public class Client {
    private Socket clientSocket = null;
    private PrintWriter socketOutput = null;
    private BufferedReader socketInput = null;

    public void playClient(String command, String filePath) {
        try {
            int port = 9500;
            try {
                clientSocket = new Socket("localhost", port);
            } catch (IOException e) {
                System.err.println("Couldn't connect to port " + port);
                System.exit(1);
            }
            socketOutput = new PrintWriter(clientSocket.getOutputStream(), true);
            socketInput = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host.");
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to host.");
            System.exit(1);
        }
    
        try {
            if (command.startsWith("put") && filePath != null) {
                if (new File(filePath).exists()) {
                    // File exists, proceed with sending the file
                } else {
                    System.err.println("Local file does not exist");
                    System.exit(1);
                }
                String fileName = new File(filePath).getName(); // Extract the filename from the file path
                socketOutput.println("put " + fileName); // Update the command with the filename
                byte[] fileData = readFile(filePath);
                OutputStream outputStream = clientSocket.getOutputStream();

                // Send file size first
                // Send fixed-length header for file size
                String sizeHeader = String.format("%10d", fileData.length);
                outputStream.write(sizeHeader.getBytes());
                // Send file data
                outputStream.write(fileData, 0, fileData.length);
                outputStream.flush();
                // Do not close the outputStream here
            }
            else {
                socketOutput.println(command);
            }
            
            // // Read the response from the server
            // String fromServer;
            // if ((fromServer = socketInput.readLine()) != null) {
            //     System.out.println("Server: " + fromServer);
            // }

            String fromServer = socketInput.readLine();
            System.out.println("Server: " + fromServer);
    
            // Now close all streams and the socket
            if (command.startsWith("put") && filePath != null) {
                clientSocket.getOutputStream().close();
            }
            socketOutput.close();
            socketInput.close();
            clientSocket.close();
        } catch (IOException e) {
            System.err.println("I/O exception during execution: " + e.getMessage());
            System.exit(1);
        }
    }
    

    private byte[] readFile(String filePath) throws IOException {
        File file = new File(filePath);
        FileInputStream fis = new FileInputStream(file);
        byte[] data = new byte[(int) file.length()];
        fis.read(data);
        fis.close();
        return data;
    }

        public static void main(String[] args) {
            if (args.length < 1) {
                System.err.println("Usage: java Client <command> [file path]");
                System.exit(1);
            }
            if (args.length > 1 && !args[0].equals("put")){
                System.err.println("Usage: java Client <command> [file path]");
                System.exit(1);
            }

            Client client = new Client();
            if (args[0].equals("put") && args.length >= 2) {
                client.playClient(args[0] + " " + args[1], args[1]);
            } else {
                client.playClient(args[0], null);
            }
        }
    }
