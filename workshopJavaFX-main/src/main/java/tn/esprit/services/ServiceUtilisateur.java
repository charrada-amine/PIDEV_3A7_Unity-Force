package tn.esprit.services;

import tn.esprit.utils.MyDatabase;
import tn.esprit.models.utilisateur;

import tn.esprit.models.Role;
import tn.esprit.models.Specialite;
import java.util.Scanner;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;

import java.util.List;
import java.text.SimpleDateFormat;

public class ServiceUtilisateur {
    private Connection cnx;

    public ServiceUtilisateur() {
        cnx = MyDatabase.getInstance().getCnx();
    }
    public void add(utilisateur utilisateur, Specialite specialite, List<String> modules, int zoneId) {
        // Vérifier que tous les champs obligatoires sont non vides
        if (utilisateur.getNom() == null || utilisateur.getNom().trim().isEmpty()) {
            System.out.println("❌ Le nom est obligatoire !");
            return;
        }
        if (utilisateur.getPrenom() == null || utilisateur.getPrenom().trim().isEmpty()) {
            System.out.println("❌ Le prénom est obligatoire !");
            return;
        }
        if (utilisateur.getEmail() == null || utilisateur.getEmail().trim().isEmpty()) {
            System.out.println("❌ L'email est obligatoire !");
            return;
        }
        if (utilisateur.getMotdepasse() == null || utilisateur.getMotdepasse().trim().isEmpty()) {
            System.out.println("❌ Le mot de passe est obligatoire !");
            return;
        }
        if (utilisateur.getRole() == null) {
            System.out.println("❌ Le rôle est obligatoire !");
            return;
        }
        if (utilisateur.getDateinscription() == null) {
            System.out.println("❌ La date d'inscription est obligatoire !");
            return;
        }

        // Préparer la requête pour insérer l'utilisateur dans la base de données
        String qry = "INSERT INTO utilisateur (nom, prenom, email, motdepasse, role, dateinscription) VALUES (?, ?, ?, ?, ?, ?)";

        try {
            PreparedStatement pstm = cnx.prepareStatement(qry, Statement.RETURN_GENERATED_KEYS);
            pstm.setString(1, utilisateur.getNom());
            pstm.setString(2, utilisateur.getPrenom());
            pstm.setString(3, utilisateur.getEmail());
            pstm.setString(4, utilisateur.getMotdepasse());
            pstm.setString(5, utilisateur.getRole().toString()); // Convertir l'énumération en chaîne de caractères
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String dateSansHeure = sdf.format(utilisateur.getDateinscription());
            pstm.setDate(6, java.sql.Date.valueOf(dateSansHeure));

            int affectedRows = pstm.executeUpdate();
            if (affectedRows > 0) {
                ResultSet generatedKeys = pstm.getGeneratedKeys();
                if (generatedKeys.next()) {
                    utilisateur.setId_utilisateur(generatedKeys.getInt(1)); // Set the generated user id

                    // Si le rôle est "technicien", ajouter la spécialité
                    if (utilisateur.getRole() == Role.technicien && specialite != null) {
                        // Ajouter la spécialité pour le technicien dans la base de données
                        String technicienQuery = "INSERT INTO technicien (id_technicien, specialite) VALUES (?, ?)";
                        try {
                            PreparedStatement technicienStmt = cnx.prepareStatement(technicienQuery);
                            technicienStmt.setInt(1, utilisateur.getId_utilisateur()); // Référence à l'ID de l'utilisateur
                            technicienStmt.setString(2, specialite.toString()); // Spécialité
                            int rowsAffected = technicienStmt.executeUpdate();

                            if (rowsAffected > 0) {
                                System.out.println("✅ Technicien ajouté avec spécialité : " + specialite);
                            } else {
                                System.out.println("❌ Échec de l'ajout du technicien.");
                            }
                        } catch (SQLException e) {
                            System.out.println("❌ Erreur SQL : " + e.getMessage());
                        }
                    }

                    // Si le rôle est "responsable", ajouter les modules
                    if (utilisateur.getRole() == Role.responsable && !modules.isEmpty()) {
                        // Convertir la liste des modules en une chaîne séparée par des virgules
                        String modulesStr = String.join(", ", modules);

                        // Enregistrer le responsable et les modules dans la base de données
                        String responsableQuery = "INSERT INTO responsable (id_responsable, modules) VALUES (?, ?)";
                        try {
                            PreparedStatement responsableStmt = cnx.prepareStatement(responsableQuery);
                            responsableStmt.setInt(1, utilisateur.getId_utilisateur());  // Référence à l'ID de l'utilisateur
                            responsableStmt.setString(2, modulesStr);  // Utiliser la chaîne de modules
                            int rowsAffected = responsableStmt.executeUpdate();

                            if (rowsAffected > 0) {
                                System.out.println("✅ Modules ajoutés avec succès pour le responsable.");
                            } else {
                                System.out.println("❌ Échec de l'ajout des modules pour le responsable.");
                            }
                        } catch (SQLException e) {
                            System.out.println("❌ Erreur SQL : " + e.getMessage());
                        }
                    }

                    // Si le rôle est "citoyen", ajouter le zoneId
                    if (utilisateur.getRole() == Role.citoyen) {
                        // Ajouter le citoyen avec son zoneId dans la base de données
                        String citoyenQuery = "INSERT INTO citoyen (id_citoyen, zoneId) VALUES (?, ?)";
                        try {
                            PreparedStatement citoyenStmt = cnx.prepareStatement(citoyenQuery);
                            citoyenStmt.setInt(1, utilisateur.getId_utilisateur()); // Référence à l'ID de l'utilisateur
                            citoyenStmt.setInt(2, zoneId);  // ZoneId pour le citoyen
                            int rowsAffected = citoyenStmt.executeUpdate();

                            if (rowsAffected > 0) {
                                System.out.println("✅ Citoyen ajouté avec zoneId : " + zoneId);
                            } else {
                                System.out.println("❌ Échec de l'ajout du citoyen.");
                            }
                        } catch (SQLException e) {
                            System.out.println("❌ Erreur SQL : " + e.getMessage());
                        }
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("❌ Erreur SQL : " + e.getMessage());
        }
    }




    public void deleteById(int id) {
        String deleteResponsableQry = "DELETE FROM responsable WHERE id_responsable = ?";
        String deleteTechnicienQry = "DELETE FROM technicien WHERE id_technicien = ?";
        String deleteCitoyenQry = "DELETE FROM citoyen WHERE id_citoyen = ?";  // Requête pour supprimer dans la table citoyen
        String deleteUtilisateurQry = "DELETE FROM utilisateur WHERE id_utilisateur = ?";

        try {
            // Vérifier si l'utilisateur existe dans la table Utilisateur
            String checkUtilisateurQry = "SELECT role FROM utilisateur WHERE id_utilisateur = ?";
            PreparedStatement checkUtilisateurStmt = cnx.prepareStatement(checkUtilisateurQry);
            checkUtilisateurStmt.setInt(1, id);
            ResultSet rs = checkUtilisateurStmt.executeQuery();

            if (rs.next()) {
                String role = rs.getString("role");

                // Supprimer d'abord de la table spécifique (technicien, responsable, citoyen)
                if ("technicien".equalsIgnoreCase(role)) {
                    PreparedStatement pstmTechnicien = cnx.prepareStatement(deleteTechnicienQry);
                    pstmTechnicien.setInt(1, id);
                    int affectedRowsTechnicien = pstmTechnicien.executeUpdate();

                    if (affectedRowsTechnicien > 0) {
                        System.out.println("✅ Technicien avec l'ID " + id + " supprimé de la table Technicien.");
                    }
                } else if ("responsable".equalsIgnoreCase(role)) {
                    PreparedStatement pstmResponsable = cnx.prepareStatement(deleteResponsableQry);
                    pstmResponsable.setInt(1, id);
                    int affectedRowsResponsable = pstmResponsable.executeUpdate();

                    if (affectedRowsResponsable > 0) {
                        System.out.println("✅ Responsable avec l'ID " + id + " supprimé de la table Responsable.");
                    }
                } else if ("citoyen".equalsIgnoreCase(role)) {  // Si l'utilisateur est un citoyen
                    PreparedStatement pstmCitoyen = cnx.prepareStatement(deleteCitoyenQry);
                    pstmCitoyen.setInt(1, id);
                    int affectedRowsCitoyen = pstmCitoyen.executeUpdate();

                    if (affectedRowsCitoyen > 0) {
                        System.out.println("✅ Citoyen avec l'ID " + id + " supprimé de la table Citoyen.");
                    }
                }

                // Suppression de l'utilisateur de la table Utilisateur
                PreparedStatement pstmUtilisateur = cnx.prepareStatement(deleteUtilisateurQry);
                pstmUtilisateur.setInt(1, id);
                int affectedRowsUtilisateur = pstmUtilisateur.executeUpdate();

                if (affectedRowsUtilisateur > 0) {
                    System.out.println("✅ Utilisateur avec l'ID " + id + " supprimé de la table Utilisateur.");
                }
            } else {
                System.out.println("⚠️ Aucun utilisateur trouvé avec l'ID " + id + ".");
            }

        } catch (SQLException e) {
            System.out.println("❌ Erreur SQL lors de la suppression : " + e.getMessage());
        }
    }


    public List<utilisateur> getAllUtilisateurs() {
        List<utilisateur> utilisateurs = new ArrayList<>();
        String qry = "SELECT * FROM utilisateur";  // On récupère toutes les colonnes de la table utilisateur

        try {
            Statement stm = cnx.createStatement();
            ResultSet rs = stm.executeQuery(qry);
            while (rs.next()) {
                utilisateur u = new utilisateur();
                u.setId_utilisateur(rs.getInt("id_utilisateur"));  // Remplacer 'id' par 'id_utilisateur'
                u.setNom(rs.getString("nom"));
                u.setPrenom(rs.getString("prenom"));
                u.setEmail(rs.getString("email"));
                u.setMotdepasse(rs.getString("motdepasse"));
                u.setDateinscription(rs.getDate("dateinscription"));

                // Récupération du rôle depuis la base de données
                String roleStr = rs.getString("role").trim().toLowerCase();  // On s'assure que le rôle est en minuscules pour comparaison
                Role role;

                try {
                    // On récupère le rôle et on le convertit en objet enum
                    role = Role.valueOf(roleStr);
                } catch (IllegalArgumentException e) {
                    System.out.println("⚠️ Rôle inconnu : " + roleStr);
                    continue;  // Ignore l'utilisateur si le rôle est invalide
                }

                // On assigne le rôle à l'utilisateur
                u.setRole(role);

                utilisateurs.add(u);
            }
        } catch (SQLException e) {
            System.out.println("❌ Erreur SQL : " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("❌ Erreur : Valeur de rôle invalide dans la base de données.");
        }

        return utilisateurs;
    }




    public void updateField(int id, String fieldName, String newValue) {
        String qry;
        boolean isZoneField = fieldName.equalsIgnoreCase("zoneId"); // Vérifier si le champ à modifier est `zoneId`

        if (isZoneField) {
            // Mise à jour de la zone dans la table `citoyen`
            qry = "UPDATE citoyen SET zoneId = ? WHERE id_citoyen = ?";
        } else {
            // Mise à jour des autres informations dans la table `utilisateur`
            qry = "UPDATE utilisateur SET " + fieldName + " = ? WHERE id_utilisateur = ?";
        }

        try {
            PreparedStatement pstm = cnx.prepareStatement(qry);

            // Vérifier si la valeur à modifier est un entier (zoneId)
            if (isZoneField) {
                pstm.setInt(1, Integer.parseInt(newValue)); // Convertir en entier pour zoneId
            } else {
                pstm.setString(1, newValue); // Insérer en tant que String pour les autres champs
            }

            pstm.setInt(2, id); // ID du citoyen/utilisateur

            int affectedRows = pstm.executeUpdate();

            if (affectedRows > 0) {
                System.out.println("✅ " + fieldName + " mis à jour avec succès pour l'utilisateur/citoyen ID " + id);
            } else {
                System.out.println("❌ Aucun enregistrement trouvé avec l'ID " + id);
            }

        } catch (SQLException e) {
            System.out.println("❌ Erreur SQL lors de la mise à jour : " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("❌ Erreur : La valeur pour zoneId doit être un nombre valide.");
        }
    }
    public void updateFieldResponsable(int idResponsable, String fieldName, String newValue) {
        String qry = "UPDATE responsable SET " + fieldName + " = ? WHERE id_responsable = ?";

        try (PreparedStatement pstm = cnx.prepareStatement(qry)) {
            // Préparer et exécuter la requête
            pstm.setString(1, newValue);
            pstm.setInt(2, idResponsable);

            int affectedRows = pstm.executeUpdate();

            if (affectedRows > 0) {
                System.out.println("✅ " + fieldName + " mis à jour avec succès pour le responsable ID " + idResponsable);
            } else {
                System.out.println("❌ Aucun responsable trouvé avec l'ID " + idResponsable);
            }
        } catch (SQLException e) {
            System.out.println("❌ Erreur SQL lors de la mise à jour : " + e.getMessage());
        }
    }

    public void updateFieldTechnicien(int idTechnicien, String fieldName, String newValue) {

        String qry = "UPDATE technicien SET " + fieldName + " = ? WHERE id_technicien = ?";

        try (PreparedStatement pstm = cnx.prepareStatement(qry)) {
            // Conversion spéciale pour le champ "specialite"
            if (fieldName.equalsIgnoreCase("specialite")) {
                try {
                    Specialite selectedSpecialite = Specialite.valueOf(newValue); // Valider et convertir en Enum
                    pstm.setString(1, selectedSpecialite.name()); // Utiliser le nom Enum en base de données
                } catch (IllegalArgumentException e) {
                    throw new IllegalArgumentException("Spécialité invalide : " + newValue);
                }
            } else {
                pstm.setString(1, newValue); // Autres champs comme String
            }

            pstm.setInt(2, idTechnicien); // ID du technicien
            int affectedRows = pstm.executeUpdate();

            if (affectedRows > 0) {
                System.out.println("✅ " + fieldName + " mis à jour avec succès pour le technicien ID " + idTechnicien);
            } else {
                System.out.println("❌ Aucun technicien trouvé avec l'ID " + idTechnicien);
            }
        } catch (SQLException e) {
            System.out.println("❌ Erreur SQL lors de la mise à jour : " + e.getMessage());
        }
    }





    // Méthode pour récupérer un utilisateur par son ID
        public utilisateur getUtilisateurById(int userId) {
            utilisateur user = null;

            String query = "SELECT * FROM utilisateur WHERE id_utilisateur = ?";

            try (Connection connection = MyDatabase.getConnection();
                 PreparedStatement stmt = connection.prepareStatement(query)) {

                stmt.setInt(1, userId); // Set l'ID dans la requête

                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        // Si un utilisateur est trouvé
                        user = new utilisateur();
                        user.setId_utilisateur(rs.getInt("id_utilisateur"));
                        user.setNom(rs.getString("nom"));
                        user.setEmail(rs.getString("email"));
                        // Ajouter d'autres champs si nécessaire
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                // Gérer les exceptions ici (par exemple, afficher un message d'erreur)
            }

            return user; // Retourner l'utilisateur trouvé ou null s'il n'existe pas
        }/*
    public boolean isEmailExists(String email) {
        // Connexion à la base de données
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/pi3a7")) {
            // Requête SQL pour vérifier si l'email existe
            String query = "SELECT COUNT(*) FROM utilisateur WHERE email = ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, email);

                // Exécuter la requête
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        // Si le résultat est supérieur à 0, l'email existe déjà
                        return resultSet.getInt(1) > 0;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Gérer l'exception ou afficher une alerte si nécessaire
        }

        return false; // Si une erreur se produit, supposez que l'email n'existe pas
    }*/

    public void updateSpecialite(int idUtilisateur, Specialite selectedSpecialite) {
    }

}


