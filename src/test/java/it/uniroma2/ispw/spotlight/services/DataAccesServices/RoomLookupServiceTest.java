package it.uniroma2.ispw.spotlight.services.DataAccesServices;

import it.uniroma2.ispw.spotlight.entities.Room.Room;
import it.uniroma2.ispw.spotlight.entities.Room.RoomProperties;
import it.uniroma2.ispw.spotlight.exceptions.AuthRequiredException;
import it.uniroma2.ispw.spotlight.exceptions.ReservationServiceException;
import it.uniroma2.ispw.spotlight.exceptions.RoomServiceException;
import it.uniroma2.ispw.spotlight.users.Teacher;
import it.uniroma2.ispw.spotlight.users.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class RoomLookupServiceTest {

    private RoomLookupService roomLookupService = new RoomLookupService();

    private Teacher testUser = new Teacher("johndoe", "John", "Doe", "john.doe@uni.com", "History");

    private String roomDepartment = "History";
    private RoomProperties roomProperties = new RoomProperties(150, true, true,true, true,true);

    @Test
    void findRoomByProperties() throws ReservationServiceException, AuthRequiredException, RoomServiceException {
        // add user
        roomLookupService.setCurrentUser(testUser);

        ArrayList<Room> rooms = roomLookupService.findRoomByProperties(roomProperties);
        System.out.println("Found: " + String.valueOf(rooms.size()) + " rooms");
        for(Room room: rooms) {
            Assertions.assertTrue(room.getProperties().getCapacity() >= roomProperties.getCapacity());
            Assertions.assertTrue(room.getProperties().hasProjector() && roomProperties.hasProjector());
            Assertions.assertTrue(room.getProperties().hasProjector() && roomProperties.hasWhiteboard());
            Assertions.assertTrue(room.getProperties().hasProjector() && roomProperties.hasInteractiveWhiteboard());
            Assertions.assertTrue(room.getProperties().hasProjector() && roomProperties.isVideocallCapable());
            Assertions.assertTrue(room.getProperties().hasProjector() && roomProperties.hasMicrophone());
        }

        rooms = roomLookupService.findRoomByProperties(roomProperties, roomDepartment);
        System.out.println("Found: " + String.valueOf(rooms.size()) + " rooms by dept");

        for(Room room: rooms) {
            Assertions.assertEquals(room.getRoomDepartment(), roomDepartment);
            Assertions.assertTrue(room.getProperties().getCapacity() >= roomProperties.getCapacity());
            Assertions.assertTrue(room.getProperties().hasProjector() && roomProperties.hasProjector());
            Assertions.assertTrue(room.getProperties().hasProjector() && roomProperties.hasWhiteboard());
            Assertions.assertTrue(room.getProperties().hasProjector() && roomProperties.hasInteractiveWhiteboard());
            Assertions.assertTrue(room.getProperties().hasProjector() && roomProperties.isVideocallCapable());
            Assertions.assertTrue(room.getProperties().hasProjector() && roomProperties.hasMicrophone());
        }

    }

    @Test
    void findRoomOfCurrentUser() throws RoomServiceException, ReservationServiceException, AuthRequiredException {
        // add user
        roomLookupService.setCurrentUser(testUser);

        ArrayList<Room> rooms = roomLookupService.findRoomOfCurrentUser();
        System.out.println("Found: " + String.valueOf(rooms.size()) + " for current user");
    }
}