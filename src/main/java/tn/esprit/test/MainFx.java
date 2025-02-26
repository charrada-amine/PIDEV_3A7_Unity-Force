package tn.esprit.test;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class MainFx extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Login.fxml"));
        try {
            Parent root = loader.load();
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.setTitle("gestion utilisateur");

            // Passer en plein écran ou maximiser la fenêtre
            primaryStage.setMaximized(true);

            primaryStage.show();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

}