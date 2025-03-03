package tn.esprit.models;

public enum EtatLampadaire {
    ACTIF,
    EN_PANNE,
    EN_MAINTENANCE;

    @Override
    public String toString() {
        return this.name().replace("_", " ").toLowerCase();
    }
}