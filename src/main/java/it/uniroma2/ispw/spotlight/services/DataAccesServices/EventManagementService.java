package it.uniroma2.ispw.spotlight.services.DataAccesServices;

import it.uniroma2.ispw.spotlight.Constants;
import it.uniroma2.ispw.spotlight.database.EventDAO;
import it.uniroma2.ispw.spotlight.database.ReservationDAO;
import it.uniroma2.ispw.spotlight.database.RoomDAO;
import it.uniroma2.ispw.spotlight.entities.Event;
import it.uniroma2.ispw.spotlight.exceptions.AuthRequiredException;
import it.uniroma2.ispw.spotlight.exceptions.EventServiceException;
import it.uniroma2.ispw.spotlight.exceptions.UserRetrievalException;

import java.time.Instant;
import java.util.ArrayList;

public class EventManagementService extends DataAccessService<Event> {

    private Integer minRoleRequired = Constants.TEACHER_ROLE;
    private Event currentEvent;

    private RoomManagementService roomManagementService;
    private UserEventLookupService eventLookupService;

    public EventManagementService() {
        // setting correct DAO to access event database
        setDatabaseInterface(new EventDAO());
    }

    public void createNewEvent(String eventName) throws AuthRequiredException {
        if (hasCapability(getCurrentUser())) {
            // generating eventID
            String newEventID = eventName + "-" + getCurrentUser().getUsername() + "-" + String.valueOf(Instant.now().getEpochSecond());
            // generating new event
            setCurrentEvent(new Event(newEventID, eventName, getCurrentUser()));
        } else {
            throw new AuthRequiredException("This user has no privileges to access this service");
        }
    }

    public ArrayList<Event> getUserEvents() throws EventServiceException, AuthRequiredException {
        return getEventLookupService().getCurrentUserEvents();
    }

    public Event selectEvent(String eventID) throws EventServiceException, UserRetrievalException, AuthRequiredException {
        if (hasCapability(getCurrentUser())) {
            Event event = ((EventDAO) getEventLookupService().getDatabaseInterface()).getEventById(eventID);
            if (event.getReferral().getUsername().equals(getCurrentUser().getUsername()))
                return event;
            else
                return null;
        } else {
            throw new AuthRequiredException("This user has no privileges to access this service");
        }
    }

    public ReservationDAO getReservationDAO() {
        return roomManagementService.getReservationDAO();
    }

    public RoomDAO getRoomDAO() {
        return (RoomDAO) roomManagementService.getDatabaseInterface();
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

    public void setRoomManagementService(RoomManagementService roomManagementService) {
        this.roomManagementService = roomManagementService;
    }

    public void setEventLookupService(UserEventLookupService eventLookupService) {
        this.eventLookupService = eventLookupService;
    }

}
