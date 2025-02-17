package tn.esprit.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.Font;
import javafx.stage.StageStyle;
import javafx.util.Duration;
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
        zones.forEach(zone -> cardContainer.getChildren().add(creerCarteZone(zone)));
    }

    private VBox creerCarteZone(Zone zone) {
        VBox card = new VBox(10);
        card.setPrefWidth(300);
        card.setPadding(new Insets(15));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 16;");
        card.setOpacity(0);
        card.setRotate(3);
        Timeline fadeIn = new Timeline(new KeyFrame(Duration.millis(400), new KeyValue(card.opacityProperty(), 1), new KeyValue(card.rotateProperty(), 0)));
        fadeIn.play();
        DropShadow hoverEffect = new DropShadow(15, Color.web("#3f37c933"));
        card.setOnMouseEntered(e -> { card.setEffect(hoverEffect); card.setScaleX(1.02); card.setScaleY(1.02); });
        card.setOnMouseExited(e -> { card.setEffect(null); card.setScaleX(1.0); card.setScaleY(1.0); });
        Text title = new Text("Zone #" + zone.getIdZone());
        title.setFont(Font.font("Segoe UI", 16));
        VBox content = new VBox(8);
        content.getChildren().addAll(
                creerTexte("Nom", zone.getNom()),
                creerTexte("Description", zone.getDescription()),
                creerTexte("Surface", zone.getSurface() + " m²"),
                creerTexte("Lampadaires", String.valueOf(zone.getNombreLampadaires())),
                creerTexte("Citoyens", String.valueOf(zone.getNombreCitoyens()))
        );
        HBox buttonBox = new HBox(10);
        Button editBtn = createStyledButton("Modifier", "#4361ee");
        Button deleteBtn = createStyledButton("Supprimer", "#ef476f");
        editBtn.setOnAction(e -> { selectedZone = zone; remplirFormulaire(zone); });
        deleteBtn.setOnAction(e -> {
            if (showConfirmation("Confirmation de suppression", "Voulez-vous vraiment supprimer cette zone ?")) {
                serviceZone.delete(zone);
                rafraichirInterface();
                showSuccessFeedback();
            }
        });
        buttonBox.getChildren().addAll(editBtn, deleteBtn);
        card.getChildren().addAll(title, new Separator(), content, buttonBox);
        return card;
    }

    private Text creerTexte(String label, String value) {
        Text text = new Text(label + ": " + value);
        text.setFont(Font.font("Segoe UI", 14));
        return text;
    }

    private Button createStyledButton(String text, String color) {
        Button btn = new Button(text);
        btn.setStyle("-fx-background-color: " + color + ";-fx-text-fill: white;-fx-background-radius: 8;-fx-padding: 8 16;");
        return btn;
    }

    @FXML
    private void handleAdd() {
        try {
            Zone nouvelleZone = new Zone();
            peuplerZoneDepuisFormulaire(nouvelleZone);
            serviceZone.add(nouvelleZone);
            rafraichirInterface();
            showSuccessFeedback();
        } catch (Exception e) {
            afficherErreur("Erreur d'ajout", e.getMessage());
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
            e.printStackTrace();
            afficherErreur("Erreur", "Impossible de charger le menu principal");
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
            afficherErreur("Erreur", "Impossible de charger la gestion des lampadaires");
        }
    }

    @FXML
    private void handleUpdate() {
        if (selectedZone != null) {
            try {
                peuplerZoneDepuisFormulaire(selectedZone);
                serviceZone.update(selectedZone);
                rafraichirInterface();
                showSuccessFeedback();
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
                showSuccessFeedback();
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
        alert.initStyle(StageStyle.TRANSPARENT);
        alert.getDialogPane().getScene().getRoot().setStyle("-fx-background-color: rgba(255,255,255,0.95);-fx-background-radius: 16;-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 16, 0, 4, 4);");
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        return alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK;
    }

    private void afficherErreur(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.initStyle(StageStyle.TRANSPARENT);
        alert.getDialogPane().getScene().getRoot().setStyle("-fx-background-color: rgba(255,255,255,0.95);-fx-background-radius: 16;-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 16, 0, 4, 4);");
        alert.getDialogPane().setTranslateY(20);
        alert.getDialogPane().setOpacity(0);
        Timeline slideIn = new Timeline(new KeyFrame(Duration.millis(300), new KeyValue(alert.getDialogPane().translateYProperty(), 0), new KeyValue(alert.getDialogPane().opacityProperty(), 1)));
        slideIn.play();
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showSuccessFeedback() {
        Pane root = (Pane) cardContainer.getParent();
        Label feedback = new Label("✓ Opération réussie !");
        feedback.setStyle("-fx-background-color: #06d6a0; -fx-text-fill: white; -fx-padding: 8 16; -fx-background-radius: 20; -fx-font-weight: 600;");
        feedback.setTranslateY(-50);
        feedback.setOpacity(0);
        root.getChildren().add(feedback);
        Timeline animation = new Timeline(
                new KeyFrame(Duration.millis(300), new KeyValue(feedback.translateYProperty(), 20), new KeyValue(feedback.opacityProperty(), 1)),
                new KeyFrame(Duration.millis(2000), new KeyValue(feedback.opacityProperty(), 0))
        );
        animation.setOnFinished(e -> root.getChildren().remove(feedback));
        animation.play();
    }
}