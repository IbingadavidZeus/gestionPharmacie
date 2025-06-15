package model;

public abstract class Produit {
    protected String reference;
    protected String nom;    
    protected double prixHt; // Modifié en protected pour accès direct dans Pharmacie (moins idéal)
    protected int quantite;

    public Produit(String reference, String nom, double prixHt, int quantite) {
        this.reference = reference;
        this.nom = nom;
        this.prixHt = prixHt;
        this.quantite = quantite;
    }

    public abstract double prixTTC();

    public void approvisionner(int quantiteAjoutee) {
        if (quantiteAjoutee <= 0) {
            System.out.println("Erreur : la quantité à ajouter doit être > 0.");
            return;
        }
        this.quantite += quantiteAjoutee;
    }

    public double achat(int quantiteAchetee) {
        if (quantiteAchetee <= 0) {
            System.out.println("Erreur : la quantité achetée doit être > 0.");
            return 0;
        }
        if (quantiteAchetee > quantite) {
            System.out.println("Stock insuffisant ! Il reste " + quantite + " unité(s).");
            return 0;
        }
        quantite -= quantiteAchetee;
        return prixTTC() * quantiteAchetee;
    }

    public String getReference() {
        return reference;
    }

    public String getNom() {
        return nom;
    }

    public double getPrixHt() { // <--- AJOUTEZ CE GETTER
        return prixHt;
    }

    public int getQuantite() {
        return quantite;
    }

    public void afficherProduit() {
        System.out.println("Référence: " + reference + " | Nom: " + nom + " | Prix HT: " + prixHt + " | Quantité: " + quantite);
    }
}