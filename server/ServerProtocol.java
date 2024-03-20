import java.io.*;
import java.net.Socket;

public class ServerProtocol {

  private static final String SERVER_FILES_DIRECTORY = "serverFiles";

  public String processInput(String[] inputArray) {
    switch (inputArray[0]) {
      case "list":
        return getListOfFiles();
      case "put":
        if (inputArray.length < 2) {
          return "No file name provided"; // client should handle this, but checked here too
        }
        String filename = inputArray[1];
        System.out.println("Filename: " + filename);
        File directory = new File(SERVER_FILES_DIRECTORY);
        if (!directory.exists()) {
          directory.mkdirs(); // create directory if it doesn't exist
        }
        File file = new File(directory, filename);
        if (file.exists()) {
          return "Error: Filename already in use on server";
        }
        return "Ready for file transfer";
      default:
        return "Error: Invalid command";
    }
  }

  public static String getListOfFiles() {
    File directory = new File(SERVER_FILES_DIRECTORY);
    StringBuilder fileList = new StringBuilder();
    if (directory.exists() && directory.isDirectory()) {
      File[] files = directory.listFiles(); // list files in directory if it exists
      if (files != null) {
        fileList.append("Listing ").append(files.length).append(" file(s):\n"); // append number of files
        for (int i = 0; i < files.length; i++) {
          File file = files[i];
          if (file.isFile()) {
            fileList.append(file.getName()); // append file name
            if (i < files.length - 1) {
              fileList.append("\n"); // append newline if not last file
            }
          }
        }
      }
    }
    return fileList.toString();
  }

  public void writeFile(String filename, byte[] data) throws IOException {
    File directory = new File(SERVER_FILES_DIRECTORY);
    File[] files = directory.listFiles();
    if (files != null) {
      for (File file : files) {
        if (file.isFile() && file.getName().equals(filename)) { // check if filename already in use
          throw new IOException("Filename already in use"); // throw exception if filename already in use
        }
      }
    }
    File file = new File(SERVER_FILES_DIRECTORY, filename);
    FileOutputStream fos = new FileOutputStream(file); // write file to server files directory using file output stream
    fos.write(data);
    fos.close(); // close file output stream after writing file
  }

  public void handleFileTransfer(Socket clientSocket, String filename)
    throws IOException {
    PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true); // output stream to client for sending data
    InputStream inputStream = clientSocket.getInputStream(); // input stream from client for reading data
    try {
      out.println("Ready for file transfer");

      byte[] headerBytes = new byte[10]; // read file size from input stream
      readFully(inputStream, headerBytes);
      long fileSize = Long.parseLong(new String(headerBytes).trim()); // parse file size from header bytes

      File file = new File(SERVER_FILES_DIRECTORY, filename);
      try (FileOutputStream fos = new FileOutputStream(file)) { // create file output stream for writing file
        byte[] buffer = new byte[1024];
        int length;
        long totalRead = 0;

        while (
          totalRead < fileSize && (length = inputStream.read(buffer)) != -1 // read file from input stream
        ) {
          fos.write(buffer, 0, length); // write file to server files directory using file output stream
          totalRead += length;
        }
      }
      out.println("File transfer complete for " + filename);
    } catch (EOFException e) {
      System.err.println("File transfer was interrupted: " + e.getMessage());
    } finally {
      inputStream.close(); // close the input stream
    }
  }

  private void readFully(InputStream input, byte[] buffer) throws IOException {
    int offset = 0;
    int bytesRead = 0;
    while (
      offset < buffer.length &&
      (bytesRead = input.read(buffer, offset, buffer.length - offset)) != -1 // read file from input stream
    ) {
      offset += bytesRead; // increment offset by number of bytes read
    }
    if (offset < buffer.length) {
      throw new EOFException("File transmission was interrupted");
    }
  }
}
