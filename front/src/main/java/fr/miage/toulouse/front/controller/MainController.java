package fr.miage.toulouse.front.controller;


import fr.miage.toulouse.cours.Etudiant;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MainController {
    private static final Logger LOGGER = Logger.getLogger(MainController.class.getName());

    // --- VARIABLES DE L'INTERFACE ---

    @FXML
    private Text titleText;

    @FXML
    private StackPane contentArea;

    @FXML
    private TextField searchField;

    @FXML
    private Button btnStat;

    @FXML
    private Button btnUe;

    @FXML
    private Button btnAjouter;

    @FXML
    private Button btnAdmin;

    private Button activeButton;

    /**
     * Méthode d'initialisation du contrôleur principal, appelée automatiquement par JavaFX.
     * <p>
     * Son rôle est de définir l'état initial de l'interface dès l'ouverture de l'application.
     * Elle appelle {@link #handleDashboard()} pour que le tableau de bord soit chargé et
     * affiché par défaut dans la zone de contenu, évitant ainsi à l'utilisateur de se
     * retrouver face à une interface vide au démarrage.
     * </p>
     */
    @FXML
    public void initialize() { handleDashboard(); }

    //------- NAVIGATION ---------

    /**
     * Gère l'affichage de la vue "Tableau de Bord" (Dashboard).
     * <p>
     * Cette méthode va plus loin qu'un simple chargement de vue : elle établit une
     * connexion entre les contrôleurs. Elle récupère l'instance du {@link DashboardController}
     * créée par le {@link FXMLLoader} et lui injecte une référence du contrôleur
     * principal ({@code this}).
     * </p>
     * <p>
     * Cette liaison est indispensable pour permettre au Dashboard de déléguer
     * des actions de navigation (comme l'ouverture d'un profil étudiant) au {@code MainController}.
     * </p>
     */
    @FXML
    private void handleDashboard() {
        setActiveButton(btnStat);
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

    /**
     * Gère l'affichage de la vue de gestion des Unités d'Enseignement (UE).
     * <p>
     * Cette méthode réalise les opérations suivantes :
     * <ul>
     * <li>Met visuellement en évidence le bouton "UE" dans le menu latéral via {@link #setActiveButton}.</li>
     * <li>Charge le fichier FXML correspondant à la vue des UE.</li>
     * <li>Récupère le contrôleur de la vue chargée ({@link UeController}) et lui injecte
     * une référence du contrôleur principal ({@code this}) pour permettre la navigation inverse.</li>
     * <li>Remplace le contenu de la zone centrale par cette nouvelle vue et met à jour le titre de la fenêtre.</li>
     * </ul>
     * </p>
     */
    @FXML
    public void handleUe() {
        setActiveButton(btnUe);
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Ue.fxml"));
            Parent view = loader.load();

            UeController ueCtrl = loader.getController();
            ueCtrl.setMainController(this);

            contentArea.getChildren().setAll(view);
            titleText.setText("UE");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gère l'affichage de la vue de saisie de masse (Inscriptions ou Validations).
     * <p>
     * Cette méthode est généralement appelée par un contrôleur enfant (comme {@link UeController}).
     * Elle charge la vue générique {@code saisieMasse.fxml} et établit la liaison bidirectionnelle
     * en injectant le {@code MainController} dans le {@link SaisieMasseController}.
     * Cela permet notamment au bouton "Retour" de la nouvelle vue de fonctionner correctement.
     * </p>
     */
    @FXML
    public void handleSaisieMasse() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/saisieMasse.fxml"));
            Parent view = loader.load();

            // On passe le MainController à la page de Saisie pour que le bouton Retour fonctionne !
            SaisieMasseController saisieCtrl = loader.getController();
            saisieCtrl.setMainController(this);

            contentArea.getChildren().setAll(view);
            // Le titre sera modifié dynamiquement plus tard
            titleText.setText("Saisie de Masse");

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Erreur chargement Saisie Masse : {}", e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Gère le changement de vue vers le formulaire d'ajout d'un étudiant.
     * <p>
     * Cette méthode fait appel à la méthode utilitaire {@link #loadView(String, String)}
     * pour charger le fichier FXML correspondant à l'inscription et mettre à jour
     * le titre de l'application. C'est une navigation dite "simple" car elle ne
     * nécessite pas d'interaction directe avec le contrôleur de la vue chargée.
     * </p>
     */
    @FXML
    private void handleAjouterEtudiant() {
        setActiveButton(btnAjouter);
        loadView("ajouterEtudiant.fxml", "Inscription Étudiant");
    }

    /**
     * Gère l'affichage de la vue dédiée à l'administration du semestre.
     * <p>
     * Cette méthode sollicite la méthode utilitaire {@link #loadView(String, String)}
     * pour charger le fichier FXML d'administration et mettre à jour le titre
     * de l'application. Elle permet d'accéder aux fonctionnalités de gestion
     * globale des semestres (dates, clôtures, etc.).
     * </p>
     */
    @FXML
    private void handleAdminSemestre() {
        setActiveButton(btnAdmin);
        loadView("adminSemestre.fxml", "Administration du Semestre");
    }

    /**
     * Gère l'affichage du profil détaillé d'un étudiant sélectionné.
     * <p>
     * Cette méthode réalise une navigation contextuelle :
     * <ul>
     * <li>Charge la vue {@code profilEtudiant.fxml}.</li>
     * <li>Accède au {@link ProfilEtudiantController} pour lui injecter l'objet {@link Etudiant}
     * choisi (permettant ainsi l'affichage de ses informations spécifiques).</li>
     * <li>Met à jour dynamiquement le titre de la fenêtre avec le nom et le prénom de l'étudiant
     * à l'aide d'un formatage de chaîne.</li>
     * <li>Remplace le contenu de la zone principale par cette nouvelle vue.</li>
     * </ul>
     * </p>
     *
     * @param etudiant L'objet {@link Etudiant} dont on souhaite afficher les détails.
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

    //----- UTILITAIRE ---------

    /**
     * Charge dynamiquement une nouvelle vue FXML dans la zone de contenu principale.
     * <p>
     * Cette méthode centralise la navigation de l'application. Elle effectue les étapes suivantes :
     * <ul>
     * <li>Localise le fichier FXML dans le dossier {@code /fxml/}.</li>
     * <li>Instancie un {@link FXMLLoader} pour transformer le fichier XML en objets JavaFX.</li>
     * <li>Remplace l'intégralité du contenu actuel de {@code contentArea} par la nouvelle vue chargée.</li>
     * <li>Met à jour le titre de l'en-tête pour refléter la page actuelle.</li>
     * </ul>
     * En cas d'erreur (fichier manquant, erreur de syntaxe FXML), l'exception est interceptée
     * et enregistrée dans les logs.
     * </p>
     *
     * @param fxmlFile Le nom du fichier FXML à charger (ex: "dashboard.fxml").
     * @param newTitle Le titre à afficher dans l'en-tête de l'application.
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

    //-----
    /**
     * Gère l'événement de survol (hover) de la souris sur un composant graphique.
     * <p>
     * Cette méthode est utilisée pour apporter un retour visuel dynamique à l'utilisateur
     * lorsqu'il passe sa souris sur un élément de l'interface (comme un bouton du menu).
     * Elle vérifie d'abord que l'élément survolé est bien un {@link Button}, puis elle
     * modifie son style CSS "à la volée" (couleur de fond rouge, bords arrondis, et
     * transformation du curseur en forme de main).
     * </p>
     *
     * @param event L'événement MouseEvent déclenché par l'entrée de la souris sur le composant.
     */
    @FXML
    private void handleMouseIn(MouseEvent event) {
        if (event.getSource() instanceof Button) {
            Button btn = (Button) event.getSource();
            // On ne fait l'effet de survol QUE si ce n'est pas le bouton déjà actif
            if (btn != activeButton) {
                btn.setStyle("-fx-background-color: #053b82; -fx-background-radius: 10; -fx-cursor: hand;");
            }
        }
    }

    /**
     * Gère l'événement de sortie de la souris (exit) d'un composant graphique.
     * <p>
     * Cette méthode agit comme le complément indispensable de {@code handleMouseIn}.
     * Elle permet de réinitialiser l'apparence des boutons du menu lorsque l'utilisateur
     * cesse de les survoler. Si l'élément quitté est un {@link Button}, son style
     * est remis en fond transparent, permettant ainsi de retrouver l'esthétique
     * épurée du menu latéral.
     * </p>
     *
     * @param event L'événement MouseEvent déclenché lorsque la souris quitte la surface du composant.
     */
    @FXML
    private void handleMouseOut(MouseEvent event) {
        if (event.getSource() instanceof Button) {
            Button btn = (Button) event.getSource();
            // On ne remet en transparent QUE si ce n'est pas le bouton actif
            if (btn != activeButton) {
                btn.setStyle("-fx-background-color: transparent;");
            }
        }
    }

    /**
     * Gère l'apparence visuelle de la navigation et indique sur quelle on est
     * @param clickedButton Le bouton cliqué par l'utilisateur
     */
    private void setActiveButton(Button clickedButton) {
        String styleNormal = "-fx-background-color: transparent;";
        btnStat.setStyle(styleNormal);
        btnUe.setStyle(styleNormal);
        btnAjouter.setStyle(styleNormal);
        btnAdmin.setStyle(styleNormal);

        clickedButton.setStyle("-fx-background-color: rgba(255, 255, 255, 0.1); -fx-background-radius: 10;");
        activeButton = clickedButton;
    }

}