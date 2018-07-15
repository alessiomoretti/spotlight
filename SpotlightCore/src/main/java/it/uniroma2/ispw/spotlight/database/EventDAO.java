package it.uniroma2.ispw.spotlight.database;

import it.uniroma2.ispw.spotlight.entities.Event;
import it.uniroma2.ispw.spotlight.entities.Room.Reservation;
import it.uniroma2.ispw.spotlight.exceptions.ReservationServiceException;
import it.uniroma2.ispw.spotlight.exceptions.RoomServiceException;
import it.uniroma2.ispw.spotlight.users.User;
import it.uniroma2.ispw.spotlight.exceptions.EventServiceException;
import it.uniroma2.ispw.spotlight.exceptions.UserRetrievalException;

import java.awt.*;
import java.sql.*;
import java.util.ArrayList;

import static java.sql.ResultSet.TYPE_SCROLL_INSENSITIVE;
import static java.sql.Statement.NO_GENERATED_KEYS;

/**
 * This DAO acts as a controller of the Event objects at persistence level
 */
public class EventDAO extends DAO<Event> {

    private UserDAO userDAO; // to access user information on event retrieval and update
    private RoomDAO roomDAO; // to access rooms information on event retrieval and update

    public EventDAO() {
        this.userDAO = new UserDAO();
        this.roomDAO = new RoomDAO();
    }

    /**
     * Return an event on its unique identifier
     * @param eventID String
     * @return Event
     * @throws UserRetrievalException
     * @throws EventServiceException
     * @throws ReservationServiceException
     * @throws RoomServiceException
     */
    public Event getEventById(String eventID) throws UserRetrievalException, EventServiceException, ReservationServiceException, RoomServiceException {
        // preparing query to select the event for a given id
        String sql = "SELECT * FROM events WHERE eventID=?";

        // retrieving results
        try {
            // retrieving database connection
            Connection db = getConnection();
            // preparing statement
            PreparedStatement pstm = db.prepareStatement(sql, TYPE_SCROLL_INSENSITIVE, NO_GENERATED_KEYS);
            pstm.setString(1, eventID);

            ResultSet results = pstm.executeQuery();
            return getEventsFromResultSet(results).get(0); // get the first element (only one result should be returned)

        } catch (ClassNotFoundException | SQLException se) {
            throw new EventServiceException("Exception caught retrieving event " + eventID);
        }
    }

    /**
     * Return the list of all the events related to a referral
     * @param referral User
     * @return ArrayList<Event>
     * @throws EventServiceException
     * @throws RoomServiceException
     * @throws ReservationServiceException
     */
    public ArrayList<Event> getEventsByReferral(User referral) throws EventServiceException, RoomServiceException, ReservationServiceException {
        // preparing query to select the event for a given referral
        String sql = "SELECT * FROM events WHERE referral=?";

        // retrieving results
        try {
            // retrieving database connection
            Connection db = getConnection();

            // preparing statement
            PreparedStatement pstm = db.prepareStatement(sql, TYPE_SCROLL_INSENSITIVE, NO_GENERATED_KEYS);
            pstm.setString(1, referral.getUsername());

            ResultSet results = pstm.executeQuery();
            return getEventsFromResultSet(results, referral); // get the first element (only one result)

        } catch (ClassNotFoundException | SQLException se) {
            throw new EventServiceException("Exception caught retrieving event referred by " + referral.getUsername());
        }
    }

    /**
     * Return a list of all the events whose names contain part of the specified event name
     * @param eventName String
     * @return ArrayList<Event>
     * @throws UserRetrievalException
     * @throws EventServiceException
     * @throws ReservationServiceException
     * @throws RoomServiceException
     */
    public ArrayList<Event> getEventsByName(String eventName) throws UserRetrievalException, EventServiceException, ReservationServiceException, RoomServiceException {
        // preparing query to get all the events containing the name
        String sql = "SELECT * FROM events WHERE event_name LIKE ?";

        // retrieving results
        try {
            // retrieving database connection
            Connection db = getConnection();

            // preparing statement
            PreparedStatement pstm = db.prepareStatement(sql, TYPE_SCROLL_INSENSITIVE, NO_GENERATED_KEYS);
            pstm.setString(1, "%" + eventName + "%");

            ResultSet results = pstm.executeQuery();
            return getEventsFromResultSet(results);

        } catch (ClassNotFoundException | SQLException se) {
            throw new EventServiceException("Exception caught retrieving event referred by event name");
        }
    }

