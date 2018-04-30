package it.uniroma2.ispw.spotlight.app.controllers;

import it.uniroma2.ispw.spotlight.entities.Event;
import it.uniroma2.ispw.spotlight.entities.Room.Reservation;
import it.uniroma2.ispw.spotlight.entities.Room.Room;
import it.uniroma2.ispw.spotlight.exceptions.*;
import it.uniroma2.ispw.spotlight.helpers.CalendarHelper;
import it.uniroma2.ispw.spotlight.services.DataAccesServices.EventManagementService;
import it.uniroma2.ispw.spotlight.services.DataAccesServices.UserEventLookupService;
import it.uniroma2.ispw.spotlight.services.ServiceManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import javax.swing.*;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;

public class EventManagerController {

    @FXML private Label userLabel;

    @FXML Button saveEventButton;
    @FXML Button newEventButton;
    @FXML Button deleteEventButton;
    @FXML Button newReservationButton;
    @FXML Button deleteReservationButton;

    @FXML private TableView<Event> eventsTable;
    @FXML private TableColumn<Event, String> eventNameColumn;
    @FXML private TableColumn<Event, String> referralColumn;
    @FXML private TableColumn<Event, String> mailColumn;
    @FXML private TableColumn<Event, Date> startDateTimeColumn;
    @FXML private TableColumn<Event, Date> endDateTimeColumn;
    @FXML private TableColumn<Event, Integer> reservationsColumn;

    @FXML private TableView<RoomReservationRow> reservationsTable;
    @FXML private TableColumn<RoomReservationRow, String> roomNameColumn;
    @FXML private TableColumn<RoomReservationRow, String> roomDepartmentColumn;
    @FXML private TableColumn<RoomReservationRow, String> startTimeColumn;
    @FXML private TableColumn<RoomReservationRow, String> endTimeColumn;

    @FXML private Label eventIDLabel;
    @FXML private Label referralLabel;
    @FXML private TextField eventNameTextField;
    @FXML private TextField mailingListTextField;
    @FXML private DatePicker startDateCalendar;
    @FXML private DatePicker endDateCalendar;

    private ArrayList<Event> events;
    private Event selectedEvent;
    private RoomReservationRow selectedReservationRow;

    @FXML
    public void initialize() {
        try {
            userLabel.setText(ServiceManager.getInstance().getLoginService().getCurrentUser().getName());
        } catch(AuthRequiredException e) {
            e.printStackTrace();
            AlertHelper.DisplayErrorAlert("Error occured during login!", "An error occured while performing query on the user database");
        }

        // initializing event table columns
        eventNameColumn.setCellValueFactory(new PropertyValueFactory<Event, String>("eventName"));
        referralColumn.setCellValueFactory(new PropertyValueFactory<Event, String>("referralName"));
        mailColumn.setCellValueFactory(new PropertyValueFactory<Event, String>("emailDL"));
        startDateTimeColumn.setCellValueFactory(new PropertyValueFactory<Event, Date>("startDateTime"));
        endDateTimeColumn.setCellValueFactory(new PropertyValueFactory<Event, Date>("endDateTime"));
        reservationsColumn.setCellValueFactory(new PropertyValueFactory<Event, Integer>("reservations"));

        // populating events table
        populateEventsTable();

        // initializing reservations table columns
        roomNameColumn.setCellValueFactory(new PropertyValueFactory<RoomReservationRow, String>("reservation"));
        roomDepartmentColumn.setCellValueFactory(new PropertyValueFactory<RoomReservationRow, String>("department"));
        startTimeColumn.setCellValueFactory(new PropertyValueFactory<RoomReservationRow, String>("startTime"));
        endTimeColumn.setCellValueFactory(new PropertyValueFactory<RoomReservationRow, String>("endTime"));

        // attaching events to buttons
        addButtonListeners();
    }

