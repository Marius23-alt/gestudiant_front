package fr.miage.toulouse.front.controller;

import fr.miage.toulouse.cours.Etudiant;
import fr.miage.toulouse.cours.Inscription;
import fr.miage.toulouse.cours.Ue;
import fr.miage.toulouse.front.DataManager;
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
import fr.miage.toulouse.database.Request;

import java.util.ArrayList;
import java.util.List;

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
    private List<Inscription> inscriptionsEnAttente = new ArrayList<>();
    private List<Inscription> changementsStatutEnAttente = new ArrayList<>();

    @FXML
    public void initialize() {
        System.out.println("Vue Profil chargée !");
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

    /**
     * Initialise la vue avec les informations de l'étudiant sélectionné.
     * Cette méthode est appelée par le contrôleur principal (MainController)
     * juste après le chargement de la vue.
     *
     * @param etudiant L'objet (Etudiant) contenant les données à afficher (nom, notes, parcours, etc.).
     */
    public void setEtudiant(Etudiant etudiant) {
        this.etudiantCourant = etudiant;

        if (this.mainController != null) {
            this.mainController.setTitrePage("Profil de " + etudiant.getNom() + " " + etudiant.getPrenom());
        }

        System.out.println("Profil chargé pour : " + etudiant.getNom() + " " + etudiant.getPrenom());

        //on vide les données écrites en dur dans le fichier FXML
        viderConteneurs();

        if (textEcts != null) textEcts.setText("ECTS : " + etudiant.getNbEcts() + "/180");
        if (textAnneeSemestre != null) textAnneeSemestre.setText("Semestre Actuel : S" + etudiant.getSemestreActuel());

        if (ectsArc != null) ectsArc.setLength((double) etudiant.getNbEcts() * 2);

        // on parcourt les  inscriptions de l'étudiant pour les classer
        if (etudiant.getInscription() != null) {
            for (Inscription inscr : etudiant.getInscription()) {

                String nomUe = inscr.getUe().getNom();
                String annee = inscr.getAnnee();
                String semestre = "S" + inscr.getUe().getSemestre();

                // On met l'affichage dans la bonne VBox selon le statut de l'inscription
                switch (inscr.getStatut().toLowerCase()) {
                    case "valide":
                        containerUeValidees.getChildren().add(creerLigneUe(nomUe, annee, semestre, "Validée", "#28a745"));
                        break;
                    case "en_cours":
                        containerUeEnCours.getChildren().add(creerLigneUeEnCours(inscr));
                        break;
                    case "echoue":
                        containerUeEchouees.getChildren().add(creerLigneUe(nomUe, annee, semestre, "Échouée", "#dc3545"));
                        break;
                }
            }
        }

        // Si  boîte  vide après  tri => message
        if (containerUeEnCours.getChildren().isEmpty()) {
            containerUeEnCours.getChildren().add(new Label("Aucune UE en cours."));
        }
        if (containerUeValidees.getChildren().isEmpty()) {
            containerUeValidees.getChildren().add(new Label("Aucune UE validée."));
        }
        if (containerUeEchouees.getChildren().isEmpty()) {
            containerUeEchouees.getChildren().add(new Label("Aucun échec."));
        }

        //  on affiche les UEs autorisées pour le semestre courant
        calculerUesAutorisees(etudiant);
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

    /**
     * Détermine et affiche la liste des Unités d'Enseignement (UE) auxquelles
     * l'étudiant peut s'inscrire pour le semestre courant.
     * La méthode filtre les UE selon plusieurs critères métiers :
     * - Appartenance au parcours de l'étudiant
     * - Cohérence avec la parité du semestre global (pair/impair)
     * - Exclusion des UE déjà validées, en cours ou échouées durant l'année
     * - Vérification du respect des prérequis académiques.
     *
     *
     * @param etudiant L'étudiant pour lequel calculer les droits d'inscription.
     */
    private void calculerUesAutorisees(Etudiant etudiant) {
        containerUeAutorises.getChildren().clear();

        boolean isSemestreGlobalImpair = DataManager.getInstance().isSemestreImpair();
        String anneeCourante = DataManager.getInstance().getAnneeUniversitaireCourante();

        List<Ue> toutesLesUes = DataManager.getInstance().getListeUes();

        List<String> codesValides = etudiant.getInscription().stream()
                .filter(inscr -> inscr.getStatut().equals("valide"))
                .map(inscr -> inscr.getUe().getCode())
                .toList();

        List<String> codesEnCours = etudiant.getInscription().stream()
                .filter(inscr -> inscr.getStatut().equals("en_cours"))
                .map(inscr -> inscr.getUe().getCode())
                .toList();

        List<String> codesEchouesCetteAnnee = etudiant.getInscription().stream()
                .filter(inscr -> inscr.getStatut().equals("echoue") && inscr.getAnnee().equals(anneeCourante))
                .map(inscr -> inscr.getUe().getCode())
                .toList();

        List<Ue> uesAutorisees = toutesLesUes.stream()
                .filter(ue -> ue.getParcour().getNom().equals(etudiant.getParcour().getNom()))

                .filter(ue -> (ue.getSemestre() % 2 != 0) == isSemestreGlobalImpair)

                .filter(ue -> !codesValides.contains(ue.getCode())
                        && !codesEnCours.contains(ue.getCode())
                        && !codesEchouesCetteAnnee.contains(ue.getCode()))

                .filter(ue -> {
                    String prerequis = ue.getCodeUePrecedente();

                    if (prerequis == null || prerequis.trim().isEmpty()) {
                        return true;
                    }

                    return codesValides.contains(prerequis);
                })
                .toList();

        if (uesAutorisees.isEmpty()) {
            Label lblVide = new Label("Aucune UE disponible à l'inscription pour ce semestre (Prérequis manquants ou parcours terminé).");
            lblVide.setTextFill(javafx.scene.paint.Color.web("#757575"));
            containerUeAutorises.getChildren().add(lblVide);
        } else {
            for (Ue ue : uesAutorisees) {
                containerUeAutorises.getChildren().add(creerLigneUeAutorisee(ue, anneeCourante, "S" + ue.getSemestre()));
            }
        }
    }

    /**
     * Crée dynamiquement une ligne d'interface pour une UE autorisée, avec son bouton d'inscription.
     */
    private HBox creerLigneUeAutorisee(Ue ue, String annee, String semestre) {
        HBox hbox = new HBox();
        hbox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        Label lblNom = new Label(ue.getNom() + " - " + annee + " - " + semestre);
        lblNom.setTextFill(javafx.scene.paint.Color.web("#575757"));
        lblNom.setFont(javafx.scene.text.Font.font("System", javafx.scene.text.FontWeight.BOLD, 13.0));
        lblNom.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(lblNom, javafx.scene.layout.Priority.ALWAYS);

        Button btnInscrire = new Button("Inscrire");
        btnInscrire.setStyle("-fx-background-color: #ffc107; -fx-background-radius: 8; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");

        btnInscrire.setOnAction(event -> {
            System.out.println("Ajout au brouillon pour : " + ue.getNom());

            Inscription nouvelleInscription = new Inscription(etudiantCourant, ue, annee, "en_cours");

            etudiantCourant.ajouterInscription(nouvelleInscription);
            ue.ajouterInscription(nouvelleInscription);

            inscriptionsEnAttente.add(nouvelleInscription);

            setEtudiant(etudiantCourant);
        });

        hbox.getChildren().addAll(lblNom, btnInscrire);
        return hbox;
    }

    /**
     * Crée une ligne pour une UE en cours avec les boutons Valider (Vert) et Échouer (Rouge).
     */
    private HBox creerLigneUeEnCours(Inscription inscr) {
        HBox hbox = new HBox();
        hbox.setAlignment(Pos.CENTER_LEFT);
        hbox.setSpacing(10);

        String texteLigne = inscr.getUe().getNom() + " - " + inscr.getAnnee() + " - S" + inscr.getUe().getSemestre();
        Label labelUe = new Label(texteLigne);
        labelUe.setTextFill(javafx.scene.paint.Color.web("#575757"));
        labelUe.setFont(Font.font("System", FontWeight.BOLD, 13));
        labelUe.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(labelUe, Priority.ALWAYS);

        Button btnValider = new Button("Valider");
        btnValider.setStyle("-fx-background-color: #28a745; -fx-background-radius: 8; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");
        btnValider.setOnAction(e -> {
            inscr.setStatut("valide");
            if (!changementsStatutEnAttente.contains(inscr)) {
                changementsStatutEnAttente.add(inscr);
            }
            setEtudiant(etudiantCourant);
        });


        Button btnEchouer = new Button("Échouer");
        btnEchouer.setStyle("-fx-background-color: #dc3545; -fx-background-radius: 8; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");
        btnEchouer.setOnAction(e -> {
            inscr.setStatut("echoue");
            if (!changementsStatutEnAttente.contains(inscr)) {
                changementsStatutEnAttente.add(inscr);
            }
            setEtudiant(etudiantCourant);
        });

        hbox.getChildren().addAll(labelUe, btnValider, btnEchouer);
        return hbox;
    }


    /**
     * Valide le brouillon (Nouvelles inscriptions + Changements de notes) et envoie tout à la BDD.
     */
    @FXML
    public void handleEnregistrer() {
        if (inscriptionsEnAttente.isEmpty() && changementsStatutEnAttente.isEmpty()) {
            System.out.println("Aucune modification à enregistrer.");
            return;
        }

        fr.miage.toulouse.database.Request req = new fr.miage.toulouse.database.Request();
        int compteur = 0;

        for (Inscription inscr : inscriptionsEnAttente) {
            boolean succes = req.ajouterInscitption(inscr.getEtudiant().getNumEtu(), inscr.getUe().getCode(), inscr.getAnnee());
            if (succes) compteur++;
        }

        for (Inscription inscrModifiee : changementsStatutEnAttente) {
            boolean succes = req.modifierStatutInscription(
                    inscrModifiee.getEtudiant().getNumEtu(),
                    inscrModifiee.getUe().getCode(),
                    inscrModifiee.getAnnee(),
                    inscrModifiee.getStatut()
            );
            if (succes) compteur++;
        }

        System.out.println("Succès : " + compteur + " modifications sauvegardées dans la BDD !");

        inscriptionsEnAttente.clear();
        changementsStatutEnAttente.clear();
    }

    /**
     * Annule la dernière action effectuée.
     */
    @FXML
    public void handleAnnulerDernier() {
        if (inscriptionsEnAttente.isEmpty() && changementsStatutEnAttente.isEmpty()) return;

        // Si nouvelles inscriptions dans le panier, on annule la dernière
        if (!inscriptionsEnAttente.isEmpty()) {
            int index = inscriptionsEnAttente.size() - 1;
            Inscription derniereAction = inscriptionsEnAttente.get(index);

            etudiantCourant.getInscription().remove(derniereAction);
            derniereAction.getUe().getInscription().remove(derniereAction);
            inscriptionsEnAttente.remove(index);

            System.out.println("Dernière inscription annulée.");
        }
        // Sinon, si changements de statut, on annule le dernier changement
        else if (!changementsStatutEnAttente.isEmpty()) {
            int index = changementsStatutEnAttente.size() - 1;
            Inscription derniereAction = changementsStatutEnAttente.get(index);

            derniereAction.setStatut("en_cours");
            changementsStatutEnAttente.remove(index);

            System.out.println("↩️ Dernier changement de statut annulé.");
        }

        // On rafraîchit l'écran
        setEtudiant(etudiantCourant);
    }

    /**
     * Annule TOUTES les modifications en attente (Inscriptions ET Statuts).
     */
    @FXML
    public void handleAnnulerTout() {
        if (inscriptionsEnAttente.isEmpty() && changementsStatutEnAttente.isEmpty()) return;

        for (Inscription inscr : inscriptionsEnAttente) {
            etudiantCourant.getInscription().remove(inscr);
            inscr.getUe().getInscription().remove(inscr);
        }
        inscriptionsEnAttente.clear();

        for (Inscription inscrModifiee : changementsStatutEnAttente) {
            inscrModifiee.setStatut("en_cours"); // On remet l'état de départ
        }
        changementsStatutEnAttente.clear();

        setEtudiant(etudiantCourant);
        System.out.println("Toutes les modifications ont été annulées.");
    }

    /**
     * Gère le clic sur le bouton Modifier le profil en ouvrant une Pop-up
     */
    @FXML
    public void handleModifierProfil() {
        try {
            // On charge le fichier visuel de la pop-up
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/fxml/modifierProfil.fxml"));
            javafx.scene.Parent root = loader.load();

            // On envoie l'étudiant actuel au contrôleur de la pop-up
            fr.miage.toulouse.front.controller.ModifierProfilController controller = loader.getController();
            controller.initData(etudiantCourant, this);

            // On crée la fenêtre et on l'affiche
            javafx.stage.Stage stage = new javafx.stage.Stage();
            stage.setTitle("Modifier Profil");
            stage.setScene(new javafx.scene.Scene(root));
            stage.initModality(javafx.stage.Modality.APPLICATION_MODAL); // Rend la fenêtre principale floue/inaccessible tant que la pop-up est ouverte
            stage.showAndWait();

        } catch (java.io.IOException e) {
            System.err.println("Erreur lors de l'ouverture de la fenêtre : " + e.getMessage());
            e.printStackTrace();
        }
    }

    // --- Setters ---

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

}