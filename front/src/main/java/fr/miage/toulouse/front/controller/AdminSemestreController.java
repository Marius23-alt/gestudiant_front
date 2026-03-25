package fr.miage.toulouse.front.controller;

import fr.miage.toulouse.cours.Etudiant;
import fr.miage.toulouse.cours.Inscription;
import fr.miage.toulouse.front.DataManager;
import fr.miage.toulouse.database.Request;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import java.util.List;
import java.util.Optional;

public class AdminSemestreController {

    // --- ZONE 1 : SIMULATION TEMPORELLE ---
    @FXML private ComboBox<String> comboAnneeSimu;
    @FXML private ComboBox<String> comboSaisonSimu;
    @FXML private Label lblHorlogeActuelle;

    // --- ZONE 2 : FILTRES & TABLEAU ---
    @FXML private ComboBox<String> comboMention;
    @FXML private ComboBox<String> comboParcours;
    @FXML private ComboBox<String> comboSemestre;
    @FXML private TextField searchField;

    @FXML private TableView<Etudiant> studentTable;
    @FXML private TableColumn<Etudiant, String> colNom;
    @FXML private TableColumn<Etudiant, String> colPrenom;
    @FXML private TableColumn<Etudiant, Integer> colNumEtudiant;
    @FXML private TableColumn<Etudiant, String> colMention;
    @FXML private TableColumn<Etudiant, String> colParcours;
    @FXML private TableColumn<Etudiant, Void> colAction;

