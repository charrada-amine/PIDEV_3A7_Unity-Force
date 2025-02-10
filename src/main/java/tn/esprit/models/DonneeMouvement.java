// DonneeMouvement
package tn.esprit.models;
import java.time.LocalDate;
import java.time.LocalTime;

public class DonneeMouvement extends Donnee {
    private boolean valeur;

    public DonneeMouvement(int id, LocalDate dateCollecte, LocalTime heureCollecte, int capteurId, boolean valeur) {
        super(id, dateCollecte, heureCollecte, capteurId);
        this.valeur = valeur;
    }

    public boolean getValeur() {
        return valeur;
    }

    public void setValeur(boolean valeur) {
        this.valeur = valeur;
    }
}
