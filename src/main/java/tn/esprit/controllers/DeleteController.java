package tn.esprit.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.event.ActionEvent;
import tn.esprit.models.Role;
import tn.esprit.models.utilisateur;
import tn.esprit.services.ServiceCitoyen;
import tn.esprit.services.ServiceResponsable;
import tn.esprit.services.ServiceTechnicien;
import tn.esprit.services.ServiceUtilisateur;

import java.util.Optional;

public class DeleteController {

    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;

    private ServiceUtilisateur serviceUtilisateur = new ServiceUtilisateur();
    private final ServiceResponsable serviceResponsable = new ServiceResponsable();

    private final ServiceCitoyen serviceCitoyen = new ServiceCitoyen();
    private final ServiceTechnicien serviceTechnicien = new ServiceTechnicien();


    // Méthode pour gérer la suppression du citoyen
    @FXML
    private void handleDelete(ActionEvent event) {
        String email = emailField.getText().trim();
        String password = passwordField.getText().trim();

        if (email.isEmpty() || password.isEmpty()) {
            showAlert("Erreur", "L'email et le mot de passe ne peuvent pas être vides.");
            return;
        }

        // Logique pour vérifier si le citoyen existe avec cet email et mot de passe
        utilisateur user = serviceUtilisateur.getByEmailAndPassword(email, password);
        if (user == null) {
            showAlert("Erreur", "Email ou mot de passe incorrect.");
            return;
        }

        // Demander une confirmation avant la suppression
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation");
        confirmation.setHeaderText("Voulez-vous vraiment supprimer l' Utilisateur : " +
                user.getNom() + " " + user.getPrenom() + "?");

        Optional<ButtonType> result = confirmation.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            serviceUtilisateur.deleteById(user.getId_utilisateur());
            showAlert("Succès", "Utilisateur supprimé avec succès !");
            loadUsers(); // Recharger la liste après suppression
        }
    }

    // Méthode pour gérer l'annulation
   /* @FXML
    private void handleCancel(ActionEvent event) {
        // Logic for closing or cancelling the deletion process
        System.out.println("Annulation de la suppression.");
    }*/

    // Méthode pour afficher une alerte
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    // Méthode pour charger les utilisateurs (à implémenter)
    private void loadUsers() {
        // Logic to reload users
    }
}
