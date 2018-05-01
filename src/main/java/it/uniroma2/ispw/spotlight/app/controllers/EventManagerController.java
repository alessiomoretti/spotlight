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
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;

public class EventManagerController {

    @FXML private Label userLabel;

    @FXML private Button saveEventButton;
    @FXML private Button newEventButton;
    @FXML private Button deleteEventButton;
    @FXML private Button newReservationButton;
    @FXML private Button deleteReservationButton;

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


    @FXML private ChoiceBox<Integer> startHourChoiceBox;
    @FXML private ChoiceBox<Integer> endHourChoiceBox;
    @FXML private ChoiceBox<String> startMinuteChoiceBox;
    @FXML private ChoiceBox<String> endMinuteChoiceBox;

    private ArrayList<Event> events;
    private Event selectedEvent;
    private RoomReservationRow selectedReservationRow;

    public EventManagerController() { }

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

        // populating choiceboxes
        for (int i = 8; i <= 20; i++) {
            startHourChoiceBox.getItems().add(i);
            endHourChoiceBox.getItems().add(i);
        }
        startMinuteChoiceBox.getItems().setAll("00", "15", "30", "45");
        endMinuteChoiceBox.getItems().setAll("00", "15", "30", "45");
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

        // disabling  buttons
        deleteEventButton.setDisable(true);
        deleteReservationButton.setDisable(true);
        newReservationButton.setDisable(true);
        saveEventButton.setDisable(true);
    }

    public void populateEventsTable() {
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
                            // enabling delete button
                            deleteEventButton.setDisable(false);
                            // enabling update buttons
                            newReservationButton.setDisable(false);
                            saveEventButton.setDisable(false);
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

        LocalDateTime startDateLDT = CalendarHelper.getLocalDateTime(event.getStartDateTime());
        LocalDateTime endDateLDT   = CalendarHelper.getLocalDateTime(event.getEndDateTime());
        startDateCalendar.setValue(CalendarHelper.getLocalDate(event.getStartDateTime()));
        endDateCalendar.setValue(CalendarHelper.getLocalDate(event.getEndDateTime()));
        startHourChoiceBox.setValue(startDateLDT.getHour());
        endHourChoiceBox.setValue(endDateLDT.getHour());
        // minutes
        String startMinute = String.valueOf(startDateLDT.getMinute());
        if (startMinute.equals("0"))
            startMinute = "00";
        String endMinute   = String.valueOf(endDateLDT.getMinute());
        if (endMinute.equals("0"))
            endMinute = "00";
        startMinuteChoiceBox.setValue(startMinute);
        endMinuteChoiceBox.setValue(endMinute);


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
                        // enabling delete button
                        deleteReservationButton.setDisable(false);
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

        // creating event dates
        LocalDate startDateL = startDateCalendar.getValue();
        LocalDate endDateL   = endDateCalendar.getValue();
        Date startDate = CalendarHelper.getDate(startDateL.getDayOfMonth(),
                startDateL.getMonthValue(),
                startDateL.getYear(),
                startHourChoiceBox.getValue(),
                Integer.valueOf(startMinuteChoiceBox.getValue()));
        Date endDate   = CalendarHelper.getDate(endDateL.getDayOfMonth(),
                endDateL.getMonthValue(),
                endDateL.getYear(),
                endHourChoiceBox.getValue(),
                Integer.valueOf(endMinuteChoiceBox.getValue()));

        // consistency check on dates and times
        if (endDate.before(startDate)) {
            AlertHelper.DisplayErrorAlert("Error on input parameters", "The end date time must be after the start date time");
            return;
        }

        event.setStartDateTime(startDate);
        event.setEndDateTime(endDate);

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
        } finally {
            // refreshing view
            populateEventsTable();
        }
    }

    private void deleteEvent(Event event) {
        try {
            // retrieving service
            EventManagementService eventManagementService = ServiceManager.getInstance().getEventManagementService();
            // deleting event after user confirmation
            Alert alert = AlertHelper.DisplayConfirmationAlert("Are you sure you want to delete the event " + event.getEventID() + "?");
            if (alert.getResult() == ButtonType.OK) {
                eventManagementService.deleteEvent(event);
            }
        } catch (AuthRequiredException e) {
            AlertHelper.DisplayErrorAlert("User authentication failed", "");
            e.printStackTrace();
        } catch (EventServiceException | ReservationServiceException | RoomServiceException e) {
            AlertHelper.DisplayErrorAlert("Error occured deleting event", "");
            e.printStackTrace();
        } finally {
            // refreshing table view
            populateEventsTable();
            // refreshing event details
            eventIDLabel.setText("-");
            referralLabel.setText("-");
            eventNameTextField.clear();
            mailingListTextField.clear();
            startDateCalendar.setValue(null);
            endDateCalendar.setValue(null);
            // disabling delete button
            deleteEventButton.setDisable(true);
        }
    }

    private void deleteReservation(Event event, RoomReservationRow reservation) {

    }

    private void openNewEventScene() {
        Parent root = null;
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/newevent.fxml"));
            root = (Parent)fxmlLoader.load();

            // passing the current controller
            NewEventController newEventController = fxmlLoader.<NewEventController>getController();
            newEventController.setEventManagerController(this);

            Scene scene = new Scene(root, 450, 360);
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
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/newreservation.fxml"));
            root = (Parent)fxmlLoader.load();

            // passing the current controller
            NewEventController newEventController = fxmlLoader.<NewEventController>getController();
            newEventController.setEventManagerController(this);

            Scene scene = new Scene(root, 450, 360);
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
