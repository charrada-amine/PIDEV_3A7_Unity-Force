package tn.esprit.utils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import tn.esprit.models.profile;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;

public class QrcodeGenerator {

    /**
     * Génère un code QR contenant les informations du profil.
     *
     * @param profile Le profil à exporter.
     * @param filePath Le chemin du fichier QR code à créer.
     * @param width La largeur du QR code.
     * @param height La hauteur du QR code.
     */
    public static void generateProfileQrCode(profile profile, String filePath, int width, int height) {
        // Convertir les informations du profil en une chaîne de caractères
        String qrCodeText = "Informations du Profil\n" +
                "ID Profil: " + profile.getIdprofile() + "\n" +
                "Consommation Jour: " + profile.getConsommationJour() + " kWh\n" +
                "Consommation Mois: " + profile.getConsommationMois() + " kWh\n" +
                "Coût Estimé: " + profile.getCoutEstime() + " €\n" +
                "Durée Activité: " + profile.getDureeActivite() + " heures\n" +
                "Lampadaire ID: " + profile.getLampadaireId() + "\n";

        // Générer le QR code
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        try {
            BitMatrix bitMatrix = qrCodeWriter.encode(qrCodeText, BarcodeFormat.QR_CODE, width, height);

            // Enregistrer le QR code dans un fichier
            Path path = FileSystems.getDefault().getPath(filePath);
            MatrixToImageWriter.writeToPath(bitMatrix, "PNG", path);

            System.out.println("✅ QR code généré avec succès : " + filePath);
        } catch (WriterException | IOException e) {
            System.err.println("❌ Erreur lors de la génération du QR code : " + e.getMessage());
        }
    }
}