package model;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Facture implements Serializable {
    private static final long serialVersionUID = 1L; // For serialization
    private String numeroFacture;
    private LocalDateTime dateHeure;
    private List<LigneFacture> lignesFacture;
    private double totalFacture;

    // Nouveaux champs pour les informations de la pharmacie
    private String pharmacieNom;
    private String pharmacieAdresse;

    // Constructeur modifié pour accepter le nom et l'adresse de la pharmacie
    public Facture(String pharmacieNom, String pharmacieAdresse) {
        this.numeroFacture = "FAC-" + System.currentTimeMillis(); // ID unique simple
        this.dateHeure = LocalDateTime.now();
        this.lignesFacture = new ArrayList<>();
        this.totalFacture = 0.0;
        this.pharmacieNom = pharmacieNom;
        this.pharmacieAdresse = pharmacieAdresse;
    }

    public void ajouterLigne(LigneFacture ligne) {
        lignesFacture.add(ligne);
        totalFacture += ligne.getTotalLigne();
    }

    public String getNumeroFacture() {
        return numeroFacture;
    }

    public LocalDateTime getDateHeure() {
        return dateHeure;
    }

    public List<LigneFacture> getLignesFacture() {
        return lignesFacture;
    }

    public double getTotalFacture() {
        return totalFacture;
    }

    @Override
    public String toString() {
        DecimalFormat df = new DecimalFormat("0.00");
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

        StringBuilder sb = new StringBuilder();
        sb.append("--- FACTURE ---\n");
        sb.append("Pharmacie: ").append(pharmacieNom).append("\n"); 
        sb.append("Adresse:   ").append(pharmacieAdresse).append("\n"); 
        sb.append("----------------\n");
        sb.append("Numéro Facture: ").append(numeroFacture).append("\n");
        sb.append("Date & Heure:   ").append(dateHeure.format(dtf)).append("\n");
        sb.append("-----------------------------------------------------------------\n");
        sb.append(String.format("%-10s %-25s %-10s %-10s %-10s\n", "Ref", "Nom Produit", "Qté", "Prix Unit", "Total"));
        sb.append("-----------------------------------------------------------------\n");

        for (LigneFacture ligne : lignesFacture) {
            sb.append(String.format("%-10s %-25s %-10d %-10s %-10s\n",
                    ligne.getRefProduit(),
                    ligne.getNomProduit(),
                    ligne.getQuantite(),
                    df.format(ligne.getPrixUnitaire()),
                    df.format(ligne.getTotalLigne())));
        }
        sb.append("-----------------------------------------------------------------\n");
        sb.append(String.format("%-45s %-10s\n", "TOTAL À PAYER:", df.format(totalFacture)));
        sb.append("-----------------------------------------------------------------\n");
        return sb.toString();
    }
}