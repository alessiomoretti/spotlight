package it.uniroma2.ispw.spotlight.app.controllers;

import javafx.scene.control.Alert;

public class AlertHelper {

    public static void DisplayErrorAlert(String errorMessage, String errorDetails) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("ERROR");
        alert.setHeaderText(errorMessage);
        alert.setContentText(errorDetails);
        alert.show();
    }

    public static void DisplayConfirmationAlert(String infoMessage, String infoDetails) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("SUCCESS");
        alert.setHeaderText(infoMessage);
        alert.setContentText(infoDetails);
        alert.show();
    }

    public static Alert DisplayConfirmationAlert(String infoMessage) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("CONFIRM");
        alert.setHeaderText(infoMessage);
        alert.showAndWait();
        return alert;
    }
}
