package it.uniroma2.ispw.spotlight.database;

import it.uniroma2.ispw.spotlight.entities.Room.Reservation;
import it.uniroma2.ispw.spotlight.exceptions.ReservationServiceException;

import java.sql.*;
import java.util.ArrayList;

import static java.sql.ResultSet.TYPE_SCROLL_INSENSITIVE;
import static java.sql.Statement.NO_GENERATED_KEYS;

/**
 * This DAO acts as a controller of the Reservation objects at persistence level
 */
public class ReservationDAO extends DAO<Reservation> {

    public ReservationDAO() { }

    /**
     * Return the list of all the Reservations associated to the given event identifier
     * @param eventID String
     * @return ArrayList<Reservation>
     * @throws ReservationServiceException
     */
    public ArrayList<Reservation> getReservationsByEventID(String eventID) throws ReservationServiceException {
        // preparing query to retrieve all the reservations (present or future) related to a given event
        String sql = "SELECT * FROM reservations WHERE eventID=?";

        // retrieving reservations
        try {
            // retrieving database connection
            Connection db = getConnection();

            // preparing statement
            PreparedStatement pstm = db.prepareStatement(sql, TYPE_SCROLL_INSENSITIVE, NO_GENERATED_KEYS);
            pstm.setString(1, eventID);

            ResultSet results = pstm.executeQuery();
            return getReservationsFromResultSet(results);
        } catch (ClassNotFoundException | SQLException se) {
            se.printStackTrace();
            throw new ReservationServiceException("Exception caught retrieving reservations related to event " + eventID);
        }
    }

    /**
     * Return the Reservation associated to a given unique identifier
     * @param reservationID
     * @return Reservation
     * @throws ReservationServiceException
     */
    public Reservation getReservationsByReservationID(String reservationID) throws ReservationServiceException {
        // preparing query to retrieve all the reservations (present or future) related to a given event
        String sql = "SELECT * FROM reservations WHERE resID=?";

        // retrieving reservations
        try {
            // retrieving database connection
            Connection db = getConnection();

            // preparing statement
            PreparedStatement pstm = db.prepareStatement(sql, TYPE_SCROLL_INSENSITIVE, NO_GENERATED_KEYS);
            pstm.setString(1, reservationID);

            ResultSet results = pstm.executeQuery();
            return getReservationsFromResultSet(results).get(0); // assuming only one reservation is returned for consistency
        } catch (ClassNotFoundException | SQLException se) {
            se.printStackTrace();
            throw new ReservationServiceException("Exception caught retrieving reservation " + reservationID);
        }
    }

    /**
     * Return the list of all the Reservations associated to a given event identifier and starting from the given timestamp
     * @param eventID String
     * @param timestamp Timestamp
     * @return ArrayList<Reservation>
     * @throws ReservationServiceException
     */
    public ArrayList<Reservation> getReservationsByEventID(String eventID, Timestamp timestamp) throws ReservationServiceException {
        // preparing query to retrieve all the reservations (present or future) related to a given event
        String sql = "SELECT * FROM reservations WHERE eventID=? AND start_timestamp  >= ?";

        // retrieving reservations
        try {
            // retrieving database connection
            Connection db = getConnection();

            // preparing statement
            PreparedStatement pstm = db.prepareStatement(sql, TYPE_SCROLL_INSENSITIVE, NO_GENERATED_KEYS);
            pstm.setString(1, eventID);
            pstm.setTimestamp(2, timestamp);

            ResultSet results = pstm.executeQuery();
            return getReservationsFromResultSet(results);
        } catch (ClassNotFoundException | SQLException se) {
            se.printStackTrace();
            throw new ReservationServiceException("Exception caught retrieving reservations related to event " + eventID);
        }
    }

