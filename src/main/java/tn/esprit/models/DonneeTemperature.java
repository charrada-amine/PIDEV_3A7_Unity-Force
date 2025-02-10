// DonneeTemperature
package tn.esprit.models;
import java.time.LocalDate;
import java.time.LocalTime;

public class DonneeTemperature extends Donnee {
    private float valeur;

    public DonneeTemperature(int id, LocalDate dateCollecte, LocalTime heureCollecte, int capteurId, float valeur) {
        super(id, dateCollecte, heureCollecte, capteurId);
        this.valeur = valeur;
    }

    public float getValeur() {
        return valeur;
    }

    public void setValeur(float valeur) {
        this.valeur = valeur;
    }
}
