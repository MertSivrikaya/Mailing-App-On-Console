package action;

import model.User;
import response.ResponseCode;
import server.ClientHandler;
import utility.ProtocolHandler;

import java.sql.SQLException;
import java.util.ArrayList;

public class ListUsersAction extends Action {
    public ListUsersAction(ClientHandler clientHandler) {
        super(clientHandler);
    }

    @Override
    public void execute() {
        if (serverResponseCode == ResponseCode.SUCCESS) {
            try {
                ArrayList<User> usersList = db.getUsersList();
                serializedResponseContent = ProtocolHandler.serializeUsersList(usersList);
            } catch (SQLException e) {
                System.err.println(e.getMessage());
                serverResponseCode = ResponseCode.ERROR;
            }
        }
    }

    @Override
    public void validate() {
        serverResponseCode = ResponseCode.SUCCESS;
    }
}
