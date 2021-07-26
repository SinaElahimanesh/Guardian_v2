package ir.guardianapp.guardian_v2.network;

public class MessageResult {
    // the whole
    public static final int SUCCESSFUL = 1;
    public static final int FAILED = 2;

    // register
    public static final int USERNAME_IS_NOT_UNIQUE = 3;
    public static final int PHONE_IS_NOT_UNIQUE = 4;

    // login
    public static final int THIS_PHONE_NOT_REGISTERED = 5;
    public static final int PHONE_AND_PASSWORD_DOES_NOT_MATCH = 6;

    // credentials
    public static final int VERSION_IS_LESS_THAN_MINIMUM = 7;
    public static final int LOGGED_OUT = 8;
    public static final int LOGGED_IN = 9;

}
