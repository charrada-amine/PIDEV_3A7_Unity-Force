package tn.esprit.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import tn.esprit.models.citoyen;
import tn.esprit.models.utilisateur;
import javafx.geometry.Pos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

import tn.esprit.services.ServiceCitoyen;
import tn.esprit.services.ServiceUtilisateur;
import tn.esprit.utils.MyDatabase;
import javafx.geometry.Pos;

import java.text.SimpleDateFormat;
import java.util.List;

public class GestionCitoyenController {
    @FXML
    private TextField idField, nameField, prenomField, emailField, passwordField, zoneIdField;
    @FXML
    private FlowPane citoyenFlowPane; // Correspond à 'fx:id="citoyenFlowPane"' dans le FXML
    private final ServiceUtilisateur serviceUtilisateur = new ServiceUtilisateur();
    private final ServiceCitoyen serviceCitoyen = new ServiceCitoyen();
    @FXML
    private void initialize() {
        if (citoyenFlowPane == null) {
            System.err.println("Erreur : 'citoyenFlowPane' est nul. Vérifiez le fichier FXML.");
        } else {
            System.out.println("FlowPane 'citoyenFlowPane' initialisé correctement.");
            loadUsers(); // Charger les citoyens
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


    private VBox createUserCard(citoyen citoyen) {
        VBox card = new VBox(10);
        card.setStyle("-fx-border-color: #ddd; -fx-border-width: 1; -fx-padding: 10; -fx-background-color: #f9f9f9; -fx-alignment: center;");

        // Champ ID (non modifiable)
        Label idLabel = new Label("ID:");
        idLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: black;");
        TextField idField = new TextField(String.valueOf(citoyen.getId_utilisateur()));
        idField.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cccccc; -fx-padding: 5;");
        idField.setEditable(false); // Non modifiable
        HBox idBox = new HBox(10, idLabel, idField); // Aligner le label et le champ ID
        idBox.setAlignment(Pos.CENTER_LEFT); // Aligner à gauche

        // Champ Nom (non modifiable)
        Label nameLabel = new Label("Nom:");
        nameLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: black;");
        TextField nameField = new TextField(citoyen.getNom());
        nameField.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cccccc; -fx-padding: 5;");
        nameField.setEditable(false); // Non modifiable
        HBox nameBox = new HBox(10, nameLabel, nameField); // Aligner le label et le champ Nom
        nameBox.setAlignment(Pos.CENTER_LEFT); // Aligner à gauche

        // Champ Prénom (non modifiable)
        Label prenomLabel = new Label("Prénom:");
        prenomLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: black;");
        TextField prenomField = new TextField(citoyen.getPrenom());
        prenomField.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cccccc; -fx-padding: 5;");
        prenomField.setEditable(false); // Non modifiable
        HBox prenomBox = new HBox(10, prenomLabel, prenomField); // Aligner le label et le champ Prénom
        prenomBox.setAlignment(Pos.CENTER_LEFT); // Aligner à gauche

        // Champ Email (non modifiable)
        Label emailLabel = new Label("Email:");
        emailLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: black;");
        TextField emailField = new TextField(citoyen.getEmail());
        emailField.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cccccc; -fx-padding: 5;");
        emailField.setEditable(false); // Non modifiable
        HBox emailBox = new HBox(10, emailLabel, emailField); // Aligner le label et le champ Email
        emailBox.setAlignment(Pos.CENTER_LEFT); // Aligner à gauche

        // Champ Mot de passe (non modifiable)
        Label passwordLabel = new Label("Mot de passe:");
        passwordLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: black;");
        TextField passwordField = new TextField(citoyen.getMotdepasse());
        passwordField.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cccccc; -fx-padding: 5;");
        passwordField.setEditable(false); // Non modifiable
        HBox passwordBox = new HBox(10, passwordLabel, passwordField); // Aligner le label et le champ Mot de passe
        passwordBox.setAlignment(Pos.CENTER_LEFT); // Aligner à gauche

        // Champ Date d'inscription (non modifiable)
        Label dateInscriptionLabel = new Label("Date d'inscription:");
        dateInscriptionLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: black;");
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        TextField dateField = new TextField(sdf.format(citoyen.getDateinscription()));
        dateField.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cccccc; -fx-padding: 5;");
        dateField.setEditable(false); // Non modifiable
        HBox dateInscriptionBox = new HBox(10, dateInscriptionLabel, dateField); // Aligner le label et le champ Date d'inscription
        dateInscriptionBox.setAlignment(Pos.CENTER_LEFT); // Aligner à gauche

        // Champ Zone ID (non modifiable)
        Label zoneIdLabel = new Label("Zone ID:");
        zoneIdLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: black;");
        TextField zoneIdField = new TextField(String.valueOf(citoyen.getZoneId()));
        zoneIdField.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cccccc; -fx-padding: 5;");
        zoneIdField.setEditable(false); // Non modifiable
        HBox zoneIdBox = new HBox(10, zoneIdLabel, zoneIdField); // Aligner le label et le champ Zone ID
        zoneIdBox.setAlignment(Pos.CENTER_LEFT); // Aligner à gauche

        // Ajouter les HBoxes à la carte
        card.getChildren().addAll(
                idBox,
                nameBox,
                prenomBox,
                emailBox,
                passwordBox,
                dateInscriptionBox,
                zoneIdBox
        );

        return card;
    }
    @FXML
    private void loadUsers() {
        if (citoyenFlowPane == null) {
            System.err.println("Erreur : 'citoyenFlowPane' est nul lors de l'appel de 'loadUsers'.");
            return;
        }

        ServiceCitoyen serviceCitoyen = new ServiceCitoyen();
        List<citoyen> citoyens;

        try {
            citoyens = serviceCitoyen.getAllCitoyens();
        } catch (Exception e) {
            System.err.println("Erreur lors de la récupération des citoyens : " + e.getMessage());
            e.printStackTrace();
            return;
        }

        if (citoyens == null || citoyens.isEmpty()) {
            System.out.println("Aucun citoyen trouvé.");
            citoyenFlowPane.getChildren().clear();
            return;
        }

        ObservableList<VBox> citoyenCards = FXCollections.observableArrayList();

        for (citoyen c : citoyens) {
            VBox citoyenCard = createUserCard(c);
            citoyenCards.add(citoyenCard);
        }

        citoyenFlowPane.getChildren().clear();
        citoyenFlowPane.getChildren().addAll(citoyenCards);
    }
    private citoyen getSelectedCitoyen() {
        // TODO: Implémentez la logique pour récupérer l'élément sélectionné.
        // Par exemple, vous pouvez avoir une liste ou une table de citoyens.
        return null; // Remplacez `null` par la sélection réelle.
    }
    public boolean emailExistsCitoyen(String email) {
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
    private void handleUpdateCitoyen() {
        try {
            // Récupérer l'ID du citoyen
            String userIdText = idField.getText();
            if (userIdText.isEmpty()) {
                showAlert("Erreur", "L'ID de l'utilisateur/citoyen ne peut pas être vide.");
                return;
            }

            int userId;
            try {
                userId = Integer.parseInt(userIdText);
            } catch (NumberFormatException e) {
                showAlert("Erreur", "L'ID doit être un nombre entier.");
                return;
            }

            // Vérifier si l'utilisateur existe
            utilisateur existingUser = serviceUtilisateur.getUtilisateurById(userId);
            if (existingUser == null) {
                showAlert("Erreur", "Aucun citoyen trouvé avec cet ID.");
                return;
            }

            // Récupérer et valider les champs
            String newName = nameField.getText();
            String newPrenom = prenomField.getText();
            String newEmail = emailField.getText();
            String newPassword = passwordField.getText();
            String newZoneIdText = zoneIdField.getText();

            if (newName.isEmpty() || newPrenom.isEmpty() || newEmail.isEmpty() || newPassword.isEmpty() || newZoneIdText.isEmpty()) {
                showAlert("Erreur", "Tous les champs doivent être remplis.");
                return;
            }

            // Validation de l'email
            if (!newEmail.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
                showAlert("Erreur", "L'email doit être valide.");
                return;
            }
            if (!newEmail.equals(existingUser.getEmail()) && emailExists(newEmail)) {
                showAlert("Erreur", "L'email est déjà utilisé.");
                return;
            }

            // Validation du mot de passe
            if (newPassword.length() < 8 || !newPassword.matches(".*\\d.*") || !newPassword.matches(".*[A-Z].*")) {
                showAlert("Erreur", "Le mot de passe doit comporter au moins 8 caractères, un chiffre et une lettre majuscule.");
                return;
            }

            // Validation du Zone ID
            int newZoneId;
            try {
                newZoneId = Integer.parseInt(newZoneIdText);
            } catch (NumberFormatException e) {
                showAlert("Erreur", "Le Zone ID doit être un nombre entier.");
                return;
            }

            // Mise à jour des champs dans le service
            serviceUtilisateur.updateField(userId, "nom", newName);
            serviceUtilisateur.updateField(userId, "prenom", newPrenom);
            serviceUtilisateur.updateField(userId, "email", newEmail);
            serviceUtilisateur.updateField(userId, "motdepasse", newPassword);
            serviceUtilisateur.updateField(userId, "zoneId", String.valueOf(newZoneId));

            // Recharger les utilisateurs pour refléter la modification
            loadUsers();

            // Réinitialiser les champs
            clearFields();

            // Confirmation de la modification
            showAlert("Succès", "Citoyen modifié avec succès.");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Une erreur est survenue lors de la modification.");
        }
    }
    // Fonction pour réinitialiser les champs du formulaire
    private void clearFields() {
        idField.clear();
        nameField.clear();
        prenomField.clear();
        emailField.clear();
        passwordField.clear();
        zoneIdField.clear();

    }



    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
    @FXML
    private void handleDeleteCitoyen() {
        // Créer un formulaire pour entrer l'ID du citoyen
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Supprimer Citoyen");
        dialog.setHeaderText("Supprimer un citoyen par son ID");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        TextField citoyenIdField = new TextField();
        grid.addRow(0, new Label("ID Citoyen :"), citoyenIdField);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(button -> {
            if (button == ButtonType.OK) {
                try {
                    int id = Integer.parseInt(citoyenIdField.getText());
                    citoyen citizen = serviceCitoyen.getCitoyenById(id);

                    if (citizen != null) {
                        // Demande de confirmation
                        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
                        confirmation.setTitle("Confirmation");
                        confirmation.setHeaderText("Voulez-vous vraiment supprimer le citoyen : " +
                                citizen.getNom() + " " + citizen.getPrenom() + "?");

                        Optional<ButtonType> result = confirmation.showAndWait();
                        if (result.isPresent() && result.get() == ButtonType.OK) {
                            serviceCitoyen.deleteById(id);
                            showAlert("Succès", "Citoyen supprimé avec succès !");
                            loadUsers(); // Recharger la liste après suppression
                        }
                    } else {
                        showAlert("Erreur", "Citoyen avec l'ID " + id + " introuvable.");
                    }
                } catch (NumberFormatException e) {
                    showAlert("Erreur", "Veuillez entrer un ID valide.");
                }
            }
            return null;
        });

        dialog.showAndWait();
    }



}