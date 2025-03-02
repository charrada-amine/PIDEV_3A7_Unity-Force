package tn.esprit.models;


import tn.esprit.Enumerations.Role;
import java.util.Date;

public class utilisateur {
    protected int id_utilisateur; // Utilisé par les classes filles (protected au lieu de private)
    private String nom;
    private String prenom;
    private String email;
    private String motdepasse;
    private Role role;
    private Date dateInscription;

    // Constructeur sans ID
    public utilisateur(String nom, String prenom, String email, String motdepasse, Role role, Date dateInscription) {
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.motdepasse = motdepasse;
        this.role = role;
        this.dateInscription = dateInscription;
    }
    // Constructeur AVEC ID
    public utilisateur(int id_utilisateur, String nom, String prenom, String email, String motdepasse, Role role, Date dateInscription) {
        this.id_utilisateur = id_utilisateur;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.motdepasse = motdepasse;
        this.role = role;
        this.dateInscription = dateInscription;
    }


    // Constructeur par défaut
    public utilisateur() {
    }



    // Getters et Setters
    public int getId_utilisateur() {
        return id_utilisateur;
    }

    public void setId_utilisateur(int id_utilisateur) {
        this.id_utilisateur = id_utilisateur;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMotdepasse() {
        return motdepasse;
    }

    public void setMotdepasse(String motdepasse) {
        this.motdepasse = motdepasse;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    // Getter pour dateInscription
    public Date getDateinscription() {
        return dateInscription;
    }

    // Setter pour dateInscription
    public void setDateinscription(Date dateInscription) {
        this.dateInscription = dateInscription;
    }

    @Override
    public String toString() {
        return "Utilisateur {id_utilisateur=" + id_utilisateur + ", nom=" + nom + ", prenom=" + prenom +
                ", email=" + email +  ", motdepasse=" + motdepasse + ", role=" + role + ", dateInscription=" + dateInscription + "}";
    }

}