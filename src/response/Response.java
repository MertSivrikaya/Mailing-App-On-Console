package response;

import server.ClientHandler;
import utility.ProtocolHandler;

public class Response {
    private final ResponseCode responseCode;
    private final String serializedResponseContent;

    public Response(ResponseCode responseCode, String serializedResponseContent) {
        this.responseCode = responseCode;
        this.serializedResponseContent = serializedResponseContent;
    }

    public ResponseCode getResponseCode() {
        return responseCode;
    }

    public String getSerializedResponseContent() {
        return serializedResponseContent;
    }

    // Sends the response to client
    public void send(ClientHandler clientHandler) {
        String serializedResponse = ProtocolHandler.serializeResponse(this);

        // Send the serialized response to client
        clientHandler.sendMessageToClient(serializedResponse);
    }
}
