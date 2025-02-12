package tn.esprit.models;

import java.sql.Date;
import java.sql.Time;

public class Intervention {

    private int id;
    public enum TypeIntervention {
        REPARATION,
        MAINTENANCE_PREVENTIVE,
        INSTALLATION
    }
    private TypeIntervention typeIntervention; // Declare the field here
    private String description;
    private String etat;
    private Date dateIntervention;
    private Time heureIntervention;
    private int lampadaireId;
    private int technicienId;
    private int ID_reclamation;

    public Intervention() {
    }

    public Intervention(TypeIntervention typeIntervention, String description, String etat, Date dateIntervention, Time heureIntervention, int lampadaireId, int technicienId,int ID_reclamation) {
        this.typeIntervention = typeIntervention;
        this.description = description;
        this.etat = etat;
        this.dateIntervention = dateIntervention;
        this.heureIntervention = heureIntervention;
        this.lampadaireId = lampadaireId;
        this.technicienId = technicienId;
        this.ID_reclamation = ID_reclamation; // Error: uninitialized variable used
    }


    public Intervention(TypeIntervention typeIntervention, String description, String etat, Date dateIntervention, Time heureIntervention, int lampadaireId, int technicienId) {
        this.typeIntervention = typeIntervention;
        this.description = description;
        this.etat = etat;
        this.dateIntervention = dateIntervention;
        this.heureIntervention = heureIntervention;
        this.lampadaireId = lampadaireId;
        this.technicienId = technicienId;
        this.ID_reclamation = ID_reclamation;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public TypeIntervention getTypeIntervention() { return typeIntervention; }
    public void setTypeIntervention(TypeIntervention typeIntervention) { this.typeIntervention = typeIntervention; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getEtat() { return etat; }
    public void setEtat(String etat) { this.etat = etat; }
    public Date getDateIntervention() { return dateIntervention; }
    public void setDateIntervention(Date dateIntervention) { this.dateIntervention = dateIntervention; }
    public Time getHeureIntervention() { return heureIntervention; }
    public void setHeureIntervention(Time heureIntervention) { this.heureIntervention = heureIntervention; }
    public int getLampadaireId() { return lampadaireId; }
    public void setLampadaireId(int lampadaireId) { this.lampadaireId = lampadaireId; }
    public int getTechnicienId() { return technicienId; }
    public void setTechnicienId(int technicienId) { this.technicienId = technicienId; }
    public int getID_reclamation() { return ID_reclamation; } // Getter pour ID_reclamation
    public void setID_reclamation(int ID_reclamation) { this.ID_reclamation = ID_reclamation; }

    @Override
    public String toString() {
        return "Intervention{" +
                "ID_intervention=" + id +
                ", typeIntervention=" + typeIntervention +
                ", description='" + description + '\'' +
                ", etat='" + etat + '\'' +
                ", dateIntervention=" + dateIntervention +
                ", heureIntervention=" + heureIntervention +
                ", lampadaireId=" + lampadaireId +
                ", technicienId=" + technicienId +
                ", ID_reclamation=" + ID_reclamation +
                "}\n";
    }
}