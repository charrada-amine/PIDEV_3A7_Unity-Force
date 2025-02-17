package tn.esprit.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.Insets;
import javafx.scene.text.Text;
import javafx.scene.text.Font;
import tn.esprit.models.Lampadaire;
import tn.esprit.models.Lampadaire.EtatLampadaire;
import tn.esprit.services.ServiceLampadaire;

import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class GestionLampadaireController implements Initializable {

    @FXML private TextField tfType;
    @FXML private TextField tfPuissance;
    @FXML private ComboBox<EtatLampadaire> cbEtat;
    @FXML private DatePicker dpDateInstallation;
    @FXML private TextField tfIdZone;
    @FXML private FlowPane cardContainer;
    @FXML private ScrollPane scrollPane;

    private final ServiceLampadaire serviceLampadaire = new ServiceLampadaire();
    private final ObservableList<Lampadaire> lampadaires = FXCollections.observableArrayList();
    private Lampadaire selectedLampadaire;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cbEtat.setItems(FXCollections.observableArrayList(EtatLampadaire.values()));

        // Configuration UI
        scrollPane.setFitToWidth(true);
        cardContainer.setHgap(20);
        cardContainer.setVgap(20);
        cardContainer.setPadding(new Insets(20));

        loadData();
    }

    private void loadData() {
        lampadaires.setAll(serviceLampadaire.getAll());
        cardContainer.getChildren().clear();
        lampadaires.forEach(lampadaire ->
                cardContainer.getChildren().add(createLampadaireCard(lampadaire))
        );
    }

    private VBox createLampadaireCard(Lampadaire lampadaire) {
        VBox card = new VBox(10);
        card.getStyleClass().add("card");
        card.setPrefWidth(300);
        card.setPadding(new Insets(15));

        // En-tête
        Text titleText = new Text("Lampadaire #" + lampadaire.getIdLamp());
        titleText.setFont(Font.font("System", 16));

        // Formatage date
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String dateInstallation = (lampadaire.getDateInstallation() != null) ?
                lampadaire.getDateInstallation().format(formatter) : "N/A";

        // Contenu
        VBox content = new VBox(8);
        content.getChildren().addAll(
                createInfoText("Type", lampadaire.getTypeLampadaire()),
                createInfoText("Puissance", lampadaire.getPuissance() + " W"),
                createInfoText("État", lampadaire.getEtat().toString()),
                createInfoText("Installation", dateInstallation),
                createInfoText("Zone", String.valueOf(lampadaire.getIdZone()))
        );

        // Boutons
        HBox buttonBox = new HBox(10);
        Button editButton = createStyledButton("Modifier", "#2196F3");
        Button deleteButton = createStyledButton("Supprimer", "#f44336");

        editButton.setOnAction(e -> fillForm(lampadaire));
        deleteButton.setOnAction(e -> handleDeleteLampadaire(lampadaire));

        buttonBox.getChildren().addAll(editButton, deleteButton);
        card.getChildren().addAll(titleText, new Separator(), content, buttonBox);

        return card;
    }

    private Button createStyledButton(String text, String color) {
        Button button = new Button(text);
        button.setStyle("-fx-background-color: " + color + ";"
                + "-fx-text-fill: white;"
                + "-fx-background-radius: 4;");
        return button; // Return statement ajouté
    }

    private Text createInfoText(String label, String value) {
        return new Text(label + ": " + value); // Return statement ajouté
    }

    private void handleDeleteLampadaire(Lampadaire lampadaire) {
        if (showConfirmation("Confirmation", "Supprimer ce lampadaire ?")) {
            try {
                serviceLampadaire.delete(lampadaire);
                loadData();
            } catch (Exception e) {
                showAlert("Erreur", "Échec de la suppression : " + e.getMessage());
            }
        }
    }
    private void fillForm(Lampadaire lampadaire) {
        tfType.setText(lampadaire.getTypeLampadaire());
        tfPuissance.setText(String.valueOf(lampadaire.getPuissance()));
        cbEtat.setValue(lampadaire.getEtat());
        dpDateInstallation.setValue(lampadaire.getDateInstallation());
        tfIdZone.setText(String.valueOf(lampadaire.getIdZone()));
    }

    private void clearForm() {
        tfType.clear();
        tfPuissance.clear();
        cbEtat.getSelectionModel().clearSelection();
        dpDateInstallation.setValue(null);
        tfIdZone.clear();
        selectedLampadaire = null;
    }

    @FXML
    private void handleAdd() {
        try {
            validateInputs();

            Lampadaire lampadaire = new Lampadaire();
            lampadaire.setTypeLampadaire(tfType.getText());
            lampadaire.setPuissance(Float.parseFloat(tfPuissance.getText()));
            lampadaire.setEtat(cbEtat.getValue());
            lampadaire.setDateInstallation(dpDateInstallation.getValue());
            lampadaire.setIdZone(Integer.parseInt(tfIdZone.getText()));

            serviceLampadaire.add(lampadaire);
            loadData();
            clearForm();
        } catch (Exception e) {
            showAlert("Erreur d'ajout", e.getMessage());
        }
    }

    @FXML
    private void handleUpdate() {
        if (selectedLampadaire == null) {
            showAlert("Erreur", "Veuillez sélectionner un lampadaire à modifier");
            return;
        }

        try {
            validateInputs();

            selectedLampadaire.setTypeLampadaire(tfType.getText());
            selectedLampadaire.setPuissance(Float.parseFloat(tfPuissance.getText()));
            selectedLampadaire.setEtat(cbEtat.getValue());
            selectedLampadaire.setDateInstallation(dpDateInstallation.getValue());
            selectedLampadaire.setIdZone(Integer.parseInt(tfIdZone.getText()));

            serviceLampadaire.update(selectedLampadaire);
            loadData();
            clearForm();
        } catch (Exception e) {
            showAlert("Erreur de modification", e.getMessage());
        }
    }

    @FXML
    private void handleDelete() {
        if (selectedLampadaire == null) {
            showAlert("Erreur", "Veuillez sélectionner un lampadaire à supprimer");
            return;
        }

        if (showConfirmation("Confirmation de suppression",
                "Voulez-vous vraiment supprimer ce lampadaire ?")) {
            try {
                serviceLampadaire.delete(selectedLampadaire);
                loadData();
                clearForm();
            } catch (Exception e) {
                showAlert("Erreur de suppression", e.getMessage());
            }
        }
    }

    @FXML
    private void handleClear() {
        clearForm();
    }

    private void validateInputs() throws Exception {
        if (tfType.getText().isEmpty()) {
            throw new Exception("Le type est requis");
        }
        if (tfPuissance.getText().isEmpty()) {
            throw new Exception("La puissance est requise");
        }
        if (cbEtat.getValue() == null) {
            throw new Exception("L'état est requis");
        }
        if (tfIdZone.getText().isEmpty()) {
            throw new Exception("L'ID de zone est requis");
        }

        try {
            Float.parseFloat(tfPuissance.getText());
        } catch (NumberFormatException e) {
            throw new Exception("La puissance doit être un nombre valide");
        }

        try {
            Integer.parseInt(tfIdZone.getText());
        } catch (NumberFormatException e) {
            throw new Exception("L'ID de zone doit être un nombre entier");
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
}