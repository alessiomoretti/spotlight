package it.uniroma2.ispw.database;

import it.uniroma2.ispw.entities.Room.Reservation;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

public class ReservationDAO extends DAO<Reservation> {

    public ReservationDAO() { }

    @Override
    public void update(Reservation reservation) throws ClassNotFoundException, SQLException {

        // retrieving database connection
        Connection db = getConnection();

        // preparing update sql
        String sql = "INSERT INTO reservations (id, roomID, start_timestamp, end_timestamp) VALUES (?, ?, ?, ?) " +
                "ON CONFLICT (id) DO UPDATE " +
                "SET start = EXCLUDED.start, end = EXCLUDED.end";

        // preparing statement
        PreparedStatement pstmt = db.prepareStatement(sql);
        // executing the update routine
        pstmt.setInt(1, Integer.valueOf(reservation.getReservationID()));
        pstmt.setString(2, reservation.getRoomID());
        pstmt.setTimestamp(3, new Timestamp(reservation.getStartDateTime().getTime()));
        pstmt.setTimestamp(4, new Timestamp(reservation.getEndDateTime().getTime()));
        pstmt.execute();
        pstmt.close();

        db.commit();
        db.close();
    }

    @Override
    public void delete(Reservation reservation) throws Exception {

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
    }
}
