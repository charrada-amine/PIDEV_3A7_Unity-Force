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
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;

public class GestionInterventionController implements Initializable {

    @FXML private ComboBox<TypeIntervention> cbType;
    @FXML private TextField tfDescription;
    @FXML private TextField tfEtat;
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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cbType.setItems(FXCollections.observableArrayList(TypeIntervention.values()));
        scrollPane.setFitToWidth(true);
        cardContainer.setHgap(20);
        cardContainer.setVgap(20);
        cardContainer.setPadding(new Insets(20));
        Font.loadFont(getClass().getResourceAsStream("/fonts/Roboto-Regular.ttf"), 14);
        loadData();
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
        tfEtat.setText(intervention.getEtat());
        dpDate.setValue(intervention.getDateIntervention().toLocalDate());
        tfHeure.setText(intervention.getHeureIntervention().toString());
        tfLampadaireId.setText(String.valueOf(intervention.getLampadaireId()));
        tfTechnicienId.setText(String.valueOf(intervention.getTechnicienId()));
        tfReclamationId.setText(intervention.getID_reclamation() != null ?
                String.valueOf(intervention.getID_reclamation()) : "");
    }

    private void clearForm() {
        cbType.getSelectionModel().clearSelection();
        tfDescription.clear();
        tfEtat.clear();
        dpDate.setValue(null);
        tfHeure.clear();
        tfLampadaireId.clear();
        tfTechnicienId.clear();
        tfReclamationId.clear();
        selectedIntervention = null;
    }

    @FXML
    private void handleAdd() {
        try {
            validateInputs();
            Intervention intervention = new Intervention(
                    cbType.getValue(),
                    tfDescription.getText(),
                    tfEtat.getText(),
                    Date.valueOf(dpDate.getValue()),
                    Time.valueOf(tfHeure.getText()),
                    Integer.parseInt(tfLampadaireId.getText()),
                    Integer.parseInt(tfTechnicienId.getText()),
                    tfReclamationId.getText().isEmpty() ? null : Integer.parseInt(tfReclamationId.getText())
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
            selectedIntervention.setEtat(tfEtat.getText());
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
                tfEtat.getText().isEmpty() ||
                dpDate.getValue() == null ||
                tfHeure.getText().isEmpty() ||
                tfLampadaireId.getText().isEmpty() ||
                tfTechnicienId.getText().isEmpty()) {
            throw new Exception("Tous les champs obligatoires doivent être remplis");
        }

        try {
            Time.valueOf(tfHeure.getText());
        } catch (Exception e) {
            throw new Exception("Format d'heure invalide (HH:MM:SS)");
        }

        try {
            Integer.parseInt(tfLampadaireId.getText());
            Integer.parseInt(tfTechnicienId.getText());
            if (!tfReclamationId.getText().isEmpty()) {
                Integer.parseInt(tfReclamationId.getText());
            }
        } catch (NumberFormatException e) {
            throw new Exception("Les IDs doivent être des nombres valides");
        }
    }

    // Alert and Feedback Methods (Keep these identical across controllers)
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

    // UI Components
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

    // Navigation Methods
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