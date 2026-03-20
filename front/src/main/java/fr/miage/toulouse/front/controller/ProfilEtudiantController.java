package fr.miage.toulouse.front.controller;

import fr.miage.toulouse.cours.Etudiant;
import fr.miage.toulouse.cours.Inscription;
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
     * juste après le chargement de la vue.
     *
     * @param etudiant L'objet (Etudiant) contenant les données à afficher (nom, notes, parcours, etc.).
     */
    public void setEtudiant(Etudiant etudiant) {
        this.etudiantCourant = etudiant;

        System.out.println("Profil chargé pour : " + etudiant.getNom() + " " + etudiant.getPrenom());

        // 1. On vide les fausses données écrites en dur dans le fichier FXML
        viderConteneurs();

        // 2. On met à jour les informations globales (ECTS et Semestre)
        if (textEcts != null) textEcts.setText("ECTS : " + etudiant.getNbEcts() + "/180");
        if (textAnneeSemestre != null) textAnneeSemestre.setText("Semestre Actuel : S" + etudiant.getSemestreActuel());

        if (ectsArc != null) ectsArc.setLength((double) etudiant.getNbEcts() * 2);

        // 3. On parcourt les vraies inscriptions de l'étudiant pour les classer
        if (etudiant.getInscription() != null) {
            for (Inscription inscr : etudiant.getInscription()) {

                String nomUe = inscr.getUe().getNom();
                String annee = inscr.getAnnee();
                String semestre = "S" + inscr.getSemestre();

                // On dispatche l'affichage dans la bonne VBox selon le statut de l'inscription
                switch (inscr.getStatut().toLowerCase()) {
                    case "valide":
                        containerUeValidees.getChildren().add(creerLigneUe(nomUe, annee, semestre, "Validée", "#28a745"));
                        break;
                    case "en_cours":
                        containerUeEnCours.getChildren().add(creerLigneUe(nomUe, annee, semestre, "En cours", "#007bff"));
                        break;
                    case "echoue":
                        containerUeEchouees.getChildren().add(creerLigneUe(nomUe, annee, semestre, "Échouée", "#dc3545"));
                        break;
                }
            }
        }

        // 4. Si une boîte est vide après le tri, on ajoute un petit message indicatif
        if (containerUeEnCours.getChildren().isEmpty()) {
            containerUeEnCours.getChildren().add(new Label("Aucune UE en cours."));
        }
        if (containerUeValidees.getChildren().isEmpty()) {
            containerUeValidees.getChildren().add(new Label("Aucune UE validée."));
        }
        if (containerUeEchouees.getChildren().isEmpty()) {
            containerUeEchouees.getChildren().add(new Label("Aucun échec."));
        }

        // Pour les UEs autorisées, on met un texte temporaire avant d'attaquer la logique complexe
        containerUeAutorises.getChildren().add(new Label("Calcul des UE autorisées en cours de développement..."));
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