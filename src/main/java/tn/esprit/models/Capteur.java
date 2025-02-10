package tn.esprit.models;

import java.time.LocalDate;

public class Capteur {
    private int id;
    private TypeCapteur type;  // Utilisation de l'énumération TypeCapteur
    private LocalDate dateinstallation;
    private EtatCapteur etat;  // Utilisation de l'énumération EtatCapteur
    private int lampadaireId;

    // Constructeur
    public Capteur(int id, TypeCapteur type, LocalDate dateinstallation, EtatCapteur etat, int lampadaireId) {
        this.id = id;
        this.type = type;
        this.dateinstallation = dateinstallation;
        this.etat = etat;
        this.lampadaireId = lampadaireId;
    }

    // Getters et Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public TypeCapteur getType() {
        return type;
    }

    public void setType(TypeCapteur type) {
        this.type = type;
    }

    public LocalDate getDateinstallation() {
        return dateinstallation;
    }

    public void setDateinstallation(LocalDate dateinstallation) {
        this.dateinstallation = dateinstallation;
    }

    public EtatCapteur getEtat() {
        return etat;
    }

    public void setEtat(EtatCapteur etat) {
        this.etat = etat;
    }

    public int getLampadaireId() {
        return lampadaireId;
    }

    public void setLampadaireId(int lampadaireId) {
        this.lampadaireId = lampadaireId;
    }

    // Enum pour le type de capteur
    public enum TypeCapteur {
        MOUVEMENT,
        TEMPERATURE,
        LUMINOSITE,
        CONSOMMATION_ENERGIE;
    }

    // Enum pour l'état du capteur
    public enum EtatCapteur {
        ACTIF,
        INACTIF,
        EN_PANNE;  // Ajout de l'état "EN_PANNE"
    }
}
