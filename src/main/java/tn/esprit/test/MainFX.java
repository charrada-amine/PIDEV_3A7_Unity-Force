package tn.esprit.test;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class MainFX extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            // Chemin corrigé pour MainMenu.fxml
            Parent root = FXMLLoader.load(getClass().getResource("/GestionCamera.fxml"));

            Scene scene = new Scene(root, 1200, 800);

            // Chemin CSS corrigé (si nécessaire)
            scene.getStylesheets().add(getClass().getResource("/styles/style.css").toExternalForm());

            // Titre mis à jour
            primaryStage.setTitle("Smart City Lighting Management");
            primaryStage.setScene(scene);
            primaryStage.show();

        } catch (IOException e) {
            System.err.println("Erreur de chargement : " + e.getMessage());
            e.printStackTrace();
        }
    }
}