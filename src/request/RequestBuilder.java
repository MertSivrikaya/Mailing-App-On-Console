package request;

import client.Client;
import database.UsersTable;
import response.ResponseCode;
import utility.ProtocolHandler;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

// A class that is used to form request.Request objects from user input
public class RequestBuilder {
    private final Scanner scanner;

    public RequestBuilder(Scanner scanner) {
        this.scanner = scanner;
    }

    public Request buildLoginRequest(Client.BooleanWrapper shouldExit) {
        Map<String,String> fields = new HashMap<>();

        System.out.println("Please enter your username or type 'exit' to quit:");
        String username = scanner.nextLine();

        if (username.equals("exit")) {
            shouldExit.value = true;
            return null;
        }

        if (username.equals(UsersTable.getDeletedUsername())) {
            System.out.println("Invalid username.");
            return null;
        }

        fields.put("username",username);

        System.out.println("Please enter your password:");
        String password = scanner.nextLine();
        fields.put("password",password);

        String serializedRequestContent = ProtocolHandler.serializeRequestContent(fields);

        return new Request("LOGIN",serializedRequestContent);
    }

    public Request buildInboxRequest() {
        return new Request("INBOX", "");
    }

    public Request buildOutboxRequest() {
        return new Request("OUTBOX", "");
    }

    public Request buildSendMessageRequest(String sender) {
        Map<String,String> fields = new HashMap<>();

        fields.put("sender",sender);

        System.out.println("Please enter the username of the user to whom you want to send message:");
        String receiver = scanner.nextLine();
        fields.put("receiver",receiver);

        System.out.println("Please enter the title of your message:");
        String title = scanner.nextLine();
        fields.put("title",title);

        System.out.println("Please enter the content of your message:");
        String content = scanner.nextLine();
        fields.put("content",content);

        String time = Timestamp.valueOf(LocalDateTime.now()).toString();
        fields.put("time",time);

        String serializedRequestContent = ProtocolHandler.serializeRequestContent(fields);

        return new Request("SEND_MESSAGE", serializedRequestContent);
    }

    public Request buildLogoutRequest() {
        return new Request("LOGOUT", "");
    }

