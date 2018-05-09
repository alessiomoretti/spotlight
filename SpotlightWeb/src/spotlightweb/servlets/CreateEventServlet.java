package spotlightweb.servlets;

import it.uniroma2.ispw.spotlight.entities.Event;
import it.uniroma2.ispw.spotlight.exceptions.AuthRequiredException;
import it.uniroma2.ispw.spotlight.exceptions.EventServiceException;
import it.uniroma2.ispw.spotlight.services.DataAccesServices.EventManagementService;
import it.uniroma2.ispw.spotlight.services.DataAccesServices.RoomManagementService;
import it.uniroma2.ispw.spotlight.users.User;
import org.json.JSONObject;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

import static spotlightweb.servlets.ServletConstants.*;

public class CreateEventServlet extends HttpServlet {

    private EventManagementService eventManagementService;

    public CreateEventServlet() { }

    public void init() { this.eventManagementService = new EventManagementService(); }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // preparing response
        JSONObject responseJSON = new JSONObject();
        // getting output writer
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        // retrieving user from request session
        User user =  (User) request.getSession().getAttribute(CURRENT_USER);

        // check if valid request and session
        if (request.getParameter(EVENT_CREATE) == null || user == null || request.getParameter(EVENT_NAME) == null ||
            request.getParameter(START_TIMESTAMP) == null || request.getParameter(END_TIMESTAMP) == null) {
            responseJSON.put(RESPONSE_STATUS, FAILURE);
            responseJSON.put(RESPONSE_ERROR_MESSAGE, "Forbbiden: invalid user authentication and method invocation");
            out.println(responseJSON.toString());
            return;
        }

        // retrieving datetime
        Date startDateTime = new Date(Long.valueOf(request.getParameter(START_TIMESTAMP)));
        Date endDateTime = new Date(Long.valueOf(request.getParameter(END_TIMESTAMP)));

        // EVENT CREATION
        Event e = createEvent(user, request.getParameter(EVENT_NAME), startDateTime, endDateTime, responseJSON);
        if (e != null) {
            responseJSON.put(RESPONSE_SUCCESS_MESSAGE, "Event created with ID: " + e.getEventID());
            responseJSON.put(RESPONSE_STATUS, SUCCESS);
        }

        // writing result
        out.println(responseJSON.toString());
    }

    private synchronized Event createEvent(User user, String eventName, Date startDate, Date endDate, JSONObject responseJSON) {
        // preparing services
        eventManagementService.setCurrentUser(user);
        RoomManagementService roomManagementService = new RoomManagementService();
        roomManagementService.setCurrentUser(user);
        eventManagementService.setRoomManagementService(roomManagementService);

        // performing event creation
        try {
            return eventManagementService.createNewEvent(eventName, startDate, endDate);
        } catch (AuthRequiredException e) {
            e.printStackTrace();
            responseJSON.put(RESPONSE_STATUS, FAILURE);
            responseJSON.put(RESPONSE_ERROR_MESSAGE, "Forbidden: error on user authentication");
            return null;
        } catch (EventServiceException e) {
            e.printStackTrace();
            responseJSON.put(RESPONSE_STATUS, FAILURE);
            responseJSON.put(RESPONSE_ERROR_MESSAGE, "Error trying to create event " + eventName);
            return null;
        }
    }
}
