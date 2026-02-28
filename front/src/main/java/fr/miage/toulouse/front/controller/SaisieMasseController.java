package fr.miage.toulouse.front.controller;

import fr.miage.toulouse.cours.Etudiant;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class SaisieMasseController {
    @FXML private Button btnRetour;
    @FXML private Label lblContexte;

    @FXML private TableView<Etudiant> tableEtudiants;
    @FXML private TableColumn<Etudiant, String> colNom;
    @FXML private TableColumn<Etudiant, String> colPrenom;
    @FXML private TableColumn<Etudiant, Void> colCoche;

    @FXML private Button btnValiderMasse;

    // --- INITIALISATION ---

    private MainController mainController;

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    @FXML
    public void initialize() {
        lblContexte.setText("Mode Saisie de masse (En travaux 🚧)");
        btnValiderMasse.setText("Action (En attente)");
    }

    // --- ACTIONS DES BOUTONS ---

    /**
     * Gère l'événement de clic sur le bouton de retour.
     * <p>
     * Sollicite le {@link MainController} préalablement injecté pour réafficher la vue
     * de gestion des UE. Si le contrôleur principal est manquant (problème d'injection),
     * une erreur est signalée dans la console pour faciliter le débogage.
     * </p>
     *
     * @param event L'événement déclenché par le clic de l'utilisateur sur le bouton.
     */
    @FXML
    private void handleRetour(ActionEvent event) {
        System.out.println("Clic sur le bouton retour détecté !");

        if (mainController != null) {
            System.out.println("Le MainController est bien là, on retourne sur la page UE !");
            mainController.handleUe();
        } else {
            System.out.println("ERREUR : Le mainController est null. L'injection a raté !");
        }
    }

    @FXML
    private void handleValiderMasse(ActionEvent event) {
        System.out.println("Clic sur Valider Masse : Il faudra traiter les cases cochées !");
    }
}