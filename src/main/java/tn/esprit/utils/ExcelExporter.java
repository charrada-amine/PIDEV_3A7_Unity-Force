package tn.esprit.utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import tn.esprit.models.profile;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class ExcelExporter {

    public static void exportProfilesToExcel(List<profile> profiles, String filePath) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Profiles");

            // Créer l'en-tête
            Row headerRow = sheet.createRow(0);
            String[] columns = {"ID", "Consommation Jour", "Consommation Mois", "Coût Estimé", "Durée Activité", "Source ID", "Lampadaire ID"};
            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
            }

            // Remplir les données
            int rowNum = 1;
            for (profile profil : profiles) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(profil.getIdprofile());
                row.createCell(1).setCellValue(profil.getConsommationJour());
                row.createCell(2).setCellValue(profil.getConsommationMois());
                row.createCell(3).setCellValue(profil.getCoutEstime());
                row.createCell(4).setCellValue(profil.getDureeActivite());
                row.createCell(5).setCellValue(profil.getSourceId());
                row.createCell(6).setCellValue(profil.getLampadaireId());
            }

            // Ajuster la taille des colonnes
            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // Écrire le fichier
            try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
                workbook.write(fileOut);
                System.out.println("✅ Fichier Excel exporté avec succès !");
            }
        } catch (IOException e) {
            System.out.println("❌ Erreur lors de l'exportation du fichier Excel : " + e.getMessage());
        }
    }
}