package it.uniroma2.ispw.spotlight.services.DataAccesServices;

import it.uniroma2.ispw.spotlight.Constants;
import it.uniroma2.ispw.spotlight.entities.Event;
import it.uniroma2.ispw.spotlight.exceptions.AuthRequiredException;
import it.uniroma2.ispw.spotlight.database.EventDAO;
import it.uniroma2.ispw.spotlight.exceptions.EventServiceException;
import it.uniroma2.ispw.spotlight.exceptions.UserRetrievalException;

import java.util.ArrayList;
import java.util.Date;

public class EventLookupService extends DataAccessService<Event> {

    private Integer minRoleRequired = Constants.INFOPOINT_ROLE;

    public EventLookupService() {
        // setting correct DAO to access rooms database
        setDatabaseInterface(new EventDAO());
    }

    public ArrayList<Event> getEventsByNameAndTime(String eventName, Date start, Date end) throws AuthRequiredException, UserRetrievalException, EventServiceException {
        if (hasCapability(getCurrentUser())) {
            // retrieving all the events containing the given name
            ArrayList<Event> retrievedEvents = ((EventDAO) getDatabaseInterface()).getEventsByName(eventName);

            // initializing result array
            ArrayList<Event> events = new ArrayList<>();
            // iterating over retrieved events to select the ones present or future
            for (Event event : retrievedEvents) {
                if ((event.getStartDateTime().before(start) || event.getStartDateTime().equals(start)) || event.getEndDateTime().after(end))
                     events.add(event);

            }
            return events;
        } else {
            throw new AuthRequiredException("This user has no privileges to access this service");
        }
    }
}
