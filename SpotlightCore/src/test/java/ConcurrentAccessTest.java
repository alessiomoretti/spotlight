import it.uniroma2.ispw.spotlight.entities.Event;
import it.uniroma2.ispw.spotlight.exceptions.*;
import it.uniroma2.ispw.spotlight.helpers.CalendarHelper;
import it.uniroma2.ispw.spotlight.services.ServiceManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Date;

import static it.uniroma2.ispw.spotlight.Constants.INFOPOINT_ROLE;

public class ConcurrentAccessTest {

    private class ConcurrentAccessRunnable implements Runnable {

        private String username;
        private String hashedPwd;

        private String eventName;
        private String updatedEventName;
        private Date startDate;
        private Date endDate;

        public void setLogin(String username, String hashedPwd) {
            this.username = username;
            this.hashedPwd = hashedPwd;
        }

        public void setEventParameters(String eventName, Date startDate, Date endDate) {
            this.eventName = eventName;
            this.startDate = startDate;
            this.endDate = endDate;
        }

        public void setUpdateEventParameters(String updatedEventName) {
            this.updatedEventName = updatedEventName;
        }

        @Override
        public void run() {
            // check if thread has all the variables to simulate concurrent access
            if (username == null || hashedPwd == null) {
                System.err.println("[LOGIN TEST] invalid login test thread - specify username or pwd");
            }

            // a service manager must be created for each client
            ServiceManager sm = new ServiceManager();
            try {
                // -> testing user login
                boolean logged = sm.getLoginService().authenticateUser(username, hashedPwd);
                Assertions.assertTrue(logged);

                if (sm.getLoginService().getCurrentUser().getRole() != INFOPOINT_ROLE) {

                    if (eventName == null || startDate == null || endDate == null) {
                        System.err.println("LOGIN TEST] specify event name, start and end dates");
                    }

                    // -> testing event creation
                    Event event = sm.getEventManagementService().createNewEvent(eventName, startDate, endDate);
                    Assertions.assertNotNull(event);

                    // -> testing event update
                    event.setEventName(updatedEventName);
                    sm.getEventManagementService().updateEvent(event);
                    Event retEvent = sm.getUserEventLookupService().getEventByID(event.getEventID());
                    Assertions.assertNotNull(retEvent);
                    Assertions.assertEquals(retEvent.getEventName(), updatedEventName);

                    // -> deleting event
                    sm.getEventManagementService().deleteEvent(event);
                    retEvent = sm.getUserEventLookupService().getEventByID(event.getEventID());
                    Assertions.assertNull(retEvent);
                }

            } catch (AuthServiceException | AuthRequiredException | UserRetrievalException e) {
                e.printStackTrace();
            } catch (EventServiceException | ReservationServiceException | RoomServiceException e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void concurrentAccessTest() {
        // preparing first user (TEACHER) runnable
        ConcurrentAccessRunnable concurrentAccess01 = new ConcurrentAccessRunnable();
        concurrentAccess01.setLogin("johndoe", "d763ec748433fb79a04f82bd46133d55");
        concurrentAccess01.setEventParameters("event01Test", CalendarHelper.getDate(22, 4, 2018, 9, 0), CalendarHelper.getDate(22, 4, 2018, 18, 0));
        concurrentAccess01.setUpdateEventParameters("event01TestUpdated");

        // preparing second user (ADMINISTRATIVE) runnable
        ConcurrentAccessRunnable concurrentAccess02 = new ConcurrentAccessRunnable();
        concurrentAccess01.setLogin("jennyseed", "d63f0ccf73571e11d728612f478738f5");
        concurrentAccess01.setEventParameters("event02Test", CalendarHelper.getDate(22, 4, 2018, 9, 0), CalendarHelper.getDate(22, 4, 2018, 18, 0));
        concurrentAccess01.setUpdateEventParameters("event02TestUpdated");

        // preparing third user (INFOPOINT) runnable
        ConcurrentAccessRunnable concurrentAccess03 = new ConcurrentAccessRunnable();
        concurrentAccess01.setLogin("jackblack", "4e661d7c21bb2711960c00ad3f31ca65");

        // executing parallel threads
        Thread t1 = new Thread(concurrentAccess01, "concurrent-01");
        t1.start();
        Thread t2 = new Thread(concurrentAccess01, "concurrent-02");
        t2.start();
        Thread t3 = new Thread(concurrentAccess01, "concurrent-03");
        t3.start();

    }
}
