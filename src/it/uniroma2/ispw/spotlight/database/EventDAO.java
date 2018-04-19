package it.uniroma2.ispw.spotlight.database;

import it.uniroma2.ispw.spotlight.entities.Event;
import it.uniroma2.ispw.spotlight.users.User;
import it.uniroma2.ispw.spotlight.exceptions.EventServiceException;
import it.uniroma2.ispw.spotlight.exceptions.UserRetrievalException;

import java.sql.*;
import java.util.ArrayList;


public class EventDAO extends DAO<Event> {

    private UserDAO userDAO;

    public EventDAO() {
        this.userDAO = new UserDAO();
    }

    public Event getEventById(String eventID) throws UserRetrievalException, EventServiceException {
        // preparing query to select the event for a given id
        String sql = "SELECT * FROM events WHERE eventID=" + eventID;

        // retrieving results
        try {
            ResultSet results = retrieve(sql);
            if (results.first()) {
                return getEventsFromResultSet(results).get(0); // get the first element (only one result)
            } else {
                return null;
            }
        } catch (ClassNotFoundException | SQLException se) {
            throw new EventServiceException("Exception caught retrieving event " + eventID);
        }
    }

    public ArrayList<Event> getEventsByReferral(User referral) throws EventServiceException {
        // preparing query to select the event for a given referral
        String sql = "SELECT * FROM events WHERE referral=" + referral.getUsername();

        // retrieving results
        try {
            ResultSet results = retrieve(sql);
            if (results.first()) {
                return getEventsFromResultSet(results, referral); // get the first element (only one result)
            } else {
                return null;
            }
        } catch (ClassNotFoundException | SQLException se) {
            throw new EventServiceException("Exception caught retrieving event referred by " + referral.getUsername());
        }
    }

    public ArrayList<Event> getEventsByName(String eventName) throws UserRetrievalException, EventServiceException {
        // preparing query to get all the events containing the name
        String sql = "SELECT * FROM events WHERE event_name LIKE " + eventName + "" +
                     "OR event_name LIKE " + eventName + "% " +
                     "OR event_name LIKE _" + eventName + "_";

        // retrieving results
        try {
            ResultSet results = retrieve(sql);
            if (results.first()) {
                return getEventsFromResultSet(results);
            } else {
                return null;
            }
        } catch (ClassNotFoundException | SQLException se) {
            throw new EventServiceException("Exception caught retrieving event referred by event name");
        }
    }

    public ArrayList<Event> getEventsByTime(Timestamp startT, Timestamp endT) throws UserRetrievalException, EventServiceException {
        // preparing query to retrieve events in the given timeslot
        String sql = "SELECT * FROM events WHERE (start_time, end_time) overlaps (" + startT.toString() + "," + endT.toString() + ")";

        // retrieving results
        try {
            ResultSet results = retrieve(sql);
            if (results.first()) {
                return getEventsFromResultSet(results);
            } else {
                return null;
            }
        } catch (ClassNotFoundException | SQLException se) {
            throw new EventServiceException("Exception caught retrieving event referred by timeslot");
        }
    }

    @Override
    public void update(Event event) throws EventServiceException {
        // preparing update query
        String sql = "INSERT INTO events (eventID, event_name, start_timestamp, end_timestamp, referral, mailing_list) VALUES (?, ?, ?, ?, ?, ?) " +
                "ON CONFLICT (eventID) DO UPDATE " +
                "SET event_name = EXCLUDED.event_name, " +
                "start_timestamp = EXCLUDED.start_timestamp, " +
                "end_timestamp = EXCLUDED.end_timestamp, " +
                "mailing_list = EXCLUDED.mailing_list";

        try {
            // retrieving database connection
            Connection db = getConnection();

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
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            throw new EventServiceException("Exception caught updating / creating a new event");
        }


    }

    @Override
    public void delete(Event event) throws EventServiceException {
        // preparing the delete query
        String sql = "DELETE FROM events WHERE eventID=" + event.getEventID();

        try {
            // retrieving database connection
            Connection db = getConnection();

            // preparing statement
            PreparedStatement pstmt = getConnection().prepareStatement(sql);
            pstmt.execute();
            pstmt.close();

            db.commit();
            db.close();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            throw new EventServiceException("Exception caught deleting event " + event.getEventID());
        }
    }

    public ArrayList<Event> getEventsFromResultSet(ResultSet results) throws SQLException, UserRetrievalException{
        ArrayList<Event> events = new ArrayList<>();
        while (results.next()) {
            // retrieving the referral as a User
            User referralUser = userDAO.getUserByUsername(results.getString("referral"));

            events.add(new Event(results.getString("eventID"),
                    results.getString("event_name"),
                    new Date(results.getTimestamp("start_time").getTime()),
                    new Date(results.getTimestamp("end_time").getTime()),
                    referralUser,
                    results.getString("mailing_list")));
        }
        return events;
    }

    public ArrayList<Event> getEventsFromResultSet(ResultSet results, User referral) throws SQLException {
        ArrayList<Event> events = new ArrayList<>();
        while (results.next()) {
            events.add(new Event(results.getString("eventID"),
                    results.getString("event_name"),
                    new Date(results.getTimestamp("start_time").getTime()),
                    new Date(results.getTimestamp("end_time").getTime()),
                    referral,
                    results.getString("mailing_list")));
        }
        return events;
    }
}
