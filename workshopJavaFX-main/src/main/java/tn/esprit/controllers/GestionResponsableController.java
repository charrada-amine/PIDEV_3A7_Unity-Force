package tn.esprit.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import tn.esprit.models.responsable;
import tn.esprit.models.utilisateur;
import tn.esprit.services.ServiceCitoyen;
import tn.esprit.services.ServiceResponsable;
import tn.esprit.services.ServiceUtilisateur;
import tn.esprit.utils.MyDatabase;

import javax.swing.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Optional;

public class GestionResponsableController {

    @FXML
    private FlowPane responsableFlowPane;
    private final ServiceResponsable serviceResponsable = new ServiceResponsable();
    private final ServiceUtilisateur serviceUtilisateur = new ServiceUtilisateur();

    @FXML
    private void initialize() {
        if (responsableFlowPane == null) {
            System.err.println("Erreur : 'responsableFlowPane' est nul. Vérifiez le fichier FXML.");
        } else {
            System.out.println("FlowPane 'responsableFlowPane' initialisé correctement.");
            loadResponsables();
        }
    }

    private VBox createResponsableCard(responsable responsable) {
        VBox card = new VBox(10);
        card.setStyle("-fx-border-color: #ddd; -fx-border-width: 1; -fx-padding: 10; -fx-background-color: #f9f9f9; -fx-alignment: center;");

        // Champ ID (non modifiable)
        Label idLabel = new Label("ID:");
        idLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: black;");
        TextField idField = new TextField(String.valueOf(responsable.getId_utilisateur()));
        idField.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cccccc; -fx-padding: 5;");
        idField.setEditable(false);
        HBox idBox = new HBox(10, idLabel, idField); // Aligner le label et le champ ID
        idBox.setAlignment(Pos.CENTER_LEFT); // Aligner à gauche

        // Champ Nom (non modifiable)
        Label nameLabel = new Label("Nom:");
        nameLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: black;");
        TextField nameField = new TextField(responsable.getNom());
        nameField.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cccccc; -fx-padding: 5;");
        nameField.setEditable(false);
        HBox nameBox = new HBox(10, nameLabel, nameField); // Aligner le label et le champ Nom
        nameBox.setAlignment(Pos.CENTER_LEFT);

        // Champ Prénom (non modifiable)
        Label prenomLabel = new Label("Prénom:");
        prenomLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: black;");
        TextField prenomField = new TextField(responsable.getPrenom());
        prenomField.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cccccc; -fx-padding: 5;");
        prenomField.setEditable(false);
        HBox prenomBox = new HBox(10, prenomLabel, prenomField); // Aligner le label et le champ Prénom
        prenomBox.setAlignment(Pos.CENTER_LEFT);

        // Champ Email (non modifiable)
        Label emailLabel = new Label("Email:");
        emailLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: black;");
        TextField emailField = new TextField(responsable.getEmail());
        emailField.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cccccc; -fx-padding: 5;");
        emailField.setEditable(false);
        HBox emailBox = new HBox(10, emailLabel, emailField); // Aligner le label et le champ Email
        emailBox.setAlignment(Pos.CENTER_LEFT);

        // Champ Mot de passe (non modifiable)
        Label passwordLabel = new Label("Mot de passe:");
        passwordLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: black;");
        TextField passwordField = new TextField(responsable.getMotdepasse());
        passwordField.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cccccc; -fx-padding: 5;");
        passwordField.setEditable(false);
        HBox passwordBox = new HBox(10, passwordLabel, passwordField); // Aligner le label et le champ Mot de passe
        passwordBox.setAlignment(Pos.CENTER_LEFT);

        // Champ Date d'inscription (non modifiable)
        Label dateInscriptionLabel = new Label("Date d'inscription:");
        dateInscriptionLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: black;");
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        TextField dateField = new TextField(sdf.format(responsable.getDateinscription()));
        dateField.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cccccc; -fx-padding: 5;");
        dateField.setEditable(false);
        HBox dateInscriptionBox = new HBox(10, dateInscriptionLabel, dateField); // Aligner le label et le champ Date d'inscription
        dateInscriptionBox.setAlignment(Pos.CENTER_LEFT);

        // Champ Modules (non modifiable)
        Label modulesLabel = new Label("Modules:");
        modulesLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: black;");
        TextArea modulesField = new TextArea(String.join(", ", responsable.getModules()));
        modulesField.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cccccc; -fx-padding: 5;");
        modulesField.setEditable(false);
        modulesField.setPrefHeight(50); // Ajuster la hauteur pour afficher plusieurs modules
        modulesField.setMaxWidth(150); // Réduire la largeur du champ Modules
        HBox modulesBox = new HBox(10, modulesLabel, modulesField); // Aligner le label et le champ Modules
        modulesBox.setAlignment(Pos.CENTER_LEFT);

        // Ajouter les HBoxes à la carte
        card.getChildren().addAll(
                idBox,
                nameBox,
                prenomBox,
                emailBox,
                passwordBox,
                dateInscriptionBox,
                modulesBox
        );

        return card;
    }

