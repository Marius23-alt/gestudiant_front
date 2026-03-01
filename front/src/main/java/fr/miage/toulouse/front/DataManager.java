package fr.miage.toulouse.front;

import fr.miage.toulouse.cours.Etudiant;
import fr.miage.toulouse.database.Request;

import java.util.ArrayList;
import java.util.List;

public class DataManager {
    private static DataManager instanceUnique;

    private List<Etudiant> listeEtudiants;

    private DataManager() {
        this.listeEtudiants = new ArrayList<>();
    }

    public static DataManager getInstance() {
        if (instanceUnique == null) {
            instanceUnique = new DataManager();
        }
        return instanceUnique;
    }

    /**
     * Cette méthode sera appelée une seule fois au démarrage de l'app.
     * Elle remplit les tiroirs de la mémoire vive avec la base de données.
     */
    public void initialiserDonnees() {
        System.out.println("💾 DataManager : Chargement des données en mémoire...");
        Request req = new Request();
        this.listeEtudiants = req.recupTousLesEtudiants();
        System.out.println("💾 DataManager : " + this.listeEtudiants.size() + " étudiants chargés en mémoire !");
    }

    // --- Getters ---
    public List<Etudiant> getListeEtudiants() {
        return listeEtudiants;
    }
}