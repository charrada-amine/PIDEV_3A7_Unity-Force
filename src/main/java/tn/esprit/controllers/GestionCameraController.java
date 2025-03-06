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
import tn.esprit.models.Session;
import tn.esprit.models.utilisateur;
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
    private void handleNavigateToLampadaires(ActionEvent event) {
        navigateTo(event, "/GestionLampadaire.fxml", "gestion des lampadaires");
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

    // Méthode pour afficher une alerte
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
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