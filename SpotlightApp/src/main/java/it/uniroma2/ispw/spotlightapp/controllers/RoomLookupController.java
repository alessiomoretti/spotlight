package it.uniroma2.ispw.spotlightapp.controllers;

import it.uniroma2.ispw.spotlight.Constants;
import it.uniroma2.ispw.spotlight.entities.Room.Reservation;
import it.uniroma2.ispw.spotlight.entities.Room.Room;
import it.uniroma2.ispw.spotlight.exceptions.*;
import it.uniroma2.ispw.spotlight.helpers.CalendarHelper;
import it.uniroma2.ispw.spotlight.services.DataAccesServices.RoomLookupService;
import it.uniroma2.ispw.spotlight.services.DataAccesServices.UserEventLookupService;
import it.uniroma2.ispw.spotlight.services.ServiceManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.text.SimpleDateFormat;
import java.util.ArrayList;


public class RoomLookupController {

    @FXML private TableView<ReservationRow> reservationsTable;
    @FXML private TableView<Room> roomsTable;

    @FXML private TableColumn<ReservationRow, String> reservationColumn;
    @FXML private  TableColumn<ReservationRow, String> startDayColumn;
    @FXML private TableColumn<ReservationRow, String> startHourColumn;
    @FXML private TableColumn<ReservationRow, String> endDayColumn;
    @FXML private TableColumn<ReservationRow, String> endHourColumn;

    @FXML private TableColumn<Room, String> roomNameColumn;
    @FXML private TableColumn<Room, String> roomDeptColumn;

    @FXML private CheckBox microphoneCheckbox;
    @FXML private CheckBox whiteboardCheckbox;
    @FXML private CheckBox intWhiteboardCheckbox;
    @FXML private CheckBox videocallCheckbox;
    @FXML private CheckBox projectorCheckbox;
    @FXML private Label capacityLabel;

    @FXML private Button refreshButton;

    private ArrayList<Room> rooms;

    public RoomLookupController() { }

    public void initialize() {
        // initializing reservations table
        reservationColumn.setCellValueFactory(new PropertyValueFactory<ReservationRow, String>("reservation"));
        startDayColumn.setCellValueFactory(new PropertyValueFactory<ReservationRow, String>("startDay"));
        startHourColumn.setCellValueFactory(new PropertyValueFactory<ReservationRow, String>("startHour"));
        endDayColumn.setCellValueFactory(new PropertyValueFactory<ReservationRow, String>("endDay"));
        endHourColumn.setCellValueFactory(new PropertyValueFactory<ReservationRow, String>("endHour"));

        // initializing rooms table
        roomNameColumn.setCellValueFactory(new PropertyValueFactory<Room, String>("roomName"));
        roomDeptColumn.setCellValueFactory(new PropertyValueFactory<Room, String>("roomDepartment"));


        // populating rooms table
        populateRoomsTable();

        // adding selection listener on event to populate details and reservations
        roomsTable.getSelectionModel().selectedItemProperty()
                .addListener((obs, oldSelection, newSelection) -> {
                    if (newSelection != null)
                        populateRoomReservationAndDetails(newSelection);
                });

        // adding action on refresh button
        refreshButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                roomsTable.getItems().clear();
                populateRoomsTable();
            }
        });

    }

    private void populateRoomsTable() {
        // preparing the observable list
        ObservableList<Room> roomRows = FXCollections.<Room> observableArrayList();

        try {
            // retrieving room lookup service
            RoomLookupService roomLookupService = ServiceManager.getInstance().getRoomLookupService();
            // retrieving list of all available rooms
            rooms = roomLookupService.getAllRooms();
            // adding rooms to observable list and updating tableview
            roomRows.addAll(rooms);
            roomsTable.getItems().setAll(roomRows);
        } catch (AuthRequiredException e) {
            e.printStackTrace();
            it.uniroma2.ispw.spotlightapp.controllers.helpers.AlertHelper.DisplayErrorAlert("User authentication failed", "");
        } catch (RoomServiceException | ReservationServiceException e) {
            e.printStackTrace();
            it.uniroma2.ispw.spotlightapp.controllers.helpers.AlertHelper.DisplayErrorAlert("Error occured retrieving rooms", "");
        }
    }

    private void populateRoomReservationAndDetails(Room room) {
        // populating room details
        microphoneCheckbox.setSelected(room.getProperties().hasMicrophone());
        whiteboardCheckbox.setSelected(room.getProperties().hasWhiteboard());
        intWhiteboardCheckbox.setSelected(room.getProperties().hasInteractiveWhiteboard());
        projectorCheckbox.setSelected(room.getProperties().hasProjector());
        videocallCheckbox.setSelected(room.getProperties().isVideocallCapable());
        capacityLabel.setText(room.getProperties().getCapacity().toString());

        // preparing the observable list to populate the reservations list
        ObservableList<ReservationRow> roomRows = FXCollections.<ReservationRow> observableArrayList();

        // preparing date formatter
        SimpleDateFormat dfDay = new SimpleDateFormat("MMM dd, YYYY");
        SimpleDateFormat dfHour = new SimpleDateFormat("HH:mm");

        try {
            // retrieving event lookup service
            UserEventLookupService userEventLookupService = ServiceManager.getInstance().getUserEventLookupService();
            ArrayList<ReservationRow> reservationRows = new ArrayList<>();

            for (Reservation reservation : room.getReservations()) {
                // check if event is present or future
                if (reservation.getEndDateTime().getTime() >= CalendarHelper.getToday().getTime()) {
                    // if teacher - only slots
                    if (ServiceManager.getInstance().getLoginService().getCurrentUser().getRole() == Constants.TEACHER_ROLE) {
                        reservationRows.add(new ReservationRow("RESERVED",
                                dfDay.format(reservation.getStartDateTime()),
                                dfHour.format(reservation.getStartDateTime()),
                                dfDay.format(reservation.getEndDateTime()),
                                dfHour.format(reservation.getEndDateTime())));
                    } else {
                        // if administrative staff - events are shown
                        reservationRows.add(new ReservationRow(userEventLookupService.getEventByID(reservation.getEventID()).getEventName(),
                                dfDay.format(reservation.getStartDateTime()),
                                dfHour.format(reservation.getStartDateTime()),
                                dfDay.format(reservation.getEndDateTime()),
                                dfHour.format(reservation.getEndDateTime())));
                    }
                }
            }

            // updating tableview
            reservationsTable.getItems().setAll(reservationRows);

        } catch (AuthRequiredException | UserRetrievalException e) {
            e.printStackTrace();
            it.uniroma2.ispw.spotlightapp.controllers.helpers.AlertHelper.DisplayErrorAlert("User retrieval failed", "");
        } catch (RoomServiceException | ReservationServiceException | EventServiceException e) {
            e.printStackTrace();
            it.uniroma2.ispw.spotlightapp.controllers.helpers.AlertHelper.DisplayErrorAlert("User occured during information retrieval", "Error occured while accessing reservations and events database");
        }

    }

    public class ReservationRow {

        private String reservation;
        private String startDay;
        private String startHour;
        private String endDay;
        private String endHour;

        public ReservationRow(String reservation, String startDay, String startHour, String endDay, String endHour) {
            this.reservation = reservation;
            this.startDay = startDay;
            this.startHour = startHour;
            this.endDay = endDay;
            this.endHour = endHour;
        }

        public String getReservation() {
            return reservation;
        }

        public String getStartDay() {
            return startDay;
        }

        public String getStartHour() {
            return startHour;
        }

        public String getEndDay() {
            return endDay;
        }

        public String getEndHour() {
            return endHour;
        }
    }

}
