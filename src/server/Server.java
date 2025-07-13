package server;

import database.DB;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;

public class Server {
    private final ServerSocket serverSocket;
    private final DB db;

    public Server(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;

        DB tempDB;
        try {
            tempDB = new DB();
        } catch (SQLException e) {
            System.out.println("Cannot create connection to database.");
            tempDB = null;
        }
        db = tempDB;
    }

    public void startServer() {
        try {
            while (!serverSocket.isClosed()) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("A new client has connected!");
                ClientHandler clientHandler = new ClientHandler(clientSocket, db); // will handle this current client

                Thread thread = new Thread(clientHandler);
                thread.start();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeServer();
        }
    }

    public void closeServer() {
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            if (db != null) {
                db.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        // Our server will listen to clients that connects to port 1234
        ServerSocket serverSocket = new ServerSocket(1234);
        Server server = new Server(serverSocket);

        if (server.db != null) {
            server.startServer();
        }
    }
}
