package tn.esprit.test;


import tn.esprit.models.DonneeTemperature;
import tn.esprit.services.ServiceTemperature;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Scanner;
public class Maintemperature {

    public static void main(String[] args) {
        ServiceTemperature serviceTemperature = new ServiceTemperature();
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\n📌 Menu de gestion des données de température :");
            System.out.println("1️⃣ Ajouter une nouvelle donnée de température");
            System.out.println("2️⃣ Afficher toutes les données de température");
            System.out.println("3️⃣ Modifier une donnée de température");
            System.out.println("4️⃣ Supprimer une donnée de température");
            System.out.println("5️⃣ Quitter");
            System.out.print("👉 Faites votre choix : ");

            int choix = lireInt(scanner);

            switch (choix) {
                case 1:
                    ajouterTemperature(serviceTemperature, scanner);
                    break;
                case 2:
                    afficherTemperatures(serviceTemperature);
                    break;
                case 3:
                    modifierTemperature(serviceTemperature, scanner);
                    break;
                case 4:
                    supprimerTemperature(serviceTemperature, scanner);
                    break;
                case 5:
                    System.out.println("👋 Programme terminé !");
                    scanner.close();
                    return;
                default:
                    System.out.println("⚠️ Choix invalide, veuillez réessayer.");
            }
        }
    }

    // 📌 Ajouter une nouvelle donnée de température
    private static void ajouterTemperature(ServiceTemperature serviceTemperature, Scanner scanner) {
        System.out.print("Date de collecte (YYYY-MM-DD) : ");
        LocalDate dateCollecte = LocalDate.parse(scanner.nextLine());

        System.out.print("Heure de collecte (HH:MM:SS) : ");
        LocalTime heureCollecte = LocalTime.parse(scanner.nextLine());

        System.out.print("ID du capteur : ");
        int capteurId = lireInt(scanner);

        System.out.print("Valeur de la température : ");
        float valeur = lireFloat(scanner);

        DonneeTemperature donnee = new DonneeTemperature(0, dateCollecte, heureCollecte, capteurId, valeur);
        serviceTemperature.add(donnee);
        System.out.println("✅ Donnée de température ajoutée avec succès !");
    }

    // 📌 Afficher toutes les données de température
    private static void afficherTemperatures(ServiceTemperature serviceTemperature) {
        List<DonneeTemperature> donnees = serviceTemperature.getAll();

        if (donnees.isEmpty()) {
            System.out.println("📭 Aucune donnée de température trouvée.");
            return;
        }

        System.out.println("\n📋 Liste des données de température :");
        for (DonneeTemperature donnee : donnees) {
            System.out.println("🔹 ID: " + donnee.getId() +
                    ", Date de collecte: " + donnee.getDateCollecte() +
                    ", Heure de collecte: " + donnee.getHeureCollecte() +
                    ", ID Capteur: " + donnee.getCapteurId() +
                    ", Valeur: " + donnee.getValeur());
        }
    }

    // 📌 Modifier une donnée de température
    private static void modifierTemperature(ServiceTemperature serviceTemperature, Scanner scanner) {
        System.out.print("Entrez l'ID de la donnée à modifier : ");
        int id = lireInt(scanner);

        System.out.print("Nouvelle date de collecte (YYYY-MM-DD) : ");
        LocalDate dateCollecte = LocalDate.parse(scanner.nextLine());

        System.out.print("Nouvelle heure de collecte (HH:MM:SS) : ");
        LocalTime heureCollecte = LocalTime.parse(scanner.nextLine());

        System.out.print("Nouvel ID de capteur : ");
        int capteurId = lireInt(scanner);

        System.out.print("Nouvelle valeur de température : ");
        float valeur = lireFloat(scanner);

        DonneeTemperature donnee = new DonneeTemperature(id, dateCollecte, heureCollecte, capteurId, valeur);
        serviceTemperature.update(donnee);
        System.out.println("✅ Donnée de température mise à jour avec succès !");
    }

    // 📌 Supprimer une donnée de température
    private static void supprimerTemperature(ServiceTemperature serviceTemperature, Scanner scanner) {
        System.out.print("Entrez l'ID de la donnée à supprimer : ");
        int id = lireInt(scanner);

        serviceTemperature.delete(id);
        System.out.println("🗑️ Donnée de température supprimée avec succès !");
    }

    // 📌 Fonction utilitaire pour lire un entier avec validation
    private static int lireInt(Scanner scanner) {
        while (!scanner.hasNextInt()) {
            System.out.println("⚠️ Veuillez entrer un nombre entier valide !");
            scanner.next();
        }
        int valeur = scanner.nextInt();
        scanner.nextLine(); // Consommer la ligne restante
        return valeur;
    }

    // 📌 Fonction utilitaire pour lire un float avec validation
    private static float lireFloat(Scanner scanner) {
        while (!scanner.hasNextFloat()) {
            System.out.println("⚠️ Veuillez entrer un nombre valide !");
            scanner.next();
        }
        float valeur = scanner.nextFloat();
        scanner.nextLine(); // Consommer la ligne restante
        return valeur;
    }
}
