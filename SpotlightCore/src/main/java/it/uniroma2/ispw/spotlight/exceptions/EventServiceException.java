package it.uniroma2.ispw.spotlight.exceptions;

public class EventServiceException extends Exception {
    public static final String MSG_IDENTIFIER = "[EVENTSERVICE]";
    public EventServiceException(String message) {
        super(MSG_IDENTIFIER + message);
    }
}