    // Helper Method for buildAddUserRequest and buildUpdateUserRequest methods
    private Request buildModifyUserRequest(String action) {
        String operation = (action.equals("ADD_USER")) ? "add" : "update";
        ResponseCode responseCode;

        Map<String,String> fields = new HashMap<>();

        System.out.println("Please enter the username of the user you want to " + operation + ":");
        String username = scanner.nextLine();

        if (username == null ||  username.isEmpty()) {
            responseCode =  ResponseCode.INVALID_FIELD_VALUES;
            System.out.println("Unable to perform action. Username is not valid. Error: " + responseCode);

            return null; // don't return a request
        }

        fields.put("username",username);

        // For buildUpdateUserRequest method only
        if (!action.equals("ADD_USER")) {
            System.out.println("Please enter the new username of the user");
            String newUsername = scanner.nextLine();

            if (newUsername == null ||  newUsername.isEmpty()) {
                responseCode =  ResponseCode.INVALID_FIELD_VALUES;
                System.out.println("Unable to perform action. Username is not valid. Error: " + responseCode);

                return null;
            }

            fields.put("newUsername",newUsername);
        }

        if (operation.equals("add"))
            System.out.println("Please enter the password of the user you want to add:");
        else
            System.out.println("Please enter the new password of the user");

        String password = scanner.nextLine();

        if (password == null ||  password.isEmpty()) {
            responseCode =  ResponseCode.INVALID_FIELD_VALUES;
            System.out.println("Unable to perform action. Password is not valid. Error: " + responseCode);

            return null;
        }

        fields.put("password",password);

        if (operation.equals("add"))
            System.out.println("Please enter the name of the user you want to add:");
        else
            System.out.println("Please enter the new name of the user");

        String name = scanner.nextLine();

        if (name == null ||  name.isEmpty()) {
            responseCode =  ResponseCode.INVALID_FIELD_VALUES;
            System.out.println("Unable to perform action. Name is not valid. Error: " + responseCode);

            return null;
        }

        fields.put("name",name);

        if (operation.equals("add"))
            System.out.println("Please enter the surname of the user you want to add:");
        else
            System.out.println("Please enter the new surname of the user");

        String surname = scanner.nextLine();

        if (surname == null ||  surname.isEmpty()) {
            responseCode =  ResponseCode.INVALID_FIELD_VALUES;
            System.out.println("Unable to perform action. Surname is not valid. Error: " + responseCode);

            return null;
        }

        fields.put("surname",surname);

        if (operation.equals("add"))
            System.out.println("Please enter the birth year of the user you want to add:");
        else
            System.out.println("Please enter the new birth year of the user");

        String birthYear = scanner.nextLine();

        boolean isYearValid;

        try {
            int year = Integer.parseInt(birthYear);
            int currentYear = java.time.LocalDateTime.now().getYear();
            isYearValid = year > 0 && year <= currentYear;
        } catch (NumberFormatException e) {
            isYearValid = false; // Not a valid integer
        }

        if (!isYearValid) {
            responseCode =  ResponseCode.INVALID_FIELD_VALUES;
            System.out.println("Unable to perform action. Birthdate is not valid. Error: " + responseCode);

            return null;
        }

        if (operation.equals("add"))
            System.out.println("Please enter the birth month of the user you want to add:");
        else
            System.out.println("Please enter the new birth month of the user");

        String birthMonth = scanner.nextLine();

        boolean isMonthValid;

        try {
            int month = Integer.parseInt(birthMonth);
            int currentMonth = java.time.LocalDateTime.now().getMonthValue();
            int currentYear = java.time.LocalDateTime.now().getYear();
            isMonthValid = month > 0 && month <= 12;
            if (Integer.parseInt(birthYear) == currentYear && month > currentMonth) {
                isMonthValid = false;
            }
        } catch (NumberFormatException e) {
            isMonthValid = false; // Not a valid integer
        }

        if (!isMonthValid) {
            responseCode =  ResponseCode.INVALID_FIELD_VALUES;
            System.out.println("Unable to perform action. Birthdate is not valid. Error: " + responseCode);

            return null;
        }

        if (Integer.parseInt(birthMonth) < 10)
            birthMonth = "0" + Integer.toString(Integer.parseInt(birthMonth));

        if (operation.equals("add"))
            System.out.println("Please enter the birth day of the user you want to add:");
        else
            System.out.println("Please enter the new birth day of the user");

        String birthDay = scanner.nextLine();

        boolean isDayValid;

        try {
            int day = Integer.parseInt(birthDay);
            int currentDay = java.time.LocalDateTime.now().getDayOfMonth();
            int currentMonth = java.time.LocalDateTime.now().getMonthValue();
            int currentYear = java.time.LocalDateTime.now().getYear();
            isDayValid = day > 0 && day <= 31;
            if (Integer.parseInt(birthYear) == currentYear && Integer.parseInt(birthMonth) == currentMonth && day > currentDay) {
                isDayValid = false;
            }
        } catch (NumberFormatException e) {
            isDayValid = false; // Not a valid integer
        }

        if (!isDayValid) {
            responseCode =  ResponseCode.INVALID_FIELD_VALUES;
            System.out.println("Unable to perform action. Birthdate is not valid. Error: " + responseCode);

            return null;
        }

        if (Integer.parseInt(birthDay) < 10)
            birthDay = "0" + Integer.toString(Integer.parseInt(birthDay));

        String birthdate = birthYear + "-" + birthMonth + "-" + birthDay;

        fields.put("birthdate",birthdate);

        if (operation.equals("add"))
            System.out.println("Please enter the gender of the user you want to add:");
        else
            System.out.println("Please enter the new gender of the user");

        String gender = scanner.nextLine();

        if (gender == null ||  gender.isEmpty()) {
            responseCode =  ResponseCode.INVALID_FIELD_VALUES;
            System.out.println("Unable to perform action. Gender is not valid. Error: " + responseCode);

            return null;
        }

        if (!gender.equalsIgnoreCase("M") && !gender.equalsIgnoreCase("F") && !gender.equalsIgnoreCase("O")) {
            responseCode =  ResponseCode.INVALID_FIELD_VALUES;
            System.out.println("Unable to perform action. Gender is not valid. Error: " + responseCode);

            return null;
        }

        fields.put("gender",gender);

        if (operation.equals("add"))
            System.out.println("Please enter the email of the user you want to add:");
        else
            System.out.println("Please enter the new email of the user");

        String email = scanner.nextLine();

        if (email == null || !email.contains("@") || !email.contains(".")) {
            responseCode =  ResponseCode.INVALID_FIELD_VALUES;
            System.out.println("Unable to perform action. Email is not valid. Error: " + responseCode);

            return null;
        }

        fields.put("email",email);

        if (operation.equals("add"))
            System.out.println("Please enter the location of the user you want to add:");
        else
            System.out.println("Please enter the new location of the user");

        String location = scanner.nextLine();

        if (location == null ||  location.isEmpty()) {
            responseCode =  ResponseCode.INVALID_FIELD_VALUES;
            System.out.println("Unable to perform action. Location is not valid. Error: " + responseCode);

            return null;
        }

        fields.put("location",location);

        if (operation.equals("add"))
            System.out.println("Please type 'true' if the user you want to add is an admin user:");
        else
            System.out.println("Please type 'true' if the user you want to modify the user as an admin user:");

        String isAdmin = scanner.nextLine();
        isAdmin = (isAdmin.equals("true")) ? "true" : "false";

        fields.put("isAdmin",isAdmin);

        String serializedRequestContent = ProtocolHandler.serializeRequestContent(fields);

        if (operation.equals("add"))
            return new Request("ADD_USER", serializedRequestContent);
        else
            return new Request("UPDATE_USER", serializedRequestContent);
    }

    public Request buildAddUserRequest() {
        return buildModifyUserRequest("ADD_USER");
    }

    public Request buildUpdateUserRequest() {
        return buildModifyUserRequest("UPDATE_USER");
    }

    public Request buildRemoveUserRequest() {
        Map<String,String> fields = new HashMap<>();

        System.out.println("Please enter the username of the user you want to remove:");
        String username = scanner.nextLine();
        fields.put("username",username);

        String serializedRequestContent = ProtocolHandler.serializeRequestContent(fields);

        return new Request("REMOVE_USER", serializedRequestContent);
    }

    public Request buildListUsersRequest() {
        return new Request("LIST_USERS", "");
    }
}
