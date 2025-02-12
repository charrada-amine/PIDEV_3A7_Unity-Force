package tn.esprit.services;

import tn.esprit.interfaces.IService;
import tn.esprit.models.Intervention;
import tn.esprit.models.Intervention.TypeIntervention;
import tn.esprit.utils.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceIntervention implements IService<Intervention> {
    private Connection cnx;

    public ServiceIntervention() {
        cnx = MyDatabase.getInstance().getCnx();
    }

    @Override
    public void add(Intervention intervention) {
        String qry = "INSERT INTO `intervention`(`typeIntervention`, `description`, `etat`, `dateIntervention`, `heureIntervention`, `lampadaireId`, `technicienId`, `ID_reclamation`) VALUES (?,?,?,?,?,?,?,?)";
        try {
            PreparedStatement pstm = cnx.prepareStatement(qry, Statement.RETURN_GENERATED_KEYS);
            pstm.setString(1, intervention.getTypeIntervention().name());
            pstm.setString(2, intervention.getDescription());
            pstm.setString(3, intervention.getEtat());
            pstm.setDate(4, intervention.getDateIntervention());
            pstm.setTime(5, intervention.getHeureIntervention());
            pstm.setInt(6, intervention.getLampadaireId());
            pstm.setInt(7, intervention.getTechnicienId());
            pstm.setInt(8, intervention.getID_reclamation()); // Ajout de ID_reclamation

            pstm.executeUpdate();

            // Retrieve the generated ID_intervention
            ResultSet generatedKeys = pstm.getGeneratedKeys();
            if (generatedKeys.next()) {
                intervention.setId(generatedKeys.getInt(1));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public List<Intervention> getAll() {
        List<Intervention> interventions = new ArrayList<>();
        String qry = "SELECT * FROM `intervention`";

        try {
            Statement stm = cnx.createStatement();
            ResultSet rs = stm.executeQuery(qry);

            while (rs.next()) {
                Intervention i = new Intervention();
                i.setId(rs.getInt("ID_intervention"));
                i.setTypeIntervention(TypeIntervention.valueOf(rs.getString("typeIntervention").toUpperCase()));
                i.setDescription(rs.getString("description"));
                i.setEtat(rs.getString("etat"));
                i.setDateIntervention(rs.getDate("dateIntervention"));
                i.setHeureIntervention(rs.getTime("heureIntervention"));
                i.setLampadaireId(rs.getInt("lampadaireId"));
                i.setTechnicienId(rs.getInt("technicienId"));
                i.setID_reclamation(rs.getInt("ID_reclamation")); // Récupération de ID_reclamation

                interventions.add(i);
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return interventions;
    }

    @Override
    public void update(Intervention intervention) {
        try {
            String qry = "UPDATE `intervention` SET `typeIntervention`=?, `description`=?, `etat`=?, `dateIntervention`=?, `heureIntervention`=?, `lampadaireId`=?, `technicienId`=?, `ID_reclamation`=? WHERE `id`=?";
            PreparedStatement pstm = cnx.prepareStatement(qry);
            pstm.setString(1, intervention.getTypeIntervention().name());
            pstm.setString(2, intervention.getDescription());
            pstm.setString(3, intervention.getEtat());
            pstm.setDate(4, intervention.getDateIntervention());
            pstm.setTime(5, intervention.getHeureIntervention());
            pstm.setInt(6, intervention.getLampadaireId());
            pstm.setInt(7, intervention.getTechnicienId());
            pstm.setInt(8, intervention.getID_reclamation()); // Ajout de ID_reclamation
            pstm.setInt(9, intervention.getId());

            int rowsUpdated = pstm.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("✅ Intervention mise à jour avec succès !");
            } else {
                System.out.println("❌ Aucune intervention trouvée avec cet ID !");
            }
        } catch (SQLException ex) {
            System.out.println("❌ Erreur lors de la mise à jour : " + ex.getMessage());
        }
    }

    @Override
    public void delete(Intervention intervention) {
        try {
            String qry = "DELETE FROM `intervention` WHERE `id`=?";
            PreparedStatement pstm = cnx.prepareStatement(qry);
            pstm.setInt(1, intervention.getId());

            int rowsDeleted = pstm.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("✅ Intervention supprimée avec succès !");
            } else {
                System.out.println("❌ Aucune intervention trouvée avec cet ID !");
            }
        } catch (SQLException ex) {
            System.out.println("❌ Erreur lors de la suppression : " + ex.getMessage());
        }
    }
}