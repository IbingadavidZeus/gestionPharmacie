package model;

import java.io.Serializable; // Pour permettre la sauvegarde/chargement si nécessaire

public class LigneFacture implements Serializable {
    private static final long serialVersionUID = 1L; // Pour la sérialisation

    private String refProduit;
    private String nomProduit;
    private int quantite;
    private double prixUnitaire; // Prix unitaire TTC au moment de l'achat

    public LigneFacture(String refProduit, String nomProduit, int quantite, double prixUnitaire) {
        this.refProduit = refProduit;
        this.nomProduit = nomProduit;
        this.quantite = quantite;
        this.prixUnitaire = prixUnitaire;
    }

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
        return quantite * prixUnitaire;
    }

    @Override
    public String toString() {
        return String.format("%-15s %-30s %-10d %-15.2f %-15.2f",
                             refProduit, nomProduit, quantite, prixUnitaire, getTotalLigne());
    }
}