package tn.esprit.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class MenuController {

    @FXML
    private Button btnProfile;

    @FXML
    private Button btnSource;

    @FXML
    private void handleProfile() {
        switchScene("/ProfileInterface.fxml", btnProfile);
    }

    @FXML
    private void handleSource() {
        switchScene("/SourceInterface.fxml", btnSource);
    }

    private void switchScene(String fxmlPath, Button button) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Stage stage = (Stage) button.getScene().getWindow();

            // Définir la scène sans taille fixe
            Scene scene = new Scene(root);
            stage.setScene(scene);

            // Permettre le redimensionnement de la fenêtre
            stage.setResizable(true);

            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
