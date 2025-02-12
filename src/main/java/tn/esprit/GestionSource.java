/*package tn.esprit;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import tn.esprit.models.Source;
import tn.esprit.services.ServiceSource;

public class GestionSource {

    @FXML
    private TextField type_tf;

    @FXML
    private TextField capacite_tf;

    @FXML
    private TextField rendement_tf;

    @FXML
    private TextField etat_tf;

    @FXML
    private TextField dateInstallation_dp; // Utilisation d'un TextField pour saisir la date

    @FXML
    void addSource(ActionEvent event) {
        ServiceSource ss = new ServiceSource();

        // Création d'un objet Source avec les données du formulaire
        Source source = new Source(
                0,  // L'id est auto-généré par la base de données
                type_tf.getText(),
                Float.parseFloat(capacite_tf.getText()),
                Float.parseFloat(rendement_tf.getText()),
                etat_tf.getText(),
                dateInstallation_dp.getText()  // Utilisation de getText() pour récupérer la date sous forme de String
        );

        ss.add(source);  // Ajouter la source via le service
    }
}*/
