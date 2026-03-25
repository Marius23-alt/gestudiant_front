package fr.miage.toulouse.front;

import fr.miage.toulouse.cours.Etudiant;
import fr.miage.toulouse.cours.Inscription;
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
    // --- TEMPORALITÉ GLOBALE DE L'UNIVERSITÉ ---
    private String anneeUniversitaireCourante;
    private boolean isSemestreImpair;

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

        String[] config = req.recupConfigurationGlobale();
        this.anneeUniversitaireCourante = config[0];
        this.isSemestreImpair = Boolean.parseBoolean(config[1]);
        System.out.println("🕒 DataManager : Horloge réglée sur l'année " + anneeUniversitaireCourante + " (Semestre Impair : " + isSemestreImpair + ")");

        this.listeEtudiants = req.recupTousLesEtudiants();
        this.listeUe = req.recupToutesLesUe();

        System.out.println(this.listeEtudiants.size() + " étudiants chargés");
        System.out.println(this.listeUe.size() + " UE chargées");

        // On relie les étudiants et les UEs
        req.lierInscriptionsEnMemoire(this.listeEtudiants, this.listeUe);
    }


    /**
     * Ajoute un étudiant fraîchement créé à la liste en mémoire.
     */
    public void ajouterEtudiantMemoire(Etudiant e) {
        this.listeEtudiants.add(e);
        System.out.println("🧠 DataManager : " + e.getNom() + " ajouté en mémoire !");
    }


    // ------- METHODE POUR LES FILTRES ----------

    public String getAnneeUniversitaireCourante() {
        return anneeUniversitaireCourante;
    }

    /**
     * Indique si l'université est actuellement sur un semestre impair (Automne: S1, S3, S5)
     * ou pair (Printemps: S2, S4, S6).
     * @return true si semestre impair, false si semestre pair.
     */
    public boolean isSemestreImpair() {
        return isSemestreImpair;
    }


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

    /**
     * Retourne la liste complète des UEs actuellement stockée en mémoire.
     */
    public List<Ue> getListeUes() {
        return listeUe;
    }


    /**
     * Retourne une liste d'étudiants selon des paramètres de filtrage qui ont au moins une ue en cours
     * @param mention la mention de filtrage
     * @param parcours le parcours de filtarge
     * @param semestre le semestre de filtrage
     * @return une liste d'étudiants qui ont une UE en cours
     */
    public List<Etudiant> getEtudiantEnCours(String mention, String parcours, String semestre) {

        List<Etudiant> etuEnCours = new ArrayList<>();

        List<Etudiant> etudiants = getEtudiantsFiltres(mention, parcours, semestre);

        for (Etudiant e : etudiants) {
            for (Inscription i : e.getInscription()) {
                if (i.getStatut().equals("en_cours")) {
                    etuEnCours.add(e);
                    // éviter les doublons si il y a plusieurs ue en cours
                    break;
                }
            }
        }
        return etuEnCours;
    }

    // ------- METHODE POUR RECUP LES ÉTUDIANTS DANS SAISIE DE MASSE ----------


    public List<Etudiant> getEtudiantsInscritsA(Ue ueSelectionne) {
        List<Etudiant> resultat = new ArrayList<>();
        String anneeCourante = getAnneeUniversitaireCourante();

        for (Etudiant e : this.listeEtudiants) {
            for (Inscription i : e.getInscription()) {
                if (i.getUe().getCode().equals(ueSelectionne.getCode())) {
                    System.out.println("-> MATCH CODE UE pour " + e.getNom() + " " + e.getPrenom());
                    System.out.println("   |- Statut actuel : '" + i.getStatut() + "' (Attendu : 'en_cours')");
                    System.out.println("   |- Année actuelle : '" + i.getAnnee() + "' (Attendue : '" + anneeCourante + "')");

                    if (i.getStatut().equals("en_cours") && i.getAnnee().equals(anneeCourante)) {
                        System.out.println("ÉTUDIANT AJOUTÉ AU TABLEAU !");
                        resultat.add(e);
                        break;
                    } else {
                        System.out.println("REFUSÉ (Mauvais statut ou mauvaise année)");
                    }
                }
            }
        }
        System.out.println(resultat.size() + " étudiants affichés dans le tableau");
        return resultat;
    }

    public List<Etudiant> getEtudiantsAutorisesA(Ue ueSelectionne) {
        List<Etudiant> resultat = new ArrayList<>();
        String anneeCourante = getAnneeUniversitaireCourante();
        boolean isSaisonImpaire = isSemestreImpair();

        // l'UE doit correspondre au semestre actuel (Pair/Impair)
        boolean ueEstImpaire = (ueSelectionne.getSemestre() % 2 != 0);
        if (ueEstImpaire != isSaisonImpaire) {
            // Si la saison ne correspond pas, on retourne une liste vide.
            return resultat;
        }

        for (Etudiant e : this.listeEtudiants) {
            //Il doit être dans le même parcours que l'UE
            if (!e.getParcour().getNom().equals(ueSelectionne.getParcour().getNom())) {
                continue;
            }

            boolean dejaPris = false;
            boolean prerequisOk = true;

            //Gestion du prérequis
            String prereq = ueSelectionne.getCodeUePrecedente();
            if (prereq != null && !prereq.trim().isEmpty()) {
                prerequisOk = false;
                for (Inscription i : e.getInscription()) {
                    if (i.getUe().getCode().equals(prereq) && i.getStatut().equals("valide")) {
                        prerequisOk = true;
                        break;
                    }
                }
            }

            //L'a-t-il déjà validée, l'a-t-il en cours, ou l'a-t-il échouée CETTE année ?
            for (Inscription i : e.getInscription()) {
                if (i.getUe().getCode().equals(ueSelectionne.getCode())) {
                    if (i.getStatut().equals("valide") ||
                            i.getStatut().equals("en_cours") ||
                            (i.getStatut().equals("echoue") && i.getAnnee().equals(anneeCourante))) {
                        dejaPris = true;
                        break;
                    }
                }
            }

            // S'il est clean et a les prérequis, on l'autorise !
            if (!dejaPris && prerequisOk) {
                resultat.add(e);
            }
        }
        return resultat;
    }

    //      -----SETTER--------

    public void setAnneeUniversitaireCourante(String anneeUniversitaireCourante){
        this.anneeUniversitaireCourante = anneeUniversitaireCourante;
    }

    public void setSemestreImpair(Boolean semestreImpair){
        this.isSemestreImpair = semestreImpair;
    }
}

