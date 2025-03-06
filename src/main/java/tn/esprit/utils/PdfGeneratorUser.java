package tn.esprit.utils;


import com.itextpdf.text.*;

import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import tn.esprit.models.utilisateur;
import java.io.FileOutputStream;
import java.io.IOException;

import java.util.List;


public class PdfGeneratorUser {

    /**
     * Génère un fichier PDF contenant la liste des utilisateurs.
     *
     * @param utilisateurs Liste des utilisateurs à exporter.
     * @param filePath Chemin du fichier PDF à créer.
     */
    public static void generateUsersPdf(List<utilisateur> utilisateurs, String filePath) {
        Document document = new Document();

        try {
            // Initialiser le PdfWriter
            PdfWriter.getInstance(document, new FileOutputStream(filePath));

            // Ouvrir le document
            document.open();

            // Ajouter un titre
            Font titleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
            Paragraph title = new Paragraph("Liste des Utilisateurs\n\n", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            // Création du tableau avec 4 colonnes
            PdfPTable table = new PdfPTable(5);
            table.setWidthPercentage(100);
            table.setSpacingBefore(10f);
            table.setSpacingAfter(10f);

            // Ajouter les en-têtes
            table.addCell("Nom");
            table.addCell("Prenom");
            table.addCell("Email");
            table.addCell("Rôle");
            table.addCell("Date inscription");

            // Ajouter les données des utilisateurs
            for (utilisateur user : utilisateurs) {
                table.addCell(user.getNom()); // Nom
                table.addCell(user.getPrenom()); // Prénom
                table.addCell(user.getEmail()); // Email
                table.addCell(user.getRole().toString()); // Rôle
                table.addCell(user.getDateinscription().toString()); // Date d'inscription
            }


            // Ajouter le tableau au document
            document.add(table);

            // Fermer le document
            document.close();

            System.out.println("✅ Fichier PDF généré avec succès : " + filePath);
        } catch (DocumentException | IOException e) {
            System.err.println("❌ Erreur lors de la génération du PDF : " + e.getMessage());
        }
    }
}
