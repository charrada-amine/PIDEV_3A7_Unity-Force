package tn.esprit.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import tn.esprit.models.citoyen;
import tn.esprit.models.utilisateur;
import java.util.Optional;

import tn.esprit.services.ServiceCitoyen;
import tn.esprit.services.ServiceUtilisateur;

import java.text.SimpleDateFormat;
import java.util.List;

public class GestionCitoyenController {

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

    private VBox createUserCard(citoyen citoyen) {
        VBox card = new VBox(10);
        card.setStyle("-fx-border-color: #ddd; -fx-border-width: 1; -fx-padding: 10; -fx-background-color: #f9f9f9; -fx-alignment: center;");

        // Champ ID
        Label idLabel = new Label("ID:");
        idLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: black;");
        TextField idField = new TextField(String.valueOf(citoyen.getId_utilisateur())); // ID converti en texte
        idField.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cccccc; -fx-padding: 5;");
        idField.setEditable(false); // ID ne doit pas être modifiable

        // Champ Nom
        Label nameLabel = new Label("Nom:");
        nameLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: black;");
        TextField nameField = new TextField(citoyen.getNom());
        nameField.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cccccc; -fx-padding: 5;");

        // Champ Prénom
        Label prenomLabel = new Label("Prénom:");
        prenomLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: black;");
        TextField prenomField = new TextField(citoyen.getPrenom());
        prenomField.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cccccc; -fx-padding: 5;");

        // Champ Email
        Label emailLabel = new Label("Email:");
        emailLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: black;");
        TextField emailField = new TextField(citoyen.getEmail());
        emailField.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cccccc; -fx-padding: 5;");

        // Champ Mot de passe (statique)
        Label passwordLabel = new Label("Mot de passe:");
        passwordLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: black;");
        TextField passwordField = new TextField(citoyen.getMotdepasse());
        passwordField.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cccccc; -fx-padding: 5;");

        // Champ Rôle
        Label roleLabel = new Label("Rôle:");
        roleLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: black;");
        TextField roleField = new TextField(citoyen.getRole().name()); // Convertir le rôle en texte
        roleField.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cccccc; -fx-padding: 5;");
        roleField.setEditable(false); // Lecture seule

        // Champ Date d'inscription
        Label dateInscriptionLabel = new Label("Date d'inscription:");
        dateInscriptionLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: black;");
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        TextField dateField = new TextField(sdf.format(citoyen.getDateinscription()));
        dateField.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cccccc; -fx-padding: 5;");
        dateField.setEditable(false);

        // Champ Zone ID
        Label zoneIdLabel = new Label("Zone ID:");
        zoneIdLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: black;");
        TextField zoneIdField = new TextField(String.valueOf(citoyen.getZoneId())); // Convertir l'entier Zone ID en texte
        zoneIdField.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cccccc; -fx-padding: 5;");
        zoneIdField.setEditable(false);

        // Ajouter les éléments à la carte
        card.getChildren().addAll(
                idLabel, idField,
                nameLabel, nameField,
                prenomLabel, prenomField,
                emailLabel, emailField,
                passwordLabel, passwordField,
                roleLabel, roleField,
                dateInscriptionLabel, dateField,
                zoneIdLabel, zoneIdField
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
    @FXML
    private void handleUpdateCitoyen() {
        // Créer un GridPane pour le formulaire de modification
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        // Champ pour entrer l'ID de l'utilisateur/citoyen
        TextField userIdField = new TextField();
        grid.addRow(0, new Label("Entrez l'ID de l'utilisateur/citoyen:"), userIdField);

        // Champ pour sélectionner quel champ modifier
        ComboBox<String> fieldSelectionCombo = new ComboBox<>();
        fieldSelectionCombo.getItems().add(""); // Option vide par défaut
        fieldSelectionCombo.getItems().addAll("Nom", "Prénom", "Email", "Mot de passe", "Zone ID");
        fieldSelectionCombo.getSelectionModel().selectFirst(); // Sélectionne l'option vide par défaut

        // Champs pour les différentes modifications
        TextField nomField = new TextField();
        TextField prenomField = new TextField();
        TextField emailField = new TextField();
        TextField mdpField = new TextField();
        TextField zoneIdField = new TextField(); // Nouveau champ pour Zone ID

        // Ajout des champs au GridPane
        grid.addRow(1, new Label("Sélectionnez le champ à modifier:"), fieldSelectionCombo);
        grid.addRow(2, new Label("Nom:"), nomField);
        grid.addRow(3, new Label("Prénom:"), prenomField);
        grid.addRow(4, new Label("Email:"), emailField);
        grid.addRow(5, new Label("Mot de passe:"), mdpField);
        grid.addRow(6, new Label("Zone ID:"), zoneIdField); // Ajout du champ Zone ID

        // Masquer tous les champs de modification par défaut
        nomField.setVisible(false);
        prenomField.setVisible(false);
        emailField.setVisible(false);
        mdpField.setVisible(false);
        zoneIdField.setVisible(false);

        // Création des boutons OK et Annuler
        ButtonType okButton = ButtonType.OK;
        ButtonType cancelButton = ButtonType.CANCEL;

        // Création du dialogue
        Dialog<utilisateur> dialog = new Dialog<>();
        dialog.setTitle("Modifier Utilisateur / Citoyen");
        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(okButton, cancelButton);

        // Listener sur le ComboBox pour afficher uniquement le champ sélectionné
        fieldSelectionCombo.valueProperty().addListener((observable, oldValue, newValue) -> {
            // Masquer tous les champs
            nomField.setVisible(false);
            prenomField.setVisible(false);
            emailField.setVisible(false);
            mdpField.setVisible(false);
            zoneIdField.setVisible(false);

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
                    case "Zone ID":
                        zoneIdField.setVisible(true);
                        break;
                }
            }
        });

        // Gérer le résultat du dialogue
        dialog.setResultConverter(button -> {
            if (button == okButton) {
                // Récupérer l'ID utilisateur/citoyen et le valider
                String userIdText = userIdField.getText();
                if (userIdText.isEmpty()) {
                    showAlert("Erreur", "L'ID de l'utilisateur/citoyen ne peut pas être vide.");
                    return null;
                }

                int userId;
                try {
                    userId = Integer.parseInt(userIdText);
                } catch (NumberFormatException e) {
                    showAlert("Erreur", "L'ID doit être un nombre entier.");
                    return null;
                }

                // Vérifier si c'est un utilisateur ou un citoyen
                utilisateur selectedUser = serviceUtilisateur.getUtilisateurById(userId);
                if (selectedUser == null) {
                    showAlert("Erreur", "Aucun utilisateur/citoyen trouvé avec cet ID.");
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
                    case "Zone ID":
                        try {
                            int newZoneId = Integer.parseInt(zoneIdField.getText()); // Conversion en entier
                            serviceUtilisateur.updateField(selectedUser.getId_utilisateur(), "zoneId", String.valueOf(newZoneId));
                        } catch (NumberFormatException e) {
                            showAlert("Erreur", "Le Zone ID doit être un nombre entier.");
                            return null;
                        }
                        break;
                    default:
                        showAlert("Erreur", "Aucun champ sélectionné pour la modification.");
                        return null;
                }

                loadUsers(); // Recharger la liste après modification
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


