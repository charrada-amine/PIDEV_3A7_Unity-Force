package tn.esprit.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import tn.esprit.models.Camera;
import tn.esprit.services.ServiceCamera;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.scene.paint.Color;

public class GestionCameraController implements Initializable {

    @FXML private TextField tfUrlFlux;
    @FXML private TextField tfIpAddress;
    @FXML private Label lblUrlFluxError;
    @FXML private Label lblIpError;
    @FXML private FlowPane cardContainer;

    private final ServiceCamera serviceCamera = new ServiceCamera();
    private final ObservableList<Camera> cameras = FXCollections.observableArrayList();
    private Camera selectedCamera;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        tfUrlFlux.textProperty().addListener((obs, oldVal, newVal) -> resetFieldError(tfUrlFlux, lblUrlFluxError));
        tfIpAddress.textProperty().addListener((obs, oldVal, newVal) -> resetFieldError(tfIpAddress, lblIpError));
        loadData();
    }

    private void loadData() {
        cameras.setAll(serviceCamera.getAll());
        cardContainer.getChildren().clear();
        cameras.forEach(camera -> cardContainer.getChildren().add(createCameraCard(camera)));
    }

    private VBox createCameraCard(Camera camera) {
        VBox card = new VBox(15);
        card.getStyleClass().add("card");

        HBox header = new HBox(10);
        FontIcon icon = new FontIcon(FontAwesomeSolid.CAMERA);
        icon.setIconSize(24);
        icon.setIconColor(Color.web("#1a73e8"));
        Label title = new Label("Caméra " + camera.getIdCamera());
        title.setStyle("-fx-font-size: 18; -fx-text-fill: #202124;");
        header.getChildren().addAll(icon, title);

        VBox content = new VBox(8);
        content.getChildren().addAll(
                createInfoRow(FontAwesomeSolid.LINK, "URL: " + camera.getUrlFlux()),
                createInfoRow(FontAwesomeSolid.NETWORK_WIRED, "IP: " + camera.getIpAddress())
        );

        HBox buttons = new HBox(10);
        Button btnModifier = createIconButton("Modifier", FontAwesomeSolid.PENCIL_ALT, "#4361ee");
        Button btnSupprimer = createIconButton("Supprimer", FontAwesomeSolid.TRASH, "#ef476f");

        btnModifier.setOnAction(e -> fillForm(camera));
        btnSupprimer.setOnAction(e -> handleDeleteCamera(camera));

        buttons.getChildren().addAll(btnModifier, btnSupprimer);

        card.getChildren().addAll(header, new Separator(), content, buttons);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-padding: 16;");
        card.setEffect(new DropShadow(10, Color.gray(0.3)));
        card.setPrefWidth(250);

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

    private void handleDeleteCamera(Camera camera) {
        if (showConfirmation("Confirmation", "Voulez-vous supprimer cette caméra ?")) {
            try {
                serviceCamera.delete(camera);
                loadData();
                clearForm();
                showSuccessFeedback();
            } catch (Exception e) {
                showAlert("Erreur", "Échec de la suppression : " + e.getMessage());
            }
        }
    }

    private void fillForm(Camera camera) {
        selectedCamera = camera;
        tfUrlFlux.setText(camera.getUrlFlux());
        tfIpAddress.setText(camera.getIpAddress());
    }

    private void clearForm() {
        tfUrlFlux.clear();
        tfIpAddress.clear();
        selectedCamera = null;
    }

    @FXML
    private void handleAdd() {
        try {
            validateInputs();
            Camera camera = new Camera();
            camera.setUrlFlux(tfUrlFlux.getText());
            camera.setIpAddress(tfIpAddress.getText());
            serviceCamera.add(camera);
            loadData();
            clearForm();
            showSuccessFeedback();
        } catch (Exception e) {
            showAlert("Erreur d'ajout", e.getMessage());
        }
    }

    @FXML
    private void handleUpdate() {
        if (selectedCamera == null) {
            showAlert("Erreur", "Veuillez sélectionner une caméra à modifier");
            return;
        }
        try {
            validateInputs();
            selectedCamera.setUrlFlux(tfUrlFlux.getText());
            selectedCamera.setIpAddress(tfIpAddress.getText());
            serviceCamera.update(selectedCamera);
            loadData();
            clearForm();
            showSuccessFeedback();
        } catch (Exception e) {
            showAlert("Erreur de modification", e.getMessage());
        }
    }

    @FXML
    private void handleDelete() {
        if (selectedCamera == null) {
            showAlert("Erreur", "Veuillez sélectionner une caméra à supprimer");
            return;
        }
        handleDeleteCamera(selectedCamera);
    }

    @FXML
    private void handleBack(ActionEvent event) {
        navigateTo(event, "/MainMenu.fxml", "menu principal");
    }

    @FXML
    private void handleNavigateToCameras(ActionEvent event) {
        navigateTo(event, "/tn/esprit/views/GestionCamera.fxml", "gestion des caméras");
    }
    @FXML
    private void handleNavigateToZoneCitoyen(ActionEvent event) {
        navigateTo(event, "/ZoneCitoyenView.fxml", "vue citoyen");
         // Stop the TrafficUpdateServer
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
    private void handleNavigateToZones(ActionEvent event) {
        navigateTo(event, "/GestionZone.fxml", "gestion des zones");
    }

    private void validateInputs() throws Exception {
        boolean hasError = false;
        resetErrorStyles();

        if (tfUrlFlux.getText().isEmpty()) {
            showError(tfUrlFlux, lblUrlFluxError, "L'URL du flux est requise");
            hasError = true;
        } else if (!tfUrlFlux.getText().matches("^(rtsp|http|https)://.*")) {
            showError(tfUrlFlux, lblUrlFluxError, "URL invalide (ex. rtsp://...)");
            hasError = true;
        }

        if (tfIpAddress.getText().isEmpty()) {
            showError(tfIpAddress, lblIpError, "L'adresse IP est requise");
            hasError = true;
        } else if (!tfIpAddress.getText().matches("\\b(?:\\d{1,3}\\.){3}\\d{1,3}\\b")) {
            showError(tfIpAddress, lblIpError, "Adresse IP invalide (ex. 192.168.1.100)");
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
        tfUrlFlux.getStyleClass().remove("error-field");
        tfIpAddress.getStyleClass().remove("error-field");
        lblUrlFluxError.setVisible(false);
        lblIpError.setVisible(false);
    }

    private void resetFieldError(Control field, Label errorLabel) {
        field.getStyleClass().remove("error-field");
        errorLabel.setVisible(false);
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
        return alert.showAndWait().filter(response -> response == ButtonType.OK).isPresent();
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

    private void showSuccessFeedback() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Succès");
        alert.setHeaderText(null);
        alert.setContentText("Opération réussie !");
        alert.showAndWait();
    }
}