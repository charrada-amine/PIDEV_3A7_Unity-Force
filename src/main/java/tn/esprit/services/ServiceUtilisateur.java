package tn.esprit.services;

import tn.esprit.utils.MyDatabase;
import tn.esprit.models.utilisateur;

import tn.esprit.models.Role;

import tn.esprit.models.Specialite;

import java.util.*;

import org.mindrot.jbcrypt.BCrypt;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.sql.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;

public class ServiceUtilisateur {
    private Connection cnx;

    public ServiceUtilisateur() {
        cnx = MyDatabase.getInstance().getCnx();
    }


    public class PasswordEncryptor {

        public static String encryptPassword(String password) {
            try {
                MessageDigest digest = MessageDigest.getInstance("SHA-256");
                byte[] hash = digest.digest(password.getBytes());
                StringBuilder hexString = new StringBuilder();
                for (byte b : hash) {
                    hexString.append(String.format("%02x", b));
                }
                return hexString.toString();  // Retourner le mot de passe crypté
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
                return null;
            }
        }
    }
    public void add(utilisateur utilisateur, Specialite specialite, List<String> modules, int zoneId) {
        // Vérifier que tous les champs obligatoires sont non vides
        if (utilisateur.getNom() == null || utilisateur.getNom().trim().isEmpty()) {
            System.out.println("❌ Le nom est obligatoire !");
            return;
        }
        if (utilisateur.getPrenom() == null || utilisateur.getPrenom().trim().isEmpty()) {
            System.out.println("❌ Le prénom est obligatoire !");
            return;
        }
        if (utilisateur.getEmail() == null || utilisateur.getEmail().trim().isEmpty()) {
            System.out.println("❌ L'email est obligatoire !");
            return;
        }
        if (utilisateur.getMotdepasse() == null || utilisateur.getMotdepasse().trim().isEmpty()) {
            System.out.println("❌ Le mot de passe est obligatoire !");
            return;
        }
        if (utilisateur.getRole() == null) {
            System.out.println("❌ Le rôle est obligatoire !");
            return;
        }
        if (utilisateur.getDateinscription() == null) {
            System.out.println("❌ La date d'inscription est obligatoire !");
            return;
        }

        // 🔹 Hashage du mot de passe avant de l'insérer dans la base de données
        String encryptedPassword = PasswordEncryptor.encryptPassword(utilisateur.getMotdepasse()); // Utiliser votre méthode de hashage ici
        utilisateur.setMotdepasse(encryptedPassword);

        // Préparer la requête pour insérer l'utilisateur dans la base de données
        String qry = "INSERT INTO utilisateur (nom, prenom, email, motdepasse, role, dateinscription) VALUES (?, ?, ?, ?, ?, ?)";

        try {
            PreparedStatement pstm = cnx.prepareStatement(qry, Statement.RETURN_GENERATED_KEYS);
            pstm.setString(1, utilisateur.getNom());
            pstm.setString(2, utilisateur.getPrenom());
            pstm.setString(3, utilisateur.getEmail());
            pstm.setString(4, encryptedPassword); // Utiliser le mot de passe hashé
            pstm.setString(5, utilisateur.getRole().toString()); // Convertir l'énumération en chaîne de caractères
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String dateSansHeure = sdf.format(utilisateur.getDateinscription());
            pstm.setDate(6, java.sql.Date.valueOf(dateSansHeure));

            int affectedRows = pstm.executeUpdate();
            if (affectedRows > 0) {
                ResultSet generatedKeys = pstm.getGeneratedKeys();
                if (generatedKeys.next()) {
                    utilisateur.setId_utilisateur(generatedKeys.getInt(1)); // Set the generated user id

                    // Si le rôle est "technicien", ajouter la spécialité
                    if (utilisateur.getRole() == Role.technicien && specialite != null) {
                        // Ajouter la spécialité pour le technicien dans la base de données
                        String technicienQuery = "INSERT INTO technicien (id_technicien, specialite) VALUES (?, ?)";
                        try {
                            PreparedStatement technicienStmt = cnx.prepareStatement(technicienQuery);
                            technicienStmt.setInt(1, utilisateur.getId_utilisateur()); // Référence à l'ID de l'utilisateur
                            technicienStmt.setString(2, specialite.toString()); // Spécialité
                            int rowsAffected = technicienStmt.executeUpdate();

                            if (rowsAffected > 0) {
                                System.out.println("✅ Technicien ajouté avec spécialité : " + specialite);
                            } else {
                                System.out.println("❌ Échec de l'ajout du technicien.");
                            }
                        } catch (SQLException e) {
                            System.out.println("❌ Erreur SQL : " + e.getMessage());
                        }
                    }

                    // Si le rôle est "responsable", ajouter les modules
                    if (utilisateur.getRole() == Role.responsable && !modules.isEmpty()) {
                        // Convertir la liste des modules en une chaîne séparée par des virgules
                        String modulesStr = String.join(", ", modules);

                        // Enregistrer le responsable et les modules dans la base de données
                        String responsableQuery = "INSERT INTO responsable (id_responsable, modules) VALUES (?, ?)";
                        try {
                            PreparedStatement responsableStmt = cnx.prepareStatement(responsableQuery);
                            responsableStmt.setInt(1, utilisateur.getId_utilisateur());  // Référence à l'ID de l'utilisateur
                            responsableStmt.setString(2, modulesStr);  // Utiliser la chaîne de modules
                            int rowsAffected = responsableStmt.executeUpdate();

                            if (rowsAffected > 0) {
                                System.out.println("✅ Modules ajoutés avec succès pour le responsable.");
                            } else {
                                System.out.println("❌ Échec de l'ajout des modules pour le responsable.");
                            }
                        } catch (SQLException e) {
                            System.out.println("❌ Erreur SQL : " + e.getMessage());
                        }
                    }

                    // Si le rôle est "citoyen", ajouter le zoneId
                    if (utilisateur.getRole() == Role.citoyen) {
                        // Ajouter le citoyen avec son zoneId dans la base de données
                        String citoyenQuery = "INSERT INTO citoyen (id_citoyen, zoneId) VALUES (?, ?)";
                        try {
                            PreparedStatement citoyenStmt = cnx.prepareStatement(citoyenQuery);
                            citoyenStmt.setInt(1, utilisateur.getId_utilisateur()); // Référence à l'ID de l'utilisateur
                            citoyenStmt.setInt(2, zoneId);  // ZoneId pour le citoyen
                            int rowsAffected = citoyenStmt.executeUpdate();

                            if (rowsAffected > 0) {
                                System.out.println("✅ Citoyen ajouté avec zoneId : " + zoneId);
                            } else {
                                System.out.println("❌ Échec de l'ajout du citoyen.");
                            }
                        } catch (SQLException e) {
                            System.out.println("❌ Erreur SQL : " + e.getMessage());
                        }
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("❌ Erreur SQL : " + e.getMessage());
        }
    }


    public void deleteById(int id) {
        String deleteResponsableQry = "DELETE FROM responsable WHERE id_responsable = ?";
        String deleteTechnicienQry = "DELETE FROM technicien WHERE id_technicien = ?";
        String deleteCitoyenQry = "DELETE FROM citoyen WHERE id_citoyen = ?";  // Requête pour supprimer dans la table citoyen
        String deleteUtilisateurQry = "DELETE FROM utilisateur WHERE id_utilisateur = ?";

        try {
            // Vérifier si l'utilisateur existe dans la table Utilisateur
            String checkUtilisateurQry = "SELECT role FROM utilisateur WHERE id_utilisateur = ?";
            PreparedStatement checkUtilisateurStmt = cnx.prepareStatement(checkUtilisateurQry);
            checkUtilisateurStmt.setInt(1, id);
            ResultSet rs = checkUtilisateurStmt.executeQuery();

            if (rs.next()) {
                String role = rs.getString("role");

                // Supprimer d'abord de la table spécifique (technicien, responsable, citoyen)
                if ("technicien".equalsIgnoreCase(role)) {
                    PreparedStatement pstmTechnicien = cnx.prepareStatement(deleteTechnicienQry);
                    pstmTechnicien.setInt(1, id);
                    int affectedRowsTechnicien = pstmTechnicien.executeUpdate();

                    if (affectedRowsTechnicien > 0) {
                        System.out.println("✅ Technicien avec l'ID " + id + " supprimé de la table Technicien.");
                    }
                } else if ("responsable".equalsIgnoreCase(role)) {
                    PreparedStatement pstmResponsable = cnx.prepareStatement(deleteResponsableQry);
                    pstmResponsable.setInt(1, id);
                    int affectedRowsResponsable = pstmResponsable.executeUpdate();

                    if (affectedRowsResponsable > 0) {
                        System.out.println("✅ Responsable avec l'ID " + id + " supprimé de la table Responsable.");
                    }
                } else if ("citoyen".equalsIgnoreCase(role)) {  // Si l'utilisateur est un citoyen
                    PreparedStatement pstmCitoyen = cnx.prepareStatement(deleteCitoyenQry);
                    pstmCitoyen.setInt(1, id);
                    int affectedRowsCitoyen = pstmCitoyen.executeUpdate();

                    if (affectedRowsCitoyen > 0) {
                        System.out.println("✅ Citoyen avec l'ID " + id + " supprimé de la table Citoyen.");
                    }
                }

                // Suppression de l'utilisateur de la table Utilisateur
                PreparedStatement pstmUtilisateur = cnx.prepareStatement(deleteUtilisateurQry);
                pstmUtilisateur.setInt(1, id);
                int affectedRowsUtilisateur = pstmUtilisateur.executeUpdate();

                if (affectedRowsUtilisateur > 0) {
                    System.out.println("✅ Utilisateur avec l'ID " + id + " supprimé de la table Utilisateur.");
                }
            } else {
                System.out.println("⚠️ Aucun utilisateur trouvé avec l'ID " + id + ".");
            }

        } catch (SQLException e) {
            System.out.println("❌ Erreur SQL lors de la suppression : " + e.getMessage());
        }
    }


    public List<utilisateur> getAllUtilisateurs() {
        List<utilisateur> utilisateurs = new ArrayList<>();
        String qry = "SELECT * FROM utilisateur";  // On récupère toutes les colonnes de la table utilisateur

        try {
            Statement stm = cnx.createStatement();
            ResultSet rs = stm.executeQuery(qry);
            while (rs.next()) {
                utilisateur u = new utilisateur();
                u.setId_utilisateur(rs.getInt("id_utilisateur"));  // Remplacer 'id' par 'id_utilisateur'
                u.setNom(rs.getString("nom"));
                u.setPrenom(rs.getString("prenom"));
                u.setEmail(rs.getString("email"));
                u.setMotdepasse(rs.getString("motdepasse"));
                u.setDateinscription(rs.getDate("dateinscription"));

                // Récupération du rôle depuis la base de données
                String roleStr = rs.getString("role").trim().toLowerCase();  // On s'assure que le rôle est en minuscules pour comparaison
                Role role;

                try {
                    // On récupère le rôle et on le convertit en objet enum
                    role = Role.valueOf(roleStr);
                } catch (IllegalArgumentException e) {
                    System.out.println("⚠️ Rôle inconnu : " + roleStr);
                    continue;  // Ignore l'utilisateur si le rôle est invalide
                }

                // On assigne le rôle à l'utilisateur
                u.setRole(role);

                utilisateurs.add(u);
            }
        } catch (SQLException e) {
            System.out.println("❌ Erreur SQL : " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("❌ Erreur : Valeur de rôle invalide dans la base de données.");
        }

        return utilisateurs;
    }

    public void update(utilisateur user) {
        String sql = "UPDATE utilisateurs SET nom = ?, prenom = ?, email = ?, motdepasse = ? WHERE id_utilisateur = ?";

        try (Connection connection = MyDatabase.getConnection(); // Assurez-vous que DatabaseConnection est bien votre gestionnaire de connexion
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            // Paramètres de la requête
            stmt.setString(1, user.getNom());
            stmt.setString(2, user.getPrenom());
            stmt.setString(3, user.getEmail());
            stmt.setString(4, user.getMotdepasse());
            stmt.setInt(6, user.getId_utilisateur());

            // Exécution de la mise à jour
            int rowsUpdated = stmt.executeUpdate();

            if (rowsUpdated > 0) {
                System.out.println("Utilisateur mis à jour avec succès.");
            } else {
                System.out.println("Aucun utilisateur trouvé avec cet ID.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Erreur lors de la mise à jour de l'utilisateur.");
        }
    }



    public void updateField(int id, String fieldName, String newValue) {
        String qry;
        boolean isZoneField = fieldName.equalsIgnoreCase("zoneId"); // Vérifier si le champ à modifier est `zoneId`

        if (isZoneField) {
            // Mise à jour de la zone dans la table `citoyen`
            qry = "UPDATE citoyen SET zoneId = ? WHERE id_citoyen = ?";
        } else {
            // Mise à jour des autres informations dans la table `utilisateur`
            qry = "UPDATE utilisateur SET " + fieldName + " = ? WHERE id_utilisateur = ?";
        }

        try {
            PreparedStatement pstm = cnx.prepareStatement(qry);

            // Vérifier si la valeur à modifier est un entier (zoneId)
            if (isZoneField) {
                pstm.setInt(1, Integer.parseInt(newValue)); // Convertir en entier pour zoneId
            } else {
                pstm.setString(1, newValue); // Insérer en tant que String pour les autres champs
            }

            pstm.setInt(2, id); // ID du citoyen/utilisateur

            int affectedRows = pstm.executeUpdate();

            if (affectedRows > 0) {
                System.out.println("✅ " + fieldName + " mis à jour avec succès pour l'utilisateur/citoyen ID " + id);
            } else {
                System.out.println("❌ Aucun enregistrement trouvé avec l'ID " + id);
            }

        } catch (SQLException e) {
            System.out.println("❌ Erreur SQL lors de la mise à jour : " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("❌ Erreur : La valeur pour zoneId doit être un nombre valide.");
        }
    }
    public void updateFieldResponsable(int id, String fieldName, String newValue) {
        String qry;
        boolean isModulesField = fieldName.equalsIgnoreCase("modules"); // Vérifier si le champ à modifier est `modules`

        if (isModulesField) {
            // Mise à jour des modules dans la table `responsable`
            qry = "UPDATE responsable SET modules = ? WHERE id_responsable = ?";
        } else {
            // Mise à jour des autres informations dans la table `utilisateur`
            qry = "UPDATE utilisateur SET " + fieldName + " = ? WHERE id_utilisateur = ?";
        }

        try {
            PreparedStatement pstm = cnx.prepareStatement(qry);

            // Vérifier si la mise à jour concerne les modules (champ texte)
            pstm.setString(1, newValue);

            pstm.setInt(2, id); // ID du responsable/utilisateur

            int affectedRows = pstm.executeUpdate();

            if (affectedRows > 0) {
                System.out.println("✅ " + fieldName + " mis à jour avec succès pour l'utilisateur/responsable ID " + id);
            } else {
                System.out.println("❌ Aucun enregistrement trouvé avec l'ID " + id);
            }

        } catch (SQLException e) {
            System.out.println("❌ Erreur SQL lors de la mise à jour : " + e.getMessage());
        }
    }


    public void updateFieldTechnicien(int id, String fieldName, String newValue) {
        String qry;
        boolean isSpecialiteField = fieldName.equalsIgnoreCase("specialite"); // Vérifier si le champ à modifier est `specialite`

        if (isSpecialiteField) {
            // Mise à jour de la spécialité dans la table `technicien`
            qry = "UPDATE technicien SET specialite = ? WHERE id_technicien = ?";
        } else {
            // Mise à jour des autres informations dans la table `utilisateur`
            qry = "UPDATE utilisateur SET " + fieldName + " = ? WHERE id_utilisateur = ?";
        }

        try {
            PreparedStatement pstm = cnx.prepareStatement(qry);

            // Vérifier si la mise à jour concerne la spécialité (champ texte)
            pstm.setString(1, newValue);

            pstm.setInt(2, id); // ID du technicien/utilisateur

            int affectedRows = pstm.executeUpdate();

            if (affectedRows > 0) {
                System.out.println("✅ " + fieldName + " mis à jour avec succès pour l'utilisateur/technicien ID " + id);
            } else {
                System.out.println("❌ Aucun enregistrement trouvé avec l'ID " + id);
            }

        } catch (SQLException e) {
            System.out.println("❌ Erreur SQL lors de la mise à jour : " + e.getMessage());
        }
    }





    // Méthode pour récupérer un utilisateur par son ID
    public utilisateur getUtilisateurById(int userId) {
        utilisateur user = null;

        String query = "SELECT * FROM utilisateur WHERE id_utilisateur = ?";

        try (Connection connection = MyDatabase.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setInt(1, userId); // Set l'ID dans la requête

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    // Si un utilisateur est trouvé
                    user = new utilisateur();
                    user.setId_utilisateur(rs.getInt("id_utilisateur"));
                    user.setNom(rs.getString("nom"));
                    user.setEmail(rs.getString("email"));
                    // Ajouter d'autres champs si nécessaire
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            // Gérer les exceptions ici (par exemple, afficher un message d'erreur)
        }

        return user; // Retourner l'utilisateur trouvé ou null s'il n'existe pas
    }/*
    public boolean isEmailExists(String email) {
        // Connexion à la base de données
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/pi3a7")) {
            // Requête SQL pour vérifier si l'email existe
            String query = "SELECT COUNT(*) FROM utilisateur WHERE email = ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, email);

                // Exécuter la requête
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        // Si le résultat est supérieur à 0, l'email existe déjà
                        return resultSet.getInt(1) > 0;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Gérer l'exception ou afficher une alerte si nécessaire
        }

        return false; // Si une erreur se produit, supposez que l'email n'existe pas
    }*/
    // Générer un code de vérification
    public int generateVerificationCode() {
        return 1000 + (int) (Math.random() * 9000);
    }
    // Mettre à jour le mot de passe
    public boolean updatePassword(String email, String newPassword) {
        String encryptedPassword = PasswordEncryptor.encryptPassword(newPassword);

        String query = "UPDATE utilisateur SET motdepasse = ? WHERE email = ?";
        try (Connection cnx = MyDatabase.getInstance().getCnx();
             PreparedStatement pstm = cnx.prepareStatement(query)) {
            pstm.setString(1, encryptedPassword);
            pstm.setString(2, email);

            int rowsUpdated = pstm.executeUpdate();
            return rowsUpdated > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    // Méthode pour envoyer un email
    public void sendEmail(String toEmail, String subject, String body) throws MessagingException {
        // Configuration des propriétés SMTP
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com"); // Serveur SMTP de Gmail
        props.put("mail.smtp.port", "587"); // Port pour TLS
        props.put("mail.smtp.auth", "true"); // Authentification requise
        props.put("mail.smtp.starttls.enable", "true"); // Activation de TLS

        // Authentification
        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("mellouli.youssef11@gmail.com\n", "cnvv wklj lydi psnl");
            }
        });

        // Création du message
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress("mellouli.youssef11@gmail.com\n")); // Expéditeur
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail)); // Destinataire
        message.setSubject(subject); // Sujet
        message.setText(body); // Corps du message

        // Envoi du message
        Transport.send(message);
    }

    // Valider l'email
    public boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        return email.matches(emailRegex);
    }
    public utilisateur getByEmailAndPassword(String email, String password) {
        String qry = "SELECT * FROM utilisateur WHERE email = ?";
        try {
            PreparedStatement pstm = cnx.prepareStatement(qry);
            pstm.setString(1, email);
            ResultSet rs = pstm.executeQuery();

            if (rs.next()) {
                String hashedPassword = rs.getString("motdepasse");  // Récupérer le mot de passe haché
                String encryptedPassword = PasswordEncryptor.encryptPassword(password);  // Hachage du mot de passe fourni

                // 🔹 Afficher les mots de passe pour le débogage (ne laissez pas dans le code final)
                System.out.println("Mot de passe saisi haché : " + encryptedPassword);
                System.out.println("Mot de passe en base de données : " + hashedPassword);

                // Comparer le mot de passe haché
                if (hashedPassword.equals(encryptedPassword)) {
                    System.out.println("✅ Connexion réussie !");
                    return new utilisateur(
                            rs.getInt("id_utilisateur"),
                            rs.getString("nom"),
                            rs.getString("prenom"),
                            rs.getString("email"),
                            hashedPassword,
                            Role.valueOf(rs.getString("role")),
                            rs.getDate("dateinscription")
                    );
                } else {
                    System.out.println("❌ Mot de passe incorrect !");
                }
            } else {
                System.out.println("❌ Aucun utilisateur trouvé avec cet email.");
            }
        } catch (SQLException e) {
            System.out.println("❌ Erreur SQL : " + e.getMessage());
        }
        return null;
    }


}




