package tn.esprit.models;

public class Zone {private int id_zone;
    private String nom;
    private String description;
    private float surface;
    private int nombreLampadaires;
    private int nombreCitoyens;

    // Constructeur par défaut
    public Zone() {
    }

    // Constructeur avec tous les attributs
    public Zone(int id_zone, String nom, String description, float surface, int nombreLampadaires, int nombreCitoyens) {
        this.id_zone = id_zone;
        this.nom = nom;
        this.description = description;
        this.surface = surface;
        this.nombreLampadaires = nombreLampadaires;
        this.nombreCitoyens = nombreCitoyens;
    }

    // Getters et Setters
    public int getIdZone() {
        return id_zone;
    }

    public void setIdZone(int id_zone) {
        this.id_zone = id_zone;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public float getSurface() {
        return surface;
    }

    public void setSurface(float surface) {
        this.surface = surface;
    }

    public int getNombreLampadaires() {
        return nombreLampadaires;
    }

    public void setNombreLampadaires(int nombreLampadaires) {
        this.nombreLampadaires = nombreLampadaires;
    }

    public int getNombreCitoyens() {
        return nombreCitoyens;
    }

    public void setNombreCitoyens(int nombreCitoyens) {
        this.nombreCitoyens = nombreCitoyens;
    }

    // Méthode toString
    @Override
    public String toString() {
        return "Zone{" +
                "id_zone=" + id_zone +
                ", nom='" + nom + '\'' +
                ", description='" + description + '\'' +
                ", surface=" + surface +
                ", nombreLampadaires=" + nombreLampadaires +
                ", nombreCitoyens=" + nombreCitoyens +
                '}';
    }  }