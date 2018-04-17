package it.uniroma2.ispw.database;

import it.uniroma2.ispw.entities.Room.Reservation;
import it.uniroma2.ispw.entities.Room.Room;
import it.uniroma2.ispw.entities.Room.RoomProperties;
import it.uniroma2.ispw.exceptions.ReservationServiceException;
import it.uniroma2.ispw.exceptions.RoomServiceException;

import java.sql.ResultSet;
import java.sql.SQLException;

public class RoomDAO extends DAO<Room>{

    private ReservationDAO reservationDAO;

    public RoomDAO() {
        this.reservationDAO = new ReservationDAO();
    }

    public ResultSet getRooms(RoomProperties properties) throws RoomServiceException {

        // preparing sql
        String sql = "SELECT * FROM rooms WHERE " +
                     "capacity >= "  + String.valueOf(properties.getCapacity()) + " AND " +
                     "projector = "  + String.valueOf(properties.hasProjector() ? 1 : 0)  +
                     "whiteboard = " + String.valueOf(properties.hasWhiteboard() ? 1 : 0) +
                     "int_whiteboard = " + String.valueOf(properties.hasInteractiveWhiteboard() ? 1 : 0) +
                     "videocall_capable = " + String.valueOf(properties.isVideocallCapable() ? 1 : 0) +
                     "microphone = " + String.valueOf(properties.hasMicrophone() ? 1 : 0);

        // retrieving results filtered by room properties
        try {
            ResultSet result = retrieve(sql);
            return result;
        } catch (ClassNotFoundException | SQLException e) {
            throw new RoomServiceException("Exception caught handling rooms retrieval");
        }
    }

    @Override
    public void update(Room room) throws RoomServiceException {

        // only reservations can be modified in Room
        try {
            for (Reservation reservation : room.getReservations()) {
                this.reservationDAO.update(reservation);
            }
        } catch (ReservationServiceException e) {
            e.printStackTrace();
            throw new RoomServiceException("Exception caught handling room update");
        }

    }

    @Override
    public void delete(Room room) throws Exception { /* no implementation needed */}
}
