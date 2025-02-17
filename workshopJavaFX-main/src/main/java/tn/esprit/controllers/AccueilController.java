package tn.esprit.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class AccueilController {

    // Méthode générique pour ouvrir l'interface basée sur le rôle
    private void ouvrirInterface(String role) {
        try {
            // Charger dynamiquement le fichier FXML en fonction du rôle
            String fxmlPath = "/GestionUtilisateur.fxml";
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            // Récupérer le contrôleur de l'interface ouverte
            Object controller = loader.getController();

            // Si le contrôleur dispose d'une méthode pour accepter un rôle, on la passe
            if (controller instanceof GestionUtilisateurController) {
                ((GestionUtilisateurController) controller).setRoleSelectionne(role);
            }

            // Créer et afficher le stage (fenêtre)
            Stage stage = new Stage();
            stage.setTitle("Gestion des " + role);
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Méthodes d'ouverture des différentes interfaces
    @FXML
    private void ouvrirInterfaceTous() {
        ouvrirInterface("Utilisateur"); // On suppose que "Tous" pourrait être représenté par "Utilisateur"
    }

    @FXML
    private void ouvrirInterfaceCitoyens() {
        ouvrirInterface("Citoyen");
    }

    @FXML
    private void ouvrirInterfaceTechniciens() {
        ouvrirInterface("Technicien");
    }

    @FXML
    private void ouvrirInterfaceResponsables() {
        ouvrirInterface("Responsable");
    }
}
