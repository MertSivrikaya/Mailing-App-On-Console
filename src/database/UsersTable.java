package database;

import model.User;

import java.sql.*;
import java.util.ArrayList;

/*

    'users' table in PostgreSQL

    CREATE TABLE users (
        user_id SERIAL PRIMARY KEY,
        username TEXT UNIQUE,
        password TEXT,
        name TEXT,
        surname TEXT,
        birthdate DATE,
        gender TEXT,
        email TEXT,
        location TEXT,
        is_admin BOOLEAN
    );
 */

public class UsersTable {
    private final Connection conn;
    private static final String DELETED_USERNAME = "deleted_user";
    private static final String ADMIN_USERNAME = "admin_user";

    public UsersTable(Connection conn) throws SQLException {
        this.conn = conn;
        initializeUsersTable();
    }

    public static String getDeletedUsername() {
        return DELETED_USERNAME;
    }

    public static String getAdminUsername() {
        return ADMIN_USERNAME;
    }

    private void initializeUsersTable() throws SQLException {
        // Create the users table if it doesn't exist
        String createTableSQL = """
        CREATE TABLE IF NOT EXISTS users (
            user_id SERIAL PRIMARY KEY,
            username TEXT UNIQUE,
            password TEXT,
            name TEXT,
            surname TEXT,
            birthdate DATE,
            gender TEXT,
            email TEXT,
            location TEXT,
            is_admin BOOLEAN
        );
        """;

        Statement stmt = conn.createStatement();
        stmt.execute(createTableSQL);
        stmt.close();

        // Insert dummy user if not already present
        boolean dummyExists = doesUserExist(DELETED_USERNAME);

        if (!dummyExists) {
            User dummyUser = new User(DELETED_USERNAME, "deleted",
                    "deleted", "1900-01-01",
                    "O", "deleted@example.com",
                    "deleted", false);

            insertUser(dummyUser, "deleted");

            System.out.println("Dummy user " + DELETED_USERNAME + " inserted.");
        } else {
            System.out.println("Dummy user " + DELETED_USERNAME + " already exists.");
        }

        // Insert admin user if not already present
        boolean adminExists = doesUserExist(ADMIN_USERNAME);

        if (!adminExists) {
            User adminUser = new User(ADMIN_USERNAME, "admin",
                    "admin", "1900-01-01",
                    "O", "admin@example.com",
                    "admin", true);

            insertUser(adminUser, "admin");

            System.out.println("Admin user " + ADMIN_USERNAME + " inserted.");
        } else {
            System.out.println("Admin user " + ADMIN_USERNAME + " already exists.");
        }
    }

    private User buildUserFromResultSet(ResultSet rs) throws SQLException {
        String username = rs.getString("username");
        String name = rs.getString("name");
        String surname = rs.getString("surname");
        String birthdate = rs.getDate("birthdate").toLocalDate().toString();
        String gender = rs.getString("gender");
        String email = rs.getString("email");
        String location = rs.getString("location");
        boolean isAdmin = rs.getBoolean("is_admin");

        return new User(username, name, surname, birthdate, gender, email, location, isAdmin);
    }

    public ArrayList<User> getUsersList() throws  SQLException {
        String querySQL = "SELECT * FROM users";

        // Creates a 'Statement object' using the `Connection` for direct SQL.
        Statement queryStmt = conn.createStatement();

        // Executes the query (SELECT * FROM users)
        // Stores the result in a ResultSet — which acts like a table in memory that you can loop through
        ResultSet rs = queryStmt.executeQuery(querySQL);

        ArrayList<User> allUsers = new ArrayList<>();

        // Loops through the ResultSet one row at a time.
        // rs.next() moves the cursor to the next row and returns true if there is one.
        while (rs.next()) {
            allUsers.add(buildUserFromResultSet(rs));
        }

        rs.close();
        queryStmt.close();

        return allUsers;
    }

