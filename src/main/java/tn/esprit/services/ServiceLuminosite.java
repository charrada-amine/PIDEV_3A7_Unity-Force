package tn.esprit.services;

import tn.esprit.utils.MyDatabase;
import tn.esprit.models.DonneeLuminosite;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class ServiceLuminosite {
    private Connection connection;

    public ServiceLuminosite() {
        this.connection = new MyDatabase().getCnx();
    }

    public void add(DonneeLuminosite donnee) {
        String query = "INSERT INTO donneeluminosite (datecollecte, heurecollecte, capteurid, valeur) VALUES (?, ?, ?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setDate(1, Date.valueOf(donnee.getDateCollecte()));
            statement.setTime(2, Time.valueOf(donnee.getHeureCollecte()));
            statement.setInt(3, donnee.getCapteurId());
            statement.setFloat(4, donnee.getValeur());

            statement.executeUpdate();
            System.out.println("✅ Donnée de luminosité ajoutée avec succès !");
        } catch (SQLException e) {
            System.out.println("❌ Erreur lors de l'ajout de la donnée : " + e.getMessage());
        }
    }

    public List<DonneeLuminosite> getAll() {
        List<DonneeLuminosite> donnees = new ArrayList<>();
        String query = "SELECT * FROM donneeluminosite";

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                DonneeLuminosite donnee = new DonneeLuminosite(
                        resultSet.getInt("id"),
                        resultSet.getDate("datecollecte").toLocalDate(),
                        resultSet.getTime("heurecollecte").toLocalTime(),
                        resultSet.getInt("capteurid"),
                        resultSet.getInt("valeur")
                );
                donnees.add(donnee);
            }
        } catch (SQLException e) {
            System.out.println("❌ Erreur lors de la récupération des données : " + e.getMessage());
        }
        return donnees;
    }

    public void delete(int id) {
        String query = "DELETE FROM donneeluminosite WHERE id = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id);
            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("✅ Donnée de luminosité supprimée avec succès !");
            } else {
                System.out.println("⚠️ Aucune donnée trouvée avec cet ID !");
            }
        } catch (SQLException e) {
            System.out.println("❌ Erreur lors de la suppression : " + e.getMessage());
        }
    }

    public void update(DonneeLuminosite donnee) {
        String query = "UPDATE donneeluminosite SET datecollecte = ?, heurecollecte = ?, capteurid = ?, valeur = ? WHERE id = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setDate(1, Date.valueOf(donnee.getDateCollecte()));
            statement.setTime(2, Time.valueOf(donnee.getHeureCollecte()));
            statement.setInt(3, donnee.getCapteurId());
            statement.setFloat(4, donnee.getValeur());
            statement.setInt(5, donnee.getId());

            int rowsUpdated = statement.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("✅ Donnée mise à jour avec succès !");
            } else {
                System.out.println("⚠️ Aucune donnée trouvée avec cet ID !");
            }
        } catch (SQLException e) {
            System.out.println("❌ Erreur lors de la mise à jour : " + e.getMessage());
        }
    }
}
