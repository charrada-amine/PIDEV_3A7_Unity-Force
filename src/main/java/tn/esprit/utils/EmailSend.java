package tn.esprit.utils;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class EmailSend {

    // Configuration SMTP
    private static final String SMTP_HOST = "smtp.gmail.com"; // Exemple avec Gmail
    private static final String SMTP_PORT = "587";
    private static final String SMTP_USERNAME = "calfados22@gmail.com"; // Votre adresse e-mail
    private static final String SMTP_PASSWORD = "rdei hzsf rxip ymiv"; // Votre mot de passe

    // Méthode pour envoyer un e-mail
    public static void sendEmail(String to, String subject, String body) {
        // Propriétés pour la configuration SMTP
        Properties properties = new Properties();
        properties.put("mail.smtp.host", SMTP_HOST);
        properties.put("mail.smtp.port", SMTP_PORT);
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");

        // Création d'une session avec authentification
        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(SMTP_USERNAME, SMTP_PASSWORD);
            }
        });

        try {
            // Création du message
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(SMTP_USERNAME));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject(subject);
            message.setText(body);

            // Envoi du message
            Transport.send(message);
            System.out.println("E-mail envoyé avec succès à " + to);
        } catch (MessagingException e) {
            System.out.println("Erreur lors de l'envoi de l'e-mail : " + e.getMessage());
        }
    }
}