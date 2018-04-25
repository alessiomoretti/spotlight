package it.uniroma2.ispw.spotlight.app.controllers;

import it.uniroma2.ispw.spotlight.services.LoginService;
import it.uniroma2.ispw.spotlight.services.ServiceManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.awt.event.ActionEvent;

public class LoginController {

    private ServiceManager serviceManager;

    @FXML
    private TextField loginUsername;

    @FXML
    private PasswordField loginPassword;

    @FXML
    private Text loginErrorText;

    public LoginController() {}

    public void loginProceedButtonAction(javafx.event.ActionEvent actionEvent) {
        // retrieving login service
        LoginService loginService = serviceManager.getInstance().getLoginService();

        System.out.println(loginUsername.getText() + " -> " + loginPassword.getText());
        loginErrorText.setVisible(true);
    }

    public void loginCancelButtonAction(javafx.event.ActionEvent actionEvent) {
        Stage stage = (Stage) loginUsername.getScene().getWindow();
        stage.close();
    }
}
