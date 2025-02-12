package tn.esprit.test;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainFx extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        // Charger l'interface utilisateur (FXML) pour la gestion des utilisateurs
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/GestionUtilisateur.fxml"));
        try {
            // Charger le fichier FXML pour la gestion des utilisateurs
            Parent root = loader.load();

            // Créer la scène et afficher la fenêtre principale
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Gestion des Utilisateurs");
            primaryStage.show();

        } catch (IOException e) {
            System.out.println("Erreur de chargement du fichier FXML : " + e.getMessage());
        }
    }
}
