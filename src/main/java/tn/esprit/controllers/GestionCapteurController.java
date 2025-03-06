package tn.esprit.controllers;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.geometry.Insets;
import javafx.stage.FileChooser;
import org.kordamp.ikonli.javafx.FontIcon;
import tn.esprit.models.Capteur;
import tn.esprit.models.Capteur.EtatCapteur;
import tn.esprit.models.Capteur.TypeCapteur;
import tn.esprit.models.Session;
import tn.esprit.models.utilisateur;
import tn.esprit.services.ServiceCapteur;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;
import javafx.scene.layout.FlowPane;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.stream.Collectors;

public class GestionCapteurController implements Initializable {

    @FXML
    private TextField idField;

    @FXML
    private ComboBox<TypeCapteur> typeComboBox;

    @FXML
    private ComboBox<EtatCapteur> etatComboBox;

    @FXML
    private DatePicker datePicker;

    @FXML
    private TextField lampadaireIdField;

    @FXML
    private FlowPane capteurCardContainer;

    // Déclarer les ComboBox
    @FXML
    private ComboBox<String> searchCriteriaComboBox; // Pour choisir le critère (type ou état)
    @FXML
    private ComboBox<String> searchValueComboBox;   // Pour choisir la valeur (type ou état)



    private ServiceCapteur serviceCapteur;

    private Capteur selectedCapteur;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        serviceCapteur = new ServiceCapteur();

        // Initialiser les ComboBox
        typeComboBox.setItems(FXCollections.observableArrayList(TypeCapteur.values()));
        etatComboBox.setItems(FXCollections.observableArrayList(EtatCapteur.values()));
        // Initialiser les ComboBox pour le type et l'état
        typeComboBox.setItems(FXCollections.observableArrayList(TypeCapteur.values()));
        etatComboBox.setItems(FXCollections.observableArrayList(EtatCapteur.values()));