    public User getUserWithUsername(String username) throws SQLException {
        // Select all columns where username = username
        String querySQL = "SELECT * FROM users WHERE username = ?";
        PreparedStatement queryStmt = conn.prepareStatement(querySQL);

        queryStmt.setString(1, username);

        // Executes the query (SELECT * FROM users)
        // Stores the result in a ResultSet — which acts like a table in memory that you can loop through
        ResultSet rs = queryStmt.executeQuery();

        // Look for the first row of the result set
        // rs.next() moves the cursor to the next row and returns true if there is one.

        User user = null;

        if (rs.next()) {
            // Create and return a new User object with fetched data
            user =  buildUserFromResultSet(rs);
        }

        rs.close();
        queryStmt.close();

        return user;
    }

    public User getUserWithID(int userID) throws SQLException {
        // Select all columns where username = username
        String querySQL = "SELECT * FROM users WHERE user_id = ?";
        PreparedStatement queryStmt = conn.prepareStatement(querySQL);

        queryStmt.setInt(1, userID);

        // Executes the query (SELECT * FROM users)
        // Stores the result in a ResultSet — which acts like a table in memory that you can loop through
        ResultSet rs = queryStmt.executeQuery();

        // Look for the first row of the result set
        // rs.next() moves the cursor to the next row and returns true if there is one.

        User user = null;

        if (rs.next()) {
            // Create and return a new User object with fetched data
            user =  buildUserFromResultSet(rs);
        } else {
            System.out.println("No user found with ID: " + userID);
        }

        rs.close();
        queryStmt.close();

        return user;
    }

    public void insertUser(User user, String password) throws SQLException {
        if (doesUserExist(user.getUsername())) {
            System.out.println("User already exists.");
            return;
        }

        // Use the connection object to create a prepared statement with placeholders
        String insertSQL = "INSERT INTO users (username,password,name,surname,birthdate,gender,email,location,is_admin) VALUES (?,?,?,?,?,?,?,?,?)";
        PreparedStatement insertStmt = conn.prepareStatement(insertSQL);

        // Fill the placeholder values in the prepared statement
        insertStmt.setString(1, user.getUsername());
        insertStmt.setString(2, password);
        insertStmt.setString(3, user.getName());
        insertStmt.setString(4, user.getSurname());
        insertStmt.setDate(5, Date.valueOf(user.getBirthdate()));
        insertStmt.setString(6, user.getGender());
        insertStmt.setString(7, user.getEmail());
        insertStmt.setString(8, user.getLocation());
        insertStmt.setBoolean(9, user.isAdmin());

        // Execute the insert statement and update the table
        insertStmt.executeUpdate();

        // Close the prepared statement
        insertStmt.close();

        // Log to console
        System.out.println("User inserted successfully.");
    }

    public void updateUser(String usernameToUpdate, User updatedUser, String updatedPassword) throws SQLException {
        if (!doesUserExist(usernameToUpdate)) {
            System.out.println("User not found.");
            return;
        }

        if (usernameToUpdate.equals(DELETED_USERNAME) || usernameToUpdate.equals(ADMIN_USERNAME)) {
            System.out.println("This username is reserved for special purposes, cannot update.");
            return;
        }

        if (updatedUser.getUsername().equals(DELETED_USERNAME) || updatedUser.getUsername().equals(ADMIN_USERNAME)) {
            System.out.println("This username is reserved for special purposes, cannot update.");
            return;
        }

        // Prepare the SQL UPDATE statement with placeholders
        String updateSQL = "UPDATE users SET username=?, password=?, name=?, surname=?, birthdate=?, gender=?, email=?, location=?, is_admin=? WHERE username=?";
        PreparedStatement updateStmt = conn.prepareStatement(updateSQL);

        // Fill in the values for the update
        updateStmt.setString(1, updatedUser.getUsername());
        updateStmt.setString(2, updatedPassword);
        updateStmt.setString(3, updatedUser.getName());
        updateStmt.setString(4, updatedUser.getSurname());
        updateStmt.setDate(5, Date.valueOf(updatedUser.getBirthdate()));
        updateStmt.setString(6, updatedUser.getGender());
        updateStmt.setString(7, updatedUser.getEmail());
        updateStmt.setString(8, updatedUser.getLocation());
        updateStmt.setBoolean(9, updatedUser.isAdmin());
        updateStmt.setString(10, usernameToUpdate); // WHERE clause

        // Execute the update statement and update the table
        updateStmt.executeUpdate();

        // Close the prepared statement
        updateStmt.close();

        // Log to console
        System.out.println("User updated successfully.");
    }

