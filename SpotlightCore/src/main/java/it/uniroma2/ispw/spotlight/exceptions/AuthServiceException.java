package it.uniroma2.ispw.spotlight.exceptions;

public class AuthServiceException extends Exception {
    public static final String MSG_IDENTIFIER = "[AUTHSERVICE]";
    public AuthServiceException(String message) {
        super(MSG_IDENTIFIER + message);
    }
}
