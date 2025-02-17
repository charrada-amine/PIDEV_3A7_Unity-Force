package tn.esprit.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.geometry.Insets;
import tn.esprit.models.Capteur;
import tn.esprit.models.Capteur.EtatCapteur;
import tn.esprit.models.Capteur.TypeCapteur;
import tn.esprit.services.ServiceCapteur;

import java.awt.event.ActionEvent;
import java.net.URL;
import java.util.ResourceBundle;


import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

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
    private VBox capteurCardContainer;

    private ServiceCapteur serviceCapteur;

    private Capteur selectedCapteur; // Variable pour stocker le capteur sélectionné

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        serviceCapteur = new ServiceCapteur();

        // Initialiser les ComboBox
        typeComboBox.setItems(FXCollections.observableArrayList(TypeCapteur.values()));
        etatComboBox.setItems(FXCollections.observableArrayList(EtatCapteur.values()));

        // Charger les capteurs dans le CardView
        loadCapteurs();
    }

    private void loadCapteurs() {
        capteurCardContainer.getChildren().clear(); // Effacer les anciennes cartes

        for (Capteur capteur : serviceCapteur.getAll()) {
            VBox card = createCapteurCard(capteur);
            capteurCardContainer.getChildren().add(card);
        }
    }

    private VBox createCapteurCard(Capteur capteur) {
        VBox card = new VBox();
        card.setSpacing(5);
        card.setPadding(new Insets(10));
        card.setStyle("-fx-border-color: gray; -fx-background-color: white; -fx-background-radius: 5; -fx-border-radius: 5;");

        Label idLabel = new Label("ID: " + capteur.getId());
        Label typeLabel = new Label("Type: " + capteur.getType());
        Label etatLabel = new Label("État: " + capteur.getEtat());
        Label dateLabel = new Label("Date d'installation: " + capteur.getDateinstallation());
        Label lampadaireIdLabel = new Label("Lampadaire ID: " + capteur.getLampadaireId());

        // Ajouter un gestionnaire d'événements pour la sélection de la carte
        card.setOnMouseClicked(event -> {
            selectCapteur(capteur); // Sélectionner le capteur
        });

        card.getChildren().addAll(idLabel, typeLabel, etatLabel, dateLabel, lampadaireIdLabel);
        card.setUserData(capteur); // Associer le capteur à la carte
        return card;
    }

    private void selectCapteur(Capteur capteur) {
        this.selectedCapteur = capteur; // Mettre à jour le capteur sélectionné
        idField.setText(String.valueOf(capteur.getId()));
        typeComboBox.setValue(capteur.getType());
        datePicker.setValue(capteur.getDateinstallation());
        etatComboBox.setValue(capteur.getEtat());
        lampadaireIdField.setText(String.valueOf(capteur.getLampadaireId()));
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

    @FXML
    private void handleUpdateCapteur() {
        if (selectedCapteur != null) {
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
                showAlert("Erreur", "Veuillez remplir tous les champs correctement.");
            }
        } else {
            showAlert("Erreur", "Veuillez sélectionner un capteur à modifier.");
        }
    }

    @FXML
    private void handleDeleteCapteur() {
        if (selectedCapteur != null) {
            serviceCapteur.delete(selectedCapteur.getId());
            loadCapteurs();
            clearFields();
            showSuccessAlert("Capteur supprimé avec succès !");
            selectedCapteur = null; // Réinitialiser la sélection après la suppression
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

    private void showSuccessAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Succès");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    public void handleGoToGestionDonnee(javafx.event.ActionEvent actionEvent) {
        try {
            // Charger le fichier FXML de la page GestionDonnee
            Parent gestionDonneeParent = FXMLLoader.load(getClass().getResource("/GestionDonnee.fxml"));
            Scene gestionDonneeScene = new Scene(gestionDonneeParent);

            // Obtenir la scène actuelle et la fenêtre
            Stage window = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();

            // Changer la scène
            window.setScene(gestionDonneeScene);
            window.show();
        } catch (IOException e) {
            e.printStackTrace(); // Afficher l'erreur en cas de problème
            System.err.println("Erreur lors du chargement de GestionDonnee.fxml : " + e.getMessage());
        }
    }
}
