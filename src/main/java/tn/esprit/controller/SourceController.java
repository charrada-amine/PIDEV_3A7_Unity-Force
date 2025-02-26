package tn.esprit.controller;

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
import tn.esprit.models.Source;
import tn.esprit.Enumerations.EnumEtat;
import tn.esprit.Enumerations.EnumType;
import tn.esprit.services.ServiceSource;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;

public class SourceController implements Initializable {


    @FXML private Button btnBack;
    @FXML private Button btnprofile;

    @FXML private ComboBox<EnumType> cbType; // Utilisation d'un ComboBox pour le type
    @FXML private TextField tfCapacite;
    @FXML private TextField tfRendement;
    @FXML private TextField  tfNom;
    @FXML private ComboBox<EnumEtat> cbEtat;
    @FXML private FlowPane cardContainer;
    @FXML private ScrollPane scrollPane;
    private final ServiceSource serviceSource = new ServiceSource();
    private final ObservableList<Source> sources = FXCollections.observableArrayList();
    private Source selectedSource;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cbType.setItems(FXCollections.observableArrayList(EnumType.values())); // Initialisation du ComboBox pour le type
        cbEtat.setItems(FXCollections.observableArrayList(EnumEtat.values()));

        scrollPane.setFitToWidth(true);
        cardContainer.setHgap(20);
        cardContainer.setVgap(20);
        cardContainer.setPadding(new Insets(20));
        Font.loadFont(getClass().getResourceAsStream("/fonts/Roboto-Regular.ttf"), 14);

