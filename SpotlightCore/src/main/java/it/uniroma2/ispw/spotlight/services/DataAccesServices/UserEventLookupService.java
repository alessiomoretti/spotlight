package it.uniroma2.ispw.spotlight.services.DataAccesServices;

import it.uniroma2.ispw.spotlight.Constants;
import it.uniroma2.ispw.spotlight.entities.Event;
import it.uniroma2.ispw.spotlight.exceptions.*;
import it.uniroma2.ispw.spotlight.database.EventDAO;


import java.util.ArrayList;


/**
 * This class is a boundary to retrieve Events based upon the current user capabilities
 */
public class UserEventLookupService  extends EventLookupService {

    private Integer minRoleRequired = Constants.TEACHER_ROLE;

    public UserEventLookupService() {
        // setting correct DAO to access event database
        setDatabaseInterface(new EventDAO());
    }

    /**
     * Return all the events of the currently authenticated user
     * @return  ArrayList<Event>
     * @throws AuthRequiredException
     * @throws EventServiceException
     * @throws RoomServiceException
     * @throws ReservationServiceException
     */
    public ArrayList<Event> getCurrentUserEvents() throws AuthRequiredException, EventServiceException, RoomServiceException, ReservationServiceException {
        if (hasCapability(getCurrentUser())) {
            return ((EventDAO) getDatabaseInterface()).getEventsByReferral(getCurrentUser());
        } else {
            throw new AuthRequiredException("This user has no privileges to access this service");
        }
    }

    /**
     * Return an event given its unique identifier (is user can access the resource)
     * @param eventID String
     * @return Event
     * @throws EventServiceException
     * @throws UserRetrievalException
     * @throws AuthRequiredException
     * @throws ReservationServiceException
     * @throws RoomServiceException
     */
    public Event getEventByID(String eventID) throws EventServiceException, UserRetrievalException, AuthRequiredException, ReservationServiceException, RoomServiceException {
        if (hasCapability(getCurrentUser())) {
            Event event = ((EventDAO) getDatabaseInterface()).getEventById(eventID);
            return event;
        } else {
            throw new AuthRequiredException("This user has no privileges to access this service");
        }
    }
}
