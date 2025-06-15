package model;

import java.util.*;
import java.io.*;

public class Pharmacie {
    // private static final long serialVersionUID = 1L;
    private String nom;
    private String adresse;
    private List<Produit> produits;
    private List<Facture> factures; 
    private List<Utilisateur> utilisateurs;

    public Pharmacie(String nom, String adresse) {
        this.nom = nom;
        this.adresse = adresse;
        this.produits = new ArrayList<>();
        this.factures = new ArrayList<>();
        this.utilisateurs = new ArrayList<>();
        initialiserUtilisateurs();
    }

    protected void initialiserUtilisateurs(){
        utilisateurs.add(new Utilisateur("davidibinga", "admin123", "admin"));
        utilisateurs.add(new Utilisateur("vendeur", "vendeur123", "vendeur"));
    }
    public Utilisateur authentifier(String nomUtilisateur, String motDePasse){
        for (Utilisateur user: utilisateurs){
            if (user.getNomUtilisateur().equals(nomUtilisateur) && user.verifierMotDePasse(motDePasse)){
                System.out.println("Authentification réussie pour : " + nomUtilisateur);
                return user;
            }
        }
        System.out.println("Echec de l'authentification pour : " + nomUtilisateur);
        return null;
    }
    public void ajouterProduit(Produit p) {
        produits.add(p);
    }

    public void afficherProduits() {
        for (Produit p : produits) {
            p.afficherProduit();
            System.out.println("Prix TTC: " + p.prixTTC());
            System.out.println("--------------------------");
        }
    }

    public double valeurStock() {
        double total = 0;
        for (Produit p : produits) {
            total += p.prixTTC() * p.getQuantite();
        }
        return total;
    }

    public Produit trouverProduitParReference(String reference) {
        for (Produit p : produits) {
            if (p.getReference().equalsIgnoreCase(reference)) {
                return p;
            }
        }
        return null;
    }

    // Méthode pour trouver un produit par son nom (utilisée par le panier)
    public Produit trouverProduitParNom(String nomProduit) {
        for (Produit p : produits) {
            if (p.getNom().equalsIgnoreCase(nomProduit)) {
                return p; // Retourne le premier trouvé
            }
        }
        return null;
    }
    
    // Ajoute une facture complète
    public void ajouterFacture(Facture f) {
        factures.add(f);
    }

    public List<Produit> getProduits() {
        return produits;
    }

    public List<Facture> getFactures() {
        return factures;
    }

    public String getNom() {
        return nom;
    }

    public String getAdresse() {
        return adresse;
    }

    public void sauvegarderDansFichier(String nomFichier) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(nomFichier))) {
            for (Produit p : produits) {
                if (p instanceof Medicament) {
                    Medicament m = (Medicament) p;
                    writer.write("MEDICAMENT;" + m.getReference() + ";" + m.getNom() + ";" +
                                 m.getPrixHt() + ";" + m.getQuantite() + ";" +
                                 m.isGenerique() + ";" + m.isOrdonnance());
                } else if (p instanceof ProduitParaPharmacie) {
                    ProduitParaPharmacie pp = (ProduitParaPharmacie) p;
                    writer.write("PARAPHARMACIE;" + pp.getReference() + ";" + pp.getNom() + ";" +
                                 pp.getPrixHt() + ";" + pp.getQuantite() + ";" + pp.getType());
                }
                writer.newLine();
            }
            System.out.println("Produits sauvegardés dans le fichier.");
        } catch (IOException e) {
            System.err.println("Erreur lors de la sauvegarde du fichier : " + e.getMessage());
        }
    }

    public boolean chargerDepuisFichier(String nomFichier) {
        this.produits.clear(); 
        try (BufferedReader reader = new BufferedReader(new FileReader(nomFichier))) {
            String ligne;
            while ((ligne = reader.readLine()) != null) {
                if (ligne.trim().isEmpty()) {
                    continue;
                }
                
                String[] parties = ligne.split(";");
                
                if (parties.length < 5) {
                    System.err.println("Ligne mal formée (nombre de champs insuffisant) : " + ligne);
                    continue; 
                }

                String typeProduit = parties[0];
                String ref = parties[1];
                String nomProduit = parties[2];
                double prixHt = 0.0;
                int qte = 0;

                try {
                    prixHt = Double.parseDouble(parties[3]);
                    qte = Integer.parseInt(parties[4]);
                } catch (NumberFormatException e) {
                    System.err.println("Erreur de format numérique dans la ligne : " + ligne + " - " + e.getMessage());
                    continue;
                }

                if (typeProduit.equals("MEDICAMENT")) {
                    if (parties.length < 7) {
                        System.err.println("Ligne MEDICAMENT mal formée (nombre de champs insuffisant) : " + ligne);
                        continue;
                    }
                    boolean generique = Boolean.parseBoolean(parties[5]);
                    boolean ordonnance = Boolean.parseBoolean(parties[6]);
                    Medicament m = new Medicament(ref, nomProduit, prixHt, qte, generique, ordonnance);
                    this.ajouterProduit(m);
                } else if (typeProduit.equals("PARAPHARMACIE")) {
                    if (parties.length < 6) {
                        System.err.println("Ligne PARAPHARMACIE mal formée (nombre de champs insuffisant) : " + ligne);
                        continue;
                    }
                    String type = parties[5];
                    ProduitParaPharmacie p = new ProduitParaPharmacie(ref, nomProduit, prixHt, qte, type);
                    this.ajouterProduit(p);
                } else {
                    System.err.println("Type de produit inconnu dans la ligne : " + ligne);
                }
            }
            System.out.println("Produits chargés depuis le fichier.");
            return true;
        } catch (FileNotFoundException e) {
            System.err.println("Erreur: Le fichier '" + nomFichier + "' n'a pas été trouvé. " + e.getMessage());
            return false;
        } catch (IOException e) {
            System.err.println("Erreur de lecture du fichier : " + e.getMessage());
            return false;
        } catch (Exception e) {
            System.err.println("Une erreur inattendue est survenue lors du chargement : " + e.getMessage());
            return false;
        }
    }
}