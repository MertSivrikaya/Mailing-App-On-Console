package action;

import model.Message;
import model.User;
import response.ResponseCode;
import server.ClientHandler;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Map;

public class SendMessageAction extends Action {
    private final Map<String, String> messageFields;

    public SendMessageAction(Map<String, String> messageFields, ClientHandler clientHandler) {
        super(clientHandler);

        this.messageFields = messageFields;
    }

    @Override
    public void execute() {
        if (serverResponseCode == ResponseCode.SUCCESS) {
            try {
                User sender = db.getUserWithUsername(messageFields.get("sender"));
                User receiver = db.getUserWithUsername(messageFields.get("receiver"));

                Message msgToSend = new Message(sender, receiver,
                        messageFields.get("title"), messageFields.get("content"),
                        Timestamp.valueOf(messageFields.get("time")));

                db.insertMessage(msgToSend);
            } catch (SQLException e) {
                System.err.println(e.getMessage());
                serverResponseCode = ResponseCode.ERROR;
            }
        }
    }

    @Override
    public void validate() { // check if special characters are in them
        if (messageFields.get("sender") == null || messageFields.get("receiver") == null) {
            serverResponseCode = ResponseCode.INVALID_FIELD_VALUES;
            return;
        }

        if (messageFields.get("sender").isEmpty() || messageFields.get("receiver").isEmpty()) {
            serverResponseCode = ResponseCode.INVALID_FIELD_VALUES;
            return;
        }

        if (messageFields.get("title") == null || messageFields.get("content") == null) {
            serverResponseCode = ResponseCode.INVALID_FIELD_VALUES;
            return;
        }

        // Cannot be empty at the same time
        if (messageFields.get("title").isEmpty() && messageFields.get("content").isEmpty()) {
            serverResponseCode = ResponseCode.INVALID_FIELD_VALUES;
            return;
        }

        try {
            if (!db.doesUserExist(messageFields.get("sender")) ||
                !db.doesUserExist(messageFields.get("receiver"))) {
                serverResponseCode = ResponseCode.NOT_FOUND;
                return;
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            serverResponseCode = ResponseCode.ERROR;
            return;
        }

        // Fields cannot contain delimiters reserved for serializing/parsing
        if (containsReservedDelimiter(messageFields.get("sender")) ||
            containsReservedDelimiter(messageFields.get("receiver")) ||
            containsReservedDelimiter(messageFields.get("title")) ||
            containsReservedDelimiter(messageFields.get("content")) ||
            containsReservedDelimiter(messageFields.get("time"))) {
            serverResponseCode = ResponseCode.INVALID_FIELD_VALUES;
            return;
        }

        serverResponseCode = ResponseCode.SUCCESS;
    }
}
