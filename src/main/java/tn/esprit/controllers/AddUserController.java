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
import javafx.util.StringConverter;
import org.kordamp.ikonli.javafx.FontIcon;
import tn.esprit.Enumerations.Role;
import tn.esprit.models.Specialite;
import tn.esprit.models.utilisateur;
import tn.esprit.models.Zone;  // Added Zone import
import tn.esprit.services.ServiceZone;  // Added ServiceZone import
import tn.esprit.services.ServiceUtilisateur;
import tn.esprit.utils.MyDatabase;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

public class AddUserController implements Initializable {

    @FXML private TextField nameField, prenomField, emailField;
    @FXML private PasswordField passwordField;
    @FXML private TextField visiblePasswordField;
    @FXML private ComboBox<String> roleComboBox, specialiteComboBox, modulesComboBox;
    @FXML private ComboBox<Zone> zoneComboBox;  // Changed from TextField to ComboBox<Zone>
    @FXML private HBox zoneBox, specialiteBox, modulesBox;
    @FXML private ToggleButton togglePasswordButton;
    @FXML private ServiceUtilisateur serviceUtilisateur = new ServiceUtilisateur();
    @FXML private ServiceZone serviceZone = new ServiceZone();  // Added ServiceZone
    @FXML private Button addUserButton;
    @FXML private VBox formVBox;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Initialize role ComboBox
        roleComboBox.setItems(FXCollections.observableArrayList("Citoyen", "Responsable", "Technicien"));

        // Initialize zone ComboBox
        zoneComboBox.setItems(FXCollections.observableArrayList(serviceZone.getAll()));
        zoneComboBox.setConverter(new StringConverter<Zone>() {
            @Override
            public String toString(Zone zone) { return (zone != null) ? zone.getNom() : ""; }
            @Override
            public Zone fromString(String string) { return null; }
        });

        specialiteComboBox.setItems(FXCollections.observableArrayList("Maintenance", "Électricité", "Autre"));
        modulesComboBox.getItems().addAll("Admin", "Infrastructure", "Profil Énergétique", "Intervention", "Donnée");

        roleComboBox.setOnAction(e -> handleRoleChange());

        togglePasswordButton.selectedProperty().addListener((obs, wasSelected, isNowSelected) -> {
            if (isNowSelected) {
                visiblePasswordField.setText(passwordField.getText());
                visiblePasswordField.setVisible(true);
                passwordField.setVisible(false);
                ((FontIcon) togglePasswordButton.getGraphic()).setIconLiteral("fas-eye");
            } else {
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
            URL fxmlLocation = getClass().getResource("/Login.fxml");
            if (fxmlLocation == null) {
                System.out.println("Erreur : fichier Login.fxml introuvable !");
                showAlert("Erreur", "Fichier Login.fxml introuvable !");
                return;
            }
            FXMLLoader loader = new FXMLLoader(fxmlLocation);
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) nameField.getScene().getWindow();
            stage.close();
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

    private void handleRoleChange() {
        String role = roleComboBox.getValue();
        if ("Citoyen".equals(role)) {
            zoneBox.setVisible(true);
            specialiteBox.setVisible(false);
            modulesBox.setVisible(false);
        } else if ("Technicien".equals(role)) {
            zoneBox.setVisible(false);
            specialiteBox.setVisible(true);
            modulesBox.setVisible(false);
        } else if ("Responsable".equals(role)) {
            zoneBox.setVisible(false);
            specialiteBox.setVisible(false);
            modulesBox.setVisible(true);
        }
    }

    private void updateFieldsVisibility(String role) {
        zoneBox.setVisible("citoyen".equals(role));
        zoneBox.setManaged("citoyen".equals(role));
        specialiteComboBox.setVisible("technicien".equals(role));
        specialiteComboBox.setManaged("technicien".equals(role));
        modulesComboBox.setVisible("responsable".equals(role));
        modulesComboBox.setManaged("responsable".equals(role));
    }

    public boolean emailExists(String email) {
        String query = "SELECT COUNT(*) FROM utilisateur WHERE email = ?";
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            Connection connection = MyDatabase.getInstance().getCnx();
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

    private void showAlert(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    public void handleAddUser() throws IOException {
        try {
            String name = nameField.getText();
            String prenom = prenomField.getText();
            String email = emailField.getText();
            String roleString = roleComboBox.getValue();

            if (name.isEmpty() || prenom.isEmpty() || email.isEmpty() || roleString == null) {
                showAlert("Erreur", "Tous les champs doivent être remplis.");
                return;
            }

            String password = togglePasswordButton.isSelected() ? visiblePasswordField.getText().trim() : passwordField.getText().trim();
            if (password.isEmpty()) {
                showAlert("Erreur", "Le mot de passe ne peut pas être vide.");
                return;
            }

            if (email.isEmpty() || !email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
                showAlert("Erreur", "L'email ne peut pas être vide et doit être valide.");
                return;
            }
            if (emailExists(email)) {
                showAlert("Erreur de saisie", "L'email est déjà utilisé.");
                return;
            }

            if (password.length() < 8 || !password.matches(".*\\d.*") || !password.matches(".*[A-Z].*")) {
                showAlert("Erreur", "Le mot de passe doit comporter au moins 8 caractères, un chiffre et une lettre majuscule.");
                return;
            }

            Role role = Role.valueOf(roleString.toLowerCase());
            utilisateur user = new utilisateur(name, prenom, email, password, role, new Date());

            switch (role) {
                case citoyen:
                    Zone selectedZone = zoneComboBox.getValue();
                    if (selectedZone == null) {
                        showAlert("Erreur", "Veuillez sélectionner une zone pour un citoyen.");
                        return;
                    }
                    int zoneId = selectedZone.getIdZone();
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

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/PhoneNumberInput.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Entrer le numéro de téléphone");
            stage.setScene(new Scene(root));
            stage.show();
            stage.setMaximized(true);

            PhoneNumberInputController phoneNumberController = loader.getController();
            phoneNumberController.handleSendCode(new ActionEvent());
            clearFields();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Une erreur est survenue lors de l'ajout.");
        }
    }

    @FXML
    public void handleClear() {
        clearFields();
    }

    private void clearFields() {
        nameField.clear();
        prenomField.clear();
        emailField.clear();
        passwordField.clear();
        visiblePasswordField.clear();
        roleComboBox.getSelectionModel().clearSelection();
        zoneComboBox.getSelectionModel().clearSelection();
        specialiteComboBox.getSelectionModel().clearSelection();
        modulesComboBox.getSelectionModel().clearSelection();
        updateFieldsVisibility("");
    }
}