package tn.esprit.controllers;

import com.fazecast.jSerialComm.SerialPort;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.geometry.Insets;
import javafx.stage.FileChooser;
import org.kordamp.ikonli.javafx.FontIcon;
import tn.esprit.models.*;
import tn.esprit.services.*;

import java.io.File;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;


import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import tn.esprit.utils.EmailSend;

import java.io.IOException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.FileOutputStream;
import java.io.IOException;
public class GestionDonneesController implements Initializable {

    @FXML
    private ComboBox<String> typeCapteurComboBox;

    @FXML
    private DatePicker dateCollectePicker;

    @FXML
    private TextField heureCollecteField;

    @FXML
    private ComboBox<Integer> capteurIdComboBox; // Nouveau ComboBox pour les ID des capteurs

    @FXML
    private TextField valeurField;

    @FXML
    private FlowPane donneeCardContainer;

    @FXML
    private Label valeurIndicationLabel;

    @FXML
    private ComboBox<String> searchTypeComboBox; // Pour choisir le type de capteur
    @FXML
    private ComboBox<String> searchCapteurIdComboBox; // Changé de Integer à String
    private final ServiceMouvement serviceMouvement = new ServiceMouvement();
    private final ServiceTemperature serviceTemperature = new ServiceTemperature();
    private final ServiceConsommation serviceConsommation = new ServiceConsommation();
    private final ServiceLuminosite serviceLuminosite = new ServiceLuminosite();
    private final ServiceCapteur serviceCapteur = new ServiceCapteur(); // Service pour les capteurs

    private final ObservableList<Donnee> donneeList = FXCollections.observableArrayList();

    private Donnee selectedDonnee; // Variable pour stocker la donnée sélectionnée

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Ajouter les types de capteurs correspondant à l'énumération
        typeCapteurComboBox.getItems().addAll("MOUVEMENT", "TEMPERATURE", "LUMINOSITE", "CONSOMMATION_ENERGIE");
        updateValeurIndication(); // Mise à jour initiale
        handleRefresh();

