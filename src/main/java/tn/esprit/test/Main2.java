package tn.esprit.test;

import tn.esprit.models.Source;
import tn.esprit.services.ServiceSource;
import tn.esprit.Enumerations.EnumType;
import tn.esprit.Enumerations.EnumEtat;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

public class Main2 {
    public static void main(String[] args) {
        ServiceSource serviceSource = new ServiceSource();
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\n📌 Menu de gestion des sources énergétiques :");
            System.out.println("1️⃣ Ajouter une nouvelle source");
            System.out.println("2️⃣ Afficher toutes les sources");
            System.out.println("3️⃣ Modifier une source");
            System.out.println("4️⃣ Supprimer une source");
            System.out.println("5️⃣ Quitter");
            System.out.print("👉 Faites votre choix : ");

            int choix = lireInt(scanner);

            switch (choix) {
                case 1:
                    ajouterSource(serviceSource, scanner);
                    break;
                case 2:
                    afficherSources(serviceSource);
                    break;
                case 3:
                    modifierSource(serviceSource, scanner);
                    break;
                case 4:
                    supprimerSource(serviceSource, scanner);
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

    // 📌 Ajouter une nouvelle source
    private static void ajouterSource(ServiceSource serviceSource, Scanner scanner) {
        System.out.print("Type de source (ELECTRICITE, SOLAIRE, BATTERIE) : ");
        String typeStr = scanner.nextLine().toUpperCase();

        EnumType type = getEnumFromString(typeStr);
        if (type == null) {
            return;
        }

        System.out.print("Capacité (kW) : ");
        float capacite = lireFloat(scanner);

        System.out.print("Rendement (%) : ");
        float rendement = lireFloat(scanner);

        // Valider l'entrée de l'état
        EnumEtat etat = null;
        while (etat == null) {
            System.out.print("État (ACTIF, Panne, Maintenance) : ");
            String etatStr = scanner.nextLine().toUpperCase();

            // Vérification avec EnumEtat
            try {
                etat = EnumEtat.valueOf(etatStr);
            } catch (IllegalArgumentException e) {
                System.out.println("⚠️ Valeur invalide pour l'état. Veuillez entrer ACTIF, Panne ou Maintenance.");
            }
        }

        LocalDate dateInstallation = LocalDate.now();

        // Création de l'objet Source avec les valeurs saisies
        Source source = new Source(0, type, capacite, rendement, etat, dateInstallation);
        serviceSource.add(source);
        System.out.println("✅ Source ajoutée avec succès !");
    }

    // 📌 Afficher toutes les sources
    private static void afficherSources(ServiceSource serviceSource) {
        List<Source> sources = serviceSource.getAll();

        if (sources.isEmpty()) {
            System.out.println("📭 Aucune source trouvée.");
            return;
        }

        System.out.println("\n📋 Liste des sources :");
        for (Source source : sources) {
            System.out.println("🔹 ID: " + source.getIdSource() +
                    ", Type: " + source.getType() +
                    ", Capacité: " + source.getCapacite() + " kW" +
                    ", Rendement: " + source.getRendement() + " %" +
                    ", État: " + source.getEtat() +
                    ", Date d'installation: " + source.getDateInstallation());
        }
    }

    // 📌 Modifier une source
    private static void modifierSource(ServiceSource serviceSource, Scanner scanner) {
        System.out.print("Entrez l'ID de la source à modifier : ");
        int id = lireInt(scanner);

        System.out.print("Nouveau type : ");
        String typeStr = scanner.nextLine().toUpperCase();
        EnumType type = getEnumFromString(typeStr);
        if (type == null) {
            return;
        }

        System.out.print("Nouvelle capacité (kW) : ");
        float capacite = lireFloat(scanner);

        System.out.print("Nouveau rendement (%) : ");
        float rendement = lireFloat(scanner);

        // Modifier l'état
        EnumEtat etat = null;
        while (etat == null) {
            System.out.print("Nouvel état (ACTIF, Panne, Maintenance) : ");
            String etatStr = scanner.nextLine().toUpperCase();

            try {
                etat = EnumEtat.valueOf(etatStr);
            } catch (IllegalArgumentException e) {
                System.out.println("⚠️ Valeur invalide pour l'état. Veuillez entrer ACTIF, Panne ou Maintenance.");
            }
        }

        LocalDate dateInstallation = LocalDate.now();

        // Création de l'objet Source avec les valeurs saisies
        Source source = new Source(id, type, capacite, rendement, etat, dateInstallation);
        serviceSource.update(source);
        System.out.println("✅ Source mise à jour avec succès !");
    }

    // 📌 Supprimer une source
    private static void supprimerSource(ServiceSource serviceSource, Scanner scanner) {
        System.out.print("Entrez l'ID de la source à supprimer : ");
        int id = lireInt(scanner);

        serviceSource.delete(id);
        System.out.println("🗑️ Source supprimée avec succès !");
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

    // 📌 Fonction utilitaire pour gérer l'énumération du type
    private static EnumType getEnumFromString(String typeStr) {
        try {
            return EnumType.valueOf(typeStr);
        } catch (IllegalArgumentException e) {
            System.out.println("⚠️ Type invalide. Veuillez entrer ELECTRICITE, SOLAIRE ou BATTERIE.");
            return null;
        }
    }
}
