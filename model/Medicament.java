package model;

public class Medicament extends Produit {
    private boolean generique;
    private boolean ordonnance;

    public Medicament(String reference, String nom, double prixHt, int quantite, boolean generique, boolean ordonnance) {
        super(reference, nom, prixHt, quantite); 
        this.generique = generique;
        this.ordonnance = ordonnance;
    }

    @Override
    public double prixTTC() {
        return prixHt * 1.18;
    }

    public boolean isGenerique() {
        return generique;
    }

    public boolean isOrdonnance() {
        return ordonnance;
    }

    @Override
    public void afficherProduit() {
        super.afficherProduit();
        System.out.println("Type: Médicament | Générique: " + generique + " | Ordonnance: " + ordonnance);
    }
}
