package tn.esprit.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
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
    private Button btnAccueil; // Nouveau bouton pour revenir à l'accueil

    // Handler pour le bouton de gestion des capteurs
    @FXML
    private void handleGestionCapteur(ActionEvent event) {
        switchScene(event, "/GestionCapteur.fxml");
    }

    // Handler pour le bouton de gestion des citoyens
    @FXML
    private void handleGestionCitoyen(ActionEvent event) {
        switchScene(event, "/GestionCitoyen.fxml");
    }

    // Handler pour le bouton de gestion des données
    @FXML
    private void handleGestionDonnee(ActionEvent event) {
        switchScene(event, "/GestionDonnee.fxml");
    }

    // Handler pour le bouton de gestion des interventions
    @FXML
    private void handleGestionIntervention(ActionEvent event) {
        switchScene(event, "/GestionIntervention.fxml");
    }

    // Handler pour le bouton de gestion des lampadaires
    @FXML
    private void handleGestionLampadaire(ActionEvent event) {
        switchScene(event, "/GestionLampadaire.fxml");
    }

    // Handler pour le bouton de gestion des réclamations
    @FXML
    private void handleGestionReclamation(ActionEvent event) {
        switchScene(event, "/GestionReclamation.fxml");
    }

    // Handler pour le bouton de gestion des responsables
    @FXML
    private void handleGestionResponsable(ActionEvent event) {
        switchScene(event, "/GestionResponsable.fxml");
    }

    // Handler pour le bouton de gestion des techniciens
    @FXML
    private void handleGestionTechnicien(ActionEvent event) {
        switchScene(event, "/GestionTechnicien.fxml");
    }

    // Handler pour le bouton de gestion des utilisateurs
    @FXML
    private void handleGestionUtilisateur(ActionEvent event) {
        switchScene(event, "/GestionUtilisateur.fxml");
    }

    // Handler pour le bouton de gestion des zones
    @FXML
    private void handleGestionZone(ActionEvent event) {
        switchScene(event, "/GestionZone.fxml");
    }

    // Handler pour le bouton de gestion du profil
    @FXML
    private void handleProfileInterface(ActionEvent event) {
        switchScene(event, "/ProfileInterface.fxml");
    }

    // Handler pour le bouton des sources
    @FXML
    private void handleSourceInterface(ActionEvent event) {
        switchScene(event, "/SourceInterface.fxml");
    }

    // Handler pour revenir à la page d'accueil (Menu)
    @FXML
    private void handleAccueil(ActionEvent event) {
        switchScene(event, "/Menu.fxml");
    }

    // Méthode pour revenir à la page précédente (en option, à personnaliser)
    @FXML
    private void handleBack() {
        System.out.println("Retour à la page précédente");
    }

    // Méthode de commutation de scène (load des FXML et mise à jour de la scène)
    private void switchScene(ActionEvent event, String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            // Récupère la scène actuelle et met à jour son contenu
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
