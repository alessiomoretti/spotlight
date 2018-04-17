package it.uniroma2.ispw.database;

import it.uniroma2.ispw.entities.Event;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;


public class EventDAO extends DAO<Event> {

    @Override
    public void update(Event event) throws ClassNotFoundException, SQLException {

        // retrieving database connection
        Connection db = getConnection();

        // preparing update sql
        String sql = "INSERT INTO events (eventID, event_name, start_timestamp, end_timestamp, referral, mailing_list) VALUES (?, ?, ?, ?, ?, ?) " +
                "ON CONFLICT (eventID) DO UPDATE " +
                "SET event_name = EXCLUDED.event_name, " +
                "start_timestamp = EXCLUDED.start_timestamp, " +
                "end_timestamp = EXCLUDED.end_timestamp, " +
                "mailing_list = EXCLUDED.mailing_list";


        // preparing statement
        PreparedStatement pstmt = db.prepareStatement(sql);
        // executing the update routine
        pstmt.setInt(1, Integer.valueOf(event.getEventID()));
        pstmt.setString(2, event.getEventName());
        pstmt.setTimestamp(3, new Timestamp(event.getStartDateTime().getTime()));
        pstmt.setTimestamp(4, new Timestamp(event.getEndDateTime().getTime()));
        pstmt.setString(5, event.getReferral().getUsername());
        pstmt.setString(6, event.getEmailDL());
        pstmt.execute();
        pstmt.close();

        db.commit();
        db.close();


    }

    @Override
    public void delete(Event event) throws ClassNotFoundException, SQLException {

        // retrieving database connection
        Connection db = getConnection();

        // preparing the delete sql
        String sql = "DELETE FROM events WHERE eventID=" + event.getEventID();

        // preparing statement
        PreparedStatement pstmt = getConnection().prepareStatement(sql);
        pstmt.execute();
        pstmt.close();

        db.commit();
        db.close();

    }
}
