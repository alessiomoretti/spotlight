package it.uniroma2.ispw.exceptions;

public class UserRetrievalException extends Exception {
    public static final String MSG_IDENTIFIER = "[USERRETRIEVAL]";
    public UserRetrievalException(String message) {
        super(MSG_IDENTIFIER + message);
    }
}
