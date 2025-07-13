package action;

import response.ResponseCode;
import server.ClientHandler;
import utility.ProtocolHandler;

import java.sql.SQLException;

public class LoginAction extends Action {
    private final String usernameEntered;
    private final String passwordEntered;

    public LoginAction(String usernameEntered, String passwordEntered, ClientHandler clientHandler) {
        super(clientHandler);

        this.usernameEntered = usernameEntered;
        this.passwordEntered = passwordEntered;
    }

    @Override
    public void execute() {
        if (serverResponseCode == ResponseCode.SUCCESS) {
            try {
                clientHandler.setHandledUser(db.getUserWithUsername(usernameEntered));
                clientHandler.setLoggedIn(true);

                ClientHandler.registerUser(usernameEntered, clientHandler);

                serializedResponseContent = ProtocolHandler.serializeUser(clientHandler.getHandledUser());
            } catch (SQLException e) {
                System.err.println(e.getMessage());
                serverResponseCode = ResponseCode.ERROR;
            }
        }
    }

    @Override
    public void validate() {
        if (usernameEntered == null || passwordEntered == null) {
            serverResponseCode = ResponseCode.INVALID_FIELD_VALUES;
            return;
        }
        if (usernameEntered.isEmpty() || passwordEntered.isEmpty()) {
            serverResponseCode = ResponseCode.INVALID_FIELD_VALUES;
            return;
        }

        try {
            if (!db.doesUserExist(usernameEntered)) {
                serverResponseCode = ResponseCode.NOT_FOUND;
                return;
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            serverResponseCode = ResponseCode.ERROR;
            return;
        }

        try {
            if (db.authenticateUser(usernameEntered, passwordEntered)) {
                serverResponseCode = ResponseCode.SUCCESS;
                return;
            } else {
                serverResponseCode = ResponseCode.LOGIN_FAIL;
                return;
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            serverResponseCode = ResponseCode.ERROR;
            return;
        }
    }


}
