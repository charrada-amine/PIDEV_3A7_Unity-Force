package tn.esprit.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import tn.esprit.models.responsable;
import tn.esprit.models.utilisateur;
import tn.esprit.services.ServiceCitoyen;
import tn.esprit.services.ServiceResponsable;
import tn.esprit.services.ServiceUtilisateur;
import tn.esprit.utils.MyDatabase;

import javax.swing.*;
import java.io.IOException;
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
    private TextField idField, nameField, prenomField, emailField, passwordField, modulesField ;
    @FXML
    private void initialize() {
        if (responsableFlowPane == null) {
            System.err.println("Erreur : 'responsableFlowPane' est nul. Vérifiez le fichier FXML.");
        } else {
            System.out.println("FlowPane 'responsableFlowPane' initialisé correctement.");
            loadUsers();
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
        TextField modulesField = new TextField(String.join(", ", responsable.getModules())); // Utiliser un TextField au lieu d'un TextArea
        modulesField.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cccccc; -fx-padding: 5;");
        modulesField.setEditable(false);
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
    private void loadUsers() {
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
            e.printStackTrace();
            return;
        }

        if (responsables == null || responsables.isEmpty()) {
            System.out.println("Aucun responsable trouvé.");
            responsableFlowPane.getChildren().clear();
            return;
        }

        ObservableList<VBox> responsableCards = FXCollections.observableArrayList();

        for (responsable r : responsables) {
            VBox responsableCard = createResponsableCard(r);  // Utilisation de la méthode createResponsableCard
            responsableCards.add(responsableCard);
        }

        responsableFlowPane.getChildren().clear();
        responsableFlowPane.getChildren().addAll(responsableCards);
    }


    @FXML
    public void handleClear() {
        clearFields();
    }
    @FXML
    public void filterByTechnicien() {
        try {
            // Charger la vue GestionResponsable.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/GestionTechnicien.fxml"));
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
    public void filterByUtilisateur() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/GestionUtilisateur.fxml"));
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
    private void handleUpdateResponsable() {
        try {
            // Récupérer l'ID du responsable
            String userIdText = idField.getText();
            if (userIdText.isEmpty()) {
                showAlert("Erreur", "L'ID de l'utilisateur/responsable ne peut pas être vide.");
                return;
            }

            int userId;
            try {
                userId = Integer.parseInt(userIdText);
                if (userId <= 0) {
                    showAlert("Erreur", "L'ID doit être un nombre entier positif.");
                    return;
                }
            } catch (NumberFormatException e) {
                showAlert("Erreur", "L'ID doit être un nombre entier.");
                return;
            }

            // Vérifier si l'utilisateur existe
            utilisateur existingUser = serviceUtilisateur.getUtilisateurById(userId);
            if (existingUser == null) {
                showAlert("Erreur", "Aucun utilisateur trouvé avec cet ID.");
                return;
            }

            // Récupérer et valider les champs
            String newName = nameField.getText().trim();
            if (newName.isEmpty() || !newName.matches("[A-Za-zÀ-ÖØ-öø-ÿ\\s-]+")) {
                showAlert("Erreur", "Le nom doit contenir uniquement des lettres et ne peut pas être vide.");
                return;
            }

            String newPrenom = prenomField.getText().trim();
            if (newPrenom.isEmpty() || !newPrenom.matches("[A-Za-zÀ-ÖØ-öø-ÿ\\s-]+")) {
                showAlert("Erreur", "Le prénom doit contenir uniquement des lettres et ne peut pas être vide.");
                return;
            }

            String newEmail = emailField.getText().trim();
            if (newEmail.isEmpty() || !newEmail.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
                showAlert("Erreur", "L'email doit être valide.");
                return;
            }
            if (!newEmail.equals(existingUser.getEmail()) && emailExists(newEmail)) {
                showAlert("Erreur", "L'email est déjà utilisé.");
                return;
            }

            String newPassword = passwordField.getText().trim();
            if (newPassword.isEmpty() || newPassword.length() < 8
                    || !newPassword.matches(".*\\d.*")
                    || !newPassword.matches(".*[A-Z].*")) {
                showAlert("Erreur", "Le mot de passe doit comporter au moins 8 caractères, un chiffre et une lettre majuscule.");
                return;
            }

            // Validation des modules
            String modulesText = modulesField.getText().trim();
            if (modulesText.isEmpty()) {
                showAlert("Erreur", "Les modules ne peuvent pas être vides.");
                return;
            }

            String[] modulesArray = modulesText.split(",");
            for (String module : modulesArray) {
                if (module.trim().isEmpty()) {
                    showAlert("Erreur", "Chaque module doit être un texte non vide.");
                    return;
                }
            }
            String formattedModules = String.join(",", modulesArray);

            // Mise à jour des champs dans le service
            serviceUtilisateur.updateFieldResponsable(userId, "nom", newName);
            serviceUtilisateur.updateFieldResponsable(userId, "prenom", newPrenom);
            serviceUtilisateur.updateFieldResponsable(userId, "email", newEmail);
            serviceUtilisateur.updateFieldResponsable(userId, "motdepasse", newPassword);
            serviceUtilisateur.updateFieldResponsable(userId, "modules", formattedModules);

            // Recharger les utilisateurs pour refléter la modification
            loadUsers();

            // Réinitialiser les champs
            clearFields();

            // Confirmation de la modification
            showAlert("Succès", "Responsable modifié avec succès.");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Une erreur est survenue lors de la modification.");
        }
    }
    private void clearFields() {
        idField.clear();
        nameField.clear();
        prenomField.clear();
        emailField.clear();
        passwordField.clear();
        modulesField.clear(); // Réinitialiser le champ des modules
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
                            loadUsers(); // Recharger la liste après suppression
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
