package tn.esprit.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import tn.esprit.services.ServiceUtilisateur;

public class ForgotPasswordNewPasswordController {

    @FXML
    private PasswordField newPasswordField;

    @FXML
    private PasswordField confirmPasswordField;

    private String email;
    private final ServiceUtilisateur serviceUtilisateur = new ServiceUtilisateur();

    public void setEmail(String email) {
        this.email = email;
    }

    @FXML
    private void handleChangePassword() {
        String newPassword = newPasswordField.getText().trim();
        String confirmPassword = confirmPasswordField.getText().trim();

        if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
            showAlert("Erreur", "Veuillez remplir tous les champs.");
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            showAlert("Erreur", "Les mots de passe ne correspondent pas.");
            return;
        }

        // Mettre à jour le mot de passe dans la base de données
        if (serviceUtilisateur.updatePassword(email, newPassword)) {
            showSuccessAlert("Succès", "Votre mot de passe a été modifié avec succès !");
        } else {
            showAlert("Erreur", "Erreur lors de la mise à jour du mot de passe.");
        }
    }

    // Méthode pour afficher une alerte
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Méthode pour afficher une alerte de succès
    private void showSuccessAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}