package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import model.Pharmacie;
import model.Produit;
import model.Medicament;
import model.ProduitParaPharmacie; 

import java.awt.*;

public class StockPanel extends JPanel {
    private Pharmacie pharmacie;
    private JTable table;
    private DefaultTableModel tableModel;

    private JTextField refApproField;
    private JTextField quantiteApproField;
    private JButton btnApprovisionner;

    private JTextField refAchatField;
    private JTextField quantiteAchatField;
    private JButton btnAcheter;

    private JLabel messageLabel;

    public StockPanel(Pharmacie pharmacie) {
        this.pharmacie = pharmacie;
        setLayout(new BorderLayout());

        // Créer le tableau
        String[] colonnes = {"Référence", "Nom", "Quantité", "Prix HT", "Prix TTC", "Type"};
        tableModel = new DefaultTableModel(colonnes, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; 
            }
        };
        table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Zone de message
        messageLabel = new JLabel(" ");
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(messageLabel, BorderLayout.NORTH);

        // Panneau pour les opérations (approvisionnement et achat)
        JPanel operationPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        JPanel approPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        approPanel.setBorder(BorderFactory.createTitledBorder("Approvisionnement du Stock"));
        approPanel.add(new JLabel("Référence :"));
        refApproField = new JTextField(10);
        approPanel.add(refApproField);
        approPanel.add(new JLabel("Quantité :"));
        quantiteApproField = new JTextField(5);
        approPanel.add(quantiteApproField);
        btnApprovisionner = new JButton("Approvisionner");
        btnApprovisionner.addActionListener(_ -> approvisionnerProduit());
        approPanel.add(btnApprovisionner);
        operationPanel.add(approPanel);

        // --- Achat (sera généralement géré par AchatPanel) ---
        JPanel achatOperationPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5)); 
        achatOperationPanel.setBorder(BorderFactory.createTitledBorder("Achat Rapide (obsolète avec AchatPanel)"));
        achatOperationPanel.add(new JLabel("Référence :"));
        refAchatField = new JTextField(10);
        achatOperationPanel.add(refAchatField);
        achatOperationPanel.add(new JLabel("Quantité :"));
        quantiteAchatField = new JTextField(5);
        achatOperationPanel.add(quantiteAchatField);
        btnAcheter = new JButton("Acheter");
        btnAcheter.addActionListener(_ -> acheterProduit());
        achatOperationPanel.add(btnAcheter);
        operationPanel.add(achatOperationPanel);

        add(operationPanel, BorderLayout.SOUTH);

        remplirTable();
    }

    public void remplirTable() {
        tableModel.setRowCount(0);
        for (Produit p : pharmacie.getProduits()) {
            String type = "";
            if (p instanceof Medicament) {
                type = "Médicament";
            } else if (p instanceof ProduitParaPharmacie) {
                type = "Parapharmacie";
            }
            Object[] row = {p.getReference(), p.getNom(), p.getQuantite(),
                            String.format("%.2f", p.getPrixHt()),
                            String.format("%.2f", p.prixTTC()), type};
            tableModel.addRow(row);
        }
    }

    private void approvisionnerProduit() {
        String ref = refApproField.getText().trim();
        String qteStr = quantiteApproField.getText().trim();

        if (ref.isEmpty() || qteStr.isEmpty()) {
            messageLabel.setText("Veuillez entrer une référence et une quantité pour approvisionner.");
            return;
        }

        try {
            int qte = Integer.parseInt(qteStr);
            if (qte <= 0) {
                messageLabel.setText("La quantité à approvisionner doit être positive.");
                return;
            }

            Produit p = pharmacie.trouverProduitParReference(ref);
            if (p == null) {
                messageLabel.setText("Produit introuvable !");
                return;
            }
            p.approvisionner(qte);
            messageLabel.setText("Stock de " + p.getNom() + " mis à jour. Nouvelle quantité: " + p.getQuantite());
            remplirTable();
            refApproField.setText("");
            quantiteApproField.setText("");
        } catch (NumberFormatException ex) {
            messageLabel.setText("Quantité invalide pour l'approvisionnement.");
        } catch (Exception ex) {
            messageLabel.setText("Erreur d'approvisionnement: " + ex.getMessage());
        }
    }

    // Cette méthode d'achat rapide dans StockPanel deviendra probablement redondante
    // une fois que l'AchatPanel est pleinement fonctionnel.
    private void acheterProduit() {
        String ref = refAchatField.getText().trim();
        String qteStr = quantiteAchatField.getText().trim();
        
        if (ref.isEmpty() || qteStr.isEmpty()) {
            messageLabel.setText("Veuillez entrer une référence et une quantité pour l'achat.");
            return;
        }

        try {
            int qte = Integer.parseInt(qteStr);
            if (qte <= 0) {
                messageLabel.setText("La quantité achetée doit être positive.");
                return;
            }

            Produit p = pharmacie.trouverProduitParReference(ref);
            if (p == null) {
                messageLabel.setText("Produit introuvable !");
                return;
            }
            
            if (qte > p.getQuantite()) {
                messageLabel.setText("Stock insuffisant ! Il reste " + p.getQuantite() + " unité(s) de " + p.getNom() + ".");
                return;
            }

            double montant = p.achat(qte);
            messageLabel.setText("Achat effectué de " + qte + " " + p.getNom() + ". Montant: " + String.format("%.2f", montant) + " FCFA");
            remplirTable(); 
            refAchatField.setText("");
            quantiteAchatField.setText("");
        } catch (NumberFormatException ex) {
            messageLabel.setText("Quantité invalide pour l'achat.");
        } catch (Exception ex) {
            messageLabel.setText("Erreur d'achat: " + ex.getMessage());
        }
    }
}