package tn.esprit.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.stage.Window;
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
                    openInterface("/GestionCitoyen.fxml", "Gestion des Citoyens");
                    break;
                case technicien:
                    openInterface("/GestionTechnicien.fxml", "Gestion des Techniciens");
                    break;
                case responsable:
                    openInterface("/GestionResponsable.fxml", "Gestion des Responsables");
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
