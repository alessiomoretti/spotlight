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

/**
 * This boundary class can be used to handle Room objects
 */
public class RoomLookupService extends DataAccessService<Room> {

    private Integer minRoleRequired = Constants.TEACHER_ROLE;

    public RoomLookupService() {
        // setting correct DAO to access rooms database
        setDatabaseInterface(new RoomDAO());
    }

    /**
     * Return a list of all the rooms in the organization
     * @return ArrayList<Room>
     * @throws AuthRequiredException
     * @throws RoomServiceException
     * @throws ReservationServiceException
     */
    public ArrayList<Room> getAllRooms() throws AuthRequiredException, RoomServiceException, ReservationServiceException {
        if (hasCapability(getCurrentUser())) {
            RoomDAO roomDAO = (RoomDAO) getDatabaseInterface();
            return roomDAO.getAllRooms();
        } else {
            throw new AuthRequiredException("This user has no privileges to access this service");
        }
    }

    /**
     * Return a list of all rooms with the given properties
     * @param properties RoomProperties
     * @return ArrayList<Room>
     * @throws AuthRequiredException
     * @throws RoomServiceException
     * @throws ReservationServiceException
     */
    public ArrayList<Room> findRoomByProperties(RoomProperties properties) throws AuthRequiredException, RoomServiceException, ReservationServiceException {
        if (hasCapability(getCurrentUser())) {
            RoomDAO roomDAO = (RoomDAO) getDatabaseInterface();
            return roomDAO.getRoomsByProperties(properties);
        } else {
            throw new AuthRequiredException("This user has no privileges to access this service");
        }
    }

    /**
     * Return a list of all rooms with the given properties and the selected department
     * @param properties RoomProperties
     * @param department String
     * @return ArrayList<Room>
     * @throws AuthRequiredException
     * @throws RoomServiceException
     * @throws ReservationServiceException
     */
    public ArrayList<Room> findRoomByProperties(RoomProperties properties, String department) throws AuthRequiredException, RoomServiceException, ReservationServiceException {
        if (hasCapability(getCurrentUser())) {
            RoomDAO roomDAO = (RoomDAO) getDatabaseInterface();
            return roomDAO.getRoomsByPropertiesAndDepartment(properties, department);
        } else {
            throw new AuthRequiredException("This user has no privileges to access this service");
        }
    }

    /**
     * Return a list of all the rooms of the currently authenticated user
     * @return ArrayList<Room>
     * @throws AuthRequiredException
     * @throws RoomServiceException
     * @throws ReservationServiceException
     */
    public ArrayList<Room> findRoomOfCurrentUser() throws AuthRequiredException, RoomServiceException, ReservationServiceException {
        if (hasCapability(getCurrentUser())) {
            RoomDAO roomDAO = (RoomDAO) getDatabaseInterface();
            return roomDAO.getRoomsByReferral(getCurrentUser().getUsername());
        } else {
            throw new AuthRequiredException("This user has no privileges to access this service");
        }
    }

    /**
     * Return a list of all the departments with at least a room in the organization
     * @return ArrayList<String>
     * @throws AuthRequiredException
     * @throws RoomServiceException
     */
    public ArrayList<String> getAllRoomDepartments() throws AuthRequiredException, RoomServiceException {
        if (hasCapability(getCurrentUser())) {
            RoomDAO roomDAO = (RoomDAO) getDatabaseInterface();
            return roomDAO.getDepartments();
        } else {
            throw new AuthRequiredException("This user has no privileges to access this service");
        }
    }

}
