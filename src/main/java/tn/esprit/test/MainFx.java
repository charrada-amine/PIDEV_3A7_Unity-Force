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
<<<<<<< HEAD
        FXMLLoader loader= new FXMLLoader(getClass().getResource("/GestionCapteur.fxml"));
        try {
            Parent root =loader.load();
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.setTitle("hello from the other side ");
            primaryStage.show();

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }


=======
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/MainView.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root, 1200, 800);
            primaryStage.setTitle("Gestion des interventions et réclamations");
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
>>>>>>> origin/AzizBenAmmar/GestionInterventions_Reclamations
    }
}