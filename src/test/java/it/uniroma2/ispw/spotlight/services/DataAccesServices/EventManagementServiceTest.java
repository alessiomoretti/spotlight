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
    void createNewEvent() throws EventServiceException, AuthRequiredException {
        // add user
        eventManagementService.setCurrentUser(testUserT);

        // create and store event
        Event event = eventManagementService.createNewEvent(eventName, start_date, end_date);
        Assertions.assertEquals(event.getEventName(), eventName);
        Assertions.assertEquals(event.getReferral().getUsername(), testUserT.getUsername());
        Assertions.assertEquals(event.getStartDateTime(), start_date);
        Assertions.assertEquals(event.getEndDateTime(), end_date);
        Assertions.assertEquals(event.getEmailDL(), testUserT.getEmailAddress());
    }

    @Test
    void updateEvent() throws UserRetrievalException, EventServiceException, AuthRequiredException, ReservationServiceException, RoomServiceException {
        // add user
        userEventLookupService.setCurrentUser(testUserT);
        // add user
        eventManagementService.setCurrentUser(testUserT);
        // setting services
        eventManagementService.setRoomManagementService(new RoomManagementService());

        // selecting event
        Event event = userEventLookupService.getEventByID("event01-johndoe-1524341621");     // TODO parametrize
        // updating event
        Date updatedEndDate = CalendarHelper.getDate(22, 4,2018, 16, 0);
        event.setEndDateTime(updatedEndDate);
        eventManagementService.updateEvent(event);

        // selecting event
        event = userEventLookupService.getEventByID("event01-johndoe-1524341621");           // TODO parametrize
        Assertions.assertEquals(event.getEndDateTime(), updatedEndDate);
    }
}