package fr.miage.toulouse.front.controller;

import fr.miage.toulouse.cours.Etudiant;
import fr.miage.toulouse.database.Request;
import fr.miage.toulouse.front.DataManager;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
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

    @FXML private ComboBox<String> comboMention;
    @FXML private ComboBox<String> comboParcours;
    @FXML private ComboBox<String> comboSemestre;

    private ObservableList<Etudiant> listeEtudiants = FXCollections.observableArrayList();
    private MainController mainController;

    /**
     * Initialise la référence vers le contrôleur principal.
     * <p>
     * Cette liaison est nécessaire pour permettre la navigation entre les différentes vues
     * (par exemple, du Dashboard vers le Profil de l'étudiant).
     * </p>
     *
     * @param mainController L'instance du {@link MainController} gérant la fenêtre principale.
     */
    public void setMainController(MainController mainController){
        this.mainController = mainController;
    }

    /**
     * Configure l'événement de double-clic sur les lignes du tableau des étudiants.
     * <p>
     * Lorsqu'une ligne contenant un étudiant est double-cliquée, cette méthode récupère
     * l'objet {@link Etudiant} correspondant et demande au {@link MainController} d'ouvrir
     * la vue détaillée de son profil.
     * </p>
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
     * Méthode d'initialisation appelée automatiquement par JavaFX après le chargement du fichier FXML.
     * <p>
     * Elle configure les colonnes du tableau (y compris l'extraction des propriétés imbriquées
     * comme la mention ou le parcours via {@code SimpleStringProperty}), initialise la détection
     * des clics, charge les données depuis le {@link DataManager} et met en place les filtres.
     * </p>
     */
    @FXML
    public void initialize() {
        // 1. Les variables simples
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colPrenom.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        colNumEtudiant.setCellValueFactory(new PropertyValueFactory<>("numEtu")); // ⚠️ Corrigé : c'est numEtu maintenant
        colSemestre.setCellValueFactory(new PropertyValueFactory<>("semestreActuel"));

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

        initialiserFiltres();
    }

    /**
     * Charge les données du tableau avec la liste complète des étudiants.
     * <p>
     * Cette méthode récupère instantanément les données depuis la mémoire vive
     * via le {@link DataManager}, puis met à jour l'interface graphique.
     * </p>
     */
    private void chargerTableau() {
        List<Etudiant> data = DataManager.getInstance().getListeEtudiants();
        listeEtudiants = FXCollections.observableArrayList(data);
        tableEtudiants.setItems(listeEtudiants);
    }

    /**
     * Initialise les listes déroulantes (ComboBox) servant de filtres pour le tableau des étudiants.
     * <p>
     * Cette méthode effectue la configuration initiale au chargement de la vue :
     * <ul>
     * <li>Elle peuple les listes de Mentions et de Semestres en récupérant les données uniques via le {@link DataManager}.</li>
     * <li>Elle verrouille (grise) la liste des Parcours par défaut, car son contenu dépend du choix préalable d'une mention.</li>
     * <li>Elle ajoute des écouteurs d'événements (listeners) pour que chaque changement dans une liste déclenche
     * dynamiquement la mise à jour des parcours disponibles et le filtrage visuel du tableau.</li>
     * </ul>
     * </p>
     */
    private void initialiserFiltres() {
        DataManager dm = DataManager.getInstance();

        // Remplissage de la Mention
        comboMention.getItems().add("Toutes les mentions");
        comboMention.getItems().addAll(dm.getToutesLesMentions());
        comboMention.getSelectionModel().selectFirst();

        // Configuration Initiale des Parcours (Désactivé par défaut)
        comboParcours.getItems().add("Tous les parcours");
        comboParcours.getSelectionModel().selectFirst();
        comboParcours.setDisable(true);

        // Remplissage des Semestres
        comboSemestre.getItems().add("Tous les semestres");
        comboSemestre.getItems().addAll(dm.getTousLesSemestres());
        comboSemestre.getSelectionModel().selectFirst();

        // Ajout des écouteurs d'événements

        // Quand on change de mention, on met à jour les parcours puis on filtre le tableau
        comboMention.setOnAction(event -> {
            mettreAJourComboParcours();
            appliquerFiltres();
        });

        comboParcours.setOnAction(event -> appliquerFiltres());
        comboSemestre.setOnAction(event -> appliquerFiltres());
    }

    /**
     * Met à jour dynamiquement le contenu et l'état de la liste déroulante des parcours
     * en fonction de la mention sélectionnée par l'utilisateur.
     * <p>
     * Logique de l'interface (UX) :
     * <ul>
     * <li>Si l'option "Toutes les mentions" est sélectionnée (ou si rien n'est sélectionné),
     * la liste des parcours est vidée, bloquée sur "Tous les parcours" et désactivée pour éviter des choix incohérents.</li>
     * <li>Si une mention spécifique est choisie, la liste est déverrouillée et peuplée
     * <b>uniquement</b> avec les parcours appartenant à cette mention (récupérés via le {@link DataManager}).</li>
     * </ul>
     * </p>
     */
    private void mettreAJourComboParcours() {
        String mentionChoisie = comboMention.getValue();

        // On vide la liste des parcours pour la remettre à zéro
        comboParcours.getItems().clear();
        comboParcours.getItems().add("Tous les parcours");

        if (mentionChoisie == null || mentionChoisie.equals("Toutes les mentions")) {
            // Si "Toutes les mentions", on verrouille les parcours
            comboParcours.setDisable(true);
        } else {
            // Si une mention est choisie, on déverrouille et on va chercher ses parcours
            comboParcours.setDisable(false);
            List<String> parcoursDeLaMention = DataManager.getInstance().getParcoursParMention(mentionChoisie);
            comboParcours.getItems().addAll(parcoursDeLaMention);
        }

        // On remet la sélection sur "Tous les parcours" (de cette mention) par défaut
        comboParcours.getSelectionModel().selectFirst();
    }

    /**
     * Filtre et met à jour en temps réel la liste des étudiants affichée dans le tableau.
     * <p>
     * Cette méthode lit les valeurs actuelles des trois listes déroulantes, délègue le tri
     * au {@link DataManager}, puis remplace le contenu du tableau par la liste résultante.
     * </p>
     */
    private void appliquerFiltres() {
        // Le contrôleur demande au cerveau de faire le tri
        List<Etudiant> etudiantsFiltres = DataManager.getInstance().getEtudiantsFiltres(
                comboMention.getValue(),
                comboParcours.getValue(),
                comboSemestre.getValue()
        );

        // Puis il affiche le résultat
        listeEtudiants.setAll(etudiantsFiltres);
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

