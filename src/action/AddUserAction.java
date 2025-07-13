package action;

import model.User;
import response.ResponseCode;
import server.ClientHandler;

import java.sql.SQLException;
import java.util.Map;

public class AddUserAction extends Action {
    private final Map<String, String> userFields;
    public AddUserAction(Map<String, String> userFields, ClientHandler clientHandler) {
        super(clientHandler);

        this.userFields = userFields;
    }
    @Override
    public void execute() {
        if (serverResponseCode == ResponseCode.SUCCESS) {
            try {
                User userToInsert = new User(userFields.get("username"), userFields.get("name"),
                        userFields.get("surname"), userFields.get("birthdate"),
                        userFields.get("gender"), userFields.get("email"),
                        userFields.get("location"),
                        Boolean.parseBoolean(userFields.get("isAdmin")));

                db.insertUser(userToInsert, userFields.get("password"));
            } catch (SQLException e) {
                System.err.println(e.getMessage());
                serverResponseCode = ResponseCode.ERROR;
            }
        }
    }

    @Override
    public void validate() {
        try {
            if (db.doesUserExist(userFields.get("username"))) {
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
