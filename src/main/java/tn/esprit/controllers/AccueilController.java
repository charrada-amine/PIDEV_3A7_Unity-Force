package tn.esprit.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class AccueilController {

    // Méthode pour afficher l'écran d'accueil en plein écran
    @FXML
    private void ouvrirInterfaceTous() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/GestionUtilisateur.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            Scene scene = new Scene(root);

            stage.setScene(scene);
            stage.setTitle("Gestion des Utilisateurs");

            // Passer la fenêtre en plein écran
            stage.setMaximized(true); // Maximiser la fenêtre
            // stage.setFullScreen(true); // (Optionnel) Activer le mode plein écran complet

            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Autres méthodes pour ouvrir d'autres interfaces
    @FXML
    private void ouvrirInterfaceCitoyens() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/GestionCitoyen.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            Scene scene = new Scene(root);

            stage.setScene(scene);
            stage.setTitle("Gestion des Citoyens");

            // Maximiser automatiquement
            stage.setMaximized(true);

            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void ouvrirInterfaceTechniciens() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/GestionTechnicien.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            Scene scene = new Scene(root);

            stage.setScene(scene);
            stage.setTitle("Gestion des Techniciens");

            // Maximiser automatiquement
            stage.setMaximized(true);

            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void ouvrirInterfaceResponsables() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/GestionResponsable.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            Scene scene = new Scene(root);

            stage.setScene(scene);
            stage.setTitle("Gestion des Responsables");

            // Maximiser automatiquement
            stage.setMaximized(true);

            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
