package it.uniroma2.ispw.spotlight.entities.Room;

import java.util.ArrayList;

/**
 * This class can be used to represent a Room
 */
public class Room {

    private String roomID;
    private String roomName;
    private String roomDepartment;
    private ArrayList<Reservation> reservations;
    private RoomProperties properties;

    /**
     * A room is defined as follows
     * @param roomID String, unique
     * @param roomName String
     * @param roomDepartment String
     * @param properties RoomProperties
     */
    public Room(String roomID, String roomName, String roomDepartment, RoomProperties properties) {
        this.roomID         = roomID;
        this.roomName       = roomName;
        this.roomDepartment = roomDepartment;
        this.properties     = properties;

        // initializing the array of reservations
        this.reservations = new ArrayList<>();
    }

    /**
     * Add a new Reservation associated to the room
     * @param reservation Reservation
     */
    public void addReservation(Reservation reservation) {
        getReservations().add(reservation);
    }

    /**
     * Delete a Reservation previously associated to the room
     * @param reservation Reservation
     */
    public void delReservation(Reservation reservation) {
        getReservations().remove(reservation);
    }

    /**
     * Delete all the reservations associated to the room
     */
    public void clearReservation() {
        getReservations().clear();
    }

    /**
     * Return unique identifier of the room
     * @return String
     */
    public String getRoomID() {
        return roomID;
    }

    /**
     * Return the room canonical
     * @return String
     */
    public String getRoomName() {
        return roomName;
    }

    /**
     * Return the room department
     * @return String
     */
    public String getRoomDepartment() {
        return roomDepartment;
    }

    /**
     * Return the room properties
     * @return RoomProperties
     */
    public RoomProperties getProperties() {
        return properties;
    }

    /**
     * Return the list of all the associated reservations
     * @return ArrayList<Reservation>
     */
    public ArrayList<Reservation> getReservations() {
        return reservations;
    }

}