    /**
     * Return a list of all the events within the given start and end timestamps
     * @param startT Timestamp
     * @param endT Timestamp
     * @return ArrayList<Event>
     * @throws UserRetrievalException
     * @throws EventServiceException
     * @throws ReservationServiceException
     * @throws RoomServiceException
     */
    public ArrayList<Event> getEventsByTime(Timestamp startT, Timestamp endT) throws UserRetrievalException, EventServiceException, ReservationServiceException, RoomServiceException {
        // preparing query to retrieve events in the given timeslot
        String sql = "SELECT * FROM events WHERE (start_timestamp, end_timestamp) overlaps (?,?)";

        // retrieving results
        try {
            // retrieving database connection
            Connection db = getConnection();

            // preparing statement
            PreparedStatement pstm = db.prepareStatement(sql, TYPE_SCROLL_INSENSITIVE, NO_GENERATED_KEYS);

            pstm.setTimestamp(1, startT);
            pstm.setTimestamp(2, endT);
            ResultSet results = pstm.executeQuery();
            return getEventsFromResultSet(results);

        } catch (ClassNotFoundException | SQLException se) {
            throw new EventServiceException("Exception caught retrieving event referred by timeslot");
        }
    }

    /**
     * Update an event (or create a new one) given a new Event representation
     * @param event Event
     * @throws EventServiceException
     */
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
            pstmt.setString(1, event.getEventID());
            pstmt.setString(2, event.getEventName());
            pstmt.setTimestamp(3, new Timestamp(event.getStartDateTime().getTime()));
            pstmt.setTimestamp(4, new Timestamp(event.getEndDateTime().getTime()));
            pstmt.setString(5, event.getReferral().getUsername());
            pstmt.setString(6, event.getEmailDL());
            pstmt.execute();
            pstmt.close();

            db.commit();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            throw new EventServiceException("Exception caught updating / creating a new event");
        }


    }

    /**
     * Delete an event
     * @param event Event
     * @throws EventServiceException
     */
    @Override
    public void delete(Event event) throws EventServiceException {
        // preparing the delete query
        String sql = "DELETE FROM events WHERE eventID=?";

        try {
            // retrieving database connection
            Connection db = getConnection();

            // preparing statement
            PreparedStatement pstmt = getConnection().prepareStatement(sql);
            pstmt.setString(1, event.getEventID());
            pstmt.execute();

            db.commit();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            throw new EventServiceException("Exception caught deleting event " + event.getEventID());
        }
    }

    /**
     * Utility to return a list of Event objects from the persistence layer ResultSet
     * @param results ResultSet
     * @return ArrayList<Event>
     * @throws SQLException
     * @throws UserRetrievalException
     * @throws ReservationServiceException
     * @throws RoomServiceException
     */
    public ArrayList<Event> getEventsFromResultSet(ResultSet results) throws SQLException, UserRetrievalException, ReservationServiceException, RoomServiceException {
        ArrayList<Event> events = new ArrayList<>();

        if (!results.isBeforeFirst()) return events;
        results.first();
        while (true) {
            // retrieving the referral as a User
            User referralUser = userDAO.getUserByUsername(results.getString("referral"));

            Event newEvent = new Event(results.getString("eventID"),
                    results.getString("event_name"),
                    new Date(results.getTimestamp("start_timestamp").getTime()),
                    new Date(results.getTimestamp("end_timestamp").getTime()),
                    referralUser,
                    results.getString("mailing_list"));

            // retrieving reserved rooms
            newEvent.getReservedRooms().addAll(roomDAO.getRoomsByEvent(newEvent.getEventID()));

            events.add(newEvent);

            if (!results.next()) break;
        }
        return events;
    }

    /**
     * Utility to return a list of Event objects from the persistence layer ResultSet
     * invoking the UserDAO to associate a User object to any event
     * @param results ResultSet
     * @return ArrayList<Event>
     * @throws SQLException
     * @throws UserRetrievalException
     * @throws ReservationServiceException
     * @throws RoomServiceException
     */
    public ArrayList<Event> getEventsFromResultSet(ResultSet results, User referral) throws SQLException, RoomServiceException, ReservationServiceException {
        ArrayList<Event> events = new ArrayList<>();

        if (!results.isBeforeFirst()) return events;
        results.first();
        while (true) {
            Event newEvent = new Event(results.getString("eventID"),
                    results.getString("event_name"),
                    new Date(results.getTimestamp("start_timestamp").getTime()),
                    new Date(results.getTimestamp("end_timestamp").getTime()),
                    referral,
                    results.getString("mailing_list"));

            // retrieving reserved rooms
            newEvent.getReservedRooms().addAll(roomDAO.getRoomsByEvent(newEvent.getEventID()));

            events.add(newEvent);

            if (!results.next()) break;
        }
        return events;
    }
}
