package fr.miage.toulouse.front.controller;


import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;

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

    @FXML // Si souris passe sur bouton, changer la couleur du bouton
    private void handleMouseIn(MouseEvent event) {
        if (event.getSource() instanceof Button) {
            Button btn = (Button) event.getSource();
            btn.setStyle("-fx-background-color: #d30000; -fx-background-radius: 5; -fx-cursor: hand;");
        }
    }

    @FXML // Si souris sort du bouton, remettre normal
    private void handleMouseOut(MouseEvent event) {
        if (event.getSource() instanceof Button) {
            Button btn = (Button) event.getSource();
            btn.setStyle("-fx-background-color: transparent;");
        }
    }

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
    /**
     * Charge et affiche la vue du tableau de bord (Dashboard).
     * ELle permet de :
     * - Charger le fichier FXML (dashboard.fxml)
     * - Récupérer l'instance du DashboardController générée par le chargeur
     * - Injecter la référence de ce contrôleur principal (this) dans le Dashboard via (setMainController)
     * Cette liaison permet au Dashboard de demander l'ouverture d'autres vues (comme le profil étudiant) via le contrôleur principal.
     */
    @FXML
    private void handleDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/dashboard.fxml"));
            Parent view = loader.load();

            DashboardController dashboardCtrl = loader.getController();
            dashboardCtrl.setMainController(this);

            contentArea.getChildren().setAll(view);
            titleText.setText("Tableau de Bord");

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Erreur chargement dashboard : {}", e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAjouterEtudiant() {
        loadView("ajouterEtudiant.fxml", "Inscription Étudiant");
    }

    @FXML
    private void handleAdminSemestre() {
        loadView("adminSemestre.fxml", "Administration du Semestre");
    }

    /**
     * Charge et affiche la vue détaillée du profil pour un étudiant donné.
     * Elle permet de :
     * - Charger le fichier FXML de la vue profil (profilEtudiant.fxml)
     * - Récupérer le contrôleur associé et lui injecte l'objet étudiant (Tous les Controller sont créé automatiquement par JavaFX donc on a pas besoin de l'instancier, on récupère seulement l'instance)
     * - Remplacer le contenu de la zone principale par cette nouvelle vue
     * - Mettre à jour le titre de la fenêtre avec le nom et le prénom de l'étudiant
     * En cas d'erreur lors du chargement du fichier FXML, une exception est loggée.
     *
     * @param etudiant L'objet sélectionné dont on souhaite visualiser les informations.
     */
    @FXML
    public void handleProfilEtudiant(Etudiant etudiant) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/profilEtudiant.fxml"));
            Parent view = loader.load();

            ProfilEtudiantController profilCtrl = loader.getController();

            profilCtrl.setEtudiant(etudiant);

            contentArea.getChildren().setAll(view);
            String titre = String.format("Profil de %s %s", etudiant.getNom(), etudiant.getPrenom());
            titleText.setText(titre);

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Erreur chargement profil : {}", e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void initialize() { handleDashboard(); }
}