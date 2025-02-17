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
import tn.esprit.models.Lampadaire.EtatLampadaire;
import tn.esprit.services.ServiceLampadaire;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class GestionLampadaireController implements Initializable {

    @FXML private TextField tfType;
    @FXML private TextField tfPuissance;
    @FXML private ComboBox<EtatLampadaire> cbEtat;
    @FXML private DatePicker dpDateInstallation;
    @FXML private TextField tfIdZone;
    @FXML private FlowPane cardContainer;
    @FXML private ScrollPane scrollPane;
    private final ServiceLampadaire serviceLampadaire = new ServiceLampadaire();
    private final ObservableList<Lampadaire> lampadaires = FXCollections.observableArrayList();
    private Lampadaire selectedLampadaire;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cbEtat.setItems(FXCollections.observableArrayList(EtatLampadaire.values()));
        scrollPane.setFitToWidth(true);
        cardContainer.setHgap(20);
        cardContainer.setVgap(20);
        cardContainer.setPadding(new Insets(20));
        loadData();
    }

    private void loadData() {
        lampadaires.setAll(serviceLampadaire.getAll());
        cardContainer.getChildren().clear();
        lampadaires.forEach(lampadaire -> cardContainer.getChildren().add(createLampadaireCard(lampadaire)));
    }

    private VBox createLampadaireCard(Lampadaire lampadaire) {
        VBox card = new VBox(10);
        card.getStyleClass().add("card");
        card.setPrefWidth(300);
        card.setPadding(new Insets(15));
        card.setOpacity(0);
        card.setTranslateY(20);
        Timeline fadeIn = new Timeline(new KeyFrame(Duration.millis(300), new KeyValue(card.opacityProperty(), 1), new KeyValue(card.translateYProperty(), 0)));
        fadeIn.play();
        DropShadow hoverEffect = new DropShadow(15, Color.web("#4361ee33"));
        hoverEffect.setSpread(0.2);
        card.setOnMouseEntered(e -> { card.setEffect(hoverEffect); card.setTranslateY(-4); });
        card.setOnMouseExited(e -> { card.setEffect(null); card.setTranslateY(0); });
        Text titleText = new Text("Lampadaire #" + lampadaire.getIdLamp());
        titleText.setFont(Font.font("Segoe UI", 16));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String dateInstallation = (lampadaire.getDateInstallation() != null) ? lampadaire.getDateInstallation().format(formatter) : "N/A";
        VBox content = new VBox(8);
        content.getChildren().addAll(
                createInfoText("Type", lampadaire.getTypeLampadaire()),
                createInfoText("Puissance", lampadaire.getPuissance() + " W"),
                createInfoText("État", lampadaire.getEtat().toString()),
                createInfoText("Installation", dateInstallation),
                createInfoText("Zone", String.valueOf(lampadaire.getIdZone()))
        );
        HBox buttonBox = new HBox(10);
        Button editButton = createStyledButton("Modifier", "#4361ee");
        Button deleteButton = createStyledButton("Supprimer", "#ef476f");
        editButton.setOnAction(e -> fillForm(lampadaire));
        deleteButton.setOnAction(e -> handleDeleteLampadaire(lampadaire));
        buttonBox.getChildren().addAll(editButton, deleteButton);
        card.getChildren().addAll(titleText, new Separator(), content, buttonBox);
        return card;
    }

    private Button createStyledButton(String text, String color) {
        Button button = new Button(text);
        button.setStyle("-fx-background-color: " + color + ";-fx-text-fill: white;-fx-background-radius: 8;-fx-padding: 8 16;");
        return button;
    }

    private Text createInfoText(String label, String value) {
        Text text = new Text(label + ": " + value);
        text.setFont(Font.font("Segoe UI", 14));
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
            selectedLampadaire.setIdZone(Integer.parseInt(tfIdZone.getText()));
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
    private void handleClear() {
        clearForm();
    }

    private void validateInputs() throws Exception {}

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
        feedback.setStyle("-fx-background-color: #06d6a0; -fx-text-fill: white; -fx-padding: 8 16; -fx-background-radius: 20; -fx-font-weight: 600;");
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
}