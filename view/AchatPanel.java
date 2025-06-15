package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import model.Pharmacie;
import model.Produit;
import model.Facture;
import model.LigneFacture;
import model.Panier;

import java.awt.*;
import java.text.DecimalFormat;
import java.util.Map;

public class AchatPanel extends JPanel {
    private Pharmacie pharmacie;
    private Panier panier; 
    
    // Champs et boutons pour l'ajout au panier
    private JTextField nomProduitPanierField;
    private JTextField quantitePanierField;
    private JButton btnAjouterAuPanier;
    private JTextArea panierTextArea; 
    private JButton btnFinaliserAchat; 
    private JButton btnViderPanier; 

    private JLabel messageLabel;

    private JTable suggestionTable;
    private DefaultTableModel suggestionTableModel;

    public AchatPanel(Pharmacie pharmacie) {
        this.pharmacie = pharmacie;
        this.panier = new Panier(pharmacie); 

        System.out.println("AchatPanel: Initialisation. Nombre de produits dans la pharmacie: " + pharmacie.getProduits().size());

        setLayout(new BorderLayout(10, 10)); 

        messageLabel = new JLabel(" ");
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(messageLabel, BorderLayout.NORTH);

        // --- Panneau de suggestions de produits (à gauche) ---
        String[] suggestionColumns = {"Référence", "Nom", "Quantité Dispo", "Prix TTC"};
        suggestionTableModel = new DefaultTableModel(suggestionColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; 
            }
        };
        suggestionTable = new JTable(suggestionTableModel);
        suggestionTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        suggestionTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting() && suggestionTable.getSelectedRow() != -1) {
                    int selectedRow = suggestionTable.getSelectedRow();
                    String productName = (String) suggestionTableModel.getValueAt(selectedRow, 1);
                    nomProduitPanierField.setText(productName);
                    messageLabel.setText("Produit sélectionné: " + productName);
                    messageLabel.setForeground(Color.BLACK); 
                }
            }
        });
        
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(suggestionTableModel);
        suggestionTable.setRowSorter(sorter);

        JScrollPane suggestionScrollPane = new JScrollPane(suggestionTable);
        suggestionScrollPane.setPreferredSize(new Dimension(350, 0));
        JPanel suggestionPanel = new JPanel(new BorderLayout());
        suggestionPanel.setBorder(BorderFactory.createTitledBorder("Suggestions de Produits Disponibles"));
        suggestionPanel.add(suggestionScrollPane, BorderLayout.CENTER);
        add(suggestionPanel, BorderLayout.WEST); 
        
        JPanel mainAchatContentPanel = new JPanel(new BorderLayout(5, 5));

        // Sous-panneau pour l'input d'ajout au panier
        JPanel panierInputPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        panierInputPanel.setBorder(BorderFactory.createTitledBorder("Ajouter des articles au panier"));
        
        panierInputPanel.add(new JLabel("Nom Produit :"));
        nomProduitPanierField = new JTextField(15);
        panierInputPanel.add(nomProduitPanierField);
        
        panierInputPanel.add(new JLabel("Quantité :"));
        quantitePanierField = new JTextField(5);
        panierInputPanel.add(quantitePanierField);
        
        btnAjouterAuPanier = new JButton("Ajouter au Panier");
        btnAjouterAuPanier.addActionListener(_ -> ajouterProduitAuPanier());
        panierInputPanel.add(btnAjouterAuPanier);
        mainAchatContentPanel.add(panierInputPanel, BorderLayout.NORTH);

        // Sous-panneau pour l'affichage et la finalisation du panier
        JPanel panierDisplayPanel = new JPanel(new BorderLayout(5,5));
        panierDisplayPanel.setBorder(BorderFactory.createTitledBorder("Contenu du Panier"));

        panierTextArea = new JTextArea(10, 60); 
        panierTextArea.setEditable(false);
        panierTextArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane panierScrollPane = new JScrollPane(panierTextArea);
        panierDisplayPanel.add(panierScrollPane, BorderLayout.CENTER);

        JPanel panierButtonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnViderPanier = new JButton("Vider Panier");
        btnViderPanier.addActionListener(_ -> viderLePanier());
        panierButtonsPanel.add(btnViderPanier);

        btnFinaliserAchat = new JButton("Finaliser Achat");
        btnFinaliserAchat.addActionListener(_ -> finaliserAchat());
        panierButtonsPanel.add(btnFinaliserAchat);
        panierDisplayPanel.add(panierButtonsPanel, BorderLayout.SOUTH);
        
        mainAchatContentPanel.add(panierDisplayPanel, BorderLayout.CENTER);

        add(mainAchatContentPanel, BorderLayout.CENTER);

        populateProductSuggestionsTable();
        updatePanierDisplay(); 
    }

    public void populateProductSuggestionsTable() { 
        suggestionTableModel.setRowCount(0); 
        DecimalFormat df = new DecimalFormat("0.00");
        
        System.out.println("AchatPanel: populateProductSuggestionsTable() appelé.");
        int productsAddedToTable = 0;

        for (Produit p : pharmacie.getProduits()) {
            System.out.println("  Produit trouvé: " + p.getNom() + " (Quantité: " + p.getQuantite() + ")");
            
            if (p.getQuantite() > 0) {
                suggestionTableModel.addRow(new Object[]{
                    p.getReference(),
                    p.getNom(),
                    p.getQuantite(),
                    df.format(p.prixTTC())
                });
                productsAddedToTable++;
            }
        }
        System.out.println("AchatPanel: " + productsAddedToTable + " produits ajoutés à la table de suggestions.");
    }

    public void updatePanierDisplay() { 
        panierTextArea.setText(panier.toString());
    }

    private void ajouterProduitAuPanier() {
        String nomProduit = nomProduitPanierField.getText().trim();
        String qteStr = quantitePanierField.getText().trim();

        if (nomProduit.isEmpty() || qteStr.isEmpty()) {
            messageLabel.setText("Veuillez remplir le nom et la quantité pour ajouter au panier.");
            messageLabel.setForeground(Color.RED);
            return;
        }

        try {
            int qte = Integer.parseInt(qteStr);
            if (qte <= 0) {
                messageLabel.setText("La quantité doit être positive.");
                messageLabel.setForeground(Color.RED);
                return;
            }

            Produit produitEnStock = pharmacie.trouverProduitParNom(nomProduit);
            if (produitEnStock == null) {
                messageLabel.setText("Produit '" + nomProduit + "' introuvable dans le stock.");
                messageLabel.setForeground(Color.RED);
                return;
            }

            int quantiteDejaDansPanier = panier.getArticles().getOrDefault(produitEnStock.getReference(), 0);
            if (produitEnStock.getQuantite() < (qte + quantiteDejaDansPanier)) {
                messageLabel.setText("Stock insuffisant pour " + produitEnStock.getNom() + ". Disponible : " + produitEnStock.getQuantite());
                messageLabel.setForeground(Color.RED);
                return;
            }

            String addedRef = panier.ajouterArticleParNom(nomProduit, qte);
            if (addedRef != null) {
                messageLabel.setText(qte + " x '" + produitEnStock.getNom() + "' ajouté(s) au panier.");
                messageLabel.setForeground(Color.BLUE);
                updatePanierDisplay();
                nomProduitPanierField.setText("");
                quantitePanierField.setText("");
            } else {
                messageLabel.setText("Erreur lors de l'ajout au panier. Produit non trouvé."); 
                messageLabel.setForeground(Color.RED);
            }

        } catch (NumberFormatException ex) {
            messageLabel.setText("Quantité invalide.");
            messageLabel.setForeground(Color.RED);
        } catch (Exception ex) {
            messageLabel.setText("Une erreur est survenue lors de l'ajout au panier.");
            messageLabel.setForeground(Color.RED);
            ex.printStackTrace();
        }
    }

    private void viderLePanier() {
        if (panier.getArticles().isEmpty()) {
            messageLabel.setText("Le panier est déjà vide.");
            messageLabel.setForeground(Color.ORANGE);
            return;
        }
        panier.viderPanier();
        updatePanierDisplay();
        messageLabel.setText("Panier vidé.");
        messageLabel.setForeground(Color.BLUE);
    }

    protected void finaliserAchat() {
        if (panier.getArticles().isEmpty()) {
            messageLabel.setText("Le panier est vide. Aucun achat à finaliser.");
            messageLabel.setForeground(Color.ORANGE);
            return;
        }

        // Récupérer les informations de la pharmacie à passer à Facture
        String pharmaNom = pharmacie.getNom();
        String pharmaAdresse = pharmacie.getAdresse();

        // Créer la facture en passant les informations de la pharmacie
        Facture facture = new Facture(pharmaNom, pharmaAdresse); 
        StringBuilder erreurs = new StringBuilder();
        
        for (Map.Entry<String, Integer> entry : panier.getArticles().entrySet()) {
            String refProduit = entry.getKey();
            int quantiteDemande = entry.getValue();

            Produit p = pharmacie.trouverProduitParReference(refProduit);
            if (p == null) {
                erreurs.append("Produit '" + refProduit + "' introuvable. Article ignoré.\n");
                continue;
            }

            if (p.getQuantite() < quantiteDemande) {
                erreurs.append("Stock insuffisant pour '" + p.getNom() + "' (demandé: " + quantiteDemande + ", disponible: " + p.getQuantite() + "). Article ignoré.\n");
                continue;
            }

            p.achat(quantiteDemande); 
            LigneFacture ligne = new LigneFacture(p.getReference(), p.getNom(), quantiteDemande, p.prixTTC());
            facture.ajouterLigne(ligne);
        }

        if (facture.getLignesFacture().isEmpty()) {
            messageLabel.setText("Aucun produit n'a pu être acheté. Le panier a été vidé.");
            messageLabel.setForeground(Color.RED);
        } else {
            pharmacie.ajouterFacture(facture); 
            messageLabel.setText("Achat finalisé ! Total : " + String.format("%.2f", facture.getTotalFacture()) + " FCFA.");
            messageLabel.setForeground(Color.BLUE);
            
            double totalAPayer = facture.getTotalFacture();
            double montantRecu = 0.0;
            boolean validInput = false;

            // Boucle pour demander le montant reçu jusqu'à ce qu'il soit valide ou que l'utilisateur annule
            while (!validInput) {
                String input = JOptionPane.showInputDialog(this, 
                                String.format("Total à payer: %.2f FCFA\nEntrez le montant reçu:", totalAPayer),
                                "Paiement", JOptionPane.QUESTION_MESSAGE);

                if (input == null) { // L'utilisateur a annulé
                    messageLabel.setText("Paiement annulé.");
                    messageLabel.setForeground(Color.ORANGE);
                    // Si l'utilisateur annule le paiement, annuler la transaction et remettre le stock
                    // Pour cet exemple, nous allons juste vider le panier et rafraîchir l'affichage
                    panier.viderPanier(); 
                    updatePanierDisplay();
                    populateProductSuggestionsTable(); // Rafraîchir l'affichage du stock
                    return; // Annuler la finalisation de l'achat
                }

                try {
                    montantRecu = Double.parseDouble(input);
                    if (montantRecu < totalAPayer) {
                        JOptionPane.showMessageDialog(this, "Montant insuffisant. Veuillez entrer un montant égal ou supérieur au total.", "Erreur de Paiement", JOptionPane.ERROR_MESSAGE);
                    } else {
                        validInput = true;
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Montant invalide. Veuillez entrer un nombre valide.", "Erreur de Paiement", JOptionPane.ERROR_MESSAGE);
                }
            }

            double montantRendu = montantRecu - totalAPayer;
            DecimalFormat df = new DecimalFormat("0.00");

            // Construire le texte complet de la facture incluant les détails de paiement
            StringBuilder fullInvoiceText = new StringBuilder(facture.toString());
            fullInvoiceText.append("\n-----------------------------------------------------------------\n");
            fullInvoiceText.append(String.format("%-45s %-10s\n", "MONTANT REÇU:", df.format(montantRecu)));
            fullInvoiceText.append(String.format("%-45s %-10s\n", "MONTANT RENDU:", df.format(montantRendu)));
            if (erreurs.length() > 0) {
                fullInvoiceText.append("\n--- Notes ---\n").append(erreurs.toString());
            }

            JTextArea factureTextArea = new JTextArea(fullInvoiceText.toString());
            factureTextArea.setEditable(false);
            factureTextArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
            JScrollPane scrollPane = new JScrollPane(factureTextArea);
            scrollPane.setPreferredSize(new Dimension(600, 400));

            JOptionPane.showMessageDialog(this, scrollPane, "Facture de la Commande", JOptionPane.INFORMATION_MESSAGE);
        }

        panier.viderPanier(); 
        updatePanierDisplay(); 
        populateProductSuggestionsTable(); // Rafraîchir les suggestions car le stock a changé
    }
}