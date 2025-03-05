package tn.esprit.utils;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

public class SmsUtil {

    // Remplacez ces valeurs par vos informations Twilio
    private static final String ACCOUNT_SID = "ACbfa0991c9e4bf8e800bdb37e013718f7";
    private static final String AUTH_TOKEN = "a805b5686ba6654f2e83d45dd62924d1";
    private static final String TWILIO_PHONE_NUMBER = "+15302912977"; // Format : +17628155615

    // Initialisation de Twilio
    static {
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
    }

    // Méthode pour envoyer un SMS
    public static void sendSms(String toPhoneNumber, String messageBody) {
        try {
            Message message = Message.creator(
                    new PhoneNumber(toPhoneNumber), // Numéro du destinataire
                    new PhoneNumber(TWILIO_PHONE_NUMBER), // Numéro Twilio
                    messageBody // Corps du message
            ).create();

            System.out.println("SMS envoyé avec succès ! SID : " + message.getSid());
        } catch (Exception e) {
            System.out.println("Erreur lors de l'envoi du SMS : " + e.getMessage());
        }
    }
}