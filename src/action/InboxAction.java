package action;

import model.Message;
import response.ResponseCode;
import server.ClientHandler;
import utility.ProtocolHandler;

import java.sql.SQLException;
import java.util.ArrayList;

public class InboxAction extends Action {
    public InboxAction(ClientHandler clientHandler) {
        super(clientHandler);
    }

    @Override
    public void execute() {
        if (serverResponseCode == ResponseCode.SUCCESS) {
            try {
                ArrayList<Message> inbox = db.getInboxOfUser(clientHandler.getHandledUser());
                serializedResponseContent = ProtocolHandler.serializeInbox(inbox);
            } catch (SQLException e) {
                System.err.println(e.getMessage());
                serverResponseCode = ResponseCode.ERROR;
            }
        }
    }

    @Override
    public void validate() {
        if (clientHandler.getHandledUser() == null) {
            serverResponseCode = ResponseCode.ERROR;
            return;
        }

        serverResponseCode = ResponseCode.SUCCESS;
    }
}
