package model;

import java.io.Serializable;

public class LigneFacture implements Serializable {
    private static final long serialVersionUID = 1L; 
    private String refProduit;
    private String nomProduit;
    private int quantite;
    private double prixUnitaire;
    private double totalLigne;

    public LigneFacture(String refProduit, String nomProduit, int quantite, double prixUnitaire) {
        this.refProduit = refProduit;
        this.nomProduit = nomProduit;
        this.quantite = quantite;
        this.prixUnitaire = prixUnitaire;
        this.totalLigne = quantite * prixUnitaire; 
    }

    // Getters pour accéder aux attributs
    public String getRefProduit() {
        return refProduit;
    }

    public String getNomProduit() {
        return nomProduit;
    }

    public int getQuantite() {
        return quantite;
    }

    public double getPrixUnitaire() {
        return prixUnitaire;
    }

    public double getTotalLigne() {
        return totalLigne;
    }
}