package it.uniroma2.ispw.spotlight.services.DataAccesServices;

import it.uniroma2.ispw.spotlight.Constants;
import it.uniroma2.ispw.spotlight.entities.Event;
import it.uniroma2.ispw.spotlight.exceptions.*;
import it.uniroma2.ispw.spotlight.database.EventDAO;

import java.util.ArrayList;
import java.util.Date;

public class EventLookupService extends DataAccessService<Event> {

    private Integer minRoleRequired = Constants.INFOPOINT_ROLE;

    public EventLookupService() {
        // setting correct DAO to access event database
        setDatabaseInterface(new EventDAO());
    }

    public ArrayList<Event> getEventsByNameAndStartDate(String eventName, Date start) throws AuthRequiredException, UserRetrievalException, EventServiceException, ReservationServiceException, RoomServiceException {
        if (hasCapability(getCurrentUser())) {
            // retrieving all the events containing the given name
            ArrayList<Event> retrievedEvents = ((EventDAO) getDatabaseInterface()).getEventsByName(eventName);
            // initializing result array
            ArrayList<Event> events = new ArrayList<>();
            // iterating over retrieved events to select the ones present or future
            for (Event event : retrievedEvents) {
                if(event.getStartDateTime().getTime() <= start.getTime() || event.getEndDateTime().getTime() >= start.getTime())
                    events.add(event);
            }
            return events;
        } else {
            throw new AuthRequiredException("This user has no privileges to access this service");
        }
    }
}
