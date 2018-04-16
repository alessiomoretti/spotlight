package it.uniroma2.ispw.entities.Room;

import java.util.Date;

public class Reservation {

    private String reservationID;
    private String roomID;
    private String eventID;
    private Date startDateTime;
    private Date endDateTime;

    public Reservation(String reservationID, String roomID, String eventID, Date startDateTime, Date endDateTime) {
        this.reservationID = reservationID;
        this.roomID        = roomID;
        this.eventID       = eventID;
        this.startDateTime = startDateTime;
        this.endDateTime   = endDateTime;
    }

    public String getReservationID() {
        return reservationID;
    }

    public void setReservationID(String reservationID) {
        this.reservationID = reservationID;
    }

    public String getRoomID() {
        return roomID;
    }

    public void setRoomID(String roomID) {
        this.roomID = roomID;
    }

    public String getEventID() {
        return eventID;
    }

    public void setEventID(String eventID) {
        this.eventID = eventID;
    }

    public Date getStartDateTime() {
        return startDateTime;
    }

    public void setStartDateTime(Date startDateTime) {
        this.startDateTime = startDateTime;
    }

    public Date getEndDateTime() {
        return endDateTime;
    }

    public void setEndDateTime(Date endDateTime) {
        this.endDateTime = endDateTime;
    }
}
