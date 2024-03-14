import java.io.File;

public class ServerProtocol {

    private static final String SERVER_FILES_DIRECTORY = "serverFiles";

    public String processInput(String[] inputArray) {
        switch(inputArray[0]){
            case "list":
                return getListOfFiles().toString();
            case "Goodbye, server!":
                return "Goodbye, client!";
            default:
        }
        return "base case";
    }


    // Function to get list of files in the server directory
    public static String getListOfFiles() {
        File directory = new File(SERVER_FILES_DIRECTORY);
        StringBuilder fileList = new StringBuilder();

        if (directory.exists() && directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile()) {
                        fileList.append(file.getName()).append("\n");
                    }
                }
            }
        }
        return fileList.toString();
    }


}

