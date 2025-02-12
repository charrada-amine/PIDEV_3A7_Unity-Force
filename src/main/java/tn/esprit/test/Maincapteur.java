package tn.esprit.test;

import tn.esprit.models.Capteur;
import tn.esprit.services.ServiceCapteur;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

public class Maincapteur {

    public static void main(String[] args) {
        ServiceCapteur serviceCapteur = new ServiceCapteur();
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\n📌 Menu de gestion des capteurs :");
            System.out.println("1️⃣ Ajouter un nouveau capteur");
            System.out.println("2️⃣ Afficher tous les capteurs");
            System.out.println("3️⃣ Modifier un capteur");
            System.out.println("4️⃣ Supprimer un capteur");
            System.out.println("5️⃣ Quitter");
            System.out.print("👉 Faites votre choix : ");

            int choix = lireInt(scanner);

            switch (choix) {
                case 1:
                    ajouterCapteur(serviceCapteur, scanner);
                    break;
                case 2:
                    afficherCapteurs(serviceCapteur);
                    break;
                case 3:
                    modifierCapteur(serviceCapteur, scanner);
                    break;
                case 4:
                    supprimerCapteur(serviceCapteur, scanner);
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

    // 📌 Ajouter un nouveau capteur
    private static void ajouterCapteur(ServiceCapteur serviceCapteur, Scanner scanner) {
        System.out.print("Type de capteur (MOUVEMENT, TEMPERATURE, LUMINOSITE, CONSOMMATION_ENERGIE) : ");
        String typeInput = scanner.nextLine().toUpperCase(); // Convertir en majuscules pour correspondre à l'énumération

        System.out.print("État (ACTIF, INACTIF, EN_PANNE) : ");
        String etatInput = scanner.nextLine().toUpperCase(); // Convertir en majuscules pour correspondre à l'énumération

        System.out.print("ID du lampadaire : ");
        int lampadaireId = lireInt(scanner);

        LocalDate dateinstallation = LocalDate.now(); // Date d'installation par défaut

        try {
            Capteur.TypeCapteur type = Capteur.TypeCapteur.valueOf(typeInput); // Conversion en énumération
            Capteur.EtatCapteur etat = Capteur.EtatCapteur.valueOf(etatInput); // Conversion en énumération
            Capteur capteur = new Capteur(0, type, dateinstallation, etat, lampadaireId);
            serviceCapteur.add(capteur);
            System.out.println("✅ Capteur ajouté avec succès !");
        } catch (IllegalArgumentException e) {
            System.out.println("⚠️ Type ou état invalide. Veuillez entrer des valeurs valides.");
        }
    }

    // 📌 Afficher tous les capteurs
    private static void afficherCapteurs(ServiceCapteur serviceCapteur) {
        List<Capteur> capteurs = serviceCapteur.getAll();

        if (capteurs.isEmpty()) {
            System.out.println("📭 Aucun capteur trouvé.");
            return;
        }

        System.out.println("\n📋 Liste des capteurs :");
        for (Capteur capteur : capteurs) {
            System.out.println("🔹 ID: " + capteur.getId() +
                    ", Type: " + capteur.getType() +
                    ", Date d'installation: " + capteur.getDateinstallation() +
                    ", État: " + capteur.getEtat() +
                    ", ID Lampadaire: " + capteur.getLampadaireId());
        }
    }

    // 📌 Modifier un capteur
    private static void modifierCapteur(ServiceCapteur serviceCapteur, Scanner scanner) {
        System.out.print("Entrez l'ID du capteur à modifier : ");
        int id = lireInt(scanner);

        System.out.print("Nouveau type (MOUVEMENT, TEMPERATURE, LUMINOSITE, CONSOMMATION_ENERGIE) : ");
        String typeInput = scanner.nextLine().toUpperCase();

        System.out.print("Nouvel état (ACTIF, INACTIF, EN_PANNE) : ");
        String etatInput = scanner.nextLine().toUpperCase();

        System.out.print("Nouvel ID de lampadaire : ");
        int lampadaireId = lireInt(scanner);

        LocalDate dateinstallation = LocalDate.now(); // Date d'installation par défaut

        try {
            Capteur.TypeCapteur type = Capteur.TypeCapteur.valueOf(typeInput);
            Capteur.EtatCapteur etat = Capteur.EtatCapteur.valueOf(etatInput);
            Capteur capteur = new Capteur(id, type, dateinstallation, etat, lampadaireId);
            serviceCapteur.update(capteur);
            System.out.println("✅ Capteur mis à jour avec succès !");
        } catch (IllegalArgumentException e) {
            System.out.println("⚠️ Type ou état invalide. Veuillez entrer des valeurs valides.");
        }
    }

    // 📌 Supprimer un capteur
    private static void supprimerCapteur(ServiceCapteur serviceCapteur, Scanner scanner) {
        System.out.print("Entrez l'ID du capteur à supprimer : ");
        int id = lireInt(scanner);

        serviceCapteur.delete(id);
        System.out.println("🗑️ Capteur supprimé avec succès !");
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
}