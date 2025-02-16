package tn.esprit.test;

import tn.esprit.models.Intervention;
import tn.esprit.models.Intervention.TypeIntervention;
import tn.esprit.models.Reclamation;
import tn.esprit.services.ServiceIntervention;
import tn.esprit.services.ServiceReclamation;

import java.sql.Date;
import java.sql.Time;
import java.util.Scanner;

public class Main {

    private static ServiceReclamation serviceReclamation = new ServiceReclamation();
    private static ServiceIntervention serviceIntervention = new ServiceIntervention();
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        boolean quit = false;
        while (!quit) {
            System.out.println("\n=== Menu Principal ===");
            System.out.println("1. Gérer les Réclamations");
            System.out.println("2. Gérer les Interventions");
            System.out.println("3. Quitter");
            System.out.print("Choisissez une option: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    manageReclamations();
                    break;
                case 2:
                    manageInterventions();
                    break;
                case 3:
                    quit = true;
                    System.out.println("Au revoir!");
                    break;
                default:
                    System.out.println("Option invalide. Veuillez réessayer.");
            }
        }
    }

    private static void manageReclamations() {
        boolean back = false;
        while (!back) {
            System.out.println("\n=== Gestion des Réclamations ===");
            System.out.println("1. Ajouter une Réclamation");
            System.out.println("2. Afficher toutes les Réclamations");
            System.out.println("3. Mettre à jour une Réclamation");
            System.out.println("4. Supprimer une Réclamation");
            System.out.println("5. Retour au Menu Principal");
            System.out.print("Choisissez une option: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    addReclamation();
                    break;
                case 2:
                    displayAllReclamations();
                    break;
                case 3:
                    updateReclamation();
                    break;
                case 4:
                    deleteReclamation();
                    break;
                case 5:
                    back = true;
                    break;
                default:
                    System.out.println("Option invalide. Veuillez réessayer.");
            }
        }
    }

    private static void addReclamation() {
        System.out.print("Description: ");
        String description = scanner.nextLine();
        System.out.print("ID du Lampadaire: ");
        int lampadaireId = scanner.nextInt();
        System.out.print("ID du Citoyen: ");
        int citoyenId = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        Reclamation reclamation = new Reclamation(
                description,
                new Date(System.currentTimeMillis()),
                new Time(System.currentTimeMillis()),
                "Pending",
                lampadaireId,
                citoyenId
        );
        serviceReclamation.add(reclamation);
        System.out.println("✅ Réclamation ajoutée avec succès!");
    }

    private static void displayAllReclamations() {
        System.out.println("\n=== Liste des Réclamations ===");
        for (Reclamation r : serviceReclamation.getAll()) {
            System.out.println(r);
        }
    }

    private static void updateReclamation() {
        System.out.print("ID de la Réclamation à mettre à jour: ");
        int id = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        System.out.print("Nouvelle Description: ");
        String description = scanner.nextLine();
        System.out.print("Nouveau Statut: ");
        String statut = scanner.nextLine();
        System.out.print("Nouvel ID du Lampadaire: ");
        int lampadaireId = scanner.nextInt();
        System.out.print("Nouvel ID du Citoyen: ");
        int citoyenId = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        Reclamation reclamation = new Reclamation(
                description,
                new Date(System.currentTimeMillis()),
                new Time(System.currentTimeMillis()),
                statut,
                lampadaireId,
                citoyenId
        );
        reclamation.setId(id);
        serviceReclamation.update(reclamation);
        System.out.println("✅ Réclamation mise à jour avec succès!");
    }

    private static void deleteReclamation() {
        System.out.print("ID de la Réclamation à supprimer: ");
        int id = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        Reclamation reclamation = new Reclamation();
        reclamation.setId(id);
        serviceReclamation.delete(reclamation);
        System.out.println("✅ Réclamation supprimée avec succès!");
    }

    private static void manageInterventions() {
        boolean back = false;
        while (!back) {
            System.out.println("\n=== Gestion des Interventions ===");
            System.out.println("1. Ajouter une Intervention");
            System.out.println("2. Afficher toutes les Interventions");
            System.out.println("3. Mettre à jour une Intervention");
            System.out.println("4. Supprimer une Intervention");
            System.out.println("5. Retour au Menu Principal");
            System.out.print("Choisissez une option: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    addIntervention();
                    break;
                case 2:
                    displayAllInterventions();
                    break;
                case 3:
                    updateIntervention();
                    break;
                case 4:
                    deleteIntervention();
                    break;
                case 5:
                    back = true;
                    break;
                default:
                    System.out.println("Option invalide. Veuillez réessayer.");
            }
        }
    }

    private static void addIntervention() {
        System.out.print("Type d'Intervention (REPARATION, REMPLACEMENT, MAINTENANCE): ");
        String type = scanner.nextLine().toUpperCase();
        System.out.print("Description: ");
        String description = scanner.nextLine();
        System.out.print("État: ");
        String etat = scanner.nextLine();
        System.out.print("ID du Lampadaire: ");
        int lampadaireId = scanner.nextInt();
        System.out.print("ID du Technicien: ");
        int technicienId = scanner.nextInt();
        System.out.print("ID de la Réclamation: ");
        int reclamationId = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        Intervention intervention = new Intervention(
                TypeIntervention.valueOf(type),
                description,
                etat,
                new Date(System.currentTimeMillis()),
                new Time(System.currentTimeMillis()),
                lampadaireId,
                technicienId,
                reclamationId
        );
        serviceIntervention.add(intervention);
        System.out.println("✅ Intervention ajoutée avec succès!");
    }

    private static void displayAllInterventions() {
        System.out.println("\n=== Liste des Interventions ===");
        for (Intervention i : serviceIntervention.getAll()) {
            System.out.println(i);
        }
    }

    private static void updateIntervention() {
        System.out.print("ID de l'Intervention à mettre à jour: ");
        int id = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        System.out.print("Nouveau Type d'Intervention (REPARATION, REMPLACEMENT, MAINTENANCE): ");
        String type = scanner.nextLine().toUpperCase();
        System.out.print("Nouvelle Description: ");
        String description = scanner.nextLine();
        System.out.print("Nouvel État: ");
        String etat = scanner.nextLine();
        System.out.print("Nouvel ID du Lampadaire: ");
        int lampadaireId = scanner.nextInt();
        System.out.print("Nouvel ID du Technicien: ");
        int technicienId = scanner.nextInt();
        System.out.print("Nouvel ID de la Réclamation: ");
        int reclamationId = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        Intervention intervention = new Intervention(
                TypeIntervention.valueOf(type),
                description,
                etat,
                new Date(System.currentTimeMillis()),
                new Time(System.currentTimeMillis()),
                lampadaireId,
                technicienId,
                reclamationId
        );
        intervention.setId(id);
        serviceIntervention.update(intervention);
        System.out.println("✅ Intervention mise à jour avec succès!");
    }

    private static void deleteIntervention() {
        System.out.print("ID de l'Intervention à supprimer: ");
        int id = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        Intervention intervention = new Intervention();
        intervention.setId(id);
        serviceIntervention.delete(intervention);
        System.out.println("✅ Intervention supprimée avec succès!");
    }
}