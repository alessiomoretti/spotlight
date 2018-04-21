package it.uniroma2.ispw.spotlight.database;

import it.uniroma2.ispw.spotlight.entities.Room.Reservation;
import it.uniroma2.ispw.spotlight.exceptions.ReservationServiceException;

import java.sql.*;
import java.util.ArrayList;

import static java.sql.ResultSet.TYPE_SCROLL_INSENSITIVE;
import static java.sql.Statement.NO_GENERATED_KEYS;

public class ReservationDAO extends DAO<Reservation> {

    public ReservationDAO() { }

    public ArrayList<Reservation> getReservationsByEventID(String eventID) throws ReservationServiceException {
        // preparing query to retrieve all the reservations (present or future) related to a given event
        String sql = "SELECT * FROM rooms WHERE eventID=? AND start_time  >= current_timestamp";

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

    public ArrayList<Reservation> getReservationsByRoomID(String roomID) throws ReservationServiceException {
        // preparing query to retrieve all the reservations (present or future) related to a given event
        String sql = "SELECT * FROM rooms WHERE roomID=? AND start_time >= current_timestamp";

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

    public boolean checkReservationsByRoomIDAndTimeslot(String roomID, Timestamp startT, Timestamp endT) throws ReservationServiceException {
        // preparing query to retrieve all the reservations in the given timeslot
        String sql = "SELECT * FROM rooms WHERE roomID=?"+
                "AND (start_time >= ?) AND (end_time <= ?) " +
                "OR (start_time <= ?) AND (end_time >= ?)";

        // retrieving reservations
        try {
            // retrieving database connection
            Connection db = getConnection();

            // preparing statement
            PreparedStatement pstm = db.prepareStatement(sql, TYPE_SCROLL_INSENSITIVE, NO_GENERATED_KEYS);
            pstm.setString(1, roomID);
            pstm.setTimestamp(2, startT);
            pstm.setTimestamp(3, endT);
            pstm.setTimestamp(2, startT);
            pstm.setTimestamp(1, endT);

            ResultSet results = pstm.executeQuery();
            return (getReservationsFromResultSet(results).size() == 0);
        } catch (ClassNotFoundException | SQLException se) {
            se.printStackTrace();
            throw new ReservationServiceException("Exception caught retrieving reservations related to room " + roomID);
        }
    }

    public ArrayList<Reservation> getReservationsByReferral(String referral) throws ReservationServiceException {
        // preparing query to retrieve all the reservations (present or future) related to a given referral
        String sql = "SELECT * FROM rooms WHERE referral=? AND start_time >= current_timestamp";

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

    @Override
    public void update(Reservation reservation) throws ReservationServiceException {
        // preparing update query
        String sql = "INSERT INTO reservations (resID, roomID, eventID, referral, start_timestamp, end_timestamp) VALUES (?, ?, ?, ?, ?, ?) " +
                "ON CONFLICT (resID) DO UPDATE " +
                "SET start_timestamp = EXCLUDED.start_timestamp, end_timestamp = EXCLUDED.end_timestamp";

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
            pstmt.close();

            db.commit();
            db.close();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            throw new ReservationServiceException("Exception caught updating / creating a new reservation");
        }

    }

    @Override
    public void delete(Reservation reservation) throws ReservationServiceException {
        try {
            // retrieving database connection
            Connection db = getConnection();

            // preparing the delete sql
            String sql = "DELETE FROM reservation WHERE id=" + reservation.getReservationID();

            // preparing statement
            PreparedStatement pstmt = getConnection().prepareStatement(sql);
            pstmt.execute();
            pstmt.close();

            db.commit();
            db.close();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            throw new ReservationServiceException("Exception deleting a reservation");
        }
    }

    public ArrayList<Reservation> getReservationsFromResultSet(ResultSet results) throws SQLException {
        ArrayList<Reservation> reservations = new ArrayList<>();
        while (results.next()) {
            reservations.add(new Reservation(results.getString("id"),
                    results.getString("roomID"),
                    results.getString("eventID"),
                    results.getString("referral"),
                    new Date(results.getTimestamp("start_time").getTime()),
                    new Date(results.getTimestamp("end_time").getTime())));
        }
        return reservations;
    }
}