        // Initialiser la ComboBox des critères de recherche
        searchCriteriaComboBox.setItems(FXCollections.observableArrayList("Type", "État"));
        searchCriteriaComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            updateSearchValueComboBox(newVal);
        });


        // Charger les capteurs
        loadCapteurs();
    }

    private void loadCapteurs() {
        capteurCardContainer.getChildren().clear();
        System.out.println("Nombre de capteurs récupérés : " + serviceCapteur.getAll().size()); // Debug

        for (Capteur capteur : serviceCapteur.getAll()) {
            VBox card = createCapteurCard(capteur);
            capteurCardContainer.getChildren().add(card);
            System.out.println("Carte ajoutée : " + capteur.getType() + " - " + capteur.getEtat()); // Debug
        }

        System.out.println("Total cartes affichées : " + capteurCardContainer.getChildren().size()); // Debug
    }


    private VBox createCapteurCard(Capteur capteur) {
        VBox card = new VBox();
        card.setSpacing(10);
        card.setPadding(new Insets(15));
        card.setPrefWidth(220); // Largeur de la carte
        card.setPrefHeight(120); // Hauteur de la carte

        // Appliquer une classe CSS pour un style uniforme
        card.getStyleClass().add("capteur-card");

        // Créer un GridPane pour organiser les labels et les icônes
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(5);

        // Ajouter des icônes à gauche des labels
        FontIcon idIcon = new FontIcon("fas-hashtag");
        FontIcon typeIcon = new FontIcon("fas-tag");
        FontIcon etatIcon = new FontIcon("fas-power-off");
        FontIcon dateIcon = new FontIcon("fas-calendar-alt");
        FontIcon lampadaireIcon = new FontIcon("fas-lightbulb");

        // Modifier l'affichage de l'ID avec un préfixe
        String prefix = "";
        TypeCapteur type = capteur.getType(); // Récupérer le type de capteur

        // Utiliser le nom de l'énumération pour déterminer le préfixe
        switch (type) {
            case MOUVEMENT:
                prefix = "mv";
                break;
            case LUMINOSITE:
                prefix = "lum";
                break;
            case TEMPERATURE:
                prefix = "temp";
                break;
            case CONSOMMATION_ENERGIE:
                prefix = "coe";
                break;
            default:
                prefix = "ref";
                break;
        }
        String idText = "Référence: " + prefix + capteur.getId();

        // Labels avec icônes
        Label idLabel = createLabelWithIcon(idText, idIcon);
        Label typeLabel = createLabelWithIcon("Type: " + type.name().toLowerCase(), typeIcon);
        Label etatLabel = createLabelWithIcon("État: " + capteur.getEtat(), etatIcon);
        Label dateLabel = createLabelWithIcon("Date d'installation: " + capteur.getDateinstallation(), dateIcon);
        Label lampadaireIdLabel = createLabelWithIcon("Lampadaire ID: " + capteur.getLampadaireId(), lampadaireIcon);

        // Ajouter les labels au GridPane
        grid.add(idLabel, 0, 0);
        grid.add(typeLabel, 0, 1);
        grid.add(etatLabel, 0, 2);
        grid.add(dateLabel, 0, 3);
        grid.add(lampadaireIdLabel, 0, 4);

        // Ajouter un effet visuel au survol
        card.setOnMouseEntered(event -> card.setStyle("-fx-background-color: #f0f0f0;"));
        card.setOnMouseExited(event -> card.setStyle("-fx-background-color: white;"));

        // Gestionnaire d'événements pour sélectionner un capteur
        card.setOnMouseClicked(event -> selectCapteur(capteur));

        // Ajouter le GridPane à la carte
        card.getChildren().add(grid);
        card.setUserData(capteur);
        return card;
    }

    // Méthode pour créer un label avec une icône
    private Label createLabelWithIcon(String text, FontIcon icon) {
        Label label = new Label(text);
        label.setGraphic(icon);
        label.getStyleClass().add("capteur-label");
        return label;
    }


    private void selectCapteur(Capteur capteur) {
        selectedCapteur = capteur;
        idField.setText(String.valueOf(capteur.getId()));
        typeComboBox.setValue(capteur.getType());
        datePicker.setValue(capteur.getDateinstallation());
        etatComboBox.setValue(capteur.getEtat());
        lampadaireIdField.setText(String.valueOf(capteur.getLampadaireId()));
    }

    @FXML
    private void handleAddCapteur() {
        if (!validateFields()) {
            return;
        }

        try {
            Capteur capteur = new Capteur(
                    0,
                    typeComboBox.getValue(),
                    datePicker.getValue(),
                    etatComboBox.getValue(),
                    Integer.parseInt(lampadaireIdField.getText())
            );
            serviceCapteur.add(capteur);
            loadCapteurs();
            clearFields();
            showSuccessAlert("Capteur ajouté avec succès !");
        } catch (Exception e) {
            showAlert("Erreur", "Une erreur est survenue lors de l'ajout du capteur.");
        }
    }

    @FXML
    private void handleUpdateCapteur() {
        if (selectedCapteur == null) {
            showAlert("Erreur", "Veuillez sélectionner un capteur à modifier.");
            return;
        }

        if (!validateFields()) {
            return;
        }

        try {
            Capteur capteur = new Capteur(
                    Integer.parseInt(idField.getText()),
                    typeComboBox.getValue(),
                    datePicker.getValue(),
                    etatComboBox.getValue(),
                    Integer.parseInt(lampadaireIdField.getText())
            );
            serviceCapteur.update(capteur);
            loadCapteurs();
            clearFields();
            showSuccessAlert("Capteur mis à jour avec succès !");
        } catch (Exception e) {
            showAlert("Erreur", "Une erreur est survenue lors de la modification du capteur.");
        }
    }

    @FXML
    private void handleDeleteCapteur() {
        if (selectedCapteur == null) {
            showAlert("Erreur", "Veuillez sélectionner un capteur à supprimer.");
            return;
        }

        serviceCapteur.delete(selectedCapteur.getId());
        loadCapteurs();
        clearFields();
        selectedCapteur = null;
        showSuccessAlert("Capteur supprimé avec succès !");
    }

    @FXML
    private void handleRefresh() {
        loadCapteurs();
    }

    private boolean validateFields() {
        String errorMessage = "";

        if (typeComboBox.getValue() == null) {
            errorMessage += "- Veuillez sélectionner un type de capteur.\n";
        }

        if (etatComboBox.getValue() == null) {
            errorMessage += "- Veuillez sélectionner un état pour le capteur.\n";
        }

        if (datePicker.getValue() == null) {
            errorMessage += "- Veuillez choisir une date d'installation.\n";
        } else if (datePicker.getValue().isAfter(LocalDate.now())) {
            errorMessage += "- La date d'installation ne peut pas être dans le futur.\n";
        }

        if (lampadaireIdField.getText().isEmpty()) {
            errorMessage += "- Veuillez entrer l'ID du lampadaire.\n";
        } else {
            try {
                Integer.parseInt(lampadaireIdField.getText());
            } catch (NumberFormatException e) {
                errorMessage += "- L'ID du lampadaire doit être un nombre valide.\n";
            }
        }

        if (!errorMessage.isEmpty()) {
            showAlert("Validation des champs", errorMessage);
            return false;
        }

        return true;
    }

    private void clearFields() {
        idField.clear();
        typeComboBox.getSelectionModel().clearSelection();
        etatComboBox.getSelectionModel().clearSelection();
        datePicker.setValue(null);
        lampadaireIdField.clear();
    }

    private void showSuccessAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Succès");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void handleGoToGestionDonnee(javafx.event.ActionEvent actionEvent) {
        try {
            Parent gestionDonneeParent = FXMLLoader.load(getClass().getResource("/GestionDonnee.fxml"));
            Scene gestionDonneeScene = new Scene(gestionDonneeParent);
            Stage window = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            window.setScene(gestionDonneeScene);
            window.show();
        } catch (IOException e) {
            System.err.println("Erreur lors du chargement de GestionDonnee.fxml : " + e.getMessage());
        }
    }



    @FXML
    private Button btnNavigateToCameras; // Bouton pour l'interface Caméras

    @FXML
    private Button btnNavigateToLampadaireMap; // Bouton pour l'interface Carte Lampadaires

    @FXML
    private Button btnNavigateToZoneCitoyen; // Bouton pour l'interface Vue Citoyen

    @FXML
    private Button btnGestionCapteur;

    @FXML
    private Button btnGestionCitoyen;

    @FXML
    private Button btnGestionDonnee;

    @FXML
    private Button btnGestionIntervention;

    @FXML
    private Button btnGestionLampadaire;

    @FXML
    private Button btnGestionReclamation;

    @FXML
    private Button btnGestionResponsable;

    @FXML
    private Button btnGestionTechnicien;

    @FXML
    private Button btnGestionZone;

    @FXML
    private Button btnProfileInterface;

    @FXML
    private Button btnSourceInterface;

    @FXML
    private Button btnAccueil; // Bouton pour revenir à l'accueil

    // Handler pour le bouton de navigation vers l'interface Caméras
    @FXML
    private void handleNavigateToCameras(ActionEvent event) {
        if (hasPermission("Caméras")) {
            switchScene(event, "/GestionCamera.fxml");
        }
    }

    // Handler pour le bouton de navigation vers l'interface Carte Lampadaires
    @FXML
    private void handleNavigateToLampadaireMap(ActionEvent event) {
        if (hasPermission("Carte Lampadaires")) {
            switchScene(event, "/LampadaireMapView.fxml");
        }
    }

    // Handler pour le bouton de navigation vers l'interface Vue Citoyen
    @FXML
    private void handleNavigateToZoneCitoyen(ActionEvent event) {
        if (hasPermission("Vue Citoyen")) {
            switchScene(event, "/ZoneCitoyenView.fxml");
        }
    }

    // Handler pour le bouton de gestion des capteurs
    @FXML
    private void handleGestionCapteur(ActionEvent event) {
        if (hasPermission("Capteurs")) {
            switchScene(event, "/GestionCapteur.fxml");
        }
    }

    // Handler pour le bouton de gestion des citoyens
    @FXML
    private void handleGestionCitoyen(ActionEvent event) {
        if (hasPermission("Citoyens")) {
            switchScene(event, "/GestionCitoyen.fxml");
        }
    }

    // Handler pour le bouton de gestion des données
    @FXML
    private void handleGestionDonnee(ActionEvent event) {
        if (hasPermission("Données")) {
            switchScene(event, "/GestionDonnee.fxml");
        }
    }

    // Handler pour le bouton de gestion des interventions
    @FXML
    private void handleGestionIntervention(ActionEvent event) {
        if (hasPermission("Interventions")) {
            switchScene(event, "/GestionIntervention.fxml");
        }
    }

    // Handler pour le bouton de gestion des lampadaires
    @FXML
    private void handleGestionLampadaire(ActionEvent event) {
        if (hasPermission("Lampadaires")) {
            switchScene(event, "/GestionLampadaire.fxml");
        }
    }

    // Handler pour le bouton de gestion des réclamations
    @FXML
    private void handleGestionReclamation(ActionEvent event) {
        if (hasPermission("Réclamations")) {
            switchScene(event, "/GestionReclamation.fxml");
        }
    }

    // Handler pour le bouton de gestion des responsables
    @FXML
    private void handleGestionResponsable(ActionEvent event) {
        if (hasPermission("Responsables")) {
            switchScene(event, "/GestionResponsable.fxml");
        }
    }

    // Handler pour le bouton de gestion des techniciens
    @FXML
    private void handleGestionTechnicien(ActionEvent event) {
        if (hasPermission("Techniciens")) {
            switchScene(event, "/GestionTechnicien.fxml");
        }
    }

    // Handler pour le bouton de gestion des zones
    @FXML
    private void handleGestionZone(ActionEvent event) {
        if (hasPermission("Zones")) {
            switchScene(event, "/GestionZone.fxml");
        }
    }

    // Handler pour le bouton de gestion du profil
    @FXML
    private void handleProfileInterface(ActionEvent event) {
        if (hasPermission("Profil énergétique")) {
            switchScene(event, "/ProfileInterface.fxml");
        }
    }

    // Handler pour le bouton des sources
    @FXML
    private void handleSourceInterface(ActionEvent event) {
        if (hasPermission("Sources")) {
            switchScene(event, "/SourceInterface.fxml");
        }
    }

    // Handler pour revenir à la page d'accueil (Menu)
    @FXML
    private void handleAccueil(ActionEvent event) {
        switchScene(event, "/Menu.fxml");
    }

    // Méthode pour vérifier les permissions
    private boolean hasPermission(String page) {
        utilisateur user = Session.getCurrentUser();
        if (user == null) {
            showAlert("Accès refusé", "Vous devez être connecté pour accéder à cette page.");
            return false;
        }

        switch (user.getRole()) {
            case responsable:
                // Le responsable a accès à tout
                return true;

            case citoyen:
                // Le citoyen a accès à Lampadaires, Réclamations, Zones
                return page.equals("Lampadaires") || page.equals("Réclamations") || page.equals("Zones");

            case technicien:
                // Le technicien a accès à Capteurs, Données, Interventions, Caméras, Profil énergétique, Sources
                return page.equals("Capteurs") || page.equals("Données") || page.equals("Interventions")
                        || page.equals("Caméras") || page.equals("Profil énergétique") || page.equals("Sources");

            default:
                // Par défaut, aucun accès
                showAlert("Accès refusé", "Vous n'avez pas l'autorisation d'accéder à cette page.");
                return false;
        }
    }



    // Méthode de commutation de scène (load des FXML et mise à jour de la scène)
    private void switchScene(ActionEvent event, String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            // Récupère la scène actuelle et met à jour son contenu
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleExportCapteurs() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Enregistrer le fichier Excel");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichiers Excel", "*.xlsx"));
        fileChooser.setInitialFileName("capteurs_export.xlsx");

        File file = fileChooser.showSaveDialog(null);
        if (file != null) {
            try {
                Workbook workbook = new XSSFWorkbook();
                Sheet sheet = workbook.createSheet("Capteurs");

                // Créer l'en-tête
                Row headerRow = sheet.createRow(0);
                headerRow.createCell(0).setCellValue("ID");
                headerRow.createCell(1).setCellValue("Type");
                headerRow.createCell(2).setCellValue("État");
                headerRow.createCell(3).setCellValue("Date d'installation");
                headerRow.createCell(4).setCellValue("Lampadaire ID");

                // Remplir les données
                int rowNum = 1;
                for (Capteur capteur : serviceCapteur.getAll()) {
                    Row row = sheet.createRow(rowNum++);
                    row.createCell(0).setCellValue(capteur.getId());
                    row.createCell(1).setCellValue(capteur.getType().toString());
                    row.createCell(2).setCellValue(capteur.getEtat().toString());
                    row.createCell(3).setCellValue(capteur.getDateinstallation().toString());
                    row.createCell(4).setCellValue(capteur.getLampadaireId());
                }

                // Ajuster la largeur des colonnes
                for (int i = 0; i < 5; i++) {
                    sheet.autoSizeColumn(i);
                }

                // Écrire le fichier
                FileOutputStream fileOut = new FileOutputStream(file);
                workbook.write(fileOut);
                fileOut.close();
                workbook.close();

                showSuccessAlert("Exportation réussie ! Les capteurs ont été exportés dans :\n" + file.getAbsolutePath());
            } catch (IOException e) {
                showAlert("Erreur", "Une erreur est survenue lors de l'exportation des capteurs.");
                e.printStackTrace();
            }
        }
    }

    private boolean sortAscending = false; // Par défaut, tri descendant

    @FXML
    private void handleSortByDate() {
        // Récupérer la liste des capteurs
        List<Capteur> capteurs = serviceCapteur.getAll();

        // Trier les capteurs par date d'installation
        capteurs.sort((c1, c2) -> {
            if (c1.getDateinstallation() == null || c2.getDateinstallation() == null) {
                return 0; // Ne pas trier si une date est null
            }
            if (sortAscending) {
                return c1.getDateinstallation().compareTo(c2.getDateinstallation()); // Tri ascendant
            } else {
                return c2.getDateinstallation().compareTo(c1.getDateinstallation()); // Tri descendant
            }
        });

        // Inverser l'état du tri pour la prochaine fois
        sortAscending = !sortAscending;

        // Mettre à jour l'affichage
        capteurCardContainer.getChildren().clear();
        for (Capteur capteur : capteurs) {
            VBox card = createCapteurCard(capteur);
            capteurCardContainer.getChildren().add(card);
        }

        showSuccessAlert("Capteurs triés par date d'installation (" + (sortAscending ? "ascendant" : "descendant") + ").");
    }




    // Mettre à jour la ComboBox des valeurs en fonction du critère sélectionné
    private void updateSearchValueComboBox(String criteria) {
        searchValueComboBox.getItems().clear();
        if ("Type".equals(criteria)) {
            for (TypeCapteur type : TypeCapteur.values()) {
                searchValueComboBox.getItems().add(type.toString());
            }
        } else if ("État".equals(criteria)) {
            for (EtatCapteur etat : EtatCapteur.values()) {
                searchValueComboBox.getItems().add(etat.toString());
            }
        }
    }

    // Méthode pour gérer la recherche
    @FXML
    private void handleSearch() {
        String criteria = searchCriteriaComboBox.getValue();
        String value = searchValueComboBox.getValue();

        if (criteria == null || value == null) {
            showAlert("Erreur", "Veuillez sélectionner un critère et une valeur.");
            return;
        }

        // Filtrer les capteurs en fonction du critère et de la valeur
        List<Capteur> filteredCapteurs = serviceCapteur.getAll().stream()
                .filter(capteur -> {
                    if ("Type".equals(criteria)) {
                        return capteur.getType().toString().equals(value);
                    } else if ("État".equals(criteria)) {
                        return capteur.getEtat().toString().equals(value);
                    }
                    return false;
                })
                .collect(Collectors.toList());

        // Mettre à jour l'affichage avec les capteurs filtrés
        capteurCardContainer.getChildren().clear();
        for (Capteur capteur : filteredCapteurs) {
            VBox card = createCapteurCard(capteur);
            capteurCardContainer.getChildren().add(card);
        }

        showSuccessAlert("Résultats de la recherche : " + filteredCapteurs.size() + " capteur(s) trouvé(s).");
    }

}