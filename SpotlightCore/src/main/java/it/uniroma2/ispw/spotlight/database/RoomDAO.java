package it.uniroma2.ispw.spotlight.database;

import it.uniroma2.ispw.spotlight.entities.Room.Reservation;
import it.uniroma2.ispw.spotlight.entities.Room.Room;
import it.uniroma2.ispw.spotlight.entities.Room.RoomProperties;
import it.uniroma2.ispw.spotlight.exceptions.RoomServiceException;
import it.uniroma2.ispw.spotlight.exceptions.ReservationServiceException;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

import static java.sql.ResultSet.TYPE_SCROLL_INSENSITIVE;
import static java.sql.Statement.NO_GENERATED_KEYS;

/**
 * This DAO acts as a controller of the Room objects at persistence level
 */
public class RoomDAO extends DAO<Room>{

    private ReservationDAO reservationDAO;

    public RoomDAO() {
        this.reservationDAO = new ReservationDAO();
    }

    /**
     * Return the list of all the rooms in the organization
     * @return ArrayList<Room>
     * @throws RoomServiceException
     * @throws ReservationServiceException
     */
    public ArrayList<Room> getAllRooms() throws RoomServiceException, ReservationServiceException {
        // preparing query to retrieve all the rooms
        String sql = "SELECT * FROM rooms";

        // retrieving results (no reservation info will be available)
        try {
            // retrieving database connection
            Connection db = getConnection();

            // preparing statement
            PreparedStatement pstm = db.prepareStatement(sql, TYPE_SCROLL_INSENSITIVE, NO_GENERATED_KEYS);

            // executing query
            ResultSet results = pstm.executeQuery();

            // preparing results and retrieving reservations
            ArrayList<Room> rooms = new ArrayList<>();
            results.first();
            while (true) {
                Room room = createRoomFromResultSet(results);
                // adding reservations
                for (Reservation reservation : reservationDAO.getReservationsByRoomID(room.getRoomID()))
                    room.addReservation(reservation);
                rooms.add(room);

                if (!results.next()) break;
            }

            return rooms;
        } catch (ClassNotFoundException | SQLException se) {
            se.printStackTrace();
            throw new RoomServiceException("Exception caught retrieving list of all rooms");
        }
    }

