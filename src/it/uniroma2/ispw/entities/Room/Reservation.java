package it.uniroma2.ispw.entities.Room;

import java.util.Date;

public class Reservation {

    private String eventID;
    private Date startDateTime;
    private Date endDateTime;

    public Reservation(String eventID, Date startDateTime, Date endDateTime) {
        this.eventID       = eventID;
        this.startDateTime = startDateTime;
        this.endDateTime   = endDateTime;
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
