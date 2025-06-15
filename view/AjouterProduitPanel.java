package view;

import javax.swing.*;
import java.awt.*;
import model.*;

public class AjouterProduitPanel extends JPanel {
    private Pharmacie pharmacie;

    private JTextField refField;
    private JTextField nomField;
    private JTextField prixField;
    private JTextField quantiteField;
    private JComboBox<String> typeCombo;
    
    // Champs spécifiques
    private JCheckBox generiqueCheck;
    private JCheckBox ordonnanceCheck;
    private JTextField typeParaField;

    private JButton btnAjouter;
    private JLabel messageLabel;

    public AjouterProduitPanel(Pharmacie pharmacie) {
        this.pharmacie = pharmacie;
        setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridLayout(10, 2, 5, 5));

        formPanel.add(new JLabel("Référence :"));
        refField = new JTextField();
        formPanel.add(refField);

        formPanel.add(new JLabel("Nom :"));
        nomField = new JTextField();
        formPanel.add(nomField);

        formPanel.add(new JLabel("Prix HT :"));
        prixField = new JTextField();
        formPanel.add(prixField);

        formPanel.add(new JLabel("Quantité :"));
        quantiteField = new JTextField();
        formPanel.add(quantiteField);

        formPanel.add(new JLabel("Type de produit :"));
        typeCombo = new JComboBox<>(new String[]{"Médicament", "Parapharmacie"});
        formPanel.add(typeCombo);

        // Médicament spécifiques
        formPanel.add(new JLabel("Générique ?"));
        generiqueCheck = new JCheckBox();
        formPanel.add(generiqueCheck);

        formPanel.add(new JLabel("Nécessite ordonnance ?"));
        ordonnanceCheck = new JCheckBox();
        formPanel.add(ordonnanceCheck);

        // Parapharmacie spécifique
        formPanel.add(new JLabel("Catégorie parapharmacie :"));
        typeParaField = new JTextField();
        formPanel.add(typeParaField);

        btnAjouter = new JButton("Ajouter");
        messageLabel = new JLabel(" ");

        add(formPanel, BorderLayout.CENTER);
        add(btnAjouter, BorderLayout.SOUTH);
        add(messageLabel, BorderLayout.NORTH);

        // Gérer affichage champs spécifiques selon type
        typeCombo.addActionListener(_ -> updateChampsSpecifiques());
        updateChampsSpecifiques();

        btnAjouter.addActionListener(_ -> ajouterProduit());
    }

    private void updateChampsSpecifiques() {
        boolean estMedicament = typeCombo.getSelectedItem().equals("Médicament");
        generiqueCheck.setEnabled(estMedicament);
        ordonnanceCheck.setEnabled(estMedicament);

        typeParaField.setEnabled(!estMedicament);

        if (estMedicament) {
            typeParaField.setText("");
        } else {
            generiqueCheck.setSelected(false);
            ordonnanceCheck.setSelected(false);
        }
    }

    private void ajouterProduit() {
        String ref = refField.getText().trim();
        String nom = nomField.getText().trim();
        String prixStr = prixField.getText().trim();
        String quantiteStr = quantiteField.getText().trim();

        if (ref.isEmpty() || nom.isEmpty() || prixStr.isEmpty() || quantiteStr.isEmpty()) {
            messageLabel.setText("Veuillez remplir tous les champs obligatoires.");
            return;
        }

        double prixHt;
        int quantite;
        try {
            prixHt = Double.parseDouble(prixStr);
            quantite = Integer.parseInt(quantiteStr);
            if (prixHt <= 0 || quantite < 0) {
                messageLabel.setText("Prix et quantité doivent être positifs.");
                return;
            }
        } catch (NumberFormatException ex) {
            messageLabel.setText("Prix ou quantité invalide.");
            return;
        }

        String typeProduit = (String) typeCombo.getSelectedItem();
        if (typeProduit.equals("Médicament")) {
            boolean generique = generiqueCheck.isSelected();
            boolean ordonnance = ordonnanceCheck.isSelected();
            Medicament med = new Medicament(ref, nom, prixHt, quantite, generique, ordonnance);
            pharmacie.ajouterProduit(med);
        } else {
            String typePara = typeParaField.getText().trim();
            if (typePara.isEmpty()) {
                messageLabel.setText("Veuillez renseigner la catégorie parapharmacie.");
                return;
            }
            ProduitParaPharmacie para = new ProduitParaPharmacie(ref, nom, prixHt, quantite, typePara);
            pharmacie.ajouterProduit(para);
        }

        messageLabel.setText("Produit ajouté avec succès !");
        clearForm();
    }

    private void clearForm() {
        refField.setText("");
        nomField.setText("");
        prixField.setText("");
        quantiteField.setText("");
        generiqueCheck.setSelected(false);
        ordonnanceCheck.setSelected(false);
        typeParaField.setText("");
    }
}

