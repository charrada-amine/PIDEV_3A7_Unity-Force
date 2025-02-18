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

import tn.esprit.models.utilisateur;
import tn.esprit.services.ServiceTechnicien;
import tn.esprit.services.ServiceUtilisateur;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class GestionTechnicienController {

    @FXML
    private FlowPane technicienFlowPane; // Correspond à 'fx:id="technicienFlowPane"' dans le FXML
    private final ServiceUtilisateur serviceUtilisateur = new ServiceUtilisateur();
    private final ServiceTechnicien serviceTechnicien = new ServiceTechnicien();
    @FXML

    private ComboBox<String> specialiteCombo = new ComboBox<>();


    @FXML
    private void initialize() {
        if (technicienFlowPane == null) {
            System.err.println("Erreur : 'technicienFlowPane' est nul. Vérifiez le fichier FXML.");
        } else {
            System.out.println("FlowPane 'technicienFlowPane' initialisé correctement.");
            loadUsers(); // Charger les techniciens
        }
            if (specialiteCombo != null) {
                // Initialiser le ComboBox avec les noms des valeurs de l'énumération Specialite
                specialiteCombo.setItems(FXCollections.observableArrayList(
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
        TextField idField = new TextField(String.valueOf(technicien.getId_utilisateur())); // ID converti en texte
        idField.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cccccc; -fx-padding: 5;");
        idField.setEditable(false); // ID ne doit pas être modifiable

        // Champ Nom (non modifiable)
        Label nameLabel = new Label("Nom:");
        nameLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: black;");
        TextField nameField = new TextField(technicien.getNom());
        nameField.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cccccc; -fx-padding: 5;");
        nameField.setEditable(false); // Non modifiable

        // Champ Prénom (non modifiable)
        Label prenomLabel = new Label("Prénom:");
        prenomLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: black;");
        TextField prenomField = new TextField(technicien.getPrenom());
        prenomField.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cccccc; -fx-padding: 5;");
        prenomField.setEditable(false); // Non modifiable

        // Champ Email (non modifiable)
        Label emailLabel = new Label("Email:");
        emailLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: black;");
        TextField emailField = new TextField(technicien.getEmail());
        emailField.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cccccc; -fx-padding: 5;");
        emailField.setEditable(false); // Non modifiable

        // Champ Mot de passe (non modifiable)
        Label passwordLabel = new Label("Mot de passe:");
        passwordLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: black;");
        TextField passwordField = new TextField(technicien.getMotdepasse());
        passwordField.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cccccc; -fx-padding: 5;");
        passwordField.setEditable(false); // Non modifiable

        // Champ Spécialité (non modifiable)
        Label specialiteLabel = new Label("Spécialité:");
        specialiteLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: black;");
        TextField specialiteField = new TextField(technicien.getSpecialite().name());
        specialiteField.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cccccc; -fx-padding: 5;");
        specialiteField.setEditable(false); // Non modifiable

        // Champ Date d'inscription (non modifiable)
        Label dateInscriptionLabel = new Label("Date d'inscription:");
        dateInscriptionLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: black;");
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        TextField dateField = new TextField(sdf.format(technicien.getDateinscription()));
        dateField.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cccccc; -fx-padding: 5;");
        dateField.setEditable(false); // Non modifiable

        // Ajouter les éléments à la carte
        card.getChildren().addAll(
                idLabel, idField,
                nameLabel, nameField,
                prenomLabel, prenomField,
                emailLabel, emailField,
                passwordLabel, passwordField,
                specialiteLabel, specialiteField,
                dateInscriptionLabel, dateField
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


    private technicien getSelectedTechnicien() {
        // TODO: Implémentez la logique pour récupérer l'élément sélectionné.
        // Par exemple, vous pouvez avoir une liste ou une table de citoyens.
        return null; // Remplacez `null` par la sélection réelle.
    }
    @FXML
    private void handleUpdateTechnicien() {
        // Créer un GridPane pour le formulaire de modification
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        // Champ pour entrer l'ID du technicien
        TextField technicienIdField = new TextField();
        grid.addRow(0, new Label("Entrez l'ID du technicien:"), technicienIdField);

        // Champ pour sélectionner quel champ modifier
        ComboBox<String> fieldSelectionCombo = new ComboBox<>();
        fieldSelectionCombo.getItems().add(""); // Option vide par défaut
        fieldSelectionCombo.getItems().addAll("Nom", "Prénom", "Email", "Mot de passe", "Spécialité");
        fieldSelectionCombo.getSelectionModel().selectFirst(); // Sélectionne l'option vide par défaut

        // Champs pour les différentes modifications
        TextField nomField = new TextField();
        TextField prenomField = new TextField();
        TextField emailField = new TextField();
        TextField mdpField = new TextField();
        ComboBox<String> specialiteCombo = new ComboBox<>(); // Spécialité sous forme de ComboBox
        specialiteCombo.getItems().addAll("maintenance", "electricite", "autre");

        // Ajout des champs au GridPane
        grid.addRow(1, new Label("Sélectionnez le champ à modifier:"), fieldSelectionCombo);
        grid.addRow(2, new Label("Nom:"), nomField);
        grid.addRow(3, new Label("Prénom:"), prenomField);
        grid.addRow(4, new Label("Email:"), emailField);
        grid.addRow(5, new Label("Mot de passe:"), mdpField);
        grid.addRow(6, new Label("Spécialité:"), specialiteCombo);

        // Masquer tous les champs de modification par défaut
        nomField.setVisible(false);
        prenomField.setVisible(false);
        emailField.setVisible(false);
        mdpField.setVisible(false);
        specialiteCombo.setVisible(false);

        // Création des boutons OK et Annuler
        ButtonType okButton = ButtonType.OK;
        ButtonType cancelButton = ButtonType.CANCEL;

        // Création du dialogue
        Dialog<utilisateur> dialog = new Dialog<>();
        dialog.setTitle("Modifier Technicien");
        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(okButton, cancelButton);

        // Listener sur le ComboBox pour afficher uniquement le champ sélectionné
        fieldSelectionCombo.valueProperty().addListener((observable, oldValue, newValue) -> {
            // Masquer tous les champs
            nomField.setVisible(false);
            prenomField.setVisible(false);
            emailField.setVisible(false);
            mdpField.setVisible(false);
            specialiteCombo.setVisible(false);

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
                        mdpField.setVisible(true);
                        break;
                    case "Spécialité":
                        specialiteCombo.setVisible(true);
                        break;
                }
            }
        });

        // Gérer le résultat du dialogue
        dialog.setResultConverter(button -> {
            if (button == okButton) {
                // Récupérer l'ID du technicien et le valider
                String technicienIdText = technicienIdField.getText();
                if (technicienIdText.isEmpty()) {
                    showAlert("Erreur", "L'ID du technicien ne peut pas être vide.");
                    return null;
                }

                int technicienId;
                try {
                    technicienId = Integer.parseInt(technicienIdText);
                } catch (NumberFormatException e) {
                    showAlert("Erreur", "L'ID doit être un nombre entier.");
                    return null;
                }

                // Vérifier si le technicien existe
                utilisateur selectedUser = serviceUtilisateur.getUtilisateurById(technicienId);
                if (selectedUser == null) {
                    showAlert("Erreur", "Aucun technicien trouvé avec cet ID.");
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
                    case "Spécialité":
                        try {
                            // Récupérer la spécialité sélectionnée dans le ComboBox
                            String selectedSpecialiteString = specialiteCombo.getValue();
                            if (selectedSpecialiteString != null && !selectedSpecialiteString.trim().isEmpty()) {
                                // Convertir le String en Specialite (Enum)
                                Specialite selectedSpecialite = Specialite.valueOf(selectedSpecialiteString);

                                // Mettre à jour la base de données avec la spécialité sous forme de String
                                serviceUtilisateur.updateField(
                                        selectedUser.getId_utilisateur(),
                                        "specialite",
                                        selectedSpecialite.name()  // Convertir l'Enum en String pour la base de données
                                );


                        } else {
                                showAlert("Erreur", "Veuillez sélectionner une spécialité valide.");
                            }

                            System.out.println("✅ Spécialité mise à jour avec succès pour l'utilisateur ID : " + selectedUser.getId_utilisateur());
                        } catch (Exception e) {
                            showAlert("Erreur", "La mise à jour de la spécialité a échoué.");
                            e.printStackTrace();
                        }
                        break;




                    default:
                        showAlert("Erreur", "Aucun champ sélectionné pour la modification.");
                        return null;
                }

                // Recharger la liste après modification
                loadUsers();
            }
            return null;
        });

        dialog.showAndWait();
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
