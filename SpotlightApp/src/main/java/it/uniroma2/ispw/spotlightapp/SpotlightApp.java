package it.uniroma2.ispw.spotlightapp;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class SpotlightApp extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("fxml/login.fxml"));

        Scene scene = new Scene(root, 434, 260);

        stage.setTitle("Spotlight - Login");
        stage.setScene(scene);
        stage.show();
    }
}
