package com.note_mail.controller;

import com.note_mail.model.Etudiant;
import com.note_mail.service.ExcelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class PageController {

    @Autowired
    private com.note_mail.service.PdfService pdfService;

    // Route de test pour voir le PDF dans le navigateur :
    // http://localhost:8081/test-pdf?matricule=3031&annees=L3
    @GetMapping("/test-pdf")
    @org.springframework.web.bind.annotation.ResponseBody
    public String testPdf(@org.springframework.web.bind.annotation.RequestParam String matricule,
            @org.springframework.web.bind.annotation.RequestParam String annees) {
        try {
            String path = pdfService.genererRelevePdf(matricule, annees);
            return "✅ PDF généré avec succès dans le dossier du projet : " + path;
        } catch (Exception e) {
            return "❌ Erreur : " + e.getMessage();
        }
    }

    @Autowired
    private ExcelService excelService;

    // Route 1 : Page d'accueil
    @GetMapping("/")
    public String accueil() {
        return "accueil";
    }

    // Route 2 : Page liste des étudiants (chargés depuis Excel !)
    @GetMapping("/etudiants")
    public String etudiants(Model model) {
        List<Etudiant> etudiants = excelService.getTousLesEtudiants();
        model.addAttribute("etudiants", etudiants);
        return "etudiants";
    }
}