        loadData();
    }

    private void loadData() {
        sources.setAll(serviceSource.getAll());
        cardContainer.getChildren().clear();
        sources.forEach(source -> cardContainer.getChildren().add(createSourceCard(source)));
    }

    private Button createStyledButton(String text, String color) {
        Button button = new Button(text);
        button.setStyle("-fx-background-color: " + color + ";-fx-text-fill: white;-fx-background-radius: 8;-fx-padding: 8 16;");
        return button;
    }

    private Text createInfoText(String label, String value) {
        Text text = new Text(label + ": " + value);
        text.setFont(Font.font("Roboto", 14));
        return text;
    }

    private void handleDeleteSource(Source source) {
        if (showConfirmation("Confirmation", "Supprimer cette source ?")) {
            try {
                serviceSource.delete(source.getIdSource());
                loadData();
                showSuccessFeedback();
            } catch (Exception e) {
                showAlert("Erreur", "Échec de la suppression : " + e.getMessage());
            }
        }
    }

    private void fillForm(Source source) {
        selectedSource = source;
        cbType.setValue(source.getType());
        tfCapacite.setText(String.valueOf(source.getCapacite()));
        tfRendement.setText(String.valueOf(source.getRendement()));
        cbEtat.setValue(source.getEtat());
        tfNom.setText(source.getNom()); // Remplir le champ nom
    }


    private void clearForm() {
        cbType.getSelectionModel().clearSelection();
        tfCapacite.clear();
        tfRendement.clear();
        cbEtat.getSelectionModel().clearSelection();
        tfNom.clear(); // Effacer le champ nom
        selectedSource = null;
    }



    @FXML

    private void handleAdd() {
        try {
            validateInputs();
            Source source = new Source();
            source.setType(cbType.getValue());
            source.setCapacite(Float.parseFloat(tfCapacite.getText()));
            source.setRendement(Float.parseFloat(tfRendement.getText()));
            source.setEtat(cbEtat.getValue());
            source.setDateInstallation(LocalDate.now()); // Automatically set to current date
            source.setNom(tfNom.getText()); // Récupérer le nom depuis le champ
            serviceSource.add(source);
            loadData();
            clearForm();
            showSuccessFeedback();
        } catch (Exception e) {
            showAlert("Erreur d'ajout", e.getMessage());
        }
    }




    @FXML
    private void handleUpdate() {
        if (selectedSource == null) {
            showAlert("Erreur", "Veuillez sélectionner une source à modifier");
            return;
        }
        try {
            validateInputs();
            selectedSource.setType(cbType.getValue());
            selectedSource.setCapacite(Float.parseFloat(tfCapacite.getText()));
            selectedSource.setRendement(Float.parseFloat(tfRendement.getText()));
            selectedSource.setEtat(cbEtat.getValue());
            selectedSource.setNom(tfNom.getText()); // Mettre à jour le nom
            // Ne pas modifier la date d'installation
            // selectedSource.setDateInstallation reste inchangé

            serviceSource.update(selectedSource);
            loadData();
            clearForm();
            showSuccessFeedback();
        } catch (Exception e) {
            showAlert("Erreur de modification", e.getMessage());
        }
    }




    //@FXML
    /*private void handleShowSources() {
        try {
            clearForm();
            sources.setAll(serviceSource.getAll());
            cardContainer.getChildren().clear();
            sources.forEach(s -> cardContainer.getChildren().add(createSourceCard(s)));
            showSuccessFeedback();
        } catch (Exception e) {
            showAlert("Erreur", "Impossible de charger les sources : " + e.getMessage());
        }
    }*/

    @FXML
    private void handleClear() {
        clearForm();
    }

    private void validateInputs() throws Exception {
        if (cbType.getValue() == null || tfCapacite.getText().isEmpty() || tfRendement.getText().isEmpty() || cbEtat.getValue() == null || tfNom.getText().isEmpty()) {
            throw new Exception("Tous les champs doivent être remplis.");
        }
        // Vous pouvez ajouter d'autres validations ici
    }
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.initStyle(StageStyle.TRANSPARENT);
        alert.getDialogPane().getScene().getRoot().setStyle("-fx-background-color: rgba(255,255,255,0.95);-fx-background-radius: 16;-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 16, 0, 4, 4);");
        alert.getDialogPane().setOpacity(0);
        Timeline fadeIn = new Timeline(new KeyFrame(Duration.millis(200), new KeyValue(alert.getDialogPane().opacityProperty(), 1)));
        fadeIn.play();
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
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

    private void showSuccessFeedback() {
        Pane root = (Pane) cardContainer.getParent();
        Label feedback = new Label("✓ Opération réussie !");
        feedback.setStyle("-fx-background-color: linear-gradient(to right, #34a853, #2d8a4a); " +
                "-fx-text-fill: white; -fx-padding: 12 24; -fx-background-radius: 24; " +
                "-fx-font-weight: 700;");
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

    private VBox createSourceCard(Source source) {
        VBox card = new VBox(15);
        card.getStyleClass().add("card");

        // En-tête avec icône
        HBox header = new HBox(10);
        FontIcon icon = new FontIcon(FontAwesomeSolid.BOLT);
        icon.setIconSize(24);
        icon.setIconColor(Color.web("#1a73e8"));

        Label title = new Label("NAME #" + source.getNom());
        title.setStyle("-fx-font-size: 18; -fx-text-fill: #202124;");

        header.getChildren().addAll(icon, title);

        // Contenu
        VBox content = new VBox(8);
        content.getChildren().addAll(
                createInfoRow(FontAwesomeSolid.TAG, "Type : " + source.getType().toString()),
                createInfoRow(FontAwesomeSolid.BOLT, "Capacité : " + source.getCapacite() + " W"),
                createInfoRow(FontAwesomeSolid.BOLT, "Rendement : " + source.getRendement() + " %"),

                createInfoRow(FontAwesomeSolid.POWER_OFF, "État : " + source.getEtat().toString()),
                createInfoRow(FontAwesomeSolid.CALENDAR, "Installation : " + source.getDateInstallation())
        );

        // Boutons d'action
        HBox buttons = new HBox(10);

        // Bouton Modifier
        Button btnModifier = createIconButton("Modifier", FontAwesomeSolid.PENCIL_ALT, "-secondary");
        btnModifier.setOnAction(e -> fillForm(source));

        // Bouton Supprimer
        Button btnSupprimer = createIconButton("Supprimer", FontAwesomeSolid.TRASH, "#ea4335");
        btnSupprimer.setOnAction(e -> handleDeleteSource(source));

        buttons.getChildren().addAll(btnModifier, btnSupprimer);

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
        button.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white;");
        button.setContentDisplay(ContentDisplay.LEFT);
        button.setGraphicTextGap(8);
        return button;
    }


    @FXML
    private void handleShowSources() {
        switchScene("/ProfileInterface.fxml",btnprofile);

    }

    @FXML
    private void handleBack(ActionEvent event) {
        switchScene("/Menu.fxml",btnBack);
    }








    private void switchScene(String fxmlPath, Button button) {
        try {
            // Charger le fichier FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            // Obtenir le stage actuel (fenêtre)
            Stage stage = (Stage) button.getScene().getWindow();

            // Garder la taille actuelle de la fenêtre
            double width = stage.getWidth();
            double height = stage.getHeight();

            // Appliquer la nouvelle scène et conserver la taille de la fenêtre
            stage.setScene(new Scene(root, width, height));

            // Permettre le redimensionnement de la fenêtre
            stage.setResizable(true);

            // Afficher la nouvelle scène
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}