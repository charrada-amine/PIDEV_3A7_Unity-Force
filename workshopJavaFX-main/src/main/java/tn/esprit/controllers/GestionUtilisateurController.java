package tn.esprit.controllers;
import java.time.format.DateTimeFormatter;
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

        // Champ ID
        Label idLabel = new Label("ID:");
        idLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: black;");
        TextField idField = new TextField(String.valueOf(user.getId_utilisateur()));  // ID en texte, converti depuis un entier
        idField.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cccccc; -fx-padding: 5;");
        idField.setEditable(false); // ID ne doit pas être modifiable

        // Champ Nom
        Label nameLabel = new Label("Nom:");
        nameLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: black;");
        TextField nameField = new TextField(user.getNom());
        nameField.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cccccc; -fx-padding: 5;");

        // Champ Prénom
        Label prenomLabel = new Label("Prénom:");
        prenomLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: black;");
        TextField prenomField = new TextField(user.getPrenom());
        prenomField.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cccccc; -fx-padding: 5;");

        // Champ Email
        Label emailLabel = new Label("Email:");
        emailLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: black;");
        TextField emailField = new TextField(user.getEmail());
        emailField.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cccccc; -fx-padding: 5;");

        // Champ Mot de passe (statique)
        Label passwordLabel = new Label("Mot de passe:");
        passwordLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: black;");
        TextField mdpfield = new TextField(user.getMotdepasse());
        mdpfield.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cccccc; -fx-padding: 5;");

        // Champ Rôle
        Label roleLabel = new Label("Rôle:");
        roleLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: black;");
        TextField roleField = new TextField(user.getRole().name());  // Le rôle actuel de l'utilisateur, directement récupéré depuis la BD
        roleField.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cccccc; -fx-padding: 5;");
        roleField.setEditable(false); // Le champ est en lecture seule, il ne peut pas être modifié


        Label dateInscriptionLabel = new Label("Date d'inscription");
        dateInscriptionLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: black;");

        // Champ pour la date d'inscription
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");  // Choisir le format souhaité
        String dateInscriptionString = sdf.format(user.getDateinscription());  // Convertir la Date en String

        TextField dateinscription = new TextField(dateInscriptionString);
        dateinscription.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cccccc; -fx-padding: 5;");
        // Boutons de modification et suppression d'ID
        //Button modifyIdButton = new Button("Modifier ID");
        //modifyIdButton.setOnAction(event -> handleUpdateUser());

        //Button deleteIdButton = new Button("Supprimer ID");
        //deleteIdButton.setOnAction(event -> handleDeleteUser());

        // Ajouter les labels, champs de texte et les boutons à la carte
        card.getChildren().addAll(idLabel, idField, nameLabel, nameField, prenomLabel, prenomField,
                emailLabel, emailField, passwordLabel, mdpfield, roleLabel, roleField,
                dateInscriptionLabel, dateinscription);

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

    @FXML
    private void handleAddUser() {
        Dialog<utilisateur> dialog = new Dialog<>();
        dialog.setTitle("Nouvel Utilisateur");

        // Set up form components
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
            if(newVal == Role.citoyen) {
                grid.addRow(5, new Label("Zone ID:"), zoneField);
            } else if(newVal == Role.technicien) {
                grid.addRow(5, new Label("Spécialité:"), specialiteCombo);
            } else if(newVal == Role.responsable) {
                grid.addRow(5, new Label("Modules (csv):"), modulesField);
            }
        });

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(buttonType -> {
            if (buttonType == ButtonType.OK) {
                // Création d'un nouvel utilisateur avec les valeurs du formulaire
                utilisateur user = new utilisateur(
                        nomField.getText(),
                        prenomField.getText(),
                        emailField.getText(),
                        passwordField.getText(),
                        roleCombo.getValue(), // Rôle sélectionné
                        new Date() // Date actuelle
                );

                // Gestion des cas en fonction du rôle sélectionné
                switch (user.getRole()) {
                    case citoyen:
                        try {
                            int zoneId = Integer.parseInt(zoneField.getText());
                            serviceUtilisateur.add(user, null, new ArrayList<>(), zoneId);
                        } catch (NumberFormatException e) {
                            showAlert("Erreur de saisie", "La zone ID doit être un nombre entier.");
                            return null;
                        }
                        break;
                    case technicien:
                        if (specialiteCombo.getValue() != null) {
                            serviceUtilisateur.add(user, specialiteCombo.getValue(), new ArrayList<>(), 0);
                        } else {
                            showAlert("Erreur de saisie", "Veuillez sélectionner une spécialité.");
                            return null;
                        }
                        break;
                    case responsable:
                        if (!modulesField.getText().isEmpty()) {
                            List<String> modules = Arrays.asList(modulesField.getText().split("\\s*,\\s*"));
                            serviceUtilisateur.add(user, null, modules, 0);
                        } else {
                            showAlert("Erreur de saisie", "Veuillez entrer des modules.");
                            return null;
                        }
                        break;
                    default:
                        showAlert("Erreur de rôle", "Rôle non reconnu.");
                        return null;
                }

                return user;
            }
            return null;
        });

        dialog.showAndWait();
        loadUsers();
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
