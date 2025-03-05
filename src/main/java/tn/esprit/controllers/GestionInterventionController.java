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
import tn.esprit.models.Intervention;
import tn.esprit.models.Intervention.TypeIntervention;
import tn.esprit.services.ServiceIntervention;
import java.net.URL;
import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;

public class GestionInterventionController implements Initializable {

    @FXML private ComboBox<TypeIntervention> cbType;
    @FXML private TextField tfDescription;
    @FXML private ComboBox<String> cbEtat;
    @FXML private DatePicker dpDate;
    @FXML private TextField tfHeure;
    @FXML private TextField tfLampadaireId;
    @FXML private TextField tfTechnicienId;
    @FXML private TextField tfReclamationId;
    @FXML private FlowPane cardContainer;
    @FXML private ScrollPane scrollPane;

    private final ServiceIntervention serviceIntervention = new ServiceIntervention();
    private final ObservableList<Intervention> interventions = FXCollections.observableArrayList();
    private Intervention selectedIntervention;
    private Integer preFilledReclamationId;
    private Integer preFilledLampadaireId;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cbType.setItems(FXCollections.observableArrayList(TypeIntervention.values()));
        scrollPane.setFitToWidth(true);
        cardContainer.setHgap(20);
        cardContainer.setVgap(20);
        cardContainer.setPadding(new Insets(20));
        Font.loadFont(getClass().getResourceAsStream("/fonts/Roboto-Regular.ttf"), 14);
        tfReclamationId.setDisable(false); // Enabled by default
        tfLampadaireId.setDisable(false); // Enabled by default
        loadData();
    }

    public void setReclamationAndLampadaireId(int reclamationId, int lampadaireId) {
        this.preFilledReclamationId = reclamationId;
        this.preFilledLampadaireId = lampadaireId;
        tfReclamationId.setText(String.valueOf(reclamationId));
        tfReclamationId.setDisable(true); // Disable when pre-filled
        tfLampadaireId.setText(String.valueOf(lampadaireId));
        tfLampadaireId.setDisable(true); // Disable when pre-filled
    }

    private void loadData() {
        interventions.setAll(serviceIntervention.getAll());
        cardContainer.getChildren().clear();
        interventions.forEach(intervention -> cardContainer.getChildren().add(createInterventionCard(intervention)));
    }

    private void handleDeleteIntervention(Intervention intervention) {
        if (showConfirmation("Confirmation", "Supprimer cette intervention ?")) {
            try {
                serviceIntervention.delete(intervention);
                loadData();
                showSuccessFeedback();
            } catch (Exception e) {
                showAlert("Erreur", "Échec de la suppression : " + e.getMessage());
            }
        }
    }

    private void fillForm(Intervention intervention) {
        selectedIntervention = intervention;
        cbType.setValue(intervention.getTypeIntervention());
        tfDescription.setText(intervention.getDescription());
        cbEtat.setValue(intervention.getEtat());
        dpDate.setValue(intervention.getDateIntervention().toLocalDate());
        tfHeure.setText(intervention.getHeureIntervention().toString());
        tfLampadaireId.setText(String.valueOf(intervention.getLampadaireId()));
        tfLampadaireId.setDisable(false); // Enable when editing existing intervention
        tfTechnicienId.setText(String.valueOf(intervention.getTechnicienId()));
        tfReclamationId.setText(intervention.getID_reclamation() != null ?
                String.valueOf(intervention.getID_reclamation()) : "");
        tfReclamationId.setDisable(false); // Enable when editing existing intervention
    }

    private void clearForm() {
        cbType.getSelectionModel().clearSelection();
        tfDescription.clear();
        cbEtat.getSelectionModel().clearSelection();
        dpDate.setValue(null);
        tfHeure.clear();
        tfTechnicienId.clear();
        if (preFilledReclamationId == null) {
            tfReclamationId.clear();
            tfReclamationId.setDisable(false);
        } else {
            tfReclamationId.setText(String.valueOf(preFilledReclamationId));
            tfReclamationId.setDisable(true);
        }
        if (preFilledLampadaireId == null) {
            tfLampadaireId.clear();
            tfLampadaireId.setDisable(false);
        } else {
            tfLampadaireId.setText(String.valueOf(preFilledLampadaireId));
            tfLampadaireId.setDisable(true);
        }
        selectedIntervention = null;
    }

    @FXML
    private void handleAdd() {
        try {
            validateInputs();
            Intervention intervention = new Intervention(
                    cbType.getValue(),
                    tfDescription.getText(),
                    cbEtat.getValue(),
                    Date.valueOf(dpDate.getValue()),
                    Time.valueOf(tfHeure.getText()),
                    preFilledLampadaireId != null ? preFilledLampadaireId : Integer.parseInt(tfLampadaireId.getText()),
                    Integer.parseInt(tfTechnicienId.getText()),
                    preFilledReclamationId != null ? preFilledReclamationId :
                            (tfReclamationId.getText().isEmpty() ? null : Integer.parseInt(tfReclamationId.getText()))
            );
            serviceIntervention.add(intervention);
            loadData();
            clearForm();
            showSuccessFeedback();
        } catch (Exception e) {
            showAlert("Erreur d'ajout", e.getMessage());
        }
    }

    @FXML
    private void handleUpdate() {
        if (selectedIntervention == null) {
            showAlert("Erreur", "Veuillez sélectionner une intervention à modifier");
            return;
        }
        try {
            validateInputs();
            selectedIntervention.setTypeIntervention(cbType.getValue());
            selectedIntervention.setDescription(tfDescription.getText());
            selectedIntervention.setEtat(cbEtat.getValue());
            selectedIntervention.setDateIntervention(Date.valueOf(dpDate.getValue()));
            selectedIntervention.setHeureIntervention(Time.valueOf(tfHeure.getText()));
            selectedIntervention.setLampadaireId(Integer.parseInt(tfLampadaireId.getText()));
            selectedIntervention.setTechnicienId(Integer.parseInt(tfTechnicienId.getText()));
            selectedIntervention.setID_reclamation(tfReclamationId.getText().isEmpty() ? null :
                    Integer.parseInt(tfReclamationId.getText()));

            serviceIntervention.update(selectedIntervention);
            loadData();
            clearForm();
            showSuccessFeedback();
        } catch (Exception e) {
            showAlert("Erreur de modification", e.getMessage());
        }
    }

    @FXML
    private void handleDelete() {
        if (selectedIntervention == null) {
            showAlert("Erreur", "Veuillez sélectionner une intervention à supprimer");
            return;
        }
        if (showConfirmation("Confirmation", "Voulez-vous vraiment supprimer cette intervention ?")) {
            try {
                serviceIntervention.delete(selectedIntervention);
                loadData();
                clearForm();
                showSuccessFeedback();
            } catch (Exception e) {
                showAlert("Erreur de suppression", e.getMessage());
            }
        }
    }

    @FXML
    private void handleClear() {
        clearForm();
    }

    private void validateInputs() throws Exception {
        if (cbType.getValue() == null ||
                tfDescription.getText().isEmpty() ||
                cbEtat.getValue() == null ||
                dpDate.getValue() == null ||
                tfHeure.getText().isEmpty() ||
                tfLampadaireId.getText().isEmpty() ||
                tfTechnicienId.getText().isEmpty()) {
            throw new Exception("Tous les champs obligatoires doivent être remplis");
        }

        LocalDate selectedDate = dpDate.getValue();
        if (selectedDate.isBefore(LocalDate.now())) {
            throw new Exception("La date d'intervention ne peut pas être antérieure à aujourd'hui");
        }

        if (!tfHeure.getText().matches("^([0-1]?\\d|2[0-3]):([0-5]\\d):([0-5]\\d)$")) {
            throw new Exception("Format d'heure invalide (HH:MM:SS)");
        }

        validatePositiveId(tfTechnicienId, "Technicien");
        if (preFilledLampadaireId == null) {
            validatePositiveId(tfLampadaireId, "Lampadaire");
        }
        if (preFilledReclamationId == null && !tfReclamationId.getText().isEmpty()) {
            validatePositiveId(tfReclamationId, "Réclamation");
        }
    }

    private void validatePositiveId(TextField field, String fieldName) throws Exception {
        try {
            int id = Integer.parseInt(field.getText());
            if (id <= 0) {
                throw new Exception("L'ID " + fieldName + " doit être un nombre positif");
            }
        } catch (NumberFormatException e) {
            throw new Exception("L'ID " + fieldName + " doit être un nombre valide");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.initStyle(StageStyle.TRANSPARENT);
        alert.getDialogPane().getScene().getRoot().setStyle(
                "-fx-background-color: rgba(255,255,255,0.95);" +
                        "-fx-background-radius: 16;" +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 16, 0, 4, 4);"
        );
        alert.getDialogPane().setOpacity(0);
        Timeline fadeIn = new Timeline(
                new KeyFrame(Duration.millis(200),
                        new KeyValue(alert.getDialogPane().opacityProperty(), 1)
                )
        );
        fadeIn.play();
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private boolean showConfirmation(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.initStyle(StageStyle.TRANSPARENT);
        alert.getDialogPane().getScene().getRoot().setStyle(
                "-fx-background-color: rgba(255,255,255,0.95);" +
                        "-fx-background-radius: 16;" +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 16, 0, 4, 4);"
        );
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        return alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK;
    }

    private void showSuccessFeedback() {
        Pane root = (Pane) cardContainer.getParent();
        Label feedback = new Label("✓ Opération réussie !");
        feedback.setStyle(
                "-fx-background-color: linear-gradient(to right, #34a853, #2d8a4a);" +
                        "-fx-text-fill: white;" +
                        "-fx-padding: 12 24;" +
                        "-fx-background-radius: 24;" +
                        "-fx-font-weight: 700;"
        );
        feedback.setTranslateY(-50);
        feedback.setOpacity(0);
        root.getChildren().add(feedback);
        Timeline animation = new Timeline(
                new KeyFrame(Duration.millis(300),
                        new KeyValue(feedback.translateYProperty(), 20),
                        new KeyValue(feedback.opacityProperty(), 1)
                ),
                new KeyFrame(Duration.millis(2000),
                        new KeyValue(feedback.opacityProperty(), 0)
                )
        );
        animation.setOnFinished(e -> root.getChildren().remove(feedback));
        animation.play();
    }

    private VBox createInterventionCard(Intervention intervention) {
        VBox card = new VBox(15);
        card.getStyleClass().add("card");

        HBox header = new HBox(10);
        FontIcon icon = new FontIcon(FontAwesomeSolid.TOOLS);
        icon.setIconSize(24);
        icon.setIconColor(Color.web("#1a73e8"));

        Label title = new Label("Intervention #" + intervention.getID_intervention());
        title.setStyle("-fx-font-size: 18; -fx-text-fill: #202124;");

        header.getChildren().addAll(icon, title);

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String dateFormatted = intervention.getDateIntervention().toLocalDate().format(dateFormatter);

        VBox content = new VBox(8);
        content.getChildren().addAll(
                createInfoRow(FontAwesomeSolid.WRENCH, "Type : " + intervention.getTypeIntervention()),
                createInfoRow(FontAwesomeSolid.INFO_CIRCLE, "Description : " + intervention.getDescription()),
                createInfoRow(FontAwesomeSolid.INFO_CIRCLE, "État : " + intervention.getEtat()),
                createInfoRow(FontAwesomeSolid.CLOCK, "Heure : " + intervention.getHeureIntervention()),
                createInfoRow(FontAwesomeSolid.CALENDAR, "Date : " + dateFormatted),
                createInfoRow(FontAwesomeSolid.LIGHTBULB, "Lampadaire ID : " + intervention.getLampadaireId()),
                createInfoRow(FontAwesomeSolid.USER, "Technicien ID : " + intervention.getTechnicienId()),
                createInfoRow(FontAwesomeSolid.EXCLAMATION_TRIANGLE, "Réclamation ID : " +
                        (intervention.getID_reclamation() != null ? intervention.getID_reclamation() : "N/A"))
        );

        HBox buttons = new HBox(10);
        Button btnModifier = createIconButton("Modifier", FontAwesomeSolid.PENCIL_ALT, "#4a90e2");
        btnModifier.setOnAction(e -> fillForm(intervention));

        Button btnSupprimer = createIconButton("Supprimer", FontAwesomeSolid.TRASH, "#ff6b6b");
        btnSupprimer.setOnAction(e -> handleDeleteIntervention(intervention));

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
        button.setStyle(
                "-fx-background-color: " + color + ";" +
                        "-fx-text-fill: white;" +
                        "-fx-background-radius: 8;" +
                        "-fx-padding: 8 16;"
        );
        button.setContentDisplay(ContentDisplay.LEFT);
        button.setGraphicTextGap(8);
        return button;
    }

    @FXML
    private void handleNavigateToReclamations(ActionEvent event) {
        loadView("GestionReclamation.fxml", event);
    }

    @FXML
    private void handleShowInterventions(ActionEvent event) {
        loadData();
        showSuccessFeedback();
    }

    @FXML
    private void handleBack(ActionEvent event) {
        loadView("MainMenu.fxml", event);
    }

    private void loadView(String fxmlFile, ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/" + fxmlFile));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            showAlert("Erreur de navigation", "Impossible de charger la vue : " + fxmlFile);
        }
    }
}