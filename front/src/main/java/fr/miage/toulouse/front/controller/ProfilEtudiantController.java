package fr.miage.toulouse.front.controller;

import fr.miage.toulouse.cours.Etudiant;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Arc;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class ProfilEtudiantController {

    @FXML private Text textEcts;
    @FXML private Text textAnneeSemestre;
    @FXML private Arc ectsArc;

    @FXML private VBox containerUeEnCours;
    @FXML private VBox containerUeEchouees;
    @FXML private VBox containerUeAutorises;
    @FXML private VBox containerUeValidees;

    private Etudiant etudiantCourant;
    private MainController mainController;

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    /**
     * Nettoie les données "en dur" du FXML pour avoir des boîtes vides avant de les remplir dynamiquement.
     */
    private void viderConteneurs() {
        containerUeEnCours.getChildren().clear();
        containerUeEchouees.getChildren().clear();
        containerUeAutorises.getChildren().clear();
        containerUeValidees.getChildren().clear();
    }

    /**
     * Initialise dynamiquement l'interface du profil pour un étudiant nouvellement créé.
     * <p>
     * Cette méthode est invoquée par le {@code MainController} lors de la redirection automatique
     * qui suit la création d'un étudiant (depuis le formulaire d'ajout). Son rôle est de préparer
     * un "contrat pédagogique" vierge, prêt à être rempli par le professeur :
     * <ul>
     * <li><b>Nettoyage :</b> Vide les conteneurs visuels et réinitialise les compteurs (0 ECTS).</li>
     * <li><b>Historique vide :</b> Affiche des messages indicatifs ("Aucune UE validée", etc.) puisque
     * l'étudiant n'a pas encore de passé universitaire.</li>
     * <li><b>Préparation :</b> Configure l'en-tête avec le semestre d'entrée prévu et pré-remplit
     * la liste des UE autorisées (actuellement via des données de test) pour faciliter l'inscription.</li>
     * </ul>
     * </p>
     *
     * @param etudiant       L'objet {@link Etudiant} fraîchement inséré en base de données et en mémoire.
     * @param semestreChoisi Le numéro du semestre d'entrée sélectionné dans le formulaire (ex: "3").
     */
    public void initialiserNouveauProfil(Etudiant etudiant, String semestreChoisi) {
        this.etudiantCourant = etudiant;

        viderConteneurs();

        if (textEcts != null) textEcts.setText("ECTS : 0/180");
        if (textAnneeSemestre != null) textAnneeSemestre.setText("Entrée prévue : Semestre " + semestreChoisi);
        if (ectsArc != null) ectsArc.setLength(0);

        containerUeEnCours.getChildren().add(new Label("Aucune inscription en cours."));
        containerUeEchouees.getChildren().add(new Label("Aucun échec."));
        containerUeValidees.getChildren().add(new Label("Aucune UE validée."));

        HBox fausseLigne = creerLigneUe("UE Test (Générée en Java)", "2024", "S" + semestreChoisi, "Inscrire", "#ffc107");
        containerUeAutorises.getChildren().add(fausseLigne);
    }

    @FXML
    public void initialize() {
        System.out.println("Vue Profil chargée !");
    }

    /**
     * Initialise la vue avec les informations de l'étudiant sélectionné.
     * Cette méthode est appelée par le contrôleur principal (MainController)
     * juste après le chargement de la vue. Elle permet de :
     * - Récupérer l'objet (Etudiant) transféré depuis le tableau de bord
     * - Stocker cet étudiant pour des opérations futures (ex: modification)
     * - Déclencher la mise à jour de l'affichage (remplissage des textes, jauge ECTS, listes d'UE)
     *
     * @param etudiant L'objet (Etudiant) contenant les données à afficher (nom, notes, parcours, etc.).
     */
    public void setEtudiant(Etudiant etudiant) {
        this.etudiantCourant = etudiant;

        System.out.println("Profil chargé pour : " + etudiant.getNom() + " " + etudiant.getPrenom());

        // ICI : Plus tard, tu mettras à jour tes Labels :
        // labelNom.setText(etudiant.getNom());
        // ectsArc.setLength(...);
    }

    /**
     * "Usine" qui fabrique dynamiquement une ligne d'UE (HBox)
     * Elle reproduit exactement le design de ton FXML, mais codé en Java !
     */
    private HBox creerLigneUe(String nomUe, String annee, String semestre, String texteBouton, String couleurHexBouton) {
        HBox hbox = new HBox();
        hbox.setAlignment(Pos.CENTER_LEFT);

        Label labelUe = new Label(nomUe + " - " + annee + " - " + semestre);
        labelUe.setTextFill(javafx.scene.paint.Color.web("#575757"));
        labelUe.setFont(Font.font("System", FontWeight.BOLD, 13));
        labelUe.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(labelUe, Priority.ALWAYS);

        Button btnStatut = new Button(texteBouton);
        btnStatut.setStyle("-fx-background-color: " + couleurHexBouton + "; -fx-background-radius: 8;");
        btnStatut.setTextFill(javafx.scene.paint.Color.WHITE);
        btnStatut.setFont(Font.font("System", FontWeight.BOLD, 12));

        hbox.getChildren().addAll(labelUe, btnStatut);
        return hbox;
    }

    // A FAIRE
    @FXML
    private void handleModifierProfil() {
        System.out.println("Clic sur modifier");
    }
}