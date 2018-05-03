package it.uniroma2.ispw.spotlight.exceptions;

public class RoomServiceException extends Exception {
    public static final String MSG_IDENTIFIER = "[ROOMSERVICE]";
    public RoomServiceException(String message) {
        super(MSG_IDENTIFIER + message);
    }
}