package it.uniroma2.ispw.spotlight.app.controllers;

import it.uniroma2.ispw.spotlight.Constants;
import it.uniroma2.ispw.spotlight.app.controllers.helpers.AlertHelper;
import it.uniroma2.ispw.spotlight.entities.Event;
import it.uniroma2.ispw.spotlight.entities.Room.Reservation;
import it.uniroma2.ispw.spotlight.entities.Room.Room;
import it.uniroma2.ispw.spotlight.exceptions.*;
import it.uniroma2.ispw.spotlight.helpers.CalendarHelper;
import it.uniroma2.ispw.spotlight.services.DataAccesServices.EventLookupService;
import it.uniroma2.ispw.spotlight.services.DataAccesServices.UserEventLookupService;
import it.uniroma2.ispw.spotlight.services.ServiceManager;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;

public class EventLookupController {

    @FXML private TableView<Event> eventsTable;
    @FXML private TableColumn<Event, String> eventNameColumn;
    @FXML private TableColumn<Event, String> referralColumn;
    @FXML private TableColumn<Event, Date> startDateTimeColumn;
    @FXML private TableColumn<Event, Date> endDateTimeColumn;

    @FXML private TableView<RoomRow> roomsTable;
    @FXML private TableColumn<RoomRow, String> roomNameColumn;
    @FXML private TableColumn<RoomRow, String> roomStartDateTimeColumn;
    @FXML private TableColumn<RoomRow, String> roomEndDateTimeColumn;

    @FXML private TextField searchTextField;
    @FXML private DatePicker eventDatePicker;

    @FXML private Pane paneEventDetails;
    @FXML private Pane paneEventTimeDetails;
    @FXML private Label eventIDDetail;
    @FXML private Label mailingListDetail;
    @FXML private Label startTimeDetail;
    @FXML private Label endTimeDetail;

    @FXML private CheckBox currentUserEventsCheckbox;

    private ArrayList<Event> events;

    public EventLookupController() { }

