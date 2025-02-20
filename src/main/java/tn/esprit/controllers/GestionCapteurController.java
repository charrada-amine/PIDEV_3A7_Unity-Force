package tn.esprit.controllers;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
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

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;
import javafx.scene.layout.FlowPane;

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
    private FlowPane capteurCardContainer;




    private ServiceCapteur serviceCapteur;

    private Capteur selectedCapteur;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        serviceCapteur = new ServiceCapteur();

        // Initialiser les ComboBox
        typeComboBox.setItems(FXCollections.observableArrayList(TypeCapteur.values()));
        etatComboBox.setItems(FXCollections.observableArrayList(EtatCapteur.values()));


        // Charger les capteurs
        loadCapteurs();
    }

    private void loadCapteurs() {
        capteurCardContainer.getChildren().clear();
        System.out.println("Nombre de capteurs récupérés : " + serviceCapteur.getAll().size()); // Debug

        for (Capteur capteur : serviceCapteur.getAll()) {
            VBox card = createCapteurCard(capteur);
            capteurCardContainer.getChildren().add(card);
            System.out.println("Carte ajoutée : " + capteur.getType() + " - " + capteur.getEtat()); // Debug
        }

        System.out.println("Total cartes affichées : " + capteurCardContainer.getChildren().size()); // Debug
    }


    private VBox createCapteurCard(Capteur capteur) {
        VBox card = new VBox();
        card.setSpacing(5);
        card.setPadding(new Insets(10));
        card.setPrefWidth(200); // Ajoute une largeur
        card.setPrefHeight(100); // Ajoute une hauteur
        card.setStyle("-fx-border-color: gray; -fx-background-color: white; -fx-background-radius: 5; -fx-border-radius: 5;");

        Label idLabel = new Label("ID: " + capteur.getId());
        Label typeLabel = new Label("Type: " + capteur.getType());
        Label etatLabel = new Label("État: " + capteur.getEtat());
        Label dateLabel = new Label("Date d'installation: " + capteur.getDateinstallation());
        Label lampadaireIdLabel = new Label("Lampadaire ID: " + capteur.getLampadaireId());

        idLabel.setStyle("-fx-text-fill: black;");  // Force la couleur du texte
        typeLabel.setStyle("-fx-text-fill: black;");
        etatLabel.setStyle("-fx-text-fill: black;");
        dateLabel.setStyle("-fx-text-fill: black;");
        lampadaireIdLabel.setStyle("-fx-text-fill: black;");

        card.setOnMouseClicked(event -> selectCapteur(capteur));

        card.getChildren().addAll(idLabel, typeLabel, etatLabel, dateLabel, lampadaireIdLabel);
        return card;
    }


    private void selectCapteur(Capteur capteur) {
        selectedCapteur = capteur;
        idField.setText(String.valueOf(capteur.getId()));
        typeComboBox.setValue(capteur.getType());
        datePicker.setValue(capteur.getDateinstallation());
        etatComboBox.setValue(capteur.getEtat());
        lampadaireIdField.setText(String.valueOf(capteur.getLampadaireId()));
    }

    @FXML
    private void handleAddCapteur() {
        if (!validateFields()) {
            return;
        }

        try {
            Capteur capteur = new Capteur(
                    0,
                    typeComboBox.getValue(),
                    datePicker.getValue(),
                    etatComboBox.getValue(),
                    Integer.parseInt(lampadaireIdField.getText())
            );
            serviceCapteur.add(capteur);
            loadCapteurs();
            clearFields();
            showSuccessAlert("Capteur ajouté avec succès !");
        } catch (Exception e) {
            showAlert("Erreur", "Une erreur est survenue lors de l'ajout du capteur.");
        }
    }

    @FXML
    private void handleUpdateCapteur() {
        if (selectedCapteur == null) {
            showAlert("Erreur", "Veuillez sélectionner un capteur à modifier.");
            return;
        }

        if (!validateFields()) {
            return;
        }

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
            showAlert("Erreur", "Une erreur est survenue lors de la modification du capteur.");
        }
    }

    @FXML
    private void handleDeleteCapteur() {
        if (selectedCapteur == null) {
            showAlert("Erreur", "Veuillez sélectionner un capteur à supprimer.");
            return;
        }

        serviceCapteur.delete(selectedCapteur.getId());
        loadCapteurs();
        clearFields();
        selectedCapteur = null;
        showSuccessAlert("Capteur supprimé avec succès !");
    }

    @FXML
    private void handleRefresh() {
        loadCapteurs();
    }

    private boolean validateFields() {
        String errorMessage = "";

        if (typeComboBox.getValue() == null) {
            errorMessage += "- Veuillez sélectionner un type de capteur.\n";
        }

        if (etatComboBox.getValue() == null) {
            errorMessage += "- Veuillez sélectionner un état pour le capteur.\n";
        }

        if (datePicker.getValue() == null) {
            errorMessage += "- Veuillez choisir une date d'installation.\n";
        } else if (datePicker.getValue().isAfter(LocalDate.now())) {
            errorMessage += "- La date d'installation ne peut pas être dans le futur.\n";
        }

        if (lampadaireIdField.getText().isEmpty()) {
            errorMessage += "- Veuillez entrer l'ID du lampadaire.\n";
        } else {
            try {
                Integer.parseInt(lampadaireIdField.getText());
            } catch (NumberFormatException e) {
                errorMessage += "- L'ID du lampadaire doit être un nombre valide.\n";
            }
        }

        if (!errorMessage.isEmpty()) {
            showAlert("Validation des champs", errorMessage);
            return false;
        }

        return true;
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
            Parent gestionDonneeParent = FXMLLoader.load(getClass().getResource("/GestionDonnee.fxml"));
            Scene gestionDonneeScene = new Scene(gestionDonneeParent);
            Stage window = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            window.setScene(gestionDonneeScene);
            window.show();
        } catch (IOException e) {
            System.err.println("Erreur lors du chargement de GestionDonnee.fxml : " + e.getMessage());
        }
    }



    @FXML
    private void handleGestionCapteur(ActionEvent event) {
        switchScene(event, "/GestionCapteur.fxml");
    }

    @FXML
    private void handleGestionCitoyen(ActionEvent event) {
        switchScene(event, "/GestionCitoyen.fxml");
    }

    @FXML
    private void handleGestionDonnee(ActionEvent event) {
        switchScene(event, "/GestionDonnee.fxml");
    }

    @FXML
    private void handleGestionIntervention(ActionEvent event) {
        switchScene(event, "/GestionIntervention.fxml");
    }

    @FXML
    private void handleGestionLampadaire(ActionEvent event) {
        switchScene(event, "/GestionLampadaire.fxml");
    }

    @FXML
    private void handleGestionReclamation(ActionEvent event) {
        switchScene(event, "/GestionReclamation.fxml");
    }

    @FXML
    private void handleGestionResponsable(ActionEvent event) {
        switchScene(event, "/GestionResponsable.fxml");
    }

    @FXML
    private void handleGestionTechnicien(ActionEvent event) {
        switchScene(event, "/GestionTechnicien.fxml");
    }

    @FXML
    private void handleGestionUtilisateur(ActionEvent event) {
        switchScene(event, "/GestionUtilisateur.fxml");
    }

    @FXML
    private void handleGestionZone(ActionEvent event) {
        switchScene(event, "/GestionZone.fxml");
    }

    @FXML
    private void handleProfileInterface(ActionEvent event) {
        switchScene(event, "/ProfileInterface.fxml");
    }

    @FXML
    private void handleSourceInterface(ActionEvent event) {
        switchScene(event, "/SourceInterface.fxml");
    }

    @FXML
    private void handleBack() {
        // Logique pour revenir à la page précédente
        System.out.println("Retour à la page précédente");
    }

    private void switchScene(ActionEvent event, String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            // Récupère la scène actuelle et met à jour son contenu
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}