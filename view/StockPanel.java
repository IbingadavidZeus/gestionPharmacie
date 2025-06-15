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

        // Panneau pour les opérations (uniquement l'approvisionnement maintenant)
        JPanel operationPanel = new JPanel(new GridLayout(1, 1, 5, 5)); // 1 ligne, 1 colonne

        // --- Approvisionnement ---
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

        // L'ancien "achatOperationPanel" a été supprimé

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

}