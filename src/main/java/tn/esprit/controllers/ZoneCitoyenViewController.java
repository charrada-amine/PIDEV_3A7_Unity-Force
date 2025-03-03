package tn.esprit.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.util.Duration;
import tn.esprit.models.Zone;
import tn.esprit.services.ServiceZone;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;
import javafx.concurrent.Worker;
import org.json.JSONObject;

public class ZoneCitoyenViewController implements Initializable {

    @FXML private WebView mapView;
    @FXML private FlowPane cardContainer;
    private final ServiceZone serviceZone = new ServiceZone();
    private final ObservableList<Zone> zones = FXCollections.observableArrayList();
    private boolean isMapLoaded = false;
    private static final String OPENWEATHERMAP_API_KEY = "eba2cfefc4dde5173997c2ad8b6bc39c"; // Remplacez par votre clé API OpenWeatherMap valide

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cardContainer.setHgap(25);
        cardContainer.setVgap(25);
        cardContainer.setPadding(new Insets(20));
        loadData();
        initializeMap();

        WebEngine webEngine = mapView.getEngine();
        webEngine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
            if (newState == Worker.State.SUCCEEDED) {
                isMapLoaded = true;
                System.out.println("Carte chargée avec succès au démarrage");
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
        Label title = new Label("Zone " + zone.getNom());
        title.setStyle("-fx-font-size: 18; -fx-text-fill: #202124;");
        header.getChildren().addAll(icon, title);

        VBox content = new VBox(8);
        content.getChildren().addAll(
                createInfoRow(FontAwesomeSolid.SIGNATURE, "Nom : " + zone.getNom()),
                createInfoRow(FontAwesomeSolid.INFO_CIRCLE, "Description : " + zone.getDescription()),
                createInfoRow(FontAwesomeSolid.EXPAND, "Surface : " + zone.getSurface() + " m²"),
                createInfoRow(FontAwesomeSolid.LIGHTBULB, "Lampadaires : " + zone.getNombreLampadaires()),
                createInfoRow(FontAwesomeSolid.USERS, "Citoyens : " + zone.getNombreCitoyens()),
                createWeatherInfoRow(zone) // Ajout des informations météo
        );

        HBox buttons = new HBox(10);
        Button btnDetails = createIconButton("Détails", FontAwesomeSolid.INFO_CIRCLE, "#1a73e8");

        btnDetails.setOnAction(e -> showZoneDetails(zone));

        buttons.getChildren().addAll(btnDetails);
        card.getChildren().addAll(header, new Separator(), content, buttons);

        card.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-padding: 16;");
        card.setEffect(new DropShadow(10, Color.gray(0.3)));
        return card;
    }

    private HBox createWeatherInfoRow(Zone zone) {
        String weatherInfo = getWeatherInfo(zone.getLatitude(), zone.getLongitude());
        String weatherIconCode = extractWeatherIconCode(zone.getLatitude(), zone.getLongitude());
        FontAwesomeSolid weatherIcon = getWeatherIcon(weatherInfo, weatherIconCode);

        FontIcon icon = new FontIcon(weatherIcon);
        icon.setIconSize(24); // Icône plus grande pour plus de visibilité
        icon.setIconColor(Color.web("#5f6368"));

        Label label = new Label("Météo : " + (weatherInfo.isEmpty() ? "Non disponible" : weatherInfo));
        label.setStyle("-fx-text-fill: #5f6368; -fx-font-size: 14px; -fx-font-weight: 500;");
        HBox weatherRow = new HBox(10, icon, label);
        weatherRow.setStyle("-fx-background-color: #f8f9fa; -fx-background-radius: 8; -fx-padding: 5; -fx-alignment: center-left;");
        return weatherRow;
    }

