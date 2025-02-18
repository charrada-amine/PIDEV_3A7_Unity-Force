package tn.esprit.services;

import tn.esprit.utils.MyDatabase2;
import tn.esprit.models.profile;
import tn.esprit.models.Source;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceProfile {
    private Connection connection;

    private final ServiceSource serviceSource = new ServiceSource(); // Ajout de l'objet ServiceSource

    // 🔹 Initialisation de la connexion à la base de données
    public ServiceProfile() {
        this.connection = new MyDatabase2().getCnx();
    }

    // Ajouter un profil
    public void add(profile profil) {
        String query = "INSERT INTO profile (consommationjour, consommationmois, coutestime, dureactivite, sourceid, lampadaireid) VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, profil.getConsommationJour());
            statement.setString(2, profil.getConsommationMois());
            statement.setString(3, profil.getCoutEstime());
            statement.setString(4, profil.getDureeActivite());
            statement.setInt(5, profil.getSourceId());
            statement.setInt(6, profil.getLampadaireId());

            int rowsInserted = statement.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("✅ Profil ajouté avec succès !");
                // Récupérer l'ID généré automatiquement
                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        profil.setIdprofile(generatedKeys.getInt(1));
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("❌ Erreur lors de l'ajout du profil : " + e.getMessage());
        }
    }

    // Récupérer tous les profils
    public List<profile> getAll() {
        List<profile> profiles = new ArrayList<>();
        String query = "SELECT * FROM profile";

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                profile profil = new profile(
                        resultSet.getInt("idprofile"),
                        resultSet.getString("consommationjour"),
                        resultSet.getString("consommationmois"),
                        resultSet.getString("coutestime"),
                        resultSet.getString("dureactivite"),
                        resultSet.getInt("sourceid"),
                        resultSet.getInt("lampadaireid")
                );
                profiles.add(profil);
            }
            System.out.println("✅ Récupération des profils réussie !");
        } catch (SQLException e) {
            System.out.println("❌ Erreur lors de la récupération des profils : " + e.getMessage());
        }
        return profiles;
    }

    // Supprimer un profil par ID
    public void delete(int id) {
        String query = "DELETE FROM profile WHERE idprofile = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id);

            int rowsDeleted = statement.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("✅ Profil avec ID " + id + " supprimé avec succès !");
            } else {
                System.out.println("⚠️ Aucun profil trouvé avec l'ID " + id);
            }
        } catch (SQLException e) {
            System.out.println("❌ Erreur lors de la suppression du profil : " + e.getMessage());
        }
    }

    // Mettre à jour un profil existant
    public void update(profile profil) {
        String query = "UPDATE profile SET consommationjour = ?, consommationmois = ?, coutestime = ?, dureactivite = ?, sourceid = ?, lampadaireid = ? WHERE idprofile = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, profil.getConsommationJour());
            statement.setString(2, profil.getConsommationMois());
            statement.setString(3, profil.getCoutEstime());
            statement.setString(4, profil.getDureeActivite());
            statement.setInt(5, profil.getSourceId());
            statement.setInt(6, profil.getLampadaireId());
            statement.setInt(7, profil.getIdprofile());

            int rowsUpdated = statement.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("✅ Profil mis à jour avec succès !");
            } else {
                System.out.println("⚠️ Aucun profil trouvé avec cet ID !");
            }
        } catch (SQLException e) {
            System.out.println("❌ Erreur lors de la mise à jour du profil : " + e.getMessage());
        }
    }

    // 🔹 Récupérer les IDs des sources

}
