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
import it.uniroma2.ispw.spotlight.helpers.MD5Helper;
import it.uniroma2.ispw.spotlight.services.Emailer;
import it.uniroma2.ispw.spotlight.services.ServiceManager;

import javax.xml.ws.Service;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;

public class RoomManagementService extends DataAccessService<Room> {

    private Integer minRoleRequired = Constants.TEACHER_ROLE;
    private ReservationDAO reservationDAO;
    private RoomLookupService roomLookup;

    private boolean adminPrivileges = false;

    public RoomManagementService() {
        // setting correct DAO to access rooms database
        setDatabaseInterface(new RoomDAO());
        // setting DAO to access reservations
        this.reservationDAO = new ReservationDAO();
    }

    public Reservation reserveRoom (String eventID, RoomProperties properties, String department, Date startDateTime, Date endDateTime) throws AuthRequiredException, ReservationServiceException, RoomServiceException {
        if (!hasCapability(getCurrentUser()))
            throw new AuthRequiredException("This user has no privileges to access this service");

        // retrieving all rooms with the desired properties
        ArrayList<Room> allRooms = getRoomLookup().findRoomByProperties(properties, department);

        // check if a room is available in the desired timespan
        for (Room room : allRooms) {
            // build reservation ID
            String reservationID = MD5Helper.getHashedString(getCurrentUser().getUsername() + "-" + eventID + "-" + String.valueOf(Instant.now().getEpochSecond()));
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

                // send confirmation mail
                Emailer.sendConfirmationMail(getCurrentUser().getEmailAddress(),
                                      "reserving room (" + newReservation.getStartDateTime().toString() + " - " + newReservation.getEndDateTime().toString() + ")",
                                             room.getRoomName());
                return newReservation;
            }
        }

        // check if user can be preemptive (ADMINISTRATIVE)
        if (getCurrentUser().getRole() >= Constants.ADMINISTRATIVE_ROLE && adminPrivileges) {
            Room room = allRooms.get(0);
            // check for conflicting time slots
            ArrayList<Reservation> conflicts = new ArrayList<>();
            for (Reservation reservation : room.getReservations()) {
                if (reservation.getStartDateTime().getTime() <= endDateTime.getTime() &&
                    startDateTime.getTime() <= reservation.getEndDateTime().getTime()) {
                    conflicts.add(reservation);
                }
            }
            // removing reservations on conflicting time slots
            for (Reservation conflict : conflicts) {
                room.delReservation(conflict);
                getReservationDAO().delete(conflict);

                // sending cancellation mail to previous referral
                Emailer.sendCancellationMail(getCurrentUser().getEmailAddress(),
                                    "reservation (" + conflict.getStartDateTime().toString() + " - " + conflict.getStartDateTime().toString() + ") of ",
                                             conflict.getRoomID(),
                                      "for admnistrative purposes.");
            }

            // build reservation ID
            String reservationID = getCurrentUser().getUsername() + "-" + eventID + "-" + (new Timestamp(System.currentTimeMillis())).toString();

            // create new reservation
            Reservation newReservation = new Reservation(reservationID, room.getRoomID(), eventID, getCurrentUser().getUsername(), startDateTime, endDateTime);
            // add reservation to room
            room.addReservation(newReservation);
            // update room and return success
            ((RoomDAO) getDatabaseInterface()).update(room);
            return newReservation;
        } else {
            return null;
        }
    }

    public void deleteRoomReservation(Reservation reservation) throws AuthRequiredException, ReservationServiceException {
        if (!hasCapability(getCurrentUser()))
            throw new AuthRequiredException("This user has no privileges to access this service");

        try {
            getReservationDAO().delete(reservation);
        } catch (ReservationServiceException e) {
            e.printStackTrace();
            throw new ReservationServiceException("Exception caught deleting reservation " + reservation.getReservationID());
        }
    }

    public void deleteRoomReservationByID(String reservationID) throws AuthRequiredException, ReservationServiceException {
        if (!hasCapability(getCurrentUser()))
            throw new AuthRequiredException("This user has no privileges to access this service");

        try {
            Reservation reservation = getReservationDAO().getReservationsByReservationID(reservationID);
            getReservationDAO().delete(reservation);
        } catch (ReservationServiceException e) {
            e.printStackTrace();
            throw new ReservationServiceException("Exception caught deleting reservation " + reservationID);
        }
    }

    public void setAdminPrivileges(boolean privileges) { this.adminPrivileges = privileges; }

    public ReservationDAO getReservationDAO() { return this.reservationDAO; }

    public RoomLookupService getRoomLookup() throws AuthRequiredException {
        if (this.roomLookup == null)
            this.roomLookup = ServiceManager.getInstance().getRoomLookupService();
        return this.roomLookup;
    }

    public void setRoomLookup(RoomLookupService roomLookupService) { this.roomLookup = roomLookupService; }

}
