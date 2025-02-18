package tn.esprit.controllers;
import java.time.format.DateTimeFormatter;
import java.text.SimpleDateFormat;

import javafx.geometry.Pos;
import tn.esprit.utils.MyDatabase;
import tn.esprit.models.utilisateur;

import tn.esprit.models.Role;
import tn.esprit.models.Specialite;
import java.util.Scanner;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;

import java.util.List;
import java.text.SimpleDateFormat;

import java.util.Date;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import tn.esprit.models.*;
import tn.esprit.services.ServiceCitoyen;
import tn.esprit.services.ServiceResponsable;
import tn.esprit.services.ServiceTechnicien;
import tn.esprit.services.ServiceUtilisateur;

import java.io.IOException;
import java.util.*;

public class GestionUtilisateurController {

    @FXML private FlowPane userFlowPane;
    @FXML private ScrollPane scrollPane;

    private final ServiceUtilisateur serviceUtilisateur = new ServiceUtilisateur();
    private final ServiceCitoyen serviceCitoyen = new ServiceCitoyen();
    private final ServiceResponsable serviceResponsable = new ServiceResponsable();
    private final ServiceTechnicien serviceTechnicien = new ServiceTechnicien();

    private String roleSelectionne = "Tous"; // Valeur par défaut
    private utilisateur selectedUser;  // Déclarez la variable selectedUser


    @FXML
    public void initialize() {
        loadUsers(); // Charge les utilisateurs au démarrage
    }


    public void setRoleSelectionne(String role) {
        this.roleSelectionne = role;
        loadUsers(); // Recharge les utilisateurs filtrés
    }

    private void loadUsers() {
        List<utilisateur> users = serviceUtilisateur.getAllUtilisateurs();

        // Filtrer les utilisateurs par rôle sélectionné
        if (!"Tous".equals(roleSelectionne)) {
            users.removeIf(user -> !user.getRole().toString().equalsIgnoreCase(roleSelectionne));
        }

        // Effacer les anciennes cartes
        userFlowPane.getChildren().clear();

        // Afficher les utilisateurs sous forme de cartes
        for (utilisateur user : users) {
            VBox card = createUserCard(user);
            userFlowPane.getChildren().add(card);
        }
    }
    private void handleUserClick(utilisateur user) {
        selectedUser = user;  // Enregistrer l'utilisateur sélectionné
        showAlert("Utilisateur sélectionné", "L'utilisateur " + user.getNom() + " a été sélectionné.");
    }


