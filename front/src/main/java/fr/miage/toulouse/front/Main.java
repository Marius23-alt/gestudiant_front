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
     * Permet de recuperer en parametre une fenetre, pour pouvoir ensuite la personnaliser et de l'a charger
     * @param primaryStage La fenetre créé par la méthode launch(args) du main grace a "extends Application"
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

            primaryStage.show();

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE,"Erreur lors du lancement de l'application : {}", e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}