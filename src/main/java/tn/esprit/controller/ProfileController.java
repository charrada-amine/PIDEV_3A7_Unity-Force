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
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import tn.esprit.models.Source;
import tn.esprit.models.profile;
import tn.esprit.services.ServiceProfile;
import tn.esprit.services.ServiceSource;
import tn.esprit.utils.EmailUtil;
import tn.esprit.utils.SmsUtil;
import tn.esprit.utils.PdfGenerator;
import tn.esprit.utils.QrcodeGenerator;


import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class ProfileController implements Initializable {

    private final ServiceProfile serviceProfile = new ServiceProfile();
    private final ServiceSource serviceSource = new ServiceSource();
    private static final double SEUIL_CONSOMMATION = 100.0; // Exemple : 100 unités


    @FXML
    private FlowPane flowPaneProfiles;

    @FXML
    private ComboBox<String> comboBoxSourceName; // Doit correspondre à fx:id dans le FXML

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
    private Button btnAdd;


    @FXML
    private Button btnCancel;
    @FXML
    private Button btnsource;


    @FXML
    private Button btnBack;

    private profile selectedProfile;
    private Map<String, Integer> sourceNameToIdMap = new HashMap<>();


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Récupérer toutes les sources depuis la base de données
        List<Source> sources = serviceSource.getAll();

        // Remplir le ComboBox avec les noms des sources
        for (Source source : sources) {
            comboBoxSourceName.getItems().add(source.getNom()); // Ajouter le nom de la source au ComboBox
            sourceNameToIdMap.put(source.getNom(), source.getIdSource()); // Associer le nom à l'ID
        }

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
        Label title = new Label("Profil \uD83D\uDC65");
        title.setStyle("-fx-font-size: 18; -fx-text-fill: #202124;");

        header.getChildren().addAll(title);

        // Récupérer le nom de la source
        String sourceName = serviceSource.getSourceNameById(profile.getSourceId());

        // Contenu
        VBox content = new VBox(8);
        content.getChildren().addAll(
                createInfoRow("Consommation Jour: " + profile.getConsommationJour() + " kWh"),
                createInfoRow("Consommation Mois: " + profile.getConsommationMois() + " kWh"),
                createInfoRow("Coût Estimé: " + profile.getCoutEstime() + " €"),
                createInfoRow("Durée Activité: " + profile.getDureeActivite() + " heures"),
                createInfoRow("Lampadaire ID: " + profile.getLampadaireId()),
                createInfoRow("Source: " + sourceName) // Afficher le nom de la source au lieu de l'ID
        );

        // Boutons d'action (ligne 1 : Modifier et Supprimer)
        HBox buttonsRow1 = new HBox(10);
        Button btnModifier = createIconButton("Modifier", "-secondary");
        btnModifier.setOnAction(e -> fillForm(profile));

        Button btnSupprimer = createIconButton("Supprimer", "#ea4335");
        btnSupprimer.setOnAction(e -> handleDeleteProfile(profile));

        buttonsRow1.getChildren().addAll(btnModifier, btnSupprimer);

        // Boutons d'action (ligne 2 : Générer PDF et Générer QR Code)
        HBox buttonsRow2 = new HBox(10);
        Button btnGenererPdf = createIconButton("Générer PDF", "#34a853"); // Couleur verte
        btnGenererPdf.setOnAction(e -> handleGeneratePdf(profile));

        Button btnGenererQrCode = createIconButton("Générer QR Code", "#4285f4"); // Couleur bleue
        btnGenererQrCode.setOnAction(e -> handleGenerateQrCode(profile));

        buttonsRow2.getChildren().addAll(btnGenererPdf, btnGenererQrCode);

        // Ajouter les deux lignes de boutons à la carte
        card.getChildren().addAll(header, new Separator(), content, buttonsRow1, buttonsRow2);
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

        // Récupérer le nom de la source correspondant à l'ID
        String sourceName = serviceSource.getSourceNameById(profile.getSourceId());
        comboBoxSourceName.setValue(sourceName); // Afficher le nom de la source dans le ComboBox
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

            // Récupérer le nom de la source sélectionnée
            String selectedSourceName = comboBoxSourceName.getValue();
            if (selectedSourceName == null) {
                throw new Exception("Veuillez sélectionner une source.");
            }

            // Récupérer l'ID de la source correspondant au nom sélectionné
            int sourceId = sourceNameToIdMap.get(selectedSourceName);

            // Créer un nouveau profil
            profile profile = new profile();
            profile.setConsommationJour(txtConsommationJour.getText());
            profile.setConsommationMois(txtConsommationMois.getText());
            profile.setCoutEstime(txtCoutEstime.getText());
            profile.setDureeActivite(txtDureeActivite.getText());
            profile.setLampadaireId(Integer.parseInt(txtLampadaireId.getText()));
            profile.setSourceId(sourceId); // Utiliser l'ID de la source

            // Ajouter le profil à la base de données
            serviceProfile.add(profile);

            // Charger les données et réinitialiser le formulaire
            loadData();
            clearForm();
            checkConsommationAndSendEmail(profile);
            sendProfileInfoBySms(profile);

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
            // Valider les entrées
            validateInputs();

            // Récupérer le nom de la source sélectionnée
            String selectedSourceName = comboBoxSourceName.getValue();
            if (selectedSourceName == null) {
                throw new Exception("Veuillez sélectionner une source.");
            }

            // Récupérer l'ID de la source correspondant au nom sélectionné
            int sourceId = sourceNameToIdMap.get(selectedSourceName);

            // Mettre à jour le profil sélectionné
            selectedProfile.setConsommationJour(txtConsommationJour.getText());
            selectedProfile.setConsommationMois(txtConsommationMois.getText());
            selectedProfile.setCoutEstime(txtCoutEstime.getText());
            selectedProfile.setDureeActivite(txtDureeActivite.getText());
            selectedProfile.setLampadaireId(Integer.parseInt(txtLampadaireId.getText()));
            selectedProfile.setSourceId(sourceId); // Utiliser l'ID de la source

            // Mettre à jour le profil dans la base de données
            serviceProfile.update(selectedProfile);

            // Recharger les données et réinitialiser le formulaire
            loadData();
            clearForm();

            // Afficher un message de succès
            showSuccessFeedback();
        } catch (Exception e) {
            // Gérer les erreurs
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
        comboBoxSourceName.getSelectionModel().clearSelection();
        selectedProfile = null;
    }

    private void validateInputs() throws Exception {
        if (txtConsommationJour.getText().isEmpty() || txtConsommationMois.getText().isEmpty() || txtCoutEstime.getText().isEmpty() || txtDureeActivite.getText().isEmpty() || txtLampadaireId.getText().isEmpty() || comboBoxSourceName.getValue() == null) {
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
    private void showSuccessAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION); // Utiliser AlertType.INFORMATION pour une alerte de succès
        alert.setTitle(title);
        alert.setHeaderText(null); // Pas de texte d'en-tête
        alert.setContentText(message); // Message à afficher
        alert.showAndWait(); // Afficher l'alerte et attendre que l'utilisateur la ferme
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
                String to = "charradinoamine@gmail.com"; // Remplace par une adresse valide
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
        if (profile == null) {
            System.out.println("Erreur : le profil est null.");
            return;
        }

        try {
            // Numéro de téléphone du destinataire
            String toPhoneNumber = "+21652904114";

            // Création du message contenant toutes les informations du profil
            String messageBody = "Informations du Profil\n" +
                    "ID Profil: " + profile.getIdprofile() + "\n" +
                    "Consommation Jour: " + profile.getConsommationJour() + " kWh\n" +
                    "Consommation Mois: " + profile.getConsommationMois() + " kWh\n" +
                    "Coût Estimé: " + profile.getCoutEstime() + " €\n" +
                    "Durée Activité: " + profile.getDureeActivite() + " h/jour\n" +
                    "Source ID: " + profile.getSourceId();

            // Envoi du SMS
            SmsUtil.sendSms(toPhoneNumber, messageBody);

            // Afficher une alerte de confirmation
            showSuccessAlert("SMS Envoyé", "Les informations du profil ID=" + profile.getIdprofile() + " ont été envoyées avec succès.");
            System.out.println("📩 SMS envoyé avec succès pour le profil ID=" + profile.getIdprofile());
        } catch (Exception e) {
            System.err.println("Erreur lors de l'envoi du SMS : " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleGeneratePdf(profile profile) {
        // Ouvrir un FileChooser pour choisir l'emplacement du fichier
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Enregistrer le PDF");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichiers PDF", "*.pdf"));
        fileChooser.setInitialFileName("profile_" + profile.getIdprofile() + ".pdf");

        File file = fileChooser.showSaveDialog(null);
        if (file == null) {
            return; // L'utilisateur a annulé
        }

        // Générer le PDF
        PdfGenerator.generateProfilePdf(profile, file.getAbsolutePath());

        // Afficher un message de succès
        showSuccessAlert("Succès", "Le fichier PDF a été généré avec succès : " + file.getAbsolutePath());
    }
    private void handleGenerateQrCode(profile profile) {
        // Ouvrir un FileChooser pour choisir l'emplacement du fichier
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Enregistrer le QR Code");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichiers PNG", "*.png"));
        fileChooser.setInitialFileName("qr_code_profile_" + profile.getIdprofile() + ".png");

        File file = fileChooser.showSaveDialog(null);
        if (file == null) {
            return; // L'utilisateur a annulé
        }

        // Générer le QR code
        QrcodeGenerator.generateProfileQrCode(profile, file.getAbsolutePath(), 300, 300);

        // Afficher un message de succès
        showSuccessAlert("Succès", "Le QR code a été généré avec succès : " + file.getAbsolutePath());
    }





}