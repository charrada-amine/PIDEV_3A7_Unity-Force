package tn.esprit.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import tn.esprit.models.*;
import tn.esprit.services.*;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ResourceBundle;

public class GestionDonneesController implements Initializable {

    @FXML
    private ComboBox<String> typeCapteurComboBox;
    @FXML
    private DatePicker dateCollectePicker;
    @FXML
    private TextField heureCollecteField;
    @FXML
    private TextField capteurIdField;
    @FXML
    private TextField valeurField;
    @FXML
    private TableView<Donnee> donneeTable;
    @FXML
    private TableColumn<Donnee, Integer> idColumn;
    @FXML
    private TableColumn<Donnee, LocalDate> dateCollecteColumn;
    @FXML
    private TableColumn<Donnee, LocalTime> heureCollecteColumn;
    @FXML
    private TableColumn<Donnee, Integer> capteurIdColumn;
    @FXML
    private TableColumn<Donnee, String> valeurColumn;
    @FXML
    private TableColumn<Donnee, String> typeDonneeColumn;
    @FXML
    private Label valeurIndicationLabel;

    private final ServiceMouvement serviceMouvement = new ServiceMouvement();
    private final ServiceTemperature serviceTemperature = new ServiceTemperature();
    private final ServiceConsommation serviceConsommation = new ServiceConsommation();
    private final ServiceLuminosite serviceLuminosite = new ServiceLuminosite();
    private final ObservableList<Donnee> donneeList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        typeCapteurComboBox.getItems().addAll("Mouvement", "Température", "Consommation", "Luminosité");
        updateValeurIndication(); // Mise à jour initiale

        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        dateCollecteColumn.setCellValueFactory(new PropertyValueFactory<>("dateCollecte"));
        heureCollecteColumn.setCellValueFactory(new PropertyValueFactory<>("heureCollecte"));
        capteurIdColumn.setCellValueFactory(new PropertyValueFactory<>("capteurId"));

        valeurColumn.setCellValueFactory(data -> {
            Donnee d = data.getValue();
            if (d instanceof DonneeMouvement) return javafx.beans.binding.Bindings.createObjectBinding(() -> String.valueOf(((DonneeMouvement) d).getValeur()));
            if (d instanceof DonneeTemperature) return javafx.beans.binding.Bindings.createObjectBinding(() -> String.valueOf(((DonneeTemperature) d).getValeur()));
            if (d instanceof DonneeConsommation) return javafx.beans.binding.Bindings.createObjectBinding(() -> String.valueOf(((DonneeConsommation) d).getValeur()));
            if (d instanceof DonneeLuminosite) return javafx.beans.binding.Bindings.createObjectBinding(() -> String.valueOf(((DonneeLuminosite) d).getValeur()));
            return javafx.beans.binding.Bindings.createObjectBinding(() -> "N/A");
        });

        typeDonneeColumn.setCellValueFactory(data -> {
            Donnee d = data.getValue();
            if (d instanceof DonneeMouvement) return javafx.beans.binding.Bindings.createObjectBinding(() -> "Mouvement");
            if (d instanceof DonneeTemperature) return javafx.beans.binding.Bindings.createObjectBinding(() -> "Température");
            if (d instanceof DonneeConsommation) return javafx.beans.binding.Bindings.createObjectBinding(() -> "Consommation");
            if (d instanceof DonneeLuminosite) return javafx.beans.binding.Bindings.createObjectBinding(() -> "Luminosité");
            return javafx.beans.binding.Bindings.createObjectBinding(() -> "Inconnu");
        });

