package it.uniroma2.ispw.spotlight.app.controllers;

import it.uniroma2.ispw.spotlight.exceptions.AuthRequiredException;
import it.uniroma2.ispw.spotlight.exceptions.EventServiceException;
import it.uniroma2.ispw.spotlight.helpers.CalendarHelper;
import it.uniroma2.ispw.spotlight.services.DataAccesServices.EventManagementService;
import it.uniroma2.ispw.spotlight.services.ServiceManager;
import it.uniroma2.ispw.spotlight.users.User;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.Date;

public class NewEventController {

    @FXML private TextField eventNameTextField;
    @FXML private TextField eventReferralTextField;
    @FXML private TextField eventMailTextField;
    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;
    @FXML private ChoiceBox<Integer> startHourChoiceBox;
    @FXML private ChoiceBox<Integer> endHourChoiceBox;
    @FXML private ChoiceBox<String> startMinuteChoiceBox;
    @FXML private ChoiceBox<String> endMinuteChoiceBox;

    @FXML private Button newEventButton;

    private EventManagerController eventManagerController;

    public NewEventController() { }

    @FXML
    public void initialize() {
        // retrieving user
        User currentUser = null;
        try {
            currentUser = ServiceManager.getInstance().getLoginService().getCurrentUser();
        } catch (AuthRequiredException e) {
            AlertHelper.DisplayErrorAlert("User authentication failed", "");
            e.printStackTrace();
        }

        // pre setting referral and mailing list
        if (currentUser != null) {
            eventReferralTextField.setText(currentUser.getName());
            eventReferralTextField.setDisable(true);
            eventMailTextField.setText(currentUser.getEmailAddress());
            eventMailTextField.setDisable(true);
        }

        // populating choiceboxes
        for (int i = 8; i <= 20; i++) {
            startHourChoiceBox.getItems().add(i);
            endHourChoiceBox.getItems().add(i);
        }
        startMinuteChoiceBox.getItems().setAll("0", "15", "30", "45");
        endMinuteChoiceBox.getItems().setAll("0", "15", "30", "45");

        // adding action on new event button
        newEventButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                createNewEvent();
            }
        });
    }

    private void createNewEvent() {
        // check on initialization parameters
        if ((eventMailTextField.getText().length() == 0 || eventNameTextField.getText().length() == 0)
            || (startDatePicker.getValue() == null || endDatePicker.getValue() == null)
            || (startHourChoiceBox.getValue() == null || endHourChoiceBox.getValue() == null)
            || (startMinuteChoiceBox.getValue() == null || endMinuteChoiceBox.getValue() == null)) {
            AlertHelper.DisplayErrorAlert("Error on input parameters", "Verify that not null parameters have been used to create the new event");
        } else {

            try {
                // retrieving event service
                EventManagementService eventManagementService = ServiceManager.getInstance().getEventManagementService();

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
                    AlertHelper.DisplayErrorAlert("Error on input parameters", "The end date time must be after the start date time");
                    return;
                }

                // create new event
                eventManagementService.createNewEvent(eventNameTextField.getText(), startDate, endDate);
                AlertHelper.DisplayConfirmationAlert("Event successfully created!", "");

                // refreshing EventManager
                this.eventManagerController.populateEventsTable();

            } catch (AuthRequiredException e) {
                AlertHelper.DisplayErrorAlert("User authentication failed", "");
                e.printStackTrace();
            } catch (EventServiceException e) {
                AlertHelper.DisplayErrorAlert("Event creation failed", "");
                e.printStackTrace();
            } finally {
                Stage stage = (Stage) newEventButton.getScene().getWindow();
                stage.close();
            }
        }
    }

    public void setEventManagerController(EventManagerController eventManagerController) { this.eventManagerController = eventManagerController; }
    public EventManagerController getEventManagerController() { return this.eventManagerController; }
}
