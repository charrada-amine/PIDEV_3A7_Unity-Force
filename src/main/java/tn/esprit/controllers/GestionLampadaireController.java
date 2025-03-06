package tn.esprit.controllers;

import com.sun.net.httpserver.HttpServer; // Ajout de cet import si nécessaire
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.util.Arrays;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.StringConverter;
import tn.esprit.models.*;
import tn.esprit.models.Lampadaire.EtatLampadaire;
import tn.esprit.services.ServiceCamera;
import tn.esprit.services.ServiceLampadaire;
import tn.esprit.services.ServiceZone;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.ResourceBundle;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.collections.ObservableList;
import javafx.concurrent.Worker;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.StageStyle;
import netscape.javascript.JSObject;
import org.json.JSONArray;
import org.json.JSONObject;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;
import tn.esprit.utils.TrafficUpdateServer;

public class GestionLampadaireController implements Initializable {

    @FXML private TextField tfType;
    @FXML private TextField tfPuissance;
    @FXML private ComboBox<EtatLampadaire> cbEtat;
    @FXML private DatePicker dpDateInstallation;
    @FXML private ComboBox<Zone> cbZone;
    @FXML private ComboBox<Camera> cbCamera;
    @FXML private Label lblZoneError;
    @FXML private Label lblTypeError;
    @FXML private Label lblPuissanceError;
    @FXML private Label lblEtatError;
    @FXML private Label lblDateError;
    @FXML private Label lblCameraError;
    @FXML private VBox mapContainer;
    @FXML private WebView globalMapView;
    @FXML private VBox globalMapContainer;
    @FXML private Label trafficStatusLabel;

    private final ServiceLampadaire serviceLampadaire = new ServiceLampadaire();
    private final ServiceZone serviceZone = new ServiceZone();
    private final ServiceCamera serviceCamera = new ServiceCamera();
    private final ObservableList<Lampadaire> lampadaires = FXCollections.observableArrayList();
    private Lampadaire selectedLampadaire;
    private WebView mapView;
    private double latitude;
    private double longitude;
    private boolean globalMapInitialized = false;

    private static final String OPENCAGE_API_KEY = "54e21fa99da1458b8ce623f0331efed6";

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cbEtat.setItems(FXCollections.observableArrayList(EtatLampadaire.values()));
        cbZone.setItems(FXCollections.observableArrayList(serviceZone.getAll()));
        cbZone.setConverter(new StringConverter<Zone>() {
            @Override
            public String toString(Zone zone) { return (zone != null) ? zone.getNom() : ""; }
            @Override
            public Zone fromString(String string) { return null; }
        });

        cbCamera.setItems(FXCollections.observableArrayList(serviceCamera.getAll()));
        cbCamera.setConverter(new StringConverter<Camera>() {
            @Override
            public String toString(Camera camera) { return camera != null ? "Caméra " + camera.getIdCamera() + " - " + camera.getUrlFlux() : ""; }
            @Override
            public Camera fromString(String string) { return null; }
        });

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
        cbCamera.valueProperty().addListener((obs, oldVal, newVal) -> resetFieldError(cbCamera, lblCameraError));

        trafficStatusLabel.setText("Statut du trafic: En attente...");
        trafficStatusLabel.setStyle(
                "-fx-font-size: 16px; -fx-font-weight: 700; -fx-text-fill: #202124; " +
                        "-fx-background-color: #f8f9fa; -fx-padding: 8; -fx-background-radius: 8;"
        );

        try {
            // Modifiez l'appel pour inclure le port (par exemple, 8089)
            TrafficUpdateServer.startServer(trafficStatusLabel, this, 8089);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de démarrer le serveur de trafic: " + e.getMessage());
        }

