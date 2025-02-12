package tn.esprit.services;

<<<<<<< HEAD
import tn.esprit.utils.MyDatabase2;
=======
import tn.esprit.utils.MyDatabase;
>>>>>>> a3c932b0e96e26782012e7d55e0b3688bdf4452d
import tn.esprit.models.DonneeConsommation;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class ServiceConsommation {
    private Connection connection;

    public ServiceConsommation() {
<<<<<<< HEAD
        this.connection = new MyDatabase2().getCnx();
=======
        this.connection = new MyDatabase().getCnx();
>>>>>>> a3c932b0e96e26782012e7d55e0b3688bdf4452d
    }

    // 🔹 Ajouter une donnée de consommation
    public void add(DonneeConsommation donnee) {
        String query = "INSERT INTO donneeconsommation (datecollecte, heurecollecte, capteurid, valeur) VALUES (?, ?, ?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setDate(1, Date.valueOf(donnee.getDateCollecte())); // Conversion LocalDate en Date SQL
            statement.setTime(2, Time.valueOf(donnee.getHeureCollecte())); // Conversion LocalTime en Time SQL
            statement.setInt(3, donnee.getCapteurId());
            statement.setFloat(4, donnee.getValeur());

            statement.executeUpdate();
            System.out.println("✅ Donnée de consommation ajoutée avec succès !");
        } catch (SQLException e) {
            System.out.println("❌ Erreur lors de l'ajout de la donnée : " + e.getMessage());
        }
    }

    // 🔹 Récupérer toutes les données de consommation
    public List<DonneeConsommation> getAll() {
        List<DonneeConsommation> donnees = new ArrayList<>();
        String query = "SELECT * FROM donneeconsommation";

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                DonneeConsommation donnee = new DonneeConsommation(
                        resultSet.getInt("id_consommation"),
                        resultSet.getDate("datecollecte").toLocalDate(), // Conversion en LocalDate
                        resultSet.getTime("heurecollecte").toLocalTime(), // Conversion en LocalTime
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

    // 🔹 Supprimer une donnée par son ID
    public void delete(int id) {
        String query = "DELETE FROM donneeconsommation WHERE id_consommation = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id);
            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("✅ Donnée de consommation supprimée avec succès !");
            } else {
                System.out.println("⚠️ Aucune donnée trouvée avec l'ID : " + id);
            }
        } catch (SQLException e) {
            System.out.println("❌ Erreur lors de la suppression : " + e.getMessage());
        }
    }

    // 🔹 Mettre à jour une donnée de consommation
    public void update(DonneeConsommation donnee) {
        String query = "UPDATE donneeconsommation SET datecollecte = ?, heurecollecte = ?, capteurid = ?, valeur = ? WHERE id_consommation = ?";

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
