package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter; 

import model.Pharmacie;
import model.Produit;
import model.Medicament;
import model.ProduitParaPharmacie;

import java.awt.*;
import java.text.DecimalFormat;

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

        // Créer le tableau principal du stock
        String[] colonnes = {"Référence", "Nom", "Quantité", "Prix HT", "Prix TTC", "Type"};
        tableModel = new DefaultTableModel(colonnes, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(sorter);
        
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Zone de message
        messageLabel = new JLabel(" ");
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(messageLabel, BorderLayout.NORTH);

        JPanel operationPanel = new JPanel(new GridLayout(1, 1, 10, 10)); 

        // --- Approvisionnement ---
        JPanel approPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        approPanel.setBorder(BorderFactory.createTitledBorder("Approvisionnement du stock"));
        
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

        add(operationPanel, BorderLayout.SOUTH);

        // Remplir le tableau initialement
        remplirTable(); 
    }

    public void remplirTable() { 
        tableModel.setRowCount(0);

        DecimalFormat df = new DecimalFormat("0.00");

        for (Produit p : pharmacie.getProduits()) {
            String type = "";
            if (p instanceof Medicament) {
                type = "Médicament";
            } else if (p instanceof ProduitParaPharmacie) {
                type = "Parapharmacie";
            }
            
            tableModel.addRow(new Object[]{
                p.getReference(),
                p.getNom(),
                p.getQuantite(),
                df.format(p.getPrixHt()),
                df.format(p.prixTTC()),
                type
            });
        }
    }
    private void approvisionnerProduit() {
        String ref = refApproField.getText().trim();
        String qteStr = quantiteApproField.getText().trim();

        if (ref.isEmpty() || qteStr.isEmpty()) {
            messageLabel.setText("Veuillez remplir tous les champs pour l'approvisionnement.");
            messageLabel.setForeground(Color.RED);
            return;
        }

        try {
            int qte = Integer.parseInt(qteStr);
            if (qte <= 0) {
                messageLabel.setText("La quantité d'approvisionnement doit être positive.");
                messageLabel.setForeground(Color.RED);
                return;
            }

            Produit p = pharmacie.trouverProduitParReference(ref);
            if (p == null) {
                messageLabel.setText("Produit introuvable !");
                messageLabel.setForeground(Color.RED);
                return;
            }
            p.approvisionner(qte);
            messageLabel.setText("Stock mis à jour pour " + p.getNom() + ". Nouvelle quantité : " + p.getQuantite());
            messageLabel.setForeground(Color.BLUE);
            remplirTable(); // Rafraîchir le tableau
            refApproField.setText("");
            quantiteApproField.setText("");
        } catch (NumberFormatException ex) {
            messageLabel.setText("Quantité invalide pour l'approvisionnement.");
            messageLabel.setForeground(Color.RED);
        } catch (Exception ex) {
            messageLabel.setText("Une erreur est survenue lors de l'approvisionnement.");
            messageLabel.setForeground(Color.RED);
            ex.printStackTrace();
        }
    }
}