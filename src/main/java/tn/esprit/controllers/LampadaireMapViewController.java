package tn.esprit.controllers;
import com.sun.net.httpserver.HttpServer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Worker;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import netscape.javascript.JSObject;
import org.json.JSONObject;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;
import tn.esprit.models.Lampadaire;
import tn.esprit.models.Zone;
import tn.esprit.services.ServiceLampadaire;
import tn.esprit.services.ServiceZone;
import tn.esprit.utils.TrafficUpdateServer;

import java.io.IOException;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.ResourceBundle;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.effect.DropShadow;

public class LampadaireMapViewController implements Initializable {

    @FXML private Label trafficStatusLabel;
    @FXML private WebView globalMapView;
    @FXML private VBox globalMapContainer;

    private final ServiceLampadaire serviceLampadaire = new ServiceLampadaire();
    private final ServiceZone serviceZone = new ServiceZone();
    private final ObservableList<Lampadaire> lampadaires = FXCollections.observableArrayList();
    private boolean globalMapInitialized = false;
    private Lampadaire selectedLampadaire;
    private HttpServer trafficServer; // Pour gérer l'arrêt du serveur

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        trafficStatusLabel.setText("Statut du trafic: En attente...");
        trafficStatusLabel.setStyle(
                "-fx-font-size: 16px; -fx-font-weight: 700; -fx-text-fill: #202124; " +
                        "-fx-background-color: #f8f9fa; -fx-padding: 8; -fx-background-radius: 8;"
        );

