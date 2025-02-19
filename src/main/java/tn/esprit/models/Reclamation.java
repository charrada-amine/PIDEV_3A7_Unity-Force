package tn.esprit.models;

import java.sql.Date;
import java.sql.Time;

public class Reclamation {
<<<<<<< HEAD
    private int id;
=======
    private int ID_reclamation; // Changement de nom pour correspondre à la BDD
>>>>>>> origin/AzizBenAmmar/GestionInterventions_Reclamations
    private String description;
    private Date dateReclamation;
    private Time heureReclamation;
    private String statut;
    private int lampadaireId;
    private int citoyenId;

    public Reclamation() {
<<<<<<< HEAD
    }

    public Reclamation(int id, String description, Date dateReclamation, Time heureReclamation, String statut, int lampadaireId, int citoyenId) {
        this.id = id;
=======
        // Constructeur par défaut requis pour JPA/DAO
    }

    // Constructeur sans ID pour les nouvelles insertions (auto-incrément)
    public Reclamation(String description, Date dateReclamation, Time heureReclamation,
                       String statut, int lampadaireId, int citoyenId) {
>>>>>>> origin/AzizBenAmmar/GestionInterventions_Reclamations
        this.description = description;
        this.dateReclamation = dateReclamation;
        this.heureReclamation = heureReclamation;
        this.statut = statut;
        this.lampadaireId = lampadaireId;
        this.citoyenId = citoyenId;
    }

<<<<<<< HEAD
    public Reclamation(String description, Date dateReclamation, Time heureReclamation, String statut, int lampadaireId, int citoyenId) {
=======
    // Constructeur complet pour les requêtes
    public Reclamation(int ID_reclamation, String description, Date dateReclamation,
                       Time heureReclamation, String statut, int lampadaireId, int citoyenId) {
        this.ID_reclamation = ID_reclamation;
>>>>>>> origin/AzizBenAmmar/GestionInterventions_Reclamations
        this.description = description;
        this.dateReclamation = dateReclamation;
        this.heureReclamation = heureReclamation;
        this.statut = statut;
        this.lampadaireId = lampadaireId;
        this.citoyenId = citoyenId;
    }

<<<<<<< HEAD
    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Date getDateReclamation() { return dateReclamation; }
    public void setDateReclamation(Date dateReclamation) { this.dateReclamation = dateReclamation; }
    public Time getHeureReclamation() { return heureReclamation; }
    public void setHeureReclamation(Time heureReclamation) { this.heureReclamation = heureReclamation; }
    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }
    public int getLampadaireId() { return lampadaireId; }
    public void setLampadaireId(int lampadaireId) { this.lampadaireId = lampadaireId; }
=======
    // Getters/Setters renommés pour correspondre à la BDD
    public int getID_reclamation() { return ID_reclamation; }
    public void setID_reclamation(int ID_reclamation) { this.ID_reclamation = ID_reclamation; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Date getDateReclamation() { return dateReclamation; }
    public void setDateReclamation(Date dateReclamation) { this.dateReclamation = dateReclamation; }

    public Time getHeureReclamation() { return heureReclamation; }
    public void setHeureReclamation(Time heureReclamation) { this.heureReclamation = heureReclamation; }

    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }

    public int getLampadaireId() { return lampadaireId; }
    public void setLampadaireId(int lampadaireId) { this.lampadaireId = lampadaireId; }

>>>>>>> origin/AzizBenAmmar/GestionInterventions_Reclamations
    public int getCitoyenId() { return citoyenId; }
    public void setCitoyenId(int citoyenId) { this.citoyenId = citoyenId; }

    @Override
    public String toString() {
        return "Reclamation{" +
<<<<<<< HEAD
                "ID_reclamation=" + id +
=======
                "ID_reclamation=" + ID_reclamation +
>>>>>>> origin/AzizBenAmmar/GestionInterventions_Reclamations
                ", description='" + description + '\'' +
                ", dateReclamation=" + dateReclamation +
                ", heureReclamation=" + heureReclamation +
                ", statut='" + statut + '\'' +
                ", lampadaireId=" + lampadaireId +
                ", citoyenId=" + citoyenId +
<<<<<<< HEAD
                "}\n";
=======
                '}';
>>>>>>> origin/AzizBenAmmar/GestionInterventions_Reclamations
    }
}