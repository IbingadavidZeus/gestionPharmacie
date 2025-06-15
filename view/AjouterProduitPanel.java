package view;

import javax.swing.*;
import java.awt.*;
import model.*;

public class AjouterProduitPanel extends JPanel {
    private Pharmacie pharmacie;
    private Utilisateur utilisateurConnecte;
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

    // Constructeur modifié pour accepter l'utilisateur connecté
    public AjouterProduitPanel(Pharmacie pharmacie, Utilisateur utilisateur) {
        this.pharmacie = pharmacie;
        this.utilisateurConnecte = utilisateur;
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

        JPanel medicamentOptionsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        generiqueCheck = new JCheckBox("Générique");
        ordonnanceCheck = new JCheckBox("Sur ordonnance");
        medicamentOptionsPanel.add(generiqueCheck);
        medicamentOptionsPanel.add(ordonnanceCheck);
        formPanel.add(new JLabel("Options Médicament :"));
        formPanel.add(medicamentOptionsPanel);

        JPanel parapharmacieOptionsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        typeParaField = new JTextField(15);
        parapharmacieOptionsPanel.add(new JLabel("Catégorie Parapharmacie :"));
        parapharmacieOptionsPanel.add(typeParaField);
        formPanel.add(new JLabel("Options Parapharmacie :"));
        formPanel.add(parapharmacieOptionsPanel);

        // Listener pour basculer les champs en fonction du type de produit sélectionné
        typeCombo.addActionListener(_ -> updateProductTypeFields());
        updateProductTypeFields(); 
        add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        btnAjouter = new JButton("Ajouter Produit");
        btnAjouter.addActionListener(_ -> ajouterProduit());
        buttonPanel.add(btnAjouter);

        add(buttonPanel, BorderLayout.SOUTH);

        messageLabel = new JLabel(" ");
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(messageLabel, BorderLayout.NORTH);

        applyPermissions();
    }

    public AjouterProduitPanel(Pharmacie pharmacie2, MainFrame mainFrame) {
        
    }

    private void updateProductTypeFields() {
        String selectedType = (String) typeCombo.getSelectedItem();
        boolean isMedicament = "Médicament".equals(selectedType);

        generiqueCheck.setVisible(isMedicament);
        ordonnanceCheck.setVisible(isMedicament);
        typeParaField.setVisible(!isMedicament);
        }

    private void applyPermissions() {
        boolean isAdmin = utilisateurConnecte != null && "Admin".equalsIgnoreCase(utilisateurConnecte.getRole());

        refField.setEnabled(isAdmin);
        nomField.setEnabled(isAdmin);
        prixField.setEnabled(isAdmin);
        quantiteField.setEnabled(isAdmin);
        typeCombo.setEnabled(isAdmin);
        generiqueCheck.setEnabled(isAdmin);
        ordonnanceCheck.setEnabled(isAdmin);
        typeParaField.setEnabled(isAdmin);
        btnAjouter.setEnabled(isAdmin);

        if (!isAdmin) {
            messageLabel.setText("Vous n'avez pas les permissions pour ajouter des produits.");
            messageLabel.setForeground(Color.ORANGE);
        } else {
            messageLabel.setText("Connecté en tant qu'Admin. Vous pouvez ajouter des produits.");
            messageLabel.setForeground(Color.BLACK);
        }
    }

    private void ajouterProduit() {
        if (utilisateurConnecte == null || !"Admin".equalsIgnoreCase(utilisateurConnecte.getRole())) {
            messageLabel.setText("Accès refusé : Seul un administrateur peut ajouter des produits.");
            messageLabel.setForeground(Color.RED);
            return;
        }

        String ref = refField.getText().trim();
        String nom = nomField.getText().trim();
        String prixStr = prixField.getText().trim();
        String quantiteStr = quantiteField.getText().trim();

        if (ref.isEmpty() || nom.isEmpty() || prixStr.isEmpty() || quantiteStr.isEmpty()) {
            messageLabel.setText("Veuillez remplir tous les champs.");
            messageLabel.setForeground(Color.RED);
            return;
        }

        double prixHt;
        int quantite;
        try {
            prixHt = Double.parseDouble(prixStr);
            quantite = Integer.parseInt(quantiteStr);
            if (prixHt <= 0 || quantite < 0) { // La quantité peut être 0 lors de l'ajout initial
                messageLabel.setText("Prix doit être positif et quantité non négative.");
                messageLabel.setForeground(Color.RED);
                return;
            }
        } catch (NumberFormatException ex) {
            messageLabel.setText("Prix ou quantité invalide.");
            messageLabel.setForeground(Color.RED);
            return;
        }

        // Vérifier si la référence existe déjà
        if (pharmacie.trouverProduitParReference(ref) != null) {
            messageLabel.setText("Erreur : Une référence de produit existe déjà avec cette référence.");
            messageLabel.setForeground(Color.RED);
            return;
        }


        String typeProduit = (String) typeCombo.getSelectedItem();
        if ("Médicament".equals(typeProduit)) {
            boolean generique = generiqueCheck.isSelected();
            boolean ordonnance = ordonnanceCheck.isSelected();
            Medicament med = new Medicament(ref, nom, prixHt, quantite, generique, ordonnance);
            pharmacie.ajouterProduit(med);
        } else { // Parapharmacie
            String typePara = typeParaField.getText().trim();
            if (typePara.isEmpty()) {
                messageLabel.setText("Veuillez renseigner la catégorie parapharmacie.");
                messageLabel.setForeground(Color.RED);
                return;
            }
            ProduitParaPharmacie para = new ProduitParaPharmacie(ref, nom, prixHt, quantite, typePara);
            pharmacie.ajouterProduit(para);
        }

        messageLabel.setText("Produit ajouté avec succès !");
        messageLabel.setForeground(Color.BLUE);
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
        typeCombo.setSelectedIndex(0); // Réinitialise à "Médicament"
        updateProductTypeFields(); // Mettre à jour l'affichage des champs
    }
}