package tn.esprit.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import tn.esprit.models.Camera;
import tn.esprit.services.ServiceCamera;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class GestionCameraController implements Initializable {

    @FXML private TextField tfUrlFlux;
    @FXML private TextField tfIpAddress; // Ajout
    @FXML private Label lblUrlFluxError;
    @FXML private Label lblIpError; // Ajout
    @FXML private ListView<Camera> lvCameras;

    private final ServiceCamera serviceCamera = new ServiceCamera();
    private final ObservableList<Camera> cameras = FXCollections.observableArrayList();
    private Camera selectedCamera;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        tfUrlFlux.textProperty().addListener((obs, oldVal, newVal) -> resetFieldError(tfUrlFlux, lblUrlFluxError));
        tfIpAddress.textProperty().addListener((obs, oldVal, newVal) -> resetFieldError(tfIpAddress, lblIpError)); // Ajout
        lvCameras.setItems(cameras);
        lvCameras.setCellFactory(param -> new ListCell<Camera>() {
            @Override
            protected void updateItem(Camera item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText("Caméra " + item.getIdCamera() + " - " + item.getUrlFlux() + " (IP: " + item.getIpAddress() + ")");
                }
            }
        });
        lvCameras.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                fillForm(newVal);
            }
        });
        loadData();
    }

    private void loadData() {
        cameras.setAll(serviceCamera.getAll());
    }

    private void fillForm(Camera camera) {
        selectedCamera = camera;
        tfUrlFlux.setText(camera.getUrlFlux());
        tfIpAddress.setText(camera.getIpAddress()); // Ajout
    }

    private void clearForm() {
        tfUrlFlux.clear();
        tfIpAddress.clear(); // Ajout
        selectedCamera = null;
    }

    @FXML
    private void handleAdd() {
        try {
            validateInputs();
            Camera camera = new Camera();
            camera.setUrlFlux(tfUrlFlux.getText());
            camera.setIpAddress(tfIpAddress.getText()); // Ajout
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
            selectedCamera.setIpAddress(tfIpAddress.getText()); // Ajout
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
        if (showConfirmation("Confirmation", "Voulez-vous supprimer cette caméra ?")) {
            serviceCamera.delete(selectedCamera);
            loadData();
            clearForm();
            showSuccessFeedback();
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
            showAlert("Erreur", "Impossible de retourner au menu principal");
        }
    }

    @FXML
    private void handleNavigateToLampadaires(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/GestionLampadaire.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de charger la gestion des lampadaires");
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

        if (tfIpAddress.getText().isEmpty()) { // Validation pour l'IP
            showError(tfIpAddress, lblIpError, "L'adresse IP est requise");
            hasError = true;
        } else if (!tfIpAddress.getText().matches("\\b(?:\\d{1,3}\\.){3}\\d{1,3}\\b")) { // Validation simple d'une adresse IP
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
        tfIpAddress.getStyleClass().remove("error-field"); // Ajout
        lblUrlFluxError.setVisible(false);
        lblIpError.setVisible(false); // Ajout
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

    private void showSuccessFeedback() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Succès");
        alert.setHeaderText(null);
        alert.setContentText("Opération réussie !");
        alert.showAndWait();
    }
}