    /**
     * Return the list of all the Reservations associated to a given room identifier and starting from the given timestamp
     * @param roomID String
     * @param timestamp Timestamp
     * @return ArrayList<Reservation>
     * @throws ReservationServiceException
     */
    public ArrayList<Reservation> getReservationsByRoomID(String roomID, Timestamp timestamp) throws ReservationServiceException {
        // preparing query to retrieve all the reservations (present or future) related to a given event
        String sql = "SELECT * FROM reservations WHERE roomID=? AND start_timestamp >= ?";

        // retrieving reservations
        try {
            // retrieving database connection
            Connection db = getConnection();

            // preparing statement
            PreparedStatement pstm = db.prepareStatement(sql, TYPE_SCROLL_INSENSITIVE, NO_GENERATED_KEYS);
            pstm.setString(1, roomID);
            pstm.setTimestamp(2, timestamp);

            ResultSet results = pstm.executeQuery();
            return getReservationsFromResultSet(results);
        } catch (ClassNotFoundException | SQLException se) {
            se.printStackTrace();
            throw new ReservationServiceException("Exception caught retrieving reservations related to room " + roomID);
        }
    }

    /**
     * Return the list of all the Reservations associated to a given room identifier
     * @param roomID String
     * @return ArrayList<Reservation>
     * @throws ReservationServiceException
     */
    public ArrayList<Reservation> getReservationsByRoomID(String roomID) throws ReservationServiceException {
        // preparing query to retrieve all the reservations (present or future) related to a given event
        String sql = "SELECT * FROM reservations WHERE roomID=?";

        // retrieving reservations
        try {
            // retrieving database connection
            Connection db = getConnection();

            // preparing statement
            PreparedStatement pstm = db.prepareStatement(sql, TYPE_SCROLL_INSENSITIVE, NO_GENERATED_KEYS);
            pstm.setString(1, roomID);

            ResultSet results = pstm.executeQuery();
            return getReservationsFromResultSet(results);
        } catch (ClassNotFoundException | SQLException se) {
            se.printStackTrace();
            throw new ReservationServiceException("Exception caught retrieving reservations related to room " + roomID);
        }
    }

    /**
     * Return true if there is any Reservation in the given timeslot for the given room identifier
     * @param roomID String
     * @param startT Timestamp
     * @param endT Timestamp
     * @return Boolean
     * @throws ReservationServiceException
     */
    public boolean checkReservationsByRoomIDAndTimeslot(String roomID, Timestamp startT, Timestamp endT) throws ReservationServiceException {
        // preparing query to retrieve all the reservations in the given timeslot
        String sql = "SELECT * FROM reservations WHERE roomID=?"+
                "AND (start_timestamp, end_timestamp) overlaps (?,?)";

        // retrieving reservations
        try {
            // retrieving database connection
            Connection db = getConnection();

            // preparing statement
            PreparedStatement pstm = db.prepareStatement(sql, TYPE_SCROLL_INSENSITIVE, NO_GENERATED_KEYS);
            pstm.setString(1, roomID);
            pstm.setTimestamp(2, startT);
            pstm.setTimestamp(3, endT);

            ResultSet results = pstm.executeQuery();
            return (getReservationsFromResultSet(results).size() == 0);
        } catch (ClassNotFoundException | SQLException se) {
            se.printStackTrace();
            throw new ReservationServiceException("Exception caught retrieving reservations related to room " + roomID);
        }
    }

    /**
     * Return the list of all the Reservations associated to a referral (username) and starting from the given timestamp
     * @param referral String, username
     * @param timestamp Timestamp
     * @return ArrayList<Reservation>
     * @throws ReservationServiceException
     */
    public ArrayList<Reservation> getReservationsByReferral(String referral, Timestamp timestamp) throws ReservationServiceException {
        // preparing query to retrieve all the reservations (present or future) related to a given referral
        String sql = "SELECT * FROM reservations WHERE referral=? AND start_timestamp >= ?";

        // retrieving reservations
        try {
            // retrieving database connection
            Connection db = getConnection();

            // preparing statement
            PreparedStatement pstm = db.prepareStatement(sql, TYPE_SCROLL_INSENSITIVE, NO_GENERATED_KEYS);
            pstm.setString(1, referral);
            pstm.setTimestamp(2, timestamp);

            ResultSet results = pstm.executeQuery();
            return getReservationsFromResultSet(results);
        } catch (ClassNotFoundException | SQLException se) {
            se.printStackTrace();
            throw new ReservationServiceException("Exception caught retrieving reservations related to referral " + referral);
        }
    }

