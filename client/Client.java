import java.io.*;
import java.net.*;

public class Client {
    private Socket clientSocket = null;
    private PrintWriter socketOutput = null;
    private BufferedReader socketInput = null;

    public void playClient(String command, String filePath) {
        try {
            clientSocket = new Socket("localhost", 9500);
            socketOutput = new PrintWriter(clientSocket.getOutputStream(), true);
            socketInput = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    
            if (command.startsWith("put") && filePath != null) {
                File file = new File(filePath);
                if (file.exists()) {
                    String fileName = file.getName();
                    socketOutput.println("put " + fileName);
    
                    String serverResponse = socketInput.readLine();
                    if ("Filename already in use".equals(serverResponse)) {
                        System.out.println("Server: " + serverResponse);
                        return; // Exit the method
                    } else if (!"Ready for file transfer".equals(serverResponse)) {
                        throw new IOException("Unexpected server response: " + serverResponse);
                    }
    
                    sendFile(file, clientSocket.getOutputStream());
                } else {
                    System.err.println("Local file does not exist");
                    System.exit(1);
                }
            } else {
                socketOutput.println(command);
            }
    
            String fromServer;
            while ((fromServer = socketInput.readLine()) != null) {
                System.out.println("Server: " + fromServer);
            }
        } catch (IOException e) {
            System.err.println("I/O exception during execution: " + e.getMessage());
        } finally {
            closeResources();
        }
    }
    
    private void sendFile(File file, OutputStream outputStream) throws IOException {
        String sizeHeader = String.format("%10d", file.length());
        outputStream.write(sizeHeader.getBytes());
    
        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] buffer = new byte[1024];
            int count;
            while ((count = fis.read(buffer)) > 0) {
                outputStream.write(buffer, 0, count);
            }
            outputStream.flush();
        }
    }
    
    
    private void closeResources() {
        try {
            if (socketOutput != null) socketOutput.close();
            if (socketInput != null) socketInput.close();
            if (clientSocket != null) clientSocket.close();
        } catch (IOException e) {
            System.err.println("Error closing resources: " + e.getMessage());
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
            if (args.length > 2){
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
