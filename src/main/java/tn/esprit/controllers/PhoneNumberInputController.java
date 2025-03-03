package tn.esprit.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import tn.esprit.utils.SmsUser;

import java.io.IOException;

public class PhoneNumberInputController {

    @FXML
    private TextField phoneField;  // Champ pour entrer le numéro de téléphone

    private Stage addUserStage;  // Référence à la fenêtre AddUser

    public void setAddUserStage(Stage stage) {
        this.addUserStage = stage;
    }

    @FXML
    void handleSendCode(ActionEvent event) {
        String phoneNumber = phoneField.getText().trim();

        // Validation du numéro de téléphone
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

        // Fermer la fenêtre actuelle (PhoneNumberInput)
        closeWindow(phoneField);


        // Ouvrir la fenêtre de connexion (Login.fxml)
        loadLoginPage();
    }

    // Fermer une fenêtre à partir d'un élément de la scène
    private void closeWindow(TextField field) {
        Stage stage = (Stage) field.getScene().getWindow();
        stage.close();
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
}