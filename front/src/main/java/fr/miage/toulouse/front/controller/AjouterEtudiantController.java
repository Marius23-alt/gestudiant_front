package fr.miage.toulouse.front.controller;

import fr.miage.toulouse.database.Request;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

import java.util.List;

public class AjouterEtudiantController {
    @FXML private TextField fieldNumeroEtudiant;
    @FXML private TextField fieldPrenom;
    @FXML private TextField fieldNom;
    @FXML private TextField fieldDate;
    @FXML private ComboBox<String> comboMention;
    @FXML private ComboBox<String> comboParcours;

    @FXML
    public void initialize(){
        // 1. On vérifie que la page charge bien le contrôleur
        System.out.println("=== CHARGEMENT DE LA PAGE AJOUTER ETUDIANT ===");

        Request req = new Request();
        comboParcours.setDisable(true);

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
}
