package tn.esprit.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
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
import tn.esprit.utils.EmailUtil;
import tn.esprit.utils.SmsUtil;


import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ProfileController implements Initializable {

    private final ServiceProfile serviceProfile = new ServiceProfile();
    private final ServiceSource serviceSource = new ServiceSource();
    private static final double SEUIL_CONSOMMATION = 100.0; // Exemple : 100 unités


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
            // Valider les entrées
            validateInputs();

            // Créer un nouveau profil
            profile profile = new profile();
            profile.setConsommationJour(txtConsommationJour.getText());
            profile.setConsommationMois(txtConsommationMois.getText());
            profile.setCoutEstime(txtCoutEstime.getText());
            profile.setDureeActivite(txtDureeActivite.getText());
            profile.setLampadaireId(Integer.parseInt(txtLampadaireId.getText()));
            profile.setSourceId(comboBoxSourceId.getValue());

            // Ajouter le profil à la base de données
            serviceProfile.add(profile);

            // Appeler la fonction pour vérifier la consommation et envoyer un email si nécessaire
            checkConsommationAndSendEmail(profile);
            sendProfileInfoBySms(profile);


            // Charger les données et réinitialiser le formulaire
            loadData();
            clearForm();

            // Afficher un message de succès
            showSuccessFeedback();
        } catch (Exception e) {
            // Gérer les erreurs
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

    public void checkConsommationAndSendEmail(profile profile) {
        try {
            String consommationStr = profile.getConsommationJour();

            // Vérifier si la chaîne est vide ou nulle
            if (consommationStr == null || consommationStr.trim().isEmpty()) {
                System.out.println("Erreur : La consommation par jour est vide ou non définie.");
                return;
            }

            // Convertir en double
            double consommationJour = Double.parseDouble(consommationStr);

            // Récupérer tous les IDs de profils existants
            List<Integer> profileIdsExistants = serviceProfile.getAllProfileids();

            // Vérifier si l'ID du profil actuel existe dans la base de données
            if (!profileIdsExistants.contains(profile.getIdprofile())) {
                System.out.println("Erreur : L'ID du profil " + profile.getIdprofile() + " n'existe pas dans la base de données.");
                return;
            }

            // Comparer avec le seuil et envoyer un e-mail si nécessaire
            if (consommationJour > SEUIL_CONSOMMATION) {
                String to = "charradinoamin@gmail.com"; // Remplace par une adresse valide
                String subject = "Alerte : Consommation élevée pour le profil " + profile.getIdprofile(); // Ajout de l'ID du profil dans le sujet
                String body = "Alerte : La consommation journalière (" + consommationJour +
                        ") dépasse le seuil autorisé (" + SEUIL_CONSOMMATION + ") pour le profilEnergetique ID=" + profile.getIdprofile() + ".\nMerci de prendre les mesures nécessaires.";

                // Envoi de l'email
                EmailUtil.sendEmail(to, subject, body);

                // Afficher une alerte indiquant que l'email a été envoyé
                showAlert("Alerte : E-mail envoyé", "L'e-mail d'alerte a été envoyé pour le profil ID=" + profile.getIdprofile() + " avec une consommation élevée.");
                System.out.println("📩 Email envoyé avec succès pour la consommation élevée du profil ID=" + profile.getIdprofile());
            }
        } catch (NumberFormatException e) {
            System.out.println("Erreur : La consommation par jour n'est pas un nombre valide.");
        }
    }


    public void sendProfileInfoBySms(profile profile) {
        try {
            // Numéro de téléphone du destinataire (à remplacer par un numéro valide)
            String toPhoneNumber = "+21652904114";

            // Création du message contenant toutes les informations du profil
            String messageBody = "📢 Informations du Profil\n" +
                    "📌 ID Profil: " + profile.getIdprofile() + "\n" +
                    "⚡ Consommation Jour: " + profile.getConsommationJour() + " kWh\n" +
                    "📆 Consommation Mois: " + profile.getConsommationMois() + " kWh\n" +
                    "💰 Coût Estimé: " + profile.getCoutEstime() + " €\n" +
                    "⏳ Durée Activité: " + profile.getDureeActivite() + " h/jour\n" +
                    "🔌 Source ID: " + profile.getSourceId();

            // Envoi du SMS
            SmsUtil.sendSms(toPhoneNumber, messageBody);

            // Afficher une alerte de confirmation
            showAlert("SMS Envoyé", "Les informations du profil ID=" + profile.getIdprofile() + " ont été envoyées avec succès.");
            System.out.println("📩 SMS envoyé avec succès pour le profil ID=" + profile.getIdprofile());
        } catch (Exception e) {
            System.out.println("Erreur lors de l'envoi du SMS : " + e.getMessage());
        }
    }






}