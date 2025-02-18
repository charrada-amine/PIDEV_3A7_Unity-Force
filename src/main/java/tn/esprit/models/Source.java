package tn.esprit.models;

import tn.esprit.Enumerations.EnumEtat;
import tn.esprit.Enumerations.EnumType;
import java.time.LocalDate;

public class Source {
    private int idSource;
    private EnumType type;
    private float capacite;
    private float rendement;
    private EnumEtat etat;
    private LocalDate dateInstallation;

    // Constructeur par défaut
    public Source() {}

    // Constructeur avec paramètres
    public Source(int id, EnumType type, float capacite, float rendement, EnumEtat etat, LocalDate dateInstallation) {
        this.idSource = id; // Correction ici
        this.type = type;
        this.capacite = capacite;
        this.rendement = rendement;
        this.etat = etat;
        this.dateInstallation = dateInstallation;
    }
    // Constructeur sans ID (utilisé pour les nouvelles sources)
    public Source(EnumType type, float capacite, float rendement, EnumEtat etat, LocalDate dateInstallation) {
        this.type = type;
        this.capacite = capacite;
        this.rendement = rendement;
        this.etat = etat;
        this.dateInstallation = dateInstallation;
    }


    // Getters et Setters
    public int getIdSource() { return idSource; }
    public void setIdSource(int idSource) { this.idSource = idSource; } // Correction ici

    public EnumType getType() { return type; }
    public void setType(EnumType type) { this.type = type; }

    public float getCapacite() { return capacite; }
    public void setCapacite(float capacite) { this.capacite = capacite; }

    public float getRendement() { return rendement; }
    public void setRendement(float rendement) { this.rendement = rendement; }

    public EnumEtat getEtat() { return etat; }
    public void setEtat(EnumEtat etat) { this.etat = etat; }

    public LocalDate getDateInstallation() { return dateInstallation; }
    public void setDateInstallation(LocalDate dateInstallation) { this.dateInstallation = dateInstallation; }

    // Méthode toString pour faciliter le débogage
    @Override
    public String toString() {
        return "Source{" +
                "idSource=" + idSource +
                ", type=" + type +
                ", capacite=" + capacite +
                ", rendement=" + rendement +
                ", etat=" + etat +
                ", dateInstallation=" + dateInstallation +
                '}';
    }
}
