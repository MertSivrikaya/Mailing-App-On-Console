package client;

import model.User;
import request.Request;
import request.RequestBuilder;
import response.Response;
import response.ResponseCode;
import response.ResponseHandler;
import utility.ConsoleUI;
import utility.ProtocolHandler;

import java.io.*;
import java.net.Socket;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Scanner;

public class Client {
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;

    private Scanner scanner;

    private User user = null;
    private boolean isLoggedIn = false;

    private RequestBuilder requestBuilder;
    private ResponseHandler responseHandler;

    public class BooleanWrapper {
        public boolean value;

        public BooleanWrapper(boolean value) {
            this.value = value;
        }
    }

    public Client(Socket socket, Scanner scanner) {
        try {
            this.socket = socket;
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            this.scanner = scanner;

            requestBuilder = new RequestBuilder(scanner);
            responseHandler = new ResponseHandler(this);

        } catch (IOException e) {
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public boolean isLoggedIn() {
        return isLoggedIn;
    }

    public void setLoggedIn(boolean loggedIn) {
        isLoggedIn = loggedIn;
    }

    public void sendMessageToServer(String message) {
        try {
            bufferedWriter.write(message);
            bufferedWriter.newLine();
            bufferedWriter.flush();
        } catch (IOException e) {
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }

    public String readMessageFromServer() {
        try {
            return bufferedReader.readLine();
        } catch (IOException e) {
            closeEverything(socket, bufferedReader, bufferedWriter);
            return null;
        }
    }

    public Response getResponseFromServer() {
        String messageFromServer = readMessageFromServer();

        if (messageFromServer == null) {
            return null;
        }

        return ProtocolHandler.parseResponse(messageFromServer);
    }

    public void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
        try {
            if (bufferedReader != null) {   // When you close the upper wrapper, the underlying stream
                // 'InputStreamReader' is also closed
                bufferedReader.close();
            }
            if (bufferedWriter != null) {
                bufferedWriter.close();
            }

            if (socket != null) { // When you close the socket, socket's
                socket.close();   // input stream and output stream is also closed
            }

            // client.Client has disconnect, clear its record
            this.user = null;
            this.isLoggedIn = false;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);

        Socket socket = new Socket("localhost", 1234);
        Client client = new Client(socket, scanner);

        BooleanWrapper shouldExit = client.new BooleanWrapper(false);

        // Get user input as long as the client program is running
        while (!socket.isClosed() && !shouldExit.value) {

            Request clientRequest;
            Response serverResponse;
            ResponseCode responseCode;

            // Attempt to log in while the client is not logged in
            while (!client.isLoggedIn && !shouldExit.value) {

                // Form the request
                clientRequest = client.requestBuilder.buildLoginRequest(shouldExit);

                // Do not proceed if the request is null
                if (clientRequest == null)
                    break;

                // Send the request to server
                clientRequest.send(client);

                // Handle the LOGIN response from the server
                serverResponse =  client.getResponseFromServer();

                // Do not proceed if the response is null
                if (serverResponse == null) {
                    shouldExit.value = true;
                    break;
                }

                // Do not proceed if the account is removed
                if (serverResponse.getResponseCode() == ResponseCode.REMOVED_ACCOUNT) {
                    System.out.println("Your account was deleted by an admin. Exiting.");
                    shouldExit.value = true;
                    break;
                }

                client.responseHandler.handleLoginResponse(serverResponse);
            }

            // If login is successful, continue
            if (client.isLoggedIn && !shouldExit.value) {

                // Display the Actions User can Perform
                ConsoleUI.displayActions(client.getUser());

                // Get the desired action from User
                String action = scanner.nextLine().toUpperCase();

                switch (action) {
                    case "0": // fall through
                    case "EXIT":
                        System.out.println("Exiting the application.");
                        shouldExit.value = true;
                        break;

                    case "1":
                    case "INBOX":
                        // Form the request
                        clientRequest = client.requestBuilder.buildInboxRequest();

                        // Do not proceed if the request is null
                        if (clientRequest == null)
                            break;

                        // Send the request to the server
                        clientRequest.send(client);

                        // Handle the INBOX response from the server
                        serverResponse =  client.getResponseFromServer();

                        // Do not proceed if the response is null
                        if (serverResponse == null) {
                            shouldExit.value = true;
                            break;
                        }

                        // Do not proceed if the account is removed
                        if (serverResponse.getResponseCode() == ResponseCode.REMOVED_ACCOUNT) {
                            System.out.println("Your account was deleted by an admin. Exiting.");
                            shouldExit.value = true;
                            break;
                        }

                        client.responseHandler.handleInboxResponse(serverResponse);

                        break;

                    case "2":
                    case "OUTBOX":
                        // Form the request
                        clientRequest = client.requestBuilder.buildOutboxRequest();

                        // Do not proceed if the request is null
                        if (clientRequest == null)
                            break;

                        // Send the request to the server
                        clientRequest.send(client);

                        // Handle the OUTBOX response from the server
                        serverResponse =  client.getResponseFromServer();

                        // Do not proceed if the response is null
                        if (serverResponse == null) {
                            shouldExit.value = true;
                            break;
                        }

                        // Do not proceed if the account is removed
                        if (serverResponse.getResponseCode() == ResponseCode.REMOVED_ACCOUNT) {
                            System.out.println("Your account was deleted by an admin. Exiting.");
                            shouldExit.value = true;
                            break;
                        }

                        client.responseHandler.handleOutboxResponse(serverResponse);

                        break;

                    case "3":
                    case "SEND_MESSAGE":
                        // Form the request
                        clientRequest = client.requestBuilder.buildSendMessageRequest(client.getUser().getUsername());

                        // Do not proceed if the request is null
                        if (clientRequest == null)
                            break;

                        // Send the request to server
                        clientRequest.send(client);

                        // Handle the SEND_MESSAGE response from the server
                        serverResponse = client.getResponseFromServer();

                        // Do not proceed if the response is null
                        if (serverResponse == null) {
                            shouldExit.value = true;
                            break;
                        }

                        // Do not proceed if the account is removed
                        if (serverResponse.getResponseCode() == ResponseCode.REMOVED_ACCOUNT) {
                            System.out.println("Your account was deleted by an admin. Exiting.");
                            shouldExit.value = true;
                            break;
                        }

                        client.responseHandler.handleSendMessageResponse(serverResponse);

                        break;

                    case "4":
                    case "LOGOUT":
                        // Form the request
                        clientRequest = client.requestBuilder.buildLogoutRequest();

                        // Do not proceed if the request is null
                        if (clientRequest == null)
                            break;

                        // Send the request to server
                        clientRequest.send(client);

                        // Handle the SEND_MESSAGE response from the server
                        serverResponse = client.getResponseFromServer();

                        // Do not proceed if the response is null
                        if (serverResponse == null) {
                            shouldExit.value = true;
                            break;
                        }

                        // Do not proceed if the account is removed
                        if (serverResponse.getResponseCode() == ResponseCode.REMOVED_ACCOUNT) {
                            System.out.println("Your account was deleted by an admin. Exiting.");
                            shouldExit.value = true;
                            break;
                        }

                        client.responseHandler.handleLogoutResponse(serverResponse);

                        break;

                    case "5":
                    case "ADD_USER":
                        // Check if the user is authorized
                        if (!client.user.isAdmin()) {
                            responseCode =  ResponseCode.UNAUTHORIZED;
                            System.out.println("Unable to perform action.Error: " + responseCode);

                            break;
                        }

                        // Form the request
                        clientRequest = client.requestBuilder.buildAddUserRequest();

                        // Do not proceed if the request is null
                        if (clientRequest == null)
                            break;

                        // Send the request to server
                        clientRequest.send(client);

                        // Handle the ADD_USER response from the server
                        serverResponse = client.getResponseFromServer();

                        // Do not proceed if the response is null
                        if (serverResponse == null) {
                            shouldExit.value = true;
                            break;
                        }

                        // Do not proceed if the account is removed
                        if (serverResponse.getResponseCode() == ResponseCode.REMOVED_ACCOUNT) {
                            System.out.println("Your account was deleted by an admin. Exiting.");
                            shouldExit.value = true;
                            break;
                        }

                        client.responseHandler.handleAddUserResponse(serverResponse);

                        break;

                    case "6":
                    case "UPDATE_USER":
                        // Check if the user is authorized
                        if (!client.user.isAdmin()) {
                            responseCode =  ResponseCode.UNAUTHORIZED;
                            System.out.println("Unable to perform action.Error: " + responseCode);

                            break;
                        }

                        // Form the request
                        clientRequest = client.requestBuilder.buildUpdateUserRequest();

                        // Do not proceed if the request is null
                        if (clientRequest == null)
                            break;

                        // Send the request to server
                        clientRequest.send(client);

                        // Handle the UPDATE_USER response from the server
                        serverResponse = client.getResponseFromServer();

                        // Do not proceed if the response is null
                        if (serverResponse == null) {
                            shouldExit.value = true;
                            break;
                        }

                        // Do not proceed if the account is removed
                        if (serverResponse.getResponseCode() == ResponseCode.REMOVED_ACCOUNT) {
                            System.out.println("Your account was deleted by an admin. Exiting.");
                            shouldExit.value = true;
                            break;
                        }

                        client.responseHandler.handleUpdateUserResponse(serverResponse);

                        break;

                    case "7":
                    case "REMOVE_USER":
                        // Check if the user is authorized
                        if (!client.user.isAdmin()) {
                            responseCode =  ResponseCode.UNAUTHORIZED;
                            System.out.println("Unable to perform action.Error: " + responseCode);

                            break;
                        }

                        // Form the request
                        clientRequest = client.requestBuilder.buildRemoveUserRequest();

                        // Do not proceed if the request is null
                        if (clientRequest == null)
                            break;

                        // Send the request to server
                        clientRequest.send(client);

                        // Handle the REMOVE_USER response from the server
                        serverResponse = client.getResponseFromServer();

                        // Do not proceed if the response is null
                        if (serverResponse == null) {
                            shouldExit.value = true;
                            break;
                        }

                        // Do not proceed if the account is removed
                        if (serverResponse.getResponseCode() == ResponseCode.REMOVED_ACCOUNT) {
                            System.out.println("Your account was deleted by an admin. Exiting.");
                            shouldExit.value = true;
                            break;
                        }

                        client.responseHandler.handleRemoveUserResponse(serverResponse);

                        break;

                    case "8":
                    case "LIST_USERS":
                        // Check if the user is authorized
                        if (!client.user.isAdmin()) {
                            responseCode =  ResponseCode.UNAUTHORIZED;
                            System.out.println("Unable to perform action.Error: " + responseCode);

                            break;
                        }

                        // Form the request
                        clientRequest = client.requestBuilder.buildListUsersRequest();

                        // Do not proceed if the request is null
                        if (clientRequest == null)
                            break;

                        // Send the request to server
                        clientRequest.send(client);

                        // Handle the LIST_USER response from the server
                        serverResponse = client.getResponseFromServer();

                        // Do not proceed if the response is null
                        if (serverResponse == null) {
                            shouldExit.value = true;
                            break;
                        }

                        // Do not proceed if the account is removed
                        if (serverResponse.getResponseCode() == ResponseCode.REMOVED_ACCOUNT) {
                            System.out.println("Your account was deleted by an admin. Exiting.");
                            shouldExit.value = true;
                            break;
                        }

                        client.responseHandler.handleListUsersResponse(serverResponse);

                        break;

                    default:
                        System.out.println("Unknown action: " + action);
                }
            }
        }

        // client.Client has disconnect, clear its record
        client.closeEverything(client.socket, client.bufferedReader, client.bufferedWriter);
    }
}
