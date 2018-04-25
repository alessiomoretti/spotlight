package it.uniroma2.ispw.spotlight.app.controllers;

import it.uniroma2.ispw.spotlight.helpers.MD5Helper;
import it.uniroma2.ispw.spotlight.services.LoginService;
import it.uniroma2.ispw.spotlight.services.ServiceManager;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.awt.event.ActionEvent;

public class LoginController {


    @FXML
    private TextField loginUsername;

    @FXML
    private PasswordField loginPassword;

    @FXML
    private Text loginErrorText;

    public LoginController() {}

    public void loginProceedButtonAction(javafx.event.ActionEvent actionEvent) {
        // retrieving login service
        LoginService loginService = ServiceManager.getInstance().getLoginService();

        // computing password MD5
        String hashedPwd = MD5Helper.getHashedString(loginPassword.getText());

        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("ERROR");
        alert.setHeaderText("Error on password digest computation");
        alert.show();

        System.out.println(loginUsername.getText() + " -> " + hashedPwd);
        loginErrorText.setVisible(true);
    }

    public void loginCancelButtonAction(javafx.event.ActionEvent actionEvent) {
        Stage stage = (Stage) loginUsername.getScene().getWindow();
        stage.close();
    }
}
