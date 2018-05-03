package it.uniroma2.ispw.spotlightapp.controllers;

import it.uniroma2.ispw.spotlight.Constants;
import it.uniroma2.ispw.spotlight.entities.Room.RoomProperties;
import it.uniroma2.ispw.spotlight.exceptions.AuthRequiredException;
import it.uniroma2.ispw.spotlight.exceptions.ReservationServiceException;
import it.uniroma2.ispw.spotlight.exceptions.RoomServiceException;
import it.uniroma2.ispw.spotlight.helpers.CalendarHelper;
import it.uniroma2.ispw.spotlight.services.DataAccesServices.RoomLookupService;
import it.uniroma2.ispw.spotlight.services.DataAccesServices.RoomManagementService;
import it.uniroma2.ispw.spotlight.services.ServiceManager;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.Date;

public class NewReservationController {

    @FXML private CheckBox microphoneCheckbox;
    @FXML private CheckBox whiteboardCheckbox;
    @FXML private CheckBox intWhiteboardCheckbox;
    @FXML private CheckBox videocallCheckbox;
    @FXML private CheckBox projectorCheckbox;

    @FXML private TextField roomCapacityTextField;
    @FXML private ChoiceBox<String> roomDeptChoiceBox;

    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;
    @FXML private ChoiceBox<Integer> startHourChoiceBox;
    @FXML private ChoiceBox<Integer> endHourChoiceBox;
    @FXML private ChoiceBox<String> startMinuteChoiceBox;
    @FXML private ChoiceBox<String> endMinuteChoiceBox;

    @FXML private CheckBox adminPrivilegeCheckBox;

    @FXML private Button newReservationButton;

    private EventManagerController eventManagerController;

    public NewReservationController() {}

    @FXML
    public void initialize() {

        try {
            // populating the department choicebox
            RoomLookupService roomLookupService = ServiceManager.getInstance().getRoomLookupService();
            roomDeptChoiceBox.getItems().setAll(roomLookupService.getAllRoomDepartments());

            // visualizing the admin privileges checkbox only if administrative staff user
            if (ServiceManager.getInstance().getLoginService().getCurrentUser().getRole() < Constants.ADMINISTRATIVE_ROLE)
                adminPrivilegeCheckBox.setVisible(false);
            else
                adminPrivilegeCheckBox.setVisible(true);
        } catch (AuthRequiredException e) {
            it.uniroma2.ispw.spotlightapp.controllers.helpers.AlertHelper.DisplayErrorAlert("User authentication failed", "");
            e.printStackTrace();
        } catch (RoomServiceException e) {
            it.uniroma2.ispw.spotlightapp.controllers.helpers.AlertHelper.DisplayErrorAlert("Error occured retrieving departments", "");
            e.printStackTrace();
        }

        // populating choiceboxes
        for (int i = 8; i <= 20; i++) {
            startHourChoiceBox.getItems().add(i);
            endHourChoiceBox.getItems().add(i);
        }
        startMinuteChoiceBox.getItems().setAll("00", "15", "30", "45");
        endMinuteChoiceBox.getItems().setAll("00", "15", "30", "45");

        // adding action on new event button
        newReservationButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                createNewReservation();
            }
        });

        // forcing capacity textfield to be numeric only
        roomCapacityTextField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                                String newValue) {
                if (!newValue.matches("\\d*")) {
                    roomCapacityTextField.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });

    }

    private void createNewReservation() {
        // check if parameters are correctly setup
        if ((roomCapacityTextField.getText().length() == 0 || roomDeptChoiceBox.getValue() == null)
                || (startDatePicker.getValue() == null || endDatePicker.getValue() == null)
                || (startHourChoiceBox.getValue() == null || endHourChoiceBox.getValue() == null)
                || (startMinuteChoiceBox.getValue() == null || endMinuteChoiceBox.getValue() == null)) {
            it.uniroma2.ispw.spotlightapp.controllers.helpers.AlertHelper.DisplayErrorAlert("Error on input parameters", "Verify that not null parameters have been used to create the new event");
        } else {

            try {
                // retrieving room management service
                RoomManagementService roomManagementService = ServiceManager.getInstance().getRoomManagementService();

                // creating event dates
                LocalDate startDateL = startDatePicker.getValue();
                LocalDate endDateL   = endDatePicker.getValue();
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
                    it.uniroma2.ispw.spotlightapp.controllers.helpers.AlertHelper.DisplayErrorAlert("Error on input parameters", "The end date time must be after the start date time");
                    return;
                }
                if (startDate.before(getEventManagerController().getSelectedEvent().getStartDateTime()) || endDate.after(getEventManagerController().getSelectedEvent().getEndDateTime())) {
                    it.uniroma2.ispw.spotlightapp.controllers.helpers.AlertHelper.DisplayErrorAlert("Error on input parameters", "Reservation must be within the event timespan");
                    return;
                }

                // setting admin privileges if checkbox is active
                roomManagementService.setAdminPrivileges(adminPrivilegeCheckBox.isSelected());

                // creating new properties object
                RoomProperties roomProperties = new RoomProperties(Integer.valueOf(roomCapacityTextField.getText()),
                                                                   projectorCheckbox.isSelected(),
                                                                   whiteboardCheckbox.isSelected(),
                                                                   intWhiteboardCheckbox.isSelected(),
                                                                   videocallCheckbox.isSelected(),
                                                                   microphoneCheckbox.isSelected());

                if (roomManagementService.reserveRoom(eventManagerController.getSelectedEvent().getEventID(), roomProperties, roomDeptChoiceBox.getValue(), startDate, endDate) != null) {
                    it.uniroma2.ispw.spotlightapp.controllers.helpers.AlertHelper.DisplayConfirmationAlert("Room successfully reserved", "");
                    // refreshing EventManager
                    getEventManagerController().populateEventsTable();
                    getEventManagerController().populateReservationsTable(getEventManagerController().getSelectedEvent());
                    // closing the reservation window
                    Stage stage = (Stage) newReservationButton.getScene().getWindow();
                    stage.close();
                } else {
                    it.uniroma2.ispw.spotlightapp.controllers.helpers.AlertHelper.DisplayErrorAlert("Room was not reserved", "No room with given properties is available");
                }
            } catch (AuthRequiredException e) {
                it.uniroma2.ispw.spotlightapp.controllers.helpers.AlertHelper.DisplayErrorAlert("User authentication failed", "");
                e.printStackTrace();
            } catch (RoomServiceException | ReservationServiceException e) {
                it.uniroma2.ispw.spotlightapp.controllers.helpers.AlertHelper.DisplayErrorAlert("Reservation creation failed", "");
                e.printStackTrace();
            }
        }

    }

    public void setEventManagerController(EventManagerController eventManagerController) { this.eventManagerController = eventManagerController; }
    public EventManagerController getEventManagerController() { return this.eventManagerController; }
}
