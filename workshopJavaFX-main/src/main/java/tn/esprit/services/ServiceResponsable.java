package tn.esprit.services;

import tn.esprit.utils.MyDatabase;
import tn.esprit.models.Role;
import tn.esprit.models.responsable;
import java.util.Arrays;

import java.sql.*;

import java.util.ArrayList;
import java.util.List;
public class ServiceResponsable {
    private Connection cnx;

    public ServiceResponsable() {
        cnx = MyDatabase.getInstance().getCnx();
    }

// Méthode pour ajouter un responsable
/*public void addResponsable(responsable resp) {
    String qry = "INSERT INTO responsable (id_utilisateur, modules) VALUES (?, ?)";

    try {
        PreparedStatement pstm = cnx.prepareStatement(qry);
        pstm.setInt(1, resp.getId_utilisateur());  // L'ID de l'utilisateur
        pstm.setString(2, String.join(",", resp.getModules()));  // Convertir la liste de modules en une chaîne

        int affectedRows = pstm.executeUpdate();
        if (affectedRows > 0) {
            System.out.println("✅ Responsable ajouté avec succès !");
        } else {
            System.out.println("❌ Échec de l'ajout du responsable.");
        }
    } catch (SQLException e) {
        System.out.println("❌ Erreur SQL : " + e.getMessage());
    }
*/
public responsable getResponsableById(int idResponsable) {
    responsable responsable = null;

    // Requête SQL pour récupérer les informations d'un responsable
    String qry = "SELECT r.id_responsable, u.nom, u.prenom, u.email, u.motdepasse, u.dateInscription, r.modules " +
            "FROM utilisateur u " +
            "JOIN responsable r ON u.id_utilisateur = r.id_responsable " + // Corrigé : Relation correcte
            "WHERE r.id_responsable = ?";

    try {
        PreparedStatement pstm = cnx.prepareStatement(qry);
        pstm.setInt(1, idResponsable);
        ResultSet rs = pstm.executeQuery();

        if (rs.next()) {
            // Construction de l'objet responsable avec les données récupérées
            responsable = new responsable(
                    rs.getString("nom"),
                    rs.getString("prenom"),
                    rs.getString("email"),
                    rs.getString("motdepasse"),
                    rs.getDate("dateInscription"),
                    Arrays.asList(rs.getString("modules").split(",\\s*")) // Conversion des modules en liste
            );
        } else {
            System.out.println("⚠️ Responsable avec l'ID " + idResponsable + " introuvable.");
        }
    } catch (SQLException e) {
        System.out.println("❌ Erreur SQL lors de la récupération du responsable : " + e.getMessage());
    }

    return responsable;
}

    public List<responsable> getAllResponsables() {
        List<responsable> responsables = new ArrayList<>();
        String qry = "SELECT * FROM responsable JOIN utilisateur ON responsable.id_responsable = utilisateur.id_utilisateur";

        try {
            Statement stm = cnx.createStatement();
            ResultSet rs = stm.executeQuery(qry);

            while (rs.next()) {
                try {
                    int id = rs.getInt("id_utilisateur");
                    String nom = rs.getString("nom");
                    String prenom = rs.getString("prenom");
                    String email = rs.getString("email");
                    String motdepasse = rs.getString("motdepasse");
                    Date dateInscription = rs.getDate("dateinscription");
                    String modulesStr = rs.getString("modules"); // Ex: "gestion, finance, RH"

                    // ✅ Convertir la chaîne des modules en liste
                    List<String> moduleList = (modulesStr != null && !modulesStr.isEmpty()) ?
                            Arrays.asList(modulesStr.split(",\\s*")) :
                            new ArrayList<>(); // Gérer le cas où il n'y a pas de modules

                    // ✅ Créer l'objet responsable avec une liste de modules
                    responsable r = new responsable(nom, prenom, email, motdepasse, dateInscription, moduleList);
                    r.setRole(Role.responsable);
                    r.setId_utilisateur(id);

                    // ✅ Afficher les infos du responsable
                    System.out.println("Responsable ID: " + id);
                    System.out.println("Nom: " + nom);
                    System.out.println("Prénom: " + prenom);
                    System.out.println("Email: " + email);
                    System.out.println("Mot de passe: " + motdepasse);
                    System.out.println("Date d'inscription: " + dateInscription);
                    System.out.println("Modules: " + moduleList);
                    System.out.println("----------------------------");

                    responsables.add(r);

                } catch (Exception e) {
                    System.out.println("❌ Erreur lors du traitement d'un responsable : " + e.getMessage());
                }
            }
        } catch (SQLException e) {
            System.out.println("❌ Erreur SQL : " + e.getMessage());
        }

        return responsables;
    }

    public void deleteById(int id) {
        String deleteResponsableQry = "DELETE FROM responsable WHERE id_responsable = ?";
        String deleteTechnicienQry = "DELETE FROM technicien WHERE id_technicien = ?";
        String deleteUtilisateurQry = "DELETE FROM utilisateur WHERE id_utilisateur = ?";

        try {
            // Vérifier si l'utilisateur existe dans la table Utilisateur
            String checkUtilisateurQry = "SELECT role FROM utilisateur WHERE id_utilisateur = ?";
            PreparedStatement checkUtilisateurStmt = cnx.prepareStatement(checkUtilisateurQry);
            checkUtilisateurStmt.setInt(1, id);
            ResultSet rs = checkUtilisateurStmt.executeQuery();

            if (rs.next()) {
                String role = rs.getString("role");

                // Supprimer d'abord de la table spécifique (technicien ou responsable)
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
    public void updateModules(int id, List<String> newModules) {
        String qryResponsable = "UPDATE responsable SET modules = ? WHERE id_responsable = ?";

        try {
            // Convertir la liste des modules en une chaîne séparée par des virgules
            String modulesString = String.join(",", newModules);

            // Préparer la requête de mise à jour
            PreparedStatement pstmResponsable = cnx.prepareStatement(qryResponsable);
            pstmResponsable.setString(1, modulesString);
            pstmResponsable.setInt(2, id);

            // Exécuter la mise à jour du responsable
            int affectedRowsResponsable = pstmResponsable.executeUpdate();

            if (affectedRowsResponsable > 0) {
                System.out.println("✅ Modules mis à jour pour le responsable avec l'ID " + id);
            } else {
                System.out.println("❌ Aucun responsable trouvé avec l'ID " + id);
            }

        } catch (SQLException e) {
            System.out.println("❌ Erreur SQL lors de la mise à jour des modules : " + e.getMessage());
        }
    }
    public List<String> getModulesById(int id) {
        String qry = "SELECT modules FROM responsable WHERE id_responsable = ?";
        List<String> modules = new ArrayList<>();

        try {
            PreparedStatement pstm = cnx.prepareStatement(qry);
            pstm.setInt(1, id);
            ResultSet rs = pstm.executeQuery();

            if (rs.next()) {
                String modulesString = rs.getString("modules");
                if (modulesString != null && !modulesString.isEmpty()) {
                    modules = Arrays.asList(modulesString.split(",")); // Convertir la chaîne en liste
                }
            } else {
                System.out.println("❌ Aucun responsable trouvé avec l'ID " + id);
            }
        } catch (SQLException e) {
            System.out.println("❌ Erreur SQL lors de la récupération des modules : " + e.getMessage());
        }

        return modules;
    }













}