    private String getWeatherInfo(double latitude, double longitude) {
        try {
            String apiUrl = String.format(Locale.US, "https://api.openweathermap.org/data/2.5/weather?lat=%.6f&lon=%.6f&appid=%s&units=metric&lang=fr",
                    latitude, longitude, OPENWEATHERMAP_API_KEY);
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            if (responseCode == 401) {
                System.err.println("Erreur 401 : Clé API non valide ou non autorisée pour l'URL : " + apiUrl);
                return "Données météo indisponibles (erreur d'authentification)";
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();
            connection.disconnect();

            JSONObject json = new JSONObject(response.toString());
            double temperature = json.getJSONObject("main").getDouble("temp");
            String weatherDescription = json.getJSONArray("weather").getJSONObject(0).getString("description");
            return String.format("Température : %.1f°C, %s", temperature, weatherDescription);
        } catch (Exception e) {
            System.err.println("Erreur lors de la récupération des données météo : " + e.getMessage());
            return "Données météo indisponibles";
        }
    }

    private String extractWeatherIconCode(double latitude, double longitude) {
        try {
            String apiUrl = String.format(Locale.US, "https://api.openweathermap.org/data/2.5/weather?lat=%.6f&lon=%.6f&appid=%s&units=metric&lang=fr",
                    latitude, longitude, OPENWEATHERMAP_API_KEY);
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            if (responseCode == 401) {
                System.err.println("Erreur 401 : Clé API non valide ou non autorisée pour l'URL : " + apiUrl);
                return "";
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();
            connection.disconnect();

            JSONObject json = new JSONObject(response.toString());
            return json.getJSONArray("weather").getJSONObject(0).getString("icon");
        } catch (Exception e) {
            System.err.println("Erreur lors de l'extraction du code d'icône météo : " + e.getMessage());
            return "";
        }
    }

    private FontAwesomeSolid getWeatherIcon(String weatherDescription, String weatherIconCode) {
        if (weatherIconCode == null || weatherIconCode.isEmpty()) {
            if (weatherDescription.toLowerCase().contains("ciel dégagé")) return FontAwesomeSolid.SUN;
            if (weatherDescription.toLowerCase().contains("pluie")) return FontAwesomeSolid.CLOUD_RAIN;
            if (weatherDescription.toLowerCase().contains("nuageux")) return FontAwesomeSolid.CLOUD;
            if (weatherDescription.toLowerCase().contains("brouillard")) return FontAwesomeSolid.CLOUD; // Changed from SMOKE
            if (weatherDescription.toLowerCase().contains("neige")) return FontAwesomeSolid.SNOWFLAKE;
            return FontAwesomeSolid.CLOUD; // Par défaut
        }

        // Mapping des codes d'icônes OpenWeatherMap à FontAwesomeSolid
        switch (weatherIconCode) {
            case "01d": // Soleil (jour)
            case "01n": // Soleil (nuit)
                return FontAwesomeSolid.SUN;
            case "02d": // Peu nuageux (jour)
            case "02n": // Peu nuageux (nuit)
            case "03d": // Nuageux (jour)
            case "03n": // Nuageux (nuit)
            case "04d": // Très nuageux (jour)
            case "04n": // Très nuageux (nuit)
                return FontAwesomeSolid.CLOUD;
            case "09d": // Pluie légère (jour)
            case "09n": // Pluie légère (nuit)
            case "10d": // Pluie (jour)
            case "10n": // Pluie (nuit)
                return FontAwesomeSolid.CLOUD_RAIN;
            case "11d": // Orage (jour)
            case "11n": // Orage (nuit)
                return FontAwesomeSolid.BOLT;
            case "13d": // Neige (jour)
            case "13n": // Neige (nuit)
                return FontAwesomeSolid.SNOWFLAKE;
            case "50d": // Brouillard (jour)
            case "50n": // Brouillard (nuit)
                return FontAwesomeSolid.CLOUD; // Changed from SMOKE
            default:
                return FontAwesomeSolid.CLOUD;
        }
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
            mapView.setVisible(false);
            mapView.setVisible(true);
        } catch (Exception e) {
            System.err.println("Erreur lors de l'exécution du script JS pour toutes les zones : " + e.getMessage());
        }
    }

    private HBox createInfoRow(FontAwesomeSolid iconType, String text) {
        FontIcon icon = new FontIcon(iconType);
        icon.setIconSize(16);
        icon.setIconColor(Color.web("#5f6368"));

        Label label = new Label(text);
        label.setStyle("-fx-text-fill: #5f6368; -fx-font-size: 14px; -fx-font-weight: 500;");
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

    private void showZoneDetails(Zone zone) {
        // Créer une boîte de dialogue personnalisée
        Dialog<Void> dialog = new Dialog<>();
        dialog.initStyle(StageStyle.TRANSPARENT);

        // Conteneur principal
        VBox content = new VBox(15);
        content.setPadding(new Insets(20));
        content.setStyle(
                "-fx-background-color: #ffffff; " +
                        "-fx-background-radius: 16; " +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 20, 0, 0, 4); " +
                        "-fx-border-radius: 16; " +
                        "-fx-border-color: #e0e0e0; " +
                        "-fx-border-width: 1;"
        );

        // Titre
        Label title = new Label("Détails de la Zone : " + zone.getNom());
        title.setStyle(
                "-fx-font-size: 20px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-text-fill: #1a73e8; " +
                        "-fx-padding: 0 0 10 0;"
        );
        content.getChildren().add(title);

        // Séparateur
        Separator separator = new Separator();
        separator.setStyle("-fx-background-color: #e0e0e0;");
        content.getChildren().add(separator);

        // Ajouter les informations avec icônes
        content.getChildren().add(createDetailRow(FontAwesomeSolid.SIGNATURE, "Nom", zone.getNom(), "#202124"));
        content.getChildren().add(createDetailRow(FontAwesomeSolid.INFO_CIRCLE, "Description", zone.getDescription(), "#202124"));
        content.getChildren().add(createDetailRow(FontAwesomeSolid.EXPAND, "Surface", zone.getSurface() + " m²", "#202124"));
        content.getChildren().add(createDetailRow(FontAwesomeSolid.LIGHTBULB, "Lampadaires", String.valueOf(zone.getNombreLampadaires()), "#202124"));
        content.getChildren().add(createDetailRow(FontAwesomeSolid.USERS, "Citoyens", String.valueOf(zone.getNombreCitoyens()), "#202124"));
        content.getChildren().add(createDetailRow(FontAwesomeSolid.MAP_MARKER_ALT, "Position", String.format("%.6f, %.6f", zone.getLatitude(), zone.getLongitude()), "#202124"));

        // Informations météo
        String weatherInfo = getWeatherInfo(zone.getLatitude(), zone.getLongitude());
        String weatherIconCode = extractWeatherIconCode(zone.getLatitude(), zone.getLongitude());
        FontAwesomeSolid weatherIcon = getWeatherIcon(weatherInfo, weatherIconCode);
        content.getChildren().add(createDetailRow(weatherIcon, "Météo", weatherInfo.isEmpty() ? "Non disponible" : weatherInfo, "#5f6368"));

        // Ajouter le contenu à la boîte de dialogue
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().setStyle("-fx-background-color: transparent;");

        // Ajouter un bouton "Fermer" standard via ButtonType
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        Node closeButton = dialog.getDialogPane().lookupButton(ButtonType.CLOSE);
        closeButton.setStyle(
                "-fx-background-color: #1a73e8; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-size: 14px; " +
                        "-fx-padding: 8 20; " +
                        "-fx-background-radius: 8;"
        );

        // Animation de fondu
        dialog.getDialogPane().setOpacity(0);
        Timeline fadeIn = new Timeline(new KeyFrame(Duration.millis(200), new KeyValue(dialog.getDialogPane().opacityProperty(), 1)));
        fadeIn.play();

        // Afficher la boîte de dialogue
        dialog.showAndWait();
    }
    private HBox createDetailRow(FontAwesomeSolid iconType, String labelText, String valueText, String textColor) {
        FontIcon icon = new FontIcon(iconType);
        icon.setIconSize(20);
        icon.setIconColor(Color.web("#1a73e8"));

        Label label = new Label(labelText + " : ");
        label.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #5f6368;");

        Label value = new Label(valueText);
        value.setStyle("-fx-font-size: 14px; -fx-text-fill: " + textColor + ";");

        HBox row = new HBox(10, icon, label, value);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(5));
        row.setStyle(
                "-fx-background-color: #f8f9fa; " +
                        "-fx-background-radius: 8; " +
                        "-fx-padding: 8;"
        );
        return row;
    }
    // Navigation handlers
    @FXML
    private void handleNavigateToZones(ActionEvent event) {
        navigateTo(event, "/GestionZone.fxml", "gestion des zones");
    }

    @FXML
    private void handleNavigateToLampadaires(ActionEvent event) {
        navigateTo(event, "/GestionLampadaire.fxml", "gestion des lampadaires");
    }

    @FXML
    private void handleNavigateToLampadaireMap(ActionEvent event) {
        navigateTo(event, "/LampadaireMapView.fxml", "carte des lampadaires");
    }

    @FXML
    private void handleNavigateToCameras(ActionEvent event) {
        navigateTo(event, "/tn/esprit/views/GestionCamera.fxml", "gestion des caméras");
    }

    @FXML
    private void handleBack(ActionEvent event) {
        navigateTo(event, "/MainMenu.fxml", "menu principal");
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
}