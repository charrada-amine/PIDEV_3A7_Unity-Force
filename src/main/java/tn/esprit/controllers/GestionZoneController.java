package tn.esprit.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
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
import tn.esprit.models.Session;
import tn.esprit.models.Zone;
import tn.esprit.models.utilisateur;
import tn.esprit.services.ServiceZone;
import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;
import javafx.scene.web.WebView;
import javafx.scene.web.WebEngine;
import javafx.concurrent.Worker;

public class GestionZoneController implements Initializable {
    @FXML private WebView mapView;
    @FXML private TextField tfNom;
    @FXML private TextField tfDescription;
    @FXML private TextField tfSurface;
    @FXML private TextField tfNombreLampadaires;
    @FXML private TextField tfNombreCitoyens;
    @FXML private FlowPane cardContainer;
    private Zone selectedZone;
    private final ServiceZone serviceZone = new ServiceZone();
    private final ObservableList<Zone> zones = FXCollections.observableArrayList();
    private boolean isMapLoaded = false;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        cardContainer.setHgap(20);
        cardContainer.setVgap(20);
        cardContainer.setPadding(new Insets(20));
        loadData();
        initializeMap();

        WebEngine webEngine = mapView.getEngine();
        webEngine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
            if (newState == Worker.State.SUCCEEDED) {
                isMapLoaded = true;
                System.out.println("Carte chargée avec succès au démarrage");
                // Attendre un court délai pour laisser Leaflet s'initialiser
                new Thread(() -> {
                    try {
                        Thread.sleep(1000); // Attendre 1 seconde
                        javafx.application.Platform.runLater(this::showAllZonesOnMap);
                    } catch (InterruptedException e) {
                        System.err.println("Erreur dans l'attente : " + e.getMessage());
                    }
                }).start();
            } else if (newState == Worker.State.FAILED) {
                System.err.println("Échec du chargement de la carte au démarrage");
            }
        });
    }

    private void loadData() {
        zones.setAll(serviceZone.getAll());
        cardContainer.getChildren().clear();
        zones.forEach(zone -> cardContainer.getChildren().add(createZoneCard(zone)));
        System.out.println("Données chargées : " + zones.size() + " zones trouvées");
    }

    private void initializeMap() {
        WebEngine webEngine = mapView.getEngine();
        try {
            URL htmlUrl = getClass().getResource("/map.html");
            if (htmlUrl != null) {
                webEngine.load(htmlUrl.toString());
                System.out.println("Chargement de map.html initié");
            } else {
                System.err.println("Fichier map.html introuvable");
            }
        } catch (Exception e) {
            System.err.println("Erreur de chargement de la carte : " + e.getMessage());
        }
    }

    private VBox createZoneCard(Zone zone) {
        VBox card = new VBox(15);
        card.getStyleClass().add("card");

        HBox header = new HBox(10);
        FontIcon icon = new FontIcon(FontAwesomeSolid.MAP_MARKER);
        icon.setIconSize(24);
        icon.setIconColor(Color.web("#1a73e8"));

        // Modification ici : supprimer l'ID du titre et utiliser le nom de la zone
        Label title = new Label("Zone " + zone.getNom());
        title.setStyle("-fx-font-size: 18; -fx-text-fill: #202124;");

        header.getChildren().addAll(icon, title);

        VBox content = new VBox(8);
        content.getChildren().addAll(
                createInfoRow(FontAwesomeSolid.SIGNATURE, "Nom : " + zone.getNom()),
                createInfoRow(FontAwesomeSolid.INFO_CIRCLE, "Description : " + zone.getDescription()),
                createInfoRow(FontAwesomeSolid.EXPAND, "Surface : " + zone.getSurface() + " m²"),
                createInfoRow(FontAwesomeSolid.LIGHTBULB, "Lampadaires : " + zone.getNombreLampadaires()),
                createInfoRow(FontAwesomeSolid.USERS, "Citoyens : " + zone.getNombreCitoyens())
        );

        HBox buttons = new HBox(10);
        Button btnModifier = createIconButton("Modifier", FontAwesomeSolid.PENCIL_ALT, "#4361ee");
        Button btnSupprimer = createIconButton("Supprimer", FontAwesomeSolid.TRASH, "#ef476f");

        btnModifier.setOnAction(e -> fillForm(zone));
        btnSupprimer.setOnAction(e -> handleDeleteZone(zone));

        buttons.getChildren().addAll(btnModifier, btnSupprimer);
        card.getChildren().addAll(header, new Separator(), content, buttons);

        card.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-padding: 16;");
        card.setEffect(new DropShadow(10, Color.gray(0.3)));
        return card;
    }

    private void showZoneOnMap(Zone zone) {
        if (!isMapLoaded) {
            System.err.println("La carte n'est pas encore chargée, impossible d'ajouter la zone : " + zone.getNom());
            return;
        }
        WebEngine webEngine = mapView.getEngine();
        String script = String.format(Locale.US,
                "if (typeof addZone === 'function') { addZone(%.6f, %.6f, %.2f, '%s'); } else { console.log('Erreur : addZone non défini'); }",
                zone.getLatitude(),
                zone.getLongitude(),
                zone.getSurface(),
                zone.getNom().replace("'", "\\'")
        );
        try {
            webEngine.executeScript(script);
            System.out.println("Marqueur ajouté pour la zone : " + zone.getNom() + " (lat: " + zone.getLatitude() + ", lng: " + zone.getLongitude() + ")");
        } catch (Exception e) {
            System.err.println("Erreur lors de l'ajout de la zone sur la carte : " + e.getMessage());
        }
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

    private void handleDeleteZone(Zone zone) {
        if (showConfirmation("Confirmation", "Supprimer cette zone ?")) {
            try {
                serviceZone.delete(zone);
                loadData();
                showAllZonesOnMap();
                showSuccessFeedback();
            } catch (Exception e) {
                showAlert("Erreur", "Échec de la suppression : " + e.getMessage());
            }
        }
    }

    private void showAllZonesOnMap() {
        if (!isMapLoaded) {
            System.err.println("La carte n'est pas encore chargée, impossible d'afficher toutes les zones");
            return;
        }
        WebEngine webEngine = mapView.getEngine();
        StringBuilder script = new StringBuilder("loadZonesFromDB([");
        zones.forEach(zone -> {
            script.append(String.format(Locale.US,
                    "{ lat: %.6f, lng: %.6f, surface: %.2f, nom: '%s' },",
                    zone.getLatitude(), zone.getLongitude(), zone.getSurface(), zone.getNom().replace("'", "\\'")
            ));
        });
        if (!zones.isEmpty()) {
            script.setLength(script.length() - 1); // Supprimer la dernière virgule
        }
        script.append("]);");
        try {
            webEngine.executeScript(script.toString());
            System.out.println("Toutes les zones ont été affichées sur la carte (" + zones.size() + " zones)");
            // Forcer un re-rendu du WebView
            mapView.setVisible(false);
            mapView.setVisible(true);
        } catch (Exception e) {
            System.err.println("Erreur lors de l'exécution du script JS pour toutes les zones : " + e.getMessage());
        }
    }

    @FXML
    private void handleAdd() {
        try {
            validateInputs();
            Zone zone = new Zone();
            updateZoneFromForm(zone);
            serviceZone.add(zone);

            if (zone.getLatitude() == 0.0 && zone.getLongitude() == 0.0) {
                showAlert("Avertissement", "Géocodage échoué pour '" + zone.getNom() + "'. Aucun marqueur n'a été ajouté à la carte.");
            } else {
                showZoneOnMap(zone);
            }

            loadData();
            clearForm();
            showSuccessFeedback();
        } catch (Exception e) {
            showAlert("Erreur", e.getMessage());
        }
    }

    @FXML
    private void handleUpdate() {
        if (selectedZone == null) {
            showAlert("Erreur", "Veuillez sélectionner une zone à modifier");
            return;
        }
        try {
            validateInputs();
            updateZoneFromForm(selectedZone);
            serviceZone.update(selectedZone);
            loadData();
            showAllZonesOnMap();
            clearForm();
            showSuccessFeedback();
        } catch (Exception e) {
            showAlert("Erreur de modification", e.getMessage());
        }
    }

    @FXML
    private void handleDelete() {
        if (selectedZone == null) {
            showAlert("Erreur", "Veuillez sélectionner une zone à supprimer");
            return;
        }
        handleDeleteZone(selectedZone);
    }

    @FXML
    private void handleClear() {
        clearForm();
    }

    private void updateZoneFromForm(Zone zone) {
        zone.setNom(tfNom.getText());
        zone.setDescription(tfDescription.getText());
        zone.setSurface(Float.parseFloat(tfSurface.getText()));
        zone.setNombreLampadaires(Integer.parseInt(tfNombreLampadaires.getText()));
        zone.setNombreCitoyens(Integer.parseInt(tfNombreCitoyens.getText()));
    }

    private void fillForm(Zone zone) {
        selectedZone = zone;
        tfNom.setText(zone.getNom());
        tfDescription.setText(zone.getDescription());
        tfSurface.setText(String.valueOf(zone.getSurface()));
        tfNombreLampadaires.setText(String.valueOf(zone.getNombreLampadaires()));
        tfNombreCitoyens.setText(String.valueOf(zone.getNombreCitoyens()));
        showZoneOnMap(zone);
    }

    private void clearForm() {
        tfNom.clear();
        tfDescription.clear();
        tfSurface.clear();
        tfNombreLampadaires.clear();
        tfNombreCitoyens.clear();
        selectedZone = null;
    }

    private void validateInputs() throws Exception {
        if (tfNom.getText().isEmpty() || tfDescription.getText().isEmpty()
                || tfSurface.getText().isEmpty() || tfNombreLampadaires.getText().isEmpty()
                || tfNombreCitoyens.getText().isEmpty()) {
            throw new Exception("Tous les champs doivent être remplis");
        }
        try {
            Float.parseFloat(tfSurface.getText());
            Integer.parseInt(tfNombreLampadaires.getText());
            Integer.parseInt(tfNombreCitoyens.getText());
        } catch (NumberFormatException e) {
            throw new Exception("Format de données invalide");
        }
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
        feedback.setStyle("-fx-background-color: linear-gradient(to right, #34a853, #2d8a4a); -fx-text-fill: white; -fx-padding: 12 24; -fx-background-radius: 24; -fx-font-weight: 700;");
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


    @FXML
    private void handleNavigateToZones(ActionEvent event) {
        navigateTo(event, "/GestionZone.fxml", "gestion des zones");
    }
    private void navigateTo(ActionEvent event, String fxmlPath, String destination) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de charger la " + destination);
        }
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