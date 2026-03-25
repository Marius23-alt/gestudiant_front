package fr.miage.toulouse.front.controller;

import fr.miage.toulouse.cours.Etudiant;
import fr.miage.toulouse.cours.Parcour;
import fr.miage.toulouse.front.DataManager;
import javafx.scene.control.*;
import fr.miage.toulouse.database.Request;
import javafx.fxml.FXML;

import java.time.LocalDate;


public class AjouterEtudiantController {
    @FXML private TextField fieldNumeroEtudiant;
    @FXML private TextField fieldPrenom;
    @FXML private TextField fieldNom;
    @FXML private DatePicker pickerDate;
    @FXML private ComboBox<String> comboMention;
    @FXML private ComboBox<String> comboParcours;
    @FXML private ComboBox<String> comboSemestre;


    private MainController mainController;

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

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
        comboSemestre.getItems().addAll("1", "2", "3", "4", "5", "6"); //Tous les semestres possibles
        comboSemestre.getSelectionModel().selectFirst();

        comboMention.getItems().addAll(DataManager.getInstance().getToutesLesMentions()); // On recupere les mentions

        comboParcours.setDisable(true);//Tant que la mention est pas choisi on peut pas choisir de parcours

        // maintenant on peut choisir un parcours
        comboMention.valueProperty().addListener((obs, ancienneValeur, nouvelleValeur) -> {
            if (nouvelleValeur != null){
                comboParcours.setDisable(false);
                comboParcours.getItems().clear();
                comboParcours.getItems().addAll(DataManager.getInstance().getParcoursParMention(nouvelleValeur));
            }
        });
    }

    /**
     * inscris un nouvel étudiant après validation  formulaire.
     * plusieurs étapes
     * - Vérifie que les champs sont bien rempli et dans le bon format
     * - Récupération du Parcours
     * - Instanciation du nouvel etudiant avec ses attributs par défaut
     * Mise à jour du cache local (DataManager) et redirection vers la vue profil
     * * @throws NumberFormatException Si le numéro étudiant saisi n'est pas un entier valide.
     */
    @FXML
    private void handleValider() {
        try {
            // vérifie si tous les champs sont rempli
            if (fieldNumeroEtudiant.getText().isEmpty() || fieldNom.getText().isEmpty() ||
                    comboParcours.getValue() == null || pickerDate.getValue() == null) {
                afficherAlerte("Champs manquants", "Veuillez remplir tous les champs obligatoires.", Alert.AlertType.WARNING);
                return;
            }

            int numEtu = Integer.parseInt(fieldNumeroEtudiant.getText());
            String nom = fieldNom.getText();
            String prenom = fieldPrenom.getText();
            LocalDate dateNaiss = pickerDate.getValue();
            int semestreChoisiInt = Integer.parseInt(comboSemestre.getValue());

            // retrouve le parcour à partir de son nom
            String nomParcoursChoisi = comboParcours.getValue();
            Parcour parcoursChoisi = null;

            for (Etudiant e : DataManager.getInstance().getListeEtudiants()) {
                if (e.getParcour().getNom().equals(nomParcoursChoisi)) {
                    parcoursChoisi = e.getParcour();
                    break;
                }
            }

            if (parcoursChoisi == null) {
                afficherAlerte("Erreur", "Impossible de retrouver l'objet Parcours associé.", Alert.AlertType.ERROR);
                return;
            }

            // Création de l'étudiant
            // par défault 0 ects
            Etudiant nouvelEtudiant = new Etudiant(numEtu, nom, prenom, dateNaiss,null, semestreChoisiInt, 0);

            // Sauvegarde dans BDD
            Request req = new Request();
            if (req.ajouterEtudiant(nouvelEtudiant)) {

                // Ajout dans la mémoire vive
                DataManager.getInstance().ajouterEtudiantMemoire(nouvelEtudiant);

                String semestreChoisiStr = comboSemestre.getValue();

                System.out.println("Redirection vers le profil de " + nom);

                if (mainController != null) {
                    mainController.handleProfilNouvelEtudiant(nouvelEtudiant, semestreChoisiStr);
                }
            } else {
                afficherAlerte("Erreur BDD", "L'étudiant n'a pas pu être inséré. (Ce numéro étudiant existe peut-être déjà ?)", Alert.AlertType.ERROR);
            }

        } catch (NumberFormatException e) {
            afficherAlerte("Erreur de saisie", "Le numéro étudiant doit être un nombre valide.", Alert.AlertType.ERROR);
        } catch (Exception e) {
            afficherAlerte("Erreur", "Une erreur inattendue est survenue : " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
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
    private void afficherAlerte(String titre, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
