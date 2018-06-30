package it.uniroma2.ispw.spotlight.services.DataAccesServices;

import it.uniroma2.ispw.spotlight.exceptions.*;
import it.uniroma2.ispw.spotlight.helpers.CalendarHelper;
import it.uniroma2.ispw.spotlight.entities.Event;
import it.uniroma2.ispw.spotlight.users.Teacher;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Date;

class EventManagementServiceTest {

    private EventManagementService eventManagementService = new EventManagementService();
    private UserEventLookupService userEventLookupService = new UserEventLookupService();
    private Teacher testUserT = new Teacher("johndoe", "John", "Doe", "john.doe@uni.com", "History");

    private String eventName = "event01";
    private Date start_date = CalendarHelper.getDate(22, 4, 2018, 9, 0);
    private Date end_date   = CalendarHelper.getDate(22, 4,2018, 18, 0);


    @Test
    void createAndUpdateEvent() throws EventServiceException, AuthRequiredException, RoomServiceException, ReservationServiceException, UserRetrievalException {
        // add user
        eventManagementService.setCurrentUser(testUserT);
        userEventLookupService.setCurrentUser(testUserT);

        // -> create and store event
        Event event = eventManagementService.createNewEvent(eventName, start_date, end_date);

        Assertions.assertEquals(event.getEventName(), eventName);
        Assertions.assertEquals(event.getReferral().getUsername(), testUserT.getUsername());
        Assertions.assertEquals(event.getStartDateTime(), start_date);
        Assertions.assertEquals(event.getEndDateTime(), end_date);
        Assertions.assertEquals(event.getEmailDL(), testUserT.getEmailAddress());

        // retrieving created event
        Event createdEvent = userEventLookupService.getEventByID(event.getEventID());
        Assertions.assertEquals(createdEvent.getEventName(), event.getEventName());

        // -> updating event
        Date updatedEndDate = CalendarHelper.getDate(22, 4,2018, 16, 0);
        createdEvent.setEndDateTime(updatedEndDate);
        eventManagementService.updateEvent(createdEvent);

        // -> retrieving updated event
        Event retEvent = userEventLookupService.getEventByID(createdEvent.getEventID());
        Assertions.assertEquals(retEvent.getEndDateTime(), createdEvent.getEndDateTime());

        // -> deleting event
        eventManagementService.deleteEvent(createdEvent);
    }
}