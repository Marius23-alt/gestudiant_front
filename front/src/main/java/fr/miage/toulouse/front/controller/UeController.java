package fr.miage.toulouse.front.controller;

import fr.miage.toulouse.cours.Mention;
import fr.miage.toulouse.cours.Parcour;
import fr.miage.toulouse.cours.Ue;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class UeController {

    // --- LIAISONS FXML (Obligatoires pour ne pas crasher) ---

    @FXML private ComboBox<Mention> comboMention;
    @FXML private ComboBox<Parcour> comboParcours;
    @FXML private ComboBox<String> comboAnnee;

    @FXML private Label lblTitreUe;

    @FXML private TableView<Ue> tableUe;
    @FXML private TableColumn<Ue, String> colMatiere;
    @FXML private TableColumn<Ue, Void> colInscription;
    @FXML private TableColumn<Ue, Void> colValide;

    // --- INITIALISATION ---

    /**
     * Méthode appelée automatiquement au chargement de la page.
     * Pour l'instant, on se contente d'afficher un message par défaut.
     */
    @FXML
    public void initialize() {
        lblTitreUe.setText("Page de gestion des UE (En travaux 🚧)");
    }

    // Les fonctionnalités (remplissage, boutons, clics) seront ajoutées plus tard !
}