package model;

import java.io.Serializable; // Pour permettre la sauvegarde/chargement si nécessaire
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Facture implements Serializable {
    private static final long serialVersionUID = 1L; // Pour la sérialisation
    private static int nextNumero = 1; // Compteur statique pour les numéros de facture

    private String numeroFacture;
    private LocalDateTime dateFacture;
    private String nomPharmacie;
    private String adressePharmacie;
    private List<LigneFacture> lignesFacture;
    private double totalGeneral;

    public Facture(String nomPharmacie, String adressePharmacie) {
        this.numeroFacture = generateNumeroFacture();
        this.dateFacture = LocalDateTime.now();
        this.nomPharmacie = nomPharmacie;
        this.adressePharmacie = adressePharmacie;
        this.lignesFacture = new ArrayList<>();
        this.totalGeneral = 0.0;
    }

    // Méthode statique pour générer le numéro de facture
    private static String generateNumeroFacture() {
        // Format: INV-YYYYMMDD-XXXX
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String datePart = LocalDateTime.now().format(formatter);
        return String.format("INV-%s-%04d", datePart, nextNumero++);
    }

    // Méthode pour réinitialiser le compteur de numéro de facture (utile pour les tests ou le chargement)
    public static void setNextNumero(int num) {
        nextNumero = num;
    }

    public void ajouterLigne(LigneFacture ligne) {
        lignesFacture.add(ligne);
        calculerTotalGeneral();
    }

    private void calculerTotalGeneral() {
        totalGeneral = 0.0;
        for (LigneFacture ligne : lignesFacture) {
            totalGeneral += ligne.getTotalLigne();
        }
    }

    // --- Getters ---
    public String getNumeroFacture() {
        return numeroFacture;
    }

    public LocalDateTime getDateFacture() {
        return dateFacture;
    }

    public String getNomPharmacie() {
        return nomPharmacie;
    }

    public String getAdressePharmacie() {
        return adressePharmacie;
    }

    public List<LigneFacture> getLignesFacture() {
        return lignesFacture;
    }

    public double getTotalGeneral() {
        return totalGeneral;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

        sb.append("******************************************************************\n");
        sb.append(String.format("%-66s\n", "PHARMACIE : " + nomPharmacie));
        sb.append(String.format("%-66s\n", "ADRESSE   : " + adressePharmacie));
        sb.append("******************************************************************\n");
        sb.append(String.format("FACTURE N°: %s\n", numeroFacture));
        sb.append(String.format("DATE      : %s\n", dateFacture.format(dateTimeFormatter)));
        sb.append("------------------------------------------------------------------\n");
        sb.append(String.format("%-15s %-30s %-10s %-15s %-15s\n", 
                                 "Réf.", "Nom Produit", "Qté", "Prix Unit.", "Total Ligne"));
        sb.append("------------------------------------------------------------------\n");

        for (LigneFacture ligne : lignesFacture) {
            sb.append(ligne.toString()).append("\n");
        }

        sb.append("------------------------------------------------------------------\n");
        sb.append(String.format("%-70s %.2f FCFA\n", "TOTAL À PAYER :", totalGeneral));
        sb.append("******************************************************************\n");
        sb.append(String.format("%-66s\n", "MERCI DE VOTRE VISITE ET À BIENTÔT !"));
        sb.append("******************************************************************\n");

        return sb.toString();
    }
}