package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import model.*;

public class AchatPanel extends JPanel {
    private Pharmacie pharmacie;

    private JComboBox<String> produitSelectionCombo;
    private JTextField quantiteChamp;
    private JButton ajouterAuPanierBtn;

    private JTable panierTable;
    private DefaultTableModel panierTableModel;

    private JLabel totalLabel;
    private JTextField montantRecuChamp;
    private JButton finaliserAchatBtn;
    private JLabel monnaieRendueLabel;

    private JLabel messageLabel;

    private List<LigneFacture> panierActuel;
    private double totalPanier;

    private PharmacieDataListener dataListener;

    public AchatPanel(Pharmacie pharmacie, PharmacieDataListener dataListener) {
        this.pharmacie = pharmacie;
        this.dataListener = dataListener;
        this.panierActuel = new ArrayList<>();
        this.totalPanier = 0.0;

        setLayout(new BorderLayout(10, 10));

        JPanel ajoutPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        ajoutPanel.setBorder(BorderFactory.createTitledBorder("Ajouter au Panier"));

        produitSelectionCombo = new JComboBox<>();
        updateProductList();
        ajoutPanel.add(new JLabel("Produit :"));
        ajoutPanel.add(produitSelectionCombo);

        quantiteChamp = new JTextField(5);
        ajoutPanel.add(new JLabel("Quantité :"));
        ajoutPanel.add(quantiteChamp);

        ajouterAuPanierBtn = new JButton("Ajouter au Panier");
        ajouterAuPanierBtn.addActionListener(_ -> ajouterProduitAuPanier());
        ajoutPanel.add(ajouterAuPanierBtn);

        add(ajoutPanel, BorderLayout.NORTH);

        JPanel panierDisplayPanel = new JPanel(new BorderLayout());
        panierDisplayPanel.setBorder(BorderFactory.createTitledBorder("Panier d'Achat"));

        String[] colonnesPanier = {"Référence", "Nom", "Quantité", "Prix Unitaire", "Total Ligne"};
        panierTableModel = new DefaultTableModel(colonnesPanier, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        panierTable = new JTable(panierTableModel);
        panierTable.setFillsViewportHeight(true);
        JScrollPane scrollPane = new JScrollPane(panierTable);
        panierDisplayPanel.add(scrollPane, BorderLayout.CENTER);
        
        JPanel panierControlPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton supprimerLigneBtn = new JButton("Supprimer du Panier");
        supprimerLigneBtn.addActionListener(_ -> supprimerProduitDuPanier());
        panierControlPanel.add(supprimerLigneBtn);
        panierDisplayPanel.add(panierControlPanel, BorderLayout.SOUTH);

        add(panierDisplayPanel, BorderLayout.CENTER);

        JPanel paiementPanel = new JPanel(new GridLayout(4, 2, 5, 5));
        paiementPanel.setBorder(BorderFactory.createTitledBorder("Paiement"));

        totalLabel = new JLabel("Total à Payer : 0.00 FCFA");
        totalLabel.setFont(new Font("Arial", Font.BOLD, 16));
        paiementPanel.add(totalLabel);
        paiementPanel.add(new JLabel(""));

        paiementPanel.add(new JLabel("Montant Reçu (FCFA) :"));
        montantRecuChamp = new JTextField(10);
        paiementPanel.add(montantRecuChamp);

        finaliserAchatBtn = new JButton("Finaliser l'Achat");
        finaliserAchatBtn.addActionListener(_ -> finaliserAchat());
        paiementPanel.add(finaliserAchatBtn);
        paiementPanel.add(new JLabel(""));

        monnaieRendueLabel = new JLabel("Monnaie à Rendre : 0.00 FCFA");
        monnaieRendueLabel.setFont(new Font("Arial", Font.BOLD, 14));
        monnaieRendueLabel.setForeground(Color.BLUE);
        paiementPanel.add(monnaieRendueLabel);
        paiementPanel.add(new JLabel(""));

        add(paiementPanel, BorderLayout.SOUTH);

        messageLabel = new JLabel(" ");
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        messageLabel.setForeground(Color.RED);
        ajoutPanel.add(messageLabel);
    }

    public void updateProductList() {
        produitSelectionCombo.removeAllItems();
        produitSelectionCombo.addItem("-- Sélectionnez un produit --");
        for (Produit p : pharmacie.getProduits()) {
            produitSelectionCombo.addItem(p.getNom() + " (Ref: " + p.getReference() + ")");
        }
        produitSelectionCombo.setSelectedIndex(0);
    }

    private void ajouterProduitAuPanier() {
        messageLabel.setText("");

        if (produitSelectionCombo.getSelectedIndex() == 0) {
            messageLabel.setText("Veuillez sélectionner un produit.");
            return;
        }

        String selectedItem = (String) produitSelectionCombo.getSelectedItem();
        String ref = selectedItem.substring(selectedItem.indexOf("Ref: ") + 5, selectedItem.lastIndexOf(")"));
        
        String quantiteStr = quantiteChamp.getText().trim();

        if (quantiteStr.isEmpty()) {
            messageLabel.setText("Veuillez entrer une quantité.");
            return;
        }

        int quantite;
        try {
            quantite = Integer.parseInt(quantiteStr);
            if (quantite <= 0) {
                messageLabel.setText("La quantité doit être positive.");
                return;
            }
        } catch (NumberFormatException ex) {
            messageLabel.setText("Quantité invalide.");
            return;
        }

        Produit produit = pharmacie.trouverProduitParReference(ref);
        if (produit == null) {
            messageLabel.setText("Produit non trouvé (erreur interne).");
            return;
        }

        LigneFacture ligneExistante = null;
        for (LigneFacture ligne : panierActuel) {
            if (ligne.getRefProduit().equals(ref)) {
                ligneExistante = ligne;
                break;
            }
        }
        
        int stockDisponible = produit.getQuantite();
        int quantiteDejaAuPanier = (ligneExistante != null) ? ligneExistante.getQuantite() : 0;
        
        if (quantite + quantiteDejaAuPanier > stockDisponible) {
             messageLabel.setText("Stock insuffisant pour " + produit.getNom() + ". Disponible: " + stockDisponible + ". Déjà au panier: " + quantiteDejaAuPanier);
             return;
        }

        if (ligneExistante != null) {
            panierActuel.remove(ligneExistante);
            panierActuel.add(new LigneFacture(ref, produit.getNom(), quantiteDejaAuPanier + quantite, produit.prixTTC()));
        } else {
            panierActuel.add(new LigneFacture(ref, produit.getNom(), quantite, produit.prixTTC()));
        }

        refreshPanierTable();
        quantiteChamp.setText("");
    }

    private void refreshPanierTable() {
        panierTableModel.setRowCount(0);
        totalPanier = 0.0;

        for (LigneFacture ligne : panierActuel) {
            Object[] row = {
                ligne.getRefProduit(),
                ligne.getNomProduit(),
                ligne.getQuantite(),
                String.format("%.2f", ligne.getPrixUnitaire()),
                String.format("%.2f", ligne.getTotalLigne())
            };
            panierTableModel.addRow(row);
            totalPanier += ligne.getTotalLigne();
        }
        totalLabel.setText("Total à Payer : " + String.format("%.2f", totalPanier) + " FCFA");
        montantRecuChamp.setText("");
        monnaieRendueLabel.setText("Monnaie à Rendre : 0.00 FCFA");
    }
    
    private void supprimerProduitDuPanier() {
        int selectedRow = panierTable.getSelectedRow();
        if (selectedRow == -1) {
            messageLabel.setText("Veuillez sélectionner une ligne à supprimer.");
            return;
        }

        panierActuel.remove(selectedRow);
        refreshPanierTable();
        messageLabel.setText("Ligne supprimée du panier.");
    }

    private void finaliserAchat() {
        messageLabel.setText("");

        if (panierActuel.isEmpty()) {
            messageLabel.setText("Le panier est vide. Veuillez ajouter des produits.");
            return;
        }

        String montantRecuStr = montantRecuChamp.getText().trim();
        if (montantRecuStr.isEmpty()) {
            messageLabel.setText("Veuillez entrer le montant reçu.");
            return;
        }

        double montantRecu;
        try {
            montantRecu = Double.parseDouble(montantRecuStr);
            if (montantRecu < totalPanier) {
                messageLabel.setText("Montant insuffisant. Il manque " + String.format("%.2f", (totalPanier - montantRecu)) + " FCFA.");
                return;
            }
        } catch (NumberFormatException ex) {
            messageLabel.setText("Montant reçu invalide.");
            return;
        }

        Facture facture = new Facture(pharmacie.getNom(), pharmacie.getAdresse());
        
        for (LigneFacture ligne : panierActuel) {
            facture.ajouterLigne(ligne);
        }

        pharmacie.ajouterFacture(facture);

        for (LigneFacture ligne : panierActuel) {
            Produit p = pharmacie.trouverProduitParReference(ligne.getRefProduit());
            if (p != null) {
                p.achat(ligne.getQuantite());
            }
        }

        double monnaieRendue = montantRecu - totalPanier;
        monnaieRendueLabel.setText("Monnaie à Rendre : " + String.format("%.2f", monnaieRendue) + " FCFA");
        monnaieRendueLabel.setForeground(Color.BLUE);

        messageLabel.setText("Achat finalisé ! Facture N°" + facture.getNumeroFacture() + " générée.");
        messageLabel.setForeground(Color.GREEN);
        
        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        FactureDialog factureDialog = new FactureDialog(parentFrame, facture);
        factureDialog.setVisible(true); 

        panierActuel.clear();
        refreshPanierTable(); 

        if (dataListener != null) {
            dataListener.onPharmacieDataChanged();
        }
    }
}