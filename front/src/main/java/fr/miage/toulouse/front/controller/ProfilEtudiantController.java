package fr.miage.toulouse.front.controller;

import fr.miage.toulouse.cours.Etudiant;
import javafx.fxml.FXML;
import javafx.scene.text.Text;

public class ProfilEtudiantController {

    private Etudiant etudiantCourant;

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

    @FXML
    private void handleModifierProfil() {
        System.out.println("Clic sur modifier");
    }
}