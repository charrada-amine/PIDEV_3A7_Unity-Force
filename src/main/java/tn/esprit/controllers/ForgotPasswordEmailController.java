package tn.esprit.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import tn.esprit.services.ServiceUtilisateur;

import javax.mail.MessagingException;

public class ForgotPasswordEmailController {

    @FXML
    private TextField emailField;

    private final ServiceUtilisateur serviceUtilisateur = new ServiceUtilisateur();

    @FXML
    private void handleSendCode() {
        String email = emailField.getText().trim();

        if (email.isEmpty() || !serviceUtilisateur.isValidEmail(email)) {
            showAlert("Erreur", "Veuillez saisir une adresse email valide.");
            return;
        }

        // Générer un code de vérification
        int code = serviceUtilisateur.generateVerificationCode();

        // Envoyer le code par email
        try {
            serviceUtilisateur.sendEmail(email, "Code de réinitialisation", "Votre code de réinitialisation est : " + code);
            showSuccessAlert("Succès", "Un code de vérification a été envoyé à votre email.");

            // Ouvrir l'interface pour saisir le code
            openForgotPasswordCodeInterface(email, code);

        } catch (MessagingException e) {
            e.printStackTrace();
            showAlert("Erreur", "Une erreur s'est produite lors de l'envoi de l'email.");
        }
    }

    private void openForgotPasswordCodeInterface(String email, int code) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ForgotPasswordCode.fxml"));
            Parent root = loader.load();

            ForgotPasswordCodeController controller = loader.getController();
            controller.setEmailAndCode(email, code);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Saisir le code de vérification");
            stage.setMaximized(true);

            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
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