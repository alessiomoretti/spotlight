package it.uniroma2.ispw.spotlightapp.controllers;

import it.uniroma2.ispw.spotlight.Constants;
import it.uniroma2.ispw.spotlight.exceptions.AuthRequiredException;
import it.uniroma2.ispw.spotlight.services.ServiceManager;
import it.uniroma2.ispw.spotlightapp.controllers.helpers.AlertHelper;
import javafx.fxml.FXML;
import javafx.scene.control.Tab;

public class SpotlightController {

    @FXML Tab eventLookupTab, eventManagerTab, roomLookupTab;

    @FXML
    public void initialize() {

        try {
            if (ServiceManager.getInstance().getLoginService().getCurrentUser().getRole() < Constants.TEACHER_ROLE) {
                eventManagerTab.setDisable(true);
                roomLookupTab.setDisable(true);
            }
        } catch (AuthRequiredException e) {
            e.printStackTrace();
            AlertHelper.DisplayErrorAlert("User authentication failed", "");

        }
    }
}
