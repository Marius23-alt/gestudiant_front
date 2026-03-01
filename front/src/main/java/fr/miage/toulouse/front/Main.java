package fr.miage.toulouse.front;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.util.logging.Logger;
import java.util.logging.Level;

public class Main extends Application {
    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

    /**
     * Point d'entrée principal de l'application JavaFX.
     * <p>
     * Cette méthode initialise le "Stage" (la fenêtre principale) et orchestre le démarrage :
     * <ul>
     * <li>Charge le fichier {@code MainLayout.fxml} qui définit la structure globale (Menu + Zone de contenu).</li>
     * <li>Instancie le {MainController} et établit les liaisons avec les éléments @FXML.</li>
     * <li>Déclenche automatiquement la méthode {@code initialize()} du contrôleur,
     * ce qui affiche par défaut le Tableau de Bord.</li>
     * <li>Configure les propriétés de la fenêtre (Titre, dimensions, état maximisé).</li>
     * </ul>
     * </p>
     *
     * @param primaryStage Le support principal (fenêtre) fourni par JavaFX sur lequel
     * la scène sera affichée.
     */
    @Override
    public void start(Stage primaryStage) {
        try {
            // Création de tous les boutons, Menus, StackPane, etc)
            // Création du controleur MainController -> JavaFX voit que le fichier fxml et lié à la class MainController
            // Injecte : Connecte les éléments du FXML aux variables @FXML
            // Appelle automatiquement la méthode initialize() de MainController
            // => Conséquence : MainController exécute handleDashboard(), qui charge la vue 'Tableau de bord" dans zone centrale
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MainLayout.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root, 1000, 700);

            primaryStage.setTitle("Système de Gestion Étudiante - MIAGE");
            primaryStage.setScene(scene);

            primaryStage.setMaximized(true);
            DataManager.getInstance().initialiserDonnees();
            primaryStage.show();

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE,"Erreur lors du lancement de l'application : {}", e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Point d'entrée standard du programme Java.
     * <p>
     * Cette méthode statique est la première appelée lors du lancement de l'application.
     * Elle ne contient qu'une seule instruction : {@code launch(args)}, qui est une
     * méthode héritée de la classe {@link javafx.application.Application}.
     * Ce mécanisme initialise l'environnement d'exécution JavaFX et finit par appeler
     * automatiquement la méthode {@link #start(Stage)}.
     * </p>
     *
     * @param args Les arguments passés en ligne de commande (peuvent être utilisés pour
     * configurer l'application au démarrage).
     */
    public static void main(String[] args) {
        launch(args);
    }
}