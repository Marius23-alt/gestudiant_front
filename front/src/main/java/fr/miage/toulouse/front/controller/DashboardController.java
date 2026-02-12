package fr.miage.toulouse.front.controller;

import fr.miage.toulouse.maven.cours.Etudiant;
import fr.miage.toulouse.maven.database.Request;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;



public class DashboardController {
    @FXML private TableView<Etudiant> tableEtudiants;
    @FXML private TableColumn<Etudiant, String> colNom;
    @FXML private TableColumn<Etudiant, String> colPrenom;
    @FXML private TableColumn<Etudiant, String> colNumEtudiant;
    @FXML private TableColumn<Etudiant, String> colMention;
    @FXML private TableColumn<Etudiant, String> colParcours;
    @FXML private TableColumn<Etudiant, String> colSemestre;

    private ObservableList<Etudiant> listeEtudiants = FXCollections.observableArrayList();

    /**
     * Lie les colonnes du tableau aux attributs de la classe Etudiant puis appel chargerTableeau()
     */
    @FXML
    public void initialize() {
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colPrenom.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        colNumEtudiant.setCellValueFactory(new PropertyValueFactory<>("numEtudiant"));
        colParcours.setCellValueFactory(new PropertyValueFactory<>("id_parcours"));
        colMention.setCellValueFactory(new PropertyValueFactory<>("id_mention"));
        colSemestre.setCellValueFactory(new PropertyValueFactory<>("semestreActuel"));


        chargerTableau();
    }

    private void chargerTableau() {

        listeEtudiants.clear();
        Request req = new Request();
        listeEtudiants = req.recupEtudiant();


        tableEtudiants.setItems(listeEtudiants);
    }
}

