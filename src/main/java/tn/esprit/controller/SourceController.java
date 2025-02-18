package tn.esprit.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import tn.esprit.models.Source;
import tn.esprit.Enumerations.EnumEtat;
import tn.esprit.Enumerations.EnumType;
import tn.esprit.services.ServiceSource;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public class SourceController {

    private final ServiceSource serviceSource = new ServiceSource();

    // Composants pour l'affichage des sources
    @FXML
    private FlowPane flowPaneSources;

    @FXML
    private Button btnAddSource;

    @FXML
    private Button btnBack;

    // Composants pour l'ajout de source
    @FXML
    private ComboBox<EnumType> comboType;

    @FXML
    private TextField txtCapacite;

    @FXML
    private TextField txtRendement;

    @FXML
    private ComboBox<EnumEtat> comboEtat;

    @FXML
    private Button btnAdd;

    @FXML
    private Button btnCancel;

    @FXML
    private void initialize() {
        // Initialisation des ComboBox pour l'ajout de source
        comboType.getItems().setAll(EnumType.values());
        comboEtat.getItems().setAll(EnumEtat.values());

        // Affichage des sources existantes
        afficherSources();
    }

    // Méthode pour afficher les sources
    private void afficherSources() {
        List<Source> sources = serviceSource.getAll();
        flowPaneSources.getChildren().clear();

        for (Source source : sources) {
            VBox card = new VBox();
            card.setSpacing(10);
            card.setAlignment(Pos.CENTER);
            card.setPadding(new Insets(15));
            card.setPrefWidth(220);
            card.setStyle(
                    "-fx-border-color: #333;" +
                            "-fx-border-width: 2;" +
                            "-fx-border-radius: 15;" +
                            "-fx-background-color: linear-gradient(to bottom, #ffffff, #e6e6e6);" +
                            "-fx-background-radius: 15;" +
                            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 8, 0.5, 0, 3);"
            );

            Label labelId = new Label("🆔 ID: " + source.getIdSource());
            labelId.setStyle("-fx-font-weight: bold; -fx-font-size: 15px; -fx-text-fill: #2c3e50;");
            Label labelType = new Label("🔌 Type: " + source.getType());
            Label labelCapacite = new Label("⚡ Capacité: " + source.getCapacite() + " kWh");
            Label labelRendement = new Label("📈 Rendement: " + source.getRendement() + " %");
            Label labelEtat = new Label("📌 État: " + source.getEtat());
            Label labelDate = new Label("📅 Installé le: " + source.getDateInstallation());

            labelType.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
            labelCapacite.setStyle("-fx-font-size: 13px;");
            labelRendement.setStyle("-fx-font-size: 13px;");
            labelEtat.setStyle("-fx-font-size: 13px;");
            labelDate.setStyle("-fx-font-size: 13px;");

            Button btnUpdate = new Button("✏ Modifier");
            btnUpdate.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-size: 13px; -fx-background-radius: 10;");
            btnUpdate.setOnMouseEntered(e -> btnUpdate.setStyle("-fx-background-color: #2980b9; -fx-text-fill: white; -fx-background-radius: 10;"));
            btnUpdate.setOnMouseExited(e -> btnUpdate.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-background-radius: 10;"));
            btnUpdate.setOnAction(event -> prefillForm(source)); // Pré-remplir le formulaire

            Button btnDelete = new Button("🗑 Supprimer");
            btnDelete.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-size: 13px; -fx-background-radius: 10;");
            btnDelete.setOnMouseEntered(e -> btnDelete.setStyle("-fx-background-color: #c0392b; -fx-text-fill: white; -fx-background-radius: 10;"));
            btnDelete.setOnMouseExited(e -> btnDelete.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-background-radius: 10;"));
            btnDelete.setOnAction(event -> {
                serviceSource.delete(source.getIdSource());
                afficherSources();
            });

            card.getChildren().addAll(labelId, labelType, labelCapacite, labelRendement, labelEtat, labelDate, btnUpdate, btnDelete);
            flowPaneSources.getChildren().add(card);
        }
    }

    // Méthode pour pré-remplir les champs du formulaire
    private void prefillForm(Source source) {
        comboType.setValue(source.getType());
        txtCapacite.setText(String.valueOf(source.getCapacite()));
        txtRendement.setText(String.valueOf(source.getRendement()));
        comboEtat.setValue(source.getEtat());

        // Changer le texte du bouton "Ajouter" en "Modifier"
        btnAdd.setText("Modifier");

        // Stocker l'ID de la source à modifier dans le bouton
        btnAdd.setUserData(source.getIdSource());

        // Changer l'action du bouton pour appeler handleModify au lieu de handleAdd
        btnAdd.setOnAction(event -> handleModify());
    }

    // Méthode pour ajouter une source
    @FXML
    private void handleAdd() {
        try {
            EnumType type = comboType.getValue();
            float capacite = Float.parseFloat(txtCapacite.getText());
            float rendement = Float.parseFloat(txtRendement.getText());
            EnumEtat etat = comboEtat.getValue();
            LocalDate dateInstallation = LocalDate.now();

            if (type == null || etat == null) {
                showAlert("Veuillez sélectionner un type et un état.");
                return;
            }

            Source source = new Source(type, capacite, rendement, etat, dateInstallation);
            serviceSource.add(source);

            showAlert("✅ Source ajoutée avec succès !");
            afficherSources(); // Rafraîchir l'affichage des sources
        } catch (NumberFormatException e) {
            showAlert("❌ Veuillez entrer des valeurs valides pour la capacité et le rendement.");
        }
    }

    // Méthode pour modifier une source
    private void handleModify() {
        try {
            // Récupérer l'ID de la source à modifier
            int sourceId = (int) btnAdd.getUserData();

            // Récupérer les nouvelles valeurs du formulaire
            EnumType type = comboType.getValue();
            float capacite = Float.parseFloat(txtCapacite.getText());
            float rendement = Float.parseFloat(txtRendement.getText());
            EnumEtat etat = comboEtat.getValue();

            if (type == null || etat == null) {
                showAlert("Veuillez sélectionner un type et un état.");
                return;
            }

            // Créer un objet Source avec les nouvelles valeurs
            Source updatedSource = new Source(sourceId, type, capacite, rendement, etat, LocalDate.now());

            // Mettre à jour la source dans la base de données
            serviceSource.update(updatedSource);

            showAlert("✅ Source modifiée avec succès !");
            afficherSources(); // Rafraîchir l'affichage des sources

            // Réinitialiser le bouton "Modifier" à "Ajouter"
            btnAdd.setText("Ajouter");
            btnAdd.setOnAction(event -> handleAdd());
        } catch (NumberFormatException e) {
            showAlert("❌ Veuillez entrer des valeurs valides pour la capacité et le rendement.");
        }
    }

    // Méthode pour annuler l'ajout et revenir à l'interface principale
    @FXML
    private void handleCancel() {
        switchScene("/SourceInterface.fxml", btnCancel);
    }

    // Méthode pour revenir au menu principal
    @FXML
    private void handleBack() {
        switchScene("/Menu.fxml", btnBack);
    }

    // Méthode pour changer de scène
    private void switchScene(String fxmlPath, Button button) {
        try {
            // Récupère la fenêtre actuelle
            Stage stage = (Stage) button.getScene().getWindow();

            // Sauvegarde la taille actuelle
            double currentWidth = stage.getWidth();
            double currentHeight = stage.getHeight();

            // Charge la nouvelle scène
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            // Crée une nouvelle scène avec la taille actuelle
            Scene scene = new Scene(root, currentWidth, currentHeight);

            // Applique la scène et fixe la taille
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("❌ Impossible de charger la vue demandée.");
        }
    }


    // Méthode pour afficher une alerte
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}