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

            // Retrieve the generated ID_reclamation
            ResultSet generatedKeys = pstm.getGeneratedKeys();
            if (generatedKeys.next()) {
                reclamation.setId(generatedKeys.getInt(1)); // Set the generated ID
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
                r.setId(rs.getInt("ID_reclamation"));
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
            pstm.setInt(7, reclamation.getId());

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
}