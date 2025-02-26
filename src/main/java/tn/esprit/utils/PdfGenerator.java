package tn.esprit.utils;



import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import tn.esprit.models.profile;

import java.io.FileOutputStream;
import java.io.IOException;

public class PdfGenerator {

    /**
     * Génère un fichier PDF contenant les informations du profil.
     *
     * @param profile Le profil à exporter.
     * @param filePath Le chemin du fichier PDF à créer.
     */
    public static void generateProfilePdf(profile profile, String filePath) {
        // Créer un nouveau document PDF
        Document document = new Document();

        try {
            // Initialiser le PdfWriter
            PdfWriter.getInstance(document, new FileOutputStream(filePath));

            // Ouvrir le document
            document.open();

            // Ajouter les informations du profil au document
            document.add(new Paragraph("Informations du Profil"));
            document.add(new Paragraph(" ")); // Ligne vide
            document.add(new Paragraph("ID Profil: " + profile.getIdprofile()));
            document.add(new Paragraph("Consommation Jour: " + profile.getConsommationJour() + " kWh"));
            document.add(new Paragraph("Consommation Mois: " + profile.getConsommationMois() + " kWh"));
            document.add(new Paragraph("Coût Estimé: " + profile.getCoutEstime() + " €"));
            document.add(new Paragraph("Durée Activité: " + profile.getDureeActivite() + " heures"));
            document.add(new Paragraph("Lampadaire ID: " + profile.getLampadaireId()));

            // Fermer le document
            document.close();

            System.out.println("✅ Fichier PDF généré avec succès : " + filePath);
        } catch (DocumentException | IOException e) {
            System.err.println("❌ Erreur lors de la génération du PDF : " + e.getMessage());
        }
    }
}