package database;

import model.Message;
import model.User;

import java.sql.*;
import java.util.ArrayList;

public class DB {
    // JDBC connection variables
    private final String url = "jdbc:postgresql://localhost:5432/mydb";
    private final String user = "postgres";
    private final String password = "dbpassword";

    private final Connection conn;

    private final UsersTable usersTable;
    private final MessagesTable messagesTable;

    public DB() throws SQLException {
        // Connect to PostgreSQl and initialize 'conn'
        conn = DriverManager.getConnection(url, user, password);
        System.out.println("Connected to PostgreSQL.");

        // Construct composition objects usersTable and messagesTable
        usersTable = new UsersTable(conn);
        messagesTable = new MessagesTable(conn, usersTable);
    }

    public void close() throws SQLException {
        if (conn != null && !conn.isClosed()) {
            conn.close();
            System.out.println("Connection closed.");
        }
    }

    public ArrayList<User> getUsersList() throws SQLException {
        return usersTable.getUsersList();
    }

    public User getUserWithUsername(String username) throws SQLException {
        return usersTable.getUserWithUsername(username);
    }

    public void insertUser(User user, String password) throws SQLException {
        usersTable.insertUser(user, password);
    }

    public void updateUser(String usernameToUpdate, User updatedUser, String updatedPassword) throws SQLException {
        usersTable.updateUser(usernameToUpdate, updatedUser, updatedPassword);
    }

    public void removeUser(String userToRemove) throws SQLException {
        usersTable.removeUser(userToRemove);
    }

    public boolean authenticateUser(String username, String password) throws SQLException {
        return usersTable.authenticateUser(username, password);
    }

    public ArrayList<Message> getInboxOfUser(User user) throws SQLException {
        return messagesTable.getInboxOfUser(user);
    }

    public ArrayList<Message> getOutboxOfUser(User user) throws SQLException {
        return messagesTable.getOutboxOfUser(user);
    }

    public void insertMessage(Message message) throws SQLException {
        messagesTable.insertMessage(message);
    }

    public boolean doesUserExist(String username) throws SQLException {
        return usersTable.doesUserExist(username);
    }






}
