package com.note_mail.model;

public class Etudiant {
    private String matricule;
    private String nom;
    private String prenoms;
    private String email;
    private String niveau;
    private String mention;
    private String parcours;

    public Etudiant() {
    }

    public Etudiant(String matricule, String nom, String prenoms, String email, String niveau, String mention,
            String parcours) {
        this.matricule = matricule;
        this.nom = nom;
        this.prenoms = prenoms;
        this.email = email;
        this.niveau = niveau;
        this.mention = mention;
        this.parcours = parcours;
    }

    public String getNomComplet() {
        return nom + " " + prenoms;
    }

    // Getters et Setters
    public String getMatricule() {
        return matricule;
    }

    public void setMatricule(String matricule) {
        this.matricule = matricule;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenoms() {
        return prenoms;
    }

    public void setPrenoms(String prenoms) {
        this.prenoms = prenoms;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNiveau() {
        return niveau;
    }

    public void setNiveau(String niveau) {
        this.niveau = niveau;
    }

    public String getMention() {
        return mention;
    }

    public void setMention(String mention) {
        this.mention = mention;
    }

    public String getParcours() {
        return parcours;
    }

    public void setParcours(String parcours) {
        this.parcours = parcours;
    }
}