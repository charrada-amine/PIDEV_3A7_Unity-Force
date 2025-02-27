package tn.esprit.services;

import tn.esprit.interfaces.IService;
import tn.esprit.models.Zone;
import tn.esprit.utils.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceZone implements IService<Zone> {
    private final Connection cnx;

    public ServiceZone() {
        cnx = MyDatabase.getInstance().getCnx();
    }

    // AJOUT DE LA MÉTHODE GETBYID MANQUANTE
    public Zone getById(int id) {
        String qry = "SELECT * FROM zones WHERE id_zone = ?";
        try (PreparedStatement pstm = cnx.prepareStatement(qry)) {
            pstm.setInt(1, id);
            try (ResultSet rs = pstm.executeQuery()) {
                if (rs.next()) {
                    Zone z = new Zone();
                    z.setIdZone(rs.getInt("id_zone"));
                    z.setNom(rs.getString("nom"));
                    z.setDescription(rs.getString("description"));
                    z.setSurface(rs.getFloat("surface"));
                    z.setNombreLampadaires(rs.getInt("nombreLampadaires"));
                    z.setNombreCitoyens(rs.getInt("nombreCitoyens"));
                    return z;
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur récupération zone par ID: " + e.getMessage());
        }
        return null;
    }

    // OPTIMISATION DES MESSAGES DE CONFIRMATION
    @Override
    public void add(Zone zone) {
        String qry = "INSERT INTO zones (nom, description, surface, nombreLampadaires, nombreCitoyens) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement pstm = cnx.prepareStatement(qry, Statement.RETURN_GENERATED_KEYS)) {
            pstm.setString(1, zone.getNom());
            pstm.setString(2, zone.getDescription());
            pstm.setFloat(3, zone.getSurface());
            pstm.setInt(4, zone.getNombreLampadaires());
            pstm.setInt(5, zone.getNombreCitoyens());

            int affectedRows = pstm.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstm.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        zone.setIdZone(generatedKeys.getInt(1));
                        System.out.println("✅ Zone ajoutée - ID: " + zone.getIdZone());
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur d'ajout: " + e.getMessage());
        }
    }

    // AJOUT DE LA GESTION DES RÉSULTATS VIDES
    @Override
    public List<Zone> getAll() {
        List<Zone> zones = new ArrayList<>();
        String qry = "SELECT * FROM zones";

        try (Statement stm = cnx.createStatement();
             ResultSet rs = stm.executeQuery(qry)) {

            while (rs.next()) {
                Zone z = new Zone(
                        rs.getInt("id_zone"),
                        rs.getString("nom"),
                        rs.getString("description"),
                        rs.getFloat("surface"),
                        rs.getInt("nombreLampadaires"),
                        rs.getInt("nombreCitoyens")
                );
                zones.add(z);
            }

            if (zones.isEmpty()) {
                System.out.println("ℹ️ Aucune zone trouvée dans la base");
            }

        } catch (SQLException e) {
            System.err.println("❌ Erreur de lecture: " + e.getMessage());
        }
        return zones;
    }

    // AMÉLIORATION DES MESSAGES DE MISE À JOUR
    @Override
    public void update(Zone zone) {
        String qry = "UPDATE zones SET nom=?, description=?, surface=?, nombreLampadaires=?, nombreCitoyens=? WHERE id_zone=?";

        try (PreparedStatement pstm = cnx.prepareStatement(qry)) {
            pstm.setString(1, zone.getNom());
            pstm.setString(2, zone.getDescription());
            pstm.setFloat(3, zone.getSurface());
            pstm.setInt(4, zone.getNombreLampadaires());
            pstm.setInt(5, zone.getNombreCitoyens());
            pstm.setInt(6, zone.getIdZone());

            int rows = pstm.executeUpdate();
            System.out.println(rows > 0 ? "✅ Zone mise à jour" : "⚠️ Aucune modification effectuée");

        } catch (SQLException e) {
            System.err.println("❌ Erreur de mise à jour: " + e.getMessage());
        }
    }

    // AJOUT DE LA VÉRIFICATION DE SUPPRESSION
    @Override
    public void delete(Zone zone) {
        String qry = "DELETE FROM zones WHERE id_zone=?";

        try (PreparedStatement pstm = cnx.prepareStatement(qry)) {
            pstm.setInt(1, zone.getIdZone());

            int rows = pstm.executeUpdate();
            System.out.println(rows > 0 ? "✅ Zone supprimée" : "⚠️ Aucune zone trouvée");

        } catch (SQLException e) {
            System.err.println("❌ Erreur de suppression: " + e.getMessage());
        }
    }
}