        // Ajouter un écouteur pour mettre à jour les ID des capteurs lorsque le type change
        typeCapteurComboBox.setOnAction(event -> {
            updateValeurIndication();
            updateCapteurIds(); // Mettre à jour les ID des capteurs
        });
        // Initialiser les nouveaux ComboBox pour la recherche
        searchTypeComboBox.getItems().addAll("MOUVEMENT", "TEMPERATURE", "LUMINOSITE", "CONSOMMATION_ENERGIE");
        searchTypeComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            updateSearchCapteurIds(newVal); // Mettre à jour les ID des capteurs en fonction du type choisi
        });

        // Ajouter l'option "Tous" dans le ComboBox des ID de capteur
        //searchCapteurIdComboBox.getItems().add(-1); // -1 représente "Tous"
    }

    private void updateCapteurIds() {
        String selectedType = typeCapteurComboBox.getValue();
        if (selectedType != null) {
            // Récupérer tous les capteurs de la base de données
            List<Capteur> capteurs = serviceCapteur.getAll();

            // Filtrer les capteurs par type
            List<Capteur> filteredCapteurs = capteurs.stream()
                    .filter(capteur -> capteur.getType().name().equals(selectedType))
                    .collect(Collectors.toList());

            // Effacer les anciens ID du ComboBox
            capteurIdComboBox.getItems().clear();

            // Ajouter les ID des capteurs filtrés au ComboBox avec leur préfixe
            for (Capteur capteur : filteredCapteurs) {
                String prefix = getPrefixForType(capteur.getType()); // Obtenir le préfixe en fonction du type
                String reference = prefix + capteur.getId(); // Construire la référence
                capteurIdComboBox.getItems().add(capteur.getId()); // Ajouter l'ID au ComboBox
                capteurIdComboBox.setButtonCell(new ListCell<Integer>() {
                    @Override
                    protected void updateItem(Integer item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item == null || empty) {
                            setText("Sélectionnez une référence");
                        } else {
                            // Afficher la référence (préfixe + ID) dans le ComboBox
                            setText(prefix + item);
                        }
                    }
                });
            }

            // Si aucun capteur n'est trouvé, afficher un message dans le ComboBox
            if (filteredCapteurs.isEmpty()) {
                capteurIdComboBox.setPromptText("Aucun capteur trouvé pour ce type");
            } else {
                capteurIdComboBox.setPromptText("Sélectionnez une référence");
            }
        }
    }

    // Méthode pour obtenir le préfixe en fonction du type de capteur
    private String getPrefixForType(Capteur.TypeCapteur type) {
        switch (type) {
            case MOUVEMENT:
                return "mv";
            case LUMINOSITE:
                return "lum";
            case TEMPERATURE:
                return "temp";
            case CONSOMMATION_ENERGIE:
                return "coe";
            default:
                return "ref";
        }
    }

    @FXML
    public void handleAddDonnee() {
        String type = typeCapteurComboBox.getValue();
        LocalDate date = dateCollectePicker.getValue();
        LocalTime heure = LocalTime.parse(heureCollecteField.getText());
        Integer capteurId = capteurIdComboBox.getValue(); // Récupérer l'ID sélectionné
        String valeurStr = valeurField.getText();

        if (type != null && date != null && heure != null && !valeurStr.isEmpty() && capteurId != null) {
            switch (type) {
                case "MOUVEMENT":
                    serviceMouvement.add(new DonneeMouvement(0, date, heure, capteurId, Boolean.parseBoolean(valeurStr)));
                    break;
                case "TEMPERATURE":
                    serviceTemperature.add(new DonneeTemperature(0, date, heure, capteurId, Float.parseFloat(valeurStr)));
                    checkTemperatureAndSendEmail(valeurStr);
                    break;
                case "CONSOMMATION_ENERGIE":
                    serviceConsommation.add(new DonneeConsommation(0, date, heure, capteurId, Float.parseFloat(valeurStr)));
                    break;
                case "LUMINOSITE":
                    serviceLuminosite.add(new DonneeLuminosite(0, date, heure, capteurId, Integer.parseInt(valeurStr)));
                    break;
            }
            handleRefresh();
            clearFields();
        }
    }

    @FXML
    public void handleUpdateDonnee() {
        if (selectedDonnee != null) {
            LocalDate date = dateCollectePicker.getValue();
            LocalTime heure = LocalTime.parse(heureCollecteField.getText());
            int capteurId = capteurIdComboBox.getValue(); // Récupérer l'ID sélectionné dans le ComboBox
            String valeurStr = valeurField.getText();

            if (selectedDonnee instanceof DonneeMouvement) {
                serviceMouvement.update(new DonneeMouvement(selectedDonnee.getId(), date, heure, capteurId, Boolean.parseBoolean(valeurStr)));
            } else if (selectedDonnee instanceof DonneeTemperature) {
                serviceTemperature.update(new DonneeTemperature(selectedDonnee.getId(), date, heure, capteurId, Float.parseFloat(valeurStr)));
            } else if (selectedDonnee instanceof DonneeConsommation) {
                serviceConsommation.update(new DonneeConsommation(selectedDonnee.getId(), date, heure, capteurId, Float.parseFloat(valeurStr)));
            } else if (selectedDonnee instanceof DonneeLuminosite) {
                serviceLuminosite.update(new DonneeLuminosite(selectedDonnee.getId(), date, heure, capteurId, Integer.parseInt(valeurStr)));
            }
            handleRefresh();
        }
    }

    @FXML
    public void handleDeleteDonnee() {
        if (selectedDonnee != null) {
            if (selectedDonnee instanceof DonneeMouvement) serviceMouvement.delete(selectedDonnee.getId());
            else if (selectedDonnee instanceof DonneeTemperature) serviceTemperature.delete(selectedDonnee.getId());
            else if (selectedDonnee instanceof DonneeConsommation) serviceConsommation.delete(selectedDonnee.getId());
            else if (selectedDonnee instanceof DonneeLuminosite) serviceLuminosite.delete(selectedDonnee.getId());

            handleRefresh();
        }
    }

    @FXML
    public void handleRefresh() {
        donneeCardContainer.getChildren().clear(); // Effacer les anciennes cartes
        donneeList.clear();
        donneeList.addAll(serviceMouvement.getAll());
        donneeList.addAll(serviceTemperature.getAll());
        donneeList.addAll(serviceConsommation.getAll());
        donneeList.addAll(serviceLuminosite.getAll());

        for (Donnee donnee : donneeList) {
            VBox card = createDonneeCard(donnee);
            donneeCardContainer.getChildren().add(card);
        }
    }

    private VBox createDonneeCard(Donnee donnee) {
        VBox card = new VBox();
        card.setSpacing(10);
        card.setPadding(new Insets(15));
        card.setPrefWidth(220);
        card.setPrefHeight(120);

        // Appliquer une classe CSS pour un style uniforme
        card.getStyleClass().add("donnee-card");

        // Créer un GridPane pour organiser les labels et les icônes
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(5);

        // Ajouter des icônes à gauche des labels
        FontIcon dateIcon = new FontIcon("fas-calendar-alt");
        FontIcon timeIcon = new FontIcon("fas-clock");
        FontIcon sensorIcon = new FontIcon("fas-microchip");
        FontIcon valueIcon = new FontIcon("fas-chart-line");
        FontIcon typeIcon = new FontIcon("fas-tag");

        // Labels avec icônes
        Label dateLabel = createLabelWithIcon("Date: " + donnee.getDateCollecte(), dateIcon);
        Label heureLabel = createLabelWithIcon("Heure: " + donnee.getHeureCollecte(), timeIcon);
        Label capteurIdLabel = createLabelWithIcon("Capteur ID: " + donnee.getCapteurId(), sensorIcon);
        Label valeurLabel = createLabelWithIcon("Valeur: " + getValeurString(donnee), valueIcon);
        Label typeLabel = createLabelWithIcon("Type: " + getTypeString(donnee), typeIcon);

        // Ajouter les labels au GridPane
        grid.add(dateLabel, 0, 0);
        grid.add(heureLabel, 0, 1);
        grid.add(capteurIdLabel, 0, 2);
        grid.add(valeurLabel, 0, 3);
        grid.add(typeLabel, 0, 4);

        // Ajouter un effet visuel au survol
        card.setOnMouseEntered(event -> card.setStyle("-fx-background-color: #f0f0f0;"));
        card.setOnMouseExited(event -> card.setStyle("-fx-background-color: white;"));

        // Gestionnaire d'événements pour sélectionner une donnée
        card.setOnMouseClicked(event -> selectDonnee(donnee));

        // Ajouter le GridPane à la carte
        card.getChildren().add(grid);
        card.setUserData(donnee);
        return card;
    }

    // Méthode pour créer un label avec une icône
    private Label createLabelWithIcon(String text, FontIcon icon) {
        Label label = new Label(text);
        label.setGraphic(icon);
        label.getStyleClass().add("donnee-label");
        return label;
    }


    private String getValeurString(Donnee donnee) {
        if (donnee instanceof DonneeMouvement) return String.valueOf(((DonneeMouvement) donnee).getValeur());
        if (donnee instanceof DonneeTemperature) return String.valueOf(((DonneeTemperature) donnee).getValeur());
        if (donnee instanceof DonneeConsommation) return String.valueOf(((DonneeConsommation) donnee).getValeur());
        if (donnee instanceof DonneeLuminosite) return String.valueOf(((DonneeLuminosite) donnee).getValeur());
        return "N/A";
    }

    private String getTypeString(Donnee donnee) {
        if (donnee instanceof DonneeMouvement) return "MOUVEMENT";
        if (donnee instanceof DonneeTemperature) return "TEMPERATURE";
        if (donnee instanceof DonneeConsommation) return "CONSOMMATION_ENERGIE";
        if (donnee instanceof DonneeLuminosite) return "LUMINOSITE";
        return "Inconnu";
    }

    private void selectDonnee(Donnee donnee) {
        this.selectedDonnee = donnee; // Mettre à jour la donnée sélectionnée
        dateCollectePicker.setValue(donnee.getDateCollecte());
        heureCollecteField.setText(donnee.getHeureCollecte().toString());
        capteurIdComboBox.setValue(donnee.getCapteurId()); // Mettre à jour le ComboBox des ID
        valeurField.setText(getValeurString(donnee));
    }

    private void clearFields() {
        dateCollectePicker.setValue(null);
        heureCollecteField.clear();
        capteurIdComboBox.getSelectionModel().clearSelection();
        valeurField.clear();
    }

    @FXML
    private void updateValeurIndication() {
        String type = typeCapteurComboBox.getValue();
        if (type == null) {
            valeurIndicationLabel.setText("Sélectionnez un type de capteur");
            return;
        }

        switch (type) {
            case "MOUVEMENT":
                valeurIndicationLabel.setText("Valeur booléenne (true/false)");
                break;
            case "TEMPERATURE":
                valeurIndicationLabel.setText("Valeur en °C (Degrés Celsius)");
                break;
            case "CONSOMMATION_ENERGIE":
                valeurIndicationLabel.setText("Valeur en W (Watts)");
                break;
            case "LUMINOSITE":
                valeurIndicationLabel.setText("Valeur en Lux");
                break;
            default:
                valeurIndicationLabel.setText("Valeur invalide");
        }
    }
    public void handleGoToGestionCapteur(javafx.event.ActionEvent actionEvent) {
        try {
            // Charger le fichier FXML de la page GestionCapteur
            Parent gestionCapteurParent = FXMLLoader.load(getClass().getResource("/GestionCapteur.fxml"));
            Scene gestionCapteurScene = new Scene(gestionCapteurParent);

            // Obtenir la scène actuelle et la fenêtre
            Stage window = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();

            // Changer la scène
            window.setScene(gestionCapteurScene);
            window.show();
        } catch (IOException e) {
            e.printStackTrace(); // Afficher l'erreur en cas de problème
            System.err.println("Erreur lors du chargement de GestionCapteur.fxml : " + e.getMessage());
        }
    }
    private void showSuccessAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Succès");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


    public void handleBack(ActionEvent actionEvent) {
    }
    @FXML
    private void handleGestionCapteur(ActionEvent event) {
        switchScene(event, "/GestionCapteur.fxml");
    }

    @FXML
    private void handleGestionCitoyen(ActionEvent event) {
        switchScene(event, "/GestionCitoyen.fxml");
    }

    @FXML
    private void handleGestionDonnee(ActionEvent event) {
        switchScene(event, "/GestionDonnee.fxml");
    }

    @FXML
    private void handleGestionIntervention(ActionEvent event) {
        switchScene(event, "/GestionIntervention.fxml");
    }

    @FXML
    private void handleGestionLampadaire(ActionEvent event) {
        switchScene(event, "/GestionLampadaire.fxml");
    }

    @FXML
    private void handleGestionReclamation(ActionEvent event) {
        switchScene(event, "/GestionReclamation.fxml");
    }

    @FXML
    private void handleGestionResponsable(ActionEvent event) {
        switchScene(event, "/GestionResponsable.fxml");
    }

    @FXML
    private void handleGestionTechnicien(ActionEvent event) {
        switchScene(event, "/GestionTechnicien.fxml");
    }

    @FXML
    private void handleGestionUtilisateur(ActionEvent event) {
        switchScene(event, "/GestionUtilisateur.fxml");
    }

    @FXML
    private void handleGestionZone(ActionEvent event) {
        switchScene(event, "/GestionZone.fxml");
    }

    @FXML
    private void handleProfileInterface(ActionEvent event) {
        switchScene(event, "/ProfileInterface.fxml");
    }

    @FXML
    private void handleSourceInterface(ActionEvent event) {
        switchScene(event, "/SourceInterface.fxml");
    }

    @FXML
    private void handleBack() {
        // Logique pour revenir à la page précédente
        System.out.println("Retour à la page précédente");
    }

    // Nouveau handler pour le bouton Accueil
    @FXML
    private void handleAccueil(ActionEvent event) {
        switchScene(event, "/Menu.fxml");
    }

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

    public void checkTemperatureAndSendEmail(String valeurStr) {
        try {
            // Conversion de la chaîne en nombre flottant
            float temperatureValeur = Float.parseFloat(valeurStr);

            // Vérification si la température est supérieure à 50
            if (temperatureValeur > 50) {
                String to = "mohamedkarim.ouertatani@esprit.tn";
                String subject = "Alerte : Température élevée détectée !";
                String body = "Attention ! Une température élevée de " + temperatureValeur +
                        "°C a été détectée.\nMerci de prendre les mesures nécessaires.";

                // Envoi de l'email
                EmailSend.sendEmail(to, subject, body);


            }
        } catch (NumberFormatException e) {
            System.out.println("Erreur : La valeur de température n'est pas un nombre valide.");
        }
    }
    public static void showAlert(String title, String message) {
        System.out.println("⚠️ " + title + " : " + message);
    }

    @FXML
    private void handleExportDonnees() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Enregistrer le fichier Excel");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichiers Excel", "*.xlsx"));
        fileChooser.setInitialFileName("donnees_export.xlsx");

        File file = fileChooser.showSaveDialog(null);
        if (file != null) {
            try {
                Workbook workbook = new XSSFWorkbook();
                Sheet sheet = workbook.createSheet("Donnees");

                // Créer l'en-tête
                Row headerRow = sheet.createRow(0);
                headerRow.createCell(0).setCellValue("ID");
                headerRow.createCell(1).setCellValue("Type");
                headerRow.createCell(2).setCellValue("Date de collecte");
                headerRow.createCell(3).setCellValue("Heure de collecte");
                headerRow.createCell(4).setCellValue("Capteur ID");
                headerRow.createCell(5).setCellValue("Valeur");

                // Remplir les données
                int rowNum = 1;
                for (Donnee donnee : donneeList) {
                    Row row = sheet.createRow(rowNum++);
                    row.createCell(0).setCellValue(donnee.getId());
                    row.createCell(1).setCellValue(getTypeString(donnee));
                    row.createCell(2).setCellValue(donnee.getDateCollecte().toString());
                    row.createCell(3).setCellValue(donnee.getHeureCollecte().toString());
                    row.createCell(4).setCellValue(donnee.getCapteurId());
                    row.createCell(5).setCellValue(getValeurString(donnee));
                }

                // Ajuster la largeur des colonnes
                for (int i = 0; i < 6; i++) {
                    sheet.autoSizeColumn(i);
                }

                // Écrire le fichier
                FileOutputStream fileOut = new FileOutputStream(file);
                workbook.write(fileOut);
                fileOut.close();
                workbook.close();

                showSuccessAlert("Exportation réussie ! Les données ont été exportées dans :\n" + file.getAbsolutePath());
            } catch (IOException e) {
                showAlert("Erreur", "Une erreur est survenue lors de l'exportation des données.");
                e.printStackTrace();
            }
        }
    }
    private boolean sortAscending = false; // Par défaut, tri descendant

    @FXML
    private void handleSortByDate() {
        // Trier les données par date de collecte
        donneeList.sort((d1, d2) -> {
            if (d1.getDateCollecte() == null || d2.getDateCollecte() == null) {
                return 0; // Ne pas trier si une date est null
            }
            if (sortAscending) {
                return d1.getDateCollecte().compareTo(d2.getDateCollecte()); // Tri ascendant
            } else {
                return d2.getDateCollecte().compareTo(d1.getDateCollecte()); // Tri descendant
            }
        });

        // Inverser l'état du tri pour la prochaine fois
        sortAscending = !sortAscending;

        // Mettre à jour l'affichage
        donneeCardContainer.getChildren().clear();
        for (Donnee donnee : donneeList) {
            VBox card = createDonneeCard(donnee);
            donneeCardContainer.getChildren().add(card);
        }

        showSuccessAlert("Données triées par date de collecte (" + (sortAscending ? "ascendant" : "descendant") + ").");
    }

    private void updateSearchCapteurIds(String type) {
        searchCapteurIdComboBox.getItems().clear(); // Effacer les anciens ID
        searchCapteurIdComboBox.getItems().add("Tous"); // Ajouter l'option "Tous"

        if (type != null) {
            // Récupérer tous les capteurs de la base de données
            List<Capteur> capteurs = serviceCapteur.getAll();

            // Filtrer les capteurs par type
            List<Capteur> filteredCapteurs = capteurs.stream()
                    .filter(capteur -> capteur.getType().name().equals(type))
                    .collect(Collectors.toList());

            // Ajouter les ID des capteurs filtrés au ComboBox
            for (Capteur capteur : filteredCapteurs) {
                searchCapteurIdComboBox.getItems().add(String.valueOf(capteur.getId())); // Convertir l'ID en String
            }
        }
    }
    @FXML
    private void handleSearch() {
        String selectedType = searchTypeComboBox.getValue();
        String selectedCapteurIdStr = searchCapteurIdComboBox.getValue();

        if (selectedType == null || selectedCapteurIdStr == null) {
            showAlert("Erreur", "Veuillez sélectionner un type et un ID de capteur.");
            return;
        }

        // Filtrer les données en fonction du type et de l'ID sélectionnés
        List<Donnee> filteredDonnees = donneeList.stream()
                .filter(donnee -> {
                    String donneeType = getTypeString(donnee);
                    boolean matchesType = donneeType.equals(selectedType);

                    // Si "Tous" est sélectionné, ignorer le filtre par ID
                    boolean matchesCapteurId = selectedCapteurIdStr.equals("Tous") ||
                            String.valueOf(donnee.getCapteurId()).equals(selectedCapteurIdStr);

                    return matchesType && matchesCapteurId;
                })
                .collect(Collectors.toList());

        // Mettre à jour l'affichage avec les données filtrées
        donneeCardContainer.getChildren().clear();
        for (Donnee donnee : filteredDonnees) {
            VBox card = createDonneeCard(donnee);
            donneeCardContainer.getChildren().add(card);
        }

        showSuccessAlert("Résultats de la recherche : " + filteredDonnees.size() + " donnée(s) trouvée(s).");
    }
    @FXML
    private void handleRecupererDonneeArduino() {
        SerialPort[] ports = SerialPort.getCommPorts();
        if (ports.length == 0) {
            showAlert("Erreur", "Aucun port série trouvé !");
            return;
        }

        // Sélectionner le premier port disponible
        SerialPort port = ports[0];
        port.setBaudRate(9600); // Configurer le débit binaire (baud rate)
        port.openPort();

        // Lire les données ligne par ligne
        StringBuilder data = new StringBuilder();
        boolean dataReceived = false;

        // Attendre un court instant pour permettre à l'Arduino d'envoyer des données
        try {
            Thread.sleep(1000); // Attendre 1 seconde
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Lire les données disponibles
        while (port.bytesAvailable() > 0) {
            byte[] buffer = new byte[port.bytesAvailable()];
            port.readBytes(buffer, buffer.length);
            data.append(new String(buffer));
        }

        // Fermer le port série après la lecture
        port.closePort();

        // Nettoyer les données reçues
        String receivedData = data.toString().trim();

        // Vérifier si les données contiennent une ligne valide
        if (receivedData.contains(",")) {
            // Extraire la dernière ligne reçue
            String[] lines = receivedData.split("\n");
            String lastLine = lines[lines.length - 1].trim(); // Prendre la dernière ligne

            // Diviser la ligne en type et valeur
            String[] parts = lastLine.split(",");
            if (parts.length == 2) {
                String type = parts[0].trim().toUpperCase(); // Convertir en majuscules pour correspondre au ComboBox
                String valeur = parts[1].trim();

                // Vérifier le type et remplir les champs automatiquement
                if (type.equals("TEMPERATURE") || type.equals("LUMINOSITE") || type.equals("MOUVEMENT") || type.equals("CONSOMMATION_ENERGIE")) {
                    typeCapteurComboBox.setValue(type); // Sélectionner le type dans le ComboBox
                    valeurField.setText(valeur); // Remplir le champ de valeur
                    dateCollectePicker.setValue(LocalDate.now()); // Remplir la date actuelle
                    heureCollecteField.setText(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"))); // Formater l'heure

                    showSuccessAlert("Données récupérées avec succès : " + lastLine);
                } else {
                    showAlert("Erreur", "Type de capteur non reconnu : " + type);
                }
            } else {
                showAlert("Erreur", "Format de données incorrect : " + lastLine);
            }
        } else {
            showAlert("Erreur", "Aucune donnée valide reçue : " + receivedData);
        }
    }
    @FXML
    private void handleRecupererDonneeMouvement() {
        DataReceiver dataReceiver = new DataReceiver();
        String receivedData = dataReceiver.fetchDataFromServer();

        // Afficher la réponse brute pour débogage
        System.out.println("Données reçues : " + receivedData);

        // Vérifier si les données contiennent une ligne valide
        if (receivedData != null && receivedData.contains(",")) {
            // Extraire la dernière ligne reçue
            String[] lines = receivedData.split("\n");
            String lastLine = lines[lines.length - 1].trim(); // Prendre la dernière ligne

            // Diviser la ligne en type et valeur
            String[] parts = lastLine.split(",");
            if (parts.length == 4) { // Maintenant, il y a 4 parties : temperature, valeur, motion, valeur
                String type1 = parts[0].trim().toUpperCase(); // "TEMPERATURE"
                String valeur1 = parts[1].trim(); // Valeur de la température
                String type2 = parts[2].trim().toUpperCase(); // "MOTION"
                String valeur2 = parts[3].trim(); // Valeur du mouvement (true/false)

                // Afficher les données de mouvement
                if (type2.equals("MOTION")) {
                    typeCapteurComboBox.setValue("MOUVEMENT"); // Sélectionner le type "MOUVEMENT" dans le ComboBox
                    valeurField.setText(valeur2); // Remplir le champ de valeur avec l'état du mouvement
                    dateCollectePicker.setValue(LocalDate.now()); // Remplir la date actuelle
                    heureCollecteField.setText(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"))); // Formater l'heure

                    showSuccessAlert("Données de mouvement récupérées avec succès : " + valeur2);
                } else {
                    showAlert("Erreur", "Type de capteur non reconnu : " + type2);
                }
            } else {
                showAlert("Erreur", "Format de données incorrect : " + lastLine);
            }
        } else {
            showAlert("Erreur", "Aucune donnée valide reçue : " + receivedData);
        }
    }
    @FXML
    private void handleRecupererDonneeTemperature() {
        DataReceiver dataReceiver = new DataReceiver();
        String receivedData = dataReceiver.fetchDataFromServer();

        // Afficher la réponse brute pour débogage
        System.out.println("Données reçues : " + receivedData);

        // Vérifier si les données contiennent une ligne valide
        if (receivedData != null && receivedData.contains(",")) {
            // Extraire la dernière ligne reçue
            String[] lines = receivedData.split("\n");
            String lastLine = lines[lines.length - 1].trim(); // Prendre la dernière ligne

            // Diviser la ligne en type et valeur
            String[] parts = lastLine.split(",");
            if (parts.length == 4) { // Maintenant, il y a 4 parties : temperature, valeur, motion, valeur
                String type1 = parts[0].trim().toUpperCase(); // "TEMPERATURE"
                String valeur1 = parts[1].trim(); // Valeur de la température
                String type2 = parts[2].trim().toUpperCase(); // "MOTION"
                String valeur2 = parts[3].trim(); // Valeur du mouvement (true/false)

                // Afficher les données de température
                if (type1.equals("TEMPERATURE")) {
                    typeCapteurComboBox.setValue("TEMPERATURE"); // Sélectionner le type "TEMPERATURE" dans le ComboBox
                    valeurField.setText(valeur1); // Remplir le champ de valeur avec la température
                    dateCollectePicker.setValue(LocalDate.now()); // Remplir la date actuelle
                    heureCollecteField.setText(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"))); // Formater l'heure

                    showSuccessAlert("Données de température récupérées avec succès : " + valeur1);
                } else {
                    showAlert("Erreur", "Type de capteur non reconnu : " + type1);
                }
            } else {
                showAlert("Erreur", "Format de données incorrect : " + lastLine);
            }
        } else {
            showAlert("Erreur", "Aucune donnée valide reçue : " + receivedData);
        }
    }
}