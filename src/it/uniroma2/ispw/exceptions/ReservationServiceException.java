package it.uniroma2.ispw.exceptions;

public class ReservationServiceException extends Exception {
    public static final String MSG_IDENTIFIER = "[RESERVATION]";
    public ReservationServiceException(String message) {
        super(MSG_IDENTIFIER + message);
    }
}
