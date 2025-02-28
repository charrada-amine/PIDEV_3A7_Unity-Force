package tn.esprit.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import tn.esprit.utils.SmsUser;

public class PhoneNumberInputController {

    @FXML
    private TextField phoneField;  // Champ pour entrer le numéro de téléphone

    @FXML
    void handleSendCode(ActionEvent event) {
        String phoneNumber = phoneField.getText().trim();

        // Validation du numéro de téléphone (format international)
        if (!phoneNumber.matches("\\+\\d{10,15}")) {
            showAlert("Alerte", "Le numéro de téléphone doit être au format international (+21612345678).");
            return;
        }

        // Message à envoyer
        String messageBody = "Votre compte est créé avec succès.";

        // Envoi du SMS via Twilio
        SmsUser.sendSms(phoneNumber, messageBody);

        // Affichage d'une alerte de confirmation
        showAlert("Succès", "Un message de confirmation a été envoyé à " + phoneNumber);
        showAlert("Succès", "Utilisateur ajouté avec succès.");

    }

    // Méthodes utilitaires
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void closeWindow() {
        // Fermer la fenêtre actuelle
        phoneField.getScene().getWindow().hide();
    }
}