        try {
            // Démarrer le serveur avec un port configurable (8089 par défaut)
            TrafficUpdateServer.startServer(trafficStatusLabel, null, 8089);
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
        if (lampadaires.isEmpty()) {
            Lampadaire testLamp = new Lampadaire();
            testLamp.setTypeLampadaire("LED Test");
            testLamp.setPuissance(100);
            testLamp.setEtat(Lampadaire.EtatLampadaire.ACTIF);
            testLamp.setLatitude(36.8);
            testLamp.setLongitude(10.2);
            lampadaires.add(testLamp);
        }
        System.out.println("Lampadaires chargés: " + lampadaires.size());
    }
    @FXML
    private void handleNavigateToLampadaires(ActionEvent event) {
        navigateTo(event, "/GestionLampadaire.fxml", "gestion des lampadaires");
        shutdown();
    }
    @FXML
    private void handleNavigateToLampadaireMap(ActionEvent event) {
        navigateTo(event, "/LampadaireMapView.fxml", "carte des lampadaires");
        shutdown();
    }
    @FXML
    private void handleNavigateToCameras(ActionEvent event) {
        navigateTo(event, "/GestionCamera.fxml", "gestion des caméras");
        shutdown();
    }
    @FXML
    private void handleBack(ActionEvent event) {
        navigateTo(event, "/MainMenu.fxml", "menu principal");
        shutdown();
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
    private void handleNavigateToZoneCitoyen(ActionEvent event) {
        navigateTo(event, "/ZoneCitoyenView.fxml", "vue citoyen");
        shutdown(); // Stop the TrafficUpdateServer
    }
    @FXML
    private void handleNavigateToZones(ActionEvent event) {
        navigateTo(event, "/GestionZone.fxml", "gestion des zones");
        shutdown(); // Arrêter le serveur
    }

    @FXML
    private void handleShowLampadaires() {
        try {
            loadData();
            javafx.application.Platform.runLater(this::initializeGlobalMap);
            showSuccessFeedback();
        } catch (Exception e) {
            showAlert("Erreur", "Impossible de charger les lampadaires : " + e.getMessage());
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

    private void showSuccessFeedback() {
        Node parentNode = globalMapContainer.getParent();
        if (parentNode instanceof Pane) {
            Pane root = (Pane) parentNode;
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
        } else {
            System.out.println("Le parent de globalMapContainer n'est pas un Pane : " + parentNode.getClass().getName());
        }
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
            webEngine.setJavaScriptEnabled(true);
            webEngine.setOnAlert(event -> {
                if (event.getData().startsWith("LAMPADAIRE_DETAILS:")) {
                    String jsonDetails = event.getData().substring("LAMPADAIRE_DETAILS:".length());
                    javafx.application.Platform.runLater(() -> showLampadaireDetails(jsonDetails));
                }
            });
            webEngine.loadContent(getGlobalMapHtml());

            webEngine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
                if (newState == Worker.State.SUCCEEDED) {
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
                            String color = lampadaire.getEtat() == Lampadaire.EtatLampadaire.EN_PANNE ? "#ea4335" :
                                    lampadaire.getEtat() == Lampadaire.EtatLampadaire.EN_MAINTENANCE ? "#fbbc04" :
                                            lampadaire.getEtat() == Lampadaire.EtatLampadaire.ACTIF ? "#34a853" : "#1a73e8";
                            markersJson.append(String.format(Locale.US,
                                    "{\"lat\":%f,\"lng\":%f,\"title\":\"%s\",\"content\":\"%s - Zone: %s<br>%s\",\"color\":\"%s\"}",
                                    lat, lng, title, title, zoneName, description, color));
                        }
                    }
                    markersJson.append("]");
                    webEngine.executeScript("addMarkers(" + markersJson.toString() + ");");
                    globalMapInitialized = true;
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

    private void showLampadaireDetails(String jsonDetails) {
        try {
            JSONObject details = new JSONObject(jsonDetails);
            double lat = details.getDouble("lat");
            double lng = details.getDouble("lng");
            String title = details.getString("title");

            double tolerance = 0.000001;
            selectedLampadaire = lampadaires.stream()
                    .filter(l -> Math.abs(l.getLatitude() - lat) < tolerance
                            && Math.abs(l.getLongitude() - lng) < tolerance
                            && l.getTypeLampadaire().equalsIgnoreCase(title))
                    .findFirst()
                    .orElse(null);

            if (selectedLampadaire == null) {
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

            Zone zone = serviceZone.getById(selectedLampadaire.getIdZone());
            String zoneName = (zone != null) ? zone.getNom() : "Inconnue";
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            String dateFormatted = (selectedLampadaire.getDateInstallation() != null) ? selectedLampadaire.getDateInstallation().format(formatter) : "N/A";

            infoContainer.getChildren().addAll(
                    createInfoRow(FontAwesomeSolid.TAG, "Type", selectedLampadaire.getTypeLampadaire()),
                    createInfoRow(FontAwesomeSolid.BOLT, "Puissance", selectedLampadaire.getPuissance() + " W"),
                    createInfoRow(FontAwesomeSolid.POWER_OFF, "État", selectedLampadaire.getEtat().toString()),
                    createInfoRow(FontAwesomeSolid.MAP_MARKER_ALT, "Zone", zoneName),
                    createInfoRow(FontAwesomeSolid.CALENDAR, "Date d'installation", dateFormatted),
                    createInfoRow(FontAwesomeSolid.MAP_PIN, "Position", String.format("%.6f, %.6f", selectedLampadaire.getLatitude(), selectedLampadaire.getLongitude()))
            );

            HBox buttonContainer = new HBox(15);
            buttonContainer.setAlignment(Pos.CENTER);
            buttonContainer.setPadding(new Insets(10, 0, 0, 0));

            Button signalerButton = new Button("Signaler");
            signalerButton.setStyle("-fx-background-color: #ea4335; -fx-text-fill: white; -fx-font-weight: 700; -fx-padding: 8 16; -fx-background-radius: 8;");
            signalerButton.setGraphic(new FontIcon(FontAwesomeSolid.EXCLAMATION_TRIANGLE));
            // Supprimer ou commenter l'action pour désactiver le bouton
            // signalerButton.setOnAction(e -> { ... });

            Button closeButton = new Button("Fermer");
            closeButton.setStyle("-fx-background-color: #5f6368; -fx-text-fill: white; -fx-font-weight: 700; -fx-padding: 8 16; -fx-background-radius: 8;");
            closeButton.setOnAction(e -> dialog.close());

            buttonContainer.getChildren().addAll(signalerButton, closeButton);
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

    public void shutdown() {
        TrafficUpdateServer.stopServer(); // Arrêter le serveur proprement
    }
}