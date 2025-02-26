/*package tn.esprit.test;

import tn.esprit.models.profile;
import tn.esprit.models.Source;
import tn.esprit.services.ServiceProfile;
import tn.esprit.services.ServiceSource;
import tn.esprit.Enumerations.EnumType;
import tn.esprit.Enumerations.EnumEtat;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

public class MainE {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        ServiceProfile serviceProfile = new ServiceProfile();
        ServiceSource serviceSource = new ServiceSource();

        while (true) {
            System.out.println("\n=== Menu Principal ===");
            System.out.println("1️⃣ Gestion des Profils Énergétiques");
            System.out.println("2️⃣ Gestion des Sources Énergétiques");
            System.out.println("3️⃣ Quitter");
            System.out.print("👉 Choisissez une option : ");

            int choixPrincipal = scanner.nextInt();
            scanner.nextLine(); // Consommer la ligne restante

            switch (choixPrincipal) {
                case 1:
                    gestionProfils(scanner, serviceProfile);
                    break;
                case 2:
                    gestionSources(scanner, serviceSource);
                    break;
                case 3:
                    System.out.println("👋 Programme terminé.");
                    scanner.close();
                    return;
                default:
                    System.out.println("⚠️ Option invalide, veuillez réessayer.");
            }
        }
    }

    private static void gestionProfils(Scanner scanner, ServiceProfile serviceProfile) {
        while (true) {
            System.out.println("\n=== Gestion des Profils Énergétiques ===");
            System.out.println("1️⃣ Ajouter un profil");
            System.out.println("2️⃣ Afficher tous les profils");
            System.out.println("3️⃣ Mettre à jour un profil");
            System.out.println("4️⃣ Supprimer un profil");
            System.out.println("5️⃣ Retour au menu principal");
            System.out.print("👉 Choisissez une option : ");

            int choix = scanner.nextInt();
            scanner.nextLine(); // Consommer la ligne restante

            switch (choix) {
                case 1:
                    ajouterProfil(scanner, serviceProfile);
                    break;
                case 2:
                    afficherProfils(serviceProfile);
                    break;
                case 3:
                    mettreAJourProfil(scanner, serviceProfile);
                    break;
                case 4:
                    supprimerProfil(scanner, serviceProfile);
                    break;
                case 5:
                    return;
                default:
                    System.out.println("⚠️ Option invalide, veuillez réessayer.");
            }
        }
    }

    private static void gestionSources(Scanner scanner, ServiceSource serviceSource) {
        while (true) {
            System.out.println("\n📌 Menu de gestion des sources énergétiques :");
            System.out.println("1️⃣ Ajouter une nouvelle source");
            System.out.println("2️⃣ Afficher toutes les sources");
            System.out.println("3️⃣ Modifier une source");
            System.out.println("4️⃣ Supprimer une source");
            System.out.println("5️⃣ Retour au menu principal");
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
                    return;
                default:
                    System.out.println("⚠️ Choix invalide, veuillez réessayer.");
            }
        }
    }

    private static void ajouterProfil(Scanner scanner, ServiceProfile serviceProfile) {
        System.out.println("\n🔹 Ajout d'un nouveau profil");

        System.out.print("Consommation par jour : ");
        String consoJour = scanner.nextLine();
        System.out.print("Consommation par mois : ");
        String consoMois = scanner.nextLine();
        System.out.print("Coût estimé : ");
        String cout = scanner.nextLine();
        System.out.print("Durée d'activité : ");
        String duree = scanner.nextLine();
        System.out.print("ID de la source énergétique : ");
        int sourceId = scanner.nextInt();
        System.out.print("ID du lampadaire : ");
        int lampadaireId = scanner.nextInt();
        scanner.nextLine(); // Consommer la ligne restante

        profile newProfile = new profile(0, consoJour, consoMois, cout, duree, sourceId, lampadaireId);
        serviceProfile.add(newProfile);
    }

    private static void afficherProfils(ServiceProfile serviceProfile) {
        System.out.println("\n🔹 Liste des profils énergétiques :");
        List<profile> profiles = serviceProfile.getAll();

        if (profiles.isEmpty()) {
            System.out.println("⚠️ Aucun profil trouvé.");
        } else {
            for (profile p : profiles) {
                System.out.println("ID : " + p.getIdprofile() +
                        " | Consommation Jour : " + p.getConsommationJour() +
                        " | Consommation Mois : " + p.getConsommationMois() +
                        " | Coût : " + p.getCoutEstime() +
                        " | Durée : " + p.getDureeActivite() +
                        " | Source ID : " + p.getSourceId() +
                        " | Lampadaire ID : " + p.getLampadaireId());
            }
        }
    }

    private static void mettreAJourProfil(Scanner scanner, ServiceProfile serviceProfile) {
        System.out.print("\n🔹 Entrez l'ID du profil à mettre à jour : ");
        int id = scanner.nextInt();
        scanner.nextLine();

        System.out.print("Nouvelle consommation par jour : ");
        String consoJour = scanner.nextLine();
        System.out.print("Nouvelle consommation par mois : ");
        String consoMois = scanner.nextLine();
        System.out.print("Nouveau coût estimé : ");
        String cout = scanner.nextLine();
        System.out.print("Nouvelle durée d'activité : ");
        String duree = scanner.nextLine();
        System.out.print("Nouvel ID de la source énergétique : ");
        int sourceId = scanner.nextInt();
        System.out.print("Nouvel ID du lampadaire : ");
        int lampadaireId = scanner.nextInt();
        scanner.nextLine(); // Consommer la ligne restante

        profile updatedProfile = new profile(id, consoJour, consoMois, cout, duree, sourceId, lampadaireId);
        serviceProfile.update(updatedProfile);
    }

    private static void supprimerProfil(Scanner scanner, ServiceProfile serviceProfile) {
        System.out.print("\n🔹 Entrez l'ID du profil à supprimer : ");
        int id = scanner.nextInt();
        serviceProfile.delete(id);
    }

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

    private static void supprimerSource(ServiceSource serviceSource, Scanner scanner) {
        System.out.print("Entrez l'ID de la source à supprimer : ");
        int id = lireInt(scanner);

        serviceSource.delete(id);
        System.out.println("🗑️ Source supprimée avec succès !");
    }

    private static int lireInt(Scanner scanner) {
        while (!scanner.hasNextInt()) {
            System.out.println("⚠️ Veuillez entrer un nombre entier valide !");
            scanner.next();
        }
        int valeur = scanner.nextInt();
        scanner.nextLine(); // Consommer la ligne restante
        return valeur;
    }

    private static float lireFloat(Scanner scanner) {
        while (!scanner.hasNextFloat()) {
            System.out.println("⚠️ Veuillez entrer un nombre valide !");
            scanner.next();
        }
        float valeur = scanner.nextFloat();
        scanner.nextLine(); // Consommer la ligne restante
        return valeur;
    }

    private static EnumType getEnumFromString(String typeStr) {
        try {
            return EnumType.valueOf(typeStr);
        } catch (IllegalArgumentException e) {
            System.out.println("⚠️ Type invalide. Veuillez entrer ELECTRICITE, SOLAIRE ou BATTERIE.");
            return null;
        }
    }
}*/