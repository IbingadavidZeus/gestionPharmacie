package view;

import model.Utilisateur; 

/**
 * Interface pour écouter les événements de connexion réussie.
 * Typiquement implémentée par la MainFrame pour gérer la transition après connexion.
 */
public interface LoginListener {
    void onLoginSuccess(Utilisateur utilisateur);
}