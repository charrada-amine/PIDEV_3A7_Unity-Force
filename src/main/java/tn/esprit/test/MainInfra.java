package tn.esprit.test;

import tn.esprit.models.Lampadaire;
import tn.esprit.models.Lampadaire.EtatLampadaire;
import tn.esprit.models.Zone;
import tn.esprit.services.ServiceLampadaire;
import tn.esprit.services.ServiceZone;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

public class MainInfra {

    public static void main(String[] args) {
        ServiceLampadaire serviceLamp = new ServiceLampadaire();
        ServiceZone serviceZone = new ServiceZone();
        Scanner scanner = new Scanner(System.in).useLocale(Locale.US);
        int mainChoice;

        do {
            System.out.println("\n=== Menu Principal ===");
            System.out.println("1. Gérer les zones");
            System.out.println("2. Gérer les lampadaires");
            System.out.println("3. Quitter");
            System.out.print("Votre choix : ");
            mainChoice = scanner.nextInt();
            scanner.nextLine();

            switch (mainChoice) {
                case 1 -> manageZones(serviceZone, scanner);
                case 2 -> manageLampadaires(serviceLamp, scanner);
                case 3 -> System.out.println("Au revoir !");
                default -> System.out.println("Choix invalide !");
            }
        } while (mainChoice != 3);
        scanner.close();
    }

    // ========================================== Gestion des Zones ==========================================
    private static void manageZones(ServiceZone service, Scanner scanner) {
        int subChoice;
        do {
            System.out.println("\n=== Gestion des Zones ===");
            System.out.println("1. Ajouter une zone");
            System.out.println("2. Modifier une zone");
            System.out.println("3. Supprimer une zone");
            System.out.println("4. Afficher toutes les zones");
            System.out.println("5. Retour au menu principal");
            System.out.print("Votre choix : ");
            subChoice = scanner.nextInt();
            scanner.nextLine();

            switch (subChoice) {
                case 1 -> ajouterZone(service, scanner);
                case 2 -> modifierZone(service, scanner);
                case 3 -> supprimerZone(service, scanner);
                case 4 -> afficherListeZones(service.getAll());
                case 5 -> System.out.println("Retour au menu principal...");
                default -> System.out.println("Choix invalide !");
            }
        } while (subChoice != 5);
    }

    private static void ajouterZone(ServiceZone service, Scanner scanner) {
        System.out.println("\n--- Ajout d'une zone ---");
        System.out.print("Nom : ");
        String nom = scanner.nextLine();

        System.out.print("Description : ");
        String description = scanner.nextLine();

        System.out.print("Surface : ");
        float surface = scanner.nextFloat();
        scanner.nextLine();

        System.out.print("Nombre de lampadaires : ");
        int nbLamp = scanner.nextInt();
        scanner.nextLine();

        System.out.print("Nombre de citoyens : ");
        int nbCitoyens = scanner.nextInt();
        scanner.nextLine();

        Zone zone = new Zone();
        zone.setNom(nom);
        zone.setDescription(description);
        zone.setSurface(surface);
        zone.setNombreLampadaires(nbLamp);
        zone.setNombreCitoyens(nbCitoyens);

        service.add(zone);
        System.out.println("✅ Zone ajoutée avec succès !");
        afficherListeZones(service.getAll());
    }

    private static void modifierZone(ServiceZone service, Scanner scanner) {
        System.out.println("\n--- Modification d'une zone ---");
        System.out.print("ID de la zone à modifier : ");
        int idZone = scanner.nextInt();
        scanner.nextLine();

        Zone existing = service.getById(idZone);
        if (existing == null) {
            System.out.println("❌ Aucune zone trouvée avec l'ID " + idZone);
            return;
        }

        System.out.println("Zone actuelle : " + existing);

        System.out.print("Nouveau nom (laissez vide pour ne pas changer) : ");
        String nom = scanner.nextLine();
        if (!nom.isEmpty()) existing.setNom(nom);

        System.out.print("Nouvelle description (laissez vide) : ");
        String description = scanner.nextLine();
        if (!description.isEmpty()) existing.setDescription(description);

        System.out.print("Nouvelle surface (laissez vide) : ");
        String surfaceStr = scanner.nextLine();
        if (!surfaceStr.isEmpty()) existing.setSurface(Float.parseFloat(surfaceStr));

        System.out.print("Nouveau nombre de lampadaires (laissez vide) : ");
        String nbLampStr = scanner.nextLine();
        if (!nbLampStr.isEmpty()) existing.setNombreLampadaires(Integer.parseInt(nbLampStr));

        System.out.print("Nouveau nombre de citoyens (laissez vide) : ");
        String nbCitoyensStr = scanner.nextLine();
        if (!nbCitoyensStr.isEmpty()) existing.setNombreCitoyens(Integer.parseInt(nbCitoyensStr));

        service.update(existing);
        System.out.println("✅ Zone mise à jour !");
        afficherListeZones(service.getAll());
    }

    private static void supprimerZone(ServiceZone service, Scanner scanner) {
        System.out.println("\n--- Suppression d'une zone ---");
        System.out.print("ID de la zone à supprimer : ");
        int idZone = scanner.nextInt();
        scanner.nextLine();

        Zone existing = service.getById(idZone);
        if (existing == null) {
            System.out.println("❌ Aucune zone trouvée avec l'ID " + idZone);
            return;
        }

        service.delete(existing);
        System.out.println("✅ Zone supprimée !");
        afficherListeZones(service.getAll());
    }

    private static void afficherListeZones(List<Zone> zones) {
        if (zones.isEmpty()) {
            System.out.println("⚠️ Aucune zone trouvée.");
        } else {
            System.out.println("\n=== Liste des zones ===");
            zones.forEach(System.out::println);
        }
    }

