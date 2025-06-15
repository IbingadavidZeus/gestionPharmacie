package view;

import javax.swing.*;
import java.awt.*;
import model.Pharmacie;
import model.Utilisateur;

public class LoginPanel extends JPanel {
    private Pharmacie pharmacie;
    private LoginListener loginListener; 
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JLabel messageLabel;

    public LoginPanel(Pharmacie pharmacie, LoginListener listener) {
        this.pharmacie = pharmacie;
        this.loginListener = listener; 
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Titre
        JLabel titleLabel = new JLabel("Connexion à PharmacieApp");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(titleLabel, gbc);

        // Nom d'utilisateur
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST;
        add(new JLabel("Nom d'utilisateur :"), gbc);

        usernameField = new JTextField(20);
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        add(usernameField, gbc);

        // Mot de passe
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.EAST;
        add(new JLabel("Mot de passe :"), gbc);

        passwordField = new JPasswordField(20);
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        add(passwordField, gbc);

        // Bouton de connexion
        loginButton = new JButton("Se connecter");
        loginButton.addActionListener(_ -> attemptLogin());
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(loginButton, gbc);

        // Message de statut
        messageLabel = new JLabel("");
        messageLabel.setForeground(Color.RED);
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(messageLabel, gbc);
    }

    private void attemptLogin() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword()); 

        Utilisateur utilisateur = pharmacie.authentifier(username, password);

        if (utilisateur != null) {
            messageLabel.setText("Connexion réussie !");
            messageLabel.setForeground(Color.GREEN);
            if (loginListener != null) {
                loginListener.onLoginSuccess(utilisateur);
            }
        } else {
            messageLabel.setText("Nom d'utilisateur ou mot de passe incorrect.");
            messageLabel.setForeground(Color.RED);
        }
    }
}