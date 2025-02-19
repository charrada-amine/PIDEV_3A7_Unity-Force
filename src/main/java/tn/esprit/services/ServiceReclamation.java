package tn.esprit.services;

import tn.esprit.interfaces.IService;
import tn.esprit.models.Reclamation;
import tn.esprit.utils.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceReclamation implements IService<Reclamation> {
    private Connection cnx;

    public ServiceReclamation() {
        cnx = MyDatabase.getInstance().getCnx();
    }

    @Override
    public void add(Reclamation reclamation) {
        String qry = "INSERT INTO `reclamation`(`description`, `dateReclamation`, `heureReclamation`, `statut`, `lampadaireId`, `citoyenId`) VALUES (?,?,?,?,?,?)";
        try {
            PreparedStatement pstm = cnx.prepareStatement(qry, Statement.RETURN_GENERATED_KEYS);
            pstm.setString(1, reclamation.getDescription());
            pstm.setDate(2, reclamation.getDateReclamation());
            pstm.setTime(3, reclamation.getHeureReclamation());
            pstm.setString(4, reclamation.getStatut());
            pstm.setInt(5, reclamation.getLampadaireId());
            pstm.setInt(6, reclamation.getCitoyenId());

            pstm.executeUpdate();

<<<<<<< HEAD
            // Retrieve the generated ID_reclamation
            ResultSet generatedKeys = pstm.getGeneratedKeys();
            if (generatedKeys.next()) {
                reclamation.setId(generatedKeys.getInt(1)); // Set the generated ID
=======
            // Mise à jour de l'ID généré
            ResultSet generatedKeys = pstm.getGeneratedKeys();
            if (generatedKeys.next()) {
                reclamation.setID_reclamation(generatedKeys.getInt(1)); // Correction ici
>>>>>>> origin/AzizBenAmmar/GestionInterventions_Reclamations
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public List<Reclamation> getAll() {
        List<Reclamation> reclamations = new ArrayList<>();
        String qry = "SELECT * FROM `reclamation`";

        try {
            Statement stm = cnx.createStatement();
            ResultSet rs = stm.executeQuery(qry);

            while (rs.next()) {
                Reclamation r = new Reclamation();
<<<<<<< HEAD
                r.setId(rs.getInt("ID_reclamation"));
=======
                r.setID_reclamation(rs.getInt("ID_reclamation")); // Correction ici
>>>>>>> origin/AzizBenAmmar/GestionInterventions_Reclamations
                r.setDescription(rs.getString("description"));
                r.setDateReclamation(rs.getDate("dateReclamation"));
                r.setHeureReclamation(rs.getTime("heureReclamation"));
                r.setStatut(rs.getString("statut"));
                r.setLampadaireId(rs.getInt("lampadaireId"));
                r.setCitoyenId(rs.getInt("citoyenId"));

                reclamations.add(r);
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return reclamations;
    }

    @Override
    public void update(Reclamation reclamation) {
        try {
            String qry = "UPDATE `reclamation` SET `description`=?, `dateReclamation`=?, `heureReclamation`=?, `statut`=?, `lampadaireId`=?, `citoyenId`=? WHERE `ID_reclamation`=?";
            PreparedStatement pstm = cnx.prepareStatement(qry);
            pstm.setString(1, reclamation.getDescription());
            pstm.setDate(2, reclamation.getDateReclamation());
            pstm.setTime(3, reclamation.getHeureReclamation());
            pstm.setString(4, reclamation.getStatut());
            pstm.setInt(5, reclamation.getLampadaireId());
            pstm.setInt(6, reclamation.getCitoyenId());
<<<<<<< HEAD
            pstm.setInt(7, reclamation.getId());
=======
            pstm.setInt(7, reclamation.getID_reclamation()); // Correction ici
>>>>>>> origin/AzizBenAmmar/GestionInterventions_Reclamations

            int rowsUpdated = pstm.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("✅ Reclamation mise à jour avec succès !");
            } else {
                System.out.println("❌ Aucune reclamation trouvée avec cet ID !");
            }
        } catch (SQLException ex) {
            System.out.println("❌ Erreur lors de la mise à jour : " + ex.getMessage());
        }
    }

    @Override
    public void delete(Reclamation reclamation) {
        try {
<<<<<<< HEAD
            String qry = "DELETE FROM `reclamation` WHERE `ID_reclamation`=?";
            PreparedStatement pstm = cnx.prepareStatement(qry);
            pstm.setInt(1, reclamation.getId());

            int rowsDeleted = pstm.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("✅ Reclamation supprimée avec succès !");
            } else {
                System.out.println("❌ Aucune reclamation trouvée avec cet ID !");
            }
        } catch (SQLException ex) {
            System.out.println("❌ Erreur lors de la suppression : " + ex.getMessage());
        }
    }
=======
            // Vérifier les dépendances avant suppression
            if (hasDependencies(reclamation.getID_reclamation())) {
                throw new SQLException("Impossible de supprimer : existence d'interventions liées");
            }

            String qry = "DELETE FROM `reclamation` WHERE `ID_reclamation`=?";
            PreparedStatement pstm = cnx.prepareStatement(qry);
            pstm.setInt(1, reclamation.getID_reclamation());

            int rowsDeleted = pstm.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("✅ Réclamation supprimée avec succès !");
            }
        } catch (SQLException ex) {
            System.out.println("❌ Erreur lors de la suppression : " + ex.getMessage());
            throw new RuntimeException(ex.getMessage());
        }
    }

    private boolean hasDependencies(int idReclamation) throws SQLException {
        String qry = "SELECT COUNT(*) FROM intervention WHERE ID_reclamation = ?";
        PreparedStatement pstm = cnx.prepareStatement(qry);
        pstm.setInt(1, idReclamation);
        ResultSet rs = pstm.executeQuery();
        return rs.next() && rs.getInt(1) > 0;
    }
>>>>>>> origin/AzizBenAmmar/GestionInterventions_Reclamations
}