    // ========================================== Gestion des Lampadaires ==========================================
    private static void manageLampadaires(ServiceLampadaire service, Scanner scanner) {
        int subChoice;
        do {
            System.out.println("\n=== Gestion des Lampadaires ===");
            System.out.println("1. Ajouter un lampadaire");
            System.out.println("2. Modifier un lampadaire");
            System.out.println("3. Supprimer un lampadaire");
            System.out.println("4. Afficher tous les lampadaires");
            System.out.println("5. Retour au menu principal");
            System.out.print("Votre choix : ");
            subChoice = scanner.nextInt();
            scanner.nextLine();

            switch (subChoice) {
                case 1 -> ajouterLampadaire(service, scanner);
                case 2 -> modifierLampadaire(service, scanner);
                case 3 -> supprimerLampadaire(service, scanner);
                case 4 -> afficherListeLampadaires(service.getAll());
                case 5 -> System.out.println("Retour au menu principal...");
                default -> System.out.println("Choix invalide !");
            }
        } while (subChoice != 5);
    }

    private static void ajouterLampadaire(ServiceLampadaire service, Scanner scanner) {
        System.out.println("\n--- Ajout d'un lampadaire ---");
        System.out.print("Type du lampadaire : ");
        String type = scanner.nextLine();

        System.out.print("Puissance : ");
        float puissance = scanner.nextFloat();
        scanner.nextLine();

        EtatLampadaire etat = saisirEtat(scanner);

        System.out.print("Date d'installation (AAAA-MM-JJ) [vide si null] : ");
        String dateStr = scanner.nextLine();
        LocalDate dateInstallation = dateStr.isEmpty() ? null : LocalDate.parse(dateStr);

        System.out.print("ID de la zone : ");
        int idZone = scanner.nextInt();
        scanner.nextLine();

        Lampadaire lampadaire = new Lampadaire(0, type, puissance, etat, dateInstallation, idZone);
        service.add(lampadaire);

        if (lampadaire.getIdLamp() > 0) {
            System.out.println("✅ Lampadaire ajouté avec succès !");
        } else {
            System.out.println("❌ Échec de l'ajout. La zone spécifiée n'existe pas.");
        }
        afficherListeLampadaires(service.getAll());
    }

    private static void modifierLampadaire(ServiceLampadaire service, Scanner scanner) {
        System.out.println("\n--- Modification d'un lampadaire ---");
        System.out.print("ID du lampadaire à modifier : ");
        int idLamp = scanner.nextInt();
        scanner.nextLine();

        Lampadaire existing = service.getById(idLamp);
        if (existing == null) {
            System.out.println("❌ Aucun lampadaire trouvé avec l'ID " + idLamp);
            return;
        }

        System.out.println("Lampadaire actuel : " + existing);

        System.out.print("Nouveau type (laissez vide pour ne pas changer) : ");
        String type = scanner.nextLine();
        if (!type.isEmpty()) existing.setTypeLampadaire(type);

        System.out.print("Nouvelle puissance (laissez vide pour ne pas changer) : ");
        String puissanceStr = scanner.nextLine();
        if (!puissanceStr.isEmpty()) existing.setPuissance(Float.parseFloat(puissanceStr));

        System.out.println("Nouvel état (" + Arrays.toString(EtatLampadaire.values()) + ") [laissez vide] : ");
        String etatStr = scanner.nextLine().toUpperCase().replace(" ", "_");
        if (!etatStr.isEmpty()) {
            try {
                existing.setEtat(EtatLampadaire.valueOf(etatStr));
            } catch (IllegalArgumentException e) {
                System.out.println("❌ État invalide ! Les valeurs possibles sont : " + Arrays.toString(EtatLampadaire.values()));
            }
        }

        System.out.print("Nouvelle date (AAAA-MM-JJ) [laissez vide] : ");
        String dateStr = scanner.nextLine();
        if (!dateStr.isEmpty()) existing.setDateInstallation(LocalDate.parse(dateStr));

        System.out.print("Nouvel ID de zone [laissez vide] : ");
        String idZoneStr = scanner.nextLine();
        if (!idZoneStr.isEmpty()) existing.setIdZone(Integer.parseInt(idZoneStr));

        service.update(existing);
        System.out.println("✅ Lampadaire mis à jour !");
        afficherListeLampadaires(service.getAll());
    }

    private static void supprimerLampadaire(ServiceLampadaire service, Scanner scanner) {
        System.out.println("\n--- Suppression d'un lampadaire ---");
        System.out.print("ID du lampadaire à supprimer : ");
        int idLamp = scanner.nextInt();
        scanner.nextLine();

        Lampadaire existing = service.getById(idLamp);
        if (existing == null) {
            System.out.println("❌ Aucun lampadaire trouvé avec l'ID " + idLamp);
            return;
        }

        service.delete(existing);
        System.out.println("✅ Lampadaire supprimé !");
        afficherListeLampadaires(service.getAll());
    }

    private static EtatLampadaire saisirEtat(Scanner scanner) {
        while (true) {
            try {
                System.out.println("États disponibles : " + Arrays.toString(EtatLampadaire.values()));
                System.out.print("État : ");
                String input = scanner.nextLine().toUpperCase().replace(" ", "_");
                return EtatLampadaire.valueOf(input);
            } catch (IllegalArgumentException e) {
                System.out.println("❌ État invalide ! Réessayez.");
            }
        }
    }

    private static void afficherListeLampadaires(List<Lampadaire> lampadaires) {
        if (lampadaires.isEmpty()) {
            System.out.println("⚠️ Aucun lampadaire trouvé.");
        } else {
            System.out.println("\n=== Liste des lampadaires ===");
            lampadaires.forEach(System.out::println);
        }
    }
}