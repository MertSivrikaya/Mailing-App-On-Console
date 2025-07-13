package action;

import response.ResponseCode;
import server.ClientHandler;

public class LogoutAction extends Action {
    private final String username;

    public LogoutAction(String username, ClientHandler clientHandler) {
        super(clientHandler);

        this.username = username;
    }

    @Override
    public void execute() {
        if (serverResponseCode == ResponseCode.SUCCESS) {
            clientHandler.setHandledUser(null);
            clientHandler.setLoggedIn(false);

            ClientHandler.unregisterUser(username);
        }
    }

    @Override
    public void validate() {
        if (!clientHandler.getLoggedIn()) {
            serverResponseCode = ResponseCode.LOGOUT_FAIL;
            return;
        }

        serverResponseCode = ResponseCode.SUCCESS;
    }
}
