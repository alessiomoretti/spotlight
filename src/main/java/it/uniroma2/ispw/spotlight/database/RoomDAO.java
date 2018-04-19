package it.uniroma2.ispw.spotlight.database;

import it.uniroma2.ispw.spotlight.entities.Room.Reservation;
import it.uniroma2.ispw.spotlight.entities.Room.Room;
import it.uniroma2.ispw.spotlight.entities.Room.RoomProperties;
import it.uniroma2.ispw.spotlight.exceptions.RoomServiceException;
import it.uniroma2.ispw.spotlight.exceptions.ReservationServiceException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class RoomDAO extends DAO<Room>{

    private ReservationDAO reservationDAO;

    public RoomDAO() {
        this.reservationDAO = new ReservationDAO();
    }

    public Room getRoomByID(String roomID) throws RoomServiceException, ReservationServiceException {
        // preparing query to retrieve the room
        String sql = "SELECT 1 FROM rooms WHERE roomID=" + roomID;

        // retrieving results (no reservation info will be available)
        try {
            ResultSet results = retrieve(sql);
            return createRoomFromResultSet(results);

        } catch (ClassNotFoundException | SQLException se) {
            se.printStackTrace();
            throw new RoomServiceException("Exception caught retrieving room " + roomID);
        }
    }

    public ArrayList<Room> getRoomsByProperties(RoomProperties properties) throws RoomServiceException {
        // preparing sql
        String sql = "SELECT * FROM rooms WHERE " +
                     "capacity >= "  + String.valueOf(properties.getCapacity()) + " AND " +
                     "projector = "  + String.valueOf(properties.hasProjector() ? 1 : 0)  + " AND " +
                     "whiteboard = " + String.valueOf(properties.hasWhiteboard() ? 1 : 0) + " AND " +
                     "int_whiteboard = " + String.valueOf(properties.hasInteractiveWhiteboard() ? 1 : 0) + " AND " +
                     "videocall_capable = " + String.valueOf(properties.isVideocallCapable() ? 1 : 0) + " AND " +
                     "microphone = " + String.valueOf(properties.hasMicrophone() ? 1 : 0);

        // retrieving results filtered by room properties
        try {
            ArrayList<Room> rooms = new ArrayList<>();

            ResultSet results = retrieve(sql);
            while (results.next()) {
                rooms.add(createRoomFromResultSet(results));
            }

            return rooms;
        } catch (ClassNotFoundException | SQLException e) {
            throw new RoomServiceException("Exception caught handling rooms retrieval");
        }
    }

    public ArrayList<Room> getRoomsByPropertiesAndDepartment(RoomProperties properties, String department) throws RoomServiceException, ReservationServiceException {
        // preparing sql
        String sql = "SELECT * FROM rooms WHERE " +
                "department=" + department + " AND " +
                "capacity >= "  + String.valueOf(properties.getCapacity()) + " AND " +
                "projector = "  + String.valueOf(properties.hasProjector() ? 1 : 0)  + " AND " +
                "whiteboard = " + String.valueOf(properties.hasWhiteboard() ? 1 : 0) + " AND " +
                "int_whiteboard = " + String.valueOf(properties.hasInteractiveWhiteboard() ? 1 : 0) + " AND " +
                "videocall_capable = " + String.valueOf(properties.isVideocallCapable() ? 1 : 0) + " AND " +
                "microphone = " + String.valueOf(properties.hasMicrophone() ? 1 : 0);

        // retrieving results filtered by room properties
        try {
            ArrayList<Room> rooms = new ArrayList<>();

            // retrieving rooms
            ResultSet results = retrieve(sql);
            while (results.next()) {
                Room room = createRoomFromResultSet(results);
                // adding reservations
                for (Reservation reservation : reservationDAO.getReservationsByRoomID(room.getRoomID()))
                    room.addReservation(reservation);
            }

            return rooms;
        } catch (ClassNotFoundException | SQLException e) {
            throw new RoomServiceException("Exception caught handling rooms retrieval");
        }
    }

    public ArrayList<Room> getRoomsByReferral(String referral) throws RoomServiceException, ReservationServiceException {
        HashMap<String, Room> room_map = new HashMap<>();                           // auxiliary HashMap<roomID, room>

        // retrieving all the reservations related to the given referral
        // (only active reservations will be shown)
        ArrayList<Reservation> reservations = reservationDAO.getReservationsByReferral(referral);
        for (Reservation reservation : reservations) {
            if (room_map.containsKey(reservation.getRoomID()))
                // add only if user is the referral
                if (reservation.getReferral().equals(referral))
                    room_map.get(reservation.getRoomID()).addReservation(reservation);
            else {
                Room r = getRoomByID(reservation.getRoomID());
                r.addReservation(reservation);
                room_map.put(r.getRoomID(), r);
            }
        }

        // return the list of rooms
        return new ArrayList<>(room_map.values());
    }

    public ArrayList<Room> getRoomsByEvent(String eventID) throws RoomServiceException, ReservationServiceException {
        HashMap<String, Room> room_map = new HashMap<>();                           // auxiliary HashMap<roomID, room>

        // retrieving all the reservations related to the given event
        // (only active reservations will be shown)
        ArrayList<Reservation> reservations = reservationDAO.getReservationsByEventID(eventID);
        for (Reservation reservation : reservations) {
            if (room_map.containsKey(reservation.getRoomID()))
                // add only if event is the one associated with the search
                if (reservation.getEventID().equals(eventID))
                    room_map.get(reservation.getRoomID()).addReservation(reservation);
            else {
                Room r = getRoomByID(reservation.getRoomID());
                r.addReservation(reservation);
                room_map.put(r.getRoomID(), r);
            }
        }

        // return the list of rooms
        return new ArrayList<>(room_map.values());
    }

    @Override
    public void update(Room room) throws RoomServiceException {

        // only reservations can be modified in Room
        try {
            for (Reservation reservation : room.getReservations()) {
                this.reservationDAO.update(reservation);
            }
        } catch (ReservationServiceException e) {
            e.printStackTrace();
            throw new RoomServiceException("Exception caught handling room update");
        }

    }

    @Override
    public void delete(Room room) throws Exception { /* no implementation needed */}

    public Room createRoomFromResultSet(ResultSet results) throws SQLException {
        // check if result set is not empty
        if (!results.first())
            return null;

        // creating room properties
        RoomProperties roomProperties = new RoomProperties(results.getInt("capacity"),
                                                           results.getInt("projector") == 1,
                                                           results.getInt("whiteboard") == 1,
                                                           results.getInt("int_whiteboard") == 1,
                                                           results.getInt("videocall_capable") == 1,
                                                           results.getInt("microphone") == 1);

        // return new Room object (reservation list is empty by default)
        return new Room(results.getString("roomID"),
                        results.getString("name"),
                        results.getString("department"),
                        roomProperties);
    }
}