    private VBox createUserCard(utilisateur user) {
        VBox card = new VBox(10);
        card.setStyle("-fx-border-color: #ddd; -fx-border-width: 1; -fx-padding: 10; -fx-background-color: #f9f9f9; -fx-alignment: center;");

        // Champ ID (non modifiable)
        Label idLabel = new Label("ID:");
        idLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: black;");
        TextField idField = new TextField(String.valueOf(user.getId_utilisateur()));
        idField.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cccccc; -fx-padding: 5;");
        idField.setEditable(false); // Non modifiable
        HBox idBox = new HBox(10, idLabel, idField); // Aligner le label et le champ ID
        idBox.setAlignment(Pos.CENTER_LEFT); // Aligner à gauche

        // Champ Nom
        Label nameLabel = new Label("Nom:");
        nameLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: black;");
        TextField nameField = new TextField(user.getNom());
        nameField.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cccccc; -fx-padding: 5;");
        nameField.setEditable(false); // Non modifiable
        HBox nameBox = new HBox(10, nameLabel, nameField); // Aligner le label et le champ Nom
        nameBox.setAlignment(Pos.CENTER_LEFT);

        // Champ Prénom
        Label prenomLabel = new Label("Prénom:");
        prenomLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: black;");
        TextField prenomField = new TextField(user.getPrenom());
        prenomField.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cccccc; -fx-padding: 5;");
        prenomField.setEditable(false); // Non modifiable
        HBox prenomBox = new HBox(10, prenomLabel, prenomField); // Aligner le label et le champ Prénom
        prenomBox.setAlignment(Pos.CENTER_LEFT);

        // Champ Email
        Label emailLabel = new Label("Email:");
        emailLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: black;");
        TextField emailField = new TextField(user.getEmail());
        emailField.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cccccc; -fx-padding: 5;");
        emailField.setEditable(false); // Non modifiable
        HBox emailBox = new HBox(10, emailLabel, emailField); // Aligner le label et le champ Email
        emailBox.setAlignment(Pos.CENTER_LEFT);

        // Champ Mot de passe
        Label passwordLabel = new Label("Mot de passe:");
        passwordLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: black;");
        TextField mdpfield = new TextField(user.getMotdepasse());
        mdpfield.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cccccc; -fx-padding: 5;");
        mdpfield.setEditable(false); // Non modifiable
        HBox mdpBox = new HBox(10, passwordLabel, mdpfield); // Aligner le label et le champ Mot de passe
        mdpBox.setAlignment(Pos.CENTER_LEFT);

        // Champ Rôle
        Label roleLabel = new Label("Rôle:");
        roleLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: black;");
        TextField roleField = new TextField(user.getRole().name());
        roleField.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cccccc; -fx-padding: 5;");
        roleField.setEditable(false); // Non modifiable
        HBox roleBox = new HBox(10, roleLabel, roleField); // Aligner le label et le champ Rôle
        roleBox.setAlignment(Pos.CENTER_LEFT);

        // Champ Date d'inscription
        Label dateInscriptionLabel = new Label("Date d'inscription");
        dateInscriptionLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: black;");
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String dateInscriptionString = sdf.format(user.getDateinscription());  // Convertir la Date en String
        TextField dateinscription = new TextField(dateInscriptionString);
        dateinscription.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cccccc; -fx-padding: 5;");
        dateinscription.setEditable(false); // Non modifiable
        HBox dateInscriptionBox = new HBox(10, dateInscriptionLabel, dateinscription); // Aligner le label et le champ Date d'inscription
        dateInscriptionBox.setAlignment(Pos.CENTER_LEFT);

        // Ajouter les HBoxes à la carte
        card.getChildren().addAll(idBox, nameBox, prenomBox, emailBox, mdpBox, roleBox, dateInscriptionBox);

        return card;
    }


    private String getRoleDetails(utilisateur user) {
        switch (user.getRole()) {
            case citoyen:
                citoyen c = serviceCitoyen.getCitoyenById(user.getId_utilisateur());
                return (c != null) ? "Zone: " + c.getZoneId() : "Citoyen non trouvé";
            case technicien:
                technicien t = serviceTechnicien.getAllTechniciens().stream()
                        .filter(tech -> tech.getId_utilisateur() == user.getId_utilisateur())
                        .findFirst().orElse(null);
                return (t != null) ? "Spécialité: " + t.getSpecialite() : "Technicien non trouvé";
            case responsable:
                responsable r = serviceResponsable.getAllResponsables().stream()
                        .filter(resp -> resp.getId_utilisateur() == user.getId_utilisateur())
                        .findFirst().orElse(null);
                return (r != null) ? "Modules: " + String.join(", ", r.getModules()) : "Responsable non trouvé";
            default:
                return "Rôle inconnu";
        }
    }
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


