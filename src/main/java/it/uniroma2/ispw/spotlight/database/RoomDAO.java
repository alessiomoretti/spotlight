package it.uniroma2.ispw.spotlight.database;

import it.uniroma2.ispw.spotlight.entities.Room.Reservation;
import it.uniroma2.ispw.spotlight.entities.Room.Room;
import it.uniroma2.ispw.spotlight.entities.Room.RoomProperties;
import it.uniroma2.ispw.spotlight.exceptions.RoomServiceException;
import it.uniroma2.ispw.spotlight.exceptions.ReservationServiceException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import static java.sql.ResultSet.TYPE_SCROLL_INSENSITIVE;

public class RoomDAO extends DAO<Room>{

    private ReservationDAO reservationDAO;

    public RoomDAO() {
        this.reservationDAO = new ReservationDAO();
    }

    public Room getRoomByID(String roomID) throws RoomServiceException, ReservationServiceException {
        // preparing query to retrieve the room
        String sql = "SELECT * FROM rooms WHERE roomID=?";

        // retrieving results (no reservation info will be available)
        try {
            // retrieving database connection
            Connection db = getConnection();

            // preparing statement
            PreparedStatement pstm = db.prepareStatement(sql, TYPE_SCROLL_INSENSITIVE, NO_GENERATED_KEYS);
            pstm.setString(1, roomID);

            ResultSet results = pstm.executeQuery();
            return createRoomFromResultSet(results);
        } catch (ClassNotFoundException | SQLException se) {
            se.printStackTrace();
            throw new RoomServiceException("Exception caught retrieving room " + roomID);
        }
    }

    public ArrayList<Room> getRoomsByProperties(RoomProperties properties) throws RoomServiceException {
        // preparing sql
        String sql = "SELECT * FROM rooms WHERE capacity >= ? AND projector = ? AND whiteboard = ? AND int_whiteboard = ?" +
                     "AND videocall_capable = ? AND microphone = ?";

        // retrieving results filtered by room properties
        try {
            ArrayList<Room> rooms = new ArrayList<>();

            // retrieving database connection
            Connection db = getConnection();

            // preparing statement
            PreparedStatement pstm = db.prepareStatement(sql, TYPE_SCROLL_INSENSITIVE, NO_GENERATED_KEYS);
            pstm.setInt(1, properties.getCapacity());
            pstm.setInt(2, properties.hasProjector() ? 1 : 0);
            pstm.setInt(3, properties.hasWhiteboard() ? 1 : 0);
            pstm.setInt(4, properties.hasInteractiveWhiteboard() ? 1 : 0);
            pstm.setInt(5, properties.isVideocallCapable() ? 1 : 0);
            pstm.setInt(6, properties.hasMicrophone() ? 1 : 0);


            ResultSet results = pstm.executeQuery();
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
        String sql = "SELECT * FROM rooms WHERE capacity >= ? AND projector = ? AND whiteboard = ? AND int_whiteboard = ?" +
                "AND videocall_capable = ? AND microphone = ? AND department = ?";

        // retrieving results filtered by room properties
        try {
            ArrayList<Room> rooms = new ArrayList<>();

            // retrieving database connection
            Connection db = getConnection();

            // preparing statement
            PreparedStatement pstm = db.prepareStatement(sql, TYPE_SCROLL_INSENSITIVE, NO_GENERATED_KEYS);
            pstm.setInt(1, properties.getCapacity());
            pstm.setInt(2, properties.hasProjector() ? 1 : 0);
            pstm.setInt(3, properties.hasWhiteboard() ? 1 : 0);
            pstm.setInt(4, properties.hasInteractiveWhiteboard() ? 1 : 0);
            pstm.setInt(5, properties.isVideocallCapable() ? 1 : 0);
            pstm.setInt(6, properties.hasMicrophone() ? 1 : 0);
            pstm.setString(7, department);

            ResultSet results = pstm.executeQuery();
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
