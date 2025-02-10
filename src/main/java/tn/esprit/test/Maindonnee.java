package tn.esprit.test;

import tn.esprit.models.DonneeTemperature;
import tn.esprit.models.DonneeMouvement;
import tn.esprit.models.DonneeLuminosite;
import tn.esprit.models.DonneeConsommation;
import tn.esprit.services.ServiceTemperature;
import tn.esprit.services.ServiceMouvement;
import tn.esprit.services.ServiceLuminosite;
import tn.esprit.services.ServiceConsommation;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Scanner;

public class Maindonnee {
    public static void main(String[] args) {
        ServiceTemperature serviceTemperature = new ServiceTemperature();
        ServiceMouvement serviceMouvement = new ServiceMouvement();
        ServiceLuminosite serviceLuminosite = new ServiceLuminosite();
        ServiceConsommation serviceConsommation = new ServiceConsommation();

        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\n📌 Menu principal de gestion :");
            System.out.println("1️⃣ Gestion des données de température");
            System.out.println("2️⃣ Gestion des données de mouvement");
            System.out.println("3️⃣ Gestion des données de luminosité");
            System.out.println("4️⃣ Gestion des données de consommation");
            System.out.println("5️⃣ Quitter");
            System.out.print("👉 Faites votre choix : ");

            int choix = lireInt(scanner);

            switch (choix) {
                case 1:
                    gererTemperature(serviceTemperature, scanner);
                    break;
                case 2:
                    gererMouvement(serviceMouvement, scanner);
                    break;
                case 3:
                    gererLuminosite(serviceLuminosite, scanner);
                    break;
                case 4:
                    gererConsommation(serviceConsommation, scanner);
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

    // 📌 Gestion des données de température
    private static void gererTemperature(ServiceTemperature serviceTemperature, Scanner scanner) {
        while (true) {
            System.out.println("\n📌 Menu de gestion des données de température :");
            System.out.println("1️⃣ Ajouter une nouvelle donnée de température");
            System.out.println("2️⃣ Afficher toutes les données de température");
            System.out.println("3️⃣ Modifier une donnée de température");
            System.out.println("4️⃣ Supprimer une donnée de température");
            System.out.println("5️⃣ Retour au menu principal");
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
                    return; // Retour au menu principal
                default:
                    System.out.println("⚠️ Choix invalide, veuillez réessayer.");
            }
        }
    }

    // 📌 Gestion des données de mouvement
    private static void gererMouvement(ServiceMouvement serviceMouvement, Scanner scanner) {
        while (true) {
            System.out.println("\n📌 Menu de gestion des données de mouvement :");
            System.out.println("1️⃣ Ajouter une nouvelle donnée de mouvement");
            System.out.println("2️⃣ Afficher toutes les données de mouvement");
            System.out.println("3️⃣ Modifier une donnée de mouvement");
            System.out.println("4️⃣ Supprimer une donnée de mouvement");
            System.out.println("5️⃣ Retour au menu principal");
            System.out.print("👉 Faites votre choix : ");

            int choix = lireInt(scanner);

            switch (choix) {
                case 1:
                    ajouterMouvement(serviceMouvement, scanner);
                    break;
                case 2:
                    afficherMouvements(serviceMouvement);
                    break;
                case 3:
                    modifierMouvement(serviceMouvement, scanner);
                    break;
                case 4:
                    supprimerMouvement(serviceMouvement, scanner);
                    break;
                case 5:
                    return; // Retour au menu principal
                default:
                    System.out.println("⚠️ Choix invalide, veuillez réessayer.");
            }
        }
    }

    // 📌 Gestion des données de luminosité
    private static void gererLuminosite(ServiceLuminosite serviceLuminosite, Scanner scanner) {
        while (true) {
            System.out.println("\n📌 Menu de gestion des données de luminosité :");
            System.out.println("1️⃣ Ajouter une nouvelle donnée de luminosité");
            System.out.println("2️⃣ Afficher toutes les données de luminosité");
            System.out.println("3️⃣ Modifier une donnée de luminosité");
            System.out.println("4️⃣ Supprimer une donnée de luminosité");
            System.out.println("5️⃣ Retour au menu principal");
            System.out.print("👉 Faites votre choix : ");

            int choix = lireInt(scanner);

            switch (choix) {
                case 1:
                    ajouterLuminosite(serviceLuminosite, scanner);
                    break;
                case 2:
                    afficherLuminosites(serviceLuminosite);
                    break;
                case 3:
                    modifierLuminosite(serviceLuminosite, scanner);
                    break;
                case 4:
                    supprimerLuminosite(serviceLuminosite, scanner);
                    break;
                case 5:
                    return; // Retour au menu principal
                default:
                    System.out.println("⚠️ Choix invalide, veuillez réessayer.");
            }
        }
    }

    // 📌 Gestion des données de consommation
    private static void gererConsommation(ServiceConsommation serviceConsommation, Scanner scanner) {
        while (true) {
            System.out.println("\n📌 Menu de gestion des données de consommation :");
            System.out.println("1️⃣ Ajouter une nouvelle donnée de consommation");
            System.out.println("2️⃣ Afficher toutes les données de consommation");
            System.out.println("3️⃣ Modifier une donnée de consommation");
            System.out.println("4️⃣ Supprimer une donnée de consommation");
            System.out.println("5️⃣ Retour au menu principal");
            System.out.print("👉 Faites votre choix : ");

            int choix = lireInt(scanner);

            switch (choix) {
                case 1:
                    ajouterConsommation(serviceConsommation, scanner);
                    break;
                case 2:
                    afficherConsommations(serviceConsommation);
                    break;
                case 3:
                    modifierConsommation(serviceConsommation, scanner);
                    break;
                case 4:
                    supprimerConsommation(serviceConsommation, scanner);
                    break;
                case 5:
                    return; // Retour au menu principal
                default:
                    System.out.println("⚠️ Choix invalide, veuillez réessayer.");
            }
        }
    }

    // 📌 Ajouter une nouvelle donnée de température
    private static void ajouterTemperature(ServiceTemperature serviceTemperature, Scanner scanner) {
        LocalDate dateCollecte = LocalDate.now(); // Obtenir la date actuelle
        LocalTime heureCollecte = LocalTime.now(); // Obtenir l'heure actuelle

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

        System.out.print("Nouvelle valeur de la température : ");
        float valeur = lireFloat(scanner);

        DonneeTemperature donnee = new DonneeTemperature(id, dateCollecte, heureCollecte, 0, valeur);
        serviceTemperature.update(donnee);
        System.out.println("✅ Donnée de température modifiée avec succès !");
    }

    // 📌 Supprimer une donnée de température
    private static void supprimerTemperature(ServiceTemperature serviceTemperature, Scanner scanner) {
        System.out.print("Entrez l'ID de la donnée à supprimer : ");
        int id = lireInt(scanner);
        serviceTemperature.delete(id);
        System.out.println("✅ Donnée de température supprimée avec succès !");
    }

    // Méthodes similaires pour le mouvement, la luminosité et la consommation...

    // Fonction pour lire un entier
    private static int lireInt(Scanner scanner) {
        while (!scanner.hasNextInt()) {
            System.out.println("⚠️ Ce n'est pas un entier, veuillez réessayer.");
            scanner.next(); // Consomme l'entrée non valide
        }
        return scanner.nextInt();
    }

    // Fonction pour lire un flottant
    private static float lireFloat(Scanner scanner) {
        while (!scanner.hasNextFloat()) {
            System.out.println("⚠️ Ce n'est pas un nombre à virgule flottante, veuillez réessayer.");
            scanner.next(); // Consomme l'entrée non valide
        }
        return scanner.nextFloat();
    }

    // Ajoutez des méthodes pour ajouter, afficher, modifier et supprimer les données de mouvement, luminosité et consommation ici...
    // 📌 Ajouter une nouvelle donnée de mouvement
    private static void ajouterMouvement(ServiceMouvement serviceMouvement, Scanner scanner) {
        LocalDate dateCollecte = LocalDate.now(); // Obtenir la date actuelle
        LocalTime heureCollecte = LocalTime.now(); // Obtenir l'heure actuelle

        System.out.print("ID du capteur : ");
        int capteurId = lireInt(scanner);

        System.out.print("Valeur du mouvement (0: Inactif, 1: Actif) : ");
        int valeurInt = lireInt(scanner); // Lire l'entier

        // Convertir la valeur entière en boolean
        boolean valeur = (valeurInt == 1); // 1 devient true, 0 devient false

        DonneeMouvement donnee = new DonneeMouvement(0, dateCollecte, heureCollecte, capteurId, valeur);
        serviceMouvement.add(donnee);
        System.out.println("✅ Donnée de mouvement ajoutée avec succès !");
    }

    // 📌 Afficher toutes les données de mouvement
    private static void afficherMouvements(ServiceMouvement serviceMouvement) {
        List<DonneeMouvement> donnees = serviceMouvement.getAll();

        if (donnees.isEmpty()) {
            System.out.println("📭 Aucune donnée de mouvement trouvée.");
            return;
        }

        System.out.println("\n📋 Liste des données de mouvement :");
        for (DonneeMouvement donnee : donnees) {
            System.out.println("🔹 ID: " + donnee.getId() +
                    ", Date de collecte: " + donnee.getDateCollecte() +
                    ", Heure de collecte: " + donnee.getHeureCollecte() +
                    ", ID Capteur: " + donnee.getCapteurId() +
                    ", Valeur: " + donnee.getValeur());
        }
    }

    // 📌 Modifier une donnée de mouvement
    private static void modifierMouvement(ServiceMouvement serviceMouvement, Scanner scanner) {
        System.out.print("Entrez l'ID de la donnée à modifier : ");
        int id = lireInt(scanner);

        System.out.print("Nouvelle date de collecte (YYYY-MM-DD) : ");
        LocalDate dateCollecte = LocalDate.parse(scanner.nextLine());

        System.out.print("Nouvelle heure de collecte (HH:MM:SS) : ");
        LocalTime heureCollecte = LocalTime.parse(scanner.nextLine());

        System.out.print("Nouvelle valeur du mouvement (0: Inactif, 1: Actif) : ");
        int valeurInt = lireInt(scanner); // Lire l'entier

        // Convertir la valeur entière en boolean
        boolean valeur = (valeurInt == 1); // 1 devient true, 0 devient false

        DonneeMouvement donnee = new DonneeMouvement(id, dateCollecte, heureCollecte, 0, valeur);
        serviceMouvement.update(donnee);
        System.out.println("✅ Donnée de mouvement modifiée avec succès !");
    }

    // 📌 Supprimer une donnée de mouvement
    private static void supprimerMouvement(ServiceMouvement serviceMouvement, Scanner scanner) {
        System.out.print("Entrez l'ID de la donnée à supprimer : ");
        int id = lireInt(scanner);
        serviceMouvement.delete(id);
        System.out.println("✅ Donnée de mouvement supprimée avec succès !");
    }

    // 📌 Ajouter une nouvelle donnée de luminosité
    private static void ajouterLuminosite(ServiceLuminosite serviceLuminosite, Scanner scanner) {
        LocalDate dateCollecte = LocalDate.now(); // Obtenir la date actuelle
        LocalTime heureCollecte = LocalTime.now(); // Obtenir l'heure actuelle

        System.out.print("ID du capteur : ");
        int capteurId = lireInt(scanner);

        System.out.print("Valeur de la luminosité : ");
        int valeur = lireInt(scanner);

        DonneeLuminosite donnee = new DonneeLuminosite(0, dateCollecte, heureCollecte, capteurId, valeur);
        serviceLuminosite.add(donnee);
        System.out.println("✅ Donnée de luminosité ajoutée avec succès !");
    }

    // 📌 Afficher toutes les données de luminosité
    private static void afficherLuminosites(ServiceLuminosite serviceLuminosite) {
        List<DonneeLuminosite> donnees = serviceLuminosite.getAll();

        if (donnees.isEmpty()) {
            System.out.println("📭 Aucune donnée de luminosité trouvée.");
            return;
        }

        System.out.println("\n📋 Liste des données de luminosité :");
        for (DonneeLuminosite donnee : donnees) {
            System.out.println("🔹 ID: " + donnee.getId() +
                    ", Date de collecte: " + donnee.getDateCollecte() +
                    ", Heure de collecte: " + donnee.getHeureCollecte() +
                    ", ID Capteur: " + donnee.getCapteurId() +
                    ", Valeur: " + donnee.getValeur());
        }
    }

    // 📌 Modifier une donnée de luminosité
    private static void modifierLuminosite(ServiceLuminosite serviceLuminosite, Scanner scanner) {
        System.out.print("Entrez l'ID de la donnée à modifier : ");
        int id = lireInt(scanner);

        System.out.print("Nouvelle date de collecte (YYYY-MM-DD) : ");
        LocalDate dateCollecte = LocalDate.parse(scanner.nextLine());

        System.out.print("Nouvelle heure de collecte (HH:MM:SS) : ");
        LocalTime heureCollecte = LocalTime.parse(scanner.nextLine());

        System.out.print("Nouvelle valeur de la luminosité (en lux) : ");
        int valeur = lireInt(scanner);

        DonneeLuminosite donnee = new DonneeLuminosite(id, dateCollecte, heureCollecte, 0, valeur);
        serviceLuminosite.update(donnee);
        System.out.println("✅ Donnée de luminosité modifiée avec succès !");
    }

    // 📌 Supprimer une donnée de luminosité
    private static void supprimerLuminosite(ServiceLuminosite serviceLuminosite, Scanner scanner) {
        System.out.print("Entrez l'ID de la donnée à supprimer : ");
        int id = lireInt(scanner);
        serviceLuminosite.delete(id);
        System.out.println("✅ Donnée de luminosité supprimée avec succès !");
    }

    // 📌 Ajouter une nouvelle donnée de consommation
    private static void ajouterConsommation(ServiceConsommation serviceConsommation, Scanner scanner) {
        LocalDate dateCollecte = LocalDate.now(); // Obtenir la date actuelle
        LocalTime heureCollecte = LocalTime.now(); // Obtenir l'heure actuelle

        System.out.print("ID du capteur : ");
        int capteurId = lireInt(scanner);

        System.out.print("Valeur de la consommation : ");
        float valeur = lireFloat(scanner);

        DonneeConsommation donnee = new DonneeConsommation(0, dateCollecte, heureCollecte, capteurId, valeur);
        serviceConsommation.add(donnee);
        System.out.println("✅ Donnée de consommation ajoutée avec succès !");
    }

    // 📌 Afficher toutes les données de consommation
    private static void afficherConsommations(ServiceConsommation serviceConsommation) {
        List<DonneeConsommation> donnees = serviceConsommation.getAll();

        if (donnees.isEmpty()) {
            System.out.println("📭 Aucune donnée de consommation trouvée.");
            return;
        }

        System.out.println("\n📋 Liste des données de consommation :");
        for (DonneeConsommation donnee : donnees) {
            System.out.println("🔹 ID: " + donnee.getId() +
                    ", Date de collecte: " + donnee.getDateCollecte() +
                    ", Heure de collecte: " + donnee.getHeureCollecte() +
                    ", ID Capteur: " + donnee.getCapteurId() +
                    ", Valeur: " + donnee.getValeur());
        }
    }

    // 📌 Modifier une donnée de consommation
    private static void modifierConsommation(ServiceConsommation serviceConsommation, Scanner scanner) {
        System.out.print("Entrez l'ID de la donnée à modifier : ");
        int id = lireInt(scanner);

        System.out.print("Nouvelle date de collecte (YYYY-MM-DD) : ");
        LocalDate dateCollecte = LocalDate.parse(scanner.nextLine());

        System.out.print("Nouvelle heure de collecte (HH:MM:SS) : ");
        LocalTime heureCollecte = LocalTime.parse(scanner.nextLine());

        System.out.print("Nouvelle valeur de la consommation (en kWh) : ");
        float valeur = lireFloat(scanner);

        DonneeConsommation donnee = new DonneeConsommation(id, dateCollecte, heureCollecte, 0, valeur);
        serviceConsommation.update(donnee);
        System.out.println("✅ Donnée de consommation modifiée avec succès !");
    }

    // 📌 Supprimer une donnée de consommation
    private static void supprimerConsommation(ServiceConsommation serviceConsommation, Scanner scanner) {
        System.out.print("Entrez l'ID de la donnée à supprimer : ");
        int id = lireInt(scanner);
        serviceConsommation.delete(id);
        System.out.println("✅ Donnée de consommation supprimée avec succès !");
    }

}

