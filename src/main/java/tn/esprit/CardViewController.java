package tn.esprit;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import tn.esprit.models.utilisateur;

public class CardViewController {

    @FXML
    private Label nomLabel;

    @FXML
    private Label prenomLabel;

    @FXML
    private Label emailLabel;

    @FXML
    private Label roleLabel;

    public void setUtilisateur(utilisateur utilisateur) {
        nomLabel.setText(utilisateur.getNom());
        prenomLabel.setText(utilisateur.getPrenom());
        emailLabel.setText(utilisateur.getEmail());

        // Conversion de Role en String pour l'afficher
        roleLabel.setText(utilisateur.getRole().name());  // Utilisation de `name()` pour obtenir une chaîne
    }

}
