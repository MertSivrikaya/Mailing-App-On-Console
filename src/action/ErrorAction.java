package action;

import response.ResponseCode;
import server.ClientHandler;

public class ErrorAction extends Action {

    public ErrorAction(ClientHandler clientHandler) {
        super(clientHandler);
        serverResponseCode = ResponseCode.INVALID_FIELD_VALUES;
    }

    @Override
    public void execute() {
        return;
    }

    @Override
    public void validate() {
        return;
    }


}
