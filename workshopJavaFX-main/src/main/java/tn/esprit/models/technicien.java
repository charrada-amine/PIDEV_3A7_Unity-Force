package tn.esprit.models;

import java.util.Date;

public class technicien extends utilisateur {
    private int id_technicien; // Correspond à id_utilisateur

    private Specialite specialite;


    // Constructeur avec le rôle en tant que String statique
    public technicien(String nom, String prenom, String email, String motdepasse, Date dateInscription, Specialite specialite) {
        super(nom, prenom, email, motdepasse, Role.technicien, dateInscription); // Utilisation directe sans conversion en majuscule
        this.id_technicien = id_utilisateur; // id_technicien = id_utilisateur
        this.specialite = specialite;    }

    // Getter et Setter pour specialite
    public Specialite getSpecialite() {
        return specialite;
    }

    public void setSpecialite(Specialite specialite) {
        this.specialite = specialite;
    }

    @Override
    public String toString() {
        return super.toString() + " | Spécialité: " + specialite;
    }
}
