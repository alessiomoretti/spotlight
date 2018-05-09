package spotlightweb.servlets;


import it.uniroma2.ispw.spotlight.entities.Room.Reservation;
import it.uniroma2.ispw.spotlight.entities.Room.RoomProperties;
import it.uniroma2.ispw.spotlight.exceptions.AuthRequiredException;
import it.uniroma2.ispw.spotlight.exceptions.ReservationServiceException;
import it.uniroma2.ispw.spotlight.exceptions.RoomServiceException;
import it.uniroma2.ispw.spotlight.services.DataAccesServices.RoomLookupService;
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

public class AddReservationServlet extends HttpServlet {

    private RoomManagementService roomManagementService;

    public AddReservationServlet() {}

    public void init() {
        roomManagementService = new RoomManagementService();
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
        if (request.getParameter(ADD_RESERVATION) == null || user == null ||
            request.getParameter(RESERVATION_DEPARTMENT) == null || request.getParameter(EVENT_ID) == null ||
            request.getParameter(START_TIMESTAMP) == null || request.getParameter(END_TIMESTAMP) == null) {
            responseJSON.put(RESPONSE_STATUS, FAILURE);
            responseJSON.put(RESPONSE_ERROR_MESSAGE, "Forbbiden: invalid user authentication and method invocation");
            out.println(responseJSON.toString());
            return;
        }

        RoomProperties roomProperties = null;
        try {
            // creating the room properties object
            roomProperties = new RoomProperties(
                    Integer.valueOf(request.getParameter(RESERVATION_CAPACITY)),
                    Boolean.valueOf(request.getParameter(RESERVATION_HAS_PROJECTOR)),
                    Boolean.valueOf(request.getParameter(RESERVATION_HAS_WHITEBOARD)),
                    Boolean.valueOf(request.getParameter(RESERVATION_HAS_INT_WHITEBOARD)),
                    Boolean.valueOf(request.getParameter(RESERVATION_VIDEOCALL_CAPABLE)),
                    Boolean.valueOf(request.getParameter(RESERVATION_HAS_MICROPHONE)));

        } catch (Exception e) {
            responseJSON.put(RESPONSE_STATUS, FAILURE);
            responseJSON.put(RESPONSE_ERROR_MESSAGE, "Forbbiden: invalid parameters on method invocation");
            out.println(responseJSON.toString());
            return;
        }

        // if administrative staff privileges are requested
        if (request.getParameter(RESERVATION_ADMIN_PRIVILEGES) != null)
            roomManagementService.setAdminPrivileges(true);

        // retrieving department
        String roomDepartment = request.getParameter(RESERVATION_DEPARTMENT);

        // retrieving datetime
        Date startDateTime = new Date(Long.valueOf(request.getParameter(START_TIMESTAMP)));
        Date endDateTime = new Date(Long.valueOf(request.getParameter(END_TIMESTAMP)));

        // RESERVATION
        reserveRoom(user, request.getParameter(EVENT_ID), roomProperties, roomDepartment, startDateTime, endDateTime, responseJSON);
        // writing result
        out.println(responseJSON.toString());

    }

    private synchronized void reserveRoom(User user, String eventID, RoomProperties properties, String department, Date startDate, Date endDate, JSONObject responseJSON) {
        // preparing services
        roomManagementService.setCurrentUser(user);
        RoomLookupService roomLookupService = new RoomLookupService();
        roomLookupService.setCurrentUser(user);
        roomManagementService.setRoomLookup(roomLookupService);

        try {
            // performing actual reservation
            Reservation reservation =  roomManagementService.reserveRoom(eventID, properties, department, startDate, endDate);

            if (reservation == null)
                responseJSON.put(RESPONSE_SUCCESS_MESSAGE, "No room could be reserved with the given parameters, try to resubmit");
            else
                responseJSON.put(RESPONSE_SUCCESS_MESSAGE, "Reservation completed!");
            responseJSON.put(RESPONSE_STATUS, SUCCESS);

            // handling exceptions
        } catch (AuthRequiredException e) {
            e.printStackTrace();
            responseJSON.put(RESPONSE_STATUS, FAILURE);
            responseJSON.put(RESPONSE_ERROR_MESSAGE, "Forbidden: error on user authentication");
        } catch (ReservationServiceException | RoomServiceException e) {
            e.printStackTrace();
            responseJSON.put(RESPONSE_STATUS, FAILURE);
            responseJSON.put(RESPONSE_ERROR_MESSAGE, "Error trying to reserve a room for event " + eventID);
        }
    }
}
