package action;

import model.User;
import response.ResponseCode;
import server.ClientHandler;

import java.sql.SQLException;
import java.util.Map;

public class UpdateUserAction extends Action {
    private final Map<String, String> userFields;
    public UpdateUserAction(Map<String, String> userFields, ClientHandler clientHandler) {
        super(clientHandler);

        this.userFields = userFields;
    }

    @Override
    public void execute() {
        if (serverResponseCode == ResponseCode.SUCCESS) {
            try {
                User updatedUser = new User(userFields.get("newUsername"), userFields.get("name"),
                        userFields.get("surname"), userFields.get("birthdate"),
                        userFields.get("gender"), userFields.get("email"),
                        userFields.get("location"),
                        Boolean.parseBoolean(userFields.get("isAdmin")));

                db.updateUser(userFields.get("username"), updatedUser, userFields.get("password"));
            } catch (SQLException e) {
                System.err.println(e.getMessage());
                serverResponseCode = ResponseCode.ERROR;
            }
        }
    }

    @Override
    public void validate() {
        try {
            if (!db.doesUserExist(userFields.get("username"))) {
                serverResponseCode = ResponseCode.NOT_FOUND;
                return;
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
            serverResponseCode = ResponseCode.ERROR;
            return;
        }

        try {
            if (!userFields.get("username").equals(userFields.get("newUsername")) && db.doesUserExist(userFields.get("newUsername"))) {
                serverResponseCode = ResponseCode.USERNAME_ALREADY_EXISTS;
                return;
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
            serverResponseCode = ResponseCode.ERROR;
            return;
        }

        serverResponseCode = ResponseCode.SUCCESS;
    }
}
