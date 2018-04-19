package it.uniroma2.ispw.spotlight.entities;

import it.uniroma2.ispw.spotlight.entities.Room.Room;
import it.uniroma2.ispw.spotlight.users.User;

import java.util.ArrayList;
import java.util.Date;

public class Event {


    private String eventID;
    private String eventName;
    private Date startDateTime;
    private Date endDateTime;
    private User referral;
    private String emailDL;
    private ArrayList<Room> reservedRooms;

    public Event(String eventID, String eventName, Date startDateTime, Date endDateTime, User referral, String emailDL) {
        this.eventID       = eventID;
        this.eventName     = eventName;
        this.startDateTime = startDateTime;
        this.endDateTime   = endDateTime;
        this.referral      = referral;
        this.emailDL       = emailDL;

        // initializing array of reserved Rooms
        this.reservedRooms = new ArrayList<>();
    }

    public void addReservedRoom(Room room) {
        getReservedRooms().add(room);
    }

    public void delReservedRoom(Room room) {
        getReservedRooms().remove(room);
    }

    public void clearReservedRooms(Room room) {
        getReservedRooms().clear();
    }

    public String getEventID() {
        return eventID;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventReference(String eventName, String eventID) {
        this.eventName = eventName;
        this.eventID   = eventID;
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

    public User getReferral() {
        return referral;
    }

    public void setReferral(User referral) {
        this.referral = referral;
    }

    public String getEmailDL() {
        return emailDL;
    }

    public void setEmailDL(String emailDL) {
        this.emailDL = emailDL;
    }

    public ArrayList<Room> getReservedRooms() {
        return reservedRooms;
    }

}
