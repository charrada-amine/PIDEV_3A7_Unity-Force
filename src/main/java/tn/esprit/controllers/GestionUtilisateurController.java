package tn.esprit.controllers;
import java.time.format.DateTimeFormatter;
import java.text.SimpleDateFormat;

import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.Node;
import tn.esprit.utils.MyDatabase;
import tn.esprit.models.utilisateur;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
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
import java.util.stream.Collectors;

public class GestionUtilisateurController {
    @FXML
    private VBox utilisateurListContainer;

    @FXML
    private TextField idField, nameField, prenomField, emailField, passwordField, zoneIdField, modulesField;

    @FXML
    private ComboBox<String> roleComboBox, specialiteComboBox;

    @FXML
    private HBox zoneIdBox, specialiteBox, modulesBox;

    @FXML
    private FlowPane userFlowPane;
    private String roleSelectionne = "Tous"; // Valeur par défaut
    private List<utilisateur> users = new ArrayList<>();
    private Button updateButton;
    @FXML
    private Button citoyenButton;  // Bouton pour les Citoyens
    @FXML
    private Button technicienButton; // Bouton pour les Techniciens
    @FXML
    private Button responsableButton; // Bouton pour les Responsables
    private Button AccueilButton;  // Bouton pour les Citoyens
    @FXML
    private ComboBox<String> roleFilterComboBox;

    @FXML
    private ScrollPane scrollPane;

    private ServiceUtilisateur serviceUtilisateur = new ServiceUtilisateur();

