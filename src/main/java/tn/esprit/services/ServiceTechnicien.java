package tn.esprit.services;

import tn.esprit.models.technicien;
import tn.esprit.models.Specialite;
import tn.esprit.utils.MyDatabase;
import tn.esprit.Enumerations.Role;


import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceTechnicien {
    private Connection cnx;

    public ServiceTechnicien() {
        cnx = MyDatabase.getInstance().getCnx();
    }
    /*public void add(technicien technicien) {
        String qryUtilisateur = "INSERT INTO utilisateur (nom, prenom, email, motdepasse, role, dateinscription) VALUES (?, ?, ?, ?, ?, ?)";

        try {
            cnx.setAutoCommit(false); // Désactiver le commit automatique

            PreparedStatement pstmUtilisateur = cnx.prepareStatement(qryUtilisateur, Statement.RETURN_GENERATED_KEYS);

            pstmUtilisateur.setString(1, technicien.getNom());
            pstmUtilisateur.setString(2, technicien.getPrenom());
            pstmUtilisateur.setString(3, technicien.getEmail());
            pstmUtilisateur.setString(4, technicien.getMotdepasse());
            pstmUtilisateur.setString(5, "technicien");  // Rôle
            pstmUtilisateur.setDate(6, new java.sql.Date(technicien.getDateinscription().getTime()));

            int affectedRowsUtilisateur = pstmUtilisateur.executeUpdate();
            if (affectedRowsUtilisateur > 0) {
                ResultSet generatedKeys = pstmUtilisateur.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int id = generatedKeys.getInt(1);  // Récupérer l'ID de l'utilisateur
                    technicien.setId_utilisateur(id);  // Assurez-vous d'affecter l'ID au technicien

                    // Insertion dans la table `technicien`
                    String qryTechnicien = "INSERT INTO technicien (id, nom, prenom, email, motdepasse, role, dateinscription, specialite) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
                    PreparedStatement pstmTechnicien = cnx.prepareStatement(qryTechnicien);
                    pstmTechnicien.setInt(1, id);  // ID de l'utilisateur
                    pstmTechnicien.setString(2, technicien.getNom());
                    pstmTechnicien.setString(3, technicien.getPrenom());
                    pstmTechnicien.setString(4, technicien.getEmail());
                    pstmTechnicien.setString(5, technicien.getMotdepasse());
                    pstmTechnicien.setString(6, "technicien");  // Rôle
                    pstmTechnicien.setDate(7, new java.sql.Date(technicien.getDateinscription().getTime()));
                    pstmTechnicien.setString(8, technicien.getSpecialite().name());

                    int affectedRowsTechnicien = pstmTechnicien.executeUpdate();
                    if (affectedRowsTechnicien > 0) {
                        cnx.commit();  // Commit de la transaction
                        System.out.println("✅ Technicien et utilisateur ajoutés avec succès !");
                        // Afficher les informations du technicien ajouté
                        System.out.println("Technicien ajouté : " + technicien);
                    } else {
                        cnx.rollback();  // Annuler la transaction si l'ajout échoue
                        System.out.println("❌ Échec de l'ajout du technicien.");
                    }
                }
            } else {
                System.out.println("❌ Échec de l'ajout de l'utilisateur.");
                cnx.rollback();  // Annuler la transaction si l'ajout échoue
            }
        } catch (SQLException e) {
            try {
                cnx.rollback();  // Annuler la transaction en cas d'erreur
            } catch (SQLException ex) {
                System.out.println("❌ Erreur lors de l'annulation de la transaction : " + ex.getMessage());
            }
            System.out.println("❌ Erreur SQL : " + e.getMessage());
        } finally {
            try {
                cnx.setAutoCommit(true);  // Réactiver le commit automatique
            } catch (SQLException e) {
                System.out.println("❌ Erreur lors de la restauration du commit automatique : " + e.getMessage());
            }
        }
    }
*/

    public void deleteById(int id) {
        String getIdUtilisateurQry = "SELECT id_technicien FROM technicien WHERE id_technicien = ?";

        // Requête pour supprimer le technicien
        String qryTechnicien = "DELETE FROM technicien WHERE id_technicien = ?";

        // Requête pour supprimer l'utilisateur
        String qryUtilisateur = "DELETE FROM utilisateur WHERE id_utilisateur = ?";

        try {
            // Étape 1 : Vérifiez que l'ID du technicien existe
            PreparedStatement getIdUtilisateurStmt = cnx.prepareStatement(getIdUtilisateurQry);
            getIdUtilisateurStmt.setInt(1, id);
            ResultSet rs = getIdUtilisateurStmt.executeQuery();

            if (rs.next()) {
                int idUtilisateur = rs.getInt("id_technicien"); // ID utilisateur est le même que l'ID technicien
                System.out.println("🔍 ID utilisateur associé au technicien : " + idUtilisateur);

                // Étape 2 : Supprimer le technicien
                PreparedStatement pstmTechnicien = cnx.prepareStatement(qryTechnicien);
                pstmTechnicien.setInt(1, id);
                int affectedRowsTechnicien = pstmTechnicien.executeUpdate();

                if (affectedRowsTechnicien > 0) {
                    System.out.println("✅ Technicien avec l'ID " + id + " supprimé de la table 'technicien'.");

                    // Étape 3 : Supprimer l'utilisateur associé au technicien
                    PreparedStatement pstmUtilisateur = cnx.prepareStatement(qryUtilisateur);
                    pstmUtilisateur.setInt(1, idUtilisateur);
                    int affectedRowsUtilisateur = pstmUtilisateur.executeUpdate();

                    if (affectedRowsUtilisateur > 0) {
                        System.out.println("✅ Utilisateur avec l'ID " + idUtilisateur + " supprimé de la table 'utilisateur'.");
                    } else {
                        System.out.println("⚠️ Aucun utilisateur trouvé avec l'ID " + idUtilisateur + " dans la table 'utilisateur'.");
                    }
                } else {
                    System.out.println("⚠️ Aucun technicien trouvé avec l'ID " + id + " dans la table 'technicien'.");
                }
            } else {
                System.out.println("⚠️ Aucun technicien trouvé avec l'ID " + id + " dans la table 'technicien'.");
            }
        } catch (SQLException e) {
            System.out.println("❌ Erreur SQL lors de la suppression : " + e.getMessage());
        }
    }


    public List<technicien> getAllTechniciens() {
        List<technicien> techniciens = new ArrayList<>();
        String qry = "SELECT * FROM technicien JOIN utilisateur ON technicien.id_technicien = utilisateur.id_utilisateur";  // Jointure avec la bonne colonne

        try {
            Statement stm = cnx.createStatement();
            ResultSet rs = stm.executeQuery(qry);

            while (rs.next()) {
                try {
                    // Récupérer l'ID de l'utilisateur et technicien
                    int id = rs.getInt("id_utilisateur"); // ID de l'utilisateur
                    String nom = rs.getString("nom");
                    String prenom = rs.getString("prenom");
                    String email = rs.getString("email");
                    String motdepasse = rs.getString("motdepasse");
                    Date dateInscription = rs.getDate("dateinscription");

                    // Récupération de la spécialité
                    Specialite specialite;
                    try {
                        specialite = Specialite.valueOf(rs.getString("specialite").trim().toLowerCase());
                    } catch (IllegalArgumentException e) {
                        System.out.println("⚠️ Spécialité inconnue : " + rs.getString("specialite"));
                        continue; // Ignore cet enregistrement si la spécialité est invalide
                    }

                    // Création de l'objet technicien et attribution des champs
                    technicien t = new technicien(
                            nom,                            // nom
                            prenom,                         // prenom
                            email,                          // email
                            motdepasse,                     // motdepasse
                            dateInscription,                // dateInscription
                            specialite                      // specialite
                    );

                    // Attribution du rôle "technicien"
                    t.setRole(Role.technicien);

                    // Assignation de l'ID au technicien
                    t.setId_utilisateur(id);

                    // Affichage des informations du technicien avec la spécialité
                    System.out.println("Technicien ID: " + id);
                    System.out.println("Nom: " + nom);
                    System.out.println("Prénom: " + prenom);
                    System.out.println("Email: " + email);
                    System.out.println("Mot de passe: " + motdepasse);
                    System.out.println("Date d'inscription: " + dateInscription);
                    System.out.println("Spécialité: " + specialite);
                    System.out.println("----------------------------");

                    // Ajout du technicien à la liste
                    techniciens.add(t);

                } catch (Exception e) {
                    System.out.println("❌ Erreur lors du traitement d'un technicien : " + e.getMessage());
                }
            }
        } catch (SQLException e) {
            System.out.println("❌ Erreur SQL : " + e.getMessage());
        }

        return techniciens;
    }






    public boolean isEmailExists(String email) {
        String url = "jdbc:mysql://localhost:3306/pi3a7";
        String username = "root"; // Remplacez par votre nom d'utilisateur
        String password = ""; // Remplacez par votre mot de passe

        String query = "SELECT COUNT(*) FROM utilisateur WHERE email = ?";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, email);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la vérification de l'email : " + e.getMessage());
        }
        return false;
    }


    public technicien getTechnicienById(int idTechnicien) {
        technicien technicien = null;

        // Requête SQL pour récupérer un technicien
        String qry = "SELECT t.id_technicien, u.nom, u.prenom, u.email, u.motdepasse,u.dateInscription, t.specialite " +
                "FROM utilisateur u " +
                "JOIN technicien t ON u.id_utilisateur = t.id_technicien " +
                "WHERE t.id_technicien = ?";

        try {
            PreparedStatement pstm = cnx.prepareStatement(qry);
            pstm.setInt(1, idTechnicien);
            ResultSet rs = pstm.executeQuery();

            if (rs.next()) {
                // Construction de l'objet technicien avec les données récupérées
                technicien = new technicien(
                        rs.getString("nom"),
                        rs.getString("prenom"),
                        rs.getString("email"),
                        rs.getString("motdepasse"),
                        rs.getDate("dateInscription"),

                        Specialite.valueOf(rs.getString("specialite")) // Convertir le texte en enum
                );
            } else {
                System.out.println("⚠️ Technicien avec l'ID " + idTechnicien + " introuvable.");
            }
        } catch (SQLException e) {
            System.out.println("❌ Erreur SQL lors de la récupération du technicien : " + e.getMessage());
        }

        return technicien;
    }






}
