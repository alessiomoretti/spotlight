package it.uniroma2.ispw.spotlight.services.DataAccesServices;

import it.uniroma2.ispw.spotlight.Constants;
import it.uniroma2.ispw.spotlight.entities.Event;
import it.uniroma2.ispw.spotlight.exceptions.*;
import it.uniroma2.ispw.spotlight.database.EventDAO;


import java.util.ArrayList;

public class UserEventLookupService  extends EventLookupService {

    private Integer minRoleRequired = Constants.TEACHER_ROLE;

    public UserEventLookupService() {
        // setting correct DAO to access event database
        setDatabaseInterface(new EventDAO());
    }

    public ArrayList<Event> getCurrentUserEvents() throws AuthRequiredException, EventServiceException, RoomServiceException, ReservationServiceException {
        if (hasCapability(getCurrentUser())) {
            return ((EventDAO) getDatabaseInterface()).getEventsByReferral(getCurrentUser());
        } else {
            throw new AuthRequiredException("This user has no privileges to access this service");
        }
    }

    public Event getEventByID(String eventID) throws EventServiceException, UserRetrievalException, AuthRequiredException, ReservationServiceException, RoomServiceException {
        if (hasCapability(getCurrentUser())) {
            Event event = ((EventDAO) getDatabaseInterface()).getEventById(eventID);
            return event;
        } else {
            throw new AuthRequiredException("This user has no privileges to access this service");
        }
    }
}
