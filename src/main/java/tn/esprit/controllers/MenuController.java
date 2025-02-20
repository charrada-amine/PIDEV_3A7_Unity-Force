package tn.esprit.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class MenuController {

    @FXML
    private Button btnGestionCapteur;

    @FXML
    private Button btnGestionCitoyen;

    @FXML
    private Button btnGestionDonnee;

    @FXML
    private Button btnGestionIntervention;

    @FXML
    private Button btnGestionLampadaire;

    @FXML
    private Button btnGestionReclamation;

    @FXML
    private Button btnGestionResponsable;

    @FXML
    private Button btnGestionTechnicien;

    @FXML
    private Button btnGestionUtilisateur;

    @FXML
    private Button btnGestionZone; // Nouveau bouton

    @FXML
    private Button btnProfileInterface;

    @FXML
    private Button btnSourceInterface;

    @FXML
    private void handleGestionCapteur() {
        switchScene("/GestionCapteur.fxml", btnGestionCapteur);
    }

    @FXML
    private void handleGestionCitoyen() {
        switchScene("/GestionCitoyen.fxml", btnGestionCitoyen);
    }

    @FXML
    private void handleGestionDonnee() {
        switchScene("/GestionDonnee.fxml", btnGestionDonnee);
    }

    @FXML
    private void handleGestionIntervention() {
        switchScene("/GestionIntervention.fxml", btnGestionIntervention);
    }

    @FXML
    private void handleGestionLampadaire() {
        switchScene("/GestionLampadaire.fxml", btnGestionLampadaire);
    }

    @FXML
    private void handleGestionReclamation() {
        switchScene("/GestionReclamation.fxml", btnGestionReclamation);
    }

    @FXML
    private void handleGestionResponsable() {
        switchScene("/GestionResponsable.fxml", btnGestionResponsable);
    }

    @FXML
    private void handleGestionTechnicien() {
        switchScene("/GestionTechnicien.fxml", btnGestionTechnicien);
    }

    @FXML
    private void handleGestionUtilisateur() {
        switchScene("/GestionUtilisateur.fxml", btnGestionUtilisateur);
    }

    @FXML
    private void handleGestionZone() { // Nouvelle méthode
        switchScene("/GestionZone.fxml", btnGestionZone);
    }

    @FXML
    private void handleProfileInterface() {
        switchScene("/ProfileInterface.fxml", btnProfileInterface);
    }

    @FXML
    private void handleSourceInterface() {
        switchScene("/SourceInterface.fxml", btnSourceInterface);
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