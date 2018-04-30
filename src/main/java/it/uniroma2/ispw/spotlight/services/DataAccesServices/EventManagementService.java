package it.uniroma2.ispw.spotlight.services.DataAccesServices;

import it.uniroma2.ispw.spotlight.Constants;
import it.uniroma2.ispw.spotlight.database.EventDAO;
import it.uniroma2.ispw.spotlight.database.ReservationDAO;
import it.uniroma2.ispw.spotlight.database.RoomDAO;
import it.uniroma2.ispw.spotlight.entities.Event;
import it.uniroma2.ispw.spotlight.exceptions.*;
import it.uniroma2.ispw.spotlight.services.ServiceManager;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;

public class EventManagementService extends DataAccessService<Event> {

    private Integer minRoleRequired = Constants.TEACHER_ROLE;
    private Event currentEvent;

    private RoomManagementService roomManagementService;

    public EventManagementService() {
        // setting correct DAO to access event database
        setDatabaseInterface(new EventDAO());
    }

    public Event createNewEvent(String eventName, Date startT, Date endT) throws EventServiceException, AuthRequiredException {
        if (hasCapability(getCurrentUser())) {
            // generating eventID
            String newEventID = eventName + "-" + getCurrentUser().getUsername() + "-" + String.valueOf(Instant.now().getEpochSecond());
            // generating new event
            Event event = new Event(newEventID, eventName, startT, endT, getCurrentUser(), getCurrentUser().getEmailAddress());
            // inserting event into DB
            ((EventDAO) getDatabaseInterface()).update(event);
            return event;
        } else {
            throw new AuthRequiredException("This user has no privileges to access this service");
        }
    }

    public void updateEvent(Event event) throws AuthRequiredException, EventServiceException {
        if (hasCapability(getCurrentUser())) {
            // updating event
            ((EventDAO) getDatabaseInterface()).update(event);
        } else {
            throw new AuthRequiredException("This user has no privileges to access this service");
        }
    }

    public void deleteEvent(Event event) throws AuthRequiredException, EventServiceException {
        if (hasCapability(getCurrentUser())) {
            // updating event
            ((EventDAO) getDatabaseInterface()).delete(event);
        } else {
            throw new AuthRequiredException("This user has no privileges to access this service");
        }
    }

    public ReservationDAO getReservationDAO() {
        return roomManagementService.getReservationDAO();
    }

    public RoomDAO getRoomDAO() throws AuthRequiredException {
        if (this.roomManagementService == null)
            ServiceManager.getInstance().getRoomManagementService();
        return (RoomDAO) roomManagementService.getDatabaseInterface();
    }

    public void setRoomManagementService(RoomManagementService roomManagementService) {
        this.roomManagementService = roomManagementService;
    }

}
