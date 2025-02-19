package tn.esprit.models;

import java.sql.Date;
import java.sql.Time;

public class Intervention {
<<<<<<< HEAD

    private int id;
=======
    private int ID_intervention; // Nom aligné avec la BDD
>>>>>>> origin/AzizBenAmmar/GestionInterventions_Reclamations
    public enum TypeIntervention {
        REPARATION,
        MAINTENANCE_PREVENTIVE,
        INSTALLATION
    }
<<<<<<< HEAD
    private TypeIntervention typeIntervention; // Declare the field here
=======
    private TypeIntervention typeIntervention;
>>>>>>> origin/AzizBenAmmar/GestionInterventions_Reclamations
    private String description;
    private String etat;
    private Date dateIntervention;
    private Time heureIntervention;
    private int lampadaireId;
    private int technicienId;
<<<<<<< HEAD
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
=======
    private Integer ID_reclamation; // Integer pour permettre null

    public Intervention() {}

    // Constructeur complet avec ID_reclamation nullable
    public Intervention(TypeIntervention typeIntervention, String description, String etat,
                        Date dateIntervention, Time heureIntervention, int lampadaireId,
                        int technicienId, Integer ID_reclamation) {
>>>>>>> origin/AzizBenAmmar/GestionInterventions_Reclamations
        this.typeIntervention = typeIntervention;
        this.description = description;
        this.etat = etat;
        this.dateIntervention = dateIntervention;
        this.heureIntervention = heureIntervention;
        this.lampadaireId = lampadaireId;
        this.technicienId = technicienId;
        this.ID_reclamation = ID_reclamation;
    }

<<<<<<< HEAD
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
=======
    // Constructeur sans ID_reclamation
    public Intervention(TypeIntervention typeIntervention, String description, String etat,
                        Date dateIntervention, Time heureIntervention, int lampadaireId,
                        int technicienId) {
        this(typeIntervention, description, etat, dateIntervention, heureIntervention,
                lampadaireId, technicienId, null);
    }

    // Getters/Setters
    public int getID_intervention() { return ID_intervention; }
    public void setID_intervention(int ID_intervention) { this.ID_intervention = ID_intervention; }

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

    public Integer getID_reclamation() { return ID_reclamation; }
    public void setID_reclamation(Integer ID_reclamation) { this.ID_reclamation = ID_reclamation; }
>>>>>>> origin/AzizBenAmmar/GestionInterventions_Reclamations

    @Override
    public String toString() {
        return "Intervention{" +
<<<<<<< HEAD
                "ID_intervention=" + id +
=======
                "ID_intervention=" + ID_intervention +
>>>>>>> origin/AzizBenAmmar/GestionInterventions_Reclamations
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
<<<<<<< HEAD
}
=======
}
>>>>>>> origin/AzizBenAmmar/GestionInterventions_Reclamations
