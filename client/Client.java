import java.io.*;
import java.net.*;

public class Client {

  private Socket clientSocket = null;
  private PrintWriter socketOutput = null;
  private BufferedReader socketInput = null;

  public void playClient(String command, String filePath) {
    try {
      clientSocket = new Socket("localhost", 9500); // fixd port number within range 9100 and 9999
      socketOutput = new PrintWriter(clientSocket.getOutputStream(), true);
      socketInput =
        new BufferedReader(
          new InputStreamReader(clientSocket.getInputStream()) // input stream from server
        );

      if (command.startsWith("put") && filePath != null) {
        File file = new File(filePath);
        if (file.exists()) {
          String fileName = file.getName();
          socketOutput.println("put " + fileName);

          String serverResponse = socketInput.readLine(); // read server response from input stream
          if (
            "Error: Filename already in use on server".equals(serverResponse)
          ) {
            System.err.println(serverResponse);
            System.exit(1);
          } else if (!"Ready for file transfer".equals(serverResponse)) {
            throw new IOException(
              "Unexpected server response: " + serverResponse
            );
          }

          sendFile(file, clientSocket.getOutputStream()); // send file to server using output stream
        } else {
          System.err.println("Error: Local file does not exist");
          System.exit(1);
        }
      } else {
        socketOutput.println(command);
      }

      String fromServer;
      while ((fromServer = socketInput.readLine()) != null) { // read server response from input stream until no more data
        System.out.println(fromServer);
      }
    } catch (IOException e) {
      System.err.println(
        "Error: I/O exception during execution: " + e.getMessage()
      );
    } finally {
      closeResources();
    }
  }

  private void sendFile(File file, OutputStream outputStream)
    throws IOException {
    String sizeHeader = String.format("%10d", file.length()); // format file size to 10 bytes string
    outputStream.write(sizeHeader.getBytes()); // write file size to output stream

    try (FileInputStream fis = new FileInputStream(file)) { // read file from file input stream
      byte[] buffer = new byte[1024];
      int count;
      while ((count = fis.read(buffer)) > 0) {
        outputStream.write(buffer, 0, count); // write file to output stream in chunks of 1024 bytes
      }
      outputStream.flush(); // flush output stream to ensure all data is sent to server
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

  public static void main(String[] args) {
    if (args.length < 1 || args.length > 2) {
      System.err.println(
        "Usage: \"java Client list\", or: \"java Client put [file path]\""
      );
      System.exit(1);
    }
    if (args.length > 1 && !args[0].equals("put")) {
      System.err.println(
        "Usage: \"java Client list\", or: \"java Client put [file path]\""
      );
      System.exit(1);
    }
    if (args.length == 1 && args[0].equals("put")) {
      System.err.println("Usage: \"java Client put [file path]\"");
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
