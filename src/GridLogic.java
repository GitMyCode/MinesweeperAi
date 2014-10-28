import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by MB on 3/12/14.
 */
public class GridLogic extends JPanel  {

    private int ROW = 15;
    private int COL = 15;
    private final int TOTAL_CELLS = 16;
    private int NB_MINES = 119;


    private final int RIEN = 0;
    private final int MINE = 9;
    private final int COUVERTE = 10;
    private final int FLAG = 11;
    private final int XFLAG = 12;

    private Image[] grid_icones;
    public Case[][] field;
    private JLabel status;
    private int mines_restantes;
    Controller controller;
    boolean gameover = false;
    Random random;

    ArrayList<int[]> listCoord = new ArrayList<int[]>();
    ArrayList<Case> nextToMine = new ArrayList<Case>();
    //ArrayList<Case> toVerify   = new ArrayList<Case>();

    boolean firstplay = true;

    int ran_x;
    int ran_y;

    public GridLogic(JLabel status, int row, int col, int nb_mines){
        this.ROW = row;
        this.COL = col;
        this.NB_MINES = nb_mines;

        this.status = status; //reference au jlabel du jframe
        grid_icones = new Image[13]; // nb d'image


        controller = new Controller();
        setLayout(new GridLayout(ROW, COL));

        for(int i=0; i<13; i++){

            //grid_icones[i] = (new ImageIcon(("img/j"+i+".gif"))).getImage();
            java.net.URL imageUrl = getClass().getResource("img/j"+i+".gif");
            grid_icones[i] = (new ImageIcon(imageUrl)).getImage();
        }
        setDoubleBuffered(true);
        game();
    }

    //Initialise la game
    public void game(){

        removeAll();
        revalidate();
        updateUI();
        gameover = false;
        mines_restantes = NB_MINES;
        firstplay = true;

        nextToMine.clear();
        //toVerify.clear();

        field=null;
        field = new Case[ROW][COL];

        /* Creer les case dans la field*/
       for(int i=0;i< ROW;i++){
            for(int j=0; j< COL; j++){
                field[i][j] = new Case();
                field[i][j].x = i;
                field[i][j].y = j;
                field[i][j].addMouseListener(controller);
                add(field[i][j]);
                field[i][j].revalidate();
            }
        }


        random = new Random();
        int ran_x,ran_y;
        status.setText(Integer.toString(NB_MINES));
        for(int i=0; i< NB_MINES; i++){ //Placer les mines!
            ran_x = random.nextInt(ROW -1);
            ran_y = random.nextInt(COL -1);
            if  (field[ran_x][ran_y].getStatus()==MINE){i--;} //Si deja une mine on recommence
            else{field[ran_x][ran_y].setStatus(MINE);} // place la mine
        }//Fini placement mine


        calculateIndice(); // Place les indice autour des mines

        //  AI();

       repaint_cases();
        repaint();
        revalidate();

    }

    public GridLogic(GridLogic toCopy){
        field = new Case[toCopy.ROW][toCopy.COL];
        for(int i=0;i < toCopy.ROW; i++){
            for(int j=0; j< toCopy.COL ; j++){
                field[i][j] = new Case();
                field[i][j].copyCase(toCopy.field[i][j]);
            }
        }
    }

    private void calculateIndice(){
        int cal_status=0;
        for(int i=0; i < ROW; i++){
            for(int j=0; j< COL; j++){
                if(field[i][j].getStatus() != MINE){
                    if(estValide(i+1,j))     cal_status+= (field[i+1][j].getStatus() == MINE) ? 1 : 0; //North
                    if(estValide(i+1,j-1))   cal_status+= (field[i+1][j-1].getStatus()==MINE) ? 1:  0; //North-Ouest
                    if(estValide(i+1,j+1))   cal_status+= field[i+1][j+1].getStatus() == MINE ? 1: 0; //North-Est
                    if(estValide(i,j-1))     cal_status+= field[i][j-1].getStatus() == MINE ? 1: 0;  //Ouest
                    if(estValide(i,j+1))     cal_status+= field[i][j+1].getStatus() == MINE ? 1: 0;  //Est
                    if(estValide(i-1,j))     cal_status+= field[i-1][j].getStatus() == MINE ? 1: 0;  //Sud
                    if(estValide(i-1,j-1))   cal_status+= field[i-1][j-1].getStatus() == MINE ? 1: 0;  //Sud-Ouest
                    if(estValide(i-1,j+1))   cal_status+= field[i-1][j+1].getStatus() == MINE ? 1: 0;  //Sud-Est*/
                    field[i][j].setStatus(cal_status);
                    cal_status=0;
                    field[i][j].repaint();
                }
            }
        }
    }

