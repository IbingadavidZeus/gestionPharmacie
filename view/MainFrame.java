package view;

import javax.swing.*;
import java.awt.*;
import model.Pharmacie;

public class MainFrame extends JFrame {
    private Pharmacie pharmacie;
    
    private AjouterProduitPanel ajouterPanel;
    private StockPanel stockPanel; 
    private AchatPanel achatPanel; 
    private InfoPanel infoPanel;

    public MainFrame() {
        pharmacie = new Pharmacie("Pharmacie Centrale La Bonne Santé", "IAI - Libreville, Gabon");

        setTitle("Gestion Pharmacie");
        setSize(900, 700); 
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JTabbedPane onglets = new JTabbedPane();

        ajouterPanel = new AjouterProduitPanel(pharmacie);
        stockPanel = new StockPanel(pharmacie); 
        
        infoPanel = new InfoPanel(pharmacie, this); 
       
        achatPanel = new AchatPanel(pharmacie) {
            @Override
            protected void finaliserAchat() {
                super.finaliserAchat(); 
                stockPanel.remplirTable(); 
            }
        };

        onglets.addTab("Ajouter Produit", ajouterPanel);
        onglets.addTab("Stock / Approvisionnement", stockPanel); 
        onglets.addTab("Achat", achatPanel); 
        onglets.addTab("Infos / Sauvegarde", infoPanel);

        add(onglets, BorderLayout.CENTER);
    }

    /**
     * Méthode publique pour rafraîchir le tableau du stock.
     * Appelée par d'autres panneaux (comme InfoPanel) si nécessaire.
     */
    public void refreshStockTable() {
        if (stockPanel != null) {
            stockPanel.remplirTable();
        }
    }
}