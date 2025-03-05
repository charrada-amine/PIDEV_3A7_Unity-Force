package tn.esprit.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;
import tn.esprit.models.utilisateur;
import tn.esprit.services.ServiceUtilisateur;

import java.io.IOException;

public class ForgotPasswordNewPasswordController {

    @FXML
    private PasswordField newPasswordField;

    @FXML
    private PasswordField confirmPasswordField;

    private String email;
    private final ServiceUtilisateur serviceUtilisateur = new ServiceUtilisateur();

    private Stage forgetPasswordEmailStage;
    private Stage forgetPasswordCodeStage;
    private Stage mainStage;  // Optionnellement, vous pouvez garder une référence à la fenêtre principale.

    public void setEmail(String email) {
        this.email = email;
    }

    public void setForgetPasswordEmailStage(Stage stage) {
        this.forgetPasswordEmailStage = stage;
    }

    public void setForgetPasswordCodeStage(Stage stage) {
        this.forgetPasswordCodeStage = stage;
    }

    // Optionnel : méthode pour la fenêtre principale
    public void setMainStage(Stage stage) {
        this.mainStage = stage;
    }

    @FXML
    private void handleChangePassword() {
        String newPassword = newPasswordField.getText().trim();
        String confirmPassword = confirmPasswordField.getText().trim();

        // Validation des champs
        if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
            showAlert("Erreur", "Veuillez remplir tous les champs.");
            return;
        }

        // Vérification de la correspondance des mots de passe
        if (!newPassword.equals(confirmPassword)) {
            showAlert("Erreur", "Les mots de passe ne correspondent pas.");
            return;
        }
        // Validation du mot de passe (au moins 8 caractères, un chiffre, une majuscule)
        if (newPassword.length() < 8 || !newPassword.matches(".*\\d.*") || !newPassword.matches(".*[A-Z].*")) {
            showAlert("Erreur", "Le mot de passe doit comporter au moins 8 caractères, un chiffre et une lettre majuscule.");
            return;
        }


        // Mise à jour du mot de passe dans la base de données
        if (serviceUtilisateur.updatePassword(email, newPassword)) {
            showSuccessAlert("Succès", "Votre mot de passe a été modifié avec succès !");

            // Essayer de se reconnecter immédiatement avec le nouveau mot de passe
            utilisateur user = serviceUtilisateur.getByEmailAndPassword(email, newPassword);




            // Charger et afficher la page de connexion
            loadLoginPage();

            // Fermer les fenêtres spécifiques
            closeAllStages();

            // Fermer la fenêtre actuelle
            closeCurrentWindow();
        } else {
            showAlert("Erreur", "Erreur lors de la mise à jour du mot de passe.");
        }
    }

    private void loadLoginPage() {
        try {
            // Charger le fichier FXML de la page de connexion
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Login.fxml"));
            Parent root = loader.load();

            // Créer une nouvelle scène
            Scene scene = new Scene(root);

            // Créer une nouvelle fenêtre (Stage)
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Connexion");
            stage.setMaximized(true); // Maximiser la fenêtre si nécessaire
            stage.show(); // Afficher la fenêtre
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de charger la page de connexion.");
        }
    }

    // Fermer toutes les fenêtres ouvertes (Stages)
    private void closeAllStages() {
        if (forgetPasswordEmailStage != null) {
            forgetPasswordEmailStage.close();
        }
        if (forgetPasswordCodeStage != null) {
            forgetPasswordCodeStage.close();
        }
        if (mainStage != null) {
            mainStage.close();  // Si vous avez une référence à la fenêtre principale, vous pouvez aussi la fermer
        }
    }

    private void closeCurrentWindow() {
        Stage currentStage = (Stage) newPasswordField.getScene().getWindow();
        currentStage.close();
    }

    /**
     * Affiche une alerte d'erreur.
     */
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Affiche une alerte de succès.
     */
    private void showSuccessAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}
