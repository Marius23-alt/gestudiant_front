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

public class UeController {
    @FXML private ComboBox<Mention> comboMention;
    @FXML private ComboBox<Parcour> comboParcours;
    @FXML private ComboBox<String> comboSemestre;

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
        lblTitreUe.setText("Page de gestion des UE (En travaux 🚧)");

        // Acceptation des boutons dans le tableau
        configurerColonnesTableau();

        // ----- A SUPPRIMER : Fausse données pour pouvoir voir les boutons dans le tableau --------------
        ObservableList<Ue> uesTest = FXCollections.observableArrayList(
                new Ue("Anglais", "ANGLAIS_1", 3, 4),
                new Ue("Bases de Données", "BDD_SQL", 6, 5)
        );
        tableUe.setItems(uesTest);
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
                            if (mainController != null) {
                                System.out.println("Ouverture de la Saisie de Masse !");
                                mainController.handleSaisieMasse();
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
}