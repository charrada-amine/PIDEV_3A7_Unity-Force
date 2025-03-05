package tn.esprit.services;

import tn.esprit.interfaces.IService;
import tn.esprit.models.Camera;
import tn.esprit.utils.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceCamera implements IService<Camera> {
    private final Connection cnx;

    public ServiceCamera() {
        cnx = MyDatabase.getInstance().getCnx();
    }

    @Override
    public void add(Camera camera) {
        String qry = "INSERT INTO camera (url_flux, ip_address) VALUES (?, ?)";
        try (PreparedStatement pstm = cnx.prepareStatement(qry, Statement.RETURN_GENERATED_KEYS)) {
            pstm.setString(1, camera.getUrlFlux());
            pstm.setString(2, camera.getIpAddress()); // Ajout
            pstm.executeUpdate();

            try (ResultSet generatedKeys = pstm.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    camera.setIdCamera(generatedKeys.getInt(1));
                }
            }
            System.out.println("Caméra ajoutée : " + camera);
        } catch (SQLException e) {
            System.err.println("Erreur ajout caméra : " + e.getMessage());
        }
    }

    @Override
    public List<Camera> getAll() {
        List<Camera> cameras = new ArrayList<>();
        String qry = "SELECT * FROM camera";
        try (Statement stm = cnx.createStatement();
             ResultSet rs = stm.executeQuery(qry)) {
            while (rs.next()) {
                Camera c = new Camera();
                c.setIdCamera(rs.getInt("id_camera"));
                c.setUrlFlux(rs.getString("url_flux"));
                c.setIpAddress(rs.getString("ip_address")); // Ajout
                cameras.add(c);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lecture caméras : " + e.getMessage());
        }
        return cameras;
    }

    @Override
    public void update(Camera camera) {
        String qry = "UPDATE camera SET url_flux = ?, ip_address = ? WHERE id_camera = ?";
        try (PreparedStatement pstm = cnx.prepareStatement(qry)) {
            pstm.setString(1, camera.getUrlFlux());
            pstm.setString(2, camera.getIpAddress()); // Ajout
            pstm.setInt(3, camera.getIdCamera());
            int rowsUpdated = pstm.executeUpdate();
            System.out.println(rowsUpdated + " ligne(s) mise(s) à jour");
        } catch (SQLException e) {
            System.err.println("Erreur mise à jour caméra : " + e.getMessage());
        }
    }

    @Override
    public void delete(Camera camera) {
        String qry = "DELETE FROM camera WHERE id_camera = ?";
        try (PreparedStatement pstm = cnx.prepareStatement(qry)) {
            pstm.setInt(1, camera.getIdCamera());
            int rowsDeleted = pstm.executeUpdate();
            System.out.println(rowsDeleted + " ligne(s) supprimée(s)");
        } catch (SQLException e) {
            System.err.println("Erreur suppression caméra : " + e.getMessage());
        }
    }

    public Camera getById(int idCamera) {
        String qry = "SELECT * FROM camera WHERE id_camera = ?";
        try (PreparedStatement pstm = cnx.prepareStatement(qry)) {
            pstm.setInt(1, idCamera);
            ResultSet rs = pstm.executeQuery();
            if (rs.next()) {
                Camera c = new Camera();
                c.setIdCamera(rs.getInt("id_camera"));
                c.setUrlFlux(rs.getString("url_flux"));
                c.setIpAddress(rs.getString("ip_address")); // Ajout
                return c;
            }
        } catch (SQLException e) {
            System.err.println("Erreur récupération caméra : " + e.getMessage());
        }
        return null;
    }
}