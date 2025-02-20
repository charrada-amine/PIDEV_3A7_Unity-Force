package tn.esprit.services;

import tn.esprit.utils.MyDatabase;
import tn.esprit.models.DonneeTemperature;
import tn.esprit.utils.MyDatabase2;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class ServiceTemperature {
    private Connection connection;

    public ServiceTemperature() {
        this.connection = new MyDatabase2().getCnx();
    }

    public void add(DonneeTemperature donnee) {
        String query = "INSERT INTO donneetemperature (datecollecte, heurecollecte, capteurid, valeur) VALUES (?, ?, ?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setDate(1, Date.valueOf(donnee.getDateCollecte()));
            statement.setTime(2, Time.valueOf(donnee.getHeureCollecte()));
            statement.setInt(3, donnee.getCapteurId());
            statement.setFloat(4, donnee.getValeur());

            statement.executeUpdate();
            System.out.println("✅ Donnée de température ajoutée avec succès !");
        } catch (SQLException e) {
            System.out.println("❌ Erreur lors de l'ajout de la donnée : " + e.getMessage());
        }
    }

    public List<DonneeTemperature> getAll() {
        List<DonneeTemperature> donnees = new ArrayList<>();
        String query = "SELECT * FROM donneetemperature";

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                DonneeTemperature donnee = new DonneeTemperature(
                        resultSet.getInt("id_temperature"),
                        resultSet.getDate("datecollecte").toLocalDate(),
                        resultSet.getTime("heurecollecte").toLocalTime(),
                        resultSet.getInt("capteurid"),
                        resultSet.getFloat("valeur")
                );
                donnees.add(donnee);
            }
        } catch (SQLException e) {
            System.out.println("❌ Erreur lors de la récupération des données : " + e.getMessage());
        }
        return donnees;
    }
    public void delete(int id) {
        String query = "DELETE FROM donneetemperature WHERE id_temperature = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id);
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("✅ Donnée de température supprimée avec succès !");
            } else {
                System.out.println("⚠️ Aucune donnée trouvée avec l'ID : " + id);
            }
        } catch (SQLException e) {
            System.out.println("❌ Erreur lors de la suppression : " + e.getMessage());
        }
    }
    public void update(DonneeTemperature donnee) {
        String query = "UPDATE donneetemperature SET datecollecte = ?, heurecollecte = ?, capteurid = ?, valeur = ? WHERE id_temperature = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setDate(1, Date.valueOf(donnee.getDateCollecte()));
            statement.setTime(2, Time.valueOf(donnee.getHeureCollecte()));
            statement.setInt(3, donnee.getCapteurId());
            statement.setFloat(4, donnee.getValeur());
            statement.setInt(5, donnee.getId());
            int rowsUpdated = statement.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("✅ Donnée de température mise à jour avec succès !");
            } else {
                System.out.println("⚠️ Aucun enregistrement trouvé avec cet ID !");
            }
        } catch (SQLException e) {
            System.out.println("❌ Erreur lors de la mise à jour : " + e.getMessage());
        }
    }
}
