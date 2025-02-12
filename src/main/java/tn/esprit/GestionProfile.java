/*package tn.esprit;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.fxml.FXMLLoader;
import tn.esprit.models.profile;
import tn.esprit.services.ServiceProfile;

import java.io.IOException;
import java.util.List;

public class GestionProfile {

    @FXML
    private TextField sourceenergetique_tf;

    @FXML
    private TextField consommationjour_tf;

    @FXML
    private TextField consommationmois_tf;

    @FXML
    private TextField coutestime_tf;

    @FXML
    private TextField dureactivite_tf;

    @FXML
    private HBox boxh;

    // Méthode d'ajout de profil
    @FXML
    void addProfile(ActionEvent event) {
        ServiceProfile sp = new ServiceProfile();

        // Créer un objet Profil avec les données du formulaire
        profile profil = new profile(
                0,  // L'id est auto-généré par la base de données
                sourceenergetique_tf.getText(),
                consommationjour_tf.getText(),
                consommationmois_tf.getText(),
                coutestime_tf.getText(),
                dureactivite_tf.getText()
        );

        sp.add(profil);  // Ajouter le profil via le service
    }

    // Méthode pour afficher tous les profils
    @FXML
    void showProfiles(ActionEvent event) {
        ServiceProfile sp = new ServiceProfile();

        // Récupérer tous les profils depuis la base de données
        List<profile> profiles = sp.getAll();

        // Vider la HBox avant d'ajouter les nouveaux profils
        boxh.getChildren().clear();

        // Pour chaque profil, charger la vue correspondante et l'ajouter à la HBox
        for (profile p : profiles) {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/cardview.fxml"));
                HBox box = fxmlLoader.load();
                CardViewController controller = fxmlLoader.getController();
                controller.setProfil(p); // Assurez-vous que votre CardViewController prend un profil

                boxh.getChildren().add(box);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}*/
