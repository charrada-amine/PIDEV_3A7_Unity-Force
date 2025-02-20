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
    @FXML private TextField tfNomZone;

    private final ServiceLampadaire serviceLampadaire = new ServiceLampadaire();
    private final ServiceZone serviceZone = new ServiceZone();
    private final ObservableList<Lampadaire> lampadaires = FXCollections.observableArrayList();
    private Lampadaire selectedLampadaire;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cbEtat.setItems(FXCollections.observableArrayList(EtatLampadaire.values()));
        scrollPane.setFitToWidth(true);
        cardContainer.setHgap(20);
        cardContainer.setVgap(20);
        cardContainer.setPadding(new Insets(20));

        // Charger les zones
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

        // Listeners pour erreurs
        tfType.textProperty().addListener((obs, oldVal, newVal) -> resetFieldError(tfType, lblTypeError));
        tfPuissance.textProperty().addListener((obs, oldVal, newVal) -> resetFieldError(tfPuissance, lblPuissanceError));
        cbEtat.valueProperty().addListener((obs, oldVal, newVal) -> resetFieldError(cbEtat, lblEtatError));
        dpDateInstallation.valueProperty().addListener((obs, oldVal, newVal) -> resetFieldError(dpDateInstallation, lblDateError));
        cbZone.valueProperty().addListener((obs, oldVal, newVal) -> resetFieldError(cbZone, lblZoneError));

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

        // Sélectionner la zone correspondante
        cbZone.getItems().stream()
                .filter(z -> z.getIdZone() == lampadaire.getIdZone())
                .findFirst()
                .ifPresent(cbZone::setValue);
    }

    private void clearForm() {
        tfType.clear();
        tfPuissance.clear();
        cbEtat.getSelectionModel().clearSelection();
        dpDateInstallation.setValue(null);
        cbZone.getSelectionModel().clearSelection();
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
            serviceLampadaire.add(lampadaire);
            loadData();
            clearForm();
            showSuccessFeedback();
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
            serviceLampadaire.update(selectedLampadaire);
            loadData();
            clearForm();
            showSuccessFeedback();
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

        Label title = new Label("Lampadaire #" + lampadaire.getIdLamp());
        title.setStyle("-fx-font-size: 18; -fx-text-fill: #202124;");

        header.getChildren().addAll(icon, title);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String dateFormatted = (lampadaire.getDateInstallation() != null)
                ? lampadaire.getDateInstallation().format(formatter)
                : "N/A";

        VBox content = new VBox(8);

        // Récupérer le nom de la zone
        Zone zone = serviceZone.getById(lampadaire.getIdZone());
        String zoneName = (zone != null) ? zone.getNom() : "Inconnue";

        content.getChildren().addAll(
                createInfoRow(FontAwesomeSolid.TAG, "Type : " + lampadaire.getTypeLampadaire()),
                createInfoRow(FontAwesomeSolid.BOLT, "Puissance : " + lampadaire.getPuissance() + " W"),
                createInfoRow(FontAwesomeSolid.POWER_OFF, "État : " + lampadaire.getEtat().toString()),
                createInfoRow(FontAwesomeSolid.MAP_MARKER, "Zone : " + zoneName),
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
}