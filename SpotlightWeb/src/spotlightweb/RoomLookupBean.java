package spotlightweb;

import it.uniroma2.ispw.spotlight.Constants;
import it.uniroma2.ispw.spotlight.entities.Room.Reservation;
import it.uniroma2.ispw.spotlight.entities.Room.Room;
import it.uniroma2.ispw.spotlight.exceptions.*;
import it.uniroma2.ispw.spotlight.services.DataAccesServices.RoomLookupService;
import it.uniroma2.ispw.spotlight.services.DataAccesServices.UserEventLookupService;
import it.uniroma2.ispw.spotlight.users.User;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class RoomLookupBean {

    private RoomLookupService roomLookupService;
    private UserEventLookupService userEventLookupService;

    private Integer currentUserRole = Constants.TEACHER_ROLE;

    private String errorMessage;

    public RoomLookupBean() {
        this.roomLookupService = new RoomLookupService();
        this.userEventLookupService = new UserEventLookupService();
    }

    public void setCurrentUser(User user) {
        this.roomLookupService.setCurrentUser(user);
        this.userEventLookupService.setCurrentUser(user);

        // user role will be used to determine which level of information show
        // about room reservation (only 'RESERVED' or the event to which it is reserved)
        currentUserRole = user.getRole();
    }

    public ArrayList<Room> getRooms() {
        try {
            // retrieving all rooms
            return roomLookupService.getAllRooms();
        } catch (AuthRequiredException e) {
            e.printStackTrace();
            this.errorMessage = "Error occured retrieving user authentication";
            return null;
        } catch (ReservationServiceException | RoomServiceException e) {
            e.printStackTrace();
            this.errorMessage = "Error occured retrieving rooms and reservations";
            return null;
        }
    }

    public String getRoomsReservationsJSON(ArrayList<Room> rooms) {
        // returning the reservations JSON in order to manipulate the UI on client side

        SimpleDateFormat dfDay = new SimpleDateFormat("MMM dd, HH:mm");

        JSONObject reservationsJSON = new JSONObject();

        for (Room room : rooms) {
            ArrayList<JSONObject> reservations = new ArrayList<>();
            for (Reservation reservation : room.getReservations()) {
                JSONObject jsonDOC = new JSONObject();
                // selecting type of reservation info
                if (currentUserRole < Constants.ADMINISTRATIVE_ROLE)
                    jsonDOC.put("reservation", "RESERVED");
                else {
                    try {
                        jsonDOC.put("reservation", userEventLookupService.getEventByID(reservation.getEventID()).getEventName());
                    } catch (AuthRequiredException | UserRetrievalException e) {
                        e.printStackTrace();
                        this.errorMessage = "Error occured retrieving user authentication";
                        return "";
                    } catch (EventServiceException | ReservationServiceException |  RoomServiceException e) {
                        e.printStackTrace();
                        this.errorMessage = "Error occured retrieving reservation event";
                        return "";
                    }
                }

                jsonDOC.put("reservationStart", dfDay.format(reservation.getStartDateTime()));
                jsonDOC.put("reservationEnd", dfDay.format(reservation.getEndDateTime()));

                reservations.add(jsonDOC);
            }

            reservationsJSON.put(room.getRoomID(), reservations);
        }

        return reservationsJSON.toString();
    }

    public String getErrorMessage() { return this.errorMessage; }
}
