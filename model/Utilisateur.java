package model;

import java.io.Serializable;

public class Utilisateur implements Serializable {
    private static final long serialVersionUID = 1L;

    private String nomUtilisateur;
    private String motDePasse;
    private String role; 

    public Utilisateur(String nomUtilisateur, String motDePasse, String role) {
        this.nomUtilisateur = nomUtilisateur;
        this.motDePasse = motDePasse;
        this.role = role;
    }

    public String getNomUtilisateur() {
        return nomUtilisateur;
    }

    public String getMotDePasse() {
        return motDePasse;
    }

    public String getRole() {
        return role;
    }

    // Méthode pour vérifier le mot de passe (simple comparaison pour cet exemple)
    public boolean verifierMotDePasse(String motDePasseSaisi) {
        return this.motDePasse.equals(motDePasseSaisi);
    }

    @Override
    public String toString() {
        return "Utilisateur [nomUtilisateur=" + nomUtilisateur + ", role=" + role + "]";
    }
}