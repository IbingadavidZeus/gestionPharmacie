package view;

import javax.swing.*;
import java.awt.*;
import model.Pharmacie;
import model.Utilisateur;

public class MainFrame extends JFrame implements PharmacieDataListener, LoginListener {

    private Pharmacie pharmacie;
    private Utilisateur currentUser;

    private AjouterProduitPanel ajouterPanel;
    private StockPanel stockPanel;
    private InfoPanel infoPanel;
    private AchatPanel achatPanel;

    private JTabbedPane onglets;
    private LoginPanel loginPanel;
    private JPanel mainContentPanel;
    private JButton deconnexionButton;

    public MainFrame() {
        pharmacie = Pharmacie.chargerDepuisFichier("produits.txt");
        if (pharmacie == null) {
            pharmacie = new Pharmacie("Pharmacie Centrale La Bonne Santé", "IAI - Libreville, Gabon");
        }

        setTitle("Gestion Pharmacie");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        loginPanel = new LoginPanel(pharmacie, this);
        
        mainContentPanel = new JPanel(new CardLayout());
        mainContentPanel.add(loginPanel, "Login");
        
        add(mainContentPanel, BorderLayout.CENTER);
        
        CardLayout cl = (CardLayout)(mainContentPanel.getLayout());
        cl.show(mainContentPanel, "Login");
    }

    @Override
    public void onLoginSuccess(Utilisateur utilisateur) {
        this.currentUser = utilisateur;
        System.out.println("Connexion réussie ! Bienvenue, " + utilisateur.getNomUtilisateur() + " (Rôle : " + utilisateur.getRole() + ")");

        mainContentPanel.removeAll();
        
        JPanel appPanel = new JPanel(new BorderLayout());

        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JLabel userInfoLabel = new JLabel("Connecté: " + utilisateur.getNomUtilisateur() + " (" + utilisateur.getRole() + ")");
        headerPanel.add(userInfoLabel);

        deconnexionButton = new JButton("Déconnexion");
        deconnexionButton.addActionListener(_ -> onLogout());
        headerPanel.add(deconnexionButton);
        appPanel.add(headerPanel, BorderLayout.NORTH);

        ajouterPanel = new AjouterProduitPanel(pharmacie, currentUser);
        stockPanel = new StockPanel(pharmacie);
        infoPanel = new InfoPanel(pharmacie, this);
        achatPanel = new AchatPanel(pharmacie, this);

        onglets = new JTabbedPane();
        onglets.addTab("Ajouter Produit", ajouterPanel);
        onglets.addTab("Stock", stockPanel);
        onglets.addTab("Achat", achatPanel);
        onglets.addTab("Infos / Sauvegarde", infoPanel);

        appPanel.add(onglets, BorderLayout.CENTER);

        mainContentPanel.add(appPanel, "Application");
        
        CardLayout cl = (CardLayout)(mainContentPanel.getLayout());
        cl.show(mainContentPanel, "Application");

        applyUserPermissions(utilisateur);

        onPharmacieDataChanged();

        revalidate();
        repaint();
    }

    private void onLogout() {
        this.currentUser = null;
        System.out.println("Déconnexion réussie.");

        loginPanel = new LoginPanel(pharmacie, this);

        mainContentPanel.removeAll();
        mainContentPanel.add(loginPanel, "Login");

        CardLayout cl = (CardLayout)(mainContentPanel.getLayout());
        cl.show(mainContentPanel, "Login");

        revalidate();
        repaint();
    }

    private void applyUserPermissions(Utilisateur user) {
        if (!"Admin".equalsIgnoreCase(user.getRole())) {
            int ajouterProduitTabIndex = onglets.indexOfTab("Ajouter Produit");
            if (ajouterProduitTabIndex != -1) {
                onglets.setEnabledAt(ajouterProduitTabIndex, false);
            }
            int infoSauvegardeTabIndex = onglets.indexOfTab("Infos / Sauvegarde");
            if (infoSauvegardeTabIndex != -1) {
            }
        }
    }

    @Override
    public void onPharmacieDataChanged() {
        stockPanel.remplirTable();
        achatPanel.updateProductList();
        infoPanel.updatePharmacyInfo();
        pharmacie.sauvegarderDansFichier("produits.txt");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new MainFrame().setVisible(true);
        });
    }
}