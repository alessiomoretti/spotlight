package it.uniroma2.ispw.spotlight.entities;

import it.uniroma2.ispw.spotlight.entities.Room.Room;
import it.uniroma2.ispw.spotlight.users.User;

import java.util.ArrayList;
import java.util.Date;

/**
 * This class can be used to represent an Event inside the system
 */
public class Event {

    private String eventID;
    private String eventName;
    private Date startDateTime;
    private Date endDateTime;
    private User referral;
    private String referralName;
    private String emailDL;
    private ArrayList<Room> reservedRooms;
    private Integer reservations;

    /**
     * An event is defined as per the following parameters
     * @param eventID String, unique
     * @param eventName String
     * @param startDateTime Date
     * @param endDateTime Date
     * @param referral User
     * @param emailDL String, the event distribution list
     */
    public Event(String eventID, String eventName, Date startDateTime, Date endDateTime, User referral, String emailDL) {
        this.eventID       = eventID;
        this.eventName     = eventName;
        this.startDateTime = startDateTime;
        this.endDateTime   = endDateTime;
        this.referral      = referral;
        this.referralName  = referral.getName();
        this.emailDL       = emailDL;

        // initializing array of reserved Rooms
        this.reservedRooms = new ArrayList<>();
    }

    public Event(String eventID, String eventName, User referral) {
        this.eventID   = eventID;
        this.eventName = eventName;
        this.referral  = referral;
    }

    /**
     * To add a new reserved room from the event representation
     * @param room Room
     */
    public void addReservedRoom(Room room) {
        getReservedRooms().add(room);
        this.reservations += 1;
    }

    /**
     * To delete a reserved room from the event representation
     * @param room Room
     */
    public void delReservedRoom(Room room) {
        getReservedRooms().remove(room);
        this.reservations -= 1;
    }

    /**
     * To remove all reserved rooms from the event representation
     */
    public void clearReservedRooms() {
        getReservedRooms().clear();
    }

    /**
     * Return the event unique identifier (created on db insertion)
     * @return String
     */
    public String getEventID() {
        return eventID;
    }

    /**
     * Set the event name
     * @param eventName String
     */
    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    /**
     * Return the event name
     * @return
     */
    public String getEventName() {
        return eventName;
    }

    /**
     * Set event references as name and identifier
     * @param eventName String
     * @param eventID string
     */
    public void setEventReference(String eventName, String eventID) {
        this.eventName = eventName;
        this.eventID   = eventID;
    }

    /**
     * Return event start Date
     * @return Date
     */
    public Date getStartDateTime() {
        return startDateTime;
    }

    /**
     * Set event start date
     * @param startDateTime Date
     */
    public void setStartDateTime(Date startDateTime) {
        this.startDateTime = startDateTime;
    }

    /**
     * Return event end Date
     * @return Date
     */
    public Date getEndDateTime() {
        return endDateTime;
    }

    /**
     * Set event end Date
     * @param endDateTime Date
     */
    public void setEndDateTime(Date endDateTime) {
        this.endDateTime = endDateTime;
    }

    /**
     * Return event referral user
     * @return User
     */
    public User getReferral() {
        return referral;
    }

    /**
     * Set event referral parameters user
     * @param referral User
     */
    public void setReferral(User referral) {
        this.referral = referral;
        this.referralName = this.referral.getName();
    }

    /**
     * Return event referral user name
     * @return String
     */
    public String getReferralName() { return this.referralName; }

    /**
     * Return event distribution list
     * @return String
     */
    public String getEmailDL() {
        return emailDL;
    }

    /**
     * Set event distribution list
     * @param emailDL
     */
    public void setEmailDL(String emailDL) {
        this.emailDL = emailDL;
    }

    /**
     * Return the array of the reserved rooms associated with this event
     * @return ArrayList<Room>
     */
    public ArrayList<Room> getReservedRooms() {
        return reservedRooms;
    }

    /**
     * Return the number of current reservations
     * @return Integer
     */
    public Integer getReservations() {
        this.reservations = getReservedRooms().size();
        return this.reservations;
    }

}
