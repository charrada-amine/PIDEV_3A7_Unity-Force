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
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class GestionInterventionController implements Initializable {

    @FXML private ComboBox<TypeIntervention> cbType;
    @FXML private TextField tfDescription;
    @FXML private TextField tfEtat;
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

        cardContainer.setHgap(20);
        cardContainer.setVgap(20);
        cardContainer.setPadding(new Insets(20));
        scrollPane.setFitToWidth(true);

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
        card.setPrefWidth(350);
        card.setStyle("-fx-background-color: #ffffff; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 0);");
        card.setPadding(new Insets(15));

        Text title = new Text("Intervention #" + intervention.getId());
        title.setFont(Font.font(16));

        VBox details = new VBox(5);
        details.getChildren().addAll(
                createDetailText("Type: " + intervention.getTypeIntervention()),
                createDetailText("Description: " + intervention.getDescription()),
                createDetailText("État: " + intervention.getEtat()),
                createDetailText("Date: " + intervention.getDateIntervention()),
                createDetailText("Heure: " + intervention.getHeureIntervention()),
                createDetailText("Lampadaire: " + intervention.getLampadaireId()),
                createDetailText("Technicien: " + intervention.getTechnicienId())
        );

        HBox buttons = new HBox(10);
        Button editBtn = createActionButton("Modifier", "#2196F3");
        Button deleteBtn = createActionButton("Supprimer", "#f44336");

        editBtn.setOnAction(e -> {
            selectedIntervention = intervention;
            fillForm(intervention);
        });

        deleteBtn.setOnAction(e -> handleDelete());

        buttons.getChildren().addAll(editBtn, deleteBtn);
        card.getChildren().addAll(title, new Separator(), details, buttons);
        return card;
    }

    private Text createDetailText(String content) {
        Text text = new Text(content);
        text.setWrappingWidth(300);
        return text;
    }

    private Button createActionButton(String text, String color) {
        Button btn = new Button(text);
        btn.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white;");
        return btn;
    }

    private void fillForm(Intervention intervention) {
        cbType.setValue(intervention.getTypeIntervention());
        tfDescription.setText(intervention.getDescription());
        tfEtat.setText(intervention.getEtat());
        dpDate.setValue(intervention.getDateIntervention().toLocalDate());
        tfHeure.setText(intervention.getHeureIntervention().toString());
        tfLampadaireId.setText(String.valueOf(intervention.getLampadaireId()));
        tfTechnicienId.setText(String.valueOf(intervention.getTechnicienId()));
        tfReclamationId.setText(String.valueOf(intervention.getID_reclamation()));
    }

    @FXML
    private void handleAdd() {
        try {
            Intervention intervention = new Intervention(
                    cbType.getValue(),
                    tfDescription.getText(),
                    tfEtat.getText(),
                    Date.valueOf(dpDate.getValue()),
                    Time.valueOf(tfHeure.getText()),
                    Integer.parseInt(tfLampadaireId.getText()),
                    Integer.parseInt(tfTechnicienId.getText()),
                    Integer.parseInt(tfReclamationId.getText())
            );

            service.add(intervention);
            loadData();
            clearForm();
        } catch (Exception e) {
            showAlert("Erreur", e.getMessage());
        }
    }

    @FXML
    private void handleUpdate() {
        if (selectedIntervention == null) return;

        selectedIntervention.setTypeIntervention(cbType.getValue());
        selectedIntervention.setDescription(tfDescription.getText());
        selectedIntervention.setEtat(tfEtat.getText());
        selectedIntervention.setDateIntervention(Date.valueOf(dpDate.getValue()));
        selectedIntervention.setHeureIntervention(Time.valueOf(tfHeure.getText()));
        selectedIntervention.setLampadaireId(Integer.parseInt(tfLampadaireId.getText()));
        selectedIntervention.setTechnicienId(Integer.parseInt(tfTechnicienId.getText()));
        selectedIntervention.setID_reclamation(Integer.parseInt(tfReclamationId.getText()));

        service.update(selectedIntervention);
        loadData();
        clearForm();
    }

    @FXML
    private void handleDelete() {
        if (selectedIntervention == null) return;

        if (showConfirmation("Confirmer suppression",
                "Voulez-vous vraiment supprimer cette intervention ?")) {
            service.delete(selectedIntervention);
            loadData();
            clearForm();
        }
    }
    private void clearForm() {
        cbType.getSelectionModel().clearSelection();
        tfDescription.clear();
        tfEtat.clear();
        dpDate.setValue(null);
        tfHeure.clear();
        tfLampadaireId.clear();
        tfTechnicienId.clear();
        tfReclamationId.clear();
        selectedIntervention = null;
    }

    @FXML
    private void handleClear() {
        clearForm(); // Utilise la méthode existante de nettoyage
    }

    @FXML
    private void handleRefresh() {
        loadData(); // Utilise la méthode existante de chargement
    }



    private boolean showConfirmation(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        return alert.showAndWait().get() == ButtonType.OK;
    }

    private void showAlert(String title, String message) {
        new Alert(Alert.AlertType.ERROR, message, ButtonType.OK).show();
    }
}