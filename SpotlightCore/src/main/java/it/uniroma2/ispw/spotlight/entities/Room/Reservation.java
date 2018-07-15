package it.uniroma2.ispw.spotlight.entities.Room;

import java.util.Date;

/**
 * This class can be used to represent logically a room reservation
 */
public class Reservation {

    private String reservationID;
    private String roomID;
    private String eventID;
    private String referral;
    private Date startDateTime;
    private Date endDateTime;

    /**
     * A reservation is defined by the following capabilities
     * @param reservationID String, unique
     * @param roomID String, unique
     * @param eventID String, unique
     * @param referral String, username
     * @param startDateTime Date
     * @param endDateTime Date
     */
    public Reservation(String reservationID, String roomID, String eventID, String referral, Date startDateTime, Date endDateTime) {
        this.reservationID = reservationID;
        this.roomID        = roomID;
        this.eventID       = eventID;
        this.referral      = referral;
        this.startDateTime = startDateTime;
        this.endDateTime   = endDateTime;
    }

    /**
     * Return the unique reservation identifier
     * @return String
     */
    public String getReservationID() {
        return reservationID;
    }

    /**
     * Set a unique reservation identifier
     * @param reservationID String
     */
    public void setReservationID(String reservationID) {
        this.reservationID = reservationID;
    }

    /**
     * Return the unique room identifier
     * @return String
     */
    public String getRoomID() {
        return roomID;
    }

    /**
     * Set the unique room identifier
     * @param roomID String
     */
    public void setRoomID(String roomID) {
        this.roomID = roomID;
    }

    /**
     * Return the unique event identifier
     * @return String
     */
    public String getEventID() {
        return eventID;
    }

    /**
     * Set a unique event identifier
     * @param eventID String
     */
    public void setEventID(String eventID) {
        this.eventID = eventID;
    }

    /**
     * Return the reservation referral username
     * @return String
     */
    public String getReferral() {
        return referral;
    }

    /**
     * Set the reservation referral username
     * @param referral String
     */
    public void setReferral(String referral) {
        this.referral = referral;
    }

    /**
     * Return the reservation start date
     * @return Date
     */
    public Date getStartDateTime() {
        return startDateTime;
    }

    /**
     * Set the reservation start date
     * @param startDateTime Date
     */
    public void setStartDateTime(Date startDateTime) {
        this.startDateTime = startDateTime;
    }

    /**
     * Return the reservation end date
     * @return Date
     */
    public Date getEndDateTime() {
        return endDateTime;
    }

    /**
     * Set the reservation end date
     * @param endDateTime Date
     */
    public void setEndDateTime(Date endDateTime) {
        this.endDateTime = endDateTime;
    }
}
