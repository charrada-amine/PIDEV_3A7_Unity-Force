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
import javafx.scene.paint.Color;
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

        // Style du FlowPane
        cardContainer.setHgap(20);
        cardContainer.setVgap(20);
        cardContainer.setPadding(new Insets(20));

        // Style du ScrollPane
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: #f4f4f4; -fx-background-color: transparent;");

        loadData();
    }

    private void loadData() {
        lampadaires.setAll(serviceLampadaire.getAll());
        cardContainer.getChildren().clear();

        for (Lampadaire lampadaire : lampadaires) {
            cardContainer.getChildren().add(createLampadaireCard(lampadaire));
        }
    }

    private VBox createLampadaireCard(Lampadaire lampadaire) {
        VBox card = new VBox(10);
        card.setPrefWidth(300);
        card.setPadding(new Insets(15));
        card.setStyle("-fx-background-color: white; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 0); " +
                "-fx-background-radius: 8; " +
                "-fx-border-radius: 8;");

        // En-tête de la carte
        Text titleText = new Text("Lampadaire #" + lampadaire.getIdLamp());
        titleText.setFont(Font.font("System", 16));

        // Informations principales
        VBox infoBox = new VBox(8);
        Text typeText = createInfoText("Type", lampadaire.getTypeLampadaire());
        Text puissanceText = createInfoText("Puissance", lampadaire.getPuissance() + " W");
        Text etatText = createInfoText("État", lampadaire.getEtat().toString());
        Text dateText = createInfoText("Installation",
                lampadaire.getDateInstallation() != null ?
                        lampadaire.getDateInstallation().format(DateTimeFormatter.ISO_LOCAL_DATE) : "N/A");
        Text zoneText = createInfoText("Zone", String.valueOf(lampadaire.getIdZone()));

        infoBox.getChildren().addAll(typeText, puissanceText, etatText, dateText, zoneText);

        // Boutons d'action
        HBox buttonBox = new HBox(10);
        buttonBox.setPadding(new Insets(10, 0, 0, 0));

        Button editButton = createStyledButton("Modifier", "#2196F3");
        Button deleteButton = createStyledButton("Supprimer", "#f44336");

        editButton.setOnAction(e -> {
            selectedLampadaire = lampadaire;
            fillForm(lampadaire);
        });

        deleteButton.setOnAction(e -> {
            if (showConfirmation("Confirmation de suppression",
                    "Voulez-vous vraiment supprimer ce lampadaire ?")) {
                try {
                    serviceLampadaire.delete(lampadaire);
                    loadData();
                    clearForm();
                } catch (Exception ex) {
                    showAlert("Erreur de suppression", ex.getMessage());
                }
            }
        });

        buttonBox.getChildren().addAll(editButton, deleteButton);

        // Assemblage de la carte
        card.getChildren().addAll(titleText, new Separator(), infoBox, buttonBox);

        return card;
    }

    private Text createInfoText(String label, String value) {
        return new Text(label + ": " + value);
    }

    private Button createStyledButton(String text, String color) {
        Button button = new Button(text);
        button.setStyle("-fx-background-color: " + color + ";" +
                "-fx-text-fill: white;" +
                "-fx-background-radius: 4;");
        return button;
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
