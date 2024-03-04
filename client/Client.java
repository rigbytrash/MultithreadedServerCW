import java.io.*;
import java.net.*;

public class Client {

	private Socket clientSocket = null;
    private PrintWriter socketOutput = null;
    private BufferedReader socketInput = null;

	public void playClient() {

        try {

            // Try and create the socket. The server is assumed to be running on the same host ('localhost'),
            // so first run 'KnockKnockServer' in another shell.
            clientSocket = new Socket( "localhost", 2323 );

            // Chain a writing stream
            socketOutput = new PrintWriter(clientSocket.getOutputStream(), true);

            // Chain a reading stream
            socketInput = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

        }
        catch (UnknownHostException e) {
            System.err.println("Don't know about host.\n");
            System.exit(1);
        }
        catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to host.\n");
            System.exit(1);
        }

        // Chain a reader from the keyboard.
        BufferedReader stdIn = new BufferedReader(
                                   new InputStreamReader(System.in) );
        String fromServer;
        String fromUser;

        // Read from server.
        try
        {
          while( (fromServer=socketInput.readLine())!=null )
          {
              // Echo server string.
              System.out.println( "Server: " + fromServer );

              // Client types in response
              fromUser = stdIn.readLine();
    	      if( fromUser!=null )
    	      {
                  // Echo client string.
                  System.out.println( "Client: " + fromUser );

                  // Write to server.
                  socketOutput.println(fromUser);
              }
          }
          socketOutput.close();
          socketInput.close();
          stdIn.close();
          clientSocket.close();
        }
        catch (IOException e) {
            System.err.println("I/O exception during execution\n");
            System.exit(1);
        }
    }

	public static void main(String[] args) {
		Client client = new Client();
		client.playClient();
	  }

}