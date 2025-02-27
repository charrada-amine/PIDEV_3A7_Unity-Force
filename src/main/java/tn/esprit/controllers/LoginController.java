package tn.esprit.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.kordamp.ikonli.javafx.FontIcon;
import tn.esprit.models.utilisateur;
import tn.esprit.services.ServiceUtilisateur;
import tn.esprit.utils.PasswordEncryptor;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;

public class LoginController {

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
    private TextField codeField;  // Champ pour le code de vérification

    @FXML
    private Button validateCodeButton;  // Bouton pour valider le code

    @FXML
    private PasswordField newPasswordField;  // Champ pour le nouveau mot de passe

    @FXML
    private PasswordField confirmPasswordField;  // Champ pour confirmer le nouveau mot de passe

    @FXML
    private Button changePasswordButton;  // Bouton pour changer le mot de passe

    private final ServiceUtilisateur serviceUtilisateur = new ServiceUtilisateur();

    private String userEmail;  // Email de l'utilisateur pour réinitialisation
    private int generatedCode;  // Code généré pour la réinitialisation

    @FXML
    public void initialize() {
        // Initialiser les champs et boutons à invisibles
        visiblePasswordField.setVisible(false);
        visiblePasswordField.setManaged(false);
        codeField.setVisible(false);
        codeField.setManaged(false);
        validateCodeButton.setVisible(false);
        validateCodeButton.setManaged(false);
        newPasswordField.setVisible(false);
        newPasswordField.setManaged(false);
        confirmPasswordField.setVisible(false);
        confirmPasswordField.setManaged(false);
        changePasswordButton.setVisible(false);
        changePasswordButton.setManaged(false);

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

    private void openInterface(String s, String gestionDesCitoyens) {
    }

    // Générer un code aléatoire pour la réinitialisation du mot de passe
    private int generateVerificationCode() {
        return 1000 + (int) (Math.random() * 9000);
    }

    @FXML
    private void handleForgotPassword(ActionEvent event) {
        String email = emailField.getText().trim();

        // Vérifier si l'email est valide
        if (email.isEmpty() || !isValidEmail(email)) {
            showAlert("Erreur", "Veuillez saisir une adresse email valide.");
            return;
        }

        userEmail = email;  // Sauvegarder l'email
        generatedCode = generateVerificationCode();  // Générer un code aléatoire

        // Envoyer l'email avec le code de vérification
        try {
            sendEmail(email, "Code de réinitialisation", "Votre code de réinitialisation est : " + generatedCode);
            showSuccessAlert("Code envoyé", "Un code de vérification a été envoyé à votre adresse email.");

            // Afficher le champ pour entrer le code et le bouton de validation
            codeField.setVisible(true);
            validateCodeButton.setVisible(true);
        } catch (MessagingException e) {
            e.printStackTrace();
            showAlert("Erreur", "Une erreur s'est produite lors de l'envoi de l'email.");
        }
    }


    // Méthode pour vérifier le code de réinitialisation
    @FXML
    private void handleValidateCode(ActionEvent event) {
        String enteredCode = codeField.getText().trim();

        if (enteredCode.isEmpty()) {
            showAlert("Erreur", "Veuillez entrer le code de vérification.");
            return;
        }

        if (Integer.parseInt(enteredCode) == generatedCode) {
            showSuccessAlert("Succès", "Code vérifié avec succès ! Entrez un nouveau mot de passe.");

            // Afficher les champs pour entrer le nouveau mot de passe
            newPasswordField.setVisible(true);
            confirmPasswordField.setVisible(true);
            changePasswordButton.setVisible(true);
        } else {
            showAlert("Erreur", "Code incorrect. Vérifiez votre email et réessayez.");
        }
    }

    // Méthode pour changer le mot de passe
    @FXML
    private void handleChangePassword(ActionEvent event) {
        String newPassword = newPasswordField.getText().trim();
        String confirmPassword = confirmPasswordField.getText().trim();

        // Vérifier que les champs ne sont pas vides et correspondent
        if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
            showAlert("Erreur", "Veuillez remplir tous les champs.");
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            showAlert("Erreur", "Les mots de passe ne correspondent pas.");
            return;
        }

        // Hasher le nouveau mot de passe (MD5)
        String encryptedPassword = PasswordEncryptor.encryptPassword(newPassword);

        // Mettre à jour le mot de passe dans la base de données
        try {
            String query = "UPDATE utilisateur SET motdepasse = ? WHERE email = ?";
            Connection cnx = null;
            PreparedStatement pstm = cnx.prepareStatement(query);
            pstm.setString(1, encryptedPassword);
            pstm.setString(2, userEmail);

            int rowsUpdated = pstm.executeUpdate();
            if (rowsUpdated > 0) {
                showSuccessAlert("Succès", "Votre mot de passe a été modifié avec succès !");
            } else {
                showAlert("Erreur", "Erreur lors de la mise à jour du mot de passe.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Une erreur SQL s'est produite.");
        }
    }

    // Méthode pour valider l'email
    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        return email.matches(emailRegex);
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

    // Méthode pour envoyer un email
    private void sendEmail(String toEmail, String subject, String body) throws MessagingException {
        // Configuration des propriétés SMTP
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com"); // Serveur SMTP de Gmail
        props.put("mail.smtp.port", "587"); // Port pour TLS
        props.put("mail.smtp.auth", "true"); // Authentification requise
        props.put("mail.smtp.starttls.enable", "true"); // Activation de TLS

        // Authentification
        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("mellouli.youssef11@gmail.com\n", "cnvv wklj lydi psnl");
            }
        });

        // Création du message
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress("mellouli.youssef11@gmail.com\n")); // Expéditeur
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail)); // Destinataire
        message.setSubject(subject); // Sujet
        message.setText(body); // Corps du message

        // Envoi du message
        Transport.send(message);
    }
}
