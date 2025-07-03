# Simple File Transfer System (COMP2221 Networks)

This repository contains a basic client-server application built in Java, designed for a Networks module coursework (COMP2221). It allows clients to request a list of files available on the server and to upload new text files to the server. The server is multi-threaded and maintains a log of client requests.

## Project Overview

This project simulates a basic FTP-like application, focusing on core networking concepts:
* **Client-Server Communication:** Implemented using TCP sockets.
* **Multi-threading:** The server uses an `Executor` with a fixed thread pool to handle multiple client connections concurrently.
* **File Operations:** Listing files on the server and transferring files from the client to the server.
* **Error Handling:** Manages scenarios like duplicate filenames during upload and invalid client commands.
* **Logging:** The server logs all valid client requests to a `log.txt` file.

## Features

### Server (`Server.java`, `ServerProtocol.java`)
* **Continuous Operation:** Runs indefinitely, listening for client connections.
* **Thread Pool:** Manages client connections with a fixed thread pool of 20 connections for concurrency.
* **File Listing:** Responds to `list` requests by returning a list of files present in the `serverFiles` directory.
* **File Upload (`put`):**
    * Receives file upload requests from clients.
    * Checks for filename conflicts: If a file with the same name already exists in `serverFiles`, it returns an error to the client.
    * If no conflict, it transfers the file from the client and saves it to the `serverFiles` directory.
* **Request Logging:** Creates and appends to `log.txt` in the server's directory, logging every valid client request in the format: `date|time|client IP address|request`.
* **Dedicated Port:** Listens on port `9500`.

### Client (`Client.java`)
* **Command-line Interface:** Accepts commands as command-line arguments.
* **`list` command:** Connects to the server and requests a list of available files.
    * Usage: `java Client list`
* **`put` command:** Uploads a specified local file to the server.
    * Usage: `java Client put [file path]`
    * Includes checks for local file existence and handles server-side errors (e.g., duplicate filename).
* **Single-shot Execution:** Exits automatically after completing its command.
* **Error Messaging:** Provides meaningful error messages for invalid commands or operational failures.

## Project Structure

The project maintains the following directory structure:
```
cwk/
├── client/
│   ├── Client.java
│   └── lipsum2.txt
└── server/
├── Server.java
├── ServerProtocol.java
└── serverFiles/
└── lipsum1.txt
```
## How to Build and Run
Both client and server applications are designed to run on the same `localhost`.
### 1. Navigate to the Project Root
Open your terminal or command prompt and navigate to the `cwk` directory.
```bash
cd /path/to/your/cwk
```
### 2. Compile Server-side Code
Compile all Java files in the `server` directory.
```Bash
javac server/*.java
```
### 3. Compile Client-side Code
Compile all Java files in the `client` directory.
```Bash
javac client/*.java
```
### 4. Start the Server
From the cwk directory, run the server. It will listen on port `9500`.
```Bash
java server.Server
```
The server will run continuously in the terminal.
### 5. Run Client Commands (in a separate terminal)
Open a new terminal or command prompt window, navigate to the `cwk` directory, and then run client commands.
- List files on the server:
```Bash
java client.Client list
```
_Example output:_
```
Listing 1 file(s):
lipsum1.txt
```
- Upload a file to the server:
  ```Bash
  java client.Client put client/lipsum2.txt
  ```
  _Example output:_
  ```
  File transfer complete for lipsum2.txt
  ```
- Verify uploaded file (list again):
  ```Bash
  java client.Client list
  ```
  _Example output (after `lipsum2.txt` upload):_
  ```
  Listing 2 file(s):
  lipsum1.txt
  lipsum2.txt
  ```
- Attempt to upload an existing file (will result in an error):
  ```Bash
  java client.Client put client/lipsum2.txt
  ```
  _Example output:_
  ```
  Error: Filename already in use on server
  ```
- Attempt to put a non-existent local file:
  ```Bash
  java client.Client put non_existent_file.txt
  ```
  _Example output:_
  ```
  Error: Local file does not exist
  ```
## Notes
- Log File (`log.txt`): This file will be created/appended to in the `cwk/server/` directory upon valid client requests. It should not be submitted with your project as it will be deleted during assessment.

- File Types: The system is designed for text file transfers only.

- File Size: It's assumed all text files will be less than `64KB` in size.

- File Naming: Use UNIX-style filenames (forward slashes) as the assessment will be conducted on a Linux environment.

- No User Interaction: Neither client nor server applications expect interactive input from the user once launched. Client commands are strictly via command-line arguments.

