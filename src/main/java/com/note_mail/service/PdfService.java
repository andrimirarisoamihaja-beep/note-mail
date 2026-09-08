package com.note_mail.service;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import com.note_mail.model.Etudiant;
import com.note_mail.model.Note;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.File;
import java.io.FileOutputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PdfService {

    @Autowired
    private ExcelService excelService;

    private static final DecimalFormat df = new DecimalFormat("00.00");

    /**
     * Génère le relevé de notes en PDF pour un étudiant et une liste d'années (ex:
     * L2 ou L1-L2)
     * 
     * @return le chemin du fichier PDF généré
     */
    public String genererRelevePdf(String matricule, String anneesSelectionnees) throws Exception {

        // 1. Récupérer l'étudiant
        Etudiant etudiant = excelService.getEtudiantParMatricule(matricule);
        if (etudiant == null) {
            throw new IllegalArgumentException("Étudiant introuvable avec le matricule : " + matricule);
        }

        // 2. Découper les années (ex: "L1-L2" -> ["L1", "L2"])
        List<String> listeAnnees = Arrays.asList(anneesSelectionnees.split("-"));

        // 3. Récupérer les notes depuis Excel
        List<Note> notes = excelService.getNotesParMatriculeEtAnnees(matricule, listeAnnees);
        if (notes.isEmpty()) {
            throw new IllegalArgumentException(
                    "Aucune note trouvée pour l'étudiant " + matricule + " pour " + anneesSelectionnees);
        }

        // 4. Dossier de sortie
        File folder = new File("releves");
        if (!folder.exists())
            folder.mkdirs();

        String filename = "releves/Releve_" + etudiant.getMatricule() + "_" + anneesSelectionnees + ".pdf";

        // 5. Création du document PDF (A4)
        Document document = new Document(PageSize.A4, 30, 30, 30, 30);
        PdfWriter.getInstance(document, new FileOutputStream(filename));
        document.open();

        // --- EN-TÊTE ENI ---
        Font fontTitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, Color.BLACK);
        Font fontSubtitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, Color.BLACK);
        Font fontNormal = FontFactory.getFont(FontFactory.HELVETICA, 9, Color.BLACK);
        Font fontBold = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, Color.BLACK);

        Paragraph p1 = new Paragraph("UNIVERSITÉ DE FIANARANTSOA", fontTitle);
        p1.setAlignment(Element.ALIGN_CENTER);
        document.add(p1);

        Paragraph p2 = new Paragraph("ECOLE NATIONALE D'INFORMATIQUE", fontSubtitle);
        p2.setAlignment(Element.ALIGN_CENTER);
        document.add(p2);

        Paragraph p3 = new Paragraph("EXTRAIT DE NOTES",
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, Font.UNDERLINE));
        p3.setAlignment(Element.ALIGN_CENTER);
        p3.setSpacingBefore(5);
        p3.setSpacingAfter(10);
        document.add(p3);

        // --- INFOS ÉTUDIANT ---
        Paragraph pInfo = new Paragraph();
        pInfo.setFont(fontNormal);
        pInfo.add(new Chunk("Mention : ", fontBold));
        pInfo.add(etudiant.getMention() + "              ");
        pInfo.add(new Chunk("Parcours : ", fontBold));
        pInfo.add(etudiant.getParcours() + "\n");

        pInfo.add(new Chunk("Nom et prénoms : ", fontBold));
        pInfo.add(etudiant.getNomComplet() + "\n");

        pInfo.add(new Chunk("Numéro d'inscription : ", fontBold));
        pInfo.add(etudiant.getMatricule() + "              ");
        pInfo.add(new Chunk("Niveau : ", fontBold));
        pInfo.add(anneesSelectionnees + "\n");
        pInfo.setSpacingAfter(10);
        document.add(pInfo);

        // --- TABLEAU DES NOTES ---
        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100);
        table.setWidths(new float[] { 45, 15, 25, 15 });

        // En-tête du tableau
        addHeaderCell(table, "MATIERES", fontBold);
        addHeaderCell(table, "NOTES /20", fontBold);
        addHeaderCell(table, "VALIDATION DE L'UE", fontBold);
        addHeaderCell(table, "CREDITS", fontBold);

        // Regrouper par UE
        Map<String, List<Note>> notesParUE = notes.stream()
                .collect(Collectors.groupingBy(Note::getCodeUE, LinkedHashMap::new, Collectors.toList()));

        double sommeMoyennesUE = 0;
        int nombreUE = 0;
        int totalCreditsAcquis = 0;
        int totalCreditsPossibles = 0;

        for (Map.Entry<String, List<Note>> entry : notesParUE.entrySet()) {
            String codeUE = entry.getKey();
            List<Note> notesUE = entry.getValue();

            double sommeNotes = 0;
            int creditsUE = notesUE.get(0).getCreditsUE();
            totalCreditsPossibles += creditsUE;

            // Ajouter les matières de cette UE
            for (int i = 0; i < notesUE.size(); i++) {
                Note n = notesUE.get(i);
                sommeNotes += n.getNote();

                PdfPCell cellMatiere = new PdfPCell(new Phrase(n.getMatiere(), fontNormal));
                cellMatiere.setPaddingLeft(5);
                table.addCell(cellMatiere);

                PdfPCell cellNote = new PdfPCell(new Phrase(df.format(n.getNote()), fontNormal));
                cellNote.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cellNote);

                // Pour la première ligne de l'UE, on fusionne les colonnes Validation et
                // Crédits
                if (i == 0) {
                    double moyUE = notesUE.stream().mapToDouble(Note::getNote).average().orElse(0.0);
                    boolean valide = moyUE >= 10.0;

                    PdfPCell cellVal = new PdfPCell(new Phrase(valide ? "VALIDE" : "NON VALIDE", fontBold));
                    cellVal.setRowspan(notesUE.size() + 1); // +1 pour la ligne "Moyenne"
                    cellVal.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    cellVal.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(cellVal);

                    PdfPCell cellCred = new PdfPCell(new Phrase(valide ? String.valueOf(creditsUE) : "0", fontNormal));
                    cellCred.setRowspan(notesUE.size() + 1);
                    cellCred.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    cellCred.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(cellCred);

                    if (valide)
                        totalCreditsAcquis += creditsUE;
                }
            }

            // Ligne Moyenne UE
            double moyenneUE = sommeNotes / notesUE.size();
            sommeMoyennesUE += moyenneUE;
            nombreUE++;

            PdfPCell cellMoyLabel = new PdfPCell(new Phrase("Moyenne " + codeUE, fontBold));
            cellMoyLabel.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cellMoyLabel.setBackgroundColor(new Color(240, 240, 240));
            table.addCell(cellMoyLabel);

            PdfPCell cellMoyVal = new PdfPCell(new Phrase(df.format(moyenneUE), fontBold));
            cellMoyVal.setHorizontalAlignment(Element.ALIGN_CENTER);
            cellMoyVal.setBackgroundColor(new Color(240, 240, 240));
            table.addCell(cellMoyVal);
        }

        document.add(table);

        // --- RÉSULTATS GLOBAUX ---
        double moyenneGenerale = nombreUE > 0 ? (sommeMoyennesUE / nombreUE) : 0.0;
        boolean admis = moyenneGenerale >= 10.0;

        Paragraph pRes = new Paragraph();
        pRes.setSpacingBefore(10);
        pRes.setFont(fontBold);
        pRes.add("MOYENNE GENERALE : " + df.format(moyenneGenerale) + " / 20\n");
        pRes.add("RESULTAT : " + (admis ? "ADMIS ✅" : "AJOURNÉ ❌") + "\n");
        pRes.add("TOTAL CREDITS ACQUIS : " + totalCreditsAcquis + " / " + totalCreditsPossibles);
        document.add(pRes);

        // --- PIED DE PAGE & SIGNATURE ---
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Paragraph pDate = new Paragraph("\nFait à Fianarantsoa, le " + sdf.format(new Date()), fontNormal);
        pDate.setAlignment(Element.ALIGN_RIGHT);
        document.add(pDate);

        Paragraph pSign = new Paragraph("Le Responsable de la Scolarité", fontBold);
        pSign.setAlignment(Element.ALIGN_RIGHT);
        pSign.setSpacingBefore(5);
        document.add(pSign);

        document.close();
        System.out.println("✅ PDF généré avec succès : " + filename);
        return filename;
    }

    private void addHeaderCell(PdfPTable table, String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBackgroundColor(new Color(220, 220, 220));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setPadding(5);
        table.addCell(cell);
    }
}