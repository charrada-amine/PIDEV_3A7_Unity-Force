package tn.esprit.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.kordamp.ikonli.javafx.FontIcon;
import tn.esprit.models.Session;
import tn.esprit.models.utilisateur;
import tn.esprit.services.ServiceUtilisateur;

import java.io.IOException;

public class LoginController {

    public Hyperlink forgotPasswordLink;
    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private TextField visiblePasswordField;

    @FXML
    private ToggleButton togglePasswordButton;

    @FXML
    private Button loginButton;

    @FXML
    private Button signUpButton;

    private final ServiceUtilisateur serviceUtilisateur = new ServiceUtilisateur();

    @FXML
    public void initialize() {
        // Initialiser les champs et boutons à invisibles
        visiblePasswordField.setVisible(false);
        visiblePasswordField.setManaged(false);

        // ToggleButton pour afficher/masquer le mot de passe
        togglePasswordButton.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                visiblePasswordField.setText(passwordField.getText());
                visiblePasswordField.setVisible(true);
                visiblePasswordField.setManaged(true);
                passwordField.setVisible(false);
                passwordField.setManaged(false);
                ((FontIcon) togglePasswordButton.getGraphic()).setIconLiteral("fas-eye");
            } else {
                passwordField.setText(visiblePasswordField.getText());
                passwordField.setVisible(true);
                passwordField.setManaged(true);
                visiblePasswordField.setVisible(false);
                visiblePasswordField.setManaged(false);
                ((FontIcon) togglePasswordButton.getGraphic()).setIconLiteral("fas-eye-slash");
            }
        });

        // Effet de survol pour le bouton de connexion
        loginButton.setOnMouseEntered(event -> {
            loginButton.setStyle("-fx-font-size: 18.0; -fx-font-family: 'Segoe UI'; " +
                    "-fx-background-color: linear-gradient(to right, #007BFF, #003d99); " +
                    "-fx-text-fill: white; -fx-background-radius: 10; " +
                    "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 15, 0, 0, 3); " +
                    "-fx-cursor: hand;");
        });
        loginButton.setOnMouseExited(event -> {
            loginButton.setStyle("-fx-font-size: 18.0; -fx-font-family: 'Segoe UI'; " +
                    "-fx-background-color: linear-gradient(to right, #007BFF, #0056b3); " +
                    "-fx-text-fill: white; -fx-background-radius: 10; " +
                    "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0, 0, 2); " +
                    "-fx-cursor: hand;");
        });

        // Effet de survol pour le bouton d'inscription
        signUpButton.setOnMouseEntered(event -> {
            signUpButton.setStyle("-fx-font-size: 16.0; -fx-font-family: 'Segoe UI'; " +
                    "-fx-background-color: #e6f0fa; " +
                    "-fx-text-fill: #007BFF; -fx-border-color: #007BFF; " +
                    "-fx-border-radius: 10; -fx-background-radius: 10; " +
                    "-fx-cursor: hand;");
        });
        signUpButton.setOnMouseExited(event -> {
            signUpButton.setStyle("-fx-font-size: 16.0; -fx-font-family: 'Segoe UI'; " +
                    "-fx-background-color: transparent; -fx-text-fill: #007BFF; " +
                    "-fx-border-color: #007BFF; -fx-border-radius: 10; " +
                    "-fx-background-radius: 10; -fx-cursor: hand;");
        });
    }

    @FXML
    private void handleLogin(ActionEvent event) {
        String email = emailField.getText();
        String password = togglePasswordButton.isSelected() ? visiblePasswordField.getText() : passwordField.getText();

        if (email.isEmpty() || password.isEmpty()) {
            showAlert("Erreur", "Veuillez remplir tous les champs.");
            return;
        }

        utilisateur utilisateur = serviceUtilisateur.getByEmailAndPassword(email, password);

        if (utilisateur == null) {
            showAlert("Échec de connexion", "Email ou mot de passe incorrect.");
        } else {
            // Enregistrer l'utilisateur dans la session
            Session.setCurrentUser(utilisateur);

            // Ouvrir l'interface en fonction du rôle
            switch (utilisateur.getRole()) {
                case citoyen:
                    openInterface("/Menu.fxml", "Gestion des Citoyens");
                    break;
                case technicien:
                    openInterface("/Menu.fxml", "Gestion des Techniciens");
                    break;
                case responsable:
                    openInterface("/Menu.fxml", "Gestion des Responsables");
                    break;
                default:
                    showAlert("Erreur", "Rôle inconnu.");
                    break;
            }
        }
    }

    @FXML
    private void handleSignUp(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AddUser.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Inscription - Ajouter un utilisateur");
            stage.setScene(new Scene(root));
            stage.show();
            stage.setMaximized(true);

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible d'ouvrir le formulaire d'inscription.");
        }
    }

    @FXML
    private void handleForgotPassword(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ForgotPasswordEmail.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Réinitialisation du mot de passe");
            stage.setMaximized(true);

            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void openInterface(String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle(title);
            Scene scene = new Scene(root);
            stage.setScene(scene);

            // Maximiser la fenêtre comme dans start()
            stage.setMaximized(true);

            stage.show();

            // Fermer la fenêtre actuelle (la fenêtre de connexion)
            Stage currentStage = (Stage) loginButton.getScene().getWindow();
            currentStage.close();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible d'ouvrir l'interface : " + title);
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