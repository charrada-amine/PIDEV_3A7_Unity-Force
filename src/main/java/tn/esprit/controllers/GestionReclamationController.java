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
import tn.esprit.models.Reclamation;
import tn.esprit.services.ServiceReclamation;

import java.net.URL;
import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class GestionReclamationController implements Initializable {

    @FXML private TextField tfDescription;
    @FXML private DatePicker dpDate;
    @FXML private TextField tfHeure;
    @FXML private ComboBox<String> cbStatut;
    @FXML private TextField tfLampadaireId;
    @FXML private TextField tfCitoyenId;
    @FXML private FlowPane cardContainer;
    @FXML private ScrollPane scrollPane;

    private final ServiceReclamation service = new ServiceReclamation();
    private final ObservableList<Reclamation> reclamations = FXCollections.observableArrayList();
    private Reclamation selectedReclamation;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cbStatut.setItems(FXCollections.observableArrayList("Ouvert", "En cours", "Résolu"));

        cardContainer.setHgap(20);
        cardContainer.setVgap(20);
        cardContainer.setPadding(new Insets(20));
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: #f4f4f4; -fx-background-color: transparent;");

        loadData();
    }

    private void loadData() {
        reclamations.setAll(service.getAll());
        cardContainer.getChildren().clear();

        for (Reclamation reclamation : reclamations) {
            cardContainer.getChildren().add(createReclamationCard(reclamation));
        }
    }

    private VBox createReclamationCard(Reclamation r) {
        VBox card = new VBox(10);
        card.setPrefWidth(300);
        card.setPadding(new Insets(15));
        card.setStyle("-fx-background-color: white; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 0);");

        Text title = new Text("Réclamation #" + r.getID_reclamation());
        title.setFont(Font.font(16));

        VBox info = new VBox(8);
        info.getChildren().addAll(
                createInfoText("Description", r.getDescription()),
                createInfoText("Date", r.getDateReclamation().toString()),
                createInfoText("Heure", r.getHeureReclamation().toString()),
                createInfoText("Statut", r.getStatut()),
                createInfoText("Lampadaire", String.valueOf(r.getLampadaireId())),
                createInfoText("Citoyen", String.valueOf(r.getCitoyenId()))
        );

        HBox buttons = new HBox(10);
        Button edit = createStyledButton("Modifier", "#2196F3");
        Button delete = createStyledButton("Supprimer", "#f44336");

        edit.setOnAction(e -> {
            selectedReclamation = r;
            fillForm(r);
        });

        delete.setOnAction(e -> handleDelete());

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

    private void fillForm(Reclamation r) {
        tfDescription.setText(r.getDescription());
        dpDate.setValue(r.getDateReclamation().toLocalDate());
        tfHeure.setText(r.getHeureReclamation().toString());
        cbStatut.setValue(r.getStatut());
        tfLampadaireId.setText(String.valueOf(r.getLampadaireId()));
        tfCitoyenId.setText(String.valueOf(r.getCitoyenId()));
    }

    @FXML
    private void handleAdd() {
        try {
            Reclamation r = new Reclamation(
                    tfDescription.getText(),
                    Date.valueOf(dpDate.getValue()),
                    Time.valueOf(tfHeure.getText()),
                    cbStatut.getValue(),
                    Integer.parseInt(tfLampadaireId.getText()),
                    Integer.parseInt(tfCitoyenId.getText())
            );
            service.add(r);
            loadData();
            clearForm();
        } catch (Exception e) {
            showAlert("Erreur", e.getMessage());
        }
    }

    @FXML
    private void handleUpdate() {
        if (selectedReclamation == null) return;

        selectedReclamation.setDescription(tfDescription.getText());
        selectedReclamation.setDateReclamation(Date.valueOf(dpDate.getValue()));
        selectedReclamation.setHeureReclamation(Time.valueOf(tfHeure.getText()));
        selectedReclamation.setStatut(cbStatut.getValue());
        selectedReclamation.setLampadaireId(Integer.parseInt(tfLampadaireId.getText()));
        selectedReclamation.setCitoyenId(Integer.parseInt(tfCitoyenId.getText()));

        service.update(selectedReclamation);
        loadData();
        clearForm();
    }

    @FXML
    private void handleDelete() {
        if (selectedReclamation == null) return;

        if (showConfirmation("Confirmer suppression", "Supprimer cette réclamation ?")) {
            service.delete(selectedReclamation);
            loadData();
            clearForm();
        }
    }

    @FXML
    private void handleClear() {
        clearForm();
    }

    private void clearForm() {
        tfDescription.clear();
        dpDate.setValue(null);
        tfHeure.clear();
        cbStatut.getSelectionModel().clearSelection();
        tfLampadaireId.clear();
        tfCitoyenId.clear();
        selectedReclamation = null;
    }

    private void showAlert(String title, String message) {
        new Alert(Alert.AlertType.ERROR, message, ButtonType.OK).show();
    }

    private boolean showConfirmation(String title, String message) {
        return new Alert(Alert.AlertType.CONFIRMATION, message, ButtonType.OK, ButtonType.CANCEL)
                .showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK;
    }
}