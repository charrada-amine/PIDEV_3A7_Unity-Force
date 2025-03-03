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
    private String nom; // Nouvelle variable

    public Source(int id, EnumType type, float capacite, float rendement, EnumEtat etat, LocalDate dateInstallation, String nom) {
        this.idSource = id;
        this.type = type;
        this.capacite = capacite;
        this.rendement = rendement;
        this.etat = etat;
        this.dateInstallation = dateInstallation;
        this.nom = nom; // Initialisation de la nouvelle variable
    }

    public Source() {
        this.idSource = 0;
        this.type = EnumType.BATTERIE;
        this.capacite = 0.0f;
        this.rendement = 0.0f;
        this.etat = EnumEtat.ACTIF;
        this.dateInstallation = LocalDate.now();
        this.nom = ""; // Initialisation par défaut
    }

    public Source(EnumType type, float capacite, float rendement, EnumEtat etat, LocalDate dateInstallation, String nom) {
        this.type = type;
        this.capacite = capacite;
        this.rendement = rendement;
        this.etat = etat;
        this.dateInstallation = dateInstallation;
        this.nom = nom; // Initialisation de la nouvelle variable
    }

    public int getIdSource() { return idSource; }
    public void setIdSource(int idSource) { this.idSource = idSource; }

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

    public String getNom() { return nom; } // Getter pour le nom
    public void setNom(String nom) { this.nom = nom; } // Setter pour le nom

    @Override
    public String toString() {
        return "Source{" +
                "idSource=" + idSource +
                ", type=" + type +
                ", capacite=" + capacite +
                ", rendement=" + rendement +
                ", etat=" + etat +
                ", dateInstallation=" + dateInstallation +
                ", nom='" + nom + '\'' + // Ajout du nom dans la méthode toString
                '}';
    }
}