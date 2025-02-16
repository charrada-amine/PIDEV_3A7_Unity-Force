package tn.esprit.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import tn.esprit.models.Lampadaire;
import tn.esprit.models.Lampadaire.EtatLampadaire;
import tn.esprit.services.ServiceLampadaire;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class GestionLampadaireController implements Initializable {

    @FXML private TextField tfType;
    @FXML private TextField tfPuissance;
    @FXML private ComboBox<EtatLampadaire> cbEtat;
    @FXML private DatePicker dpDateInstallation;
    @FXML private TextField tfIdZone;

    @FXML private TableView<Lampadaire> tableView;
    @FXML private TableColumn<Lampadaire, Integer> colId;
    @FXML private TableColumn<Lampadaire, String> colType;
    @FXML private TableColumn<Lampadaire, Float> colPuissance;
    @FXML private TableColumn<Lampadaire, String> colEtat;
    @FXML private TableColumn<Lampadaire, LocalDate> colDate;
    @FXML private TableColumn<Lampadaire, Integer> colZone;

    private final ServiceLampadaire serviceLampadaire = new ServiceLampadaire();
    private final ObservableList<Lampadaire> lampadaires = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialiser ComboBox
        cbEtat.setItems(FXCollections.observableArrayList(EtatLampadaire.values()));

        // Configurer TableView
        colId.setCellValueFactory(cellData -> cellData.getValue().idLampProperty().asObject());
        colType.setCellValueFactory(cellData -> cellData.getValue().typeLampadaireProperty());
        colPuissance.setCellValueFactory(cellData -> cellData.getValue().puissanceProperty().asObject());
        colEtat.setCellValueFactory(cellData -> cellData.getValue().etatProperty().asString());
        colDate.setCellValueFactory(cellData -> cellData.getValue().dateInstallationProperty());
        colZone.setCellValueFactory(cellData -> cellData.getValue().idZoneProperty().asObject());

        loadData();

        // Selection listener
        tableView.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    if (newSelection != null) {
                        fillForm(newSelection);
                    }
                });
    }

    private void loadData() {
        lampadaires.setAll(serviceLampadaire.getAll());
        tableView.setItems(lampadaires);
    }

    private void fillForm(Lampadaire lampadaire) {
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
    }

    @FXML
    private void handleAdd() {
        try {
            Lampadaire lampadaire = new Lampadaire();
            lampadaire.setTypeLampadaire(tfType.getText());
            lampadaire.setPuissance(Float.parseFloat(tfPuissance.getText()));
            lampadaire.setEtat(cbEtat.getValue());
            lampadaire.setDateInstallation(dpDateInstallation.getValue());
            lampadaire.setIdZone(Integer.parseInt(tfIdZone.getText()));

            serviceLampadaire.add(lampadaire);
            loadData();
            clearForm();
        } catch (Exception e) {
            showAlert("Erreur d'ajout", e.getMessage());
        }
    }

    @FXML
    private void handleUpdate() {
        Lampadaire selected = tableView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            try {
                selected.setTypeLampadaire(tfType.getText());
                selected.setPuissance(Float.parseFloat(tfPuissance.getText()));
                selected.setEtat(cbEtat.getValue());
                selected.setDateInstallation(dpDateInstallation.getValue());
                selected.setIdZone(Integer.parseInt(tfIdZone.getText()));

                serviceLampadaire.update(selected);
                loadData();
            } catch (Exception e) {
                showAlert("Erreur de modification", e.getMessage());
            }
        } else {
            showAlert("Aucune sélection", "Veuillez sélectionner un lampadaire");
        }
    }

    @FXML
    private void handleDelete() {
        Lampadaire selected = tableView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            try {
                serviceLampadaire.delete(selected);
                lampadaires.remove(selected);
                clearForm();
            } catch (Exception e) {
                showAlert("Erreur de suppression", e.getMessage());
            }
        } else {
            showAlert("Aucune sélection", "Veuillez sélectionner un lampadaire");
        }
    }

    @FXML
    private void handleClear() {
        clearForm();
        tableView.getSelectionModel().clearSelection();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}