package tn.esprit.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import tn.esprit.models.Zone;
import tn.esprit.services.ServiceZone;

import java.net.URL;
import java.util.ResourceBundle;

public class GestionZoneController implements Initializable {

    @FXML private TextField tfNom;
    @FXML private TextField tfDescription;
    @FXML private TextField tfSurface;
    @FXML private TextField tfNombreLampadaires;
    @FXML private TextField tfNombreCitoyens;

    @FXML private TableView<Zone> tableView;
    @FXML private TableColumn<Zone, Integer> colId;
    @FXML private TableColumn<Zone, String> colNom;
    @FXML private TableColumn<Zone, String> colDescription;
    @FXML private TableColumn<Zone, Float> colSurface;
    @FXML private TableColumn<Zone, Integer> colLampadaires;
    @FXML private TableColumn<Zone, Integer> colCitoyens;

    private final ServiceZone serviceZone = new ServiceZone();
    private final ObservableList<Zone> zones = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configureTableColumns();
        loadData();
        setupSelectionListener();
    }

    private void configureTableColumns() {
        colId.setCellValueFactory(new PropertyValueFactory<>("idZone"));
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colSurface.setCellValueFactory(new PropertyValueFactory<>("surface"));
        colLampadaires.setCellValueFactory(new PropertyValueFactory<>("nombreLampadaires"));
        colCitoyens.setCellValueFactory(new PropertyValueFactory<>("nombreCitoyens"));
    }

    private void loadData() {
        zones.setAll(serviceZone.getAll());
        tableView.setItems(zones);
    }

    private void setupSelectionListener() {
        tableView.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    if (newSelection != null) {
                        fillForm(newSelection);
                    }
                });
    }

    private void fillForm(Zone zone) {
        tfNom.setText(zone.getNom());
        tfDescription.setText(zone.getDescription());
        tfSurface.setText(String.valueOf(zone.getSurface()));
        tfNombreLampadaires.setText(String.valueOf(zone.getNombreLampadaires()));
        tfNombreCitoyens.setText(String.valueOf(zone.getNombreCitoyens()));
    }

    private void clearForm() {
        tfNom.clear();
        tfDescription.clear();
        tfSurface.clear();
        tfNombreLampadaires.clear();
        tfNombreCitoyens.clear();
    }

    @FXML
    private void handleAdd() {
        try {
            Zone zone = new Zone();
            populateZoneFromFields(zone);
            serviceZone.add(zone);
            loadData();
            clearForm();
        } catch (Exception e) {
            showAlert("Erreur d'ajout", e.getMessage());
        }
    }

    @FXML
    private void handleUpdate() {
        Zone selected = tableView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            try {
                populateZoneFromFields(selected);
                serviceZone.update(selected);
                loadData();
            } catch (Exception e) {
                showAlert("Erreur de modification", e.getMessage());
            }
        } else {
            showAlert("Aucune sélection", "Veuillez sélectionner une zone");
        }
    }

    @FXML
    private void handleDelete() {
        Zone selected = tableView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            try {
                serviceZone.delete(selected);
                zones.remove(selected);
                clearForm();
            } catch (Exception e) {
                showAlert("Erreur de suppression", e.getMessage());
            }
        } else {
            showAlert("Aucune sélection", "Veuillez sélectionner une zone");
        }
    }

    @FXML
    private void handleClear() {
        clearForm();
        tableView.getSelectionModel().clearSelection();
    }

    private void populateZoneFromFields(Zone zone) throws NumberFormatException {
        zone.setNom(tfNom.getText());
        zone.setDescription(tfDescription.getText());
        zone.setSurface(Float.parseFloat(tfSurface.getText()));
        zone.setNombreLampadaires(Integer.parseInt(tfNombreLampadaires.getText()));
        zone.setNombreCitoyens(Integer.parseInt(tfNombreCitoyens.getText()));
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}