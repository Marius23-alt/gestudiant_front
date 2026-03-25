package fr.miage.toulouse.front.controller;

import fr.miage.toulouse.cours.Etudiant;
import fr.miage.toulouse.cours.Ue;
import fr.miage.toulouse.front.DataManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;

public class SaisieMasseInscriptionController {

    @FXML
    private Button btnRetour;
    @FXML private Label lblContexte;
    @FXML private CheckBox checkAll;

    @FXML private TableView<Etudiant> tableEtudiants;
    @FXML private TableColumn<Etudiant, String> colNom;
    @FXML private TableColumn<Etudiant, String> colPrenom;
    @FXML private TableColumn<Etudiant, Void> colCoche;

    @FXML private Button btnValiderMasse;

    // --- INITIALISATION ---

    private MainController mainController;

    private Ue ueSelectionne;

    private ObservableList<Etudiant> listeEtudiants = FXCollections.observableArrayList();




    @FXML
    public void initialize() {
        lblContexte.setText("Chargement...");

        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colPrenom.setCellValueFactory(new  PropertyValueFactory<>("prenom"));

        this.tableEtudiants.setItems(listeEtudiants);
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    public void setUeSelectionne(Ue ue){
        this.ueSelectionne = ue;
        if (this.ueSelectionne != null){
            lblContexte.setText("Mode Saisie de masse pour l'UE " + this.ueSelectionne.getNom());
            btnValiderMasse.setText("Inscrire à " + this.ueSelectionne.getNom());
        }

        List<Etudiant> etudiants = DataManager.getInstance().getEtudiantsAutorisesA(this.ueSelectionne);

        listeEtudiants.setAll(etudiants);
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

    /**
     * Coche ou décoche tous les étudiants présents dans le tableau.
     */
    @FXML
    private void handleSelectAll() {
        boolean selected = checkAll.isSelected();
        System.out.println("Tout sélectionner : " + selected);
    }
}
