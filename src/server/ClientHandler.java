package server;

import action.*;
import database.DB;
import model.User;
import request.Request;
import utility.ProtocolHandler;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ClientHandler implements Runnable {
    // User for logging-out the users on removal
    private static final Map<String, ClientHandler> activeUsers = new ConcurrentHashMap<>();

    private Socket clientSocket; // this socket is on the server, so
    private BufferedReader bufferedReader; // this is what the server receives from the client
    private BufferedWriter bufferedWriter; // this is what the server send to the client

    private DB db;

    private User handledUser = null;
    private boolean isLoggedIn = false;

    public ClientHandler(Socket clientSocket, DB db) {
        try {
            this.clientSocket = clientSocket;
            this.bufferedReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
            this.db = db;
        } catch (IOException e) {
            closeEverything(clientSocket, bufferedReader, bufferedWriter);
        }
    }

    public Socket getSocket() {
        return clientSocket;
    }

    public BufferedReader getBufferedReader() {
        return bufferedReader;
    }

    public BufferedWriter getBufferedWriter() {
        return bufferedWriter;
    }

    public DB getDB () {
        return db;
    }

    public User getHandledUser() {
        return handledUser;
    }

    public void setHandledUser(User handledUser) {
        this.handledUser = handledUser;
    }

    public boolean getLoggedIn() {
        return isLoggedIn;
    }

    public void setLoggedIn(boolean isLoggedIn) {
        this.isLoggedIn = isLoggedIn;
    }

    public static void registerUser(String username, ClientHandler handler) {
        activeUsers.put(username, handler);
    }

    public static void unregisterUser(String username) {
        activeUsers.remove(username);
    }

    public static ClientHandler getHandler(String username) {
        return activeUsers.get(username);
    }

    public void sendMessageToClient(String message) {
        try {
            bufferedWriter.write(message);
            bufferedWriter.newLine();
            bufferedWriter.flush();
        } catch (IOException e) {
            closeEverything(clientSocket, bufferedReader, bufferedWriter);
        }
    }

    public String readMessageFromClient() {
        try {
            return bufferedReader.readLine();
        } catch (IOException e) {
            closeEverything(clientSocket, bufferedReader, bufferedWriter);
            return null;
        }
    }

    public Request getRequestFromClient() {
        String messageFromClient = readMessageFromClient();

        // If client has disconnected
        if (messageFromClient == null) {
            return null;
        }

        return ProtocolHandler.parseRequest(messageFromClient);
    }

    @Override
    public void run() { // This will run in a separate thread

        while (clientSocket.isConnected()) {

            Request clientRequest = getRequestFromClient();

            // If client disconnected or sent invalid message
            if (clientRequest == null) {
                System.out.println("Client disconnected.");
                closeEverything(clientSocket, bufferedReader, bufferedWriter);
                break;
            }

            String requestedAction = clientRequest.getAction();
            String serializedRequestContent = clientRequest.getSerializedRequestContent();

            Map<String, String> requestContentFields = ProtocolHandler.parseRequestContent(serializedRequestContent);

            Action action;

            switch (requestedAction) {
                case "LOGIN":
                    String username = requestContentFields.get("username");
                    String password = requestContentFields.get("password");

                    action = new LoginAction(username, password, this);
                    break;

                case "LOGOUT":
                    action = new LogoutAction(handledUser.getUsername(), this);
                    break;

                case "INBOX":
                    action = new InboxAction(this);
                    break;

                case "OUTBOX":
                    action = new OutboxAction(this);
                    break;

                case "SEND_MESSAGE":
                    String sender = handledUser.getUsername();
                    String receiver = requestContentFields.get("receiver");
                    String title = requestContentFields.get("title");
                    String content = requestContentFields.get("content");
                    String time = requestContentFields.get("time");

                    Map<String, String> fields = new HashMap<>();
                    fields.put("sender", sender);
                    fields.put("receiver", receiver);
                    fields.put("title", title);
                    fields.put("content", content);
                    fields.put("time", time);

                    action = new SendMessageAction(fields, this);
                    break;

                case "ADD_USER":
                    String usernameToAdd = requestContentFields.get("username");
                    String passwordToAdd = requestContentFields.get("password");
                    String nameToAdd = requestContentFields.get("name");
                    String surnameToAdd = requestContentFields.get("surname");
                    String birthdateToAdd = requestContentFields.get("birthdate");
                    String genderToAdd = requestContentFields.get("gender");
                    String emailToAdd = requestContentFields.get("email");
                    String locationToAdd = requestContentFields.get("location");
                    String isAdminToAdd = requestContentFields.get("isAdmin");

                    Map<String, String> fieldsToAddAddUser = new HashMap<>();
                    fieldsToAddAddUser.put("username", usernameToAdd);
                    fieldsToAddAddUser.put("password", passwordToAdd);
                    fieldsToAddAddUser.put("name", nameToAdd);
                    fieldsToAddAddUser.put("surname", surnameToAdd);
                    fieldsToAddAddUser.put("birthdate", birthdateToAdd);
                    fieldsToAddAddUser.put("gender", genderToAdd);
                    fieldsToAddAddUser.put("email", emailToAdd);
                    fieldsToAddAddUser.put("location", locationToAdd);
                    fieldsToAddAddUser.put("isAdmin", isAdminToAdd);

                    action = new AddUserAction(fieldsToAddAddUser, this);
                    break;

                case "UPDATE_USER":
                    String usernameToUpdate = requestContentFields.get("username");
                    String updatedUsername = requestContentFields.get("newUsername");
                    String updatedPassword = requestContentFields.get("password");
                    String updatedName = requestContentFields.get("name");
                    String updatedSurname = requestContentFields.get("surname");
                    String updatedBirthdate = requestContentFields.get("birthdate");
                    String updatedGender = requestContentFields.get("gender");
                    String updatedEmail = requestContentFields.get("email");
                    String updatedLocation = requestContentFields.get("location");
                    String updatedIsAdmin = requestContentFields.get("isAdmin");

                    Map<String, String> fieldsToAddUpdateUser = new HashMap<>();
                    fieldsToAddUpdateUser.put("username", usernameToUpdate);
                    fieldsToAddUpdateUser.put("newUsername", updatedUsername);
                    fieldsToAddUpdateUser.put("password", updatedPassword);
                    fieldsToAddUpdateUser.put("name", updatedName);
                    fieldsToAddUpdateUser.put("surname", updatedSurname);
                    fieldsToAddUpdateUser.put("birthdate", updatedBirthdate);
                    fieldsToAddUpdateUser.put("gender", updatedGender);
                    fieldsToAddUpdateUser.put("email", updatedEmail);
                    fieldsToAddUpdateUser.put("location", updatedLocation);
                    fieldsToAddUpdateUser.put("isAdmin", updatedIsAdmin);

                    action = new UpdateUserAction(fieldsToAddUpdateUser, this);
                    break;

                case "REMOVE_USER":
                    String usernameToRemove = requestContentFields.get("username");

                    action = new RemoveUserAction(usernameToRemove, this);
                    break;

                case "LIST_USERS":
                    action = new ListUsersAction(this);
                    break;

                default: // invalid action
                    action = new ErrorAction(this);
            }

            action.validateAndExecute();
        }
    }

    public void closeEverything(Socket clientSocket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
        try {
            if (bufferedReader != null) {   // When you close the upper wrapper, the underlying stream
                // 'InputStreamReader' is also closed
                bufferedReader.close();
            }
            if (bufferedWriter != null) {
                bufferedWriter.close();
            }

            if (clientSocket != null) { // When you close the socket, socket's
                clientSocket.close();   // input stream and output stream is also closed
            }

            if (handledUser != null) {
                unregisterUser(handledUser.getUsername());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
