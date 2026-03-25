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

import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import java.util.Map;
import java.util.stream.Collectors;



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

    @FXML private Label lblStatTitle;
    @FXML private PieChart pieChartRepartition;
    @FXML private BarChart<String, Number> barChartEvolution;

    private String texteRecherche = "";

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
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colPrenom.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        colNumEtudiant.setCellValueFactory(new PropertyValueFactory<>("numEtu"));
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
        appliquerFiltres();
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
     * Définit le texte de recherche global et déclenche instantanément la mise à jour de la vue.
     * <p>
     * Cette méthode est sollicitée par le contrôleur principal ({@link MainController})
     * à chaque nouvelle frappe de l'utilisateur dans la barre de recherche de l'application.
     * Le texte reçu est converti en minuscules pour garantir un filtrage insensible à la casse
     * (case-insensitive), puis la méthode {@link #appliquerFiltres()} est invoquée pour
     * rafraîchir en direct le tableau et les statistiques.
     * </p>
     *
     * @param texte La chaîne de caractères saisie dans la barre de recherche globale.
     */
    public void setTexteRecherche(String texte) {
        this.texteRecherche = texte.toLowerCase();
        appliquerFiltres();
    }

    /**
     * Filtre et met à jour en temps réel la liste des étudiants affichée dans le tableau
     * ainsi que les statistiques du Dashboard.
     * <p>
     * Le processus de filtrage s'effectue en deux étapes (en entonnoir) :
     * <ol>
     * <li><b>Filtres globaux :</b> Lit les valeurs actuelles des trois listes déroulantes
     * (Mention, Parcours, Semestre) et délègue le premier tri au {@link DataManager}.</li>
     * <li><b>Recherche textuelle :</b> Si un texte a été saisi dans la barre de recherche globale,
     * affine la liste en conservant uniquement les étudiants dont le nom, le prénom
     * ou le numéro d'étudiant contient la chaîne saisie (insensible à la casse).</li>
     * </ol>
     * </p>
     * <p>
     * Une fois la liste finale calculée, cette méthode remplace le contenu du {@code TableView}
     * et déclenche la mise à jour des graphiques via {@link #mettreAJourStatistiques(List)}.
     * </p>
     */
    private void appliquerFiltres() {
        List<Etudiant> etudiantsFiltres = DataManager.getInstance().getEtudiantsFiltres(
                comboMention.getValue(),
                comboParcours.getValue(),
                comboSemestre.getValue()
        );

        if (texteRecherche != null && !texteRecherche.isEmpty()) {
            etudiantsFiltres = etudiantsFiltres.stream()
                    .filter(e -> e.getNom().toLowerCase().contains(texteRecherche)
                            || e.getPrenom().toLowerCase().contains(texteRecherche)
                            || String.valueOf(e.getNumEtu()).contains(texteRecherche))
                    .toList();
        }
        listeEtudiants.setAll(etudiantsFiltres);
        mettreAJourStatistiques(etudiantsFiltres);
    }

    /**
     * Met à jour dynamiquement les composants visuels de statistiques (titre et graphiques) du Dashboard.
     * <p>
     * Cette méthode recalcule les données statistiques en se basant sur la liste d'étudiants fournie
     * (qui correspond à la liste actuellement filtrée et affichée dans le tableau).
     * Elle effectue les opérations suivantes :
     * <ul>
     * <li><b>Titre :</b> Adapte le texte explicatif en fonction de la mention actuellement sélectionnée.</li>
     * <li><b>Graphique Circulaire (PieChart) :</b> Calcule et affiche la répartition des étudiants par parcours.</li>
     * <li><b>Graphique en Barres (BarChart) :</b> Calcule et affiche le nombre d'étudiants inscrits pour chaque semestre (trié par ordre croissant).</li>
     * </ul>
     * </p>
     *
     * @param listeFiltree La liste des objets {@link Etudiant} à analyser pour générer les statistiques.
     */
    private void mettreAJourStatistiques(List<Etudiant> listeFiltree) {

        // Mise à jour du Titre
        String mention = comboMention.getValue();
        if (mention == null || mention.equals("Toutes les mentions")) {
            lblStatTitle.setText("Statistiques globales de l'université");
        } else {
            lblStatTitle.setText("Statistiques : " + mention);
        }

        // Mise à jour du PieChart (Ex: Répartition par Parcours)
        pieChartRepartition.getData().clear();

        // On regroupe les étudiants par nom de parcours et on les compte
        Map<String, Long> repartitionParcours = listeFiltree.stream()
                .collect(Collectors.groupingBy(e -> e.getParcour().getNom(), Collectors.counting()));

        // On crée une part de camembert pour chaque parcours trouvé
        for (Map.Entry<String, Long> entry : repartitionParcours.entrySet()) {
            pieChartRepartition.getData().add(new PieChart.Data(entry.getKey() + " (" + entry.getValue() + ")", entry.getValue()));
        }

        // Mise à jour du BarChart (Ex: Répartition par Semestre)
        barChartEvolution.getData().clear();

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Nombre d'étudiants");

        // On regroupe par semestre et on compte
        Map<Integer, Long> repartitionSemestre = listeFiltree.stream()
                .collect(Collectors.groupingBy(Etudiant::getSemestreActuel, Collectors.counting()));

        // On trie les semestres dans l'ordre (1, 2, 3...) pour que le graphique soit logique
        repartitionSemestre.keySet().stream().sorted().forEach(semestre -> {
            series.getData().add(new XYChart.Data<>("S" + semestre, repartitionSemestre.get(semestre)));
        });

        barChartEvolution.getData().add(series);
    }
}

