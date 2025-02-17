package tn.esprit.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.geometry.Insets;
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
    private VBox donneeCardContainer;

    @FXML
    private Label valeurIndicationLabel;

    private final ServiceMouvement serviceMouvement = new ServiceMouvement();
    private final ServiceTemperature serviceTemperature = new ServiceTemperature();
    private final ServiceConsommation serviceConsommation = new ServiceConsommation();
    private final ServiceLuminosite serviceLuminosite = new ServiceLuminosite();
    private final ObservableList<Donnee> donneeList = FXCollections.observableArrayList();

    private Donnee selectedDonnee; // Variable pour stocker la donnée sélectionnée

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        typeCapteurComboBox.getItems().addAll("Mouvement", "Température", "Consommation", "Luminosité");
        updateValeurIndication(); // Mise à jour initiale
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
        if (selectedDonnee != null) {
            LocalDate date = dateCollectePicker.getValue();
            LocalTime heure = LocalTime.parse(heureCollecteField.getText());
            int capteurId = Integer.parseInt(capteurIdField.getText());
            String valeurStr = valeurField.getText();

            if (selectedDonnee instanceof DonneeMouvement) {
                serviceMouvement.update(new DonneeMouvement(selectedDonnee.getId(), date, heure, capteurId, Boolean.parseBoolean(valeurStr)));
            } else if (selectedDonnee instanceof DonneeTemperature) {
                serviceTemperature.update(new DonneeTemperature(selectedDonnee.getId(), date, heure, capteurId, Float.parseFloat(valeurStr)));
            } else if (selectedDonnee instanceof DonneeConsommation) {
                serviceConsommation.update(new DonneeConsommation(selectedDonnee.getId(), date, heure, capteurId, Float.parseFloat(valeurStr)));
            } else if (selectedDonnee instanceof DonneeLuminosite) {
                serviceLuminosite.update(new DonneeLuminosite(selectedDonnee.getId(), date, heure, capteurId, Integer.parseInt(valeurStr)));
            }
            handleRefresh();
        }
    }

    @FXML
    public void handleDeleteDonnee() {
        if (selectedDonnee != null) {
            if (selectedDonnee instanceof DonneeMouvement) serviceMouvement.delete(selectedDonnee.getId());
            else if (selectedDonnee instanceof DonneeTemperature) serviceTemperature.delete(selectedDonnee.getId());
            else if (selectedDonnee instanceof DonneeConsommation) serviceConsommation.delete(selectedDonnee.getId());
            else if (selectedDonnee instanceof DonneeLuminosite) serviceLuminosite.delete(selectedDonnee.getId());

            handleRefresh();
        }
    }

    @FXML
    public void handleRefresh() {
        donneeCardContainer.getChildren().clear(); // Effacer les anciennes cartes
        donneeList.clear();
        donneeList.addAll(serviceMouvement.getAll());
        donneeList.addAll(serviceTemperature.getAll());
        donneeList.addAll(serviceConsommation.getAll());
        donneeList.addAll(serviceLuminosite.getAll());

        for (Donnee donnee : donneeList) {
            VBox card = createDonneeCard(donnee);
            donneeCardContainer.getChildren().add(card);
        }
    }

    private VBox createDonneeCard(Donnee donnee) {
        VBox card = new VBox();
        card.setSpacing(5);
        card.setPadding(new Insets(10));
        card.setStyle("-fx-border-color: gray; -fx-background-color: white; -fx-background-radius: 5; -fx-border-radius: 5;");

        Label idLabel = new Label("ID: " + donnee.getId());
        Label dateLabel = new Label("Date: " + donnee.getDateCollecte());
        Label heureLabel = new Label("Heure: " + donnee.getHeureCollecte());
        Label capteurIdLabel = new Label("Capteur ID: " + donnee.getCapteurId());
        Label valeurLabel = new Label("Valeur: " + getValeurString(donnee));
        Label typeLabel = new Label("Type: " + getTypeString(donnee));

        // Ajouter un gestionnaire d'événements pour la sélection de la carte
        card.setOnMouseClicked(event -> {
            selectDonnee(donnee); // Sélectionner la donnée
        });

        card.getChildren().addAll(idLabel, dateLabel, heureLabel, capteurIdLabel, valeurLabel, typeLabel);
        card.setUserData(donnee); // Associer la donnée à la carte
        return card;
    }

    private String getValeurString(Donnee donnee) {
        if (donnee instanceof DonneeMouvement) return String.valueOf(((DonneeMouvement) donnee).getValeur());
        if (donnee instanceof DonneeTemperature) return String.valueOf(((DonneeTemperature) donnee).getValeur());
        if (donnee instanceof DonneeConsommation) return String.valueOf(((DonneeConsommation) donnee).getValeur());
        if (donnee instanceof DonneeLuminosite) return String.valueOf(((DonneeLuminosite) donnee).getValeur());
        return "N/A";
    }

    private String getTypeString(Donnee donnee) {
        if (donnee instanceof DonneeMouvement) return "Mouvement";
        if (donnee instanceof DonneeTemperature) return "Température";
        if (donnee instanceof DonneeConsommation) return "Consommation";
        if (donnee instanceof DonneeLuminosite) return "Luminosité";
        return "Inconnu";
    }

    private void selectDonnee(Donnee donnee) {
        this.selectedDonnee = donnee; // Mettre à jour la donnée sélectionnée
        dateCollectePicker.setValue(donnee.getDateCollecte());
        heureCollecteField.setText(donnee.getHeureCollecte().toString());
        capteurIdField.setText(String.valueOf(donnee.getCapteurId()));
        valeurField.setText(getValeurString(donnee));
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