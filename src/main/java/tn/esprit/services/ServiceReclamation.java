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
        String qry = "INSERT INTO `reclamation`(`description`, `date_reclamation`, `heure_reclamation`, `statut`, `lampadaire_id`, `citoyen_id`) VALUES (?,?,?,?,?,?)";
        try {
            PreparedStatement pstm = cnx.prepareStatement(qry, Statement.RETURN_GENERATED_KEYS);
            pstm.setString(1, reclamation.getDescription());
            pstm.setDate(2, reclamation.getDateReclamation());
            pstm.setTime(3, reclamation.getHeureReclamation());
            pstm.setString(4, reclamation.getStatut());
            pstm.setInt(5, reclamation.getLampadaireId());
            pstm.setInt(6, reclamation.getCitoyenId());

            pstm.executeUpdate();

            // Récupérer l'ID généré
            ResultSet generatedKeys = pstm.getGeneratedKeys();
            if (generatedKeys.next()) {
                reclamation.setID_reclamation(generatedKeys.getInt(1));
            }
        } catch (SQLException e) {
            System.out.println("❌ Erreur lors de l'ajout : " + e.getMessage());
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
                r.setID_reclamation(rs.getInt("ID_reclamation"));
                r.setDescription(rs.getString("description"));
                r.setDateReclamation(rs.getDate("date_reclamation"));
                r.setHeureReclamation(rs.getTime("heure_reclamation"));
                r.setStatut(rs.getString("statut"));
                r.setLampadaireId(rs.getInt("lampadaire_id"));
                r.setCitoyenId(rs.getInt("citoyen_id"));

                reclamations.add(r);
            }

        } catch (SQLException e) {
            System.out.println("❌ Erreur lors de la récupération : " + e.getMessage());
        }

        return reclamations;
    }

    @Override
    public void update(Reclamation reclamation) {
        try {
            String qry = "UPDATE `reclamation` SET `description`=?, `date_reclamation`=?, `heure_reclamation`=?, `statut`=?, `lampadaire_id`=?, `citoyen_id`=? WHERE `ID_reclamation`=?";
            PreparedStatement pstm = cnx.prepareStatement(qry);
            pstm.setString(1, reclamation.getDescription());
            pstm.setDate(2, reclamation.getDateReclamation());
            pstm.setTime(3, reclamation.getHeureReclamation());
            pstm.setString(4, reclamation.getStatut());
            pstm.setInt(5, reclamation.getLampadaireId());
            pstm.setInt(6, reclamation.getCitoyenId());
            pstm.setInt(7, reclamation.getID_reclamation());

            int rowsUpdated = pstm.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("✅ Réclamation mise à jour avec succès !");
            } else {
                System.out.println("❌ Aucune réclamation trouvée avec cet ID !");
            }
        } catch (SQLException ex) {
            System.out.println("❌ Erreur lors de la mise à jour : " + ex.getMessage());
        }
    }

    @Override
    public void delete(Reclamation reclamation) {
        try {
            if (hasDependencies(reclamation.getID_reclamation())) {
                throw new SQLException("Impossible de supprimer : existence d'interventions liées.");
            }

            String qry = "DELETE FROM `reclamation` WHERE `ID_reclamation`=?";
            PreparedStatement pstm = cnx.prepareStatement(qry);
            pstm.setInt(1, reclamation.getID_reclamation());

            int rowsDeleted = pstm.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("✅ Réclamation supprimée avec succès !");
            } else {
                System.out.println("❌ Aucune réclamation trouvée avec cet ID !");
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
}
