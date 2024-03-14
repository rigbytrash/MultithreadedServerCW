import java.net.*;
import java.io.*;

public class Server 
{
	private ServerSocket serverSocket = null;
    private ServerProtocol server = null;

	public Server() {
        try {
            serverSocket = new ServerSocket(2323);
        }
        catch (IOException e) {
            System.err.println("Could not listen on port: 2323.");
            System.exit(1);
        }
        server = new ServerProtocol();
    }

	public void runServer() {

        Socket clientSocket = null;

        while( true ){

            try {
   	            clientSocket = serverSocket.accept();
            }
            catch (IOException e) {
                System.err.println("Accept failed.");
                System.exit(1);
            }

            System.out.println("clientSocket port: " + clientSocket.getPort() );
            try {
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(
    	    	                		new InputStreamReader(
    	                        			clientSocket.getInputStream()));
                String inputLine, outputLine;
                
                if ((inputLine = in.readLine()) != null) {
                    // split inputLine into an array of strings
                    String[] inputArray = inputLine.split(" ");

                     outputLine = server.processInput(inputArray);
                     out.println(outputLine);
                     if (outputLine.equals("Bye."))
                        break;
                }
                out.close();
                in.close();
                clientSocket.close();
            }
            catch (IOException e) {
                System.out.println( e );
            }
        }
    }

	public static void main( String[] args ) {
		Server ss = new Server();
		ss.runServer();
	  }
}