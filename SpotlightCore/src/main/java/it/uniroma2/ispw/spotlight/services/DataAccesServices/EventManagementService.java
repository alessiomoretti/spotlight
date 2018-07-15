package it.uniroma2.ispw.spotlight.services.DataAccesServices;

import it.uniroma2.ispw.spotlight.Constants;
import it.uniroma2.ispw.spotlight.database.EventDAO;
import it.uniroma2.ispw.spotlight.database.ReservationDAO;
import it.uniroma2.ispw.spotlight.database.RoomDAO;
import it.uniroma2.ispw.spotlight.entities.Event;
import it.uniroma2.ispw.spotlight.entities.Room.Reservation;
import it.uniroma2.ispw.spotlight.entities.Room.Room;
import it.uniroma2.ispw.spotlight.exceptions.*;
import it.uniroma2.ispw.spotlight.helpers.MD5Helper;
import it.uniroma2.ispw.spotlight.services.ServiceManager;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;

/**
 *  This boundary service can be used to handle and manage Event objects
 */
public class EventManagementService extends DataAccessService<Event> {

    private Integer minRoleRequired = Constants.TEACHER_ROLE;
    private Event currentEvent;

    private RoomManagementService roomManagementService;

    public EventManagementService() {
        // setting correct DAO to access event database
        setDatabaseInterface(new EventDAO());
    }

    /**
     * Return a newly created Event
     * @param eventName String
     * @param startT Date
     * @param endT Date
     * @return Event
     * @throws EventServiceException
     * @throws AuthRequiredException
     */
    public Event createNewEvent(String eventName, Date startT, Date endT) throws EventServiceException, AuthRequiredException {
        if (hasCapability(getCurrentUser())) {
            // generating eventID
            String newEventID = MD5Helper.getHashedString(eventName + "-" + getCurrentUser().getUsername() + "-" + String.valueOf(Instant.now().getEpochSecond()));
            // generating new event
            Event event = new Event(newEventID, eventName, startT, endT, getCurrentUser(), getCurrentUser().getEmailAddress());
            // inserting event into DB
            ((EventDAO) getDatabaseInterface()).update(event);
            return event;
        } else {
            throw new AuthRequiredException("This user has no privileges to access this service");
        }
    }

    /**
     * Update an event given its new representation
     * @param event Event
     * @throws AuthRequiredException
     * @throws EventServiceException
     */
    public void updateEvent(Event event) throws AuthRequiredException, EventServiceException {
        if (hasCapability(getCurrentUser())) {
            // updating event
            ((EventDAO) getDatabaseInterface()).update(event);
        } else {
            throw new AuthRequiredException("This user has no privileges to access this service");
        }
    }

    /**
     * Delete the event with the given representation
     * @param event Event
     * @throws AuthRequiredException
     * @throws EventServiceException
     * @throws ReservationServiceException
     * @throws RoomServiceException
     */
    public void deleteEvent(Event event) throws AuthRequiredException, EventServiceException, ReservationServiceException, RoomServiceException {
        if (hasCapability(getCurrentUser())) {
            // deleting all reservations
            for (Room room : event.getReservedRooms()) {
                for (Reservation reservation : room.getReservations()) {
                    if (reservation.getEventID().equals(event.getEventID())) {
                        getReservationDAO().delete(reservation);
                    }
                }
            }
            // deleting event
            ((EventDAO) getDatabaseInterface()).delete(event);
        } else {
            throw new AuthRequiredException("This user has no privileges to access this service");
        }
    }

    /**
     * Return the ReservationDAO associated
     * @return ReservationDAO
     */
    public ReservationDAO getReservationDAO() {
        return roomManagementService.getReservationDAO();
    }

    /**
     * Return the RoomDAO associated
     * @return RoomDAO
     * @throws AuthRequiredException
     */
    public RoomDAO getRoomDAO() throws AuthRequiredException {
        if (this.roomManagementService == null)
            ServiceManager.getInstance().getRoomManagementService();
        return (RoomDAO) roomManagementService.getDatabaseInterface();
    }

    /**
     * Set a RoomManagementService to delegate the Room / Reservation update and delete operations
     * @param roomManagementService RoomManagementService
     */
    public void setRoomManagementService(RoomManagementService roomManagementService) {
        this.roomManagementService = roomManagementService;
    }

}
