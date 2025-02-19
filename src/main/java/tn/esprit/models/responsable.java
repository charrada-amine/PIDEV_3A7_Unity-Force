package tn.esprit.models;

import java.util.List;
import java.util.Date;

public class responsable extends utilisateur {
    private int id_responsable;  // Correspond à id_utilisateur
    private List<String> modules;  // Liste des modules

    // Constructeur avec le rôle en tant que String statique
    public responsable(String nom, String prenom, String email, String motdepasse, Date dateInscription, List<String> modules) {
        super(nom, prenom, email, motdepasse, Role.responsable, dateInscription);  // Utilisation directe du rôle "responsable"
        this.id_responsable = id_utilisateur;  // id_responsable = id_utilisateur
        this.modules = modules;
    }

    // Getter et Setter pour modules
    public List<String> getModules() {
        return modules;
    }

    public void setModules(List<String> modules) {
        this.modules = modules;
    }

    @Override
    public String toString() {
        return super.toString() + " | Modules: " + String.join(", ", modules);
    }
}

