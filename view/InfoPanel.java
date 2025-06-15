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

    public InfoPanel(Pharmacie pharmacie, PharmacieDataListener listener) {
        this.pharmacie = pharmacie;
        this.dataListener = listener; 
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
        btnCharger = new JButton("Charger données de la pharmacie"); 
        btnSauvegarder = new JButton("Sauvegarder données de la pharmacie"); 
        btnPanel.add(btnCharger);
        btnPanel.add(btnSauvegarder);

        add(btnPanel, BorderLayout.CENTER);

        // Label message
        messageLabel = new JLabel(" ");
        messageLabel.setForeground(Color.BLUE);
        add(messageLabel, BorderLayout.SOUTH);

        // Action boutons
        btnCharger.addActionListener(_ -> chargerDonneesPharmacie()); 
        btnSauvegarder.addActionListener(_ -> sauvegarderDonneesPharmacie()); 
 
    }

    // Méthode pour mettre à jour les labels (si les infos de la pharmacie changent après un chargement)
    public void updatePharmacyInfo() {
        if (pharmacie != null) {
            nomLabel.setText("Nom : " + pharmacie.getNom());
            adresseLabel.setText("Adresse : " + pharmacie.getAdresse());
        }
    }

    private void chargerDonneesPharmacie() { 
        Pharmacie loadedPharmacie = Pharmacie.chargerDepuisFichier("produits.txt"); 
        if (loadedPharmacie != null) {
            this.pharmacie = loadedPharmacie; 
            messageLabel.setText("Données de la pharmacie chargées depuis 'produits.txt'.");
            messageLabel.setForeground(Color.BLUE);
            updatePharmacyInfo(); 
        } else {
            messageLabel.setText("Échec du chargement des données de la pharmacie.");
            messageLabel.setForeground(Color.RED);
        }
        
        // Notifier le listener que les données ont potentiellement changé
        if (dataListener != null) {
            dataListener.onPharmacieDataChanged();
        }
    }

    private void sauvegarderDonneesPharmacie() { 
        pharmacie.sauvegarderDansFichier("produits.txt"); 
        messageLabel.setText("Données de la pharmacie sauvegardées dans 'produits.txt'.");
        messageLabel.setForeground(Color.BLUE);
        
        // Notifier le listener que les données ont changé
        if (dataListener != null) {
            dataListener.onPharmacieDataChanged();
        }
    }
}