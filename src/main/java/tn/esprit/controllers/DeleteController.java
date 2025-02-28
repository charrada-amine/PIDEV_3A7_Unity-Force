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

    @FXML
    private void handleDeleteResponsable(ActionEvent event) {
        String email = emailField.getText().trim();
        String password = passwordField.getText().trim();

        if (email.isEmpty() || password.isEmpty()) {
            showAlert("Erreur", "L'email et le mot de passe ne peuvent pas être vides.");
            return;
        }

        // Logique pour vérifier si le responsable existe avec cet email et mot de passe
        utilisateur responsable = serviceUtilisateur.getByEmailAndPassword(email, password);
        if (responsable == null || responsable.getRole() != Role.responsable) {
            showAlert("Erreur", "Email ou mot de passe incorrect, ou utilisateur non responsable.");
            return;
        }

        // Demander une confirmation avant la suppression
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation");
        confirmation.setHeaderText("Voulez-vous vraiment supprimer le responsable : " +
                responsable.getNom() + " " + responsable.getPrenom() + "?");

        Optional<ButtonType> result = confirmation.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            // Supprimer le responsable
            serviceResponsable.deleteById(responsable.getId_utilisateur());
            showAlert("Succès", "Responsable supprimé avec succès !");
            loadUsers(); // Recharger la liste après suppression
        }
    }
    @FXML
    private void handleDeleteTechnicien(ActionEvent event) {
        String email = emailField.getText().trim();
        String password = passwordField.getText().trim();

        if (email.isEmpty() || password.isEmpty()) {
            showAlert("Erreur", "L'email et le mot de passe ne peuvent pas être vides.");
            return;
        }

        // Logique pour vérifier si le technicien existe avec cet email et mot de passe
        utilisateur technicien = serviceUtilisateur.getByEmailAndPassword(email, password);
        if (technicien == null || technicien.getRole() != Role.technicien) {
            showAlert("Erreur", "Email ou mot de passe incorrect, ou utilisateur non technicien.");
            return;
        }

        // Demander une confirmation avant la suppression
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation");
        confirmation.setHeaderText("Voulez-vous vraiment supprimer le technicien : " +
                technicien.getNom() + " " + technicien.getPrenom() + "?");

        Optional<ButtonType> result = confirmation.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            // Supprimer le technicien
            serviceTechnicien.deleteById(technicien.getId_utilisateur());
            showAlert("Succès", "Technicien supprimé avec succès !");
            loadUsers(); // Recharger la liste après suppression
        }
    }
    // Méthode pour gérer la suppression du citoyen
    @FXML
    private void handleDeleteCitoyen(ActionEvent event) {
        String email = emailField.getText().trim();
        String password = passwordField.getText().trim();

        if (email.isEmpty() || password.isEmpty()) {
            showAlert("Erreur", "L'email et le mot de passe ne peuvent pas être vides.");
            return;
        }

        // Logique pour vérifier si le citoyen existe avec cet email et mot de passe
        utilisateur citizen = serviceUtilisateur.getByEmailAndPassword(email, password);
        if (citizen == null) {
            showAlert("Erreur", "Email ou mot de passe incorrect.");
            return;
        }

        // Demander une confirmation avant la suppression
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation");
        confirmation.setHeaderText("Voulez-vous vraiment supprimer le citoyen : " +
                citizen.getNom() + " " + citizen.getPrenom() + "?");

        Optional<ButtonType> result = confirmation.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            serviceCitoyen.deleteById(citizen.getId_utilisateur());
            showAlert("Succès", "Citoyen supprimé avec succès !");
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
