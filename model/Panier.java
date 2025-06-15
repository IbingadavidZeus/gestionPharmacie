package model;

import java.util.HashMap;
import java.util.Map;
import java.text.DecimalFormat;

public class Panier {
    
    private Map<String, Integer> articles; 
    private Pharmacie pharmacie; 

    public Panier(Pharmacie pharmacie) {
        this.articles = new HashMap<>(); 
        this.pharmacie = pharmacie;
    }

    /**
     * Ajoute un produit au panier.
     * @param reference Référence du produit.
     * @param quantite Quantité à ajouter.
     * @return true si le produit a été ajouté (ou la quantité mise à jour), false si non trouvé.
     */
    public boolean ajouterArticle(String reference, int quantite) {
        Produit p = pharmacie.trouverProduitParReference(reference);
        if (p == null) {
            return false;
        }
        articles.put(reference, articles.getOrDefault(reference, 0) + quantite);
        return true;
    }

    /**
     * Ajoute un produit au panier en cherchant par nom.
     * Gère le cas où le nom n'est pas unique en retournant la référence du premier trouvé.
     * @param nomProduit Nom du produit.
     * @param quantite Quantité à ajouter.
     * @return La référence du produit ajouté, ou null si non trouvé.
     */
    public String ajouterArticleParNom(String nomProduit, int quantite) {
        Produit p = pharmacie.trouverProduitParNom(nomProduit); 
        if (p == null) {
            return null; 
        }
        
        articles.put(p.getReference(), articles.getOrDefault(p.getReference(), 0) + quantite);
        return p.getReference(); 
    }

    /**
     * Retire un produit du panier.
     * @param reference Référence du produit à retirer.
     * @param quantite Quantité à retirer.
     * @return true si retiré (ou quantité réduite), false si produit non trouvé ou quantité insuffisante.
     */
    public boolean retirerArticle(String reference, int quantite) {
        if (!articles.containsKey(reference)) {
            return false; 
        }
        int currentQuantite = articles.get(reference);
        if (quantite >= currentQuantite) {
            articles.remove(reference); 
        } else {
            articles.put(reference, currentQuantite - quantite);
        }
        return true;
    }

    public Map<String, Integer> getArticles() {
        return articles;
    }

    
    public double calculerTotalPanier() {
        double total = 0;
        for (Map.Entry<String, Integer> entry : articles.entrySet()) {
            Produit p = pharmacie.trouverProduitParReference(entry.getKey());
            if (p != null) {
                total += p.prixTTC() * entry.getValue();
            }
        }
        return total;
    }

    
    public void viderPanier() {
        articles.clear();
    }

    @Override
    public String toString() {
        if (articles.isEmpty()) {
            return "Le panier est vide.";
        }

        DecimalFormat df = new DecimalFormat("0.00");
        StringBuilder sb = new StringBuilder();
        sb.append("--- Contenu du Panier ---\n");
        sb.append(String.format("%-15s %-30s %-10s %-15s %-15s\n",
            "Ref.", "Nom", "Qté", "Prix U. TTC", "Sous-Total"));
        sb.append("-----------------------------------------------------------------------------------\n");

        for (Map.Entry<String, Integer> entry : articles.entrySet()) {
            Produit p = pharmacie.trouverProduitParReference(entry.getKey());
            if (p != null) {
                double prixU = p.prixTTC();
                int qte = entry.getValue();
                sb.append(String.format("%-15s %-30s %-10d %-15s %-15s\n",
                    p.getReference(), p.getNom(), qte, df.format(prixU), df.format(prixU * qte)));
            }
        }
        sb.append("-----------------------------------------------------------------------------------\n");
        sb.append(String.format("%-75s %s FCFA\n", "Total provisoire:", df.format(calculerTotalPanier())));
        sb.append("-----------------------------------------------------------------------------------\n");
        return sb.toString();
    }
}