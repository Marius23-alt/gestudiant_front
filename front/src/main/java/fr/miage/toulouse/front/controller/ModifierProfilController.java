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

        int idParcours = 1; // Par défault on met 1

        // MAJ dans la bdd
        Request req = new Request();
        boolean succes = req.updateEtudiant(etudiant.getNumEtu(), nouveauNom, nouveauPrenom, idParcours);

        if (succes) {
            // maj de la mémoire Java
            etudiant.setNom(nouveauNom);
            etudiant.setPrenom(nouveauPrenom);

            parentController.setEtudiant(etudiant);

            // fermeture fenetre
            Stage stage = (Stage) champNom.getScene().getWindow();
            stage.close();

            System.out.println("Profil mis à jour !");
        } else {
            System.err.println("Erreur lors de la mise à jour du profil.");
        }
    }
}