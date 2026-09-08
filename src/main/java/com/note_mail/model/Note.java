package com.note_mail.model;

public class Note {
    private String matricule;
    private String annee; // L1, L2, L3
    private String codeUE; // UE1, UE2, etc.
    private String matiere; // Ex: Java, Analyse
    private double note; // Ex: 16.0
    private int creditsUE; // Ex: 12

    public Note() {
    }

    public Note(String matricule, String annee, String codeUE, String matiere, double note, int creditsUE) {
        this.matricule = matricule;
        this.annee = annee;
        this.codeUE = codeUE;
        this.matiere = matiere;
        this.note = note;
        this.creditsUE = creditsUE;
    }

    // Getters et Setters
    public String getMatricule() {
        return matricule;
    }

    public void setMatricule(String matricule) {
        this.matricule = matricule;
    }

    public String getAnnee() {
        return annee;
    }

    public void setAnnee(String annee) {
        this.annee = annee;
    }

    public String getCodeUE() {
        return codeUE;
    }

    public void setCodeUE(String codeUE) {
        this.codeUE = codeUE;
    }

    public String getMatiere() {
        return matiere;
    }

    public void setMatiere(String matiere) {
        this.matiere = matiere;
    }

    public double getNote() {
        return note;
    }

    public void setNote(double note) {
        this.note = note;
    }

    public int getCreditsUE() {
        return creditsUE;
    }

    public void setCreditsUE(int creditsUE) {
        this.creditsUE = creditsUE;
    }
}