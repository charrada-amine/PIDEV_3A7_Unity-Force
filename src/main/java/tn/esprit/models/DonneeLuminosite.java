// DonneeLuminosite
package tn.esprit.models;
import java.time.LocalDate;
import java.time.LocalTime;

public class DonneeLuminosite extends Donnee {
    private int valeur;

    public DonneeLuminosite(int id, LocalDate dateCollecte, LocalTime heureCollecte, int capteurId, int valeur) {
        super(id, dateCollecte, heureCollecte, capteurId);
        this.valeur = valeur;
    }

    public int getValeur() {
        return valeur;
    }

    public void setValeur(int valeur) {
        this.valeur = valeur;
    }
}
