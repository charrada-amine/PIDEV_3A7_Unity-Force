package tn.esprit.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import tn.esprit.models.Capteur;
import tn.esprit.models.Capteur.EtatCapteur;
import tn.esprit.models.Capteur.TypeCapteur;
import tn.esprit.services.ServiceCapteur;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class GestionCapteurController implements Initializable {

    @FXML
    private TextField idField;

    @FXML
    private ComboBox<TypeCapteur> typeComboBox;

    @FXML
    private ComboBox<EtatCapteur> etatComboBox;

    @FXML
    private DatePicker datePicker;

    @FXML
    private TextField lampadaireIdField;

    @FXML
    private TableView<Capteur> capteurTable;

    @FXML
    private TableColumn<Capteur, Integer> idColumn;

    @FXML
    private TableColumn<Capteur, TypeCapteur> typeColumn;

    @FXML
    private TableColumn<Capteur, EtatCapteur> etatColumn;

    @FXML
    private TableColumn<Capteur, LocalDate> dateColumn;

    @FXML
    private TableColumn<Capteur, Integer> lampadaireIdColumn;

    private ServiceCapteur serviceCapteur;

    private ObservableList<Capteur> capteurList;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        serviceCapteur = new ServiceCapteur();
        capteurList = FXCollections.observableArrayList();

        // Initialiser les ComboBox
        typeComboBox.setItems(FXCollections.observableArrayList(TypeCapteur.values()));
        etatComboBox.setItems(FXCollections.observableArrayList(EtatCapteur.values()));

        // Configurer les colonnes de la table
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        etatColumn.setCellValueFactory(new PropertyValueFactory<>("etat"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("dateinstallation"));
        lampadaireIdColumn.setCellValueFactory(new PropertyValueFactory<>("lampadaireId"));

        // Charger les capteurs dans la table
        loadCapteurs();
    }

    private void loadCapteurs() {
        capteurList.clear();
        capteurList.addAll(serviceCapteur.getAll());
        capteurTable.setItems(capteurList);
    }

    @FXML
    private void handleAddCapteur() {
        try {
            Capteur capteur = new Capteur(
                    0, // ID sera généré automatiquement par la base de données
                    typeComboBox.getValue(),
                    datePicker.getValue(),
                    etatComboBox.getValue(),
                    Integer.parseInt(lampadaireIdField.getText())
            );
            serviceCapteur.add(capteur);
            loadCapteurs();
            clearFields();
        } catch (Exception e) {
            showAlert("Erreur", "Veuillez remplir tous les champs correctement.");
        }
    }
    private void showSuccessAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Succès");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    @FXML
    private void handleTableClick() {
        Capteur selectedCapteur = capteurTable.getSelectionModel().getSelectedItem();
        if (selectedCapteur != null) {
            // Remplir les champs de modification avec les données du capteur sélectionné
            idField.setText(String.valueOf(selectedCapteur.getId()));
            typeComboBox.setValue(selectedCapteur.getType());
            datePicker.setValue(selectedCapteur.getDateinstallation());
            etatComboBox.setValue(selectedCapteur.getEtat());
            lampadaireIdField.setText(String.valueOf(selectedCapteur.getLampadaireId()));
        }
    }

    @FXML
    private void handleUpdateCapteur() {
        try {
            Capteur capteur = new Capteur(
                    Integer.parseInt(idField.getText()),
                    typeComboBox.getValue(),
                    datePicker.getValue(),
                    etatComboBox.getValue(),
                    Integer.parseInt(lampadaireIdField.getText())
            );
            serviceCapteur.update(capteur);
            loadCapteurs();
            clearFields();
            showSuccessAlert("Capteur mis à jour avec succès !");
        } catch (Exception e) {
            showAlert("Erreur", "Veuillez sélectionner un capteur à modifier.");
        }
    }



    @FXML
    private void handleDeleteCapteur() {
        Capteur selectedCapteur = capteurTable.getSelectionModel().getSelectedItem();
        if (selectedCapteur != null) {
            serviceCapteur.delete(selectedCapteur.getId());
            loadCapteurs();
            clearFields();
        } else {
            showAlert("Erreur", "Veuillez sélectionner un capteur à supprimer.");
        }
    }

    @FXML
    private void handleRefresh() {
        loadCapteurs();
    }

    private void clearFields() {
        idField.clear();
        typeComboBox.getSelectionModel().clearSelection();
        etatComboBox.getSelectionModel().clearSelection();
        datePicker.setValue(null);
        lampadaireIdField.clear();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}