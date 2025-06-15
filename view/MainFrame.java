package view;

import javax.swing.*;
import java.awt.*;
import model.Pharmacie;

// MainFrame implémente PharmacieDataListener
public class MainFrame extends JFrame implements PharmacieDataListener {
    private Pharmacie pharmacie;
    
    private AjouterProduitPanel ajouterPanel;
    private StockPanel stockPanel;
    private InfoPanel infoPanel;
    protected AchatPanel achatPanel; 

    public MainFrame() {
        pharmacie = new Pharmacie("Pharmacie Centrale La Bonne Santé", "IAI - Libreville, Gabon");

        setTitle("Gestion Pharmacie");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JTabbedPane onglets = new JTabbedPane();

        // Ligne corrigée: Passer 'this' (qui est l'instance de MainFrame et implémente PharmacieDataListener)
        ajouterPanel = new AjouterProduitPanel(pharmacie, this); 
        
        // Les autres sont déjà corrigés
        infoPanel = new InfoPanel(pharmacie, this); 
        stockPanel = new StockPanel(pharmacie); 
        achatPanel = new AchatPanel(pharmacie, this); 

        onglets.addTab("Ajouter Produit", ajouterPanel);
        onglets.addTab("Stock / Approvisionnement", stockPanel);
        onglets.addTab("Achat", achatPanel); // Nouvel onglet pour AchatPanel
        onglets.addTab("Infos / Sauvegarde", infoPanel);

        add(onglets, BorderLayout.CENTER);
    }

    // Implémentation de la méthode de l'interface PharmacieDataListener
    @Override
    public void onPharmacieDataChanged() {
        // Cette méthode est appelée quand les données de la pharmacie changent
        // (par exemple, après un chargement depuis fichier, un ajout de produit ou un achat)
        
        // Rafraîchir le StockPanel
        if (stockPanel != null) {
            stockPanel.remplirTable();
        }
        
        // Rafraîchir la liste des produits dans AchatPanel (au cas où de nouveaux produits seraient ajoutés)
        if (achatPanel != null) {
            achatPanel.updateProductList();
        }
        
        // Vous pouvez ajouter d'autres rafraîchissements si nécessaire
        System.out.println("Données pharmacie rafraîchies dans l'interface.");
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new MainFrame().setVisible(true);
        });
    }
}