        donneeTable.setItems(donneeList);
        handleRefresh();
    }

    @FXML
    public void handleAddDonnee() {
        String type = typeCapteurComboBox.getValue();
        LocalDate date = dateCollectePicker.getValue();
        LocalTime heure = LocalTime.parse(heureCollecteField.getText());
        int capteurId = Integer.parseInt(capteurIdField.getText());
        String valeurStr = valeurField.getText();

        if (type != null && date != null && heure != null && !valeurStr.isEmpty()) {
            switch (type) {
                case "Mouvement":
                    serviceMouvement.add(new DonneeMouvement(0, date, heure, capteurId, Boolean.parseBoolean(valeurStr)));
                    break;
                case "Température":
                    serviceTemperature.add(new DonneeTemperature(0, date, heure, capteurId, Float.parseFloat(valeurStr)));
                    break;
                case "Consommation":
                    serviceConsommation.add(new DonneeConsommation(0, date, heure, capteurId, Float.parseFloat(valeurStr)));
                    break;
                case "Luminosité":
                    serviceLuminosite.add(new DonneeLuminosite(0, date, heure, capteurId, Integer.parseInt(valeurStr)));
                    break;
            }
            handleRefresh();
            clearFields();
        }
    }

    @FXML
    public void handleUpdateDonnee() {
        Donnee selectedItem = donneeTable.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            LocalDate date = dateCollectePicker.getValue();
            LocalTime heure = LocalTime.parse(heureCollecteField.getText());
            int capteurId = Integer.parseInt(capteurIdField.getText());
            String valeurStr = valeurField.getText();

            if (selectedItem instanceof DonneeMouvement) {
                serviceMouvement.update(new DonneeMouvement(selectedItem.getId(), date, heure, capteurId, Boolean.parseBoolean(valeurStr)));
            } else if (selectedItem instanceof DonneeTemperature) {
                serviceTemperature.update(new DonneeTemperature(selectedItem.getId(), date, heure, capteurId, Float.parseFloat(valeurStr)));
            } else if (selectedItem instanceof DonneeConsommation) {
                serviceConsommation.update(new DonneeConsommation(selectedItem.getId(), date, heure, capteurId, Float.parseFloat(valeurStr)));
            } else if (selectedItem instanceof DonneeLuminosite) {
                serviceLuminosite.update(new DonneeLuminosite(selectedItem.getId(), date, heure, capteurId, Integer.parseInt(valeurStr)));
            }
            handleRefresh();
        }
    }

    @FXML
    public void handleDeleteDonnee() {
        Donnee selectedItem = donneeTable.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            if (selectedItem instanceof DonneeMouvement) serviceMouvement.delete(selectedItem.getId());
            else if (selectedItem instanceof DonneeTemperature) serviceTemperature.delete(selectedItem.getId());
            else if (selectedItem instanceof DonneeConsommation) serviceConsommation.delete(selectedItem.getId());
            else if (selectedItem instanceof DonneeLuminosite) serviceLuminosite.delete(selectedItem.getId());

            handleRefresh();
        }
    }

    @FXML
    public void handleRefresh() {
        donneeList.clear();
        donneeList.addAll(serviceMouvement.getAll());
        donneeList.addAll(serviceTemperature.getAll());
        donneeList.addAll(serviceConsommation.getAll());
        donneeList.addAll(serviceLuminosite.getAll());
    }

    @FXML
    public void handleTableClick() {
        Donnee selectedItem = donneeTable.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            dateCollectePicker.setValue(selectedItem.getDateCollecte());
            heureCollecteField.setText(selectedItem.getHeureCollecte().toString());
            capteurIdField.setText(String.valueOf(selectedItem.getCapteurId()));
            valeurField.setText(selectedItem instanceof DonneeMouvement ? String.valueOf(((DonneeMouvement) selectedItem).getValeur()) :
                    selectedItem instanceof DonneeTemperature ? String.valueOf(((DonneeTemperature) selectedItem).getValeur()) :
                            selectedItem instanceof DonneeConsommation ? String.valueOf(((DonneeConsommation) selectedItem).getValeur()) :
                                    String.valueOf(((DonneeLuminosite) selectedItem).getValeur()));
        }
    }

    private void clearFields() {
        dateCollectePicker.setValue(null);
        heureCollecteField.clear();
        capteurIdField.clear();
        valeurField.clear();
    }

    @FXML
    private void updateValeurIndication() {
        String type = typeCapteurComboBox.getValue();
        if (type == null) {
            valeurIndicationLabel.setText("Sélectionnez un type de capteur");
            return;
        }

        switch (type) {
            case "Mouvement":
                valeurIndicationLabel.setText("Valeur booléenne (true/false)");
                break;
            case "Température":
                valeurIndicationLabel.setText("Valeur en °C (Degrés Celsius)");
                break;
            case "Consommation":
                valeurIndicationLabel.setText("Valeur en W (Watts)");
                break;
            case "Luminosité":
                valeurIndicationLabel.setText("Valeur en Lux");
                break;
            default:
                valeurIndicationLabel.setText("Valeur invalide");
        }
    }
}