    private boolean estValide(int x, int y){
        return (x >= 0 && x < ROW
                && y >= 0 && y < COL && !field[x][y].estDecouvert);
    }



    /*S'occuper de la mecanique d'un coup dans le jeu*/
    public void play(Case current,boolean flag){


        if(flag && current.flaged){
            current.switchFlag();
            current.repaint();
            status.setText(Integer.toString(mines_restantes));
            return;}
        if(!gameover && !(!flag && current.flaged)){ //Si partie pas fini et que la case n'est pas flager
            if(flag && mines_restantes>0 ){
                current.switchFlag();
                status.setText(Integer.toString(mines_restantes));
                gameover =  (gameWin());
            }else{
                current.estDecouvert = true;
                if(current.getStatus()==MINE) System.out.println("MINE!!");
                gameover = (current.getStatus() == MINE) || (gameWin());
                if(current.getStatus()==RIEN){
                    ArrayList<Case> voisins =  getVoisins(current,this.field);
                    for(Case v : voisins){
                        play(v,false);
                    }
                }
            }
            current.repaint();
            if(gameover){
                if(gameWin()){status.setText("Game Win!");}
                else             {status.setText("Game Lost!");}

           /* repaint() pour reveler les caes*/
              repaint_cases();
            }//fin gameover
        }
    }
    public void repaint_cases(){
        for(Case[] cases : field){
            for(Case c : cases){
                c.revalidate();
                c.repaint();}
        }
    }
    /* Va chercher les voisins immediat et les retourne
     * dans un arraylist */
    public ArrayList<Case> getVoisins(Case c,Case[][] field){
        ArrayList<Case> voisins = new ArrayList<Case>();
        int x = c.getXpos();
        int y = c.getYpos();
        if(estValide(x+1,y))      voisins.add(field[x+1][y]);
        if(estValide(x+1,y-1))    voisins.add(field[x+1][y-1]);
        if(estValide(x+1,y+1))    voisins.add(field[x+1][y+1]);
        if(estValide(x,y-1))      voisins.add(field[x][y-1]);
        if(estValide(x,y+1))      voisins.add(field[x][y+1]);
        if(estValide(x-1,y))      voisins.add(field[x-1][y]);
        if(estValide(x-1,y-1))    voisins.add(field[x-1][y-1]);
        if(estValide(x-1,y+1))    voisins.add(field[x-1][y+1]);
        return voisins;
    }

    /*Verifie si la game est gagné
     check si tout les flags sont deposer et ensuite si il sont bien deposer
    * */
    public boolean gameWin(){
        boolean gameWin = true;
        if(mines_restantes == 0){
            for(int i=0; i< ROW; i++){
                for(int j =0 ;j< COL ;j++){
                    if(  (field[i][j].getStatus() == MINE && !field[i][j].getFlag()) //Si une mine n'est pas flagger
                      || (field[i][j].estDecouvert == false && !field[i][j].getFlag())){ // ou Si un element n'est pas decouvert
                        gameWin = false;
                        break;
                    }
                }
            }
        }else{gameWin = false;}

        return gameWin;
    }

    public void calculProbabilite(){
        System.out.println("CalculProbabilite");

       /*
       * copy field
       * pour chaque case de bordure
       *    faire toute les combinaison de flag qui satisfait l'indice
       *    pour chacune de ces combinaison
       *        faire la meme chose pour la bordure suivante( sur la meme field)
       *        place flag appel calculRecurs(field)
       *        si calculRecurs(retourne)vrai alors +1 pour cette case
       *        if(calculRecurs){ +1}
       * */ // cpy field


        for(Case[] cases : field){
            for(Case c : cases){
               if(c.estDecouvert && c.status >RIEN && c.status< MINE) nextToMine.add(c);
            }
        }

          //System.out.print("=="+b.x+" "+b.y+"    sta:"+b.status);

        for(Case[] cases : field){
           for(Case c : cases) {
               c.nbFlag=0;
               if(c.flaged){c.setFlag(false);
                c.repaint();
               }
           }
        }
        mines_restantes=NB_MINES;

        GridLogic copyGrid = new GridLogic(this);// un copy de la field pour la nouvelle combinaison
            calculRecurs(copyGrid, 0);
/*
        for(Case[] cases: field){
            for(Case c : cases){
                if(c.getFlag()){
                    System.out.println(" x:"+c.x+"  y:"+c.y +"    nbFlag:"+ c.nbFlag);
                }
            }
        }
*/

        nextToMine.clear();
        //toVerify.clear();
    }