    @FXML
    private void loadResponsables() {
        if (responsableFlowPane == null) {
            System.err.println("Erreur : 'responsableFlowPane' est nul lors de l'appel de 'loadResponsables'.");
            return;
        }
        ServiceResponsable serviceResponsable = new ServiceResponsable();


        List<responsable> responsables;

        try {
            responsables = serviceResponsable.getAllResponsables();
        } catch (Exception e) {
            System.err.println("Erreur lors de la récupération des responsables : " + e.getMessage());
            return;
        }

        if (responsables == null || responsables.isEmpty()) {
            System.out.println("Aucun responsable trouvé.");
            responsableFlowPane.getChildren().clear();
            return;
        }

        ObservableList<VBox> responsableCards = FXCollections.observableArrayList();

        for (responsable r : responsables) {
            VBox responsableCard = createResponsableCard(r);
            responsableCards.add(responsableCard);
        }

        responsableFlowPane.getChildren().clear();
        responsableFlowPane.getChildren().addAll(responsableCards);
    }
    public boolean emailExistsResponsable(String email) {
        String query = "SELECT COUNT(*) FROM utilisateur WHERE email = ?";
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            Connection connection = MyDatabase.getInstance().getCnx(); // Connexion partagée
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, email);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getInt(1) > 0; // Si le résultat est supérieur à 0, l'email existe
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
    private void handleUpdateResponsable() {
        // Créer un nouveau GridPane chaque fois que cette méthode est appelée
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        // Champ pour entrer l'ID du responsable
        TextField userIdField = new TextField();
        grid.addRow(0, new Label("Entrez l'ID du responsable:"), userIdField);

        // Champ pour sélectionner quel champ modifier
        ComboBox<String> fieldSelectionCombo = new ComboBox<>();
        fieldSelectionCombo.getItems().add(""); // Option vide par défaut
        fieldSelectionCombo.getItems().addAll("Nom", "Prénom", "Email", "Mot de passe" ,"Modules");
        fieldSelectionCombo.getSelectionModel().selectFirst();

        // Champs pour les différentes modifications
        TextField nomField = new TextField();
        TextField prenomField = new TextField();
        TextField emailField = new TextField();
        TextField passwordField = new TextField();
        TextArea modulesField = new TextArea();

        // Ajout des champs au GridPane
        grid.addRow(1, new Label("Sélectionnez le champ à modifier:"), fieldSelectionCombo);
        grid.addRow(2, new Label("Nom:"), nomField);
        grid.addRow(3, new Label("Prénom:"), prenomField);
        grid.addRow(4, new Label("Email:"), emailField);
        grid.addRow(5, new Label("Mot de passe:"), passwordField);
        grid.addRow(6, new Label("Modules (séparés par des virgules):"), modulesField);

        // Masquer tous les champs de modification par défaut
        nomField.setVisible(false);
        prenomField.setVisible(false);
        emailField.setVisible(false);
        passwordField.setVisible(false);
        modulesField.setVisible(false);

        // Création des boutons OK et Annuler
        ButtonType okButton = ButtonType.OK;
        ButtonType cancelButton = ButtonType.CANCEL;

        // Création du dialogue
        Dialog<utilisateur> dialog = new Dialog<>();
        dialog.setTitle("Modifier Responsable");
        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(okButton, cancelButton);

        // Listener sur le ComboBox pour afficher uniquement le champ sélectionné
        fieldSelectionCombo.valueProperty().addListener((observable, oldValue, newValue) -> {
            // Masquer tous les champs
            nomField.setVisible(false);
            prenomField.setVisible(false);
            emailField.setVisible(false);
            passwordField.setVisible(false);
            modulesField.setVisible(false);

            // Afficher uniquement le champ sélectionné
            if (newValue != null && !newValue.isEmpty()) {
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
                        passwordField.setVisible(true);
                        break;
                    case "Modules":
                        modulesField.setVisible(true);
                        break;
                }
            }
        });