    private void addButtonListeners() {
        // SAVE EVENT
        saveEventButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (selectedEvent != null)
                    updateEvent(selectedEvent);
            }
        });
        // DELETE EVENT
        deleteEventButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (selectedEvent != null)
                    deleteEvent(selectedEvent);
            }
        });
        // DELETE RESERVATION
        deleteReservationButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (selectedReservationRow != null && selectedEvent != null)
                    deleteReservation(selectedEvent, selectedReservationRow);
            }
        });
        // NEW RESERVATION
        newReservationButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                openNewReservationScene();
            }
        });
        // NEW EVENT
        newEventButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                openNewEventScene();
            }
        });
    }

    private void populateEventsTable() {
        try {
            // retrieving event lookup service
            UserEventLookupService userEventLookupService = ServiceManager.getInstance().getUserEventLookupService();
            // retrieving events
            events = userEventLookupService.getCurrentUserEvents();
            ObservableList<Event> eventRows = FXCollections.<Event> observableArrayList();
            eventRows.addAll(events);
            // updating tableview
            eventsTable.getItems().setAll(eventRows);

            // adding selection listener to populate details and reservation section
            eventsTable.getSelectionModel().selectedItemProperty()
                    .addListener((obs, oldSelection, newSelection) -> {
                        if (newSelection != null) {
                            // setting the currently selected event
                            selectedEvent = newSelection;
                            // populate event details
                            populateEventDetails(newSelection);
                            // populate reservations
                            try {
                                populateReservationsTable(newSelection);
                            } catch (AuthRequiredException e) {
                                e.printStackTrace();
                            }
                        }
                    });

        } catch (AuthRequiredException e) {
            e.printStackTrace();
            AlertHelper.DisplayErrorAlert("User authentication failed", "");
        } catch (EventServiceException | RoomServiceException | ReservationServiceException e) {
            e.printStackTrace();
            AlertHelper.DisplayErrorAlert("Error occured retrieving events", "");
        }
    }

    private void populateEventDetails(Event event) {
        eventIDLabel.setText(event.getEventID());
        referralLabel.setText(event.getReferral().getUsername());
        eventNameTextField.setText(event.getEventName());
        mailingListTextField.setText(event.getEmailDL());
        startDateCalendar.setValue(CalendarHelper.getLocalDate(event.getStartDateTime()));
        endDateCalendar.setValue(CalendarHelper.getLocalDate(event.getStartDateTime()));

    }

    private void populateReservationsTable(Event event) throws AuthRequiredException {
        // preparing date formatters
        SimpleDateFormat dfDay = new SimpleDateFormat("MMM dd, YYYY");
        SimpleDateFormat dfHour = new SimpleDateFormat("HH:mm");

        // preparing the observable list to populate the room reservations list
        ObservableList<RoomReservationRow> roomReservationRows = FXCollections.<RoomReservationRow> observableArrayList();

        ArrayList<RoomReservationRow> reservationRows = new ArrayList<>();

        for (Room room : event.getReservedRooms()) {
            for (Reservation reservation : room.getReservations()) {
                reservationRows.add(new RoomReservationRow(
                        room.getRoomName(),
                        room.getRoomID(),
                        room.getRoomDepartment(),
                        dfDay.format(reservation.getStartDateTime()),
                        dfHour.format(reservation.getStartDateTime()),
                        dfDay.format(reservation.getEndDateTime()),
                        dfHour.format(reservation.getEndDateTime())
                ));
            }
        }

        // adding selection listener to reservation table
        reservationsTable.getSelectionModel().selectedItemProperty()
                .addListener((obs, oldSelection, newSelection) -> {
                    if (newSelection != null) {
                        // setting the currently selected reservation row
                        selectedReservationRow = newSelection;
                    }
                });

        // populating reservations table
        roomReservationRows.addAll(reservationRows);
        reservationsTable.getItems().setAll(roomReservationRows);
    }


    private void updateEvent(Event event) {
        // updating event
        if (mailingListTextField.getText().length() != 0 && eventNameTextField.getText().length() != 0) {
            event.setEmailDL(mailingListTextField.getText());
            event.setEventName(eventNameTextField.getText());
        } else {
            return;
        }
        event.setStartDateTime(Date.from(Instant.from(startDateCalendar.getValue().atStartOfDay(ZoneId.systemDefault()))));
        event.setEndDateTime(Date.from(Instant.from(endDateCalendar.getValue().atStartOfDay(ZoneId.systemDefault()))));


        try {
            // retrieving service to update the event
            EventManagementService eventManagementService = ServiceManager.getInstance().getEventManagementService();
            // updating event
            eventManagementService.updateEvent(event);
        } catch (EventServiceException e) {
            AlertHelper.DisplayErrorAlert("User authentication failed", "");
            e.printStackTrace();
        } catch (AuthRequiredException e) {
            AlertHelper.DisplayErrorAlert("Error occured updating event", "");
            e.printStackTrace();
        }

        // refreshing view
        populateEventsTable();
    }

    private void deleteEvent(Event event) {

    }

    private void deleteReservation(Event event, RoomReservationRow reservation) {

    }

    private void openNewEventScene() {
        Parent root = null;
        try {
            root = FXMLLoader.load(getClass().getClassLoader().getResource("fxml/newevent.fxml"));
            Scene scene = new Scene(root, 470, 360);
            Stage stage = new Stage();
            stage.setTitle("Spotlight - New Event");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void openNewReservationScene() {
        Parent root = null;
        try {
            root = FXMLLoader.load(getClass().getClassLoader().getResource("fxml/newreservation.fxml"));
            Scene scene = new Scene(root, 470, 360);
            Stage stage = new Stage();
            stage.setTitle("Spotlight - New Reservation");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public class RoomReservationRow {

        private String reservation;
        private String department;
        private String roomID;
        private String startTime;
        private String startDay;
        private String startHour;
        private String endTime;
        private String endDay;
        private String endHour;

        public RoomReservationRow(String reservation, String roomID, String department, String startDay, String startHour, String endDay, String endHour) {
            this.reservation = reservation;
            this.roomID = roomID;
            this.department = department;
            this.startDay = startDay;
            this.startHour = startHour;
            this.startTime = startDay + " " + startHour;
            this.endDay = endDay;
            this.endHour = endHour;
            this.endTime = endDay + " " + endHour;
        }

        public String getReservation() {
            return reservation;
        }

        public String getDepartment() {
            return department;
        }

        public String getRoomID() {
            return roomID;
        }

        public String getStartTime() { return this.startTime; }

        public String getEndTime() { return this.endTime; }

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
