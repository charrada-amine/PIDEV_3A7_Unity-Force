package tn.esprit.test;

import tn.esprit.models.Intervention;
import tn.esprit.models.Intervention.TypeIntervention;
import tn.esprit.models.Reclamation;
import tn.esprit.services.ServiceIntervention;
import tn.esprit.services.ServiceReclamation;

import java.sql.Date;
import java.sql.Time;

public class Main {

    public static void main(String[] args) {
        ServiceReclamation serviceReclamation = new ServiceReclamation();
        ServiceIntervention serviceIntervention = new ServiceIntervention();

        // Ajouter une réclamation
        Reclamation reclamation = new Reclamation(
                "Broken lamp post",
                new Date(System.currentTimeMillis()),
                new Time(System.currentTimeMillis()),
                "Pending",
                1,
                1
        );
        serviceReclamation.add(reclamation);
        System.out.println("✅ Reclamation added successfully!");

        // Ajouter une intervention liée à la réclamation
        Intervention intervention = new Intervention(
                TypeIntervention.REPARATION,
                "Fix broken light",
                "In Progress",
                new Date(System.currentTimeMillis()),
                new Time(System.currentTimeMillis()),
                1,
                1,
                reclamation.getId() // Lier l'intervention à la réclamation
        );
        serviceIntervention.add(intervention);
        System.out.println("✅ Intervention added successfully!");

        // Lister toutes les interventions
        System.out.println("Listing all interventions:");
        for (Intervention i : serviceIntervention.getAll()) {
            System.out.println(i);
        }
    }
}