    public boolean calculRecurs(GridLogic grid,int index){
        /*
        * Okey. Je check le nombre de flag qui doit etre placer autour
        * (1)
        * -status -nbdeFlag = nb de flag restant  a placer
        * -nbVoisinNonDecouvert retourn une liste des cases avec mine possible
        *
        * (2)
        * - trouver combinaison possible qui satisfait la case
        * - pour chaque combinison cree une nouvelle grille et placer la combinaison
        * - appel recurssif sur prochaine case avec cette grille
        *   -si retourne false alors on essai une autre
        *
        * (3) condition d'arret
         * -on est au bout de la liste
         *      -True
         * -checkValidFlag ( sur les case deja combiner) retourn faux
         *      -False

        * */

        if(!checkValidFlag(grid.field,index)){
            return false;
        }
        if(index >= nextToMine.size()){
            return true;
        }
        ArrayList<Case> possibleMine = getVoisinNonDecouvert(nextToMine.get(index),grid.field); // Les voisin inconnu de la case en cour
        ArrayList<int[]> listC = new ArrayList<int[]>();                                        // listC c'est les combinaison possible de flag qui satisfait l'indice



        ArrayList<Case> voisins = getVoisins(nextToMine.get(index),grid.field);       // prendre les voisin et voir combien de flag il reste a placer pour satisfaire l'indice
        int nbFlagAplacer = 0;
        for(int i=0 ;i<voisins.size(); i++){
            if(voisins.get(i).getFlag()){ nbFlagAplacer--;}
        }
        nbFlagAplacer+= nextToMine.get(index).status;
        if(nbFlagAplacer <0){return false;} // Si il y a plus de flag que l'indice l'indique alors on retourn faux




        int[] combinaison = new int[nbFlagAplacer];
        combinaisonFlag(0,nbFlagAplacer,possibleMine.size(),combinaison,listC); // tout les combinaison de flag pour cette case

        // (2)                           //////////////////////////////


        if(nbFlagAplacer == 0){
            return calculRecurs(grid, index+1);
        }


        for(int[] list : listC){ // Pour chaque combinison trouvé
            Case temp;
            for(int i=0; i<nbFlagAplacer ; i++){
                temp = possibleMine.get(list[i]);
                grid.field[temp.x][temp.y].switchFlag(); // placer les flag sur la nouvelle field

            }
            if(calculRecurs(grid, index+1)){
                    for(Case[] cases: grid.field){
                        for (Case c : cases){
                            if(c.getFlag()){
                               this.field[c.x][c.y].nbFlag +=1;
                               if(!this.field[c.x][c.y].flaged){
                                this.field[c.x][c.y].setFlag(true);
                                this.field[c.x][c.y].repaint();
                                mines_restantes--;
                               }

                            }
                        }

                    }
                    status.setText(Integer.toString(mines_restantes));
                //return true;
            }
            for(int i=0; i<nbFlagAplacer ; i++){
                temp = possibleMine.get(list[i]);
                grid.field[temp.x][temp.y].setFlag(false); // placer les flag sur la nouvelle field
            }


        }
        listC.clear();
        voisins.clear();

        //Arrive jusqu'ici si il y a des flag a poser mais peut importe ou il le place
        // Cela cause une erreur plus loin

        return false;


    }

