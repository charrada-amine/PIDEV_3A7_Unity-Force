package tn.esprit.test;

import tn.esprit.models.Lampadaire;
import tn.esprit.models.Lampadaire.EtatLampadaire;
import tn.esprit.services.ServiceLampadaire;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        ServiceLampadaire service = new ServiceLampadaire();
        Scanner scanner = new Scanner(System.in).useLocale(Locale.US);
        int choice;

        do {
            System.out.println("\n=== Menu Principal ===");
            System.out.println("1. Ajouter un lampadaire");
            System.out.println("2. Modifier un lampadaire");
            System.out.println("3. Supprimer un lampadaire");
            System.out.println("4. Afficher tous les lampadaires");
            System.out.println("5. Quitter");
            System.out.print("Votre choix : ");
            choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1 -> ajouterLampadaire(service, scanner);
                case 2 -> modifierLampadaire(service, scanner);
                case 3 -> supprimerLampadaire(service, scanner);
                case 4 -> afficherListe(service.getAll());
                case 5 -> System.out.println("Au revoir !");
                default -> System.out.println("Choix invalide !");
            }
        } while (choice != 5);
        scanner.close();
    }

    private static void ajouterLampadaire(ServiceLampadaire service, Scanner scanner) {
        System.out.println("\n--- Ajout d'un lampadaire ---");

        System.out.print("Type du lampadaire : ");
        String type = scanner.nextLine();

        System.out.print("Puissance : ");
        float puissance = scanner.nextFloat();
        scanner.nextLine();

        // Gestion de l'état avec l'énumération
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
        afficherListe(service.getAll());
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

        // Modification de l'état
        System.out.println("Nouvel état (" + Arrays.toString(EtatLampadaire.values()) + ") [laissez vide] : ");
        String etatStr = scanner.nextLine().toUpperCase().replace(" ", "_");
        if (!etatStr.isEmpty()) {
            try {
                existing.setEtat(EtatLampadaire.valueOf(etatStr));
            } catch (IllegalArgumentException e) {
                System.out.println("❌ État invalide ! Les valeurs possibles sont : "
                        + Arrays.toString(EtatLampadaire.values()));
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
        afficherListe(service.getAll());
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

    // Les méthodes supprimerLampadaire() et afficherListe() restent inchangées
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
        afficherListe(service.getAll());
    }

    private static void afficherListe(List<Lampadaire> lampadaires) {
        if (lampadaires.isEmpty()) {
            System.out.println("⚠️ Aucun lampadaire trouvé.");
        } else {
            System.out.println("\n=== Liste des lampadaires ===");
            lampadaires.forEach(System.out::println);
        }
    }
}