    /**
     * Return the list of all the Reservations associated to a referral (username)
     * @param referral String
     * @return ArrayList<Reservation>
     * @throws ReservationServiceException
     */
    public ArrayList<Reservation> getReservationsByReferral(String referral) throws ReservationServiceException {
        // preparing query to retrieve all the reservations (present or future) related to a given referral
        String sql = "SELECT * FROM reservations WHERE referral=?";

        // retrieving reservations
        try {
            // retrieving database connection
            Connection db = getConnection();

            // preparing statement
            PreparedStatement pstm = db.prepareStatement(sql, TYPE_SCROLL_INSENSITIVE, NO_GENERATED_KEYS);
            pstm.setString(1, referral);

            ResultSet results = pstm.executeQuery();
            return getReservationsFromResultSet(results);
        } catch (ClassNotFoundException | SQLException se) {
            se.printStackTrace();
            throw new ReservationServiceException("Exception caught retrieving reservations related to referral " + referral);
        }
    }

    /**
     * Update a Reservation (or create a new one) given a new Reservation representation
     * @param reservation Reservation
     * @throws ReservationServiceException
     */
    @Override
    public void update(Reservation reservation) throws ReservationServiceException {
        // preparing update query
        String sql = "INSERT INTO reservations (resID, roomID, eventID, referral, start_timestamp, end_timestamp) VALUES (?, ?, ?, ?, ?, ?) " +
                "ON CONFLICT (resID) DO UPDATE " +
                "SET referral = EXCLUDED.referral, " +
                "start_timestamp = EXCLUDED.start_timestamp, end_timestamp = EXCLUDED.end_timestamp";

        try {
            // retrieving database connection
            Connection db = getConnection();

            // preparing statement
            PreparedStatement pstmt = db.prepareStatement(sql);
            // executing the update routine
            pstmt.setString(1, reservation.getReservationID());
            pstmt.setString(2, reservation.getRoomID());
            pstmt.setString(3, reservation.getEventID());
            pstmt.setString(4, reservation.getReferral());
            pstmt.setTimestamp(5, new Timestamp(reservation.getStartDateTime().getTime()));
            pstmt.setTimestamp(6, new Timestamp(reservation.getEndDateTime().getTime()));
            pstmt.execute();

            db.commit();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            throw new ReservationServiceException("Exception caught updating / creating a new reservation");
        }

    }

    /**
     * Delete the reservation
     * @param reservation Reservation
     * @throws ReservationServiceException
     */
    @Override
    public void delete(Reservation reservation) throws ReservationServiceException {
        try {
            // retrieving database connection
            Connection db = getConnection();

            // preparing the delete sql
            String sql = "DELETE FROM reservations WHERE resID=?";

            // preparing statement
            PreparedStatement pstmt = getConnection().prepareStatement(sql);
            pstmt.setString(1, reservation.getReservationID());
            pstmt.execute();
            pstmt.close();

            db.commit();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            throw new ReservationServiceException("Exception deleting a reservation");
        }
    }

    /**
     * Utility to return a list of Reservation objects from the persistence layer ResultSet
     * @param results ResultSet
     * @return ArrayList<Reservation>
     * @throws SQLException
     */
    public ArrayList<Reservation> getReservationsFromResultSet(ResultSet results) throws SQLException {
        ArrayList<Reservation> reservations = new ArrayList<>();

        if (!results.isBeforeFirst()) return reservations;
        results.first();
        while (true) {
            reservations.add(new Reservation(
                    results.getString("resID"),
                    results.getString("roomID"),
                    results.getString("eventID"),
                    results.getString("referral"),
                    new Date(results.getTimestamp("start_timestamp").getTime()),
                    new Date(results.getTimestamp("end_timestamp").getTime())));

            if (!results.next()) break;
        }
        return reservations;
    }
}
