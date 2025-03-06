package tn.esprit.controllers;

import javafx.collections.ObservableList;
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
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import tn.esprit.models.Session;
import tn.esprit.models.Source;
import tn.esprit.models.profile;
import tn.esprit.models.utilisateur;
import tn.esprit.services.ServiceProfile;
import tn.esprit.services.ServiceSource;
import tn.esprit.utils.*;


import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;

public class ProfileController implements Initializable {

    private final ServiceProfile serviceProfile = new ServiceProfile();
    private final ServiceSource serviceSource = new ServiceSource();
    private static final double SEUIL_CONSOMMATION = 100.0; // Exemple : 100 unités
    private ObservableList<profile> profiles;
    private boolean isAscendingOrder = false;
    private final Map<Integer, List<String>> profileModifications = new HashMap<>();

    @FXML
    private VBox profileContainer;



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
        btnModifier.setPrefSize(80, 40); // Fixer la taille du bouton Modifier
        btnModifier.setMinSize(80, 40);
        btnModifier.setMaxSize(80, 40);

        Button btnSupprimer = createIconButton("Supprimer", "#ea4335");
        btnSupprimer.setOnAction(e -> handleDeleteProfile(profile));
        btnSupprimer.setPrefSize(80, 40); // Fixer la taille du bouton Supprimer
        btnSupprimer.setMinSize(80, 40);
        btnSupprimer.setMaxSize(80, 40);

        buttonsRow1.getChildren().addAll(btnModifier, btnSupprimer);

        // Boutons d'action (ligne 2 : Générer PDF et Générer QR Code)
        HBox buttonsRow2 = new HBox(10);
        Button btnGenererPdf = createIconButton(" PDF", "#34a853"); // Couleur verte
        btnGenererPdf.setOnAction(e -> handleGeneratePdf(profile));
        btnGenererPdf.setPrefSize(80, 40); // Fixer la taille du bouton Générer PDF
        btnGenererPdf.setMinSize(80, 40);
        btnGenererPdf.setMaxSize(80, 40);

        Button btnGenererQrCode = createIconButton("  QR Code", "#4285f4"); // Couleur bleue
        btnGenererQrCode.setOnAction(e -> handleGenerateQrCode(profile));
        btnGenererQrCode.setPrefSize(80, 40); // Fixer la taille du bouton Générer QR Code
        btnGenererQrCode.setMinSize(80, 40);
        btnGenererQrCode.setMaxSize(80, 40);

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

            // Enregistrer les différences entre l'ancienne et la nouvelle version
            StringBuilder modificationBuilder = new StringBuilder();
            modificationBuilder.append("Profil mis à jour le ").append(new java.util.Date()).append("\n");

            // Comparer chaque champ et enregistrer les différences
            if (!selectedProfile.getConsommationJour().equals(txtConsommationJour.getText())) {
                modificationBuilder.append("- Consommation Jour: ")
                        .append(selectedProfile.getConsommationJour())
                        .append(" -> ")
                        .append(txtConsommationJour.getText())
                        .append("\n");
            }
            if (!selectedProfile.getConsommationMois().equals(txtConsommationMois.getText())) {
                modificationBuilder.append("- Consommation Mois: ")
                        .append(selectedProfile.getConsommationMois())
                        .append(" -> ")
                        .append(txtConsommationMois.getText())
                        .append("\n");
            }
            if (!selectedProfile.getCoutEstime().equals(txtCoutEstime.getText())) {
                modificationBuilder.append("- Coût Estimé: ")
                        .append(selectedProfile.getCoutEstime())
                        .append(" -> ")
                        .append(txtCoutEstime.getText())
                        .append("\n");
            }
            if (!selectedProfile.getDureeActivite().equals(txtDureeActivite.getText())) {
                modificationBuilder.append("- Durée Activité: ")
                        .append(selectedProfile.getDureeActivite())
                        .append(" -> ")
                        .append(txtDureeActivite.getText())
                        .append("\n");
            }
            if (selectedProfile.getLampadaireId() != Integer.parseInt(txtLampadaireId.getText())) {
                modificationBuilder.append("- Lampadaire ID: ")
                        .append(selectedProfile.getLampadaireId())
                        .append(" -> ")
                        .append(txtLampadaireId.getText())
                        .append("\n");
            }

            // Ajouter la modification à la liste des modifications du profil
            String modification = modificationBuilder.toString();
            profileModifications.computeIfAbsent(selectedProfile.getIdprofile(), k -> new ArrayList<>()).add(modification);

