package tn.esprit.controllers;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.kordamp.ikonli.javafx.FontIcon;
import tn.esprit.models.Role;
import tn.esprit.models.Specialite;
import tn.esprit.models.utilisateur;
import tn.esprit.utils.MyDatabase;
import tn.esprit.services.ServiceUtilisateur;

import javax.swing.text.JTextComponent;
import java.io.IOException;
import java.net.URL;
import java.security.cert.PolicyNode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

public class AddUserController implements Initializable {

    @FXML
    private TextField  nameField, prenomField, emailField, passwordField, zoneIdField, modulesField;
    @FXML
    private TextField visiblePasswordField;
    @FXML
    private ComboBox<String> modulesComboBox;
    @FXML
    private ComboBox<String> roleComboBox, specialiteComboBox;

    @FXML
    private HBox zoneIdBox, specialiteBox, modulesBox;
    @FXML
    private ToggleButton togglePasswordButton;
    @FXML
    private ServiceUtilisateur serviceUtilisateur = new ServiceUtilisateur();

    @FXML
    private Button addUserButton;

    @FXML
    private VBox formVBox;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Remplir les ComboBox avec des valeurs prédéfinies pour les rôles et spécialités
        roleComboBox.setItems(FXCollections.observableArrayList("Citoyen", "Responsable", "Technicien"));
        specialiteComboBox.setItems(FXCollections.observableArrayList("Maintenance", "Électricité", "Autre"));
        modulesComboBox.getItems().addAll("Admin", "Infrastructure", "Profil Énergétique" , "Intervention" ,"Donnée");

