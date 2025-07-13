package response;

import client.Client;
import model.Message;
import model.User;
import utility.ConsoleUI;
import utility.ProtocolHandler;

import java.util.ArrayList;

// Class for handling the response client receives from the sever
public class ResponseHandler {
    private final Client client;

    public ResponseHandler(Client client) {
        this.client = client;
    }

    public void handleLoginResponse(Response serverResponse) {
        ResponseCode responseCode = serverResponse.getResponseCode();
        String serializedResponseContent = serverResponse.getSerializedResponseContent();

        if (responseCode == ResponseCode.SUCCESS) {
            client.setLoggedIn(true);
            client.setUser(ProtocolHandler.parseUser(serializedResponseContent));

            System.out.println("Login Successful. Welcome " + client.getUser().getUsername());
        } else if  (responseCode == ResponseCode.LOGIN_FAIL) {
            System.out.println("Login Failed. Please try again with the correct password.");
        } else if (responseCode == ResponseCode.NOT_FOUND) {
            System.out.println("Login Failed. There is no user with that username.");
        }
    }

    public void handleInboxResponse(Response serverResponse) {
        ResponseCode responseCode = serverResponse.getResponseCode();
        String serializedResponseContent = serverResponse.getSerializedResponseContent();

        if (responseCode ==  ResponseCode.SUCCESS) {
            ArrayList<Message> inbox = ProtocolHandler.parseInbox(serializedResponseContent);
            ConsoleUI.displayInbox(inbox);

        } else if  (responseCode ==  ResponseCode.ERROR) {
            System.out.println("Something went wrong. Please try again.");
        }
    }

    public void handleOutboxResponse(Response serverResponse) {
        ResponseCode responseCode = serverResponse.getResponseCode();
        String serializedResponseContent = serverResponse.getSerializedResponseContent();

        if (responseCode ==  ResponseCode.SUCCESS) {
            ArrayList<Message> outbox = ProtocolHandler.parseOutbox(serializedResponseContent);
            ConsoleUI.displayOutbox(outbox);
        } else if  (responseCode ==  ResponseCode.ERROR) {
            System.out.println("Something went wrong. Please try again.");
        }
    }

    public void handleSendMessageResponse(Response serverResponse) {
        ResponseCode responseCode = serverResponse.getResponseCode();

        if (responseCode == ResponseCode.SUCCESS) {
            System.out.println("Message sent successfully.");
        } else if  (responseCode == ResponseCode.NOT_FOUND) {
            System.out.println("Action failed. Error: " + responseCode);
        } else if (responseCode == ResponseCode.INVALID_FIELD_VALUES) {
            System.out.println("Action failed. Error: " + responseCode);
        } else if (responseCode == ResponseCode.ERROR) {
            System.out.println("Something went wrong. Please try again.");
        }
    }

    public void handleLogoutResponse(Response serverResponse) {
        ResponseCode responseCode = serverResponse.getResponseCode();

        if (responseCode ==  ResponseCode.SUCCESS) {
            client.setUser(null);
            client.setLoggedIn(false);

            System.out.println("Logout Successful");
        } else {
            System.out.println("Something went wrong. Please try again.");
        }
    }

    public void handleAddUserResponse(Response serverResponse) {
        ResponseCode responseCode = serverResponse.getResponseCode();

        if (responseCode ==  ResponseCode.SUCCESS) {
            System.out.println("User has been added successfully.");
        } else if (responseCode ==  ResponseCode.INVALID_FIELD_VALUES) {
            System.out.println("Action failed. Error: " + responseCode);
        } else if (responseCode ==  ResponseCode.USERNAME_ALREADY_EXISTS) {
            System.out.println("Action failed. Error: " + responseCode);
        } else if (responseCode == ResponseCode.ERROR) {
            System.out.println("Something went wrong. Please try again.");
        }
    }

    public void handleUpdateUserResponse(Response serverResponse) {
        ResponseCode responseCode = serverResponse.getResponseCode();

        if (responseCode ==  ResponseCode.SUCCESS) {
            System.out.println("User has been updated successfully.");
        } else if (responseCode ==  ResponseCode.NOT_FOUND) {
            System.out.println("Action failed. Error: " + responseCode);
        } else if (responseCode ==  ResponseCode.USERNAME_ALREADY_EXISTS) {
            System.out.println("Action failed. Error: " + responseCode);
        } else if (responseCode == ResponseCode.ERROR) {
            System.out.println("Something went wrong. Please try again.");
        }
    }

    public void handleRemoveUserResponse(Response serverResponse) {
        ResponseCode responseCode = serverResponse.getResponseCode();

        if (responseCode ==  ResponseCode.SUCCESS) {
            System.out.println("User has been removed successfully.");
        } else if (responseCode ==  ResponseCode.NOT_FOUND) {
            System.out.println("Action failed. Error: " + responseCode);
        } else if (responseCode == ResponseCode.ERROR) {
            System.out.println("Something went wrong. Please try again.");
        }
    }

    public void handleListUsersResponse(Response serverResponse) {
        ResponseCode responseCode = serverResponse.getResponseCode();
        String serializedResponseContent = serverResponse.getSerializedResponseContent();

        if (responseCode ==  ResponseCode.SUCCESS) {
            ArrayList<User> usersList = ProtocolHandler.parseUsersList(serializedResponseContent);
            ConsoleUI.displayUsersList(usersList);
        } else if  (responseCode ==  ResponseCode.ERROR) {
            System.out.println("Something went wrong. Please try again.");
        }
    }
}
