package view;

import javax.swing.*;
import java.awt.*;
import model.Pharmacie;

public class InfoPanel extends JPanel {
    private Pharmacie pharmacie;
    private PharmacieDataListener dataListener; 

    private JLabel nomLabel;
    private JLabel adresseLabel;

    private JButton btnCharger;
    private JButton btnSauvegarder;

    private JLabel messageLabel;

    // CONSTRUCTEUR modifié pour accepter le PharmacieDataListener
    public InfoPanel(Pharmacie pharmacie, PharmacieDataListener listener) {
        this.pharmacie = pharmacie;
        this.dataListener = listener; // Initialisation du listener

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
        // Vider la liste AVANT de charger, pour éviter les doublons si déjà des produits en mémoire
        pharmacie.getProduits().clear();
        pharmacie.chargerDepuisFichier("produits.txt");
        messageLabel.setText("Produits chargés depuis 'produits.txt'.");
        
        // Notifier le listener que les données ont changé
        if (dataListener != null) {
            dataListener.onPharmacieDataChanged();
        }
    }

    private void sauvegarderProduits() {
        pharmacie.sauvegarderDansFichier("produits.txt");
        messageLabel.setText("Produits sauvegardés dans 'produits.txt'.");
        
        // Notifier le listener que les données ont changé (utile si la sauvegarde implique un tri, etc.)
        if (dataListener != null) {
            dataListener.onPharmacieDataChanged();
        }
    }
}