package it.uniroma2.ispw.spotlight.services.DataAccesServices;

import it.uniroma2.ispw.spotlight.CalendarHelper;
import it.uniroma2.ispw.spotlight.entities.Room.Reservation;
import it.uniroma2.ispw.spotlight.entities.Room.RoomProperties;
import it.uniroma2.ispw.spotlight.exceptions.AuthRequiredException;
import it.uniroma2.ispw.spotlight.exceptions.ReservationServiceException;
import it.uniroma2.ispw.spotlight.exceptions.RoomServiceException;
import it.uniroma2.ispw.spotlight.users.AdministrativeStaffMember;
import it.uniroma2.ispw.spotlight.users.Teacher;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import sun.jvm.hotspot.utilities.Assert;

import java.util.ArrayList;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class RoomManagementServiceTest {

    private RoomManagementService roomManagementService = new RoomManagementService();
    private Teacher testUserT = new Teacher("johndoe", "John", "Doe", "john.doe@uni.com", "History");
    private AdministrativeStaffMember testUserA = new AdministrativeStaffMember("jennyseed", "Jenny", "Seed", "jenny.seed@uni.com");
    private RoomProperties roomProperties = new RoomProperties(150, true, true,true, true,true);
    private String testEventID = "testevent - johndoe - 1524317641";

    @Test
    void reserveRoom() throws RoomServiceException, ReservationServiceException, AuthRequiredException {
        // add user - TEACHER
        roomManagementService.setCurrentUser(testUserT);
        roomManagementService.getRoomLookup().setCurrentUser(testUserT);

        // setting start and end datetime
        Date start_date = CalendarHelper.getDate(22, 4, 2018, 10, 0);
        Date end_date   = CalendarHelper.getDate(22, 4,2018, 12, 0);

        // create new reservation
        Reservation r = roomManagementService.reserveRoom(testEventID, roomProperties, "History", start_date, end_date);
        Assertions.assertNotNull(r);
        Assertions.assertEquals(r.getStartDateTime(), start_date);
        Assertions.assertEquals(r.getEndDateTime(), end_date);

        // repeat reservation - must fail
        Reservation dr = roomManagementService.reserveRoom(testEventID, roomProperties, "History", start_date, end_date);
        Assertions.assertNull(dr);

        // add user - ADMINISTRATIVE
        roomManagementService.setCurrentUser(testUserA);
        roomManagementService.getRoomLookup().setCurrentUser(testUserA);
        roomManagementService.setAdminPrivileges(true);

        // create new reservation
        r = roomManagementService.reserveRoom(testEventID, roomProperties, "History", start_date, end_date);
        Assertions.assertNotNull(r);
        Assertions.assertEquals(r.getStartDateTime(), start_date);
        Assertions.assertEquals(r.getEndDateTime(), end_date);
        Assertions.assertEquals(r.getReferral(), testUserA.getUsername());

        // double check -- only one reservation for this test case with final referral 'jennyseed'
        ArrayList<Reservation> reservations = roomManagementService.getReservationDAO().getReservationsByEventID(testEventID);
        Assertions.assertEquals(reservations.size(), 1);
        Assertions.assertEquals(reservations.get(0).getReferral(), testUserA.getUsername());
    }

}