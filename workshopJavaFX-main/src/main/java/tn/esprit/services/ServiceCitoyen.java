package tn.esprit.services;

import tn.esprit.models.Role;
import tn.esprit.models.citoyen;
import tn.esprit.utils.MyDatabase;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceCitoyen {
    private Connection cnx;

    public ServiceCitoyen() {
        cnx = MyDatabase.getInstance().getCnx();
    }

    // Ajouter un citoyen
    /*public void addCitoyen(citoyen citoyen) {
        String qry = "INSERT INTO citoyen (id_citoyen, zoneId) VALUES (?, ?)";

        try {
            PreparedStatement pstm = cnx.prepareStatement(qry, Statement.RETURN_GENERATED_KEYS);
            pstm.setInt(1, citoyen.getId_utilisateur());  // Utiliser l'ID utilisateur
            pstm.setInt(2, citoyen.getZoneId());  // Zone de citoyen

            int affectedRows = pstm.executeUpdate();
            if (affectedRows > 0) {
                ResultSet generatedKeys = pstm.getGeneratedKeys();
                if (generatedKeys.next()) {
                    citoyen.setId_utilisateur(generatedKeys.getInt(1)); // Mise à jour de l'ID de l'utilisateur
                }
                System.out.println("✅ Citoyen ajouté avec succès !");
            } else {
                System.out.println("❌ Échec de l'ajout du citoyen.");
            }
        } catch (SQLException e) {
            System.out.println("❌ Erreur SQL : " + e.getMessage());
        }
    }
*/
    public citoyen getCitoyenById(int idCitoyen) {
        citoyen citoyen = null;

        // Correction de la requête SQL
        String qry = "SELECT c.id_citoyen, u.nom, u.prenom, u.email, u.motdepasse, u.dateInscription, c.zoneId " +
                "FROM utilisateur u " +
                "JOIN citoyen c ON u.id_utilisateur = c.id_citoyen " + // Corrigé : Relation correcte
                "WHERE c.id_citoyen = ?";

        try {
            PreparedStatement pstm = cnx.prepareStatement(qry);
            pstm.setInt(1, idCitoyen);
            ResultSet rs = pstm.executeQuery();

            if (rs.next()) {
                // Construction de l'objet citoyen avec les données récupérées
                citoyen = new citoyen(
                        rs.getString("nom"),
                        rs.getString("prenom"),
                        rs.getString("email"),
                        rs.getString("motdepasse"),
                        rs.getDate("dateInscription"),
                        rs.getInt("zoneId")
                );
            } else {
                System.out.println("⚠️ Citoyen avec l'ID " + idCitoyen + " introuvable.");
            }
        } catch (SQLException e) {
            System.out.println("❌ Erreur SQL lors de la récupération du citoyen : " + e.getMessage());
        }

        return citoyen;
    }



    public List<citoyen> getAllCitoyens() {
        List<citoyen> citoyens = new ArrayList<>();
        String qry = "SELECT * FROM citoyen JOIN utilisateur ON citoyen.id_citoyen = utilisateur.id_utilisateur";  // Jointure avec la table utilisateur

        try {
            Statement stm = cnx.createStatement();
            ResultSet rs = stm.executeQuery(qry);

            while (rs.next()) {
                try {
                    // Récupérer l'ID de l'utilisateur et citoyen
                    int id = rs.getInt("id_utilisateur"); // ID de l'utilisateur
                    String nom = rs.getString("nom");
                    String prenom = rs.getString("prenom");
                    String email = rs.getString("email");
                    String motdepasse = rs.getString("motdepasse");
                    Date dateInscription = rs.getDate("dateinscription");
                    int zoneId = rs.getInt("zoneId");  // Récupérer la zone associée au citoyen
                    String roleStr = rs.getString("role"); // Récupérer le rôle



                    // Création de l'objet citoyen et attribution des champs
                    citoyen c = new citoyen(
                            nom,                             // nom
                            prenom,                          // prenom
                            email,                           // email
                            motdepasse,                      // motdepasse
                            dateInscription,                 // dateInscription
                            zoneId                           // zoneId
                    );

                    // Attribution du rôle et de l'ID au citoyen
// Attribution du rôle "technicien"
                    c.setRole(Role.citoyen);
                    c.setId_utilisateur(id);

                    // Affichage des informations du citoyen
                    System.out.println("Citoyen ID: " + id);
                    System.out.println("Nom: " + nom);
                    System.out.println("Prénom: " + prenom);
                    System.out.println("Email: " + email);
                    System.out.println("Mot de passe: " + motdepasse);
                    System.out.println("Date d'inscription: " + dateInscription);
                    System.out.println("Zone ID: " + zoneId);
                    System.out.println("----------------------------");

                    // Ajout du citoyen à la liste
                    citoyens.add(c);

                } catch (Exception e) {
                    System.out.println("❌ Erreur lors du traitement d'un citoyen : " + e.getMessage());
                }
            }
        } catch (SQLException e) {
            System.out.println("❌ Erreur SQL : " + e.getMessage());
        }

        return citoyens;
    }


    public void updateZone(int id, int newZoneId) {
        String qryCitoyen = "UPDATE citoyen SET zoneId = ? WHERE id_citoyen = ?"; // Requête pour citoyen

        try {
            // Préparer la mise à jour pour le citoyen
            PreparedStatement pstmCitoyen = cnx.prepareStatement(qryCitoyen);
            pstmCitoyen.setInt(1, newZoneId); // Passer la nouvelle zone
            pstmCitoyen.setInt(2, id); // Passer l'ID du citoyen

            // Exécuter la mise à jour du citoyen
            int affectedRowsCitoyen = pstmCitoyen.executeUpdate();

            if (affectedRowsCitoyen > 0) {
                System.out.println("✅ Zone mise à jour pour le citoyen avec l'ID " + id);
            } else {
                System.out.println("❌ Aucun citoyen trouvé avec l'ID " + id);
            }

        } catch (SQLException e) {
            System.out.println("❌ Erreur SQL lors de la mise à jour de la zone : " + e.getMessage());
        }
    }


    public void deleteById(int id) {
        // Requête pour récupérer l'ID de l'utilisateur associé au citoyen
        String getIdUtilisateurQry = "SELECT id_citoyen FROM citoyen WHERE id_citoyen = ?";

        // Requête pour supprimer le citoyen
        String qryCitoyen = "DELETE FROM citoyen WHERE id_citoyen = ?";

        // Requête pour supprimer l'utilisateur
        String qryUtilisateur = "DELETE FROM utilisateur WHERE id_utilisateur = ?";

        try {
            // Étape 1 : Récupérer l'id_utilisateur du citoyen
            PreparedStatement getIdUtilisateurStmt = cnx.prepareStatement(getIdUtilisateurQry);
            getIdUtilisateurStmt.setInt(1, id);
            ResultSet rs = getIdUtilisateurStmt.executeQuery();

            if (rs.next()) {
                int idUtilisateur = rs.getInt("id_citoyen");
                System.out.println("🔍 ID utilisateur associé au citoyen : " + idUtilisateur);

                // Étape 2 : Supprimer le citoyen
                PreparedStatement pstmCitoyen = cnx.prepareStatement(qryCitoyen);
                pstmCitoyen.setInt(1, id);
                int affectedRowsCitoyen = pstmCitoyen.executeUpdate();

                if (affectedRowsCitoyen > 0) {
                    System.out.println("✅ Citoyen avec l'ID " + id + " supprimé de la table 'citoyen'.");

                    // Étape 3 : Supprimer l'utilisateur associé au citoyen
                    PreparedStatement pstmUtilisateur = cnx.prepareStatement(qryUtilisateur);
                    pstmUtilisateur.setInt(1, idUtilisateur);
                    int affectedRowsUtilisateur = pstmUtilisateur.executeUpdate();

                    if (affectedRowsUtilisateur > 0) {
                        System.out.println("✅ Utilisateur avec l'ID " + idUtilisateur + " supprimé de la table 'utilisateur'.");
                    } else {
                        System.out.println("⚠️ Aucun utilisateur trouvé avec l'ID " + idUtilisateur + " dans la table 'utilisateur'.");
                    }
                } else {
                    System.out.println("⚠️ Aucun citoyen trouvé avec l'ID " + id + " dans la table 'citoyen'.");
                }
            } else {
                System.out.println("⚠️ Aucun citoyen trouvé avec l'ID " + id + " dans la table 'citoyen'.");
            }
        } catch (SQLException e) {
            System.out.println("❌ Erreur SQL lors de la suppression : " + e.getMessage());
        }
    }



}
