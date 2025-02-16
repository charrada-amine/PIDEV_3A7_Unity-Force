package tn.esprit.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.Insets;
import javafx.scene.text.Text;
import javafx.scene.text.Font;
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
    @FXML private FlowPane cardContainer;

    private Zone selectedZone;
    private final ServiceZone serviceZone = new ServiceZone();
    private final ObservableList<Zone> zones = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cardContainer.setHgap(20);
        cardContainer.setVgap(20);
        cardContainer.setPadding(new Insets(20));
        chargerZones();
    }

    private void chargerZones() {
        cardContainer.getChildren().clear();
        zones.setAll(serviceZone.getAll());

        for (Zone zone : zones) {
            cardContainer.getChildren().add(creerCarteZone(zone));
        }
    }

    private VBox creerCarteZone(Zone zone) {
        VBox card = new VBox(10);
        card.setPrefWidth(300);
        card.setPadding(new Insets(15));
        card.setStyle("-fx-background-color: white; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 0); " +
                "-fx-background-radius: 8; " +
                "-fx-border-radius: 8;");

        // En-tête
        Text title = new Text("Zone #" + zone.getIdZone());
        title.setFont(Font.font("System", 16));

        // Contenu
        VBox content = new VBox(8);
        content.getChildren().addAll(
                creerTexte("Nom", zone.getNom()),
                creerTexte("Description", zone.getDescription()),
                creerTexte("Surface", zone.getSurface() + " m²"),
                creerTexte("Lampadaires", String.valueOf(zone.getNombreLampadaires())),
                creerTexte("Citoyens", String.valueOf(zone.getNombreCitoyens()))
        );

        // Boutons
        HBox buttonBox = new HBox(10);
        Button editBtn = createStyledButton("Modifier", "#2196F3");
        Button deleteBtn = createStyledButton("Supprimer", "#f44336");

        editBtn.setOnAction(e -> {
            selectedZone = zone;
            remplirFormulaire(zone);
        });

        deleteBtn.setOnAction(e -> {
            if (showConfirmation("Confirmation de suppression", "Voulez-vous vraiment supprimer cette zone ?")) {
                serviceZone.delete(zone);
                rafraichirInterface();
            }
        });

        buttonBox.getChildren().addAll(editBtn, deleteBtn);
        card.getChildren().addAll(title, new Separator(), content, buttonBox);

        return card;
    }

    private Text creerTexte(String label, String value) {
        return new Text(label + ": " + value);
    }

    private Button createStyledButton(String text, String color) {
        Button btn = new Button(text);
        btn.setStyle("-fx-background-color: " + color + ";" +
                "-fx-text-fill: white;" +
                "-fx-background-radius: 4;");
        return btn;
    }

    // Les méthodes restantes restent identiques...
    @FXML
    private void handleAdd() {
        try {
            Zone nouvelleZone = new Zone();
            peuplerZoneDepuisFormulaire(nouvelleZone);
            serviceZone.add(nouvelleZone);
            rafraichirInterface();
        } catch (Exception e) {
            afficherErreur("Erreur d'ajout", e.getMessage());
        }
    }

    @FXML
    private void handleUpdate() {
        if (selectedZone != null) {
            try {
                peuplerZoneDepuisFormulaire(selectedZone);
                serviceZone.update(selectedZone);
                rafraichirInterface();
            } catch (Exception e) {
                afficherErreur("Erreur de modification", e.getMessage());
            }
        } else {
            afficherErreur("Aucune sélection", "Veuillez sélectionner une zone");
        }
    }

    @FXML
    private void handleDelete() {
        if (selectedZone != null) {
            try {
                serviceZone.delete(selectedZone);
                rafraichirInterface();
            } catch (Exception e) {
                afficherErreur("Erreur de suppression", e.getMessage());
            }
        } else {
            afficherErreur("Aucune sélection", "Veuillez sélectionner une zone");
        }
    }

    @FXML
    private void handleClear() {
        viderFormulaire();
        selectedZone = null;
    }

    private void peuplerZoneDepuisFormulaire(Zone zone) {
        zone.setNom(tfNom.getText());
        zone.setDescription(tfDescription.getText());
        zone.setSurface(Float.parseFloat(tfSurface.getText()));
        zone.setNombreLampadaires(Integer.parseInt(tfNombreLampadaires.getText()));
        zone.setNombreCitoyens(Integer.parseInt(tfNombreCitoyens.getText()));
    }

    private void remplirFormulaire(Zone zone) {
        tfNom.setText(zone.getNom());
        tfDescription.setText(zone.getDescription());
        tfSurface.setText(String.valueOf(zone.getSurface()));
        tfNombreLampadaires.setText(String.valueOf(zone.getNombreLampadaires()));
        tfNombreCitoyens.setText(String.valueOf(zone.getNombreCitoyens()));
    }

    private void viderFormulaire() {
        tfNom.clear();
        tfDescription.clear();
        tfSurface.clear();
        tfNombreLampadaires.clear();
        tfNombreCitoyens.clear();
    }

    private void rafraichirInterface() {
        chargerZones();
        viderFormulaire();
        selectedZone = null;
    }

    private boolean showConfirmation(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        return alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK;
    }

    private void afficherErreur(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}