    /**
     * Return the Room associated with the given room identifier
     * @param roomID String
     * @return Room
     * @throws RoomServiceException
     */
    public Room getRoomByID(String roomID) throws RoomServiceException {
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
            results.first();
            return createRoomFromResultSet(results);
        } catch (ClassNotFoundException | SQLException se) {
            se.printStackTrace();
            throw new RoomServiceException("Exception caught retrieving room " + roomID);
        }
    }

    /**
     * Return all the rooms satisfying the given properties
     * @param properties RoomProperties
     * @return ArrayList<Room>
     * @throws RoomServiceException
     * @throws ReservationServiceException
     */
    public ArrayList<Room> getRoomsByProperties(RoomProperties properties) throws RoomServiceException, ReservationServiceException {
        // preparing sql
        String sql = "SELECT * FROM rooms WHERE capacity >= ?";

        // retrieving results filtered by room properties
        try {
            ArrayList<Room> rooms = new ArrayList<>();

            // retrieving database connection
            Connection db = getConnection();

            // preparing statement
            PreparedStatement pstm = db.prepareStatement(sql, TYPE_SCROLL_INSENSITIVE, NO_GENERATED_KEYS);
            pstm.setInt(1, properties.getCapacity());


            ResultSet results = pstm.executeQuery();
            if (!results.isBeforeFirst()) return rooms;

            results.first();
            while (true) {
                Room room = createRoomFromResultSet(results);
                if (verifyProperties(room.getProperties(), properties)) {
                    // adding reservations
                    for (Reservation reservation : reservationDAO.getReservationsByRoomID(room.getRoomID()))
                        room.addReservation(reservation);
                    rooms.add(room);
                }

                if (!results.next()) break;
            }

            return rooms;
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            throw new RoomServiceException("Exception caught handling rooms retrieval");
        }
    }

    /**
     * Return all the rooms satisfying the given properties in the selected department
     * @param properties RoomProperties
     * @param department String
     * @return ArrayList<Room>
     * @throws RoomServiceException
     * @throws ReservationServiceException
     */
    public ArrayList<Room> getRoomsByPropertiesAndDepartment(RoomProperties properties, String department) throws RoomServiceException, ReservationServiceException {
        // preparing sql
        String sql = "SELECT * FROM rooms WHERE capacity >= ? AND department = ?";

        // retrieving results filtered by room properties
        try {
            ArrayList<Room> rooms = new ArrayList<>();

            // retrieving database connection
            Connection db = getConnection();

            // preparing statement
            PreparedStatement pstm = db.prepareStatement(sql, TYPE_SCROLL_INSENSITIVE, NO_GENERATED_KEYS);
            pstm.setInt(1, properties.getCapacity());
            pstm.setString(2, department);

            ResultSet results = pstm.executeQuery();
            if (!results.isBeforeFirst()) return rooms;

            results.first();
            while (true) {
                Room room = createRoomFromResultSet(results);
                if (verifyProperties(room.getProperties(), properties)) {
                    // adding reservations
                    for (Reservation reservation : reservationDAO.getReservationsByRoomID(room.getRoomID()))
                        room.addReservation(reservation);
                    rooms.add(room);
                }
                if (!results.next()) break;
            }

            return rooms;
        } catch (ClassNotFoundException | SQLException e) {
            throw new RoomServiceException("Exception caught handling rooms retrieval");
        }
    }

    /**
     * Return all the rooms associated with the given referral (username)
     * @param referral String, username
     * @return ArrayList<Room>
     * @throws RoomServiceException
     * @throws ReservationServiceException
     */
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

    /**
     * Return all the rooms associated with the given event identifier
     * @param eventID String
     * @return ArrayList<Room>
     * @throws RoomServiceException
     * @throws ReservationServiceException
     */
    public ArrayList<Room> getRoomsByEvent(String eventID) throws RoomServiceException, ReservationServiceException {
        HashMap<String, Room> room_map = new HashMap<>();                           // auxiliary HashMap<roomID, room>

        // retrieving all the reservations related to the given event
        // (only active reservations will be shown)
        ArrayList<Reservation> reservations = reservationDAO.getReservationsByEventID(eventID);
        for (Reservation reservation : reservations) {
            if (room_map.containsKey(reservation.getRoomID())) {
                // add only if event is the one associated with the search
                if (reservation.getEventID().equals(eventID)) {
                    Room r = room_map.get(reservation.getRoomID());
                    r.addReservation(reservation);
                    room_map.replace(r.getRoomID(), r);
                }
            } else {
                Room r = getRoomByID(reservation.getRoomID());
                r.addReservation(reservation);
                room_map.put(r.getRoomID(), r);
            }
        }

        // return the list of rooms
        return new ArrayList<>(room_map.values());
    }

    /**
     * Return all the departments with at least a room in the organization
     * @return ArrayList<String>
     * @throws RoomServiceException
     */
    public ArrayList<String> getDepartments() throws RoomServiceException {
        // preparing sql to retrieve all the departments
        String sql = "SELECT * FROM departments";

        // retrieving results
        try {
            ArrayList<String> departments = new ArrayList<>();

            // retrieving database connection
            Connection db = getConnection();

            // preparing statement
            PreparedStatement pstm = db.prepareStatement(sql, TYPE_SCROLL_INSENSITIVE, NO_GENERATED_KEYS);

            ResultSet results = pstm.executeQuery();

            results.first();
            while (true) {
                departments.add(results.getString("department"));
                if (!results.next()) break;
            }

            return departments;
        } catch (ClassNotFoundException | SQLException e) {
            throw new RoomServiceException("Exception caught handling room department retrieval");
        }
    }

    /**
     * Update a room (or create a new one) given a new Room representation
     * @param room Room
     * @throws RoomServiceException
     */
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

    /**
     * Delete the room - not implemented
     * @param room
     * @throws Exception
     */
    @Override
    public void delete(Room room) throws Exception { /* no implementation needed */}

    /**
     * Return a Room from a given ResultSet
     * @param results ResultSet
     * @return Room
     * @throws SQLException
     */
    public Room createRoomFromResultSet(ResultSet results) throws SQLException {

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

    /**
     * Return true if the requested properties are satisfied
     * @param retrieved RoomProperties
     * @param requested RoomProperties
     * @return Boolean
     */
    private boolean verifyProperties(RoomProperties retrieved, RoomProperties requested) {
        if (requested.hasProjector())
            if (!retrieved.hasProjector()) return false;
        if (requested.hasMicrophone())
            if (!retrieved.hasMicrophone()) return false;
        if (requested.hasInteractiveWhiteboard())
            if (!retrieved.hasInteractiveWhiteboard()) return false;
        if (requested.hasWhiteboard())
            if (!retrieved.hasWhiteboard()) return false;
        if (requested.isVideocallCapable())
            if (!retrieved.isVideocallCapable()) return false;
        return true;
    }
}
