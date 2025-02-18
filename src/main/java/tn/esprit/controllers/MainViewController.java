package tn.esprit.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.VBox;
import java.io.IOException;
import java.util.Collections;

public class MainViewController {

    @FXML
    private VBox contentArea;

    @FXML
    private void initialize() {
        loadView("/GestionIntervention.fxml");
    }

    @FXML
    private void showInterventions() {
        loadView("/GestionIntervention.fxml");
    }

    @FXML
    private void showReclamations() {
        loadView("/GestionReclamation.fxml");
    }

    private void loadView(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            contentArea.getChildren().setAll(Collections.singletonList(root));

        } catch (IOException e) {
            showAlert("Erreur", "Impossible de charger la vue: " + fxmlPath);
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private boolean showConfirmation(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        return alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK;
    }
}
