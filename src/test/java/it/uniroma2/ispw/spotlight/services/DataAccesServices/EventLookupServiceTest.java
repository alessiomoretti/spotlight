package it.uniroma2.ispw.spotlight.services.DataAccesServices;

import it.uniroma2.ispw.spotlight.helpers.CalendarHelper;
import it.uniroma2.ispw.spotlight.entities.Event;
import it.uniroma2.ispw.spotlight.exceptions.AuthRequiredException;
import it.uniroma2.ispw.spotlight.exceptions.EventServiceException;
import it.uniroma2.ispw.spotlight.exceptions.UserRetrievalException;
import it.uniroma2.ispw.spotlight.users.InfoPointCrewMember;
import it.uniroma2.ispw.spotlight.users.Teacher;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Date;

class EventLookupServiceTest {

    private EventLookupService eventLookupService = new EventLookupService();
    private UserEventLookupService userEventLookupService = new UserEventLookupService();

    private Teacher testUserT = new Teacher("johndoe", "John", "Doe", "john.doe@uni.com", "History");
    private InfoPointCrewMember testUserI = new InfoPointCrewMember("jackblack", "Jack", "Black", "jack.black@uni.com");

    private String partialEventName = "test";
    private String completeEventName = "testevent";
    private Date start_date = CalendarHelper.getDate(22, 4, 2018, 9, 0);
    private Date end_date   = CalendarHelper.getDate(22, 4,2018, 11, 0);

    @Test
    void getEventsByNameAndTime() throws EventServiceException, UserRetrievalException, AuthRequiredException {
        eventLookupService.setCurrentUser(testUserI);

        ArrayList<Event> events = eventLookupService.getEventsByNameAndStartDate(partialEventName, start_date);
        for(Event event : events) {
            Assertions.assertEquals(events.get(0).getEventName(), completeEventName);
            Assertions.assertTrue(((events.get(0).getStartDateTime().getTime() <= end_date.getTime()) &&
                    (start_date.getTime() <= events.get(0).getEndDateTime().getTime())));
        }

    }

    @Test
    void getEventsOfCurrentUser() throws EventServiceException, AuthRequiredException {
        userEventLookupService.setCurrentUser(testUserT);

        ArrayList<Event> events = userEventLookupService.getCurrentUserEvents();
        for(Event event : events) {
            Assertions.assertEquals(event.getReferral().getUsername(), testUserT.getUsername());
        }
    }
}