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
          directory.mkdirs(); // Create directory if it doesn't exist
        }
        File file = new File(directory, filename);
        if (file.exists()) {
          return "Filename already in use";
        }
        return "Ready for file transfer";
      default:
        return "Invalid command";
    }
  }

  public static String getListOfFiles() {
    File directory = new File(SERVER_FILES_DIRECTORY);
    StringBuilder fileList = new StringBuilder();
    if (directory.exists() && directory.isDirectory()) {
      File[] files = directory.listFiles();
      if (files != null) {
        fileList.append("There are ").append(files.length).append(" file(s):");
        for (File file : files) {
          if (file.isFile()) {
            fileList.append(", ").append(file.getName());
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
        if (file.isFile() && file.getName().equals(filename)) {
          throw new IOException("Filename already in use");
        }
      }
    }
    File file = new File(SERVER_FILES_DIRECTORY, filename);
    FileOutputStream fos = new FileOutputStream(file);
    fos.write(data);
    fos.close();
  }

  public void handleFileTransfer(Socket clientSocket, String filename)
    throws IOException {
    PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
    InputStream inputStream = clientSocket.getInputStream();
    try {
      out.println("Ready for file transfer");

      byte[] headerBytes = new byte[10];
      readFully(inputStream, headerBytes);
      long fileSize = Long.parseLong(new String(headerBytes).trim());
      System.out.println("File size: " + fileSize);

      File file = new File(SERVER_FILES_DIRECTORY, filename);
      try (FileOutputStream fos = new FileOutputStream(file)) {
        byte[] buffer = new byte[1024];
        int length;
        long totalRead = 0;

        while (
          totalRead < fileSize && (length = inputStream.read(buffer)) != -1
        ) {
          fos.write(buffer, 0, length);
          totalRead += length;
        }
      }
      out.println("File transfer complete for " + filename);
    } catch (EOFException e) {
      System.err.println("File transfer was interrupted: " + e.getMessage());
    } finally {
      inputStream.close(); // Close the input stream
    }
  }

  private void readFully(InputStream input, byte[] buffer) throws IOException {
    int offset = 0;
    int bytesRead = 0;
    while (
      offset < buffer.length &&
      (bytesRead = input.read(buffer, offset, buffer.length - offset)) != -1
    ) {
      offset += bytesRead;
    }
    if (offset < buffer.length) {
      throw new EOFException("File transmission was interrupted");
    }

    System.out.println(Integer.toString(bytesRead));
  }
}
