package database;

import model.Message;
import model.User;

import java.sql.*;
import java.util.ArrayList;

/*
    'messages' table in PostgreSQL

    CREATE TABLE messages (
        message_id SERIAL PRIMARY KEY,
        sender_id INTEGER NOT NULL,
        receiver_id INTEGER NOT NULL,
        title TEXT NOT NULL,
        content TEXT NOT NULL,
        time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
        FOREIGN KEY (sender_id) REFERENCES users(user_id),
        FOREIGN KEY (receiver_id) REFERENCES users(user_id)
    );
 */

public class MessagesTable {
    private final Connection conn;
    private final UsersTable usersTable;

    public MessagesTable(Connection conn,  UsersTable usersTable) throws SQLException {
        this.conn = conn;
        initializeMessagesTable();
        this.usersTable = usersTable;
    }

    private void initializeMessagesTable() throws SQLException {
        String createTableSQL = """
        CREATE TABLE IF NOT EXISTS messages (
            message_id SERIAL PRIMARY KEY,
            sender_id INTEGER NOT NULL,
            receiver_id INTEGER NOT NULL,
            title TEXT NOT NULL,
            content TEXT NOT NULL,
            time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
            FOREIGN KEY (sender_id) REFERENCES users(user_id),
            FOREIGN KEY (receiver_id) REFERENCES users(user_id)
        );
        """;

        Statement stmt = conn.createStatement();
        stmt.execute(createTableSQL);
        stmt.close();

        System.out.println("Messages table initialized.");
    }

    public void insertMessage(Message message) throws SQLException {
        // Use the connection object to create a prepared statement with placeholders
        String insertSQL = "INSERT INTO messages (sender_id,receiver_id,title,content,time) VALUES (?,?,?,?,?)";
        PreparedStatement insertStmt = conn.prepareStatement(insertSQL);

        // Fill the placeholder values in the prepared statement
        int senderID = usersTable.getUserID(message.getSender());
        int receiverID = usersTable.getUserID(message.getReceiver());

        insertStmt.setInt(1, senderID);
        insertStmt.setInt(2, receiverID);
        insertStmt.setString(3, message.getTitle());
        insertStmt.setString(4, message.getContent());
        insertStmt.setTimestamp(5, message.getTime());

        // Execute the insert statement and update the table
        insertStmt.executeUpdate();

        // Close the prepared statement
        insertStmt.close();

        // Log to console
        System.out.println("Message inserted successfully.");
    }

    public ArrayList<Message> getInboxOfUser(User receiver) throws SQLException {
        int receiverID = usersTable.getUserID(receiver);
        System.out.println("Getting inbox of user");

        // Select all columns where receiver_id = receiverID
        String querySQL = "SELECT * FROM messages WHERE receiver_id = ? ORDER BY time DESC LIMIT 5";
        PreparedStatement queryStmt = conn.prepareStatement(querySQL);

        queryStmt.setInt(1, receiverID);

        // Executes the query
        // Stores the result in a ResultSet — which acts like a table in memory that you can loop through
        ResultSet rs = queryStmt.executeQuery();

        ArrayList<Message> messages = new ArrayList<>();

        // Loops through the ResultSet one row at a time.
        // rs.next() moves the cursor to the next row and returns true if there is one.
        while (rs.next()) {
            int senderID = rs.getInt("sender_id");
            User sender = usersTable.getUserWithID(senderID);

            String title = rs.getString("title");
            String content = rs.getString("content");
            Timestamp time = rs.getTimestamp("time");

            // Create and return a new model.Message object with fetched data
            messages.add(new Message(sender, receiver, title, content, time));
        }

        rs.close();
        queryStmt.close();

        return messages;
    }
    public ArrayList<Message> getOutboxOfUser(User sender) throws SQLException {
        int senderID = usersTable.getUserID(sender);

        // Select all columns where sender_id = senderID
        String querySQL = "SELECT * FROM messages WHERE sender_id = ? ORDER BY time DESC LIMIT 5";
        PreparedStatement queryStmt = conn.prepareStatement(querySQL);

        queryStmt.setInt(1, senderID);

        // Executes the query
        // Stores the result in a ResultSet — which acts like a table in memory that you can loop through
        ResultSet rs = queryStmt.executeQuery();

        ArrayList<Message> messages = new ArrayList<>();

        // Loops through the ResultSet one row at a time.
        // rs.next() moves the cursor to the next row and returns true if there is one.
        while (rs.next()) {
            int receiverID = rs.getInt("receiver_id");
            User receiver = usersTable.getUserWithID(receiverID);

            String title = rs.getString("title");
            String content = rs.getString("content");
            Timestamp time = rs.getTimestamp("time");

            // Create and return a new model.Message object with fetched data
            messages.add(new Message(sender, receiver, title, content, time));
        }

        rs.close();
        queryStmt.close();

        return messages;
    }

    public static void updateSenderId(Connection conn, int oldSenderId, int newSenderId) throws SQLException {
        String updateSenderSQL = "UPDATE messages SET sender_id = ? WHERE sender_id = ?";
        PreparedStatement updateSenderStmt = conn.prepareStatement(updateSenderSQL);

        updateSenderStmt.setInt(1, newSenderId);
        updateSenderStmt.setInt(2, oldSenderId);

        updateSenderStmt.executeUpdate();

        updateSenderStmt.close();
    }

    public static void updateReceiverId(Connection conn, int oldReceiverId, int newReceiverId) throws SQLException {
        String updateReceiverSQL = "UPDATE messages SET receiver_id = ? WHERE receiver_id = ?";
        PreparedStatement updateReceiverStmt = conn.prepareStatement(updateReceiverSQL);

        updateReceiverStmt.setInt(1, newReceiverId);
        updateReceiverStmt.setInt(2, oldReceiverId);

        updateReceiverStmt.executeUpdate();

        updateReceiverStmt.close();
    }


}
