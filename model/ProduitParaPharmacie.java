package model;

public class ProduitParaPharmacie extends Produit {
    private String type;

    public ProduitParaPharmacie(String reference, String nom, double prixHt, int quantite, String type) {
        super(reference, nom, prixHt, quantite); 
        this.type = type;
    }

    @Override
    public double prixTTC() {
        return prixHt * 1.10;
    }

    public String getType() {
        return type;
    }

    @Override
    public void afficherProduit() {
        super.afficherProduit();
        System.out.println("Type: Parapharmacie | Catégorie: " + type);
    }
}

