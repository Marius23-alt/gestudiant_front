package fr.miage.toulouse.front.controller;

import fr.miage.toulouse.cours.Mention;
import fr.miage.toulouse.cours.Parcour;
import fr.miage.toulouse.cours.Ue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;
import java.util.List;
import fr.miage.toulouse.front.DataManager;

public class UeController {
    @FXML private ComboBox<String> comboMention;
    @FXML private ComboBox<String> comboParcours;
    @FXML private ComboBox<String> comboSemestre;

    // La liste qui mettra à jour le tableau en temps réel
    private ObservableList<Ue> listeUesAffichables = FXCollections.observableArrayList();

    @FXML private Label lblTitreUe;

    @FXML private TableView<Ue> tableUe;
    @FXML private TableColumn<Ue, String> colMatiere;
    @FXML private TableColumn<Ue, Void> colInscription;
    @FXML private TableColumn<Ue, Void> colValide;

    // --- NAVIGATION ---

    private MainController mainController;

    /**
     * Injecte l'instance du contrôleur principal (MainController).
     * <p>
     * Cette méthode est essentielle pour mettre en place l'injection de dépendance.
     * Elle permet à ce contrôleur de déléguer les actions de navigation (comme
     * l'ouverture de la page de saisie de masse) au chef d'orchestre de l'interface.
     * </p>
     *
     * @param mainController L'instance active du contrôleur principal gérant la fenêtre.
     */
    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    // --- INITIALISATION ---

    /**
     * Méthode appelée automatiquement au chargement de la page.
     */
    @FXML
    public void initialize() {
        lblTitreUe.setText("Page de gestion des UE");

        // Configuration des colonnes (noms + boutons d'action)
        configurerColonnesTableau();

        // On prépare les listes déroulantes
        initialiserFiltres();

        // On applique les filtres une première fois pour remplir le tableau
        appliquerFiltres();
    }


    /**
     * Configure le comportement et l'affichage des colonnes du tableau des UE.
     * <p>
     * Cette méthode lie la colonne des matières à l'attribut "nom" des objets {@link Ue}.
     * Elle fait également appel à la méthode utilitaire {@link #ajouterBoutonsAction}
     * pour générer dynamiquement les boutons interactifs ("Inscription" et "Validé UE")
     * dans leurs colonnes respectives.
     * </p>
     */
    private void configurerColonnesTableau() {
        colMatiere.setCellValueFactory(new PropertyValueFactory<>("nom"));
        ajouterBoutonsAction(colInscription, "Inscription", "#FFC107");
        ajouterBoutonsAction(colValide, "Validé UE", "#007BFF");
    }

    /**
     * Génère et insère dynamiquement des boutons cliquables dans une colonne spécifique du tableau.
     * <p>
     * Utilise une {@link Callback} (CellFactory) pour remplacer l'affichage textuel
     * par défaut des cellules par un objet {@link Button} stylisé. Lors du clic sur
     * un de ces boutons, la méthode sollicite le {@link MainController} pour
     * déclencher la navigation vers l'écran de saisie de masse.
     * </p>
     *
     * @param colonne      La colonne du tableau dans laquelle insérer les boutons.
     * @param texteBouton  Le texte à afficher sur le bouton (ex: "Inscription").
     * @param couleurHex   Le code couleur hexadécimal pour le fond du bouton (ex: "#FFC107").
     */
    private void ajouterBoutonsAction(TableColumn<Ue, Void> colonne, String texteBouton, String couleurHex) {

        Callback<TableColumn<Ue, Void>, TableCell<Ue, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<Ue, Void> call(final TableColumn<Ue, Void> param) {
                return new TableCell<>() {

                    private final Button btn = new Button(texteBouton);
                    {
                        btn.setStyle("-fx-background-color: " + couleurHex + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");
                        btn.setMaxWidth(Double.MAX_VALUE);

                        btn.setOnAction(event -> {
                            Ue ue = getTableView().getItems().get(getIndex());
                            if (mainController != null) {
                                if (texteBouton.equals("Inscription")) {
                                    mainController.handleSaisieMasseInscription(ue);
                                } else if (texteBouton.equals("Validé UE")) {
                                    mainController.handleSaisieMasseValide(ue);
                                }
                            } else {
                                System.out.println("Erreur : Le MainController n'a pas été injecté !");
                            }
                        });
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(btn);
                        }
                    }
                };
            }
        };
        colonne.setCellFactory(cellFactory);
    }

    /**
     * Initialise les valeurs par défaut des ComboBox et leurs écouteurs d'événements.
     */
    private void initialiserFiltres() {
        DataManager dm = DataManager.getInstance();

        // 1. Remplissage de la Mention
        comboMention.getItems().clear();
        comboMention.getItems().add("Toutes les mentions");
        comboMention.getItems().addAll(dm.getToutesLesMentions());
        comboMention.getSelectionModel().selectFirst();

        // 2. Configuration Initiale des Parcours (Désactivé par défaut)
        comboParcours.getItems().clear();
        comboParcours.getItems().add("Tous les parcours");
        comboParcours.getSelectionModel().selectFirst();
        comboParcours.setDisable(true);

        // 3. Remplissage des Semestres (Pour les UEs, ça va de 1 à 6)
        comboSemestre.getItems().clear();
        comboSemestre.getItems().add("Tous les semestres");
        comboSemestre.getItems().addAll("1", "2", "3", "4", "5", "6");
        comboSemestre.getSelectionModel().selectFirst();

        // 4. Ajout des actions lors d'un clic
        comboMention.setOnAction(event -> {
            mettreAJourComboParcours();
            appliquerFiltres();
        });

        comboParcours.setOnAction(event -> appliquerFiltres());
        comboSemestre.setOnAction(event -> appliquerFiltres());
    }

    /**
     * Déverrouille et remplit les parcours en fonction de la mention choisie.
     */
    private void mettreAJourComboParcours() {
        String mentionChoisie = comboMention.getValue();

        comboParcours.getItems().clear();
        comboParcours.getItems().add("Tous les parcours");

        if (mentionChoisie == null || mentionChoisie.equals("Toutes les mentions")) {
            comboParcours.setDisable(true);
        } else {
            comboParcours.setDisable(false);
            comboParcours.getItems().addAll(DataManager.getInstance().getParcoursParMention(mentionChoisie));
        }

        comboParcours.getSelectionModel().selectFirst();
    }

    /**
     * Récupère les bons filtres, interroge le DataManager, et met à jour le tableau.
     */
    private void appliquerFiltres() {
        String mention = comboMention.getValue();
        String parcours = comboParcours.getValue();
        String semestre = comboSemestre.getValue();

        // On appelle la méthode que tu avais déjà préparée dans DataManager !
        List<Ue> uesFiltrees = DataManager.getInstance().getUeFiltres(mention, parcours, semestre);

        // On met à jour l'affichage
        listeUesAffichables.setAll(uesFiltrees);
        tableUe.setItems(listeUesAffichables);
    }
}