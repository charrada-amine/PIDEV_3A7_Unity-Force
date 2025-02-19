package tn.esprit.models;

public class profile {
    private int idprofile;
    private String consommationJour;
    private String consommationMois;
    private String coutEstime;
    private String dureeActivite;
    private int sourceId; // Clé étrangère vers la table Source
    private int lampadaireId;

    // Constructeur
    public profile(int idprofile, String consommationJour, String consommationMois, String coutEstime, String dureeActivite, int sourceId, int lampadaireId) {
        this.idprofile = idprofile;
        this.consommationJour = consommationJour;
        this.consommationMois = consommationMois;
        this.coutEstime = coutEstime;
        this.dureeActivite = dureeActivite;
        this.sourceId = sourceId;
        this.lampadaireId = lampadaireId;
    }
    // No-argument constructor
    public profile() {
        // Optionally, set default values if needed
        this.consommationJour = "";
        this.consommationMois = "";
        this.coutEstime = "";
        this.dureeActivite = "";
        this.sourceId = 0;
        this.lampadaireId = 0;
    }


    // Constructeur
    public profile( String consommationJour, String consommationMois, String coutEstime, String dureeActivite, int sourceId, int lampadaireId) {

        this.consommationJour = consommationJour;
        this.consommationMois = consommationMois;
        this.coutEstime = coutEstime;
        this.dureeActivite = dureeActivite;
        this.sourceId = sourceId;
        this.lampadaireId = lampadaireId;
    }

    // Getters et Setters
    public int getIdprofile() { return idprofile; }
    public void setIdprofile(int idprofile) { this.idprofile = idprofile; }

    public String getConsommationJour() { return consommationJour; }
    public void setConsommationJour(String consommationJour) { this.consommationJour = consommationJour; }

    public String getConsommationMois() { return consommationMois; }
    public void setConsommationMois(String consommationMois) { this.consommationMois = consommationMois; }

    public String getCoutEstime() { return coutEstime; }
    public void setCoutEstime(String coutEstime) { this.coutEstime = coutEstime; }

    public String getDureeActivite() { return dureeActivite; }
    public void setDureeActivite(String dureeActivite) { this.dureeActivite = dureeActivite; }

    public int getSourceId() { return sourceId; }
    public void setSourceId(int sourceId) { this.sourceId = sourceId; }

    public int getLampadaireId() { return lampadaireId; }
    public void setLampadaireId(int lampadaireId) { this.lampadaireId = lampadaireId; }
}
