package view;

import model.Facture;
import javax.swing.*;
import java.awt.*;
import java.awt.print.PrinterException;
// import java.awt.print.PrinterJob;

public class FactureDialog extends JDialog {
    private JTextArea factureTextArea;
    private JButton printButton;

    public FactureDialog(JFrame parent, Facture facture) {
        super(parent, "Détails de la Facture", true); 
        setSize(550, 600); 
        setLocationRelativeTo(parent); 
        setLayout(new BorderLayout(10, 10));

        // Zone de texte pour afficher la facture
        factureTextArea = new JTextArea();
        factureTextArea.setFont(new Font("Monospaced", Font.PLAIN, 12)); 
        factureTextArea.setEditable(false); 
        factureTextArea.setText(facture.toString()); 

        JScrollPane scrollPane = new JScrollPane(factureTextArea);
        add(scrollPane, BorderLayout.CENTER);

        // Bouton Imprimer
        printButton = new JButton("Imprimer la Facture");
        printButton.addActionListener(_ -> {
            try {
                // Utilise la fonctionnalité d'impression de JTextArea
                boolean complete = factureTextArea.print();
                if (complete) {
                    JOptionPane.showMessageDialog(this, "Impression terminée.", "Impression", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Impression annulée.", "Impression", JOptionPane.WARNING_MESSAGE);
                }
            } catch (PrinterException pe) {
                JOptionPane.showMessageDialog(this, "Erreur lors de l'impression: " + pe.getMessage(), "Erreur d'impression", JOptionPane.ERROR_MESSAGE);
            }
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(printButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }
}