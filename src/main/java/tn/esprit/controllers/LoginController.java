package tn.esprit.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import org.kordamp.ikonli.javafx.FontIcon;
import tn.esprit.models.utilisateur;
import tn.esprit.services.ServiceUtilisateur;

import java.io.IOException;

public class LoginController {

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;  // Champ de mot de passe caché

    @FXML
    private TextField visiblePasswordField;  // Champ de mot de passe visible

    @FXML
    private ToggleButton togglePasswordButton;  // Bouton pour afficher/masquer le mot de passe

    @FXML
    private Button loginButton;  // Bouton de connexion

    private final ServiceUtilisateur serviceUtilisateur = new ServiceUtilisateur();

    @FXML
    public void initialize() {
        // Initialiser le champ de mot de passe visible comme non visible
        visiblePasswordField.setVisible(false);
        visiblePasswordField.setManaged(false);

        // Ajouter un écouteur pour le ToggleButton
        togglePasswordButton.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                // Afficher le mot de passe en texte clair
                visiblePasswordField.setText(passwordField.getText());
                visiblePasswordField.setVisible(true);
                visiblePasswordField.setManaged(true);
                passwordField.setVisible(false);
                passwordField.setManaged(false);

                // Changer l'icône pour "hide"
                ((FontIcon) togglePasswordButton.getGraphic()).setIconLiteral("fas-eye");
            } else {
                // Masquer le mot de passe
                passwordField.setText(visiblePasswordField.getText());
                passwordField.setVisible(true);
                passwordField.setManaged(true);
                visiblePasswordField.setVisible(false);
                visiblePasswordField.setManaged(false);

                // Changer l'icône pour "show"
                ((FontIcon) togglePasswordButton.getGraphic()).setIconLiteral("fas-eye-slash");
            }
        });
    }

    @FXML
    private void handleLogin(ActionEvent event) {
        String email = emailField.getText();
        String password = togglePasswordButton.isSelected() ? visiblePasswordField.getText() : passwordField.getText();

        // Vérifier si les champs sont vides
        if (email.isEmpty() || password.isEmpty()) {
            showAlert("Erreur", "Veuillez remplir tous les champs.");
            return;
        }

        // Vérifier l'utilisateur dans la base de données
        utilisateur utilisateur = serviceUtilisateur.getByEmailAndPassword(email, password);

        if (utilisateur == null) {
            showAlert("Échec de connexion", "Email ou mot de passe incorrect.");
        } else {
            // Vérifier le rôle et charger l'interface correspondante avec un titre
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

    // Méthode pour afficher une alerte
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void ouvrirInterfaceCitoyens() {
        openInterface("/GestionCitoyen.fxml", "Gestion des Citoyens");
    }

    @FXML
    private void ouvrirInterfaceTechniciens() {
        openInterface("/GestionTechnicien.fxml", "Gestion des Techniciens");
    }

    @FXML
    private void ouvrirInterfaceResponsables() {
        openInterface("/GestionResponsable.fxml", "Gestion des Responsables");
    }

    // Méthode générique pour ouvrir une nouvelle fenêtre
    private void openInterface(String fxmlFile, String titre) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();

            Stage stage = new Stage();
            Scene scene = new Scene(root);

            stage.setScene(scene);
            stage.setTitle(titre);

            // Maximiser automatiquement
            stage.setMaximized(true);

            stage.show();

            // Fermer la fenêtre de connexion après ouverture de la nouvelle
            Stage currentStage = (Stage) emailField.getScene().getWindow();
            currentStage.close();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de charger l'interface " + fxmlFile);
        }
    }
    @FXML
    private void handleForgotPassword(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Réinitialisation du mot de passe");
        alert.setHeaderText(null);
        alert.setContentText("Un lien de réinitialisation a été envoyé à votre email.");
        alert.showAndWait();
    }

}