        // Gérer le résultat du dialogue
        dialog.setResultConverter(button -> {
            if (button == okButton) {
                // Récupérer l'ID du responsable et le valider
                String userIdText = userIdField.getText();
                if (userIdText.isEmpty()) {
                    showAlert("Erreur", "L'ID du responsable ne peut pas être vide.");
                    return null;
                }

                int userId;
                try {
                    userId = Integer.parseInt(userIdText);
                } catch (NumberFormatException e) {
                    showAlert("Erreur", "L'ID doit être un nombre entier.");
                    return null;
                }

                // Vérifier si c'est un responsable valide
                utilisateur selectedResponsable = serviceUtilisateur.getUtilisateurById(userId);
                if (selectedResponsable == null) {
                    showAlert("Erreur", "Aucun responsable trouvé avec cet ID.");
                    return null;
                }

                // Vérification de l'email (si le champ est modifié)
                if ("Email".equals(fieldSelectionCombo.getValue()) && !emailField.getText().isEmpty()) {
                    String email = emailField.getText();
                    if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
                        showAlert("Erreur", "L'email doit être valide.");
                        return null;
                    }

                    // Vérifier si l'email existe déjà
                    if (emailExistsResponsable(email)) {
                        showAlert("Erreur", "L'email est déjà utilisé.");
                        return null;
                    }
                }

                // Mettre à jour le champ sélectionné
                switch (fieldSelectionCombo.getValue()) {
                    case "Nom":
                        if (nomField.getText().isEmpty()) {
                            showAlert("Erreur", "Le nom ne peut pas être vide.");
                            return null;
                        }
                        serviceUtilisateur.updateField(selectedResponsable.getId_utilisateur(), "nom", nomField.getText());
                        break;
                    case "Prénom":
                        if (prenomField.getText().isEmpty()) {
                            showAlert("Erreur", "Le prénom ne peut pas être vide.");
                            return null;
                        }
                        serviceUtilisateur.updateField(selectedResponsable.getId_utilisateur(), "prenom", prenomField.getText());
                        break;
                    case "Email":
                        serviceUtilisateur.updateField(selectedResponsable.getId_utilisateur(), "email", emailField.getText());
                        break;
                    case "Mot de passe":
                        if (passwordField.getText().isEmpty() || passwordField.getText().length() < 8 ||
                                !passwordField.getText().matches(".*[A-Z].*") || !passwordField.getText().matches(".*\\d.*")) {
                            showAlert("Erreur de saisie", "Le mot de passe doit comporter au moins 8 caractères, avec une majuscule et un chiffre.");
                            return null;
                        }
                        serviceUtilisateur.updateField(selectedResponsable.getId_utilisateur(), "motdepasse", passwordField.getText());
                        break;
                    case "Modules":
                        String modulesText = modulesField.getText();
                        if (modulesText.isEmpty()) {
                            showAlert("Erreur", "Les modules ne peuvent pas être vides.");
                            return null;
                        }

                        // Valider le format des modules
                        String[] modulesArray = modulesText.split(",");
                        for (String module : modulesArray) {
                            if (module.trim().isEmpty()) {
                                showAlert("Erreur", "Chaque module doit être un texte non vide.");
                                return null;
                            }
                        }

                        // Conversion en format texte compatible avec la base de données
                        String formattedModules = String.join(",", modulesArray);

                        try {
                            serviceUtilisateur.updateFieldResponsable(selectedResponsable.getId_utilisateur(), "modules", formattedModules);
                        } catch (Exception e) {
                            e.printStackTrace();
                            showAlert("Erreur", "Échec de la mise à jour des modules : " + e.getMessage());
                            return null;
                        }
                        break;

                    default:
                        showAlert("Erreur", "Aucun champ sélectionné pour la modification.");
                        return null;
                }

                loadResponsables(); // Recharger la liste après modification
            }
            return null;
        });

        dialog.showAndWait();
    }


    @FXML
    private void handleDeleteResponsable() {
        // Créer un formulaire pour entrer l'ID du responsable
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Supprimer Responsable");
        dialog.setHeaderText("Supprimer un responsable par son ID");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        TextField responsableIdField = new TextField();
        grid.addRow(0, new Label("ID Responsable :"), responsableIdField);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(button -> {
            if (button == ButtonType.OK) {
                try {
                    int id = Integer.parseInt(responsableIdField.getText());
                    utilisateur responsable = serviceResponsable.getResponsableById(id);

                    if (responsable != null) {
                        // Demande de confirmation
                        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
                        confirmation.setTitle("Confirmation");
                        confirmation.setHeaderText("Voulez-vous vraiment supprimer le responsable : " +
                                responsable.getNom() + " " + responsable.getPrenom() + "?");

                        Optional<ButtonType> result = confirmation.showAndWait();
                        if (result.isPresent() && result.get() == ButtonType.OK) {
                            serviceResponsable.deleteById(id);
                            showAlert("Succès", "Responsable supprimé avec succès !");
                            loadResponsables(); // Recharger la liste après suppression
                        }
                    } else {
                        showAlert("Erreur", "Responsable avec l'ID " + id + " introuvable.");
                    }
                } catch (NumberFormatException e) {
                    showAlert("Erreur", "Veuillez entrer un ID valide.");
                }
            }
            return null;
        });

        dialog.showAndWait();
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
