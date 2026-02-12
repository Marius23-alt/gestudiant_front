package fr.miage.toulouse.front.controller;


import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MainController {
    private static final Logger LOGGER = Logger.getLogger(MainController.class.getName());

    @FXML
    private Text titleText;

    @FXML
    private StackPane contentArea;


    /**
     * A completer
     * @param fxmlFile
     * @param newTitle
     */
    private void loadView(String fxmlFile, String newTitle) {
        try {
            URL fxmlLocation = getClass().getResource("/fxml/" + fxmlFile);

            if (fxmlLocation == null) {
                throw new IOException("Fichier FXML introuvable : /fxml/" + fxmlFile);
            }

            FXMLLoader loader = new FXMLLoader(fxmlLocation);
            Parent view = loader.load();
            contentArea.getChildren().setAll(view);

            if (titleText != null) {
                titleText.setText(newTitle);
            }

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE,"Erreur de navigation : {}", e.getMessage());
            e.printStackTrace();
        }

    }

    @FXML
    private void handleDashboard() {
        loadView("dashboard.fxml", "Tableau de Bord");
    }

    @FXML
    private void handleAjouterEtudiant() {
        loadView("ajouterEtudiant.fxml", "Inscription Étudiant");
    }

    @FXML
    private void handleAdminSemestre() {
        loadView("adminSemestre.fxml", "Administration du Semestre");
    }

    @FXML
    public void initialize() { handleDashboard(); }
}