    @FXML
    public void initialize() {
        // setting as invisible the detail panes
        paneEventDetails.setVisible(false);
        paneEventTimeDetails.setVisible(false);

        // initializing event table columns
        eventNameColumn.setCellValueFactory(new PropertyValueFactory<Event, String>("eventName"));
        referralColumn.setCellValueFactory(new PropertyValueFactory<Event, String>("referralName"));
        startDateTimeColumn.setCellValueFactory(new PropertyValueFactory<Event, Date>("startDateTime"));
        endDateTimeColumn.setCellValueFactory(new PropertyValueFactory<Event, Date>("endDateTime"));

        // initializing room table columns
        roomNameColumn.setCellValueFactory(new PropertyValueFactory<RoomRow, String>("roomName"));
        roomStartDateTimeColumn.setCellValueFactory(new PropertyValueFactory<RoomRow, String>("startDateTime"));
        roomEndDateTimeColumn.setCellValueFactory(new PropertyValueFactory<RoomRow, String>("endDateTime"));

        // adding selection listener on event to populate details section
        eventsTable.getSelectionModel().selectedItemProperty()
                .addListener((obs, oldSelection, newSelection) -> {
                    if (newSelection != null)
                        populateEventDetails(newSelection);
                });

        // visualizing "current user only" checkbox only if Teacher or Adm. Staff
        try {
            if (ServiceManager.getInstance().getLoginService()
                    .getCurrentUser().getRole() < Constants.TEACHER_ROLE)
                currentUserEventsCheckbox.setVisible(false);
            else {
                // adding checkbox listener to disable search text field and date picker if filter is active
                currentUserEventsCheckbox.selectedProperty().addListener(new ChangeListener<Boolean>() {
                    @Override
                    public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                        if (newValue) {
                            searchTextField.setDisable(true);
                            eventDatePicker.setDisable(true);
                        } else {
                            searchTextField.setDisable(false);
                            eventDatePicker.setDisable(false);
                        }
                    }
                });
            }
        } catch (AuthRequiredException e) {
            e.printStackTrace();
            AlertHelper.DisplayErrorAlert("User authentication failed", "");
        }

    }

    public void searchEventButtonClick(javafx.event.ActionEvent actionEvent) {
        // clearing previous search results
        if (events != null) {
            events.clear();
            eventsTable.getItems().clear();
            roomsTable.getItems().clear();
            paneEventDetails.setVisible(false);
            paneEventTimeDetails.setVisible(false);
        }

        // check if current user filter is active
        if (currentUserEventsCheckbox.isSelected())
            searchByUser();
        else
            searchByNameAndDate();

    }

    private void searchByUser() {
        try {
            // retrieving event lookup service
            UserEventLookupService userEventLookupService = ServiceManager.getInstance().getUserEventLookupService();
            // retrieving events
            events = userEventLookupService.getCurrentUserEvents();
            ObservableList<Event> eventRows = FXCollections.<Event> observableArrayList();
            eventRows.addAll(events);
            // updating tableview
            eventsTable.getItems().setAll(eventRows);

        } catch (AuthRequiredException e) {
            e.printStackTrace();
            AlertHelper.DisplayErrorAlert("User authentication failed", "");
        } catch (EventServiceException | RoomServiceException | ReservationServiceException e) {
            e.printStackTrace();
            AlertHelper.DisplayErrorAlert("Error occured retrieving events", "");
        }
    }

    private void searchByNameAndDate() {
        if (eventDatePicker.getValue() == null) {
            AlertHelper.DisplayErrorAlert("Error on input parameters", "Insert a valid date");
            return;
        }

        // retrieving date and verifying if present or future date - system default date zone
        Date eventDate = Date.from(Instant.from(eventDatePicker.getValue().atStartOfDay(ZoneId.systemDefault())));
        if (eventDate.getTime() < CalendarHelper.getToday().getTime()) {
            AlertHelper.DisplayErrorAlert("Error on input parameters", "Date must be valid, present or future");
        }

        // check on text field
        if (searchTextField.getText().length() == 0){
            AlertHelper.DisplayErrorAlert("Error on input parameters", "Event search text must be not empty");
        }

        else {
            try {
                // retrieving event lookup service
                EventLookupService eventLookupService = ServiceManager.getInstance().getEventLookupService();
                // retrieving events
                events = eventLookupService.getEventsByNameAndStartDate(searchTextField.getText(), eventDate);
                ObservableList<Event> eventRows = FXCollections.<Event> observableArrayList();
                eventRows.addAll(events);
                // updating tableview
                eventsTable.getItems().setAll(eventRows);

                // adding selection listener to populate details section
                eventsTable.getSelectionModel().selectedItemProperty()
                        .addListener((obs, oldSelection, newSelection) -> {
                            if (newSelection != null)
                                populateEventDetails(newSelection);
                        });

            } catch (AuthRequiredException | UserRetrievalException e) {
                e.printStackTrace();
                AlertHelper.DisplayErrorAlert("User authentication failed", "");
            } catch (EventServiceException | RoomServiceException | ReservationServiceException e) {
                e.printStackTrace();
                AlertHelper.DisplayErrorAlert("Error occured retrieving events", "");
            }
        }
    }

    public void populateEventDetails(Event event) {
        // making visible the detail panes
        paneEventDetails.setVisible(true);
        paneEventTimeDetails.setVisible(true);

        // retrieving date formatter
        SimpleDateFormat df = new SimpleDateFormat("MMM dd, YYYY HH:mm");

        // populating detail panes
        eventIDDetail.setText("Event: " + event.getEventName().toUpperCase());
        mailingListDetail.setText("Mailing list: " + event.getEmailDL());
        startTimeDetail.setText("Starting: " + df.format(event.getStartDateTime()));
        endTimeDetail.setText("Ending: " + df.format(event.getEndDateTime()));

        // preparing list of rooms to be displayed
        ObservableList<RoomRow> roomRows = FXCollections.observableArrayList();
        for (Room room : event.getReservedRooms()) {
            for (Reservation reservation : room.getReservations()) {
                if (reservation.getEventID().equals(event.getEventID())) {
                    roomRows.add(new RoomRow(room.getRoomName(), df.format(reservation.getStartDateTime()), df.format(reservation.getEndDateTime())));
                }
            }
        }

        // updating rooms tableview
        roomsTable.getItems().setAll(roomRows);
    }


    public class RoomRow {

        private String roomName;
        private String startDateTime;
        private String endDateTime;

        public RoomRow(String roomName, String startDateTime, String endDateTime) {
            this.roomName = roomName;
            this.startDateTime = startDateTime;
            this.endDateTime = endDateTime;
        }

        public String getRoomName() {
            return roomName;
        }

        public String getStartDateTime() {
            return startDateTime;
        }

        public String getEndDateTime() {
            return endDateTime;
        }
    }

}
