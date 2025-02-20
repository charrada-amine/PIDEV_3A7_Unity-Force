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
import tn.esprit.models.Lampadaire;
import tn.esprit.models.EtatLampadaire;
import tn.esprit.models.Zone;
import tn.esprit.services.ServiceLampadaire;
import tn.esprit.services.ServiceZone;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;

public class GestionLampadaireController implements Initializable {

    @FXML private TextField tfType;
    @FXML private TextField tfPuissance;

    @FXML private DatePicker dpDateInstallation;
    @FXML private TextField tfIdZone;
    @FXML private FlowPane cardContainer;
    @FXML private ScrollPane scrollPane;
    @FXML private Label lblZoneError;
    @FXML
    private ComboBox<Lampadaire.EtatLampadaire> cbEtat;

    private final ServiceLampadaire serviceLampadaire = new ServiceLampadaire();
    private final ServiceZone serviceZone = new ServiceZone();
    private final ObservableList<Lampadaire> lampadaires = FXCollections.observableArrayList();
    private Lampadaire selectedLampadaire;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cbEtat.setItems(FXCollections.observableArrayList());
        scrollPane.setFitToWidth(true);
        cardContainer.setHgap(20);
        cardContainer.setVgap(20);
        cardContainer.setPadding(new Insets(20));
        Font.loadFont(getClass().getResourceAsStream("/fonts/Roboto-Regular.ttf"), 14);

        // Reset error style on text change
        tfIdZone.textProperty().addListener((observable, oldValue, newValue) -> {
            tfIdZone.getStyleClass().remove("error-field");
            lblZoneError.setVisible(false);
            tfType.textProperty().addListener((obs, oldVal, newVal) -> resetFieldError(tfType, lblTypeError));
            tfPuissance.textProperty().addListener((obs, oldVal, newVal) -> resetFieldError(tfPuissance, lblPuissanceError));
            cbEtat.valueProperty().addListener((obs, oldVal, newVal) -> resetFieldError(cbEtat, lblEtatError));
            dpDateInstallation.valueProperty().addListener((obs, oldVal, newVal) -> resetFieldError(dpDateInstallation, lblDateError));
            tfIdZone.textProperty().addListener((obs, oldVal, newVal) -> resetFieldError(tfIdZone, lblZoneError));

        });

