package tn.esprit.controllers;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import tn.esprit.models.Reclamation;
import tn.esprit.services.ServiceReclamation;
import java.net.URL;
import java.sql.Date;
import java.sql.Time;
import java.util.ResourceBundle;

public class SignalerReclamationController implements Initializable {

    @FXML private TextField tfDescription;
    @FXML private DatePicker dpDate;
    @FXML private TextField tfHeure;
    @FXML private ComboBox<String> cbStatut;
    @FXML private TextField tfCitoyenId;

    private final ServiceReclamation serviceReclamation = new ServiceReclamation();
    private int lampadaireId; // ID du lampadaire récupéré automatiquement

    // Méthode pour initialiser l'ID du lampadaire depuis le contrôleur appelant
    public void setLampadaireId(int lampadaireId) {
        this.lampadaireId = lampadaireId;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cbStatut.setItems(FXCollections.observableArrayList("Ouvert", "En cours", "Résolu", "Fermé"));
        cbStatut.setValue("Ouvert"); // Statut par défaut pour une nouvelle réclamation
    }

    @FXML
    private void handleAdd() {
        try {
            validateInputs();
            Reclamation reclamation = new Reclamation(
                    tfDescription.getText(),
                    Date.valueOf(dpDate.getValue()),
                    Time.valueOf(tfHeure.getText()),
                    cbStatut.getValue(),
                    lampadaireId, // ID du lampadaire récupéré automatiquement
                    Integer.parseInt(tfCitoyenId.getText())
            );
            serviceReclamation.add(reclamation);
            clearForm();
            showSuccessFeedback();
        } catch (Exception e) {
            showAlert("Erreur d'ajout", e.getMessage());
        }
    }

    @FXML
    private void handleClear() {
        clearForm();
    }

    private void validateInputs() throws Exception {
        if (tfDescription.getText().isEmpty() ||
                dpDate.getValue() == null ||
                tfHeure.getText().isEmpty() ||
                cbStatut.getValue() == null ||
                tfCitoyenId.getText().isEmpty()) {
            throw new Exception("Tous les champs obligatoires doivent être remplis");
        }

        try {
            Time.valueOf(tfHeure.getText());
        } catch (Exception e) {
            throw new Exception("Format d'heure invalide (HH:MM:SS)");
        }

        try {
            Integer.parseInt(tfCitoyenId.getText());
        } catch (NumberFormatException e) {
            throw new Exception("L'ID du citoyen doit être un nombre valide");
        }
    }

    private void clearForm() {
        tfDescription.clear();
        dpDate.setValue(null);
        tfHeure.clear();
        cbStatut.getSelectionModel().clearSelection();
        cbStatut.setValue("Ouvert"); // Remettre "Ouvert" par défaut
        tfCitoyenId.clear();
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

    private void showSuccessFeedback() {
        Pane root = (Pane) tfDescription.getScene().getRoot();
        Label feedback = new Label("✓ Réclamation signalée avec succès !");
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
}