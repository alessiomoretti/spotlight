package it.uniroma2.ispw.spotlight.services.DataAccesServices;

import it.uniroma2.ispw.spotlight.Constants;
import it.uniroma2.ispw.spotlight.entities.Room.Reservation;
import it.uniroma2.ispw.spotlight.entities.Room.Room;
import it.uniroma2.ispw.spotlight.entities.Room.RoomProperties;
import it.uniroma2.ispw.spotlight.exceptions.AuthRequiredException;
import it.uniroma2.ispw.spotlight.exceptions.RoomServiceException;
import it.uniroma2.ispw.spotlight.database.ReservationDAO;
import it.uniroma2.ispw.spotlight.database.RoomDAO;
import it.uniroma2.ispw.spotlight.exceptions.ReservationServiceException;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;

public class RoomManagementService extends DataAccessService<Room> {

    private Integer minRoleRequired = Constants.TEACHER_ROLE;
    private ReservationDAO reservationDAO;
    private RoomLookupService roomLookup;

    public RoomManagementService() {
        // setting correct DAO to access rooms database
        setDatabaseInterface(new RoomDAO());
        // setting lookup service
        this.roomLookup = new RoomLookupService();
        // setting DAO to access reservations
        this.reservationDAO = new ReservationDAO();
    }

    public boolean reserveRoom (String eventID, RoomProperties properties, String department, Date startDateTime, Date endDateTime) throws AuthRequiredException, ReservationServiceException, RoomServiceException {
        if (!hasCapability(getCurrentUser()))
            throw new AuthRequiredException("This user has no privileges to access this service");

        // retrieving all rooms with the desired properties
        ArrayList<Room> allRooms = roomLookup.findRoomByProperties(properties, department);

        // check if a room is available in the desired timespan
        for (Room room : allRooms) {
            // build reservation ID
            String reservationID = getCurrentUser().getUsername() + "-" + eventID + "-" + (new Timestamp(Instant.now().getEpochSecond())).toString();
            // if no reservation or a timeslot is available
            if (room.getReservations().size() == 0 || getReservationDAO().checkReservationsByRoomIDAndTimeslot(room.getRoomID(),
                                                                                                               new Timestamp(startDateTime.getTime()),
                                                                                                               new Timestamp(endDateTime.getTime()))) {

                // create new reservation
                Reservation newReservation = new Reservation(reservationID, room.getRoomID(), eventID, getCurrentUser().getUsername(), startDateTime, endDateTime);
                // add reservation to room
                room.addReservation(newReservation);
                // update room and return success
                ((RoomDAO) getDatabaseInterface()).update(room);
                return true;
            }
        }

        // check if user can be pre-emptive (ADMINISTRATIVE)
        if (getCurrentUser().getRole() >= Constants.ADMINISTRATIVE_ROLE) {
            Room room = allRooms.get(0);
            // check for conflicting timeslots
            ArrayList<Reservation> conflicts = new ArrayList<>();
            for (Reservation reservation : room.getReservations()) {
                if (reservation.getEndDateTime().after(endDateTime))
                    conflicts.add(reservation);
            }
            // removing conflicting timeslots
            for (Reservation conflict : conflicts) {
                room.delReservation(conflict);
            }
            // TODO Emailer

            // build reservation ID
            String reservationID = getCurrentUser().getUsername() + "-" + eventID + "-" + (new Timestamp(Instant.now().getEpochSecond())).toString();

            // create new reservation
            Reservation newReservation = new Reservation(reservationID, room.getRoomID(), eventID, getCurrentUser().getUsername(), startDateTime, endDateTime);

            // update room and return success
            ((RoomDAO) getDatabaseInterface()).update(room);
            return true;
        } else {
            return false;
        }
    }

    public ReservationDAO getReservationDAO() { return this.getReservationDAO(); }
}
