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
import java.time.format.DateTimeFormatter;
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
        // Configuration des statuts possibles
        cbStatut.setItems(FXCollections.observableArrayList(
                "EN_COURS",
                "RESOLUE",
                "FERMEE"
        ));

        // Configuration du layout
        cardContainer.setHgap(20);
        cardContainer.setVgap(20);
        cardContainer.setPadding(new Insets(20));
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: #f4f4f4;");

        loadData();
    }

    private void loadData() {
        reclamations.setAll(service.getAll());
        cardContainer.getChildren().clear();

        for (Reclamation reclamation : reclamations) {
            cardContainer.getChildren().add(createReclamationCard(reclamation));
        }
    }

    private VBox createReclamationCard(Reclamation reclamation) {
        VBox card = new VBox(10);
        card.setPrefWidth(300);
        card.setPadding(new Insets(15));
        card.setStyle("-fx-background-color: white; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 0); " +
                "-fx-background-radius: 8;");

        // Titre
        Text title = new Text("Réclamation #" + reclamation.getId());
        title.setFont(Font.font("System", 16));

        // Détails
        VBox details = new VBox(8);
        details.getChildren().addAll(
                createDetailText("Description: " + reclamation.getDescription()),
                createDetailText("Date: " + reclamation.getDateReclamation()),
                createDetailText("Heure: " + reclamation.getHeureReclamation()),
                createDetailText("Statut: " + reclamation.getStatut()),
                createDetailText("Lampadaire: " + reclamation.getLampadaireId()),
                createDetailText("Citoyen: " + reclamation.getCitoyenId())
        );

        // Boutons d'action
        HBox buttons = new HBox(10);
        Button editBtn = createStyledButton("Modifier", "#2196F3");
        Button deleteBtn = createStyledButton("Supprimer", "#f44336");

        editBtn.setOnAction(e -> {
            selectedReclamation = reclamation;
            fillForm(reclamation);
        });

        deleteBtn.setOnAction(e -> handleDelete());

        buttons.getChildren().addAll(editBtn, deleteBtn);
        card.getChildren().addAll(title, new Separator(), details, buttons);

        return card;
    }

    private Text createDetailText(String content) {
        Text text = new Text(content);
        text.setWrappingWidth(280);
        return text;
    }

    private Button createStyledButton(String text, String color) {
        Button button = new Button(text);
        button.setStyle("-fx-background-color: " + color + "; " +
                "-fx-text-fill: white; " +
                "-fx-background-radius: 4;");
        return button;
    }

    private void fillForm(Reclamation reclamation) {
        tfDescription.setText(reclamation.getDescription());
        dpDate.setValue(reclamation.getDateReclamation().toLocalDate());
        tfHeure.setText(reclamation.getHeureReclamation().toString());
        cbStatut.setValue(reclamation.getStatut());
        tfLampadaireId.setText(String.valueOf(reclamation.getLampadaireId()));
        tfCitoyenId.setText(String.valueOf(reclamation.getCitoyenId()));
    }

    @FXML
    private void handleAdd() {
        try {
            validateInputs();

            Reclamation reclamation = new Reclamation(
                    tfDescription.getText(),
                    Date.valueOf(dpDate.getValue()),
                    Time.valueOf(tfHeure.getText() + ":00"), // Format HH:mm:ss
                    cbStatut.getValue(),
                    Integer.parseInt(tfLampadaireId.getText()),
                    Integer.parseInt(tfCitoyenId.getText())
            );

            service.add(reclamation);
            loadData();
            clearForm();
        } catch (Exception e) {
            showAlert("Erreur", e.getMessage());
        }
    }

    @FXML
    private void handleUpdate() {
        if (selectedReclamation == null) {
            showAlert("Erreur", "Aucune réclamation sélectionnée");
            return;
        }

        try {
            validateInputs();

            selectedReclamation.setDescription(tfDescription.getText());
            selectedReclamation.setDateReclamation(Date.valueOf(dpDate.getValue()));
            selectedReclamation.setHeureReclamation(Time.valueOf(tfHeure.getText() + ":00"));
            selectedReclamation.setStatut(cbStatut.getValue());
            selectedReclamation.setLampadaireId(Integer.parseInt(tfLampadaireId.getText()));
            selectedReclamation.setCitoyenId(Integer.parseInt(tfCitoyenId.getText()));

            service.update(selectedReclamation);
            loadData();
            clearForm();
        } catch (Exception e) {
            showAlert("Erreur", e.getMessage());
        }
    }

    @FXML
    private void handleDelete() {
        if (selectedReclamation == null) return;

        if (showConfirmation("Confirmer suppression",
                "Voulez-vous vraiment supprimer cette réclamation ?")) {
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

    private void validateInputs() throws Exception {
        if (tfDescription.getText().isEmpty()) throw new Exception("Description requise");
        if (dpDate.getValue() == null) throw new Exception("Date requise");
        if (tfHeure.getText().isEmpty()) throw new Exception("Heure requise");
        if (cbStatut.getValue() == null) throw new Exception("Statut requis");

        try {
            Time.valueOf(tfHeure.getText() + ":00");
        } catch (IllegalArgumentException e) {
            throw new Exception("Format d'heure invalide (HH:mm)");
        }

        try {
            Integer.parseInt(tfLampadaireId.getText());
            Integer.parseInt(tfCitoyenId.getText());
        } catch (NumberFormatException e) {
            throw new Exception("Les IDs doivent être des nombres valides");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private boolean showConfirmation(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        return alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK;
    }

    @FXML
    private void handleRefresh() {
        loadData(); // Utilise la méthode existante de chargement
    }
}