package it.uniroma2.ispw.spotlight.services.DataAccesServices;

import it.uniroma2.ispw.spotlight.Constants;
import it.uniroma2.ispw.spotlight.entities.Room.Room;
import it.uniroma2.ispw.spotlight.entities.Room.RoomProperties;
import it.uniroma2.ispw.spotlight.exceptions.AuthRequiredException;
import it.uniroma2.ispw.spotlight.exceptions.RoomServiceException;
import it.uniroma2.ispw.spotlight.database.RoomDAO;
import it.uniroma2.ispw.spotlight.exceptions.ReservationServiceException;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class RoomLookupService extends DataAccessService<Room> {

    private Integer minRoleRequired = Constants.TEACHER_ROLE;

    public RoomLookupService() {
        // setting correct DAO to access rooms database
        setDatabaseInterface(new RoomDAO());
    }

    public ArrayList<Room> getAllRooms() throws AuthRequiredException, RoomServiceException, ReservationServiceException {
        if (hasCapability(getCurrentUser())) {
            RoomDAO roomDAO = (RoomDAO) getDatabaseInterface();
            return roomDAO.getAllRooms();
        } else {
            throw new AuthRequiredException("This user has no privileges to access this service");
        }
    }

    public ArrayList<Room> findRoomByProperties(RoomProperties properties) throws AuthRequiredException, RoomServiceException, ReservationServiceException {
        if (hasCapability(getCurrentUser())) {
            RoomDAO roomDAO = (RoomDAO) getDatabaseInterface();
            return roomDAO.getRoomsByProperties(properties);
        } else {
            throw new AuthRequiredException("This user has no privileges to access this service");
        }
    }

    public ArrayList<Room> findRoomByProperties(RoomProperties properties, String department) throws AuthRequiredException, RoomServiceException, ReservationServiceException {
        if (hasCapability(getCurrentUser())) {
            RoomDAO roomDAO = (RoomDAO) getDatabaseInterface();
            return roomDAO.getRoomsByPropertiesAndDepartment(properties, department);
        } else {
            throw new AuthRequiredException("This user has no privileges to access this service");
        }
    }

    public ArrayList<Room> findRoomOfCurrentUser() throws AuthRequiredException, RoomServiceException, ReservationServiceException {
        if (hasCapability(getCurrentUser())) {
            RoomDAO roomDAO = (RoomDAO) getDatabaseInterface();
            return roomDAO.getRoomsByReferral(getCurrentUser().getUsername());
        } else {
            throw new AuthRequiredException("This user has no privileges to access this service");
        }
    }

    public ArrayList<String> getAllRoomDepartments() throws AuthRequiredException, RoomServiceException {
        if (hasCapability(getCurrentUser())) {
            RoomDAO roomDAO = (RoomDAO) getDatabaseInterface();
            return roomDAO.getDepartments();
        } else {
            throw new AuthRequiredException("This user has no privileges to access this service");
        }
    }

}