            // Mettre à jour le profil sélectionné
            selectedProfile.setConsommationJour(txtConsommationJour.getText());
            selectedProfile.setConsommationMois(txtConsommationMois.getText());
            selectedProfile.setCoutEstime(txtCoutEstime.getText());
            selectedProfile.setDureeActivite(txtDureeActivite.getText());
            selectedProfile.setLampadaireId(Integer.parseInt(txtLampadaireId.getText()));
            selectedProfile.setSourceId(sourceId);

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
        // Récupérer les modifications du profil
        List<String> modifications = profileModifications.getOrDefault(profile.getIdprofile(), new ArrayList<>());

        // Créer une chaîne de caractères contenant les modifications
        StringBuilder modificationsBuilder = new StringBuilder();
        modificationsBuilder.append("Dernières modifications pour le profil ID: ").append(profile.getIdprofile()).append("\n\n");

        // Ajouter chaque modification
        for (String modification : modifications) {
            modificationsBuilder.append(modification).append("\n");
        }

        // Convertir les modifications en une chaîne
        String modificationsText = modificationsBuilder.toString();

        // Ouvrir un FileChooser pour choisir l'emplacement du fichier
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Enregistrer le QR Code");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichiers PNG", "*.png"));
        fileChooser.setInitialFileName("qr_code_profile_" + profile.getIdprofile() + ".png");

        File file = fileChooser.showSaveDialog(null);
        if (file == null) {
            return; // L'utilisateur a annulé
        }

        // Générer le QR code avec les modifications
        QrcodeGenerator.generateProfileQrCode(modificationsText, file.getAbsolutePath(), 300, 300);

        // Afficher un message de succès
        showSuccessAlert("Succès", "Le QR code a été généré avec succès : " + file.getAbsolutePath());
    }




    @FXML
    private void handleExport() {
        // Récupérer la liste des profils
        List<profile> profiles = serviceProfile.getAll();

        // Ouvrir un FileChooser pour choisir l'emplacement du fichier
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Enregistrer le fichier Excel");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichiers Excel (*.xlsx)", "*.xlsx"));
        fileChooser.setInitialFileName("profiles_export.xlsx");

        // Afficher la boîte de dialogue et attendre que l'utilisateur choisisse un emplacement
        File file = fileChooser.showSaveDialog(null); // null signifie que la boîte de dialogue n'est liée à aucune fenêtre spécifique
        if (file == null) {
            return; // L'utilisateur a annulé
        }

        // Exporter les données vers le fichier Excel
        ExcelExporter.exportProfilesToExcel(profiles, file.getAbsolutePath());

        // Afficher un message de succès
        showSuccessAlert("Succès", "Le fichier Excel a été enregistré avec succès : " + file.getAbsolutePath());
    }

    @FXML
    private void handleSortByConsommationJour() {
        // Récupérer la liste des profils
        List<profile> profiles = serviceProfile.getAll();

        // Trier les profils par consommation par jour (ascendant ou descendant)
        if (isAscendingOrder) {
            profiles.sort(Comparator.comparingDouble(p -> Double.parseDouble(p.getConsommationJour())));
        } else {
            profiles.sort((p1, p2) -> Double.compare(Double.parseDouble(p2.getConsommationJour()), Double.parseDouble(p1.getConsommationJour())));
        }

        // Inverser l'ordre pour le prochain clic
        isAscendingOrder = !isAscendingOrder;

        // Mettre à jour l'affichage des profils
        flowPaneProfiles.getChildren().clear();
        profiles.forEach(profile -> flowPaneProfiles.getChildren().add(createProfileCard(profile)));
    }


    @FXML
    private Button btnNavigateToCameras; // Bouton pour l'interface Caméras

    @FXML
    private Button btnNavigateToLampadaireMap; // Bouton pour l'interface Carte Lampadaires

    @FXML
    private Button btnNavigateToZoneCitoyen; // Bouton pour l'interface Vue Citoyen

    @FXML
    private Button btnGestionCapteur;

    @FXML
    private Button btnGestionCitoyen;

    @FXML
    private Button btnGestionDonnee;

    @FXML
    private Button btnGestionIntervention;

    @FXML
    private Button btnGestionLampadaire;

    @FXML
    private Button btnGestionReclamation;

    @FXML
    private Button btnGestionResponsable;

    @FXML
    private Button btnGestionTechnicien;

    @FXML
    private Button btnGestionZone;

    @FXML
    private Button btnProfileInterface;

    @FXML
    private Button btnSourceInterface;

    @FXML
    private Button btnAccueil; // Bouton pour revenir à l'accueil

    // Handler pour le bouton de navigation vers l'interface Caméras
    @FXML
    private void handleNavigateToCameras(ActionEvent event) {
        if (hasPermission("Caméras")) {
            switchScene(event, "/GestionCamera.fxml");
        }
    }

    // Handler pour le bouton de navigation vers l'interface Carte Lampadaires
    @FXML
    private void handleNavigateToLampadaireMap(ActionEvent event) {
        if (hasPermission("Carte Lampadaires")) {
            switchScene(event, "/LampadaireMapView.fxml");
        }
    }

    // Handler pour le bouton de navigation vers l'interface Vue Citoyen
    @FXML
    private void handleNavigateToZoneCitoyen(ActionEvent event) {
        if (hasPermission("Vue Citoyen")) {
            switchScene(event, "/ZoneCitoyenView.fxml");
        }
    }

    // Handler pour le bouton de gestion des capteurs
    @FXML
    private void handleGestionCapteur(ActionEvent event) {
        if (hasPermission("Capteurs")) {
            switchScene(event, "/GestionCapteur.fxml");
        }
    }

    // Handler pour le bouton de gestion des citoyens
    @FXML
    private void handleGestionCitoyen(ActionEvent event) {
        if (hasPermission("Citoyens")) {
            switchScene(event, "/GestionCitoyen.fxml");
        }
    }

    // Handler pour le bouton de gestion des données
    @FXML
    private void handleGestionDonnee(ActionEvent event) {
        if (hasPermission("Données")) {
            switchScene(event, "/GestionDonnee.fxml");
        }
    }

    // Handler pour le bouton de gestion des interventions
    @FXML
    private void handleGestionIntervention(ActionEvent event) {
        if (hasPermission("Interventions")) {
            switchScene(event, "/GestionIntervention.fxml");
        }
    }

    // Handler pour le bouton de gestion des lampadaires
    @FXML
    private void handleGestionLampadaire(ActionEvent event) {
        if (hasPermission("Lampadaires")) {
            switchScene(event, "/GestionLampadaire.fxml");
        }
    }

    // Handler pour le bouton de gestion des réclamations
    @FXML
    private void handleGestionReclamation(ActionEvent event) {
        if (hasPermission("Réclamations")) {
            switchScene(event, "/GestionReclamation.fxml");
        }
    }

    // Handler pour le bouton de gestion des responsables
    @FXML
    private void handleGestionResponsable(ActionEvent event) {
        if (hasPermission("Responsables")) {
            switchScene(event, "/GestionResponsable.fxml");
        }
    }

    // Handler pour le bouton de gestion des techniciens
    @FXML
    private void handleGestionTechnicien(ActionEvent event) {
        if (hasPermission("Techniciens")) {
            switchScene(event, "/GestionTechnicien.fxml");
        }
    }

    // Handler pour le bouton de gestion des zones
    @FXML
    private void handleGestionZone(ActionEvent event) {
        if (hasPermission("Zones")) {
            switchScene(event, "/GestionZone.fxml");
        }
    }

    // Handler pour le bouton de gestion du profil
    @FXML
    private void handleProfileInterface(ActionEvent event) {
        if (hasPermission("Profil énergétique")) {
            switchScene(event, "/ProfileInterface.fxml");
        }
    }

    // Handler pour le bouton des sources
    @FXML
    private void handleSourceInterface(ActionEvent event) {
        if (hasPermission("Sources")) {
            switchScene(event, "/SourceInterface.fxml");
        }
    }

    // Handler pour revenir à la page d'accueil (Menu)
    @FXML
    private void handleAccueil(ActionEvent event) {
        switchScene(event, "/Menu.fxml");
    }

    // Méthode pour vérifier les permissions
    private boolean hasPermission(String page) {
        utilisateur user = Session.getCurrentUser();
        if (user == null) {
            showAlert("Accès refusé", "Vous devez être connecté pour accéder à cette page.");
            return false;
        }

        switch (user.getRole()) {
            case responsable:
                // Le responsable a accès à tout
                return true;

            case citoyen:
                // Le citoyen a accès à Lampadaires, Réclamations, Zones
                return page.equals("Lampadaires") || page.equals("Réclamations") || page.equals("Zones");

            case technicien:
                // Le technicien a accès à Capteurs, Données, Interventions, Caméras, Profil énergétique, Sources
                return page.equals("Capteurs") || page.equals("Données") || page.equals("Interventions")
                        || page.equals("Caméras") || page.equals("Profil énergétique") || page.equals("Sources");

            default:
                // Par défaut, aucun accès
                showAlert("Accès refusé", "Vous n'avez pas l'autorisation d'accéder à cette page.");
                return false;
        }
    }



    // Méthode de commutation de scène (load des FXML et mise à jour de la scène)
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