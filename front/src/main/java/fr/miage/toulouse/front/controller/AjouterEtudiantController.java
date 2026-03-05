package fr.miage.toulouse.front.controller;

import javafx.scene.control.*;
import fr.miage.toulouse.database.Request;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;

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
    @FXML private RadioButton radioImmediat;
    @FXML private RadioButton radioDiffere;
    @FXML private ToggleGroup groupeEffet;

    /**
     * Méthode d'initialisation appelée automatiquement par JavaFX après le chargement du fichier FXML.
     * <p>
     * Cette méthode configure l'état initial des composants de l'interface graphique :
     * <ul>
     * <li>Désactive la liste déroulante des parcours tant qu'aucune mention n'est sélectionnée.</li>
     * <li>Remplit la liste déroulante des semestres avec des valeurs fixes (1 à 6).</li>
     * <li>Charge dynamiquement les mentions disponibles depuis la base de données.</li>
     * <li>Ajoute un écouteur d'événements (listener) sur la sélection de la mention pour
     * filtrer et afficher dynamiquement les parcours correspondants.</li>
     * </ul>
     * </p>
     */
    @FXML
    public void initialize(){
//        Request req = new Request();
////        List<String> listeMentions = req.recupMentions();
//
//        comboParcours.setDisable(true);
//        comboSemestre.getItems().addAll("1", "2", "3", "4", "5", "6");
//
////        if (listeMentions != null) {
////            System.out.println("Succès : " + listeMentions.size() + " mentions trouvées dans la BD !");
////            comboMention.getItems().addAll(listeMentions);
////        } else {
////            System.out.println("Erreur : La liste des mentions est NULL !");
////        }
//
//        comboMention.getSelectionModel().selectedItemProperty().addListener((obs, ancienneValeur, nouvelleValeur) -> {
//
//            if (nouvelleValeur != null){
//                comboParcours.setDisable(false);
//                comboParcours.getItems().clear();
//
//                List<String> nouveauxParcours = req.recupParcoursParMention(nouvelleValeur);
//                if (nouveauxParcours != null) {
//                    comboParcours.getItems().addAll(nouveauxParcours);
//                }
//            }
//        });
    }

    /**
     * A faire et revoir la méthode concernant les objets
     */
    @FXML
    public void handleValider(ActionEvent event) {
//        String numero = fieldNumeroEtudiant.getText();
//        String prenom = fieldPrenom.getText();
//        String nom = fieldNom.getText();
//        String nomParcours = comboParcours.getValue();
//        String semestre = comboSemestre.getValue();
//        LocalDate dateNaissance = pickerDate.getValue();
//
//        if (numero.isEmpty() || prenom.isEmpty() || nom.isEmpty() || nomParcours == null || dateNaissance == null || semestre == null) {
//            afficherAlerte("Erreur", "Veuillez remplir tous les champs obligatoires.", Alert.AlertType.ERROR);
//            return;
//        }
//
//        Request req = new Request();
//
//        String idParcours = req.recupIdParcours(nomParcours);
//        String dateMySQL = dateNaissance.toString();
//
//        if (idParcours != null) {
//            boolean ajoutReussi = req.ajouterEtudiant(numero, nom, prenom, dateMySQL, idParcours, semestre);
//            if (ajoutReussi) {
//                afficherAlerte("Succès", "L'étudiant " + prenom + " " + nom + " a bien été ajouté !", Alert.AlertType.INFORMATION);
//                fieldNumeroEtudiant.clear();
//                fieldNom.clear();
//                fieldPrenom.clear();
//                pickerDate.setValue(null);
//            } else {
//                afficherAlerte("Erreur", "L'ajout a échoué. Cet étudiant existe peut-être déjà ou les informations sont incorrectes.", Alert.AlertType.ERROR);
//            }
//        } else {
//            afficherAlerte("Erreur serveur", "Impossible de trouver l'identifiant de ce parcours.", Alert.AlertType.ERROR);
//        }
    }



    /**
     * Affiche une boîte de dialogue (pop-up) à l'écran pour informer l'utilisateur.
     * <p>
     * Cette méthode utilitaire permet de générer rapidement des alertes personnalisées
     * (message de succès, d'erreur, d'avertissement...) en centralisant la configuration
     * de base de JavaFX. La méthode bloque l'interaction avec le reste de l'application
     * (showAndWait) jusqu'à ce que l'utilisateur ferme la fenêtre.
     * </p>
     *
     * @param titre   Le titre qui s'affiche tout en haut de la fenêtre d'alerte.
     * @param message Le texte explicatif détaillé affiché au centre de la boîte de dialogue.
     * @param type    Le style visuel de l'alerte (ex: {@code Alert.AlertType.ERROR} ou
     * {@code Alert.AlertType.INFORMATION}), qui définit l'icône affichée.
     */
//    private void afficherAlerte(String titre, String message, Alert.AlertType type) {
//        Alert alert = new Alert(type);
//        alert.setTitle(titre);
//        alert.setHeaderText(null);
//        alert.setContentText(message);
//        alert.showAndWait();
//    }
}
