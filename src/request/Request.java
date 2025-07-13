package request;

import client.Client;
import server.ClientHandler;
import utility.ProtocolHandler;

public class Request {
    private final String action;
    private final String serializedRequestContent;

    public Request(String action, String serializedRequestContent) {
        this.action = action;
        this.serializedRequestContent = serializedRequestContent;
    }

    public String getAction() {
        return action;
    }

    public String getSerializedRequestContent() {
        return serializedRequestContent;
    }

    // Sends the request to server
    public void send(Client client) {
        String serializedRequest = ProtocolHandler.serializeRequest(this);

        // Send the serialized response to client
        client.sendMessageToServer(serializedRequest);
    }
}
