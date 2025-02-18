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
import tn.esprit.models.Intervention;
import tn.esprit.models.Intervention.TypeIntervention;
import tn.esprit.services.ServiceIntervention;

import java.net.URL;
import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class GestionInterventionController implements Initializable {

    @FXML private ComboBox<TypeIntervention> cbType;
    @FXML private TextField tfDescription;
    @FXML private ComboBox<String> cbEtat;
    @FXML private DatePicker dpDate;
    @FXML private TextField tfHeure;
    @FXML private TextField tfLampadaireId;
    @FXML private TextField tfTechnicienId;
    @FXML private TextField tfReclamationId;
    @FXML private FlowPane cardContainer;
    @FXML private ScrollPane scrollPane;

    private final ServiceIntervention service = new ServiceIntervention();
    private final ObservableList<Intervention> interventions = FXCollections.observableArrayList();
    private Intervention selectedIntervention;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cbType.setItems(FXCollections.observableArrayList(TypeIntervention.values()));
        cbEtat.setItems(FXCollections.observableArrayList("En attente", "En cours", "Terminée"));

        cardContainer.setHgap(20);
        cardContainer.setVgap(20);
        cardContainer.setPadding(new Insets(20));
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: #f4f4f4; -fx-background-color: transparent;");

        loadData();
    }

    private void loadData() {
        interventions.setAll(service.getAll());
        cardContainer.getChildren().clear();

        for (Intervention intervention : interventions) {
            cardContainer.getChildren().add(createInterventionCard(intervention));
        }
    }

    private VBox createInterventionCard(Intervention intervention) {
        VBox card = new VBox(10);
        card.setPrefWidth(300);
        card.setPadding(new Insets(15));
        card.setStyle("-fx-background-color: white; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 0);");

        Text title = new Text("Intervention #" + intervention.getID_intervention());
        title.setFont(Font.font(16));

        VBox info = new VBox(8);
        info.getChildren().addAll(
                createInfoText("Type", intervention.getTypeIntervention().toString()),
                createInfoText("Description", intervention.getDescription()),
                createInfoText("État", intervention.getEtat()),
                createInfoText("Date", intervention.getDateIntervention().toLocalDate().toString()),
                createInfoText("Heure", intervention.getHeureIntervention().toString()),
                createInfoText("Lampadaire", String.valueOf(intervention.getLampadaireId())),
                createInfoText("Technicien", String.valueOf(intervention.getTechnicienId())),
                createInfoText("Réclamation",
                        intervention.getID_reclamation() != null ?
                                String.valueOf(intervention.getID_reclamation()) : "Aucune")
        );

        HBox buttons = new HBox(10);
        Button edit = createStyledButton("Modifier", "#2196F3");
        Button delete = createStyledButton("Supprimer", "#f44336");

        // Gestion suppression directe
        delete.setOnAction(e -> {
            if (showConfirmation("Confirmation", "Voulez-vous vraiment supprimer cette intervention ?")) {
                service.delete(intervention);
                cardContainer.getChildren().remove(card); // Suppression visuelle immédiate
                interventions.remove(intervention); // Mise à jour de la liste
            }
        });

        // Gestion modification avec sélection
        edit.setOnAction(e -> {
            selectedIntervention = intervention;
            fillForm(intervention);
            scrollPane.setVvalue(0); // Défilement vers le formulaire
        });

        buttons.getChildren().addAll(edit, delete);
        card.getChildren().addAll(title, new Separator(), info, buttons);
        return card;
    }
    private Text createInfoText(String label, String value) {
        Text text = new Text(label + ": " + value);
        text.setFill(javafx.scene.paint.Color.GRAY); // Version sécurisée sans import
        return text;
    }

    private Button createStyledButton(String text, String color) {
        Button btn = new Button(text);
        btn.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white;");
        return btn;
    }

    private void fillForm(Intervention i) {
        cbType.setValue(i.getTypeIntervention());
        tfDescription.setText(i.getDescription());
        cbEtat.setValue(i.getEtat());
        dpDate.setValue(i.getDateIntervention().toLocalDate());
        tfHeure.setText(i.getHeureIntervention().toString());
        tfLampadaireId.setText(String.valueOf(i.getLampadaireId()));
        tfTechnicienId.setText(String.valueOf(i.getTechnicienId()));
        tfReclamationId.setText(String.valueOf(i.getID_reclamation()));
    }

    @FXML
    private void handleAdd() {
        try {
            Intervention i = new Intervention(
                    cbType.getValue(),
                    tfDescription.getText(),
                    cbEtat.getValue(),
                    Date.valueOf(dpDate.getValue()),
                    Time.valueOf(tfHeure.getText()),
                    Integer.parseInt(tfLampadaireId.getText()),
                    Integer.parseInt(tfTechnicienId.getText()),
                    Integer.parseInt(tfReclamationId.getText())
            );
            service.add(i);
            loadData();
            clearForm();
        } catch (Exception e) {
            showAlert("Erreur", e.getMessage());
        }
    }

    @FXML
    private void handleUpdate() {
        if (selectedIntervention == null) {
            showAlert("Erreur", "Aucune intervention sélectionnée !");
            return;
        }

        try {
            // Mise à jour de toutes les propriétés
            selectedIntervention.setTypeIntervention(cbType.getValue());
            selectedIntervention.setDescription(tfDescription.getText());
            selectedIntervention.setEtat(cbEtat.getValue());
            selectedIntervention.setDateIntervention(Date.valueOf(dpDate.getValue()));
            selectedIntervention.setHeureIntervention(Time.valueOf(tfHeure.getText()));
            selectedIntervention.setLampadaireId(Integer.parseInt(tfLampadaireId.getText()));
            selectedIntervention.setTechnicienId(Integer.parseInt(tfTechnicienId.getText()));

            // Gestion de la réclamation nullable
            if (!tfReclamationId.getText().isEmpty()) {
                selectedIntervention.setID_reclamation(Integer.parseInt(tfReclamationId.getText()));
            } else {
                selectedIntervention.setID_reclamation(null);
            }

            service.update(selectedIntervention);
            loadData(); // Rechargement complet des données
            clearForm();
            showAlert("Succès", "Intervention mise à jour avec succès !");

        } catch (IllegalArgumentException ex) {
            showAlert("Erreur de format", ex.getMessage());
        } catch (Exception ex) {
            showAlert("Erreur", "Échec de la mise à jour : " + ex.getMessage());
        }
    }
    @FXML
    private void handleDelete() {
        if (selectedIntervention != null) {
            if (showConfirmation("Confirmer suppression", "Supprimer cette intervention ?")) {
                service.delete(selectedIntervention);
                loadData();
                clearForm();
            }
        } else {
            showAlert("Erreur", "Aucune intervention sélectionnée !");
        }
    }

    @FXML
    private void handleClear() {
        clearForm();
    }

    private void clearForm() {
        cbType.getSelectionModel().clearSelection();
        tfDescription.clear();
        cbEtat.getSelectionModel().clearSelection();
        dpDate.setValue(null);
        tfHeure.clear();
        tfLampadaireId.clear();
        tfTechnicienId.clear();
        tfReclamationId.clear();
        selectedIntervention = null;
    }

    private void showAlert(String title, String message) {
        new Alert(Alert.AlertType.ERROR, message, ButtonType.OK).show();
    }

    private boolean showConfirmation(String title, String message) {
        return new Alert(Alert.AlertType.CONFIRMATION, message, ButtonType.OK, ButtonType.CANCEL)
                .showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK;
    }
}