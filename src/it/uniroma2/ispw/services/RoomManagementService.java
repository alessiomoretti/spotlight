package it.uniroma2.ispw.services;

import it.uniroma2.ispw.database.ReservationDAO;
import it.uniroma2.ispw.database.RoomDAO;
import it.uniroma2.ispw.entities.Room.Reservation;
import it.uniroma2.ispw.entities.Room.Room;
import it.uniroma2.ispw.entities.Room.RoomProperties;
import it.uniroma2.ispw.exceptions.AuthRequiredException;
import it.uniroma2.ispw.exceptions.ReservationServiceException;
import it.uniroma2.ispw.exceptions.RoomServiceException;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;

import static it.uniroma2.ispw.Constants.*;

public class RoomManagementService extends DataAccessService<Room> {

    private Integer minRoleRequired = TEACHER_ROLE;
    private ReservationDAO reservationDAO;

    public RoomManagementService() {
        // setting correct DAO to access rooms database
        setDatabaseInterface(new RoomDAO());
        // setting DAO to access reservations
        this.reservationDAO = new ReservationDAO();
    }

    public boolean reserveRoom (String eventID, RoomProperties properties, String department, Date startDateTime, Date endDateTime) throws AuthRequiredException, ReservationServiceException, RoomServiceException {
        if (!hasCapability(getCurrentUser()))
            throw new AuthRequiredException("This user has no privileges to access this service");

        // retrieving all rooms with the desired properties and department
        ArrayList<Room> allRooms = ((RoomDAO) getDatabaseInterface()).getRoomsByPropertiesAndDepartment(properties, department);

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

                // update room and return success
                ((RoomDAO) getDatabaseInterface()).update(room);
                return true;
            }
        }

        // check if user can be pre-emptive (ADMINISTRATIVE)
        if (getCurrentUser().getRole() >= ADMINISTRATIVE_ROLE) {
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
