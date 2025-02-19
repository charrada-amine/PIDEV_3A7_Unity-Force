package tn.esprit.models;

<<<<<<< HEAD
import java.time.LocalDate;

public class Lampadaire {
    private int id_lamp;
    private String typeLampadaire;
    private float puissance;
    private EtatLampadaire etat;
    private LocalDate dateInstallation;
    private int id_zone;
=======
import javafx.beans.property.*;
import java.time.LocalDate;

public class Lampadaire {
    private final IntegerProperty idLamp = new SimpleIntegerProperty();
    private final StringProperty typeLampadaire = new SimpleStringProperty();
    private final FloatProperty puissance = new SimpleFloatProperty();
    private final ObjectProperty<EtatLampadaire> etat = new SimpleObjectProperty<>();
    private final ObjectProperty<LocalDate> dateInstallation = new SimpleObjectProperty<>();
    private final IntegerProperty idZone = new SimpleIntegerProperty();
>>>>>>> origin/MedRayenSansa/GestionInfrastructure

    public Lampadaire() {}

    public Lampadaire(int id_lamp, String typeLampadaire, float puissance,
                      EtatLampadaire etat, LocalDate dateInstallation, int id_zone) {
<<<<<<< HEAD
        this.id_lamp = id_lamp;
        this.typeLampadaire = typeLampadaire;
        this.puissance = puissance;
        this.etat = etat;
        this.dateInstallation = dateInstallation;
        this.id_zone = id_zone;
=======
        this.idLamp.set(id_lamp);
        this.typeLampadaire.set(typeLampadaire);
        this.puissance.set(puissance);
        this.etat.set(etat);
        this.dateInstallation.set(dateInstallation);
        this.idZone.set(id_zone);
    }

    public Lampadaire(String text, float v, EtatLampadaire value, LocalDate value1, int i) {
>>>>>>> origin/MedRayenSansa/GestionInfrastructure
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

<<<<<<< HEAD
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
=======
    // Getters pour les propriétés
    public IntegerProperty idLampProperty() { return idLamp; }
    public StringProperty typeLampadaireProperty() { return typeLampadaire; }
    public FloatProperty puissanceProperty() { return puissance; }
    public ObjectProperty<EtatLampadaire> etatProperty() { return etat; }
    public ObjectProperty<LocalDate> dateInstallationProperty() { return dateInstallation; }
    public IntegerProperty idZoneProperty() { return idZone; }

    // Getters classiques
    public int getIdLamp() { return idLamp.get(); }
    public String getTypeLampadaire() { return typeLampadaire.get(); }
    public float getPuissance() { return puissance.get(); }
    public EtatLampadaire getEtat() { return etat.get(); }
    public LocalDate getDateInstallation() { return dateInstallation.get(); }
    public int getIdZone() { return idZone.get(); }

    // Setters
    public void setIdLamp(int id_lamp) { this.idLamp.set(id_lamp); }
    public void setTypeLampadaire(String typeLampadaire) { this.typeLampadaire.set(typeLampadaire); }
    public void setPuissance(float puissance) { this.puissance.set(puissance); }
    public void setEtat(EtatLampadaire etat) { this.etat.set(etat); }
    public void setDateInstallation(LocalDate dateInstallation) { this.dateInstallation.set(dateInstallation); }
    public void setIdZone(int id_zone) { this.idZone.set(id_zone); }
>>>>>>> origin/MedRayenSansa/GestionInfrastructure

    @Override
    public String toString() {
        return "Lampadaire{" +
<<<<<<< HEAD
                "id_lamp=" + id_lamp +
                ", typeLampadaire='" + typeLampadaire + '\'' +
                ", puissance=" + puissance +
                ", etat=" + etat +
                ", dateInstallation=" + dateInstallation +
                ", id_zone=" + id_zone +
                '}';
    }
}
=======
                "id_lamp=" + idLamp.get() +
                ", typeLampadaire='" + typeLampadaire.get() + '\'' +
                ", puissance=" + puissance.get() +
                ", etat=" + etat.get() +
                ", dateInstallation=" + dateInstallation.get() +
                ", id_zone=" + idZone.get() +
                '}';
    }
}
>>>>>>> origin/MedRayenSansa/GestionInfrastructure
