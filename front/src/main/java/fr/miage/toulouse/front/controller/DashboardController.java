package fr.miage.toulouse.front.controller;

import fr.miage.toulouse.cours.Etudiant;
import fr.miage.toulouse.database.Request;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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

    private MainController mainController;

    /**
     * Initialisation du mainControlleur pour pouvoir ensuite appeler ses méthodes (en particulier pour changement vue dashboard -> profilEtudiant
     * @param mainController Un objet de type MainController
     */
    public void setMainController(MainController mainController){
        this.mainController = mainController;
    }

    /**
     * Configure la gestion des interactions souris sur le tableau des étudiants.
     * Cette méthode définit une (RowFactory = comportement des lignes) personnalisée pour détecter les double-clics
     * sur les lignes du tableau. Lorsqu'un étudiant est double-cliqué :
     * - L'objet (Etudiant) correspondant à la ligne est récupéré
     * - Une demande d'ouverture de la vue "Profil" est envoyée au contrôleur principal
     * via (handleProfilEtudiant)
     */
    private void initialiserGestionDoubleClic() {
        tableEtudiants.setRowFactory(tv -> {
            TableRow<Etudiant> row = new TableRow<>();

            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {

                    Etudiant etudiantSelectionne = row.getItem();

                    if (mainController != null) {
                        System.out.println("Ouverture du profil de : " + etudiantSelectionne.getNom());
                        mainController.handleProfilEtudiant(etudiantSelectionne);
                    } else {
                        System.out.println("Erreur : MainController n'est pas lié !");
                    }
                }
            });

            return row;
        });
    }

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

        initialiserGestionDoubleClic();

        chargerTableau();
    }

    private void chargerTableau() {

        listeEtudiants.clear();
        Request req = new Request();
        listeEtudiants = req.recupEtudiant();


        tableEtudiants.setItems(listeEtudiants);
    }
}

