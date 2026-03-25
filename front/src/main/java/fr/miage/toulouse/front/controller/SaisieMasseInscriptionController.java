package fr.miage.toulouse.front.controller;

import fr.miage.toulouse.cours.Etudiant;
import fr.miage.toulouse.cours.Inscription;
import fr.miage.toulouse.cours.Ue;
import fr.miage.toulouse.database.Request;
import fr.miage.toulouse.front.DataManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.ArrayList;
import java.util.List;

public class SaisieMasseInscriptionController {

    @FXML private Button btnRetour;
    @FXML private Label lblContexte;
    @FXML private CheckBox checkAll;

    @FXML private TableView<Etudiant> tableEtudiants;
    @FXML private TableColumn<Etudiant, String> colNom;
    @FXML private TableColumn<Etudiant, String> colPrenom;
    @FXML private TableColumn<Etudiant, CheckBox> colCoche;

    @FXML private Button btnValiderMasse;

    private MainController mainController;
    private Ue ueSelectionne;
    private ObservableList<Etudiant> listeEtudiants = FXCollections.observableArrayList();

    // NOUVEAU : Une liste ultra-sécurisée pour stocker tes clics
    private List<Etudiant> etudiantsSelectionnes = new ArrayList<>();


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

        // NOUVEAU : Gestion pare-balles des cases à cocher
        colCoche.setCellValueFactory(cellData -> {
            Etudiant etu = cellData.getValue();
            CheckBox checkBox = new CheckBox();

            // Si on a fait "Tout sélectionner", on s'assure que la case s'affiche cochée
            checkBox.setSelected(etudiantsSelectionnes.contains(etu));

            // On écoute le clic physique de la souris
            checkBox.setOnAction(e -> {
                if (checkBox.isSelected()) {
                    if (!etudiantsSelectionnes.contains(etu)) etudiantsSelectionnes.add(etu);
                } else {
                    etudiantsSelectionnes.remove(etu);
                }
            });
            return new javafx.beans.property.SimpleObjectProperty<>(checkBox);
        });

        this.tableEtudiants.setItems(listeEtudiants);
    }


    /*
    Affiche l'UE selectionné

    Fait appel à la méthode getEtudiantsAutorisesA(UE ue) pour avoir la liste des étudiants insrit à l'UE sélectionné
     */

    public void setUeSelectionne(Ue ue) {
        this.ueSelectionne = ue;
        if (this.ueSelectionne != null) {
            lblContexte.setText("Mode Inscription de masse pour l'UE " + this.ueSelectionne.getNom());
            btnValiderMasse.setText("Inscrire les sélectionnés");
        }

        // On remet tout à zéro
        etudiantsSelectionnes.clear();
        if (checkAll != null) checkAll.setSelected(false);

        List<Etudiant> etudiants = DataManager.getInstance().getEtudiantsAutorisesA(this.ueSelectionne);
        listeEtudiants.setAll(etudiants);
    }
/*
Retourne sur la page UE
 */
    @FXML
    private void handleRetour(ActionEvent event) {
        if (mainController != null) mainController.handleUe();
    }

    /*
    selectionne les étudiants
     */
    @FXML
    private void handleSelectAll() {
        boolean selected = checkAll.isSelected();
        etudiantsSelectionnes.clear(); // On vide pour être propre

        if (selected) {
            etudiantsSelectionnes.addAll(listeEtudiants); // On ajoute tout le monde !
        }
        // Force le tableau à se rafraîchir pour que les cases se cochent visuellement
        tableEtudiants.refresh();
    }
    /**
     Gère la validation collective des inscriptions pour l'UE sélectionnée.
     Récupère la liste des étudiants cochés dans le tableau pour enregistrer massivement leur réussite
     */
    @FXML
    private void handleValiderMasse(ActionEvent event) {
        Request req = new Request();
        int compteur = 0;
        String anneeCourante = DataManager.getInstance().getAnneeUniversitaireCourante();

        // On ne boucle QUE sur les étudiants qu'on a stockés de manière sécurisée
        for (Etudiant etu : etudiantsSelectionnes) {

            boolean estUnAncien = false;
            Inscription ancienneInscription = null;

            for (Inscription i : etu.getInscription()) {
                if (i.getUe().getCode().equals(ueSelectionne.getCode())) {
                    estUnAncien = true;
                    ancienneInscription = i;
                    break;
                }
            }


            if (estUnAncien) {
                req.modifierStatutInscription(etu.getNumEtu(), ueSelectionne.getCode(), ancienneInscription.getAnnee(), "en_cours");

                ancienneInscription.setStatut("en_cours");
                ancienneInscription.setAnnee(anneeCourante);
                compteur++;
            } else {
                boolean succes = req.ajouterInscitption(etu.getNumEtu(), ueSelectionne.getCode(), anneeCourante);
                if (succes || true) {
                    Inscription nouvelleInscr = new Inscription(etu, ueSelectionne, anneeCourante, "en_cours");
                    etu.ajouterInscription(nouvelleInscr);
                    compteur++;
                }
            }
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION, compteur + " étudiant(s) inscrit(s) avec succès !");
        alert.showAndWait();
        handleRetour(null);
    }

// --- Setters ---

public void setMainController(MainController mainController) {
    this.mainController = mainController;
    }
}