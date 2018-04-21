package it.uniroma2.ispw.spotlight.services.DataAccesServices;

import it.uniroma2.ispw.spotlight.CalendarHelper;
import it.uniroma2.ispw.spotlight.entities.Event;
import it.uniroma2.ispw.spotlight.exceptions.AuthRequiredException;
import it.uniroma2.ispw.spotlight.exceptions.EventServiceException;
import it.uniroma2.ispw.spotlight.exceptions.UserRetrievalException;
import it.uniroma2.ispw.spotlight.users.Teacher;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class EventManagementServiceTest {

    private EventManagementService eventManagementService = new EventManagementService();
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
    void updateEvent() throws UserRetrievalException, EventServiceException, AuthRequiredException {
        // add user
        eventManagementService.setCurrentUser(testUserT);
        // setting services
        eventManagementService.setRoomManagementService(new RoomManagementService());
        eventManagementService.setEventLookupService(new UserEventLookupService());

        // selecting event
        Event event = eventManagementService.selectEventByID("event01-johndoe-1524341621");     // TODO parametrize
        // updating event
        Date updatedEndDate = CalendarHelper.getDate(22, 4,2018, 16, 0);
        event.setEndDateTime(updatedEndDate);
        eventManagementService.updateEvent(event);

        // selecting event
        event = eventManagementService.selectEventByID("event01-johndoe-1524341621");           // TODO parametrize
        Assertions.assertEquals(event.getEndDateTime(), updatedEndDate);
    }
}