    // Initialisation du contrôleur
    @FXML
    public void initialize() {
        loadUsers();

        // Remplir les ComboBox avec des valeurs prédéfinies pour les rôles et spécialités
        roleComboBox.setItems(FXCollections.observableArrayList("Citoyen", "Responsable", "Technicien"));
        specialiteComboBox.setItems(FXCollections.observableArrayList("Maintenance", "Électricité", "Autre"));

        // Cacher les champs spécifiques selon le rôle sélectionné
        roleComboBox.setOnAction(e -> handleRoleChange());
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
    @FXML
    public void filterByResponsable() {
        try {
            // Charger la vue GestionResponsable.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/GestionResponsable.fxml"));
            Parent root = loader.load();

            // Créer une nouvelle scène avec la vue chargée
            Stage stage = new Stage();
            Scene scene = new Scene(root);

            // Configurer la scène et la fenêtre
            stage.setScene(scene);
            stage.setTitle("Gestion des Responsables");

            // Maximiser automatiquement la fenêtre
            stage.setMaximized(true); // Maximiser la fenêtre

            stage.show();  // Afficher la scène
        } catch (IOException e) {
            e.printStackTrace();  // Afficher l'erreur en cas de problème
        }
    }



    // Méthode de filtrage pour les Citoyens
    @FXML
    public void filterByCitoyen() {
        {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/GestionCitoyen.fxml"));
                Parent root = loader.load();

                Stage stage = new Stage();
                Scene scene = new Scene(root);

                stage.setScene(scene);
                stage.setTitle("Gestion des Citoyens");

                // Maximiser automatiquement
                stage.setMaximized(true);

                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Méthode de filtrage pour les Techniciens
    @FXML
    public void filterByTechnicien() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/GestionTechnicien.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            Scene scene = new Scene(root);

            stage.setScene(scene);
            stage.setTitle("Gestion des Techniciens");

            // Maximiser automatiquement
            stage.setMaximized(true);

            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    public void filterByAccueil() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Accueil.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            Scene scene = new Scene(root);

            stage.setScene(scene);
            stage.setTitle("Gestion des utilisateurs");

            // Maximiser automatiquement
            stage.setMaximized(true);

            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private VBox createUserCard(utilisateur user) {
        VBox card = new VBox(10);
        card.setStyle("-fx-border-color: #ddd; -fx-border-width: 1; -fx-padding: 10; -fx-background-color: #f9f9f9; -fx-alignment: center;");

        // Champ ID (non modifiable)
        /*Label idLabel = new Label("ID:");
        idLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: black;");
        TextField idField = new TextField(String.valueOf(user.getId_utilisateur()));
        idField.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cccccc; -fx-padding: 5;");
        idField.setEditable(false); // Non modifiable
        HBox idBox = new HBox(10, idLabel, idField);
        idBox.setAlignment(Pos.CENTER_LEFT);*/

        // Champ Nom
        Label nameLabel = new Label("Nom:");
        nameLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: black;");
        TextField nameField = new TextField(user.getNom());
        nameField.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cccccc; -fx-padding: 5;");
        nameField.setEditable(false);
        HBox nameBox = new HBox(10, nameLabel, nameField);
        nameBox.setAlignment(Pos.CENTER_LEFT);

        // Champ Prénom
        Label prenomLabel = new Label("Prénom:");
        prenomLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: black;");
        TextField prenomField = new TextField(user.getPrenom());
        prenomField.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cccccc; -fx-padding: 5;");
        prenomField.setEditable(false);
        HBox prenomBox = new HBox(10, prenomLabel, prenomField);
        prenomBox.setAlignment(Pos.CENTER_LEFT);

        // Champ Email
        Label emailLabel = new Label("Email:");
        emailLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: black;");
        TextField emailField = new TextField(user.getEmail());
        emailField.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cccccc; -fx-padding: 5;");
        emailField.setEditable(false);
        HBox emailBox = new HBox(10, emailLabel, emailField);
        emailBox.setAlignment(Pos.CENTER_LEFT);

        // Champ Mot de passe
        /*Label passwordLabel = new Label("Mot de passe:");
        passwordLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: black;");
        TextField mdpfield = new TextField(user.getMotdepasse());
        mdpfield.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cccccc; -fx-padding: 5;");
        mdpfield.setEditable(false);
        HBox mdpBox = new HBox(10, passwordLabel, mdpfield);
        mdpBox.setAlignment(Pos.CENTER_LEFT);*/

        // Champ Rôle
        Label roleLabel = new Label("Rôle:");
        roleLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: black;");
        TextField roleField = new TextField(user.getRole().name());
        roleField.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cccccc; -fx-padding: 5;");
        roleField.setEditable(false);
        HBox roleBox = new HBox(10, roleLabel, roleField);
        roleBox.setAlignment(Pos.CENTER_LEFT);

        // Champ Date d'inscription
        Label dateInscriptionLabel = new Label("Date d'inscription");
        dateInscriptionLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: black;");
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String dateInscriptionString = sdf.format(user.getDateinscription());
        TextField dateinscription = new TextField(dateInscriptionString);
        dateinscription.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cccccc; -fx-padding: 5;");
        dateinscription.setEditable(false);
        HBox dateInscriptionBox = new HBox(10, dateInscriptionLabel, dateinscription);
        dateInscriptionBox.setAlignment(Pos.CENTER_LEFT);

        // Ajouter les HBoxes à la carte
        card.getChildren().addAll(nameBox, prenomBox, emailBox,  roleBox, dateInscriptionBox);

        return card;
    }


    @FXML
    public void handleAddUser() throws IOException {
        try {
            String name = nameField.getText();
            String prenom = prenomField.getText();
            String email = emailField.getText();
            String password = passwordField.getText();
            String roleString = roleComboBox.getValue();

            // Validation des champs
            if (name.isEmpty() || prenom.isEmpty() || email.isEmpty() || password.isEmpty() || roleString == null) {
                showAlert("Erreur", "Tous les champs doivent être remplis.");
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
                    if (modulesField.getText().isEmpty()) {
                        showAlert("Erreur", "Veuillez entrer des modules.");
                        return;
                    }
                    List<String> modules = List.of(modulesField.getText().split("\\s*,\\s*"));
                    serviceUtilisateur.add(user, null, modules, 0);
                    break;
            }

            // Ajoutez la carte utilisateur dans l'interface après l'ajout
            VBox userCard = createUserCard(user); // Créer la carte de l'utilisateur
            userFlowPane.getChildren().add(userCard); // Ajouter la carte à l'FlowPane

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






    @FXML
    private void handleUpdateUser() {
        // Créer un GridPane pour le formulaire de modification
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        // Ajouter un champ pour entrer l'ID de l'utilisateur
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

        // Ajouter les champs de texte pour les modifications
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

        // Créer le dialog
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

                // Validation de l'email seulement si l'utilisateur modifie le champ "Email"
                if (fieldSelectionCombo.getValue().equals("Email")) {
                    if (emailField.getText().isEmpty()) {
                        showAlert("Erreur", "L'email ne peut pas être vide.");
                        return null;
                    }
                    if (!emailField.getText().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
                        showAlert("Erreur", "L'email n'est pas valide.");
                        return null;
                    }
                    if (emailExists(emailField.getText())) {
                        showAlert("Erreur de saisie", "L'email est déjà utilisé.");
                        return null;
                    }
                }

                // Validation du mot de passe uniquement si le champ "Mot de passe" est sélectionné
                if (fieldSelectionCombo.getValue().equals("Mot de passe")) {
                    if (mdpField.getText().isEmpty()) {
                        showAlert("Erreur", "Le mot de passe ne peut pas être vide.");
                        return null;
                    }
                    if (mdpField.getText().length() < 8 || !mdpField.getText().matches(".*\\d.*") || !mdpField.getText().matches(".*[A-Z].*")) {
                        showAlert("Erreur", "Le mot de passe doit comporter au moins 8 caractères, un chiffre et une lettre majuscule.");
                        return null;
                    }

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



    // Méthode pour trouver un utilisateur par ID
   /* private utilisateur findUserById(int id) {
        // Implémentation pour retrouver un utilisateur par son ID dans votre service ou base de données
        // Par exemple, avec votre serviceUtilisateur:
        return serviceUtilisateur.getUtilisateurById(id);
    }*/



    // Supprimer un utilisateur
    @FXML
    public void handleDeleteUser() {
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
    private void handleShowRoleFilter() {
        // Afficher le ComboBox quand on clique sur le bouton
        roleFilterComboBox.setVisible(true);
    }

    @FXML
    private void handleFilterByRole() {
        String selectedRole = roleFilterComboBox.getValue();
        if (selectedRole == null || userFlowPane == null) return;

        // Récupérer tous les utilisateurs
        List<utilisateur> utilisateurs = serviceUtilisateur.getAllUtilisateurs();

        // Filtrer par rôle (sauf si "Tous" est sélectionné)
        if (!"Tous".equals(selectedRole)) {
            utilisateurs = utilisateurs.stream()
                    .filter(u -> u.getRole().name().equalsIgnoreCase(selectedRole))
                    .collect(Collectors.toList());
        }

        // Mettre à jour l'affichage
        userFlowPane.getChildren().clear();
        for (utilisateur user : utilisateurs) {
            VBox card = createUserCard(user);
            userFlowPane.getChildren().add(card);
        }
    }
    // Réinitialiser les champs
    @FXML
    public void handleClear() {
        clearFields();
    }

    // Fonction pour réinitialiser les champs du formulaire
    private void clearFields() {
        nameField.clear();
        prenomField.clear();
        emailField.clear();
        passwordField.clear();
        zoneIdField.clear();
        modulesField.clear();
        roleComboBox.setValue(null);
        specialiteComboBox.setValue(null);
        zoneIdBox.setVisible(false);
        specialiteBox.setVisible(false);
        modulesBox.setVisible(false);
    }

    // Fonction pour afficher une alerte
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
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
    @FXML
    private void handleGestionCapteur(ActionEvent event) {
        switchScene(event, "/GestionCapteur.fxml");
    }

    @FXML
    private void handleGestionCitoyen(ActionEvent event) {
        switchScene(event, "/GestionCitoyen.fxml");
    }

    @FXML
    private void handleGestionDonnee(ActionEvent event) {
        switchScene(event, "/GestionDonnee.fxml");
    }

    @FXML
    private void handleGestionIntervention(ActionEvent event) {
        switchScene(event, "/GestionIntervention.fxml");
    }

    @FXML
    private void handleGestionLampadaire(ActionEvent event) {
        switchScene(event, "/GestionLampadaire.fxml");
    }

    @FXML
    private void handleGestionReclamation(ActionEvent event) {
        switchScene(event, "/GestionReclamation.fxml");
    }

    @FXML
    private void handleGestionResponsable(ActionEvent event) {
        switchScene(event, "/GestionResponsable.fxml");
    }

    @FXML
    private void handleGestionTechnicien(ActionEvent event) {
        switchScene(event, "/GestionTechnicien.fxml");
    }

    @FXML
    private void handleGestionUtilisateur(ActionEvent event) {
        switchScene(event, "/GestionUtilisateur.fxml");
    }

    @FXML
    private void handleGestionZone(ActionEvent event) {
        switchScene(event, "/GestionZone.fxml");
    }

    @FXML
    private void handleProfileInterface(ActionEvent event) {
        switchScene(event, "/ProfileInterface.fxml");
    }

    @FXML
    private void handleSourceInterface(ActionEvent event) {
        switchScene(event, "/SourceInterface.fxml");
    }

    @FXML
    private void handleBack() {
        // Logique pour revenir à la page précédente
        System.out.println("Retour à la page précédente");
    }

    private void switchScene(ActionEvent event, String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            // Récupère la scène actuelle et met à jour son contenu
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}