package tn.esprit.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.kordamp.ikonli.javafx.FontIcon;
import tn.esprit.models.Role;
import tn.esprit.models.Session;
import tn.esprit.models.responsable;
import tn.esprit.models.utilisateur;
import tn.esprit.services.ServiceCitoyen;
import tn.esprit.services.ServiceResponsable;
import tn.esprit.services.ServiceUtilisateur;
import tn.esprit.utils.MyDatabase;
import tn.esprit.utils.PdfGeneratorUser;

import javax.swing.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import java.io.File;

public class GestionResponsableController {
    @FXML
    private Label welcomeLabel;
    @FXML
    private ComboBox<String> modulesComboBox;
    @FXML
    private Button logOutButton;  // Le bouton Log Out
    @FXML
    private FlowPane userFlowPane;
    @FXML
    private FlowPane responsableFlowPane;
    private final ServiceResponsable serviceResponsable = new ServiceResponsable();
    private final ServiceUtilisateur serviceUtilisateur = new ServiceUtilisateur();
    @FXML
    private TextField idField, nameField, prenomField, emailField, passwordField, modulesField ;
    @FXML
    private TextField visiblePasswordField;
    @FXML
    private ComboBox<String> roleFilterComboBox;

    private String roleSelectionne = "Tous"; // Valeur par défaut
    @FXML
    private ToggleButton togglePasswordButton;
    @FXML
    private void initialize() {
        if (userFlowPane == null) {
            System.err.println("Erreur : 'userflowpane' est nul. Vérifiez le fichier FXML.");
        } else {
            System.out.println("FlowPane 'userflowpane' initialisé correctement.");
            loadUsers();
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
    private VBox createUserCard(utilisateur user) {
        VBox card = new VBox(10);
        card.setStyle("-fx-border-color: #ddd; -fx-border-width: 1; -fx-padding: 10; -fx-background-color: #f9f9f9; -fx-alignment: center;");

        // Champ ID (non modifiable)
        /*Label idLabel = new Label("ID:");
        idLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: black;");
        TextField idField = new TextField(String.valueOf(user.getId_utilisateur()));
        idField.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cccccc; -fx-padding: 5;");
        idField.setEditable(false); // Non modifiable
        HBox idBox = new HBox(10, idLabel, idField);
        idBox.setAlignment(Pos.CENTER_LEFT);*/

        // Champ Nom
        Label nameLabel = new Label("Nom:");
        nameLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: black;");
        TextField nameField = new TextField(user.getNom());
        nameField.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cccccc; -fx-padding: 5;");
        nameField.setEditable(false);
        HBox nameBox = new HBox(10, nameLabel, nameField);
        nameBox.setAlignment(Pos.CENTER_LEFT);

        // Champ Prénom
        Label prenomLabel = new Label("Prénom:");
        prenomLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: black;");
        TextField prenomField = new TextField(user.getPrenom());
        prenomField.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cccccc; -fx-padding: 5;");
        prenomField.setEditable(false);
        HBox prenomBox = new HBox(10, prenomLabel, prenomField);
        prenomBox.setAlignment(Pos.CENTER_LEFT);

        // Champ Email
        Label emailLabel = new Label("Email:");
        emailLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: black;");
        TextField emailField = new TextField(user.getEmail());
        emailField.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cccccc; -fx-padding: 5;");
        emailField.setEditable(false);
        HBox emailBox = new HBox(10, emailLabel, emailField);
        emailBox.setAlignment(Pos.CENTER_LEFT);

        // Champ Mot de passe
        /*Label passwordLabel = new Label("Mot de passe:");
        passwordLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: black;");
        TextField mdpfield = new TextField(user.getMotdepasse());
        mdpfield.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cccccc; -fx-padding: 5;");
        mdpfield.setEditable(false);
        HBox mdpBox = new HBox(10, passwordLabel, mdpfield);
        mdpBox.setAlignment(Pos.CENTER_LEFT);*/

        // Champ Rôle
        Label roleLabel = new Label("Rôle:");
        roleLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: black;");
        TextField roleField = new TextField(user.getRole().name());
        roleField.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cccccc; -fx-padding: 5;");
        roleField.setEditable(false);
        HBox roleBox = new HBox(10, roleLabel, roleField);
        roleBox.setAlignment(Pos.CENTER_LEFT);

        // Champ Date d'inscription
        Label dateInscriptionLabel = new Label("Date d'inscription");
        dateInscriptionLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: black;");
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String dateInscriptionString = sdf.format(user.getDateinscription());
        TextField dateinscription = new TextField(dateInscriptionString);
        dateinscription.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cccccc; -fx-padding: 5;");
        dateinscription.setEditable(false);
        HBox dateInscriptionBox = new HBox(10, dateInscriptionLabel, dateinscription);
        dateInscriptionBox.setAlignment(Pos.CENTER_LEFT);

        // Ajouter les HBoxes à la carte
        card.getChildren().addAll(nameBox, prenomBox, emailBox,  roleBox, dateInscriptionBox);

        return card;
    }

    private VBox createResponsableCard(responsable responsable) {
        VBox card = new VBox(10);
        card.setStyle("-fx-border-color: #ddd; -fx-border-width: 1; -fx-padding: 10; -fx-background-color: #f9f9f9; -fx-alignment: center;");


        // Champ Nom (non modifiable)
        Label nameLabel = new Label("Nom:");
        nameLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: black;");
        TextField nameField = new TextField(responsable.getNom());
        nameField.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cccccc; -fx-padding: 5;");
        nameField.setEditable(false);
        HBox nameBox = new HBox(10, nameLabel, nameField); // Aligner le label et le champ Nom
        nameBox.setAlignment(Pos.CENTER_LEFT);

        // Champ Prénom (non modifiable)
        Label prenomLabel = new Label("Prénom:");
        prenomLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: black;");
        TextField prenomField = new TextField(responsable.getPrenom());
        prenomField.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cccccc; -fx-padding: 5;");
        prenomField.setEditable(false);
        HBox prenomBox = new HBox(10, prenomLabel, prenomField); // Aligner le label et le champ Prénom
        prenomBox.setAlignment(Pos.CENTER_LEFT);

        // Champ Email (non modifiable)
        Label emailLabel = new Label("Email:");
        emailLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: black;");
        TextField emailField = new TextField(responsable.getEmail());
        emailField.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cccccc; -fx-padding: 5;");
        emailField.setEditable(false);
        HBox emailBox = new HBox(10, emailLabel, emailField); // Aligner le label et le champ Email
        emailBox.setAlignment(Pos.CENTER_LEFT);

        // Champ Mot de passe (non modifiable)
      /*  Label passwordLabel = new Label("Mot de passe:");
        passwordLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: black;");
        TextField passwordField = new TextField(responsable.getMotdepasse());
        passwordField.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cccccc; -fx-padding: 5;");
        passwordField.setEditable(false);
        HBox passwordBox = new HBox(10, passwordLabel, passwordField); // Aligner le label et le champ Mot de passe
        passwordBox.setAlignment(Pos.CENTER_LEFT);*/

        // Champ Date d'inscription (non modifiable)
        Label dateInscriptionLabel = new Label("Date d'inscription:");
        dateInscriptionLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: black;");
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        TextField dateField = new TextField(sdf.format(responsable.getDateinscription()));
        dateField.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cccccc; -fx-padding: 5;");
        dateField.setEditable(false);
        HBox dateInscriptionBox = new HBox(10, dateInscriptionLabel, dateField); // Aligner le label et le champ Date d'inscription
        dateInscriptionBox.setAlignment(Pos.CENTER_LEFT);

        // Champ Modules (non modifiable)
        Label modulesLabel = new Label("Modules:");
        modulesLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: black;");
        TextField modulesField = new TextField(String.join(", ", responsable.getModules())); // Utiliser un TextField au lieu d'un TextArea
        modulesField.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cccccc; -fx-padding: 5;");
        modulesField.setEditable(false);
        HBox modulesBox = new HBox(10, modulesLabel, modulesField); // Aligner le label et le champ Modules
        modulesBox.setAlignment(Pos.CENTER_LEFT);

        // Ajouter les HBoxes à la carte
        card.getChildren().addAll(
                nameBox,
                prenomBox,
                emailBox,
                dateInscriptionBox,
                modulesBox
        );

        return card;
    }
    @FXML
    private void loadUsers() {
        List<utilisateur> users = serviceUtilisateur.getAllUtilisateurs();

        // Filtrer les utilisateurs par rôle sélectionné
        if (!"Tous".equals(roleSelectionne)) {
            users.removeIf(user -> !user.getRole().toString().equalsIgnoreCase(roleSelectionne));
        }

        // Effacer les anciennes cartes
        userFlowPane.getChildren().clear();

        // Afficher les utilisateurs sous forme de cartes
        for (utilisateur user : users) {
            VBox card = createUserCard(user);
            userFlowPane.getChildren().add(card);
        }
    }


    @FXML
    public void handleClear() {
        clearFields();
    }
    @FXML
    public void filterByTechnicien() {
        try {
            // Charger la vue GestionResponsable.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/GestionTechnicien.fxml"));
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
    private void handleUpdateResponsable() {
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

            // Vérifier si le responsable existe avec cet email et mot de passe
            utilisateur responsable = serviceUtilisateur.getByEmailAndPassword(email, password);
            if (responsable == null) {
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

            // Récupérer le module sélectionné dans la ComboBox
            String selectedModule = modulesComboBox.getValue();
            if (selectedModule == null) {
                showAlert("Erreur", "Veuillez sélectionner un module.");
                return;
            }

            // Mettre à jour les champs autorisés (nom, prénom, modules)
            serviceUtilisateur.updateFieldResponsable(responsable.getId_utilisateur(), "nom", newName);
            serviceUtilisateur.updateFieldResponsable(responsable.getId_utilisateur(), "prenom", newPrenom);
            serviceUtilisateur.updateFieldResponsable(responsable.getId_utilisateur(), "modules", selectedModule);

            // Recharger la liste des responsables
            loadUsers();

            // Réinitialiser les champs
            clearFields();

            // Afficher confirmation
            showAlert("Succès", "Responsable modifié avec succès.");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Une erreur est survenue lors de la modification.");
        }
    }

    @FXML
    private void handleShowRoleFilter() {
        // Afficher le ComboBox quand on clique sur le bouton
        roleFilterComboBox.setVisible(true);
    }

    @FXML
    private void handleFilterByRole() {
        String selectedRole = roleFilterComboBox.getValue();
        if (selectedRole == null || userFlowPane == null) return;

        // Récupérer tous les utilisateurs
        List<utilisateur> utilisateurs = serviceUtilisateur.getAllUtilisateurs();

        // Filtrer par rôle (sauf si "Tous" est sélectionné)
        if (!"Tous".equals(selectedRole)) {
            utilisateurs = utilisateurs.stream()
                    .filter(u -> u.getRole().name().equalsIgnoreCase(selectedRole))
                    .collect(Collectors.toList());
        }

        // Mettre à jour l'affichage
        userFlowPane.getChildren().clear();
        for (utilisateur user : utilisateurs) {
            VBox card = createUserCard(user);
            userFlowPane.getChildren().add(card);
        }
    }



    private void clearFields() {
        nameField.clear();
        prenomField.clear();
        emailField.clear();
        passwordField.clear();
        modulesComboBox.setValue(null); // Réinitialiser le champ des modules
        togglePasswordButton.setSelected(false); // Désactiver le basculement

    }




    @FXML
    private void handleDeleteResponsable() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Delete.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Suppression de Responsable");
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

    private void handleGeneratePdf(List<utilisateur> utilisateurs) {
        // Ouvrir un FileChooser pour choisir l'emplacement du fichier
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Enregistrer le PDF des Utilisateurs");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichiers PDF", "*.pdf"));
        fileChooser.setInitialFileName("utilisateurs.pdf");

        File file = fileChooser.showSaveDialog(null);
        if (file == null) {
            return; // L'utilisateur a annulé
        }

        // Générer le PDF des utilisateurs
        PdfGeneratorUser.generateUsersPdf(utilisateurs, file.getAbsolutePath());

        // Afficher un message de succès
        showAlert("Succès", "Le fichier PDF a été généré avec succès : " + file.getAbsolutePath());
    }
    @FXML
    private void handleGeneratePdf() {
        List<utilisateur> utilisateurs = serviceUtilisateur.getAllUtilisateurs(); // Récupérez les utilisateurs depuis votre service
        handleGeneratePdf(utilisateurs); // Appelez la méthode existante avec la liste
    }

    @FXML
    private Button btnNavigateToCameras; // Bouton pour l'interface Caméras

    @FXML
    private Button btnNavigateToLampadaireMap; // Bouton pour l'interface Carte Lampadaires

    @FXML
    private Button btnNavigateToLampadaires; // Bouton pour l'interface Lampadaires (alternative)

    @FXML
    private Button btnNavigateToZoneCitoyen; // Bouton pour l'interface Vue Citoyen




    // Handler pour le bouton de navigation vers l'interface Caméras
    @FXML
    private void handleNavigateToCameras(ActionEvent event) {
        switchScene(event, "/GestionCamera.fxml");
    }

    // Handler pour le bouton de navigation vers l'interface Carte Lampadaires
    @FXML
    private void handleNavigateToLampadaireMap(ActionEvent event) {
        switchScene(event, "/LampadaireMapView.fxml");
    }

    // Handler pour le bouton de navigation vers l'interface Lampadaires (alternative)
    @FXML
    private void handleNavigateToLampadaires(ActionEvent event) {
        switchScene(event, "/GestionLampadaire.fxml"); // Note : doublon potentiel avec handleGestionLampadaire
    }

    // Handler pour le bouton de navigation vers l'interface Vue Citoyen
    @FXML
    private void handleNavigateToZoneCitoyen(ActionEvent event) {
        switchScene(event, "/ZoneCitoyenView.fxml");
    }

    // Handler pour le bouton de navigation vers l'interface Zones (alternative)
    @FXML
    private void handleNavigateToZones(ActionEvent event) {
        switchScene(event, "/GestionZone.fxml"); // Note : doublon potentiel avec handleGestionZone
    }
    // Handler pour le bouton de gestion des techniciens
    @FXML
    private void handleGestionTechnicien(ActionEvent event) {
        switchScene(event, "/GestionTechnicien.fxml");
    }

    // Handler pour le bouton de gestion des utilisateurs
    @FXML
    private void handleGestionUtilisateur(ActionEvent event) {
        switchScene(event, "/GestionUtilisateur.fxml");
    }

    // Handler pour le bouton de gestion des zones
    @FXML
    private void handleGestionZone(ActionEvent event) {
        switchScene(event, "/GestionZone.fxml");
    }

    // Handler pour le bouton de gestion du profil
    @FXML
    private void handleProfileInterface(ActionEvent event) {
        switchScene(event, "/ProfileInterface.fxml");
    }

    // Handler pour le bouton des sources
    @FXML
    private void handleSourceInterface(ActionEvent event) {
        switchScene(event, "/SourceInterface.fxml");
    }

    // Handler pour revenir à la page d'accueil (Menu)
    @FXML
    private void handleAccueil(ActionEvent event) {
        switchScene(event, "/Menu.fxml");
    }

    // Méthode pour revenir à la page précédente (en option, à personnaliser)
    @FXML
    private void handleBack() {
        System.out.println("Retour à la page précédente");
    }


}
