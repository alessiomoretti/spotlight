package it.uniroma2.ispw.spotlightapp.controllers;

import it.uniroma2.ispw.spotlight.exceptions.AuthServiceException;
import it.uniroma2.ispw.spotlight.helpers.MD5Helper;
import it.uniroma2.ispw.spotlight.services.LoginService;
import it.uniroma2.ispw.spotlight.services.ServiceManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {


    @FXML private TextField loginUsername;
    @FXML private PasswordField loginPassword;
    @FXML private Text loginErrorText;

    public LoginController() {}

    public void loginProceedButtonAction(javafx.event.ActionEvent actionEvent) {
        // retrieving login service
        LoginService loginService = ServiceManager.getInstance().getLoginService();

        // retrieving username and password and check if not empty
        if (loginUsername.getText().length() == 0 || loginPassword.getText().length() == 0) {
            loginErrorText.setVisible(true);
        } else {
            // computing password MD5
            String hashedPwd = MD5Helper.getHashedString(loginPassword.getText());

            // authenticating user
            try {
                boolean authenticated = loginService.authenticateUser(loginUsername.getText(), hashedPwd);
                if (!authenticated)
                    loginErrorText.setVisible(true);
                else {
                    // changing scene to main app
                    Stage stage = (Stage) loginUsername.getScene().getWindow();
                    Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("fxml/spotlight.fxml"));
                    Scene scene = new Scene(root, 1000, 600);
                    it.uniroma2.ispw.spotlightapp.controllers.helpers.StageHelper.centerStage(stage, 1000, 600);
                    stage.setTitle("Spotlight - App");
                    stage.setScene(scene);
                }
            } catch(AuthServiceException e) {
                e.printStackTrace();
                it.uniroma2.ispw.spotlightapp.controllers.helpers.AlertHelper.DisplayErrorAlert("Error occured during login!", "An error occured while performing query on the user database");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void loginCancelButtonAction(javafx.event.ActionEvent actionEvent) {
        Stage stage = (Stage) loginUsername.getScene().getWindow();
        stage.close();
    }
}
