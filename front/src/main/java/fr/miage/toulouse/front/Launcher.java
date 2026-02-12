package fr.miage.toulouse.front;


/**
 * Classe d'entrée technique pour l'application.
 * Permet de lancer JavaFX sans configuration (car pas possible avec notre version Java)
 * en déléguant l'exécution de la classe Main.
 */
public class Launcher {
    public static void main(String[] args) {
        Main.main(args);
    }
}