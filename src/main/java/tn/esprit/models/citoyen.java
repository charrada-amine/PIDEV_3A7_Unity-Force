package tn.esprit.models;

import tn.esprit.Enumerations.Role;

import java.util.Date;

public class citoyen extends utilisateur {
    private int id_citoyen; // Correspond à id_utilisateur
    private int zoneId;     // Zone du citoyen, fait référence à la zoneId

    // Constructeur
    public citoyen(String nom, String prenom, String email, String motdepasse, Date dateInscription, int zoneId) {
        super(nom, prenom, email, motdepasse, Role.citoyen, dateInscription); // Utilisation directe de "citoyen" comme rôle
        this.id_citoyen = id_utilisateur; // id_citoyen = id_utilisateur
        this.zoneId = zoneId;             // Attribuer la zoneId manuellement
    }

    // Getter et Setter pour zoneId
    public int getZoneId() {
        return zoneId;
    }

    public void setZoneId(int zoneId) {
        this.zoneId = zoneId;
    }

    @Override
    public String toString() {
        return super.toString() + " | Zone: " + zoneId;
    }
}
