package tn.esprit.services;

import tn.esprit.utils.MyDatabase;
import tn.esprit.models.DonneeMouvement;
import tn.esprit.utils.MyDatabase2;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class ServiceMouvement {
    private Connection connection;

    public ServiceMouvement() {
        this.connection = new MyDatabase2().getCnx();
    }

    public void add(DonneeMouvement donnee) {
        String query = "INSERT INTO donneemouvement (datecollecte, heurecollecte, capteurid, valeur) VALUES (?, ?, ?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setDate(1, Date.valueOf(donnee.getDateCollecte()));
            statement.setTime(2, Time.valueOf(donnee.getHeureCollecte()));
            statement.setInt(3, donnee.getCapteurId());
            statement.setBoolean(4, donnee.getValeur());

            statement.executeUpdate();
            System.out.println("✅ Donnée de mouvement ajoutée avec succès !");
        } catch (SQLException e) {
            System.out.println("❌ Erreur lors de l'ajout de la donnée : " + e.getMessage());
        }
    }

    public List<DonneeMouvement> getAll() {
        List<DonneeMouvement> donnees = new ArrayList<>();
        String query = "SELECT * FROM donneemouvement";

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                DonneeMouvement donnee = new DonneeMouvement(
                        resultSet.getInt("id_mouvement"),
                        resultSet.getDate("datecollecte").toLocalDate(),
                        resultSet.getTime("heurecollecte").toLocalTime(),
                        resultSet.getInt("capteurid"),
                        resultSet.getBoolean("valeur")
                );
                donnees.add(donnee);
            }
        } catch (SQLException e) {
            System.out.println("❌ Erreur lors de la récupération des données : " + e.getMessage());
        }
        return donnees;
    }
    // ✅ Supprimer une donnée par ID
    public void delete(int id) {
        String query = "DELETE FROM donneemouvement WHERE id_mouvement = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id);
            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("✅ Donnée de mouvement supprimée avec succès !");
            } else {
                System.out.println("⚠️ Aucune donnée trouvée avec l'ID : " + id);
            }
        } catch (SQLException e) {
            System.out.println("❌ Erreur lors de la suppression : " + e.getMessage());
        }
    }

    // ✅ Mettre à jour une donnée par ID
    public void update(DonneeMouvement donnee) {
        String query = "UPDATE donneemouvement SET datecollecte = ?, heurecollecte = ?, capteurid = ?, valeur = ? WHERE id_mouvement = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setDate(1, Date.valueOf(donnee.getDateCollecte()));
            statement.setTime(2, Time.valueOf(donnee.getHeureCollecte()));
            statement.setInt(3, donnee.getCapteurId());
            statement.setBoolean(4, donnee.getValeur());
            statement.setInt(5, donnee.getId());

            int rowsUpdated = statement.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("✅ Donnée de mouvement mise à jour avec succès !");
            } else {
                System.out.println("⚠️ Aucun enregistrement trouvé avec cet ID !");
            }
        } catch (SQLException e) {
            System.out.println("❌ Erreur lors de la mise à jour : " + e.getMessage());
        }
    }
}
