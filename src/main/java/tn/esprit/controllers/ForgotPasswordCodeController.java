package tn.esprit.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class ForgotPasswordCodeController {

    @FXML
    private TextField codeField;

    private String email;
    private int generatedCode;

    public void setEmailAndCode(String email, int code) {
        this.email = email;
        this.generatedCode = code;
    }

    @FXML
    private void handleValidateCode() {
        String enteredCode = codeField.getText().trim();

        if (enteredCode.isEmpty()) {
            showAlert("Erreur", "Veuillez entrer le code de vérification.");
            return;
        }

        if (Integer.parseInt(enteredCode) == generatedCode) {
            showSuccessAlert("Succès", "Code vérifié avec succès !");

            // Ouvrir l'interface pour saisir le nouveau mot de passe
            openForgotPasswordNewPasswordInterface(email);
        } else {
            showAlert("Erreur", "Code incorrect. Vérifiez votre email et réessayez.");
        }
    }

    private void openForgotPasswordNewPasswordInterface(String email) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ForgotPasswordNewPassword.fxml"));
            Parent root = loader.load();

            ForgotPasswordNewPasswordController controller = loader.getController();
            controller.setEmail(email);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Saisir le nouveau mot de passe");
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