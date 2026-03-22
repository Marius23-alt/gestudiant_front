package fr.miage.toulouse.front.controller;

import fr.miage.toulouse.cours.Etudiant;
import fr.miage.toulouse.database.Request;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class ModifierProfilController {

    @FXML private TextField champNom;
    @FXML private TextField champPrenom;

    private Etudiant etudiant;
    private ProfilEtudiantController parentController;

    /**
     * Reçoit les données de la page principale pour pré-remplir les champs.
     */
    public void initData(Etudiant etudiant, ProfilEtudiantController parentController) {
        this.etudiant = etudiant;
        this.parentController = parentController;

        champNom.setText(etudiant.getNom());
        champPrenom.setText(etudiant.getPrenom());
    }

    /**
     * Ce qu'il se passe quand on clique sur "Valider"
     */
    @FXML
    public void handleValider() {
        String nouveauNom = champNom.getText();
        String nouveauPrenom = champPrenom.getText();

        // On récupère l'ID du parcours actuel (remplace "getId_parcours()" par le bon nom de méthode si besoin)
        int idParcours = 1; // On met 1 par défaut le temps de faire le test, on ajoutera le menu déroulant après !

        // 1. Mise à jour dans la base de données
        Request req = new Request();
        boolean succes = req.updateEtudiant(etudiant.getNumEtu(), nouveauNom, nouveauPrenom, idParcours);

        if (succes) {
            // 2. Mise à jour de la mémoire Java
            etudiant.setNom(nouveauNom);
            etudiant.setPrenom(nouveauPrenom);

            // 3. On demande à la page en arrière-plan de se rafraîchir
            parentController.setEtudiant(etudiant);

            // 4. On ferme la fenêtre pop-up
            Stage stage = (Stage) champNom.getScene().getWindow();
            stage.close();

            System.out.println("✅ Profil mis à jour !");
        } else {
            System.err.println("❌ Erreur lors de la mise à jour du profil.");
        }
    }
}