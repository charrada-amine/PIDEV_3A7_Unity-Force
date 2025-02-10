package tn.esprit.models;

import java.time.LocalDate;
import java.time.LocalTime;

// Classe mère
public class Donnee {
    protected int id;
    protected LocalDate dateCollecte;
    protected LocalTime heureCollecte;
    protected int capteurId;

    public Donnee(int id, LocalDate dateCollecte, LocalTime heureCollecte, int capteurId) {
        this.id = id;
        this.dateCollecte = dateCollecte;
        this.heureCollecte = heureCollecte;
        this.capteurId = capteurId;
    }

    // Getters et Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDate getDateCollecte() {
        return dateCollecte;
    }

    public void setDateCollecte(LocalDate dateCollecte) {
        this.dateCollecte = dateCollecte;
    }

    public LocalTime getHeureCollecte() {
        return heureCollecte;
    }

    public void setHeureCollecte(LocalTime heureCollecte) {
        this.heureCollecte = heureCollecte;
    }

    public int getCapteurId() {
        return capteurId;
    }

    public void setCapteurId(int capteurId) {
        this.capteurId = capteurId;
    }
}
