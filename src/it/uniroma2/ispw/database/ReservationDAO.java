package it.uniroma2.ispw.database;

import it.uniroma2.ispw.entities.Room.Reservation;
import it.uniroma2.ispw.exceptions.ReservationServiceException;

import java.sql.*;
import java.time.Instant;
import java.util.ArrayList;

public class ReservationDAO extends DAO<Reservation> {

    public ReservationDAO() { }

    public ArrayList<Reservation> getReservationsByEventID(String eventID) throws ReservationServiceException {

        // preparing query to retrieve all the reservations (present or future) related to a given event
        String sql = "SELECT * FROM rooms WHERE eventID=" + eventID +
                "AND start_time >= " + (new Timestamp(Instant.now().getEpochSecond()).toString());

        // retrieving reservations
        try {
            ResultSet results = retrieve(sql);
            return getReservationsFromResultSet(results);
        } catch (ClassNotFoundException | SQLException se) {
            se.printStackTrace();
            throw new ReservationServiceException("Exception caught retrieving reservations related to event " + eventID);
        }
    }

    public ArrayList<Reservation> getReservationsByRoomID(String roomID) throws ReservationServiceException {

        // preparing query to retrieve all the reservations (present or future) related to a given event
        String sql = "SELECT * FROM rooms WHERE roomID=" + roomID +
                "AND start_time >= " + (new Timestamp(Instant.now().getEpochSecond()).toString());

        // retrieving reservations
        try {
            ResultSet results = retrieve(sql);
            return getReservationsFromResultSet(results);
        } catch (ClassNotFoundException | SQLException se) {
            se.printStackTrace();
            throw new ReservationServiceException("Exception caught retrieving reservations related to room " + roomID);
        }
    }

    public ArrayList<Reservation> getReservationsByReferral(String referral) throws ReservationServiceException {

        // preparing query to retrieve all the reservations (present or future) related to a given referral
        String sql = "SELECT * FROM rooms WHERE eventID=" + referral +
                "AND start_time >= " + (new Timestamp(Instant.now().getEpochSecond()).toString());

        // retrieving reservations
        try {
            ResultSet results = retrieve(sql);
            return getReservationsFromResultSet(results);
        } catch (ClassNotFoundException | SQLException se) {
            se.printStackTrace();
            throw new ReservationServiceException("Exception caught retrieving reservations related to referral " + referral);
        }
    }

    @Override
    public void update(Reservation reservation) throws ReservationServiceException {

        // preparing update query
        String sql = "INSERT INTO reservations (id, roomID, eventID, referral, start_timestamp, end_timestamp) VALUES (?, ?, ?, ?, ?, ?) " +
                "ON CONFLICT (id) DO UPDATE " +
                "SET start_timestamp = EXCLUDED.start_timestamp, end_timestamp = EXCLUDED.end_timestamp";

        try {
            // retrieving database connection
            Connection db = getConnection();

            // preparing statement
            PreparedStatement pstmt = db.prepareStatement(sql);
            // executing the update routine
            pstmt.setInt(1, Integer.valueOf(reservation.getReservationID()));
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

    public static ArrayList<Reservation> getReservationsFromResultSet(ResultSet results) throws SQLException {
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
