package tn.esprit.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import tn.esprit.models.Intervention;
import tn.esprit.models.Intervention.TypeIntervention;
import tn.esprit.services.ServiceIntervention;
import java.net.URL;
import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class AddInterventionController implements Initializable {

    @FXML private ComboBox<TypeIntervention> cbType;
    @FXML private TextField tfDescription;
    @FXML private ComboBox<String> cbEtat;
    @FXML private DatePicker dpDate;
    @FXML private TextField tfHeure;
    @FXML private TextField tfTechnicienId;

    private final ServiceIntervention serviceIntervention = new ServiceIntervention();
    private Integer reclamationId;
    private Integer lampadaireId;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cbType.setItems(FXCollections.observableArrayList(TypeIntervention.values()));
        cbEtat.setItems(FXCollections.observableArrayList("En attente", "En cours", "Terminée", "Annulée"));
    }

    public void setReclamationAndLampadaireId(int reclamationId, int lampadaireId) {
        this.reclamationId = reclamationId;
        this.lampadaireId = lampadaireId;
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
                    lampadaireId,
                    Integer.parseInt(tfTechnicienId.getText()),
                    reclamationId
            );
            serviceIntervention.add(intervention);
            Stage stage = (Stage) tfDescription.getScene().getWindow();
            stage.close(); // Close the dialog on success
        } catch (Exception e) {
            showAlert("Erreur d'ajout", e.getMessage());
        }
    }

    @FXML
    private void handleCancel() {
        Stage stage = (Stage) tfDescription.getScene().getWindow();
        stage.close(); // Close the dialog on cancel
    }

    private void clearForm() {
        cbType.getSelectionModel().clearSelection();
        tfDescription.clear();
        cbEtat.getSelectionModel().clearSelection();
        dpDate.setValue(null);
        tfHeure.clear();
        tfTechnicienId.clear();
    }

    private void validateInputs() throws Exception {
        if (cbType.getValue() == null ||
                tfDescription.getText().isEmpty() ||
                cbEtat.getValue() == null ||
                dpDate.getValue() == null ||
                tfHeure.getText().isEmpty() ||
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
        if (reclamationId == null || lampadaireId == null) {
            throw new Exception("Les IDs de réclamation et de lampadaire doivent être définis");
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
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}