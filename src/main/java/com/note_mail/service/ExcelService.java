package com.note_mail.service;

import com.note_mail.model.Etudiant;
import com.note_mail.model.Note;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

@Service
public class ExcelService {

    private String getFilePath() {
        // Tente de trouver data/donnees.xlsx à la racine du projet
        File f = new File("data/donnees.xlsx");
        if (f.exists()) {
            return f.getAbsolutePath();
        }
        // Secours : tente à la racine directe
        return "donnees.xlsx";
    }

    public List<Etudiant> getTousLesEtudiants() {
        List<Etudiant> etudiants = new ArrayList<>();
        File file = new File(getFilePath());

        System.out.println("🔍 Recherche du fichier Excel ici : " + file.getAbsolutePath());

        if (!file.exists()) {
            System.err.println(
                    "❌ ERREUR : Fichier Excel introuvable ! Placez 'donnees.xlsx' dans le dossier 'data' à la racine du projet.");
            return etudiants;
        }

        try (FileInputStream fis = new FileInputStream(file);
                Workbook workbook = WorkbookFactory.create(fis)) {

            // Recherche insensible à la casse de la feuille "Etudiants"
            Sheet sheet = null;
            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                if (workbook.getSheetName(i).equalsIgnoreCase("Etudiants")
                        || workbook.getSheetName(i).equalsIgnoreCase("Etudiant")) {
                    sheet = workbook.getSheetAt(i);
                    break;
                }
            }

            if (sheet == null) {
                System.err.println("❌ ERREUR : Feuille 'Etudiants' introuvable dans le fichier Excel !");
                return etudiants;
            }

            DataFormatter formatter = new DataFormatter();

            // Parcourir les lignes (en sautant l'en-tête i=0)
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null)
                    continue;

                String matricule = formatter.formatCellValue(row.getCell(0)).trim();
                if (matricule.isEmpty())
                    continue; // Ligne vide

                Etudiant e = new Etudiant();
                e.setMatricule(matricule);
                e.setNom(formatter.formatCellValue(row.getCell(1)).trim());
                e.setPrenoms(formatter.formatCellValue(row.getCell(2)).trim());
                e.setEmail(formatter.formatCellValue(row.getCell(3)).trim());
                e.setNiveau(formatter.formatCellValue(row.getCell(4)).trim());
                e.setMention(formatter.formatCellValue(row.getCell(5)).trim());
                e.setParcours(formatter.formatCellValue(row.getCell(6)).trim());

                etudiants.add(e);
            }

            System.out.println("✅ OK : " + etudiants.size() + " étudiant(s) chargé(s) depuis Excel !");

        } catch (Exception e) {
            System.err.println("❌ ERREUR lors de la lecture du fichier Excel : " + e.getMessage());
            e.printStackTrace();
        }

        return etudiants;
    }

    public Etudiant getEtudiantParMatricule(String matricule) {
        return getTousLesEtudiants().stream()
                .filter(e -> e.getMatricule().equalsIgnoreCase(matricule.trim()))
                .findFirst()
                .orElse(null);
    }

    public List<Note> getNotesParMatriculeEtAnnees(String matricule, List<String> annees) {
        List<Note> notes = new ArrayList<>();
        File file = new File(getFilePath());

        if (!file.exists())
            return notes;

        try (FileInputStream fis = new FileInputStream(file);
                Workbook workbook = WorkbookFactory.create(fis)) {

            Sheet sheet = null;
            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                if (workbook.getSheetName(i).equalsIgnoreCase("Notes")
                        || workbook.getSheetName(i).equalsIgnoreCase("Note")) {
                    sheet = workbook.getSheetAt(i);
                    break;
                }
            }

            if (sheet == null)
                return notes;

            DataFormatter formatter = new DataFormatter();

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null)
                    continue;

                String rowMatricule = formatter.formatCellValue(row.getCell(0)).trim();
                String rowAnnee = formatter.formatCellValue(row.getCell(1)).trim();

                if (rowMatricule.equalsIgnoreCase(matricule.trim()) && annees.contains(rowAnnee)) {
                    Note n = new Note();
                    n.setMatricule(rowMatricule);
                    n.setAnnee(rowAnnee);
                    n.setCodeUE(formatter.formatCellValue(row.getCell(2)).trim());
                    n.setMatiere(formatter.formatCellValue(row.getCell(3)).trim());

                    try {
                        n.setNote(Double.parseDouble(formatter.formatCellValue(row.getCell(4)).replace(",", ".")));
                    } catch (Exception ex) {
                        n.setNote(0.0);
                    }

                    try {
                        n.setCreditsUE((int) Double.parseDouble(formatter.formatCellValue(row.getCell(5))));
                    } catch (Exception ex) {
                        n.setCreditsUE(0);
                    }

                    notes.add(n);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return notes;
    }
}