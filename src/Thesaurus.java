import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class Thesaurus {

    private class EntreeSortie implements Comparable<EntreeSortie> {
        private String entree; // un mot
        private String sortie; // sa forme canonique

        public EntreeSortie(String entree, String sortie) {
            this.entree = entree;
            this.sortie = sortie;
        }

        public int compareTo(EntreeSortie o) {
            return this.entree.compareTo(o.entree);
        }
    }

    private ArrayList<EntreeSortie> table;

    public Thesaurus(String nomFichier) {
        //{}=>{ constructeur créant et initialisant l'attribut table à partir du contenu du fichier dont le nom est passé en paramètre, puis triant la table
        // en utilisant la méthode compareTo d'EntreeSortie
        // remarque 1 : utilise ajouterEntreeSortie et trierEntreesSorties
        // remarque 2 : pour la lecture du fichier, inspirez-vous de lireMotsOutils de Utilitaire
        // remarque 3 : pour les traitements de la chaîne lue, utilisez les méthodes indexOf,substring de String
        table = new ArrayList<>();

        try {
            FileInputStream file = new FileInputStream(nomFichier);
            Scanner scanner = new Scanner(file);

            while (scanner.hasNextLine()) {
                String ligne = scanner.nextLine();
                int indexSeparation = ligne.indexOf(":");
                String entree = ligne.substring(0,indexSeparation);
                String sortie = ligne.substring(indexSeparation+1);
                this.ajouterEntreeSortie(entree, sortie);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        if (!table.isEmpty()){
            trierEntreesSorties(table, 0, table.size()-1);
        }

    }

    public void ajouterEntreeSortie(String entree, String sortie) {
        //{}=>{ajoute à la fin de la table une nouvelle EntreeSortie avec les attributs entree et sortie}
        this.table.add(new EntreeSortie(entree, sortie));
    }


    public String rechercherSortiePourEntree(String entree) {
        // {l'attribut table du thesaurus est trié sur l'attribut entree des Entree-Sortie}=>
        // {résultat = la forme canonique associée à entree dans le thésaurus si l'entrée entree existe,
        // entree elle-même si elle n'existe pas. La recherche doit être dichotomique.
        // remarque : utilise compareTo de EntreeSortie }
        if (this.table.isEmpty()){
            return entree;
        }
        int inf, sup, m;
        inf = 0;
        sup = this.table.size()-1;
        while (inf<sup){
            m = (inf+sup)/2;
            if (this.table.get(m).entree.compareTo(entree)>=0){
                sup = m;
            }else {
                inf = m+1;
            }
        }
        if (this.table.get(sup).entree.compareTo(entree)==0){
            return this.table.get(sup).sortie;
        }else {
            return entree;
        }
    }

    static void trierEntreesSorties(ArrayList<EntreeSortie> v, int inf, int sup) {
//        {} => {trie v sur la base de la méthode compareTo de EntreeSortie}
        if (inf < sup){
            int m = (inf+sup)/2;
            trierEntreesSorties(v, inf , m );
            trierEntreesSorties(v, m+1 , sup);
            trierEntreesSortiesWK(v, inf , m , sup);
        }
    }

    private static void trierEntreesSortiesWK(ArrayList<EntreeSortie> v, int inf, int m, int sup){
        ArrayList<EntreeSortie> temp = new ArrayList <>();
        if (inf < sup){
            int iG = inf;
            int iD = m+1;
            while(iG <= m && iD <= sup){
                if(v.get(iG).compareTo(v.get(iD)) <= 0){
                    temp.add(v.get(iG));
                    iG++;
                } else {
                    temp.add(v.get(iD));
                    iD++;
                }
            }
            while(iG <= m){
                temp.add(v.get(iG));
                iG++;
            }

            while(iD <= sup){
                temp.add(v.get(iD));
                iD++;
            }

            int k = 0;
            for (int i = inf; i<=sup ; i++){
                v.set(i,temp.get(k));
                k++;
            }


        }

    }

}
