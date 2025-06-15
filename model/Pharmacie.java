package model;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Pharmacie implements Serializable {
    private static final long serialVersionUID = 1L;

    private String nom;
    private String adresse;
    private List<Produit> produits;
    private List<Facture> historiqueFactures;
    private List<Utilisateur> utilisateurs;

    public Pharmacie(String nom, String adresse) {
        this.nom = nom;
        this.adresse = adresse;
        this.produits = new ArrayList<>();
        this.historiqueFactures = new ArrayList<>();
        this.utilisateurs = new ArrayList<>();
        ajouterUtilisateur(new Utilisateur("davidibinga", "admin123", "admin"));
        ajouterUtilisateur(new Utilisateur("vendeur", "vendeur123", "employe"));
    }

    public void ajouterUtilisateur(Utilisateur user) {
        this.utilisateurs.add(user);
    }

    public Utilisateur authentifier(String nomUtilisateurSaisi, String motDePasseSaisi) {
        for (Utilisateur user : utilisateurs) {
            if (user.getNomUtilisateur().equals(nomUtilisateurSaisi) && user.verifierMotDePasse(motDePasseSaisi)) {
                return user;
            }
        }
        return null;
    }

    public void ajouterProduit(Produit p) {
        produits.add(p);
    }

    public void ajouterFacture(Facture facture) {
        historiqueFactures.add(facture);
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

    public Produit trouverProduitParNom(String nomProduit) {
        for (Produit p : produits) {
            if (p.getNom().equalsIgnoreCase(nomProduit)) {
                return p;
            }
        }
        return null;
    }

    public List<Produit> getProduits() {
        return produits;
    }

    public List<Facture> getHistoriqueFactures() {
        return historiqueFactures;
    }

    public String getNom() {
        return nom;
    }

    public String getAdresse() {
        return adresse;
    }

    public void sauvegarderDansFichier(String nomFichier) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(nomFichier))) {
            oos.writeObject(this);
            System.out.println("Données de la pharmacie (produits, factures, utilisateurs) sauvegardées dans '" + nomFichier + "'.");
        } catch (IOException e) {
            System.out.println("Erreur de sauvegarde: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static Pharmacie chargerDepuisFichier(String nomFichier) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(nomFichier))) {
            Pharmacie loadedPharmacie = (Pharmacie) ois.readObject();
            int maxNumero = 0;
            for(Facture f : loadedPharmacie.getHistoriqueFactures()) {
                try {
                    String numPart = f.getNumeroFacture().substring(f.getNumeroFacture().lastIndexOf('-') + 1);
                    int currentNum = Integer.parseInt(numPart);
                    if (currentNum > maxNumero) {
                        maxNumero = currentNum;
                    }
                } catch (NumberFormatException | StringIndexOutOfBoundsException e) {
                }
            }
            Facture.setNextNumero(maxNumero + 1);

            System.out.println("Données de la pharmacie (produits, factures, utilisateurs) chargées depuis '" + nomFichier + "'.");
            return loadedPharmacie;
        } catch (FileNotFoundException e) {
            System.out.println("Fichier de sauvegarde non trouvé: '" + nomFichier + "'. Création d'une nouvelle pharmacie.");
            return null;
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Erreur de chargement: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}