        // Cacher les champs spécifiques selon le rôle sélectionné
        roleComboBox.setOnAction(e -> handleRoleChange());
        // Ajouter un écouteur pour basculer la visibilité du mot de passe
        togglePasswordButton.selectedProperty().addListener((obs, wasSelected, isNowSelected) -> {
            if (isNowSelected) {
                // Afficher le mot de passe en clair
                visiblePasswordField.setText(passwordField.getText());
                visiblePasswordField.setVisible(true);
                passwordField.setVisible(false);
                ((FontIcon) togglePasswordButton.getGraphic()).setIconLiteral("fas-eye");
            } else {
                // Masquer le mot de passe
                passwordField.setText(visiblePasswordField.getText());
                passwordField.setVisible(true);
                visiblePasswordField.setVisible(false);
                ((FontIcon) togglePasswordButton.getGraphic()).setIconLiteral("fas-eye-slash");

            }
        });

    }
    @FXML
    private void handleReturnToLogin() {
        try {
            // Vérifier si le fichier FXML est bien trouvé
            URL fxmlLocation = getClass().getResource("/Login.fxml");
            if (fxmlLocation == null) {
                System.out.println("Erreur : fichier Login.fxml introuvable !");
                showAlert("Erreur", "Fichier Login.fxml introuvable !");
                return;
            }

            // Charger le fichier FXML de la page de connexion
            FXMLLoader loader = new FXMLLoader(fxmlLocation);
            Parent root = loader.load();

            // Créer une nouvelle scène
            Scene scene = new Scene(root);

            // Obtenir la fenêtre actuelle et la fermer
            Stage stage = (Stage) nameField.getScene().getWindow();
            stage.close();

            // Ouvrir la fenêtre de connexion
            Stage loginStage = new Stage();
            loginStage.setScene(scene);
            loginStage.setTitle("Connexion");
            loginStage.setMaximized(true);
            loginStage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de charger la page de connexion.");
        }
    }



    // Gérer l'affichage des champs selon le rôle sélectionné
    private void handleRoleChange() {
        String role = roleComboBox.getValue();
        if ("Citoyen".equals(role)) {
            zoneIdBox.setVisible(true);
            specialiteBox.setVisible(false);
            modulesBox.setVisible(false);
        } else if ("Technicien".equals(role)) {
            zoneIdBox.setVisible(false);
            specialiteBox.setVisible(true);
            modulesBox.setVisible(false);
        } else if ("Responsable".equals(role)) {
            zoneIdBox.setVisible(false);
            specialiteBox.setVisible(false);
            modulesBox.setVisible(true);
        }
    }

    /**
     * Gère l'affichage des champs en fonction du rôle sélectionné
     */
    private void updateFieldsVisibility(String role) {
        zoneIdField.setVisible(role.equals("citoyen"));
        zoneIdField.setManaged(role.equals("citoyen"));

        specialiteComboBox.setVisible(role.equals("technicien"));
        specialiteComboBox.setManaged(role.equals("technicien"));

        modulesComboBox.setVisible(role.equals("responsable"));
        modulesComboBox.setManaged(role.equals("responsable"));
    }

    /**
     * Vérifie si l'email existe déjà dans la base de données
     */
    public boolean emailExists(String email) {
        String query = "SELECT COUNT(*) FROM utilisateur WHERE email = ?";
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            Connection connection = MyDatabase.getInstance().getCnx(); // Connexion partagée
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, email);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la vérification de l'email : " + e.getMessage());
        } finally {
            try {
                if (resultSet != null) resultSet.close();
                if (preparedStatement != null) preparedStatement.close();
            } catch (SQLException e) {
                System.out.println("Erreur lors de la fermeture des ressources : " + e.getMessage());
            }
        }
        return false;
    }

    /**
     * Affiche une boîte de dialogue d'alerte
     */
    private void showAlert(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Gère l'ajout d'un utilisateur
     */
    @FXML
    public void handleAddUser() throws IOException{
        try {
            String name = nameField.getText();
            String prenom = prenomField.getText();
            String email = emailField.getText();
            String roleString = roleComboBox.getValue();

            // Validation des champs
            if (name.isEmpty() || prenom.isEmpty() || email.isEmpty()  || roleString == null) {
                showAlert("Erreur", "Tous les champs doivent être remplis.");
                return;
            }
            // Récupérer et valider le mot de passe
            String password = togglePasswordButton.isSelected() ? visiblePasswordField.getText().trim() : passwordField.getText().trim();
            if (password.isEmpty()) {
                showAlert("Erreur", "Le mot de passe ne peut pas être vide.");
                return;
            }

            // Validation de l'email
            if (email.isEmpty() || !email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
                showAlert("Erreur", "L'email ne peut pas être vide et doit être valide.");
                return;
            }
            if (emailExists(email)) {
                showAlert("Erreur de saisie", "L'email est déjà utilisé.");
                return;
            }

            // Validation du mot de passe
            if (password.length() < 8 || !password.matches(".*\\d.*") || !password.matches(".*[A-Z].*")) {
                showAlert("Erreur", "Le mot de passe doit comporter au moins 8 caractères, un chiffre et une lettre majuscule.");
                return;
            }

            // Afficher la valeur du rôle sélectionné
            System.out.println("Role selected: " + roleString);

            // Conversion du rôle avec le bon format
            Role role = Role.valueOf(roleString.toLowerCase());  // Utilisation de toLowerCase()

            utilisateur user = new utilisateur(name, prenom, email, password, role, new Date());

            // Traitement spécifique selon le rôle
            switch (role) {
                case citoyen:
                    if (zoneIdField.getText().isEmpty()) {
                        showAlert("Erreur", "Le champ Zone ID est obligatoire pour un citoyen.");
                        return;
                    }
                    int zoneId = Integer.parseInt(zoneIdField.getText());
                    serviceUtilisateur.add(user, null, new ArrayList<>(), zoneId);
                    break;
                case technicien:
                    String specialiteString = specialiteComboBox.getValue();
                    if (specialiteString == null) {
                        showAlert("Erreur", "Veuillez sélectionner une spécialité.");
                        return;
                    }
                    Specialite specialite = Specialite.valueOf(specialiteString.toLowerCase());
                    serviceUtilisateur.add(user, specialite, new ArrayList<>(), 0);
                    break;
                case responsable:
                    String selectedModule = modulesComboBox.getValue();
                    if (selectedModule == null) {
                        showAlert("Erreur", "Veuillez sélectionner un module.");
                        return;
                    }
                    List<String> modules = List.of(selectedModule);

                    serviceUtilisateur.add(user, null, modules, 0);
                    break;
            }


            // Ouvrir la fenêtre PhoneNumberInput.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/PhoneNumberInput.fxml")); // Mettre le bon chemin de fichier FXML
            Parent root = loader.load();

            // Créer une scène et l'afficher
            Stage stage = new Stage();
            stage.setTitle("Entrer le numéro de téléphone");
            stage.setScene(new Scene(root));
            stage.show();
            stage.setMaximized(true);


            // Appeler la méthode handleSendCode sur le contrôleur du PhoneNumberInput.fxml
            PhoneNumberInputController phoneNumberController = loader.getController();
            phoneNumberController.handleSendCode(new ActionEvent()); // V
            // Réinitialiser les champs
            clearFields();
        } catch (Exception e) {
            e.printStackTrace(); // Ajouter un trace de l'erreur
            showAlert("Erreur", "Une erreur est survenue lors de l'ajout.");
        }
    }

    /**
     * Fonction pour ajouter l'utilisateur (Simulé ici, remplacer par la logique réelle)
     */
    private void ajouterUtilisateur(String name, String prenom, String email, String password, String role, int zoneId, String specialite, List<String> modules) {
        System.out.println("Utilisateur ajouté : " + name + " " + prenom + " (" + role + ")");
        if (role.equals("citoyen")) {
            System.out.println("Zone ID: " + zoneId);
        }
        if (role.equals("technicien")) {
            System.out.println("Spécialité: " + specialite);
        }
        if (role.equals("responsable")) {
            System.out.println("Modules: " + modules);
        }
    }

    /**
     * Réinitialise les champs après l'ajout
     */
    // Réinitialiser les champs
    @FXML
    public void handleClear() {
        clearFields();
    }
    private void clearFields() {
        nameField.clear();
        prenomField.clear();
        emailField.clear();
        passwordField.clear();
        roleComboBox.getSelectionModel().clearSelection();
        zoneIdField.clear();
        specialiteComboBox.getSelectionModel().clearSelection();
        modulesComboBox.getSelectionModel().clearSelection();
        updateFieldsVisibility(""); // Cacher tous les champs conditionnels
    }
}
