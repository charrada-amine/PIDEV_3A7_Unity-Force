package tn.esprit.models;

import java.sql.Date;
import java.sql.Time;

public class Reclamation {
    private int id;
    private String description;
    private Date dateReclamation;
    private Time heureReclamation;
    private String statut;
    private int lampadaireId;
    private int citoyenId;

    public Reclamation() {
    }

    public Reclamation(int id, String description, Date dateReclamation, Time heureReclamation, String statut, int lampadaireId, int citoyenId) {
        this.id = id;
        this.description = description;
        this.dateReclamation = dateReclamation;
        this.heureReclamation = heureReclamation;
        this.statut = statut;
        this.lampadaireId = lampadaireId;
        this.citoyenId = citoyenId;
    }

    public Reclamation(String description, Date dateReclamation, Time heureReclamation, String statut, int lampadaireId, int citoyenId) {
        this.description = description;
        this.dateReclamation = dateReclamation;
        this.heureReclamation = heureReclamation;
        this.statut = statut;
        this.lampadaireId = lampadaireId;
        this.citoyenId = citoyenId;
    }

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
    public int getCitoyenId() { return citoyenId; }
    public void setCitoyenId(int citoyenId) { this.citoyenId = citoyenId; }

    @Override
    public String toString() {
        return "Reclamation{" +
                "ID_reclamation=" + id +
                ", description='" + description + '\'' +
                ", dateReclamation=" + dateReclamation +
                ", heureReclamation=" + heureReclamation +
                ", statut='" + statut + '\'' +
                ", lampadaireId=" + lampadaireId +
                ", citoyenId=" + citoyenId +
                "}\n";
    }
}