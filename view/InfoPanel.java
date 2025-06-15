package view;

import javax.swing.*;
import java.awt.*;
import model.Pharmacie;

public class InfoPanel extends JPanel {
    private Pharmacie pharmacie;
    private MainFrame mainFrame;
    private JLabel nomLabel;
    private JLabel adresseLabel;
    private JButton btnCharger;
    private JButton btnSauvegarder;
    private JLabel messageLabel;

    // Constructeur modifié pour accepter MainFrame
    public InfoPanel(Pharmacie pharmacie, MainFrame mainFrame) {
        this.pharmacie = pharmacie;
        this.mainFrame = mainFrame; // Initialise la référence

        setLayout(new BorderLayout(10, 10));

        // Infos pharmacie
        JPanel infoPanel = new JPanel(new GridLayout(2, 1));
        nomLabel = new JLabel("Nom : " + pharmacie.getNom());
        adresseLabel = new JLabel("Adresse : " + pharmacie.getAdresse());
        infoPanel.add(nomLabel);
        infoPanel.add(adresseLabel);
        infoPanel.setBorder(BorderFactory.createTitledBorder("Informations Pharmacie"));

        add(infoPanel, BorderLayout.NORTH);

        // Panel boutons
        JPanel btnPanel = new JPanel(new FlowLayout());
        btnCharger = new JButton("Charger produits depuis fichier");
        btnSauvegarder = new JButton("Sauvegarder produits dans fichier");
        btnPanel.add(btnCharger);
        btnPanel.add(btnSauvegarder);

        add(btnPanel, BorderLayout.CENTER);

        // Label message
        messageLabel = new JLabel(" ");
        messageLabel.setForeground(Color.BLUE);
        add(messageLabel, BorderLayout.SOUTH);

        // Action boutons
        btnCharger.addActionListener(_ -> chargerProduits());
        btnSauvegarder.addActionListener(_ -> sauvegarderProduits());
    }

    private void chargerProduits() {
        pharmacie.getProduits().clear();  
        pharmacie.chargerDepuisFichier("produits.txt");
        messageLabel.setText("Produits chargés depuis 'produits.txt'.");
        
        // Rafraîchir le tableau du stock après le chargement
        if (mainFrame != null) {
            mainFrame.refreshStockTable();
        }
    }

    private void sauvegarderProduits() {
        pharmacie.sauvegarderDansFichier("produits.txt");
        messageLabel.setText("Produits sauvegardés dans 'produits.txt'.");
        
        if (mainFrame != null) {
            mainFrame.refreshStockTable();
        }
    }
}