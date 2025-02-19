package tn.esprit.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import tn.esprit.models.citoyen;
import tn.esprit.models.technicien;
import tn.esprit.models.Specialite;
import tn.esprit.models.Specialite;
import javafx.geometry.Pos;

import tn.esprit.models.utilisateur;
import tn.esprit.services.ServiceTechnicien;
import tn.esprit.services.ServiceUtilisateur;
import tn.esprit.utils.MyDatabase;
import tn.esprit.models.Role; // Adaptez le chemin selon votre projet.

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class GestionTechnicienController {

    @FXML
    private TextField idField, nameField, prenomField, emailField, passwordField;
    @FXML
    private ComboBox<String> specialiteComboBox;
    @FXML

    private FlowPane technicienFlowPane; // Correspond à 'fx:id="technicienFlowPane"' dans le FXML
    private final ServiceUtilisateur serviceUtilisateur = new ServiceUtilisateur();
    private final ServiceTechnicien serviceTechnicien = new ServiceTechnicien();
    @FXML



    private void initialize() {
        if (technicienFlowPane == null) {
            System.err.println("Erreur : 'technicienFlowPane' est nul. Vérifiez le fichier FXML.");
        } else {
            System.out.println("FlowPane 'technicienFlowPane' initialisé correctement.");
            loadUsers(); // Charger les techniciens
        }
            if (specialiteComboBox != null) {
                // Initialiser le ComboBox avec les noms des valeurs de l'énumération Specialite
                specialiteComboBox.setItems(FXCollections.observableArrayList(
                        Arrays.stream(Specialite.values())
                                .map(Specialite::name) // Récupérer le nom des spécialités sous forme de String
                                .collect(Collectors.toList())
                ));
            } else {
                System.err.println("Erreur : 'specialiteCombo' est nul.");
            }


    }

    private VBox createTechnicienCard(technicien technicien) {
        VBox card = new VBox(10);
        card.setStyle("-fx-border-color: #ddd; -fx-border-width: 1; -fx-padding: 10; -fx-background-color: #f9f9f9; -fx-alignment: center;");

        // Champ ID (non modifiable)
        Label idLabel = new Label("ID:");
        idLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: black;");
        TextField idField = new TextField(String.valueOf(technicien.getId_utilisateur()));
        idField.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cccccc; -fx-padding: 5;");
        idField.setEditable(false); // Non modifiable
        HBox idBox = new HBox(10, idLabel, idField); // Aligner le label et le champ ID
        idBox.setAlignment(Pos.CENTER_LEFT); // Aligner à gauche

        // Champ Nom (non modifiable)
        Label nameLabel = new Label("Nom:");
        nameLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: black;");
        TextField nameField = new TextField(technicien.getNom());
        nameField.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cccccc; -fx-padding: 5;");
        nameField.setEditable(false); // Non modifiable
        HBox nameBox = new HBox(10, nameLabel, nameField); // Aligner le label et le champ Nom
        nameBox.setAlignment(Pos.CENTER_LEFT);

        // Champ Prénom (non modifiable)
        Label prenomLabel = new Label("Prénom:");
        prenomLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: black;");
        TextField prenomField = new TextField(technicien.getPrenom());
        prenomField.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cccccc; -fx-padding: 5;");
        prenomField.setEditable(false); // Non modifiable
        HBox prenomBox = new HBox(10, prenomLabel, prenomField); // Aligner le label et le champ Prénom
        prenomBox.setAlignment(Pos.CENTER_LEFT);

        // Champ Email (non modifiable)
        Label emailLabel = new Label("Email:");
        emailLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: black;");
        TextField emailField = new TextField(technicien.getEmail());
        emailField.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cccccc; -fx-padding: 5;");
        emailField.setEditable(false); // Non modifiable
        HBox emailBox = new HBox(10, emailLabel, emailField); // Aligner le label et le champ Email
        emailBox.setAlignment(Pos.CENTER_LEFT);

        // Champ Mot de passe (non modifiable)
        Label passwordLabel = new Label("Mot de passe:");
        passwordLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: black;");
        TextField passwordField = new TextField(technicien.getMotdepasse());
        passwordField.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cccccc; -fx-padding: 5;");
        passwordField.setEditable(false); // Non modifiable
        HBox passwordBox = new HBox(10, passwordLabel, passwordField); // Aligner le label et le champ Mot de passe
        passwordBox.setAlignment(Pos.CENTER_LEFT);



        // Champ Date d'inscription (non modifiable)
        Label dateInscriptionLabel = new Label("Date d'inscription:");
        dateInscriptionLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: black;");
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        TextField dateField = new TextField(sdf.format(technicien.getDateinscription()));
        dateField.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cccccc; -fx-padding: 5;");
        dateField.setEditable(false); // Non modifiable
        HBox dateInscriptionBox = new HBox(10, dateInscriptionLabel, dateField); // Aligner le label et le champ Date d'inscription
        dateInscriptionBox.setAlignment(Pos.CENTER_LEFT);

        // Champ Spécialité (non modifiable)
        Label specialiteLabel = new Label("Spécialité:");
        specialiteLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: black;");
        TextField specialiteField = new TextField(technicien.getSpecialite().name());
        specialiteField.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cccccc; -fx-padding: 5;");
        specialiteField.setEditable(false); // Non modifiable
        HBox specialiteBox = new HBox(10, specialiteLabel, specialiteField); // Aligner le label et le champ Spécialité
        specialiteBox.setAlignment(Pos.CENTER_LEFT);

        // Ajouter les HBoxes à la carte
        card.getChildren().addAll(
                idBox,
                nameBox,
                prenomBox,
                emailBox,
                passwordBox,
                dateInscriptionBox,
                specialiteBox
        );

        return card;
    }


    @FXML
    private void loadUsers() {
        if (technicienFlowPane == null) {
            System.err.println("Erreur : 'technicienFlowPane' est nul lors de l'appel de 'loadTechniciens'.");
            return;
        }

        ServiceTechnicien serviceTechnicien = new ServiceTechnicien();
        List<technicien> techniciens;

        try {
            techniciens = serviceTechnicien.getAllTechniciens();
        } catch (Exception e) {
            System.err.println("Erreur lors de la récupération des techniciens : " + e.getMessage());
            e.printStackTrace();
            return;
        }

        if (techniciens == null || techniciens.isEmpty()) {
            System.out.println("Aucun technicien trouvé.");
            technicienFlowPane.getChildren().clear();
            return;
        }

        ObservableList<VBox> technicienCards = FXCollections.observableArrayList();

        for (technicien t : techniciens) {
            VBox technicienCard = createTechnicienCard(t);
            technicienCards.add(technicienCard);
        }

        technicienFlowPane.getChildren().clear();
        technicienFlowPane.getChildren().addAll(technicienCards);
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


    private technicien getSelectedTechnicien() {
        // TODO: Implémentez la logique pour récupérer l'élément sélectionné.
        // Par exemple, vous pouvez avoir une liste ou une table de citoyens.
        return null; // Remplacez `null` par la sélection réelle.
    }
    @FXML
    private void handleUpdateTechnicien() {
        try {
            // Récupérer l'ID du technicien
            String userIdText = idField.getText();
            if (userIdText.isEmpty()) {
                showAlert("Erreur", "L'ID de l'utilisateur/technicien ne peut pas être vide.");
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

            // Validation de la spécialité
            String newSpecialite = specialiteComboBox.getValue();
            if (newSpecialite == null || newSpecialite.trim().isEmpty()) {
                showAlert("Erreur", "La spécialité doit être sélectionnée.");
                return;
            }

            // Mise à jour des champs dans le service
            serviceUtilisateur.updateFieldTechnicien(userId, "nom", newName);
            serviceUtilisateur.updateFieldTechnicien(userId, "prenom", newPrenom);
            serviceUtilisateur.updateFieldTechnicien(userId, "email", newEmail);
            serviceUtilisateur.updateFieldTechnicien(userId, "motdepasse", newPassword);
            serviceUtilisateur.updateFieldTechnicien(userId, "specialite", newSpecialite);

            // Recharger les utilisateurs pour refléter la modification
            loadUsers();

            // Réinitialiser les champs
            clearFields();

            // Confirmation de la modification
            showAlert("Succès", "Technicien modifié avec succès.");
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
        specialiteComboBox.setValue(null); // Réinitialiser le ComboBox
    }

    @FXML
    private void handleDeleteTechnicien() {
        // Créer un formulaire pour entrer l'ID du technicien
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Supprimer Technicien");
        dialog.setHeaderText("Supprimer un technicien par son ID");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        TextField technicienIdField = new TextField();
        grid.addRow(0, new Label("ID Technicien :"), technicienIdField);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(button -> {
            if (button == ButtonType.OK) {
                try {
                    int id = Integer.parseInt(technicienIdField.getText());
                    technicien technician = serviceTechnicien.getTechnicienById(id);

                    if (technician != null) {
                        // Demande de confirmation
                        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
                        confirmation.setTitle("Confirmation");
                        confirmation.setHeaderText("Voulez-vous vraiment supprimer le technicien : " +
                                technician.getNom() + " " + technician.getPrenom() + "?");

                        Optional<ButtonType> result = confirmation.showAndWait();
                        if (result.isPresent() && result.get() == ButtonType.OK) {
                            serviceTechnicien.deleteById(id);
                            showAlert("Succès", "Technicien supprimé avec succès !");
                            loadUsers(); // Recharger la liste après suppression
                        }
                    } else {
                        showAlert("Erreur", "Technicien avec l'ID " + id + " introuvable.");
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
