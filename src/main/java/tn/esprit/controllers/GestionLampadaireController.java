package tn.esprit.controllers;

import java.util.Locale;
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
import javafx.util.StringConverter;
import tn.esprit.models.Lampadaire;
import tn.esprit.models.Lampadaire.EtatLampadaire;
import tn.esprit.models.Zone;
import tn.esprit.services.ServiceLampadaire;
import tn.esprit.services.ServiceZone;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import org.json.JSONObject;
import org.json.JSONArray;
import javafx.scene.web.WebView;
import javafx.scene.web.WebEngine;
import javafx.concurrent.Worker;
import netscape.javascript.JSObject;

public class GestionLampadaireController implements Initializable {

    @FXML private TextField tfType;
    @FXML private TextField tfPuissance;
    @FXML private ComboBox<EtatLampadaire> cbEtat;
    @FXML private DatePicker dpDateInstallation;
    @FXML private ComboBox<Zone> cbZone;
    @FXML private FlowPane cardContainer;
    @FXML private ScrollPane scrollPane;
    @FXML private Label lblZoneError;
    @FXML private Label lblTypeError;
    @FXML private Label lblPuissanceError;
    @FXML private Label lblEtatError;
    @FXML private Label lblDateError;
    @FXML private VBox mapContainer;      // Placeholder for the map
    @FXML private WebView globalMapView;  // Déclaré dans le FXML
    @FXML private VBox globalMapContainer;
    private final ServiceLampadaire serviceLampadaire = new ServiceLampadaire();
    private final ServiceZone serviceZone = new ServiceZone();
    private final ObservableList<Lampadaire> lampadaires = FXCollections.observableArrayList();
    private Lampadaire selectedLampadaire;
    private WebView mapView;              // Dynamically created map
    private double latitude;              // Internal storage for latitude
    private double longitude;             // Internal storage for longitude
    private boolean globalMapInitialized = false;

    // OpenCage API key (replace with your own key)
    private static final String OPENCAGE_API_KEY = "54e21fa99da1458b8ce623f0331efed6";

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cbEtat.setItems(FXCollections.observableArrayList(EtatLampadaire.values()));
        scrollPane.setFitToWidth(true);
        cardContainer.setHgap(20);
        cardContainer.setVgap(20);
        cardContainer.setPadding(new Insets(20));

        // Load zones
        cbZone.setItems(FXCollections.observableArrayList(serviceZone.getAll()));
        cbZone.setConverter(new StringConverter<Zone>() {
            @Override
            public String toString(Zone zone) {
                return (zone != null) ? zone.getNom() : "";
            }

            @Override
            public Zone fromString(String string) {
                return null;
            }
        });

        // Listeners for error handling and map display
        tfType.textProperty().addListener((obs, oldVal, newVal) -> resetFieldError(tfType, lblTypeError));
        tfPuissance.textProperty().addListener((obs, oldVal, newVal) -> resetFieldError(tfPuissance, lblPuissanceError));
        cbEtat.valueProperty().addListener((obs, oldVal, newVal) -> resetFieldError(cbEtat, lblEtatError));
        dpDateInstallation.valueProperty().addListener((obs, oldVal, newVal) -> resetFieldError(dpDateInstallation, lblDateError));
        cbZone.valueProperty().addListener((obs, oldVal, newVal) -> {
            resetFieldError(cbZone, lblZoneError);
            if (newVal != null) {
                showMapForZone(newVal.getNom());
            } else {
                hideMap();
            }
        });

