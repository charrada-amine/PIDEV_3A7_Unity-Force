package tn.esprit.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import tn.esprit.models.profile;
import tn.esprit.services.ServiceProfile;
import tn.esprit.services.ServiceSource;

import java.io.IOException;
import java.util.List;

public class ProfileController {

    private final ServiceProfile serviceProfile = new ServiceProfile();
    private final ServiceSource serviceSource = new ServiceSource();

    // Composants pour l'affichage des profils
    @FXML
    private FlowPane flowPaneProfiles;

    @FXML
    private Button btnAddProfile;

    @FXML
    private Button btnBack;

    // Composants pour l'ajout/modification de profil
    @FXML
    private TextField txtConsommationJour;

    @FXML
    private TextField txtConsommationMois;

    @FXML
    private TextField txtCoutEstime;

    @FXML
    private TextField txtDureeActivite;

    @FXML
    private TextField txtLampadaireId;

    @FXML
    private ComboBox<Integer> comboBoxSourceId;

    @FXML
    private Button btnAdd;

    @FXML
    private Button btnCancel;

    @FXML
    private void initialize() {
        // Initialisation des IDs des sources dans le ComboBox
        List<Integer> sourceIds = serviceSource.getAllSourceIds();
        comboBoxSourceId.getItems().addAll(sourceIds);

        // Affichage des profils existants
        afficherProfiles();
    }

