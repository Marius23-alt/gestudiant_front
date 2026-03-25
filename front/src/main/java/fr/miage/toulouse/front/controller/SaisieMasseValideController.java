package fr.miage.toulouse.front.controller;

import fr.miage.toulouse.cours.Etudiant;
import fr.miage.toulouse.cours.Inscription;
import fr.miage.toulouse.cours.Ue;
import fr.miage.toulouse.front.DataManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.CheckBox;
import fr.miage.toulouse.database.Request;

import java.util.List;

public class SaisieMasseValideController {
    @FXML private Button btnRetour;
    @FXML private Label lblContexte;
    @FXML private CheckBox checkAll;

    @FXML private TableView<Etudiant> tableEtudiants;
    @FXML private TableColumn<Etudiant, String> colNom;
    @FXML private TableColumn<Etudiant, String> colPrenom;
    @FXML private TableColumn<Etudiant, CheckBox> colCoche;

    @FXML private Button btnValiderMasse;
    private java.util.Map<Etudiant, CheckBox> mapCheckBoxes = new java.util.HashMap<>();

    // --- INITIALISATION ---

    private MainController mainController;

    private Ue ueSelectionne;

    private ObservableList<Etudiant> listeEtudiants = FXCollections.observableArrayList(); //Liste des étudiants qui sont présent quand on ouvre la page


    /**
     * Initialise le contrôleur et configure l'affichage du tableau.
     * Définit le texte d'attente du contexte, lie les colonnes du tableau aux attributs
     * de l'objet Etudiant et injecte la liste observable dans la vue.
     */

    @FXML
    public void initialize() {
        lblContexte.setText("Chargement...");

        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colPrenom.setCellValueFactory(new PropertyValueFactory<>("prenom"));

        colCoche.setCellValueFactory(cellData -> {
            Etudiant etu = cellData.getValue();
            // On crée la checkbox et on la range dans la Map
            CheckBox checkBox = new CheckBox();
            mapCheckBoxes.put(etu, checkBox);
            return new javafx.beans.property.SimpleObjectProperty<>(checkBox);
        });

        this.tableEtudiants.setItems(listeEtudiants);
    }


/*
    Affiche l'UE selectionné

    Fait appel à la méthode getEtudiantsInscrits(UE ue) pour avoir la liste des étudiants insrit à l'UE sélectionné
     */
    public void setUeSelectionne(Ue ue){
        this.ueSelectionne = ue;
        if (this.ueSelectionne != null){
            lblContexte.setText("Mode Saisie de masse pour l'UE " + this.ueSelectionne.getNom());
            btnValiderMasse.setText("Valider l'UE pour les sélectionnés");
        }

        // On vide la Map avant de charger les nouveaux étudiants pour éviter les restes d'une autre UE
        mapCheckBoxes.clear();

        List<Etudiant> etudiants = DataManager.getInstance().getEtudiantsInscritsA(this.ueSelectionne);
        listeEtudiants.setAll(etudiants);
    }

    // --- ACTIONS DES BOUTONS ---

    /**
     * Gère l'événement de clic sur le bouton de retour.
     * Sollicite le {@link MainController} préalablement injecté pour réafficher la vue
     * de gestion des UE. Si le contrôleur principal est manquant (problème d'injection),
     * une erreur est signalée dans la console pour faciliter le débogage.
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

    /**
      Gère la validation collective des inscriptions pour l'UE sélectionnée.
      Récupère la liste des étudiants cochés dans le tableau pour enregistrer massivement leur réussite
     */

    @FXML
    private void handleValiderMasse(ActionEvent event) {
        Request req = new Request();
        int compteur = 0;
        String annee = DataManager.getInstance().getAnneeUniversitaireCourante();

        for (Etudiant etu : listeEtudiants) {
            CheckBox cb = mapCheckBoxes.get(etu);

            if (cb != null && cb.isSelected()) {
                boolean succes = req.modifierStatutInscription(
                        etu.getNumEtu(),
                        ueSelectionne.getCode(),
                        annee,
                        "valide"
                );

                if (succes) {
                    for (Inscription inscr : etu.getInscription()) {
                        if (inscr.getUe().getCode().equals(ueSelectionne.getCode())
                                && inscr.getAnnee().equals(annee)) {
                            inscr.setStatut("valide");
                            break;
                        }
                    }
                    compteur++;
                }
            }
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION, compteur + " étudiant(s) ont été validés avec succès !");
        alert.showAndWait();
        handleRetour(null);
    }

    /**
     * Coche ou décoche tous les étudiants présents dans le tableau.
     */
    @FXML
    private void handleSelectAll() {
        boolean selected = checkAll.isSelected();
        // On parcourt toutes les checkboxes de notre Map et on les coche/décoche
        for (CheckBox cb : mapCheckBoxes.values()) {
            cb.setSelected(selected);
        }
    }



    // --- Setters ----
    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }
}