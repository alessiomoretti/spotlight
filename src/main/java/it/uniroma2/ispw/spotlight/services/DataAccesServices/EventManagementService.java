package it.uniroma2.ispw.spotlight.services.DataAccesServices;

import it.uniroma2.ispw.spotlight.Constants;
import it.uniroma2.ispw.spotlight.database.EventDAO;
import it.uniroma2.ispw.spotlight.database.ReservationDAO;
import it.uniroma2.ispw.spotlight.entities.Event;
import it.uniroma2.ispw.spotlight.exceptions.AuthRequiredException;
import it.uniroma2.ispw.spotlight.exceptions.EventServiceException;
import it.uniroma2.ispw.spotlight.exceptions.UserRetrievalException;

import java.time.Instant;
import java.util.ArrayList;

public class EventManagementService extends DataAccessService<Event> {

    private Integer minRoleRequired = Constants.TEACHER_ROLE;
    private Event currentEvent;

    private ReservationDAO reservationDAO;
    private UserEventLookupService eventLookupService;

    public EventManagementService() {
        // setting correct DAO to access event database
        setDatabaseInterface(new EventDAO());
        // setting reservation DAO
        this.reservationDAO = new ReservationDAO();
        // setting event lookup service
        this.eventLookupService = new UserEventLookupService();
    }

    public void createNewEvent(String eventName) {
        // generating eventID
        String newEventID = eventName + "-" + getCurrentUser().getUsername() + "-" + String.valueOf(Instant.now().getEpochSecond());
        // generating new event
        setCurrentEvent(new Event(newEventID, eventName, getCurrentUser()));
    }

    public ArrayList<Event> getUserEvents() throws EventServiceException, AuthRequiredException {
        return getEventLookupService().getCurrentUserEvents();
    }

    public Event selectEvent(String eventID) throws EventServiceException, UserRetrievalException {
        Event event = ((EventDAO) getEventLookupService().getDatabaseInterface()).getEventById(eventID);
        if (event.getReferral().getUsername().equals(getCurrentUser().getUsername()))
            return event;
        else
            return null;
    }

    public void updateEvent(Event event) {

    }

    public ReservationDAO getReservationDAO() {
        return reservationDAO;
    }

    public UserEventLookupService getEventLookupService() {
        return eventLookupService;
    }

    public Event getCurrentEvent() {
        return currentEvent;
    }

    public void setCurrentEvent(Event currentEvent) {
        this.currentEvent = currentEvent;
    }

}
