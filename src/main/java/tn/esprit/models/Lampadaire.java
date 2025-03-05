package tn.esprit.models;

import javafx.beans.property.*;

import java.time.LocalDate;

public class Lampadaire {
    private final IntegerProperty idLamp = new SimpleIntegerProperty();
    private final StringProperty typeLampadaire = new SimpleStringProperty();
    private final FloatProperty puissance = new SimpleFloatProperty();
    private final ObjectProperty<EtatLampadaire> etat = new SimpleObjectProperty<>();
    private final ObjectProperty<LocalDate> dateInstallation = new SimpleObjectProperty<>();
    private final IntegerProperty idZone = new SimpleIntegerProperty();
    private final DoubleProperty latitude = new SimpleDoubleProperty();
    private final DoubleProperty longitude = new SimpleDoubleProperty();
    private final IntegerProperty idCamera = new SimpleIntegerProperty(); // Ajout pour la clé étrangère

    public Lampadaire() {}

    public Lampadaire(int id_lamp, String typeLampadaire, float puissance,
                      EtatLampadaire etat, LocalDate dateInstallation, int id_zone,
                      double latitude, double longitude, int idCamera) {
        this.idLamp.set(id_lamp);
        this.typeLampadaire.set(typeLampadaire);
        this.puissance.set(puissance);
        this.etat.set(etat);
        this.dateInstallation.set(dateInstallation);
        this.idZone.set(id_zone);
        this.latitude.set(latitude);
        this.longitude.set(longitude);
        this.idCamera.set(idCamera); // Ajout
    }

    // Getters et setters pour idCamera
    public int getIdCamera() { return idCamera.get(); }
    public void setIdCamera(int idCamera) { this.idCamera.set(idCamera); }
    public IntegerProperty idCameraProperty() { return idCamera; }

    // Getters et setters existants inchangés
    public IntegerProperty idLampProperty() { return idLamp; }
    public StringProperty typeLampadaireProperty() { return typeLampadaire; }
    public FloatProperty puissanceProperty() { return puissance; }
    public ObjectProperty<EtatLampadaire> etatProperty() { return etat; }
    public ObjectProperty<LocalDate> dateInstallationProperty() { return dateInstallation; }
    public IntegerProperty idZoneProperty() { return idZone; }
    public DoubleProperty latitudeProperty() { return latitude; }
    public DoubleProperty longitudeProperty() { return longitude; }

    public int getIdLamp() { return idLamp.get(); }
    public String getTypeLampadaire() { return typeLampadaire.get(); }
    public float getPuissance() { return puissance.get(); }
    public EtatLampadaire getEtat() { return etat.get(); }
    public LocalDate getDateInstallation() { return dateInstallation.get(); }
    public int getIdZone() { return idZone.get(); }
    public double getLatitude() { return latitude.get(); }
    public double getLongitude() { return longitude.get(); }

    public void setIdLamp(int id_lamp) { this.idLamp.set(id_lamp); }
    public void setTypeLampadaire(String typeLampadaire) { this.typeLampadaire.set(typeLampadaire); }
    public void setPuissance(float puissance) { this.puissance.set(puissance); }
    public void setEtat(EtatLampadaire etat) { this.etat.set(etat); }
    public void setDateInstallation(LocalDate dateInstallation) { this.dateInstallation.set(dateInstallation); }
    public void setIdZone(int id_zone) { this.idZone.set(id_zone); }
    public void setLatitude(double latitude) { this.latitude.set(latitude); }
    public void setLongitude(double longitude) { this.longitude.set(longitude); }

    @Override
    public String toString() {
        return "Lampadaire{" +
                "id_lamp=" + idLamp.get() +
                ", typeLampadaire='" + typeLampadaire.get() + '\'' +
                ", puissance=" + puissance.get() +
                ", etat=" + etat.get() +
                ", dateInstallation=" + dateInstallation.get() +
                ", id_zone=" + idZone.get() +
                ", latitude=" + latitude.get() +
                ", longitude=" + longitude.get() +
                ", id_camera=" + idCamera.get() +
                '}';
    }


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
}