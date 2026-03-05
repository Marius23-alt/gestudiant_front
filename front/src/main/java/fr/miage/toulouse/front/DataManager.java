package fr.miage.toulouse.front;

import fr.miage.toulouse.cours.Etudiant;
import fr.miage.toulouse.cours.Ue;
import fr.miage.toulouse.database.Request;

import java.util.ArrayList;
import java.util.List;

/**
 * Classe agissant comme la mémoire vive (cache) de l'application (Design Pattern Singleton).
 * <p>
 * Le {@code DataManager} a pour rôle de stocker les données récupérées de la base de données
 * lors du lancement de l'application. Cela permet aux différents contrôleurs (vues) de l'application
 * d'accéder aux données instantanément sans avoir à refaire de multiples requêtes SQL,
 * améliorant ainsi considérablement les performances.
 * </p>
 */
public class DataManager {
    private static DataManager instanceUnique;

    private List<Etudiant> listeEtudiants;
    private List<Ue> listeUe;

    /**
     * Constructeur privé pour empêcher l'instanciation directe avec "new".
     * Initialise la liste vide.
     */
    private DataManager() {
        this.listeEtudiants = new ArrayList<>();
        this.listeUe = new ArrayList<>();
    }


    /**
     * Retourne l'instance unique du {@code DataManager}.
     * <p>
     * Si l'instance n'existe pas encore, elle est créée (Lazy Loading).
     * </p>
     *
     * @return L'unique instance de {@code DataManager}.
     */
    public static DataManager getInstance() {
        if (instanceUnique == null) {
            instanceUnique = new DataManager();
        }
        return instanceUnique;
    }

    /**
     * Charge les données depuis la base de données dans la mémoire vive.
     * <p>
     * Cette méthode doit être appelée <b>une seule fois</b>, au démarrage de l'application
     * (généralement dans la méthode {@code start()} du Main). Elle utilise la classe {@link Request}
     * pour extraire l'ensemble des étudiants.
     * </p>
     */
    public void initialiserDonnees() {
        System.out.println("💾 DataManager : Chargement des données en mémoire...");
        Request req = new Request();
        this.listeEtudiants = req.recupTousLesEtudiants();
        this.listeUe = req.recupToutesLesUe();
        System.out.println("💾 DataManager : " + this.listeEtudiants.size() + " étudiants chargés en mémoire !");
        System.out.println("💾 DataManager : " + this.listeUe.size() + " UE chargés en mémoire !");

    }

    // ------- METHODE POUR LES FILTRES ----------

    /**
     * Extrait et retourne la liste de toutes les mentions uniques existantes.
     *
     * @return Une liste de {@code String} contenant les noms des mentions, triée par ordre alphabétique.
     */
    public List<String> getToutesLesMentions() {
        return listeEtudiants.stream()
                .map(e -> e.getParcour().getMention().getNom())
                .distinct().sorted().toList();
    }

    /**
     * Extrait et retourne la liste de tous les parcours uniques existants.
     *
     * @return Une liste de {@code String} contenant les noms des parcours, triée par ordre alphabétique.
     */
    public List<String> getTousLesParcours() {
        return listeEtudiants.stream()
                .map(e -> e.getParcour().getNom())
                .distinct().sorted().toList();
    }

    /**
     * Extrait et retourne la liste de tous les semestres uniques existants.
     *
     * @return Une liste de {@code String} contenant les numéros des semestres, triée par ordre numérique/alphabétique.
     */
    public List<String> getTousLesSemestres() {
        return listeEtudiants.stream()
                .map(e -> String.valueOf(e.getSemestreActuel()))
                .distinct().sorted().toList();
    }

    /**
     * Retourne une sous-liste d'étudiants correspondant aux critères de filtrage spécifiés.
     * <p>
     * Si un paramètre vaut {@code null} ou contient la valeur par défaut (ex: "Toutes les mentions"),
     * le critère est ignoré pour ce champ.
     * </p>
     *
     * @param mention  Le nom de la mention souhaitée, ou "Toutes les mentions" pour ignorer ce filtre.
     * @param parcours Le nom du parcours souhaité, ou "Tous les parcours" pour ignorer ce filtre.
     * @param semestre Le numéro du semestre souhaité, ou "Tous les semestres" pour ignorer ce filtre.
     * @return Une liste d'{@link Etudiant} correspondant aux trois critères cumulés.
     */
    public List<Etudiant> getEtudiantsFiltres(String mention, String parcours, String semestre) {
        return listeEtudiants.stream()
                .filter(e -> mention == null || mention.equals("Toutes les mentions") || e.getParcour().getMention().getNom().equals(mention))
                .filter(e -> parcours == null || parcours.equals("Tous les parcours") || e.getParcour().getNom().equals(parcours))
                .filter(e -> semestre == null || semestre.equals("Tous les semestres") || String.valueOf(e.getSemestreActuel()).equals(semestre))
                .toList();
    }

    public List<Ue> getUeFiltres(String mention, String parcours, String semestre) {
        return listeUe.stream()
                .filter(u -> mention == null || mention.equals("Toutes les mentions") || u.getParcour().getMention().getNom().equals(mention))
                .filter(u -> parcours == null || parcours.equals("Tous les parcours") || u.getParcour().getNom().equals(parcours))
                .filter(u -> semestre == null || semestre.equals("Tous les semestres") || String.valueOf(u.getSemestre()).equals(semestre))
                .toList();
    }

    /**
     * Extrait et retourne la liste des parcours appartenant UNIQUEMENT à une mention spécifique.
     *
     * @param mention Le nom de la mention ciblée.
     * @return Une liste de {@code String} contenant les noms des parcours de cette mention.
     */
    public List<String> getParcoursParMention(String mention) {
        return listeEtudiants.stream()
                // On garde que les étudiants de CETTE mention
                .filter(e -> e.getParcour().getMention().getNom().equals(mention))
                // On récupère le nom de leur parcours
                .map(e -> e.getParcour().getNom())
                .distinct().sorted().toList();
    }

    // --- Getters ---
    /**
     * Retourne la liste complète des étudiants actuellement stockée en mémoire.
     *
     * @return La liste complète de tous les objets {@link Etudiant}.
     */
    public List<Etudiant> getListeEtudiants() {
        return listeEtudiants;
    }
}