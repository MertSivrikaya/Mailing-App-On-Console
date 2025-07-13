package utility;

import model.Message;
import model.User;
import request.Request;
import response.Response;
import response.ResponseCode;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/*
    request.Request Format:
        action%action~serializedRequestContent%username%username~(...)
    response.Response Format: // code here denotes the response code
        code%code%__RESP__user%(...)
 */

// Utility class for handling the network protocol
public class ProtocolHandler {
    // Delimiters for Our Network Protocol
    private static final String LINE_DELIMITER = "__LINE__" ;                   // Used to separate different protocols/lines (old value: #)
    private static final String REQUEST_DELIMITER = "__REQ__";                  // Used to separate parameters inside a request from client (old value: ~)
    private static final String REQUEST_CONTENT_DELIMITER = "__REQCONTENT__";   // Used to separate parameters inside a request content
    private static final String RESPONSE_DELIMITER = "__RESP__";                // Used to separate parameters inside a response from the server
    private static final String USER_FIELD_DELIMITER = "__USER__";              // Used to separate fields inside model.User (old value: |, needs escaping in regex context with "\\" +)
    private static final String MESSAGE_FIELD_DELIMITER = "__MESG__";           // Used to separate fields inside model.Message (old value: ^)
    private static final String KEY_VALUE_DELIMITER = "__KV__";                 // Used to separate a key and value inside a key-value pair (old value: %)

    // Used to remove the redundant delimiter placed at the end of the serialized fields
    private static String removeLastOccurrence(String input, String target) {
        int lastIndex = input.lastIndexOf(target);
        if (lastIndex == -1) return input; // target not found

        return input.substring(0, lastIndex) + input.substring(lastIndex + target.length());
    }

    // Serializes the given fields with the given delimiter
    private static String serialize(Map<String, String> fields, String fieldDelimiter) {
        if (fields == null || fields.isEmpty()) {
            return "";
        }

        String protocolMessage = "";

        for (Map.Entry<String, String> entry : fields.entrySet()) {
            protocolMessage += entry.getKey() + KEY_VALUE_DELIMITER + entry.getValue();
            protocolMessage += fieldDelimiter;
        }

        return removeLastOccurrence(protocolMessage, fieldDelimiter);
    }

    // Parses the given serialized fields into a map of key-value pairs
    private static Map<String, String> parse(String serializedFields, String fieldDelimiter) {
        if  (serializedFields == null || serializedFields.isEmpty()) {
            return new HashMap<>();
        }

        Map<String, String> fields = new HashMap<>();
        String[] parts = serializedFields.split(fieldDelimiter);

        for (String part : parts) {
            String[] keyVal = part.split(KEY_VALUE_DELIMITER, 2);
            if (keyVal.length == 2) {
                fields.put(keyVal[0], keyVal[1]);
            }
        }

        return fields;
    }

    // Serializes the given fields and creates a request message
    public static String serializeRequest(Request request) {
        Map<String, String> fields = new HashMap<>();

        fields.put("action", request.getAction());
        fields.put("serializedRequestContent", request.getSerializedRequestContent());

        return serialize(fields, REQUEST_DELIMITER);
    }

    // Parse the given protocol message into a map of related fields, the first field is action
    public static Request parseRequest(String serializedRequest) {
        Map<String, String> fields = parse(serializedRequest, REQUEST_DELIMITER);

        String action = fields.get("action");
        String serializedRequestContent = fields.get("serializedRequestContent");

        return new Request(action, serializedRequestContent);
    }

    public static String serializeRequestContent(Map<String, String> fields) {
        if (fields == null || fields.isEmpty()) {
            return "";
        }

        return serialize(fields, REQUEST_CONTENT_DELIMITER);
    }

    public static Map<String, String> parseRequestContent(String serializedRequestContent) {
        if  (serializedRequestContent == null || serializedRequestContent.isEmpty()) {
            return new HashMap<>();
        }

        return parse(serializedRequestContent, REQUEST_CONTENT_DELIMITER);
    }

    public static String serializeResponse(Response response) {
        Map<String, String> fields = new HashMap<>();

        fields.put("responseCode", response.getResponseCode().getCode());
        fields.put("serializedResponseContent", response.getSerializedResponseContent());

        return serialize(fields, RESPONSE_DELIMITER);
    }

    public static Response parseResponse(String serializedResponse) {
        Map<String, String> fields = parse(serializedResponse, RESPONSE_DELIMITER);

        ResponseCode responseCode = ResponseCode.fromCode(Integer.parseInt(fields.get("responseCode")));
        String serializedResponseContent = fields.get("serializedResponseContent");

        return new Response(responseCode, serializedResponseContent);
    }

