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
        String qry = "INSERT INTO `intervention`(`type_intervention`, `description`, `etat`, `date_intervention`, `heure_intervention`, `lampadaire_id`, `technicien_id`, `ID_reclamation`) VALUES (?,?,?,?,?,?,?,?)";
        try {
            PreparedStatement pstm = cnx.prepareStatement(qry, Statement.RETURN_GENERATED_KEYS);
            pstm.setString(1, intervention.getTypeIntervention().name());
            pstm.setString(2, intervention.getDescription());
            pstm.setString(3, intervention.getEtat());
            pstm.setDate(4, intervention.getDateIntervention());
            pstm.setTime(5, intervention.getHeureIntervention());
            pstm.setInt(6, intervention.getLampadaireId());
            pstm.setInt(7, intervention.getTechnicienId());

            if (intervention.getID_reclamation() != null) {
                pstm.setInt(8, intervention.getID_reclamation());
            } else {
                pstm.setNull(8, Types.INTEGER);
            }

            pstm.executeUpdate();

            ResultSet generatedKeys = pstm.getGeneratedKeys();
            if (generatedKeys.next()) {
                intervention.setID_intervention(generatedKeys.getInt(1));
            }
        } catch (SQLException e) {
            System.out.println("❌ Erreur lors de l'ajout : " + e.getMessage());
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
                i.setID_intervention(rs.getInt("ID_intervention"));
                i.setTypeIntervention(TypeIntervention.valueOf(rs.getString("type_intervention")));
                i.setDescription(rs.getString("description"));
                i.setEtat(rs.getString("etat"));
                i.setDateIntervention(rs.getDate("date_intervention"));
                i.setHeureIntervention(rs.getTime("heure_intervention"));
                i.setLampadaireId(rs.getInt("lampadaire_id"));
                i.setTechnicienId(rs.getInt("technicien_id"));

                Integer idReclamation = rs.getObject("ID_reclamation", Integer.class);
                i.setID_reclamation(idReclamation);

                interventions.add(i);
            }

        } catch (SQLException e) {
            System.out.println("❌ Erreur lors de la récupération : " + e.getMessage());
        }

        return interventions;
    }

    @Override
    public void update(Intervention intervention) {
        String qry = "UPDATE `intervention` SET `type_intervention`=?, `description`=?, `etat`=?, `date_intervention`=?, `heure_intervention`=?, `lampadaire_id`=?, `technicien_id`=?, `ID_reclamation`=? WHERE `ID_intervention`=?";
        try {
            PreparedStatement pstm = cnx.prepareStatement(qry);
            pstm.setString(1, intervention.getTypeIntervention().name());
            pstm.setString(2, intervention.getDescription());
            pstm.setString(3, intervention.getEtat());
            pstm.setDate(4, intervention.getDateIntervention());
            pstm.setTime(5, intervention.getHeureIntervention());
            pstm.setInt(6, intervention.getLampadaireId());
            pstm.setInt(7, intervention.getTechnicienId());

            if (intervention.getID_reclamation() != null) {
                pstm.setInt(8, intervention.getID_reclamation());
            } else {
                pstm.setNull(8, Types.INTEGER);
            }

            pstm.setInt(9, intervention.getID_intervention());

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
        delete(intervention.getID_intervention());
    }

    private void delete(int id) {
        try {
            String qry = "DELETE FROM intervention WHERE ID_intervention=?";
            PreparedStatement pstm = cnx.prepareStatement(qry);
            pstm.setInt(1, id);
            pstm.executeUpdate();
        } catch (SQLException ex) {
            System.out.println("❌ Erreur suppression: " + ex.getMessage());
        }
    }
}
