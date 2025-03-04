package tn.esprit.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class MainMenuController {

    @FXML
    private void navigateToLampadaires(ActionEvent event) throws IOException {
        loadView("GestionLampadaire.fxml", event);
    }

    @FXML
    private void navigateToZones(ActionEvent event) throws IOException {
        loadView("GestionZone.fxml", event);
    }

    private void loadView(String fxmlFile, ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/" + fxmlFile));
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
    }
}