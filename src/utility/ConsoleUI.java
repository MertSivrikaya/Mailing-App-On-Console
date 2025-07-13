package utility;

import database.UsersTable;
import model.Message;
import model.User;

import java.util.ArrayList;

public class ConsoleUI {
    public static void displayActions(User user) {
        if (user == null) {
            System.out.println("User is null");
            return;
        }

        // Non-admin actions
        System.out.println("Please Choose the Action You Want to Perform:");

        System.out.println("0. EXIT");
        System.out.println("1. INBOX");
        System.out.println("2. OUTBOX");
        System.out.println("3. SEND_MESSAGE");
        System.out.println("4. LOGOUT");

        // Admin actions
        if (user.isAdmin()) {
            System.out.println("5. ADD_USER");
            System.out.println("6. UPDATE_USER");
            System.out.println("7. REMOVE_USER");
            System.out.println("8. LIST_USERS");
        }
    }

    public static void displayInbox(ArrayList<Message> inbox) {
        if (inbox == null || inbox.isEmpty()) {
            System.out.println("Inbox is empty.");
            return;
        }

        System.out.println("Inbox Messages:\n");

        // Header
        System.out.printf("%-20s | %-20s | %-30s | %-19s\n", "From", "Title", "Content", "Time");
        System.out.println("----------------------------------------------------------------------------------------------");

        // Rows
        for (Message message : inbox) {
            String sender = message.getSender().getUsername();
            String title = message.getTitle();
            String content = message.getContent();
            String time = message.getTime().toString();

            // Print each row with padding (truncate if too long)
            System.out.printf("%-20.20s | %-20.20s | %-30.30s | %-19s\n",
                    sender, title, content, time);
        }

        System.out.println();
    }

    public static void displayOutbox(ArrayList<Message> outbox) {
        if (outbox == null || outbox.isEmpty()) {
            System.out.println("Outbox is empty.");
            return;
        }

        System.out.println("Outbox Messages:\n");

        // Header
        System.out.printf("%-20s | %-20s | %-30s | %-19s\n", "To", "Title", "Content", "Time");
        System.out.println("----------------------------------------------------------------------------------------------");

        // Rows
        for (Message message : outbox) {
            String receiver = message.getReceiver().getUsername();
            String title = message.getTitle();
            String content = message.getContent();
            String time = message.getTime().toString();

            // Print each row with padding (truncate if too long)
            System.out.printf("%-20.20s | %-20.20s | %-30.30s | %-19s\n",
                    receiver, title, content, time);
        }

        System.out.println();
    }

    public static void displayUsersList(ArrayList<User> usersList) {
        if (usersList == null || usersList.isEmpty()) {
            System.out.println("No users found.");
            return;
        }

        if (usersList.size() == 2) { // only dummy user and admin user
            System.out.println("No users found.");
            return;
        }

        final int unameW = 15, nameW = 10, snameW = 10, bdayW = 10, genderW = 6;
        final int emailW = 24, locW = 12, adminW = 5;

        String top = "╔" + "═".repeat(unameW + 2) + "╦" + "═".repeat(nameW + 2) + "╦" +
                     "═".repeat(snameW + 2) + "╦" + "═".repeat(bdayW + 2) + "╦" +
                     "═".repeat(genderW + 2) + "╦" + "═".repeat(emailW + 2) + "╦" +
                     "═".repeat(locW + 2) + "╦" + "═".repeat(adminW + 2) + "╗";

        String mid = "╠" + "═".repeat(unameW + 2) + "╬" + "═".repeat(nameW + 2) + "╬" +
                     "═".repeat(snameW + 2) + "╬" + "═".repeat(bdayW + 2) + "╬" +
                     "═".repeat(genderW + 2) + "╬" + "═".repeat(emailW + 2) + "╬" +
                     "═".repeat(locW + 2) + "╬" + "═".repeat(adminW + 2) + "╣";

        String bot = "╚" + "═".repeat(unameW + 2) + "╩" + "═".repeat(nameW + 2) + "╩" +
                     "═".repeat(snameW + 2) + "╩" + "═".repeat(bdayW + 2) + "╩" +
                     "═".repeat(genderW + 2) + "╩" + "═".repeat(emailW + 2) + "╩" +
                     "═".repeat(locW + 2) + "╩" + "═".repeat(adminW + 2) + "╝";

        System.out.println(top);
        System.out.printf("║ %-" + unameW + "s ║ %-" + nameW + "s ║ %-" + snameW + "s ║ %-" + bdayW + "s ║ %-" + genderW + "s ║ %-" + emailW + "s ║ %-" + locW + "s ║ %-" + adminW + "s ║\n",
                "Username", "Name", "Surname", "Birthdate", "Gender", "Email", "Location", "Admin");
        System.out.println(mid);

        for (User user : usersList) {
            if (!user.getUsername().equals(UsersTable.getDeletedUsername()) &&
                !user.getUsername().equals(UsersTable.getAdminUsername())) {
                System.out.printf("║ %-" + unameW + "." + unameW + "s ║ %-" + nameW + "." + nameW + "s ║ %-" + snameW + "." + snameW + "s ║ %-" + bdayW + "." + bdayW + "s ║ %-" + genderW + "." + genderW + "s ║ %-" + emailW + "." + emailW + "s ║ %-" + locW + "." + locW + "s ║ %-" + adminW + "s ║\n",
                        user.getUsername(),
                        user.getName(),
                        user.getSurname(),
                        user.getBirthdate(),
                        user.getGender(),
                        user.getEmail(),
                        user.getLocation(),
                        user.isAdmin() ? "Yes" : "No");
            }
        }

        System.out.println(bot);

        System.out.println();
    }
}
