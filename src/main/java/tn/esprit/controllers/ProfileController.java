package tn.esprit.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import tn.esprit.models.profile;
import tn.esprit.services.ServiceProfile;
import tn.esprit.services.ServiceSource;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ProfileController implements Initializable {

    private final ServiceProfile serviceProfile = new ServiceProfile();
    private final ServiceSource serviceSource = new ServiceSource();

    @FXML
    private FlowPane flowPaneProfiles;

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
    private Button btnsource;


    @FXML
    private Button btnBack;

    private profile selectedProfile;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialisation des IDs des sources dans le ComboBox
        List<Integer> sourceIds = serviceSource.getAllSourceIds();
        comboBoxSourceId.getItems().addAll(sourceIds);

        // Affichage des profils existants
        loadData();
    }

    private void loadData() {
        List<profile> profiles = serviceProfile.getAll();
        flowPaneProfiles.getChildren().clear();
        profiles.forEach(profile -> flowPaneProfiles.getChildren().add(createProfileCard(profile)));
    }

    private VBox createProfileCard(profile profile) {
        VBox card = new VBox(15);
        card.getStyleClass().add("card");

        // En-tête avec icône
        HBox header = new HBox(10);
        Label title = new Label("Profil #" + profile.getIdprofile());
        title.setStyle("-fx-font-size: 18; -fx-text-fill: #202124;");

        header.getChildren().addAll(title);

        // Contenu
        VBox content = new VBox(8);
        content.getChildren().addAll(
                createInfoRow("Consommation Jour: " + profile.getConsommationJour() + " kWh"),
                createInfoRow("Consommation Mois: " + profile.getConsommationMois() + " kWh"),
                createInfoRow("Coût Estimé: " + profile.getCoutEstime() + " €"),
                createInfoRow("Durée Activité: " + profile.getDureeActivite() + " heures"),
                createInfoRow("Lampadaire ID: " + profile.getLampadaireId()),
                createInfoRow("Source ID: " + profile.getSourceId())
        );

        // Boutons d'action
        HBox buttons = new HBox(10);

        // Bouton Modifier
        Button btnModifier = createIconButton("Modifier", "-secondary");
        btnModifier.setOnAction(e -> fillForm(profile));

        // Bouton Supprimer
        Button btnSupprimer = createIconButton("Supprimer", "#ea4335");
        btnSupprimer.setOnAction(e -> handleDeleteProfile(profile));

        buttons.getChildren().addAll(btnModifier, btnSupprimer);

        card.getChildren().addAll(header, new Separator(), content, buttons);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-padding: 16;");
        card.setEffect(new DropShadow(10, Color.gray(0.3)));

        return card;
    }

    private HBox createInfoRow(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-text-fill: #5f6368;");
        return new HBox(10, label);
    }

    private Button createIconButton(String text, String color) {
        Button button = new Button(text);
        button.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white;");
        return button;
    }

    private void fillForm(profile profile) {
        selectedProfile = profile;
        txtConsommationJour.setText(profile.getConsommationJour());
        txtConsommationMois.setText(profile.getConsommationMois());
        txtCoutEstime.setText(profile.getCoutEstime());
        txtDureeActivite.setText(profile.getDureeActivite());
        txtLampadaireId.setText(String.valueOf(profile.getLampadaireId()));
        comboBoxSourceId.setValue(profile.getSourceId());
    }

    private void handleDeleteProfile(profile profile) {
        if (showConfirmation("Confirmation", "Supprimer ce profil ?")) {
            try {
                serviceProfile.delete(profile.getIdprofile());
                loadData();
                showSuccessFeedback();
            } catch (Exception e) {
                showAlert("Erreur", "Échec de la suppression : " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleAdd() {
        try {
            validateInputs();
            profile profile = new profile();
            profile.setConsommationJour(txtConsommationJour.getText());
            profile.setConsommationMois(txtConsommationMois.getText());
            profile.setCoutEstime(txtCoutEstime.getText());
            profile.setDureeActivite(txtDureeActivite.getText());
            profile.setLampadaireId(Integer.parseInt(txtLampadaireId.getText()));
            profile.setSourceId(comboBoxSourceId.getValue());

            serviceProfile.add(profile);
            loadData();
            clearForm();
            showSuccessFeedback();
        } catch (Exception e) {
            showAlert("Erreur d'ajout", e.getMessage());
        }
    }

    @FXML
    private void handleUpdate() {
        if (selectedProfile == null) {
            showAlert("Erreur", "Veuillez sélectionner un profil à modifier");
            return;
        }
        try {
            validateInputs();
            selectedProfile.setConsommationJour(txtConsommationJour.getText());
            selectedProfile.setConsommationMois(txtConsommationMois.getText());
            selectedProfile.setCoutEstime(txtCoutEstime.getText());
            selectedProfile.setDureeActivite(txtDureeActivite.getText());
            selectedProfile.setLampadaireId(Integer.parseInt(txtLampadaireId.getText()));
            selectedProfile.setSourceId(comboBoxSourceId.getValue());

            serviceProfile.update(selectedProfile);
            loadData();
            clearForm();
            showSuccessFeedback();
        } catch (Exception e) {
            showAlert("Erreur de modification", e.getMessage());
        }
    }

    @FXML
    private void handleClear() {
        clearForm();
    }

    private void clearForm() {
        txtConsommationJour.clear();
        txtConsommationMois.clear();
        txtCoutEstime.clear();
        txtDureeActivite.clear();
        txtLampadaireId.clear();
        comboBoxSourceId.getSelectionModel().clearSelection();
        selectedProfile = null;
    }

    private void validateInputs() throws Exception {
        if (txtConsommationJour.getText().isEmpty() || txtConsommationMois.getText().isEmpty() || txtCoutEstime.getText().isEmpty() || txtDureeActivite.getText().isEmpty() || txtLampadaireId.getText().isEmpty() || comboBoxSourceId.getValue() == null) {
            throw new Exception("Veuillez remplir tous les champs.");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private boolean showConfirmation(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        return alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK;
    }

    private void showSuccessFeedback() {
        // Implémentez un feedback visuel pour indiquer que l'opération a réussi
    }

    public void handleBack(ActionEvent actionEvent) {
        switchScene("/Menu.fxml",btnBack);

    }

    public void handleShowProfiles(ActionEvent actionEvent) {
        switchScene("/SourceInterface.fxml",btnsource);

    }

    private void switchScene(String fxmlPath, Button button) {
        try {
            // Charger le fichier FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            // Obtenir le stage actuel (fenêtre)
            Stage stage = (Stage) button.getScene().getWindow();

            // Garder la taille actuelle de la fenêtre
            double width = stage.getWidth();
            double height = stage.getHeight();

            // Appliquer la nouvelle scène et conserver la taille de la fenêtre
            stage.setScene(new Scene(root, width, height));

            // Permettre le redimensionnement de la fenêtre
            stage.setResizable(true);

            // Afficher la nouvelle scène
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
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