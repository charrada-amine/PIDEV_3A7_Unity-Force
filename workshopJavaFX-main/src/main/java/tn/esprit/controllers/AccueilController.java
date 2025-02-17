package tn.esprit.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class AccueilController {

    // Méthode pour ouvrir l'interface de gestion des utilisateurs
    @FXML
    private void ouvrirInterfaceTous() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/GestionUtilisateur.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Gestion des Utilisateurs");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Méthode pour ouvrir l'interface de gestion des citoyens
    @FXML
    private void ouvrirInterfaceCitoyens() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/GestionCitoyen.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Gestion des Citoyens");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Méthode pour ouvrir l'interface de gestion des techniciens
    @FXML
    private void ouvrirInterfaceTechniciens() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/GestionTechnicien.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Gestion des Techniciens");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Méthode pour ouvrir l'interface de gestion des responsables
    @FXML
    private void ouvrirInterfaceResponsables() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/GestionResponsable.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Gestion des Responsables");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