        loadData();
        globalMapContainer.setVisible(true);
        globalMapContainer.setManaged(true);
        javafx.application.Platform.runLater(this::initializeGlobalMap);
    }

    private void loadData() {
        lampadaires.setAll(serviceLampadaire.getAll());
        // Ajouter un lampadaire de test si aucun n'existe
        if (lampadaires.isEmpty()) {
            Lampadaire testLamp = new Lampadaire();
            testLamp.setTypeLampadaire("LED Test");
            testLamp.setPuissance(100);
            testLamp.setEtat(EtatLampadaire.ACTIF);
            testLamp.setLatitude(36.8);
            testLamp.setLongitude(10.2);
            testLamp.setIdCamera(1); // Associe à cameraId 1
            lampadaires.add(testLamp);
        }
        System.out.println("Lampadaires chargés: " + lampadaires.size());
        lampadaires.forEach(l -> System.out.println("ID Caméra: " + l.getIdCamera() + ", Lat: " + l.getLatitude() + ", Lng: " + l.getLongitude()));
        javafx.application.Platform.runLater(this::initializeGlobalMap);
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
        cbCamera.setValue(lampadaire.getIdCamera() > 0 ? serviceCamera.getById(lampadaire.getIdCamera()) : null);
    }

    private void clearForm() {
        tfType.clear();
        tfPuissance.clear();
        cbEtat.getSelectionModel().clearSelection();
        dpDateInstallation.setValue(null);
        cbZone.getSelectionModel().clearSelection();
        cbCamera.getSelectionModel().clearSelection();
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
            lampadaire.setIdCamera(cbCamera.getValue() != null ? cbCamera.getValue().getIdCamera() : 0);
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
        navigateTo(event, "/GestionZone.fxml", "gestion des zones");
    }


    @FXML
    private void handleNavigateToLampadaires(ActionEvent event) {
        navigateTo(event, "/GestionLampadaire.fxml", "gestion des lampadaires");
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
            selectedLampadaire.setIdCamera(cbCamera.getValue() != null ? cbCamera.getValue().getIdCamera() : 0);
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
    private void handleShowLampadaires() {
        try {
            clearForm();
            loadData();
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
        cbCamera.getStyleClass().remove("error-field");
        lblTypeError.setVisible(false);
        lblPuissanceError.setVisible(false);
        lblEtatError.setVisible(false);
        lblDateError.setVisible(false);
        lblZoneError.setVisible(false);
        lblCameraError.setVisible(false);
    }

    private void resetFieldError(Control field, Label errorLabel) {
        field.getStyleClass().remove("error-field");
        errorLabel.setVisible(false);
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
        return alert.showAndWait().filter(response -> response == ButtonType.OK).isPresent();
    }

    private void showSuccessFeedback() {
        Pane root = (Pane) globalMapContainer.getParent();
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

    private void showMapForZone(String zoneName) {
        double[] coordinates = getZoneCoordinates(zoneName);
        if (coordinates == null) {
            showAlert("Erreur", "Impossible de charger les coordonnées de la zone.");
            return;
        }

        hideMap();
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

    private void initializeGlobalMap() {
        try {
            if (globalMapView == null) {
                globalMapView = new WebView();
                globalMapView.setPrefHeight(600);
                globalMapView.setPrefWidth(1200);
                globalMapContainer.getChildren().add(globalMapView);
                System.out.println("Nouvelle instance de WebView créée");
            } else {
                globalMapContainer.getChildren().clear();
                globalMapContainer.getChildren().add(globalMapView);
                System.out.println("WebView existant réutilisé");
            }

            WebEngine webEngine = globalMapView.getEngine();
            webEngine.setJavaScriptEnabled(true);
            webEngine.setOnAlert(event -> {
                System.out.println("JavaScript Alert: " + event.getData());
                if (event.getData().startsWith("LAMPADAIRE_DETAILS:")) {
                    String jsonDetails = event.getData().substring("LAMPADAIRE_DETAILS:".length());
                    javafx.application.Platform.runLater(() -> showLampadaireDetails(jsonDetails));
                }
            });
            webEngine.setOnError(event -> System.out.println("JavaScript Error: " + event.getMessage()));
            webEngine.loadContent(getGlobalMapHtml());
            System.out.println("Contenu de la carte chargé");

            webEngine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
                System.out.println("État de chargement de la carte: " + newState);
                if (newState == Worker.State.SUCCEEDED) {
                    System.out.println("Carte globale chargée avec succès");
                    StringBuilder markersJson = new StringBuilder("[");
                    boolean first = true;
                    for (Lampadaire lampadaire : lampadaires) {
                        double lat = lampadaire.getLatitude();
                        double lng = lampadaire.getLongitude();
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
                                    lat, lng, title, title, zoneName, description, color));
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
                    System.out.println("globalMapInitialized défini à true");
                } else if (newState == Worker.State.FAILED) {
                    System.out.println("Échec du chargement de la carte");
                }
            });

            globalMapContainer.setVisible(true);
            globalMapContainer.setManaged(true);
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
                "        .marker-pin { width: 30px; height: 48px; position: relative; }\n" +
                "        .marker-pin svg { width: 100%; height: 100%; }\n" +
                "        .lamp-pole { stroke: #333; stroke-width: 2; }\n" +
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
                "        function createCustomIcon(color) {\n" +
                "            return L.divIcon({\n" +
                "                className: 'custom-div-icon',\n" +
                "                html: `<div class='marker-pin'><svg width='24' height='48' viewBox='0 0 24 48' fill='none' xmlns='http://www.w3.org/2000/svg'><path d='M12 2L12 20' class='lamp-pole'/><circle cx='12' cy='25' r='8' class='lamp-light' fill='${color}'/><path d='M12 33L12 46' class='lamp-pole'/></svg></div>`,\n" +
                "                iconSize: [30, 48],\n" +
                "                iconAnchor: [15, 48]\n" +
                "            });\n" +
                "        }\n" +
                "        function addMarker(lat, lng, title, content, color, details) {\n" +
                "            var customIcon = createCustomIcon(color);\n" +
                "            var marker = L.marker([lat, lng], {icon: customIcon}).addTo(map);\n" +
                "            marker.on('click', function(e) {\n" +
                "                alert('LAMPADAIRE_DETAILS:' + JSON.stringify(details));\n" +
                "            });\n" +
                "            return marker;\n" +
                "        }\n" +
                "        function addMarkers(markers) {\n" +
                "            markers.forEach(function(m) {\n" +
                "                addMarker(m.lat, m.lng, m.title, m.content, m.color, {lat: m.lat, lng: m.lng, title: m.title, content: m.content, color: m.color});\n" +
                "            });\n" +
                "        }\n" +
                "        function highlightLampadaire(lat, lng) {\n" +
                "            alert('Animation déclenchée pour lat=' + lat + ', lng=' + lng);\n" +
                "            map.setView([lat, lng], 15);\n" +
                "            var circle = L.circle([lat, lng], {\n" +
                "                color: 'red',\n" +
                "                fillColor: '#f03',\n" +
                "                fillOpacity: 0.5,\n" +
                "                radius: 50\n" +
                "            }).addTo(map);\n" +
                "            var opacity = 0.5;\n" +
                "            var interval = setInterval(function() {\n" +
                "                opacity = (opacity === 0.5) ? 0 : 0.5;\n" +
                "                circle.setStyle({fillOpacity: opacity});\n" +
                "            }, 500);\n" +
                "            setTimeout(function() {\n" +
                "                clearInterval(interval);\n" +
                "                map.removeLayer(circle);\n" +
                "            }, 3000);\n" +
                "        }\n" +
                "    </script>\n" +
                "</body>\n" +
                "</html>";
    }

    private String escapeJsonString(String input) {
        if (input == null) return "";
        return input.replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r");
    }

    private HBox createInfoRow(FontAwesomeSolid iconType, String labelText, String value) {
        FontIcon icon = new FontIcon(iconType);
        icon.setIconSize(16);
        icon.setIconColor(Color.web("#5f6368"));

        Label label = new Label(labelText + ": ");
        label.setStyle("-fx-font-weight: 600; -fx-text-fill: #202124;");

        Label valueLabel = new Label(value);
        valueLabel.setStyle("-fx-text-fill: #5f6368;");

        HBox row = new HBox(8, icon, label, valueLabel);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(0, 0, 0, 10));
        return row;
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
    private void showLampadaireDetails(String jsonDetails) {
        try {
            JSONObject details = new JSONObject(jsonDetails);
            double lat = details.getDouble("lat");
            double lng = details.getDouble("lng");
            String title = details.getString("title");

            double tolerance = 0.000001;
            Lampadaire lampadaire = lampadaires.stream()
                    .filter(l -> Math.abs(l.getLatitude() - lat) < tolerance
                            && Math.abs(l.getLongitude() - lng) < tolerance
                            && l.getTypeLampadaire().equalsIgnoreCase(title))
                    .findFirst()
                    .orElse(null);

            if (lampadaire == null) {
                showAlert("Erreur", "Lampadaire non trouvé dans les données.");
                return;
            }

            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.initStyle(StageStyle.TRANSPARENT);
            dialog.getDialogPane().setStyle("-fx-background-color: transparent;");

            VBox dialogContainer = new VBox(15);
            dialogContainer.setPadding(new Insets(20));
            dialogContainer.setStyle("-fx-background-color: white; -fx-background-radius: 16; -fx-border-radius: 16;");
            dialogContainer.setEffect(new DropShadow(10, Color.gray(0.2)));

            HBox header = new HBox(10);
            header.setAlignment(Pos.CENTER_LEFT);
            header.setPadding(new Insets(0, 0, 0, 10));
            FontIcon headerIcon = new FontIcon(FontAwesomeSolid.LIGHTBULB);
            headerIcon.setIconSize(24);
            headerIcon.setIconColor(Color.web("#1a73e8"));
            Label titleLabel = new Label("Détails du Lampadaire");
            titleLabel.setStyle("-fx-font-size: 20; -fx-font-weight: 700; -fx-text-fill: #202124;");
            header.getChildren().addAll(headerIcon, titleLabel);

            VBox infoContainer = new VBox(12);
            infoContainer.setPadding(new Insets(10));
            infoContainer.setStyle("-fx-background-color: #f8f9fa; -fx-background-radius: 8; -fx-border-radius: 8; -fx-border-color: #dadce0; -fx-border-width: 1;");

            Zone zone = serviceZone.getById(lampadaire.getIdZone());
            String zoneName = (zone != null) ? zone.getNom() : "Inconnue";
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            String dateFormatted = (lampadaire.getDateInstallation() != null) ? lampadaire.getDateInstallation().format(formatter) : "N/A";

            infoContainer.getChildren().addAll(
                    createInfoRow(FontAwesomeSolid.TAG, "Type", lampadaire.getTypeLampadaire()),
                    createInfoRow(FontAwesomeSolid.BOLT, "Puissance", lampadaire.getPuissance() + " W"),
                    createInfoRow(FontAwesomeSolid.POWER_OFF, "État", lampadaire.getEtat().toString()),
                    createInfoRow(FontAwesomeSolid.MAP_MARKER_ALT, "Zone", zoneName),
                    createInfoRow(FontAwesomeSolid.CALENDAR, "Date d'installation", dateFormatted),
                    createInfoRow(FontAwesomeSolid.MAP_PIN, "Position", String.format("%.6f, %.6f", lampadaire.getLatitude(), lampadaire.getLongitude())),
                    createInfoRow(FontAwesomeSolid.CAMERA, "Caméra", lampadaire.getIdCamera() > 0 ? "Caméra " + lampadaire.getIdCamera() : "Aucune")
            );

            HBox buttonContainer = new HBox(15);
            buttonContainer.setAlignment(Pos.CENTER);
            buttonContainer.setPadding(new Insets(10, 0, 0, 0));
            Button modifyCoordsButton = new Button("Modifier Coordonnées");
            modifyCoordsButton.setStyle("-fx-background-color: #1a73e8; -fx-text-fill: white; -fx-font-weight: 700; -fx-padding: 8 16; -fx-background-radius: 8;");
            modifyCoordsButton.setGraphic(new FontIcon(FontAwesomeSolid.MAP_PIN));
            modifyCoordsButton.setOnAction(e -> {
                fillForm(lampadaire);
                dialog.close();
            });

            Button deleteButton = new Button("Supprimer");
            deleteButton.setStyle("-fx-background-color: #ea4335; -fx-text-fill: white; -fx-font-weight: 700; -fx-padding: 8 16; -fx-background-radius: 8;");
            deleteButton.setGraphic(new FontIcon(FontAwesomeSolid.TRASH));
            deleteButton.setOnAction(e -> {
                if (showConfirmation("Confirmation", "Voulez-vous supprimer ce lampadaire ?")) {
                    try {
                        serviceLampadaire.delete(lampadaire);
                        loadData();
                        showSuccessFeedback();
                        dialog.close();
                    } catch (Exception ex) {
                        showAlert("Erreur", "Erreur lors de la suppression : " + ex.getMessage());
                    }
                }
            });

            Button liveStreamButton = new Button("Afficher Live");
            liveStreamButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: 700; -fx-padding: 8 16; -fx-background-radius: 8;");
            liveStreamButton.setGraphic(new FontIcon(FontAwesomeSolid.VIDEO));
            liveStreamButton.setOnAction(e -> showLiveStream(lampadaire));

            Button closeButton = new Button("Fermer");
            closeButton.setStyle("-fx-background-color: #5f6368; -fx-text-fill: white; -fx-font-weight: 700; -fx-padding: 8 16; -fx-background-radius: 8;");
            closeButton.setOnAction(e -> dialog.close());

            buttonContainer.getChildren().addAll(modifyCoordsButton, deleteButton, liveStreamButton, closeButton);

            dialogContainer.getChildren().addAll(header, infoContainer, buttonContainer);
            dialog.getDialogPane().setContent(dialogContainer);

            dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
            dialog.getDialogPane().lookupButton(ButtonType.CLOSE).setVisible(false);

            dialog.showAndWait();

        } catch (Exception e) {
            showAlert("Erreur", "Impossible d'afficher les détails : " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showLiveStream(Lampadaire lampadaire) {
        if (lampadaire.getIdCamera() <= 0) {
            showAlert("Erreur", "Aucune caméra n'est associée à ce lampadaire.");
            return;
        }

        Camera camera = serviceCamera.getById(lampadaire.getIdCamera());
        if (camera == null) {
            showAlert("Erreur", "Caméra non trouvée.");
            return;
        }

        String ipAddress = camera.getIpAddress();
        if (ipAddress == null || ipAddress.isEmpty()) {
            showAlert("Erreur", "Aucune adresse IP n'est définie pour cette caméra.");
            return;
        }

        String streamUrl = "http://" + ipAddress + "/stream";
        System.out.println("Tentative de chargement du flux : " + streamUrl);

        Dialog<Void> streamDialog = new Dialog<>();
        streamDialog.initStyle(StageStyle.TRANSPARENT);
        streamDialog.setTitle("Flux Vidéo en Direct");

        VBox streamContainer = new VBox(15);
        streamContainer.setPadding(new Insets(20));
        streamContainer.setStyle("-fx-background-color: white; -fx-background-radius: 16; -fx-border-radius: 16;");
        streamContainer.setEffect(new DropShadow(10, Color.gray(0.2)));

        ImageView imageView = new ImageView();
        imageView.setFitWidth(640);
        imageView.setFitHeight(480);
        imageView.setPreserveRatio(true);

        Thread streamThread = new Thread(() -> {
            try {
                URL url = new URL(streamUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestProperty("User-Agent", "Mozilla/5.0");
                connection.setConnectTimeout(5000);
                InputStream inputStream = connection.getInputStream();
                ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                int nRead;
                byte[] data = new byte[16384];
                byte[] boundary = "--123456789000000000000987654321".getBytes();

                while (!Thread.currentThread().isInterrupted() && (nRead = inputStream.read(data, 0, data.length)) != -1) {
                    buffer.write(data, 0, nRead);
                    byte[] content = buffer.toByteArray();

                    int boundaryIndex = indexOf(content, boundary);
                    if (boundaryIndex != -1) {
                        int nextBoundaryIndex = indexOf(content, boundary, boundaryIndex + boundary.length);
                        if (nextBoundaryIndex != -1) {
                            int headerEnd = indexOf(content, new byte[]{0x0D, 0x0A, 0x0D, 0x0A}, boundaryIndex);
                            if (headerEnd != -1) {
                                int imageStart = headerEnd + 4;
                                int imageEnd = nextBoundaryIndex;
                                byte[] imageData = Arrays.copyOfRange(content, imageStart, imageEnd);

                                Image image = new Image(new ByteArrayInputStream(imageData));
                                javafx.application.Platform.runLater(() -> imageView.setImage(image));
                            }
                            buffer.reset();
                            buffer.write(content, nextBoundaryIndex, content.length - nextBoundaryIndex);
                        }
                    }
                }
                inputStream.close();
                connection.disconnect();
            } catch (Exception e) {
                System.out.println("Erreur lors du chargement du flux : " + e.getMessage());
                javafx.application.Platform.runLater(() -> showAlert("Erreur", "Impossible de charger le flux : " + e.getMessage()));
            }
        });
        streamThread.setDaemon(true);
        streamThread.start();

        streamContainer.getChildren().add(imageView);

        HBox buttonContainer = new HBox(15);
        buttonContainer.setAlignment(Pos.CENTER);
        buttonContainer.setPadding(new Insets(10, 0, 0, 0));
        Button closeStreamButton = new Button("Fermer");
        closeStreamButton.setStyle("-fx-background-color: #5f6368; -fx-text-fill: white; -fx-font-weight: 700; -fx-padding: 8 16; -fx-background-radius: 8;");
        closeStreamButton.setOnAction(e -> {
            streamThread.interrupt();
            streamDialog.close();
        });

        buttonContainer.getChildren().add(closeStreamButton);
        streamContainer.getChildren().add(buttonContainer);

        streamDialog.getDialogPane().setContent(streamContainer);
        streamDialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        streamDialog.getDialogPane().lookupButton(ButtonType.CLOSE).setVisible(false);

        streamDialog.showAndWait();
    }

    private int indexOf(byte[] source, byte[] pattern) {
        return indexOf(source, pattern, 0);
    }

    private int indexOf(byte[] source, byte[] pattern, int start) {
        if (source == null || pattern == null || start >= source.length) return -1;
        outer: for (int i = start; i < source.length - pattern.length + 1; i++) {
            for (int j = 0; j < pattern.length; j++) {
                if (source[i + j] != pattern[j]) continue outer;
            }
            return i;
        }
        return -1;
    }

    public void highlightLampadaireWithCamera(int cameraId) {
        System.out.println("Appel de highlightLampadaireWithCamera pour cameraId: " + cameraId);
        Lampadaire lampadaire = lampadaires.stream()
                .filter(l -> l.getIdCamera() == cameraId)
                .findFirst()
                .orElse(null);

        if (lampadaire != null) {
            System.out.println("Lampadaire trouvé: " + lampadaire.getTypeLampadaire() + " à " + lampadaire.getLatitude() + ", " + lampadaire.getLongitude());
            WebEngine webEngine = globalMapView.getEngine();
            if (webEngine != null) {
                if (!globalMapInitialized) {
                    System.out.println("Carte non encore initialisée, réinitialisation...");
                    javafx.application.Platform.runLater(() -> {
                        initializeGlobalMap();
                        try { Thread.sleep(1000); } catch (Exception e) {} // Attendre 1 seconde pour le chargement
                        executeHighlightScript(webEngine, lampadaire.getLatitude(), lampadaire.getLongitude());
                    });
                } else {
                    executeHighlightScript(webEngine, lampadaire.getLatitude(), lampadaire.getLongitude());
                }
            } else {
                System.out.println("WebEngine est null");
            }

            Timeline blinkAnimation = new Timeline(
                    new KeyFrame(Duration.millis(300), new KeyValue(trafficStatusLabel.opacityProperty(), 0)),
                    new KeyFrame(Duration.millis(600), new KeyValue(trafficStatusLabel.opacityProperty(), 1))
            );
            blinkAnimation.setCycleCount(6);
            System.out.println("Déclenchement de l'animation du label");
            blinkAnimation.play();
        } else {
            System.out.println("Aucun lampadaire trouvé pour la caméra ID: " + cameraId);
        }
    }

    private void executeHighlightScript(WebEngine webEngine, double lat, double lng) {
        String js = String.format(Locale.US,
                "highlightLampadaire(%f, %f);",
                lat, lng
        );
        System.out.println("Exécution du JS: " + js);
        try {
            webEngine.executeScript(js);
            System.out.println("Script JS exécuté avec succès");
        } catch (Exception e) {
            System.out.println("Erreur lors de l'exécution du script JS: " + e.getMessage());
            e.printStackTrace();
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