    public static String serializeUser(User user) {
        Map<String, String> fields = new HashMap<>();

        fields.put("username", user.getUsername());
        fields.put("name", user.getName());
        fields.put("surname", user.getSurname());
        fields.put("birthdate", user.getBirthdate());
        fields.put("gender", user.getGender());
        fields.put("email", user.getEmail());
        fields.put("location", user.getLocation());
        fields.put("isAdmin", user.isAdmin() ? "true" : "false");

        return serialize(fields, USER_FIELD_DELIMITER);
    }

    public static User parseUser(String serializedUserFields) {
        Map<String, String> fields = parse(serializedUserFields, USER_FIELD_DELIMITER);

        return new User(fields.get("username"),fields.get("name"), fields.get("surname"),
                fields.get("birthdate"), fields.get("gender"), fields.get("email"),
                fields.get("location"), Boolean.parseBoolean(fields.get("isAdmin")));
    }

    // Sends the user data(sender/receiver) as a serialized model.User object
    // Used in server-side
    public static String serializeMessage(Message message) {
        Map<String, String> fields = new HashMap<>();

        fields.put("sender",  serializeUser(message.getSender()));
        fields.put("receiver",  serializeUser(message.getReceiver()));

        fields.put("title",  message.getTitle());
        fields.put("content",  message.getContent());
        fields.put("time", message.getTime().toString());

        return serialize(fields,  MESSAGE_FIELD_DELIMITER);
    }

    // Retrieves the user data(sender/receiver) as a model.User object
    // Used in client-side
    public static Message parseMessage(String serializedMessageFields) {
        Map<String, String> fields = parse(serializedMessageFields, MESSAGE_FIELD_DELIMITER);

        return new Message(parseUser(fields.get("sender")),
                parseUser(fields.get("receiver")),
                fields.get("title"),
                fields.get("content"),
                Timestamp.valueOf(fields.get("time")));
    }

    private static String serializeMessageBox(ArrayList<Message> messageBox) {
        if  (messageBox == null || messageBox.isEmpty()) {
            return "";
        }

        String serializedMessageBox = "";

        for (Message message : messageBox) {
            serializedMessageBox += serializeMessage(message);
            serializedMessageBox += LINE_DELIMITER;
        }

        return removeLastOccurrence(serializedMessageBox, LINE_DELIMITER);
    }

    public static String serializeInbox(ArrayList<Message> inbox) {
        return serializeMessageBox(inbox);
    }

    public static String serializeOutbox(ArrayList<Message> outbox) {
        return serializeMessageBox(outbox);
    }

    private static ArrayList<Message> parseMessageBox(String serializedMessageBox) {
        if  (serializedMessageBox == null || serializedMessageBox.isEmpty()) {
            return new ArrayList<>();
        }

        ArrayList<Message> messageBox = new ArrayList<>();
        String[] lines = serializedMessageBox.split(LINE_DELIMITER);

        for (String serializedMessage : lines) {
            messageBox.add(parseMessage(serializedMessage));
        }

        return  messageBox;
    }

    public static ArrayList<Message> parseInbox(String serializedInbox) {
        return parseMessageBox(serializedInbox);
    }

    public static ArrayList<Message> parseOutbox(String serializedOutbox) {
        return parseMessageBox(serializedOutbox);
    }

    public static String serializeUsersList(ArrayList<User> usersList) {
        if  (usersList == null || usersList.isEmpty()) {
            return "";
        }

        String serializedUsersList = "";

        for (User user : usersList) {
            serializedUsersList += serializeUser(user);
            serializedUsersList += LINE_DELIMITER;
        }

        return removeLastOccurrence(serializedUsersList, LINE_DELIMITER);
    }

    public static ArrayList<User> parseUsersList(String serializedUsersList) {
        if   (serializedUsersList == null || serializedUsersList.isEmpty()) {
            return new ArrayList<>();
        }

        ArrayList<User> usersList = new ArrayList<>();
        String[] lines = serializedUsersList.split(LINE_DELIMITER);

        for (String serializedUser : lines) {
            usersList.add(parseUser(serializedUser));
        }

        return usersList;
    }

    public static ArrayList<String> getReservedDelimiters() {
        ArrayList<String> delimiters = new ArrayList<>();

        delimiters.add(LINE_DELIMITER);
        delimiters.add(REQUEST_DELIMITER);
        delimiters.add(REQUEST_CONTENT_DELIMITER);
        delimiters.add(RESPONSE_DELIMITER);
        delimiters.add(USER_FIELD_DELIMITER);
        delimiters.add(MESSAGE_FIELD_DELIMITER);
        delimiters.add(KEY_VALUE_DELIMITER);

        return delimiters;
    }
}
