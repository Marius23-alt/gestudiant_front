package fr.miage.toulouse.front.controller;

import fr.miage.toulouse.cours.Etudiant;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Arc;
import javafx.scene.text.Text;

public class ProfilEtudiantController {

    @FXML private Text textEcts;
    @FXML private Text textAnneeSemestre; // Ligne 30 de ton FXML (ex: "2025-2026 | Semestre Pair")
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
     * Initialise le profil pour un nouvel étudiant fraîchement créé.
     * Cette méthode sera codée dans le cadre du Ticket 4.
     */
    public void initialiserNouveauProfil(Etudiant etudiant, boolean estImmediat, String semestreChoisi) {
        // Le code du Ticket 4 viendra ici !
        System.out.println("Préparation du profil pour le nouvel étudiant : " + etudiant.getNom());
    }

    @FXML
    private void handleModifierProfil() {
        System.out.println("Clic sur modifier");
    }
}