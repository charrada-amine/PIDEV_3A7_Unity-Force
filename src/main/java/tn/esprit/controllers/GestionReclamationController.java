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
import tn.esprit.models.Reclamation;
import tn.esprit.services.ServiceReclamation;
import java.net.URL;
import java.sql.Date;
import java.sql.Time;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.ResourceBundle;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;
import org.json.JSONObject;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import java.io.File;

public class GestionReclamationController implements Initializable {

    @FXML private TextField tfDescription;
    @FXML private DatePicker dpDate;
    @FXML private TextField tfHeure;
    @FXML private ComboBox<String> cbStatut;
    @FXML private TextField tfLampadaireId;
    @FXML private TextField tfCitoyenId;
    @FXML private FlowPane cardContainer;
    @FXML private ScrollPane scrollPane;
    @FXML private ComboBox<String> cbSort;

    private final ServiceReclamation serviceReclamation = new ServiceReclamation();
    private final ObservableList<Reclamation> reclamations = FXCollections.observableArrayList();
    private Reclamation selectedReclamation;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cbStatut.setItems(FXCollections.observableArrayList("Ouvert", "En cours", "Résolu", "Fermé"));
        cbSort.setItems(FXCollections.observableArrayList("Aucun tri", "Date croissante", "Date décroissante"));
        cbSort.setValue("Aucun tri");
        cbSort.setOnAction(e -> sortReclamations());
        scrollPane.setFitToWidth(true);
        cardContainer.setHgap(20);
        cardContainer.setVgap(20);
        cardContainer.setPadding(new Insets(20));
        Font.loadFont(getClass().getResourceAsStream("/fonts/Roboto-Regular.ttf"), 14);
        loadData();
    }

    private void loadData() {
        reclamations.setAll(serviceReclamation.getAll());
        sortReclamations();
    }

    private void sortReclamations() {
        String sortOption = cbSort.getValue();
        if (sortOption != null) {
            switch (sortOption) {
                case "Date croissante":
                    reclamations.sort(Comparator.comparing(Reclamation::getDateReclamation));
                    break;
                case "Date décroissante":
                    reclamations.sort(Comparator.comparing(Reclamation::getDateReclamation).reversed());
                    break;
                default:
                    reclamations.setAll(serviceReclamation.getAll());
                    break;
            }
            refreshCards();
        }
    }

    private void refreshCards() {
        cardContainer.getChildren().clear();
        reclamations.forEach(reclamation -> cardContainer.getChildren().add(createReclamationCard(reclamation)));
    }

    private void handleDeleteReclamation(Reclamation reclamation) {
        if (showConfirmation("Confirmation", "Supprimer cette réclamation ?")) {
            try {
                serviceReclamation.delete(reclamation);
                loadData();
                showSuccessFeedback();
            } catch (Exception e) {
                showAlert("Erreur", "Échec de la suppression : " + e.getMessage());
            }
        }
    }

    private void fillForm(Reclamation reclamation) {
        selectedReclamation = reclamation;
        tfDescription.setText(reclamation.getDescription());
        dpDate.setValue(reclamation.getDateReclamation().toLocalDate());
        tfHeure.setText(reclamation.getHeureReclamation().toString());
        cbStatut.setValue(reclamation.getStatut());
        tfLampadaireId.setText(String.valueOf(reclamation.getLampadaireId()));
        tfCitoyenId.setText(String.valueOf(reclamation.getCitoyenId()));
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

    // Méthode pour vérifier les mots inappropriés
    private boolean checkBadWords(String text) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://bad-words-filter-api.p.rapidapi.com/badwords"))
                    .header("x-rapidapi-key", "6abc316058msh50ae9be18c5e6d8p1ca615jsn3bf705815064")
                    .header("x-rapidapi-host", "bad-words-filter-api.p.rapidapi.com")
                    .header("Content-Type", "application/json")
                    .method("POST", HttpRequest.BodyPublishers.ofString("{\"text\":\"" + text + "\"}"))
                    .build();

            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            String responseBody = response.body();
            System.out.println("Réponse de l'API pour '" + text + "' : " + responseBody);

            JSONObject jsonResponse = new JSONObject(responseBody);

            if (jsonResponse.has("contains_bad_words")) {
                return jsonResponse.getBoolean("contains_bad_words");
            } else if (jsonResponse.has("bad_words") && jsonResponse.getJSONArray("bad_words").length() > 0) {
                return true;
            } else if (jsonResponse.has("is_profane")) {
                return jsonResponse.getBoolean("is_profane");
            }

            return false;
        } catch (Exception e) {
            showAlert("Erreur API", "Erreur lors de la vérification des mots inappropriés : " + e.getMessage());
            System.err.println("Erreur API : " + e.getMessage());
            return true; // Refuser si l'API échoue
        }
    }

    // Méthode pour générer un PDF
    private void generatePdf(Reclamation reclamation) {
        try {
            String fileName = "PDFs/Reclamation_" + reclamation.getID_reclamation() + ".pdf";
            new File("PDFs").mkdirs(); // Crée le dossier s'il n'existe pas
            PdfWriter writer = new PdfWriter(new File(fileName));
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

            document.add(new Paragraph("Détails de la Réclamation #" + reclamation.getID_reclamation())
                    .setBold().setFontSize(16));
            document.add(new Paragraph("Description : " + reclamation.getDescription()));
            document.add(new Paragraph("Date : " + reclamation.getDateReclamation().toLocalDate().format(dateFormatter)));
            document.add(new Paragraph("Heure : " + reclamation.getHeureReclamation()));
            document.add(new Paragraph("Statut : " + reclamation.getStatut()));
            document.add(new Paragraph("Lampadaire ID : " + reclamation.getLampadaireId()));
            document.add(new Paragraph("Citoyen ID : " + reclamation.getCitoyenId()));

            document.close();
            showSuccessFeedback("PDF généré avec succès : " + fileName);
        } catch (Exception e) {
            showAlert("Erreur PDF", "Erreur lors de la génération du PDF : " + e.getMessage());
        }
    }

    @FXML
    private void handleAdd() {
        try {
            validateInputs();
            String description = tfDescription.getText();

            if (checkBadWords(description)) {
                showAlert("Contenu inapproprié", "La description contient des mots inappropriés. Veuillez la modifier.");
                return;
            }

            Reclamation reclamation = new Reclamation(
                    description,
                    Date.valueOf(dpDate.getValue()),
                    Time.valueOf(tfHeure.getText()),
                    cbStatut.getValue(),
                    Integer.parseInt(tfLampadaireId.getText()),
                    Integer.parseInt(tfCitoyenId.getText())
            );
            serviceReclamation.add(reclamation);
            loadData();
            clearForm();
            showSuccessFeedback();
        } catch (Exception e) {
            showAlert("Erreur d'ajout", e.getMessage());
        }
    }

    @FXML
    private void handleUpdate() {
        if (selectedReclamation == null) {
            showAlert("Erreur", "Veuillez sélectionner une réclamation");
            return;
        }
        try {
            validateInputs();
            String description = tfDescription.getText();

            if (checkBadWords(description)) {
                showAlert("Contenu inapproprié", "La description contient des mots inappropriés. Veuillez la modifier.");
                return;
            }

            selectedReclamation.setDescription(description);
            selectedReclamation.setDateReclamation(Date.valueOf(dpDate.getValue()));
            selectedReclamation.setHeureReclamation(Time.valueOf(tfHeure.getText()));
            selectedReclamation.setStatut(cbStatut.getValue());
            selectedReclamation.setLampadaireId(Integer.parseInt(tfLampadaireId.getText()));
            selectedReclamation.setCitoyenId(Integer.parseInt(tfCitoyenId.getText()));

            serviceReclamation.update(selectedReclamation);
            loadData();
            clearForm();
            showSuccessFeedback();
        } catch (Exception e) {
            showAlert("Erreur de modification", e.getMessage());
        }
    }

    @FXML
    private void handleDelete() {
        if (selectedReclamation == null) {
            showAlert("Erreur", "Veuillez sélectionner une réclamation");
            return;
        }
        if (showConfirmation("Confirmation", "Supprimer cette réclamation ?")) {
            try {
                serviceReclamation.delete(selectedReclamation);
                loadData();
                clearForm();
                showSuccessFeedback();
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
        if (tfDescription.getText().isEmpty() ||
                dpDate.getValue() == null ||
                tfHeure.getText().isEmpty() ||
                cbStatut.getValue() == null ||
                tfLampadaireId.getText().isEmpty() ||
                tfCitoyenId.getText().isEmpty()) {
            throw new Exception("Tous les champs obligatoires doivent être remplis");
        }

        try {
            Time.valueOf(tfHeure.getText());
        } catch (Exception e) {
            throw new Exception("Format d'heure invalide (HH:MM:SS)");
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
        alert.initStyle(StageStyle.TRANSPARENT);
        alert.getDialogPane().getScene().getRoot().setStyle(
                "-fx-background-color: rgba(255,255,255,0.95);" +
                        "-fx-background-radius: 16;" +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 16, 0, 4, 4);"
        );
        alert.getDialogPane().setOpacity(0);
        Timeline fadeIn = new Timeline(
                new KeyFrame(Duration.millis(200),
                        new KeyValue(alert.getDialogPane().opacityProperty(), 1)
                )
        );
        fadeIn.play();
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private boolean showConfirmation(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.initStyle(StageStyle.TRANSPARENT);
        alert.getDialogPane().getScene().getRoot().setStyle(
                "-fx-background-color: rgba(255,255,255,0.95);" +
                        "-fx-background-radius: 16;" +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 16, 0, 4, 4);"
        );
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        return alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK;
    }

    private void showSuccessFeedback() {
        showSuccessFeedback("Opération réussie !");
    }

    private void showSuccessFeedback(String message) {
        Pane root = (Pane) cardContainer.getParent();
        Label feedback = new Label("✓ " + message);
        feedback.setStyle(
                "-fx-background-color: linear-gradient(to right, #34a853, #2d8a4a);" +
                        "-fx-text-fill: white;" +
                        "-fx-padding: 12 24;" +
                        "-fx-background-radius: 24;" +
                        "-fx-font-weight: 700;"
        );
        feedback.setTranslateY(-50);
        feedback.setOpacity(0);
        root.getChildren().add(feedback);
        Timeline animation = new Timeline(
                new KeyFrame(Duration.millis(300),
                        new KeyValue(feedback.translateYProperty(), 20),
                        new KeyValue(feedback.opacityProperty(), 1)
                ),
                new KeyFrame(Duration.millis(2000),
                        new KeyValue(feedback.opacityProperty(), 0)
                )
        );
        animation.setOnFinished(e -> root.getChildren().remove(feedback));
        animation.play();
    }

    private VBox createReclamationCard(Reclamation reclamation) {
        VBox card = new VBox(15);
        card.getStyleClass().add("card");

        HBox header = new HBox(10);
        FontIcon icon = new FontIcon(FontAwesomeSolid.EXCLAMATION_CIRCLE);
        icon.setIconSize(24);
        icon.setIconColor(Color.web("#1a73e8"));

        Label title = new Label("Réclamation #" + reclamation.getID_reclamation());
        title.setStyle("-fx-font-size: 18; -fx-text-fill: #202124;");

        header.getChildren().addAll(icon, title);

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String dateFormatted = reclamation.getDateReclamation().toLocalDate().format(dateFormatter);

        VBox content = new VBox(8);
        content.getChildren().addAll(
                createInfoRow(FontAwesomeSolid.COMMENT, "Description : " + reclamation.getDescription()),
                createInfoRow(FontAwesomeSolid.CALENDAR, "Date : " + dateFormatted),
                createInfoRow(FontAwesomeSolid.CLOCK, "Heure : " + reclamation.getHeureReclamation()),
                createInfoRow(FontAwesomeSolid.INFO_CIRCLE, "Statut : " + reclamation.getStatut()),
                createInfoRow(FontAwesomeSolid.LIGHTBULB, "Lampadaire ID : " + reclamation.getLampadaireId()),
                createInfoRow(FontAwesomeSolid.USER, "Citoyen ID : " + reclamation.getCitoyenId())
        );

        HBox buttons = new HBox(10);
        Button btnModifier = createIconButton("Modifier", FontAwesomeSolid.PENCIL_ALT, "#4a90e2");
        btnModifier.setOnAction(e -> fillForm(reclamation));

        Button btnSupprimer = createIconButton("Supprimer", FontAwesomeSolid.TRASH, "#ff6b6b");
        btnSupprimer.setOnAction(e -> handleDeleteReclamation(reclamation));

        Button btnGeneratePdf = createIconButton("Générer PDF", FontAwesomeSolid.FILE_PDF, "#28a745");
        btnGeneratePdf.setOnAction(e -> generatePdf(reclamation));

        buttons.getChildren().addAll(btnModifier, btnSupprimer, btnGeneratePdf);

        card.getChildren().addAll(header, new Separator(), content, buttons);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-padding: 16;");
        card.setEffect(new DropShadow(10, Color.gray(0.3)));

        return card;
    }

    private HBox createInfoRow(FontAwesomeSolid iconType, String text) {
        FontIcon icon = new FontIcon(iconType);
        icon.setIconSize(16);
        icon.setIconColor(Color.web("#5f6368"));

        Label label = new Label(text);
        label.setStyle("-fx-text-fill: #5f6368;");

        return new HBox(10, icon, label);
    }

    private Button createIconButton(String text, FontAwesomeSolid iconType, String color) {
        FontIcon icon = new FontIcon(iconType);
        icon.setIconSize(16);
        icon.setIconColor(Color.WHITE);

        Button button = new Button(text, icon);
        button.setStyle(
                "-fx-background-color: " + color + ";" +
                        "-fx-text-fill: white;" +
                        "-fx-background-radius: 8;" +
                        "-fx-padding: 8 16;"
        );
        button.setContentDisplay(ContentDisplay.LEFT);
        button.setGraphicTextGap(8);
        return button;
    }

    @FXML
    private void handleNavigateToInterventions(ActionEvent event) {
        loadView("GestionIntervention.fxml", event);
    }

    @FXML
    private void handleShowReclamations(ActionEvent event) {
        loadData();
        showSuccessFeedback();
    }

    @FXML
    private void handleBack(ActionEvent event) {
        loadView("MainMenu.fxml", event);
    }

    private void loadView(String fxmlFile, ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/" + fxmlFile));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            showAlert("Erreur de navigation", "Impossible de charger la vue : " + fxmlFile);
        }
    }
}