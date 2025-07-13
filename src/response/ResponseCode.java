package response;

public enum ResponseCode {
    SUCCESS(200, "success"),
    LOGIN_FAIL(201, "login failed"), // Wrong password
    LOGOUT_FAIL(301, "logout failed"),
    UNAUTHORIZED(401, "unauthorized"),
    NOT_FOUND(404, "not found"),
    INVALID_FIELD_VALUES(501, "invalid field values"),
    USERNAME_ALREADY_EXISTS(502, "username already exists"),
    REMOVED_ACCOUNT(503, "removed account"),
    ERROR(503, "error"),
    EXIT(601, "exit");

    private final int code;
    private final String message;

    ResponseCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() { return String.valueOf(code); }
    public String getMessage() { return message; }

    @Override
    public String toString() {
        return code + " " + message;
    }

    public static ResponseCode fromCode(int code) {
        for (ResponseCode rc : values()) {
            if (rc.code == code) return rc;
        }
        return ERROR;  // or throw exception
    }
}
