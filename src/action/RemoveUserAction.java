package action;

import client.Client;
import model.User;
import response.Response;
import response.ResponseCode;
import server.ClientHandler;
import utility.ProtocolHandler;

import java.sql.SQLException;

public class RemoveUserAction extends Action {
    private final String userToRemove;

    public RemoveUserAction(String userToRemove, ClientHandler clientHandler) {
        super(clientHandler);

        this.userToRemove = userToRemove;
    }

    @Override
    public void execute() {
        if (serverResponseCode == ResponseCode.SUCCESS) {
            try {
                db.removeUser(userToRemove);

                ClientHandler handlerToRemove = ClientHandler.getHandler(userToRemove);
                if (handlerToRemove != null) {
                    // If we don't start a new thread, the admin who removed the user
                    // cannot perform any actions until the removed user types something on the console.
                    // Because the thread of the admin now waits a response from removed user's Client.
                    new Thread(() -> {
                        try {
                            handlerToRemove.sendMessageToClient(
                                    ProtocolHandler.serializeResponse(new Response(ResponseCode.REMOVED_ACCOUNT, ""))
                            );
                            // Give client time to process
                            Thread.sleep(500);
                        } catch (Exception ignored) {
                        } finally {
                            handlerToRemove.closeEverything(
                                    handlerToRemove.getSocket(),
                                    handlerToRemove.getBufferedReader(),
                                    handlerToRemove.getBufferedWriter()
                            );
                        }
                    }).start();
                }

            } catch (SQLException e) {
                System.err.println(e.getMessage());
                serverResponseCode = ResponseCode.ERROR;
            }
        }
    }

    @Override
    public void validate() {
        try {
            if (!db.doesUserExist(userToRemove)) {
                serverResponseCode = ResponseCode.NOT_FOUND;
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