    // Méthode pour afficher les profils
    private void afficherProfiles() {
        List<profile> profiles = serviceProfile.getAll();
        flowPaneProfiles.getChildren().clear();

        for (profile profil : profiles) {
            VBox card = new VBox();
            card.setSpacing(10);
            card.setAlignment(Pos.CENTER);
            card.setPadding(new Insets(15));
            card.setPrefWidth(220);
            card.setStyle(
                    "-fx-border-color: #333;" +
                            "-fx-border-width: 2;" +
                            "-fx-border-radius: 15;" +
                            "-fx-background-color: linear-gradient(to bottom, #ffffff, #e6e6e6);" +
                            "-fx-background-radius: 15;" +
                            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 8, 0.5, 0, 3);"
            );

            // Labels d'information
            Label labelId = new Label("🆔 ID: " + profil.getIdprofile());
            Label labelConsommationJour = new Label("💡 Consommation Jour: " + profil.getConsommationJour() + " kWh");
            Label labelConsommationMois = new Label("💡 Consommation Mois: " + profil.getConsommationMois() + " kWh");
            Label labelCoutEstime = new Label("💰 Coût Estimé: " + profil.getCoutEstime() + " €");
            Label labelDureeActivite = new Label("⏳ Durée Activité: " + profil.getDureeActivite() + " heures");
            Label labelSourceId = new Label("🔌 Source ID: " + profil.getSourceId());
            Label labelLampadaireId = new Label("💡 Lampadaire ID: " + profil.getLampadaireId());

            // Appliquer du style aux labels
            labelSourceId.setStyle("-fx-font-size: 13px;");
            labelLampadaireId.setStyle("-fx-font-size: 13px;");
            labelConsommationJour.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
            labelConsommationMois.setStyle("-fx-font-size: 13px;");
            labelCoutEstime.setStyle("-fx-font-size: 13px;");
            labelDureeActivite.setStyle("-fx-font-size: 13px;");

            // Bouton Modifier avec style
            Button btnUpdate = new Button("✏ Modifier");
            btnUpdate.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-size: 13px; -fx-background-radius: 10;");
            btnUpdate.setOnAction(event -> prefillForm(profil)); // Pré-remplir le formulaire

            // Bouton Supprimer avec style
            Button btnDelete = new Button("🗑 Supprimer");
            btnDelete.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-size: 13px; -fx-background-radius: 10;");
            btnDelete.setOnAction(event -> {
                serviceProfile.delete(profil.getIdprofile());
                afficherProfiles();
            });

            // Ajout des éléments à la carte
            card.getChildren().addAll(labelId, labelSourceId, labelLampadaireId, labelConsommationJour, labelConsommationMois, labelCoutEstime, labelDureeActivite, btnUpdate, btnDelete);

            // Ajout de la carte dans le FlowPane
            flowPaneProfiles.getChildren().add(card);
        }
    }

    // Méthode pour pré-remplir les champs du formulaire
    private void prefillForm(profile profil) {
        txtConsommationJour.setText(profil.getConsommationJour());
        txtConsommationMois.setText(profil.getConsommationMois());
        txtCoutEstime.setText(profil.getCoutEstime());
        txtDureeActivite.setText(profil.getDureeActivite());
        txtLampadaireId.setText(String.valueOf(profil.getLampadaireId()));
        comboBoxSourceId.setValue(profil.getSourceId());

        // Changer le texte du bouton "Ajouter" en "Modifier"
        btnAdd.setText("Modifier");

        // Stocker l'ID du profil à modifier dans le bouton
        btnAdd.setUserData(profil.getIdprofile());

        // Changer l'action du bouton pour appeler handleModify au lieu de handleAdd
        btnAdd.setOnAction(event -> handleModify());
    }

    // Méthode pour ajouter un profil
    @FXML
    private void handleAdd() {
        try {
            String consommationJour = txtConsommationJour.getText();
            String consommationMois = txtConsommationMois.getText();
            String coutEstime = txtCoutEstime.getText();
            String dureeActivite = txtDureeActivite.getText();
            int lampadaireId = Integer.parseInt(txtLampadaireId.getText());

            if (consommationJour.isEmpty() || consommationMois.isEmpty() || coutEstime.isEmpty() || dureeActivite.isEmpty()) {
                showAlert("❌ Veuillez remplir tous les champs.");
                return;
            }

            // Récupérer l'ID de source sélectionné dans le ComboBox
            Integer selectedSourceId = comboBoxSourceId.getValue();

            if (selectedSourceId == null) {
                showAlert("❌ Veuillez sélectionner une source.");
                return;
            }

            // Créer le profil avec les informations récupérées
            profile profile = new profile(consommationJour, consommationMois, coutEstime, dureeActivite, selectedSourceId, lampadaireId);
            serviceProfile.add(profile);

            showAlert("✅ Profil ajouté avec succès !");
            afficherProfiles(); // Rafraîchir l'affichage des profils
        } catch (NumberFormatException e) {
            showAlert("❌ Veuillez entrer des valeurs valides pour les IDs.");
        }
    }

    // Méthode pour modifier un profil
    private void handleModify() {
        try {
            // Récupérer l'ID du profil à modifier
            int profileId = (int) btnAdd.getUserData();

            // Récupérer les nouvelles valeurs du formulaire
            String consommationJour = txtConsommationJour.getText();
            String consommationMois = txtConsommationMois.getText();
            String coutEstime = txtCoutEstime.getText();
            String dureeActivite = txtDureeActivite.getText();
            int lampadaireId = Integer.parseInt(txtLampadaireId.getText());
            Integer selectedSourceId = comboBoxSourceId.getValue();

            if (consommationJour.isEmpty() || consommationMois.isEmpty() || coutEstime.isEmpty() || dureeActivite.isEmpty() || selectedSourceId == null) {
                showAlert("❌ Veuillez remplir tous les champs.");
                return;
            }

            // Créer un objet profil avec les nouvelles valeurs
            profile updatedProfile = new profile(profileId, consommationJour, consommationMois, coutEstime, dureeActivite, selectedSourceId, lampadaireId);

            // Mettre à jour le profil dans la base de données
            serviceProfile.update(updatedProfile);

            showAlert("✅ Profil modifié avec succès !");
            afficherProfiles(); // Rafraîchir l'affichage des profils

            // Réinitialiser le bouton "Modifier" à "Ajouter"
            btnAdd.setText("Ajouter");
            btnAdd.setOnAction(event -> handleAdd());
        } catch (NumberFormatException e) {
            showAlert("❌ Veuillez entrer des valeurs valides pour les IDs.");
        }
    }

    // Méthode pour annuler l'ajout/modification
    @FXML
    private void handleCancel() {
        switchScene("/ProfileInterface.fxml", btnCancel);
    }

    // Méthode pour revenir au menu principal
    @FXML
    private void handleBack() {
        switchScene("/Menu.fxml", btnBack);
    }

    // Méthode pour changer de scène
    private void switchScene(String fxmlPath, Button button) {
        try {
            // Récupère la fenêtre actuelle
            Stage stage = (Stage) button.getScene().getWindow();

            // Sauvegarde la taille actuelle
            double currentWidth = stage.getWidth();
            double currentHeight = stage.getHeight();

            // Charge la nouvelle scène
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            // Crée une nouvelle scène avec la taille actuelle
            Scene scene = new Scene(root, currentWidth, currentHeight);

            // Applique la scène et fixe la taille
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("❌ Impossible de charger la vue demandée.");
        }
    }

    // Méthode pour afficher une alerte
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}