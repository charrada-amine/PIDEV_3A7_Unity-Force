package tn.esprit.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import org.kordamp.ikonli.javafx.FontIcon;
import tn.esprit.models.*;

import javafx.geometry.Pos;

import tn.esprit.services.ServiceTechnicien;
import tn.esprit.services.ServiceUtilisateur;
import tn.esprit.utils.MyDatabase;

import javax.swing.text.JTextComponent;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class GestionTechnicienController {
    @FXML
    private Label welcomeLabel;

    @FXML
    private TextField  nameField, prenomField, emailField, passwordField;
    @FXML
    private ComboBox<String> specialiteComboBox;
    @FXML
    private Button logOutButton;  // Le bouton Log Out
    @FXML
    private FlowPane technicienFlowPane; // Correspond à 'fx:id="technicienFlowPane"' dans le FXML
    private final ServiceUtilisateur serviceUtilisateur = new ServiceUtilisateur();
    private final ServiceTechnicien serviceTechnicien = new ServiceTechnicien();
    @FXML
    private TextField visiblePasswordField;
    @FXML
    private ToggleButton togglePasswordButton;



    @FXML
    private void initialize() {
        if (technicienFlowPane == null) {
            System.err.println("Erreur : 'technicienFlowPane' est nul. Vérifiez le fichier FXML.");
        } else {
            System.out.println("FlowPane 'technicienFlowPane' initialisé correctement.");
            loadUsers(); // Charger les techniciens
        }

        if (specialiteComboBox != null) {
            // Initialiser le ComboBox avec les noms des valeurs de l'énumération Specialite
            specialiteComboBox.setItems(FXCollections.observableArrayList(
                    Arrays.stream(Specialite.values())
                            .map(Specialite::name) // Récupérer le nom des spécialités sous forme de String
                            .collect(Collectors.toList())
            ));
        } else {
            System.err.println("Erreur : 'specialiteCombo' est nul.");
        }
        // Récupérer l'utilisateur de la session
        utilisateur user = Session.getCurrentUser();

        // Vérifier si un utilisateur est connecté
        if (user != null) {
            // Afficher le nom et le prénom de l'utilisateur
            welcomeLabel.setText("Bienvenue, " + user.getNom() + " " + user.getPrenom());
        } else {
            // Si aucun utilisateur n'est connecté, afficher un message générique
            welcomeLabel.setText("Bienvenue, invité");
        }
        // Ajouter un écouteur pour basculer la visibilité du mot de passe
        togglePasswordButton.selectedProperty().addListener((obs, wasSelected, isNowSelected) -> {
            if (isNowSelected) {
                // Afficher le mot de passe en clair
                visiblePasswordField.setText(passwordField.getText());
                visiblePasswordField.setVisible(true);
                passwordField.setVisible(false);
                ((FontIcon) togglePasswordButton.getGraphic()).setIconLiteral("fas-eye");
            } else {
                // Masquer le mot de passe
                passwordField.setText(visiblePasswordField.getText());
                passwordField.setVisible(true);
                visiblePasswordField.setVisible(false);
                ((FontIcon) togglePasswordButton.getGraphic()).setIconLiteral("fas-eye-slash");

            }
        });
    }


    private VBox createTechnicienCard(technicien technicien) {
        VBox card = new VBox(10);
        card.setStyle("-fx-border-color: #ddd; -fx-border-width: 1; -fx-padding: 10; -fx-background-color: #f9f9f9; -fx-alignment: center;");

        // Champ ID (non modifiable)
       /* Label idLabel = new Label("ID:");
        idLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: black;");
        TextField idField = new TextField(String.valueOf(technicien.getId_utilisateur()));
        idField.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cccccc; -fx-padding: 5;");
        idField.setEditable(false); // Non modifiable
        HBox idBox = new HBox(10, idLabel, idField); // Aligner le label et le champ ID
        idBox.setAlignment(Pos.CENTER_LEFT); // Aligner à gauche*/

        // Champ Nom (non modifiable)
        Label nameLabel = new Label("Nom:");
        nameLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: black;");
        TextField nameField = new TextField(technicien.getNom());
        nameField.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cccccc; -fx-padding: 5;");
        nameField.setEditable(false); // Non modifiable
        HBox nameBox = new HBox(10, nameLabel, nameField); // Aligner le label et le champ Nom
        nameBox.setAlignment(Pos.CENTER_LEFT);

        // Champ Prénom (non modifiable)
        Label prenomLabel = new Label("Prénom:");
        prenomLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: black;");
        TextField prenomField = new TextField(technicien.getPrenom());
        prenomField.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cccccc; -fx-padding: 5;");
        prenomField.setEditable(false); // Non modifiable
        HBox prenomBox = new HBox(10, prenomLabel, prenomField); // Aligner le label et le champ Prénom
        prenomBox.setAlignment(Pos.CENTER_LEFT);

        // Champ Email (non modifiable)
        Label emailLabel = new Label("Email:");
        emailLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: black;");
        TextField emailField = new TextField(technicien.getEmail());
        emailField.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cccccc; -fx-padding: 5;");
        emailField.setEditable(false); // Non modifiable
        HBox emailBox = new HBox(10, emailLabel, emailField); // Aligner le label et le champ Email
        emailBox.setAlignment(Pos.CENTER_LEFT);

        // Champ Mot de passe (non modifiable)
       /* Label passwordLabel = new Label("Mot de passe:");
        passwordLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: black;");
        TextField passwordField = new TextField(technicien.getMotdepasse());
        passwordField.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cccccc; -fx-padding: 5;");
        passwordField.setEditable(false); // Non modifiable
        HBox passwordBox = new HBox(10, passwordLabel, passwordField); // Aligner le label et le champ Mot de passe
        passwordBox.setAlignment(Pos.CENTER_LEFT);*/



        // Champ Date d'inscription (non modifiable)
        Label dateInscriptionLabel = new Label("Date d'inscription:");
        dateInscriptionLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: black;");
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        TextField dateField = new TextField(sdf.format(technicien.getDateinscription()));
        dateField.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cccccc; -fx-padding: 5;");
        dateField.setEditable(false); // Non modifiable
        HBox dateInscriptionBox = new HBox(10, dateInscriptionLabel, dateField); // Aligner le label et le champ Date d'inscription
        dateInscriptionBox.setAlignment(Pos.CENTER_LEFT);

        // Champ Spécialité (non modifiable)
        Label specialiteLabel = new Label("Spécialité:");
        specialiteLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: black;");
        TextField specialiteField = new TextField(technicien.getSpecialite().name());
        specialiteField.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cccccc; -fx-padding: 5;");
        specialiteField.setEditable(false); // Non modifiable
        HBox specialiteBox = new HBox(10, specialiteLabel, specialiteField); // Aligner le label et le champ Spécialité
        specialiteBox.setAlignment(Pos.CENTER_LEFT);

        // Ajouter les HBoxes à la carte
        card.getChildren().addAll(
                nameBox,
                prenomBox,
                emailBox,
                dateInscriptionBox,
                specialiteBox
        );

        return card;
    }


    @FXML
    private void loadUsers() {
        if (technicienFlowPane == null) {
            System.err.println("Erreur : 'technicienFlowPane' est nul lors de l'appel de 'loadTechniciens'.");
            return;
        }

        ServiceTechnicien serviceTechnicien = new ServiceTechnicien();
        List<technicien> techniciens;

        try {
            techniciens = serviceTechnicien.getAllTechniciens();
        } catch (Exception e) {
            System.err.println("Erreur lors de la récupération des techniciens : " + e.getMessage());
            e.printStackTrace();
            return;
        }

        if (techniciens == null || techniciens.isEmpty()) {
            System.out.println("Aucun technicien trouvé.");
            technicienFlowPane.getChildren().clear();
            return;
        }

        ObservableList<VBox> technicienCards = FXCollections.observableArrayList();

        for (technicien t : techniciens) {
            VBox technicienCard = createTechnicienCard(t);
            technicienCards.add(technicienCard);
        }

        technicienFlowPane.getChildren().clear();
        technicienFlowPane.getChildren().addAll(technicienCards);
    }
    public boolean emailExists(String email) {
        String query = "SELECT COUNT(*) FROM utilisateur WHERE email = ?";
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            Connection connection = MyDatabase.getInstance().getCnx(); // Connexion partagée
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, email);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la vérification de l'email : " + e.getMessage());
        } finally {
            try {
                if (resultSet != null) resultSet.close();
                if (preparedStatement != null) preparedStatement.close();
            } catch (SQLException e) {
                System.out.println("Erreur lors de la fermeture des ressources : " + e.getMessage());
            }
        }
        return false;
    }
    @FXML
    private void handleLogOut(ActionEvent event) {
        // Met fin à la session
        Session.logOut();

        // Afficher un message de déconnexion
        showAlert("Déconnexion", "Vous avez été déconnecté avec succès.");

        // Fermer la fenêtre actuelle (l'écran principal)
        Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        currentStage.close();

        // Ouvrir la fenêtre de connexion
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Login.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Connexion");
            stage.setScene(new Scene(root));
            stage.show();
            stage.setMaximized(true);

        } catch (IOException e) {
            showAlert("Erreur", "Une erreur est survenue lors de la déconnexion.");
            e.printStackTrace();
        }
    }


    private technicien getSelectedTechnicien() {
        // TODO: Implémentez la logique pour récupérer l'élément sélectionné.
        // Par exemple, vous pouvez avoir une liste ou une table de citoyens.
        return null; // Remplacez `null` par la sélection réelle.
    }
    @FXML
    private void handleUpdateTechnicien() {
        try {
            // Récupérer et valider l'email
            String email = emailField.getText().trim();
            if (email.isEmpty() || !email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
                showAlert("Erreur", "L'email doit être valide.");
                return;
            }

            // Récupérer et valider le mot de passe
            String password = togglePasswordButton.isSelected() ? visiblePasswordField.getText().trim() : passwordField.getText().trim();
            if (password.isEmpty()) {
                showAlert("Erreur", "Le mot de passe ne peut pas être vide.");
                return;
            }

            // Vérifier si le technicien existe avec cet email et mot de passe
            utilisateur technicien = serviceUtilisateur.getByEmailAndPassword(email, password);
            if (technicien == null) {
                showAlert("Erreur", "Email ou mot de passe incorrect.");
                return;
            }

            // Récupérer et valider le nouveau nom
            String newName = nameField.getText().trim();
            if (newName.isEmpty() || !newName.matches("[A-Za-zÀ-ÖØ-öø-ÿ\\s-]+")) {
                showAlert("Erreur", "Le nom doit contenir uniquement des lettres et ne peut pas être vide.");
                return;
            }

            // Récupérer et valider le nouveau prénom
            String newPrenom = prenomField.getText().trim();
            if (newPrenom.isEmpty() || !newPrenom.matches("[A-Za-zÀ-ÖØ-öø-ÿ\\s-]+")) {
                showAlert("Erreur", "Le prénom doit contenir uniquement des lettres et ne peut pas être vide.");
                return;
            }

            String newSpecialite = specialiteComboBox.getValue();
            if (newSpecialite == null || newSpecialite.trim().isEmpty()) {
                showAlert("Erreur", "La spécialité doit être sélectionnée.");
                return;
            }

            // Mettre à jour les champs autorisés (nom, prénom, spécialité)
            serviceUtilisateur.updateFieldTechnicien(technicien.getId_utilisateur(), "nom", newName);
            serviceUtilisateur.updateFieldTechnicien(technicien.getId_utilisateur(), "prenom", newPrenom);
            serviceUtilisateur.updateFieldTechnicien(technicien.getId_utilisateur(), "specialite", newSpecialite);

            // Recharger la liste des techniciens
            loadUsers();

            // Réinitialiser les champs
            clearFields();

            // Afficher confirmation
            showAlert("Succès", "Technicien modifié avec succès.");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Une erreur est survenue lors de la modification.");
        }
    }


    private void clearFields() {
        nameField.clear();
        prenomField.clear();
        emailField.clear();
        passwordField.clear();
        specialiteComboBox.setValue(null); // Réinitialiser le ComboBox
        togglePasswordButton.setSelected(false); // Désactiver le basculement

    }
    @FXML
    public void handleClear() {
        clearFields();
    }
    @FXML
    public void filterByResponsable() {
        try {
            // Charger la vue GestionResponsable.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/GestionResponsable.fxml"));
            Parent root = loader.load();

            // Créer une nouvelle scène avec la vue chargée
            Stage stage = new Stage();
            Scene scene = new Scene(root);

            // Configurer la scène et la fenêtre
            stage.setScene(scene);
            stage.setTitle("Gestion des Responsables");

            // Maximiser automatiquement la fenêtre
            stage.setMaximized(true); // Maximiser la fenêtre

            stage.show();  // Afficher la scène
        } catch (IOException e) {
            e.printStackTrace();  // Afficher l'erreur en cas de problème
        }
    }



    // Méthode de filtrage pour les Citoyens
    @FXML
    public void filterByCitoyen() {
        {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/GestionCitoyen.fxml"));
                Parent root = loader.load();

                Stage stage = new Stage();
                Scene scene = new Scene(root);

                stage.setScene(scene);
                stage.setTitle("Gestion des Citoyens");

                // Maximiser automatiquement
                stage.setMaximized(true);

                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Méthode de filtrage pour les Techniciens
    @FXML
    public void filterByUtilisateur() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/GestionUtilisateur.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            Scene scene = new Scene(root);

            stage.setScene(scene);
            stage.setTitle("Gestion des Techniciens");

            // Maximiser automatiquement
            stage.setMaximized(true);

            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    public void filterByAccueil() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Accueil.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            Scene scene = new Scene(root);

            stage.setScene(scene);
            stage.setTitle("Gestion des utilisateurs");

            // Maximiser automatiquement
            stage.setMaximized(true);

            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleDeleteTechnicien(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Delete.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Suppression de Technicien");
            stage.setScene(new Scene(root));
            stage.setMaximized(true);

            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de charger la fenêtre de suppression.");
        }
    }


    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
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
}
