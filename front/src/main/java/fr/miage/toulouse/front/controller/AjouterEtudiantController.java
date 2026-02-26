package fr.miage.toulouse.front.controller;

import javafx.scene.control.Alert;
import fr.miage.toulouse.database.Request;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.DatePicker;
import java.time.LocalDate;

import javafx.event.ActionEvent;
import java.util.List;

public class AjouterEtudiantController {
    @FXML private TextField fieldNumeroEtudiant;
    @FXML private TextField fieldPrenom;
    @FXML private TextField fieldNom;
    @FXML private DatePicker pickerDate;
    @FXML private ComboBox<String> comboMention;
    @FXML private ComboBox<String> comboParcours;
    @FXML private ComboBox<String> comboSemestre;

    @FXML
    public void initialize(){
        // 1. On vérifie que la page charge bien le contrôleur
        System.out.println("=== CHARGEMENT DE LA PAGE AJOUTER ETUDIANT ===");

        Request req = new Request();
        comboParcours.setDisable(true);

        comboSemestre.getItems().addAll("1", "2", "3", "4", "5", "6");

        List<String> listeMentions = req.recupMentions();

        if (listeMentions != null) {
            System.out.println("Succès : " + listeMentions.size() + " mentions trouvées dans la BD !");
            comboMention.getItems().addAll(listeMentions);
        } else {
            System.out.println("Erreur : La liste des mentions est NULL !");
        }

        comboMention.getSelectionModel().selectedItemProperty().addListener((obs, ancienneValeur, nouvelleValeur) -> {

            if (nouvelleValeur != null){
                comboParcours.setDisable(false);
                comboParcours.getItems().clear();

                List<String> nouveauxParcours = req.recupParcoursParMention(nouvelleValeur);
                if (nouveauxParcours != null) {
                    comboParcours.getItems().addAll(nouveauxParcours);
                }
            }
        });
    }

    @FXML
    public void handleValider(ActionEvent event) {
        // 1. On récupère le texte tapé dans les champs
        String numero = fieldNumeroEtudiant.getText();
        String prenom = fieldPrenom.getText();
        String nom = fieldNom.getText();
        String nomParcours = comboParcours.getValue();
        String semestre = comboSemestre.getValue();
        LocalDate dateNaissance = pickerDate.getValue();

        if (numero.isEmpty() || prenom.isEmpty() || nom.isEmpty() || nomParcours == null || dateNaissance == null) {
            afficherAlerte("Erreur", "Veuillez remplir tous les champs obligatoires.", Alert.AlertType.ERROR);
            return; // On arrête tout, on n'envoie rien à la base de données
        }

        Request req = new Request();

        // N'oublie pas de l'ajouter dans ton 'if' de vérification pour vérifier qu'il n'est pas null !
        if (numero.isEmpty() || prenom.isEmpty() || nom.isEmpty() || nomParcours == null || semestre == null) {
            afficherAlerte("Erreur", "Veuillez remplir tous les champs obligatoires.", Alert.AlertType.ERROR);
            return;
        }

        String idParcours = req.recupIdParcours(nomParcours);
        String dateMySQL = dateNaissance.toString();

        if (idParcours != null) {
            // On récupère le vrai résultat de la base de données
            boolean ajoutReussi = req.ajouterEtudiant(numero, nom, prenom, dateMySQL, idParcours, semestre);

            if (ajoutReussi) {
                afficherAlerte("Succès", "L'étudiant " + prenom + " " + nom + " a bien été ajouté !", Alert.AlertType.INFORMATION);
                fieldNumeroEtudiant.clear();
                fieldNom.clear();
                fieldPrenom.clear();
                pickerDate.setValue(null);
            } else {
                // Si ajoutReussi est false
                afficherAlerte("Erreur", "L'ajout a échoué. Cet étudiant existe peut-être déjà ou les informations sont incorrectes.", Alert.AlertType.ERROR);
            }
        } else {
            afficherAlerte("Erreur serveur", "Impossible de trouver l'identifiant de ce parcours.", Alert.AlertType.ERROR);
        }
    }

    private void afficherAlerte(String titre, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
