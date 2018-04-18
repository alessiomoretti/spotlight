package it.uniroma2.ispw.services;

import it.uniroma2.ispw.database.RoomDAO;
import it.uniroma2.ispw.entities.Room.Room;
import it.uniroma2.ispw.entities.Room.RoomProperties;
import it.uniroma2.ispw.exceptions.AuthRequiredException;
import it.uniroma2.ispw.exceptions.ReservationServiceException;
import it.uniroma2.ispw.exceptions.RoomServiceException;

import java.util.ArrayList;

import static it.uniroma2.ispw.Constants.*;

public class RoomLookupService extends DataAccessService<Room> {

    private Integer minRoleRequired = TEACHER_ROLE;

    public RoomLookupService() {
        // setting correct DAO to access rooms database
        setDatabaseInterface(new RoomDAO());
    }

    public ArrayList<Room> findRoomByProperties(RoomProperties properties) throws AuthRequiredException, RoomServiceException {
        if (hasCapability(getCurrentUser())) {
            RoomDAO roomDAO = (RoomDAO) getDatabaseInterface();
            return roomDAO.getRoomsByProperties(properties);
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

}
