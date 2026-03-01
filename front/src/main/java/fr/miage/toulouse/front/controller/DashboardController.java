package fr.miage.toulouse.front.controller;

import fr.miage.toulouse.cours.Etudiant;
import fr.miage.toulouse.database.Request;
import fr.miage.toulouse.front.DataManager;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;

import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;


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

    @FXML
    public void initialize() {
        // 1. Les variables simples
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colPrenom.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        colNumEtudiant.setCellValueFactory(new PropertyValueFactory<>("numEtu")); // ⚠️ Corrigé : c'est numEtu maintenant
        colSemestre.setCellValueFactory(new PropertyValueFactory<>("semestreActuel"));

        // 🌟 2. Les objets imbriqués (La magie opère ici !)
        // On dit à la colonne d'aller chercher le nom du parcours à l'intérieur de l'objet Parcours
        colParcours.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getParcour().getNom())
        );

        // On dit à la colonne d'aller chercher la mention, à l'intérieur du parcours, à l'intérieur de l'étudiant
        colMention.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getParcour().getMention().getNom())
        );

        initialiserGestionDoubleClic();
        chargerTableau();
    }

    private void chargerTableau() {
        // 🌟 FINI LES REQUÊTES SQL ICI ! On pioche directement dans le DataManager de manière instantanée
        List<Etudiant> data = DataManager.getInstance().getListeEtudiants();

        listeEtudiants = FXCollections.observableArrayList(data);
        tableEtudiants.setItems(listeEtudiants);
    }


//    /**
//     * Initialise la gestion du double-clic sur les lignes du tableau des étudiants.
//     * <p>
//     * Cette méthode personnalise le comportement des lignes ({@code TableRow}) du {@code TableView}.
//     * Lorsqu'un utilisateur effectue un double-clic sur une ligne contenant des données, la méthode :
//     * <ul>
//     * <li>Récupère l'objet {@link Etudiant} lié à la ligne sélectionnée.</li>
//     * <li>Délègue l'action au contrôleur principal ({@code MainController}) pour charger
//     * et afficher la vue détaillée du profil de cet étudiant.</li>
//     * <li>Affiche des informations de débogage dans la console pour suivre la navigation.</li>
//     * </ul>
//     * </p>
//     */
//    private void initialiserGestionDoubleClic() {
//        tableEtudiants.setRowFactory(tv -> {
//            TableRow<Etudiant> row = new TableRow<>();
//            row.setOnMouseClicked(event -> {
//                if (event.getClickCount() == 2 && (!row.isEmpty())) {
//
//                    Etudiant etudiantSelectionne = row.getItem();
//
//                    if (mainController != null) {
//                        System.out.println("Ouverture du profil de : " + etudiantSelectionne.getNom());
//                        mainController.handleProfilEtudiant(etudiantSelectionne);
//                    } else {
//                        System.out.println("Erreur : MainController n'est pas lié !");
//                    }
//                }
//            });
//
//            return row;
//        });
//    }

    /**
     * Méthode d'initialisation appelée automatiquement par JavaFX après le chargement du fichier FXML.
     * <p>
     * Cette méthode prépare et configure le tableau d'affichage des étudiants (Dashboard) :
     * <ul>
     * <li>Associe chaque colonne du tableau ({@code TableColumn}) à l'attribut correspondant
     * dans la classe modèle {@link Etudiant} grâce aux {@code PropertyValueFactory}.</li>
     * <li>Configure les interactions utilisateur en appelant la méthode de gestion du double-clic.</li>
     * <li>Déclenche le chargement initial des données depuis la base de données pour remplir
     * le tableau dès l'ouverture de la vue.</li>
     * </ul>
     * </p>
     */
//    @FXML
//    public void initialize() {
//        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
//        colPrenom.setCellValueFactory(new PropertyValueFactory<>("prenom"));
//        colNumEtudiant.setCellValueFactory(new PropertyValueFactory<>("numEtudiant"));
//        colParcours.setCellValueFactory(new PropertyValueFactory<>("idParcours"));
//        colMention.setCellValueFactory(new PropertyValueFactory<>("idMention"));
//        colSemestre.setCellValueFactory(new PropertyValueFactory<>("semestreActuel"));
//
//        initialiserGestionDoubleClic();
//
//        chargerTableau();
//    }

    /**
     * Charge ou actualise les données du tableau (Dashboard) avec la liste des étudiants.
     * <p>
     * Cette méthode effectue les opérations suivantes :
     * <ul>
     * <li>Vide la liste actuelle en mémoire pour éviter d'afficher des doublons.</li>
     * <li>Interroge la base de données via la classe {@link Request} pour récupérer
     * tous les étudiants (ayant une inscription "en_cours").</li>
     * <li>Met à jour l'interface graphique en injectant la nouvelle liste ({@code ObservableList})
     * dans le composant {@code TableView}.</li>
     * </ul>
     * </p>
     */
//    private void chargerTableau() {
//
//        listeEtudiants.clear();
//        Request req = new Request();
//        listeEtudiants = req.recupEtudiant();
//
//        tableEtudiants.setItems(listeEtudiants);
//    }
}

