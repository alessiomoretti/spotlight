package spotlightweb;

import it.uniroma2.ispw.spotlight.entities.Event;
import it.uniroma2.ispw.spotlight.entities.Room.Reservation;
import it.uniroma2.ispw.spotlight.entities.Room.Room;
import it.uniroma2.ispw.spotlight.exceptions.*;
import it.uniroma2.ispw.spotlight.helpers.CalendarHelper;
import it.uniroma2.ispw.spotlight.services.DataAccesServices.EventLookupService;
import it.uniroma2.ispw.spotlight.services.DataAccesServices.UserEventLookupService;
import it.uniroma2.ispw.spotlight.users.User;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class EventLookupBean {

    private EventLookupService eventLookupService;
    private UserEventLookupService userEventLookupService;

    private String eventNameSearchText;
    private Date eventSearchDate;
    private String eventDateSearchText;

    private String errorMessage;


    public EventLookupBean() {
        this.eventLookupService = new EventLookupService();
        this.userEventLookupService = new UserEventLookupService();

        this.eventNameSearchText = "";
        this.eventDateSearchText = "";
    }

    public ArrayList<Event> searchEvents() {
        System.out.println(eventNameSearchText);
        System.out.println(eventDateSearchText);

        // check on parameters
        if (!checkSearchParameters())
            return null;

        try {
            return eventLookupService.getEventsByNameAndStartDate(eventNameSearchText, eventSearchDate);
        } catch (AuthRequiredException | UserRetrievalException e) {
            e.printStackTrace();
            this.errorMessage = "Error occured retrieving user authentication";
            return null;
        } catch (EventServiceException | ReservationServiceException |  RoomServiceException e) {
            e.printStackTrace();
            this.errorMessage = "Error occured retrieving events";
            return null;
        }
    }

    public ArrayList<Event> searchUserEvents() {
        try {
            return userEventLookupService.getCurrentUserEvents();
        } catch (AuthRequiredException e) {
            e.printStackTrace();
            this.errorMessage = "Error occured retrieving user authentication";
            return null;
        } catch (EventServiceException | ReservationServiceException |  RoomServiceException e) {
            e.printStackTrace();
            this.errorMessage = "Error occured retrieving user events";
            return null;
        }
    }

    public boolean checkSearchParameters() {
        // search parameters must be not empty
        if (eventNameSearchText.length() == 0 || eventDateSearchText.length() == 0) {
            this.errorMessage = "Invalid parameters: type a non empty string and a valid date MM/DD/YYYY";
            return false;
        }

        // building up the search date if not empty
        try {
            String[] d = eventDateSearchText.split("/");
            eventSearchDate = CalendarHelper.getDate(Integer.valueOf(d[1]), Integer.valueOf(d[0]), Integer.valueOf(d[2]), 0, 0);
        } catch (Exception e) {
            this.errorMessage = "Invalid date format";
            return false;
        }

        // check on date
        if (eventSearchDate.before(CalendarHelper.getToday())) {
            this.errorMessage = "The date must be a present or future one";
            return false;
        }

        return true;
    }

    public static String getEventRoomsJSON(ArrayList<Event> events) {
        // returning the rooms JSON in order to manipulate the UI on client side

        SimpleDateFormat dfDay = new SimpleDateFormat("MMM dd, HH:mm");

        JSONObject reservationsJSON = new JSONObject();

        for (Event event : events) {
            ArrayList<JSONObject> reservations = new ArrayList<>();
            for (Room room : event.getReservedRooms()) {
                for (Reservation reservation : room.getReservations()) {
                    if (reservation.getEventID().equals(event.getEventID())) {
                        JSONObject jsonDoc = new JSONObject();
                        jsonDoc.put("reservationID", reservation.getReservationID());
                        jsonDoc.put("roomName", room.getRoomName());
                        jsonDoc.put("roomDepartment", room.getRoomDepartment());
                        jsonDoc.put("reservationStart", dfDay.format(reservation.getStartDateTime()));
                        jsonDoc.put("reservationEnd", dfDay.format(reservation.getEndDateTime()));
                        reservations.add(jsonDoc);
                    }
                }
            }

            reservationsJSON.put(event.getEventID(), reservations);
        }

        return reservationsJSON.toString();
    }

    public void setCurrentUser(User user) {
        eventLookupService.setCurrentUser(user);
        userEventLookupService.setCurrentUser(user);
    }

    public User getCurrentUser() throws AuthRequiredException{
        return eventLookupService.getCurrentUser();
    }


    public String getEventNameSearchText() {
        return eventNameSearchText;
    }

    public void setEventNameSearchText(String eventNameSearchText) {
        this.eventNameSearchText = eventNameSearchText;
    }

    public Date getEventSearchDate() {
        return eventSearchDate;
    }

    public void setEventSearchDate(Date eventSearchDate) {
        this.eventSearchDate = eventSearchDate;
    }

    public String getEventDateSearchText() {
        return eventDateSearchText;
    }

    public void setEventDateSearchText(String eventSearchDateText) {
        this.eventDateSearchText = eventSearchDateText;
    }

    public String getErrorMessage() { return this.errorMessage; }

}
