package spotlightweb.servlets;

import it.uniroma2.ispw.spotlight.entities.Event;
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
import java.util.Date;

import static spotlightweb.servlets.ServletConstants.*;
import static spotlightweb.servlets.ServletConstants.END_TIMESTAMP;

public class UpdateEventServlet extends HttpServlet {

    private UserEventLookupService eventLookupService;
    private EventManagementService eventManagementService;

    public UpdateEventServlet() {}

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
        if (request.getParameter(EVENT_UPDATE) == null || request.getParameter(EVENT_ID) == null || request.getParameter(EVENT_NAME) == null ||
            request.getParameter(EVENT_MAIL) == null|| user == null ||
            request.getParameter(START_TIMESTAMP) == null || request.getParameter(END_TIMESTAMP) == null) {
            responseJSON.put(RESPONSE_STATUS, FAILURE);
            responseJSON.put(RESPONSE_ERROR_MESSAGE, "Forbbiden: invalid user authentication and method invocation");
            out.println(responseJSON.toString());
            return;
        }

        // retrieving datetime
        Date startDateTime = new Date(Long.valueOf(request.getParameter(START_TIMESTAMP)));
        Date endDateTime = new Date(Long.valueOf(request.getParameter(END_TIMESTAMP)));

        // UPDATING EVENT
        updateEvent(user,
                    request.getParameter(EVENT_ID),
                    request.getParameter(EVENT_NAME),
                    request.getParameter(EVENT_MAIL),
                    startDateTime, endDateTime,
                    responseJSON);

        // writing result
        out.println(responseJSON.toString());
    }

    private synchronized void updateEvent(User user, String eventID, String eventName, String eventMail, Date startDate, Date endDate, JSONObject responseJSON) {
        // preparing services
        eventManagementService.setCurrentUser(user);
        RoomManagementService roomManagementService = new RoomManagementService();
        roomManagementService.setCurrentUser(user);
        eventManagementService.setRoomManagementService(roomManagementService);
        eventLookupService.setCurrentUser(user);

        // performing event update
        try {
            // retrieving and updating event object
            Event event = eventLookupService.getEventByID(eventID);
            event.setEventName(eventName);
            event.setEmailDL(eventMail);
            event.setStartDateTime(startDate);
            event.setEndDateTime(endDate);

            // updating event on persistence layer
            eventManagementService.updateEvent(event);
            responseJSON.put(RESPONSE_SUCCESS_MESSAGE, "Event " + eventName + " update completed!");
            responseJSON.put(RESPONSE_STATUS, SUCCESS);

        } catch (AuthRequiredException | UserRetrievalException e) {
            e.printStackTrace();
            responseJSON.put(RESPONSE_STATUS, FAILURE);
            responseJSON.put(RESPONSE_ERROR_MESSAGE, "Forbidden: error on user authentication");
        } catch (EventServiceException | ReservationServiceException | RoomServiceException e) {
            e.printStackTrace();
            responseJSON.put(RESPONSE_STATUS, FAILURE);
            responseJSON.put(RESPONSE_ERROR_MESSAGE, "Error trying update event " + eventName);
        }
    }
}
