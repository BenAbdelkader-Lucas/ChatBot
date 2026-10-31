import java.util.ArrayList;
import java.util.Scanner;

public class Chatbot {

    private static final String MESSAGE_IGNORANCE = "Je ne sais pas.";
    private static final String MESSAGE_APPRENTISSAGE = "Je vais te l'apprendre.";
    private static final String MESSAGE_BIENVENUE = "J'attends tes questions de culture générale.";
    private static final String MESSAGE_QUITTER = "Au revoir.";
    private static final String MESSAGE_INVITATION = "Je t'écoute.";
    private static final String MESSAGE_CONFIRMATION = "Très bien, c'est noté.";

    private static final String REPONSESFILE = "reponses-IA.txt";
    private static final String THESAURUSFILE = "thesaurus.txt";
    private static final String QUESTIONSREPONSESFILE = "questions-reponses-IA.txt";
    private static final String MOTSOUTILS = "mots-outils.txt";

    private static Index indexThemes; // index pour trouver rapidement les réponses à partir des mots NON outils de la question
    private static Index indexFormes; // index pour trouver rapidement les formes de réponse possibles à partir des mots-outils de la question

    static private ArrayList<String> motsOutils; // vecteur trié des mots outils
    static private ArrayList<String> reponses; // vecteur des réponses
    private static ArrayList<String> formesReponses; //vecteur des formes de réponses
    private static Thesaurus thesaurus; //thésaurus



    public static void main(String[] args) {

        // initialisation du vecteur des mots outils
        motsOutils = Utilitaire.lireMotsOutils(MOTSOUTILS);
        // tri du vecteur des mots outils
        Utilitaire.trierChaines(motsOutils);

        // initialisation du vecteur des réponses
        reponses = Utilitaire.lireReponses(REPONSESFILE);
        //reponses = Utilitaire.lireReponses("mini_reponses.txt");

        // initialisation du thésaurus (partie 2)
        thesaurus = new Thesaurus(THESAURUSFILE);

        // construction de l'index pour retrouver rapidement les réponses sur leurs thématiques
        indexThemes = Utilitaire.constructionIndexReponses(reponses, motsOutils, thesaurus);
        //indexThemes.afficher();

        // construction de la table des formes de réponses
        formesReponses = Utilitaire.constructionTableFormes(reponses, motsOutils, thesaurus);
        //System.out.println("formesReponses "+formesReponses);

        // initialisation du vecteur des questions/réponses idéales
        ArrayList<String> questionsReponses = Utilitaire.lireQuestionsReponses(QUESTIONSREPONSESFILE);
        //ArrayList<String> questionsReponses = Utilitaire.lireQuestionsReponses("mini_questions-reponses.txt");

        // construction de l'index pour retrouver rapidement les formes possibles de réponses à partir des mots outils de la question
        indexFormes = Utilitaire.constructionIndexFormes(questionsReponses, formesReponses, motsOutils, thesaurus);
        //indexFormes.afficher();

        String reponse = "";
        String entreeUtilisateur = ""; // la dernière entrée de l'utilisateur
        String entreeUtilisateurPrecedente = ""; // la question précédente de l'utilisateur


        Scanner lecteur = new Scanner(System.in);
        System.out.println();
        System.out.print("> ");
        System.out.println(MESSAGE_BIENVENUE);

        do { // on attend des questions
            System.out.print("> ");
            entreeUtilisateur = lecteur.nextLine();
            if (entreeUtilisateur.compareTo(MESSAGE_QUITTER) != 0) { //tant que l'utilisateur ne veut pas arrêter
                if (reponse.compareTo(MESSAGE_INVITATION)==0){
                    //apprendre au chatbot
                    if(!Utilitaire.formeQuestionReponseExiste(entreeUtilisateurPrecedente, entreeUtilisateur, indexFormes, formesReponses, motsOutils, thesaurus)){
                        //ajoute entreeUtilisateurPrecedente+entreeUtilisateur à QUESTIONSREPONSESFILE si la forme n'y est pas

                        Utilitaire.integrerNouvelleQuestionReponse(entreeUtilisateurPrecedente, entreeUtilisateur, formesReponses, indexFormes, motsOutils, thesaurus);
                        reponse=MESSAGE_CONFIRMATION;
                        Utilitaire.ecrireFichier(QUESTIONSREPONSESFILE, entreeUtilisateurPrecedente+" "+entreeUtilisateur);
                        if (!Utilitaire.reponseExiste(entreeUtilisateur, indexThemes, reponses, motsOutils, thesaurus)){
                            //ajoute entreeUtilisateur à REPONSEFILE si la réponse n'y est pas.

                            Utilitaire.IntegrerNouvelleReponse(entreeUtilisateur, reponses, indexThemes, motsOutils, thesaurus);
                            Utilitaire.ecrireFichier(REPONSESFILE, entreeUtilisateur);
                        }
                    }else if (!Utilitaire.reponseExiste(entreeUtilisateur, indexThemes, reponses, motsOutils, thesaurus)){
                        //ajoute entreeUtilisateur à REPONSEFILE si la réponse n'y est pas.

                        Utilitaire.IntegrerNouvelleReponse(entreeUtilisateur, reponses, indexThemes, motsOutils, thesaurus);
                        reponse=MESSAGE_CONFIRMATION;
                        Utilitaire.ecrireFichier(REPONSESFILE, entreeUtilisateur);
                    }
                }else if (reponse.compareTo(MESSAGE_IGNORANCE)==0 & entreeUtilisateur.compareTo(MESSAGE_APPRENTISSAGE)==0) {
                    reponse = MESSAGE_INVITATION;
                }else if (Utilitaire.entierementInclus(motsOutils, entreeUtilisateur)){
                    //repondre avec le contexte

                    reponse = repondreEnContexte(entreeUtilisateur, entreeUtilisateurPrecedente);
                }else{
                    reponse = repondre(entreeUtilisateur);
                    entreeUtilisateurPrecedente = entreeUtilisateur; // modifier entreeUtilisateurPrecedente seulement si il y a des mots nom outils.
                }
                System.out.println("> " + reponse);
            }

        } while (entreeUtilisateur.compareToIgnoreCase(MESSAGE_QUITTER) != 0);


    }


