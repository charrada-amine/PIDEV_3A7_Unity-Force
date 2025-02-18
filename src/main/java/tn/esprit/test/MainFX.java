package tn.esprit.test;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainFX extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/Menu.fxml"));
        primaryStage.setTitle("Gestion des Profils et Sources");

        // Définir une taille fixe pour la fenêtre
        Scene scene = new Scene(root, 1000, 700);

        primaryStage.setScene(scene);



        // Empêcher le redimensionnement de la fenêtre
        primaryStage.setResizable(false);

        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
