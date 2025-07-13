package action;

import server.ClientHandler;
import database.DB;
import response.Response;
import response.ResponseCode;
import utility.ProtocolHandler;

public abstract class Action {
    protected final DB db;
    protected ClientHandler clientHandler;

    protected ResponseCode serverResponseCode;
    protected String serializedResponseContent;

    protected Action(ClientHandler clientHandler) {
        this.clientHandler = clientHandler;
        this.db = clientHandler.getDB();

        // default values
        serverResponseCode = ResponseCode.ERROR;
        serializedResponseContent = "";
    }

    public abstract void validate();
    public abstract void execute();

    public void validateAndExecute() {
        validate();
        execute();

        // Create the response
        Response serverResponse = new Response(serverResponseCode, serializedResponseContent);

        // Send the response to client
        serverResponse.send(clientHandler);
    }

    protected boolean containsReservedDelimiter(String target) {
        boolean containsReservedDelimiter = false;

        for (String keyword : ProtocolHandler.getReservedDelimiters()) {
            if (target.contains(keyword)) {
                containsReservedDelimiter = true;
                break;
            }
        }

        return containsReservedDelimiter;
    }
}