    static private String repondre(String question) {
        ArrayList<Integer> reponsesCandidates = Utilitaire.constructionReponsesCandidates(question, indexThemes, motsOutils, thesaurus);
        if (reponsesCandidates.isEmpty()) {
            return MESSAGE_IGNORANCE;
        }
        ArrayList<Integer> reponsesSelectionnees = Utilitaire.selectionReponsesCandidates(question, reponsesCandidates, indexFormes, reponses, formesReponses, motsOutils, thesaurus);
        if (reponsesSelectionnees.isEmpty()){
            return MESSAGE_IGNORANCE;
        }


        int choix = (int) (Math.random() * reponsesSelectionnees.size());
        return (reponses.get(reponsesSelectionnees.get(choix)));
    }

    // partie 2
    static private String repondreEnContexte(String question, String questionPrecedente) {
        ArrayList<Integer> reponsesCandidates = Utilitaire.constructionReponsesCandidates(questionPrecedente, indexThemes, motsOutils, thesaurus);
        if (reponsesCandidates.isEmpty()) {
            return MESSAGE_IGNORANCE;
        }

        ArrayList<Integer> reponsesSelectionnees = Utilitaire.selectionReponsesCandidates(question, reponsesCandidates, indexFormes, reponses, formesReponses, motsOutils, thesaurus);
        if (reponsesSelectionnees.isEmpty()){
            return MESSAGE_IGNORANCE;
        }


        int choix = (int) (Math.random() * reponsesSelectionnees.size());
        return (reponses.get(reponsesSelectionnees.get(choix)));
    }


}