        loadData();
    }
    private void resetFieldError(Control field, Label errorLabel) {
        field.getStyleClass().remove("error-field");
        errorLabel.setVisible(false);
    }

    private void loadData() {
        lampadaires.setAll(serviceLampadaire.getAll());
        cardContainer.getChildren().clear();
        lampadaires.forEach(lampadaire -> cardContainer.getChildren().add(createLampadaireCard(lampadaire)));
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
        tfIdZone.setText(String.valueOf(lampadaire.getIdZone()));
    }
    private void clearForm() {
        tfType.clear();
        tfPuissance.clear();
        cbEtat.getSelectionModel().clearSelection();
        dpDateInstallation.setValue(null);
        tfIdZone.clear();
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
            lampadaire.setIdZone(Integer.parseInt(tfIdZone.getText()));
            serviceLampadaire.add(lampadaire);
            loadData();
            clearForm();
            showSuccessFeedback();
        } catch (Exception e) {
            if (e.getMessage().startsWith("La zone avec l'ID")) {
                tfIdZone.getStyleClass().add("error-field");
                lblZoneError.setText(e.getMessage());
                lblZoneError.setVisible(true);
            } else {
                showAlert("Erreur d'ajout", e.getMessage());
            }
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
            selectedLampadaire.setIdZone(Integer.parseInt(tfIdZone.getText()));
            serviceLampadaire.update(selectedLampadaire);
            loadData();
            clearForm();
            showSuccessFeedback();
        } catch (Exception e) {
            if (e.getMessage().startsWith("La zone avec l'ID")) {
                tfIdZone.getStyleClass().add("error-field");
                lblZoneError.setText(e.getMessage());
                lblZoneError.setVisible(true);
            } else {
                showAlert("Erreur de modification", e.getMessage());
            }
        }
    }
    @FXML private Label lblTypeError;
    @FXML private Label lblPuissanceError;
    @FXML private Label lblEtatError;
    @FXML private Label lblDateError;

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

        // Réinitialiser les erreurs
        resetErrorStyles();

        // Vérification du champ Type
        if (tfType.getText().isEmpty()) {
            showError(tfType, lblTypeError, "Le type est requis");
            hasError = true;
        }

        // Vérification du champ Puissance
        try {
            Float.parseFloat(tfPuissance.getText());
        } catch (NumberFormatException e) {
            showError(tfPuissance, lblPuissanceError, "La puissance doit être un nombre");
            hasError = true;
        }

        // Vérification de l'état
        if (cbEtat.getValue() == null) {
            showError(cbEtat, lblEtatError, "Sélectionnez un état");
            hasError = true;
        }

        // Vérification de la date
        if (dpDateInstallation.getValue() == null) {
            showError(dpDateInstallation, lblDateError, "Sélectionnez une date");
            hasError = true;
        }

        // Vérification de l'ID de la zone
        try {
            int idZone = Integer.parseInt(tfIdZone.getText());
            if (serviceZone.getById(idZone) == null) {
                throw new Exception("La zone avec l'ID " + idZone + " n'existe pas");
            }
        } catch (NumberFormatException e) {
            showError(tfIdZone, lblZoneError, "L'ID de la zone doit être un nombre entier");
            hasError = true;
        } catch (Exception e) {
            showError(tfIdZone, lblZoneError, e.getMessage());
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
        // Supprimer les styles d'erreur de tous les champs
        tfType.getStyleClass().remove("error-field");
        tfPuissance.getStyleClass().remove("error-field");
        cbEtat.getStyleClass().remove("error-field");
        dpDateInstallation.getStyleClass().remove("error-field");
        tfIdZone.getStyleClass().remove("error-field");

        // Cacher les labels d'erreur
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
                new KeyFrame(Duration.millis(300), new KeyValue(feedback.translateYProperty(), 20), new KeyValue(feedback.opacityProperty(), 1)),
                new KeyFrame(Duration.millis(2000), new KeyValue(feedback.opacityProperty(), 0))
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

        Label title = new Label("Lampadaire #" + lampadaire.getIdLamp());
        title.setStyle("-fx-font-size: 18; -fx-text-fill: #202124;");

        header.getChildren().addAll(icon, title);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String dateFormatted = (lampadaire.getDateInstallation() != null)
                ? lampadaire.getDateInstallation().format(formatter)
                : "N/A";

        VBox content = new VBox(8);
        content.getChildren().addAll(
                createInfoRow(FontAwesomeSolid.TAG, "Type : " + lampadaire.getTypeLampadaire()),
                createInfoRow(FontAwesomeSolid.BOLT, "Puissance : " + lampadaire.getPuissance() + " W"),
                createInfoRow(FontAwesomeSolid.POWER_OFF, "État : " + lampadaire.getEtat().toString()),
                createInfoRow(FontAwesomeSolid.MAP_MARKER, "Zone ID : " + lampadaire.getIdZone()),
                createInfoRow(FontAwesomeSolid.CALENDAR, "Installation : " + dateFormatted)
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
    @FXML
    private void handleGestionCapteur(ActionEvent event) {
        switchScene(event, "/GestionCapteur.fxml");
    }

    @FXML
    private void handleGestionCitoyen(ActionEvent event) {
        switchScene(event, "/GestionCitoyen.fxml");
    }

    @FXML
    private void handleGestionDonnee(ActionEvent event) {
        switchScene(event, "/GestionDonnee.fxml");
    }

    @FXML
    private void handleGestionIntervention(ActionEvent event) {
        switchScene(event, "/GestionIntervention.fxml");
    }

    @FXML
    private void handleGestionLampadaire(ActionEvent event) {
        switchScene(event, "/GestionLampadaire.fxml");
    }

    @FXML
    private void handleGestionReclamation(ActionEvent event) {
        switchScene(event, "/GestionReclamation.fxml");
    }

    @FXML
    private void handleGestionResponsable(ActionEvent event) {
        switchScene(event, "/GestionResponsable.fxml");
    }

    @FXML
    private void handleGestionTechnicien(ActionEvent event) {
        switchScene(event, "/GestionTechnicien.fxml");
    }

    @FXML
    private void handleGestionUtilisateur(ActionEvent event) {
        switchScene(event, "/GestionUtilisateur.fxml");
    }

    @FXML
    private void handleGestionZone(ActionEvent event) {
        switchScene(event, "/GestionZone.fxml");
    }

    @FXML
    private void handleProfileInterface(ActionEvent event) {
        switchScene(event, "/ProfileInterface.fxml");
    }

    @FXML
    private void handleSourceInterface(ActionEvent event) {
        switchScene(event, "/SourceInterface.fxml");
    }

    @FXML
    private void handleBack() {
        // Logique pour revenir à la page précédente
        System.out.println("Retour à la page précédente");
    }

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