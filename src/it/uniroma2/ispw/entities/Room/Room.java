package it.uniroma2.ispw.entities.Room;

import java.util.ArrayList;
import java.util.function.Consumer;

public class Room {

    private String roomID;
    private String roomName;
    private String roomDepartment;
    private ArrayList<Reservation> reservations;
    private RoomProperties properties;

    public Room(String roomID, String roomName, String roomDepartment, RoomProperties properties) {
        this.roomID         = roomID;
        this.roomName       = roomName;
        this.roomDepartment = roomDepartment;
        this.properties     = properties;

        // initializing the array of reservations
        this.reservations = new ArrayList<>();
    }

    public void addReservation(Reservation reservation) {
        getReservations().add(reservation);
    }

    public void delReservation(Reservation reservation) {
        getReservations().remove(reservation);
    }

    public void clearReservation() {
        getReservations().clear();
    }

    public String getRoomID() {
        return roomID;
    }

    public String getRoomName() {
        return roomName;
    }

    public String getRoomDepartment() {
        return roomDepartment;
    }

    public RoomProperties getProperties() {
        return properties;
    }

    public ArrayList<Reservation> getReservations() {
        return reservations;
    }

}
