package com.note_mail.service;

import com.note_mail.model.Etudiant;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String expediteur;

    /**
     * Envoie le relevé de notes PDF par email
     */
    public void envoyerReleve(Etudiant etudiant, String cheminPdf, String annees) throws Exception {

        File fichierPdf = new File(cheminPdf);
        if (!fichierPdf.exists()) {
            throw new IllegalArgumentException("Fichier PDF introuvable : " + cheminPdf);
        }

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        // Expéditeur & destinataire
        helper.setFrom(expediteur);
        helper.setTo(etudiant.getEmail());
        helper.setSubject("📋 Extrait de Notes - " + etudiant.getNomComplet() + " (" + annees + ")");

        // Corps HTML de l'email
        String corpsHtml = """
                <html>
                <body style="font-family: Arial, sans-serif; color: #333; max-width: 600px; margin: 0 auto;">
                    <div style="background-color: #071A10; color: #00FF66; padding: 20px; text-align: center; border-radius: 10px 10px 0 0;">
                        <h2 style="margin: 0;">🎓 ECOLE NATIONALE D'INFORMATIQUE</h2>
                        <p style="margin: 5px 0 0; color: #fff;">Université de Fianarantsoa</p>
                    </div>
                    <div style="border: 1px solid #ddd; padding: 25px; border-radius: 0 0 10px 10px;">
                        <p>Bonjour <strong>%s</strong>,</p>
                        <p>Veuillez trouver ci-joint votre <strong>extrait de notes</strong> pour le niveau <strong>%s</strong>.</p>
                        <p>Matricule : <strong>%s</strong></p>
                        <hr style="border: none; border-top: 1px solid #eee; margin: 20px 0;">
                        <p style="color: #888; font-size: 12px;">
                            Ceci est un email automatique envoyé par NOTE MAIL.<br>
                            Pour toute réclamation, contactez le service de scolarité : scolarite@eni.mg
                        </p>
                    </div>
                </body>
                </html>
                """
                .formatted(etudiant.getNomComplet(), annees, etudiant.getMatricule());

        helper.setText(corpsHtml, true);

        // Pièce jointe PDF
        FileSystemResource file = new FileSystemResource(fichierPdf);
        String nomPieceJointe = "Extrait_Notes_" + etudiant.getMatricule() + "_" + annees + ".pdf";
        helper.addAttachment(nomPieceJointe, file);

        // Envoi
        mailSender.send(message);
        System.out.println("✅ Email envoyé avec succès à : " + etudiant.getEmail());
    }
}