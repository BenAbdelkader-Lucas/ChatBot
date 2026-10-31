import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class Utilitaire {

    private static final int NBMOTS_FORME = 5; // nombre maximal de mots-outils pris en compte pour les formes dans l'étape 2

    static public ArrayList<String> lireMotsOutils(String nomFichier) {
        //{}=>{résultat = le vecteur des mots outils construit à partir du fichier nomFichier}
        ArrayList<String> motsOutils = new ArrayList<>();
        try {
            FileInputStream file = new FileInputStream(nomFichier);
            Scanner scanner = new Scanner(file);

            while (scanner.hasNextLine()) {
                String ligne = scanner.nextLine();
                motsOutils.add(ligne);
            }

            scanner.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return motsOutils;
    }

    static public ArrayList<String> lireReponses(String nomFichier) {
        //{}=>{résultat = le vecteur des réponses construit à partir du fichier nomFichier}
        ArrayList<String> reponses = new ArrayList<>();

        try {
            FileInputStream file = new FileInputStream(nomFichier);
            Scanner scanner = new Scanner(file);

            while (scanner.hasNextLine()) {
                String ligne = scanner.nextLine();
                reponses.add(ligne);
            }

            scanner.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return reponses;
    }

    static public ArrayList<String> lireQuestionsReponses(String nomFichier) {
        //{}=>{résultat = le vecteur des questions/réponses construit à partir du fichier nomFichier}
        ArrayList<String> questionsReponses = new ArrayList<>();

        try {
            FileInputStream file = new FileInputStream(nomFichier);
            Scanner scanner = new Scanner(file);

            while (scanner.hasNextLine()) {
                String ligne = scanner.nextLine();
                questionsReponses.add(ligne);
            }

            scanner.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return questionsReponses;


    }

    public static void ecrireFichier(String nomFichier, String chaineAEcrire) {
        //{}=>{la chaîne  chaineAEcrire est écrite après saut de ligne à la suite du fichier nomFichier}
        // true = mode append ? écrit à la suite sans effacer ce qui existe
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(nomFichier, true))) {
            writer.newLine();
            writer.write(chaineAEcrire);// ajoute un retour à la ligne
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    static private ArrayList<String> decoupeEnMots(String contenu) {
        //{}=>{résultat = le vecteur des mots de la chaîne contenu après pré-traitements divers}
        String chaine = contenu.toLowerCase();
        chaine = chaine.replace('\n', ' ');
        chaine = chaine.replace('?', ' ');
        chaine = chaine.replace('-', ' ');
        chaine = chaine.replace('\'', ' ');
        chaine = chaine.replace('.', ' ');
        chaine = chaine.replace(',', ' ');
        chaine = chaine.replace(':', ' ');
        chaine = chaine.replace(';', ' ');
        chaine = chaine.replace('\'', ' ');
        chaine = chaine.replace('"', ' ');
        chaine = chaine.replace('', ' ');
        chaine = chaine.replace('', ' ');
        chaine = chaine.replace("'", " ");
        chaine = chaine.replace('(', ' ');
        chaine = chaine.replace(')', ' ');
        chaine = chaine.replace('«', ' ');
        chaine = chaine.replace('-', ' ');
        chaine = chaine.replace('’', ' ');


        String[] tabchaine = chaine.split(" ");
        ArrayList<String> resultat = new ArrayList<>();

        for (int i = 0; i < tabchaine.length; ++i) {
            if (!tabchaine[i].equals("")) {
                resultat.add(tabchaine[i]);
            }
        }

        return resultat;
    }


    static private boolean existeChaine(ArrayList<String> mots, String mot) {
        //{}=>  {recherche séquentielle de mot dans mots
        // résultat =  true si trouvé et false sinon }
        if (mots.isEmpty()){
            return false;
        }
        int i = 0;
        while (i<mots.size() && mots.get(i).compareTo(mot)!=0){
            i++;
        }
        if (i<mots.size() && mots.get(i).compareTo(mot)==0){
            return true;
        }
        return false;
    }


    static private boolean existeChaineDicho(ArrayList<String> lesChaines, String chaine) {
        //{lesChaines (triée dans l'ordre lexicographique)}=>  {recherche dichotomique de chaine dans lesChaines
        // résultat =  true si trouvé et false sinon }
        if (lesChaines.isEmpty() || chaine.compareTo(lesChaines.get(lesChaines.size()-1))>0){
            return false;
        }else {
            int inf = 0;
            int sup = lesChaines.size()-1;
            int m;
            while (inf < sup){
                m = (inf+sup)/2;
                if (lesChaines.get(m).compareTo(chaine)>=0){
                    sup=m;
                }else{
                    inf = m+1;
                }
            }
            if (lesChaines.get(sup).compareTo(chaine)==0){
                return true;
            }else {
                return false;
            }
        }
    }

    static public boolean entierementInclus(ArrayList<String> mots, String question) {
        //{mots est trié dans l'ordre lexicographique}=>
        // résultat = true si tous les mots de questions sont dans mots, false sinon
        // remarque : utilise decoupeEnMots et existeChaineDicho}
        ArrayList<String> motsQ = decoupeEnMots(question);
        int i = 0;
        while (i<motsQ.size() && existeChaineDicho(mots, motsQ.get(i))){
            i++;
        }
        if (i<motsQ.size()){
            return false;
        }
        return true;
    }


    static private int rechercherChaine(ArrayList<String> lesChaines, String chaine) {
        // {}=>{résultat = l'indice de chaine dans lesChaines si trouvé et -1 sinon }
        if (lesChaines.isEmpty()){
            return -1;
        }else {
            int i = 0;
            while (i<lesChaines.size() && chaine.compareTo(lesChaines.get(i))!=0){
                i++;
            }
            if (i<lesChaines.size() && chaine.compareTo(lesChaines.get(i))==0){
                return i;
            }
            return -1;
        }
    }


    static public void integrerNouvelleQuestionReponse(String question, String reponse, ArrayList<String> formes, Index indexFormes, ArrayList<String> motsOutils, Thesaurus thesaurus) {
        //{la forme de reponse n'existe pas ou n'est pas associée à question dans indexFormes}=>{la forme de reponse est ajoutée à la fin de formes si elle n'y est pas déjà
        // et indexFormes est mis à jour pour tenir compte de cette nouvelle question-réponse
        // remarque 1 : utilise calculForme, rechercherChaine, decoupeEnMots, existeChaineDicho, ajouterSortieAEntree, rechercherSortiePourEntree
        // remarque 2 : seuls les NBMOTS_FORME premiers mots-outils de la question sont pris en compte}

        String formeR = calculForme(reponse, motsOutils, thesaurus);

        int indexForme = rechercherChaine(formes, formeR);
        if (indexForme==-1){
            formes.add(formeR);
            indexForme = formes.size()-1;
        }

        String formeQ = calculForme(question, motsOutils, thesaurus);
        ArrayList<String> formeQDecoupe = decoupeEnMots(formeQ);
        ArrayList<String> formeNum = new ArrayList<>();

        int i = 0;

        while (i<formeQDecoupe.size() && i<NBMOTS_FORME){
            String forme = formeQDecoupe.get(i); //pas de thesaurus car calculForme le fait
            if (estUnNombre(forme)){
                forme = "NUM";
            }
            forme = forme+"_"+i;
            formeNum.add(forme);
            i++;
        }


        for (int j = 0; j < formeNum.size(); j++) {
            indexFormes.ajouterSortieAEntree(formeNum.get(j), indexForme);
        }

    }


    static public void IntegrerNouvelleReponse(String reponse, ArrayList<String> reponses, Index indexContenu, ArrayList<String> motsOutils, Thesaurus thesaurus) {
        //{reponse n'est pas présent dans reponses}=>{reponse est ajoutée à la fin de reponses et indexContenu est mis à jour pour tenir compte de cette nouvelle réponse
        // remarque : utilise decoupeEnMots, existeChaineDicho, ajouterSortieAEntree, rechercherSortiePourEntree
        reponses.add(reponse);

        ArrayList<String> reponseDecoupe = decoupeEnMots(reponse);
        ArrayList<String> reponseDecoupeSansMotsOutils = new ArrayList<>();
        //garde les mots thématiques
        for (int i=0; i<reponseDecoupe.size(); i++){
            String leMot = reponseDecoupe.get(i);
            leMot = thesaurus.rechercherSortiePourEntree(leMot);
            if (!existeChaineDicho(motsOutils, leMot)){
                reponseDecoupeSansMotsOutils.add(leMot);
            }
        }

        for (int i = 0; i < reponseDecoupeSansMotsOutils.size(); i++) {
            indexContenu.ajouterSortieAEntree(reponseDecoupeSansMotsOutils.get(i), reponses.size()-1);
        }
    }

    static public Index constructionIndexReponses(ArrayList<String> reponses, ArrayList<String> motsOutils, Thesaurus thesaurus) {
        //{}=>{résultat = un index dont les entrées sont les mots des réponses (reponses) absents de motsOutils.
        // et les sorties sont les indices (dans reponses) des réponses les contenant.
        // remarque : utilise existeChaineDicho, decoupeEnMots et ajouterSortieAEntree }
        Index index = new Index();
        for (int i=0; i<reponses.size(); i++){
            ArrayList<String> lesMots = decoupeEnMots(reponses.get(i));

            for (int j=0; j<lesMots.size(); j++){
                String leMot = thesaurus.rechercherSortiePourEntree(lesMots.get(j));
                //garde les mots thématiques
                if (!existeChaineDicho(motsOutils, leMot)){
                    index.ajouterSortieAEntree(leMot, i);
                }
            }

        }
        return index;
    }


    static void trierChaines(ArrayList<String> v) {
        //{}=>{v est trié dans l'ordre lexicographique }
        int j;
        String valeurAPlacer;
        int i = 1;
        while (i < v.size()){
            j = i;
            valeurAPlacer = v.get(i);
            while (j > 0 && v.get(j-1).compareTo(valeurAPlacer)>0) {
                v.set(j, v.get(j-1));
                j--;
            }
            v.set(j, valeurAPlacer);
            i++;
        }

    }


    static ArrayList<Integer> maxOccurences(ArrayList<Integer> v, int seuil) {
        //{v trié} => {résultat = vecteur des entiers dont le nombre d'occurences
        // est maximal et au moins égal au seuil. Si le nombre d'occurences maximal est inférieur au seuil , un vecteur vide est retourné.
        // Par exemple, si V est [3,4,5,5,5,6,6,8,8,8,12,16,16,20]
        // si seuil<=3 alors le résultat est [5,8].
        // si le seuil>3 alors le résultat est []}
        ArrayList<Integer> vIntMax = new ArrayList<>();
        if (v.isEmpty()){
            return vIntMax;
        }
        int nbOcc = 0;
        int max = 0;
        for (int i=1; i<v.size(); i++){
            nbOcc++;
            if (v.get(i).compareTo(v.get(i-1))!=0){
                if (nbOcc>=seuil){
                    if (nbOcc>max){
                        max = nbOcc;
                        vIntMax.clear();
                        vIntMax.add(v.get(i-1));
                    } else if (nbOcc == max) {
                        vIntMax.add(v.get(i-1));
                    }
                }
                nbOcc = 0;
            }

        }
        nbOcc++;
        if (nbOcc>=seuil){
            if (nbOcc>max){
                max = nbOcc;
                vIntMax.clear();
                vIntMax.add(v.get(v.size()-1));
            } else if (nbOcc == max) {
                vIntMax.add(v.get(v.size()-1));
            }
        }
        return vIntMax;
    }


    static ArrayList<Integer> fusion(ArrayList<Integer> v1, ArrayList<Integer> v2) {
        //{v1 et v2 triés}=>{résultat = vecteur trié fusionnant v1 et v2 sans supprimer les répétitions
        // par exemple si v1 est [4,8,8,10,25] et v2 est [5,8,9,25]
        // le résultat est [4,5,8,8,8,9,10,25,25]}
        ArrayList<Integer> listeFusion = new ArrayList<>();
        int indv1 = 0;
        int indv2 = 0;
        while (indv1<v1.size() & indv2<v2.size()){
            if (v1.get(indv1) >= v2.get(indv2)){
                listeFusion.add(v2.get(indv2));
                indv2 ++;
            }else {
                listeFusion.add(v1.get(indv1));
                indv1++;
            }
        }
        while (indv1<v1.size()){
            listeFusion.add(v1.get(indv1));
            indv1++;
        }
        while (indv2<v2.size()){
            listeFusion.add(v2.get(indv2));
            indv2 ++;
        }
        return listeFusion;
    }


    static String calculForme(String chaine, ArrayList<String> motsOutils, Thesaurus thesaurus) {
        //{}=>{résultat = la concaténation des NBMOTS_FORME premiers mots-outils de chaine séparés par des blancs
        // remarque 1 : utilise decoupeMots et existeChaineDicho
        // remarque 2 : la limitation de la taille des formes permet d'accepter des réponses terminant par des précisions }
        ArrayList<String> listeMots = decoupeEnMots(chaine);
        String motsForme = "";

        int i = 0;
        int nbForme = 0;
        while (i<listeMots.size() && nbForme<NBMOTS_FORME){
            String leMot = thesaurus.rechercherSortiePourEntree(listeMots.get(i));
            if (estUnNombre(leMot)){
                leMot = "NUM";
            }
            if (existeChaineDicho(motsOutils, leMot)){
                motsForme = motsForme + leMot + " ";
                nbForme++;
            }
            i++;
        }
        return motsForme;
    }

    static public ArrayList<String> constructionTableFormes(ArrayList<String> reponses, ArrayList<String> motsOutils, Thesaurus thesaurus) {
        //{}=>{résultat = le vecteur de toutes les formes de réponses dans reponses.
        // remarque : utilise calculForme et existeChaine }
        ArrayList<String> vFormes = new ArrayList<>();
        for (int i = 0; i<reponses.size(); i++){
            String forme = calculForme(reponses.get(i),motsOutils, thesaurus);
            if (!existeChaine(vFormes, forme)){
                vFormes.add(forme);
            }
        }
        return vFormes;
    }

    static public Index constructionIndexFormes(ArrayList<String> questionsReponses, ArrayList<String> formes, ArrayList<String> motsOutils, Thesaurus thesaurus) {
        //{}=>{résultat = un index dont les entrées sont les "mots-outils positionnés" des questions (par exemple l'entrée pour un "Qui" en première position sera "qui_0")
        // et les sorties sont les indices (dans formes) des formes de réponses répondant aux questions contenant le mot-outil à cette position.
        // remarque 1 : utilise calculForme, rechercherChaine, decoupeEnMots, rechercherEntree, existeChaineDicho et ajouterSortieAEntree
        // remarque 2 : utilisez les méthodes indexOf et substring de String pour décomposer la question-réponse en question et réponse
        // remarque 3 : seuls les NBMOTS_FORME premiers mots-outils de la question sont pris en compte}
        Index index = new Index();
        for (int i = 0; i<questionsReponses.size(); i++){
            int indexSeparation = questionsReponses.get(i).indexOf("?");
            String question = questionsReponses.get(i).substring(0, indexSeparation);
            String reponse = questionsReponses.get(i).substring(indexSeparation+1);
            String formeQ = calculForme(question, motsOutils, thesaurus);
            ArrayList<String> formeDecoupe = decoupeEnMots(formeQ);
            String formeNum = "";
            String formeR = calculForme(reponse, motsOutils, thesaurus);

            int j = 0;
            while (j<formeDecoupe.size() && j<NBMOTS_FORME){
                formeNum = formeDecoupe.get(j);

                if (estUnNombre(formeNum)){
                    formeNum = "NUM";
                }

                formeNum = formeNum+"_"+j;

                int indexFormeR = rechercherChaine(formes,formeR);
                if (indexFormeR>=0){
                    index.ajouterSortieAEntree(formeNum, indexFormeR);
                }
                j++;
            }

        }
        return index;
    }

    static public ArrayList<Integer> constructionReponsesCandidates(String question, Index IndexReponses, ArrayList<String> motsOutils, Thesaurus thesaurus) {
        //{}=>{résultat = vecteur des identifiants de réponses contenant l'ensemble des mots non outils de la question.
        // remarque 1 : utilise decoupeEnMots, existeChaineDicho, rechercherSorties, fusion et maxOccurences
        // remarque 2 : maxOccurences est appelé en passant le nombre de mots non outils de la question comme valeur de seuil.
        // remarque 3 : on aurait pu calculer directement une intersection au lieu d'une fusion et se passer de maxOccurences mais on
        // souhaite pouvoir garder la possibilité d'assouplir par la suite la contrainte sur la présence de l'intégralité
        // des mots de la question dans la réponse }

        ArrayList<String> motsQuestion = decoupeEnMots(question);
        ArrayList<String> motsQuestionSansMotsOutils = new ArrayList<>();

        //garde les mots thématiques
        for (int i=0; i<motsQuestion.size(); i++){
            String leMot = thesaurus.rechercherSortiePourEntree(motsQuestion.get(i));
            if (!existeChaineDicho(motsOutils, leMot)){
                motsQuestionSansMotsOutils.add(leMot);
            }
        }

        int seuil = (int) (motsQuestionSansMotsOutils.size()*0.5); //Ici on peut jouer avec la "sensibilité" du chatbot
        ArrayList<Integer> vOcc = new ArrayList<>();

        if (!motsQuestionSansMotsOutils.isEmpty()) {
            //fusion de tout les vecteur d'index
            ArrayList<Integer> vFusion = IndexReponses.rechercherSorties(motsQuestionSansMotsOutils.get(0));
            for (int i = 1; i < motsQuestionSansMotsOutils.size(); i++) {
                vFusion = fusion(vFusion, IndexReponses.rechercherSorties(motsQuestionSansMotsOutils.get(i)));
            }
            vOcc = maxOccurences(vFusion, seuil);
        }
        return vOcc;
    }


    static public boolean estUnNombre(String s) {
        //{s est non vide}=>{résultat = true si s ne contient que des caractères représentant des chiffres (>='0'&<='9') et false sinon}
        int i = 0;
        while (i<s.length() && (s.charAt(i)<=57 & s.charAt(i)>=48)){
            i++;
        }
        return !(i<s.length());
    }


    static public ArrayList<Integer> selectionReponsesCandidates(String question,
                                                                 ArrayList<Integer> candidates,
                                                                 Index IndexFormes,
                                                                 ArrayList<String> reponses,
                                                                 ArrayList<String> formesReponses,
                                                                 ArrayList<String> motsOutils,
                                                                 Thesaurus thesaurus) {
        //{}=>{résultat = vecteur des identifiants de réponses (parmi les candidates) dont la forme est cohérente
        // avec la question.
        // remarque 1 : utilise decoupeEnMots, existeChaineDicho, rechercherSorties, fusion, maxOccurences, calculForme
        // remarque 2 : l'algorithme procède en 2 temps. D'abord il trouve les formes de réponses qui répondent à la question.
        // puis ajoute au résultat l'identifiant des réponses candidates qui respectent au moins une de ces formes.
        // remarque 3 : pour trouver les formes de réponses qui répondent à la question, on utilise l'index des formes, et on sélectionne
        // en appelant maxOccurences (avec seuil = nombre des mots-outils de la question) celles associées dans l'index à tous les mots-outils de la question.
        // remarque 4 : seuls les NBMOTS_FORME premiers mots-outils de la question sont pris en compte}

        //Partie 1
        String formeQ = calculForme(question, motsOutils, thesaurus);
        ArrayList<String> formeDecoupeQ = decoupeEnMots(formeQ);
        ArrayList<String> formeNumQ = new ArrayList<>();

        int i = 0;
        while (i<formeDecoupeQ.size() && i<NBMOTS_FORME){
            String forme = formeDecoupeQ.get(i);
            if (estUnNombre(forme)){
                forme = "NUM";
            }
            formeNumQ.add(forme+"_"+i);
            i++;
        }


        //fusion de tout les vecteur d'index
        ArrayList<Integer> vFusion = new ArrayList<>();
        for (int j = 0; j<formeNumQ.size(); j++){
            ArrayList<Integer> indexFormesReponse = IndexFormes.rechercherSorties(formeNumQ.get(j));
            vFusion = fusion(vFusion, indexFormesReponse);
        }

        int seuil = formeNumQ.size();

        ArrayList<Integer> vIndexFormesOk = maxOccurences(vFusion, seuil);


        //Partie 2
        ArrayList<Integer> candidateFormeOk = new ArrayList<>();

        //parcours toutes les phrases, puis pas toutes les formesReponses
        for (i = 0; i<candidates.size(); i++){
            String formeR = calculForme(reponses.get(candidates.get(i)), motsOutils, thesaurus);
            int j = 0;
            String formeATrouve = "";

            while (j<vIndexFormesOk.size() && formeATrouve.compareTo(formeR)!=0){
                formeATrouve = formesReponses.get(vIndexFormesOk.get(j));
                j++;
            }

            if (formeATrouve.compareTo(formeR) == 0){
                candidateFormeOk.add(candidates.get(i));
            }
        }

        return candidateFormeOk;
    }

    static public boolean reponseExiste(String reponse,
                                        Index indexReponses,
                                        ArrayList<String> reponses,
                                        ArrayList<String> motsOutils,
                                        Thesaurus thesaurus) {
        //{}=>{résultat = true si la reponse est présente dans reponses et false sinon.
        // remarque 1 : utilise decoupeEnMots, rechercherSortiePourEntree, existeChaineDicho, rechercherSorties, fusion, maxOccurences
        // remarque 2 : Le vecteur reponses n'est pas trié. Afin d'éviter le coûteux parcours séquentiel du
        // vecteur, on utilise indexReponses pour trouver les réponses contenant tous les mots non outils de la
        // reponse, puis on vérifie si l'une d'entre elle est identique à reponse.}
        ArrayList<String> reponseDecoupe = decoupeEnMots(reponse);
        ArrayList<String> reponseDecoupeSansMotsOutils = new ArrayList<>();

        //garde les mots thématiques
        for (int i=0; i<reponseDecoupe.size(); i++){
            String leMot = reponseDecoupe.get(i);
            leMot = thesaurus.rechercherSortiePourEntree(leMot);
            if (!existeChaineDicho(motsOutils, leMot)){
                reponseDecoupeSansMotsOutils.add(leMot);
            }
        }

        int seuil = reponseDecoupeSansMotsOutils.size();
        ArrayList<Integer> vOcc = new ArrayList<>();
        if (!reponseDecoupeSansMotsOutils.isEmpty()) {
            //fusion de tout les vecteur d'index
            ArrayList<Integer> vFusion = indexReponses.rechercherSorties(reponseDecoupeSansMotsOutils.get(0));
            for (int i = 1; i < reponseDecoupeSansMotsOutils.size(); i++) {
                vFusion = fusion(vFusion, indexReponses.rechercherSorties(reponseDecoupeSansMotsOutils.get(i)));
            }
            vOcc = maxOccurences(vFusion, seuil);
        }

        int i = 0;
        while (i<vOcc.size() && reponses.get(vOcc.get(i)).compareTo(reponse)!=0){
            i++;
        }


        if (i<vOcc.size() && reponses.get(vOcc.get(i)).compareTo(reponse)==0){
            return true;
        }
        return false;
    }


    static public boolean formeQuestionReponseExiste(String question,
                                                     String reponse,
                                                     Index indexFormes,
                                                     ArrayList<String> formesReponses,
                                                     ArrayList<String> motsOutils,
                                                     Thesaurus thesaurus) {
        //{}=>{résultat = * true si la forme de reponse est présente dans formesReponses
        // et qu'elle est accessible à partir des mots de la question en utilisant indexFormes.
        //                * false sinon.
        // remarque 1 : utilise decoupeEnMots, rechercherSortiePourEntree, existeChaineDicho, rechercherSorties, fusion, maxOccurences, calculForme
        // remarque 2 : Le vecteur formesReponses n'est pas trié. Afin d'éviter le coûteux parcours séquentiel du
        // vecteur, et afin de vérifier l'accessibilité à partir des mots de la question en utilisant indexFormes,
        // on utilise indexFormes pour trouver les formes indexées par les mots-outils de la
        // question, puis on vérifie si l'une de ces formes est identique à la forme de reponse.
        // remarque 3 : seuls les NBMOTS_FORME premiers mots-outils de question sont pris en compte}

        String questionForme = calculForme(question, motsOutils, thesaurus);
        ArrayList<String> questionDecoupe = decoupeEnMots(questionForme);
        ArrayList<String> formeNum = new ArrayList<>();

        int i = 0;
        while (i<questionDecoupe.size() && i<NBMOTS_FORME){
            String forme = thesaurus.rechercherSortiePourEntree(questionDecoupe.get(i));
            forme = forme+"_"+i;
            formeNum.add(forme);
            i++;
        }

        int seuil = formeNum.size();
        ArrayList<Integer> vOcc = new ArrayList<>();
        if (!formeNum.isEmpty()) {
            //fusion de tout les vecteur d'index
            ArrayList<Integer> vFusion = indexFormes.rechercherSorties(formeNum.get(0));
            for (i = 1; i < formeNum.size(); i++) {
                vFusion = fusion(vFusion, indexFormes.rechercherSorties(formeNum.get(i)));
            }
            vOcc = maxOccurences(vFusion, seuil);
        }

        String formeR = calculForme(reponse, motsOutils, thesaurus);

        i = 0;
        while (i<vOcc.size() && formeR.compareTo(formesReponses.get(vOcc.get(i)))!=0){
            i++;
        }
        if (i<vOcc.size() && formeR.compareTo(formesReponses.get(vOcc.get(i)))==0){
            return true;
        }
        return false;
    }
}
