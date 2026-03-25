package fr.miage.toulouse.front;



public class Launcher {

    /**
     * Point d'entrée alternatif pour le lancement de l'application.
     * <p>
     * Cette classe est utilisée pour contourner certaines restrictions de la JVM et de
     * JavaFX lors de l'exécution d'un fichier JAR. En appelant la méthode {@code main}
     * d'une classe qui n'hérite pas directement de {@code Application}, on s'assure
     * que l'application démarre correctement sans nécessiter de configuration complexe
     * des modules JavaFX sur l'ordinateur de l'utilisateur.
     * </p>
     *
     * @param args Les arguments de la ligne de commande transmis à l'application principale.
     */
    public static void main(String[] args) {
        Main.main(args);
    }
}