    public void removeUser(String userToRemove) throws SQLException {
        if (!doesUserExist(userToRemove)) {
            System.out.println("User not found.");
            return;
        }

        if (userToRemove.equals(DELETED_USERNAME) || userToRemove.equals(ADMIN_USERNAME)) {
            System.out.println("This username is reserved for special purposes, cannot delete.");
            return;
        }

        // Get user_id of the user to remove
        int userIdToRemove = getUserID(getUserWithUsername(userToRemove));

        // Get user_id of 'deleted_user'
        int deletedUserId = getUserID(getUserWithUsername(DELETED_USERNAME));

        if (deletedUserId == -1) {
            System.out.println("Dummy user " + DELETED_USERNAME + " not found. Aborting delete.");
            return;
        }

        // Update sender_id of all messages whose previous sender_id is userIdToRemove
        MessagesTable.updateSenderId(conn, userIdToRemove, deletedUserId);

        // Update receiver_id of all messages whose previous receiver_id is userIdToRemove
        MessagesTable.updateReceiverId(conn, userIdToRemove, deletedUserId);

        // Use the connection object to create a prepared statement with placeholders
        String deleteSQL = "DELETE FROM users WHERE username = ?";
        PreparedStatement deleteStmt = conn.prepareStatement(deleteSQL);

        // Fill the placeholder values in the prepared statement
        deleteStmt.setString(1, userToRemove);

        // Execute the remove statement and update the table
        deleteStmt.executeUpdate();

        // Close the prepared statement
        deleteStmt.close();

        // Log to console
        System.out.println("User deleted successfully. All messages reassigned to " + DELETED_USERNAME + ".");
    }

    public int getUserID(User user) throws SQLException {
        String querySQL = "SELECT user_id FROM users WHERE username = ?";
        PreparedStatement queryStmt = conn.prepareStatement(querySQL);

        queryStmt.setString(1, user.getUsername());

        // Executes the query (SELECT * FROM users)
        // Stores the result in a ResultSet — which acts like a table in memory that you can loop through
        ResultSet rs = queryStmt.executeQuery();

        int id = -1;

        // Look for the first row of the result set
        // rs.next() moves the cursor to the next row and returns true if there is one.
        if (rs.next()) {
            id = rs.getInt("user_id");
        } else {
            System.out.println("User not found.");
        }

        rs.close();
        queryStmt.close();

        return id;
    }

    private String getUserPassword(String username) throws SQLException {
        String querySQL = "SELECT password FROM users WHERE username = ?";
        PreparedStatement queryStmt = conn.prepareStatement(querySQL);

        queryStmt.setString(1, username);

        // Executes the query (SELECT * FROM users)
        // Stores the result in a ResultSet — which acts like a table in memory that you can loop through
        ResultSet rs = queryStmt.executeQuery();

        String password = null;

        // Look for the first row of the result set
        // rs.next() moves the cursor to the next row and returns true if there is one.
        if (rs.next()) {
            password = rs.getString("password");
        } else {
            System.out.println("User not found.");
        }

        rs.close();
        queryStmt.close();

        return password;
    }

    public boolean authenticateUser(String username, String password) throws SQLException {
        String passwordInDB = getUserPassword(username);
        return passwordInDB.equals(password);
    }

    public boolean doesUserExist(String username) throws SQLException {
        String querySQL = "SELECT * FROM users WHERE username = ?";
        PreparedStatement queryStmt = conn.prepareStatement(querySQL);

        queryStmt.setString(1, username);

        ResultSet rs = queryStmt.executeQuery();

        boolean exists = rs.next();

        rs.close();
        queryStmt.close();

        return exists;
    }
}
