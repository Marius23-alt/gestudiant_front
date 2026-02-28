package fr.miage.toulouse.front.controller;

import fr.miage.toulouse.cours.Etudiant;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class SaisieMasseController {

    // --- LIAISONS FXML ---

    @FXML private Button btnRetour;
    @FXML private Label lblContexte;

    @FXML private TableView<Etudiant> tableEtudiants;
    @FXML private TableColumn<Etudiant, String> colNom;
    @FXML private TableColumn<Etudiant, String> colPrenom;
    @FXML private TableColumn<Etudiant, Void> colCoche;

    @FXML private Button btnValiderMasse;

    // --- INITIALISATION ---

    @FXML
    public void initialize() {
        // Textes par défaut pour voir si la page charge bien
        lblContexte.setText("Mode Saisie de masse (En travaux 🚧)");
        btnValiderMasse.setText("Action (En attente)");
    }

    // --- ACTIONS DES BOUTONS ---

    /**
     * Gère le clic sur le bouton "< Retour".
     * Pour l'instant, on met juste un message dans la console.
     */
    @FXML
    private void handleRetour(ActionEvent event) {
        System.out.println("Clic sur Retour : Il faudra coder la navigation vers la page UE !");
    }

    /**
     * Gère le clic sur le gros bouton d'action en bas.
     */
    @FXML
    private void handleValiderMasse(ActionEvent event) {
        System.out.println("Clic sur Valider Masse : Il faudra traiter les cases cochées !");
    }

    // Les méthodes pour changer le mode (Jaune/Bleu) et charger les étudiants arriveront plus tard !
}