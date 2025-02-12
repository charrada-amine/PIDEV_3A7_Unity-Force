package tn.esprit.services;

import tn.esprit.utils.MyDatabase2;
import tn.esprit.models.Source;
import tn.esprit.Enumerations.EnumType;
import tn.esprit.Enumerations.EnumEtat;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceSource {
    private Connection connection;

    public ServiceSource() {
        this.connection = new MyDatabase2().getCnx();
    }

    // 🔹 Ajouter une source
    public void add(Source source) {
        String query = "INSERT INTO source (type, capacite, rendement, etat, dateInstallation) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, source.getType().name()); // Utilisation de Enum.name()
            statement.setFloat(2, source.getCapacite());
            statement.setFloat(3, source.getRendement());
            statement.setString(4, source.getEtat().name());
            statement.setDate(5, Date.valueOf(source.getDateInstallation()));

            int rowsInserted = statement.executeUpdate();

            if (rowsInserted > 0) {
                // Récupérer l'ID généré automatiquement
                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        source.setIdSource(generatedKeys.getInt(1));
                        System.out.println("✅ Source ajoutée avec succès, ID : " + source.getIdSource());
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("❌ Erreur lors de l'ajout de la source : " + e.getMessage());
        }
    }

    // 🔹 Récupérer toutes les sources
    public List<Source> getAll() {
        List<Source> sources = new ArrayList<>();
        String query = "SELECT * FROM source";

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                EnumType type = EnumType.valueOf(resultSet.getString("type"));
                EnumEtat etat = EnumEtat.valueOf(resultSet.getString("etat"));

                Source source = new Source(
                        resultSet.getInt("idSource"),
                        type,
                        resultSet.getFloat("capacite"),
                        resultSet.getFloat("rendement"),
                        etat,
                        resultSet.getDate("dateInstallation").toLocalDate()
                );
                sources.add(source);
            }
        } catch (SQLException e) {
            System.out.println("❌ Erreur lors de la récupération des sources : " + e.getMessage());
        }
        return sources;
    }

    // 🔹 Supprimer une source par son ID
    public void delete(int id) {
        String query = "DELETE FROM source WHERE idSource = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id);
            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("✅ Source supprimée avec succès !");
            } else {
                System.out.println("⚠️ Aucune source trouvée avec l'ID : " + id);
            }
        } catch (SQLException e) {
            System.out.println("❌ Erreur lors de la suppression de la source : " + e.getMessage());
        }
    }

    // 🔹 Mettre à jour une source par son ID
    public void update(Source source) {
        String query = "UPDATE source SET type = ?, capacite = ?, rendement = ?, etat = ?, dateInstallation = ? WHERE idSource = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, source.getType().name());
            statement.setFloat(2, source.getCapacite());
            statement.setFloat(3, source.getRendement());
            statement.setString(4, source.getEtat().name());
            statement.setDate(5, Date.valueOf(source.getDateInstallation()));
            statement.setInt(6, source.getIdSource());

            int rowsUpdated = statement.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("✅ Source mise à jour avec succès !");
            } else {
                System.out.println("⚠️ Aucun enregistrement trouvé avec cet ID !");
            }
        } catch (SQLException e) {
            System.out.println("❌ Erreur lors de la mise à jour de la source : " + e.getMessage());
        }
    }
}
