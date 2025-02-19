package tn.esprit.controllers;

<<<<<<< HEAD
import javafx.event.ActionEvent; // Added missing import
=======
import javafx.event.ActionEvent;
>>>>>>> origin/MedRayenSansa/GestionInfrastructure
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class MainMenuController {

    @FXML
<<<<<<< HEAD
    private void navigateToInterventions(ActionEvent event) throws IOException {
        loadView("GestionIntervention.fxml", event);
    }

    @FXML
    private void navigateToReclamations(ActionEvent event) throws IOException {
        loadView("GestionReclamation.fxml", event);
=======
    private void navigateToLampadaires(ActionEvent event) throws IOException {
        loadView("GestionLampadaire.fxml", event);
    }

    @FXML
    private void navigateToZones(ActionEvent event) throws IOException {
        loadView("GestionZone.fxml", event);
>>>>>>> origin/MedRayenSansa/GestionInfrastructure
    }

    private void loadView(String fxmlFile, ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/" + fxmlFile));
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
<<<<<<< HEAD
        stage.show(); // Added missing show() call
=======
>>>>>>> origin/MedRayenSansa/GestionInfrastructure
    }
}