    private MainController mainController;
    private ObservableList<Etudiant> masterData = FXCollections.observableArrayList();

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    @FXML
    public void initialize() {
        comboAnneeSimu.getItems().addAll("2024-2025", "2025-2026", "2026-2027", "2027-2028");
        comboSaisonSimu.getItems().addAll("Semestre Impair (Automne)", "Semestre Pair (Printemps)");
        mettreAJourLabelHorloge();

        comboMention.getItems().add("Toutes");
        comboMention.getItems().addAll(DataManager.getInstance().getToutesLesMentions());
        comboMention.getSelectionModel().selectFirst();

        comboParcours.getItems().add("Tous");
        comboParcours.getItems().addAll(DataManager.getInstance().getTousLesParcours());
        comboParcours.getSelectionModel().selectFirst();

        comboSemestre.getItems().addAll("Tous", "1", "2", "3", "4", "5", "6");
        comboSemestre.getSelectionModel().selectFirst();

        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colPrenom.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        colNumEtudiant.setCellValueFactory(new PropertyValueFactory<>("numEtu"));

        colMention.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getParcour().getMention().getNom()));
        colParcours.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getParcour().getNom()));

        ajouterBoutonProfilDansTableau();

        comboMention.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                comboParcours.getItems().clear();
                comboParcours.getItems().add("Tous");

                if (newVal.equals("Toutes")) {
                    comboParcours.getItems().addAll(DataManager.getInstance().getTousLesParcours());
                } else {
                    comboParcours.getItems().addAll(DataManager.getInstance().getParcoursParMention(newVal));
                }

                comboParcours.getSelectionModel().selectFirst();

                chargerDonneesTableau();
            }
        });

        comboParcours.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) chargerDonneesTableau();
        });
        comboSemestre.valueProperty().addListener((obs, oldVal, newVal) -> chargerDonneesTableau());
        searchField.textProperty().addListener((obs, oldVal, newVal) -> chargerDonneesTableau());
        chargerDonneesTableau();
    }

    /**
     * Charge les étudiants dans le tableau
     */
    private void chargerDonneesTableau() {
        String mention = (comboMention.getValue() == null || comboMention.getValue().equals("Toutes")) ? "Toutes les mentions" : comboMention.getValue();
        String parcours = (comboParcours.getValue() == null || comboParcours.getValue().equals("Tous")) ? "Tous les parcours" : comboParcours.getValue();
        String semestre = (comboSemestre.getValue() == null || comboSemestre.getValue().equals("Tous")) ? "Tous les semestres" : comboSemestre.getValue();

        String recherche = searchField.getText() != null ? searchField.getText().toLowerCase() : "";

        List<Etudiant> etudiantsFiltres = DataManager.getInstance().getEtudiantEnCours(mention, parcours, semestre);

        List<Etudiant> resultatFinal = etudiantsFiltres.stream()
                .filter(e -> e.getNom().toLowerCase().contains(recherche) ||
                        e.getPrenom().toLowerCase().contains(recherche) ||
                        String.valueOf(e.getNumEtu()).contains(recherche))
                .toList();

        masterData.setAll(resultatFinal);
        studentTable.setItems(masterData);
    }

    /**
     * Ajoute un bouton "Dossier" sur chaque ligne du tableau
     */
    private void ajouterBoutonProfilDansTableau() {
        Callback<TableColumn<Etudiant, Void>, TableCell<Etudiant, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<Etudiant, Void> call(final TableColumn<Etudiant, Void> param) {
                return new TableCell<>() {
                    private final Button btn = new Button("Voir Profil");
                    {
                        btn.setStyle("-fx-background-color: #007bff; -fx-text-fill: white; -fx-background-radius: 5; -fx-cursor: hand;");
                        btn.setOnAction(event -> {
                            Etudiant etudiant = getTableView().getItems().get(getIndex());
                            if (mainController != null) {
                                mainController.handleProfilEtudiant(etudiant); // Ouvre le profil !
                            }
                        });
                    }
                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            HBox box = new HBox(btn);
                            box.setAlignment(javafx.geometry.Pos.CENTER);
                            setGraphic(box);
                        }
                    }
                };
            }
        };
        colAction.setCellFactory(cellFactory);
    }

    /**
     * Change l'horloge sans toucher à la Base de Données
     */
    @FXML
    public void handleSimulerTemps() {
        String anneeChoisie = comboAnneeSimu.getValue();
        String saisonChoisie = comboSaisonSimu.getValue();

        if (anneeChoisie == null || saisonChoisie == null) {
            Alert al = new Alert(Alert.AlertType.WARNING, "Veuillez choisir une année et une saison à simuler.");
            al.show();
            return;
        }

        boolean isImpair = saisonChoisie.contains("Impair");

        DataManager.getInstance().setAnneeUniversitaireCourante(anneeChoisie);
        DataManager.getInstance().setSemestreImpair(isImpair);

        mettreAJourLabelHorloge();

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Simulation Temporelle");
        alert.setHeaderText("Voyage dans le temps réussi !");
        alert.setContentText("L'application est maintenant en " + anneeChoisie + " (" + (isImpair ? "Impair" : "Pair") + ").\nAllez voir le profil d'un étudiant pour observer les nouvelles UE autorisées !");
        alert.showAndWait();
    }

    private void mettreAJourLabelHorloge() {
        String annee = DataManager.getInstance().getAnneeUniversitaireCourante();
        boolean isImpair = DataManager.getInstance().isSemestreImpair();
        lblHorlogeActuelle.setText("Horloge actuelle de l'Université : " + annee + " (" + (isImpair ? "Impair" : "Pair") + ")");
    }

    /**
     * Passe toutes les UEs "en_cours" en "echoue" pour de vrai
     */
    @FXML
    public void handleCloturerSemestre() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Clôture du semestre");
        alert.setHeaderText("Vous êtes sur le point de clôturer le semestre.");
        alert.setContentText("Tous les étudiants ayant encore des matières 'en cours' dans ce tableau se verront attribuer un ÉCHEC pour ces matières. Voulez-vous continuer ?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {

            Request req = new Request();
            int compteurModifs = 0;

            for (Etudiant e : masterData) {
                for (Inscription inscr : e.getInscription()) {
                    if (inscr.getStatut().equals("en_cours")) {

                        // Modif bd
                        boolean succes = req.modifierStatutInscription(e.getNumEtu(), inscr.getUe().getCode(), inscr.getAnnee(), "echoue");

                        // modif en mémoire Java
                        if (succes) {
                            inscr.setStatut("echoue");
                            compteurModifs++;
                        }
                    }
                }
            }

            chargerDonneesTableau();

            Alert info = new Alert(Alert.AlertType.INFORMATION, "Clôture terminée. " + compteurModifs + " matières ont été passées en 'Échouée'.");
            info.show();
        }
    }
}