        loadData();
        // On s'assure que le conteneur de la carte globale est visible
        globalMapContainer.setVisible(true);
        globalMapContainer.setManaged(true);
        // On initialise la carte avec un délai pour s'assurer que tout est chargé
        javafx.application.Platform.runLater(this::initializeGlobalMap);
    }

    private void showMapForZone(String zoneName) {
        double[] coordinates = getZoneCoordinates(zoneName);
        if (coordinates == null) {
            showAlert("Erreur", "Impossible de charger les coordonnées de la zone.");
            return;
        }

        // Remove existing map if present
        hideMap();

        // Create and configure the map
        mapView = new WebView();
        mapView.setPrefHeight(200);
        mapView.setPrefWidth(360);
        WebEngine webEngine = mapView.getEngine();
        webEngine.loadContent(getMapHtml());

        webEngine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
            if (newState == Worker.State.SUCCEEDED) {
                JSObject window = (JSObject) webEngine.executeScript("window");
                window.setMember("javaController", this);
                webEngine.executeScript("initMap(" + coordinates[0] + ", " + coordinates[1] + ");");
                // Set initial coordinates
                latitude = coordinates[0];
                longitude = coordinates[1];
            }
        });

        webEngine.setOnAlert(event -> {
            String message = event.getData();
            if (message.startsWith("COORDS:")) {
                String[] coords = message.substring(7).split(",");
                latitude = Double.parseDouble(coords[0]);
                longitude = Double.parseDouble(coords[1]);
            }
        });

        // Add map to container and make it visible
        mapContainer.getChildren().add(mapView);
        mapContainer.setVisible(true);
        mapContainer.setManaged(true);
    }

    private void hideMap() {
        if (mapView != null) {
            mapContainer.getChildren().remove(mapView);
            mapView = null;
        }
        mapContainer.setVisible(false);
        mapContainer.setManaged(false);
        latitude = 0.0;
        longitude = 0.0;
    }

    private String getMapHtml() {
        return "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "    <title>Carte intégrée</title>\n" +
                "    <meta charset=\"utf-8\" />\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "    <link rel=\"stylesheet\" href=\"https://unpkg.com/leaflet@1.7.1/dist/leaflet.css\" />\n" +
                "    <script src=\"https://unpkg.com/leaflet@1.7.1/dist/leaflet.js\"></script>\n" +
                "    <style>\n" +
                "        html, body, #map {\n" +
                "            height: 100%;\n" +
                "            width: 100%;\n" +
                "            margin: 0;\n" +
                "            padding: 0;\n" +
                "        }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <div id=\"map\"></div>\n" +
                "    <script>\n" +
                "        var map;\n" +
                "        var marker;\n" +
                "\n" +
                "        function initMap(lat, lng) {\n" +
                "            map = L.map('map').setView([lat, lng], 15);\n" +
                "            L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {\n" +
                "                maxZoom: 19,\n" +
                "                attribution: '© OpenStreetMap contributors'\n" +
                "            }).addTo(map);\n" +
                "            marker = L.marker([lat, lng], {draggable: true}).addTo(map);\n" +
                "            marker.on('dragend', updateCoords);\n" +
                "            map.on('click', function(e) {\n" +
                "                marker.setLatLng(e.latlng);\n" +
                "                updateCoords();\n" +
                "            });\n" +
                "            updateCoords();\n" +
                "        }\n" +
                "        function updateCoords() {\n" +
                "            var pos = marker.getLatLng();\n" +
                "            alert('COORDS:' + pos.lat + ',' + pos.lng);\n" +
                "        }\n" +
                "    </script>\n" +
                "</body>\n" +
                "</html>";
    }

    private double[] getZoneCoordinates(String zoneName) {
        try {
            String encodedQuery = URLEncoder.encode(zoneName, StandardCharsets.UTF_8.toString());
            URL url = new URL("https://api.opencagedata.com/geocode/v1/json?q=" + encodedQuery + "&key=" + OPENCAGE_API_KEY);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();
            JSONObject jsonResponse = new JSONObject(response.toString());
            JSONArray results = jsonResponse.getJSONArray("results");
            if (results.length() > 0) {
                JSONObject firstResult = results.getJSONObject(0);
                JSONObject geometry = firstResult.getJSONObject("geometry");
                double lat = geometry.getDouble("lat");
                double lng = geometry.getDouble("lng");
                return new double[]{lat, lng};
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors de la géolocalisation: " + e.getMessage());
        }
        return null;
    }

    private void resetFieldError(Control field, Label errorLabel) {
        field.getStyleClass().remove("error-field");
        errorLabel.setVisible(false);
    }

    private void loadData() {
        lampadaires.setAll(serviceLampadaire.getAll());
        cardContainer.getChildren().clear();
        lampadaires.forEach(lampadaire -> cardContainer.getChildren().add(createLampadaireCard(lampadaire)));

        // Si la carte globale est déjà initialisée, réinitialisez-la
        if (globalMapInitialized) {
            javafx.application.Platform.runLater(this::initializeGlobalMap);
        }
    }

    private void handleDeleteLampadaire(Lampadaire lampadaire) {
        if (showConfirmation("Confirmation", "Supprimer ce lampadaire ?")) {
            try {
                serviceLampadaire.delete(lampadaire);
                loadData();
                showSuccessFeedback();
            } catch (Exception e) {
                showAlert("Erreur", "Échec de la suppression : " + e.getMessage());
            }
        }
    }

    private void fillForm(Lampadaire lampadaire) {
        selectedLampadaire = lampadaire;
        tfType.setText(lampadaire.getTypeLampadaire());
        tfPuissance.setText(String.valueOf(lampadaire.getPuissance()));
        cbEtat.setValue(lampadaire.getEtat());
        dpDateInstallation.setValue(lampadaire.getDateInstallation());
        latitude = lampadaire.getLatitude();
        longitude = lampadaire.getLongitude();
        cbZone.getItems().stream()
                .filter(z -> z.getIdZone() == lampadaire.getIdZone())
                .findFirst()
                .ifPresent(zone -> {
                    cbZone.setValue(zone);
                    showMapForZone(zone.getNom());
                });
    }

    private void clearForm() {
        tfType.clear();
        tfPuissance.clear();
        cbEtat.getSelectionModel().clearSelection();
        dpDateInstallation.setValue(null);
        cbZone.getSelectionModel().clearSelection();
        hideMap();
        selectedLampadaire = null;
    }

    @FXML
    private void handleAdd() {
        try {
            validateInputs();
            Lampadaire lampadaire = new Lampadaire();
            lampadaire.setTypeLampadaire(tfType.getText());
            lampadaire.setPuissance(Float.parseFloat(tfPuissance.getText()));
            lampadaire.setEtat(cbEtat.getValue());
            lampadaire.setDateInstallation(dpDateInstallation.getValue());
            lampadaire.setIdZone(cbZone.getValue().getIdZone());
            lampadaire.setLatitude(latitude);
            lampadaire.setLongitude(longitude);
            serviceLampadaire.add(lampadaire);
            loadData();
            clearForm();
            showSuccessFeedback();
            javafx.application.Platform.runLater(this::initializeGlobalMap);
        } catch (Exception e) {
            showAlert("Erreur d'ajout", e.getMessage());
        }
    }

    @FXML
    private void handleBack(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/MainMenu.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de charger le menu principal");
        }
    }

    @FXML
    private void handleNavigateToZones(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/GestionZone.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de charger la gestion des zones");
        }
    }

    @FXML
    private void handleUpdate() {
        if (selectedLampadaire == null) {
            showAlert("Erreur", "Veuillez sélectionner un lampadaire à modifier");
            return;
        }
        try {
            validateInputs();
            selectedLampadaire.setTypeLampadaire(tfType.getText());
            selectedLampadaire.setPuissance(Float.parseFloat(tfPuissance.getText()));
            selectedLampadaire.setEtat(cbEtat.getValue());
            selectedLampadaire.setDateInstallation(dpDateInstallation.getValue());
            selectedLampadaire.setIdZone(cbZone.getValue().getIdZone());
            selectedLampadaire.setLatitude(latitude);
            selectedLampadaire.setLongitude(longitude);
            serviceLampadaire.update(selectedLampadaire);
            loadData();
            clearForm();
            showSuccessFeedback();
            javafx.application.Platform.runLater(this::initializeGlobalMap);
        } catch (Exception e) {
            showAlert("Erreur de modification", e.getMessage());
        }
    }

    @FXML
    private void handleDelete() {
        if (selectedLampadaire == null) {
            showAlert("Erreur", "Veuillez sélectionner un lampadaire à supprimer");
            return;
        }
        if (showConfirmation("Confirmation de suppression", "Voulez-vous vraiment supprimer ce lampadaire ?")) {
            try {
                serviceLampadaire.delete(selectedLampadaire);
                loadData();
                clearForm();
                showSuccessFeedback();
                javafx.application.Platform.runLater(this::initializeGlobalMap);
            } catch (Exception e) {
                showAlert("Erreur de suppression", e.getMessage());
            }
        }
    }

    @FXML
    private void handleShowLampadaires() {
        try {
            clearForm();
            lampadaires.setAll(serviceLampadaire.getAll());
            cardContainer.getChildren().clear();
            lampadaires.forEach(l -> cardContainer.getChildren().add(createLampadaireCard(l)));
            javafx.application.Platform.runLater(this::initializeGlobalMap);
            showSuccessFeedback();
        } catch (Exception e) {
            showAlert("Erreur", "Impossible de charger les lampadaires : " + e.getMessage());
        }
    }

    @FXML
    private void handleClear() {
        clearForm();
    }

    private void validateInputs() throws Exception {
        boolean hasError = false;
        resetErrorStyles();

        if (tfType.getText().isEmpty()) {
            showError(tfType, lblTypeError, "Le type est requis");
            hasError = true;
        }

        try {
            Float.parseFloat(tfPuissance.getText());
        } catch (NumberFormatException e) {
            showError(tfPuissance, lblPuissanceError, "La puissance doit être un nombre");
            hasError = true;
        }

        if (cbEtat.getValue() == null) {
            showError(cbEtat, lblEtatError, "Sélectionnez un état");
            hasError = true;
        }

        if (dpDateInstallation.getValue() == null) {
            showError(dpDateInstallation, lblDateError, "Sélectionnez une date");
            hasError = true;
        }

        if (cbZone.getValue() == null) {
            showError(cbZone, lblZoneError, "Sélectionnez une zone");
            hasError = true;
        }

        if (latitude == 0.0 && longitude == 0.0) {
            showAlert("Coordonnées manquantes", "Veuillez sélectionner une position sur la carte");
            hasError = true;
        } else if (latitude < -90 || latitude > 90 || longitude < -180 || longitude > 180) {
            showAlert("Coordonnées invalides", "Les coordonnées GPS ne sont pas dans les plages valides");
            hasError = true;
        }

        if (hasError) {
            throw new Exception("Corrigez les erreurs avant de continuer");
        }
    }

    private void showError(Control field, Label errorLabel, String message) {
        if (!field.getStyleClass().contains("error-field")) {
            field.getStyleClass().add("error-field");
        }
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }

    private void resetErrorStyles() {
        tfType.getStyleClass().remove("error-field");
        tfPuissance.getStyleClass().remove("error-field");
        cbEtat.getStyleClass().remove("error-field");
        dpDateInstallation.getStyleClass().remove("error-field");
        cbZone.getStyleClass().remove("error-field");
        lblTypeError.setVisible(false);
        lblPuissanceError.setVisible(false);
        lblEtatError.setVisible(false);
        lblDateError.setVisible(false);
        lblZoneError.setVisible(false);
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
                new KeyFrame(Duration.millis(300),
                        new KeyValue(feedback.translateYProperty(), 20),
                        new KeyValue(feedback.opacityProperty(), 1)),
                new KeyFrame(Duration.millis(2000),
                        new KeyValue(feedback.opacityProperty(), 0))
        );
        animation.setOnFinished(e -> root.getChildren().remove(feedback));
        animation.play();
    }

    private VBox createLampadaireCard(Lampadaire lampadaire) {
        VBox card = new VBox(15);
        card.getStyleClass().add("card");

        HBox header = new HBox(10);
        FontIcon icon = new FontIcon(FontAwesomeSolid.LIGHTBULB);
        icon.setIconSize(24);
        icon.setIconColor(Color.web("#1a73e8"));

        Label title = new Label("Lampadaire " + lampadaire.getTypeLampadaire());
        title.setStyle("-fx-font-size: 18; -fx-text-fill: #202124;");

        header.getChildren().addAll(icon, title);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String dateFormatted = (lampadaire.getDateInstallation() != null)
                ? lampadaire.getDateInstallation().format(formatter)
                : "N/A";

        VBox content = new VBox(8);
        Zone zone = serviceZone.getById(lampadaire.getIdZone());
        String zoneName = (zone != null) ? zone.getNom() : "Inconnue";

        content.getChildren().addAll(
                createInfoRow(FontAwesomeSolid.TAG, "Type : " + lampadaire.getTypeLampadaire()),
                createInfoRow(FontAwesomeSolid.BOLT, "Puissance : " + lampadaire.getPuissance() + " W"),
                createInfoRow(FontAwesomeSolid.POWER_OFF, "État : " + lampadaire.getEtat().toString()),
                createInfoRow(FontAwesomeSolid.MAP_MARKER, "Zone : " + zoneName),
                createInfoRow(FontAwesomeSolid.CALENDAR, "Installation : " + dateFormatted),
                createInfoRow(FontAwesomeSolid.MAP_PIN, String.format("Position : %.6f, %.6f",
                        lampadaire.getLatitude(), lampadaire.getLongitude()))
        );

        HBox buttons = new HBox(10);
        Button btnModifier = createIconButton("Modifier", FontAwesomeSolid.PENCIL_ALT, "-secondary");
        Button btnSupprimer = createIconButton("Supprimer", FontAwesomeSolid.TRASH, "#ea4335");

        btnModifier.setOnAction(e -> fillForm(lampadaire));
        btnSupprimer.setOnAction(e -> handleDeleteLampadaire(lampadaire));

        buttons.getChildren().addAll(btnModifier, btnSupprimer);
        card.getChildren().addAll(header, new Separator(), content, buttons);

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

    // Helper method to escape JSON strings
    private String escapeJsonString(String input) {
        if (input == null) return "";
        return input.replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r");
    }

    private void initializeGlobalMap() {
        try {
            if (globalMapView == null) {
                globalMapView = new WebView();
                globalMapView.setPrefHeight(600);
                globalMapView.setPrefWidth(1200);
                globalMapContainer.getChildren().add(globalMapView);
            } else {
                globalMapContainer.getChildren().clear();
                globalMapContainer.getChildren().add(globalMapView);
            }

            WebEngine webEngine = globalMapView.getEngine();
            webEngine.setOnAlert(event -> System.out.println("JavaScript Alert: " + event.getData()));
            webEngine.setOnError(event -> System.out.println("JavaScript Error: " + event.getMessage()));
            webEngine.setJavaScriptEnabled(true);
            webEngine.loadContent(getGlobalMapHtml());

            webEngine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
                if (newState == Worker.State.SUCCEEDED) {
                    System.out.println("Carte globale chargée avec succès");

                    StringBuilder markersJson = new StringBuilder("[");
                    boolean first = true;
                    for (Lampadaire lampadaire : lampadaires) {
                        double lat = lampadaire.getLatitude();
                        double lng = lampadaire.getLongitude();
                        // Log pour débogage
                        System.out.println("Lampadaire: " + lampadaire.getTypeLampadaire() + ", État: " + lampadaire.getEtat() + ", Color: " +
                                (lampadaire.getEtat() == EtatLampadaire.EN_PANNE ? "#ea4335" :
                                        lampadaire.getEtat() == EtatLampadaire.EN_MAINTENANCE ? "#fbbc04" :
                                                lampadaire.getEtat() == EtatLampadaire.ACTIF ? "#34a853" : "#1a73e8"));
                        if (lat != 0 && lng != 0 && lat >= -90 && lat <= 90 && lng >= -180 && lng <= 180) {
                            if (!first) markersJson.append(",");
                            first = false;
                            String title = escapeJsonString(lampadaire.getTypeLampadaire());
                            String description = escapeJsonString("Puissance: " + lampadaire.getPuissance() + "W, État: " + lampadaire.getEtat().toString());
                            Zone zone = serviceZone.getById(lampadaire.getIdZone());
                            String zoneName = escapeJsonString((zone != null) ? zone.getNom() : "Inconnue");
                            String color = lampadaire.getEtat() == EtatLampadaire.EN_PANNE ? "#ea4335" :
                                    lampadaire.getEtat() == EtatLampadaire.EN_MAINTENANCE ? "#fbbc04" :
                                            lampadaire.getEtat() == EtatLampadaire.ACTIF ? "#34a853" : "#1a73e8";
                            markersJson.append(String.format(Locale.US,
                                    "{\"lat\":%f,\"lng\":%f,\"title\":\"%s\",\"content\":\"%s - Zone: %s<br>%s\",\"color\":\"%s\"}",
                                    lat, lng, title, title, zoneName, description, color
                            ));
                        }
                    }
                    markersJson.append("]");
                    System.out.println("JSON des marqueurs : " + markersJson.toString());

                    try {
                        webEngine.executeScript("addMarkers(" + markersJson.toString() + ");");
                        System.out.println("Marqueurs ajoutés avec succès");
                    } catch (Exception e) {
                        System.out.println("Erreur lors de l'ajout des marqueurs : " + e.getMessage());
                        e.printStackTrace();
                    }
                    globalMapInitialized = true;
                }
            });

            globalMapContainer.setVisible(true);
            globalMapContainer.setManaged(true);
            System.out.println("Taille du conteneur : " + globalMapContainer.getWidth() + "x" + globalMapContainer.getHeight());
        } catch (Exception e) {
            System.out.println("Erreur lors de l'initialisation de la carte globale : " + e.getMessage());
            e.printStackTrace();
        }
    }

    private String getGlobalMapHtml() {
        return "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "    <title>Carte des Lampadaires</title>\n" +
                "    <meta charset=\"utf-8\" />\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "    <link rel=\"stylesheet\" href=\"https://unpkg.com/leaflet@1.7.1/dist/leaflet.css\" />\n" +
                "    <script src=\"https://unpkg.com/leaflet@1.7.1/dist/leaflet.js\"></script>\n" +
                "    <style>\n" +
                "        html, body, #map {\n" +
                "            height: 100%;\n" +
                "            width: 100%;\n" +
                "            margin: 0;\n" +
                "            padding: 0;\n" +
                "        }\n" +
                "        .custom-icon {\n" +
                "            text-align: center;\n" +
                "            display: flex;\n" +
                "            align-items: center;\n" +
                "            justify-content: center;\n" +
                "        }\n" +
                "        .marker-pin {\n" +
                "            width: 30px;\n" +
                "            height: 48px;\n" +
                "            position: relative;\n" +
                "        }\n" +
                "        .marker-pin svg {\n" +
                "            width: 100%;\n" +
                "            height: 100%;\n" +
                "        }\n" +
                "        .lamp-pole {\n" +
                "            stroke: #333;\n" +
                "            stroke-width: 2;\n" +
                "        }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <div id=\"map\"></div>\n" +
                "    <script>\n" +
                "        var map = L.map('map').setView([36.8, 10.2], 13);\n" +
                "        L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {\n" +
                "            maxZoom: 19,\n" +
                "            attribution: '© OpenStreetMap contributors'\n" +
                "        }).addTo(map);\n" +
                "\n" +
                "        function createCustomIcon(color) {\n" +
                "            return L.divIcon({\n" +
                "                className: 'custom-div-icon',\n" +
                "                html: `<div class='marker-pin'><svg width='24' height='48' viewBox='0 0 24 48' fill='none' xmlns='http://www.w3.org/2000/svg'><path d='M12 2L12 20' class='lamp-pole'/><circle cx='12' cy='25' r='8' class='lamp-light' fill='${color}'/><path d='M12 33L12 46' class='lamp-pole'/></svg></div>`,\n" +
                "                iconSize: [30, 48],\n" +
                "                iconAnchor: [15, 48]\n" +
                "            });\n" +
                "        }\n" +
                "\n" +
                "        function addMarker(lat, lng, title, content, color) {\n" +
                "            var customIcon = createCustomIcon(color);\n" +
                "            var marker = L.marker([lat, lng], {icon: customIcon}).addTo(map);\n" +
                "            marker.bindPopup('<b>' + content + '</b>');\n" +
                "        }\n" +
                "\n" +
                "        function addMarkers(markers) {\n" +
                "            markers.forEach(function(m) {\n" +
                "                addMarker(m.lat, m.lng, m.title, m.content, m.color);\n" +
                "            });\n" +
                "        }\n" +
                "    </script>\n" +
                "</body>\n" +
                "</html>";
    }
}