    ///////////////////////////////////////////////////
    public boolean checkValidFlag(Case[][] copyField,int index){
        for(int i=0; i< index ; i++){
            Case c = nextToMine.get(i);
            ArrayList<Case> voisins = getVoisins(copyField[c.x][c.y],copyField);
            int indice=0;
            for(Case v : voisins){
                if(v.flaged) indice++;
            }
            if(indice!= c.getStatus()){
               voisins.clear();
                voisins=null;

                return false;}
        }

        return true;
    }
    ///////////////////////////////////////////////////
    public ArrayList<Case> getVoisinNonDecouvert(Case c ,Case[][]field){
        ArrayList<Case> vInconnu = new ArrayList<Case>();
        if((Object)c == null){return null;}  // Je n'aime pas cette ligne, cela rend la fonction moins previsible
        if(estValide(c.x+1,c.y)   && !field[c.x+1][c.y].estDecouvert   && !field[c.x+1][c.y].flaged)   vInconnu.add(field[c.x+1][c.y]);
        if(estValide(c.x+1,c.y-1) && !field[c.x+1][c.y-1].estDecouvert && !field[c.x+1][c.y-1].flaged) vInconnu.add(field[c.x+1][c.y-1]);
        if(estValide(c.x+1,c.y+1) && !field[c.x+1][c.y+1].estDecouvert && !field[c.x+1][c.y+1].flaged) vInconnu.add(field[c.x+1][c.y+1]);
        if(estValide(c.x,c.y-1)   && !field[c.x][c.y-1].estDecouvert   && !field[c.x][c.y-1].flaged)   vInconnu.add(field[c.x][c.y-1]);
        if(estValide(c.x,c.y+1)   && !field[c.x][c.y+1].estDecouvert   && !field[c.x][c.y+1].flaged)   vInconnu.add(field[c.x][c.y+1]);
        if(estValide(c.x-1,c.y)   && !field[c.x-1][c.y].estDecouvert   && !field[c.x-1][c.y].flaged)   vInconnu.add(field[c.x-1][c.y]);
        if(estValide(c.x-1,c.y-1) && !field[c.x-1][c.y-1].estDecouvert && !field[c.x-1][c.y-1].flaged) vInconnu.add(field[c.x-1][c.y-1]);
        if(estValide(c.x-1,c.y+1) && !field[c.x-1][c.y+1].estDecouvert && !field[c.x-1][c.y+1].flaged) vInconnu.add(field[c.x-1][c.y+1]);


        return vInconnu;
    }
    ///////////////////////////////////////////////////
    public void AI(int temp){
        final Timer timer = new Timer(temp,null);
        //http://stackoverflow.com/questions/3858920/stop-a-swing-timer-from-inside-the-action-listener
        timer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                AiPlay();

                if(gameover){
                        if(gameWin()){status.setText("Game Win!");}
                        else             {status.setText("Game Lost!");}

           /* repaint() pour reveler les caes*/
                        for(Case[] cases : field){
                            for(Case c : cases){c.repaint();}
                        }
                    System.out.println("FINI");
                    timer.stop();
                }

                //partir des cases non-null (qui on un indice) et prendre leurs voisins non decouvert


                //check les combinaison de flag qui satisfait l'indice

                //
            }
        });
        timer.start();
    }
    public void AiPlay(){


        ArrayList<Case> bordures = null;

/*
        Case essaiUnCoup =null;
        if(bordures==null){
            for(Case[] cases: field){
                for(Case c : cases){
                    if(!c.estDecouvert && !c.getFlag()){
                        essaiUnCoup= c;
                        break;
                    }
                }
            }
            if(essaiUnCoup == null) gameover=true;
            play(essaiUnCoup,false);
        }*/
        //calculProbabilite();
        bordures = getCaseBordure();
        ArrayList<Case> nonDecouvert =null;
        if(bordures!=null){
            System.out.println("netoyage");
         for(Case c : bordures){
            nonDecouvert = getVoisinNonDecouvert(c,field);
            for(Case v : nonDecouvert){
                play(v,false);
            }

        };
        }


        if(bordures == null ){
            ArrayList<Case> free = getUndiscovered();
            System.out.println("Hasard");
                int next = random.nextInt(free.size());
                play(free.get(next),false);
        }

        if( nonDecouvert != null && nonDecouvert.isEmpty()){

            calculProbabilite();
            System.out.println("null?");
            Case caseToPlays = caseToPlay();
            if(caseToPlays==null)gameover=true;
            play(caseToPlays,false);

        }


        if(!gameover)calculProbabilite();


    }

    public ArrayList<Case> getUndiscovered(){
        ArrayList<Case> undiscovered = new ArrayList<Case>();
        for(int i =0; i< field.length; i++){
            for (Case c: field[i]){
                if(!c.estDecouvert){
                    undiscovered.add(c);
                }
            }

        }

        return undiscovered;

    }


    public Case caseToPlay(){
        Case play =null;
/*
        Case bestBordure =null;
        ArrayList<Case> bordures = getCaseBordure();
/*
        for(Case c : bordures){
            ArrayList<Case> nonDecouvert = getVoisinNonDecouvert(c,field);
            for(Case v : nonDecouvert){
                play(v,false);
            }

        }


        for(Case c : bordures){
            int nbVoisinInconnu = getNbVoisinInconnu(c);
            if(nbVoisinInconnu> 0){
             if( bestBordure==null || getNbVoisinInconnu(bestBordure) >= nbVoisinInconnu) {
                if(bestBordure!= null && getNbVoisinInconnu(bestBordure) == nbVoisinInconnu){

                    bestBordure = (bestBordure.getStatus() > c.getStatus())? c:bestBordure;

                }else{
                    bestBordure=c;
                }
             }
            }

        }

      // if(bestBordure!=null) System.out.println("  bordure choisi: x:"+ bestBordure.x + " y:"+bestBordure.y);
        ArrayList<Case> voisins = getVoisinNonDecouvert(bestBordure,field);
        if(voisins !=null){
            for(Case voisin : voisins){
                if(!voisin.estDecouvert && !voisin.flaged){
                    if(play!=null && play.riskProbability > (bestBordure.status*1.5) - getNbVoisinSafe(voisin) ){
                        play=voisin;
                        play.riskProbability = (bestBordure.status*1.5) - getNbVoisinSafe(voisin);
                    }else if(play==null){
                        play= voisin;
                        play.riskProbability = (bestBordure.status* 1.5) - getNbVoisinSafe(voisin);
                    }
                }
            }

        }
*/

        for(Case[] cases : this.field){
            for(Case c : cases){
               if(c.getFlag()){
                   ArrayList<Case> voisins = getVoisins(c,this.field);
                   for(Case voisin : voisins){
                       if(voisin.getStatus() ==RIEN){
                           System.out.println("Flag inutil trouver x:"+c.x +"  y:"+c.y);
                            c.switchFlag();
                           play(c,false);
                            play= c; // Inutile, c'est juste pour ne pas retourner null;
                           //break;
                       }
                   }
               }
               if(c.estDecouvert ){
                   if(indiceEgalFlag(c)){
                      ArrayList<Case> voisins = getVoisins(c,field);
                       for(Case voisin:voisins){
                           if(!voisin.estDecouvert && !voisin.flaged){
                               if(play!=null && play.riskProbability > (c.status*1.5) - getNbVoisinSafe(c) ){
                                   play=voisin;
                                   play.riskProbability = (c.status*1.5) - getNbVoisinSafe(c);
                               }else if(play==null){
                                   play= voisin;
                                   play.riskProbability = (c.status* 1.5) - getNbVoisinSafe(c);
                               }
                           }
                       }
                   }
               }
            }
        }


        if(play!=null)
            return play;

        for(Case[] cases: field){
            for(Case c : cases){
                if(!c.estDecouvert && !c.getFlag()){
                    return play= c;
                }
            }
        }

        /*Si il ne reste plus de case libre essayer meilleur chance*/

        if(play==null && mines_restantes <0){
            System.out.println("Dans essai coup");
            for(Case[] zcases : field){
               for(Case zc: zcases){
                   if(zc.getFlag()){
                       if(play ==null || zc.nbFlag <= play.nbFlag){

                           //System.out.println(" essai un coup: x:"+zc.x +"  y"+zc.y);
                           play = zc;
                       }
                   }

               }
            }
            System.out.println("le choix:  x:"+play.x+ " y:"+play.y+ " nbFlag:"+play.nbFlag);

            play.switchFlag();
        }

        ran_x = random.nextInt(ROW-1);
        ran_y = random.nextInt(COL - 1);

        return play;
    }


    public ArrayList<Case> getCaseBordure(){
        ArrayList<Case> casesBordure = new ArrayList<Case>();
        for(Case[] cases: field){
            for(Case c : cases){
                if(c.estDecouvert && c.getStatus()>RIEN && c.getStatus() <MINE){
                    casesBordure.add(c);
                }
            }
        }
        if (casesBordure.isEmpty())
            return null;

        return casesBordure;
    }
    public boolean indiceEgalFlag(Case c){
        ArrayList<Case> voisins = getVoisins(c,field);
        int nbVoisinFlag = 0;
        for(Case voisin : voisins){
           if(voisin.flaged){
               nbVoisinFlag++;
           }
        }

        return (nbVoisinFlag == c.getStatus());
    }

    public int getNbVoisinInconnu(Case c){
        int nbVoisinInconnu=0;
        ArrayList<Case> voisins = getVoisins(c,field);
        for(Case voisin : voisins){
            if(!voisin.estDecouvert && !voisin.flaged ){
                nbVoisinInconnu++;
            }
        }
        return nbVoisinInconnu ;

    }
    public int getNbVoisinSafe(Case c){
        int nbVoisinSafe =0;

       // if(indiceEgalFlag(c)){return 10;}
        ArrayList<Case> voisins = getVoisins(c,field);
        for(Case voisin : voisins){
            if(voisin.estDecouvert ){
                nbVoisinSafe++;
            }
        }

        return nbVoisinSafe;
    }
    /*Verifie si cet case est peut etre une mine
    * Elle regarde si elle a une case avec un indice a cote
    * */
    public boolean canBeMine(Case c){
        boolean canBeMine = false;

        ArrayList<Case> voisins = getVoisins(c,field);
        for(Case c_voisin : voisins ){
           if(c_voisin.getStatus()>RIEN && c_voisin.getStatus()<MINE){
               return true;
           }
        }

        return canBeMine;
    }
    ///////////////////////////////////////////////////
    public void combinaisonFlag(int index,int nbFlag, int nbCase,int[] combinaison, ArrayList<int[]> listeC){
        if(nbFlag ==0){return;}
        if(index >= nbFlag){

            int[] newCombinaison = combinaison.clone();
             listeC.add(newCombinaison);
            return ;
        }
        int start =0;
        if(index >0) start = combinaison[index-1]+1;
        for(int i=start; i<nbCase;i++){
            combinaison[index]=i;
            combinaisonFlag(index+1, nbFlag, nbCase, combinaison, listeC);
        }
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
    }

    ///////////////////////////////////////  CLASSE CASE  //////////////////////////////////////////////
    public class Case extends JPanel{
        boolean estDecouvert = false;
        int status;
        boolean flaged = false;
        Image icone;

        int nbFlag=0;
        double riskProbability;
        int x;
        int y;
        ArrayList<Case> voisins;


        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if(!gameover){ // Si patie en cours
                if(flaged)            {g.drawImage(grid_icones[FLAG],0,0,this);}
                else if(estDecouvert) {g.drawImage(grid_icones[status],0,0,this);}
                else                  {g.drawImage(grid_icones[COUVERTE],0,0,this);}


            }else{// Si la partie est fini
                if(status != MINE && flaged){g.drawImage(grid_icones[XFLAG],0,0,this);}
                else if(flaged)             {g.drawImage(grid_icones[FLAG],0,0,this);}
                else                        {g.drawImage(grid_icones[status],0,0,this);}
            }
        }


        /*Setter getter -------------------------*/
        public void copyCase(Case c){
            this.x = c.x;
            this.y= c.y;
            this.estDecouvert = c.estDecouvert;
            this.status = c.status;
            this.flaged = c.flaged;
        }
        public void setStatus(int new_status){ this.status=new_status; }
        public int getStatus(){return this.status;}
        public int getXpos(){return this.x;} public int getYpos(){return this.y;}
        public void setFlag(boolean flag){this.flaged = flag;}
        public boolean getFlag(){return this.flaged;}
        public void switchFlag(){
            this.flaged = !flaged;
            if(flaged){ mines_restantes--;}
            else      { mines_restantes++;}
        }
        //////////////////////////////////////

    }
    /////////////////////////////////// CLASSE CONTROLLER UTILISATEUR///////////////////////////////////////////////
    class Controller extends MouseAdapter{
        @Override
        public void mousePressed(MouseEvent e) {
            super.mousePressed(e);
            Case current = (Case) e.getSource();
            if(e.getButton() == MouseEvent.BUTTON3){
                play(current,true);
            }else{
                play(current,false);
            }
        }
    }
}
