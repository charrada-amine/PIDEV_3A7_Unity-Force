package tn.esprit.models;

import java.time.LocalDate;

public class Lampadaire {
    private int id_lamp;
    private String typeLampadaire;
    private float puissance;
    private EtatLampadaire etat;
    private LocalDate dateInstallation;
    private int id_zone;

    public Lampadaire() {}

    public Lampadaire(int id_lamp, String typeLampadaire, float puissance,
                      EtatLampadaire etat, LocalDate dateInstallation, int id_zone) {
        this.id_lamp = id_lamp;
        this.typeLampadaire = typeLampadaire;
        this.puissance = puissance;
        this.etat = etat;
        this.dateInstallation = dateInstallation;
        this.id_zone = id_zone;
    }

    // Enum avec gestion des anciennes valeurs
    public enum EtatLampadaire {
        ACTIF("actif", "allume"),
        EN_PANNE("en panne", "eteint"),
        EN_MAINTENANCE("en maintenance");

        private final String[] aliases;

        EtatLampadaire(String... aliases) {
            this.aliases = aliases;
        }

        public static EtatLampadaire fromString(String text) {
            String cleanedText = text.trim().replace(" ", "_").toUpperCase();
            for (EtatLampadaire etat : values()) {
                for (String alias : etat.aliases) {
                    if (alias.replace(" ", "_").equalsIgnoreCase(cleanedText)) {
                        return etat;
                    }
                }
                if (etat.name().equalsIgnoreCase(cleanedText)) return etat;
            }
            throw new IllegalArgumentException("État non valide: " + text);
        }

        @Override
        public String toString() {
            return aliases[0];
        }
    }

    // Getters/Setters
    public int getIdLamp() { return id_lamp; }
    public void setIdLamp(int id_lamp) { this.id_lamp = id_lamp; }
    public String getTypeLampadaire() { return typeLampadaire; }
    public void setTypeLampadaire(String typeLampadaire) { this.typeLampadaire = typeLampadaire; }
    public float getPuissance() { return puissance; }
    public void setPuissance(float puissance) { this.puissance = puissance; }
    public EtatLampadaire getEtat() { return etat; }
    public void setEtat(EtatLampadaire etat) { this.etat = etat; }
    public LocalDate getDateInstallation() { return dateInstallation; }
    public void setDateInstallation(LocalDate dateInstallation) { this.dateInstallation = dateInstallation; }
    public int getIdZone() { return id_zone; }
    public void setIdZone(int id_zone) { this.id_zone = id_zone; }

    @Override
    public String toString() {
        return "Lampadaire{" +
                "id_lamp=" + id_lamp +
                ", typeLampadaire='" + typeLampadaire + '\'' +
                ", puissance=" + puissance +
                ", etat=" + etat +
                ", dateInstallation=" + dateInstallation +
                ", id_zone=" + id_zone +
                '}';
    }
}