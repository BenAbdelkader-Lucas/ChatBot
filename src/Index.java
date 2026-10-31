import java.util.ArrayList;
public class Index {

    // un index est un vecteur d'EntreeIndex
    private class EntreeIndex {

        // une EntreeIndex associe un vecteur trié d'entiers (sorties) à une String (entree)
        private String entree;
        private ArrayList<Integer> sorties;

        public ArrayList<Integer> getSorties() {
            return sorties;
        }


        //constructeur
        public EntreeIndex(String entree) {
            this.entree = entree;
            sorties = new ArrayList<>();
        }


        public int rechercherSortie(Integer sortie) {
            //{}=>{recherche dichotomique de sortie dans sorties (triée dans l'ordre croissant)
            // résultat = l'indice de sortie dans sorties si trouvé, - l'indice d'insertion si non trouvé }
            if (sortie.compareTo(this.sorties.get(this.sorties.size()-1))>0){
                return -this.sorties.size();
            }else {
                int inf = 0;
                int sup = this.sorties.size()-1;
                int m;
                while (inf < sup){
                    m = (inf+sup)/2;
                    if (this.sorties.get(m).compareTo(sortie)>=0){
                        sup = m;
                    }else {
                        inf = m + 1;
                    }
                }
                if (this.sorties.get(sup).compareTo(sortie)==0){
                    return sup;
                }else {
                    return -sup;
                }
            }
        }


        public void ajouterSortie(Integer sortie) {
            //{}=>{insère sortie à la bonne place dans sorties (triée dans l'ordre croissant)
            // remarque : utilise rechercherSortie de EntreeIndex }
            if (this.sorties.isEmpty()){
                this.sorties.add(0, sortie);
            }else {
                int index = this.rechercherSortie(sortie);
                if (index <= 0 & sorties.get(0).compareTo(sortie) !=0){
                    this.sorties.add(-index, sortie);
                }
            }
        }


        @Override
        public String toString() {
            return entree + "=>" + sorties;
        }
    }

    //Un vecteur d'EntreeIndex trié sur l'attribut entree (String) des EntreeIndex
    private ArrayList<EntreeIndex> table;

    //constructeur
    public Index() {
        table = new ArrayList<>();
    }


    public int rechercherEntree(String entree) {
        //{}=>  {recherche dichotomique de entree dans table (triée dans l'ordre lexicographique des attributs entree des EntreeIndex) }
        //résultat =  l'indice de entree dans table si trouvé et -l'indice d'insertion sinon }
        if (entree.compareTo(this.table.get(this.table.size()-1).entree)>0){
            return -this.table.size();
        }else {
            int inf = 0;
            int sup = this.table.size()-1;
            int m;
            while (inf < sup){
                m = (inf+sup)/2;
                if (this.table.get(m).entree.compareTo(entree) >= 0){
                    sup = m;
                }else {
                    inf = m + 1;
                }
            }
            if (this.table.get(sup).entree.compareTo(entree)==0){
                return sup;
            }else {
                return -sup;
            }
        }
    }


    public void ajouterSortieAEntree(String entree, Integer sortie) {
        // {}=>{ajoute l'entier sortie dans les sorties associées à l'entrée entree
        // si l'entrée entree n'existe pas elle est créée.
        // ne fait rien si sortie était déjà présente dans ses sorties.
        // remarque : utilise la fonction rechercherEntree de Index et la procedure ajouterSortie de EntreeIndex}
        if (this.table.size()==0){
            this.table.add(0, new EntreeIndex(entree));
            this.table.get(0).ajouterSortie(sortie);
        }else {
            int indexEntree = this.rechercherEntree(entree);
            if (indexEntree <= 0 & this.table.get(0).entree.compareTo(entree) !=0 ) {
                indexEntree = -indexEntree;
                this.table.add(indexEntree, new EntreeIndex(entree));
            }
            this.table.get(indexEntree).ajouterSortie(sortie);
        }
    }


    public ArrayList<Integer> rechercherSorties(String entree) {
        // {}=>{résultat = les sorties associées à l'entrée entree
        // si l'entrée entree n'existe pas, une ArrayList vide est retournée.
        // remarque : utilise la fonction rechercherEntree de Index}
        int indexEntree = this.rechercherEntree(entree);
        if (indexEntree>=0){
            return this.table.get(indexEntree).getSorties();
        }
        return new ArrayList<Integer>();
    }

    public void afficher() {
        // {}=>{affiche la table de l'index}
        for (int i = 0; i < table.size(); i++) {
            System.out.println(this.table.get(i));
        }
        System.out.println(this.table);
    }


}
