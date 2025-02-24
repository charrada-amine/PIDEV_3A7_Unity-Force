package tn.esprit.services;

import tn.esprit.interfaces.IService;
import tn.esprit.models.Lampadaire;
import tn.esprit.models.Lampadaire.EtatLampadaire;
import tn.esprit.utils.MyDatabase;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ServiceLampadaire implements IService<Lampadaire> {
    private final Connection cnx;

    public ServiceLampadaire() {
        cnx = MyDatabase.getInstance().getCnx();
    }

    @Override
    public void add(Lampadaire lampadaire) {
        String checkZoneQuery = "SELECT COUNT(*) FROM zones WHERE id_zone = ?";
        try (PreparedStatement checkStmt = cnx.prepareStatement(checkZoneQuery)) {
            checkStmt.setInt(1, lampadaire.getIdZone());
            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next() && rs.getInt(1) == 0) {
                    System.err.println("Erreur : Zone ID " + lampadaire.getIdZone() + " n'existe pas !");
                    return;
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur vérification zone : " + e.getMessage());
            return;
        }

        String qry = "INSERT INTO lampadaire(typeLampadaire, puissance, etat, dateInstallation, id_zone, latitude, longitude) VALUES (?,?,?,?,?,?,?)";
        try (PreparedStatement pstm = cnx.prepareStatement(qry, Statement.RETURN_GENERATED_KEYS)) {
            pstm.setString(1, lampadaire.getTypeLampadaire());
            pstm.setFloat(2, lampadaire.getPuissance());
            pstm.setString(3, lampadaire.getEtat().name());
            if (lampadaire.getDateInstallation() != null) {
                pstm.setDate(4, Date.valueOf(lampadaire.getDateInstallation()));
            } else {
                pstm.setNull(4, Types.DATE);
            }
            pstm.setInt(5, lampadaire.getIdZone());
            pstm.setDouble(6, lampadaire.getLatitude());
            pstm.setDouble(7, lampadaire.getLongitude());
            pstm.executeUpdate();

            try (ResultSet generatedKeys = pstm.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    lampadaire.setIdLamp(generatedKeys.getInt(1));
                }
            }
            System.out.println("Lampadaire ajouté : " + lampadaire);
        } catch (SQLException e) {
            System.err.println("Erreur ajout : " + e.getMessage());
        }
    }

    @Override
    public List<Lampadaire> getAll() {
        List<Lampadaire> lampadaires = new ArrayList<>();
        String qry = "SELECT * FROM lampadaire";

        try (Statement stm = cnx.createStatement();
             ResultSet rs = stm.executeQuery(qry)) {
            while (rs.next()) {
                Lampadaire l = new Lampadaire();
                l.setIdLamp(rs.getInt("id_lamp"));
                l.setTypeLampadaire(rs.getString("typeLampadaire"));
                l.setPuissance(rs.getFloat("puissance"));
                String etatStr = rs.getString("etat");
                l.setEtat(EtatLampadaire.valueOf(etatStr));
                Date sqlDate = rs.getDate("dateInstallation");
                l.setDateInstallation(sqlDate != null ? sqlDate.toLocalDate() : null);
                l.setIdZone(rs.getInt("id_zone"));
                l.setLatitude(rs.getDouble("latitude"));
                l.setLongitude(rs.getDouble("longitude"));
                lampadaires.add(l);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lecture : " + e.getMessage());
        }
        return lampadaires;
    }

    @Override
    public void update(Lampadaire lampadaire) {
        String qry = "UPDATE lampadaire SET typeLampadaire=?, puissance=?, etat=?, dateInstallation=?, id_zone=?, latitude=?, longitude=? WHERE id_lamp=?";
        try (PreparedStatement pstm = cnx.prepareStatement(qry)) {
            pstm.setString(1, lampadaire.getTypeLampadaire());
            pstm.setFloat(2, lampadaire.getPuissance());
            pstm.setString(3, lampadaire.getEtat().name());
            if (lampadaire.getDateInstallation() != null) {
                pstm.setDate(4, Date.valueOf(lampadaire.getDateInstallation()));
            } else {
                pstm.setNull(4, Types.DATE);
            }
            pstm.setInt(5, lampadaire.getIdZone());
            pstm.setDouble(6, lampadaire.getLatitude());
            pstm.setDouble(7, lampadaire.getLongitude());
            pstm.setInt(8, lampadaire.getIdLamp());
            int rowsUpdated = pstm.executeUpdate();
            System.out.println(rowsUpdated + " ligne(s) mise(s) à jour");
        } catch (SQLException e) {
            System.err.println("Erreur mise à jour : " + e.getMessage());
        }
    }

    @Override
    public void delete(Lampadaire lampadaire) {
        String qry = "DELETE FROM lampadaire WHERE id_lamp = ?";
        try (PreparedStatement pstm = cnx.prepareStatement(qry)) {
            pstm.setInt(1, lampadaire.getIdLamp());
            int rowsDeleted = pstm.executeUpdate();
            System.out.println(rowsDeleted + " ligne(s) supprimée(s)");
        } catch (SQLException e) {
            System.err.println("Erreur suppression : " + e.getMessage());
        }
    }

    public Lampadaire getById(int id_lamp) {
        String qry = "SELECT * FROM lampadaire WHERE id_lamp = ?";
        try (PreparedStatement pstm = cnx.prepareStatement(qry)) {
            pstm.setInt(1, id_lamp);
            ResultSet rs = pstm.executeQuery();
            if (rs.next()) {
                Lampadaire l = new Lampadaire();
                l.setIdLamp(rs.getInt("id_lamp"));
                l.setTypeLampadaire(rs.getString("typeLampadaire"));
                l.setPuissance(rs.getFloat("puissance"));
                String etatStr = rs.getString("etat");
                l.setEtat(EtatLampadaire.valueOf(etatStr));
                Date sqlDate = rs.getDate("dateInstallation");
                l.setDateInstallation(sqlDate != null ? sqlDate.toLocalDate() : null);
                l.setIdZone(rs.getInt("id_zone"));
                l.setLatitude(rs.getDouble("latitude"));
                l.setLongitude(rs.getDouble("longitude"));
                return l;
            }
        } catch (SQLException e) {
            System.err.println("Erreur récupération par ID : " + e.getMessage());
        }
        return null;
    }
}