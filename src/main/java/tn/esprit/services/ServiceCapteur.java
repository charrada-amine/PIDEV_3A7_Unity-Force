package tn.esprit.services;

import tn.esprit.utils.MyDatabase;
import tn.esprit.models.Capteur;
import tn.esprit.models.Capteur.TypeCapteur; // Importer l'énumération avec le préfixe Capteur
import tn.esprit.models.Capteur.EtatCapteur; // Importer l'énumération avec le préfixe Capteur

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static java.sql.Date.valueOf;

public class ServiceCapteur {
    private Connection connection;

    public ServiceCapteur() {
        this.connection = new MyDatabase().getCnx();
    }

    // 🔹 Ajouter un capteur
    public void add(Capteur capteur) {
        String query = "INSERT INTO capteur (type, etat, dateinstallation, lampadaireid) VALUES (?, ?, ?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, capteur.getType().name()); // Utiliser name() pour obtenir la chaîne de caractères de l'énumération
            statement.setString(2, capteur.getEtat().name()); // Utiliser name() pour obtenir la chaîne de caractères de l'énumération

            // Conversion LocalDate en java.sql.Date
            statement.setDate(3, valueOf(capteur.getDateinstallation()));

            statement.setInt(4, capteur.getLampadaireId());

            statement.executeUpdate();
            System.out.println("Capteur ajouté avec succès !");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 🔹 Récupérer tous les capteurs
    public List<Capteur> getAll() {
        List<Capteur> capteurs = new ArrayList<>();
        String query = "SELECT * FROM capteur";

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                Capteur capteur = new Capteur(
                        resultSet.getInt("id"),
                        TypeCapteur.valueOf(resultSet.getString("type")), // Utiliser valueOf pour convertir la chaîne en énumération
                        // Conversion de java.sql.Date en LocalDate
                        resultSet.getDate("dateinstallation").toLocalDate(),
                        EtatCapteur.valueOf(resultSet.getString("etat")), // Utiliser valueOf pour convertir la chaîne en énumération
                        resultSet.getInt("lampadaireid")
                );
                capteurs.add(capteur);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return capteurs;
    }

    // 🔹 Supprimer un capteur par son ID
    public void delete(int id) {
        String query = "DELETE FROM capteur WHERE id = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id);  // On définit l'ID à supprimer dans la requête
            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Capteur supprimé avec succès !");
            } else {
                System.out.println("Aucun capteur trouvé avec l'ID : " + id);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 🔹 Mettre à jour un capteur par son ID
    public void update(Capteur capteur) {
        String query = "UPDATE capteur SET type = ?, etat = ?, dateinstallation = ?, lampadaireid = ? WHERE id = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, capteur.getType().name()); // Utiliser name() pour obtenir la chaîne de caractères de l'énumération
            statement.setString(2, capteur.getEtat().name()); // Utiliser name() pour obtenir la chaîne de caractères de l'énumération

            // Conversion LocalDate en java.sql.Date
            statement.setDate(3, valueOf(capteur.getDateinstallation()));
            statement.setInt(4, capteur.getLampadaireId());
            statement.setInt(5, capteur.getId());

            int rowsUpdated = statement.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("✅ Capteur mis à jour avec succès !");
            } else {
                System.out.println("⚠️ Aucun enregistrement trouvé avec cet ID !");
            }
        } catch (SQLException e) {
            System.out.println("❌ Erreur lors de la mise à jour du capteur : " + e.getMessage());
        }
    }
}
