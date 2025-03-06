package tn.esprit.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.Font;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import tn.esprit.models.*;
import tn.esprit.Enumerations.EnumEtat;
import tn.esprit.Enumerations.EnumType;
import tn.esprit.services.ServiceSource;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import tn.esprit.models.profile;
import tn.esprit.utils.QrcodeGenerator;

import java.io.FileOutputStream;
import java.io.IOException;

public class SourceController implements Initializable {


    @FXML private Button btnBack;
    @FXML private Button btnprofile;
    @FXML
    private TextField tfSearch; // Ajoutez ce champ pour la recherche
    @FXML private ComboBox<EnumType> cbType; // Utilisation d'un ComboBox pour le type
    @FXML private TextField tfCapacite;
    @FXML private TextField tfRendement;
    @FXML private TextField  tfNom;
    @FXML private ComboBox<EnumEtat> cbEtat;
    @FXML private FlowPane cardContainer;
    @FXML private ScrollPane scrollPane;
    private final ServiceSource serviceSource = new ServiceSource();
    private final ObservableList<Source> sources = FXCollections.observableArrayList();
    private final Map<Integer, List<String>> sourceModifications = new HashMap<>();
    private Source selectedSource;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cbType.setItems(FXCollections.observableArrayList(EnumType.values())); // Initialisation du ComboBox pour le type
        cbEtat.setItems(FXCollections.observableArrayList(EnumEtat.values()));

        scrollPane.setFitToWidth(true);
        cardContainer.setHgap(20);
        cardContainer.setVgap(20);
        cardContainer.setPadding(new Insets(20));
        Font.loadFont(getClass().getResourceAsStream("/fonts/Roboto-Regular.ttf"), 14);

        // Initialiser le champ de recherche
        tfSearch.setPromptText("Rechercher par nom...");

