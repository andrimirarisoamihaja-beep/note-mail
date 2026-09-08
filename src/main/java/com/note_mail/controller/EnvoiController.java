package com.note_mail.controller;

import com.note_mail.model.Etudiant;
import com.note_mail.service.EmailService;
import com.note_mail.service.ExcelService;
import com.note_mail.service.PdfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
public class EnvoiController {

    @Autowired
    private ExcelService excelService;

    @Autowired
    private PdfService pdfService;

    @Autowired
    private EmailService emailService;

    /**
     * API appelée par le modal JavaScript :
     * POST /api/envoyer
     * Params : matricule, email, annees
     */
    @PostMapping("/api/envoyer")
    public Map<String, Object> envoyerReleve(
            @RequestParam String matricule,
            @RequestParam String email,
            @RequestParam String annees) {

        Map<String, Object> response = new HashMap<>();

        try {
            // ─── 1. Vérifier que le matricule existe ───
            Etudiant etudiant = excelService.getEtudiantParMatricule(matricule.trim());
            if (etudiant == null) {
                response.put("succes", false);
                response.put("message", "Matricule \"" + matricule + "\" introuvable dans le fichier Excel.");
                return response;
            }

            // ─── 2. Vérifier que l'email correspond ───
            if (!etudiant.getEmail().equalsIgnoreCase(email.trim())) {
                response.put("succes", false);
                response.put("message", "L'adresse email ne correspond pas au matricule \"" + matricule
                        + "\".\nEmail attendu : " + etudiant.getEmail());
                return response;
            }

            // ─── 3. Générer le PDF ───
            String cheminPdf = pdfService.genererRelevePdf(matricule.trim(), annees.trim());

            // ─── 4. Envoyer l'email ───
            emailService.envoyerReleve(etudiant, cheminPdf, annees.trim());

            // ─── 5. Succès ───
            response.put("succes", true);
            response.put("message", "Le relevé de notes de " + etudiant.getNomComplet()
                    + " a été envoyé avec succès à " + etudiant.getEmail() + " !");

        } catch (IllegalArgumentException e) {
            // Erreurs métier (pas de notes, etc.)
            response.put("succes", false);
            response.put("message", e.getMessage());

        } catch (Exception e) {
            // Erreurs techniques (SMTP, fichier, etc.)
            e.printStackTrace();
            response.put("succes", false);
            response.put("message", "Erreur technique lors de l'envoi : " + e.getMessage());
        }

        return response;
    }
}