package it.uniroma2.ispw.spotlight.app.controllers;

import javafx.scene.control.Alert;

public class AlertHelper {

    public static void DisplayErrorAlert(String errorMessage) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("ERROR");
        alert.setHeaderText(errorMessage);
        alert.show();
    }
}
