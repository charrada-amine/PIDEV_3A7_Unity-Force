package tn.esprit.test;
import tn.esprit.models.Specialite;  // Importer l'énumération Specialite
import java.sql.*;

import tn.esprit.models.technicien;
import tn.esprit.models.utilisateur;
import tn.esprit.models.responsable;
import tn.esprit.models.citoyen;

import tn.esprit.services.ServiceTechnicien;
import tn.esprit.services.ServiceUtilisateur;
import tn.esprit.services.ServiceResponsable;
import tn.esprit.services.ServiceCitoyen;
import java.util.Arrays;


import tn.esprit.Enumerations.Role;
import java.util.List;
import java.util.ArrayList;


import java.util.Date;
import java.util.Scanner;

public class Mainuser {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        ServiceUtilisateur su = new ServiceUtilisateur();
        ServiceTechnicien st = new ServiceTechnicien();
        ServiceResponsable sr = new ServiceResponsable();
        ServiceCitoyen sc = new ServiceCitoyen();


        while (true) {
            System.out.println("\n======= MENU =======");
            System.out.println("1️⃣ Ajouter un utilisateur");
            System.out.println("2️⃣ Supprimer un utilisateur par ID");
            System.out.println("3️⃣ Modifier un utilisateur");
            System.out.println("4️⃣ Afficher tous les utilisateurs");
            //System.out.println("5️⃣ Ajouter un technicien");
            System.out.println("6 liste un technicien");
            System.out.println("7 supprimer technicien");
            System.out.println("8 modifier un technicien");
            System.out.println("9 liste responsable");
            System.out.println("10 supprimer responsable");
            System.out.println("11 modifier responsable");
            System.out.println("12 liste citoyen");
            System.out.println("13 supprimer citoyen");
            System.out.println("14 modifier citoyen");
            System.out.println("15 Quitter");






            System.out.print("👉 Choisissez une option : ");

            int choix = scanner.nextInt();
            scanner.nextLine(); // Nettoyer le buffer

            switch (choix) {
                case 1:
                    System.out.println("📌 Ajout d'un nouvel utilisateur");
                    System.out.print("Nom : ");
                    String nom = scanner.nextLine();
                    System.out.print("Prénom : ");
                    String prenom = scanner.nextLine();
                    System.out.print("Email : ");
                    String email = scanner.nextLine();
                    System.out.print("Mot de passe : ");
                    String motDePasse = scanner.nextLine();
                    System.out.print("Rôle (citoyen/technicien/responsable) : ");
                    String roleInput = scanner.nextLine();

                    // Convertir la chaîne de rôle en objet Role
                    Role role = Role.valueOf(roleInput.toLowerCase());

                    // Obtenir la date actuelle sans l'heure
                    java.sql.Date dateSansHeure = new java.sql.Date(System.currentTimeMillis());

                    // Création de l'utilisateur
                    utilisateur user = new utilisateur(nom, prenom, email, motDePasse, role, dateSansHeure);

                    // Si le rôle est "technicien", demander la spécialité
                    if (role == Role.technicien) {
                        System.out.println("Choisissez une spécialité pour le technicien :");
                        System.out.println("1. Maintenance");
                        System.out.println("2. Électricité");
                        System.out.println("3. Autre");
                        int choixSpecialite = scanner.nextInt();
                        scanner.nextLine(); // pour consommer le '\n' restant après l'input entier

                        Specialite specialite = null;
                        switch (choixSpecialite) {
                            case 1:
                                specialite = Specialite.maintenance;
                                break;
                            case 2:
                                specialite = Specialite.electricite;
                                break;
                            case 3:
                                specialite = Specialite.autre;
                                break;
                            default:
                                System.out.println("❌ Choix invalide de spécialité.");
                                break;
                        }

                        // Ajouter l'utilisateur avec la spécialité dans la base de données
                        if (specialite != null) {
                            su.add(user, specialite, new ArrayList<>(), -1);  // Passer -1 pour zoneId, non applicable aux techniciens
                            System.out.println("✅ Utilisateur technicien ajouté avec spécialité : " + specialite);
                        }
                    }
                    // Si le rôle est "citoyen", demander le zoneId
                    else if (role == Role.citoyen) {
                        System.out.println("Entrez le zoneId du citoyen :");
                        int zoneId = scanner.nextInt();
                        scanner.nextLine(); // Consomme le '\n' restant

                        // Ajouter l'utilisateur citoyen avec son zoneId
                        su.add(user, null, new ArrayList<>(), zoneId);  // Passer zoneId pour le citoyen
                        System.out.println("✅ Citoyen ajouté avec zoneId : " + zoneId);
                    }
                    // Si le rôle est "responsable", demander les modules
                    else if (role == Role.responsable) {
                        System.out.println("Entrez les modules pour le responsable (séparez-les par des virgules) :");
                        String modulesInput = scanner.nextLine();

                        // Diviser la chaîne en une liste de modules
                        List<String> modules = Arrays.asList(modulesInput.split("\\s*,\\s*"));

                        // Ajouter l'utilisateur responsable dans la base de données avec les modules
                        su.add(user, null, modules, -1);  // Les modules sont gérés ici
                        System.out.println("✅ Responsable ajouté avec les modules : " + String.join(", ", modules));
                    }
                    else {
                        // Ajouter l'utilisateur sans spécialité ou modules si ce n'est pas un technicien, responsable, ou citoyen
                        su.add(user, null, new ArrayList<>(), -1);  // Passer -1 pour zoneId si non applicable
                        System.out.println("✅ Utilisateur ajouté avec succès !");
                    }
                    break;


                case 2:
                    System.out.print("📌 Entrez l'ID de l'utilisateur à supprimer : ");
                    int idSupp = scanner.nextInt();
                    su.deleteById(idSupp);
                    System.out.println("✅ Utilisateur supprimé !");
                    break;

                case 3:
                    System.out.print("📌 Entrez l'ID de l'utilisateur à modifier : ");
                    int idModif = scanner.nextInt();
                    scanner.nextLine();  // Consommer la nouvelle ligne après nextInt()

                    // Demander à l'utilisateur quel champ il souhaite modifier
                    System.out.println("📌 Choisissez le champ à modifier : ");
                    System.out.println("1 - nom");
                    System.out.println("2 - prenom");
                    System.out.println("3 - Email");
                    System.out.println("4 - Mot de passe");
                    System.out.print("Entrez le numéro du champ à modifier : ");
                    int choixChamp = scanner.nextInt();
                    scanner.nextLine();  // Consommer la nouvelle ligne après nextInt()

                    // Variable pour la nouvelle valeur du champ
                    String newValue = "";

                    switch (choixChamp) {
                        case 1:
                            System.out.print("📌 Entrez le nouveau nom : ");
                            newValue = scanner.nextLine();
                            su.updateField(idModif, "nom", newValue); // Appel de la méthode pour mettre à jour le nom
                            break;

                        case 2:
                            System.out.print("📌 Entrez le nouveau prénom : ");
                            newValue = scanner.nextLine();
                            su.updateField(idModif, "prenom", newValue); // Appel de la méthode pour mettre à jour le prénom
                            break;

                        case 3:
                            System.out.print("📌 Entrez le nouvel email : ");
                            newValue = scanner.nextLine();
                            su.updateField(idModif, "email", newValue); // Appel de la méthode pour mettre à jour l'email
                            break;

                        case 4:
                            System.out.print("📌 Entrez le nouveau mot de passe : ");
                            newValue = scanner.nextLine();
                            su.updateField(idModif, "motdepasse", newValue); // Appel de la méthode pour mettre à jour le mot de passe
                            break;

                        default:
                            System.out.println("❌ Option invalide !");
                            break;
                    }

                    break;

                case 4:
                    System.out.println("\n📋 Liste des utilisateurs :");
                    List<utilisateur> utilisateurs = su.getAllUtilisateurs();
                    for (utilisateur u : utilisateurs) {
                        System.out.println(u);
                    }
                    break;

                /*6
                case 5:
                    System.out.println("📌 Ajout d'un Technicien");

                    System.out.print("Nom : ");
                    String nomTech = scanner.nextLine();

                    System.out.print("Prénom : ");
                    String prenomTech = scanner.nextLine();

                    System.out.print("Email : ");
                    String emailTech = scanner.nextLine();

                    System.out.print("Mot de passe : ");
                    String mdpTech = scanner.nextLine();

                    System.out.print("Spécialité (electricite/maintenance/autre) : ");
                    String specialiteInput = scanner.nextLine();  // Ne pas convertir en majuscules

                    Specialite specialite;
                    try {
                        specialite = Specialite.valueOf(specialiteInput);  // Utilise la valeur telle quelle
                    } catch (IllegalArgumentException e) {
                        System.out.println("❌ Spécialité invalide ! Veuillez entrer electricite, maintenance ou autre.");
                        break;  // Arrêter si la spécialité est incorrecte
                    }



// Date actuelle sans l'heure
                    java.sql.Date dateSansHeuretech = new java.sql.Date(System.currentTimeMillis());

// Création du technicien avec le rôle directement défini comme "technicien"
                    technicien tech = new technicien(nomTech, prenomTech, emailTech, mdpTech, dateSansHeuretech, specialite);

// Ajouter le technicien à la base de données
                    st.add(tech);
                    System.out.println("✅ Technicien ajouté avec succès !");


                    break;
*/


                case 6:
                    System.out.println("\n📋 Liste des techniciens :");
                    List<technicien> techniciens = st.getAllTechniciens();
                    for (technicien t : techniciens) {
                        System.out.println(t);
                    }
                    break;
                case 7:
                    System.out.print("📌 Entrez l'ID du technicien à supprimer : ");
                    int idTechSupp = scanner.nextInt();
                    st.deleteById(idTechSupp);
                    break;
                case 8:
                    System.out.print("📌 Entrez l'ID du technicien à modifier : ");
                    int idModiftech = scanner.nextInt();
                    scanner.nextLine();  // Consommer la nouvelle ligne après nextInt()

                    // Demander à l'utilisateur quelle spécialité il souhaite attribuer
                    System.out.println("📌 Choisissez la nouvelle spécialité : ");
                    System.out.println("1 - Maintenance");
                    System.out.println("2 - Électricité");
                    System.out.println("3 - Autre");
                    System.out.print("Entrez le numéro de la spécialité à attribuer : ");
                    int choixSpecialite = scanner.nextInt();
                    scanner.nextLine();  // Consommer la nouvelle ligne après nextInt()

                    // Variable pour la nouvelle spécialité
                    Specialite newSpecialite = null;

                    switch (choixSpecialite) {
                        case 1:
                            newSpecialite = Specialite.maintenance;
                            break;
                        case 2:
                            newSpecialite = Specialite.electricite;
                            break;
                        case 3:
                            newSpecialite = Specialite.autre;
                            break;
                        default:
                            System.out.println("❌ Choix invalide. Veuillez sélectionner une option valide.");
                            break;
                    }

                    /*if (newSpecialite != null) {
                        // Convertir l'énumération en chaîne avant de passer à la méthode
                        st.updateSpecialite(idModiftech, newSpecialite.name());
                    }*/

                    break;

                case 9:
                    System.out.println("\n📋 Liste des responsables :");
                    List<responsable> responsables = sr.getAllResponsables();  // Récupérer la liste des responsables
                    for (responsable r : responsables) {
                        System.out.println(r);  // Afficher chaque responsable
                    }
                    break;


                case 10:
                    System.out.print("📌 Entrez l'ID du technicien à supprimer : ");
                    int idRes = scanner.nextInt();
                    sr.deleteById(idRes);
                    break;
                case 11:
                    System.out.print("📌 Entrez l'ID du responsable à modifier : ");
                    int idModifResp = scanner.nextInt();
                    scanner.nextLine();  // Consommer la nouvelle ligne après nextInt()

// Récupérer les modules actuels du responsable
                    List<String> modulesActuels = sr.getModulesById(idModifResp);

                    if (modulesActuels.isEmpty()) {
                        System.out.println("⚠️ Aucun module trouvé pour ce responsable.");
                    } else {
                        System.out.println("\n📋 Modules actuels du responsable :");
                        for (int i = 0; i < modulesActuels.size(); i++) {
                            System.out.println((i + 1) + " - " + modulesActuels.get(i));
                        }

                        System.out.print("📌 Entrez le numéro du module à modifier : ");
                        int choixModule = scanner.nextInt();
                        scanner.nextLine();  // Consommer la nouvelle ligne après nextInt()

                        if (choixModule < 1 || choixModule > modulesActuels.size()) {
                            System.out.println("❌ Choix invalide.");
                        } else {
                            System.out.print("✍️ Entrez le nouveau module : ");
                            String nouveauModule = scanner.nextLine();

                            // Modifier le module sélectionné
                            modulesActuels.set(choixModule - 1, nouveauModule);

                            // Mettre à jour dans la base de données
                            sr.updateModules(idModifResp, modulesActuels);
                        }
                    }

                    break;
                case 12:
                    System.out.println("\n📋 Liste des citoyens :");
                    List<citoyen> citoyens = sc.getAllCitoyens();  // Récupérer la liste des citoyens
                    for (citoyen c : citoyens) {
                        System.out.println(c);  // Afficher chaque citoyen
                    }
                    break;
                case 13:
                    System.out.print("📌 Entrez l'ID du technicien à supprimer : ");
                    int idCit = scanner.nextInt();
                    sc.deleteById(idCit);
                    break;
                case 14:
                    System.out.print("📌 Entrez l'ID du citoyen à modifier : ");
                    int idModifCitoyen = scanner.nextInt();
                    scanner.nextLine();  // Consommer la nouvelle ligne après nextInt()

                    // Demander à l'utilisateur quel zone il souhaite attribuer
                    System.out.println("📌 Choisissez la nouvelle zone : ");
                    System.out.print("Entrez l'ID de la zone : ");
                    int newZoneId = scanner.nextInt();
                    scanner.nextLine();  // Consommer la nouvelle ligne après nextInt()

                    // Appeler la méthode pour mettre à jour le zoneId du citoyen
                    sc.updateZone(idModifCitoyen, newZoneId);
                    break;


                case 15:
                    System.out.println("👋 Au revoir !");
                    scanner.close();
                    return;


                default:
                    System.out.println("❌ Option invalide, veuillez réessayer !");
            }
        }
    }
}
