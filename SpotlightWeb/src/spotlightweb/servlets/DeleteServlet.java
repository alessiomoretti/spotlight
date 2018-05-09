package spotlightweb.servlets;

import it.uniroma2.ispw.spotlight.exceptions.*;
import it.uniroma2.ispw.spotlight.services.DataAccesServices.EventManagementService;
import it.uniroma2.ispw.spotlight.services.DataAccesServices.RoomManagementService;
import it.uniroma2.ispw.spotlight.services.DataAccesServices.UserEventLookupService;
import it.uniroma2.ispw.spotlight.users.User;
import org.json.JSONObject;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

import static spotlightweb.servlets.ServletConstants.*;
import static spotlightweb.servlets.ServletConstants.FAILURE;
import static spotlightweb.servlets.ServletConstants.RESPONSE_ERROR_MESSAGE;

public class DeleteServlet extends HttpServlet {

    private UserEventLookupService eventLookupService;
    private EventManagementService eventManagementService;
    private RoomManagementService roomManagementService;

    public DeleteServlet() {}

    public void init() {
        this.eventLookupService = new UserEventLookupService();
        this.eventManagementService = new EventManagementService();
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // preparing response
        JSONObject responseJSON = new JSONObject();
        // getting output writer
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        // retrieving user from request session
        User user =  (User) request.getSession().getAttribute(CURRENT_USER);

        // check if valid request and session
        if (!(request.getParameter(EVENT_DELETE) != null && request.getParameter(EVENT_ID) != null) ||
            ! (request.getParameter(RESERVATION_DELETE) != null && request.getParameter(RESERVATION_ID) != null)
            || user == null) {
            responseJSON.put(RESPONSE_STATUS, FAILURE);
            responseJSON.put(RESPONSE_ERROR_MESSAGE, "Forbbiden: invalid user authentication and method invocation");
            out.println(responseJSON.toString());
            return;
        }

        // DELETING EVENT
        if (request.getParameter(EVENT_DELETE) != null) {
            deleteEvent(user, request.getParameter(EVENT_ID), responseJSON);
        }
        // DELETING RESERVATION
        if (request.getParameter(RESERVATION_DELETE) != null) {
            deleteReservation(user, request.getParameter(RESERVATION_ID), responseJSON);
        }

        // writing result
        out.println(responseJSON.toString());
    }

    private synchronized void deleteEvent(User user, String eventID, JSONObject responseJSON) {
        // preparing services
        eventManagementService.setCurrentUser(user);
        roomManagementService = new RoomManagementService();
        roomManagementService.setCurrentUser(user);
        eventManagementService.setRoomManagementService(roomManagementService);
        eventLookupService.setCurrentUser(user);

        try {
            // deleting event by ID
            eventManagementService.deleteEvent(eventLookupService.getEventByID(eventID));
            responseJSON.put(RESPONSE_SUCCESS_MESSAGE, "Event " + eventID + " deleted");
            responseJSON.put(RESPONSE_STATUS, SUCCESS);
        } catch (AuthRequiredException | UserRetrievalException e) {
            e.printStackTrace();
            responseJSON.put(RESPONSE_STATUS, FAILURE);
            responseJSON.put(RESPONSE_ERROR_MESSAGE, "Forbidden: error on user authentication");
        } catch (EventServiceException | ReservationServiceException | RoomServiceException e) {
            e.printStackTrace();
            responseJSON.put(RESPONSE_STATUS, FAILURE);
            responseJSON.put(RESPONSE_ERROR_MESSAGE, "Error trying delete event " + eventID);
        }
    }

    private synchronized void deleteReservation(User user, String reservationID, JSONObject responseJSON) {
        roomManagementService = new RoomManagementService();
        roomManagementService.setCurrentUser(user);

        try {
            // deleting reservation by ID
            roomManagementService.deleteRoomReservationByID(reservationID);
            responseJSON.put(RESPONSE_SUCCESS_MESSAGE, "Reservation " + reservationID + " deleted");
            responseJSON.put(RESPONSE_STATUS, SUCCESS);

        } catch (AuthRequiredException e) {
            e.printStackTrace();
            responseJSON.put(RESPONSE_STATUS, FAILURE);
            responseJSON.put(RESPONSE_ERROR_MESSAGE, "Forbidden: error on user authentication");
        } catch (ReservationServiceException e) {
            e.printStackTrace();
            responseJSON.put(RESPONSE_STATUS, FAILURE);
            responseJSON.put(RESPONSE_ERROR_MESSAGE, "Error trying delete reservation " + reservationID);
        }
    }
}