    @FXML
    private void handleAddUser() {
        try {
            Dialog<utilisateur> dialog = new Dialog<>();
            dialog.setTitle("Nouvel Utilisateur");

            // Configuration du formulaire
            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);

            TextField nomField = new TextField();
            TextField prenomField = new TextField();
            TextField emailField = new TextField();
            TextField passwordField = new TextField();
            ComboBox<Role> roleCombo = new ComboBox<>();
            roleCombo.getItems().addAll(Role.values());

            TextField zoneField = new TextField();
            ComboBox<Specialite> specialiteCombo = new ComboBox<>();
            specialiteCombo.getItems().addAll(Specialite.values());
            TextField modulesField = new TextField();

            grid.addRow(0, new Label("Nom:"), nomField);
            grid.addRow(1, new Label("Prénom:"), prenomField);
            grid.addRow(2, new Label("Email:"), emailField);
            grid.addRow(3, new Label("Mot de passe:"), passwordField);
            grid.addRow(4, new Label("Rôle:"), roleCombo);

            roleCombo.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
                grid.getChildren().removeIf(node -> GridPane.getRowIndex(node) != null && GridPane.getRowIndex(node) >= 5);
                if (newVal == Role.citoyen) {
                    grid.addRow(5, new Label("Zone ID:"), zoneField);
                } else if (newVal == Role.technicien) {
                    grid.addRow(5, new Label("Spécialité:"), specialiteCombo);
                } else if (newVal == Role.responsable) {
                    grid.addRow(5, new Label("Modules (csv):"), modulesField);
                }
            });

            dialog.getDialogPane().setContent(grid);
            dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

            dialog.setResultConverter(buttonType -> {
                if (buttonType == ButtonType.OK) {
                    // Validation des champs
                    if (nomField.getText().isEmpty()) {
                        showAlert("Erreur de saisie", "Le nom ne peut pas être vide.");
                        return null;
                    }
                    if (prenomField.getText().isEmpty()) {
                        showAlert("Erreur de saisie", "Le prénom ne peut pas être vide.");
                        return null;
                    }
                    if (emailField.getText().isEmpty() || !emailField.getText().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
                        showAlert("Erreur de saisie", "Veuillez entrer un email valide.");
                        return null;
                    }
                    if (emailExists(emailField.getText())) {
                        showAlert("Erreur de saisie", "L'email est déjà utilisé.");
                        return null;
                    }
                    if (passwordField.getText().isEmpty() || passwordField.getText().length() < 8 ||
                            !passwordField.getText().matches(".*[A-Z].*") || !passwordField.getText().matches(".*\\d.*")) {
                        showAlert("Erreur de saisie", "Le mot de passe doit comporter au moins 8 caractères, avec une majuscule et un chiffre.");
                        return null;
                    }
                    if (roleCombo.getValue() == null) {
                        showAlert("Erreur de saisie", "Veuillez sélectionner un rôle.");
                        return null;
                    }

                    utilisateur user = new utilisateur(
                            nomField.getText(),
                            prenomField.getText(),
                            emailField.getText(),
                            passwordField.getText(),
                            roleCombo.getValue(),
                            new Date()
                    );

                    try {
                        switch (user.getRole()) {
                            case citoyen:
                                if (zoneField.getText().isEmpty() || !zoneField.getText().matches("\\d+")) {
                                    showAlert("Erreur de saisie", "La zone ID doit être un nombre entier.");
                                    return null;
                                }
                                int zoneId = Integer.parseInt(zoneField.getText());
                                serviceUtilisateur.add(user, null, new ArrayList<>(), zoneId);
                                break;
                            case technicien:
                                if (specialiteCombo.getValue() == null) {
                                    showAlert("Erreur de saisie", "Veuillez sélectionner une spécialité.");
                                    return null;
                                }
                                serviceUtilisateur.add(user, specialiteCombo.getValue(), new ArrayList<>(), 0);
                                break;
                            case responsable:
                                if (modulesField.getText().isEmpty()) {
                                    showAlert("Erreur de saisie", "Veuillez entrer des modules.");
                                    return null;
                                }
                                List<String> modules = Arrays.asList(modulesField.getText().split("\\s*,\\s*"));
                                serviceUtilisateur.add(user, null, modules, 0);
                                break;
                        }
                    } catch (Exception e) {
                        showAlert("Erreur", "Erreur lors de l'enregistrement : " + e.getMessage());
                        return null;
                    }

                    return user;
                }
                return null;
            });

            dialog.showAndWait().ifPresent(user -> {
                System.out.println("Utilisateur ajouté : " + user);
                loadUsers(); // Recharge la liste des utilisateurs après l'ajout
            });
        } catch (Exception e) {
            System.err.println("Erreur : " + e.getMessage());
            e.printStackTrace();
        }
    }


    @FXML
    private void handleUpdateUser() {
        // Créer un GridPane pour le formulaire de modification
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        // Ajoute un champ pour entrer l'ID de l'utilisateur
        TextField userIdField = new TextField();
        grid.addRow(0, new Label("Entrez l'ID de l'utilisateur:"), userIdField);

        // Champ pour sélectionner quel champ modifier
        ComboBox<String> fieldSelectionCombo = new ComboBox<>();
        fieldSelectionCombo.getItems().add(""); // Ajouter une option vide par défaut
        fieldSelectionCombo.getItems().addAll("Nom", "Prénom", "Email", "Mot de passe");
        fieldSelectionCombo.getSelectionModel().selectFirst();  // Sélectionne le premier élément vide par défaut

        // Champs à modifier (Nom, Prénom, Email, Mot de passe)
        TextField nomField = new TextField();
        TextField prenomField = new TextField();
        TextField emailField = new TextField();
        TextField mdpField = new TextField();

        // Ajoute les champs de texte pour les modifications
        grid.addRow(1, new Label("Sélectionnez le champ à modifier:"), fieldSelectionCombo);
        grid.addRow(2, new Label("Nom:"), nomField);
        grid.addRow(3, new Label("Prénom:"), prenomField);
        grid.addRow(4, new Label("Email:"), emailField);
        grid.addRow(5, new Label("Mot de passe:"), mdpField);

        // Masquer tous les champs de modification par défaut
        nomField.setVisible(false);
        prenomField.setVisible(false);
        emailField.setVisible(false);
        mdpField.setVisible(false);

        // Créer un bouton OK pour la confirmation
        ButtonType okButton = ButtonType.OK;
        ButtonType cancelButton = ButtonType.CANCEL;

        // Crée le dialog
        Dialog<utilisateur> dialog = new Dialog<>();
        dialog.setTitle("Modifier Utilisateur");
        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(okButton, cancelButton);

        // Listener sur le ComboBox pour afficher uniquement le champ à modifier
        fieldSelectionCombo.valueProperty().addListener((observable, oldValue, newValue) -> {
            // Masquer tous les champs
            nomField.setVisible(false);
            prenomField.setVisible(false);
            emailField.setVisible(false);
            mdpField.setVisible(false);

            // Afficher uniquement le champ sélectionné
            switch (newValue) {
                case "Nom":
                    nomField.setVisible(true);
                    break;
                case "Prénom":
                    prenomField.setVisible(true);
                    break;
                case "Email":
                    emailField.setVisible(true);
                    break;
                case "Mot de passe":
                    mdpField.setVisible(true);
                    break;
            }
        });

        // Gérer le résultat du dialog
        dialog.setResultConverter(button -> {
            if (button == okButton) {
                // Récupérer l'ID utilisateur et le valider
                String userIdText = userIdField.getText();
                if (userIdText.isEmpty()) {
                    showAlert("Erreur", "L'ID de l'utilisateur ne peut pas être vide.");
                    return null;
                }

                int userId;
                try {
                    userId = Integer.parseInt(userIdText);
                } catch (NumberFormatException e) {
                    showAlert("Erreur", "L'ID de l'utilisateur doit être un nombre entier.");
                    return null;
                }

                // Vérifier que le champ de modification est sélectionné
                if (fieldSelectionCombo.getValue().isEmpty()) {
                    showAlert("Erreur", "Veuillez sélectionner un champ à modifier.");
                    return null;
                }

                // Vérifier que le champ à modifier est rempli si visible
                if (fieldSelectionCombo.getValue().equals("Nom") && nomField.getText().isEmpty()) {
                    showAlert("Erreur", "Le nom ne peut pas être vide.");
                    return null;
                }
                if (fieldSelectionCombo.getValue().equals("Prénom") && prenomField.getText().isEmpty()) {
                    showAlert("Erreur", "Le prénom ne peut pas être vide.");
                    return null;
                }
                if (fieldSelectionCombo.getValue().equals("Email") && emailField.getText().isEmpty() || !emailField.getText().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
                    showAlert("Erreur", "L'email ne peut pas être vide.");
                    return null;
                }
                if (emailExists(emailField.getText())) {
                    showAlert("Erreur de saisie", "L'email est déjà utilisé.");
                    return null;
                }
                if (fieldSelectionCombo.getValue().equals("Mot de passe") && mdpField.getText().isEmpty()) {
                    showAlert("Erreur", "Le mot de passe ne peut pas être vide.");
                    return null;
                }
                if (fieldSelectionCombo.getValue().equals("Mot de passe") && mdpField.getText().length() < 6) {
                    showAlert("Erreur", "Le mot de passe doit avoir au moins 6 caractères.");
                    return null;
                }

                // Récupérer l'utilisateur correspondant à l'ID
                utilisateur selectedUser = serviceUtilisateur.getUtilisateurById(userId); // Méthode pour récupérer l'utilisateur par ID
                if (selectedUser == null) {
                    showAlert("Erreur", "Aucun utilisateur trouvé avec cet ID.");
                    return null;
                }

                // Mettre à jour le champ sélectionné
                switch (fieldSelectionCombo.getValue()) {
                    case "Nom":
                        serviceUtilisateur.updateField(selectedUser.getId_utilisateur(), "nom", nomField.getText());
                        break;
                    case "Prénom":
                        serviceUtilisateur.updateField(selectedUser.getId_utilisateur(), "prenom", prenomField.getText());
                        break;
                    case "Email":
                        serviceUtilisateur.updateField(selectedUser.getId_utilisateur(), "email", emailField.getText());
                        break;
                    case "Mot de passe":
                        serviceUtilisateur.updateField(selectedUser.getId_utilisateur(), "motdepasse", mdpField.getText());
                        break;
                    default:
                        showAlert("Erreur", "Aucun champ sélectionné pour la modification.");
                        return null;
                }

                loadUsers(); // Recharger la liste des utilisateurs après modification
            }
            return null;
        });

        dialog.showAndWait();
    }


    @FXML
    private void handleDeleteUser() {
        // Créer un GridPane pour le formulaire de suppression
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        // Ajoute un champ pour entrer l'ID de l'utilisateur
        TextField userIdField = new TextField();
        grid.addRow(0, new Label("Entrez l'ID de l'utilisateur à supprimer:"), userIdField);

        // Créer un bouton OK pour la confirmation
        ButtonType okButton = ButtonType.OK;
        ButtonType cancelButton = ButtonType.CANCEL;

        // Crée le dialog
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Supprimer Utilisateur");
        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(okButton, cancelButton);

        // Gérer le résultat du dialog
        dialog.setResultConverter(button -> {
            if (button == okButton) {
                // Récupérer l'ID utilisateur et le valider
                String userIdText = userIdField.getText();
                if (userIdText.isEmpty()) {
                    showAlert("Erreur", "L'ID de l'utilisateur ne peut pas être vide.");
                    return null;
                }

                int userId;
                try {
                    userId = Integer.parseInt(userIdText); // Récupérer l'ID de l'utilisateur
                } catch (NumberFormatException e) {
                    showAlert("Erreur", "L'ID de l'utilisateur doit être un nombre entier.");
                    return null;
                }

                // Vérifier si l'utilisateur existe
                utilisateur user = serviceUtilisateur.getUtilisateurById(userId);
                if (user == null) {
                    showAlert("Utilisateur non trouvé", "Aucun utilisateur trouvé avec cet ID.");
                    return null;
                }

                // Afficher un dialog de confirmation avant de supprimer l'utilisateur
                Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
                confirm.setTitle("Confirmation");
                confirm.setHeaderText("Supprimer l'utilisateur " + user.getNom());
                confirm.setContentText("Êtes-vous sûr de vouloir supprimer cet utilisateur ?");

                Optional<ButtonType> confirmationResult = confirm.showAndWait();
                if (confirmationResult.isPresent() && confirmationResult.get() == ButtonType.OK) {
                    // Appeler la méthode de suppression dans le service
                    serviceUtilisateur.deleteById(userId); // Suppression de l'utilisateur par son ID

                    // Afficher un message de succès
                    showAlert("Succès", "L'utilisateur a été supprimé.");
                    loadUsers(); // Recharger la liste des utilisateurs après suppression
                }
            }
            return null;
        });

        dialog.showAndWait();
    }


    @FXML
    private void handleRefresh() {
        loadUsers();
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}