        loadData();
    }

    private void loadData() {
        sources.setAll(serviceSource.getAll());
        cardContainer.getChildren().clear();
        sources.forEach(source -> cardContainer.getChildren().add(createSourceCard(source)));
    }

    private Button createStyledButton(String text, String color) {
        Button button = new Button(text);
        button.setStyle("-fx-background-color: " + color + ";-fx-text-fill: white;-fx-background-radius: 8;-fx-padding: 8 16;");
        return button;
    }

    private Text createInfoText(String label, String value) {
        Text text = new Text(label + ": " + value);
        text.setFont(Font.font("Roboto", 14));
        return text;
    }

    private void handleDeleteSource(Source source) {
        if (showConfirmation("Confirmation", "Supprimer cette source ?")) {
            try {
                serviceSource.delete(source.getIdSource());
                loadData();
                showSuccessFeedback();
            } catch (Exception e) {
                showAlert("Erreur", "Échec de la suppression : " + e.getMessage());
            }
        }
    }

    private void fillForm(Source source) {
        selectedSource = source;
        cbType.setValue(source.getType());
        tfCapacite.setText(String.valueOf(source.getCapacite()));
        tfRendement.setText(String.valueOf(source.getRendement()));
        cbEtat.setValue(source.getEtat());
        tfNom.setText(source.getNom()); // Remplir le champ nom
    }


    private void clearForm() {
        cbType.getSelectionModel().clearSelection();
        tfCapacite.clear();
        tfRendement.clear();
        cbEtat.getSelectionModel().clearSelection();
        tfNom.clear(); // Effacer le champ nom
        selectedSource = null;
    }



    @FXML

    private void handleAdd() {
        try {
            validateInputs();
            Source source = new Source();
            source.setType(cbType.getValue());
            source.setCapacite(Float.parseFloat(tfCapacite.getText()));
            source.setRendement(Float.parseFloat(tfRendement.getText()));
            source.setEtat(cbEtat.getValue());
            source.setDateInstallation(LocalDate.now()); // Automatically set to current date
            source.setNom(tfNom.getText()); // Récupérer le nom depuis le champ
            serviceSource.add(source);
            loadData();
            clearForm();
            showSuccessFeedback();
        } catch (Exception e) {
            showAlert("Erreur d'ajout", e.getMessage());
        }
    }



    @FXML
    private void handleUpdate() {
        if (selectedSource == null) {
            showAlert("Erreur", "Veuillez sélectionner une source à modifier");
            return;
        }

        try {
            // Valider les entrées
            validateInputs();

            // Enregistrer les différences entre l'ancienne et la nouvelle version
            StringBuilder modificationBuilder = new StringBuilder();
            modificationBuilder.append("Source mise à jour le ").append(new java.util.Date()).append("\n");

            // Comparer chaque champ et enregistrer les différences
            if (!selectedSource.getType().equals(cbType.getValue())) {
                modificationBuilder.append("- Type: ")
                        .append(selectedSource.getType())
                        .append(" -> ")
                        .append(cbType.getValue())
                        .append("\n");
            }
            if (selectedSource.getCapacite() != Float.parseFloat(tfCapacite.getText())) {
                modificationBuilder.append("- Capacité: ")
                        .append(selectedSource.getCapacite())
                        .append(" -> ")
                        .append(tfCapacite.getText())
                        .append("\n");
            }
            if (selectedSource.getRendement() != Float.parseFloat(tfRendement.getText())) {
                modificationBuilder.append("- Rendement: ")
                        .append(selectedSource.getRendement())
                        .append(" -> ")
                        .append(tfRendement.getText())
                        .append("\n");
            }
            if (!selectedSource.getEtat().equals(cbEtat.getValue())) {
                modificationBuilder.append("- État: ")
                        .append(selectedSource.getEtat())
                        .append(" -> ")
                        .append(cbEtat.getValue())
                        .append("\n");
            }
            if (!selectedSource.getNom().equals(tfNom.getText())) {
                modificationBuilder.append("- Nom: ")
                        .append(selectedSource.getNom())
                        .append(" -> ")
                        .append(tfNom.getText())
                        .append("\n");
            }

            // Ajouter la modification à la liste des modifications de la source
            String modification = modificationBuilder.toString();
            sourceModifications.computeIfAbsent(selectedSource.getIdSource(), k -> new ArrayList<>()).add(modification);

            // Mettre à jour la source sélectionnée
            selectedSource.setType(cbType.getValue());
            selectedSource.setCapacite(Float.parseFloat(tfCapacite.getText()));
            selectedSource.setRendement(Float.parseFloat(tfRendement.getText()));
            selectedSource.setEtat(cbEtat.getValue());
            selectedSource.setNom(tfNom.getText());

            // Mettre à jour la source dans la base de données
            serviceSource.update(selectedSource);

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

    private void handleGenerateQrCode(Source source) {
        // Récupérer les modifications de la source
        List<String> modifications = sourceModifications.getOrDefault(source.getIdSource(), new ArrayList<>());

        // Créer une chaîne de caractères contenant les modifications
        StringBuilder modificationsBuilder = new StringBuilder();
        modificationsBuilder.append("Dernières modifications pour la source ID: ").append(source.getIdSource()).append("\n\n");

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
        fileChooser.setInitialFileName("qr_code_source_" + source.getIdSource() + ".png");

        File file = fileChooser.showSaveDialog(null);
        if (file == null) {
            return; // L'utilisateur a annulé
        }

        // Générer le QR code avec les modifications
        QrcodeGenerator.generateProfileQrCode(modificationsText, file.getAbsolutePath(), 300, 300);

        // Afficher un message de succès
        showSuccessAlert("Succès", "Le QR code a été généré avec succès : " + file.getAbsolutePath());
    }



    //@FXML
    /*private void handleShowSources() {
        try {
            clearForm();
            sources.setAll(serviceSource.getAll());
            cardContainer.getChildren().clear();
            sources.forEach(s -> cardContainer.getChildren().add(createSourceCard(s)));
            showSuccessFeedback();
        } catch (Exception e) {
            showAlert("Erreur", "Impossible de charger les sources : " + e.getMessage());
        }
    }*/

    @FXML
    private void handleClear() {
        clearForm();
    }

    private void validateInputs() throws Exception {
        if (cbType.getValue() == null || tfCapacite.getText().isEmpty() || tfRendement.getText().isEmpty() || cbEtat.getValue() == null || tfNom.getText().isEmpty()) {
            throw new Exception("Tous les champs doivent être remplis.");
        }
        // Vous pouvez ajouter d'autres validations ici
    }
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.initStyle(StageStyle.TRANSPARENT);
        alert.getDialogPane().getScene().getRoot().setStyle("-fx-background-color: rgba(255,255,255,0.95);-fx-background-radius: 16;-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 16, 0, 4, 4);");
        alert.getDialogPane().setOpacity(0);
        Timeline fadeIn = new Timeline(new KeyFrame(Duration.millis(200), new KeyValue(alert.getDialogPane().opacityProperty(), 1)));
        fadeIn.play();
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private boolean showConfirmation(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.initStyle(StageStyle.TRANSPARENT);
        alert.getDialogPane().getScene().getRoot().setStyle("-fx-background-color: rgba(255,255,255,0.95);-fx-background-radius: 16;-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 16, 0, 4, 4);");
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        return alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK;
    }

    private void showSuccessFeedback() {
        Pane root = (Pane) cardContainer.getParent();
        Label feedback = new Label("✓ Opération réussie !");
        feedback.setStyle("-fx-background-color: linear-gradient(to right, #34a853, #2d8a4a); " +
                "-fx-text-fill: white; -fx-padding: 12 24; -fx-background-radius: 24; " +
                "-fx-font-weight: 700;");
        feedback.setTranslateY(-50);
        feedback.setOpacity(0);
        root.getChildren().add(feedback);
        Timeline animation = new Timeline(
                new KeyFrame(Duration.millis(300), new KeyValue(feedback.translateYProperty(), 20), new KeyValue(feedback.opacityProperty(), 1)),
                new KeyFrame(Duration.millis(2000), new KeyValue(feedback.opacityProperty(), 0))
        );
        animation.setOnFinished(e -> root.getChildren().remove(feedback));
        animation.play();
    }

    private VBox createSourceCard(Source source) {
        VBox card = new VBox(15);
        card.getStyleClass().add("card");

        // En-tête avec icône
        HBox header = new HBox(10);
        FontIcon icon = new FontIcon(FontAwesomeSolid.BOLT);
        icon.setIconSize(24);
        icon.setIconColor(Color.web("#1a73e8"));

        Label title = new Label("SOURCE #" + source.getNom());
        title.setStyle("-fx-font-size: 18; -fx-text-fill: #202124;");

        header.getChildren().addAll(icon, title);

        // Contenu
        VBox content = new VBox(8);
        content.getChildren().addAll(
                createInfoRow(FontAwesomeSolid.TAG, "Type : " + source.getType().toString()),
                createInfoRow(FontAwesomeSolid.BOLT, "Capacité : " + source.getCapacite() + " W"),
                createInfoRow(FontAwesomeSolid.BOLT, "Rendement : " + source.getRendement() + " %"),
                createInfoRow(FontAwesomeSolid.POWER_OFF, "État : " + source.getEtat().toString()),
                createInfoRow(FontAwesomeSolid.CALENDAR, "Installation : " + source.getDateInstallation())
        );

        // Boutons d'action (ligne 1 : Modifier et Supprimer)
        HBox buttonsRow1 = new HBox(10);

        // Bouton Modifier
        Button btnModifier = createIconButton("Modifier", FontAwesomeSolid.PENCIL_ALT, "-secondary");
        btnModifier.setOnAction(e -> fillForm(source));
        btnModifier.setPrefSize(100, 40);  // Fixer la taille du bouton Modifier
        btnModifier.setMinSize(100, 40);
        btnModifier.setMaxSize(100, 40);

        // Bouton Supprimer
        Button btnSupprimer = createIconButton("Supprimer", FontAwesomeSolid.TRASH, "#ea4335");
        btnSupprimer.setOnAction(e -> handleDeleteSource(source));
        btnSupprimer.setPrefSize(100, 40);  // Fixer la taille du bouton Supprimer
        btnSupprimer.setMinSize(100, 40);
        btnSupprimer.setMaxSize(100, 40);

        buttonsRow1.getChildren().addAll(btnModifier, btnSupprimer);

        // Boutons d'action (ligne 2 : Générer PDF)
        HBox buttonsRow2 = new HBox(10);

        // Bouton PDF
        Button btnPdf = createIconButton("PDF", FontAwesomeSolid.FILE_PDF, "#34a853");
        btnPdf.setOnAction(e -> handleGeneratePdf(source));
        btnPdf.setPrefSize(100, 40);  // Fixer la taille du bouton PDF
        btnPdf.setMinSize(100, 40);
        btnPdf.setMaxSize(100, 40);

        Button btnGenererQrCode = createIconButton("QR Code", FontAwesomeSolid.FILE_PDF, "#34a853");
// Couleur bleue
        btnGenererQrCode.setOnAction(e -> handleGenerateQrCode(source));
        btnGenererQrCode.setPrefSize(100, 40); // Fixer la taille du bouton Générer QR Code
        btnGenererQrCode.setMinSize(100, 40);
        btnGenererQrCode.setMaxSize(100, 40);

        buttonsRow2.getChildren().addAll(btnPdf,btnGenererQrCode);

        // Ajouter les éléments à la carte
        card.getChildren().addAll(header, new Separator(), content, buttonsRow1, buttonsRow2);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-padding: 16;");
        card.setEffect(new DropShadow(10, Color.gray(0.3)));

        return card;
    }

    private HBox createInfoRow(FontAwesomeSolid iconType, String text) {
        FontIcon icon = new FontIcon(iconType);
        icon.setIconSize(16);
        icon.setIconColor(Color.web("#5f6368"));

        Label label = new Label(text);
        label.setStyle("-fx-text-fill: #5f6368;");

        return new HBox(10, icon, label);
    }

    private Button createIconButton(String text, FontAwesomeSolid iconType, String color) {
        FontIcon icon = new FontIcon(iconType);
        icon.setIconSize(16);
        icon.setIconColor(Color.WHITE);

        Button button = new Button(text, icon);
        button.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white;");
        button.setContentDisplay(ContentDisplay.LEFT);
        button.setGraphicTextGap(8);
        return button;
    }


    @FXML
    private void handleShowSources() {
        switchScene("/ProfileInterface.fxml",btnprofile);

    }

    @FXML
    private void handleBack(ActionEvent event) {
        switchScene("/Menu.fxml",btnBack);
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
    private void handleSearch() {
        String searchText = tfSearch.getText().trim().toLowerCase(); // Récupérer le texte de recherche et le mettre en minuscules

        if (searchText.isEmpty()) {
            // Si le champ de recherche est vide, afficher toutes les sources
            loadData();
        } else {
            // Filtrer les sources par nom
            List<Source> filteredSources = serviceSource.getAll().stream()
                    .filter(source -> source.getNom().toLowerCase().contains(searchText))
                    .collect(Collectors.toList());

            // Mettre à jour l'affichage avec les sources filtrées
            sources.setAll(filteredSources);
            cardContainer.getChildren().clear();
            sources.forEach(source -> cardContainer.getChildren().add(createSourceCard(source)));
        }
    }
    private void handleGeneratePdf(Source source){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Enregistrer le document de la source");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichiers PDF", "*.pdf"));
        fileChooser.setInitialFileName("source_" + source.getIdSource() + ".pdf");

        File file = fileChooser.showSaveDialog(null);
        if (file == null) {
            return; // L'utilisateur a annulé
        }

        // Créer un document PDF
        Document document = new Document();

        try {
            // Créer un écrivain pour écrire dans le fichier PDF
            PdfWriter.getInstance(document, new FileOutputStream(file));

            // Ouvrir le document pour pouvoir y ajouter des éléments
            document.open();

            // Ajouter des informations sur la source dans le PDF
            document.add(new Paragraph("Informations sur la Source Énergétique"));
            document.add(new Paragraph("ID: " + source.getIdSource()));
            document.add(new Paragraph("Type: " + source.getType()));
            document.add(new Paragraph("Capacité: " + source.getCapacite() + " kWh"));
            document.add(new Paragraph("Rendement: " + source.getRendement() + "%"));
            document.add(new Paragraph("État: " + source.getEtat()));
            document.add(new Paragraph("Date d'Installation: " + source.getDateInstallation()));

        } catch (DocumentException | IOException e) {
            System.err.println("❌ Erreur lors de la création du PDF : " + e.getMessage());
        } finally {
            // Fermer le document
            document.close();
        }

        // Afficher un message de succès
        showSuccessAlert("Succès", "Le fichier PDF de la source a été généré avec succès : " + file.getAbsolutePath());
    }

    private static void showSuccessAlert(String title, String message) {
        System.out.println("✅ " + title + " : " + message);
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

