import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

  private ExecutorService executorService;
  private ServerProtocol server;

  public Server() {
    executorService = Executors.newFixedThreadPool(20); // 20 threads for handling clients concurrently
    server = new ServerProtocol();
  }

  private void handleClient(Socket clientSocket) throws IOException {
    try (
      PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true); // output stream to client for sending data
      BufferedReader in = new BufferedReader(
        new InputStreamReader(clientSocket.getInputStream()) // input stream from client for reading data
      )
    ) {
      String inputLine;
      if ((inputLine = in.readLine()) != null) {
        String[] inputArray = inputLine.split(" ");
        String outputLine = server.processInput(inputArray); // process input from client

        if (outputLine.equals("Ready for file transfer")) {
          server.handleFileTransfer(clientSocket, inputArray[1]);
          out.println("File transfer complete for " + inputArray[1]);
        } else { // send server response to client using output stream if not ready for file transfer (e.g. "put")
          out.println(outputLine);
        }
        if ( // log successful requests to log file
          !outputLine.equals("Invalid command") ||
          !outputLine.equals("No Filename Provided")
        ) {
          sucessToLog(
            inputArray[0],
            clientSocket.getInetAddress().getHostAddress()
          );
        }
      }
    } finally {
      clientSocket.close();
    }
  }

  private void sucessToLog(String request, String clientIPAddress) {
    Date currentDate = new Date();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
    String currentDateStr = dateFormat.format(currentDate);
    String currentTimeStr = timeFormat.format(currentDate);
    String logEntry =
      currentDateStr +
      "|" +
      currentTimeStr +
      "|" +
      clientIPAddress +
      "|" +
      request;

    try (PrintWriter out = new PrintWriter(new FileWriter("log.txt", true))) { // append to log file if it exists or create new file
      out.println(logEntry);
    } catch (IOException e) {
      System.err.println("Error writing to log file: " + e.getMessage());
    }
  }

  private void startServerOnPort(int port) {
    try {
      ServerSocket serverSocket = new ServerSocket(port); // create server socket on port requested
      while (true) {
        Socket clientSocket = serverSocket.accept();

        executorService.submit(() -> {
          try {
            handleClient(clientSocket);
          } catch (IOException e) {
            System.err.println(
              "Error handling client on port " + port + ": " + e.getMessage()
            );
          }
        });
      }
    } catch (IOException e) {
      System.err.println("Could not listen on port: " + port);
    }
  }

  public void runServer() {
    startServerOnPort(9500);
  }

  public static void main(String[] args) {
    Server ss = new Server();
    ss.runServer();
  }
}
